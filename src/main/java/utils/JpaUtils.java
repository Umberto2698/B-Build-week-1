package utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtils {
    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("archive");

        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static void close() {
        emf.close();
    }
}
