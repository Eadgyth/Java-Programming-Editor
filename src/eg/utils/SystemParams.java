package eg.utils;

import java.io.File;

/**
 * Static system properties
 */
public class SystemParams {
   /**
    * True if the OS is Windows, false otherwise */
   public final static boolean IS_WINDOWS;
   /**
    * The Java version */
   public final static String JAVA_VERSION;
   /**
    * True if the Java version is higher than 8 */
   public final static boolean IS_JAVA_9_OR_HIGHER;
   /**
    * True if the java version is 13 */
   public final static boolean IS_JAVA_13;
   /**
    * The path of the '.eadgyth' folder where data are saved */
   public final static String EADGYTH_DATA_DIR;

   static {
      String os = System.getProperty("os.name");
      IS_WINDOWS = os.startsWith("Windows");
      String userHome = System.getProperty("user.home");
      EADGYTH_DATA_DIR = userHome + File.separator + ".eadgyth";
      JAVA_VERSION = System.getProperty("java.version");
      IS_JAVA_9_OR_HIGHER = !JAVA_VERSION.startsWith("1.8");
      IS_JAVA_13 = JAVA_VERSION.startsWith("13");
   }

   /**
    * Returns if the Eadgyth data directory '.eadgyth' exists
    * in the user home directory
    *
    * @return  true if the directory exists, false otherwise
    * @see #EADGYTH_DATA_DIR
    */
   public static boolean existsEadgythDataDir() {
      return new File(EADGYTH_DATA_DIR).exists();
   }

   //
   //--private--/
   //

   private SystemParams() {}
}