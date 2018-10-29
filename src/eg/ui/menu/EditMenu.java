package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--/
import eg.Edit;
import eg.TabbedDocuments;
import eg.Languages;
import eg.ui.IconFiles;
import eg.edittools.EditTools;

/**
 * The menu for edit actions
 */
public class EditMenu {

   private final JMenu menu = new JMenu("Edit");
   private final JMenuItem undoItm = new JMenuItem("Undo", IconFiles.UNDO_ICON);
   private final JMenuItem redoItm = new JMenuItem("Redo", IconFiles.REDO_ICON);
   private final JMenuItem cutItm = new JMenuItem("Cut", IconFiles.CUT_ICON);
   private final JMenuItem copyItm = new JMenuItem("Copy", IconFiles.COPY_ICON);
   private final JMenuItem pasteItm = new JMenuItem("Paste", IconFiles.PASTE_ICON);
   private final JMenuItem selectAllItm = new JMenuItem("Select all");
   private final JMenuItem[] editToolsItm
         = new JMenuItem[EditTools.values().length];

   private final JMenuItem indentItm
         = new JMenuItem("Increase indentation", IconFiles.INDENT_ICON);

   private final JMenuItem outdentItm
         = new JMenuItem("Reduce indentation", IconFiles.OUTDENT_ICON);

   private final JMenuItem changeIndentItm = new JMenuItem("Set indent length");
   private final JMenuItem clearSpacesItm = new JMenuItem("Clear trailing spaces");
   private final JMenu languageMenu = new JMenu("Language");
   private final JCheckBoxMenuItem[] selectLangChBxItm
         = new JCheckBoxMenuItem[Languages.values().length];

   public EditMenu() {
      assembleMenu();
      shortCuts();
   }

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu getMenu() {
      return menu;
   }

   /**
    * Sets listeners for actions to edit text
    *
    * @param edit  the reference to {@link Edit}
    * @param clearSpaces  the listener for actions to clear trailing
    * spaces
    */
   public void setEditActions(Edit edit, ActionListener clearSpaces) {
      undoItm.addActionListener(e -> edit.undo());
      redoItm.addActionListener(e -> edit.redo());
      cutItm.addActionListener(e -> edit.cut());
      copyItm.addActionListener(e -> edit.setClipboard());
      pasteItm.addActionListener(e -> edit.pasteText());
      selectAllItm.addActionListener(e -> edit.selectAll());
      indentItm.addActionListener(e -> edit.indent());
      outdentItm.addActionListener(e -> edit.outdent());
      clearSpacesItm.addActionListener(clearSpaces);
      changeIndentItm.addActionListener(e -> edit.setIndentUnit());
   }

   /**
    * Sets the listener to an element in the array of items for actions
    * to open an <code>AddableEditTool</code>
    *
    * @param al  the <code>ActionListener</code>
    * @param i  the index of the array element
    */
   public void setEditToolsActions(ActionListener al, int i) {
      editToolsItm[i].addActionListener(al);
   }

   /**
    * Sets the listeners to the elements in the array of items for
    * actions to select the language
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setChangeLanguageActions(TabbedDocuments td) {
      for (JCheckBoxMenuItem item : selectLangChBxItm) {
         item.addActionListener(e -> setLanguage(e, td));
      }
   }

   /**
    * Enables or disables the items for und/redo actions. The
    * specified booleans each are true to enable, false to disable
    *
    * @param isUndo  the boolean for undo actions
    * @param isRedo  the boolean for redo actions
    */
   public void enableUndoRedoItms(boolean isUndo, boolean isRedo) {
      undoItm.setEnabled(isUndo);
      redoItm.setEnabled(isRedo);
   }

   /**
    * Enables or disables the items for actions to cut and copy text
    *
    * @param b  true to enable, false to disable
    */
   public void enableCutCopyItms(boolean b) {
      cutItm.setEnabled(b);
      copyItm.setEnabled(b);
   }

   /**
    * Selects and disables the item for the specified language
    *
    * @param lang  the language that has one of the constant values in
    * {@link Languages}
    * @param b   true to enable, false to disable the items for the
    * other languages
    */
   public void selectLanguageItm(Languages lang, boolean b) {
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (lang == Languages.values()[i]) {
            selectLangChBxItm[i].setEnabled(false);
            selectLangChBxItm[i].setSelected(true);
         }
         else {
            selectLangChBxItm[i].setEnabled(b);
            selectLangChBxItm[i].setSelected(false);
         }
      }
   }

   //
   //--private--/
   //

   private void setLanguage(ActionEvent e, TabbedDocuments td) {
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (e.getSource() == selectLangChBxItm[i]) {
            Languages lang = Languages.values()[i];
            td.changeLanguage(lang);
            selectLangChBxItm[i].setEnabled(false);
         }
         else {
            selectLangChBxItm[i].setSelected(false);
            selectLangChBxItm[i].setEnabled(true);
         }
      }
   }

   private void assembleMenu() {
      menu.add(undoItm);
      menu.add(redoItm);
      menu.addSeparator();
      menu.add(cutItm);
      menu.add(copyItm);
      menu.add(pasteItm );
      menu.add(selectAllItm);
      menu.addSeparator();
      for (int i = 0; i < editToolsItm.length; i++) {
         editToolsItm[i] = new JMenuItem(EditTools.values()[i].display());
         menu.add(editToolsItm[i]);
      }
      menu.addSeparator();
      menu.add(indentItm);
      menu.add(outdentItm);
      menu.add(changeIndentItm);
      menu.add(clearSpacesItm);
      menu.addSeparator();
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         selectLangChBxItm[i] = new JCheckBoxMenuItem(
               Languages.values()[i].display());

         languageMenu.add(selectLangChBxItm[i]);
      }
      menu.add(languageMenu);
      menu.setMnemonic(KeyEvent.VK_E);
   }

   private void shortCuts() {
      undoItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            ActionEvent.CTRL_MASK));
      redoItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
            ActionEvent.CTRL_MASK));
      cutItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            ActionEvent.CTRL_MASK));
      copyItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            ActionEvent.CTRL_MASK));
      pasteItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            ActionEvent.CTRL_MASK));
      selectAllItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
            ActionEvent.CTRL_MASK));
      indentItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
            ActionEvent.CTRL_MASK));
      outdentItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
            ActionEvent.CTRL_MASK));
   }
}
