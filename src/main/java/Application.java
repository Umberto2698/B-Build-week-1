import com.github.javafaker.Faker;
import dao.MezziDAO;
import enteties.Mezzi;
import enums.TipoMezzo;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.util.Locale;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        EntityManager em = JpaUtils.getEmf().createEntityManager();
        Faker faker = new Faker(Locale.ITALY);
        Scanner input = new Scanner(System.in);

        MezziDAO md = new MezziDAO(em);
        Mezzi m1 = new Mezzi(TipoMezzo.AUTOBUS);
        try {
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
            JpaUtils.close();
            input.close();
        }
    }
}
