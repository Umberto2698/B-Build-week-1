package enteties;

import javax.persistence.*;
import java.util.Random;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "seller")
public abstract class Venditore {
    @Id
    long id = new Random().nextLong(1000000000000L, 10000000000000L);

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
