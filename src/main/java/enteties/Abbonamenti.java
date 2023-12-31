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
    @Enumerated(EnumType.STRING)
    private TipoAbbonamento tipoAbbonamento;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "venditore_id", nullable = false)
    private Venditore venditore;

    public Abbonamenti() {
    }

    public Abbonamenti(TipoAbbonamento tipoAbbonamento, LocalDate dataEmissione, User user, Venditore venditore) {
        this.tipoAbbonamento = tipoAbbonamento;
        this.dataEmissione = dataEmissione;
        switch (tipoAbbonamento) {
            case MENSILE -> {
                this.costo = 139.90;
                this.dataScadenza = dataEmissione.plusMonths(1);
                this.user = user;
                this.venditore = venditore;
            }
            case SETTIMANALE -> {
                this.costo = 39.90;
                this.dataScadenza = dataEmissione.plusDays(7);
                this.user = user;
                this.venditore = venditore;
            }
        }
    }

    public Abbonamenti(TipoAbbonamento tipoAbbonamento, User user, Venditore venditore) {
        this.tipoAbbonamento = tipoAbbonamento;
        switch (tipoAbbonamento) {
            case MENSILE -> {
                this.costo = 139.90;
                this.dataScadenza = LocalDate.now().plusMonths(1);
                this.user = user;
                this.venditore = venditore;
            }
            case SETTIMANALE -> {
                this.costo = 39.90;
                this.dataScadenza = LocalDate.now().plusDays(7);
                this.user = user;
                this.venditore = venditore;
            }
        }
    }

    @Override
    public String toString() {
        return "Abbonamenti{" +
                "id=" + id +
                ", costo=" + costo +
                ", dataEmissione=" + dataEmissione +
                ", dataScadenza=" + dataScadenza +
                ", tipoAbbonamento=" + tipoAbbonamento +
                ", user=" + user +
                ", venditore=" + venditore +
                '}';
    }

    public long getId() {
        return id;
    }

    public double getCosto() {
        return costo;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public TipoAbbonamento getTipoAbbonamento() {
        return tipoAbbonamento;
    }

    public User getUser() {
        return user;
    }

    public Venditore getVenditore() {
        return venditore;
    }


}
