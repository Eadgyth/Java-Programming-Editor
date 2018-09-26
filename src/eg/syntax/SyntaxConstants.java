package eg.syntax;

/**
 * Constants for strings and characters used for searching text elements
 */
public class SyntaxConstants {
   
   /**
    * The single quote character */
   public final static char SINGLE_QUOTE = '\'';
   /**
    * The double quote character */
   public  final static char DOUBLE_QUOTE = '\"';
   /**
    * The slash-star string for block comment start */
   public final static String SLASH_STAR = "/*";
   /**
    * The star-slash string for block comment end */
   public final static String STAR_SLASH = "*/";
   /**
    * The double slash string for line comment start */
   public final static String DOUBLE_SLASH = "//";
   /**
    * The hash sign as String for line comment start */
   public final static String HASH = "#";
   /**
    * The HTML block comment start */
   public final static String HTML_BLOCK_CMNT_START = "<!--";
   /**
    * The HTML block comment end */
   public final static String HTML_BLOCK_CMNT_END = "-->";
   /**
    * The array of characters that end an XML tag */
   public final static char[] XML_TAG_END_CHARS = {' ', '\n', '=', '>', '<', '/'};
}
