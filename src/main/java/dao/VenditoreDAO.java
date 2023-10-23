package dao;

import enteties.Venditore;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.concurrent.TimeUnit;

public class VenditoreDAO {
    private final EntityManager em;

    public VenditoreDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Venditore venditore) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(venditore);
            transaction.commit();
            System.err.println("Venditore salvato correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(venditore);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante il salvataggio del venditore." + e);
            throw e;
        }
    }

    public Venditore getById(long id) {
        return em.find(Venditore.class, id);
    }

    public void delete(long id) throws InterruptedException {
        Venditore venditore = em.find(Venditore.class, id);
        if (venditore != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(venditore);
                transaction.commit();
                System.err.println("Venditore eliminato correttamente");
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(venditore);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println("Errore durante l'eliminazione del venditore." + e);
                throw e;
            }
        }
    }
}