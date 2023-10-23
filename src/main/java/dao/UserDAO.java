package dao;

import enteties.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class UserDAO {
    private final EntityManager em;

    public UserDAO(EntityManager em) {this.em=em;}
    public void save(User p) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(p);
            transaction.commit();
            System.out.println("User salvato correttamente: " + p);
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println(ex.getMessage());

        }
    }

    public User getById(long id) {
        return em.find(User.class, id);
    }

    public void deleteById(long id ) {
        User u = em.find(User.class, id);
        if (u != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(u);
                transaction.commit();
                System.out.println("User eliminato correttamente: " + u);
            } catch (Exception ex) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println(ex.getMessage());

            }
        }
    }
}
