package facades;

import DTOs.BookDTO;
import entities.Book;
import entities.Loan;
import errorhandling.exceptions.CreationException;
import errorhandling.exceptions.NotFoundException;
import errorhandling.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 *
 * @author Nicklas Nielsen
 */
public class BookFacade {

    private static EntityManagerFactory emf;
    private static BookFacade instance;

    private BookFacade() {
        // Private constructor to ensure Singleton
    }

    public static BookFacade getBookFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new BookFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public BookDTO createBook(long isbn, String title, String authors, String publisher, int yearPublished) throws CreationException, DatabaseException, NotFoundException {
        EntityManager em = getEntityManager();

        try {
            try {
                findBookByISBN(isbn);
                throw new CreationException(isbn + " already in use.");
            } catch (NotFoundException e) {

            }

            Book book = new Book(isbn, title, authors, publisher, yearPublished);

            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();

            return new BookDTO(book);
        } catch (RollbackException e) {
            if (em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (RuntimeException ex) {
                    throw new DatabaseException("Book not created.");
                }
            }

            throw new DatabaseException("Book not created.");
        } finally {
            em.close();
        }
    }

    public BookDTO editBook(long isbn, String title, String authors, String publisher, int yearPublished) throws DatabaseException, NotFoundException {
        EntityManager em = getEntityManager();

        try {
            Book book = em.find(Book.class, isbn);

            if (book == null) {
                throw new NotFoundException("book");
            }

            em.getTransaction().begin();

            if (!title.isEmpty()) {
                book.setTitle(title);
            }

            if (!authors.isEmpty()) {
                book.setAuthors(authors);
            }

            if (!publisher.isEmpty()) {
                book.setPublisher(publisher);
            }

            if (yearPublished > 0) {
                book.setYearPublished(yearPublished);
            }

            em.getTransaction().commit();

            return new BookDTO(book);
        } catch (RollbackException e) {
            if (em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (RuntimeException ex) {
                    throw new DatabaseException("Book not updated.");
                }
            }

            throw new DatabaseException("Book not updated.");
        } finally {
            em.close();
        }
    }

    public boolean deleteBook(long isbn) throws NotFoundException {
        EntityManager em = getEntityManager();

        try {
            try {
                findBookByISBN(isbn);
            } catch (NotFoundException e) {
                return false;
            }

            em.getTransaction().begin();
            Book book = em.find(Book.class, isbn);

            List<Loan> loans = book.getLoans();
            for (Loan loan : loans) {
                em.remove(loan);
            }

            em.remove(book);
            em.getTransaction().commit();

            return true;
        } catch (RollbackException e) {
            if (em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (RuntimeException ex) {
                    return false;
                }
            }

            return false;
        } finally {
            em.close();
        }
    }

    public List<BookDTO> findBook(String searchCriteria) {
        List<BookDTO> bookDTOs = new ArrayList();
        Set<Book> books = new HashSet<>();

        try {
            try {
                long isbn = Long.parseLong(searchCriteria);
                books.add(findBookByISBN(isbn));
            } catch (NumberFormatException e) {

            }

            for (Book book : findBooksByTitle(searchCriteria)) {
                System.out.println(book.getTitle());
                books.add(book);
            }

            for (Book book : findBooksByAuthors(searchCriteria)) {
                System.out.println(book.getTitle());
                books.add(book);
            }

            for (Book book : findBooksByPublisher(searchCriteria)) {
                System.out.println(book.getTitle());
                books.add(book);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        books.forEach(book -> {
            bookDTOs.add(new BookDTO(book));
        });

        return bookDTOs;
    }

    public Book findBookByISBN(long isbn) throws NotFoundException {
        EntityManager em = getEntityManager();

        try {
            Book book = em.find(Book.class, isbn);

            if (book == null) {
                throw new NotFoundException("book");
            }

            return book;
        } finally {
            em.close();
        }
    }

    public List<Book> findBooksByTitle(String title) {
        EntityManager em = getEntityManager();

        List<Book> books;

        try {
            Query query = em.createNamedQuery("Book.findByTitle");
            query.setParameter("title", title);

            books = query.getResultList();

            return books;
        } finally {
            em.close();
        }
    }

    public List<Book> findBooksByAuthors(String authors) {
        EntityManager em = getEntityManager();

        List<Book> books;

        try {
            Query query = em.createNamedQuery("Book.findByAuthors");
            query.setParameter("authors", authors);

            books = query.getResultList();

            return books;
        } finally {
            em.close();
        }
    }

    public List<Book> findBooksByPublisher(String publisher) {
        EntityManager em = getEntityManager();

        List<Book> books;

        try {
            Query query = em.createNamedQuery("Book.findByPublisher");
            query.setParameter("publisher", publisher);

            books = query.getResultList();

            return books;
        } finally {
            em.close();
        }
    }

    public List<BookDTO> getAll() {
        EntityManager em = getEntityManager();

        List<BookDTO> bookDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Book.getAll");
            List<Book> books = query.getResultList();

            for (Book book : books) {
                bookDTOs.add(new BookDTO(book));
            }

            return bookDTOs;
        } finally {
            em.close();
        }
    }

}
