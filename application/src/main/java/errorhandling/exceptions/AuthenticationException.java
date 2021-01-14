package errorhandling.exceptions;

/**
 *
 * @author Nicklas Nielsen
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException() {
        super("Could not be Authenticated");
    }

}
