package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--/
import eg.Projects;
import eg.projects.ProjectTypes;
import eg.ui.IconFiles;

/**
 * The menu for project actions
 */
public class ProjectMenu {

   private final JMenu menu = new JMenu("Project");
   private final JMenu assignProjMenu = new JMenu("Setup for...");
   private final JCheckBoxMenuItem[] assignProjItm
         = new JCheckBoxMenuItem[ProjectTypes.values().length];

   private final JMenuItem openSetWinItm = new JMenuItem("Settings...");
   private final JMenuItem changeProjItm
         = new JMenuItem("Change project", IconFiles.CHANGE_PROJ_ICON);

   private final JMenuItem compileItm
         = new JMenuItem("Save and compile", IconFiles.COMPILE_ICON);

   private final JMenuItem runItm = new JMenuItem(IconFiles.RUN_ICON);
   private final JMenuItem buildItm = new JMenuItem();

   public ProjectMenu() {
      assembleMenu();
      shortCuts();
   }

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu getMenu() {
      return menu;
   }

   /**
    * Sets listeners for actions defined in <code>Projects</code>
    *
    * @param p  the reference to {@link Projects}
    */
   public void setActions(Projects p) {
      for (JMenuItem itm : assignProjItm) {
         itm.addActionListener(e -> assignProject(e, p));
      }
      openSetWinItm.addActionListener(e -> p.openSettingsWindow());
      changeProjItm.addActionListener(e -> p.change());
      compileItm.addActionListener(e -> p.compile());
      runItm.addActionListener(e -> p.run());
      buildItm.addActionListener(e -> p.build());
   }

   /**
    * Enables or disables the item for actions to open the project
    * settings window
    *
    * @param b  true to enable, false to disable
    */
   public void enableOpenSetWinItm(boolean b) {
      openSetWinItm.setEnabled(b);
   }

   /**
    * Enables or disables the sub-menu for actions to assign a project
    *
    * @param b  true to enable, false  to disable
    */
   public void enableAssignProjMenu(boolean b) {
      assignProjMenu.setEnabled(b);
   }

   public void enableAssignProjectItms() {
      for (int i = 0; i < assignProjItm.length; i++) {
         assignProjItm[i].setEnabled(true);
         assignProjItm[i].setSelected(false);
      }
   }

    /**
    * Disables the item for actions to assign a project of
    * the specified project type
    *
    * @param  projType  the project type to disable
    */
   public void disableAssignProjectItm(ProjectTypes projType) {
      for (int i = 0; i < assignProjItm.length; i++) {
         if (projType == ProjectTypes.values()[i]) {
            assignProjItm[i].setEnabled(false);
            assignProjItm[i].setSelected(true);
         }
         else {
            assignProjItm[i].setEnabled(true);
            assignProjItm[i].setSelected(false);
         }
      }
   }

   /**
    * Enables or disables the item for actions to change project
    *
    * @param b  true to enable, false to disable
    */
   public void enableChangeProjItm(boolean b) {
      changeProjItm.setEnabled(b);
   }

   /**
    * Enables the item for actions to compile a project
    *
    * @param b  true to enable, false to disable
    */
   public void enableCompileItm(boolean b) {
      compileItm.setEnabled(b);
   }

   /**
    * Enables the item for actions to run a project
    *
    * @param b  true to enable, false to disable
    * @param label  the label for the item
    */
   public void enableRunItm(boolean b, String label) {
      runItm.setEnabled(b);
      runItm.setText(label);
   }

   /**
    * Enables the item for actions to build a project
    *
    * @param b  true to enable, false to disable
    * @param label  the label for the item
    */
   public void enableBuildItm(boolean b, String label) {
      buildItm.setEnabled(b);
      buildItm.setText(label);
   }

   //
   //--private--/
   //

   private void assignProject(ActionEvent e, Projects p) {
      for (int i = 0; i < assignProjItm.length; i++) {
         if (e.getSource() == assignProjItm[i]) {
            ProjectTypes projType = ProjectTypes.values()[i];
            p.assign(projType);
         }
      }
   }

   private void assembleMenu() {
      menu.add(assignProjMenu);
      for (int i = 0; i < assignProjItm.length; i++) {
         assignProjItm[i] = new JCheckBoxMenuItem(ProjectTypes.values()[i].display());
         assignProjMenu.add(assignProjItm[i]);
         if (i == assignProjItm.length - 2) {
            assignProjMenu.addSeparator();
         }
      }
      menu.add(openSetWinItm);
      menu.add(changeProjItm);
      menu.addSeparator();
      menu.add(compileItm);
      menu.add(runItm);
      menu.add(buildItm);
      menu.setMnemonic(KeyEvent.VK_P);
      assignProjMenu.setEnabled(false);
      openSetWinItm.setEnabled(false);
      changeProjItm.setEnabled(false);
      compileItm.setEnabled(false);
      runItm.setEnabled(false);
      buildItm.setEnabled(false);
   }

   private void shortCuts() {
      compileItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));

      runItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
   }
}
