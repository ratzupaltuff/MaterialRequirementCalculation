package mareca;

/**
 * @author ratzupaltuff 
 */
public class UnexpectedInputException extends Exception {

    /**
     * auto generated serialID
     */
    private static final long serialVersionUID = -8278793382808556581L;
   

    /**
     * error which is thrown when an error occured, caused by an invalid user input,
     * in other words, if there is an unexpected input by the user
     * 
     * @param errorMessage the error message which should be displayed if this error
     *                     occurs
     */
    public UnexpectedInputException(String errorMessage) {
        super(errorMessage);
    }
}