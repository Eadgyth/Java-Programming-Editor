package eg.ui;

import java.awt.FlowLayout;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//--Eadgyth--
import eg.TabbedDocuments;
import eg.Projects;
import eg.Edit;

/**
 * Defines the tool bar
 */
public class ToolBar {

   private final JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
   private final JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);

   private final JButton openBt       = new JButton(IconFiles.OPEN_ICON);
   private final JButton saveBt       = new JButton(IconFiles.SAVE_ICON);
   private final JButton undoBt       = new JButton(IconFiles.UNDO_ICON);
   private final JButton redoBt       = new JButton(IconFiles.REDO_ICON);
   private final JButton cutBt        = new JButton(IconFiles.CUT_ICON);
   private final JButton copyBt       = new JButton(IconFiles.COPY_ICON);
   private final JButton pasteBt      = new JButton(IconFiles.PASTE_ICON);
   private final JButton indentBt     = new JButton(IconFiles.INDENT_ICON);
   private final JButton outdentBt    = new JButton(IconFiles.OUTDENT_ICON);
   private final JButton changeProjBt = new JButton(IconFiles.CHANGE_PROJ_ICON);
   private final JButton compileBt    = new JButton(IconFiles.COMPILE_ICON);
   private final JButton runBt        = new JButton(IconFiles.RUN_ICON);

   public ToolBar() {
      init();
   }

   /**
    * Gets this <code>JPanel</code> the contains the tool bar
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Sets listeners for file actions
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setFileActions(TabbedDocuments td) {
      openBt.addActionListener(e -> td.open());
      saveBt.addActionListener(e -> td.save());
   }

   /**
    * Sets listeners for project actions
    *
    * @param p  the reference to {@link Projects}
    */
   public void setProjectActions(Projects p) {
      changeProjBt.addActionListener(e -> p.change());
      runBt.addActionListener(e -> p.run());
      compileBt.addActionListener(e -> p.saveAllAndCompile());
   }

   /**
    * Sets listeners for actions to edit text
    *
    * @param edit  the reference to {@link Edit}
    */
   public void setEditActions(Edit edit) {
      undoBt.addActionListener(e -> edit.undo());
      redoBt.addActionListener(e -> edit.redo());
      cutBt.addActionListener(e -> edit.cut());
      copyBt.addActionListener(e -> edit.setClipboard());
      pasteBt.addActionListener(e -> edit.pasteText());
      indentBt.addActionListener(e -> edit.indent());
      outdentBt.addActionListener(e -> edit.outdent());
   }

   /**
    * Enables the buttons for actions to compile and run a project.
    * The specified booleans each are true to enable, false to disable
    *
    * @param isCompile  the boolean for compile actions
    * @param isRun  the boolean for run actions
    */
   public void enableProjectActionsBts(boolean isCompile, boolean isRun) {
      compileBt.setEnabled(isCompile);
      runBt.setEnabled(isRun);
   }

   /**
    * Enables or disables the button for actions to change project
    *
    * @param b  true to enable, false to disable
    */
   public void enableChangeProjBt(boolean b) {
      changeProjBt.setEnabled(b);
   }

   /**
    * Enables or diables the buttons for cutting and copying actions.
    *
    * @param b  true to enable, false to disable
    */
   public void enableCutCopyBts(boolean b) {
      cutBt.setEnabled(b);
      copyBt.setEnabled(b);
   }
   
   /**
    * Enables or disables the button for saving actions
    *
    * @param b  true to enable, false to disable
    */
    public void enableSaveBt(boolean b) {
       saveBt.setEnabled(b);
    }

   /**
    * Enables or disables the buttons for und/redo actions. The
    * specified boolean each are true to enable, false to disable
    *
    * @param isUndo  the boolean for undo actions
    * @param isRedo  the boolean for redo actions
    */
   public void enableUndoRedoBts(boolean isUndo, boolean isRedo) {
      undoBt.setEnabled(isUndo);
      redoBt.setEnabled(isRedo);
   }

   //
   //--private--/
   //

   private void init() {
      toolBar.setOpaque(false);
      toolBar.setBorder(null);
      toolBar.setFloatable(false);
      enableProjectActionsBts(false, false);
      changeProjBt.setEnabled(false);
      JButton[] bts = new JButton[] {
         openBt, saveBt,
         undoBt, redoBt, cutBt, copyBt, pasteBt,
         indentBt, outdentBt,
         compileBt, runBt, changeProjBt
      };
      String[] toolTips = new String[] {
         "Open file", "Save file",
         "Undo", "Redo", "Cut selection", "Copy selection", "Paste",
         "Increase indentation by the set indent length",
         "Reduce indentation by the set indent length",
         "Save all opened source files of active project and compile",
         "Run project", "Change project"
      };
      for (int i = 0; i < bts.length; i++) {
         toolBar.add(bts[i]);
         bts[i].setBorder(new EmptyBorder(8, 8, 8, 8));
         bts[i].setToolTipText(toolTips[i]);
         bts[i].setFocusable(false);
         bts[i].setFocusPainted(false);
      }
      content.add(toolBar);
   }
}
