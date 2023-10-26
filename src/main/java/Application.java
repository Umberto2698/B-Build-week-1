import com.github.javafaker.Faker;
import dao.*;
import enteties.*;
import enums.TipoAbbonamento;
import enums.TipoUser;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        //            **************************** IMPORTANTE NON CANCELLARE ************************************

//          try{
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

        List<Venditore> allSellers = vDAO.getAllSellers();
        int allSellersSize = allSellers.size();
        List<User> allUsers = uDAO.getAllUsers();
        int allUsersSize = allUsers.size();

        Supplier<Biglietti> bigliettiSupplier = () -> {
            int n = new Random().nextInt(1, allSellersSize);
            int m = new Random().nextInt(1, allUsersSize);
            return new Biglietti(faker.date().between(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(m), allSellers.get(n));
        };
//        LocalDate dataEmissione = LocalDate.of(2023, 10, 26);
        User user1 = uDAO.getById(1336144989408L);
        Venditore venditore = vDAO.getById(1010266087025L);
//        Biglietti biglietto = new Biglietti(dataEmissione, user1, venditore);
//        bDAO.save(biglietto);
        TipoAbbonamento tipoAbbonamento1 = TipoAbbonamento.MENSILE;
        Abbonamenti abbonamento1 = new Abbonamenti(tipoAbbonamento1, user1, venditore);
//        aDAO.save(abbonamento1);

        int n;
        long currentUserId = 0;
        int n1;
        int n2;
        User user = null;


        System.out.println("0 per interrompere, 1 per registrati sul sito, 2 per accedere ");
        n1 = Integer.parseInt(input.nextLine().trim());
        switch (n1) {
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
                    currentUserId = buddy.getId();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            case 2 -> {
                System.out.println("Inserisci l'ID utente per accedere :");
                long userId = Long.parseLong(input.nextLine().trim());
                user = uDAO.getById(userId);
                if (user != null) {
                    currentUserId = user.getId();
                    if (user.getTipoUser() == TipoUser.CUSTOMER) {
                        do {

                            System.out.println("menu , numeri case da inserire qui ");

                            n = Integer.parseInt(input.nextLine().trim());
                            switch (n) {
                                case 0 -> {
                                    input.close();
                                    em.close();
                                    JpaUtils.close();
                                }

                                case 3 -> {
                                    user = uDAO.getById(currentUserId);
                                    if (user != null) {
                                        LocalDate dataEmissione1 = LocalDate.now();
                                        Tessera tesseraNuova = new Tessera(dataEmissione1, user);
                                        tDAO.save(tesseraNuova);
                                    } else {
                                        System.err.println("ID utente non trovato!");
                                    }

                                }
                                case 4 -> {

                                    if (currentUserId != 0) {
                                        Tessera tesseraUtente = tDAO.getTesseraByUserId(currentUserId);
                                        if (tesseraUtente != null) {
                                            tDAO.isTesseraScadutaById(tesseraUtente.getId());
                                        }
                                    } else {
                                        System.out.println("Non hai una tessera");
                                    }
                                }
                                case 5 -> {
                                    if (currentUserId != 0) {
                                        Biglietti bigliettoNonValidato = bDAO.findNonValidatedTicketForUser(currentUserId);
                                        System.out.println(bigliettoNonValidato);
                                        if (bigliettoNonValidato != null) {
                                            bDAO.validateTicket(mDAO, bigliettoNonValidato);
                                        } else {
                                            System.out.println("Nessun biglietto non validato trovato , comprane uno nuovo");
                                        }
                                    } else {
                                        System.err.println("Errore ID utente non trovato");
                                    }
                                }
                                case 6 -> {
                                    if (currentUserId != 0) {
                                        Abbonamenti abbonamentoUtente = aDAO.getAbbonamentoByUserId(currentUserId);
                                        if (abbonamentoUtente != null) {
                                            aDAO.isAbbonamentoScaduto(abbonamentoUtente);
                                        } else {
                                            System.out.println("Non hai un abbonamento, vai a farlo");
                                        }
                                    }
                                }

                            }
                        }
                        while (n != 0);


                    } else {
                        //sei un admin.
                        do {
                            System.out.println("MENU ADMIN");
                            n2 = Integer.parseInt(input.nextLine().trim());


                            switch (n2) {
                                case 1 -> {
                                    System.out.println("Inserisci la prima data (formato: yyyy-MM-dd): ");
                                    String inputDate1 = input.nextLine();
                                    LocalDate date1 = LocalDate.parse(inputDate1);

                                    System.out.println("Inserisci la seconda data (formato: yyyy-MM-dd): ");
                                    String inputDate2 = input.nextLine();
                                    LocalDate date2 = LocalDate.parse(inputDate2);

                                    if (date1.isEqual(date2)) {
                                        System.out.println("Le date sono uguali. Inserisci date diverse.");
                                    } else {
                                        long numberOfTickets = bDAO.getNumberOfTicketsInTimeIntervall(date1, date2);
                                        System.out.println("Numero di biglietti emessi nell'intervallo di date: " + numberOfTickets);
                                    }
                                }
                                case 2 -> {
                                    System.out.println("ciao");
                                }


                            }
                        } while (n2 != 0);

                    }
                } else {
                    System.err.println("ID utente non trovato, assicurati di aver inserito l'id corretto!");
                }
            }


        }


    }
}
