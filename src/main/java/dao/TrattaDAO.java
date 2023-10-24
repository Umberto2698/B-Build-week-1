package dao;

import enteties.Tratta;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.concurrent.TimeUnit;

public class TrattaDAO {
    private final EntityManager em;

    public TrattaDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Tratta tratta) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(tratta);
            transaction.commit();
            System.err.println("Tratta salvata correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(tratta);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante il salvataggio della tartta." + e);
            throw e;
        }
    }

    public Tratta getById(long id) {
        return em.find(Tratta.class, id);
    }

    public void delete(long id) throws InterruptedException {
        Tratta tratta = em.find(Tratta.class, id);
        if (tratta != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(tratta);
                transaction.commit();
                System.err.println("Tratta eliminata correttamente");
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(tratta);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println("Errore durante l'eliminazione della tratta." + e);
                throw e;
            }
        }
    }

    public long getTimeTrattaPercorsa(String id) {
        Query q = em.createQuery("SELECT COUNT(m) FROM Mezzo m WHERE m.tratta.id = :id");
        q.setParameter("id", id);
        return (Long) q.getSingleResult();
    }

    public long getTimeTrattaPercorsaBySingleMezzo(String trattaId, String mezzoId) {
        Query q = em.createQuery("SELECT COUNT(m) FROM Mezzo m WHERE m.tratta.id = :trattaId AND m.id = :mezzoId");
        q.setParameter("trattaId", trattaId);
        q.setParameter("mezzoId", mezzoId);
        return (Long) q.getSingleResult();
    }
}
