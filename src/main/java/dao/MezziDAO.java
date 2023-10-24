package dao;

import enteties.Mezzi;
import enteties.Periodi;
import enums.StatoMezzo;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MezziDAO {
    private final EntityManager em;

    public MezziDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Mezzi mezzo) throws InterruptedException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(mezzo);
            transaction.commit();
            System.err.println("Mezzo registrato correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(mezzo);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Errore durante la registrazione del mezzo." + e);
            throw e;
        }
    }

    public Mezzi getById(long id) {
        TypedQuery<Mezzi> getElement = em.createQuery("SELECT m FROM Mezzi m WHERE m.id = :id", Mezzi.class);
        getElement.setParameter("id", id);
        return getElement.getSingleResult();
    }

    public List<Mezzi> getAllOnService() {
        TypedQuery<Mezzi> getElements = em.createQuery("SELECT m FROM Mezzi m WHERE m.statoMezzo = :IN_SERVIZIO", Mezzi.class);
        getElements.setParameter("IN_SERVIZIO", StatoMezzo.IN_SERVIZIO);
        return getElements.getResultList();
    }

    public void delete(long id) throws InterruptedException {
        Mezzi mezzo = em.find(Mezzi.class, id);
        if (mezzo != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(mezzo);
                transaction.commit();
                System.err.println("Mezzo eliminato correttamente");
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(mezzo);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println("Errore durante l'eliminazione del mezzo." + e);
                throw e;
            }
        }
    }


    public List<Periodi> getPeriodListForTransport(long mezzo_id) {
        TypedQuery<Periodi> getPeriods = em.createQuery("SELECT p FROM Periodi p WHERE p.mezzo.id = :mezzo_id", Periodi.class);
        getPeriods.setParameter("mezzo_id", mezzo_id);
        return getPeriods.getResultList();
    }


    public Long getBigliettiVidimatiPerMezzoPerPeriodo(Long idMezzo, LocalDate inizioPeriodo, LocalDate finePeriodo) {
        TypedQuery<Long> q = null;
        if (inizioPeriodo.isBefore(finePeriodo)
        ) {
            q = em.createQuery(
                    "SELECT COUNT(v) FROM Biglietti v WHERE v.mezzo.id = :idMezzo AND v.dataValidazione BETWEEN :inizioPeriodo AND :finePeriodo",
                    Long.class);
            q.setParameter("idMezzo", idMezzo);
            q.setParameter("inizioPeriodo", inizioPeriodo);
            q.setParameter("finePeriodo", finePeriodo);
        } else if (
                inizioPeriodo.isAfter(finePeriodo)
        ) {
            q = em.createQuery(
                    "SELECT COUNT(v) FROM Biglietti v WHERE v.mezzo.id = :idMezzo AND v.dataValidazione BETWEEN :finePeriodo AND :inizioPeriodo",
                    Long.class);
            q.setParameter("idMezzo", idMezzo);
            q.setParameter("inizioPeriodo", inizioPeriodo);
            q.setParameter("finePeriodo", finePeriodo);
        }
        return q != null ? q.getSingleResult() : -1;

    }

}
