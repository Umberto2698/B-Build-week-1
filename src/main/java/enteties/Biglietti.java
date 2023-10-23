package enteties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;


@Entity
public class Biglietti {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "price")
    private double prezzo = 1.20;
    @Column(name = "emission_date")
    private LocalDate dataEmissione = LocalDate.now();
    @Column(name = "validation_name")
    private LocalDate dataValidazione = null;
    @ManyToOne
    @JoinColumn(name = "IdPuntoVendita")
    private Venditore IdPuntoVendita;
    public Biglietti (Venditore venditore) {
        this.IdPuntoVendita = venditore;
    }
    public Biglietti() {
    }
}
