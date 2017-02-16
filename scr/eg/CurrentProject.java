package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectActions;
import eg.projects.SelectedProject;

import eg.document.TextDocument;

import eg.utils.JOptions;
import eg.utils.FileUtils;

/**
 * The managing of projects.
 * <p>
 * A project is represented by an object of type {@link ProjectActions} and
 * is configured and/or set active depending on the {@link TextDocument} that
 * is set as selected at the time.
 * <p>
 * Several configured projects are stored in a {@code List} of projects. Any
 * of these can be (re-) activated depending on the currently set
 * {@link TextDocument}
 */ 
public class CurrentProject {

   private final static String F_SEP = File.separator;

   private final String NO_FILE_IN_TAB_MESSAGE
         = "A project can be set after opening a file or"
         + " saving a new file.";

   private final String IS_IN_PROJ_MESSAGE 
         = "The selected file belongs to the"
         + " currently active project.";
         
   private final String NOT_IN_PROJ_MESSAGE 
         = "The selected file does not belong to the"
         + " currently active project.";

   private final String WRONG_TYPE_MESSAGE
         = "No project can be defined for this file type.";
         
   private final String FILES_NOT_FOUND_MESSAGE
         = "The following file could not be found anymore:";

   private final SelectedProject selProj;
   private final DisplaySetter displSet;
   private final ProcessStarter proc;
   private final FileTree fileTree;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private TextDocument[] txtDoc;
   private TextDocument currDoc;
   private String currExt;
   private Languages lang;

   public CurrentProject(DisplaySetter displSet, ProcessStarter proc,
         ConsolePanel consPnl, FileTree fileTree) {

      this.displSet = displSet;
      this.proc = proc;
      this.fileTree = fileTree;
      selProj = new SelectedProject(displSet, proc, consPnl, fileTree);
   }

   /**
    * Sets in this the array of {@code TextDocument}
    * @param txtDoc  the array of {@link TextDocument}
    */
   public void setDocumentArr(TextDocument[] txtDoc) {
      this.txtDoc = txtDoc;
   }

   /**
    * Selects the object of this array of {@code TextDocument} that is
    * used to configure and/or set active a project
    * @param docIndex  the index of the element in this array of
    * {@link TextDocument}
    */
   public void setDocumentIndex(int docIndex) {
      currDoc = txtDoc[docIndex];
      currExt = FileUtils.fileSuffix(currDoc.filename());
   }

   /**
    * Sets the current language
    * @param lang  the language that is one of the constants in
    * {@link Languages}
    */   
   public void setLanguage(Languages lang) {
      this.lang = lang;
   }

   /**
    * If at least one project has been created
    * @return  if at least one project has been created
    */
   public boolean isProjectSet() {
      return current != null;
   }

   /**
    * Tries to assign to this current project a project which a configuration
    * exists for in a local 'eadconfig' file or in the program's 'prefs' file.
    * <p>
    * Assignment of the project to this current project (setting the project
    * active) will happen only if no other project was assigned before.
    * However, if a local 'config' is found the project is always added to
    * this list of configured projects.
    */
   public void retrieveProject() {
      if (isProjectSet() && current.isInProject(currDoc.dir())) {
         return;
      }
      ProjectActions prToFind = selProj.createProject(currExt, lang);
      boolean isFound
            =  prToFind != null
            && prToFind.retrieveProject(currDoc.dir());
      if (isFound) {
         if (!isProjectSet()) {   
            current = prToFind;
            current.addOkAction(e -> configureProject(current)); 
            projList.add(current); 
            updateProjectSetting(current);
         }
         else {
            if (selectFromList(currDoc.dir()) == null) {
               prToFind.addOkAction(e -> configureProject(prToFind));
               projList.add(prToFind);
               if (projList.size() == 2) {
                  displSet.enableChangeProjItm();
               }
               changeProject(prToFind);            
            }
         }
      }
   }

   /**
    * Opens the window of the {@code SettingsWin} object that belongs to
    * a project.
    * <p>
    * Depending on the currently set {@link TextDocument} the opened window
    * belongs to the current project, to one of this listed projects that were
    * set before or to a newly created project.
    */
   public void openSettingsWindow() {
      if (!isProjectSet()) {
         if (currDoc.filename().length() == 0) {
            JOptions.titledInfoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         }
         else {
            newProject();
         }
      }
      else {
         boolean openCurrent
              =  currDoc.filename().length() == 0
              || current.isInProject(currDoc.dir());
         if (openCurrent) {
            current.makeSetWinVisible(true);
         }      
         else {
            ProjectActions fromList = selectFromList(currDoc.dir());
            if (fromList != null) {
               if (changeProject(fromList)) {
                   current.makeSetWinVisible(true);
               }
            }
            else {
               newProject();
            }
         }
      }       
   }

