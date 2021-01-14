package rest;

import DTOs.RoleDTO;
import DTOs.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import facades.UserFacade;
import java.util.Date;
import java.util.List;
import errorhandling.exceptions.API_Exception;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import errorhandling.exceptions.AuthenticationException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.UserCreationException;
import javax.persistence.EntityManagerFactory;
import security.SharedSecret;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("auth")
public class AuthenticationResource {

    public static final long TOKEN_EXPIRE_TIME = TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES); // 30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException, API_Exception {
        String username, password;
        JsonObject jsonObject = new JsonObject();

        // Extracts login credentials
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("userName").getAsString();
            password = json.get("password").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        // Checking login credentials and creating token if needed
        try {
            UserDTO user = USER_FACADE.login(username, password);
            String token = createToken(user);

            // Preparing respone
            jsonObject.addProperty("userName", user.getUserName());
            jsonObject.addProperty("token", token);

            return Response.ok(GSON.toJson(jsonObject)).build();
        } catch (JOSEException | AuthenticationException e) {
            if (e instanceof AuthenticationException) {
                throw (AuthenticationException) e;
            }

            throw new API_Exception("Something went wrong, please try again later ...");
        }
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String jsonString) throws AuthenticationException, API_Exception, DatabaseException, UserCreationException {
        String username, firstname, lastname, password;
        JsonObject jsonObject = new JsonObject();

        // Extracts user credentials
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("userName").getAsString();
            firstname = json.get("firstName").getAsString();
            lastname = json.get("lastName").getAsString();
            password = json.get("password").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        // Creating user and signing in if possible
        try {
            UserDTO user = USER_FACADE.createUser(username, firstname, lastname, password);
            String token = createToken(user);

            // Preparing respone
            jsonObject.addProperty("userName", user.getUserName());
            jsonObject.addProperty("token", token);

            return Response.ok(GSON.toJson(jsonObject)).build();
        } catch (JOSEException | DatabaseException | UserCreationException e) {
            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            } else if (e instanceof UserCreationException) {
                throw (UserCreationException) e;
            }

            throw new API_Exception("Something went wrong, please try again later ...");
        }
    }

    private String createToken(UserDTO user) throws JOSEException {
        List<String> roles = new ArrayList<>();

        for (RoleDTO role : user.getRoleList()) {
            roles.add(role.getRoleName());
        }

        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())
                .claim("username", user.getUserName())
                .claim("roles", String.join(",", roles))
                .issuer("NewBiz")
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(new MACSigner(SharedSecret.getSharedKey()));

        return signedJWT.serialize();
    }

}
