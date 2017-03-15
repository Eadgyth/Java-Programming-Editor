package eg.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;

//--Eadgyth--//
import eg.Preferences;
import eg.Constants;

/**
 * The frame that contains components to control the display of the
 * main window
 */
public class ViewSettingWin {

   public final static String[] LAF_OPT = {"System", "Java default"};
   public final static String[] BACKGR_COL_OPT = {"White", "Black"};

   private final JFrame frame = new JFrame("View settings");
   private final Preferences prefs = new Preferences();
   
   private final JComboBox<String> selectLaf    = new JComboBox<>(LAF_OPT);
   private final JCheckBox checkLineNumbers     = new JCheckBox();
   private final JCheckBox checkToolbar         = new JCheckBox();
   private final JCheckBox checkStatusbar       = new JCheckBox();
   private final JButton   okBt                 = new JButton("OK");
   
   public ViewSettingWin() {
      prefs.readPrefs();
      selectLaf.setSelectedItem(prefs.getProperty("LaF"));
      initFrame();
   }
   
   public void makeVisible(boolean isVisible) {
      frame.setVisible(isVisible);
   }
   
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   public boolean isShowToolbar() {
      return checkToolbar.isSelected();
   }
   
   public boolean isShowLineNumbers() {
      return checkLineNumbers.isSelected();
   }
   
   public boolean isShowStatusbar() {
      return checkStatusbar.isSelected();
   }
   
   public int selectedLaf() {
      return selectLaf.getSelectedIndex();
   }
   
   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(allPanels());
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
   }
   
   private JPanel allPanels() {
      JPanel allPanels = new JPanel(new GridLayout(5, 1));
      allPanels.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      allPanels.add(setLineNumberPanel());
      allPanels.add(setToolbarPanel());
      allPanels.add(setStatusBarPanel());
      allPanels.add(setLafPnl());
      allPanels.add(buttonsPanel());
      frame.getRootPane().setDefaultButton(okBt);
      
      return allPanels;
   }
   
   private JPanel setToolbarPanel() {      
      if ("show".equals(prefs.getProperty("toolbar"))) {
         checkToolbar.setSelected(true);
      }
      else {
         checkToolbar.setSelected(false);
      }    
      return checkBxPnl(checkToolbar, "Show toolbar:");
   }
   
   private JPanel setLineNumberPanel() {      
      if ("show".equals(prefs.getProperty("lineNumbers"))) {
         checkLineNumbers.setSelected(true);
      }
      else {
         checkLineNumbers.setSelected(false);
      }
      return checkBxPnl(checkLineNumbers,
            "Show line numbers when wordwrap is disabled:");
   }
   
   private JPanel setStatusBarPanel() {      
      if ("show".equals(prefs.getProperty("statusbar"))) {
         checkStatusbar.setSelected(true);
      }
      else {
         checkStatusbar.setSelected(false);
      }      
      return checkBxPnl(checkStatusbar, "Show status bar:");
   }
   
   private JPanel setLafPnl() {
      return comboBxPnl(selectLaf, "Look & feel (needs restarting Eadgyth):");
   }

   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JLabel label = new JLabel(title);
      label.setFont(Constants.SANSSERIF_BOLD_12);
      JPanel holdCheckBx = new JPanel(new FlowLayout(FlowLayout.LEFT));
      
      JPanel checkBxPnl = new JPanel(); 
      checkBxPnl.setLayout(new BoxLayout(checkBxPnl, BoxLayout.LINE_AXIS));
      checkBox.setHorizontalTextPosition(JCheckBox.LEFT);     
      checkBxPnl.add(label);
      checkBxPnl.add(Box.createHorizontalGlue());
      checkBxPnl.add(checkBox);
      checkBxPnl.add(Box.createRigidArea(new Dimension(80, 0)));
      return checkBxPnl;
   }
   
   private JPanel comboBxPnl(JComboBox comboBox, String title) {      
      JLabel lb = new JLabel(title);
      lb.setFont(Constants.SANSSERIF_BOLD_12);
      //lb.setFont(Constants.SANSSERIF_PLAIN_12);

      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      pnl.add(lb);
      pnl.add(Box.createHorizontalGlue());
      JPanel holdComboBx = new JPanel(new FlowLayout());
      pnl.add(Box.createRigidArea(new Dimension(59, 0)));
      holdComboBx.add(comboBox);
      pnl.add(Box.createRigidArea(new Dimension(5, 0)));
      pnl.add(holdComboBx);
      return pnl;
   }
   
   private JPanel buttonsPanel() {
      JPanel buttonsPanel = new JPanel(new FlowLayout());   
      buttonsPanel.add(okBt);
      return buttonsPanel;
   }
}
