package eg;

import java.util.Observer;
import java.util.Observable;

import java.awt.EventQueue;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

import eg.ui.menu.ViewMenu;

/**
 * Controls operations that require knowledge of the documents in
 * the tabs
 */
public class TabbedFiles implements Observer {

   private final TextDocument[] txtDoc = new TextDocument[10];
   private final EditArea[] editArea = new EditArea[10];
   private final FileChooserOpen fo;
   private final FileChooserSave fs;
   private final Preferences prefs = new Preferences();
   private final MainWin mw;
   private final TabbedPane tabPane;
   private final ViewMenu vMenu;
   private final EditAreaFormat format;
   private final DocumentUpdate docUpdate;
   private final CurrentProject currProj;
   private final ChangeListener changeListener;

   /*
    * The index of the selected tab */
   private int iTab = 0;

   /*
    * The language read from prefs or set in the Edit>Langugae menu */
   Languages lang;

   public TabbedFiles(MainWin mw, EditAreaFormat format,
         CurrentProject currProj, DocumentUpdate docUpdate) {

      this.mw = mw;
      tabPane = mw.tabPane();
      vMenu = mw.menu().viewMenu();
      this.format = format;
      this.docUpdate = docUpdate;
      this.currProj = currProj;

      docUpdate.setDocumentArrays(txtDoc);
      currProj.setDocumentArr(txtDoc);
      format.setEditAreaArr(editArea);

      prefs.readPrefs();
      lang = Languages.valueOf(prefs.getProperty("language"));
      currProj.setLanguage(lang);
      String recentDir = prefs.getProperty("recentPath");
      fo = new FileChooserOpen(recentDir);
      fs = new FileChooserSave(recentDir);

      changeListener = (ChangeEvent changeEvent) -> {
         changeTabEvent(changeEvent);
      };
      tabPane.changeListen(changeListener);

      newEmptyTab();
   }

   /**
    * Sets the current language
    *
    * @param lang  the language that has one of the constant
    * values in {@link Languages}
    */
   public void changeLanguage(Languages lang) {
      this.lang = lang;
      currProj.setLanguage(lang);
      for (TextDocument t : txtDoc) {
         if (t != null) {
            t.changeLanguage(lang); // no effect if a file is assigned
         }
      }
      prefs.storePrefs("language", lang.toString());
   }

   /**
    * Sets the focus in the selected document
    */
   public void focusInSelectedTab() {
      txtDoc[iTab].requestFocus();
   }

   /**
    * Opens a new 'unnamed' tab
    */
   public void newEmptyTab() {
      int n = tabPane.nTabs();
      if (n == 1 && !vMenu.isTabItmSelected()) {
         close();
      }
      else {
         editArea[n] = format.createEditArea();
         txtDoc[n] = new TextDocument(editArea[n], lang);
         addNewTab("unnamed", editArea[n].textPanel(), n);
      }
   }

   /**
    * Opens a file selected in {@code FileTree}
    */
   @Override
   public void update(Observable o, Object arg) {
      File f = new File(arg.toString());
      open(f);
   }

   /**
    * Opens a file that is selected in the file chooser.
    * If a project is not yet defined it is tried to set active a
    * project
    */
   public void openFileByChooser() {
      File f = fo.chosenFile();     
      if (f == null) {
         return;
      }     
      if (!f.exists()) {
         JOptions.warnMessage(f.getName() + " was not found");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves the text content in the selected tab. {@link #saveAs(boolean)}
    * is called if the selected tab is unnamed or if the content was read in
    * from a file which no longer exists.
    *
    * @param replaceInTab  if a newly saved file is shown in the selected tab
    * and applied to define a project
    * @return  if the content was saved
    */
   public boolean save(boolean replaceInTab) {
      if (txtDoc[iTab].filename().length() == 0
            || !new File(txtDoc[iTab].filepath()).exists()) {
         return saveAs(replaceInTab);
      }
      else {
         return txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the text content in all tabs. A warning is shown if any
    * files no longer exist.
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < tabPane.nTabs(); i++) {
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
    * Saves the text content in the selected tab as a new file that
    * is specified in the file chooser
    *
    * @param replaceInTab  if the new file is shown in the selected tab
    * and applied to define a project
    * @return  if the content was saved
    */
   public boolean saveAs(boolean replaceInTab) {
      File f = fs.fileToSave(txtDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         JOptions.warnMessage(f.getName() + " already exists");
         isSave = false;
      }
      isSave = isSave && txtDoc[iTab].saveFileAs(f);
      if (isSave && replaceInTab) {
         setProject(iTab);
         tabPane.changeTabTitle(iTab, txtDoc[iTab].filename());
         mw.displayFrameTitle(txtDoc[iTab].filepath());
         prefs.storePrefs("recentPath", txtDoc[iTab].dir());
         EventQueue.invokeLater(() ->
               currProj.updateFileTree(txtDoc[iTab].dir()));
      }
      return isSave;
   }

   /**
    * Saves a copy of the content in the selected document to the file
    * that is selected in the file chooser.
    * <p> Method does not change the file of the document in the tab.
    */
   public void saveCopy() {
      File f = fs.fileToSave(txtDoc[iTab].filepath());
      if (f == null) {
         return;
      }
      int res = 0;
      boolean storable = true;
      if (f.exists()) {
         res = JOptions.confirmYesNo(f.getName() + " already exists.\n"
               + " Replace file?");
         if (res == 0) {
            storable = f.delete();
         }
      }
      if (res == 0 & storable) {
         txtDoc[iTab].saveCopy(f);
      }
      if (!storable) {
         JOptions.warnMessage(txtDoc[iTab].filepath()
               + "could not be replaced");
      }
   }

   /**
    * Prints the text content in the selected tab to a printer
    */
   public void print() {
      editArea[iTab].print();
   }

   /**
    * Closes a tab if the text content is saved or asks if closing
    * shall happen with or without saving
    */
   public void close() {
      if (txtDoc[iTab].isContentSaved()) {
         removeTab();
      }
      else {
         tabPane.selectTab(iTab);
         int res = saveOrCloseOption(iTab);
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               removeTab();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            removeTab();
         }
      }
   }

   /**
    * Closes all tabs or selects the first tab whose text content is found
    * unsaved
    */
   public void closeAll() {
      int count = unsavedTab();
      if (count == tabPane.nTabs()) {
         while(tabPane.nTabs() > 0) {
            tabPane.removeTab(iTab);
         }
         for (int i = 0; i < txtDoc.length; i++) {
            txtDoc[i] = null;
            editArea[i] = null;
         }
         newEmptyTab();
      }
      else {
         tabPane.selectTab(count);
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               closeAll();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            removeTab();
            closeAll();
         }
      }
   }

   /**
    * Exits the program or selects the first tab whose text content is
    * found unsaved
    */
   public void exit() {
      int count = unsavedTab();
      if (count == tabPane.nTabs()) {
         System.exit(0);
      }
      else {
         tabPane.selectTab(count);
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               exit();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            exit();
         }
      }
   }

   /**
    * Calls {@link #exit()} when the close window button is pressed
    */
   public WindowListener closeWindow = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         exit();
      }
   };

