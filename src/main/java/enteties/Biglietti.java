package enteties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Random;


@Entity
public class Biglietti {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "cost")
    private double costo = 1.20;
    @Column(name = "emission_date")
    private LocalDate dataEmissione = LocalDate.now();

    @Column(name = "validation_name")
    private LocalDate dataValidazione = null;

    public Biglietti() {
    }

}
