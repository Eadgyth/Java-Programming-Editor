package eg.edittools;

import java.io.File;
import java.awt.EventQueue;

//--Eadgyth--/
import eg.Preferences;
import eg.Languages;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The exchange of text between one <code>EditableDocument</code> set in
 * the constructor and a settable object of <code>EditableDocument</code>.
 * The first is named 'exchange document' and the second 'source document'.
 * The source document is the document currently viewed in the main editor
 * area
 */
public class TextExchange {

   private final static String F_SEP = File.separator;
   private final static File BACK_UP
         = new File(System.getProperty("user.dir")
         + F_SEP + "exchangeContent.txt");

   private final EditableDocument exchangeDoc;
   private final Preferences prefs = new Preferences();

   private EditableDocument sourceDoc;

   /**
    * @param exchangeDoc  the <code>EditableDocument</code> that
    * represents the exchange document
    */
   public TextExchange(EditableDocument exchangeDoc) {
      this.exchangeDoc = exchangeDoc;
      prefs.readPrefs();
      exchangeDoc.setIndentUnit(prefs.getProperty("indentUnit"));
      if (BACK_UP.exists()) {
         exchangeDoc.displayFileContent(BACK_UP);
      }
   }

   /**
    * Sets the <code>EditableDocument</code> that represents the source
    * document
    *
    * @param sourceDoc  the <code>EditableDocument</code>
    */
   public void setSourceDocument(EditableDocument sourceDoc) {
      this.sourceDoc  = sourceDoc;
   }

   /**
    * Copies text selected in the source document and inserts the text
    * at the caret position in the exchange document. The text to copy
    * replaces any selected text
    */
   public void copyTextFromSource() {
      String text = sourceDoc.docTextArea().getSelectedText();
      if (text == null) {
         String filename = sourceDoc.filename();
         if (filename.length() == 0) {
            filename = "unnamed";
         }
         Dialogs.warnMessage("No text is selected in " + filename);
         return;
      }
      copy(exchangeDoc, text);
   }

   /**
    * Copies text selected in the exchange document and inserts the text
    * at the caret position in the source document. The text to copy
    * replaces any selected text
    */
   public void copyTextToSource() {
      String text = exchangeDoc.docTextArea().getSelectedText();
      if (text == null) {
         Dialogs.warnMessage("No text is selected in the exchange editor");
         return;
      }
      copy(sourceDoc, text);
   }

   /**
    * Changes the language and switches on or off auto-indention depending
    * on whether or not the language is a coding language
    *
    * @param lang  the language which is a constant in {@link Languages}
    */
   public void changeCodeEditing(Languages lang) {
      exchangeDoc.changeLanguage(lang);
   }

   /**
    * Clears the exchange document
    */
   public void clear() {
      exchangeDoc.remove(0, exchangeDoc.docLength(), false);
   }

   /**
    * Saves the content contained in the exchange document to the file
    * 'exchangeContent.txt' which is saved in the program's directory.
    * It is asked before in a dialog to confirm that the content shall
    * be saved
    */
   public void save() {
      if (exchangeDoc.docLength() > 0) {
         int res = Dialogs.confirmYesNo("Keep content in exchange editor?");
         if (0 != res) {
            clear();
         }
      }
      exchangeDoc.saveCopy(BACK_UP);
   }
   
   //
   //--private--//
   //
   
   private void copy(EditableDocument destination, String text) {
      destination.docTextArea().requestFocusInWindow();
      String toReplace = destination.docTextArea().getSelectedText();
      int pos = destination.docTextArea().getSelectionStart();
      int end = destination.docTextArea().getSelectionEnd();
      int length = end - pos;
      destination.replace(pos, length, text);
   }
}
