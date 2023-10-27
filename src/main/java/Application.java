import com.github.javafaker.Faker;
import dao.*;
import enteties.*;
import enums.*;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Application {

    public static void main(String[] args) throws InterruptedException {
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
        int n2 = 0;
        User user = null;
        User user1 = null;

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
                    try {
                        String nome;
                        do {
                            System.out.println("Inserisci il tuo nome:");
                            nome = input.nextLine().trim();
                            if (nome.length() < 3) {
                                System.err.println("Il nome deve contenere almeno 3 lettere. Riprova.");
                            }
                        } while (nome.length() < 3);

                        String cognome;
                        do {
                            System.out.println("Inserisci il tuo cognome:");
                            cognome = input.nextLine().trim();
                            if (cognome.length() < 3) {
                                System.err.println("Il cognome deve contenere almeno 3 lettere. Riprova.");
                            }
                        } while (cognome.length() < 3);

                        LocalDate dataNascita = ottieniData(input);
                        user1 = new User(nome, cognome, dataNascita);
                        System.out.println("Dati utente registrati con successo.");
                    } catch (NumberFormatException e) {
                        System.err.println("Errore: Inserisci i dati di registrazione correttamente.");
                    }

                    uDAO.save(user1);
                    System.out.println("il tuo id è :" + user1.getId());
                    currentUserId = user1.getId();
                    System.out.println("---------------------------------------------------------------------------------MENU CUSTOMER---------------------------------------------------------------------------------");
                    System.out.println("                                                          Benvenuto " + user1.getNome() + " " + user1.getCognome() + ", scegli una aziona da fare");
                    do {
                        System.out.println(" ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                        System.out.println("- 1 compra un biglietto; - 2 compra un abbonamento; - 3 compra una tessera; - 4 verificha se hai una tessera; - 5 valida un biglietto; - 6 verifica se il tuo abbonamento è scaduto; ");
                        System.out.println("                         - 7 controlla i rivenditori in una zona; - 8 vedi il tempo medio di una tratta; - 9 rinnova la tua tessera; - 0 termina il programma ");
                        n = Integer.parseInt(input.nextLine().trim());
                        switch (n) {
                            case 0 -> {
                                input.close();
                                em.close();
                                JpaUtils.close();
                            }
                            case 1 -> {
                                try {
                                    int nVenditoreRandom = new Random().nextInt(1, allSellersSize);
                                    Biglietti buddy = new Biglietti(LocalDate.now(), user1, allSellers.get(nVenditoreRandom));
                                    System.out.println("questo è il tuo biglietto : ");
                                    bDAO.save(buddy);
                                    System.out.println("l'id del tuo biglietto è : " + buddy.getId());
                                } catch (Exception e) {
                                    System.err.println(e.getMessage());
                                }
                            }
                            case 2 -> {
                                int piano = 0;
                                List<Abbonamenti> userAbb = aDAO.getAbbonamentoByUserId(user.getId());
                                if (userAbb.stream().anyMatch(abbonamento -> abbonamento.getDataScadenza().isAfter(LocalDate.now()))) {
                                    System.out.println("Hai già un abbonamento valido");
                                } else {
                                    while (piano != 1 && piano != 2) {
                                        System.out.println("Inserisci 1 per comprare il piano mensile, 2 per il piano settimanale: ");
                                        try {
                                            piano = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                                            if (piano != 1 && piano != 2) {
                                                System.err.println("Inserisci un valore valido tra 1 o 2");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.err.println("Inserisci un valore numerico valido tra 1 o 2");
                                        }
                                        if (piano == 1) {
                                            try {
                                                int nVenditoreRandom = new Random().nextInt(1, allSellersSize);
                                                Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.MENSILE, user, allSellers.get(nVenditoreRandom));
                                                aDAO.save(buddy);
                                            } catch (Exception e) {
                                                System.err.println(e.getMessage());
                                            }
                                        } else if (piano == 2) {
                                            try {
                                                int nVenditoreRandom = new Random().nextInt(1, allSellersSize);
                                                Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.SETTIMANALE, user, allSellers.get(nVenditoreRandom));
                                                aDAO.save(buddy);
                                            } catch (Exception e) {
                                                System.err.println(e.getMessage());
                                            }
                                        } else {
                                            System.err.println("Errore ID utente non trovato");
                                        }
                                    }
                                }
                            }
                            case 3 -> {
                                user1 = uDAO.getById(currentUserId);
                                if (user1 != null) {
                                    LocalDate dataEmissione1 = LocalDate.now();
                                    Tessera tesseraNuova = new Tessera(dataEmissione1, user1);
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
                                    List<Biglietti> bigliettoNonValidato = bDAO.findNonValidatedTicketForUser(currentUserId);
                                    if (!bigliettoNonValidato.isEmpty()) {
                                        bDAO.validateTicket(mDAO, bigliettoNonValidato.get(0));
                                        System.out.println(bigliettoNonValidato.get(0));
                                    } else {
                                        System.out.println("Nessun biglietto non validato trovato , comprane uno nuovo");
                                    }
                                }
                            }
                            case 6 -> {
                                if (currentUserId != 0) {
                                    List<Abbonamenti> abbonamentoUtente = aDAO.getAbbonamentoByUserId(currentUserId);
                                    if (abbonamentoUtente != null) {
                                        int num = 0;
                                        abbonamentoUtente.forEach(abbonamento -> aDAO.isAbbonamentoScaduto(abbonamento));
                                    } else {
                                        System.out.println("Non hai un abbonamento, vai a farlo");
                                    }
                                }
                            }
                            case 7 -> {
                                System.out.println("Inserisci una via per controllare quali Rivenditori ci sono: ");
                                String viaInput = input.nextLine().trim();
                                //////////////CONTROLLO SE LA VIA è vuota////////////////////
                                if (viaInput.isEmpty()) {
                                    System.err.println("inserisci una via valida.");
                                } else {
                                    List<Venditore> listaVenditoriInZona = vDAO.getVenditoriInZona(viaInput);
                                    if (listaVenditoriInZona.isEmpty()) {
                                        System.err.println("Nessun Rivenditore trovato in questa zona o la via non è valida.");
                                    } else {
                                        listaVenditoriInZona.forEach(System.out::println);
                                    }
                                }
                            }
                            case 8 -> {
                                int piano = -1;
                                while (piano != 1 && piano != 2 && piano != 0) {
                                    System.out.println("inserisci 1 per vedere il tempo medio tramite id, 2 per vedere il tempo medio scrivendto punto di partenza e capolinea | 0 per tornare indietro : ");
                                    try {
                                        piano = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                                        if (piano != 1 && piano != 2 && piano != 0) {
                                            System.err.println("Inserisci un valore numerico valido 1 o 2 | 0 per tornare indietro");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.err.println("Inserisci un valore numerico valido 1 o 2 | 0 per tornare indietro");
                                    }
                                }
                                if (piano == 1) {
                                    long trattaid = 0;
                                    while (true) {
                                        try {
                                            System.out.println("inserisci l'id della tratta per vedere il tempo medio : ");
                                            trattaid = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                            if (trattaid > 0) {
                                                Tratta buddy = trDAO.getTempoMedioById(trattaid);
                                                System.out.println("Tempo stimato : " + buddy.getTempoMedio());
                                                break;
                                            } else {
                                                System.err.println("Nessuna tratta trovata");

                                            }
                                        } catch (Exception e) {
                                            System.err.println("Nessuna tratta trovata");

                                        }
                                    }
                                } else if (piano == 2) {
                                    try {
                                        System.out.println("inserisci zona partenza : ");
                                        String partenzaInput = input.nextLine();
                                        System.out.println("inserisci capolinea : ");
                                        String capolineaInput = input.nextLine();
                                        List<Tratta> buddy = trDAO.getTempoMedioByPartenzaCapolinea(partenzaInput, capolineaInput);
                                        buddy.forEach(System.out::println);
                                    } catch (Exception e) {
                                        System.err.println(e.getMessage());
                                    }
                                } else if (piano == 0) {
                                    System.out.println("Torno indietro");

                                }
                            }
                            case 9 -> {
                                try {

                                    Tessera tesseraUser = tDAO.getTesseraByUserId(user1.getId());
                                    if (tesseraUser != null) {
                                        System.out.println("La tua tessera : " + tesseraUser);
                                        tDAO.isTesseraScadutaById(tesseraUser.getId());
                                    } else {
                                        System.err.println("Non hai una tessera , vai a farla");
                                    }
                                } catch (NullPointerException nullE) {
                                    System.err.println(nullE.getMessage());
                                }
                            }
                        }
                    }
                    while (n != 0);

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
                        System.out.println("---------------------------------------------------------------------------------MENU CUSTOMER---------------------------------------------------------------------------------");
                        System.out.println("                                                          Bentornato " + user.getNome() + " " + user.getCognome() + ", scegli una aziona da fare");
                        do {
                            System.out.println(" ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                            System.out.println("- 1 compra un biglietto; - 2 compra un abbonamento; - 3 compra una tessera; - 4 verificha se hai una tessera; - 5 valida un biglietto; - 6 verifica se il tuo abbonamento è scaduto; ");
                            System.out.println("                         - 7 controlla i rivenditori in una zona; - 8 vedi il tempo medio di una tratta; - 9 rinnova la tua tessera; - 0 termina il programma ");
                            n = Integer.parseInt(input.nextLine().trim());
                            switch (n) {
                                case 0 -> {
                                    input.close();
                                    em.close();
                                    JpaUtils.close();
                                }
                                case 1 -> {
                                    try {
                                        int nVenditoreRandom = new Random().nextInt(1, allSellersSize);
                                        Biglietti buddy = new Biglietti(LocalDate.now(), user, allSellers.get(nVenditoreRandom));
                                        System.out.println("questo è il tuo biglietto : ");
                                        bDAO.save(buddy);
                                        System.out.println("l'id del tuo biglietto è : " + buddy.getId());
                                    } catch (Exception e) {
                                        System.err.println(e.getMessage());
                                    }
                                }
                                case 2 -> {
                                    int piano = 0;
                                    List<Abbonamenti> userAbb = aDAO.getAbbonamentoByUserId(user.getId());
                                    if (userAbb.stream().anyMatch(abbonamento -> abbonamento.getDataScadenza().isAfter(LocalDate.now()))) {
                                        System.out.println("Hai già un abbonamento valido");
                                    } else {
                                        while (piano != 1 && piano != 2) {
                                            System.out.println("Inserisci 1 per comprare il piano mensile, 2 per il piano settimanale: ");
                                            try {
                                                piano = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                                                if (piano != 1 && piano != 2) {
                                                    System.err.println("Inserisci un valore valido tra 1 o 2");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.err.println("Inserisci un valore numerico valido tra 1 o 2");
                                            }
                                            if (piano == 1) {
                                                try {
                                                    int nVenditoreRandom = new Random().nextInt(1, allSellersSize);
                                                    Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.MENSILE, user, allSellers.get(nVenditoreRandom));
                                                    aDAO.save(buddy);
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            } else if (piano == 2) {
                                                try {
                                                    int nVenditoreRandom = new Random().nextInt(1, allSellersSize);
                                                    Abbonamenti buddy = new Abbonamenti(TipoAbbonamento.SETTIMANALE, user, allSellers.get(nVenditoreRandom));
                                                    aDAO.save(buddy);
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            } else {
                                                System.err.println("Errore ID utente non trovato");
                                            }
                                        }
                                    }
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
                                        List<Biglietti> bigliettoNonValidato = bDAO.findNonValidatedTicketForUser(currentUserId);
                                        if (!bigliettoNonValidato.isEmpty()) {
                                            bDAO.validateTicket(mDAO, bigliettoNonValidato.get(0));
                                            System.out.println(bigliettoNonValidato.get(0));
                                        } else {
                                            System.out.println("Nessun biglietto non validato trovato , comprane uno nuovo");
                                        }
                                    }
                                }
                                case 6 -> {
                                    if (currentUserId != 0) {
                                        List<Abbonamenti> abbonamentoUtente = aDAO.getAbbonamentoByUserId(currentUserId);
                                        if (abbonamentoUtente != null) {
                                            int num = 0;
                                            abbonamentoUtente.forEach(abbonamento -> aDAO.isAbbonamentoScaduto(abbonamento));
                                        } else {
                                            System.out.println("Non hai un abbonamento, vai a farlo");
                                        }
                                    }
                                }
                                case 7 -> {
                                    System.out.println("Inserisci una via per controllare quali Rivenditori ci sono: ");
                                    String viaInput = input.nextLine().trim();
                                    //////////////CONTROLLO SE LA VIA è vuota////////////////////
                                    if (viaInput.isEmpty()) {
                                        System.err.println("inserisci una via valida.");
                                    } else {
                                        List<Venditore> listaVenditoriInZona = vDAO.getVenditoriInZona(viaInput);
                                        if (listaVenditoriInZona.isEmpty()) {
                                            System.err.println("Nessun Rivenditore trovato in questa zona o la via non è valida.");
                                        } else {
                                            listaVenditoriInZona.forEach(System.out::println);
                                        }
                                    }
                                }
                                case 8 -> {
                                    int piano = -1;
                                    while (piano != 1 && piano != 2 && piano != 0) {
                                        System.out.println("inserisci 1 per vedere il tempo medio tramite id, 2 per vedere il tempo medio scrivendto punto di partenza e capolinea | 0 per tornare indietro : ");
                                        try {
                                            piano = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                                            if (piano != 1 && piano != 2 && piano != 0) {
                                                System.err.println("Inserisci un valore numerico valido 1 o 2 | 0 per tornare indietro");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.err.println("Inserisci un valore numerico valido 1 o 2 | 0 per tornare indietro");
                                        }
                                    }
                                    if (piano == 1) {
                                        long trattaid = 0;
                                        while (true) {
                                            try {
                                                System.out.println("inserisci l'id della tratta per vedere il tempo medio : ");
                                                trattaid = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                if (trattaid > 0) {
                                                    Tratta buddy = trDAO.getTempoMedioById(trattaid);
                                                    System.out.println("Tempo stimato : " + buddy.getTempoMedio());
                                                    break;
                                                } else {
                                                    System.err.println("Nessuna tratta trovata");

                                                }
                                            } catch (Exception e) {
                                                System.err.println("Nessuna tratta trovata");

                                            }
                                        }
                                    } else if (piano == 2) {
                                        try {
                                            System.out.println("inserisci zona partenza : ");
                                            String partenzaInput = input.nextLine();
                                            System.out.println("inserisci capolinea : ");
                                            String capolineaInput = input.nextLine();
                                            List<Tratta> buddy = trDAO.getTempoMedioByPartenzaCapolinea(partenzaInput, capolineaInput);
                                            buddy.forEach(System.out::println);
                                        } catch (Exception e) {
                                            System.err.println(e.getMessage());
                                        }
                                    } else if (piano == 0) {
                                        System.out.println("Torno indietro");
                                    }
                                }
                                case 9 -> {
                                    try {
                                        Tessera tesseraUser = tDAO.getTesseraByUserId(user.getId());
                                        if (tesseraUser != null) {
                                            System.out.println("La tua tessera : " + tesseraUser);
                                            tDAO.isTesseraScadutaById(tesseraUser.getId());
                                        } else {
                                            System.err.println("Non hai una tessera , vai a farla");
                                        }
                                    } catch (NullPointerException nullE) {
                                        System.err.println(nullE.getMessage());
                                    }
                                }
                            }
                        }
                        while (n != 0);
                    } else {
                        System.out.println("---------------------------------------------------------------------------------MENU ADMIN---------------------------------------------------------------------------------");
                        System.out.println("                                                        Bentornato " + user.getNome() + " " + user.getCognome() + ", scegli una aziona da fare");
                        do {
                            System.out.println(" ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                            System.out.println("Scegli un'azione da svolgere:");
                            System.out.println("1 - Gestisci il reparto mezzi; 2 - Gestisci il reparto vendite; 3 - Gestisci tratte; 4 - Ottieni informazioni sullo stato della compagnia; 0 - Chiudi il terminale.");
                            try {
                                n2 = Integer.parseInt(input.nextLine().trim());
                                if (n2 < 0 || n2 > 5) System.err.println("Inserisci un valore consentito.");
                            } catch (NumberFormatException ex) {
                                System.err.println("Il valore inserito non è un numero.");
                            } catch (Exception ex) {
                                System.err.println("Problema generico");
                            }
                            switch (n2) {
                                case 1 -> {
                                    int m = 0;
                                    do {
                                        System.out.println("Scegli un'azione da svolgere:");
                                        System.out.println("1 - Acquista un nuovo mezzo; 2 - Gestisci lo stato di un mezzo; 3 - Vendi un mezzo; 0 - Torna indietro.");
                                        try {
                                            m = Integer.parseInt(input.nextLine().trim());
                                            if (m < 0 || m > 4) System.err.println("Inserisci un valore consentito.");
                                        } catch (NumberFormatException ex) {
                                            System.err.println("Il valore inserito non è un numero.");
                                        } catch (Exception ex) {
                                            System.err.println("Problema generico");
                                        }
                                        switch (m) {
                                            case 1 -> {
                                                //  acquistaMezzoDiTrasporto(input);
                                                // Controllore(input);
                                                int k = 0;
                                                do {
                                                    System.err.println("Che tipo di mezzo vuoi acquistare?");
                                                    System.out.println("1 - TRAM; 2 - AUTOBUS; 3 - Ottieni info sui modelli disponibili; 0 - Torna indietro;");
                                                    try {
                                                        k = Integer.parseInt(input.nextLine().trim());
                                                        if (k < 0 || k > 4)
                                                            System.err.println("Inserisci un valore consentito.");
                                                    } catch (NumberFormatException ex) {
                                                        System.err.println("Il valore inserito non è un numero.");
                                                    } catch (Exception ex) {
                                                        System.err.println("Problema generico");
                                                    }
                                                    switch (k) {
                                                        case 1 -> {
                                                            Mezzi mezzo = new Mezzi(TipoMezzo.TRAM);
                                                            mDAO.save(mezzo);
                                                            System.err.println("Mezzo acquistato con successo.");
                                                            System.out.println(mezzo);
                                                        }
                                                        case 2 -> {
                                                            Mezzi mezzo = new Mezzi(TipoMezzo.AUTOBUS);
                                                            mDAO.save(mezzo);
                                                            System.err.println("Mezzo acquistato con successo.");
                                                            System.out.println(mezzo);
                                                        }
                                                        case 3 -> {
                                                            System.out.println("In catalogo sono disponibili un modello di TRAM dalla capienza di 53 posti e un modello di AUTOBUS dalla capienza di 89 posti.");
                                                        }
                                                        case 0 -> {
                                                            System.out.println("Torno indietro");
                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                            System.out.println(".");
                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                            System.out.println("..");
                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                            System.out.println("...");
                                                        }
                                                    }
                                                } while (k < 2 || k > 4);
                                            }
                                            case 2 -> {
                                                try {
                                                    System.err.println("Lista dei mezzi in servizio:");
                                                    List<Mezzi> listaMezziInServizio = mDAO.getAllOnService();
                                                    listaMezziInServizio.forEach(System.out::println);
                                                    System.err.println("Lista dei mezzi in manutenzione:");
                                                    List<Mezzi> listaMezziInManutenzione = mDAO.getAllUnderMaintenance();
                                                    listaMezziInManutenzione.forEach(System.out::println);
                                                    long mezzo_id = 0;
                                                    do {
                                                        System.err.println("Scegli l'id di un mezzo dalle liste sopra per cambiare il suo stato.");
                                                        try {
                                                            mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                    } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                    if (mDAO.getById(mezzo_id) != null) {
                                                        Mezzi mezzo = mDAO.getById(mezzo_id);
                                                        if (mezzo.getStatoMezzo() == StatoMezzo.IN_SERVIZIO) {
                                                            mDAO.findByIdAndUpdateState(mezzo_id, StatoMezzo.IN_MANUTENZIONE);
                                                            Periodi period = new Periodi(LocalDate.now(), null, mezzo);
                                                            pDAO.save(period);
                                                            System.out.println("Il mezzo \n" + mezzo + " ora è in manutenzione.");
                                                        } else {
                                                            mDAO.findByIdAndUpdateState(mezzo_id, StatoMezzo.IN_SERVIZIO);
                                                            mDAO.getLastPeriodForTransportAndUpdate(mezzo_id);
                                                            System.out.println("Il mezzo \n" + mezzo + " ora è in servizio.");
                                                        }
                                                    } else {
                                                        throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                    }
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 3 -> {
                                                try {
                                                    System.err.println("Lista dei mezzi in servizio:");
                                                    List<Mezzi> listaMezziInServizio = mDAO.getAllOnService();
                                                    listaMezziInServizio.forEach(System.out::println);
                                                    System.err.println("Lista dei mezzi in manutenzione:");
                                                    List<Mezzi> listaMezziInManutenzione = mDAO.getAllUnderMaintenance();
                                                    listaMezziInManutenzione.forEach(System.out::println);
                                                    long mezzo_id = 0;
                                                    do {
                                                        System.err.println("Scegli l'id di un mezzo dalle liste sopra per venderlo.");
                                                        try {
                                                            mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                    } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                    if (mDAO.getById(mezzo_id) != null) {
                                                        mDAO.delete(mezzo_id);
                                                        System.err.println("Mezzo venduto correttamente.");
                                                    } else {
                                                        throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                    }
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 0 -> {
                                                System.out.println("Torno indietro");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println(".");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("..");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("...");
                                            }
                                        }
                                    } while (m != 0);
                                }
                                case 2 -> {
                                    int m = 0;
                                    do {
                                        System.out.println("Scegli un'azione da svolgere:");
                                        System.out.println("1 - Acquista un nuovo distributore; 2 - Gestisci lo stato di un distributore; 4 - Abilita un nuovo rivenditore; 5 - Disabilita un rivenditore; 0 - Torna indietro.");
                                        try {
                                            m = Integer.parseInt(input.nextLine().trim());
                                            if (m < 0 || m > 6) System.err.println("Inserisci un valore consentito.");
                                        } catch (NumberFormatException ex) {
                                            System.err.println("Il valore inserito non è un numero.");
                                        } catch (Exception ex) {
                                            System.err.println("Problema generico");
                                        }
                                        switch (m) {
                                            case 1 -> {
                                                try {
                                                    vDAO.save(new Distributore());
                                                    System.err.println("Distributore acquistato correttamente.");
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 2 -> {
                                                try {
                                                    System.err.println("Lista dei distributori in servizio:");
                                                    List<Venditore> listaDistributoriInServizio = vDAO.getAllDistributoriAttivi();
                                                    listaDistributoriInServizio.forEach(System.out::println);
                                                    System.err.println("Lista dei distributori in manutenzione:");
                                                    List<Venditore> listaDistributoriFuoriServizio = vDAO.getAllDistributoriFuoriServizio();
                                                    listaDistributoriFuoriServizio.forEach(System.out::println);
                                                    long distributore_id = 0;
                                                    do {
                                                        System.err.println("Scegli l'id di un distributore dalle liste sopra per venderlo.");
                                                        try {
                                                            distributore_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (distributore_id < 1000000000000L || distributore_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                    } while (distributore_id < 1000000000000L || distributore_id >= 10000000000000L);
                                                    if (vDAO.getById(distributore_id) != null) {
                                                        try {
                                                            Distributore distributore = (Distributore) vDAO.getById(distributore_id);
                                                            System.out.println("Hai selezionato:\n" + distributore);
                                                            if (distributore.getStato() == StatoDistributore.ATTIVO) {
                                                                vDAO.updateStatoDistributore(distributore_id, StatoDistributore.FUORISERVIZIO);
                                                            } else {
                                                                vDAO.updateStatoDistributore(distributore_id, StatoDistributore.ATTIVO);
                                                            }
                                                            System.err.println("Risultato operazione");
                                                            System.out.println(vDAO.getById(distributore_id));
                                                        } catch (ClassCastException e) {
                                                            System.err.println("Hai inserito l'id di un rivenditore. Inserisci quello di un distributore.");
                                                        }
                                                    } else {
                                                        throw new Exception("Nessuna corrispondenza tra id inserito e distributori nel nostro database.");
                                                    }
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 3 -> {
                                                try {
                                                    System.out.println("Inserisci l'indirizzo del rivenditore che si desidera abilitare.");
                                                    String address = input.nextLine();
                                                    Rivenditore rivenditore = new Rivenditore(address);
                                                    vDAO.save(rivenditore);
                                                    System.out.println("Hai abilitato il seguente rivenditore " + rivenditore);
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 4 -> {
                                                try {
                                                    System.err.println("Lista rivenditori abilitati - biglietti venduti.");
                                                    List<Object[]> list = vDAO.getRivenditoriEBigliettiVenduti();
                                                    for (Object[] el : list) {
                                                        Venditore venditore = (Venditore) el[0];
                                                        long biglietti = (Long) el[1];
                                                        System.out.println(venditore + " - " + biglietti);
                                                    }
                                                    long venditore_id = 0;
                                                    do {
                                                        System.out.println("Inserisci l'id del rivenditore che si desidera disabilitare.");
                                                        try {
                                                            venditore_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (venditore_id < 1000000000000L || venditore_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                        if (vDAO.getById(venditore_id) != null) {
                                                            try {
                                                                Rivenditore venditore = (Rivenditore) vDAO.getById(venditore_id);
                                                                vDAO.delete(venditore_id);
                                                                System.err.println("Rivenditore disabilitato con successo.");
                                                            } catch (ClassCastException e) {
                                                                System.err.println("Hai inserito l'id di un distributore. Inserisci quello di un rivenditore.");
                                                            }
                                                        } else {
                                                            throw new Exception("Nessuna corrispondenza tra id inserito e rivenditori nel nostro database.");
                                                        }
                                                    } while (venditore_id < 1000000000000L || venditore_id >= 10000000000000L);
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 0 -> {
                                                System.out.println("Torno indietro");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println(".");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("..");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("...");
                                            }
                                        }
                                    } while (m != 0);
                                }
                                case 3 -> {
                                    int m = 0;
                                    do {
                                        System.out.println("Scegli un'azione da svolgere:");
                                        System.out.println("1 - Aggiungi una tratta; 2 - Elimina una tratta; 3 - Assegna tratta ad un mezzo; 0 - Torna indietro.");
                                        try {
                                            m = Integer.parseInt(input.nextLine().trim());
                                            if (m < 0 || m > 4) System.err.println("Inserisci un valore consentito.");
                                        } catch (NumberFormatException ex) {
                                            System.err.println("Il valore inserito non è un numero.");
                                        } catch (Exception ex) {
                                            System.err.println("Problema generico");
                                        }
                                        switch (m) {
                                            case 1 -> {
                                                try {
                                                    System.out.println("Inserisci città di partenza:");
                                                    String partenza = input.nextLine().trim();
                                                    System.out.println("Inserisci il capolinea:");
                                                    String capolinea = input.nextLine().trim();
                                                    Tratta tratta = new Tratta(partenza, capolinea, Double.parseDouble(new DecimalFormat("0.0").format(new Random().nextDouble(0.1, 2)).replaceAll(",", ".")));
                                                    trDAO.save(tratta);
                                                    System.out.println(tratta);
                                                    System.err.println("Tratta salvata con successo.");
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 2 -> {
                                                try {
                                                    System.err.println("Lista tratte:");
                                                    trDAO.gettAllRoutes().forEach(System.out::println);
                                                    long tratta_id = 0;
                                                    do {
                                                        System.out.println("Scegli l'id della tratta che vuoi eliminare.");
                                                        try {
                                                            tratta_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (tratta_id < 1000000000000L || tratta_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                    } while (tratta_id < 1000000000000L || tratta_id >= 10000000000000L);
                                                    if (trDAO.getById(tratta_id) != null) {
                                                        trDAO.delete(tratta_id);
                                                        System.err.println("Tratta eliminata con successo.");
                                                    } else {
                                                        throw new Exception("Nessuna corrispondenza tra id inserito e tratte nel nostro database.");
                                                    }
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 3 -> {
                                                try {
                                                    System.err.println("Lista tratte:");
                                                    trDAO.gettAllRoutes().forEach(System.out::println);
                                                    long tratta_id = 0;
                                                    do {
                                                        System.out.println("Scegli l'id dalla lista di tratte sopra per assegnarla ad un mezzo.");
                                                        try {
                                                            tratta_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (tratta_id < 1000000000000L || tratta_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                    } while (tratta_id < 1000000000000L || tratta_id >= 10000000000000L);
                                                    if (trDAO.getById(tratta_id) != null) {
                                                        Tratta tratta = trDAO.getById(tratta_id);
                                                        try {
                                                            System.err.println("Lista dei mezzi in servizio:");
                                                            mDAO.getAllOnService().forEach(System.out::println);
                                                            long mezzo_id = 0;
                                                            do {
                                                                System.out.println("Scegli l'id di un mezzo dalla lista sopra.");
                                                                try {
                                                                    mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                    if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                        System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                    }
                                                                } catch (NumberFormatException ex) {
                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                } catch (Exception ex) {
                                                                    System.err.println("Problema generico");
                                                                }
                                                            } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                            if (mDAO.getById(mezzo_id) != null) {
                                                                try {
                                                                    Mezzi mezzo = mDAO.getById(mezzo_id);
                                                                    Tratta_Mezzo trattaMezzo = new Tratta_Mezzo(Double.parseDouble(new DecimalFormat("0.0").format(new Random().nextDouble(0.1, 2)).replaceAll(",", ".")), mezzo, tratta);
                                                                    tr_m_DAO.save(trattaMezzo);
                                                                    System.err.println("Tratta assegnata correttamente.");
                                                                } catch (Exception e) {
                                                                    System.err.println(e.getMessage());
                                                                }
                                                            } else {
                                                                throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                            }
                                                        } catch (Exception e) {
                                                            System.err.println(e.getMessage());
                                                        }
                                                    } else {
                                                        throw new Exception("Nessuna corrispondenza tra id inserito e tratte nel nostro database.");
                                                    }
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 0 -> {
                                                System.out.println("Torno indietro");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println(".");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("..");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("...");
                                            }
                                        }
                                    } while (m != 0);
                                    try {
                                        LocalDate date1 = ottieniData(input);
                                        LocalDate date2 = ottieniData(input);
                                        long mezzo_id = 0;
                                        do {
                                            System.out.println("Inserisci l'id di un mezzo");
                                            try {
                                                mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                }
                                            } catch (NumberFormatException ex) {
                                                System.err.println("Il valore inserito non è un numero.");
                                            } catch (Exception ex) {
                                                System.err.println("Problema generico");
                                            }
                                        } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                        if (mDAO.getById(mezzo_id) != null) {
                                            Mezzi mezzo = mDAO.getById(mezzo_id);
                                            long numeroBigliettiValidati = mDAO.getBigliettiVidimatiPerMezzoPerPeriodo(mezzo_id, date1, date2);
                                            if (date1.isBefore(date2)) {
                                                System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati vidimati " + numeroBigliettiValidati + " biglietti nel mezzo \n" + mezzo);
                                            } else {
                                                System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati vidimati " + numeroBigliettiValidati + " biglietti nel mezzo \n" + mezzo);
                                            }
                                        } else {
                                            throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                        }
                                    } catch (Exception e) {
                                        System.err.println(e.getMessage());
                                    }
                                }
                                case 4 -> {
                                    int m = 0;
                                    do {
                                        System.out.println("Scegli una categoria:");
                                        System.out.println("1 - Info biglietti e abbonamenti; 2 - Info andamento corse; 3 - Lista periodo manutenzione mezzi; 0 - Torna indietro.");
                                        try {
                                            m = Integer.parseInt(input.nextLine().trim());
                                            if (m < 0 || m > 4) System.err.println("Inserisci un valore consentito.");
                                        } catch (NumberFormatException ex) {
                                            System.err.println("Il valore inserito non è un numero.");
                                        } catch (Exception ex) {
                                            System.err.println("Problema generico");
                                        }
                                        switch (m) {
                                            case 1 -> {
                                                int k = 0;
                                                do {
                                                    System.out.println("Scegli un'informazione:");
                                                    System.out.println("1 - Biglietti emessi; 2 - Biglietti vidimati; 3 - Abbonamenti emessi; 0 - Torna indietro.");
                                                    try {
                                                        k = Integer.parseInt(input.nextLine().trim());
                                                        if (k < 0 || k > 4)
                                                            System.err.println("Inserisci un valore consentito.");
                                                    } catch (NumberFormatException ex) {
                                                        System.err.println("Il valore inserito non è un numero.");
                                                    } catch (Exception ex) {
                                                        System.err.println("Problema generico");
                                                    }
                                                    switch (k) {
                                                        case 1 -> {
                                                            int l = 0;
                                                            do {
                                                                System.out.println("Vuoi selezionare un periodo?");
                                                                System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                try {
                                                                    l = Integer.parseInt(input.nextLine().trim());
                                                                    if (l < 0 || l > 3)
                                                                        System.err.println("Inserisci un valore consentito.");
                                                                } catch (NumberFormatException ex) {
                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                } catch (Exception ex) {
                                                                    System.err.println("Problema generico");
                                                                }
                                                                switch (l) {
                                                                    case 1 -> {
                                                                        LocalDate date1 = ottieniData(input);
                                                                        LocalDate date2 = ottieniData(input);
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi selezionare un venditore?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                        } while (j < 0 || j > 3);
                                                                        switch (j) {
                                                                            case 1 -> {
                                                                                try {
                                                                                    System.err.println("Lista dei venditori:");
                                                                                    List<Venditore> listaDistributoriInServizio = vDAO.getAllSellers();
                                                                                    listaDistributoriInServizio.forEach(System.out::println);
                                                                                    long venditore_id = 0;
                                                                                    do {
                                                                                        System.err.println("Scegli l'id di un venditore dalla lista sopra.");
                                                                                        try {
                                                                                            venditore_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                            if (venditore_id < 1000000000000L || venditore_id >= 10000000000000L) {
                                                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                            }
                                                                                        } catch (
                                                                                                NumberFormatException ex) {
                                                                                            System.err.println("Il valore inserito non è un numero.");
                                                                                        } catch (Exception ex) {
                                                                                            System.err.println("Problema generico");
                                                                                        }
                                                                                    } while (venditore_id < 1000000000000L || venditore_id >= 10000000000000L);
                                                                                    if (vDAO.getById(venditore_id) != null) {
                                                                                        Venditore venditore = vDAO.getById(venditore_id);
                                                                                        long numberOfTickets = bDAO.getNumberOfTicketsInTimeIntervallForSeller(date1, date1, venditore);
                                                                                        if (date1.isBefore(date2)) {
                                                                                            System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati rilasciati " + numberOfTickets + " biglietti dal \n" + venditore);
                                                                                        } else {
                                                                                            System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati rilasciati " + numberOfTickets + " biglietti dal \n" + venditore);
                                                                                        }
                                                                                    } else {
                                                                                        throw new Exception("Nessuna corrispondenza tra id inserito e venditori nel nostro database.");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    System.err.println(e.getMessage());
                                                                                }
                                                                            }
                                                                            case 2 -> {
                                                                                try {
                                                                                    long numberOfTickets = bDAO.getNumberOfTicketsInTimeIntervall(date1, date2);
                                                                                    if (date1.isBefore(date2)) {
                                                                                        System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati emessi " + numberOfTickets + "biglietti.");
                                                                                    } else {
                                                                                        System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati emessi " + numberOfTickets + "biglietti.");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    System.err.println(e.getMessage());
                                                                                }
                                                                            }
                                                                            case 0 -> {
                                                                                System.out.println("Torno indietro");
                                                                                TimeUnit.MILLISECONDS.sleep(500);
                                                                                System.out.println(".");
                                                                                TimeUnit.MILLISECONDS.sleep(500);
                                                                                System.out.println("..");
                                                                                TimeUnit.MILLISECONDS.sleep(500);
                                                                                System.out.println("...");
                                                                            }
                                                                        }
                                                                    }
                                                                    case 2 -> {
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi selezionare un venditore?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                            switch (j) {
                                                                                case 1 -> {
                                                                                    try {
                                                                                        System.err.println("Lista dei venditori:");
                                                                                        List<Venditore> listaVenditori = vDAO.getAllSellers();
                                                                                        listaVenditori.forEach(System.out::println);
                                                                                        long venditore_id = 0;
                                                                                        do {
                                                                                            System.err.println("Scegli l'id di un venditore dalla lista sopra.");
                                                                                            try {
                                                                                                venditore_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                if (venditore_id < 1000000000000L || venditore_id >= 10000000000000L) {
                                                                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                }
                                                                                            } catch (
                                                                                                    NumberFormatException ex) {
                                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                                            } catch (Exception ex) {
                                                                                                System.err.println("Problema generico");
                                                                                            }
                                                                                        } while (venditore_id < 1000000000000L || venditore_id >= 10000000000000L);
                                                                                        if (vDAO.getById(venditore_id) != null) {
                                                                                            Venditore venditore = vDAO.getById(venditore_id);
                                                                                            long numberOfTickets = bDAO.getAllSelledTicketsForSeller(venditore_id);
                                                                                            System.out.println("In totale sono stati venduti " + numberOfTickets + " biglietti dal \n" + venditore);
                                                                                        } else {
                                                                                            throw new Exception("Nessuna corrispondenza tra id inserito e venditori nel nostro database.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 2 -> {
                                                                                    try {
                                                                                        long numberOfTickets = bDAO.getAllSelledTickets();
                                                                                        System.out.println("In totale sono stati emessi " + numberOfTickets + " biglietti.");
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 0 -> {
                                                                                    System.out.println("Torno indietro");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println(".");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("..");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("...");
                                                                                }
                                                                            }
                                                                        } while (j < 0 || j > 3);
                                                                    }
                                                                    case 0 -> {
                                                                        System.out.println("Torno indietro");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println(".");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println("..");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println("...");
                                                                    }
                                                                }
                                                            } while (l < 0 || l > 3);
                                                        }
                                                        case 2 -> {
                                                            int l = 0;
                                                            do {
                                                                System.out.println("Vuoi selezionare un periodo?");
                                                                System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                try {
                                                                    l = Integer.parseInt(input.nextLine().trim());
                                                                    if (l < 0 || l > 3)
                                                                        System.err.println("Inserisci un valore consentito.");
                                                                } catch (NumberFormatException ex) {
                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                } catch (Exception ex) {
                                                                    System.err.println("Problema generico");
                                                                }
                                                                switch (l) {
                                                                    case 1 -> {
                                                                        LocalDate date1 = ottieniData(input);
                                                                        LocalDate date2 = ottieniData(input);
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi selezionare un mezzo?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                            switch (j) {
                                                                                case 1 -> {
                                                                                    try {
                                                                                        System.err.println("Lista mezzi della compagnia:");
                                                                                        mDAO.getAll().forEach(System.out::println);
                                                                                        long mezzo_id = 0;
                                                                                        do {
                                                                                            System.out.println("Inserisci l'id di un mezzo dalla lista sopra.");
                                                                                            try {
                                                                                                mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                }
                                                                                            } catch (
                                                                                                    NumberFormatException ex) {
                                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                                            } catch (Exception ex) {
                                                                                                System.err.println("Problema generico");
                                                                                            }
                                                                                        } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                                                        if (mDAO.getById(mezzo_id) != null) {
                                                                                            Mezzi mezzo = mDAO.getById(mezzo_id);
                                                                                            long numberOfTickets = bDAO.bigliettiValidatiSuUnMezzoPerIntervallo(date1, date2, mezzo_id);
                                                                                            if (date1.isBefore(date2)) {
                                                                                                System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati vidimati " + numberOfTickets + " biglietti nel mezzo \n" + mezzo);
                                                                                            } else {
                                                                                                System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati rilasciati " + numberOfTickets + " biglietti nel mezzo \n" + mezzo);
                                                                                            }
                                                                                        } else {
                                                                                            throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 2 -> {
                                                                                    try {
                                                                                        long numberOfTickets = bDAO.bigliettiValidatiSuUnMezzoPerIntervallo(date1, date2);
                                                                                        if (date1.isBefore(date2)) {
                                                                                            System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati vidimati " + numberOfTickets + " biglietti in totale.");
                                                                                        } else {
                                                                                            System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati rilasciati " + numberOfTickets + " biglietti in totale.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 0 -> {
                                                                                    System.out.println("Torno indietro");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println(".");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("..");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("...");
                                                                                }
                                                                            }
                                                                        } while (j < 0 || j > 3);
                                                                    }
                                                                    case 2 -> {
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi selezionare un mezzo?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                            switch (j) {
                                                                                case 1 -> {
                                                                                    try {
                                                                                        System.err.println("Lista mezzi della compagnia:");
                                                                                        mDAO.getAll().forEach(System.out::println);
                                                                                        long mezzo_id = 0;
                                                                                        do {
                                                                                            System.out.println("Inserisci l'id di un mezzo dalla lista sopra.");
                                                                                            try {
                                                                                                mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                }
                                                                                            } catch (
                                                                                                    NumberFormatException ex) {
                                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                                            } catch (Exception ex) {
                                                                                                System.err.println("Problema generico");
                                                                                            }
                                                                                        } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                                                        if (mDAO.getById(mezzo_id) != null) {
                                                                                            Mezzi mezzo = mDAO.getById(mezzo_id);
                                                                                            long numberOfTickets = bDAO.bigliettiValidatiSuUnMezzo(mezzo_id);
                                                                                            System.out.println("In totale sono stati vidimati " + numberOfTickets + " biglietti nel mezzo \n" + mezzo);

                                                                                        } else {
                                                                                            throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 2 -> {
                                                                                    try {
                                                                                        long numberOfTickets = bDAO.getAllValidatedTickets();
                                                                                        System.out.println("In totale sono stati vidimati " + numberOfTickets + " biglietti.");
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 0 -> {
                                                                                    System.out.println("Torno indietro");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println(".");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("..");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("...");
                                                                                }
                                                                            }
                                                                        } while (j < 0 || j > 3);
                                                                    }
                                                                }
                                                            } while (l < 0 || l > 3);
                                                        }
                                                        case 3 -> {
                                                            int l = 0;
                                                            do {
                                                                System.out.println("Vuoi selezionare un periodo?");
                                                                System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                try {
                                                                    l = Integer.parseInt(input.nextLine().trim());
                                                                    if (l < 0 || l > 3)
                                                                        System.err.println("Inserisci un valore consentito.");
                                                                } catch (NumberFormatException ex) {
                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                } catch (Exception ex) {
                                                                    System.err.println("Problema generico");
                                                                }
                                                                switch (l) {
                                                                    case 1 -> {
                                                                        LocalDate date1 = ottieniData(input);
                                                                        LocalDate date2 = ottieniData(input);
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi selezionare un venditore?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                            switch (j) {
                                                                                case 1 -> {
                                                                                    try {
                                                                                        System.err.println("Lista dei venditori:");
                                                                                        List<Venditore> listaDistributoriInServizio = vDAO.getAllSellers();
                                                                                        listaDistributoriInServizio.forEach(System.out::println);
                                                                                        long venditore_id = 0;
                                                                                        do {
                                                                                            System.err.println("Scegli l'id di un venditore dalla lista sopra.");
                                                                                            try {
                                                                                                venditore_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                if (venditore_id < 1000000000000L || venditore_id >= 10000000000000L) {
                                                                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                }
                                                                                            } catch (
                                                                                                    NumberFormatException ex) {
                                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                                            } catch (Exception ex) {
                                                                                                System.err.println("Problema generico");
                                                                                            }
                                                                                        } while (venditore_id < 1000000000000L || venditore_id >= 10000000000000L);
                                                                                        if (vDAO.getById(venditore_id) != null) {
                                                                                            Venditore venditore = vDAO.getById(venditore_id);
                                                                                            long numberOfTickets = bDAO.getNumberOfAbbonamentiInTimeIntervallForSeller(date1, date1, venditore);
                                                                                            if (date1.isBefore(date2)) {
                                                                                                System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati rilasciati " + numberOfTickets + " abbonamenti dal \n" + venditore);
                                                                                            } else {
                                                                                                System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati rilasciati " + numberOfTickets + " abbonamenti dal \n" + venditore);
                                                                                            }
                                                                                        } else {
                                                                                            throw new Exception("Nessuna corrispondenza tra id inserito e venditori nel nostro database.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 2 -> {
                                                                                    try {
                                                                                        long numberOfTickets = bDAO.getNumberOfAbbonamentiInTimeIntervall(date1, date2);
                                                                                        if (date1.isBefore(date2)) {
                                                                                            System.out.println("Tra il " + date1 + " e il " + date2 + " sono stati emessi " + numberOfTickets + "abbonamenti.");
                                                                                        } else {
                                                                                            System.out.println("Tra il " + date2 + " e il " + date1 + " sono stati emessi " + numberOfTickets + "abbonamenti.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 0 -> {
                                                                                    System.out.println("Torno indietro");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println(".");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("..");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("...");
                                                                                }
                                                                            }
                                                                        } while (j != 0);
                                                                    }
                                                                    case 2 -> {
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi selezionare un venditore?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                            switch (j) {
                                                                                case 1 -> {
                                                                                    try {
                                                                                        System.err.println("Lista dei venditori:");
                                                                                        List<Venditore> listaVenditori = vDAO.getAllSellers();
                                                                                        listaVenditori.forEach(System.out::println);
                                                                                        long venditore_id = 0;
                                                                                        do {
                                                                                            System.err.println("Scegli l'id di un venditore dalla lista sopra.");
                                                                                            try {
                                                                                                venditore_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                if (venditore_id < 1000000000000L || venditore_id >= 10000000000000L) {
                                                                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                }
                                                                                            } catch (
                                                                                                    NumberFormatException ex) {
                                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                                            } catch (Exception ex) {
                                                                                                System.err.println("Problema generico");
                                                                                            }
                                                                                        } while (venditore_id < 1000000000000L || venditore_id >= 10000000000000L);
                                                                                        if (vDAO.getById(venditore_id) != null) {
                                                                                            Venditore venditore = vDAO.getById(venditore_id);
                                                                                            long numberOfTickets = bDAO.getAllSelledAbbonamentiForSeller(venditore_id);
                                                                                            System.out.println("In totale sono stati venduti " + numberOfTickets + " abbonamenti dal \n" + venditore);
                                                                                        } else {
                                                                                            throw new Exception("Nessuna corrispondenza tra id inserito e venditori nel nostro database.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 2 -> {
                                                                                    try {
                                                                                        long numberOfTickets = bDAO.getAllSelledAbbonamenti();
                                                                                        System.out.println("In totale sono stati emessi " + numberOfTickets + " abbonamenti.");
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 0 -> {
                                                                                    System.out.println("Torno indietro");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println(".");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("..");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("...");
                                                                                }
                                                                            }
                                                                        } while (j != 0);
                                                                    }
                                                                    case 0 -> {
                                                                        System.out.println("Torno indietro");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println(".");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println("..");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println("...");
                                                                    }
                                                                }
                                                            } while (l != 0);
                                                        }
                                                    }
                                                } while (k != 0);
                                            }
                                            case 2 -> {
                                                int l = 0;
                                                do {
                                                    System.out.println("Che tipo di informazioni vuoi ottenere?:");
                                                    System.out.println("1 - Ottieni tempi effettivi delle corse; 2 - Ottieni numero di corse; 0 - Torna indietro.");
                                                    try {
                                                        l = Integer.parseInt(input.nextLine().trim());
                                                        if (l < 0 || l > 3)
                                                            System.err.println("Inserisci un valore consentito.");
                                                    } catch (NumberFormatException ex) {
                                                        System.err.println("Il valore inserito non è un numero.");
                                                    } catch (Exception ex) {
                                                        System.err.println("Problema generico");
                                                    }
                                                    switch (l) {
                                                        case 1 -> {
                                                            try {
                                                                long mezzo_id = 0;
                                                                do {
                                                                    System.out.println("Inserisci l'id di un mezzo");
                                                                    try {
                                                                        mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                        if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                            System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                        }
                                                                    } catch (NumberFormatException ex) {
                                                                        System.err.println("Il valore inserito non è un numero.");
                                                                    } catch (Exception ex) {
                                                                        System.err.println("Problema generico");
                                                                    }
                                                                } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                                if (mDAO.getById(mezzo_id) != null) {
                                                                    Mezzi mezzo = mDAO.getById(mezzo_id);
                                                                    System.err.println("Ecco la lista delle tratte percorse dal mezzo:");
                                                                    List<Long> trette_id = new ArrayList<>();
                                                                    mezzo.getTratteMezzo().forEach(tratta_mezzo -> {
                                                                        if (!trette_id.contains(tratta_mezzo.getTratta().getId())) {
                                                                            trette_id.add(tratta_mezzo.getTratta().getId());
                                                                            System.out.println(tratta_mezzo.getTratta());
                                                                        }
                                                                    });
                                                                    long tratta_id = 0;
                                                                    do {
                                                                        System.out.println("Ora inserisci l'id di una tratta");
                                                                        try {
                                                                            tratta_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                            if (tratta_id < 1000000000000L || tratta_id >= 10000000000000L) {
                                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                            }
                                                                        } catch (NumberFormatException ex) {
                                                                            System.err.println("Il valore inserito non è un numero.");
                                                                        } catch (Exception ex) {
                                                                            System.err.println("Problema generico");
                                                                        }
                                                                    } while (tratta_id < 1000000000000L || tratta_id >= 10000000000000L);
                                                                    if (trette_id.contains(tratta_id)) {
                                                                        List<Double> listaTempi = trDAO.getTimeTrattaPercorsaBySingleMezzo(tratta_id, mezzo_id);
                                                                        listaTempi.forEach(System.out::println);
                                                                    } else {
                                                                        System.err.println("Il mezzo non ha mai percorso questa tratta o quest'ultima non esiste. \n Scegline una dalla lista proposta.");
                                                                    }
                                                                } else {
                                                                    throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                                }
                                                            } catch (Exception e) {
                                                                System.err.println(e.getMessage());
                                                            }
                                                        }
                                                        case 2 -> {
                                                            int k = 0;
                                                            do {
                                                                System.out.println("Vuoi filtrare i risultati inserendo un mezzo?");
                                                                System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                try {
                                                                    k = Integer.parseInt(input.nextLine().trim());
                                                                    if (k < 0 || k > 3)
                                                                        System.err.println("Inserisci un valore consentito.");
                                                                } catch (NumberFormatException ex) {
                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                } catch (Exception ex) {
                                                                    System.err.println("Problema generico");
                                                                }
                                                                switch (k) {
                                                                    case 1 -> {
                                                                        try {
                                                                            long mezzo_id = 0;
                                                                            do {
                                                                                System.out.println("Inserisci l'id di un mezzo");
                                                                                try {
                                                                                    mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                    if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                                        System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                    }
                                                                                } catch (NumberFormatException ex) {
                                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                                } catch (Exception ex) {
                                                                                    System.err.println("Problema generico");
                                                                                }
                                                                            } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                                            if (mDAO.getById(mezzo_id) != null) {
                                                                                Mezzi mezzo = mDAO.getById(mezzo_id);
                                                                                int j = 0;
                                                                                do {
                                                                                    System.out.println("Vuoi filtrare i risultati inserendo una tratta?");
                                                                                    System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                                    try {
                                                                                        j = Integer.parseInt(input.nextLine().trim());
                                                                                        if (j < 0 || j > 3)
                                                                                            System.err.println("Inserisci un valore consentito.");
                                                                                    } catch (NumberFormatException ex) {
                                                                                        System.err.println("Il valore inserito non è un numero.");
                                                                                    } catch (Exception ex) {
                                                                                        System.err.println("Problema generico");
                                                                                    }
                                                                                    switch (j) {
                                                                                        case 1 -> {
                                                                                            long tratta_id = 0;
                                                                                            do {
                                                                                                System.out.println("Ora inserisci l'id di una tratta");
                                                                                                try {
                                                                                                    tratta_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                    if (tratta_id < 1000000000000L || tratta_id >= 10000000000000L) {
                                                                                                        System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                    }
                                                                                                } catch (
                                                                                                        NumberFormatException ex) {
                                                                                                    System.err.println("Il valore inserito non è un numero.");
                                                                                                } catch (Exception ex) {
                                                                                                    System.err.println("Problema generico");
                                                                                                }
                                                                                            } while (tratta_id < 1000000000000L || tratta_id >= 10000000000000L);
                                                                                            if (tDAO.getById(tratta_id) != null) {
                                                                                                Tratta tratta = trDAO.getById(tratta_id);
                                                                                                Long numeroVolte = tr_m_DAO.getNumVolteMezzoPercorsoTratta(mezzo_id, tratta_id);
                                                                                                System.out.println("il mezzo: " + mezzo);
                                                                                                System.out.println("ha percorso la tratta : " + tratta);
                                                                                                System.out.println(numeroVolte + "  volte.");
                                                                                            } else {
                                                                                                throw new Exception("Nessuna corrispondenza tra id inserito e tratta nel nostro database.");
                                                                                            }
                                                                                        }
                                                                                        case 2 -> {
                                                                                            Long numeroVolte = tr_m_DAO.getNumVolteMezzo(mezzo_id);
                                                                                            System.out.println("Il mezzo: " + mezzo + "\n ha effettuato " + numeroVolte + " corse.");
                                                                                        }
                                                                                        case 0 -> {
                                                                                            System.out.println("Torno indietro");
                                                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                                                            System.out.println(".");
                                                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                                                            System.out.println("..");
                                                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                                                            System.out.println("...");
                                                                                        }
                                                                                    }
                                                                                }
                                                                                while (j != 0);
                                                                            } else {
                                                                                throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                                            }
                                                                        } catch (Exception e) {
                                                                            System.err.println(e.getMessage());
                                                                        }
                                                                    }
                                                                    case 2 -> {
                                                                        int j = 0;
                                                                        do {
                                                                            System.out.println("Vuoi filtrare i risultati inserendo una tratta?");
                                                                            System.out.println("1 - Sì; 2 - No; 0 - Torna indietro.");
                                                                            try {
                                                                                j = Integer.parseInt(input.nextLine().trim());
                                                                                if (j < 0 || j > 3)
                                                                                    System.err.println("Inserisci un valore consentito.");
                                                                            } catch (NumberFormatException ex) {
                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                            } catch (Exception ex) {
                                                                                System.err.println("Problema generico");
                                                                            }
                                                                            switch (j) {
                                                                                case 1 -> {
                                                                                    long tratta_id = 0;
                                                                                    try {
                                                                                        do {
                                                                                            System.out.println("Ora inserisci l'id di una tratta");
                                                                                            try {
                                                                                                tratta_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                                                                if (tratta_id < 1000000000000L || tratta_id >= 10000000000000L) {
                                                                                                    System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                                                                }
                                                                                            } catch (
                                                                                                    NumberFormatException ex) {
                                                                                                System.err.println("Il valore inserito non è un numero.");
                                                                                            } catch (Exception ex) {
                                                                                                System.err.println("Problema generico");
                                                                                            }
                                                                                        } while (tratta_id < 1000000000000L || tratta_id >= 10000000000000L);
                                                                                        if (tDAO.getById(tratta_id) != null) {
                                                                                            Tratta tratta = trDAO.getById(tratta_id);
                                                                                            long numeroVolte = tr_m_DAO.getNumVolteTratta(tratta_id);
                                                                                            System.out.println("La tratta : " + tratta);
                                                                                            System.out.println("è stata percorsa " + numeroVolte + "  volte.");
                                                                                        } else {
                                                                                            throw new Exception("Nessuna corrispondenza tra id inserito e tratta nel nostro database.");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        System.err.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                                case 2 -> {
                                                                                    long numeroVolte = tr_m_DAO.getAll();
                                                                                    System.out.println("In totale sono state effettuate " + numeroVolte + " corse.");
                                                                                }
                                                                                case 0 -> {
                                                                                    System.out.println("Torno indietro");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println(".");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("..");
                                                                                    TimeUnit.MILLISECONDS.sleep(500);
                                                                                    System.out.println("...");
                                                                                }
                                                                            }
                                                                        } while (j != 0);
                                                                    }
                                                                    case 0 -> {
                                                                        System.out.println("Torno indietro");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println(".");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println("..");
                                                                        TimeUnit.MILLISECONDS.sleep(500);
                                                                        System.out.println("...");
                                                                    }
                                                                }
                                                            }
                                                            while (k != 0);
                                                        }
                                                        case 0 -> {
                                                            System.out.println("Torno indietro");
                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                            System.out.println(".");
                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                            System.out.println("..");
                                                            TimeUnit.MILLISECONDS.sleep(500);
                                                            System.out.println("...");
                                                        }
                                                    }
                                                } while (l != 0);
                                            }
                                            case 3 -> {
                                                try {
                                                    long mezzo_id = 0;
                                                    do {
                                                        System.out.println("Inserisci l'id di un mezzo");
                                                        try {
                                                            mezzo_id = Long.parseLong(input.nextLine().trim().replaceAll(" ", ""));
                                                            if (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L) {
                                                                System.err.println("Inserisci un id valido (un codice di 13 cifre)");
                                                            }
                                                        } catch (NumberFormatException ex) {
                                                            System.err.println("Il valore inserito non è un numero.");
                                                        } catch (Exception ex) {
                                                            System.err.println("Problema generico");
                                                        }
                                                    } while (mezzo_id < 1000000000000L || mezzo_id >= 10000000000000L);
                                                    if (mDAO.getById(mezzo_id) != null) {
                                                        List<Periodi> listaPeriodi = mDAO.getPeriodListForTransport(mezzo_id);
                                                        if (!listaPeriodi.isEmpty()) {
                                                            System.err.println("Lista periodi:");
                                                            listaPeriodi.forEach(System.out::println);
                                                        } else {
                                                            System.err.println("Il mezzo non è mai stato in manutenzione.");
                                                        }
                                                    } else {
                                                        throw new Exception("Nessuna corrispondenza tra id inserito e mezzi nel nostro database.");
                                                    }
                                                } catch (Exception e) {
                                                    System.err.println(e.getMessage());
                                                }
                                            }
                                            case 0 -> {
                                                System.out.println("Torno indietro");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println(".");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("..");
                                                TimeUnit.MILLISECONDS.sleep(500);
                                                System.out.println("...");
                                            }
                                        }
                                    }
                                    while (m != 0);
                                }
                                case 0 -> {
                                    System.out.println("Spegnimento");
                                    TimeUnit.MILLISECONDS.sleep(500);
                                    System.out.println(".");
                                    TimeUnit.MILLISECONDS.sleep(500);
                                    System.out.println("..");
                                    TimeUnit.MILLISECONDS.sleep(500);
                                    System.out.println("...");
                                    System.err.println("Spento.");
                                    input.close();
                                }
                            }
                        }
                        while (n2 != 0);
                    }
                } else {
                    System.err.println("ID utente non trovato, assicurati di aver inserito l'id corretto!");
                }
            }
        }
    }

    public static boolean verificaAnnoBisestile(int anno) {
        return ((anno % 400 == 0) || (anno % 4 == 0 && anno % 100 != 0));
    }

    public static LocalDate ottieniData(Scanner input) {
        int year1 = 0;
        do {
            System.out.println("Inserisci l'anno nel formato yyyy");
            try {
                year1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                if (year1 <= 0) {
                    System.err.println("Inserisci un anno valido.");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Il valore inserito non è un numero.");
            } catch (Exception ex) {
                System.err.println("Problema generico");
            }
        } while (year1 <= 0);
        int month1 = 0;
        do {
            System.out.println("Inserisci il mese nel formato mm (un valore tra 1 e 12)");
            try {
                month1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                if (month1 <= 0 || month1 > 12) {
                    System.err.println("Inserisci un mese valido.");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Il valore inserito non è un numero.");
            } catch (Exception ex) {
                System.err.println("Problema generico");
            }
        } while (month1 <= 0 || month1 > 12);
        int day1 = 0;
        if (verificaAnnoBisestile(year1)) {
            if (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11) {
                do {
                    System.out.println("Inserisci il giorno nel formato gg (un valore tra 1 e 30)");
                    try {
                        day1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        if (day1 <= 0 || day1 > 30) {
                            System.err.println("Inserisci un giorno valido.");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Il valore inserito non è un numero.");
                    } catch (Exception ex) {
                        System.err.println("Problema generico");
                    }
                } while (day1 <= 0 || day1 > 30);
            } else if (month1 == 2) {
                do {
                    System.out.println("Inserisci il giorno nel formato gg (un valore tra 1 e 29)");
                    try {
                        day1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        if (day1 <= 0 || day1 > 29) {
                            System.err.println("Inserisci un giorno valido.");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Il valore inserito non è un numero.");
                    } catch (Exception ex) {
                        System.err.println("Problema generico");
                    }
                } while (day1 <= 0 || day1 > 29);
            } else {
                do {
                    System.out.println("Inserisci il giorno nel formato gg (un valore tra 1 e 31)");
                    try {
                        day1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        if (day1 <= 0 || day1 > 31) {
                            System.err.println("Inserisci un giorno valido.");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Il valore inserito non è un numero.");
                    } catch (Exception ex) {
                        System.err.println("Problema generico");
                    }
                } while (day1 <= 0 || day1 > 31);
            }
        } else {
            if (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11) {
                do {
                    System.out.println("Inserisci il giorno nel formato gg (un valore tra 1 e 30)");
                    try {
                        day1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        if (day1 <= 0 || day1 > 30) {
                            System.err.println("Inserisci un giorno valido.");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Il valore inserito non è un numero.");
                    } catch (Exception ex) {
                        System.err.println("Problema generico");
                    }
                } while (day1 <= 0 || day1 > 30);
            } else if (month1 == 2) {
                do {
                    System.out.println("Inserisci il giorno nel formato gg (un valore tra 1 e 28)");
                    try {
                        day1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        if (day1 <= 0 || day1 > 28) {
                            System.err.println("Inserisci un giorno valido.");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Il valore inserito non è un numero.");
                    } catch (Exception ex) {
                        System.err.println("Problema generico");
                    }
                } while (day1 <= 0 || day1 > 28);
            } else {
                do {
                    System.out.println("Inserisci il giorno nel formato gg (un valore tra 1 e 31)");
                    try {
                        day1 = Integer.parseInt(input.nextLine().trim().replaceAll(" ", ""));
                        if (day1 <= 0 || day1 > 31) {
                            System.err.println("Inserisci un giorno valido.");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Il valore inserito non è un numero.");
                    } catch (Exception ex) {
                        System.err.println("Problema generico");
                    }
                } while (day1 <= 0 || day1 > 31);
            }
        }
        return LocalDate.of(year1, month1, day1);
    }

    public static void acquistaAutobus() {
        System.out.println("Hai acquistato un nuovo autobus!");
    }

    public static void acquistaTram() {
        System.out.println("Hai acquistato un nuovo tram!");
    }

    public static void acquistaMezzoDiTrasporto(Scanner input) {

        int scelta = 0;
        do {
            System.out.println("Scegli il mezzo di trasporto da acquistare:");
            System.out.println("1. Autobus");
            System.out.println("2. Tram");
            System.out.println("0. Termina");
            try {
                scelta = Integer.parseInt(input.nextLine().trim());
                if (scelta < 0 || scelta > 3) System.err.println("Inserisci un valore consentito.");
            } catch (NumberFormatException ex) {
                System.err.println("Il valore inserito non è un numero.");
            } catch (Exception ex) {
                System.err.println("Problema generico");
            }
            switch (scelta) {
                case 1:
                    acquistaAutobus();
                    break;
                case 2:
                    acquistaTram();
                    break;
                case 0:
                    System.out.println("Annulla l'acquisto.");
            }
        } while (scelta < 0 || scelta > 3);
    }

    public static void bigliettoValido() {
        System.out.println("Passeggero con biglietto valido!");
    }

    public static void abbonamentoValido() {
        System.out.println("Passeggero con abbonamento valido!");
    }

    public static void abbonamentoScaduto() {
        System.out.println("Passeggero con abbonamento scaduto!");
    }

    public static void Controllore(Scanner input) {

        int scelta = 0;
        do {
            System.out.println("Seleziona un'opzione: ");
            System.out.println("1. Passeggero con biglietto valido!");
            System.out.println("2. Passeggero con abbonamento valido!");
            System.out.println("3. Passeggero con abbonamento scaduto!");
            System.out.println("Inserisci la tua scelta: ");
            try {
                scelta = Integer.parseInt(input.nextLine().trim());
                if (scelta < 0 || scelta > 4) System.err.println("Inserisci un valore consentito.");
            } catch (NumberFormatException ex) {
                System.err.println("Il valore inserito non è un numero.");
            } catch (Exception ex) {
                System.err.println("Problema generico");
            }
            switch (scelta) {
                case 1:
                    bigliettoValido();
                    break;
                case 2:
                    abbonamentoValido();
                    break;
                case 3:
                    abbonamentoScaduto();
                case 0:
                    System.out.println("Annulla l'acquisto.");
            }
        } while (scelta < 0 || scelta > 4);
    }
}
