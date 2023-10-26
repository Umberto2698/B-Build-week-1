package dao;

import enteties.Venditore;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
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

    public List<Venditore> getAllSellers() {
        TypedQuery<Venditore> getAllSellers = em.createQuery("SELECT v FROM Venditore v", Venditore.class);
        return getAllSellers.getResultList();
    }

//    public List<Venditore> getAllActiveSellers() {
//        TypedQuery<Venditore> getAllActiveSellers = em.createQuery("SELECT v FROM Venditore v WHERE v.stato LIKE :state", Venditore.class);
//        getAllActiveSellers.setParameter("state", StatoDistributore.ATTIVO);
//        return getAllActiveSellers.getResultList();
//    }

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

    public List<Venditore> getVenditoriInZona(String adress) {
        TypedQuery<Venditore> getVenditoriInZona = em.createQuery("SELECT v FROM Venditore v WHERE v.adress LIKE :adress", Venditore.class);
        getVenditoriInZona.setParameter("adress", "%" + adress + "%");
        return getVenditoriInZona.getResultList();

    }
}
