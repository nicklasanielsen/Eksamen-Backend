package rest;

import DTOs.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facades.UserFacade;
import errorhandling.exceptions.UserNotFoundException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

/**
 * @author Mathias Nielsen
 * @author Nicklas Nielsen
 */
@Path("info")
public class UserResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Path("allUsers")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<UserDTO> users = FACADE.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("user/{userName}")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userName") String user) throws UserNotFoundException {
        UserDTO userDTO = FACADE.getUserByUserName(user);
        return Response.ok(userDTO).build();
    }

}
