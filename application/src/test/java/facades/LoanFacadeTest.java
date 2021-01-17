package facades;

import DTOs.LoanDTO;
import entities.Book;
import entities.Loan;
import entities.Role;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class LoanFacadeTest {

    private static EntityManagerFactory emf;
    private static LoanFacade facade;

    private static List<Loan> loans;
    private static List<LoanDTO> loanDTOs;
    private static List<Book> books;
    private static List<Role> roles;
    private static List<User> users;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = LoanFacade.getLoanFacade(emf);

        loans = new ArrayList();
        loanDTOs = new ArrayList();
        books = new ArrayList();
        roles = new ArrayList();
        users = new ArrayList();
    }

    @AfterAll
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Loan.deleteAllRows").executeUpdate();
            em.createNamedQuery("Book.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        roles.add(new Role("User"));

        users.add(new User("test1", "firstName", "lastName", "password", roles));
        users.add(new User("test2", "firstName", "lastName", "password", roles));

        books.add(new Book(1L, "title", "authors", "publisher", 2013));
        books.add(new Book(2L, "title", "authors", "publisher", 2013));

        loans.add(new Loan(users.get(0), books.get(1)));

        try {
            em.getTransaction().begin();

            for (Role role : roles) {
                em.persist(role);
            }

            for (User user : users) {
                em.persist(user);
            }

            for (Book book : books) {
                em.persist(book);
            }

            for (Loan loan : loans) {
                em.persist(loan);
            }

            em.getTransaction().commit();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        loans.clear();
        loanDTOs.clear();
        books.clear();
        roles.clear();
        users.clear();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Loan.deleteAllRows").executeUpdate();
            em.createNamedQuery("Book.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testLoanBook_Success() throws DatabaseException {
        // Arrange
        User user = users.get(0);
        Book book = books.get(1);
        LoanDTO expected = new LoanDTO(new Loan(user, book));

        // Act
        LoanDTO actual = facade.loanBook(user, book);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllLoans_Success() {
        // Arrange
        List<LoanDTO> expected = loanDTOs;

        // Act
        List<LoanDTO> actual = facade.getAllLoans();

        // Assert
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testGetAllActiveLoans_Success() {
        // Arrange
        List<LoanDTO> expected = loanDTOs;

        // Act
        List<LoanDTO> actual = facade.getAllActiveLoans();

        // Assert
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testGetAllInactiveLoans_Success() {
        // Act
        List<LoanDTO> actual = facade.getAllInactiveLoans();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetAllLoansByUser_Success() {
        // Arrange
        List<LoanDTO> expected = new ArrayList();
        User user = users.get(0);

        for (LoanDTO loanDTO : loanDTOs) {
            if (loanDTO.getUserDTO().getUserName().equals(user.getUserName())) {
                expected.add(loanDTO);
            }
        }

        // Act
        List<LoanDTO> actual = facade.getAllLoansByUser(user);

        // Assert
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testGetAllActiveLoansByUser_Success() {
        // Arrange
        List<LoanDTO> expected = new ArrayList();
        User user = users.get(0);

        for (LoanDTO loanDTO : loanDTOs) {
            if (loanDTO.getUserDTO().getUserName().equals(user.getUserName())) {
                expected.add(loanDTO);
            }
        }

        // Act
        List<LoanDTO> actual = facade.getAllActiveLoansByUser(user);

        // Assert
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testGetAllInactiveLoansByUser_Success() {
        // Arrange
        User user = users.get(0);

        // Act
        List<LoanDTO> actual = facade.getAllInactiveLoansByUser(user);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetAllLoansOverDue_Success() {
        // Act
        List<LoanDTO> actual = facade.getAllLoansOverDue();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetAllLoansOverDueByUser_Success() {
        // Arrange
        User user = users.get(0);

        // Act
        List<LoanDTO> actual = facade.getAllLoansOverDueByUser(user);

        // Assert
        assertTrue(actual.isEmpty());
    }

}
