package eg;

import java.util.Observer;
import java.util.Observable;

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
import eg.utils.ShowJOption;
import eg.utils.FileUtils;
import eg.document.TextDocument;
import eg.ui.MainWin;
import eg.ui.TabbedPane;
import eg.ui.filetree.FileTree;
import eg.projects.*;
import eg.plugin.PluginStarter;

/**
 * Controls actions that require knowledge of all open tabs and the
 * selected tab
 */
public class TabActions implements Observer{

   private final TabbedPane tabPane = new TabbedPane();
   private final TextDocument[] txtDoc = new TextDocument[20];
   private final FileChooserOpen fo = new FileChooserOpen();
   private final FileChooserSave fs = new FileChooserSave();
   private final Preferences prefs = new Preferences();   
   private final MainWin mw;
   private final FileTree fileTree;
   private final ProjectFactory projFact;
   private final Edit edit;
   private final PluginStarter plugStart;
   private final ChangeListener changeListener;
   private final WindowListener winListener;

   private ProjectActions projAct;

   /* The index of the selected tab */
   private int iTab = 0;

   /* Set if a java project is defined*/
   private boolean isProjectSet = false;
   
   public TabActions(MainWin mw, Edit edit,
          FileTree fileTree, ProjectFactory projFact,
          PluginStarter plugStart) {
       
      this.winListener = new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent we) {
            tryExit();
         }
      };    
      this.changeListener = (ChangeEvent changeEvent) -> {
         changeTabEvent(changeEvent);
      };

      this.mw = mw;
      this.fileTree = fileTree;
      this.projFact = projFact;
      this.edit = edit;
      this.plugStart = plugStart;

      mw.addTextArea(tabPane.tabbedPane());
      mw.winListen(winListener);
      tabPane.changeListen(changeListener);
      prefs.readPrefs();  
      newEmptyTab();
   }

   /**
    * Returns the array of type {@code TextDocument}
    * @return  this array of type {@link TextDocument}
    */
   public TextDocument[] getTextDocument() {
      return txtDoc;
   }

   public void focusInSelectedTab() { 
      txtDoc[iTab].requestFocus();
   }
   
   @Override
   public void update(Observable o, Object arg) {
      File f = new File(arg.toString());
      open(f);
   }

   public void newEmptyTab() {
      txtDoc[tabPane.tabCount()] = new TextDocument();
      addNewTab("unnamed", txtDoc[tabPane.tabCount()].scrolledTextArea(),
            tabPane.tabCount());       
   }

   /**
    * Opens in a new tab a file that is selected in the file chooser
    */
   public void openFileByChooser() {
      File f = fo.chosenFile();     
      if (f == null) { // if cancel or close window clicked
         return;
      }     
      if (!f.exists()) {
         ShowJOption.warnMessage(f.getName() + " is was not found");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves text area's content of the selected tab or calls
    * {@link #saveAs()} if the tab is unnamed
    */
   public void saveOrSaveAs() {  
      if (txtDoc[iTab].filename().length() == 0) {
         saveAs();
      }
      else {
         txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the content of all tabs
    */
   public void saveAll() {
      for (int count = 0; count < tabPane.tabCount(); count++) {
         if (txtDoc[count].filename().length() > 0) {
            txtDoc[count].saveToFile();
         }
      }
   }

   /**
    * Saves the file of a selected tab as a new file selected
    * in file chooser
    */
   public void saveAs() {
      File f = fs.fileToSave();
      if (f == null) {
         return; // if cancel or close window clicked
      }
      if (f.exists()) {
         ShowJOption.warnMessage(f.getName() + " already exists");
      }
      else {      
         txtDoc[iTab].saveFileAs(f);
         if (!isProjectSet) {
            retrieveProject(txtDoc[iTab].dir());
         }
         else {
            if (projAct.isInProjectPath(txtDoc[iTab].dir())) {
               fileTree.addFile(txtDoc[iTab].filepath());
            }
         }
         tabPane.changeTabTitle(iTab, txtDoc[iTab].filename());
         mw.displayFrameTitle(txtDoc[iTab].filepath());
         prefs.storePrefs("recentPath", txtDoc[iTab].dir());
      }
   }

   /**
    * Closes a tab if the content of the text document is saved
    * or asks if closing shall happen with or without saving
    */
   public void tryClose() {
      if (txtDoc[iTab].isContentSaved()) {
         close();
      }
      else {                 
         int res = saveOrCloseOption(iTab);
         if (res == JOptionPane.YES_OPTION) {
            if (txtDoc[iTab].filename().length() == 0) {
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
    * Closes all tabs or selects the first tab which is found
    * unsaved
    */
   public void tryCloseAll() {
      int count = unsavedTab();
      if (count == tabPane.tabCount()) {     
         while(tabPane.tabCount() > 0) {
            tabPane.removeTab(iTab);
         }
         newEmptyTab();
      }
      else {
         tabPane.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            saveOrSaveAs();
            tryCloseAll();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryCloseAll();
         }
      }    
   }

   /**
    * Exits the programm or selects the first tab which is found
    * unsaved
    */
   public void tryExit() {
      int count = unsavedTab();
      if (count == tabPane.tabCount()) {     
         System.exit(0);
      }
      else {
         tabPane.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            saveOrSaveAs();
            tryExit();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryExit();
         }
      }
   }

   /**
    * Sets visible the settings window of a {@code ProjectActions} object <p>
    * See also {@link SettingsWin} 
    */
   public void openProjectSetWin() {
      if (txtDoc[iTab].filename().length() == 0) {
         ShowJOption.infoMessage("A project cannot be set for an unnamed tab");
         return;
      }
      
      if (isProjectSet && projAct.isInProjectPath(txtDoc[iTab].dir())) {
         projAct.makeSetWinVisible(true);
      }
      else {
         ProjectActions projNew 
               = projFact.getProjAct(FileUtils.extension(txtDoc[iTab].filepath()));
         if (projNew == null) {
            ShowJOption.infoMessage("A project cannot be set for this file type");
         }
         else {         
            int result = 0;
            if (isProjectSet) {
               result = ShowJOption.confirmYesNo("Change project ?");
            }
            if (result == 0) {
               projNew.makeSetWinVisible(true);
               projNew.getSetWin().okAct(e -> configureProject(projNew));
            }
         }
      }
   }
   
   /**
    * Saves all open files and compiles this project
    */
   public void saveAndCompile() {
      saveAll();
      projAct.compile();
   }
   
   /**
    * Runs this project
    */
   public void runProj() {
      projAct.runProject();
   }
   
   /**
    * Creates a build of this project
    */
   public void buildProj() {
      projAct.build();
   }

   //
   //---private methods --//
   //

   private void open(File file) {
      if (isFileOpen(file.toString())) {
         ShowJOption.warnMessage(file.getName() + " is open");
      }
      else {
         int openIndex = 0;
         boolean isUnnamedBlank = txtDoc[openIndex].filename().length() == 0
               && txtDoc[openIndex].getText().length() == 0;
         if (isUnnamedBlank && tabPane.tabCount() == 1) { 
            txtDoc[openIndex].openFile(file);
         }
         else {
            openIndex = tabPane.tabCount();
            if (openIndex < txtDoc.length) {
               txtDoc[openIndex] = new TextDocument();
               txtDoc[openIndex].openFile(file);
            }
            else {
               ShowJOption.warnMessage("Could not open " + file.getName()
                  + ". The maximum number of tabs is reached.");
               return;
            }
         }

         addNewTab(txtDoc[openIndex].filename(),
               txtDoc[openIndex].scrolledTextArea(), openIndex);
         mw.displayFrameTitle(txtDoc[openIndex].filepath());
           
         if (!isProjectSet) {
            retrieveProject(txtDoc[openIndex].dir());
         }      
         prefs.storePrefs("recentPath", txtDoc[openIndex].dir());
      }
   }

   private boolean isFileOpen(String fileToOpen) {
      boolean isFileOpen = false;
      for (int i = 0; i < tabPane.tabCount(); i++) {
         if (txtDoc[i].filepath().equals(fileToOpen)) {
           isFileOpen = true;
         }
      }
      return isFileOpen;
   }
   
   private void addNewTab(String filename, JPanel pnl, int index) {
      JButton closeBt = new JButton();
      tabPane.addNewTab(filename, pnl, closeBt, index);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         tryClose();
      });
   }
   
   private int saveOrCloseOption(int index) {
      String filename = txtDoc[index].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return ShowJOption.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }
   
   private int unsavedTab() {
      int count;
      for (count = 0; count < tabPane.tabCount(); count++) { 
         if (!txtDoc[count].isContentSaved()) {
            break;
         }
      }
      return count;
   }

   private void close() {
      int count = iTab; // remember the index of the tab that will be removed
      tabPane.removeTab(iTab);
      for (int i = count; i < tabPane.tabCount(); i++) {
         txtDoc[i] = txtDoc[i + 1];
      }
      if (tabPane.tabCount() > 0) {
         int index = tabPane.selectedIndex();
         mw.displayFrameTitle(txtDoc[index].filepath());
      }
      else { 
         newEmptyTab();
      }     
   }
   
   private void retrieveProject(String newPath) {
      ProjectActions prNew
            = projFact.getProjAct(FileUtils.extension(txtDoc[iTab].filepath()));
      if (prNew != null) {
         projAct = prNew;
         projAct.getSetWin().okAct(e -> configureProject(projAct));
         projAct.findPreviousProjectRoot(newPath);
         if (projAct.getProjectRoot().length() > 0) {
            isProjectSet = true;
            updateProjectDisplay(projAct.getProjectRoot());
         }
      }
   }

   private void configureProject(ProjectActions proj) {
      proj.configFromSetWin(txtDoc[iTab].dir(),
            FileUtils.extension(txtDoc[iTab].filename()));
      if (proj.getProjectRoot().length() > 0) {
         if (projAct != proj) {
            projAct = proj;       
            updateProjectDisplay(projAct.getProjectRoot());
            isProjectSet = true;
         }
      }
   }

   private void updateProjectDisplay(String path) {
      File file = new File(path);
      mw.showProjectInfo(file.getName());
      fileTree.setProjectTree(path);
   }
   
   private void changeTabEvent(ChangeEvent changeEvent) {
      JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
      iTab = sourceTb.getSelectedIndex();
      if (iTab > -1) {
         mw.displayFrameTitle(txtDoc[iTab].filepath());
         focusInSelectedTab();
         edit.setTextObject(txtDoc[iTab]);
         plugStart.setTextDocument(txtDoc[iTab]);
      }
   }
}