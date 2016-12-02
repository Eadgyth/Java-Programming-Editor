package eg.javatools;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.awt.EventQueue;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.console.ConsolePanel;
import eg.utils.JOptions;

/**
 * The creation of a jar file of a project.
 * <p>
 * TODO: the jar is created in the folder containing the class files (packages)
 * which is the classpath. If the class files are located in a subfolder of
 * the project's root folder the jar is additionally copied to the root. How
 * can the destination for the jar be specified?
 */
public class CreateJar {

   private final static String SEP = File.separator;

   private final ConsolePanel cw;
   private String usedJarName = "";   
   
   /**
    * @param cw  the reference to {@link ConsolePanel} in whose
    * text area messages are displeayed
    */
   public CreateJar(ConsolePanel cw) {
      this.cw = cw;
   }

   /**
    * @return  the name of the jar file actually used
    */
   public String getUsedJarName() {
      return usedJarName;
   }

   /**
    * Creates a jar file
    * <p>
    * @param root  the project's root directory
    * @param main  the name of the main class
    * @param packageName the name of the package that incluses the
    * main class. Can be the empty String but is not null
    * @param classDir  the name of the subdirectory that contains class
    * files. Can be the empty String but is not null
    * @param jarName  the name for the jar. If jarName is the empty
    * String the name of the main class is used
    */
   public void createJar(String root, String main, String packageName,
         String classDir, String jarName) {      
      if (jarName.length() == 0) {
         jarName = main;
      }
      usedJarName = jarName;
      final String jarFin = jarName;

      File manifest = new File(root + SEP + classDir + SEP + "manifest.txt");
      createManifest(manifest, main, packageName);

      EventQueue.invokeLater(() -> {
         ProcessBuilder pb = new ProcessBuilder(commandForJar(root, jarFin, classDir));
         pb.directory(new File(root + SEP + classDir));
         
         BufferedReader br = null;
         InputStreamReader isr;
         InputStream is;
   
         try {
            Process p = pb.start();
            is  = p.getInputStream();
            isr = new InputStreamReader(is);
            br  = new BufferedReader(isr);
            String line;
            while (( line = br.readLine()) != null ) {
               cw.appendText(line + "\n");
            }
            copyJarToProjectDir(classDir, root, jarFin);
         }
         catch(IOException e) {
            System.out.println(e.getMessage());
         }
         finally {
            try {
               if (br != null) {
                  br.close();
               }
            }
            catch (IOException e) {
               System.out.println(e.getMessage());
            }
         }
      });
   }

   private List<String> commandForJar(String path, String jarName, String classDir) {
      List<String> commandForJar = new ArrayList<>();

      /* ( c: create jar file; v: verbose output; f: output to file, not stdout;
         m: include manifest info ) */ 
      Collections.addAll(commandForJar, "jar", "-cvfm", jarName + ".jar",
            "manifest.txt" );

      List<File> classesPath
           = new SearchFiles().filteredFiles(path + SEP + classDir, ".class" );
      List<File> classesRelativePath
           = relativePath(path + SEP + classDir, classesPath );
      for (File i : classesRelativePath) {
         commandForJar.add( i.toString());
      }
      return commandForJar;
   }

   private void createManifest(File manifest, String main, String packageName) {
      try (PrintWriter write = new PrintWriter(manifest)) {
         if (packageName.length() > 0) {
            write.println("Main-Class: " + packageName + "." + main);
         }
         else {
            write.println("Main-Class: " + main);
         }
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }

   private List<File> relativePath(String path, List<File> listOfFiles) {
      if (path.endsWith(SEP)) {
         path = path.substring(0, path.length() - 1);
      }
      List<File> relativePath = new ArrayList<>();
      for (File i : listOfFiles) {
         String filePath = i.getAbsolutePath();
         relativePath.add(new File(filePath.substring(path.length() + 1)));
      }
      return relativePath;
   }

   private void copyJarToProjectDir(String classDir, String projectPath, String jarName)
         throws IOException {
      if (classDir.length() > 0) {
         File source = new File(projectPath + SEP + classDir + SEP + jarName + ".jar");
         File destination = new File(projectPath  + SEP + jarName + ".jar");

         OutputStream out;
         try (InputStream in = new FileInputStream(source)) {
            out = new FileOutputStream(destination);
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
         }
         out.close();
      }
      JOptions.infoMessage("Saved jar file named " + jarName);
   }
}