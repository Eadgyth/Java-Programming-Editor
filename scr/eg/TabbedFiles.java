package eg;

import java.util.Observer;
import java.util.Observable;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.document.TextDocument;
import eg.ui.MainWin;
import eg.ui.TabbedPane;
import eg.ui.EditArea;

/**
 * Controls file operations that require knowledge of the opened tabs and 
 * the selected tab
 */
public class TabbedFiles implements Observer{

   private final TextDocument[] txtDoc = new TextDocument[10];
   private final EditArea[] edArea = new EditArea[10];
   private final FileChooserOpen fo;
   private final FileChooserSave fs;
   private final Preferences prefs = new Preferences();   
   private final TabbedPane tp;
   private final DisplaySetter displSet;
   private final FontSetter fontSet;
   private final DocumentUpdate docUpdate;
   private final ChangeListener changeListener;
   private final CurrentProject currProj;

   /* The index of the selected tab */
   private int iTab = 0;
   
   public TabbedFiles(TabbedPane tp, DisplaySetter displSet,
         CurrentProject currProj, DocumentUpdate docUpdate) {

      this.tp = tp;
      this.displSet = displSet;
      this.docUpdate = docUpdate;
      this.currProj = currProj;

      fontSet = new FontSetter(edArea);
      currProj.setDocumentArr(txtDoc);
      docUpdate.setDocumentArrays(txtDoc, edArea);
      changeListener = (ChangeEvent changeEvent) -> {
         changeTabEvent(changeEvent);
      };
      tp.changeListen(changeListener);
      prefs.readPrefs();
      String recentDir = prefs.getProperty("recentPath");
      fo = new FileChooserOpen(recentDir);
      fs = new FileChooserSave(recentDir);
      newEmptyTab();
   }

   /**
    * Returns this array of type {@code TextDocument}
    * @return  this array of type {@link TextDocument}
    */
   public TextDocument[] getTextDocument() {
      return txtDoc;
   }
   
   /**
    * Returns this array of type {@code EditArea}
    * @return  this array of type {@link EditArea}
    */
   public EditArea[] getEditArea() {
      return edArea;
   }
   
   /**
    * Returns this reference to the {@code FontSetter}
    * @return  this reference to the {@link FontSetter}
    */
   public FontSetter getFontSet() {
      return fontSet;
   }

   /**
    * Sets the focus in the selected document
    */
   public void focusInSelectedTab() { 
      txtDoc[iTab].requestFocus();
   }

   /**
    * Opens a new 'unnamed' Tab to which no file is assigned
    */
   public final void newEmptyTab() {
      edArea[tp.nTabs()] = createEditArea();
      txtDoc[tp.nTabs()] = new TextDocument(edArea[tp.nTabs()]);
      addNewTab("unnamed", edArea[tp.nTabs()].scrolledArea(),
            tp.nTabs());       
   }
   
   /**
    * Opens a file selected in {@code FileTree} in a new tab
    */
   @Override
   public void update(Observable o, Object arg) {
      File f = new File(arg.toString());
      open(f);
   }

