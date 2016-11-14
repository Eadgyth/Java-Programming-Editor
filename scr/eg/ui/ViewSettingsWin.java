package eg.ui;

import java.awt.FlowLayout;
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
 * The frame that contains components to control the view
 */
class ViewSettingsWin {

   final static String[] LAF_OPT = {"System", "Java default"};

   private final JFrame frame = new JFrame("View settings");
   private final Preferences prefs = new Preferences();
   
   private final JComboBox<String> selectLaf = new JComboBox<>(LAF_OPT);
   private final JCheckBox checkLineNumbers  = new JCheckBox();
   private final JCheckBox checkToolbar      = new JCheckBox();
   private final JCheckBox checkStatusbar    = new JCheckBox();
   private final JButton okBt                = new JButton("OK");
   
   public ViewSettingsWin() {
      prefs.readPrefs();
      selectLaf.setSelectedItem(prefs.prop.getProperty("LaF"));
      initFrame();
   }
   
   public void makeViewSetWinVisible(boolean isVisible) {
      frame.setVisible(isVisible);
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
   
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }
   
   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combinedPanel());
      frame.setSize(500, 200);
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.eadgythIcon.getImage());
   }
   
   private JPanel combinedPanel() {
      JPanel combined = new JPanel(new FlowLayout());
      combined.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      combined.add(allPanels());
      combined.add(buttonsPanel());
      frame.getRootPane().setDefaultButton(okBt);
      
      return combined;
   }
   
   private JPanel allPanels() {
      JPanel allPanels = new JPanel();
      allPanels.setPreferredSize(new Dimension(480, 100));
      allPanels.add(setLineNumberPanel());
      allPanels.add(setToolbarPanel());
      allPanels.add(setStatusBarPanel());      
      allPanels.add(setLafPanel());
      
      return allPanels;
   }
   
   private JPanel setToolbarPanel() {      
      if (Constants.SHOW.equals(prefs.prop.getProperty("toolbar"))) {
         checkToolbar.setSelected(true);
      }
      else {
         checkToolbar.setSelected(false);
      }    
      return checkBxPnl(checkToolbar, "Show toolbar");
   }
   
   private JPanel setLineNumberPanel() {      
      if (Constants.SHOW.equals(prefs.prop.getProperty("lineNumbers"))) {
         checkLineNumbers.setSelected(true);
      }
      else {
         checkLineNumbers.setSelected(false);
      }
      return checkBxPnl(checkLineNumbers, "Show line numbers");
   }
   
   private JPanel setStatusBarPanel() {      
      if (Constants.SHOW.equals(prefs.prop.getProperty("statusbar"))) {
         checkStatusbar.setSelected(true);
      }
      else {
         checkStatusbar.setSelected(false);
      }      
      return checkBxPnl(checkStatusbar, "Show status bar");
   }
   
   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JLabel label = new JLabel(title);
      label.setFont(Constants.SANSSERIF_BOLD_12);
      
      JPanel checkBxPnl = new JPanel(); 
      checkBxPnl.setLayout(new BoxLayout(checkBxPnl, BoxLayout.LINE_AXIS));
      checkBxPnl.setPreferredSize(new Dimension(480, 20));
      checkBox.setHorizontalTextPosition(JCheckBox.LEFT);     
      checkBxPnl.add(label);
      checkBxPnl.add(checkBox);
      return checkBxPnl;
   }
      
   private JPanel setLafPanel() {      
      JLabel lafLabel = new JLabel( 
            "Look & feel (takes effect after restarting Eadgyth):");
      lafLabel.setFont(Constants.SANSSERIF_BOLD_12);
      selectLaf.setFont(Constants.SANSSERIF_PLAIN_12);

      JPanel setLafPanel = new JPanel();
      setLafPanel.setLayout(new BoxLayout(setLafPanel, BoxLayout.LINE_AXIS));
      setLafPanel.setPreferredSize(new Dimension(480, 20));
      setLafPanel.add(lafLabel);
      setLafPanel.add(Box.createHorizontalGlue());
      setLafPanel.add(selectLaf);
      return setLafPanel;
   }
   
   private JPanel buttonsPanel() {
      JPanel buttonsPanel = new JPanel(new FlowLayout());   
      buttonsPanel.add( okBt );
      return buttonsPanel;
   }
}