package eg.document;

//--Eadgyth--/
import eg.document.styledtext.EditableText;

/**
 * The auto-indentation
 */
public class Indentation {

   private final EditableText txt;

   private String indentUnit = "";
   private int indentLength = 0;
   private boolean curlyBracketMode;

   /**
    * @param txt  the {@link EditableText}
    */
   public Indentation(EditableText txt) {
      this.txt = txt;
   }

   /**
    * Sets the indent unit which consists of a certain number of spaces
    *
    * @param indentUnit  the indent unit
    */
   public void setIndentUnit(String indentUnit) {
      if (indentUnit == null
            || (indentUnit.length() > 0 && !indentUnit.matches("[\\s]+"))) {

         throw new IllegalArgumentException(
               "The indent unit must consist of spaces");
      }
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();
   }

   /**
    * Sets the boolen which indicates that indentation is increased
    * after an opening and reduced after a closing curly bracket
    *
    * @param b  true to enable, false to disable extra curly-bracket
    * indentation
    */
   public void setCurlyBracketMode(boolean b) {
      curlyBracketMode = b;
   }

   /**
    * Returns the currently set indent unit
    *
    * @return  the indent unit
    */
   public String indentUnit() {
      return indentUnit;
   }

   /**
    * Maintains or adjusts the indentation
    *
    * @param pos  the position
    */
   public void adjustIndent(int pos) {
      if ('\n' == txt.text().charAt(pos)) {
         indent(pos);
      }
      else if (pos >= indentLength && curlyBracketMode
            && '}' == txt.text().charAt(pos)) {

         outdent(pos);
      }
   }

   //
   //--private--/
   //

   private void indent(int pos) {
      String currIndent = currentIndentAt(pos);
      if (pos > 1 && curlyBracketMode && '{' == txt.text().charAt(pos - 1)) {
         currIndent += indentUnit;
      }
      txt.insert(pos + 1, currIndent);
   }

   private void outdent(int pos) {
      int outdentPos = pos - indentLength;
      boolean ok = isOutdent(pos)
            && txt.text().substring(outdentPos, pos).equals(indentUnit);

      if (ok) {
         txt.remove(outdentPos, indentLength);
      }
   }

   private boolean isOutdent(int pos) {
      int lastOpeningPos = txt.text().lastIndexOf('{', pos - 1);
      int lastClosingPos = txt.text().lastIndexOf('}', pos - 1);
      int indentAtChange = currentIndentLengthAt(pos);
      int indentAtBraceAhead;
      if (lastOpeningPos > lastClosingPos) {
         indentAtBraceAhead = currentIndentLengthAt(lastOpeningPos);
         return indentAtChange - indentAtBraceAhead >= indentLength;
      }
      else {
         indentAtBraceAhead = currentIndentLengthAt(lastClosingPos);
         return indentAtChange >= indentAtBraceAhead;
      }
   }

   private String currentIndentAt(int pos) {
      StringBuilder currIndent = new StringBuilder();
      if (pos > 1) {
         char[] line = lineAt(pos);
         for (int i = 0; i < line.length && line[i] == ' '; i++) {
            currIndent.append(" ");
         }
      }
      return currIndent.toString();
   }

   private int currentIndentLengthAt(int pos) {
      int length = 0;
      if (pos > 1) {
         char[] line = lineAt(pos);
         for (int i = 0; i < line.length && line[i] == ' '; i++) {
            length++;
         }
      }
      return length;
   }

   private char[] lineAt(int pos) {
      int lineStart = txt.text().lastIndexOf('\n', pos - 1) + 1;
      return txt.text().substring(lineStart, pos).toCharArray();
   }
}
