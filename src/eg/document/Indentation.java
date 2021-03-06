package eg.document;

//--Eadgyth--/
import eg.document.styledtext.EditableText;

/**
 * The auto-indentation
 */
public class Indentation {

   private final EditableText txt;
   private final StringBuilder joinTabs = new StringBuilder();
   private final StringBuilder joinSpaces = new StringBuilder();
   private final StringBuilder indent = new StringBuilder();

   private String indentUnit = "";
   private int indentLength = 0;
   private boolean indentTab;
   private boolean curlyBracketMode;

   /**
    * @param txt  the {@link EditableText}
    */
   public Indentation(EditableText txt) {
      this.txt = txt;
   }

   /**
    * Sets the indent unit which consists of a certain number of
    * spaces
    *
    * @param indentUnit  the indent unit which consists of spaces
    * @param indentTab  true to indent tabs, false to indent spaces
    * false otherwise
    */
   public void setMode(String indentUnit, boolean indentTab) {
      if (indentUnit == null || indentUnit.isEmpty()
            || !indentUnit.trim().isEmpty()) {

         throw new IllegalArgumentException(
               "The indent unit does not consist of white spaces");
      }
      this.indentUnit = indentUnit;
      this.indentTab = indentTab;
      indentLength = indentUnit.length();
   }

   /**
    * Enables or disables curly bracket mode
    *
    * @param b  true to enable, false to disable
    */
   public void enableCurlyBracketMode(boolean b) {
      curlyBracketMode = b;
   }

   /**
    * Returns the indent unit
    *
    * @return  the indent unit
    */
   public String indentUnit() {
      return indentUnit;
   }

   /**
    * Returns if tabs are used for indentation
    *
    * @return  true if tabs, false if spaces are used
    */
   public boolean indentTab() {
      return indentTab;
   }

   /**
    * Maintains or adjusts the indentation
    *
    * @param pos  the position
    */
   public void adjustIndent(int pos) {
      char charAtPos = txt.text().charAt(pos);
      if ('\n' == charAtPos) {
         indent(pos);
      }
      else if (curlyBracketMode && '}' == charAtPos) {
         outdent(pos);
      }
   }

   //
   //--private--/
   //

   private void indent(int pos) {
      indent.setLength(0);
      int length = indentLengthAt(pos);
      int remainder = 0;
      if (indentTab) {
         remainder = length % indentLength;
         length = length / indentLength;
         indent.append(joinTabs(length));
      }
      else {
         indent.append(joinSpaces(length));
      }
      if (curlyBracketMode && pos >= 1 && '{' == txt.text().charAt(pos - 1)) {
         if (indentTab) {
            indent.append('\t');
         }
         else {
            indent.append(indentUnit);
         }
      }
      if (remainder > 0) {
         indent.append(joinSpaces(remainder));
      }
      txt.insert(pos + 1, indent.toString());
   }

   private void outdent(int pos) {
      int lineStart = txt.text().lastIndexOf('\n', pos - 1) + 1;
      if (pos == lineStart) {
         return;
      }
      char[] line = lineUpTo(pos);
      for (int i = 0; i < line.length; i++) {
         if (line[i] != '\t' && line[i] != ' ') {
            return;
         }
      }
      int length = outdentPos(pos);
      if (length < 0) {
         return;
      }
      indent.setLength(0);
      int remainder = 0;
      if (indentTab) {
         remainder = length % indentLength;
         length = length / indentLength;
         indent.append(joinTabs(length));
         if (remainder > 0) {
            indent.append(joinSpaces(remainder));
         }
      }
      else {
         indent.append(joinSpaces(length));
      }
      txt.remove(lineStart, pos - lineStart);
      txt.insert(lineStart, indent.toString());
   }

    private int outdentPos(int pos) {
      int outdentPos = 0;
      int lastOpeningPos = txt.text().lastIndexOf('{', pos - 1);
      int lastClosingPos = txt.text().lastIndexOf('}', pos - 1);
      int indentAtLastBrace = 0;
      if (lastOpeningPos > lastClosingPos) {
         indentAtLastBrace = indentLengthAt(lastOpeningPos);
         outdentPos = indentAtLastBrace;
      }
      else if (lastClosingPos > lastOpeningPos) {
         indentAtLastBrace = indentLengthAt(lastClosingPos);
         outdentPos = indentAtLastBrace - indentLength;
      }
      return outdentPos;
	}

   private int indentLengthAt(int pos) {
      int countTabs = 0;
      int countSpaces = 0;
      if (pos > 1) {
         char[] line = lineUpTo(pos);
         for (int i = 0; i < line.length; i++) {
            if (line[i] == ' ') {
               countSpaces++;
            }
            else if (line[i] == '\t') {
               countTabs++;
               if (countSpaces < indentLength) {
                  countSpaces = 0;
               }
            }
            else {
               break;
            }
         }
      }
      return countTabs * indentLength + countSpaces;
   }

   private char[] lineUpTo(int pos) {
      int lineStart = txt.text().lastIndexOf('\n', pos - 1) + 1;
      return txt.text().substring(lineStart, pos).toCharArray();
   }

   private String joinTabs(int length) {
      joinTabs.setLength(0);
      for (int i = 0; i < length; i++) {
         joinTabs.append('\t');
      }
      return joinTabs.toString();
   }

   private String joinSpaces(int length) {
      joinSpaces.setLength(0);
      for (int i = 0; i < length; i++) {
         joinSpaces.append(' ');
      }
      return joinSpaces.toString();
   }
}
