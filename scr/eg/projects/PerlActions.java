package eg.projects;

import java.io.File;
import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.console.*;

import eg.ui.ViewSettings;
import eg.ui.filetree.FileTree;

/**
 * Represents a programming project in Perl
 */
public class PerlActions extends ProjectConfig implements ProjectActions {
   
   private final ViewSettings viewSet;
   private final ProcessStarter proc;
   private final ConsolePanel cw;
   private final FileTree fileTree;
   
   private String startCommand = "";
   
   public PerlActions(ViewSettings viewSet, ProcessStarter proc,
         ConsolePanel cw, FileTree fileTree) {
      super(new SettingsWin(
               "Name of perl script",
               null,
               true,
               false,
               true,
               null
            ),
            ".pl"
      );
      this.viewSet = viewSet;
      this.proc = proc;
      this.cw = cw;
      this.fileTree = fileTree;
   }
   
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setStartCommand();
      }
      return success;
   }
   
   /**
    * If a project configuration stored in 'config' or 'prefs' can be
    * retrieved
    * @param dir  the directory of a file that maybe part of the project
    * @return  If a project configuration stored in 'config' or 'prefs'
    * can be retrieved
    */
   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setStartCommand();
      }
      return success;
   }
   
   /**
    * Passes the project's root to this {@code ProcessStarter}
    * and this {@code FileTree}
    */
   @Override
   public void applyProject() {
      proc.addWorkingDir(getProjectPath());
      fileTree.setProjectTree(getProjectPath());
   }
   
   /**
    * Not used
    */
   @Override
   public void compile() {
   }
   
   /**
    * Runs the script in the console panel
    */
   @Override
   public void runProject() {
      if (!viewSet.isConsoleSelected()) {
         viewSet.setShowConsoleState(true);
      }
      proc.startProcess(startCommand);
   }
   
   /**
    * Not used
    */
   @Override
   public void build() {
   }
   
   private void setStartCommand() {
      String main = getMainFile() + ".pl";
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }

      if (getSourceDirName().length() == 0 ) {
         startCommand = "perl " + main;
      }
      else {
         startCommand = "perl "
               + getSourceDirName()
               + File.separator
               + main;
      }
   }
}
