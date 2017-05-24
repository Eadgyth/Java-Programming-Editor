package eg;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

/**
 * Holds different static values
 */
public class Constants {

    //
    // Colors

    /**
     * The gray color for borders */
    public final static Color BORDER_GRAY = new Color(150, 150, 150);
    
    /**
     * The darker gray color for borders */
    public final static Color BORDER_DARK_GRAY = new Color(100, 100, 100);
    
    /**
     * The light gray color for borders */
    public final static Color BORDER_LIGHT_GRAY = new Color(210, 210, 210);

    // Borders
    //
    /**
     * The  line border with gray color */
    public final static Border BORDER = new LineBorder(BORDER_GRAY, 1);
    
    /**
     * The  line border with dark gray color */
    public final static Border DARK_BORDER = new LineBorder(BORDER_DARK_GRAY, 1);  

    /**
     * Empty border with thickness 5 pt */
    public final static Border EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);
  
    // Fonts
    //

    /**
     * The font sans-serif, plain, size 12 pt */
    public final static Font SANSSERIF_PLAIN_12
          = new Font("SansSerif", Font.PLAIN, 12);
    /**
     * The font sans-serif, bold, size 12 pt */
    public final static Font SANSSERIF_BOLD_12
          = new Font("SansSerif", Font.BOLD, 12);
    /**
     * The font verdana, plain, size 11 pt */
    public final static Font VERDANA_PLAIN_11
          = new Font("Verdana", Font.PLAIN, 11);
          
    // Strings
    //
    
    /**
     * Extensions of files that can be opened */
    public final static String[] SUFFIXES = {".bat", ".java", ".txt", ".properties",
            ".html", ".htm", "xml", ".pl", ".pm"};
}
