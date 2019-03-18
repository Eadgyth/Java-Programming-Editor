package eg.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

/**
 * The screen size and parameters that depend on the screen resolution.
 */
public class ScreenParams {

   private final static int SCREEN_RES
         = Toolkit.getDefaultToolkit().getScreenResolution();

   private final static double SCREEN_RES_RATIO = SCREEN_RES / 72.0;

  /**
   * The screen size */
   public final static Dimension SCREEN_SIZE
        = Toolkit.getDefaultToolkit().getScreenSize();

   /**
    * Returns a new <code>Dimension</code> which may be scaled
    * depending on the criteria in {@link #scaledSize}
    *
    * @param unscaledWidth  the width in pt
    * @param unscaledHeight  the height in pt
    * @return   the Dimension
    */
   public static Dimension scaledDimension(int unscaledWidth, int unscaledHeight) {
      int width = scaledSize(unscaledWidth);
      int height = scaledSize(unscaledHeight);
      return new Dimension(width, height);
   }
   
    /**
    * Scaled sans-serif, plain; unscaled size 8 pt */
   public final static Font SANSSERIF_PLAIN_8
         = new Font("SansSerif", Font.PLAIN, scaledSize(8));

   /**
    * Scaled sans-serif, plain; unscaled size 9 pt */
   public final static Font SANSSERIF_PLAIN_9
         = new Font("SansSerif", Font.PLAIN, scaledSize(9));

   /**
    * Scaled sans-serif, bold; unscaled size 9 pt */
   public final static Font SANSSERIF_BOLD_9
         = new Font("SansSerif", Font.BOLD, scaledSize(9));

   /**
    * Scaled sans-serif, bold; unscaled size 11 pt */
   public final static Font SANSSERIF_BOLD_11
         = new Font("SansSerif", Font.BOLD, scaledSize(11));
   
   /**
    * Returns the specified font with a scaled size and type plain
    *
    * @param f  the font
    * @param unscaledSize  the original unscaled size
    * @return  the font
    */ 
   public static Font scaledFontToPlain(Font f, int unscaledSize) {
      float s = (float) scaledSize(unscaledSize);
      Font fNew = f.deriveFont(Font.PLAIN, s);
      return fNew;
   }
   
   /**
    * Returns the specified font with a scaled size and type bold
    *
    * @param f  the font
    * @param unscaledSize  the original unscaled size
    * @return  the font
    */
   public static Font scaledFontToBold(Font f, int unscaledSize) {
      float s = (float) scaledSize(unscaledSize);
      Font fNew = f.deriveFont(Font.BOLD, s);
      return fNew;
   }
   
   /**
    * Returns the size which is scaled to the screen resolution. The
    * specified <code>unscaledSize</code> is the size for the resolution
    * assumed by Java (72 dpi).
    *
    * @param  unscaledSize  the unscaled size
    * @return  the scaled size
    */
   public static int scaledSize(int unscaledSize) {
      //
      // comment if-else statements and uncomment the last line if ui
      // scaling is not set to 1 in main method in eg.Eadgyth
      //
      /*if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         if (SystemParams.IS_WINDOWS) {
            return Math.round(unscaledSize * 96 / 72);
         }
         else {
            return unscaledSize;
         }
      }
      else {
         return (int) (Math.round(unscaledSize * SCREEN_RES_RATIO));
      }*/
      //
      // comment this line and uncomment if-else statements above if
      // ui scaling is set to 1 main method in eg.Eadgyth
      //
      return (int) (Math.round(unscaledSize * SCREEN_RES_RATIO));
   }
   
   /**
    * Returns the size that is the inversion of the scaled size
    *
    * @param  scaledSize  the previously scaled
    * @return  the rounded inverted scaled size
    * @see scaledSize
    */
   public static int invertedScaledSize(int scaledSize) {
      //
      // comment if-else statements and uncomment last line if ui scaling
      // is not set to 1 in main method in eg.Eadgyth 
      //
      /*if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         if (SystemParams.IS_WINDOWS) {
            return Math.round(scaledSize / (96 / 72));
         }
         else {
            return scaledSize;
         }
      }
      else {
         return (int) (Math.round(scaledSize / SCREEN_RES_RATIO));
      }*/
      //
      // comment this line and uncomment if-else statements above if
      // ui scaling is set to 1 main method in eg.Eadgyth
      //
      return (int) (Math.round(scaledSize / SCREEN_RES_RATIO));
   }

   //
   //--private--/
   //

   private ScreenParams() {}
}
