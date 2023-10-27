package dao;

import enteties.Biglietti;
import enteties.Mezzi;
import enteties.Venditore;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BigliettiDAO {
    private final EntityManager em;

    public BigliettiDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Biglietti biglietto) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(biglietto);
            transaction.commit();
            System.err.println("Biglietto emesso correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(biglietto);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante l'emissione del biglietto." + e);
            throw e;
        }
    }

    public Biglietti getById(long id) {
        return em.find(Biglietti.class, id);
    }

    public List<Biglietti> getAllTickets() {
        TypedQuery<Biglietti> getAllTickets = em.createQuery("SELECT b FROM Biglietti b", Biglietti.class);
        return getAllTickets.getResultList();
    }

    public long getAllSelledTickets() {
        TypedQuery<Long> getAllTickets = em.createQuery("SELECT COUNT(b) FROM Biglietti b", Long.class);
        return getAllTickets.getSingleResult();
    }

    public long getAllValidatedTickets() {
        TypedQuery<Long> getAllTickets = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataValidazione IS NOT NULL", Long.class);
        return getAllTickets.getSingleResult();
    }

    public long getAllSelledTicketsForSeller(long seller_id) {
        TypedQuery<Long> getAllTickets = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.venditore = :seller_id", Long.class);
        getAllTickets.setParameter("seller_id", seller_id);
        return getAllTickets.getSingleResult();
    }

    public long getNumberOfTicketsInTimeIntervall(LocalDate date1, LocalDate date2) {
        TypedQuery<Long> getCount = null;
        if (date1.isBefore(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataEmissione >= :date1 AND b.dataEmissione <= :date2", Long.class);
            getCount.setParameter("date1", date1);
            getCount.setParameter("date2", date2);
        } else if (date1.isAfter(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataEmissione >= :date2 AND b.dataEmissione <= :date1", Long.class);
            getCount.setParameter("date2", date2);
            getCount.setParameter("date1", date1);
        } else {
            System.err.println("Inserisci due date diverse");
        }
        return getCount != null ? getCount.getSingleResult() : -1;
    }

    public long getNumberOfTicketsInTimeIntervallForSeller(LocalDate date1, LocalDate date2, Venditore seller) {
        TypedQuery<Long> getCount = null;
        if (date1.isBefore(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataEmissione >= :date1 AND b.dataEmissione <= :date2 AND b.venditore.id = :seller_id", Long.class);
            getCount.setParameter("date1", date1);
            getCount.setParameter("date2", date2);
            getCount.setParameter("seller_id", seller.getId());
        } else if (date1.isAfter(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataEmissione >= :date2 AND b.dataEmissione <= :date1 AND b.venditore.id = :seller_id", Long.class);
            getCount.setParameter("date2", date2);
            getCount.setParameter("date1", date1);
            getCount.setParameter("seller_id", seller.getId());
        } else {
            System.err.println("Inserisci due date diverse");
        }
        return getCount != null ? getCount.getSingleResult() : -1;
    }


    public long bigliettiValidatiSuUnMezzo(long id) {
        TypedQuery<Long> bigliettiValidatiSuUnMezzo = em.createQuery("SELECT COUNT(b) FROM Biglietti b JOIN b.mezzo m WHERE m.id = :id", Long.class);
        bigliettiValidatiSuUnMezzo.setParameter("id", id);
        return bigliettiValidatiSuUnMezzo.getSingleResult();
    }

    public long bigliettiValidatiSuUnMezzoPerIntervallo(LocalDate date1, LocalDate date2) {
        TypedQuery<Long> getCount = null;
        if (date1.isBefore(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataValidazione >= :date1 AND b.dataValidazione <= :date2", Long.class);
            getCount.setParameter("date1", date1);
            getCount.setParameter("date2", date2);
        } else if (date1.isAfter(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataValidazione >= :date2 AND b.dataValidazione <= :date1", Long.class);
            getCount.setParameter("date2", date2);
            getCount.setParameter("date1", date1);
        } else {
            System.err.println("Inserisci due date diverse");
        }
        return getCount != null ? getCount.getSingleResult() : -1;
    }

    public long bigliettiValidatiSuUnMezzoPerIntervallo(LocalDate date1, LocalDate date2, long mezzo_id) {
        TypedQuery<Long> getCount = null;
        if (date1.isBefore(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataValidazione >= :date1 AND b.dataValidazione <= :date2 AND b.mezzo.id = :mezzo_id", Long.class);
            getCount.setParameter("date1", date1);
            getCount.setParameter("date2", date2);
            getCount.setParameter("mezzo_id", mezzo_id);
        } else if (date1.isAfter(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataValidazione >= :date2 AND b.dataValidazione <= :date1 AND b.mezzo.id = :mezzo_id", Long.class);
            getCount.setParameter("date2", date2);
            getCount.setParameter("date1", date1);
            getCount.setParameter("mezzo_id", mezzo_id);
        } else {
            System.err.println("Inserisci due date diverse");
        }
        return getCount != null ? getCount.getSingleResult() : -1;
    }

    public List<Biglietti> findNonValidatedTicketForUser(long userId) {
        TypedQuery<Biglietti> query = em.createQuery(
                "SELECT b FROM Biglietti b WHERE b.user.id = :userId AND b.dataValidazione IS NULL",
                Biglietti.class
        );
        query.setParameter("userId", userId);
        try {
            return query.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void validateTicket(MezziDAO md, Biglietti b) {
        List<Mezzi> mezziInServizio = md.getAllOnService().stream().toList();
        int size = mezziInServizio.size();
        int n = new Random().nextInt(1, size);
        b.setMezzo(mezziInServizio.get(n));
        b.setDataValidazione(LocalDate.now());
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            getById(b.getId());
            Query validaQuery = em.createQuery("UPDATE Biglietti b SET b.mezzo = :newMezzo WHERE b.id = :id");
            validaQuery.setParameter("newMezzo", b.getMezzo());
            validaQuery.setParameter("id", b.getId());
            int numeroModificati = validaQuery.executeUpdate();
            transaction.commit();
            if (numeroModificati > 0) {
                System.out.println("Biglietto validato");
            } else {
                System.out.println("Non ho trovato nessun biglietto: " + b);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante la validazione del biglietto." + e);
            throw e;
        }
    }


    public void validateTicketWithTransport(Mezzi mezzo, Biglietti b, LocalDate date) {
        b.setMezzo(mezzo);
        b.setDataValidazione(date);
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            getById(b.getId());
            Query validaQuery = em.createQuery("UPDATE Biglietti b SET b.mezzo = :newMezzo WHERE b.id = :id");
            validaQuery.setParameter("newMezzo", b.getMezzo());
            validaQuery.setParameter("id", b.getId());
            int numeroModificati = validaQuery.executeUpdate();
            transaction.commit();
            if (numeroModificati > 0) {
                System.out.println("Biglietto validato");
            } else {
                System.out.println("Non ho trovato nessun biglietto: " + b);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante la validazione del biglietto." + e);
            throw e;
        }
    }
}
