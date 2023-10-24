import com.github.javafaker.Faker;
import dao.*;
import enteties.*;
import enums.StatoDistributore;
import enums.StatoMezzo;
import enums.TipoAbbonamento;
import enums.TipoMezzo;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

public class FillDatabase {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        EntityManager em = JpaUtils.getEmf().createEntityManager();

        UserDAO uDAO = new UserDAO(em);
        VenditoreDAO vDAO = new VenditoreDAO(em);
        TesseraDAO tDAO = new TesseraDAO(em);
        BigliettiDAO bDAO = new BigliettiDAO(em);
        AbbonamentiDAO aDAO = new AbbonamentiDAO(em);
        MezziDAO mDAO = new MezziDAO(em);
        PeriodiDAO pDAO = new PeriodiDAO(em);
        TrattaDAO trDAO = new TrattaDAO(em);
        Tratta_MezzoDAO tr_m_DAO = new Tratta_MezzoDAO(em);

        Faker faker = new Faker(Locale.ITALY);

        Supplier<User> userSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().streetAddress());
        Supplier<Distributore> distributoreFuoriServizioSupplier = () -> new Distributore(StatoDistributore.FUORISERVIZIO);
        Supplier<Distributore> distributoreAttivoSupplier = () -> new Distributore(StatoDistributore.ATTIVO);
        Supplier<Mezzi> autobusInServizioSupplier = () -> new Mezzi(TipoMezzo.AUTOBUS);
        Supplier<Mezzi> autobusInManutenzioneSupplier = () -> new Mezzi(TipoMezzo.AUTOBUS, StatoMezzo.IN_MANUTENZIONE);
        Supplier<Mezzi> tramInServizioSupplier = () -> new Mezzi(TipoMezzo.TRAM);
        Supplier<Mezzi> tramInManutenzioneSupplier = () -> new Mezzi(TipoMezzo.TRAM, StatoMezzo.IN_MANUTENZIONE);
        Supplier<Tratta> trattaSupplier = () -> new Tratta(faker.address().cityName(), faker.address().cityName(), Double.parseDouble(new DecimalFormat("0.0").format(new Random().nextDouble(0, 2)).replaceAll(",", ".")));

        List<Venditore> allSellers = vDAO.getAllSellers();
        int allSellersSize = allSellers.size();
        List<User> allUsers = uDAO.getAllUsers();
        int allUsersSize = allUsers.size();
        List<User> allUsersWithValidCards = uDAO.getAllUsersWithValidCard();
        int allUsersWithValidCardsSize = allUsersWithValidCards.size();

        Supplier<Tessera> tesseraSupplier = () -> {
            int n = new Random().nextInt(1, allUsersSize);
            return new Tessera(faker.date().between(Date.from(allUsers.get(n).getDataNascita().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(n));
        };


        Supplier<Biglietti> bigliettiSupplier = () -> {
            int n = new Random().nextInt(1, allSellersSize);
            int m = new Random().nextInt(1, allUsersSize);
            return new Biglietti(faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(m), allSellers.get(n));
        };

        Supplier<Abbonamenti> abbonamentiSettimanaliSupplier = () -> {
            int n = new Random().nextInt(1, allSellersSize);
            int m = new Random().nextInt(1, allUsersWithValidCardsSize);
            return new Abbonamenti(TipoAbbonamento.SETTIMANALE, faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsersWithValidCards.get(m), allSellers.get(n));
        };

        Supplier<Abbonamenti> abbonamentiMensiliSupplier = () -> {
            int n = new Random().nextInt(1, allSellersSize);
            int m = new Random().nextInt(1, allUsersWithValidCardsSize);
            return new Abbonamenti(TipoAbbonamento.MENSILE, faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsersWithValidCards.get(m), allSellers.get(n));
        };

        List<Mezzi> allTransports = mDAO.getAll();

        allTransports.forEach(transport -> {
            LocalDate start = faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.of(2010, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = faker.date().between(Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.of(2010, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Periodi period = new Periodi(start, end, transport, StatoMezzo.IN_MANUTENZIONE);
            try {
                pDAO.save(period);
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        Supplier<Tratta_Mezzo> tratta_mezzoSupplier = () -> {
            List<Mezzi> allOnService = mDAO.getAllOnService();
            int n = new Random().nextInt(1, allOnService.size());
            List<Tratta> allRoutes = trDAO.gettAllRoutes();
            int n1 = new Random().nextInt(1, allRoutes.size());
            return new Tratta_Mezzo(Double.parseDouble(new DecimalFormat("0.0").format(new Random().nextDouble(0, 2)).replaceAll(",", ".")), allOnService.get(n), allRoutes.get(n1));
        };

        try {
// ******************************************** PRIMO AVVIO *********************************************
            for (int i = 0; i < 50; i++) {
                uDAO.save(userSupplier.get());
                vDAO.save(distributoreAttivoSupplier.get());
                vDAO.save(rivenditoreSupplier.get());
            }
            for (int i = 0; i < 10; i++) {
                vDAO.save(distributoreFuoriServizioSupplier.get());
                mDAO.save(tramInManutenzioneSupplier.get());
                mDAO.save(tramInServizioSupplier.get());
                mDAO.save(autobusInManutenzioneSupplier.get());
                mDAO.save(autobusInServizioSupplier.get());
                trDAO.save(trattaSupplier.get());
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
