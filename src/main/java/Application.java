import com.github.javafaker.Faker;
import dao.*;
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

        Faker faker = new Faker(Locale.ITALY);
        Supplier<User> userSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().fullAddress());


//        Supplier<Tessera> tesseraSupplier = () -> new Tessera (faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),userSupplier.get());

        Scanner input = new Scanner(System.in);

        BigliettiDAO bd = new BigliettiDAO(em);
        MezziDAO md = new MezziDAO(em);
        VenditoreDAO vDAO = new VenditoreDAO(em);
        PeriodiDAO pDAO = new PeriodiDAO(em);
        TrattaDAO trDao = new TrattaDAO(em);
        Tratta_MezzoDAO tr_Mez_DAO = new Tratta_MezzoDAO(em);


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
//            for (int i = 0; i < 5; i++) {
//                md.save(autobusSupplier.get());
//                md.save(tramSupplier.get());
//            }
//            Mezzi mezzo = em.find(Mezzi.class, 1594772249836L);
            Mezzi mezzo2 = em.find(Mezzi.class, 1641620105572L);
//            Periodi periodo1 = new Periodi(LocalDate.of(2022, 1, 1), LocalDate.of(2023, 2, 28), mezzo, StatoMezzo.IN_SERVIZIO);
//            Periodi periodo2 = new Periodi(LocalDate.of(2021, 1, 1), LocalDate.of(2022, 4, 18), mezzo, StatoMezzo.IN_MANUTENZIONE);
//            pDAO.save(periodo2);
//            pDAO.save(periodo1);

//            Periodi periodo3= em.find(Periodi.class,1977984903376L);
//            System.out.println(md.getPeriodListForTransport(1641620105572L));
//            List<Periodi> periodiList = md.getPeriodListForTransport(1594772249836L);
//            for (Periodi periodo : periodiList) {
//                System.out.println(periodo);
//
//            }
//            Tratta tratta2 = new Tratta("Roma", "Australia", 25.4);
////            trDao.save(tratta2);

            Tratta tratta2 = em.find(Tratta.class, 2142233632185L);

            Tratta_Mezzo tratta_mezzo2 = new Tratta_Mezzo(23.3, mezzo2, tratta2);
//            tr_Mez_DAO.save(tratta_mezzo2);
            System.out.println(tr_Mez_DAO.getNumVolteMezzoPercorsoTratta(1641620105572L, 2142233632185L));


//

//            LocalDate dataEmissione2 = faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            LocalDate dataScadenza = dataEmissione2.plusYears(1);
//            User user2 = em.find (User.class,1078141130855L);
//            Tessera tesseraGenerata2 = new Tessera(dataEmissione2,dataScadenza,user2);
//            tesseraGenerata2.setUser(user2);
//            tDAO.save(tesseraGenerata2);
//
//            User user3 = em.find(User.class,3961913075403L);
//            Tessera tesseraGenerata3= new Tessera(user3);
//            tesseraGenerata3.setUser(user3);
//            tDAO.save(tesseraGenerata3);

//            tDAO.isTesseraScadutaById(453070569857L);
//            tDAO.isTesseraScadutaById(728465382503L);


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
