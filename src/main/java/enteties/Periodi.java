package enteties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
public class Periodi {
    @ManyToOne
    @JoinColumn(name = "mezzo_id", nullable = false)
    Mezzi mezzoPeriodo;
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "start_date")
    private LocalDate dataInizio;
    @Column(name = "end_date")
    private LocalDate dataFine;
}
