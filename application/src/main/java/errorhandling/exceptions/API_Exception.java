package errorhandling.exceptions;

/**
 *
 * @author Nicklas Nielsen
 */
public class API_Exception extends Exception {

    int errorCode;

    public API_Exception(String message, int errCode, Throwable course) {
        super(message, course);
        this.errorCode = errCode;
    }

    public API_Exception(String message, int errCode) {
        super(message);
        errorCode = errCode;
    }

    public API_Exception(String message) {
        super(message);
        errorCode = 400;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
