package entities;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserTest {

    private User user;
    private static List<Role> roles;

    @BeforeAll
    public static void setUpClass() {
        roles = new ArrayList();
    }

    @BeforeEach
    public void setUp() {
        roles.add(new Role("Admin"));
        user = new User("username", "firstname", "lastname", "password", roles);
    }

    @AfterEach
    public void tearDown() {
        user = null;
        roles.clear();
    }

    @Test
    public void testVerifyPassword_Success() {
        // Arrange
        String password = "password";

        // Act
        boolean actual = user.verifyPassword(password);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testVerifyPassword_Fail() {
        // Arrange
        String password = "password123";

        // Act
        boolean actual = user.verifyPassword(password);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testAddRole_AddedToRole() {
        // Arrange
        Role role = new Role("User");
        roles.add(role);

        // Act
        user.addRole(role);
        List<Role> actual = user.getRoles();

        // Assert
        assertTrue(actual.containsAll(roles));
    }

}
