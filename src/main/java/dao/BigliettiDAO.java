package dao;

import enteties.Biglietti;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

}
