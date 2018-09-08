package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//--Eadgyth--//
import eg.Prefs;
import eg.utils.Dialogs;

/**
 * The configuration of a project.
 * <p>
 * This class works in combination with an object of
 * {@link SettingsWindow} where values required for a configuration
 * are entered.
 */
public abstract class AbstractProject implements Configurable {

   /**
    * The object of <code>InputOptionsBuilder</code> which a project
    * uses to build the content of this <code>SettingsWindow</code>
    *
    * @see SettingsWindow.InputOptionsBuilder
    */
   protected final SettingsWindow.InputOptionsBuilder inputOptions;

   private final static String F_SEP = File.separator;
   private final Prefs prefs = new Prefs();
   private final SettingsWindow sw;
   private final ProjectTypes projType;
   private final String sourceExtension;
   private final boolean useProjectFile;
   //
   // The Prefs object that stores and reads from a ProjConfig file
   // in a project's root directory
   private Prefs conf;
   //
   // Variables available to a project and partly to a class that
   // creates a project
   private String projectRoot = "";
   private String mainFileName = "";
   private String namespace = "";
   private String execDirName = "";
   private String sourceDirName = "";
   private String startOptions = "";
   private String args = "";
   private String compileOption = "";
   private String extensions = "";
   private String buildName = "";
   //
   // Variables to control the configuration
   private String rootToTest = "";
   private boolean isPathname = false;
   private boolean isNameConflict = false;

   @Override
   public final void setConfiguringAction(ActionListener al) {
      sw.okAct(al);
   }

   @Override
   public final void makeSettingsWindowVisible() {
      sw.setVisible(true);
   }

   @Override
   public final boolean configureProject(String dir) {
      String root = findRoot(dir);
      if (root.length() > 0) {
         if (useProjectFile) {
            configureByTextFieldsInput(root);
         }
         else {
            rootToTest = root;
         }
      }
      boolean success = isConfigSuccessful();
      if (success) {
         setCommandParameters();
         sw.setVisible(false);
      }
      return success;
   }

   /**
    * {@inheritDoc}.
    * <p>
    * First it is tried to find a "ProjConfig" file in <code>dir</code>
    * or further upward the directory path and, if this is not found,
    * it is tested if <code>dir</code> is contained in the recent project
    * saved in the "Prefs" file in the program folder.
    */
   @Override
   public final boolean retrieveProject(String dir) {
      String root = findRootByFile(dir, Prefs.PROJ_CONFIG_FILE);
      if (root.length() > 0) {
         sw.setSaveProjConfigSelected(true);
         conf = new Prefs(root);
         configByPropertiesFile(root, conf);
      }
      else {
         sw.setSaveProjConfigSelected(false);
         root = prefs.getProperty("ProjectRoot");
         if (isInProject(dir, root)) {
             configByPropertiesFile(root, prefs);
         }
      }
      boolean success = projectRoot.length() > 0;
      if (success) {
         setCommandParameters();
      }
      return success;
   }

   @Override
   public final ProjectTypes getProjectType() {
      return projType;
   }

   @Override
   public final boolean usesProjectFile() {
      return useProjectFile;
   }

   @Override
   public final boolean isInProject(String dir) {
      return isInProject(dir, projectRoot);
   }

   @Override
   public final String getProjectPath() {
      return projectRoot;
   }

   @Override
   public final String getProjectName() {
      File f = new File(projectRoot);
      return f.getName();
   }

   /**
    * {@inheritDoc}.
    * <p>
    * The directory does not have to exist, even if its name is
    * specified in this {@link SettingsWindow}, for a successful
    * project configuration.
    */
   @Override
   public final String getExecutableDirName() {
      return execDirName;
   }

   @Override
   public final void storeConfiguration() {
      storeConfigurationImpl();
   }

   /**
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
    * @param useProjectFile  the boolean value that is true to indicate that
    * the project uses a main project file
    * @param sourceExtension  the extension of source files (or of the main
    * file if extensions differ). Null if no main project file is used
    */
   protected AbstractProject(ProjectTypes projType, boolean useProjectFile,
         String sourceExtension) {

      this.projType = projType;
      this.sourceExtension = "." + sourceExtension;
      this.useProjectFile = useProjectFile;
      sw = new SettingsWindow();
      inputOptions = sw.getInputOptionsBuilder();
      sw.setCancelAct(e -> undoSettings());
      sw.setDefaultCloseAct(DefaultClosing);
   }

   /**
    * Sets command parameters that are necessary for actions defined in
    * <code>ProjectActions</code>
    *
    * @see ProjectActions
    */
   protected abstract void setCommandParameters();

   /**
    * Returns the name of the project's main file (without extension)
    *
    * @return  the name
    */
   protected String getMainFileName() {
      if (!useProjectFile) {
         throw new IllegalStateException("A main file is not used");
      }
      return mainFileName;
   }

   /**
    * Returns the namespace of the main file. This is a relative
    * directory or directory path based at the sources directory or,
    * if a sources directory is not given, at the project's root
    * directory.
    *
    * @return  the namespace. The empty string if no namespace is given
    */
   protected String getNamespace() {
      return namespace;
   }

