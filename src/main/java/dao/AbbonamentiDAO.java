package dao;

import enteties.Abbonamenti;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
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

    public void delete(long id) throws InterruptedException {
        Abbonamenti abbonamento = em.find(Abbonamenti.class, id);
        if (abbonamento != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(abbonamento);
                transaction.commit();
                System.err.println("Abbonamento eliminato correttamente");
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(abbonamento);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println("Errore durante l'eliminazione dell'abbonamento." + e);
                throw e;
            }
        }
    }

    public List<Abbonamenti> getAbbonamentoByUserId(long userId) {
        TypedQuery<Abbonamenti> query = em.createQuery(
                "SELECT a FROM Abbonamenti a WHERE a.user.id = :userId",
                Abbonamenti.class
        );
        query.setParameter("userId", userId);

        try {
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Nessun abbonamento Trovato");
            return null;
        }
    }

    public void isAbbonamentoScaduto(Abbonamenti abbonamento) {
        LocalDate currentDate = LocalDate.now();
        LocalDate dataScadenza = abbonamento.getDataScadenza();

        if (currentDate.isBefore(dataScadenza)) {

            System.out.println("Abbonamento valido! scadra' il " + abbonamento.getDataScadenza());
        } else {

            System.out.println("Abbonamento scaduto il " + abbonamento.getDataScadenza());
        }
    }
}
