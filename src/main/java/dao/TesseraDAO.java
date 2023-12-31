package dao;

import enteties.Tessera;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;

public class TesseraDAO {
    private final EntityManager em;

    public TesseraDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Tessera p) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(p);
            transaction.commit();
            System.out.println("Tessera salvata correttamente: " + p);
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println(ex.getMessage());

        }
    }

    public Tessera getById(long id) {
        return em.find(Tessera.class, id);
    }

    public void deleteById(long id) {
        Tessera u = em.find(Tessera.class, id);
        if (u != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(u);
                transaction.commit();
                System.out.println("Tessera eliminata correttamente: " + u);
            } catch (Exception ex) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println(ex.getMessage());

            }
        }
    }

    public void isTesseraScadutaById(long tesseraId) {
        LocalDate currentDate = LocalDate.now();
        Tessera tesseraDaVerificare = em.find(Tessera.class, tesseraId);
        if (tesseraDaVerificare == null) {
            System.out.println("tessera Non trovata");
        }
        LocalDate dataScadenza = tesseraDaVerificare.getDataScadenza();

        if (currentDate.isBefore(dataScadenza)) {
            System.out.println("La tessera è valida, scadra' il : " + tesseraDaVerificare.getDataScadenza());

        } else {
            tesseraDaVerificare.setDataScadenza(LocalDate.now().plusYears(1));
            Query validaQuery = em.createQuery("UPDATE Tessera t SET t.dataScadenza = :nuovaScadenza WHERE t.id = :id");
            validaQuery.setParameter("nuovaScadenza", tesseraDaVerificare.getDataScadenza());
            validaQuery.setParameter("id", tesseraId);
            System.out.println("La tessera è Scaduta , Rinnovata!");
            System.out.println(tesseraDaVerificare);
        }
    }

    public Tessera getTesseraByUserId(long userId) {
        TypedQuery<Tessera> query = em.createQuery("SELECT t FROM Tessera t WHERE t.user.id = :userId", Tessera.class);
        query.setParameter("userId", userId);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
