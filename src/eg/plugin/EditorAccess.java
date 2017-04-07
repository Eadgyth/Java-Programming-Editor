package eg.plugin;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.document.TextDocument;

/**
 * Makes accessible for a plugin methods to work with the text that is
 * currently viewed in the main window's text area (i.e., the opened or
 * tab selected document).
 * <p>
 * Also includes the possibility to add a {@code Component} to the 'function
 * panel' in the main window.
 */
public class EditorAccess {
   
   MainWin mw;
   TextDocument txtDoc;

   EditorAccess(MainWin mw) {
      this.mw = mw;
   }
   
   void setTextDocument(TextDocument txtDoc) {
      this.txtDoc = txtDoc;
   }
   
   /**
    * Returns the {@code JTextPane} object that represents the current
    * text area view
    *
    * @return  the {@code JTextPane} object that represents the current
    * text area view
    */
   public JTextPane textArea() {
      return txtDoc.getTextArea();
   }
   
   /**
    * Returns the text content in the currently viewed text area
    *
    * @return  the text content in the currently viewed text area.
    * The returned text is contained in the {@code StyledDocument}
    * associated with the {@code JTextPane} that displays the text
    */
   public String getText() {
      return txtDoc.getText();
   }
   
   /**
    * Inserts text in the document that is currently viewed
    *
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      txtDoc.insertStr(pos, toInsert);
   }

   /**
    * Removes text from the document that is currently viewed
    *
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */  
   public void removeStr(int start, int length) {
      txtDoc.removeStr(start, length);
   }
   
   /**
    * Asks the currently viewed text area to gain the focus
    */
   public void requestFocus() {
      txtDoc.requestFocus();
   }
   
   /**
    * Adds a {@code Component} to the 'function panel' in the main window
    *
    * @param c  the {@code Component} that is added
    * @param title  the title for the function
    * @see MainWin#addToFunctionPanel(java.awt.Component, String)
    */
   public void addToFunctionPanel(java.awt.Component c, String title) {
      mw.addToFunctionPanel(c, title);
   }
}
