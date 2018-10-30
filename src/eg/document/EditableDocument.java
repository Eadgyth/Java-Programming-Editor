package eg.document;

import java.awt.EventQueue;

import javax.swing.JTextPane;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Languages;
import eg.LanguageSelector;
import eg.Constants;
import eg.utils.FileUtils;
import eg.ui.EditArea;

/**
 * Represents the document that is edited
 */
public final class EditableDocument {

   private final TypingEdit type;
   private final StyledText txt;

   private File docFile = null;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   String savedContent = "";
   private Languages lang;

   /**
    * Creates an <code>EditableDocument</code> that displays the content
    * of the specified file
    *
    * @param editArea  a new {@link EditArea}
    * @param f  the file
    */
   public EditableDocument(EditArea editArea, File f) {
      this(editArea);
      displayFileContent(f);
      setFileParams(f);
      savedContent = txt.text();
   }

   /**
    * Creates a blank <code>EditableDocument</code> with the specified
    * language
    *
    * @param editArea  a new {@link EditArea}
    * @param lang  the language
    */
   public EditableDocument(EditArea editArea, Languages lang) {
      this(editArea);
      this.lang = lang;
      type.setEditingMode(lang);
   }

   /**
    * Creates a blank <code>EditableDocument</code>
    *
    * @param editArea  a new {@link EditArea}
    */
   public EditableDocument(EditArea editArea) {
      txt = new StyledText(editArea.textArea());
      LineNumbers lineNum = new LineNumbers(editArea.lineNrArea(),
            editArea.lineNrWidth());

      type = new TypingEdit(txt, lineNum);
   }

   /**
    * Sets the refenence to <code>EditingStateReadable</code>
    *
    * @param esr  the {@link EditingStateReadable}
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
      type.setEditingStateReadable(esr);
   }

   /**
    * Sets the indent unit which consists of spaces
    *
    * @param indentUnit  the indend unit
    */
   public void setIndentUnit(String indentUnit) {
      type.setIndentUnit(indentUnit);
   }

   /**
    * Asks the text area that shows this document to gain focus and reads
    * the current editing state
    * @see TypingEdit#readEditingState()
    */
   public void setFocused() {
      txt.textArea().requestFocusInWindow();
      type.readEditingState();
   }

   /**
    * Gets the text area that displays the text
    *
    * @return  the text area
    */
    public JTextPane textArea() {
       return txt.textArea();
    }

   /**
    * Gets the last name in the path of this file
    *
    * @return  the filename; the empty string of no file is set
    */
   public String filename() {
      return filename;
   }

   /**
    * Gets the path of the parent directory of this file
    *
    * @return  the parent directory; the empty string of no file is set
    */
   public String dir() {
      return dir;
   }

   /**
    * Gets the path of this file
    *
    * @return  the path; the empty string of no file is set
    */
   public String filepath() {
      return filepath;
   }

   /**
    * Returns if a file is set
    *
    * @return  true if a file is set, false otherwise
    */
   public boolean hasFile() {
      return docFile != null;
   }

