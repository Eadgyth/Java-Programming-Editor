package eg.document;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent.EventType;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.Languages;
import eg.syntax.Coloring;
import eg.syntax.LanguageSetter;

/**
 * Mediates between the editing of the text document and changes that shall
 * happen in response.
 * Changes include syntax coloring, line numbering, indention and the adding
 * of text changes for undo editing.<br>
 * <p> Created in {@link FileDocument}
 */
public class TypingEdit {

   private final TextDocument textDoc;
   private final Coloring col;
   private final LanguageSetter langSet;
   private final AutoIndent autoInd;
   private final LineNumbers lineNum;
   private final UndoEdit undo;

   private UndoableChangeEvent uce;
   private UndoableChangeListener ucl;
   private TextSelectionEvent tse;
   private TextSelectionListener tsl;
   private boolean isDocListen = true;
   private boolean isAddToUndo = true;
   private boolean isTypeEdit = false;
   private DocumentEvent.EventType event;
   private int pos = 0;
   private String text = "";
   private String change = "";
   private boolean isSelectionTmp = false;
   private boolean canUndoTmp = false;
   private boolean canRedoTmp = false;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    * @param lineNrDoc  the reference to {@link LineNumberDocument}
    */
   public TypingEdit(TextDocument textDoc, LineNumberDocument lineNrDoc) {
      this.textDoc = textDoc;
      col = new Coloring(textDoc.doc(), textDoc.attrSet());
      langSet = new LanguageSetter(col);
      lineNum = new LineNumbers(lineNrDoc);
      autoInd = new AutoIndent(textDoc);
      undo = new UndoEdit(textDoc);
      textDoc.doc().addDocumentListener(docListen);
      textDoc.docTextArea().addCaretListener(caretListen);
   }

   /**
    * Sets an <code>UndoableChangeListener</code> if none was set before
    *
    * @param ucl  an {@link UndoableChangeListener}
    */
   public void setUndoableChangeListener(UndoableChangeListener ucl) {
      if (ucl == null) {
         throw new IllegalStateException(
               "An UndoableChangeListener is already set");
      }
      this.ucl = ucl;
   }

  /**
    * Sets a <code>TextSelectionLister</code> if none was set before
    *
    * @param tsl  a {@link TextSelectionListener}
    */
   public void setTextSelectionListener(TextSelectionListener tsl) {
      if (tsl == null) {
         throw new IllegalStateException(
               "A TextSelectionListener is already set");
      }
      this.tsl = tsl;
   }

   /**
    * Enabled/disables the update methods in this
    * <code>DocumentListener</code>
    *
    * @param isEnabled  true/false to enable/disabled the update
    * methods in this <code>DocumentListener</code>
    */
   public void enableDocListen(boolean isEnabled) {
      isDocListen = isEnabled;
      if (isEnabled) {
         text = textDoc.getText();
         lineNum.updateLineNumber(text);
         textDoc.docTextArea().setCaretPosition(0);
      }
      else {
         undo.discardEdits();
         notifyUndoableChangeEvent();
      }
   }

   /**
    * Enables/disables syntax coloring and auto-indentation
    *
    * @param isEnabled  true/false to enable/disable editing during
    * typing
    */
   public void enableTypeEdit(boolean isEnabled) {
      isTypeEdit = isEnabled;
   }

   /**
    * Sets the editing during typing depending on the language
    *
    * @param lang  the language which is one of the constants in
    * {@link Languages}
    */
   public void setUpEditing(Languages lang) {
      if (lang == Languages.PLAIN_TEXT) {
         col.setAllCharAttrBlack();
         enableTypeEdit(false);
      }
      else {
         langSet.setColorable(lang);
         colorSection(null, 0);
         enableTypeEdit(true);
      }
   }
   
   /**
    * Gets the text that is set in the update methods in this
    * <code>DocumentListener</code>
    *
    * @return  the text in the document
    */
   public String getText() {
      return text;
   }

