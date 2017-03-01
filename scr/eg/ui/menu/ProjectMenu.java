package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.CurrentProject;

import eg.ui.IconFiles;

public class ProjectMenu {
   
   private final JMenu     menu       = new JMenu("Project");
   private final JMenuItem SaveCompile
         = new JMenuItem("Save selected source file and compile");
   private final JMenuItem SaveAllCompile
         = new JMenuItem("Save all open source files and compile",
         IconFiles.COMPILE_ICON);
   private final JMenuItem run        = new JMenuItem("Run", IconFiles.RUN_ICON);
   private final JMenuItem build      = new JMenuItem("Build");
   private final JMenuItem setProject = new JMenuItem("Project settings");
   private final JMenuItem interlacedProj
         = new JMenuItem("New settings for interlaced project");
    private final JMenuItem changeProj
         = new JMenuItem("Change project", IconFiles.CHANGE_PROJ_ICON);
   
   ProjectMenu() {
      assembleMenu();
      shortCuts();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void registerAct(CurrentProject currProj) {
      setProject.addActionListener(e -> currProj.openSettingsWindow());
      changeProj.addActionListener(e -> currProj.changeProject());
      interlacedProj.addActionListener(e -> currProj.createInterlacedProject());
      run.addActionListener(e -> currProj.runProj());
      build.addActionListener(e -> currProj.buildProj());
      SaveCompile.addActionListener(e -> currProj.saveAndCompile());
      SaveAllCompile.addActionListener(e -> currProj.saveAllAndCompile());
   }
   
   public void enableChangeProjItm() {
      changeProj.setEnabled(true);
   }
   
   public void enableInterlacedProjItm() {
      interlacedProj.setEnabled(true);
   }

   public void enableProjItms(boolean isCompile, boolean isRun, boolean isBuild) {
      SaveCompile.setEnabled(isCompile);
      SaveAllCompile.setEnabled(isCompile);
      run.setEnabled(isRun);
      build.setEnabled(isBuild);
   }
   
   public void setBuildKind(String kind) {
      build.setText(kind);
   }

   private void assembleMenu() {
      menu.add(SaveCompile);
      SaveCompile.setEnabled(false);
      menu.add(SaveAllCompile);
      SaveAllCompile.setEnabled(false);
      menu.add(run);
      run.setEnabled(false);
      menu.addSeparator();
      menu.add(build);
      build.setEnabled(false);
      menu.addSeparator();
      menu.add(setProject);
      menu.add(interlacedProj);
      menu.add(changeProj);
      changeProj.setEnabled(false);
      interlacedProj.setEnabled(false);
      menu.setMnemonic(KeyEvent.VK_P);
   }
   
   private void shortCuts() {
      SaveAllCompile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));
      run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
   }
}
      
