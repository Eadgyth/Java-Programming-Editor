package eg.ui.tabpane;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.border.EmptyBorder;

//--Eadgyth-/
import eg.Constants; 

/**
 * A JTabbedPane with a close button in the tabs, the possibility
 * to show and hide the tab bar and a somewhat changed tab appearance.
 * The tab placement is restricted to be at the top.
 */
public class ExtTabbedPane extends JTabbedPane {

   private final static FlowLayout FLOW_LAYOUT_LEFT
         = new FlowLayout(FlowLayout.LEFT, 0, 0);

   private final static EmptyBorder EMPTY_BORDER = new EmptyBorder(1, 5, 0, 0);

   private final ExtTabbedPaneUI ui = new ExtTabbedPaneUI();

   private int iTabMouseOver = -1;
   private boolean isShowTabbar;
   
   /**
    * Creates an <code>ExtTabbedPane</code> using the parameterless constructor
    * of the superclass (tab placement at the top) and sets this
    * {@link ExtTabbedPaneUI}
    */
   public ExtTabbedPane() {
      ui.setHeight(Constants.BAR_HEIGHT);
      super.setUI(ui);
      addMouseMotionListener(mml);
   }
   
   /**
    * Sets the specified boolean that indicates if the tabbar is shown
    * or hidden
    *
    * @param b  the boolean
    * @throws IllegalStateException  if <code>b</code> is false and more
    * than one tab is open
    */
   public void showTabbar(boolean b) {
      if (!b && getTabCount() > 1) {
         throw new IllegalStateException("Hiding tabs is illegal"
               + " when the number of tabs is larger than one");
      }
      ui.setShowTabs(b);
      updateUI();
      isShowTabbar = b;
   }

   /**
    * Adds a new tab.
    *
    * <p> It is required that the specified <code>closeBt</code> has got an
    * <code>ActionListener</code> added. This listener must first call this
    * method {@link #iTabMouseOver()}.
    *
    * @param title  the title for the tab
    * @param c  the component to be displayed when the tab is selected
    * @param closeBt  the button displayed in the tab
    */
   public void addTab(String title, Component c, JButton closeBt) {
      int index = getTabCount();
      super.addTab(null, c);
      JPanel tabPnl = new JPanel(FLOW_LAYOUT_LEFT);
      tabPnl.setOpaque(false);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(eg.Constants.VERDANA_PLAIN_8);
      closeBt.setBorder(EMPTY_BORDER);
      closeBt.setContentAreaFilled(false);
      closeBt.setFocusable(false);
      tabPnl.add(titleLb);
      tabPnl.add(closeBt);
      setTabComponentAt(index, tabPnl);
      setSelectedIndex(index);
   }
   
   @Override
   public void setTitleAt(int index, String title) {
      JPanel p = (JPanel) getTabComponentAt(index);
      JLabel lb = (JLabel) p.getComponent(0);
      lb.setText(title);
   }

   /**
    * Sets this UI
    */
   @Override
   public void updateUI() {
       super.setUI(ui);
   }
   
   /**
    * No effect. The UI object is set in this class
    */
   @Override
   public void setUI(TabbedPaneUI ui) {
   }
   
   /**
    * No effect. The placement must remain at the top
    */
   @Override
   public void setTabPlacement(int tabPlacement) {
   }
   
   /**
    * Returns the index of the tab where the mouse was moved over
    *
    * @return  the index
    */
   public int iTabMouseOver() {
      return iTabMouseOver;
   }
   
   /**
    * Returns the boolean that indicates if this tabbar is set visible
    *
    * @return  the boolean value
    */
   public boolean isShowTabbar() {
      return isShowTabbar;
   }
   
   //
   //--private--/
   //

   private final MouseMotionListener mml = new MouseMotionAdapter() {

      @Override
      public void mouseMoved(MouseEvent e) {
         JTabbedPane sourceTb = (JTabbedPane) e.getSource();
         int x = sourceTb.indexAtLocation(e.getX(), e.getY());
         if (x != -1 & x != iTabMouseOver) {
            iTabMouseOver = x;
         }
      }
   };
}