   /**
    * Opens a file that is selected in the file chooser.
    * <p>
    * If a project is not yet defined it is tried to set active a
    * project 
    */
   public void openFileByChooser() {
      File f = fo.chosenFile();     
      if (f == null) {
         return;
      }     
      if (!f.exists()) {
         JOptions.warnMessage(f.getName() + " is was not found");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves the text content of the selected document.
    * <p>
    * If the selected document is unnamed {@link #saveAs()} is used. 
    * <p>
    * 'Save-as-mode' also applies if a file has been assigned to the
    * selected document but the file does not exists anymore.
    */
   public void save() {  
      if (txtDoc[iTab].filename().length() == 0 
            || !new File(txtDoc[iTab].filepath()).exists()) {
         saveAs();
      }
      else {
         txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the text content in all tabs.
    * <p>
    * A warning is shown if files no longer exist.
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < tp.nTabs(); i++) {
         if (txtDoc[i].filename().length() > 0) {
            if (new File(txtDoc[i].filepath()).exists()) {
               txtDoc[i].saveToFile();
            }
            else {
               sb.append(txtDoc[i].filename());
               sb.append("\n");
            }
         } 
      }
      if (sb.length() > 0) {
         sb.insert(0, "These files were not found:\n");
         JOptions.warnMessage(sb.toString());
      }
   }

   /**
    * Saves the text content of the selected document as new file that
    * is specified in the file chooser
    */
   public void saveAs() {
      File f = fs.fileToSave(txtDoc[iTab].filepath());
      if (f == null) {
         return;
      }
      if (f.exists()) {
         JOptions.warnMessage(f.getName() + " already exists");
      }
      else {      
         txtDoc[iTab].saveFileAs(f);
         currProj.setDocumentIndex(iTab);
         currProj.retrieveProject();
         currProj.updateFileTree(txtDoc[iTab].dir());
         tp.changeTabTitle(iTab, txtDoc[iTab].filename());
         displSet.displayFrameTitle(txtDoc[iTab].filepath());
      }
   }
   
   /**
    * Prints the text content of the selected document to a printer
    */
   public void print() {
      txtDoc[iTab].print();
   }

   /**
    * Closes a tab if the text content of its {@code TextDocument} is saved
    * or asks if closing shall happen with or without saving
    */
   public void tryClose() {
      if (txtDoc[iTab].isContentSaved()) {
         close();
      }
      else {                 
         int res = saveOrCloseOption(iTab);
         if (res == JOptionPane.YES_OPTION) {
            if (txtDoc[iTab].filename().length() == 0
                  || !new File(txtDoc[iTab].filepath()).exists()) {
               saveAs();
            }
            else {
               txtDoc[iTab].saveToFile();
               close();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
         }
      } 
   }
   
   /**
    * Closes all tabs or selects the first tab whose text content is found
    * unsaved
    */
   public void tryCloseAll() {
      int count = unsavedTab();
      if (count == tp.nTabs()) {     
         while(tp.nTabs() > 0) {
            txtDoc[iTab] = null;
            edArea[iTab] = null;
            tp.removeTab(iTab);
         }
         newEmptyTab();
      }
      else {
         tp.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            save();
            tryCloseAll();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryCloseAll();
         }
      }    
   }

   /**
    * Exits the program or selects the first tab whose text content is found
    * unsaved
    */
   public void tryExit() {
      int count = unsavedTab();
      if (count == tp.nTabs()) {
         storeToPrefs();
         System.exit(0);
      }
      else {
         tp.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            save();
            tryExit();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryExit();
         }
      }
   }

   //
   //---private methods --//
   //

   private void open(File file) {
      if (isFileOpen(file.toString())) {
         JOptions.warnMessage(file.getName() + " is open");
         return;
      }
      if (tp.nTabs() == txtDoc.length) {
         JOptions.warnMessage("The maximum number of tabs is reached.");
         return;
      }
      
      int openIndex = 0;
      boolean isUnnamedBlank = txtDoc[openIndex].filename().length() == 0
            && txtDoc[openIndex].textLength() == 0;
      if (isUnnamedBlank && tp.nTabs() == 1) { 
         txtDoc[openIndex].openFile(file);
      }
      else {
         openIndex = tp.nTabs();       
         edArea[openIndex] = createEditArea();
         txtDoc[openIndex] = new TextDocument(edArea[openIndex]);
         txtDoc[openIndex].openFile(file);
      }
      addNewTab(txtDoc[openIndex].filename(),
      edArea[openIndex].scrolledArea(), openIndex);
      displSet.displayFrameTitle(txtDoc[openIndex].filepath());         
      currProj.retrieveProject();
   }

   private boolean isFileOpen(String fileToOpen) {
      boolean isFileOpen = false;
      for (int i = 0; i < tp.nTabs(); i++) {
         if (txtDoc[i].filepath().equals(fileToOpen)) {
           isFileOpen = true;
         }
      }
      return isFileOpen;
   }
   
   private EditArea createEditArea() {
      boolean isWordWrap = displSet.isWordWrap();
      boolean isLineNr = displSet.isLineNumbers();
      String font = fontSet.getFont();
      int fontSize = fontSet.getFontSize();
      return new EditArea(isWordWrap, isLineNr, font, fontSize);
   }

   private void addNewTab(String filename, JPanel pnl, int index) {
      JButton closeBt = new JButton();
      tp.addNewTab(filename, pnl, closeBt, index);
      closeBt.addActionListener(e -> {
         iTab = tp.iTabMouseOver();
         tryClose();
      });
   }
   
   private int saveOrCloseOption(int index) {
      String filename = txtDoc[index].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return JOptions.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }
   
   private int unsavedTab() {
      int count;
      for (count = 0; count < tp.nTabs(); count++) { 
         if (!txtDoc[count].isContentSaved()) {
            break;
         }
      }
      return count;
   }

   private void close() {
      int count = iTab; // remember the index of the tab that will be removed
      tp.removeTab(iTab);
      for (int i = count; i < tp.nTabs(); i++) {
         txtDoc[i] = txtDoc[i + 1];
         edArea[i] = edArea[i+1];
      }
      if (tp.nTabs() > 0) {
         txtDoc[tp.nTabs()] = null;
         edArea[tp.nTabs()] = null;
         int index = tp.selectedIndex();
         displSet.displayFrameTitle(txtDoc[index].filepath());
      }
      else { 
         newEmptyTab();
      }     
   }
   
   private void changeTabEvent(ChangeEvent changeEvent) {
      JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
      iTab = sourceTb.getSelectedIndex();
      if (iTab > -1) {
         txtDoc[iTab].requestFocus();
         docUpdate.updateDocument(iTab);
         currProj.setDocumentIndex(iTab);
         displSet.displayFrameTitle(txtDoc[iTab].filepath());
      }
   }
   
   private void storeToPrefs() {
      prefs.storePrefs("font", fontSet.getFont());
      prefs.storePrefs("fontSize", String.valueOf(fontSet.getFontSize()));
      String dir = txtDoc[tp.nTabs() - 1].dir();
      if (dir.length() > 0) {
         prefs.storePrefs("recentPath", txtDoc[tp.nTabs() - 1].dir());
      }
      displSet.storeToPrefs();
   }
}
