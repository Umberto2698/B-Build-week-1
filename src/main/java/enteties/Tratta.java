package enteties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "route")
public class Tratta {
    @Id
    private long id;

    @Column(name = "starting_place")
    private String zonaPartenza;

    @Column(name = "terminal")
    private String capolinea;

    @Column(name = "average_time")
    private double tempoMedio;

    @OneToMany(mappedBy = "tratta", cascade = CascadeType.REMOVE)
    private Set<Tratta_Mezzo> tratteMezzo = new HashSet<>();

    public Tratta() {
    }

    public Tratta(String zonaPartenza, String capolinea, double tempoMedio) {
        this.id = new Random().nextLong(1000000000000L, 10000000000000L);
        this.zonaPartenza = zonaPartenza;
        this.capolinea = capolinea;
        this.tempoMedio = tempoMedio;
    }

    public long getId() {
        return id;
    }

    public String getZonaPartenza() {
        return zonaPartenza;
    }

    public void setZonaPartenza(String zonaPartenza) {
        this.zonaPartenza = zonaPartenza;
    }

    public String getCapolinea() {
        return capolinea;
    }

    public void setCapolinea(String capolinea) {
        this.capolinea = capolinea;
    }

    public double getTempoMedio() {
        return tempoMedio;
    }

    public void setTempoMedio(double tempoMedio) {
        this.tempoMedio = tempoMedio;
    }

    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", zonaPartenza='" + zonaPartenza + '\'' +
                ", capolinea='" + capolinea + '\'' +
                ", tempoMedio=" + tempoMedio +
                '}';
    }
}
