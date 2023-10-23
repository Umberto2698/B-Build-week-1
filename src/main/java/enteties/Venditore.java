package enteties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "seller")
public abstract class Venditore {
    @Id
    long id = (new Random().nextLong(1000000000000L, 10000000000000L));
    @OneToMany(mappedBy = "venditore")
    private Set<Abbonamenti> abbonamenti = new HashSet<>();

    @OneToMany(mappedBy = "IdPuntoVendita")
    private Set<Biglietti> biglietti = new HashSet<>();

    public Venditore() {
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Venditore{" +
                "id=" + id +
                '}';
    }
}
