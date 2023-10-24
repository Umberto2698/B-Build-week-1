import com.github.javafaker.Faker;
import dao.BigliettiDAO;
import dao.MezziDAO;
import dao.VenditoreDAO;
import enteties.*;
import enums.TipoMezzo;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

public class Application {

    public static void main(String[] args) {
        EntityManager em = JpaUtils.getEmf().createEntityManager();
        BigliettiDAO bd = new BigliettiDAO(em);
        MezziDAO md = new MezziDAO(em);
        VenditoreDAO vDAO = new VenditoreDAO(em);

        Faker faker = new Faker(Locale.ITALY);

        Supplier<User> userSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().fullAddress());
        Supplier<Biglietti> bigliettiSupplier = () -> {
            List<Venditore> allSellers = vDAO.getAllSellers();
            int size = allSellers.size();
            int n = new Random().nextInt(1, size);
            return new Biglietti(faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allSellers.get(n));
        };
        Supplier<Mezzi> autobusSupplier = () -> new Mezzi(TipoMezzo.AUTOBUS);
        Supplier<Mezzi> tramSupplier = () -> new Mezzi(TipoMezzo.TRAM);

        Scanner input = new Scanner(System.in);
        try {
//            Venditore v1 = vDAO.getById(5911833229806L);
//            System.out.println(bd.getNumberOfTicketsInTimeIntervallForSeller(LocalDate.of(2020, 12, 12), LocalDate.of(2017, 8, 1), v1));
//            for (int i = 0; i < 100; i++) {
//                Biglietti randomTicket = bigliettiSupplier.get();
//                bd.save(randomTicket);
//            }
//            for (int i = 0; i < 10; i++) {
//                Rivenditore randomRivenditore = rivenditoreSupplier.get();
//                vDAO.save(randomRivenditore);
//            }
//            for (int i = 0; i < 10; i++) {
//                User randomUser = userSupplier.get();
//                uDAO.save(randomUser);
//            }
//            for (int i = 0; i < 10; i++) {
//                md.save(autobusSupplier.get());
//                md.save(tramSupplier.get());
//            }

//            Mezzi m1 = md.getById(4810079588026L);
//            Biglietti b1 = bd.getById(1623506813794L);
////            bd.validateTicket(md, b1);
//            System.out.println(bd.bigliettiValidatiSuUnMezzo(4810079588026L));
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
            JpaUtils.close();
            input.close();
        }
    }
}
