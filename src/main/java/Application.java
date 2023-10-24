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
        VenditoreDAO vDAO = new VenditoreDAO(em);
        Supplier<User> userSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().fullAddress());


//        Supplier<Tessera> tesseraSupplier = () -> new Tessera (faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),userSupplier.get());


        Supplier<Biglietti> bigliettiSupplier = () -> {
            List<Venditore> allSellers = vDAO.getAllSellers();
            int size = allSellers.size();
            int n = new Random().nextInt(1, size);
            return new Biglietti(faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allSellers.get(n));
        };
        Supplier<Mezzi> mezziSupplierA = () -> new Mezzi(TipoMezzo.AUTOBUS);
        Supplier<Mezzi> mezziSupplierT = () -> new Mezzi(TipoMezzo.TRAM);

        Scanner input = new Scanner(System.in);

        BigliettiDAO bd = new BigliettiDAO(em);
        MezziDAO md = new MezziDAO(em);

        PeriodiDAO pDAO = new PeriodiDAO(em);
        TrattaDAO trDao = new TrattaDAO(em);
        UserDAO uDAO = new UserDAO(em);
        Tratta_MezzoDAO tr_Mez_DAO = new Tratta_MezzoDAO(em);

        Supplier<Mezzi> autobusSupplier = () -> new Mezzi(TipoMezzo.AUTOBUS);
        Supplier<Mezzi> tramSupplier = () -> new Mezzi(TipoMezzo.TRAM);


        int n;
        do {
            System.out.println("0 per interrompere, 1 per registrati sul sito");
            n = Integer.parseInt(input.nextLine().trim());
            switch (n) {
                case 0 -> {
                    input.close();
                    em.close();
                    JpaUtils.close();
                }

                case 1 -> {
                    try {
                        System.out.println("inserisci il tuo nome");
                        String nome = input.nextLine().trim();
                        System.out.println("inserisci il tuo cognome");
                        String cognome = input.nextLine().trim();
                        System.out.println("inserisci anno di nascita");
                        int anno = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        System.out.println("inserisci mese di nascita");
                        int mese = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        System.out.println("inserisci giorno di nascita");
                        int giorno = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        User buddy = new User(nome, cognome, LocalDate.of(anno, mese, giorno));
                        uDAO.save(buddy);
                        System.out.println("il tuo id è :" + buddy.getId());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }


            }

        } while (n != 0);

    }
}


