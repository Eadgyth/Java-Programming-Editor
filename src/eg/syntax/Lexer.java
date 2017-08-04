package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import java.awt.Color;

//--Eadgyth--//
import eg.utils.Finder;

/**
 * The search and coloring of different syntax elements
 */
public class Lexer {

   private final static Color BLUE   = new Color(20, 30, 255);
   private final static Color RED    = new Color(230, 0, 80);
   private final static Color GREEN  = new Color(80, 190, 80);
   private final static Color GRAY   = new Color(30, 30, 30);
   private final static Color PURPLE = new Color(180, 30, 220);
   private final static Color ORANGE = new Color(255, 127, 20);

   private final SimpleAttributeSet normalSet      = new SimpleAttributeSet();
   private final SimpleAttributeSet redPlainSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet bluePlainSet   = new SimpleAttributeSet();
   private final SimpleAttributeSet blueBoldSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet greenPlainSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet grayBoldSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet orangePlainSet = new SimpleAttributeSet();
   private final SimpleAttributeSet purplePlainSet = new SimpleAttributeSet();

   private final StyledDocument doc;

   private Colorable colorable;
   private String allText = "";
   private String toColor = "";
   private int pos;
   private int posStart;
   private boolean isBlockCmnt = true;
   private boolean isTypeMode = false;

   /**
    * Creates a Lexer
    *
    * @param doc  the <code>StyledDocument</code> that contains
    * the text to color
    */
   public Lexer(StyledDocument doc) {
      this.doc = doc;
      setStyles();
   }

   /**
    * Sets a <code>Colorable</code>
    *
    * @param colorable  a {@link Colorable} object
    */
   public void setColorable(Colorable colorable) {
      this.colorable = colorable;
   }

   /**
    * Sets the text to be colored
    *
    * @param allText  the entire text of the document
    * @param toColor  the line or lines to be colored
    * @param pos  the pos within the document where a change happened
    * @param posStart  the pos within the document where
    * <code>toColor</code> starts
    */
   public void setTextToColor(String allText, String toColor, int pos,
         int posStart) {

      this.allText = allText;
      this.toColor = toColor;
      this.pos = pos;
      this.posStart = posStart;
   }

   /**
    * Enables type mode.
    * If enabled, coloring may take place in sections (single lines)
    * of the document taking into account, however, corrections that
    * need multiline analysis (i.e. commenting/uncommenting of
    * block comments).
    *
    * @param isEnabled  true to enable type mode
    */
   public void enableTypeMode(boolean isEnabled) {
      this.isTypeMode = isEnabled;
   }

   /**
    * (Re-)colors in black this section of text that is to be colored
    */
   public void setCharAttrBlack() {
      setCharAttr(posStart, toColor.length(), normalSet);
   }

   /**
    * (Re-)colors in black the entire text
    */
   public void setAllCharAttrBlack() {
      setCharAttr(0, doc.getLength(), normalSet);
   }

