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

    public static void main(String[] args) {
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

        int n;
        long currentUserId = 0;
        int n1;
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
                    System.out.println("ciao");
                    currentUserId = user.getId();
                    if (user.getTipoUser() == TipoUser.CUSTOMER) {
                        System.out.println("ciao2");
                        do {

                            System.out.println("0 per interrompere, 1 per registrati sul sito, 2 per accedere ");

                            n = Integer.parseInt(input.nextLine().trim());
                            switch (n) {
                                case 0 -> {
                                    input.close();
                                    em.close();
                                    JpaUtils.close();
                                }
                                case 3 -> {
                                    try {
                                        Biglietti buddy = bigliettiSupplier.get();
                                        System.out.println("questo è il tuo biglietto : ");
                                        bDAO.save(buddy);
                                        System.out.println("l'id del tuo biglietto è : " + buddy.getId());
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }


                                }
                                case 2 -> {
                                    System.out.println("Inserisci l'ID utente per creare una tessera:");
                                    user = uDAO.getById(userId);
                                    if (user != null) {
                                        LocalDate dataEmissione = LocalDate.now();
                                        Tessera tesseraNuova = new Tessera(dataEmissione, user);
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
                                        int m = new Random().nextInt(1, allSellersSize);
                                        Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.MENSILE, utente, allSellers.get(m));
                                    } else if (piano == 2) {
                                        System.out.println("inserisci l'id del utente : ");
                                        long idUtente = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                                        User utente = uDAO.getById(idUtente);
                                        int m = new Random().nextInt(1, allSellersSize);
                                        Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.SETTIMANALE, utente, allSellers.get(m));
                                    } else {
                                        break;
                                    }

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
                        }
                        while (n != 0);


                    } else {
                        //sei un admin.

                    }
                } else {
                    System.err.println("ID utente non trovato, assicurati di aver inserito l'id corretto!");
                }
            }


        }


    }
}
