package dao;

import enteties.Biglietti;
import enteties.Mezzi;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
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

    public List<Biglietti> bigliettiValidatiSuUnMezzo(long id) {
        TypedQuery<Biglietti> bigliettiValidatiSuUnMezzo = em.createQuery("SELECT b FROM Biglietti b JOIN b.mezzo m WHERE m.id = :id", Biglietti.class);
        bigliettiValidatiSuUnMezzo.setParameter("id", id);
        return bigliettiValidatiSuUnMezzo.getResultList();

    }

    public void validateTicket(MezziDAO md, Biglietti b) {
        List<Mezzi> mezziInServizio = md.getAllOnService().stream().toList();
        int size = mezziInServizio.size();
        int n = new Random().nextInt(1, size + 1);
        b.setMezzo(mezziInServizio.get(n));
        b.setDataValidazione(LocalDate.now());
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            getById(b.getId());
            Query validaQuery = em.createQuery("UPDATE Biglietti b SET b.mezzo = :newMezzo WHERE b.id = :id");
            validaQuery.setParameter("newMezzo", b.getMezzo());
            validaQuery.setParameter("id", b.getId());
            int numeroModificati = validaQuery.executeUpdate();
            transaction.commit();
            if (numeroModificati > 0) {
                System.out.println("Biglietto validato");
            } else {
                System.out.println("Non ho trovato nessun biglietto: " + b);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante la validazione del biglietto." + e);
            throw e;
        }
    }

}
