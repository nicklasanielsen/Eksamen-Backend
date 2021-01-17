package rest;

import DTOs.BookDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.BookCreationException;
import errorhandling.exceptions.BookNotFoundException;
import errorhandling.exceptions.DatabaseException;
import facades.BookFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("book")
public class BookResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final BookFacade FACADE = BookFacade.getBookFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @POST
    @Path("create")
    @RolesAllowed("Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(String jsonString) throws API_Exception, DatabaseException, BookCreationException {
        long isbn;
        String title, authors, publisher;
        int yearPublished;
        JsonObject jsonObject = new JsonObject();

        // Extracts information
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            isbn = json.get("isbn").getAsLong();
            title = json.get("title").getAsString();
            authors = json.get("authors").getAsString();
            publisher = json.get("publisher").getAsString();
            yearPublished = json.get("yearPublished").getAsInt();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        // Creating book
        try {
            BookDTO bookDTO = FACADE.createBook(isbn, title, authors, publisher, yearPublished);

            return Response.ok(bookDTO).build();
        } catch (Exception e) {
            if (e instanceof BookCreationException) {
                throw (BookCreationException) e;
            } else if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new API_Exception("Something went wrong, please try again later ...");
        }
    }

    @PUT
    @Path("{isbn}")
    @RolesAllowed("Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editBook(@PathParam("isbn") String isbn, String jsonString) throws API_Exception, DatabaseException, BookNotFoundException {
        long bookIsbn;
        String title, authors, publisher;
        int yearPublished;

        // Extracts information
        try {
            bookIsbn = Long.parseLong(isbn);
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            title = json.get("title").getAsString();
            authors = json.get("authors").getAsString();
            publisher = json.get("publisher").getAsString();
            yearPublished = json.get("yearPublished").getAsInt();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        BookDTO bookDTO = FACADE.editBook(bookIsbn, title, authors, publisher, yearPublished);

        return Response.ok(bookDTO).build();
    }

    @DELETE
    @Path("{isbn}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("isbn") String isbn) throws API_Exception, BookNotFoundException {
        long id;

        try {
            id = Long.parseLong(isbn);
        } catch (Exception e) {
            throw new API_Exception("Malformed ISBN Suplied", 400, e);
        }

        boolean isDeleted = FACADE.deleteBook(id);
        if (isDeleted) {
            return Response.ok().build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("{searchCriteria}")
    public Response findBook(@PathParam("searchCriteria") String searchCriteria
    ) {
        List<BookDTO> bookDTOs = FACADE.findBook(searchCriteria);

        return Response.ok(bookDTOs).build();
    }

    @GET
    @Path("all")
    public Response getAll() {
        return Response.ok(FACADE.getAll()).build();
    }

}