   /**
    * Assigns to this current project the project from this {@code List} of
    * configured projects which the currently selected {@code TextDocument}
    * belongs to.
    * <p>
    * If the currently set {@code TextDocument} does not belong to a listed
    * project it is asked to set up a new project.
    */
   public void changeProject() {
      ProjectActions fromList = selectFromList(currDoc.dir());
      if (fromList == current) {
         JOptions.infoMessage(IS_IN_PROJ_MESSAGE);
      }
      else {
         if (fromList != null) {
            changeProject(fromList);
         }
         else {
            newProject();
         }   
      }
   }      

   /**
    * Updates the file tree of {@code FileTree} if the specified
    * directory includes the project's root directory.
    * @param path  the directory that may include the project's root
    * directory
    * See {@link FileTree#updateTree()}
    */
   public void updateFileTree(String path) {
      if (isProjectSet() && current.isInProject(path)) {
         fileTree.updateTree();
      }
   }

   /**
    * Compiles this current project
    */
   public void compile() {
      if (!current.isInProject(currDoc.dir())) {
         JOptions.warnMessage(NOT_IN_PROJ_MESSAGE);
         return;
      }     
      displSet.setBusyCursor(true);
      StringBuilder missingFiles = new StringBuilder();
      try {
         for (int i = 0; i < txtDoc.length; i++) {
            boolean approved
                  = txtDoc[i] != null
                  && txtDoc[i].filename().endsWith(current.getSourceSuffix());
            if (approved) {
               boolean exists = new File(txtDoc[i].filepath()).exists();
               if (exists) {
                  txtDoc[i].saveToFile();
               }
               else {
                  missingFiles.append("\n");
                  missingFiles.append(txtDoc[i].filename());
               }
            }
         }  
         if (missingFiles.length() == 0) {
            current.compile();
         }
         else {
            JOptions.warnMessage(FILES_NOT_FOUND_MESSAGE + missingFiles);
         }
      }   
      finally {
         EventQueue.invokeLater(() -> {
            fileTree.updateTree();
            displSet.setBusyCursor(false);
         });
      }
   }

   /**
    * Runs this project
    */
   public void runProj() {
      current.runProject();
   }

   /**
    * Creates a build of this current project
    */
   public void buildProj() {
      try {
         displSet.setBusyCursor(true);
         current.build();
      }
      finally {
         displSet.setBusyCursor(false);
      }
   }

   //
   //--private methods
   //

   private void newProject() {
      ProjectActions projNew = selProj.createProject(currExt, lang);
      if (projNew == null) {
         JOptions.titledInfoMessage(WRONG_TYPE_MESSAGE, "Note");
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

   private boolean changeProject(ProjectActions toChangeTo) {
      String projName = new File(toChangeTo.getProjectPath()).getName();  
      int result = JOptions.confirmYesNo("Change to project '"
                 + projName + "'");
      if (result == 0) {
         current = toChangeTo;
         current.storeInPrefs();
         updateProjectSetting(current);
         return true;
      }
      else {
         return false;
      }
   }

   private ProjectActions selectFromList(String dir) {
      ProjectActions inList = null;
      for (ProjectActions p : projList) {
         if (p.isInProject(dir)) {
            inList = p;
         }
      }
      return inList;
   }

   private void configureProject(ProjectActions projToConf) {
      if (projToConf.configureProject(currDoc.dir())) {
         if (current != projToConf) {
            current = projToConf;
            projList.add(current);
         }
         updateProjectSetting(current);
      }
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      String projName = new File(projToSet.getProjectPath()).getName();
      fileTree.setProjectTree(projToSet.getProjectPath());
      fileTree.setDeletableDirName(projToSet.getExecutableDirName());
      proc.addWorkingDir(projToSet.getProjectPath());
      displSet.showProjectInfo(projName);
      selProj.enableActions(projToSet.getClass().getSimpleName(), projList.size());
   }
}
