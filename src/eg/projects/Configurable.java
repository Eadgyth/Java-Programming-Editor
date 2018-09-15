package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface that defines the configuration of a project.
 * <p>
 * The implementing class works in combination with class that defines a
 * a window where the settings for a project are entered.
 */
public interface Configurable {

   /**
    * Builds the settings window depending on the input options
    * required in a project
    */
   public void buildSettingsWindow();

   /**
    * Opens the settings window
    */
   public void openSettingsWindow();

   /**
    * Sets the <code>ActionListener</code> that is called when
    * the entries in the settings window are applied
    *
    * @param al  <code>the ActionListener</code>
    */
   public void setConfiguringAction(ActionListener al);

   /**
    * Tries to configure a project based on entries in the settings
    * window
    *
    * @param dir  the directory that may be or be contained in the
    * project root directory named in the settings window
    * @return  the boolean value that is true if the project could be
    * configured
    */
   public boolean configureProject(String dir);

   /**
    * Tries to retrieve a project stored in a preferences file
    *
    * @param dir  the directory that may be or be contained in the
    * project root directory stored in a preferences file
    * @return  the boolean value that is true if a saved project could
    * be retrieved
    */
   public boolean retrieveProject(String dir);

   /**
    * Returns the type of project
    *
    * @return  the type of project which is a constant in
    * {@link ProjectTypes}
    */
   public ProjectTypes getProjectType();

   /**
    * Returns if the project uses a main file
    *
    * @return  the boolean value that is true if a main file is used
    */
   public boolean usesProjectFile();

   /**
    * Returns if the specified directory belongs to the project
    *
    * @param dir  the directory
    * @return  the boolean value that is true if the directory is or is
    * contained in the project root directory
    */
   public boolean isInProject(String dir);

   /**
    * Returns the path of the project root directory
    *
    * @return  the directory
    */
   public String getProjectPath();

   /**
    * Returns the name of the project root directory
    *
    * @return  the name
    */
   public String getProjectName();

   /**
    * Returns the name of the directory where executable files
    * are saved
    *
    * @return  the name, the empty string if no such directory is given
    */
   public String getExecutableDirName();

   /**
    * Stores the configuration in a preferences file
    */
   public void storeConfiguration();
}
