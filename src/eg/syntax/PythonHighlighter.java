package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Python
 */
public class PythonHighlighter implements Highlighter {

   private final static String TRI_DOUBLE_QUOTE = "\"\"\"";
   private final static String TRI_SINGLE_QUOTE = "\'\'\'";

   private final static String[] KEYWORDS = {
      "and", "as", "assert",
      "break",
      "class", "continue",
      "def", "del",
      "elif", "else", "except",
      "False", "finally", "for", "from",
      "global",
      "if", "import", "in", "is",
      "lambda",
      "None", "nonlocal", "not",
      "or",
      "pass",
      "raise", "return",
      "True", "try",
      "while", "with",
      "yield"
   };

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(TRI_SINGLE_QUOTE, TRI_SINGLE_QUOTE, false)
            && !s.isInBlock(TRI_DOUBLE_QUOTE, TRI_DOUBLE_QUOTE, false))  {

         s.resetAttributes();
         s.keywords(KEYWORDS, true, null, attr.redPlain);
         s.brackets();
         s.braces();
         s.quoteInLine();
         s.lineComments(SyntaxConstants.HASH);
      }
      s.block(TRI_SINGLE_QUOTE, TRI_SINGLE_QUOTE, false);
      s.block(TRI_DOUBLE_QUOTE, TRI_DOUBLE_QUOTE, false);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