   /**
    * Gets this file or throws an exception if no file is set
    *
    * @return  the file
    */
   public File docFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been set");
      }
      return docFile;
   }

   /**
    * Saves the text content to this file or throws an exception if
    * no file is set
    *
    * @return  the boolen value that is true if the text content could
    * be saved
    */
   public boolean saveFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been assigned");
      }
      boolean isWritten = writeToFile(docFile);
      if (isWritten) {
         savedContent = txt.text();
         type.resetInChangeState();
      }
      return isWritten;
   }

   /**
    * Sets the specified file and saves the text content to the file.
    * A previously set file is replaced.
    *
    * @param f  the file
    * @return  true if the text content could be saved, false otherwise
    */
   public boolean setFile(File f) {
      setFileParams(f);
      setEditingMode(f);
      type.resetInChangeState();
      savedContent = txt.text();
      return writeToFile(f);
   }

   /**
    * Diplays the content of the specified file but does not set the file
    *
    * @param f  the file
    */
   public void displayFileContent(File f) {
      type.enableDocUpdate(false);
      displayFileContentImpl(f);
      type.enableDocUpdate(true);
      setEditingMode(f);
   }

   /**
    * Saves the text content to the specified file but does not set
    * the file
    *
    * @param f  the file
    * @return  true if the text content could be saved, false otherwise
    */
   public boolean saveCopy(File f) {
      return writeToFile(f);
   }

   /**
    * Returns if the text content equals the text stored at the
    * last saving point
    *
    * @return  true if saved, false otherwise
    */
   public boolean isSaved() {
      return txt.text().equals(savedContent);
   }

   /**
    * Gets the document text
    *
    * @return  the text
    */
   public String docText() {
      return txt.text();
   }

   /**
    * Gets the length of the document text
    *
    * @return  the length
    */
   public int docLength() {
      return txt.text().length();
   }

   /**
    * Gets this language
    *
    * @return  the language
    */
   public Languages language() {
      if (lang == null) {
         throw new IllegalStateException("A language is not set");
      }
      return lang;
   }

   /**
    * Gets the currently set indent unit
    *
    * @return  the indent unit
    */
   public String currIndentUnit() {
      return type.getIndentUnit();
   }

   /**
    * Changes the language or throws an exception if a file is set
    *
    * @param lang  the language to change to
    */
   public void changeLanguage(Languages lang) {
      if (null != docFile) {
         throw new IllegalStateException(
               "The language cannot be changed once a file is assigned.");
      }
      this.lang = lang;
      EventQueue.invokeLater(() -> type.setEditingMode(lang));
   }

   /**
    * Marks the beginning or the end of a merged undoable unit.
    * Calls {@link TypingEdit#disableBreakpointAdding(boolean)}
    *
    * @param b  true to begin meging, false to end merging
    */
   public void enableUndoMerging(boolean b) {
      type.disableBreakpointAdding(b);
   }

   /**
    * Inserts the specified string at the specified position
    *
    * @param pos  the position
    * @param s  the string
    */
   public void insert(int pos, String s) {
      type.insert(pos, s);
   }

    /**
    * Replaces a section with the specified string
    *
    * @param pos  the position where the section to be replaced starts
    * @param length  the length of the section
    * @param s  the string
    */
   public void replace(int pos, int length, String s) {
      type.replace(pos, length, s);
   }

   /**
    * Removes a section from the document
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param highlight  true to update syntax highlighting after the
    * removal, false otherwise. Is ignored if this language is not a
    * coding language
    */
   public void remove(int pos, int length, boolean highlight) {
      if (lang == Languages.NORMAL_TEXT) {
         highlight = false;
      }
      type.remove(pos, length, highlight);
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

   //
   //--private--/
   //

   public void displayFileContentImpl(File f) {
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         String line = br.readLine();
         String nextLine = br.readLine();
         while (null != line) {
            if (null == nextLine) {
               txt.append(line);
            }
            else {
               txt.append(line + "\n");
            }
            line = nextLine;
            nextLine = br.readLine();
         }
      }
      catch (IOException e) {
         FileUtils.log(e);
      }
   }

   private boolean writeToFile(File f) {
      boolean isWriteable = FileUtils.isWriteable(f);
      if (!isWriteable) {
          return false;
      }
      String[] lines = txt.text().split("\n");
      try (FileWriter writer = new FileWriter(f)) {
         for (String s : lines) {
            writer.write(s + Constants.LINE_SEP);
         }
         return true;
      }
      catch (IOException e) {
         FileUtils.log(e);
      }
      return false;
   }

   private void setEditingMode(File f) {
      lang = LanguageSelector.selectLanguage(f.getName());
      type.setEditingMode(lang);
   }

   private void setFileParams(File f) {
      docFile = f;
      filename = f.getName();
      filepath = f.toString();
      dir = f.getParent();
   }
}
