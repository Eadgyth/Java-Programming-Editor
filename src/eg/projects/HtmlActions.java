package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.FileUtils;
import eg.utils.JOptions;

/**
 * Represents a project to write a webpage in HTML
 */
public final class HtmlActions extends ProjectConfig implements ProjectActions {

   private final static String F_SEP = File.separator;
   private File htmlFile;
   
   HtmlActions(String suffix) {
      super(suffix);
   }
   
   /**
    * Creates a basic {@link SettingsWin}.
    */
   @Override
   public void createSettingsWin() { 
      SettingsWin setWin = SettingsWin.basicWindow("Name of main HTML file");
      setSettingsWin(setWin);
   }
   
   /**
    * {@inheritDoc}.
    * Creates the path for the html file to show it in a fil browser
    */
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setHtmlFile();
      }
      return success;
   }

   /**
    * {@inheritDoc}.
    * Creates the path for the html file to show it in a fil browser
    */
   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setHtmlFile();
      }
      return success;
   }
   
   /**
    * Shows the html document in the default file browser
    */
   @Override
   public void runProject() {
      try {
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(htmlFile);
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
   }
   
   //--private--//
   
   private void setHtmlFile() {
      htmlFile = new File(getProjectPath() + F_SEP + getModuleName()
               + F_SEP + getMainFile() + "." + getSourceSuffix());
   }
}
