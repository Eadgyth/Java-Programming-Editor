package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project.
 * <p>
 * 'Configuration' means to define the directory structure of project
 * and the finding of the project's root directory based on this
 * structure.
 * For example, the project root of a Java project could be the parent
 * of the path {sources Directory}/{package}/{main java file}. It
 * could as well be just the parent of the main file if subdirectories
 * are not specified.
 * <p>
 * The implementing class must receive a reference to an object of
 * {@link SettingsWin}.
 * <p>
 * The project may be configured by the entries in the settings window
 * or by reading in a file which the directories are saved in
 */
public interface Configurable {
   
   /**
    * Adds an {@code ActionListener} to the ok button of this
    * {@code SettingsWin} 
    * @param al  the ActionListener
    */
   public void addOkAction(ActionListener al);
   
   /**
    * Makes the window of this {@code SettingsWin} object 
    * visible/invisible
    * @param enable  true to make the window for project settings
    * visible, false to make it invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * If a project can be successfully configured based on entries in
    * the window of this {@code SettingsWin}
    * @param dir  the directory of a file that maybe part of the project
    */
   public boolean configureProject(String dir);
   
   /**
    * If a project configuration saved to file can be retrieved
    * @param dir  the directory of a file that maybe part of the project 
    * @return  if a project configuration saved to a file can be retrieved
    */
   public boolean retrieveProject(String dir);
   
   /**
    * Passes to other classes the project's root directory or other
    * directories that are defined for the project, as needed
    */
    public void applyProject();
   
   /**
    * If the specified directory includes the project's root directory
    * @param path  the filepath that may include the project's root
    * directory
    * @return  if the specified directory includes the project's root
    * directory
    */
   public boolean isProjectInPath(String path);
   
   /**
     * Returns the name of the project's root directory
     * @return  the name of the project's root directory
     */
   public String getProjectName();
   
   /**
    * Saves the current configuration to the preferences file
    */
   public void storeInPrefs();
}