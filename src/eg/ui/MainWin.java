package eg.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.Constants;
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;
import eg.EditAreaFormat;
import eg.Preferences;
import eg.Languages;
import eg.FunctionalAction;
import eg.edittools.*;
import eg.ui.menu.MenuBar;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.filetree.FileTree;
import eg.ui.tabpane.ExtTabbedPane;
import eg.console.ConsolePanel;
import eg.utils.UIComponents;
import eg.utils.ScreenParams;
import eg.utils.FileUtils;

/**
 * The main window
 */
public class MainWin implements ConsoleOpenable {

   private final static Cursor BUSY_CURSOR
         = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
   private final static Cursor DEF_CURSOR
         = Cursor.getDefaultCursor();

   private final JFrame frame = new JFrame();
   private final JPanel statusBar = new JPanel();
   private final JLabel projectLb = new JLabel();
   private final JLabel languageLb = new JLabel();
   private final JLabel cursorPosLb = new JLabel();
   private final JLabel wordwrapLb = new JLabel();

   private final MenuBar menuBar = new MenuBar();
   private final Toolbar toolbar = new Toolbar();
   private final ExtTabbedPane tabPane = UIComponents.scolledUnfocusableTabPane();
   private final FileTree fileTree = new FileTree();
   private final ConsolePanel console = new ConsolePanel();
   private final ToolPanel toolPnl = new ToolPanel();
   private final List<AddableEditTool> editTools = new ArrayList<>();
   private final Preferences prefs = new Preferences();

   private JSplitPane splitHorAll;
   private JSplitPane splitHor;
   private JSplitPane splitVert;
   private int dividerLocVert = 0;
   private int dividerLocHor = 0;

   public MainWin() {
      createAddableEditTools();
      initFrame();
      setViewActions();
      initShowTabbar();
   }

   @Override
   public boolean isConsoleOpen() {
      return menuBar.viewMenu().isConsoleItmSelected();
   }

   @Override
   public void openConsole() {
      menuBar.viewMenu().doConsoleItmAct(true);
   }

   /**
    * Sets this JFrame visible
    */
   public void makeVisible() {
      frame.setVisible(true);
   }

   /**
    * Gets this <code>ExtTabbedPane</code>
    *
    * @return  this {@link ExtTabbedPane}
    */
   public ExtTabbedPane tabPane() {
      return tabPane;
   }

   /**
    * Gets this <code>ConsolePanel</code>
    *
    * @return  this {@link ConsolePanel}
    */
    public ConsolePanel console() {
       return console;
    }

   /**
    * Gets this <code>FileTree</code>
    *
    * @return  this {@link FileTree}
    */
    public FileTree fileTree() {
       return fileTree;
    }

    /**
     * Gets this List of <code>AddableEditTool</code>
     *
     * @return  this List of type {@link AddableEditTool}
     */
    public List<AddableEditTool> editTools() {
       return editTools;
    }

   /**
    * Displays text in the title bar
    *
    * @param title  the title
    */
   public void displayFrameTitle(String title) {
      frame.setTitle(title);
   }

   /**
    * Displays the current language in the status bar
    *
    * @param lang   the language which is a constant in {@link Languages}
    */
   public void displayLanguage(Languages lang) {
      languageLb.setText("Language: " + lang.display());
   }

   /**
    * Displays the project name in the status bar
    *
    * @param projName  the name
    */
   public void displayProjectName(String projName) {
      projectLb.setText("Active project: " + projName);
   }

   /**
    * Displays the line and column number of the cursor position in
    * the status bar
    *
    * @param lineNr  the line number
    * @param colNr  the column number
    */
   public void displayLCursorPosition(int lineNr, int colNr) {
      cursorPosLb.setText("Line " + lineNr + "  Col " + colNr);
   }

   /**
    * Sets the specified label for the menu item for building actions
    *
    * @param label  the label
    */
   public void setBuildLabel(String label) {
      menuBar.projectMenu().setBuildLabel(label);
   }

