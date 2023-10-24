package enteties;

import dao.MezziDAO;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;


@Entity
@Table(name = "tickets")
public class Biglietti {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "price")
    private double prezzo = 1.20;
    @Column(name = "emission_date")
    private LocalDate dataEmissione = LocalDate.now();
    @Column(name = "validation_date")
    private LocalDate dataValidazione = null;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Venditore venditore;
    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Mezzi mezzo;

    public Biglietti(Venditore venditore) {
        this.venditore = venditore;
    }

    public Biglietti() {
    }

    public Mezzi getMezzo() {
        return mezzo;
    }

    public void setMezzo(Mezzi mezzo) {
        this.mezzo = mezzo;
    }


    public LocalDate getDataValidazione() {
        return dataValidazione;
    }

    public void setDataValidazione(LocalDate dataValidazione) {
        this.dataValidazione = dataValidazione;
    }

    public void validateTicket(MezziDAO md) {
        List<Mezzi> mezziInServizio = md.getAllOnService().stream().toList();
        int size = mezziInServizio.size();
        int n = new Random().nextInt(1, size);
        this.setMezzo(mezziInServizio.get(n));
        this.setDataValidazione(LocalDate.now());
    }
}
