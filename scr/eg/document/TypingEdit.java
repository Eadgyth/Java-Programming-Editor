/**
 * This inner class {@code DocUndoManager} is based on and uses
 * methods of CompoundUndoManager class from JSyntaxPane at
 * https://github.com/aymanhs/jsyntaxpane
 * Copyright 2008 Ayman Al-Sairafi
 */
package eg.document;

import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.text.AbstractDocument;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import javax.swing.undo.UndoManager;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import java.awt.EventQueue;
import java.awt.Color;

//--Eadgyth--//
import eg.Languages;
import eg.ui.EditArea;
import eg.utils.FileUtils;

/**
 * Responsible for the edits in the {@code EditArea} that shall happen 
 * during typing.
 * <p>
 * The changes include the line numbering, the syntax coloring,
 * auto-indentation and undo/redo editing.
 */
class TypingEdit {

   private final static char[] EDIT_SEP = {' ', '\n', '(', ')', '{', '}'};

   private final StyledDocument doc;  
   private final Element el;
   private final SimpleAttributeSet normalSet = new SimpleAttributeSet(); 

   private final UndoManager undomanager = new DocUndoManager();
   private final Coloring col;
   private final AutoIndent autoInd;
   private final RowNumbers rowNum;

   private boolean isDocListen = true; 
   private boolean isTextModify = false;
   private boolean isIndent = false;
   private char typed = '\0';

   TypingEdit(EditArea editArea) {
      doc = editArea.textArea().getStyledDocument();
      el = doc.getParagraphElement(0);
      setStyles();

      doc.addDocumentListener(docListen);
      doc.addUndoableEditListener(undomanager);
      undomanager.setLimit(1000);

      col = new Coloring(doc, normalSet);
      rowNum = new RowNumbers(editArea.lineArea(), editArea.scrolledArea());
      autoInd = new AutoIndent(editArea.textArea(), doc, normalSet);
   }

   void enableDocListen(boolean isDocListen) {
      this.isDocListen = isDocListen;
   }

   void enableTextModify(boolean isTextModify) {
      this.isTextModify = isTextModify;
      col.enableSingleLines(isTextModify);
   }

   void enableIndent(boolean isEnabled) {
      isIndent = isEnabled;
      if (!isEnabled) {
         autoInd.resetIndent();
      }
   }

   void configColoring(Languages language) {
      col.configColoring(language);
   }

   void setKeywords(String[] keywords, boolean constrainWord) {
      col.setKeywords(keywords, constrainWord);
   }

   StyledDocument doc() {
      return doc;
   }

   SimpleAttributeSet normalSet() {
      return normalSet;
   }

   String getDocText() {
      String in = null;
      try {
         in = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
      return in;
   }

   String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   void changeIndentUnit(String indentUnit) {
      autoInd.changeIndentUnit(indentUnit);
   }

   void addAllRowNumbers(String in) {
      rowNum.addAllRowNumbers(in);
   }

   void updateRowNumber(String content) {
      rowNum.updateRowNumber(content);
   }

   void colorAll() {
      enableTextModify(false);
      String all = getDocText();
      doc.setCharacterAttributes(0, all.length(), normalSet(), false);
      col.color(all, 0);
      enableTextModify(true);
   }

   void undo() {
      try {
         if (undomanager.canUndo()) {
            undomanager.undo();
            if (isTextModify) {
               EventQueue.invokeLater(() -> {
                  colorAll();
               });
            }
         }
      }
      catch (CannotUndoException e) {
         FileUtils.logStack(e);
      }
   }

   void redo() {
      try {
         if (undomanager.canRedo()) {
            undomanager.redo();
            if (isTextModify) {
               EventQueue.invokeLater(() -> {
                  colorAll();
               });
            }
         }
      }
      catch (CannotRedoException e) {
         FileUtils.logStack(e);
      }
   }

   //
   //--private--
   //

   private void setStyles() {
      StyleConstants.setForeground(normalSet, Color.BLACK);
      StyleConstants.setLineSpacing(normalSet, 0.2f);
      StyleConstants.setBold(normalSet, false);
      doc.setParagraphAttributes(0, el.getEndOffset(), normalSet, false);
   }

   private final DocumentListener docListen = new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent documentEvent) {
      }

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (isDocListen) {
            String in = getDocText();
            int pos = de.getOffset();
            typed = in.charAt(pos);
            updateRowNumber(in);
            if (isTextModify) {
               insertTextModify(de, in, pos);
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (isDocListen) {
            String in = getDocText();
            updateRowNumber(in);
            typed = '\0';
            if (isTextModify) {
               int pos = de.getOffset();
               removeTextModify(de, in, pos);
            }
         }
      }
   };

   private void insertTextModify(DocumentEvent de, String in, int pos) {
      if (pos > 0 && isIndent) {
         autoInd.openBracketIndent(in, pos);
      }
      EventQueue.invokeLater(() -> {
         if (isIndent) {
            autoInd.closeBracketIndent(in, pos);
         }
         col.color(in, pos);
      });
   }

   private void removeTextModify(DocumentEvent de, String in, int pos) {
      EventQueue.invokeLater( () -> {
         col.color(in, pos);
      });
   }

   class DocUndoManager extends UndoManager implements UndoableEditListener {

      CompoundEdit comp = null;

      @Override
      public void undoableEditHappened(UndoableEditEvent e) {
         if (!isDocListen) {
            return;
         }
         //
         // Exclude changes of the style
         AbstractDocument.DefaultDocumentEvent event =
               (AbstractDocument.DefaultDocumentEvent) e.getEdit();
         if (event.getType().equals(DocumentEvent.EventType.CHANGE)) {
            return;
         }
         addAnEdit(e.getEdit());
      }

      @Override
      public boolean canUndo() {
         commitCompound();
         return super.canUndo();
      }

      @Override
      public boolean canRedo() {
         commitCompound();
         return super.canRedo();
      }

      @Override
      public void undo() {
         commitCompound();
         super.undo();
      }

      @Override
      public void redo() {
         commitCompound();
         super.redo();
      }

      private boolean addAnEdit(UndoableEdit anEdit) {
         if (comp == null) {
             comp = new CompoundEdit();
         }
         if (typed != '\0') {
            comp.addEdit(anEdit);
         }
         else {
            comp.end();
            super.addEdit(comp);
            comp = null;
            super.addEdit(anEdit);
         }
         if (isEditSep()) {
            comp.end();
            super.addEdit(comp);
            comp = null;
         }            
         return true;
      }

      private void commitCompound() {
         if (comp != null) {
            comp.end();
            super.addEdit(comp);
            comp = null;
         }
      }

      private boolean isEditSep() {
         int i = 0;
         for (i = 0; i < EDIT_SEP.length; i++) {
            if (EDIT_SEP[i] == typed) {
               break;
            }
         }
         return i != EDIT_SEP.length;
      }
   }
}
