package eg.projects;

import java.io.File;
import java.io.IOException;

/**
 * Define a project root for txt file to view files in the file explorer
 */
public class TxtActions implements ProjectActions {
   
   private ProjectConfig projConf;
   
   @Override
   public void setProjectConfig(ProjectConfig projConf) {
      this.projConf = projConf;
   }
   
   @Override
   public SettingsWin getSetWin() {
      return projConf.getSetWin();
   }
   
   @Override
   public void makeSetWinVisible(boolean enable) {
      projConf.makeSetWinVisible(enable);
   }
   
   @Override
   public void configFromSetWin(String dir) {
      projConf.configFromSetWin(dir, ".txt");
      if (projConf.getProjectPath().length() > 0) {
      }
   }
   
   @Override
   public void findPreviousProjectRoot(String dir) {
      projConf.findPreviousProjectRoot(dir);
      if (projConf.getProjectPath().length() > 0) {
      }
   }
   
   @Override
   public String getProjectRoot() {
       return projConf.getProjectPath();
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return projConf.isInProjectPath(dir);
   }
   
   /**
    * Not used
    */
   @Override
   public void compile() {  
   }
   
   /**
    * not used
    */
   @Override
   public void runProject() {
   }
   
   /**
    * Not used
    */
   @Override
   public void build() {     
   }
}