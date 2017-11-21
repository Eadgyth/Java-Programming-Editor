package eg.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;

//--Eadgyth--//
import eg.Constants;

/**
 * A frame that contains combo boxes to change the font and font size
 */
public class FontSettingWin {

   private final static String[] FONT_SIZES = {
      "8", "9", "10", "11", "12", "13", "14", "15", "16", "18", "20"
   };

   private final JFrame frame = new JFrame("Font");
   private final JComboBox<String> selectFont;
   private final JComboBox<String> selectSize = new JComboBox<>(FONT_SIZES);
   private final JButton okBt = new JButton("OK");

   private final String[] fonts; 
   private final String font;
   private final int fontSize;

   public FontSettingWin(String initFont, int initFontSize) {
      font = initFont;
      fontSize = initFontSize;
      fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
      selectFont = new JComboBox<>(fonts);
      initFrame();
   }

   /**
    * Makes this frame visible/unvisible
    *
    * @param isVisible  true/false to make this frame visible/unvisible
    */
   public void makeVisible(boolean isVisible) {
      frame.setVisible(isVisible);
   }
   
   /**
    * Adds an action handler to this ok button
    *
    * @param al  the {@code ActionListener}
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Returns the font selection in the corresponding combobox and
    * and stores the selection to the preferences file
    *
    * @return the selected font
    */
   public String fontComboBxRes() {
      String aFont = fonts[selectFont.getSelectedIndex()];
      return aFont;
   }

   /**
    * Returns the font size selection in the corresponding combobox and
    * and stores the selection to the preferences file
    *
    * @return the selected font
    */
   public int sizeComboBxRes() {
      String size = FONT_SIZES[selectSize.getSelectedIndex()];
      return Integer.parseInt(size);
   }

   //
   //--private methods
   //

   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combinedPnl());
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
   }

   private JPanel combinedPnl() {
      JPanel twoComboBx = new JPanel();
      twoComboBx.setLayout(new BoxLayout(twoComboBx, BoxLayout.LINE_AXIS));
      twoComboBx.add(fontPnl());
      twoComboBx.add(Box.createRigidArea(eg.utils.ScreenParams.scaledDimension(10, 0)));
      twoComboBx.add(sizePnl());

      JPanel combined = new JPanel();
      combined.setLayout(new BoxLayout(combined, BoxLayout.Y_AXIS));
      combined.add(twoComboBx);
      combined.add(buttonPnl());
      combined.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      return combined;
   }

   private JPanel fontPnl() {
      selectFont.setSelectedItem(font);
      return comboBoxPnl(selectFont, "Font:   ");     
   }

   private JPanel sizePnl() {
      selectSize.setSelectedItem(String.valueOf(fontSize));
      return comboBoxPnl(selectSize, "Size:   ");     
   }

   private JPanel comboBoxPnl(JComboBox<String> comboBox, String title) {
      comboBox.setFocusable(false);
      comboBox.setFont(Constants.VERDANA_PLAIN_8);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(Constants.SANSSERIF_BOLD_9);
      JPanel comboBoxPnl = new JPanel();
      comboBoxPnl.setLayout(new BoxLayout(comboBoxPnl, BoxLayout.LINE_AXIS));
      comboBoxPnl.add(titleLb);
      comboBoxPnl.add(comboBox);      
      return comboBoxPnl;
   }

    private JPanel buttonPnl() {
      JPanel buttonsPanel = new JPanel(new FlowLayout());
      buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));   
      buttonsPanel.add(okBt);
      return buttonsPanel;
   }
}
