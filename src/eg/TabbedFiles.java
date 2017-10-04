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

import eg.document.FileDocument;
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * The control of operations that require knowledge of the documents in
 * the tabs
 */
public class TabbedFiles implements Observer {

   private final FileDocument[] fDoc = new FileDocument[10];
   private final EditArea[] editArea = new EditArea[10];
   private final Preferences prefs = new Preferences();
   private final FileChooser fc;
   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final EditAreaFormat format;
   private final DocumentUpdate docUpdate;

   /*
    * The index of the selected tab */
   private int iTab = -1;
   /*
    * The language read from prefs or set in the Langugae menu */
   private Languages lang;

   public TabbedFiles(EditAreaFormat format, MainWin mw) {
      this.format = format;
      this.mw = mw;
      tabPane = mw.tabPane();
      docUpdate = new DocumentUpdate(mw, fDoc);
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
      fDoc[iTab].changeLanguage(lang);
   }

   /**
    * Opens a new 'unnamed' tab
    */
   public void openEmptyTab() {
      if (isTabOpenable()) {
         createDocument();
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
    * Saves the text content of the selected tab.
    * <p>{@link #saveAs(boolean)} is called if the selected tab is
    * unnamed or if the content was read in from a file that no longer
    * exists.
    *
    * @param update   if the view (e.g. tab title, file view) is
    * updated and it is tried to retrieve a project
    * @return  if the text content was saved
    */
   public boolean save(boolean update) {
      if (!fDoc[iTab].hasFile()
            || !fDoc[iTab].docFile().exists()) {
         return saveAs(update);
      }
      else {
         return fDoc[iTab].saveFile();
      }
   }

   /**
    * Saves the text content in all tabs. A warning is shown if any
    * files no longer exist on the hard drive.
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < nTabs(); i++) {
         if (fDoc[i].hasFile()) {
            if (fDoc[i].docFile().exists()) {
               fDoc[i].saveFile();
            }
            else {
               sb.append(fDoc[i].filename());
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
      File f = fc.fileToSave(fDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         if (0 == replaceOption(f)) {
            isSave = true;
         }
      }     
      isSave = isSave && fDoc[iTab].setFile(f);
      if (isSave && update) {
         docUpdate.changedFileUpdate(iTab, true);
         tabPane.changeTitle(iTab, fDoc[iTab].filename());
         prefs.storePrefs("recentPath", fDoc[iTab].dir());
      }   
      return isSave;
   }

   /**
    * Saves a copy of the content in the selected document to the file
    * that is selected in the file chooser.
    * <p> Method does not change the file of the document.
    */
   public void saveCopy() {
      File f = fc.fileToSave(fDoc[iTab].filepath());
      if (f == null) {
         return;
      }
      int res = 0;
      if (f.exists()) {
         res = replaceOption(f);
      }
      if (res == 0) {
         fDoc[iTab].saveCopy(f);
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
      boolean removable = fDoc[iTab].isContentSaved();
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
            openEmptyTab();
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
            fDoc[i] = null;
            editArea[i] = null;
            i--;
         }
         openEmptyTab();
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
      if (isFileOpen(f) || isMaxTabNumber()) {
         return;
      }
      boolean isBlankFirstTab = nTabs() == 1 && !fDoc[0].hasFile()
            && fDoc[0].getText().length() == 0;
      if (isBlankFirstTab) {
         removeTab();
      }
      if (isTabOpenable()) {
         createDocument(f);
      }
   }

   private void createDocument(File f) {
      try {
         mw.setBusyCursor(true);
         int n = nTabs();
         editArea[n] = format.createEditArea();
         fDoc[n] = new FileDocument(editArea[n], f);
         fDoc[n].setIndentUnit(prefs.getProperty("indentUnit"));
         addNewTab(fDoc[n].filename(), editArea[n].editAreaPanel());
         setUIUpdateListenersAt(n);
         docUpdate.changedFileUpdate(n, false);
         prefs.storePrefs("recentPath", fDoc[n].dir());
      }
      finally {
         mw.setBusyCursor(false);
      } 
   }
   
   private void createDocument() {
      int n = nTabs();
      editArea[n] = format.createEditArea();
      fDoc[n] = new FileDocument(editArea[n], lang);
      fDoc[n].setIndentUnit(prefs.getProperty("indentUnit"));
      addNewTab("unnamed", editArea[n].editAreaPanel());
      setUIUpdateListenersAt(n);
   }
   
   private void addNewTab(String filename, JPanel pnl) {
      JButton closeBt = new JButton(eg.ui.IconFiles.CLOSE_ICON);
      tabPane.addTab(filename, pnl, closeBt);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         close(true);
      });
   }
   
   private void setUIUpdateListenersAt(int i) {
      fDoc[i].setUndoableChangeListener(e ->
            mw.enableUndoRedo(e.canUndo(), e.canRedo()));
      fDoc[i].setTextSelectionListener(e ->
            mw.enableCutCopy(e.isSelection()));
   }
   
   private void removeTab() {
      int count = iTab;
      tabPane.removeTabAt(iTab);
      for (int i = count; i < nTabs(); i++) {
         fDoc[i] = fDoc[i + 1];
         editArea[i] = editArea[i + 1];
      }
      int n = nTabs();    
      fDoc[n] = null;
      editArea[n] = null;
      if (n > 0) {
         iTab = tabPane.getSelectedIndex();
         changedTabUpdate();
      }
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
      mw.setWordWrapSelected(format.isWordwrap());
      docUpdate.changedDocUpdate(iTab, nTabs());
   }
   
   private boolean isFileOpen(File f) {
      boolean isFileOpen = false;
      for (int i = 0; i < nTabs(); i++) {
         if (fDoc[i].filepath().equals(f.toString())) {
           isFileOpen = true;
           JOptions.warnMessage(f.getName() + " is already open.");
           break;
         }
      }
      return isFileOpen;
   }
   
   private boolean isMaxTabNumber() {
      boolean isMax = nTabs() == fDoc.length;
      if (isMax) {
         JOptions.warnMessage("The maximum number of tabs is reached.");
      }
      return isMax;
   }
   
   private int unsavedTab() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (!fDoc[i].isContentSaved()) {
            break;
         }
      }
      return i;
   }
   
   private boolean isTabOpenable() {
      boolean isOpenable = iTab == -1 || tabPane.isShowTabbar();
      if (!isOpenable) {
         close(false);
         isOpenable = iTab == -1;
      }
      return isOpenable;
   }

   private int saveOrCloseOption(int i) {
      String filename = fDoc[i].filename();
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

   private int nTabs() {
      return tabPane.getTabCount();
   }
}
