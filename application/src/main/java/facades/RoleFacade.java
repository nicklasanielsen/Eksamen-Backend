package facades;

import entities.Role;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author Nicklas Nielsen
 */
public class RoleFacade {

    private static EntityManagerFactory emf;
    private static RoleFacade instance;

    private RoleFacade() {
        // Private constructor to ensure Singleton
    }

    public static RoleFacade getRoleFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RoleFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    protected List<Role> getDefaultRoles() {
        EntityManager em = getEntityManager();

        List<Role> roles;

        try {
            Query query = em.createNamedQuery("Role.getDefaults");
            roles = query.getResultList();

            return roles;
        } finally {
            em.close();
        }
    }

}
