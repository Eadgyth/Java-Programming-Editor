package eg.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Static constants and methods to obtain quantities that depend on the screen size and
 * resolution
 */
public class ScreenParams {

   private final static boolean IS_WINDOWS
         = System.getProperty("os.name").toLowerCase().contains("win");
   private final static String VERSION = System.getProperty("java.version");
   private final static boolean IS_JAVA_9_OR_HIGHER = !VERSION.startsWith("1.8");
   private final static int SCREEN_RES
         = Toolkit.getDefaultToolkit().getScreenResolution();
   private final static double SCREEN_RES_RATIO = SCREEN_RES / 72.0;

  /**
   * The screen size */
   public final static Dimension SCREEN_SIZE
        = Toolkit.getDefaultToolkit().getScreenSize();

   /**
    * Returns a new <code>Dimension</code> that is scaled to the
    * screen resolution ratio
    * @see #scaledSize(double)
    *
    * @param width  the width in pt
    * @param height  the height in pt
    * @return   the Dimension
    */
   public static Dimension scaledDimension(int width, int height) {
      width = scaledSize(width);
      height = scaledSize(height);
      return new Dimension(width, height);
   }

   /**
    * Returns an integer that is the rounded product of the specified
    * size and the resolution ratio.<br>
    * This ratio is the screen resolution divided by the graphics
    * resolution assumed by Java (72 dpi).<br>
    * If the program is run using Java 9 or higher under Windows the
    * resolution ratio is constantly 96/72. If the program is run using
    * Java 9 or higher on another operation system <code>size</code> is
    * returned unchanged
    *
    * @param size  the size
    * @return  the rounded rescaled size
    */
   public static int scaledSize(double size) {
      //System.out.println(VERSION); 
      if (IS_JAVA_9_OR_HIGHER) {
         if (IS_WINDOWS) {
            return (int) (Math.round(size * 96/72));
         }
         else {
            return (int) size;
         }
      }
      else {
         return (int) (Math.round(size * SCREEN_RES_RATIO));
      }
   }
}
