package rest;

import entities.User;
import entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import security.SharedSecret;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class AuthenticationResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static User user;
    private static List<Role> roles;
    private static Role role;
    private static String password;
    private static String securityToken;
    private static SecretKey secretKey;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;

        secretKey = SharedSecret.getSharedKey();

        roles = new ArrayList<>();
        role = new Role("User");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void closeTestServer() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        roles.add(role);
        password = "STRONG_PASSWORD";
        user = new User("user", "TestFirstName", "TestLastName", password, roles);

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        user = null;
        password = null;
        securityToken = null;

        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private static String login(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", password: \"%s\"}", username, password))
                .when().post("/auth/login")
                .then()
                .extract().path("token");
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();
    }

    @Test
    public void login_Success_Verify_Subject() throws ParseException {
        // Arrange
        String expected = user.getUserName();

        // Act
        securityToken = login(user.getUserName(), password);
        Claims claims = getClaims(securityToken);
        String actual = claims.getSubject();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void login_Success_Verify_Claim_Username() throws ParseException {
        // Arrange
        String expected = user.getUserName();

        // Act
        securityToken = login(user.getUserName(), password);
        Claims claims = getClaims(securityToken);
        String actual = claims.get("username", String.class);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void login_Success_Verify_Claim_Roles() throws ParseException {
        // Arrange
        List<String> rolesArray = new ArrayList<>();

        for (Role roleStr : user.getRoles()) {
            rolesArray.add(roleStr.getRoleName());
        }

        String expected = String.join(",", rolesArray);

        // Act
        securityToken = login(user.getUserName(), password);
        Claims claims = getClaims(securityToken);
        String actual = claims.get("roles", String.class);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void login_Success_Verify_Claim_Issuer() throws ParseException {
        // Arrange
        String expected = "NewBiz";

        // Act
        securityToken = login(user.getUserName(), password);
        Claims claims = getClaims(securityToken);
        String actual = claims.getIssuer();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void login_Success_Verify_Token_Lifetime() throws ParseException {
        // Arrange
        long expected = 30;

        // Act
        securityToken = login(user.getUserName(), password);

        Claims claims = getClaims(securityToken);
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        long issueTime = TimeUnit.MINUTES.convert(issuedAt.getTime(), TimeUnit.MILLISECONDS);
        long expirationTime = TimeUnit.MINUTES.convert(expiration.getTime(), TimeUnit.MILLISECONDS);
        long actual = expirationTime - issueTime;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void login_Failed_Invalid_Username() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", password: \"%s\"}", "", password))
                .when().post("/auth/login")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.FORBIDDEN_403.getStatusCode())).and()
                .body("message", is("Invalid username and/or password."));
    }

    @Test
    public void login_Failed_Incorrect_Password() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", password: \"%s\"}", user.getUserName(), ""))
                .when().post("/auth/login")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.FORBIDDEN_403.getStatusCode())).and()
                .body("message", is("Invalid username and/or password."));
    }

    @Test
    public void register_Success() {
        // Arrange
        String expected = "TestUserNotAddedBeforeNow";

        // Act
        String actual = given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", "
                        + "firstName: \"%s\", "
                        + "lastName: \"%s\" , "
                        + "password: \"%s\"}",
                        expected, user.getFirstName(), user.getLastName(), password))
                .when().post("/auth/register")
                .then()
                .extract().path("userName");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void register_Failed_Username_In_Use() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", "
                        + "firstName: \"%s\", "
                        + "lastName: \"%s\" , "
                        + "password: \"%s\"}",
                        user.getUserName(), user.getFirstName(), user.getLastName(), password))
                .when().post("/auth/register")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.BAD_REQUEST_400.getStatusCode())).and()
                .body("message", is("Username already in use."));
    }

    @Test
    public void register_Failed_Username_Not_Provided() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", "
                        + "firstName: \"%s\", "
                        + "lastName: \"%s\" , "
                        + "password: \"%s\"}",
                        "", user.getFirstName(), user.getLastName(), password))
                .when().post("/auth/register")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.BAD_REQUEST_400.getStatusCode())).and()
                .body("message", is("Not all user credentials was provided."));
    }

    @Test
    public void register_Failed_Firstname_Not_Provided() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", "
                        + "firstName: \"%s\", "
                        + "lastName: \"%s\" , "
                        + "password: \"%s\"}",
                        user.getUserName(), "", user.getLastName(), password))
                .when().post("/auth/register")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.BAD_REQUEST_400.getStatusCode())).and()
                .body("message", is("Not all user credentials was provided."));
    }

    @Test
    public void register_Failed_Lastname_Not_Provided() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", "
                        + "firstName: \"%s\", "
                        + "lastName: \"%s\" , "
                        + "password: \"%s\"}",
                        user.getUserName(), user.getFirstName(), "", password))
                .when().post("/auth/register")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.BAD_REQUEST_400.getStatusCode())).and()
                .body("message", is("Not all user credentials was provided."));
    }

    @Test
    public void register_Failed_Password_Not_Provided() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", "
                        + "firstName: \"%s\", "
                        + "lastName: \"%s\" , "
                        + "password: \"%s\"}",
                        user.getUserName(), user.getFirstName(), user.getLastName(), ""))
                .when().post("/auth/register")
                .then()
                .assertThat()
                .body("code", is(HttpStatus.BAD_REQUEST_400.getStatusCode())).and()
                .body("message", is("Not all user credentials was provided."));
    }

}
