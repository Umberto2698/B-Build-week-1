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

        Supplier<User> customerSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().between(Date.from(
                                LocalDate.of(1940, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                        , Date.from(LocalDate.of(2017, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Supplier<User> adminSupplier = () -> new User(faker.name().firstName(), faker.name().lastName(), faker.date().between(Date.from(
                                LocalDate.of(1940, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                        , Date.from(LocalDate.of(2017, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), TipoUser.ADMIN);
        Supplier<Rivenditore> rivenditoreSupplier = () -> new Rivenditore(faker.address().streetAddress());
        Supplier<Distributore> distributoreFuoriServizioSupplier = () -> new Distributore(StatoDistributore.FUORISERVIZIO);
        Supplier<Distributore> distributoreAttivoSupplier = () -> new Distributore(StatoDistributore.ATTIVO);
        Supplier<Mezzi> autobusInServizioSupplier = () -> new Mezzi(TipoMezzo.AUTOBUS);
        Supplier<Mezzi> autobusInManutenzioneSupplier = () -> new Mezzi(TipoMezzo.AUTOBUS, StatoMezzo.IN_MANUTENZIONE);
        Supplier<Mezzi> tramInServizioSupplier = () -> new Mezzi(TipoMezzo.TRAM);
        Supplier<Mezzi> tramInManutenzioneSupplier = () -> new Mezzi(TipoMezzo.TRAM, StatoMezzo.IN_MANUTENZIONE);
        Supplier<Tratta> trattaSupplier = () -> new Tratta(faker.address().cityName(), faker.address().cityName(), Double.parseDouble(new DecimalFormat("0.0").format(new Random().nextDouble(0.1, 2)).replaceAll(",", ".")));
        Supplier<Tratta_Mezzo> tratta_mezzoSupplier = () -> {
            List<Mezzi> allTransport = mDAO.getAll();
            int n = new Random().nextInt(1, allTransport.size());
            List<Tratta> allRoutes = trDAO.gettAllRoutes();
            int n1 = new Random().nextInt(1, allRoutes.size());
            return new Tratta_Mezzo(Double.parseDouble(new DecimalFormat("0.0").format(new Random().nextDouble(0.1, 2)).replaceAll(",", ".")), allTransport.get(n), allRoutes.get(n1));
        };

        List<Venditore> allSellers = vDAO.getAllSellers();
        List<User> allUsers = uDAO.getAllUsers();
        int allUsersSize = allUsers.size();
        List<User> allUsersWithValidCard = uDAO.getAllUsersWithValidCard();
        int allUsersWithValidCardsSize = allUsersWithValidCard.size();


        Supplier<Biglietti> bigliettiSupplier = () -> {
            int n = new Random().nextInt(1, allSellers.size());
            int m = new Random().nextInt(1, allUsersSize);
            return new Biglietti(faker.date().between(Date.from(allUsers.get(m).getDataNascita().plusYears(5).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.now().minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(m), allSellers.get(n));
        };

        try {
// ******************************************** PRIMO AVVIO *********************************************
//            for (int i = 0; i < 50; i++) {
//                uDAO.save(customerSupplier.get());
//                vDAO.save(distributoreAttivoSupplier.get());
//                vDAO.save(rivenditoreSupplier.get());
//                trDAO.save(trattaSupplier.get());
//            }
//            for (int i = 0; i < 10; i++) {
//                uDAO.save(adminSupplier.get());
//                vDAO.save(distributoreFuoriServizioSupplier.get());
//                mDAO.save(tramInManutenzioneSupplier.get());
//                mDAO.save(tramInServizioSupplier.get());
//                mDAO.save(autobusInManutenzioneSupplier.get());
//                mDAO.save(autobusInServizioSupplier.get());
//            }
// ******************************************** SECONDO AVVIO *********************************************
            for (int i = 0; i < 200; i++) {
                bDAO.save(bigliettiSupplier.get());
                tr_m_DAO.save(tratta_mezzoSupplier.get());
            }
            for (int i = 0; i < allUsersSize; i++) {
                Tessera tessera = null;
                if (i < 20) {
                    tessera = new Tessera(faker.date().between(Date.from(allUsers.get(i).getDataNascita().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                    , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(i));
                } else if (i >= 20 && i < 30) {
                    tessera = new Tessera(faker.date().between(Date.from(LocalDate.now().minusMonths(8).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                    , Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(i));
                } else if (i >= 30 && i < 40) {
                    tessera = new Tessera(faker.date().between(Date.from(allUsers.get(i).getDataNascita().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                    , Date.from(LocalDate.now().minusYears(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsers.get(i));
                }
                if (tessera != null) {
                    tDAO.save(tessera);
                }
            }
            periodiPerAnno(mDAO, faker, 2010).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2011).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2012).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2013).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2014).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2015).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2016).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2017).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2018).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2019).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2020).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2021).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2022).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerAnno(mDAO, faker, 2023).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            periodiPerMezziAttualmenteInManutenzione(mDAO, faker).forEach(period -> {
                try {
                    pDAO.save(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
// ******************************************** TERZO AVVIO *********************************************
//            abbonamentiMensili(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiMensili(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiMensili(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiMensili(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            abbonamentiSettimanali(uDAO, vDAO, faker).forEach(abbonamento -> {
//                try {
//                    aDAO.save(abbonamento);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            validateTicket(bDAO, mDAO, faker);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
            JpaUtils.close();
            input.close();
        }
    }

    public static List<Periodi> periodiPerAnno(MezziDAO mDAO, Faker faker, int year1) {
        List<Mezzi> allTransports = mDAO.getAll();
        List<Periodi> periods = new ArrayList<>();
        allTransports.forEach(transport -> {
            LocalDate start = faker.date().between(Date.from(LocalDate.of(year1, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.of(year1, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = faker.date().between(Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                            , Date.from(LocalDate.of(year1, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Periodi period = new Periodi(start, end, transport);
            periods.add(period);
        });
        return periods;
    }

    public static List<Periodi> periodiPerMezziAttualmenteInManutenzione(MezziDAO mDAO, Faker faker) {
        List<Mezzi> transportsUnderMaintenance = mDAO.getAllUnderMaintenance();
        List<Periodi> periods = new ArrayList<>();
        transportsUnderMaintenance.forEach(transport -> {
            Periodi period = new Periodi(LocalDate.now(), null, transport);
            periods.add(period);
        });
        return periods;
    }

    public static List<Abbonamenti> abbonamentiSettimanali(UserDAO uDAO, VenditoreDAO vDAO, Faker faker) {
        List<Abbonamenti> abbonamenti = new ArrayList<>();
        List<User> allUsersWithCard = uDAO.getAllUsersWithCard();
        List<Venditore> allSellers = vDAO.getAllSellers();
        for (int i = 0; i < 10; i++) {
            int n = new Random().nextInt(1, allSellers.size());
            int m = new Random().nextInt(1, allUsersWithCard.size());
            if (!allUsersWithCard.get(m).getAbbonamenti().isEmpty()) {
                if (allUsersWithCard.get(m).getAbbonamenti().get(allUsersWithCard.get(m).getAbbonamenti().size() - 1).getDataScadenza().isBefore(allUsersWithCard.get(m).getTessera().getDataScadenza().minusDays(7))) {
                    Abbonamenti abbonamento = new Abbonamenti(TipoAbbonamento.SETTIMANALE, faker.date().between(Date.from(
                                            allUsersWithCard.get(m).getAbbonamenti().get(allUsersWithCard.get(m).getAbbonamenti().size() - 1).getDataScadenza().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                    , Date.from(allUsersWithCard.get(m).getTessera().getDataScadenza().minusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsersWithCard.get(m), allSellers.get(n));
                    abbonamenti.add(abbonamento);
                }
            } else {
                Abbonamenti abbonamento = new Abbonamenti(TipoAbbonamento.SETTIMANALE, faker.date().between(Date.from(allUsersWithCard.get(m).getTessera().getDataEmissione().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                , Date.from(allUsersWithCard.get(m).getTessera().getDataScadenza().minusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsersWithCard.get(m), allSellers.get(n));
                abbonamenti.add(abbonamento);
            }
        }
        return abbonamenti;
    }

    public static List<Abbonamenti> abbonamentiMensili(UserDAO uDAO, VenditoreDAO vDAO, Faker faker) {
        List<Abbonamenti> abbonamenti = new ArrayList<>();
        List<User> allUsersWithCard = uDAO.getAllUsersWithCard();
        List<Venditore> allSellers = vDAO.getAllSellers();
        for (int i = 0; i < 10; i++) {
            int n = new Random().nextInt(1, allSellers.size());
            int m = new Random().nextInt(1, allUsersWithCard.size());
            if (!allUsersWithCard.get(m).getAbbonamenti().isEmpty()) {
                if (allUsersWithCard.get(m).getAbbonamenti().get(allUsersWithCard.get(m).getAbbonamenti().size() - 1).getDataScadenza().isBefore(allUsersWithCard.get(m).getTessera().getDataScadenza().minusMonths(1))) {
                    Abbonamenti abbonamento = new Abbonamenti(TipoAbbonamento.MENSILE, faker.date().between(Date.from(
                                            allUsersWithCard.get(m).getAbbonamenti().get(allUsersWithCard.get(m).getAbbonamenti().size() - 1).getDataEmissione().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                    , Date.from(allUsersWithCard.get(m).getTessera().getDataScadenza().minusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsersWithCard.get(m), allSellers.get(n));
                    abbonamenti.add(abbonamento);
                }
            } else {
                Abbonamenti abbonamento = new Abbonamenti(TipoAbbonamento.MENSILE, faker.date().between(Date.from(allUsersWithCard.get(m).getTessera().getDataEmissione().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                                , Date.from(allUsersWithCard.get(m).getTessera().getDataScadenza().minusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), allUsersWithCard.get(m), allSellers.get(n));
                abbonamenti.add(abbonamento);
            }
        }
        return abbonamenti;
    }

    public static void validateTicket(BigliettiDAO bDAO, MezziDAO mDAO, Faker faker) {
        List<Biglietti> allTickets = bDAO.getAllTickets();
        List<Mezzi> allTransports = mDAO.getAll();
        for (int i = 0; i < allTickets.size(); i++) {
            if (i < 150) {
                for (int j = 0; j < allTransports.size(); j++) {
                    int n = new Random().nextInt(0, allTransports.size());
                    List<Periodi> validPeriods = mDAO.getPeriodListForTransport(allTransports.get(n).getId()).stream().filter(period -> period.getDataFine() != null).toList();
                    int finalI = i;
                    List<Periodi> allConflictedPeriods = new ArrayList<>();
                    List<Periodi> conflictedPeriods1 = validPeriods.stream().filter(period -> period.getDataInizio().isBefore(allTickets.get(finalI).getDataEmissione())).toList()
                            .stream().filter(period -> period.getDataFine().isBefore(LocalDate.of(allTickets.get(finalI).getDataEmissione().getYear(), 12, 30))).toList();
                    allConflictedPeriods.addAll(conflictedPeriods1);
                    List<Periodi> conflictedPeriods2 = validPeriods.stream().filter(period -> period.getDataInizio().isAfter(allTickets.get(finalI).getDataEmissione())).toList()
                            .stream().filter(period -> period.getDataFine().isBefore(LocalDate.of(allTickets.get(finalI).getDataEmissione().getYear(), 12, 30))).toList();
                    allConflictedPeriods.addAll(conflictedPeriods2);
                    List<Periodi> conflictedPeriods3 = validPeriods.stream().filter(period -> period.getDataInizio().isAfter(allTickets.get(finalI).getDataEmissione())).toList()
                            .stream().filter(period -> period.getDataFine().isAfter(LocalDate.of(allTickets.get(finalI).getDataEmissione().getYear(), 12, 30))).toList();
                    allConflictedPeriods.addAll(conflictedPeriods3);

                    LocalDate randomDataValidazione = faker.date().between(Date.from(allTickets.get(i).getDataEmissione().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                                    Date.from(LocalDate.of(allTickets.get(i).getDataEmissione().getYear(), 12, 30).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (allConflictedPeriods.isEmpty()) {
                        bDAO.validateTicketWithTransport(allTransports.get(n), allTickets.get(i), randomDataValidazione);
                    } else {
                        for (int k = 0; k < allConflictedPeriods.size(); k++) {
                            if (!(randomDataValidazione.isAfter(allConflictedPeriods.get(k).getDataInizio()) && randomDataValidazione.isBefore(allConflictedPeriods.get(k).getDataFine()))) {
                                bDAO.validateTicketWithTransport(allTransports.get(n), allTickets.get(i), randomDataValidazione);
                            }
                        }
                    }
                }
            }
        }
    }
}