   //
   //---private methods --//
   //

   private void open(File f) {
      if (isFileOpen(f.toString())) {
         JOptions.warnMessage(f.getName() + " is open");
         return;
      }
      if (tabPane.nTabs() == txtDoc.length) {
         JOptions.warnMessage("The maximum number of tabs is reached.");
         return;
      }

      int iOpen = 0;
      boolean isOpenable
            =  tabPane.nTabs() == 1
            && txtDoc[iOpen].filename().length() == 0
            && editArea[iOpen].getDoc().getLength() == 0;
      if (isOpenable) {
         txtDoc[iOpen].openFile(f); // no new doc
         tabPane.changeTabTitle(iOpen, txtDoc[iOpen].filename());
      }
      else {
         if (vMenu.isTabItmSelected()) {
            iOpen = tabPane.nTabs();
            isOpenable = true;
         }
         else {
            isOpenable = saveAndRemoveFirstTab();
         }
         if (isOpenable) {
            createDocument(iOpen, f);
            addNewTab(txtDoc[iOpen].filename(),
                  editArea[iOpen].textPanel(), iOpen);
         }
      }
      if (isOpenable) {
         setProject(iOpen);
         mw.displayFrameTitle(txtDoc[iOpen].filepath());
         prefs.storePrefs("recentPath", txtDoc[iOpen].dir());
      }
   }

   private boolean isFileOpen(String fileToOpen) {
      boolean isFileOpen = false;
      for (int i = 0; i < tabPane.nTabs(); i++) {
         if (txtDoc[i].filepath().equals(fileToOpen)) {
           isFileOpen = true;
         }
      }
      return isFileOpen;
   }

   private void createDocument(int i, File f) {
      editArea[i] = format.createEditArea();
      txtDoc[i] = new TextDocument(editArea[i]);
      txtDoc[i].openFile(f);
   }

   private void addNewTab(String filename, JPanel pnl, int i) {
      JButton closeBt = new JButton();
      tabPane.addNewTab(filename, pnl, closeBt, i);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         close();
      });
   }
   
   private void setProject(int i) {
      currProj.setCurrTextDocument(i);
      currProj.retrieveProject();
   }

   private int saveOrCloseOption(int i) {
      String filename = txtDoc[i].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return JOptions.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }

   private int unsavedTab() {
      int count;
      for (count = 0; count < tabPane.nTabs(); count++) {
         if (!txtDoc[count].isContentSaved()) {
            break;
         }
      }
      return count;
   }
   
   private boolean saveAndRemoveFirstTab() {
      boolean remove = true;
      if (!txtDoc[0].isContentSaved()) {
         int res = saveOrCloseOption(0);
         if (res == JOptionPane.YES_OPTION) {
            remove = save(false);
         }
         else if (res == JOptionPane.CANCEL_OPTION) {
            remove = false;
         }
      }
      if (remove) {
        tabPane.removeTab(0);
      }
      return remove;
   }

   private void removeTab() {
      int count = iTab; // remember the index of the tab that will be removed
      tabPane.removeTab(iTab);
      for (int i = count; i < tabPane.nTabs(); i++) {
         txtDoc[i] = txtDoc[i + 1];
         editArea[i] = editArea[i+1];
      }
      if (tabPane.nTabs() > 0) {
         txtDoc[tabPane.nTabs()] = null;
         editArea[tabPane.nTabs()] = null;
         int index = tabPane.selectedIndex();
         mw.displayFrameTitle(txtDoc[index].filepath());
      }
      else {
         newEmptyTab();
      }
   }

   private void changeTabEvent(ChangeEvent changeEvent) {
      JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
      iTab = sourceTb.getSelectedIndex();
      if (iTab > -1) {
         focusInSelectedTab();
         format.setCurrEditArea(iTab);
         docUpdate.updateDocument(iTab);
         currProj.setCurrTextDocument(iTab);
         mw.displayFrameTitle(txtDoc[iTab].filepath());
         vMenu.enableTabItm(tabPane.nTabs() == 1);
      }
   }
}
