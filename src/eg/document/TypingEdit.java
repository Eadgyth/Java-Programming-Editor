package eg.document;

import java.awt.EventQueue;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

//--Eadgyth--/
import eg.Languages;
import eg.utils.LinesFinder;
import eg.syntax.*;

/**
 * The mediation between the editing of the document by typing in, removing,
 * pasting or replacing text and the actions that happen in response.
 * <p>
 * Created in {@link EditableDocument}
 */
public class TypingEdit {

   private final TextDocument textDoc;
   private final LineNumberDocument lineNrDoc;
   private final SyntaxHighlighter syntax;
   private final AutoIndent autoInd;
   private final UndoEdit undo;

   private boolean isDocUpdate = true;
   private boolean isCodeEditing = false;
   private boolean isInsert;
   private int chgPos = 0;
   private String text = "";
   private String change = "";
   private boolean isAddToUndo = true;
   private EditingStateReadable esr;
   private boolean isInChange = false;
   private boolean selectionState = false;
   private boolean canUndoState = false;
   private boolean canRedoState = false;
   private int lineNr = 1;
   private int colNr = 1;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    * @param lineNrDoc  the reference to {@link LineNumberDocument}
    */
   public TypingEdit(TextDocument textDoc, LineNumberDocument lineNrDoc) {
      this.textDoc = textDoc;
      this.lineNrDoc = lineNrDoc;
      syntax = new SyntaxHighlighter(textDoc);
      autoInd = new AutoIndent(textDoc);
      undo = new UndoEdit(textDoc);
      textDoc.addDocumentListener(docListener);
      textDoc.textArea().addCaretListener(caretListener);
   }

   /**
    * Sets an <code>EditingStateReadable</code>
    *
    * @param esr  an {@link EditingStateReadable}
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
      if (this.esr != null) {
         throw new IllegalStateException(
               "An EditingStateReadable is already set");
      }
      this.esr = esr;
   }

   /**
    * Sets the boolean that controls if the update methods in this
    * <code>DocumentListener</code> are enabled or disabled
    *
    * @param b  the boolean value. True to enable, false to disable
    */
   public void enableDocUpdate(boolean b) {
      isDocUpdate = b;
      if (b) {
         updateText();
         textDoc.textArea().setCaretPosition(0);
      }
   }

   /**
    * Sets the editing mode that depends on the specified language
    *
    * @param lang  the language which is a constant in
    * {@link Languages}
    */
   public void setEditingMode(Languages lang) {
      if (lang == Languages.NORMAL_TEXT) {
         textDoc.setAllCharAttrBlack();
         isCodeEditing = false;
      }
      else {
         Highlighter hl = HighlighterSelector.createHighlighter(lang);
         syntax.setHighlighter(hl);
         syntax.highlightAllText(text);
         isCodeEditing = true;
      }
   }

   /**
    * Gets the text in the document which is updated in the insert and
    * remove methods of this <code>DocumentListener</code>
    *
    * @return  the text
    */
   public String getText() {
      return text;
   }

   /**
    * Resets the state in which text is being changed
    */
   public void resetInChangeState() {
      isInChange = false;
      esr.setInChangeState(isInChange);
   }

   /**
    * Sets the indent unit which consists of spaces
    *
    * @param indentUnit  the indend unit
    */
   public void setIndentUnit(String indentUnit) {
      autoInd.setIndentUnit(indentUnit);
   }

   /**
    * Gets the current indent unit
    *
    * @return  the indent unit
    */
   public String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   /**
    * Sets the boolean that disables/re-enables the addition of
    * breakpoint that define unduable units.
    *
    * @param b  the boolean value; true to disable, false to re-enable
    * @see UndoEdit #disableBreakpointAdding(boolean)
    */
   public void disableBreakpointAdding(boolean b) {
      undo.disableBreakpointAdding(b);
   }

