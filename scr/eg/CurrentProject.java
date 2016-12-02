package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.Toolbar;

import eg.ui.filetree.FileTree;
import eg.ui.menu.Menu;

import eg.projects.ProjectActions;
import eg.projects.ProjectFactory;

import eg.document.TextDocument;

import eg.utils.FileUtils;
import eg.utils.JOptions;

/**
 * The current project that is configured for the file of a
 * {@code TextDocument}.
 * <p>
 * A project is represented by an object of type {@link ProjectActions}.
 * <p>
 * A successfully configured project is assigned to this current
 * project and also added to this list of projects. By this any of
 * the listed projects are re-assigned to the current project if the
 * currently set {@link TextDocument} belongs to it.
 * <p>
 * See also {@link eg.projects.ProjectConfig}
 */
public class CurrentProject {

   private final ProjectFactory projFact;
   private final MainWin mw;
   private final FileTree fileTree;
   private final Menu menu;
   private final Toolbar tBar;
   private final Preferences prefs = new Preferences();
   
   private final List<ProjectActions> recent = new ArrayList<>();

   private ProjectActions proj;
   private TextDocument txtDoc;

   public CurrentProject(ProjectFactory projFact, MainWin mw,
         FileTree fileTree, Menu menu, Toolbar tBar) {
      this.projFact = projFact;
      this.mw = mw;
      this.fileTree = fileTree;
      this.menu = menu;
      this.tBar = tBar;
   }

   /**
    * Sets in this the TextDocument which a project is configured for
    * @param txtDoc  an object of {@link TextDocument}
    */
   public void setTextDocument(TextDocument txtDoc) {
      this.txtDoc = txtDoc;
   }

   /**
    * If at least one project has been created
    */
   public boolean isProjectSet() {
      return proj != null;
   }

   /**
    * Tries to assign to this the project that was active when the program was
    * closed the last time.
    * <p>
    * Retrieving a previous project requires that (i) no other project has
    * been assigned and (ii) the current {@link TextDocument} is part of the
    * the previous project (which is saved in 'prefs').
    */
   public void retrieveProject() {
      if (!isProjectSet()) {
         ProjectActions prPrevious
               = projFact.getProjAct(FileUtils.extension(txtDoc.filepath()));
         if (prPrevious != null) {
            if (prPrevious.findPreviousProjectRoot(txtDoc.dir())) {        
               prPrevious.addOkAction(e -> configureProject(prPrevious));
               recent.add(prPrevious);
               proj = prPrevious;
               updateProjectSetting(proj);
            }
         }
      }
   }

   /**
    * Opens the window of the {@code SettingsWin} object of a 
    * previously or a newly created project
    */
   public void openSettingsWindow() {
      if (!isProjectSet()) {
         if (txtDoc.filename().length() == 0) {
            JOptions.titledInfoMessage("A project can be set after a file was"
                  + " opened or a new file was saved", "Note");
         }
         else {
            newProject();
         }
      }
      else {  
         if (txtDoc.filename().length() == 0
               || proj.isInProjectPath(txtDoc.dir())) {
            proj.makeSetWinVisible(true);
         }      
         else if (txtDoc.filename().length() > 0
               & !proj.isInProjectPath(txtDoc.dir())) {
            ProjectActions recent = searchRecent(txtDoc.dir());
            if (recent != null) {
               if (changeProject(recent)) {
                  proj.makeSetWinVisible(true);
               }
            }
            else {
               newProject();
            }
         }
      }       
   }
   
   /**
    * Changes this current project if the the current
    * {@code TextDocument} does not belong to the current but to
    * a project saved in the list of already created projects
    */
   public void changeProject() {
      ProjectActions recent = searchRecent(txtDoc.dir());
      if (recent == proj) {
         JOptions.infoMessage("The selected file belongs to the"
               + " current project '" + proj.getProjectName() + "'.");
      }
      else {
         if (recent != null) {
            changeProject(recent);
         }
         else {
            openSettingsWindow();
         }   
      }
   } 

   /**
    * Adds a file to the file tree if the file belongs
    * to this current project
    */
   public void addFileToTree(String dir, String file) {
      if (isProjectSet() & proj.isInProjectPath(dir)) {
         fileTree.addFile(file);
      }
   }

   /**
    * compiles this project
    */
   public void compile() {
      mw.setCursor(MainWin.BUSY_CURSOR);         
      proj.compile();
      EventQueue.invokeLater(() -> {
         mw.setCursor(MainWin.DEF_CURSOR);
      });
   }

   /**
    * Runs this project
    */
   public void runProj() {
      proj.runProject();
   }

   /**
    * creates a build of this project
    */
   public void buildProj() {      
      proj.build();
   }
   
   /**
    * Stores the settings of this project to prefs
    */
   public void storeConfig() {
      if (isProjectSet()) {
         proj.storeConfig();
      }
   }

   //
   //--private methods
   //
   
   private void newProject() {
      ProjectActions projNew 
            = projFact.getProjAct(FileUtils.extension(txtDoc.filepath()));
      if (projNew == null) {
         JOptions.titledInfoMessage(
               "A project cannot be set for this file type", "Note");
      }
      else {         
         int result = 0;
         if (isProjectSet()) {
            result = JOptions.confirmYesNo("Set new project ?");
         }
         if (result == 0) {
            projNew.makeSetWinVisible(true);
            projNew.addOkAction(e -> configureProject(projNew));
         }
      }
   }

   private void configureProject(ProjectActions projToConf) {
      if (projToConf.configFromSetWin(txtDoc.dir(),
            FileUtils.extension(txtDoc.filename()))) {
         if (proj != projToConf) {
            proj = projToConf;
            recent.add(proj);
         }
         updateProjectSetting(proj);
      }
   }
   
   private boolean changeProject(ProjectActions recent) {
      int result = JOptions.confirmYesNo("Change to project '"
            + recent.getProjectName() + "' ?");
      if (result == 0) {
         proj = recent;
         updateProjectSetting(proj);
         return true;
      }
      else {
         return false;
      }
   }

   private ProjectActions searchRecent(String dir) {
      ProjectActions old = null;
      for (ProjectActions p : recent) {
         if (p.isInProjectPath(dir)) {
            old = p;
         }
      }
      return old;
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      mw.showProjectInfo(projToSet.getProjectName());
      String root = projToSet.getProjectRoot();
      fileTree.setProjectTree(root);
      if (recent.size() == 1) {
         menu.getViewMenu().enableFileView();
      }
      if (recent.size() == 2) {
         menu.getProjectMenu().enableChangeProjItm();
      }
      enableActions();
   }
   
   private void enableActions() {
      String ext = FileUtils.extension(txtDoc.filepath());
      switch (ext) {
          case ".java":
            enableActions(true, true, true);
            break;
         case ".html":
            enableActions(false, true, false);
            break;
         case ".txt":
            enableActions(false, false, false);
            break;
      }
   }
   
   private void enableActions(boolean isCompile, boolean isRun, boolean isBuild) {
      menu.getProjectMenu().enableProjItms(isCompile, isRun, isBuild);
      tBar.enableProjBts(isCompile, isRun);
   }
}