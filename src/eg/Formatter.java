package eg;

//--Eadgyth--//

import eg.ui.EditArea;
import eg.ui.FontSettingWin;

/**
 * The formatting of <code>EditArea</code> objects
 *
 * @see EditArea
 */
public class Formatter {

   private final FontSettingWin fontWin;
   private final Prefs prefs = new Prefs();
   private final EditArea[] editArea;

   private String fontKey;
   private String fontSizeKey;
   private String wordwrapKey;

   private int index = 0;
   private boolean isWordwrap;
   private String font;
   private int fontSize;
   private boolean isShowLineNr = false;

   /**
    * Creates a <code>Formatter</code> for a given number of
    * <code>EditArea</code> objects. Only if <code>number</code> is
    * equal to 1 a formatted EditArea is created and accessed through
    * {@link #editArea()}. If <code>number</code> is minimum 2 an
    * initially empty array of type <code>EditArea</code> and size
    * correposding to <code>number</code> is created.
    *
    * @param number  the number of EditArea objects
    * @param keyPrefix  A prefix for the keys that are used for the
    * format properties set in {@link Prefs}. Can be the empty string
    */
   public Formatter (int number, String keyPrefix) {
      setPropertyKeys(keyPrefix);
      getFormatProperties();
      fontWin = new FontSettingWin(font, fontSize);
      fontWin.okAct(e -> setFont());
      editArea = new EditArea[number];
      if (number == 1) {
         editArea[0] = formattedEditArea();
      }
   }

   /**
    * Returns this single formatted <code>EditArea</code>
    *
    * @return  this EditArea
    */
   public EditArea editArea() {
      if (editArea.length > 1) {
         throw new IllegalStateException(
               "Formatting more than one EditArea is specified.");
      }
      return editArea[0];
   }

   /**
    * Returns this <code>EditArea</code> array
    *
    * @return  this EditArea array
    */
   public EditArea[] editAreaArray() {
      if (editArea.length < 2) {
         throw new IllegalStateException(
               "Formatting only one EditArea is set.");
      }
      return editArea;
   }

   /**
    * Assigns a new formatted <code>EditArea</code> to this EditArea
    * array at the specified index
    *
    * @param i  the index of the array element
    */
   public void createEditAreaAt(int i) {
      if (i > editArea.length - 1) {
         throw new IllegalArgumentException(
               "i is larger than the maximum possible index");
      }
      if (editArea.length < 2) {
         throw new IllegalStateException(
               "Formatting only one EditArea is set.");
      }
      editArea[i] = formattedEditArea();
   }

   /**
    * Selects the element in this <code>EditArea</code> array in which
    * the wordwrap state may be changed.
    *
    * @param i  the index of the array element
    */
   public void setIndex(int i) {
      if (editArea[i] == null) {
         throw new IllegalStateException(
               "The EditArea at index is null");
      }
      if (i > editArea.length - 1) {
         throw new IllegalArgumentException(
               "i is larger than the maximum possible index");
      }
      index = i;
   }

   /**
    * Makes the dialog for setting the font visible
    */
   public void openSetFontDialog() {
      fontWin.setVisible(true);
   }

   /**
    * Sets the boolean that specifies if wordwrap is enabled. Enabling
    * wordwrap also hides line numbers whereas disabling wordwrap shows
    * line numbers if the corresponding boolean is set to true in
    * {@link #showLineNumbers(boolean)}.
    *
    * @param wordwrap  true to enable, false to disable
    */
   public void enableWordWrap(boolean wordwrap) {
      if (wordwrap) {
         editArea[index].enableWordwrap();
      }
      else {
         editArea[index].disableWordwrap(isShowLineNr);
      }
      isWordwrap = wordwrap;
   }

   /**
    * Sets the boolean that specifies if line numbers are shown.
    * <p>
    * Line numbers are not shown in {@link EditArea} objects in which
    * wordwrap is enabled.
    *
    * @param b  true to show, false to hide line numbers
    */
   public void showLineNumbers(boolean b) {
      if (isShowLineNr == b) {
         return;
      }
      for (EditArea ea : editArea) {
         if (ea != null && !ea.isWordwrap()) {
            ea.showLineNumbers(b);
         }
      }
      isShowLineNr = b;
   }

   /**
    * Stores the current values for the properties wordwrap, font and
    * font size in <code>Prefs</code>
    */
   public void storeProperties() {
      prefs.setYesNoProperty(wordwrapKey, isWordwrap);
      prefs.setProperty(fontKey, font);
      prefs.setProperty(fontSizeKey, String.valueOf(fontSize));
   }

   //
   //--private--/
   //

   private EditArea formattedEditArea() {
      return new EditArea(isWordwrap, isShowLineNr, font, fontSize);
   }

   private void setFont() {
      font = fontWin.font();
      fontSize = fontWin.size();
      for (EditArea ea : editArea) {
         if (ea != null) {
             ea.setFont(font, fontSize);
         }
      }
      fontWin.setVisible(false);
   }

   private void setPropertyKeys(String prefix) {
      wordwrapKey = prefix + Prefs.WORDWRAP_KEY;
      fontKey = prefix + Prefs.FONT_KEY;
      fontSizeKey = prefix + Prefs.FONT_SIZE_KEY;
   }

   private void getFormatProperties() {
      isWordwrap = prefs.yesNoProperty(wordwrapKey);
      font = prefs.property(fontKey);
      try {
         fontSize = Integer.parseInt(prefs.property(fontSizeKey));
      }
      catch (NumberFormatException e) {
         fontSize = 9;
      }
   }
}
