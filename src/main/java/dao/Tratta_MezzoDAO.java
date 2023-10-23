package dao;

import enteties.Tratta_Mezzo;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
            System.err.println("Corsa salvata correttamente");
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

    public Tratta_Mezzo getById(long id) {
        return em.find(Tratta_Mezzo.class, id);
    }
}
