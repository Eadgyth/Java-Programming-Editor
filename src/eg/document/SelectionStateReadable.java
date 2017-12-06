package eg.document;

/**
 * The interface to read the state, in which text is selected or not
 */
@FunctionalInterface
public interface SelectionStateReadable {
   
   /**
    * Sets the boolean that indicates if text is selected
    *
    * @param b  the boolean value 
    */
   public void setSelectionState(boolean b);
}
