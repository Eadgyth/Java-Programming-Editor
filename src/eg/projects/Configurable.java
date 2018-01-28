package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface that defnes a configured project
 */
public interface Configurable {
   
   /**
    * Builds the content of the settings window
    */
   public void buildSettingsWindow();
   
   /**
    * Sets the <code>ActionListener</code> that is called when
    * the entries in the settings window are applied
    *
    * @param al  <code>the ActionListener</code>
    */
   public void setConfiguringAction(ActionListener al);
   
   /**
    * Makes this settings window visible
    */
   public void makeSettingsWindowVisible();
   
   /**
    * Returns if a project can be successfully configured based on the
    * entries in this settings window and the specified directory
    *
    * @param dir  the directory that may be or be contained in the
    * presumed root directory of the project
    * @return  the boolean value that is true if the project could be
    * configured
    */
   public boolean configureProject(String dir);
   
   /**
    * Returns if a project stored in a preferences file can be retrieved
    * based on the specified directory
    *
    * @param dir  the directory that may be or be contained in the
    * presumed root directory of the project
    * @return  the boolean value that is true if a saved project could
    * be retrieved
    */
   public boolean retrieveProject(String dir);
   
   /**
    * Returns if the project uses a main project file
    *
    * @return  the boolean value. True if a project file is used.
    */
   public boolean usesProjectFile();
   
   /**
    * Returns if the specified directory belongs to the project
    *
    * @param dir  the directory
    * @return  the boolean value that is true if the directory is or is
    * contained in the project root
    */
   public boolean isInProject(String dir);
   
   /**
    * Returns the path of the project's root directory
    *
    * @return  the directory
    */
   public String getProjectPath();
   
   /**
    * Returns the name of the project's root directory
    *
    * @return  the name
    */
   public String getProjectName();
   
   /**
    * Returns the name of the directory where executable files
    * are saved
    *
    * @return  the name
    */
   public String getExecutableDirName();
   
   /**
    * Returns the extension of source files used in the project
    *
    * @return  the file extension
    */
   public String getSourceFileExtension();
   
   /**
    * Stores the configuration in a properties file
    */
   public void storeConfiguration();
}
