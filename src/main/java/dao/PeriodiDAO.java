package dao;

import enteties.Periodi;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class PeriodiDAO {
    private final EntityManager em;

    public PeriodiDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Periodi periodi) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(periodi);
            transaction.commit();
//            System.err.println("Periodo salvato correttamente");
//            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(periodi);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante il salvataggio del periodo." + e);
            throw e;
        }
    }

    public Periodi getById(long id) {
        return em.find(Periodi.class, id);
    }
}
