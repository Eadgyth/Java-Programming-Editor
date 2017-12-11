package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.javatools.*;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.ui.ConsoleOpenable;

/**
 * Represents a programming project in Java
 */
public final class JavaActions extends ProjectConfig implements ProjectActions {

   private final static String F_SEP = File.separator;

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final Compilation comp;
   private final JarBuilder jar;

   private String startCommand = "";
   private String[] includedExt = null;

   JavaActions(ConsoleOpenable co, ProcessStarter proc, ConsolePanel consPnl) {
      super("java", true);
      this.co = co;
      this.proc = proc;
      this.consPnl = consPnl;
      comp = new Compilation(consPnl);
      jar = new JarBuilder(consPnl);
   }

   @Override
   public void createSettingsWin() {
      setWin = SettingsWin.adaptableWindow();
      setWin.addFileOption("Name of main class")
            .addModuleOption("Package path to main class")
            .addSourceDirOption()
            .addExecDirOption()
            .addArgsOption()
            .addIncludeFilesOption()
            .addBuildOption("jar file")
            .setupWindow();
   }

   /**
    * {@inheritDoc}
    * Creates the start command to run the java project
    */
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setStartCommand();
         setIncludedExtArr();
      }
      return success;
   }

   /**
    * {@inheritDoc}
    * Creates the start command to run the java project
    */
   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setStartCommand();
         setIncludedExtArr();
      }
      return success;
   }

   /**
    * Compiles java files
    */
   @Override
   public void compile() {
      consPnl.setText("<<Compile " + getProjectName() + ">>\n");
      EventQueue.invokeLater(() -> {
         if (proc.isProcessEnded()) {
            comp.compile(getProjectPath(), getExecutableDirName(), getSourceDirName(),
                  includedExt);

            consPnl.setCaretUneditable(0);
            if (!co.isConsoleOpen()) {
               if (!comp.isCompiled()) {
                  int res = Dialogs.warnConfirmYesNo(
                        "Compilation failed.\n"
                        + comp.getFirstErrSource() + ".\n"
                        + "Open the console window to view messages?");

                  if (0 == res) {
                     co.openConsole();
                  }
               }
               else {
                  Dialogs.infoMessage("Compilation successful", null);
               }
            }
         }
      });
   }

   /**
    * Runs the project and shows output in the console panel
    */
   @Override
   public void runProject() {
      if (!mainClassFileExists()) {
         return;
      }
      if (!co.isConsoleOpen()) {
         co.openConsole();
      }
      proc.startProcess(startCommand);
   }

   /**
    * Creates a jar file
    */
   @Override
   public void build() {
      if (!mainClassFileExists()) {
         return;
      }

      String jarName = getBuildName();
      if (jarName.length() == 0) {
         jarName = getMainFile();
      }
      boolean existed = jarFileExists(jarName);
      try {
         consPnl.setText("");
         jar.createJar(getProjectPath(), getMainFile(), getModuleName(),
               getExecutableDirName(), getSourceDirName(), jarName,
               includedExt);

         if (!existed) {
            boolean exists = false;
            while (!exists) {
               try {
                  Thread.sleep(200);
               }
               catch (InterruptedException e) {
                  FileUtils.logStack(e);
               }
               exists = jarFileExists(jarName);
            }
         }
         String jarNameFin = jarName;
         EventQueue.invokeLater(() -> {
            consPnl.appendText("<<Saved jar file named " + jarNameFin + ">>\n");
            Dialogs.infoMessage("Saved jar file named " + jarNameFin, null);
         });
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
   }

   //
   //--private--/
   //

   private boolean mainClassFileExists() {
      boolean exists = mainExecFileExists(".class");
      if (!exists) {
         Dialogs.warnMessage("A compiled main class file could not be found");
      }
      return exists;
   }

   private boolean jarFileExists(String jarName) {
      String execDir = getProjectPath() + F_SEP + getExecutableDirName();
      return new File(execDir + F_SEP + jarName + ".jar").exists();
   }

   private void setStartCommand() {
      StringBuilder sb = new StringBuilder("java ");
      if (getExecutableDirName().length() > 0) {
         sb.append("-cp " + getExecutableDirName() + " ");
      }
      if (getModuleName().length() > 0) {
         String dotted = FileUtils.dottedFileSeparators(getModuleName());
         sb.append(dotted + ".");
      }
      String main = getMainFile();
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }
      sb.append(main);
      startCommand = sb.toString();
   }
   
   public void setIncludedExtArr() {
       if (getIncludedExtensions().length() > 0) {
          includedExt = getIncludedExtensions().split(";");
       }
       else {
          includedExt = null;
       }
    }   
}
