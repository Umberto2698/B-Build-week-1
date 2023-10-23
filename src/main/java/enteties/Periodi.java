package enteties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Random;

@Entity
public class Periodi {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);

    @Column(name = "start_date")
    private LocalDate dataInizio;

    @Column(name = "end_date")
    private LocalDate dataFine;
}
