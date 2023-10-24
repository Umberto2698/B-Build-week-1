import com.github.javafaker.Faker;
import dao.*;
import enteties.*;
import enums.TipoAbbonamento;
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
        TesseraDAO tDAO = new TesseraDAO(em);
        Supplier<User> userSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().fullAddress());
        Supplier<Tratta> trattaSupplier = () -> new Tratta(faker.address().cityName(), faker.address().cityName(), new Random().nextDouble(15, 45));


//        Supplier<Tessera> tesseraSupplier = () -> new Tessera (faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),userSupplier.get());
        List<Venditore> allSellers = vDAO.getAllSellers();
        int size = allSellers.size();

        Supplier<Biglietti> bigliettiSupplier = () -> {

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
        Supplier<Tratta_Mezzo> tratta_mezzoSupplier = () -> {
            List<Mezzi> allOnService = md.getAllOnService();
            int size1 = allSellers.size();
            int n = new Random().nextInt(1, size1);
            List<Tratta> allRoutes = trDao.gettAllRoutes();
            int size2 = allRoutes.size();
            int n1 = new Random().nextInt(1, size2);
            return new Tratta_Mezzo(new Random().nextDouble(15, 45), allOnService.get(n), allRoutes.get(n1));
        };


        int n;

        do {
            System.out.println("0 per interrompere, 1 per registrati sul sito, 2 per creare una tessera, 3 compra un biglietto, 4 per verificare Scadenza Tessera");

            n = Integer.parseInt(input.nextLine().trim());
            switch (n) {
                case 0 -> {
                    input.close();
                    em.close();
                    JpaUtils.close();
                }


//            for (int i = 0; i < 10; i++) {
//                trDao.save(trattaSupplier.get());
//
//            }
//            for (int i = 0; i < 10; i++) {
//                tr_Mez_DAO.save(tratta_mezzoSupplier.get());
//            }
//            trDao.getTimeTrattaPercorsaBySingleMezzo(7124696535489L, 6259713503238L).forEach(System.out::println);

                case 3 -> {
                    try {
                        Biglietti buddy = bigliettiSupplier.get();
                        System.out.println("questo è il tuo biglietto : ");
                        bd.save(buddy);
                        System.out.println("l'id del tuo biglietto è : " + buddy.getId());
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
                case 2 -> {
                    System.out.println("Inserisci l'ID utente per creare una tessera:");
                    long userId = Long.parseLong(input.nextLine().trim());
                    User user = uDAO.getById(userId);
                    if (user != null) {
                        LocalDate dataEmissione = LocalDate.now();
                        LocalDate dataScadenza = dataEmissione.plusYears(1);
                        Tessera tesseraNuova = new Tessera(dataEmissione, dataScadenza, user);
                        tDAO.save(tesseraNuova);
                    } else {
                        System.err.println("ID utente non trovato, assicurati di aver inserito l'id corretto!");
                    }

                }
                case 4 -> {
                    System.out.println("Inserisci l'ID della tessera da verificare:");
                    long tesseraId = Long.parseLong(input.nextLine().trim());
                    Tessera tessera = tDAO.getById(tesseraId);
                    if (tessera != null) {
                        tDAO.isTesseraScadutaById(tesseraId);
                    } else {
                        System.err.println("Tessera non trovata, assicurati di aver inserito l'id corretto!");
                    }

                }

                case 5 -> {
                    System.out.println("inserisci 1 per comprare il piano mensile, 2 per il piano settimanale : ");
                    int piano = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                    if (piano == 1) {
                        System.out.println("inserisci l'id del utente : ");
                        long idUtente = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        User utente = uDAO.getById(idUtente);
                        int m = new Random().nextInt(1, size);
                        Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.MENSILE, utente, allSellers.get(m));
                    } else if (piano == 2) {
                        System.out.println("inserisci l'id del utente : ");
                        long idUtente = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        User utente = uDAO.getById(idUtente);
                        int m = new Random().nextInt(1, size);
                        Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.SETTIMANALE, utente, allSellers.get(m));
                    } else {
                        break;
                    }

                }

//            **************************** IMPORTANTE NON CANCELLARE ************************************

//
//            List<Object[]> lista = trDao.getTimeTrattaPercorsa(7124696535489L);
//            for (Object[] el : lista) {
//                double tempo = (double) el[0];
//                Mezzi mezzo = (Mezzi) el[1];
//
//                System.out.println("Tempo effettivo: " + tempo + " mezzo: " + mezzo);
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        } finally {
//            em.close();
//            JpaUtils.close();
//        }


//        do {
//            System.out.println("0 per interrompere, 1 per registrati sul sito");
//            n = Integer.parseInt(input.nextLine().trim());
//            switch (n) {
//                case 0 -> {
//                    input.close();
//                    em.close();
//                    JpaUtils.close();
//                }
//
//                case 1 -> {
//                    try {
//                        System.out.println("inserisci il tuo nome");
//                        String nome = input.nextLine().trim();
//                        System.out.println("inserisci il tuo cognome");
//                        String cognome = input.nextLine().trim();
//                        System.out.println("inserisci anno di nascita");
//                        int anno = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
//                        System.out.println("inserisci mese di nascita");
//                        int mese = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
//                        System.out.println("inserisci giorno di nascita");
//                        int giorno = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
//                        User buddy = new User(nome, cognome, LocalDate.of(anno, mese, giorno));
//                        uDAO.save(buddy);
//                        System.out.println("il tuo id è :" + buddy.getId());
//                    } catch (Exception e) {
//                        System.out.println(e);
//                    }
//                }
//
//
//            }
//
            }
        }
        while (n != 0);

    }
}
