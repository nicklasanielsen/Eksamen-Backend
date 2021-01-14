package errorhandling.exceptions;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String username) {
        super("Couldn't find user with username: " + username);
    }

}
