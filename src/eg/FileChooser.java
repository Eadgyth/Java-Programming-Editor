package eg;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.Icon;

import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileSystemView;

import java.io.File;

/**
 * Defines objects of <code>JFileChooser</code> to open and to save files
 */
public class FileChooser {

   private final JFrame frame = new JFrame();
   private JFileChooser chOpen = null;
   private JFileChooser chSave = null;
   private File currentDir;

   /**
    * @param startingDir  the initially selected directory; not null
    */
   public FileChooser(String startingDir) {
      currentDir = new File(startingDir);
      initChooserOpen(startingDir);
      initChooserSave(startingDir);
   }

   /**
    * Opens the chooser to open a file and returns a File object
    *
    * @return  the file or null if cancel was clicked or the chooser
    * window was closed
    */
   public File fileToOpen() {
      File f = null;
      int res = chOpen.showOpenDialog(frame);
      if (res == JFileChooser.APPROVE_OPTION) {
         f = chOpen.getSelectedFile();
         currentDir = f.getParentFile();
         chSave.setCurrentDirectory(currentDir);
      }
      return f;
   }

   /**
    * Opens the chooser to save a file and returns a File object
    *
    * @param presetFile  the filename that is shown in the text field
    * to specify a file. Can be Null or the empty string
    * @return  the file or null if cancel was clicked or the chooser
    * window was closed
    */
   public File fileToSave(String presetFile) {
      File f = null;
      if (presetFile != null && presetFile.length() > 0) {
         File toSet = new File(presetFile);
         chSave.setSelectedFile(toSet);
      }
      int res = chSave.showSaveDialog(frame);
      if (res == JFileChooser.APPROVE_OPTION) {
         f = chSave.getSelectedFile();
         currentDir = f.getParentFile();
         chSave.setCurrentDirectory(currentDir);
      }
      return f;
   }

   /**
    * Returns the directory where a file was opened or saved most recently
    *
    * @return  the directory
    */
   public String currentDir() {
      return currentDir.toString();
   }

   //
   //--private--/
   //

   private void initChooserOpen(String startingDir) {
      chOpen = new JFileChooser(startingDir);
      chOpen.setDialogTitle("Open");
      chOpen.setAcceptAllFileFilterUsed(true);
      chOpen.setApproveButtonText("Open");
      chOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
      setIcons(chOpen);
   }

   private void initChooserSave(String startingDir) {
      chSave = new JFileChooser(startingDir);
      chSave.setAcceptAllFileFilterUsed(true);
      chSave.setDialogTitle("Save file as...");
      setIcons(chSave);
   }

   private void setIcons(JFileChooser ch) {
      if ("Metal".equals(UIManager.getLookAndFeel().getName())) {
         ch.setFileView(new FileView() {

            @Override
            public Icon getIcon(File f) {
               return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
         });
      }
   }
}
