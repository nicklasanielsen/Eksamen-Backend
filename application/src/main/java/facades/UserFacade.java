package facades;

import DTOs.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.CreationException;
import errorhandling.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import errorhandling.exceptions.AuthenticationException;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;
    private static RoleFacade roleFacade;

    private UserFacade() {
        // Private constructor to ensure Singleton
    }

    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            roleFacade = RoleFacade.getRoleFacade(emf);
            instance = new UserFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public UserDTO createUser(String username, String firstName, String lastName, String password) throws DatabaseException, CreationException {
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            throw new CreationException("user, not all user credentials was provided.");
        }

        EntityManager em = getEntityManager();

        List<Role> defaultRoles = roleFacade.getDefaultRoles();

        User user = new User(username, firstName, lastName, password, defaultRoles);

        try {
            // Checking if username is in use
            if (em.find(User.class, username) != null) {
                throw new CreationException("user, username already in use.");
            }

            em.getTransaction().begin();
            em.persist(user);

            for (Role role : user.getRoles()) {
                em.merge(role);
            }

            em.getTransaction().commit();

            return new UserDTO(user);
        } catch (Exception e) {
            if (e instanceof CreationException) {
                throw e;
            }

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException("Something went wrong! Failed to create user, please try again later.");
        } finally {
            em.close();
        }
    }

    public UserDTO login(String userName, String password) throws AuthenticationException {
        EntityManager em = getEntityManager();

        try {
            User user = getUserEntityByUserName(userName);

            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid username and/or password.");
            }

            return new UserDTO(user);
        } catch (NotFoundException e) {
            throw new AuthenticationException("Invalid username and/or password.");
        } finally {
            em.close();
        }
    }

    public UserDTO getUserByUserName(String username) throws NotFoundException {
        EntityManager em = getEntityManager();

        try {
            User user = em.find(User.class, username);

            if (user == null) {
                throw new NotFoundException(username);
            }

            return new UserDTO(user);
        } finally {
            em.close();
        }
    }

    public User getUserEntityByUserName(String username) throws NotFoundException {
        EntityManager em = getEntityManager();

        try {
            User user = em.find(User.class, username);

            if (user == null) {
                throw new NotFoundException(username);
            }

            return user;
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getAllUsers() {
        EntityManager em = getEntityManager();

        List<User> users;
        List<UserDTO> userDTOs = new ArrayList<>();

        try {
            Query query = em.createNamedQuery("User.getAll");
            users = query.getResultList();

            users.forEach(user -> {
                userDTOs.add(new UserDTO(user));
            });

            return userDTOs;
        } finally {
            em.close();
        }
    }

}
