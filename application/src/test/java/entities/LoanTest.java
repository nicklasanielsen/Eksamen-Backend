package entities;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Nicklas Nielsen
 */
public class LoanTest {

    private Loan loan;
    private Book book;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("username", "firstname", "lastname", "password", new ArrayList());
        book = new Book(123L, "title", "authors", "publisher", 2010);
        loan = new Loan(user, book);
    }

    @AfterEach
    public void tearDown() {
        loan = null;
        book = null;
        user = null;
    }

    @Test
    public void testAddedToBook() {
        // Arrange
        Loan expected = loan;

        // Act
        List<Loan> actual = book.getLoans();

        // Assert
        assertTrue(actual.contains(expected));
    }

    @Test
    public void testAddedToUser() {
        // Arrange
        Loan expected = loan;

        // Act
        List<Loan> actual = user.getLoans();

        // Assert
        assertTrue(actual.contains(expected));
    }

}
