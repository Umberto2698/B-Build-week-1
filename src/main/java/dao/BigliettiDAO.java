package dao;

import enteties.Biglietti;
import enteties.Venditore;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
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
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataEmissione >= :date1 AND b.dataEmissione <= :date2 AND b.venditore.id = :seller.id", Long.class);
            getCount.setParameter("date1", date1);
            getCount.setParameter("date2", date2);
            getCount.setParameter("seller.id", seller.getId());
        } else if (date1.isAfter(date2)) {
            getCount = em.createQuery("SELECT COUNT(b) FROM Biglietti b WHERE b.dataEmissione >= :date2 AND b.dataEmissione <= :date1", Long.class);
            getCount.setParameter("date2", date2);
            getCount.setParameter("date1", date1);
            getCount.setParameter("seller.id", seller.getId());
        } else {
            System.err.println("Inserisci due date diverse");
        }
        return getCount != null ? getCount.getSingleResult() : -1;
    }
}
