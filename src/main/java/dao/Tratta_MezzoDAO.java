package dao;

import enteties.Tratta_Mezzo;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.concurrent.TimeUnit;

public class Tratta_MezzoDAO {
    private final EntityManager em;

    public Tratta_MezzoDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Tratta_Mezzo trattaMezzo) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(trattaMezzo);
            transaction.commit();
            System.err.println("Tratta assegnata correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(trattaMezzo);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante il salvataggio della corsa." + e);
            throw e;
        }
    }

    public long getAll() {
        TypedQuery<Long> getElements = em.createQuery("SELECT COUNT(tm) FROM Tratta_Mezzo tm ", Long.class);
        return getElements.getSingleResult();
    }

    public Tratta_Mezzo getById(long id) {
        return em.find(Tratta_Mezzo.class, id);
    }


    public Long getNumVolteMezzoPercorsoTratta(long mezzoId, long trattaID) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(tm) FROM Tratta_Mezzo tm WHERE tm.mezzo.id = :mezzoId AND tm.tratta.id = :trattaId", Long.class);
        query.setParameter("mezzoId", mezzoId);
        query.setParameter("trattaId", trattaID);
        return query.getSingleResult();
    }

    public Long getNumVolteMezzo(long mezzoId) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(tm) FROM Tratta_Mezzo tm WHERE tm.mezzo.id = :mezzoId", Long.class);
        query.setParameter("mezzoId", mezzoId);
        return query.getSingleResult();
    }

    public Long getNumVolteTratta(long trattaId) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(tm) FROM Tratta_Mezzo tm WHERE tm.tratta.id = :trattaId", Long.class);
        query.setParameter("mezzoId", trattaId);
        return query.getSingleResult();
    }
}
