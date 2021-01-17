package facades;

import entities.Role;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class RoleFacadeTest {

    private static EntityManagerFactory emf;
    private static RoleFacade facade;

    private static List<Role> roles;
    private Role defaultRole;
    private Role role;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = RoleFacade.getRoleFacade(emf);

        roles = new ArrayList();
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
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        defaultRole = new Role("testDefault");
        role = new Role("test");

        roles.add(defaultRole);
        roles.add(role);

        roles.get(0).setDefaultRole(true);

        try {
            em.getTransaction().begin();

            for (Role r : roles) {
                em.persist(r);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        roles.clear();
        defaultRole = null;
        role = null;

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetDefaultRoles_Success() {
        // Arrange
        List<Role> expected = new ArrayList();
        expected.add(defaultRole);

        // Act
        List<Role> actual = facade.getDefaultRoles();

        // Assert
        assertTrue(actual.containsAll(expected));
    }

}
