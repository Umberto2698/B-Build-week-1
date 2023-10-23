package dao;

import enteties.Abbonamenti;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.concurrent.TimeUnit;

public class AbbonamentiDAO {
    private final EntityManager em;

    public AbbonamentiDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Abbonamenti abbonamento) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(abbonamento);
            transaction.commit();
            System.err.println("Abbonamento registrato correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(abbonamento);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante la registrazione dell'abbonamento." + e);
            throw e;
        }
    }

    public Abbonamenti getById(long id) {
        return em.find(Abbonamenti.class, id);
    }
    
}
