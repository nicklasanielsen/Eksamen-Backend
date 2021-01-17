package errorhandling.exceptions;

/**
 *
 * @author Nicklas Nielsen
 */
public class CreationException extends Exception {

    public CreationException(String msg) {
        super("Could not create " + msg);
    }

}
