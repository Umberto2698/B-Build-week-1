package dao;

import enteties.Tessera;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class TesseraDAO {
    private final EntityManager em;

    public TesseraDAO(EntityManager em) {this.em=em;}
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

    public void deleteById(long id ) {
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
    }}
