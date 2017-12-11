/**
 * The Java(TM) Compiler is invoked in the method compile() essentially as
 * shown in the docu for the javax.tools.JavaCompiler interface(
 * https://docs.oracle.com/javase/7/docs/api/javax/tools/JavaCompiler.html).
 */
package eg.javatools;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.*;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.console.ConsolePanel;

/**
 * Compiles java files in a given working directory using the
 * JavaCompiler API.
 */
public class Compilation {

   private final static String F_SEP = File.separator;
   private final static String DIVIDING_LINE;

   private final ConsolePanel consPnl;

   private boolean success = false;
   private String errorSource = "";

   static {
      String s = "";
      for (int i = 0; i <= 90; i++) {
         s += "_";
      }
      DIVIDING_LINE = s;
   }

   /**
    * @param consPnl  the reference to {@link ConsolePanel} in whose
    * text area messages are displayed
    */
   public Compilation(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }

   /**
    * Returns the boolean that indicates if the java files could be
    * compiled successfully
    *
    * @return  the boolean value which true in the case of success
    */
   public boolean isCompiled() {
      return success;
   }

   /**
    * Gets a shortened error message which only includes the source
    * file and the line number of the first compilation error from
    * the entire list of errors.<br>
    * The entire list of errors messages is printed to this
    * <code>ConsolePanel</code>.
    *
    * @return  the message
    */
   public String getFirstErrSource() {
      return errorSource;
   }

   /**
    * Invokes the javac compiler
    *
    * @param root  the root directory of the project
    * @param execDir  the name of destination directory of the class files
    * @param sourceDir  the name directory that contains java files or packages
    * @param includedExt  the array of extensions of files that are included
    *       in the executables folder in addition to class files. May be null.
    */
   public void compile(String root, String execDir, String sourceDir,
         String[] includedExt) {

      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      if (compiler == null) {
         Dialogs.errorMessage("The programm may not be run using the JRE in a JDK.");
         return;
      }
      String excludedSearchDir = null;
      if (execDir.length() > 0) {
         excludedSearchDir = execDir;
      }
      String targetDir = targetDir(root, execDir);
      String[] options = new String[] {"-d", targetDir} ;
      Iterable<String> compileOptions = Arrays.asList(options);
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);
      Iterable<? extends JavaFileObject> units;
      List<File> classes = new FilesFinder().filteredFiles(root + F_SEP
            + sourceDir, ".java", excludedSearchDir);

      File[] fileArr = classes.toArray(new File[classes.size()]);
      units = fileManager.getJavaFileObjects(fileArr);
      CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
              compileOptions, null, units);
      success = task.call();
      output(diagnostics);
      try {
         fileManager.close();
      } catch (IOException e) {
         FileUtils.logStack(e);
      }
      if (includedExt != null) {
         copyIncludedFiles(root, sourceDir, execDir, includedExt, excludedSearchDir);
      }
   }

   //
   //--private--/
   //

   private void output(DiagnosticCollector<JavaFileObject> diagnostics) {
      errorSource = "";
      if (success) {
         consPnl.appendText("<<Compilation successful>>");
      }
      else {
         Diagnostic<?> firstSource = diagnostics.getDiagnostics().get(0);
         if (firstSource != null) {
            String file = new File(firstSource.getSource().toString()).getName();
            file = file.substring(0, file.length() - 1);
            errorSource = "First listed error is found in " + file + ", line "
                  + firstSource.getLineNumber();
         }
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            consPnl.appendText(diagnostic.getKind().toString() + ":\n");
            consPnl.appendText(diagnostic.getCode() + ": ");
            consPnl.appendText(diagnostic.getMessage( null ) + "\n");
            consPnl.appendText("at line: " + diagnostic.getLineNumber() + "\n");
            consPnl.appendText("at column: " + diagnostic.getColumnNumber() + "\n");
            if (diagnostic.getSource() != null) {
               consPnl.appendText(diagnostic.getSource().toString() + "\n");
            }
            consPnl.appendText(DIVIDING_LINE + "\n");
         }
      }
   }

   private String targetDir(String root, String execDir) {
      String targetDir;
      if (execDir.length() > 0) {
         File target = new File(root + F_SEP + execDir);
         target.mkdirs();
         targetDir = root + F_SEP + execDir;
      }
      else {
         targetDir = root;
      }
      return targetDir;
   }

   private void copyIncludedFiles(String root, String sourceDir, String execDir,
         String[] includedExt, String excludedDir) {

      if (sourceDir.length() == 0 && execDir.length() == 0) {
         return;
      }
      String searchRoot = root;
      if (sourceDir.length() > 0) {
         searchRoot += F_SEP + sourceDir;
      }
      for (String ext : includedExt) {
         List<File> includedFiles = new FilesFinder().filteredFiles(searchRoot,
               ext, excludedDir);

         try {
            for (File f : includedFiles) {
               String source = f.getPath();
               if (".txt".equals(ext) && source.endsWith("manifest.txt")) {
                  continue;
               }
               if (".properties".equals(ext) && source.endsWith("eadconfig.properties")) {
                  continue;
               }
               String destination = null;
               if (sourceDir.length() > 0 && execDir.length() > 0) {
                  destination = source.replace(sourceDir, execDir);
               }
               else if (sourceDir.length() == 0 && execDir.length() > 0) {
                  String relativePath = source.substring(root.length() + 1);
                  destination = root + F_SEP + execDir + F_SEP + relativePath;
               }
               else if (sourceDir.length() > 0 && execDir.length() == 0) {
                  destination = source.replace(sourceDir, "");
               }
               if (destination != null) {
                  File fDest = new File(destination);
                  java.nio.file.Files.copy(f.toPath(), fDest.toPath(),
                        REPLACE_EXISTING);

                  System.out.println("Copied resources file: " + destination);
               }
            }
         }
         catch (IOException e) {
            FileUtils.logStack(e);
         }
      }
   }
}
