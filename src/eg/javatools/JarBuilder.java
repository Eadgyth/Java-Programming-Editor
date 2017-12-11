package eg.javatools;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.console.ConsolePanel;
import eg.utils.FileUtils;

/**
 * The creation of a jar file of a project.
 * <p>
 * The jar is saved in the folder containing the class files (packages)
 * which is the classpath.
 */
public class JarBuilder {

   private final static String F_SEP = File.separator;
   private final ConsolePanel consPnl;

   /**
    * @param consPnl  the reference to {@link ConsolePanel}
    */
   public JarBuilder(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }

   /**
    * Creates a jar file
    *
    * @param root  the root directory of the project
    * @param main  the name of the main class
    * @param packagePath  the package path relative to the root or
    *       to the sources directory where the main class is found.
    *       Can be the empty string but cannot be not null
    * @param execDir  the name of the directory that contains class
    *       files. Can be the empty string but cannot be null.
    * @param sourceDir  the name of the directory that contains
    *       source files. Can be an empty string but cannot be not null.
    * @param jarName  the name for the jar. If jarName is the empty
    *       string the name of the main class is used
    * @param includedExt  the array of extensions of files that are
    *       included in the jar file in addition to class files. May be
    *       null.
    * @throws IOException  if the process that creates a jar cannot receive
    *       any input
    */
   public void createJar(String root, String main, String packagePath,
         String execDir, String sourceDir, String jarName, String[] includedExt)
               throws IOException {

      File manifest = new File(root + F_SEP + execDir + F_SEP + "manifest.txt");
      createManifest(manifest, main, packagePath);
      List<String> cmd = jarCmd(root, jarName, execDir, sourceDir, includedExt);
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.directory(new File(root + F_SEP + execDir));
      pb.redirectErrorStream(true);
      Process p = pb.start();
      try (BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream()))) {

         String ch;
         while((ch = br.readLine()) != null) {
            consPnl.appendText(ch + "\n");
         }
      }
   }

   //
   //--private---/
   //

   private List<String> jarCmd(String root, String jarName, String execDir,
         String sourceDir, String[] includedExt) {

      List<String> cmd = new ArrayList<>();
      //
      // c: create jar file; v: verbose output; f: output to file, not stdout;
      // m: include manifest info )
      Collections.addAll(cmd, "jar", "-cvfm", jarName + ".jar", "manifest.txt");
      String searchRoot = root;
      if (execDir.length() > 0) {
         searchRoot += F_SEP + execDir;
      }
      String excludedSearchDir = null;
      if (sourceDir.length() > 0) {
         excludedSearchDir = sourceDir;
      }
      List<File> classes
            = new FilesFinder().filteredFiles(searchRoot, ".class", excludedSearchDir);
      List<File> relativeClassFilePaths = relativePaths(searchRoot, classes);
      for (File i : relativeClassFilePaths) {
         cmd.add(i.toString());
      }
      if (includedExt != null) {
         for (String ext : includedExt) {
            List<File> includedFiles
                  = new FilesFinder().filteredFiles(searchRoot, ext, excludedSearchDir);
            List<File> relativeInclFilePaths = relativePaths(searchRoot, includedFiles);
            for (File f : relativeInclFilePaths) {
               String path = f.getPath();
               if (".txt".equals(ext) && path.endsWith("manifest.txt")) {
                  continue;
               }
               if (".properties".equals(ext) && path.endsWith("eadconfig.properties")) {
                  continue;
               }
               cmd.add(f.toString());
            }
         }
      }
      return cmd;
   }

   private void createManifest(File manifest, String main, String packagePath) {
      try (PrintWriter write = new PrintWriter(manifest)) {
         if (packagePath.length() > 0) {
            String dotted = FileUtils.dottedFileSeparators(packagePath);
            write.println("Main-Class: " + dotted + "." + main);
         }
         else {
            write.println("Main-Class: " + main);
         }
      }
      catch(IOException e) {
         FileUtils.logStack(e);
      }
   }

   private List<File> relativePaths(String searchPath, List<File> listOfFiles) {
      if (searchPath.endsWith(F_SEP)) {
         searchPath = searchPath.substring(0, searchPath.length() - 1);
      }
      List<File> relativePath = new ArrayList<>();
      for (File i : listOfFiles) {
         String filePath = i.getAbsolutePath();
         relativePath.add(new File(filePath.substring(searchPath.length() + 1)));
      }
      return relativePath;
   }
}
