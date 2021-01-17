package facades;

import DTOs.BookDTO;
import entities.Book;
import errorhandling.exceptions.CreationException;
import errorhandling.exceptions.NotFoundException;
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
public class BookFacadeTest {

    private static EntityManagerFactory emf;
    private static BookFacade facade;

    private static List<Book> books;
    private static List<BookDTO> bookDTOs;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = BookFacade.getBookFacade(emf);
        books = new ArrayList();
        bookDTOs = new ArrayList();
    }

    @AfterAll
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Book.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        books.add(new Book(123L, "test1", "Manden", "WWW", 2010));
        books.add(new Book(12345L, "test2", "Dansker", "Danskerne", 2013));

        try {
            em.getTransaction().begin();

            for (Book book : books) {
                em.persist(book);
                bookDTOs.add(new BookDTO(book));
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        books.clear();
        bookDTOs.clear();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Book.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testCreateBook_Success() throws CreationException, DatabaseException, NotFoundException {
        // Arrange
        Book book = new Book(1111L, "test", "testerne", "testen", 2077);
        BookDTO expected = new BookDTO(book);

        // Act
        BookDTO actual = facade.createBook(book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateBook_Fail_BookCreationException() {
        // Arrange
        Book book = books.get(0);

        assertThrows(CreationException.class, () -> {
            facade.createBook(book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPublisher(), book.getYearPublished());
        });
    }

    @Test
    public void testDeleteBook_Success() throws NotFoundException {
        // Arrange
        long isbn = books.get(0).getIsbn();

        // Act
        boolean actual = facade.deleteBook(isbn);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testDeleteBook_Fail_BookNotFoundException() throws NotFoundException {
        // Arrange
        long isbn = 1L;

        // Act
        boolean actual = facade.deleteBook(isbn);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testFindBook_Success() {
        // Arrange
        BookDTO bookDTO = bookDTOs.get(0);
        List<BookDTO> expected = new ArrayList<>();
        expected.add(bookDTO);

        // Act
        List<BookDTO> actual = facade.findBook("123");

        // Assert
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testFindBook_Fail_Not_Found() {
        // Arrage
        String searchCriteria = "test";

        // Act
        List<BookDTO> actual = facade.findBook(searchCriteria);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFindBookByISBN_Success() throws NotFoundException {
        // Arrange
        Book expected = books.get(0);
        long isbn = expected.getIsbn();

        // Act
        Book actual = facade.findBookByISBN(isbn);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testFindBookByISBN_Fail_BookNotFoundException() {
        // Arrange
        long isbn = 1L;

        assertThrows(NotFoundException.class, () -> {
            facade.findBookByISBN(isbn);
        });
    }

}
