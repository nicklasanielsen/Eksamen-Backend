package DTOs;

/**
 *
 * @author Nicklas Nielsen
 */
public class ExceptionDTO {

    private int code;
    private String message;

    public ExceptionDTO(int code, String description) {
        this.code = code;
        message = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