   /**
    * Returns the name of the directoy where source files are saved
    *
    * @return  the name. The empty string if no source directory is given
    */
   protected String getSourceDirName() {
      return sourceDirName;
   }

   /**
    * Returns the extension of source files (or of the main
    * file if extensions differ) with the beginning period
    *
    * @return  the extension. Null if no source extension is given
    */
   protected String getSourceExtension() {
      return sourceExtension;
   }

   /**
    * Returns command options
    *
    * @return  the options. The empty string if no options are given
    */
   protected String getCmdOptions() {
      return startOptions;
   }

   /**
    * Returns command arguments
    *
    * @return  the arguments. The empty string if no arguments are given
    */
   protected String getCmdArgs() {
      return args;
   }

   /**
    * Returns the compile option
    *
    * @return  the option. The empty string if no option is given
    */
   protected String getCompileOption() {
      return compileOption;
   }

   /**
    * Returns the array that contains file extensions which may be used
    * for a file search
    *
    * @return  the array or null of no extensions are given
    */
   protected String[] getFileExtensions() {
      if (extensions.length() == 0) {
          return null;
       }
       else {
          return extensions.split(",");
       }
   }

   /**
    * Returns the name for a build
    *
    * @return  the name
    */
   protected String getBuildName() {
      return buildName;
   }

   /**
    * Returns if the main executable file exists. The file is searched
    * in the executables directory if it is specified or in the project
    * root directory
    *
    * @param ext  the extension of the executable file (with starting period)
    * @return  the boolean value that is true if the file exists
    */
   protected boolean mainExecFileExists(String ext) {
      StringBuilder sb = new StringBuilder(projectRoot + "/");
      if (execDirName.length() > 0) {
         sb.append(execDirName).append("/");
      }
      if (namespace.length() > 0) {
         sb.append(namespace).append("/");
      }
      sb.append(mainFileName).append(ext);
      File f = new File(sb.toString());
      return f.exists();
   }

   //
   //--private--//
   //

