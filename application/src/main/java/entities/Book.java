package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "BOOKS")
@NamedQueries({
    @NamedQuery(name = "Book.getAll", query = "SELECT b FROM Book b"),
    @NamedQuery(name = "Book.deleteAllRows", query = "DELETE FROM Book"),
    @NamedQuery(name = "Book.findByTitle", query = "SELECT b FROM Book b WHERE b.title LIKE :title"),
    @NamedQuery(name = "Book.findByAuthors", query = "SELECT b FROM Book b WHERE b.authors LIKE :authors"),
    @NamedQuery(name = "Book.findByPublisher", query = "SELECT b FROM Book b WHERE b.publisher LIKE :publisher")
})
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ISBN")
    private long isbn;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Loan> loans = new ArrayList<>();

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AUTHORS")
    private String authors;

    @Column(name = "PUBLISHER")
    private String publisher;

    @Column(name = "YEAR_PUBLISHED")
    private int yearPublished;

    public Book(long isbn, String title, String authors, String publisher, int yearPublished) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
    }

    public Book() {

    }

    public long getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
        loan.setBook(this);
    }

    public void removeLoan(Loan loan) {
        loans.remove(loan);
        loan.setBook(null);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (this.isbn ^ (this.isbn >>> 32));
        hash = 23 * hash + Objects.hashCode(this.title);
        hash = 23 * hash + Objects.hashCode(this.authors);
        hash = 23 * hash + Objects.hashCode(this.publisher);
        hash = 23 * hash + this.yearPublished;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if (this.isbn != other.isbn) {
            return false;
        }
        if (this.yearPublished != other.yearPublished) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.authors, other.authors)) {
            return false;
        }
        if (!Objects.equals(this.publisher, other.publisher)) {
            return false;
        }
        return true;
    }

}
