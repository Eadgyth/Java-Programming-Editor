package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth--//
import eg.ui.ViewSettings;

public class ViewMenu {
   
   private final JMenu menu = new JMenu("View");

   private final JCheckBoxMenuItem showConsole
         = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem showFileView
         = new JCheckBoxMenuItem("Project explorer");
   private final JCheckBoxMenuItem showFunction
         = new JCheckBoxMenuItem("Function panel");
   private final JMenuItem openViewSettings
         = new JMenuItem("Other...");

   ViewMenu() {
      assembleMenu();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void registerAct(ViewSettings viewSet) {
      showConsole.addActionListener(e -> viewSet.showConsole(isConsoleSelected()));
      showFileView.addActionListener(e -> viewSet.showFileView(isFileViewSelected()));
      showFunction.addActionListener(e -> viewSet.showFunction(isFunctionPnlSelected()));
      openViewSettings.addActionListener(e -> viewSet.makeViewSetWinVisible());
   }
   
   /**
    * @return  true if this checkbox menu item for showing the console
    * is selected
    */
   public boolean isConsoleSelected() {
      return showConsole.getState();
   }

   /**
    * @return  true if this checkbox menu item for showing the file explorer
    * is selected
    */
   public boolean isFileViewSelected() {
      return showFileView.getState();
   }

   /**
    * @return  if this checkbox menu item for showing the function panel
    * is selected
    */
   public boolean isFunctionPnlSelected() {
      return showFunction.getState();
   }
   
   public void selectShowConsole(boolean select) {
      showConsole.setState(select);
   }

   public void selectShowFileView(boolean select) {
      showFileView.setState(select);
   }

   public void selectShowFunction(boolean select) {
      showFunction.setState(select);
   }
   
   /**
    * Enables to open the file explorer
    */
   public void enableFileView() {
      showFileView.setEnabled(true);
   }
   
   private void assembleMenu() {
      menu.add(showConsole);
      menu.add(showFileView);
      showFileView.setEnabled(false);
      menu.add(showFunction);
      menu.addSeparator();
      menu.add(openViewSettings);
   }
}
