package enteties;

import enums.TipoAbbonamento;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
public class Abbonamenti {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "cost")
    private double costo;
    @Column(name = "emission_date")
    private LocalDate dataEmissione = LocalDate.now();

    @Column(name = "expiration_date")
    private LocalDate dataScadenza;

    @Column(name = "pass_type")
    private TipoAbbonamento tipoAbbonamento;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "venditore_id", nullable = false)
    private Venditore venditore;

    public Abbonamenti() {
    }

    public Abbonamenti(TipoAbbonamento tipoAbbonamento) {
        this.tipoAbbonamento = tipoAbbonamento;
        switch (tipoAbbonamento) {
            case MENSILE -> {
                this.costo = 139.90;
                this.dataScadenza = LocalDate.now().plusMonths(1);
            }
            case SETTIMANALE -> {
                this.costo = 39.90;
                this.dataScadenza = LocalDate.now().plusDays(7);
            }
        }
    }
}
