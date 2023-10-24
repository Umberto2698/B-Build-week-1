import com.github.javafaker.Faker;
import dao.BigliettiDAO;
import dao.MezziDAO;
import dao.VenditoreDAO;
import enteties.Biglietti;
import enteties.Distributore;
import enteties.Mezzi;
import enteties.Venditore;
import enums.TipoMezzo;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        EntityManager em = JpaUtils.getEmf().createEntityManager();
        Faker faker = new Faker(Locale.ITALY);
        Scanner input = new Scanner(System.in);
        BigliettiDAO bd = new BigliettiDAO(em);
        MezziDAO md = new MezziDAO(em);
        VenditoreDAO vd = new VenditoreDAO(em);
        Mezzi m1 = new Mezzi(TipoMezzo.TRAM);

        Distributore dist = new Distributore();
        Venditore d = vd.getById(2029612248213L);
        Biglietti test = new Biglietti(d);
        try {
            //md.save(m1);
            //vd.save(dist);
            Biglietti b = bd.getById(2514217303237L);
            // b.validateTicket(md);
            //System.out.println(b);

            // bd.validateTicket(md, b);

            List<Biglietti> listB = bd.bigliettiValidatiSuUnMezzo(3114517253680L);
            listB.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
            JpaUtils.close();
            input.close();
        }
    }
}
