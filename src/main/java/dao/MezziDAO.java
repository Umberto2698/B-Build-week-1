package dao;

import enteties.Mezzi;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
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
        return em.find(Mezzi.class, id);
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
    public Long getBigliettiVidimatiPerMezzoPerPeriodo(Long idMezzo, LocalDate inizioPeriodo, LocalDate finePeriodo) {
        TypedQuery<Long> q = null;
        if ( inizioPeriodo.isBefore(finePeriodo)
        ) {
        q = em.createQuery(
                "SELECT COUNT(v) FROM Biglietti v WHERE v.mezzo.id = :idMezzo AND v.dataVidimazione BETWEEN :inizioPeriodo AND :finePeriodo",
                Long.class);
        q.setParameter("idMezzo", idMezzo);
        q.setParameter("inizioPeriodo", inizioPeriodo);
        q.setParameter("finePeriodo", finePeriodo);
        } else if (
                inizioPeriodo.isAfter(finePeriodo)
        ) {
            q = em.createQuery(
                    "SELECT COUNT(v) FROM Biglietti v WHERE v.mezzo.id = :idMezzo AND v.dataVidimazione BETWEEN :finePeriodo AND :inizioPeriodo",
                    Long.class);
            q.setParameter("idMezzo", idMezzo);
            q.setParameter("inizioPeriodo", inizioPeriodo);
            q.setParameter("finePeriodo", finePeriodo);
        }
        return q != null ? q.getSingleResult() : -1;
    }
}