   /**
    * Sets the selection state of the menu item for setting wordwrap
    * actions
    *
    * @param b  true to select, false to unselect the item
    */
   public void setWordWrapSelected(boolean b) {
      menuBar.formatMenu().selectWordWrapItm(b);
      setWordwrapInStatusBar(b);
   }

   /**
    * Selects the menu item for the specified language and displays
    * the file type in the status bar
    *
    * @param lang  the language
    * @param b  if the non-selected items are set enabled
    */
   public void setLanguageSelected(Languages lang, boolean b){
      menuBar.editMenu().selectLanguageItm(lang, b);
      displayLanguage(lang);
   }

   /**
    * Sets the booleans that specify if undoing and redoing actions
    * are enabled or disabled
    *
    * @param isUndo  the boolean value for undo actions
    * @param isRedo  the boolean value for redo actions
    */
   public void enableUndoRedo(boolean isUndo, boolean isRedo) {
      toolbar.enableUndoRedoBts(isUndo, isRedo);
      menuBar.editMenu().enableUndoRedoItms(isUndo, isRedo);
   }

   /**
    * Sets the boolean that specifies if cutting and copying actions
    * are enabled or disabled
    *
    * @param b  the boolean value
    */
   public void enableCutCopy(boolean b) {
      toolbar.enableCutCopyBts(b);
      menuBar.editMenu().enableCutCopyItms(b);
   }

   /**
    * Sets the boolean that specifies if actions to set the visiblity
    * of the tabbar are enabled or disabled
    *
    * @param b  the boolean value
    */
   public void enableShowTabbar(boolean b) {
      menuBar.viewMenu().enableTabItm(b);
   }

   /**
    * Sets the boolean that specifies if actions to change project are
    * enabled or disabled
    *
    * @param b  the boolean value
    */
   public void enableChangeProject(boolean b) {
      menuBar.projectMenu().enableChangeProjItm(b);
      toolbar.enableChangeProjBt(b);
   }

   /**
    * Sets the booleans that specify if actions to compile, run and
    * build a project are enabled or disabled
    *
    * @param isCompile  the boolean value for compile actions
    * @param isRun  the boolean value for run actions
    * @param isBuild  the boolean value for build actions
    */
   public void enableSrcCodeActions(boolean isCompile, boolean isRun,
         boolean isBuild) {

      menuBar.projectMenu().enableSrcCodeActionItms(isCompile, isRun,
            isBuild);

      toolbar.enableSrcCodeActionBts(isCompile, isRun);
   }

   /**
    * Sets the boolean that specified if the toolbar is shown
    * or hidden
    *
    * @param b  the boolean value
    */
   public void showToolbar(boolean b) {
      if (b) {
         frame.add(toolbar.toolbar(), BorderLayout.NORTH);
      }
      else {
         frame.remove(toolbar.toolbar());
      }
      frame.revalidate();
   }

   /**
    * Sets the boolean that specified if the statusbar is shown
    * or hidden
    *
    * @param b  the boolean value
    */
   public void showStatusbar(boolean b) {
      if (b) {
         frame.add(statusBar, BorderLayout.SOUTH);
      }
      else {
         frame.remove(statusBar);
      }
      frame.revalidate();
   }

   /**
    * Sets the busy cursor
    */
   public void setBusyCursor() {
      Component glassPane = frame.getGlassPane();
      glassPane.setVisible(true);
      glassPane.setCursor(BUSY_CURSOR);
   }

   /**
    * Sets the default cursor
    */
   public void setDefaultCursor() {
      Component glassPane = frame.getGlassPane();
      glassPane.setVisible(false);
      glassPane.setCursor(DEF_CURSOR);
   }

