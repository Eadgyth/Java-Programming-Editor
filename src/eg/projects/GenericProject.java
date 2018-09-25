package eg.projects;
/**
 * Defines a project that is only defined by its directory
 */
public final class GenericProject extends AbstractProject implements ProjectActions {

   GenericProject() {
      super(ProjectTypes.GENERIC, false, null);
   }
   
   @Override
   public void buildSettingsWindow() {
         inputOptions.buildWindow();
   }
   
   /**
    * Not implemented
    */
   @Override
   protected void setCommandParameters() {}
}
