package eg;

import java.awt.Component;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.Toolbar;
import eg.ui.menu.Menu;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.menu.ProjectMenu;
import eg.ui.DisplaySettingWin;
import eg.ui.filetree.FileTree;
import eg.ui.TabbedPane;
import eg.utils.JOptions;

/**
 * The setting of the display and behavior of the main window
 */
public class DisplaySetter {

   private final MainWin mw;
   private final FormatMenu fMenu;
   private final ViewMenu vMenu;
   private final ProjectMenu prMenu;
   private final Toolbar tBar;
   private final FileTree fileTree;
   private final TabbedPane tPane;
   private final DisplaySettingWin displSetWin = new DisplaySettingWin();
   private final Preferences prefs = new Preferences();

   private EditArea[] editArea;
   private EditArea currEdArea;

   private boolean isWordWrap;
   private boolean isShowLineNumbers;
   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private boolean isShowTabs;
   private int selectedLafInd;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param menu  the reference to {@link Menu}
    * @param tBar  the reference to {@link Toolbar}
    * @param fileTree  the reference to {@link FileTree}
    * @param tPane  the reference to {@link TabbedPane}
    */
   public DisplaySetter(MainWin mw, Menu menu, Toolbar tBar,
         FileTree fileTree, TabbedPane tPane) {
      this.mw = mw;
      this.vMenu = menu.getViewMenu();
      this.fMenu = menu.getFormatMenu();
      this.prMenu = menu.getProjectMenu();
      this.tBar = tBar;
      this.fileTree = fileTree;
      this.tPane = tPane;
      prefs.readPrefs();
      isShowStatusbar = displSetWin.isShowStatusbar();
      isShowToolbar = displSetWin.isShowToolbar();
      isShowLineNumbers = displSetWin.isShowLineNumbers();
      isWordWrap = "enabled".equals(prefs.getProperty("wordWrap"));
      selectedLafInd = displSetWin.selectedLaf();
      displSetWin.okAct(e -> applySetWinOk());
      fileTree.closeAct(e -> setShowFileViewState(false));
      mw.closeFunctPnlAct(e -> setShowFunctionState(false));
      isShowTabs = "show".equals(prefs.getProperty("showTabs"));
      vMenu.selectTabsItm(isShowTabs);
      tPane.showTabs(isShowTabs);
   }

