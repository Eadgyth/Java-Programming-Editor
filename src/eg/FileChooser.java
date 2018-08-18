package eg;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.Icon;

import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileSystemView;

import java.io.File;

/**
 * Defines a <code>JFileChooser</code> to open and another
 * to save a file
 */
public class FileChooser {

   private final JFrame frame = new JFrame();
   private JFileChooser chOpen = null;
   private JFileChooser chSave = null;

   public FileChooser(String startingDir) {
      initChooserOpen(startingDir);
      initChooserSave(startingDir);
   }

   /**
    * Opens the chooser for opening a file and returns a File object
    *
    * @return  a File object. Null if cancel was clicked or the
    * chooser window was closed
    */
   public File fileToOpen() {
      File f = null;
      int res = chOpen.showOpenDialog(frame);
      if (res == JFileChooser.APPROVE_OPTION) {
         f = chOpen.getSelectedFile();
         chOpen.setCurrentDirectory(f.getParentFile());
      }
      return f;
   }

   /**
    * Opens the chooser for saving a file and returns a File object
    *
    * @param presetFile  the filename that is shown in the text
    * field to specify a file. Can be Null or the empty string
    * @return  a File object. Null if cancel was clicked or the
    * chooser window was closed
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
         chSave.setCurrentDirectory(f.getParentFile());
      }
      return f;
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
