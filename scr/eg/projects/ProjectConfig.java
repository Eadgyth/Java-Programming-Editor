package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;
import eg.Constants;
import eg.utils.JOptions;

/**
 * Represents the configuration of a project.
 * <p>
 * Class implements methods in {@link Configurable}
 */
public abstract class ProjectConfig implements Configurable {
   
   private final static Preferences PREFS = new Preferences();
   private final static Preferences CONFIG = new Preferences();
   private final static String F_SEP = Constants.F_SEP;

   private SettingsWin setWin = null;
   private String suffix;
   private String projectPath = "";
   private String mainFile = "";
   private String moduleDir = "";
   private String execDir = "";
   private String sourceDir = "";
   private String args = "";
   private String buildName = "";
   
   public ProjectConfig(SettingsWin setWin, String suffix) {
      this.setWin = setWin;
      this.suffix = suffix;
   }

   @Override
   public void addOkAction(ActionListener al) {
      setWin.okAct(al);
   }
   
   @Override
   public void makeSetWinVisible(boolean isVisible) {
      setWin.makeVisible(isVisible);
   }
   
   @Override
   public boolean configureProject(String dir) {    
      projectPath = findRootByFile(dir, pathRelToRoot());
      boolean success = storeInputs();
      if (success) {
         setWin.makeVisible(false);
      }
      return success;
   }
   
   /**
    * If a project configuration stored in 'config' or 'prefs' can be
    * retrieved
    * @param path  the directory of a file that maybe part of the project 
    * @return  If a project configuration stored in 'config' or 'prefs'
    * can be retrieved
    */
   @Override
   public boolean retrieveProject(String path) {
      findSavedProject(path);
      return projectPath.length() > 0;
   }
   
   @Override
   public boolean isProjectInPath(String path) {
      return findRootInPath(path, PREFS).length() > 0;
   }
   
   @Override
   public String getProjectName() {
      File f = new File(projectPath);
      return f.getName();
   }
   
   @Override
   public void storeInPrefs() {
      PREFS.storePrefs("recentProject", projectPath);
      PREFS.storePrefs("recentMain", mainFile);
      PREFS.storePrefs("recentModule", moduleDir);
      PREFS.storePrefs("recentSourceDir", sourceDir);
      PREFS.storePrefs("recentExecDir", execDir);
      PREFS.storePrefs("recentBuildName", buildName);
      PREFS.storePrefs("recentSuffix", suffix);
   }
   
   /**
    * Returns the path of the project's root directory
    * @return  the the path of the project's root directory
    */
   protected String getProjectPath() {
      return projectPath;
   }
   
   /**
    * Returns the the name of the project's main file
    * @return  the name of project's main file
    */ 
   protected String getMainFile() {
      return mainFile;
   }

   /**
    * Returns the name of the directory of a module.
    * <p>
    * It is not specified what a 'module' is. In the case of a Java
    * project a module would be a package
    * @return  the name of the directory of a module.
    */ 
   protected String getModuleName() {
      return moduleDir;
   }
   
   /**
    * Returns the name of the directoy where source files are
    * saved
    * @return  the name of the directoy where source files are
    * saved
    */ 
   protected String getSourceDirName() {
      return sourceDir;
   }
   
   /**
    * Returns the name of the directoy where executable files are
    * saved
    * @return  the name of the directoy where executable files are
    * saved
    */
   protected String getExecDirName() {
      return execDir;
   }

   /**
    * @return  the name for a build entered in the
    * text field of this {@code SettingsWin}
    */ 
   protected String getBuildName() {
      return buildName;
   }
   
   /**
    * @return  the arguments for a start command entered in the
    * text field of this {@code SettingsWin}
    */ 
   protected String getArgs() {
      return args;
   }
   
