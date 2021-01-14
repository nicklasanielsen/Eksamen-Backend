package rest;

import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Mathias
 */
public class UserResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User user;
    private static User admin;
    private static Role role1;
    private static Role role2;
    private static List<Role> roles;
    private static List<Role> adminRole;
    private static String securityToken;
    private static List<User> users;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        roles = new ArrayList<>();
        adminRole = new ArrayList<>();
        users = new ArrayList<>();

        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
        role1 = new Role("User");
        role2 = new Role("Admin");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role1);
            em.persist(role2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
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

        roles.add(role1);
        adminRole.add(role1);
        adminRole.add(role2);
        user = new User("userName", "Sven", "Bentsen", "password123", roles);
        admin = new User("admin", "Admin", "Jensen", "1234", adminRole);
        users.add(user);
        users.add(admin);

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        roles.clear();
        adminRole.clear();
        users.clear();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    //Utility method to login and set the returned securityToken
    private static String login(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", password: \"%s\"}", username, password))
                .when().post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    public void testGetAllUsersOnUsername() {
        List<String> userNames = new ArrayList();

        users.forEach(User -> {
            userNames.add(User.getUserName());
        });

        securityToken = login("admin", "1234");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/allUsers").then()
                .statusCode(200)
                .assertThat()
                .body("userName", hasItems(userNames.toArray(new String[0])));
    }

    @Test
    public void testGetAllUsersOnUsernameAsUser() {
        securityToken = login("userName", "password123");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/allUsers").then()
                .statusCode(401)
                .body("code", is(HttpStatus.UNAUTHORIZED_401.getStatusCode())).and()
                .body("message", is("You are not authorized to perform the requested operation"));
    }

    @Test
    public void testGetUser() {
        String user = users.get(0).getUserName();
        String usersFName = users.get(0).getFirstName();
        String usersLName = users.get(0).getLastName();

        securityToken = login("userName", "password123");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user/" + user).then()
                .statusCode(200)
                .body("fullName", is(usersFName + " " + usersLName));
    }

    @Test
    public void testGetUserNotFound() {
        String user = users.get(0).getUserName();
        String usersFName = users.get(0).getFirstName();
        String usersLName = users.get(0).getLastName();

        securityToken = login("userName", "password123");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user/fail").then()
                .statusCode(400)
                .assertThat()
                .body("code", is(HttpStatus.BAD_REQUEST_400.getStatusCode())).and()
                .body("message", is("Couldn't find user with username: fail"));
    }

    @Test
    public void testGetUserAsAdmin() {
        securityToken = login("admin", "1234");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user/admin")
                .then()
                .statusCode(200)
                .assertThat()
                .body("fullName", is("Admin Jensen"));
    }
}
