package rest;

import DTOs.JokeDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import errorhandling.exceptions.FetchException;
import facades.JokeFacade;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("fun")
public class JokeResource {

    private static final JokeFacade FACADE = JokeFacade.getJokeFacade();
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Path("jokes")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJokes() throws FetchException {
        List<JokeDTO> jokeDTOs = FACADE.getJokes(THREAD_POOL);
        return Response.ok(jokeDTOs).build();
    }

}
