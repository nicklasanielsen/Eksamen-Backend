package rest;

import DTOs.LoanDTO;
import entities.Book;
import entities.User;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.BookNotFoundException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.UserNotFoundException;
import facades.BookFacade;
import facades.LoanFacade;
import facades.UserFacade;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("loan")
public class LoanResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final LoanFacade FACADE = LoanFacade.getLoanFacade(EMF);
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final BookFacade BOOK_FACADE = BookFacade.getBookFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @POST
    @Path("{isbn}")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loanBook(@PathParam("isbn") String isbn) throws API_Exception, UserNotFoundException, BookNotFoundException, DatabaseException {
        String userName = securityContext.getUserPrincipal().getName();

        long id;

        try {
            id = Long.parseLong(isbn);
        } catch (Exception e) {
            throw new API_Exception("Malformed ISBN Suplied", 400, e);
        }

        User user = USER_FACADE.getUserEntityByUserName(userName);
        Book book = BOOK_FACADE.findBookByISBN(id);

        LoanDTO loanDTO = FACADE.loanBook(user, book);

        return Response.ok(loanDTO).build();
    }

    @DELETE
    @Path("{loanId}")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(@PathParam("loanId") String loanId) throws API_Exception, DatabaseException {
        String userName = securityContext.getUserPrincipal().getName();

        long loanNumber;

        try {
            loanNumber = Long.parseLong(loanId);
        } catch (Exception e) {
            throw new API_Exception("Malformed ISBN Suplied", 400, e);
        }

        return Response.ok(FACADE.returnBook(loanNumber, userName)).build();
    }

    @GET
    @Path("all")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLoans() {
        return Response.ok(FACADE.getAllLoans()).build();
    }

    @GET
    @Path("all/active")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActiveLoans() {
        return Response.ok(FACADE.getAllActiveLoans()).build();
    }

    @GET
    @Path("all/inactive")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllInactiveLoans() {
        return Response.ok(FACADE.getAllInactiveLoans()).build();
    }

    @GET
    @Path("user")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLoansByUser() throws UserNotFoundException {
        String userName = securityContext.getUserPrincipal().getName();
        User user = USER_FACADE.getUserEntityByUserName(userName);

        return Response.ok(FACADE.getAllLoansByUser(user)).build();
    }

    @GET
    @Path("user/active")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActiveLoansByUser() throws UserNotFoundException {
        String userName = securityContext.getUserPrincipal().getName();
        User user = USER_FACADE.getUserEntityByUserName(userName);

        return Response.ok(FACADE.getAllActiveLoansByUser(user)).build();
    }

    @GET
    @Path("user/inactive")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllInactiveLoansByUser() throws UserNotFoundException {
        String userName = securityContext.getUserPrincipal().getName();
        User user = USER_FACADE.getUserEntityByUserName(userName);

        return Response.ok(FACADE.getAllInactiveLoansByUser(user)).build();

    }

    @GET
    @Path("all/overdue")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLoansOverDue() {
        return Response.ok(FACADE.getAllLoansOverDue()).build();
    }

    @GET
    @Path("user/overdue")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLoansOverDueByUser() throws UserNotFoundException {
        String userName = securityContext.getUserPrincipal().getName();
        User user = USER_FACADE.getUserEntityByUserName(userName);

        return Response.ok(FACADE.getAllLoansOverDueByUser(user)).build();
    }

}
