package eg;

import java.util.Locale;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;
import eg.ui.Fonts;
import eg.utils.FileUtils;

/**
 * Contains the main method
 * <p>
 * @author Malte Bussiek, m.bussiek@web.de
 */
public class Eadgyth {
    
   public static void main(String[] arg) {
      Locale.setDefault(Locale.US);
      uiManagerSettings();
      Prefs prefs = new Prefs();
      prefs.load();
      String laf = prefs.getProperty("LaF");
      setLaf(laf);

      MainWin mw = new MainWin();
      ViewSettingWin viewSetWin = new ViewSettingWin();
      Formatter f = new Formatter(15, "");
      ViewSetter viewSet = new ViewSetter(mw, viewSetWin, f);
      TabbedDocuments tabDocs = new TabbedDocuments(mw, f);

      mw.setFileActions(tabDocs);
      mw.setViewSettingWinAction(viewSetWin);
      mw.setFormatActions(f);
      viewSetWin.setOkAct(e -> {
         viewSet.applySettings();
         viewSetWin.setVisible(false);
      });
      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         tabDocs.createBlankDocument();
      });
   }

   private static void uiManagerSettings() {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Fonts.SANSSERIF_PLAIN_9);
      UIManager.put("MenuItem.font", Fonts.SANSSERIF_PLAIN_9);
      UIManager.put("CheckBoxMenuItem.font", Fonts.SANSSERIF_PLAIN_9);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", eg.utils.ScreenParams.scaledSize(14));
   }

   private static void setLaf(String laf) {
      if (laf.length() > 0 && laf.equals("System")) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
         catch (ClassNotFoundException
              | IllegalAccessException
              | InstantiationException
              | UnsupportedLookAndFeelException e) {
            FileUtils.log(e);
         }
      }
   }
}
