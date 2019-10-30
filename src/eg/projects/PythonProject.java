package eg.projects;

//--Eadgyth--/
import eg.TaskRunner;
import eg.Projects.ProjectActionsUpdate;

/**
 * Represents a programming project in Python
 */
public final class PythonProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   private String startCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public PythonProject(TaskRunner runner) {
      super(ProjectTypes.PYTHON, true, "py");
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of Python script file")
            .addSourceDirInput()
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(true);
   }

   @Override
   public void run() {
      if (!locateMainFile()) {
         return;
      }
      runner.runSystemCommand(startCmd);
   }

   @Override
   protected void setCommandParameters() {
      StringBuilder sb = new StringBuilder("python ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      sb.append(relMainFilePath());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }
}
