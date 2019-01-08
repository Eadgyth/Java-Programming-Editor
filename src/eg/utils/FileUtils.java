package eg.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Static methods for file operations
 */
public class FileUtils {

   /**
    * The system's line separator */
   public final static String LINE_SEP = System.lineSeparator();

   /**
    * Replaces slashes in the specified string (forward or backward) with
    * periods
    *
    * @param path  the string
    * @return  the string with slashes replaced with periods
    */
   public static String dottedFileSeparators(String path) {
      String dottedPath = path.replace("\\", "/");
      dottedPath = dottedPath.replace("/", ".");
      return dottedPath;
   }

   /**
    * Returns if a file is writeable and shows a message dialog
    * if not
    *
    * @param f  the file
    * @return  true if writeable
    */
   public static boolean isWriteable(File f) {
      File sameName = new File(f.toString());
      boolean isWriteable = !f.exists() || f.renameTo(sameName);
      if (!isWriteable) {
         Dialogs.errorMessage(
               f.getName()
               + " cannot be accessed."
               + " It may be used by another process.",
               null);
      }
      return isWriteable;
   }

   /**
    * Appends to the file 'log.txt' in the program folder the date,
    * message and stack trace of an exception
    *
    * @param e  the Exception
    */
   public static void log(Exception e) {
      File f = new File("log.txt");
      String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
      try (FileWriter writer = new FileWriter(f, true)) {
         writer.write(date + "\n");
         writer.write(e.getMessage() + LINE_SEP);
         for (StackTraceElement el : e.getStackTrace()) {
            writer.write("   " + el.toString() + LINE_SEP);
         }
         writer.write("_________________" + LINE_SEP);
         Dialogs.errorMessage("Error: " + e.getMessage(), null);
      }
      catch(IOException ioe) {
         throw new RuntimeException(
               "Could not write to the log file", ioe);
      }
   }
}