   private String findRoot(String dir) {
      rootToTest = "";
      File root = new File(dir);
      String existingName;
      String rootInput = sw.projDirNameInput();
      while (root != null) {
         existingName = root.getName();
         if (rootInput.equals(existingName) && root.exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private String findRootByFile(String dir, String file) {
      File root = new File(dir);
      String relToRoot = F_SEP + file;
      String existingPath;
      while (root != null) {
         existingPath = root.getPath() + relToRoot;
         if (new File(existingPath).exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private void configureByTextFieldsInput(String root) {
      namespace = "";
      isNameConflict = false;
      getTextFieldsInput(); // assigns value to isPathname
      if (!isPathname) {
         String sourceRoot = root;
         if (sourceDirName.length() > 0) {
            sourceRoot = sourceRoot + "/" + sourceDirName;
         }
         findNamespace(sourceRoot, mainFileName + sourceExtension);
         if (namespace.length() > 0) {
            if (namespace.length() > sourceRoot.length()) {
               namespace = namespace.substring(sourceRoot.length() + 1);
            }
            else {
               namespace = ""; // no subdir in source root
            }
            rootToTest = root;
         }
      }
      else {
         File fToTest = new File(root + "/" + pathRelToRoot());
         if (fToTest.exists()) {
            rootToTest = root;
         }
      }
   }

   private void getTextFieldsInput() {
      String mainFileInput = sw.fileNameInput();
      splitMainFileInput(mainFileInput);
      sourceDirName = sw.sourcesDirNameInput();
      execDirName = sw.execDirNameInput();
      startOptions = sw.cmdOptionsInput();
      args = sw.cmdArgsInput();
      compileOption = sw.compileOptionInput();
      extensions = sw.extensionsInput();
      buildName = sw.buildNameInput();
   }      

   private void findNamespace(String sourceRoot, String name) {
      File f = new File(sourceRoot);
      File[] list = f.listFiles();
      if (list != null) {
         for (File fInList : list) {
           if (fInList.isFile()) {
               if (fInList.getName().equals(name)) {
                  if (namespace.length() > 0) {
                     namespace = "";
                     isNameConflict = true;
                  }
                  else {
                     namespace = fInList.getParent();
                  }
               }
            }
            else {
               findNamespace(fInList.getPath(), name);
            }
         }
      }
   }
   
   private void setTextFieldsDisplay() {
      sw.displayProjDirName(getProjectName());
      if (isPathname) {
         sw.displayFile(namespace + F_SEP + mainFileName);
      }
      else {
         sw.displayFile(mainFileName);
      }
      sw.displaySourcesDir(sourceDirName);
      sw.displayExecDir(execDirName);
      sw.displayExtensions(extensions);
      sw.displayBuildName(buildName);
   }      
      
   private void configByPropertiesFile(String root, Prefs pr) {
      String projTypeToTest = pr.getProperty("ProjectType");
      if (!projTypeToTest.equals(projType.toString())) {
         return;
      }
      String mainFileInput = pr.getProperty("MainProjectFile");
      splitMainFileInput(mainFileInput);
      if (isPathname) {
         sw.displayFile(namespace + F_SEP + mainFileName);
      }
      else {
         namespace = pr.getProperty("Namespace");
      }
      sourceDirName = pr.getProperty("SourceDir");
      execDirName = pr.getProperty("ExecDir");
      extensions = pr.getProperty("IncludedFiles");
      buildName = pr.getProperty("BuildName");

      File fToTest = new File(root + F_SEP + pathRelToRoot());
      if (fToTest.exists()) {
         boolean ok = (useProjectFile && fToTest.isFile())
               || (!useProjectFile && fToTest.isDirectory());

         if (ok) {
            projectRoot = root;
            setTextFieldsDisplay();
            if (pr == conf) {
               store(prefs);
            }
         }
      }
   }

   private void splitMainFileInput(String mainFileInput) {
      String formatted = mainFileInput.replace("\\", "/");
      int lastSepPos = formatted.lastIndexOf("/", mainFileInput.length());
      isPathname = lastSepPos != -1;
      if (isPathname) {
         namespace = formatted.substring(0, lastSepPos).replaceAll("/",
               java.util.regex.Matcher.quoteReplacement(F_SEP));

         mainFileName = formatted.substring(lastSepPos + 1);
      }
      else {
         mainFileName = mainFileInput;
      }
   }

   private String pathRelToRoot() {
      StringBuilder sb = new StringBuilder();
      if (sourceDirName.length() > 0) {
         sb.append(sourceDirName).append("/");
      }
      if (namespace.length() > 0) {
         sb.append(namespace).append("/");
      }
      if (mainFileName.length() > 0) {
         sb.append(mainFileName).append(sourceExtension);
      }
      return sb.toString();
   }

   private boolean isInProject(String dir, String projRoot) {
      File child = new File(dir);
      File root = new File(projRoot);
      while(child != null) {
         if (child.equals(root)) {
            return true;
         }
         child = child.getParentFile();
      }
      return false;
   }

   private boolean isConfigSuccessful() {
      boolean success = rootToTest.length() > 0;
      if (!success) {
         if (isNameConflict) {
            Dialogs.warnMessageOnTop(nameConflictMessage());
         }
         else {
            if (useProjectFile) {
               Dialogs.warnMessageOnTop(INPUT_ERROR_GENERAL);
            }
            else {
               Dialogs.warnMessageOnTop(INPUT_ERROR_PROJ_ROOT);
            }
         }
      }
      else {
         if (projectRoot.length() == 0 || !projectRoot.equals(rootToTest)) {
            projectRoot = rootToTest;
         }
      }
      return success;
   }

   private void storeConfigurationImpl() {
      if (projectRoot.length() == 0) {
         throw new IllegalStateException("The project is not configured");
      }
      store(prefs);
      if (sw.isSaveToProjConfig()) {
         if (conf == null) {
            conf = new Prefs(projectRoot);
         }
         store(conf);
      }
      else {
         deleteProjConfigFile();
      }
   }

   private void store(Prefs pr) {
      if (pr == prefs) {
         pr.setProperty("ProjectRoot", projectRoot);
      }
      pr.setProperty("SourceDir", sourceDirName);
      pr.setProperty("ExecDir", execDirName);
      pr.setProperty("IncludedFiles", extensions);
      pr.setProperty("BuildName", buildName);
      pr.setProperty("ProjectType", projType.toString());
      if (isPathname) {
         pr.setProperty("MainProjectFile", namespace + F_SEP + mainFileName);
         pr.setProperty("Namespace", "");
      }
      else {
         pr.setProperty("MainProjectFile", mainFileName);
         pr.setProperty("Namespace", namespace);
      }
      pr.store();
   }

   private void deleteProjConfigFile() {
      File configFile = new File(projectRoot + "/" + Prefs.PROJ_CONFIG_FILE);
      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(DELETE_CONF_OPT);
         if (res == 0) {
            boolean success = configFile.delete();
            if (!success) {
               Dialogs.warnMessage("Deleting the ProgConfig file failed");
            }
         }
         else {
            sw.setSaveProjConfigSelected(true);
            store(conf);
         }
      }
   }
   
   private void undoSettings() {
      setTextFieldsDisplay();
      sw.setVisible(false);
   }
   
   private final WindowAdapter DefaultClosing = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         undoSettings();
      }
   };

   //
   //--Strings for messages
   //

   private String nameConflictMessage() {
      return
         "<html>"
         + "The filename \"" + mainFileName + "\" seems to exist more"
         + " than once in the project.<br>"
         + "<br>"
         + "If this name should be maintained its pathname relative"
         + " to the source root must be specified.<br>"
         + "The source root is the sources directory if available or"
         + " the root directory of the project."
         + "</html>";
   }

   private final static String DELETE_CONF_OPT
         =  "<html>"
         + "Saving the project settings in the project folder is"
         + " no more selected.<br>"
         + " Remove the \"ProjConfig\" file?"
         + "</html>";

   private final static String INPUT_ERROR_PROJ_ROOT
         =  "The entry for the project root cannot be matched with an"
         + " existing file.";

   private final static String INPUT_ERROR_GENERAL
         =  "The entries cannot be matched with an existing file.";
}