   /**
    * If the main executable file exists
    * @param suffix  the extension of the project's main file
    * @return  true if the main executable file exists
    */
   protected boolean mainProgramFileExists(String suffix) { 
      File target
            = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + suffix);
      return target.exists();
   }
   
   //
   //--private--//
   //

   private void findSavedProject(String path) {
      Preferences props = null;
      //
      // firstly look if there is a eadconfig file...
      String root = findRootByFile(path, Preferences.CONFIG_FILE);
      if (root.length() > 0) {
         props = CONFIG;
         props.readConfig(root);
         setWin.setSaveConfigSelected(true);
      }
      //
      // ... if not successful look if the dir includes the project root
      // stored in prefs
      else {
         props = PREFS;
         props.readPrefs();
         setWin.setSaveConfigSelected(false);
         root = findRootInPath(path, props);
      }
      //
      // if the project type conforms to the type in props file    
      boolean isTypeCorrect
            = suffix.equals(props.getProperty("recentSuffix"));
      //
      // read in props and set text fields in this SettingsWin  
      if (isTypeCorrect && root.length() > 0) {        
         configProjectByProps(root, props);
      }
   }
   
   /**
    * Tries to find the project root in the specified path by
    * looking for an existing file that is a child of this root.
    * 'file' may be a file or itself a (relative) path
    */
   private String findRootByFile(String path, String file) {
      File searched = new File(path);
      String relToRootStr = F_SEP + file;
      String existingPath = path + relToRootStr;
      boolean exists = new File(existingPath).exists();
      while(!exists) {
         if (searched.getParentFile() == null) {
            searched = null;
            break;
         }
         searched     = new File(searched.getParent());
         existingPath = searched.getPath() + relToRootStr;
         exists       = new File(existingPath).exists();
      }
      if (searched == null) {
         return "";
      }
      else {
         return searched.getPath();
      }
   }

   private String findRootInPath(String path, Preferences props) { 
      File searched = new File(path);
      File project;
      if (projectPath.length() > 0) {
         project = new File(projectPath);
      }
      else {  
         project = new File(props.getProperty("recentProject"));
      }
      String searchedStr = searched.getPath();
      String projStr = project.getPath();
      boolean isEqual = projStr.equals(searchedStr);
      while(!isEqual) {
         if (searched.getParentFile() == null) {
            searchedStr = "";
            break;
         }       
         searched    = new File(searched.getParent());
         searchedStr = searched.getPath();
         isEqual     = projStr.equals(searchedStr);
      }
      return searchedStr;     
   }
   
   private void configProjectByProps(String previousRoot, Preferences props) {
      mainFile = props.getProperty("recentMain");
      setWin.displayFile(mainFile);
      
      moduleDir = props.getProperty("recentModule");
      setWin.displayModule(moduleDir);
      
      sourceDir = props.getProperty("recentSourceDir");
      setWin.displaySourcesDir(sourceDir);
      
      execDir = props.getProperty("recentExecDir");
      setWin.displayExecDir(execDir);

      buildName = props.getProperty("recentBuildName");
      setWin.displayBuildName(buildName);
      
      projectPath = previousRoot;
      if (props == CONFIG) {
         storeInPrefs();
      }
   }
   
   private String pathRelToRoot() {
      getTextFieldsInput();
      String dirRelToRoot = "";
      if (sourceDir.length() > 0 & moduleDir.length() == 0) {
         dirRelToRoot += sourceDir;
      }
      else if (sourceDir.length() == 0 & moduleDir.length() > 0) {
         dirRelToRoot += moduleDir;
      }
      else if (sourceDir.length() > 0 & moduleDir.length() > 0) {
         dirRelToRoot += sourceDir + F_SEP + moduleDir;
      }     
      return dirRelToRoot + F_SEP + mainFile + suffix;
   }      
   
   private void getTextFieldsInput() {
      mainFile = setWin.projectFileIn();
      moduleDir = setWin.moduleIn();
      sourceDir = setWin.sourcesDirIn();
      execDir = setWin.execDirIn();
      args = setWin.argsIn();
      buildName = setWin.buildNameIn();
   }
   
   private boolean storeInputs() {
      boolean canStore = true;
      if (projectPath.length() == 0) {
         JOptions.warnMessageToFront(
               "An entry in the 'Project' panel is incorrect");      
         canStore = false;
      }
      else {
         storeInPrefs();   
         if (setWin.isSaveConfig()) {
            CONFIG.storeConfig("recentMain", mainFile, projectPath);
            CONFIG.storeConfig("recentModule", moduleDir, projectPath);
            CONFIG.storeConfig("recentSourceDir", sourceDir, projectPath);
            CONFIG.storeConfig("recentExecDir", execDir, projectPath);
            CONFIG.storeConfig("recentBuildName", buildName, projectPath);
            CONFIG.storeConfig("recentSuffix", suffix, projectPath);
         }
         else {
            File configFile;
            configFile = new File(projectPath + F_SEP
                     + Preferences.CONFIG_FILE);
            if (configFile.exists()) {
               int res = JOptions.confirmYesNo(
                       "'Save settings in project folder' is disabled."
                     + " Remove the 'config' file?");
               if (res == 0) {
                  boolean success = configFile.delete();
                  if (!success) {
                     JOptions.warnMessage(
                           "Deleting the 'config' file failed");
                  }
               }
            }
         }
      }
      return canStore;
   }
}
