package eg.document;

import java.awt.EventQueue;

import javax.swing.JTextPane;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Preferences;
import eg.Languages;

import eg.utils.FileUtils;
import eg.ui.EditArea;

/**
 * Class represents the text document which a file can be assigned to
 */
public final class TextDocument {

   private final static String LINE_SEP = System.lineSeparator();
   private final static Preferences PREFS = new Preferences();

   private final JTextPane textArea;
   private final EditArea editArea;
   private final TypingEdit type;

   private File docFile = null;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   private String content = "";
   boolean isPlainText = false;

   /**
    * Creates a TextDocument
    * @param editArea  the reference to the {@link EditArea}
    */
  public TextDocument(EditArea editArea) {
      this.editArea = editArea;
      this.textArea = editArea.textArea();
      type = new TypingEdit(editArea);
      type.addAllLineNumbers(content);
      PREFS.readPrefs();
      String indentUnit = PREFS.getProperty("indentUnit");
      changeIndentUnit(indentUnit);    
   }

   /**
    * Creates a TextDocument with a specified language
    *
    * @param editArea  the reference to an {@link EditArea}
    * @param lang  the language that is one of the constants in
    * {@link Languages}
    */
   public TextDocument(EditArea editArea, Languages lang) {
      this(editArea);
      type.setUpEditing(lang);
      isPlainText = Languages.PLAIN_TEXT == lang;
   }

   /**
    * Returns this text area
    *
    * @return  this text area which is of type {@link EditArea}
    */
    public JTextPane getTextArea() {
       return editArea.textArea();
    }

   /**
    * Returns the name of this file
    *
    * @return  the String that represents the name of this file
    */
   public String filename() {
      return filename;
   }

   /**
    * Returns the filepath of this file
    *
    * @return  the String that represents the filepath of this file
    */
   public String filepath() {
      return filepath;
   }

   /**
    * Returns the directory of this file
    *
    * @return  the String that represents the directory of this file
    */
   public String dir() {
      return dir;
   }
   
   /**
    * If the set Language is any coding language, i.e. not plain text
    *
    * @return  the set Language is any coding language, i.e. not plain text
    */
   public boolean isCodingLanguage() {
      return !isPlainText;
   }

   /**
    * Sets the specified file and displays the file content
    *
    * @param file  the file whose content is displayed in this text area
    */
   public void openFile(File file) {
      if (this.filepath().length() != 0) {
         throw new IllegalStateException(
               "Illegal attempt to assign a file to a "
               + " TextDocument which a file was assigned to before");
      }
      assignFileStrings(file);
      EventQueue.invokeLater(() -> {
         displayFileContent();
         setLanguageBySuffix();
         setContent();
         type.addAllLineNumbers(content);
      });
   }

   /**
    * Saves the current content to this file
    */
   public void saveToFile() {
      saveToFile(docFile);
   }

   /**
    * Saves the current content to the specified file but does not
    * assign the file to this
    *
    * @param file  the file which the current content is saved to
    */
   public void saveCopy(File file) {
      saveToFile(file);
   }

   /**
    * Sets the specified file and saves the content of this text
    * area the file
    *
    * @param file  the new file which the current content is saved to
    */
   public void saveFileAs(File file) {
      assignFileStrings(file);
      saveToFile(file);
      setLanguageBySuffix();
   }

   /**
    * Returns if the content of this text area equals has been saved
    *
    * @return  if the content of this text area equals the content
    * since the last saving point
    */
   public boolean isContentSaved() {
      return content.equals(editArea.getDocText());
   }

   /**
    * Changes the indentation unit
    *
    * @param indentUnit  the String that consists of a certain number of
    * white spaces
    */
   public void changeIndentUnit(String indentUnit) {
      if (indentUnit == null || !indentUnit.matches("[\\s]+")) {
         throw new IllegalArgumentException("Argument indentUnit is"
               + " incorrect");
      }
      type.changeIndentUnit(indentUnit);
      PREFS.storePrefs("indentUnit", type.getIndentUnit());
   }

   /**
    * Returns the current indentation unit
    *
    * @return the current indentation unit
    */
   public String getIndentUnit() {
      return type.getIndentUnit();
   }

   /**
    * Enables/disables syntax coloring and auto-indentation
    *
    * @param isEnabled  true to enable coloring and auto-indentation,
    * false to disable. No effect if this language is plain text
    */
   public void enableTypeEdit(boolean isEnabled) {
      if (!isPlainText) {
         type.enableTypeEdit(isEnabled);
      }
   }

   /**
    * Colors a section of text which also may be the entire text
    *
    * @param section  a section of from the document. Null to color the entire
    * entire text
    * @param posStart  the pos within the entire text where the section to
    * be colored starts. Set to 0 if '{code section}' is null.
    */
   public void colorSection(String section, int posStart) {
      if (!isPlainText) {
         type.colorSection(getText(), section, posStart);
      }
   }

   /**
    * Performs an undo action
    */
   public void undo() {
      type.undo();
   }

   /**
    * Performs a redo action
    */
   public void redo() {
      type.redo();
   }

   /**
    * Returns the text in the document associated with this text area
    * @return  the text in the document associated with this text area
    */
   public String getText() {
      return editArea.getDocText();
   }

   /**
    * Inserts text in this text area
    *
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      editArea.insertStr(pos, toInsert);
   }

   /**
    * Removes text from this document
    *
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */
   public void removeStr(int start, int length) {
      editArea.removeStr(start, length);
   }

   /**
    * Asks this text area to gain the focus
    */
   public void requestFocus() {
      textArea.requestFocusInWindow();
   }

   /**
    * Changes the language if no file has been set
    * @param lang  the language which has one of the constant values
    * in {@link eg.Languages}
    */
   public void changeLanguage(Languages lang) {
      if (filename.length() == 0) {
         isPlainText = Languages.PLAIN_TEXT == lang;
         type.setUpEditing(lang);
      }
   }

   //
   //----private methods----//
   //

   private void displayFileContent() {
      type.enableEvaluateText(false);
      try (BufferedReader br = new BufferedReader(new FileReader(docFile))) {
         String line;
         while ((line = br.readLine()) != null) {
            insertStr(editArea.getDocText().length(), line + "\n");
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
      if (editArea.getDocText().endsWith("\n")) {
         editArea.removeStr(getText().length() - 1, 1);
      }
      editArea.textArea().setCaretPosition(0);
      type.enableEvaluateText(true);
   }

   /**
    * Saves the current content to this file
    */
   private void saveToFile(File file) {
      setContent();
      String[] lines = content.split("\n");
      try (FileWriter writer = new FileWriter(file)) {
         for (String s : lines) {
            writer.write(s + LINE_SEP);
         }
      }
      catch(IOException e) {
         FileUtils.logStack(e);
      }
   }

   private void setContent() {
      content = editArea.getDocText();
   }

   private void assignFileStrings(File filepath) {
      docFile = filepath;
      filename = filepath.getName();
      this.filepath = filepath.toString();
      dir = filepath.getParent();
   }

   private void setLanguageBySuffix() {
      String suffix = FileUtils.fileSuffix(filename);
      Languages lang;
      switch (suffix) {
         case "java":
           lang = Languages.JAVA;
           break;
         case "html": case "htm": case "xml":
            lang = Languages.HTML;
            break;
         case "pl": case "pm":
            lang = Languages.PERL;
            break;
         default:
            lang = Languages.PLAIN_TEXT;
      }
      isPlainText = Languages.PLAIN_TEXT == lang;
      type.setUpEditing(lang);
   }
}
