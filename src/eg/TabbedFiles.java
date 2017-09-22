package eg;

import java.util.Observer;
import java.util.Observable;

import java.awt.EventQueue;

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
import eg.ui.EditArea;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * The control of operations that require knowledge of the documents in
 * the tabs
 */
public class TabbedFiles implements Observer {

   private final TextDocument[] txtDoc = new TextDocument[10];
   private final EditArea[] editArea = new EditArea[10];
   private final Preferences prefs = new Preferences();
   private final FileChooser fc;
   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final EditAreaFormat format;
   private final DocumentUpdate docUpdate;

   /*
    * The index of the selected tab */
   private int iTab = 0;
   /*
    * The language read from prefs or set in the Langugae menu */
   private Languages lang;

   public TabbedFiles(MainWin mw, EditAreaFormat format) {
      this.mw = mw;
      tabPane = mw.tabPane();
      docUpdate = new DocumentUpdate(mw);
      docUpdate.setDocumentArr(txtDoc);     
      this.format = format;
      format.setEditAreaArr(editArea);
      prefs.readPrefs();
      lang = Languages.valueOf(prefs.getProperty("language"));
      String recentDir = prefs.getProperty("recentPath");
      fc = new FileChooser(recentDir);

      tabPane.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent ce) {
            changeTabEvent(ce);
         }
      });
      
      mw.winListen(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent we) {
            exit();
         }
      });
   }

   /**
    * Sets the current language
    *
    * @param lang  the language that has one of the constant
    * values in {@link Languages}
    */
   public void changeLanguage(Languages lang) {
      this.lang = lang;
      txtDoc[iTab].changeLanguage(lang); // no effect if a file is already assigned
   }

   /**
    * Opens a new 'unnamed' tab
    */
   public void createEmptyTab() {
      boolean ok = true;
      if (nTabs() == 1 && !tabPane.isShowTabbar()) {
         close(false);
         ok = nTabs() == 0;
      }
      if (ok) {
         int n = nTabs();
         editArea[n] = format.createEditArea();
         txtDoc[n] = new TextDocument(editArea[n], lang);
         addNewTab("unnamed", editArea[n].textPanel());
         docUpdate.setUIUpdateListenersAt(n);
      }
   }

   /**
    * Opens a file selected in <code>FileTree</code>
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
      File f = fc.fileToOpen();
      if (f == null) {
         return;
      }
      if (!f.exists()) {
         JOptions.warnMessage(f.getName() + "\nThe file was not found.");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves the text content in the selected tab.
    * <p>{@link #saveAs(boolean)} is called if the selected tab is
    * unnamed or if the content was read in from a file that no
    * longer exists.
    *
    * @param update   if the view (e.g. tab title, file view) is
    * updated and it is tried to retrieve a project
    * @return  if the text content was saved
    */
   public boolean save(boolean update) {
      if (txtDoc[iTab].filename().length() == 0
            || !txtDoc[iTab].docFile().exists()) {
         return saveAs(update);
      }
      else {
         return txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the text content in all tabs. A warning is shown if any
    * files no longer exist on the hard drive.
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < nTabs(); i++) {
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
         sb.insert(0, "These files could not be found:\n");
         JOptions.warnMessage(sb.toString());
      }
   }

   /**
    * Saves the text content in the selected tab as a new file that
    * is specified in the file chooser or asks to replace the file
    * if it exists
    *
    * @param update  if the view (e.g. tab title, file view) is
    * updated and it is tried to retrieve a project
    * @return  if the text content was saved
    */
   public boolean saveAs(boolean update) {
      File f = fc.fileToSave(txtDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         if (0 == replaceOption(f)) {
            isSave = true;
         }
      }     
      isSave = isSave && txtDoc[iTab].saveFileAs(f);
      if (isSave && update) {
         docUpdate.changedFileUpdate(iTab, nTabs(), true);
         tabPane.changeTitle(iTab, txtDoc[iTab].filename());
         prefs.storePrefs("recentPath", txtDoc[iTab].dir());
      }   
      return isSave;
   }

   /**
    * Saves a copy of the content in the selected document to the file
    * that is selected in the file chooser.
    * <p> Method does not change the file of the document.
    */
   public void saveCopy() {
      File f = fc.fileToSave(txtDoc[iTab].filepath());
      if (f == null) {
         return;
      }
      int res = 0;
      if (f.exists()) {
         res = replaceOption(f);
      }
      if (res == 0) {
         txtDoc[iTab].saveCopy(f);
      }
   }

   /**
    * Closes a tab if the text content is saved or asks if closing
    * shall happen with or without saving
    *
    * @param createEmptyTab  true to create a new empty tab when all tabs
    * are closed
    */
   public void close(boolean createEmptyTab) {
      boolean removable = txtDoc[iTab].isContentSaved();
      if (!removable) {
         int res = saveOrCloseOption(iTab);
         if (res == JOptionPane.YES_OPTION) {
            removable = save(false);
         }
         else {
            removable = res == JOptionPane.NO_OPTION;
         }
      }
      if (removable) {
         removeTab();
         if (createEmptyTab && nTabs() == 0) {
            createEmptyTab();
         }
      }
   }

   /**
    * Closes all tabs or selects the first tab whose text content is found
    * unsaved
    */
   public void closeAll() {
      int count = unsavedTab();
      if (count == nTabs()) {
         int i = count - 1;
         while(i > -1) {     
            tabPane.removeTabAt(i);
            txtDoc[i] = null;
            editArea[i] = null;
            i--;
         }
         createEmptyTab();
      }
      else {
         tabPane.setSelectedIndex(count);
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
      if (count == nTabs()) {
         prefs.storePrefs("language", lang.toString());
         System.exit(0);
      }
      else {
         tabPane.setSelectedIndex(count);
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               exit();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            removeTab();
            exit();
         }
      }
   }

   /**
    * Prints the text content in the selected tab to a printer
    */
   public void print() {
      editArea[iTab].print();
   }

   //
   //--private methods--//
   //

   private void open(File f) {
      if (isMaxTabNumber() || isFileOpen(f)) {
         return;
      }

      int iOpen = 0;
      boolean isOpenable
            =  nTabs() == 1
            && txtDoc[iOpen].filename().length() == 0
            && txtDoc[iOpen].getText().length() == 0;
      if (isOpenable) {
         openFile(iOpen, f); // no new doc
         tabPane.changeTitle(iOpen, txtDoc[iOpen].filename());
      }
      else {
         if (tabPane.isShowTabbar()) {
            iOpen = nTabs();
            isOpenable = true;
         }
         else {
            close(false);
            isOpenable = iTab == -1;
         }
         if (isOpenable) {
            createDocument(iOpen, f);
            addNewTab(txtDoc[iOpen].filename(), editArea[iOpen].textPanel());
         }
      }
      if (isOpenable) {
         docUpdate.changedFileUpdate(iOpen, nTabs(), false);
         prefs.storePrefs("recentPath", txtDoc[iOpen].dir());
      }
   }

   private boolean isFileOpen(File f) {
      boolean isFileOpen = false;
      for (int i = 0; i < nTabs(); i++) {
         if (txtDoc[i].filepath().equals(f.toString())) {
           isFileOpen = true;
           JOptions.warnMessage(f.getName() + " is already open.");
           break;
         }
      }
      return isFileOpen;
   }
   
   private boolean isMaxTabNumber() {
      boolean isMax = nTabs() == txtDoc.length;
      if (isMax) {
         JOptions.warnMessage("The maximum number of tabs is reached.");
      }
      return isMax;
   }

   private void createDocument(int i, File f) {
      editArea[i] = format.createEditArea();
      txtDoc[i] = new TextDocument(editArea[i]);
      docUpdate.setUIUpdateListenersAt(i);
      openFile(i, f);
   }
   
   private void openFile(int i, File f) {
      try {
         mw.setBusyCursor(true);
         txtDoc[i].openFile(f);
      }
      finally {
         EventQueue.invokeLater(() -> mw.setBusyCursor(false));
      }
   }     

   private void addNewTab(String filename, JPanel pnl) {
      JButton closeBt = new JButton(eg.ui.IconFiles.CLOSE_ICON);
      tabPane.addTab(filename, pnl, closeBt);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         close(true);
      });
   }
   
   private void removeTab() {
      int count = iTab; // remember the index of the tab that will be removed
      tabPane.removeTabAt(iTab);
      for (int i = count; i < nTabs(); i++) {
         txtDoc[i] = txtDoc[i + 1];
         editArea[i] = editArea[i + 1];
      }
      int n = nTabs();
      if (n > 0) {
         txtDoc[n] = null;
         editArea[n] = null;
         iTab = tabPane.getSelectedIndex();
         changedTabUpdate();
      }
   }
   
   private int unsavedTab() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (!txtDoc[i].isContentSaved()) {
            break;
         }
      }
      return i;
   }

   private int saveOrCloseOption(int i) {
      String filename = txtDoc[i].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return JOptions.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }
   
   private int replaceOption(File f) {
      return JOptions.confirmYesNo(f.getName()
             + "\nThe file already exists. Replace file?");
   }

   private void changeTabEvent(ChangeEvent changeEvent) {
      JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
      iTab = sourceTb.getSelectedIndex();
      if (iTab > -1) {
         changedTabUpdate();
      }
   }
   
   private void changedTabUpdate() {
      format.setEditAreaAt(iTab);
      docUpdate.changedDocUpdate(iTab, nTabs());
   }

   private int nTabs() {
      return tabPane.getTabCount();
   }
}
