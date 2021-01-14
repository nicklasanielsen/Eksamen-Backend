package DTOs;

import entities.Role;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Nikolaj Larsen
 */
public class UserDTOTest {

    private static List<Role> roles;
    private User user;
    private UserDTO dto;

    @BeforeAll
    public static void setupClass() {
        roles = new ArrayList<>();
    }

    @BeforeEach
    public void setUp() {
        roles.add(new Role("User"));
        user = new User("User1", "Sven", "Svensen", "password123", roles);
        dto = new UserDTO(user);
    }

    @AfterEach
    public void tearDown() {
        roles.clear();
        user = null;
        dto = null;
    }

    @Test
    public void testGetFullName() {
        String expected = "Sven Svensen";

        String actual = dto.getFullName();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRoleList() {
        List<RoleDTO> expected = new ArrayList<>();
        for (Role role : roles) {
            expected.add(new RoleDTO(role));
        }

        List<RoleDTO> actual = dto.getRoleList();

        assertTrue(actual.containsAll(expected));
    }

}
