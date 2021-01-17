package DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.Book;
import entities.Loan;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Nicklas Nielsen
 */
public class BookDTO {

    private final String ISBN;
    private final String TITLE;
    private final String AUTHORS;
    private final String PUBLISHER;
    private final boolean IS_AVAILABLE;
    private final int YEAR_PUBLISHED;

    public BookDTO(Book book) {
        ISBN = String.valueOf(book.getIsbn());
        TITLE = book.getTitle();
        AUTHORS = book.getAuthors();
        PUBLISHER = book.getPublisher();
        YEAR_PUBLISHED = book.getYearPublished();
        IS_AVAILABLE = checkIfAvaiable(book.getLoans());
    }

    private boolean checkIfAvaiable(List<Loan> loans) {
        for (Loan loan : loans) {
            if (loan.getReturnedDate() == null) {
                return false;
            }
        }

        return true;
    }

    @JsonProperty("isbn")
    public String getIsbn() {
        return ISBN;
    }

    @JsonProperty("title")
    public String getTitle() {
        return TITLE;
    }

    @JsonProperty("authors")
    public String getAuthors() {
        return AUTHORS;
    }

    @JsonProperty("publisher")
    public String getPublisher() {
        return PUBLISHER;
    }

    @JsonProperty("isAvaiable")
    public boolean isAvaiable() {
        return IS_AVAILABLE;
    }

    @JsonProperty("yearPublished")
    public int getYearPublished() {
        return YEAR_PUBLISHED;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.ISBN);
        hash = 79 * hash + Objects.hashCode(this.TITLE);
        hash = 79 * hash + Objects.hashCode(this.AUTHORS);
        hash = 79 * hash + Objects.hashCode(this.PUBLISHER);
        hash = 79 * hash + this.YEAR_PUBLISHED;
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
        final BookDTO other = (BookDTO) obj;
        if (this.YEAR_PUBLISHED != other.YEAR_PUBLISHED) {
            return false;
        }
        if (!Objects.equals(this.ISBN, other.ISBN)) {
            return false;
        }
        if (!Objects.equals(this.TITLE, other.TITLE)) {
            return false;
        }
        if (!Objects.equals(this.AUTHORS, other.AUTHORS)) {
            return false;
        }
        if (!Objects.equals(this.PUBLISHER, other.PUBLISHER)) {
            return false;
        }
        return true;
    }

}
