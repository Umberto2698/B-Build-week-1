package dao;

import enteties.Venditore;
import enums.StatoDistributore;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
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
            System.err.println("Venditore abilitato correttamente");
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

    public List<Venditore> getVenditoriInZona(String address) {
        TypedQuery<Venditore> getVenditoriInZona = em.createQuery("SELECT v FROM Venditore v WHERE LOWER(v.adress) LIKE :address", Venditore.class);
        getVenditoriInZona.setParameter("address", "%" + address.toLowerCase() + "%");
        return getVenditoriInZona.getResultList();
    }

    public List<Venditore> getAllRivenditori() {
        TypedQuery<Venditore> getAllRivenditori = em.createQuery("SELECT v FROM Venditore v WHERE v.adress IS NOT NULL", Venditore.class);
        return getAllRivenditori.getResultList();
    }

    public List<Venditore> getAllDistributoriAttivi() {
        TypedQuery<Venditore> getAllDistributoriInServizio = em.createQuery("SELECT v FROM Venditore v WHERE v.stato = :stato", Venditore.class);
        getAllDistributoriInServizio.setParameter("stato", StatoDistributore.ATTIVO);
        return getAllDistributoriInServizio.getResultList();
    }

    public List<Venditore> getAllDistributoriFuoriServizio() {
        TypedQuery<Venditore> getAllDistributoriFuoriServizio = em.createQuery("SELECT v FROM Venditore v WHERE v.stato = :stato", Venditore.class);
        getAllDistributoriFuoriServizio.setParameter("stato", StatoDistributore.FUORISERVIZIO);
        return getAllDistributoriFuoriServizio.getResultList();
    }

    public List<Object[]> getRivenditoriEBigliettiVenduti() {
        Query getResults = em.createQuery("SELECT v, COUNT(v.biglietti) FROM Venditore v");
        return getResults.getResultList();
    }

    public void updateStatoDistributore(long distributorId, StatoDistributore nuovoStato) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Query updateDistributoreQuery = em.createQuery("UPDATE Distributore d SET d.stato = :nuovoStato WHERE d.id = :distributorId");
            updateDistributoreQuery.setParameter("nuovoStato", nuovoStato);
            updateDistributoreQuery.setParameter("distributorId", distributorId);

            int updatedCount = updateDistributoreQuery.executeUpdate();
            transaction.commit();

            if (updatedCount > 0) {
                System.out.println("Stato del distributore cambiato con successo");
            } else {
                System.out.println("Nessun distributore trovato con l'ID : " + distributorId);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante il cambiamento dello stato del distributore: " + e.getMessage());

        }
    }


}
