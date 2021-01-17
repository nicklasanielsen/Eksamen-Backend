package facades;

import DTOs.LoanDTO;
import entities.Book;
import entities.Loan;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 *
 * @author Nicklas Nielsen
 */
public class LoanFacade {

    private static EntityManagerFactory emf;
    private static LoanFacade instance;

    private LoanFacade() {
        // Private constructor to ensure Singleton
    }

    public static LoanFacade getLoanFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new LoanFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public LoanDTO loanBook(User user, Book book) throws DatabaseException {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();

            Loan loan = new Loan(user, book);
            em.persist(loan);

            em.getTransaction().commit();

            return new LoanDTO(loan);
        } catch (RollbackException e) {
            if (em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (RuntimeException ex) {
                    throw new DatabaseException("Failed to loan the book.");
                }
            }

            throw new DatabaseException("Failed to loan the book.");
        } finally {
            em.close();
        }
    }

    public LoanDTO returnBook(long loanId, String userName) throws DatabaseException {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();

            Loan loan = em.find(Loan.class, loanId);

            if (loan == null || !loan.getUser().getUserName().equals(userName)) {
                throw new UnsupportedOperationException();
                // TO DO
            }

            loan.setReturnedDate(new Date());

            em.getTransaction().commit();

            return new LoanDTO(loan);
        } catch (RollbackException e) {
            if (em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (RuntimeException ex) {
                    throw new DatabaseException("Failed to return the book.");
                }
            }

            throw new DatabaseException("Failed to return the book.");
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllLoans() {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAll");
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllActiveLoans() {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllActive");
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllInactiveLoans() {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllInactive");
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllLoansByUser(User user) {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllByUser");
            query.setParameter("user", user);
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllActiveLoansByUser(User user) {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllActiveByUser");
            query.setParameter("user", user);
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllInactiveLoansByUser(User user) {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllInactiveByUser");
            query.setParameter("user", user);
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllLoansOverDue() {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllOverDue");
            query.setParameter("timeStamp", new Date());
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

    public List<LoanDTO> getAllLoansOverDueByUser(User user) {
        EntityManager em = getEntityManager();

        List<Loan> loans;
        List<LoanDTO> loanDTOs = new ArrayList();

        try {
            Query query = em.createNamedQuery("Loan.getAllOverDueByUser");
            query.setParameter("user", user);
            query.setParameter("timeStamp", new Date());
            loans = query.getResultList();

            for (Loan loan : loans) {
                loanDTOs.add(new LoanDTO(loan));
            }

            return loanDTOs;
        } finally {
            em.close();
        }
    }

}
