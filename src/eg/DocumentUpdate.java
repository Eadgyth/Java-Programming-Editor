package eg;

//--Eadgyth--//
import eg.plugin.PluginStarter;
import eg.document.TextDocument;
import eg.ui.EditArea;

/**
 * Class informs other objects which document is currently
 * selected in the tabs
 */
public class DocumentUpdate {

   private final Edit edit;
   private final EditAreaView edArView;
   private final PluginStarter plugStart;
   
   private TextDocument[] txtDoc;
   private EditArea[] editArea;
 
   public DocumentUpdate(Edit edit, EditAreaView edArView,
         PluginStarter plugStart) {

      this.edit = edit;
      this.edArView = edArView; 
      this.plugStart = plugStart;
   }
   
   /**
    * Assigns the array of {@code TextDocument} and {@code EditArea} objects
    * and to this {@code DisplaySetter} the array of {@code EditArea} objects
    * @param txtDoc  the array of {@link TextDocument}
    * @param editArea  the array of {@link EditArea}
    */
   public void setDocumentArrays(TextDocument[] txtDoc, EditArea[] editArea) {
      this.txtDoc = txtDoc;
      this.editArea = editArea;
      edArView.setEditAreaArr(editArea);
   }

   /**
    * @param index  the index of the element in the arrays of
    * {@link TextDocument} and {@link EditArea}
    */
   public void updateDocument(int index) {
      edit.setTextDocument(txtDoc[index]);
      plugStart.setTextDocument(txtDoc[index]);
      edArView.setEditAreaIndex(index);
   }
}
