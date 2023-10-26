package enteties;


import enums.StatoMezzo;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
@Table(name = "periods")
public class Periodi {
    @ManyToOne
    @JoinColumn(name = "transport_id", nullable = false)
    Mezzi mezzo;
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "start_date")
    private LocalDate dataInizio;
    @Column(name = "end_date")
    private LocalDate dataFine;

    @Column(name = "period_type")
    @Enumerated(EnumType.STRING)
    private StatoMezzo statoMezzo;

    public Periodi() {
    }

    public Periodi(LocalDate dataInizio, LocalDate dataFine, Mezzi mezzo, StatoMezzo statoMezzo) {
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.statoMezzo = statoMezzo;
        this.mezzo = mezzo;
    }

    public Mezzi getMezzo() {
        return mezzo;
    }

    public void setMezzo(Mezzi mezzo) {
        this.mezzo = mezzo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public StatoMezzo getStatoMezzo() {
        return statoMezzo;
    }

    public void setStatoMezzo(StatoMezzo statoMezzo) {
        this.statoMezzo = statoMezzo;
    }

    @Override
    public String toString() {
        return "Periodo{" +
                "id=" + id +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", statoMezzo=" + statoMezzo +
                ", mezzo=" + mezzo +
                '}';
    }
}
