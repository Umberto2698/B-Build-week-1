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
    private StatoMezzo statoMezzo;
}