   /**
    * Sets the array of type {@code EditArea}
    * @param editArea  the array of {@link EditArea}
    */
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }

  /**
    * Sets the {@code EditArea} at the specified index.
    * <p>
    * The method also selects/unselects the wordwrap menu
    * item depending on te state of the {@code EditArea} at the given
    * index
    * @param index  the index of the {@link EditArea} element
    */
   public void setEditAreaIndex(int index) {
      currEdArea = editArea[index];
      fMenu.selectWordWrapItm(currEdArea.isWordWrap());
   }

   /**
    * Makes the frame of this {@link DisplaySettingWin} visible
    */
   public void makeViewSetWinVisible() {
      displSetWin.makeViewSetWinVisible(true);
   }
   
   /**
    * If showing tabs (and openeing multiple files) is
    * is selected
    * @return if showing tabs is selected
    */
   public boolean isShowTabs() {
      return isShowTabs;
   }
   
   /**
    * If enabling word wrap is currently set.
    * @return  if wordwrap is currently set
    */
   public boolean isWordWrap() {
      return isWordWrap;
   }
   
   /**
    * If showing line numbering is currently set
    * @return If showing line numbering is currenty set
    */
   public boolean isLineNumbers() {
      return isShowLineNumbers;
   }

   /**
    * Enables/disables wordwrap in the {@code EditArea} whose
    * index is currently set.
    * <p>
    * @param isWordWrap  true to enable wordwrap, if false line
    * numbers are shown depending on whether showing line numbers
    * is selected in the view settings win
    */
   public void changeWordWrap(boolean isWordWrap) {
      this.isWordWrap = isWordWrap;
      if (isWordWrap) {
         currEdArea.enableWordWrap();
      }
      else {
         if (isShowLineNumbers) {
            currEdArea.showLineNumbers();
         }
         else {
            currEdArea.hideLineNumbers();
         }
      }
      String state = isWordWrap ? "enabled" : "disabled";
      prefs.storePrefs("wordWrap", state);  
   }

   /**
    * If the console panel is shown
    * @return  true if the console panel is shown
    */
   public boolean isConsoleSelected() {
      return vMenu.isConsoleItmSelected();
   }
   
   /**
    * Enabled/disabled the menu item to control visiblity of the tab bar
    * @param isEnabled  true/false to enable/disable the menu item to
    * control visiblity of the tab bar
    */
   public void enableTabItm(boolean isEnabled) {
      vMenu.enableTabItm(isEnabled);
   }

   /**
    * Shows/hides the console panel and selects/deselects
    * the checked menu item for the consel panel
    * @param show  true/false to show(hide the console panel
    */
   public void setShowConsoleState(boolean show) {
      showConsole(show);
      vMenu.selectConsoleItm(show);
   }

   /**
    * Shows/hides the file view panel and selects/deselects
    * the checked menu item for showing the file view
    * @param show  true to show the file view panel
    */
   public void setShowFileViewState(boolean show) {
      showFileView(show);
      vMenu.selectFileViewItm(show);
   }

   /**
    * Shows/hides the function panel and selects/deselects
    * the checked menu item for showing the function panel
    * @param show  true/false to show/hide the function panel
    */
   public void setShowFunctionState(boolean show) {
      showFunction(show);
      vMenu.selectFunctionItm(show);
   }
   
   /**
    * Shows/hides the tab bar
    * @param show  true/false to show/hide the tab bar
    */
   public void showTabs(boolean show) {       
      tPane.showTabs(show);
      String state = show ? "show" : "hide";
      prefs.storePrefs("showTabs", state);
      this.isShowTabs = show;
   }

   /**
    * Shows/hides the console panel
    * @param show  true/false to show/hide the console panel
    */
   public void showConsole(boolean show) {
      mw.showConsole(show);
   }

   /**
    * Shows/hides the fileview panel
    * @param show  true/false to show/hide the fileview panel
    */
   public void showFileView(boolean show) {
      mw.showFileView(show);
   }

   /**
    * Shows/hides the function panel
    * @param show  true to show the function panel
    */
   public void showFunction(boolean show) {
      mw.showFunctionPnl(show);
   }
   
   /**
    * Updates the file tree
    */
   public void updateFileTree() {
      fileTree.updateTree();
   }
   
   /**
    * Sets the text displayed by the menu item for creating a build
    * @param buildKind  the name that descibes the kind of build
    */
   public void setBuildMenuItmText(String buildKind) {
      prMenu.setBuildKind(buildKind);
   }
   
   /**
    * Enables/disables menu items and toolbar buttons for project
    * actions
    * @param isCompile  true to enable the compilation action
    * @param isRun  true to enable the run action
    * @param isBuild  true to enable the build action
    * @param projCount  the number of loaded projects. If 1 the action
    * to show the fileview is enabled, if 2 the action to change between
    * projects is enabled
    */
   public void enableProjActions(boolean isCompile, boolean isRun,
         boolean isBuild, int projCount) {
      if (projCount == 1) {
         vMenu.enableFileView();
      }
      if (projCount == 2) {
         enableChangeProj();
      }
      prMenu.enableProjItms(isCompile, isRun, isBuild);
      tBar.enableProjBts(isCompile, isRun);
   }
   
   /**
    * Enables the menu items to change between projects and t
    * to set a new project
    */
   public void enableChangeProj() {
      prMenu.enableChangeProjItm();
      tBar.enableChangeProjBt();
   }
   
   /**
    * Sets busy or default curser
    * @param isBusy  true to set a busy curor, false to set the default
    * cursor
    */
   public void setBusyCursor(boolean isBusy) {
      mw.setBusyCursor(isBusy);
   }
   
   /**
    * Displays text in the title bar of the main window (i.e., the file)
    * @param title  the text that is displayed in the title bar of the
    * main window
    */
   public void displayFrameTitle(String title) {
      mw.displayFrameTitle(title);
   }
   
   /**
    * Displays the project name in the status bar
    * @param name  the name of the project
    */
   public void showProjectInfo(String name) {
      mw.showProjectInfo(name);
   }

   //
   // private methods
   //

   private void applySetWinOk() {
      boolean show = false;
      String state = null;

      show = displSetWin.isShowToolbar();
      if (isShowToolbar != show) {
         mw.showToolbar(show);
         isShowToolbar = show; 
         state = isShowToolbar ? "show" : "hide";
         prefs.storePrefs("toolbar", state);
      }

      show = displSetWin.isShowStatusbar();
      if (isShowStatusbar != show) {
         mw.showStatusbar(show);
         isShowStatusbar = show;
         state = isShowStatusbar ? "show" : "hide";
         prefs.storePrefs("statusbar", state);
      }

      show = displSetWin.isShowLineNumbers();
      if (isShowLineNumbers != show) {
         isShowLineNumbers = show;
         showHideLineNumbers();
         state = isShowLineNumbers ? "show" : "hide";
         prefs.storePrefs("lineNumbers", state);
      }

      int index = displSetWin.selectedLaf();
      if (selectedLafInd != index) {
         selectedLafInd = index;
         prefs.storePrefs("LaF", DisplaySettingWin.LAF_OPT[selectedLafInd]);
      }    
      displSetWin.makeViewSetWinVisible(false);
   }

   private void showHideLineNumbers() {
      for (EditArea ea : editArea) {
         if (ea != null && !ea.isWordWrap()) {
            if (!isShowLineNumbers) {
               ea.hideLineNumbers();
            } else {
               ea.showLineNumbers();
            }
         }
      }
   }
}
