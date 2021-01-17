package DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.Loan;
import java.util.Objects;

/**
 *
 * @author Nicklas Nielsen
 */
public class LoanDTO {

    private final String ID;
    private final String CHECK_OUT_DATE;
    private final String DUE_DATE;
    private final String RETURNED_DATE;
    private final boolean IS_RETURNED;
    private final UserDTO USER_DTO;
    private final BookDTO BOOK_DTO;

    public LoanDTO(Loan loan) {
        ID = String.valueOf(loan.getId());
        CHECK_OUT_DATE = loan.getCheckOutDate().toString();
        DUE_DATE = loan.getDueDate().toString();
        USER_DTO = new UserDTO(loan.getUser());
        BOOK_DTO = new BookDTO(loan.getBook());

        boolean returned = loan.getReturnedDate() != null;
        if (returned) {
            IS_RETURNED = true;
            RETURNED_DATE = loan.getReturnedDate().toString();
        } else {
            this.IS_RETURNED = false;
            RETURNED_DATE = null;
        }
    }

    @JsonProperty("id")
    public String getId() {
        return ID;
    }

    @JsonProperty("checkoutDate")
    public String getCheckOutDate() {
        return CHECK_OUT_DATE;
    }

    @JsonProperty("dueDate")
    public String getDueDate() {
        return DUE_DATE;
    }

    @JsonProperty("returnedDate")
    public String getReturnedDate() {
        return RETURNED_DATE;
    }

    @JsonProperty("isReturned")
    public boolean isReturned() {
        return IS_RETURNED;
    }

    @JsonProperty("user")
    public UserDTO getUserDTO() {
        return USER_DTO;
    }

    @JsonProperty("book")
    public BookDTO getBookDTO() {
        return BOOK_DTO;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.CHECK_OUT_DATE);
        hash = 53 * hash + Objects.hashCode(this.DUE_DATE);
        hash = 53 * hash + Objects.hashCode(this.RETURNED_DATE);
        hash = 53 * hash + (this.IS_RETURNED ? 1 : 0);
        hash = 53 * hash + Objects.hashCode(this.USER_DTO);
        hash = 53 * hash + Objects.hashCode(this.BOOK_DTO);
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
        final LoanDTO other = (LoanDTO) obj;
        if (this.IS_RETURNED != other.IS_RETURNED) {
            return false;
        }
        if (!Objects.equals(this.CHECK_OUT_DATE, other.CHECK_OUT_DATE)) {
            return false;
        }
        if (!Objects.equals(this.DUE_DATE, other.DUE_DATE)) {
            return false;
        }
        if (!Objects.equals(this.RETURNED_DATE, other.RETURNED_DATE)) {
            return false;
        }
        if (!Objects.equals(this.USER_DTO, other.USER_DTO)) {
            return false;
        }
        if (!Objects.equals(this.BOOK_DTO, other.BOOK_DTO)) {
            return false;
        }
        return true;
    }

}
