package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth--//
import eg.Preferences;
import eg.FontSetter;
import eg.DisplaySetter;

public class FormatMenu {
   
   private final Preferences prefs = new Preferences();
   
   private final JMenu     menu     = new JMenu("Format");
   private final JMenuItem fontItm   = new JMenuItem("Font ...");
   private final JCheckBoxMenuItem wordWrapItm
                                  = new JCheckBoxMenuItem("Wordwrap");
                             
   FormatMenu() {
      assembleMenu();
   }
   
   public void registerAct(FontSetter fontSet, DisplaySetter displSet) {
      fontItm.addActionListener(e ->
            fontSet.makeFontSetWinVisible(true));
      wordWrapItm.addActionListener(e ->
            displSet.changeWordWrap(wordWrapItm.getState()));
   }
   
   public void selectWordWrapItm(boolean select) {
      wordWrapItm.setState(select);
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   private void assembleMenu() {
      prefs.readPrefs();
      menu.add(fontItm);
      menu.add(wordWrapItm);
      if ("enabled".equals(prefs.getProperty("wordWrap"))) {
         wordWrapItm.setState(true);
      }
   }
}
