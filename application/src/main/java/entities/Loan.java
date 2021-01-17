package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "LOANS")
@NamedQueries({
    @NamedQuery(name = "Loan.getAll", query = "SELECT l FROM Loan l"),
    @NamedQuery(name = "Loan.getAllActive", query = "SELECT l FROM Loan l WHERE l.returnedDate IS NULL"),
    @NamedQuery(name = "Loan.getAllInactive", query = "SELECT l FROM Loan l WHERE l.returnedDate IS NOT NULL"),
    @NamedQuery(name = "Loan.getAllByUser", query = "SELECT l FROM Loan l JOIN FETCH l.user u WHERE u = :user"),
    @NamedQuery(name = "Loan.getAllActiveByUser", query = "SELECT l FROM Loan l JOIN FETCH l.user u WHERE u = :user AND l.returnedDate IS NULL"),
    @NamedQuery(name = "Loan.getAllInactiveByUser", query = "SELECT l FROM Loan l JOIN FETCH l.user u WHERE u = :user AND l.returnedDate IS NOT NULL"),
    @NamedQuery(name = "Loan.getAllOverDue", query = "SELECT l FROM Loan l WHERE l.dueDate < :timeStamp"),
    @NamedQuery(name = "Loan.getAllOverDueByUser", query = "SELECT l FROM Loan l JOIN FETCH l.user u WHERE u = :user AND l.dueDate < :timeStamp"),
    @NamedQuery(name = "Loan.deleteAllRows", query = "DELETE FROM Loan")})
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CHECKOUT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOutDate;

    @Column(name = "DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "RETURNED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date returnedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ISBN")
    private Book book;

    public Loan(User user, Book book) {
        checkOutDate = new Date();
        dueDate = calculateDueDate(checkOutDate);
        this.user = user;
        this.book = book;

        user.addLoan(this);
        book.addLoan(this);
    }

    public Loan() {

    }

    private Date calculateDueDate(Date checkoutDate) {
        int loanPeriod = 10; // In days

        Date calculatedDate = new Date(checkoutDate.getTime());
        calculatedDate.setTime(calculatedDate.getTime() + loanPeriod * 86400000);

        return calculatedDate;
    }

    public Long getId() {
        return id;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.checkOutDate);
        hash = 89 * hash + Objects.hashCode(this.dueDate);
        hash = 89 * hash + Objects.hashCode(this.returnedDate);
        hash = 89 * hash + Objects.hashCode(this.user);
        hash = 89 * hash + Objects.hashCode(this.book);
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
        final Loan other = (Loan) obj;
        if (!Objects.equals(this.checkOutDate, other.checkOutDate)) {
            return false;
        }
        if (!Objects.equals(this.dueDate, other.dueDate)) {
            return false;
        }
        if (!Objects.equals(this.returnedDate, other.returnedDate)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.book, other.book)) {
            return false;
        }
        return true;
    }

}
