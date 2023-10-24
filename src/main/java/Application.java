import com.github.javafaker.Faker;
import dao.*;
import enteties.*;
import enums.TipoMezzo;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Supplier;

public class Application {

    public static void main(String[] args) {
        EntityManager em = JpaUtils.getEmf().createEntityManager();
        Faker faker = new Faker(Locale.ITALY);
        Supplier<User> userSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().fullAddress());
        Supplier<Biglietti> bigliettiSupplier = () -> new Biglietti(faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                        , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), rivenditoreSupplier.get());

//        Supplier<Tessera> tesseraSupplier = () -> new Tessera (faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),userSupplier.get());

        Scanner input = new Scanner(System.in);

        MezziDAO md = new MezziDAO(em);
        BigliettiDAO bDAO = new BigliettiDAO(em);
        UserDAO uDAO = new UserDAO(em);
        TesseraDAO tDAO = new TesseraDAO(em);
        VenditoreDAO vDAO = new VenditoreDAO(em);

        Mezzi m1 = new Mezzi(TipoMezzo.AUTOBUS);
        try {
//            for (int i = 0; i < 10; i++) {
//                Rivenditore randomRivenditore = rivenditoreSupplier.get();
//                vDAO.save(randomRivenditore);
//            }
//            for (int i = 0; i < 10; i++) {
//                User randomUser = userSupplier.get();
//                uDAO.save(randomUser);
//            }
            for(int i =0; i<10;i ++){
                LocalDate dataEmissione = faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Tessera tesseraGenerata = new Tessera(dataEmissione);

                tDAO.save(randomTessera);
            }


        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
            JpaUtils.close();
            input.close();
        }
    }
}
