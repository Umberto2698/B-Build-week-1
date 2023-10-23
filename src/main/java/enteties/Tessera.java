package enteties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Random;

@Entity
public class Tessera {
    @Id
    private long tessera_id = new Random().nextLong(1000000000000L, 100000000000L);
    @Column(name = "data_emissione")
    private LocalDate dataEmissione;
    @Column(name = "data_scadenza")
    private LocalDate dataScadenza;
    @OneToOne(mappedBy = "tessera")
    private User user;


    public Tessera(LocalDate dataEmissione, LocalDate dataScadenza) {
        this.dataEmissione = dataEmissione;
        this.dataScadenza = dataScadenza;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(LocalDate dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }
}
