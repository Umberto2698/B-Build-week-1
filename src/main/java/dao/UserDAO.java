package dao;

import enteties.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
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
//            System.err.println("User salvato correttamente");
//            TimeUnit.MILLISECONDS.sleep(1000);
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

    public List<User> getAllUsers() {
        TypedQuery<User> getAllUsers = em.createQuery("SELECT u FROM User u", User.class);
        return getAllUsers.getResultList();
    }

    public List<User> getAllUsersWithCard() {
        TypedQuery<User> getAllUsersWithCard = em.createQuery("SELECT u FROM User u WHERE u.tessera IS NOT EMPTY", User.class);
        return getAllUsersWithCard.getResultList();
    }

    public List<User> getAllUsersWithValidCard() {
        TypedQuery<User> getAllUsersWithValidCards = em.createQuery("SELECT u FROM User u WHERE u.tessera IS NOT EMPTY AND u.tessera.dataScadenza < :now", User.class);
        getAllUsersWithValidCards.setParameter("now", LocalDate.now());
        return getAllUsersWithValidCards.getResultList();
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