   /**
    * Inserts the specified string at the specified position
    *
    * @param pos  the position
    * @param toInsert  the string
    */
   public void insert(int pos, String toInsert) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = false;
      textDoc.insert(pos, toInsert);
      if (isCodeEditingHelper) {
         highlightMultiline();
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Replaces a section of the document with the specified string
    *
    * @param pos  the position where the section to be replaced starts
    * @param length  the length of the section
    * @param toInsert  the String to insert
    */
   public void replace(int pos, int length, String toInsert) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = false;  
      if (length != 0) {
         textDoc.remove(pos, length);
         if (isCodeEditingHelper) {
            highlightLine();
         }
      }
      textDoc.insert(pos, toInsert);
      if (isCodeEditingHelper) {
         highlightMultiline();
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Removes a section from the document
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param useHighlighting  if syntax highlighting of the line that
    * contains the position is done
    */
   public void remove(int pos, int length, boolean useHighlighting) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = useHighlighting;
      textDoc.remove(pos, length);
      if (isCodeEditingHelper) {
         highlightLine();
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Reads the current editing state by calling the methods defined in
    * {@link EditingStateReadable}
    */
   public void readEditingState() {
      if (esr != null) {
         esr.setInChangeState(isInChange);
         esr.setCursorPosition(lineNr, colNr);
         esr.setUndoableState(canUndoState, canRedoState);
         esr.setSelectionState(selectionState);
      }
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
   //--private--/
   //

   private void updateAfterUndoRedo() {
      outputUndoableState();
      if (isCodeEditing) {
         if (isInsert) {          
            syntax.highlightAllText(text);
         }
         else {
            highlightLine();
         }
      }
      isAddToUndo = true;
   }

   private void updateText() {
      text = textDoc.docText();
      lineNrDoc.updateLineNumber(text);
      outputInChangeState();
   }
   
   private void highlightLine() {
      syntax.highlightLine(text, chgPos);
      if (isInsert && change.equals("\n")) {
         syntax.highlightLine(text, chgPos + 1);
      }
   }

   private void highlightMultiline() {
      if (change.length() > 0) {
         syntax.highlightMultiline(text, change, chgPos);
      }
   }

   private void outputInChangeState() {
      if (esr == null) {
         return;
      }
      if (!isInChange) {
         isInChange = true;
         esr.setInChangeState(isInChange);
      }
   }

   private void outputUndoableState() {
      if (esr == null) {
         return;
      }
      boolean isUndoableChange = canUndoState != undo.canUndo();
      boolean isRedoableChange = canRedoState != undo.canRedo();
      if (isUndoableChange) {
         canUndoState = undo.canUndo();
      }
      if (isRedoableChange) {
         canRedoState = undo.canRedo();
      }
      if (isUndoableChange || isRedoableChange) {
         esr.setUndoableState(canUndoState, canRedoState);
      }
   }

   private void outputSelectionState(boolean isSelection) {
      if (esr == null) {
         return;
      }
      if (selectionState != isSelection) {
         selectionState = isSelection;
         esr.setSelectionState(selectionState);
      }
   }

   private void outputCursorPosition(int caret) {
      if (esr == null) {
         return;
      }
      int lastNewLine = LinesFinder.lastNewline(text, caret);
      lineNr = LinesFinder.lineNrAtPos(text, caret);
      if (lastNewLine == -1) {
         colNr = caret + 1;
      }
      else {
         colNr = caret - lastNewLine;
      }
      esr.setCursorPosition(lineNr, colNr);
   }

   private void markUndoBreakpoint(int caret) {
      if (!isAddToUndo) {
         return;
      }
      boolean isBreakpoint;
      if (isInsert) {
         isBreakpoint = caret - chgPos != 1;
      }
      else {
         isBreakpoint = caret - chgPos != 0;
      }
      if (isBreakpoint) {
         undo.markBreakpoint();
      }
   }

   private final DocumentListener docListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocUpdate) {
            return;
         }
         isInsert = true;
         chgPos = de.getOffset();
         updateText();
         change = text.substring(chgPos, chgPos + de.getLength());
         if (isAddToUndo) {
            if (isCodeEditing) {
               EventQueue.invokeLater(() -> highlightLine());
               EventQueue.invokeLater(() -> autoInd.adjustIndent(text, chgPos));
            }
            undo.addEdit(change, chgPos, isInsert);
            outputUndoableState();
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (!isDocUpdate) {
            return;
         }
         isInsert = false;
         chgPos = de.getOffset();
         change = text.substring(chgPos, chgPos + de.getLength());
         updateText();
         if (isAddToUndo) {
            undo.addEdit(change, chgPos, isInsert);
            outputUndoableState();   
            if (isCodeEditing) {
               EventQueue.invokeLater(() -> highlightLine());
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {}
   };

   private final CaretListener caretListener = (CaretEvent ce) -> {
      outputSelectionState(ce.getDot() != ce.getMark());
      outputCursorPosition(ce.getDot());
      markUndoBreakpoint(ce.getDot());
   };
}