   /**
    * Searches and colors keywords in red
    *
    * @param keys  the array of keywords
    * @param reqWord  if the keyword must be a word
    */
   public void keywordsRed(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, redPlainSet, reqWord);
      }
   }

   /**
    * Searches and colors keywords in blue
    *
    * @param keys  the array of keywords
    * @param reqWord  if the keyword must be a word
    */
   public void keywordsBlue(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, bluePlainSet, reqWord);
      }
   }

   /**
    * Searches and colors variables that start with a sign in blue
    * (like $ in Perl)
    *
    * @param signs  the array of characters that marks a variable
    * @param endChars  the array of characters that mark the end of the
    * variable
    */
   public void signedKeywordsBlue(String[] signs, char[] endChars) {
      for (String s : signs) {
         signedVariable(s, endChars);
      }
   }

   /**
    * Searches and colors html tags in blue
    *
    * @param tags  the array of tags
    */
   public void htmlTags(String[] tags) {
      for (String s : tags) {
         htmlTag(s);
      }
   }

   /**
    * Searched and colors html attributes in red
    *
    * @param attributes  the array of attributes
    */
   public void htmlAttributes(String[] attributes) {
       for (String s : attributes) {
          htmlAttr(s);
       }
   }

   /**
    * Searches the braces and displays them in bold gray
    */
   public void bracesGray() {
      key("{", grayBoldSet, false);
      key("}", grayBoldSet, false);
   }

   /**
    * Searches the brackets and displays them in bold blue
    */
   public void bracketsBlue() {
      key("(", blueBoldSet, false);
      key(")", blueBoldSet, false);
   }

   /**
    * Searches and colors quoted text in orange. The quote mark is ignored
    * if a backslash precedes it
    */
   public void quotedLineWise() {
      if (Finder.isMultiline(toColor)) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            quoted(s, posStart + sum, "\"");
            quoted(s, posStart + sum, "\'");
            sum += s.length() + 1;
         }
      }
      else {
         quoted(toColor, posStart, "\"");
         quoted(toColor, posStart, "\'");
      }
   }

   /**
    * Searches and colors quoted text in a html document. The quote
    * mark is ignored if a backslash precedes it
    */
   public void quotedLineWiseHtml() {
      if (Finder.isMultiline(toColor)) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            htmlQuoted(s, posStart + sum, "\"");
            htmlQuoted(s, posStart + sum, "\'");
            sum += s.length() + 1;
         }
      }
      else {
         htmlQuoted(toColor, posStart, "\"");
         htmlQuoted(toColor, posStart, "\'");
      }
   }

   /**
    * Searches and colors line comments in green
    *
    * @param lineCmnt  the String that represents the start of a line
    * comment
    * @param exception  the character that disables the line comment
    * when it precedes <code>lineCmt</code>. The null character to skip
    * any exception
    */
   public void lineComments(String lineCmnt, char exception) {
      int start = 0;
      boolean isException = false;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (exception != '\0' && start > 0) {
               isException = false;
               isException = toColor.charAt(start - 1) == exception;
            }
            int length = 0;
            if (!isException
                  && !SyntaxUtils.isInQuotes(toColor, start, lineCmnt.length())) {

               int lineEnd = toColor.indexOf("\n", start + 1);
               if (lineEnd != -1) {
                  length = lineEnd - start;
               }
               else {
                  length = toColor.length() - start;
               }
               setCharAttr(start + posStart, length, greenPlainSet);
            }
            start += length + 1;
         }
      }
   }

   /**
    * Searches and colors in green block comments
    *
    * @param blockStart  the String that represents the start of a block
    * @param blockEnd  the String that represents the end of a block
    */
   public void blockComments(String blockStart, String blockEnd) {
      if (!isBlockCmnt) {
         return;
      }
      removedFirstBlockStart(allText, blockStart, blockEnd);
      int start = 0;
      int length = 0;
      while (start != -1) {
         start = allText.indexOf(blockStart, start);
         int end = 0;
         if (start != -1) {
            length = 0;
            if (!SyntaxUtils.isInQuotes(allText, start, blockStart.length())) {
               end = SyntaxUtils.nextBlockEnd(allText, start + 1,
                     blockStart, blockEnd);
               if (end != -1) {
                  length = end - start + blockEnd.length();
                  setCharAttr(start, length, greenPlainSet);
                  removedBlockStart(allText, end + blockEnd.length(),
                         blockStart, blockEnd);
               }
               else {
                  removedBlockEnd(allText, start, blockStart);
               }
            }
            start += length + 1;
         }
      }
   }

   /**
    * Returns if this pos where a change happened is found in a block of
    * text that is delimited by the specified start and end signals
    *
    * @param blockStart  the String that defines the block start
    * @param blockEnd  the String that defines the block end
    * @return  if this pos is found in a certain block of text
    */
   public boolean isInBlock(String blockStart, String blockEnd) {
      return isInBlock(blockStart, blockEnd, pos);
   }

   //
   //--used in this package and in this class--
   //

   void color() {
       colorable.color(this);
   }

   //
   //--private methods--
   //

   private void key(String str, SimpleAttributeSet set, boolean reqWord) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(str, start);
         if (start != -1) {
            boolean ok = !reqWord || SyntaxUtils.isWord(toColor, start, str.length());
            if (ok) {
               setCharAttr(start + posStart, str.length(), set);
            }
            start += str.length();
         }
      }
   }

   private void htmlQuoted(String toColor, int posStart, String quoteMark) {
      boolean isSingleQuote = quoteMark.equals("\'");
      boolean notQuoted = true;
      int start = 0;
      int end = 0;
      int length = 0;
      while (start != -1 && end != -1) {
         start = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start);
         if (start != -1) {
            if (isSingleQuote) {
               notQuoted = SyntaxUtils.isNotQuoted(toColor, start);
            }
            end = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start + 1);
            if (end != -1) {
               if (isSingleQuote) {
                  notQuoted = notQuoted && SyntaxUtils.isNotQuoted(toColor, end);
               }
               length = end - start + 1;
               if (notQuoted) {
                  int absStart = start + posStart;
                  if (isInBlock("<", ">", absStart)) {
                     setCharAttr(absStart, length, purplePlainSet);
                  }
                  else if (isInBlock("<script>", "</script>", absStart)) {
                     setCharAttr(absStart, length, orangePlainSet); 
                  }
               }
               start += length + 1;
            }
         }
      }
   }

   private void quoted(String toColor, int posStart, String quoteMark) {
      boolean isSingleQuote = quoteMark.equals("\'");
      boolean notQuoted = true;
      int start = 0;
      int end = 0;
      int length = 0;
      while (start != -1 && end != -1) {
         start = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start);
         if (start != -1) {
            if (isSingleQuote) {
               notQuoted = SyntaxUtils.isNotQuoted(toColor, start);
            }
            end = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start + 1);
            if (end != -1) {
               if (isSingleQuote) {
                  notQuoted = notQuoted && SyntaxUtils.isNotQuoted(toColor, end);
               }
               length = end - start + 1;
               if (notQuoted) {
                  int absStart = start + posStart;
                  setCharAttr(absStart, length, orangePlainSet);
               }
               start += length + 1;
            }
         }
      }
   }

   private void htmlTag(String tag) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(tag, start);
         if (start != -1) {
            if (SyntaxUtils.isHtmlTag(toColor, start, start + tag.length())) {
               setCharAttr(start + posStart, tag.length(), blueBoldSet);
            }
            start += tag.length();
         }
      }
   }

   private void htmlAttr(String attr) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(attr, start);
         if (start != -1) {
            int absStart = start + posStart;
            boolean ok = (SyntaxUtils.isWord(toColor, start, attr.length())
                  & isInBlock("<", ">", absStart))
                  && !SyntaxUtils.isTagStart(toColor, start);
            if (ok) {
               setCharAttr(absStart, attr.length(), redPlainSet);
            }
            start += attr.length();
         }
      }
   }

   private void signedVariable(String sign,  char[] endChars) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length = 0;
         if (start != -1) {
            if (SyntaxUtils.isWordStart(toColor, start)) {
               length = SyntaxUtils.wordLength(toColor, start, endChars);
               setCharAttr(start + posStart, length, bluePlainSet);
               start += length;
            }
            else {
               start++;
            }
         }
      }
   }

   private void removedFirstBlockStart(String allText, String blockStart,
         String blockEnd) {

      if (isTypeMode) {
         int firstEnd = SyntaxUtils.nextBlockEnd(allText, 0, blockStart,
               blockEnd);
         if (firstEnd != -1) {
            colSectionExBlock(allText, allText.substring(0, firstEnd + 2), 0);
         }
      }
   }

   private void removedBlockStart(String allText, int pos, String blockStart,
         String blockEnd) {

      if (isTypeMode) {
         int lastStart = SyntaxUtils.lastBlockStart(allText, pos, blockStart,
               blockEnd);
         int nextEnd   = SyntaxUtils.nextBlockEnd(allText, pos, blockStart,
               blockEnd);
         if (nextEnd != -1 && lastStart == -1) {
            String toUncomment = allText.substring(pos, nextEnd + blockEnd.length());
            colSectionExBlock(allText, toUncomment, pos);
         }
      }
   }

   private void removedBlockEnd(String allText, int pos, String blockStart) {
      if (isTypeMode) {
         int nextStart = allText.indexOf(blockStart, pos + 1);
         while (nextStart != -1 && SyntaxUtils.isInQuotes(allText, nextStart,
                blockStart.length())) {
            nextStart = allText.lastIndexOf(blockStart, nextStart + 1);
         }
         if (nextStart != -1) {
            colSectionExBlock(allText, allText.substring(pos, nextStart), pos);
         }
         else {
            colSectionExBlock(allText, allText.substring(pos), pos);
         }
      }
   }

   private void colSectionExBlock(String allText, String section, int pos) {
      if (isTypeMode) {
         enableTypeMode(false);
         isBlockCmnt = false;
         setTextToColor(allText, section, pos, pos);
         color();
         enableTypeMode(true);
         isBlockCmnt = true;
      }
   }

   private boolean isInBlock(String blockStart, String blockEnd, int pos) {
      int lastStart = SyntaxUtils.lastBlockStart(allText, pos, blockStart,
            blockEnd);
      int nextEnd = SyntaxUtils.nextBlockEnd(allText, pos, blockStart,
            blockEnd);
      return lastStart != -1 & nextEnd != -1;
   }

   private void setCharAttr(int start, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(start, length, set, false);
   }

   private void setStyles() {
      StyleConstants.setForeground(normalSet, Color.BLACK);
      StyleConstants.setBold(normalSet, false);

      StyleConstants.setForeground(redPlainSet, RED);
      StyleConstants.setBold(redPlainSet, false);

      StyleConstants.setForeground(bluePlainSet, BLUE);
      StyleConstants.setBold(bluePlainSet, false);

      StyleConstants.setForeground(blueBoldSet, BLUE);
      StyleConstants.setBold(blueBoldSet, true);

      StyleConstants.setForeground(greenPlainSet, GREEN);
      StyleConstants.setBold(greenPlainSet, false);

      StyleConstants.setForeground(purplePlainSet, PURPLE);
      StyleConstants.setBold(purplePlainSet, false);

      StyleConstants.setForeground(grayBoldSet, GRAY);
      StyleConstants.setBold(grayBoldSet, true);

      StyleConstants.setForeground(orangePlainSet, ORANGE);
      StyleConstants.setBold(orangePlainSet, false);
   }
}
