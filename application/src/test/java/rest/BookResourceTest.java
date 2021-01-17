package rest;

import entities.Book;
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
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class BookResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static Role adminRole;
    private static Role userRole;

    private static String securityToken;
    private static List<Book> books;

    private User adminUser;
    private User userUser;

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

        EntityManager em = emf.createEntityManager();

        adminRole = new Role("Admin");
        adminRole.setDefaultRole(true);

        books = new ArrayList();

        userRole = new Role("User");

        try {
            em.getTransaction().begin();
            em.persist(adminRole);
            em.persist(userRole);
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
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Book.deleteAllRows").executeUpdate();
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

        adminUser = new User("admin", "firstName", "lastName", "password", new ArrayList<>());
        adminUser.addRole(userRole);
        adminUser.addRole(adminRole);

        userUser = new User("user", "firstName", "lastName", "password", new ArrayList<>());
        userUser.addRole(userRole);

        books.add(new Book(123L, "test1", "author", "pub", 2011));
        books.add(new Book(456L, "test2", "author", "pub", 2012));

        try {
            em.getTransaction().begin();
            em.persist(adminUser);
            em.persist(userUser);

            for (Book book : books) {
                em.persist(book);
            }

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        adminUser = null;
        userUser = null;
        securityToken = null;

        books.clear();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Book.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // This is how we hold on to the token after login, similar to that a client must store the token somewhere
    // Utility method to login and set the returned securityToken
    private static String login(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{userName: \"%s\", password: \"%s\"}", username, password))
                .when().post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    public void testCreateBook_Success() {
        Book book = new Book(1L, "tester", "tester", "tester", 2077);

        securityToken = login("admin", "password");
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{isbn: \"%s\", title: \"%s\", authors: \"%s\", publisher: \"%s\", yearPublished: %d}", String.valueOf(book.getIsbn()), book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished()))
                .header("x-access-token", securityToken)
                .when().post("/book/create").then()
                .statusCode(200)
                .assertThat()
                .body("isbn", is(String.valueOf(book.getIsbn())));
    }

    @Test
    public void testCreateBook_Fail_Not_Logged_In() {
        Book book = new Book(1L, "tester", "tester", "tester", 2077);

        given()
                .contentType(ContentType.JSON)
                .body(String.format("{isbn: \"%s\", title: \"%s\", authors: \"%s\", publisher: \"%s\", yearPublished: %d}", String.valueOf(book.getIsbn()), book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished()))
                .when().post("/book/create").then()
                .statusCode(403);
    }

    @Test
    public void testCreateBook_Fail_Not_Admin() {
        Book book = new Book(1L, "tester", "tester", "tester", 2077);

        securityToken = login("user", "password");
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{isbn: \"%s\", title: \"%s\", authors: \"%s\", publisher: \"%s\", yearPublished: %d}", String.valueOf(book.getIsbn()), book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished()))
                .header("x-access-token", securityToken)
                .when().post("/book/create").then()
                .statusCode(401);
    }

    @Test
    public void testEditBook_Success() {
        Book book = books.get(0);
        book.setTitle("nyTitle");

        securityToken = login("admin", "password");
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{title: \"%s\", authors: \"%s\", publisher: \"%s\", yearPublished: %d}", book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished()))
                .header("x-access-token", securityToken)
                .when().put("/book/" + String.valueOf(book.getIsbn())).then()
                .statusCode(200);
    }

    @Test
    public void testEditBook_Fail_Not_Logged_In() {
        Book book = books.get(0);
        book.setTitle("nyTitle");

        given()
                .contentType(ContentType.JSON)
                .body(String.format("{title: \"%s\", authors: \"%s\", publisher: \"%s\", yearPublished: %d}", book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished()))
                .when().put("/book/" + String.valueOf(book.getIsbn())).then()
                .statusCode(403);
    }

    @Test
    public void testEditBook_Fail_Not_Admin() {
        Book book = books.get(0);
        book.setTitle("nyTitle");

        securityToken = login("user", "password");
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{title: \"%s\", authors: \"%s\", publisher: \"%s\", yearPublished: %d}", book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished()))
                .header("x-access-token", securityToken)
                .when().put("/book/" + String.valueOf(book.getIsbn())).then()
                .statusCode(401);
    }

    @Test
    public void testDeleteBook_Success() {
        String isbn = String.valueOf(books.get(0).getIsbn());

        securityToken = login("admin", "password");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when().delete("/book/" + isbn).then()
                .statusCode(200);
    }

    @Test
    public void testDeleteBook_Fail_Not_Logged_In() {
        String isbn = String.valueOf(books.get(0).getIsbn());

        given()
                .contentType(ContentType.JSON)
                .when().delete("/book/" + isbn).then()
                .statusCode(403);
    }

    @Test
    public void testDeleteBook_Fail_Not_Admin() {
        String isbn = String.valueOf(books.get(0).getIsbn());

        securityToken = login("user", "password");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when().delete("/book/" + isbn).then()
                .statusCode(401);
    }

    @Test
    public void testFindBook_Success_Logged_In_As_Admin() {
        Book book = books.get(0);

        securityToken = login("admin", "password");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when().get("/book/" + String.valueOf(book.getIsbn())).then()
                .statusCode(200);
    }

    @Test
    public void testFindBook_Success_Logged_In_As_User() {
        Book book = books.get(0);

        securityToken = login("user", "password");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when().get("/book/" + String.valueOf(book.getIsbn())).then()
                .statusCode(200);
    }

    @Test
    public void testFindBook_Success_Not_Logged_In() {
        Book book = books.get(0);

        given()
                .contentType(ContentType.JSON)
                .when().get("/book/" + String.valueOf(book.getIsbn())).then()
                .statusCode(200);
    }

}
