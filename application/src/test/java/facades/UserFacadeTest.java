package facades;

import DTOs.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.UserCreationException;
import errorhandling.exceptions.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import errorhandling.exceptions.AuthenticationException;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private static RoleFacade roleFacade;
    private static User user;
    private static UserDTO userDTO;
    private static List<Role> roles;

    @BeforeAll
    public static void setupClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
        roleFacade = RoleFacade.getRoleFacade(emf);
        roles = roleFacade.getDefaultRoles();
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
    }

    @BeforeEach
    public void setup() {
        EntityManager em = emf.createEntityManager();

        user = new User("user", "TestFirstName", "TestLastName", "STRONG_PASSWORD", roles);

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            userDTO = new UserDTO(user);
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        user = null;
        userDTO = null;

        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void createUser_Success() throws DatabaseException, UserCreationException {
        // Arrange
        user.setUserName("testUser");
        UserDTO expected = new UserDTO(user);

        // Act
        UserDTO actual = facade.createUser(user.getUserName(),
                user.getFirstName(), user.getLastName(), "STRONG_PASSWORD");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void createUser_Failed_UserName_Already_In_Use() {
        assertThrows(UserCreationException.class, () -> {
            facade.createUser(user.getUserName(),
                    user.getFirstName(), user.getLastName(), "STRONG_PASSWORD");
        });
    }

    @Test
    public void login_Success() throws AuthenticationException {
        // Arrange
        UserDTO excepted = userDTO;

        // Act
        UserDTO actual = facade.login(user.getUserName(), "STRONG_PASSWORD");

        // Assert
        assertEquals(excepted, actual);
    }

    @Test
    public void login_Failed_Invalid_Username() {
        user.setUserName("testUser");

        assertThrows(AuthenticationException.class, () -> {
            facade.login(user.getUserName(), "STRONG_PASSWORD");
        });
    }

    @Test
    public void login_Failed_Incorrect_Password() {
        assertThrows(AuthenticationException.class, () -> {
            facade.login(user.getUserName(), "INCORRECT_PASSWORD");
        });
    }

    @Test
    public void getUserByUserName_Success() throws UserNotFoundException {
        // Arrange
        UserDTO expected = userDTO;

        // Act
        UserDTO actual = facade.getUserByUserName(user.getUserName());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getUserByUserName_User_Not_Found() {
        user.setUserName("testUser");

        assertThrows(UserNotFoundException.class, () -> {
            facade.getUserByUserName(user.getUserName());
        });
    }

    @Test
    public void getAllUsers() {
        // Arrange
        List<UserDTO> expected = new ArrayList<>();
        expected.add(userDTO);

        // Act
        List<UserDTO> actual = facade.getAllUsers();

        // Assert
        assertTrue(actual.containsAll(expected));
    }

}
