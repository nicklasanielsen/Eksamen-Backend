package errorhandling.exceptions;

/**
 *
 * @author Nicklas Nielsen
 */
public class NotFoundException extends Exception {

    public NotFoundException(String msg) {
        super("Could not find " + msg);
    }

}
