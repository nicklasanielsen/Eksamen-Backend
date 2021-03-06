package errorhandling.mappers;

import errorhandling.exceptions.API_Exception;
import DTOs.ExceptionDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
public class API_ExceptionMapper implements ExceptionMapper<API_Exception> {

    @Context
    ServletContext context;

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(API_Exception ex) {
        ExceptionDTO err = new ExceptionDTO(ex.getErrorCode(), ex.getMessage());

        return Response
                .status(ex.getErrorCode())
                .entity(gson.toJson(err))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
