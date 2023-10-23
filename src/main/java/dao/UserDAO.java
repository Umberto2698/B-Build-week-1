package dao;

import enteties.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.concurrent.TimeUnit;

public class UserDAO {
    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public void save(User user) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(user);
            transaction.commit();
            System.err.println("User salvato correttamente");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(user);
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

    public void deleteById(long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.remove(user);
                transaction.commit();
                System.err.println("User eliminato correttamente");
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(user);
            } catch (Exception ex) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println(ex.getMessage());

            }
        }
    }
}