   /**
    * Sets the indentation unit which consists in any number of spaces
    *
    * @param indentUnit  the String that consists of a certain number of
    * white spaces
    */
   public void setIndentUnit(String indentUnit) {
      autoInd.setIndentUnit(indentUnit);
   }

   /**
    * Gets the current indentation unit
    *
    * @return the current indentation unit
    */
   public String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   /**
    * Colors a section of the document or the entire document
    *
    * @param section  a section of the document. Null to color the
    * entire text.
    * @param pos  the pos where the section starts. Set to 0 if
    * <code>section</code> is null
    */
   public void colorSection(String section, int pos) {
      col.colorMultipleLines(text, section, pos);
   }

   /**
    * Returns if edits can be undone
    * 
    * @return  if edits can be undone
    */
   public boolean canUndo() {
      return undo.canUndo();
   }

   /**
    * Returns if edits can be redone
    * 
    * @return  if edits can be redone
    */
   public boolean canRedo() {
      return undo.canRedo();
   }

   /**
    * Performs an undo action
    */
   public void undo() {
      isAddToUndo = false;
      undo.undo();
      updateAfterUndoRedo();
   }

   /**
    * Performs a redo action
    */
   public void redo() {
      isAddToUndo = false;
      undo.redo();
      updateAfterUndoRedo();
   }

   //
   //--private methods/classes--//
   //

   private void updateAfterUndoRedo() {
      notifyUndoableChangeEvent();
      if (isTypeEdit) {
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            colorSection(change, pos);
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            EventQueue.invokeLater(() -> col.colorLine(text, pos));
         }
      }
      isAddToUndo = true;
   }

   private void notifyUndoableChangeEvent() {
      if (ucl == null) {
         return;
      }
      boolean isUndoableChange = canUndoTmp != undo.canUndo();
      boolean isRedoableChange = canRedoTmp != undo.canRedo();
      if (isUndoableChange) {
         canUndoTmp = undo.canUndo();
      }
      if (isRedoableChange) {
         canRedoTmp = undo.canRedo();
      }
      if (isUndoableChange || isRedoableChange) {
         uce = new UndoableChangeEvent(canUndoTmp, canRedoTmp);
         ucl.undoableStateChanged(uce);
      }
   }

   private void notifyTextSelectionEvent(boolean isSelection) {
      if (tsl == null) {
         return;
      }
      if (isSelectionTmp != isSelection) {
         isSelectionTmp = isSelection;
         tse = new TextSelectionEvent(isSelection);
         tsl.selectionUpdate(tse);
      }
   }

   private void markUndoBreakpoint(int caret) {
      if (!isAddToUndo || event == null) {
         return;
      }
      if (caret > 0) {
         boolean isStop = false;
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            isStop = caret - pos != 1;
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            isStop = caret - pos != 0;
         }
         if (isStop) {
            undo.markBreakpoint();
         }
      }
   }

   private DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         event = de.getType();
         pos = de.getOffset();
         textUpdate();
         change = text.substring(pos, pos + de.getLength());
         if (isAddToUndo) {
            undo.addEdit(change, pos, true);
            notifyUndoableChangeEvent();
            if (isTypeEdit) { 
               EventQueue.invokeLater(() -> {
                  col.colorLine(text, pos);
                  autoInd.indent(text, pos);
                  autoInd.closedBracketIndent(text, pos);
               });
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         event = de.getType();
         pos = de.getOffset();
         change = text.substring(pos, pos + de.getLength());
         textUpdate();
         if (isAddToUndo) {
            undo.addEdit(change, pos, false);
            notifyUndoableChangeEvent();
            if (isTypeEdit) {
               EventQueue.invokeLater(() -> col.colorLine(text, pos));
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         // nothing to do
      }

      private void textUpdate() {
         text = textDoc.getText();
         lineNum.updateLineNumber(text);
      }
   };
   
   private CaretListener caretListen = new CaretListener() {
      @Override
      public void caretUpdate(CaretEvent ce) {
         notifyTextSelectionEvent(ce.getDot() != ce.getMark());
         markUndoBreakpoint(ce.getDot());
      }
   };
}
