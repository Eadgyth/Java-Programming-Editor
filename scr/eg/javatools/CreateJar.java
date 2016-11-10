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

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.ConsolePanel;

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

   private String usedJarName = "";   
   private ConsolePanel cw;

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
    * main class. May be the empty String but must not be null
    * @param dir  the name of the subdirectory that contains class
    * files relative to root. May be the empty String but must not
    * be null
    * @param jarName  the name for the jar. If jarName is the empty
    * String the name of the main class is used
    */
   public void createJar(String root, String main, String packageName,
         String dir, String jarName) {      
      if (jarName.length() == 0) {
         jarName = main;
      }
      usedJarName = jarName;

      File manifest = new File(root + SEP + dir + SEP + "manifest.txt");
      createManifest(manifest, main, packageName);

      ProcessBuilder pb = new ProcessBuilder(commandForJar(root, jarName, dir));
      pb.directory(new File(root + SEP + dir));

      BufferedReader br = null;
      InputStreamReader isr = null;
      InputStream is = null;

      try {
         Process p = pb.start();
         is  = p.getInputStream();
         isr = new InputStreamReader(is);
         br  = new BufferedReader(isr);
         String line;
         while (( line = br.readLine()) != null ) {
            cw.appendText(line + "\n");
         }
         copyJarToProjectDir(dir, root, jarName);
      }
      catch(IOException e) {
         e.printStackTrace();
      }
      finally {
         try {
            br.close();
         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private List<String> commandForJar(String path, String jarName, String dir) {
      List<String> commandForJar = new ArrayList<String>();

      /* ( c: create jar file; v: verbose output; f: output to file, not stdout;
         m: include manifest info ) */ 
      Collections.addAll(commandForJar, "jar", "-cvfm", jarName + ".jar",
            "manifest.txt" );

      List<File> classesPath
           = new SearchFiles().filteredFiles(path + SEP + dir, ".class" );
      List<File> classesRelativePath
           = relativePath(path + SEP + dir, classesPath );
      for (File i : classesRelativePath) {
         commandForJar.add( i.toString());
      }
      return commandForJar;
   }

   private void createManifest(File manifest, String main, String packageName ) {
      try {
         PrintWriter write = new PrintWriter(manifest);
         if (packageName.length() > 0) {
            write.println("Main-Class: " + packageName + "." + main);
         }
         else {
            write.println("Main-Class: " + main);
         }
         write.close();
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }

   private List<File> relativePath(String path, List<File> listOfFiles) {
      if ( path.endsWith( SEP ) ) {
         path = path.substring(0, path.length() - 1);
      }
      List<File> relativePath = new ArrayList<File>();
      for (File i : listOfFiles) {
         String filePath = i.getAbsolutePath();
         relativePath.add(new File(filePath.substring(path.length() + 1)));
      }
      return relativePath;
   }

   public void copyJarToProjectDir(String dir, String projectPath, String main)
         throws IOException 
   {
      if (dir.length() > 0) {
         File source
               = new File(projectPath + SEP + dir + SEP + main + ".jar");
         File destination = new File(projectPath  + SEP + main + ".jar");

         InputStream in = new FileInputStream(source);
         OutputStream out = new FileOutputStream(destination);
         byte[] buf = new byte[1024];
         int length;
         while ((length = in.read(buf)) > 0) {
            out.write(buf, 0, length);
         }
         in.close();
         out.close();
      }
   }
}