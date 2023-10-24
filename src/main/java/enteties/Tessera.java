package enteties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
@Table(name = "cards")
public class Tessera {
    @Id
    private long id = new Random().nextLong(100000000000L, 1000000000000L);
    @Column(name = "emission_date")
    private LocalDate dataEmissione = LocalDate.now();
    @Column(name = "expiry_date")
    private LocalDate dataScadenza = LocalDate.now().plusYears(1);
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Tessera() {
    }

//    public Tessera(LocalDate dataEmissione, User user) {
//        this.dataEmissione = dataEmissione;
//        this.dataScadenza = dataEmissione.plusYears(1);
//        this.user = user;
//    }


    public Tessera(User user) {
        this.user = user;
    }

    public Tessera(LocalDate dataEmissione, LocalDate dataScadenza, User user) {
        this.dataEmissione = dataEmissione;
        this.dataScadenza = dataScadenza;
        this.user = user;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    @Override
    public String toString() {
        return "Tessera{" +
                "id=" + id +
                ", dataEmissione=" + dataEmissione +
                ", dataScadenza=" + dataScadenza +
                '}';
    }
}
