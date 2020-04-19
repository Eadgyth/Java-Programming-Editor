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
import java.io.StringWriter;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.TaskRunner.ConsolePrinter;

/**
 * The compilation of java files using the Java Compiler API
 */
public class Compilation {

   private final static String DIVIDING_LINE
         = new String(new char[90]).replace('\0', '_');

   private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
   private final FilesFinder fFind = new FilesFinder();
   private final ConsolePrinter pr;

   private boolean success = false;

   /**
    * @param printer  the reference <code>ConsolePrinter</code>
    */
   public Compilation(ConsolePrinter printer) {
      pr = printer;
   }

   /**
    * Compiles java files and copies non java files with the specified
    * extensions to the compilation
    *
    * @param classDir  the destination directory for the compiles class
    * files/packages
    * @param sourceDir  the directory that contains java files/packages
    * @param nonJavaExt  the array of extensions of files that are copied
    * to the compilation if classDir and sourceDir differ. May be the
    * zero length array.
    * @param libs  the libraries in which individual paths are separated
    * by the system's path separator. May be the empty string
    * @param options  Compiler options, in which several options and
    * arguments are separated by spaces.
    */
   public void compile(
            String classDir,
            String sourceDir,
            String[] nonJavaExt,
            String libs,
            String options) {

      if (compiler == null) {
         Dialogs.errorMessage("The compiler was not found.", null);
         return;
      }
      success = false;
      DiagnosticCollector<JavaFileObject> diagnostics
            = new DiagnosticCollector<>();

      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);
      //
      // Java files
      List<File> sources = fFind.filteredFiles(sourceDir, ".java", classDir, "");
      File[] fileArr = sources.toArray(new File[sources.size()]);
      Iterable<? extends JavaFileObject>units
            = fileManager.getJavaFileObjects(fileArr);
      //
      // Compiler options
      Iterable<String> compileOptions
            = compileOptions(classDir, sourceDir, libs, options);

      StringWriter writer = new StringWriter();
      //
      // compile, print messages
      try {
         CompilationTask task = compiler.getTask(
               writer,
               fileManager,
               diagnostics,
               compileOptions,
               null,
               units);

         success = task.call();
         if (nonJavaExt.length > 0) {
            copyFiles(sourceDir, classDir, nonJavaExt);
         }
         pr.printLine(writer.toString());
         printDiagnostics(diagnostics);
      }
      catch (IllegalStateException e) {
         FileUtils.log(e);
      }
      catch (RuntimeException e) {
         //
         // not checked before if compile option arguments are valid
         pr.printLine(e.getMessage());
      }
      finally {
         try {
            fileManager.close();
         } catch (IOException e) {
            FileUtils.log(e);
         }
      }
   }

   //
   //--private--/
   //

   private Iterable<String> compileOptions(String classDir, String sourceDir,
         String libs, String options) {

      List <String> optList = new ArrayList<>();
      optList.add("-d");
      optList.add(classDir);
      if (!sourceDir.isEmpty()) {
         optList.add("-sourcepath");
         optList.add(sourceDir);
      }
      if (!libs.isEmpty()) {
         optList.add("-cp");
         optList.add(libs);
      }
      if (!options.isEmpty()) {
         String[] test = options.split("\\s+");
         boolean ok = true;
         String msg = "";
         for (int i = 0; i < test.length; i++) {
            int args;
            if (test[i].startsWith("-")) {
               args = compiler.isSupportedOption(test[i]);
               ok = -1 < args;
               if (!ok) {
                  msg = test[i] + " is not a valid compiler option.";
                  break;
               }
               if (args == 0) {
                  ok = i == test.length - 1
                        || (i < test.length - 1 && test[i + 1].startsWith("-"));

                  if (!ok) {
                     msg = test[i] + "  does not take arguments.";
                     break;
                  }
               }
               if (args > 0) {
                  ok = i < test.length - 1 && !test[i + 1].startsWith("-");
                  if (!ok) {
                     msg = test[i] + " requires an argument.";
                     break;
                  }
               }
            }
            else {
               ok = i > 0 && test[i - 1].startsWith("-");
               if (!ok) {
                  msg = test[i] + " is not a valid compiler option.";
                  break;
               }
            }
            if (ok) {
               optList.add(test[i]);
            }
         }
         if (!ok) {
            String err = "NOTE: " + msg;
            pr.printBr(err);
         }
      }
      return optList;
   }

   private void copyFiles(String sourceDir, String classDir,
         String[] nonJavaExt) {

      if (classDir.equals(sourceDir)) {
         return;
      }

      for (String ext : nonJavaExt) {
         List<File> toCopy = fFind.filteredFiles(sourceDir, ext, classDir, "");
         if (toCopy.isEmpty()) {
            String copyFilesErr =
                  "NOTE: Files with extension \""
                  + ext
                  + "\" for copying to the compilation were not found";

            pr.printBr(copyFilesErr);
         }
         else {
            try {
               for (File f : toCopy) {
                  String source = f.getPath().replace("\\", "/");
                  String destination = source.replace(
                        sourceDir.replace("\\", "/"), classDir.replace("\\", "/"));

                  File fDest = new File(destination);
                  if (fDest.isAbsolute()) {
                     File destDir = fDest.getParentFile();
                     if (!destDir.exists()) {
                        destDir.mkdirs();
                     }
                     Files.copy(f.toPath(), fDest.toPath(),
                           StandardCopyOption.REPLACE_EXISTING);
                     }
               }
            }
            catch (IOException e) {
               FileUtils.log(e);
            }
         }
      }
   }

   private void printDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
      if (success) {
         pr.printBr("Compilation successful");
      }
      if (!diagnostics.getDiagnostics().isEmpty()) {
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            pr.print(diagnostic.getKind().toString() + ":\n");
            pr.print(diagnostic.getCode() + ":\n   ");
            pr.printLine(diagnostic.getMessage( null ));
            pr.printLine("   at line: " + diagnostic.getLineNumber());
            pr.printLine("   at column: " + diagnostic.getColumnNumber());
            if (diagnostic.getSource() != null) {
               pr.printLine(diagnostic.getSource().toString());
            }
            pr.printLine(DIVIDING_LINE);
         }
      }
   }
}