   /**
    * Sets listeners for file actions
    *
    * @param tf  the reference to {@link TabbedFiles}
    */
   public void setFileActions(TabbedFiles tf) {
      menuBar.fileMenu().setActions(tf);
      menuBar.fileMenu().setExitActions(e -> exit(tf));
      menuBar.editMenu().setChangeLanguageAction(tf);
      toolbar.setFileActions(tf);
      fileTree.addObserver(tf);
      
      winListen(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent we) {
            exit(tf);
         }
      });
   }

   /**
    * Sets listeners for actions to edit text
    *
    * @param edit  the reference to {@link Edit}
    */
   public void setEditTextActions(Edit edit) {
      toolbar.setEditTextActions(edit);
      menuBar.editMenu().setEditTextActions(edit);
   }

   /**
    * Sets the listener for actions to opens the window for the view
    * preferences
    *
    * @param viewSetWin  the reference to <code>ViewSettingWin</code>
    */
   public void setViewSettingWinAction(ViewSettingWin viewSetWin) {
      menuBar.viewMenu().openSettingWinItmAction(e ->
            viewSetWin.makeVisible(true));
   }

   /**
    * Sets listeners for format actions
    *
    * @param format  the reference to {@link EditAreaFormat}
    */
   public void setFormatActions(EditAreaFormat format) {
      FormatMenu fm =  menuBar.formatMenu();
      fm.setChangeWordWrapAct((ActionEvent e) -> {
         boolean isWordwrap = fm.isWordWrapItmSelected();
         format.changeWordWrap(isWordwrap);
         setWordwrapInStatusBar(isWordwrap);
      });
      fm.setFontAction(e -> format.makeFontSettingWinVisible());
   }

   /**
    * Sets listeners for project actions
    *
    * @param cp  the reference to {@link CurrentProject}
    */
   public void setProjectActions(CurrentProject cp) {
      menuBar.projectMenu().setActions(cp);
      toolbar.setProjectActions(cp);
   }

   //
   //--private--/
   //

   private void setViewActions() {
      ViewMenu vm = menuBar.viewMenu();
      vm.setConsoleItmAction(e -> showConsole(vm.isConsoleItmSelected()));
      vm.setFileViewItmAction(e -> showFileView(vm.isFileViewItmSelected()));
      vm.setTabItmAction(e -> showTabbar(vm.isTabItmSelected()));
      fileTree.setCloseAct(e -> vm.doUnselectFileViewAct());
      console.setCloseAct(e -> vm.doConsoleItmAct(false));
   }
   
   private void winListen(WindowListener wl) {
      frame.addWindowListener(wl);
   }

   private void showConsole(boolean b) {
      if (b) {
         splitVert.setDividerSize(6);
         splitVert.setRightComponent(console.consolePnl());
         if (dividerLocVert == 0) {
            dividerLocVert = (int)(frame.getHeight() * 0.65);
         }
         splitVert.setDividerLocation(dividerLocVert);
      }
      else {
         dividerLocVert = splitVert.getDividerLocation();
         splitVert.setDividerSize(0);
         splitVert.setRightComponent(null);
      }
   }

   private void showToolPnl(boolean b) {
      if (b) {
         splitHorAll.setDividerSize(6);
         splitHorAll.setRightComponent(toolPnl.panel());
      }
      else {
         splitHorAll.setDividerSize(0);
         splitHorAll.setRightComponent(null);
      }
   }

   private void showFileView(boolean b) {
      if (b) {
         splitHor.setDividerSize(6);
         splitHor.setLeftComponent(fileTree.fileTreePnl());
         if (dividerLocHor == 0) {
            dividerLocHor = (int)(frame.getWidth() * 0.22);
         }
         splitHor.setDividerLocation(dividerLocHor);
      }
      else {
         dividerLocHor = splitHor.getDividerLocation();
         splitHor.setDividerSize(0);
         splitHor.setLeftComponent(null);
      }
   }

   private void showTabbar(boolean show) {
      tabPane.showTabbar(show);
      String state = show ? "show" : "hide";
      prefs.storePrefs("showTabs", state);
   }

   private void setWordwrapInStatusBar(boolean isWordwrap) {
      if (isWordwrap) {
         cursorPosLb.setForeground(Constants.GRAY);
         wordwrapLb.setText("Word-wrap ");
      }
      else {
         cursorPosLb.setForeground(Color.BLACK);
         wordwrapLb.setText("");
      }
   }
   
   private void exit(TabbedFiles tf) {
      editTools.forEach((t) -> {
         t.end();
      });
      if (tf.isAllClosed()) {
         System.exit(0);
      }
   }

   private void initShowTabbar() {
      prefs.readPrefs();
      boolean show = "show".equals(prefs.getProperty("showTabs"));
      tabPane.showTabbar(show);
      menuBar.viewMenu().selectTabsItm(show);
   }

   private void createAddableEditTools() {
      try {
         for (int i = 0; i < EditTools.values().length; i++) {
            editTools.add((AddableEditTool) Class.forName("eg.edittools."
                  + EditTools.values()[i].className()).newInstance());

            setEditToolsActions(editTools().get(i), i);
         }
      }
      catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException e) {

         FileUtils.logStack(e);
      }
   }

   private void setEditToolsActions(AddableEditTool tool, int i) {
      JButton closeBt = new JButton();
      closeBt.setAction(new FunctionalAction("", IconFiles.CLOSE_ICON,
            e -> showToolPnl(false)));

      tool.createTool(closeBt);
      menuBar.editMenu().setEditToolsActions(
            e -> {
               toolPnl.addComponent(tool.toolComponent());
               showToolPnl(true);
            }, i);
   }

   private void initFrame() {
      initSplitPane();
      initStatusbar();
      frame.setJMenuBar(menuBar.menuBar());
      frame.add(splitHorAll, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.setLocation(5, 5);
      Dimension screen = ScreenParams.SCREEN_SIZE;
      frame.setSize(screen.width - screen.width/3, screen.height - screen.height/4);
   }

   private void initSplitPane() {
      splitHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
           null, tabPane);
      splitHor.setDividerSize(0);
      splitHor.setBorder(null);
      splitVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
           splitHor, null);
      splitVert.setDividerSize(0);
      splitVert.setResizeWeight(0);
      splitVert.setBorder(null);
      splitHorAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
            splitVert, null);
      splitHorAll.setResizeWeight(1);
      splitHorAll.setDividerSize(0);
      splitHorAll.setBorder(null);
   }

   private void initStatusbar() {
      int lbHeight = 15;
      Dimension width5   = ScreenParams.scaledDimension(5, lbHeight);
      Dimension width20  = ScreenParams.scaledDimension(20, lbHeight);
      Dimension width100 = ScreenParams.scaledDimension(100, lbHeight);
      Dimension width150 = ScreenParams.scaledDimension(150, lbHeight);
      Dimension width200 = ScreenParams.scaledDimension(200, lbHeight);
      JLabel[] lbArr = { languageLb, projectLb, cursorPosLb, wordwrapLb };
      setLbFont(lbArr);
      setLbWidth(languageLb, width100);
      setLbWidth(projectLb, width200);
      setLbWidth(cursorPosLb, width150);
      statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
      statusBar.add(Box.createRigidArea(width5));
      statusBar.add(languageLb);
      statusBar.add(Box.createRigidArea(width20));
      statusBar.add(projectLb);
      statusBar.add(Box.createRigidArea(width20));
      statusBar.add(wordwrapLb);
      statusBar.add(Box.createRigidArea(width5));
      statusBar.add(cursorPosLb);
      displayProjectName("none");
   }

   private void setLbFont(JLabel[] lb) {
      for (JLabel l : lb) {
         l.setFont(Constants.VERDANA_PLAIN_8);
      }
   }

   private void setLbWidth(JLabel lb, Dimension dim) {
      lb.setPreferredSize(dim);
      lb.setMinimumSize(dim);
      lb.setMaximumSize(dim);
   }
}
