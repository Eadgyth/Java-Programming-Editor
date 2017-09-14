package eg;

import java.awt.EventQueue;

import java.awt.Toolkit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JTextPane;

import java.io.IOException;

//--Eadgyth--//
import eg.utils.*;
import eg.document.TextDocument;

/**
 * The editing of the document in the selected tab by actions
 * that are invoked in the edit menu/toolbar except the language
 */
public class Edit {

   /* Options for the numbers of white spaces in indentation unit */
   private static final String[] SPACE_NUMBER = { "1", "2", "3", "4", "5", "6" };

   private TextDocument txtDoc;
   private JTextPane textArea;
   private String indentUnit;
   private int indentLength;

   /**
    * Sets the {@code TextDocument} that is edited and its current
    * indentation unit
    *
    * @param txtDoc  the {@link TextDocument} that is edited
    */
   public void setTextDocument(TextDocument txtDoc) {
      this.txtDoc  = txtDoc;
      this.textArea = txtDoc.textArea();
      indentUnit = txtDoc.getIndentUnit();
      indentLength = indentUnit.length();
   }

   /**
    * Performs undo action
    */
   public void undo() {
      txtDoc.undo();
   }

   /**
    * Performs redo action
    */
   public void redo() {
      txtDoc.redo();
   }

   /**
    * Cuts selected text and stores it to the system's clipboard
    */
   public void cut() {
      int start = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      setClipboard();
      txtDoc.removeStr(start, end - start);
   }

   /**
    * Copies selected text to the system's clipboard
    */
   public void setClipboard() {
      String str = textArea.getSelectedText();
      if (str != null) {
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringSelection strSel = new StringSelection(str);
         clipboard.setContents(strSel, null);
      }
   }

   /**
    * Pastes text stored in the clipboard and replaces selected text
    */
   public void pasteText() {
      String clipboard = getClipboard();
      if (clipboard.length() == 0) {
         return;
      }
      String sel = textArea.getSelectedText();
      int pos = textArea.getSelectionStart();
      txtDoc.enableTypeEdit(false);
      if (sel != null) {
         txtDoc.removeStr(pos, sel.length());
      }
      EventQueue.invokeLater(() -> {
         txtDoc.insertStr(pos, clipboard);
         if (txtDoc.isCodingLanguage()) {
            txtDoc.colorSection(clipboard, pos);
            txtDoc.enableTypeEdit(true);
         }
      });
   }

   /**
    * Selects the entire text
    */
   public void selectAll() {
      textArea.selectAll();
   }

   /**
    * Sets a new indentation length
    */
   public void setNewIndentUnit() {
      String selectedNumber = JOptions.comboBoxRes(
            "Select the number of spaces:",
            "Indentation length",
            SPACE_NUMBER,
            String.valueOf(indentLength), false);

      if (selectedNumber != null) {
         indentLength = Integer.parseInt(selectedNumber);
         indentUnit = "";
         for (int i = 0; i < indentLength; i++) {
            indentUnit += " ";
         }
         txtDoc.setIndentUnit(indentUnit);
      }
   }

   /**
    * Indents text by one indentation unit
    */
   public void indent()  {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      txtDoc.enableTypeEdit(false);
      if (sel == null) {
         txtDoc.insertStr(start, indentUnit);
      }
      else {
         String[] selArr = sel.split("\n");
         int sum = 0;
         for (String s : selArr) {
            int lineLength = s.length() + indentLength;
            txtDoc.insertStr(start + sum, indentUnit);
            sum += lineLength + 1;
         }
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Reduces the indentation by one indentation unit
    */
   public void outdent() {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      String text = txtDoc.getText();
      boolean isAtLineStart
            = LinesFinder.lastNewline(text, start) > start - indentLength;
 
      txtDoc.enableTypeEdit(false);
      if (sel == null) {
         if (!isAtLineStart && start >= indentLength) {
            if (indentUnit.equals(text.substring(start - indentLength, start))) {
               txtDoc.removeStr(start - indentLength, indentLength);
            }
            else {
               textArea.setCaretPosition(start - indentLength);
            }
         }
      }
      else {
         String[] selArr = sel.split("\n");
         boolean corrNeeded
               = selArr[0].startsWith(" ")
               && !selArr[0].startsWith(indentUnit);

         if (corrNeeded) {
            int countSpaces = 0;
            while (selArr[0].charAt(countSpaces) == ' ') {
               countSpaces++;
            }
            int diff = indentLength - countSpaces;
            start -= diff;
            selArr[0] = text.substring(start, start + selArr[0].length() + diff);
         }
         if (selArr[0].startsWith(" ") && isIndentConsistent(selArr)) {
            int sum = 0;
            for (String s : selArr) {
               if (s.startsWith(indentUnit)) {
                  txtDoc.removeStr(start + sum, indentLength);
                  sum += (s.length() - indentLength) + 1;
               } else {
                  sum += s.length() + 1;
               }
            }
         }
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Clears trailing spaces
    */
   public void clearTrailingSpaces() {
      txtDoc.enableTypeEdit(false);
      String text = txtDoc.getText();
      String[] textArr = text.split("\n");
      int sum = 0;
      for (String s : textArr) {
         int startOfSpaces = startOfTrailingSpaces(s);
         int spacesLength = s.length() - startOfSpaces;
         txtDoc.removeStr(startOfSpaces + sum, spacesLength);
         sum += startOfSpaces + 1;
      }
      txtDoc.enableTypeEdit(true);
   }
   
   //
   //--private--//
   //

   private String getClipboard() {
      String inClipboard = "";
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Clipboard clipboard = toolkit.getSystemClipboard();
      DataFlavor flavor = DataFlavor.stringFlavor;
      try {
         inClipboard = (String) clipboard.getData(flavor);
      }
      catch (UnsupportedFlavorException | IOException e) {
         FileUtils.logStack(e);
      }
      return inClipboard;
   }

   private int startOfTrailingSpaces(String line) {
      char[] c = line.toCharArray();
      int i = 0;
      for (i = c.length - 1; i >= 0; i--) {
         if (c[i] != ' ') {
            break;
         }
      }
      return i + 1;
   }

   private boolean isIndentConsistent(String[] textArr) {
      boolean isConsistent = true;
      for (String s : textArr) {
         if (!s.startsWith(indentUnit)
                && s.length() > 0 && !s.matches("[\\s]+")) {
            isConsistent = false;
            break;
         }
      }
      if (!isConsistent) {
         JOptions.warnMessage("The selected text is not consistently"
               + " indented by at least one indentation length");
      }
      return isConsistent;
   }
}
