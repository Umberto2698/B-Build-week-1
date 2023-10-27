package enteties;

import enums.StatoMezzo;
import enums.TipoMezzo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "transport")
public class Mezzi {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "transport_type")
    @Enumerated(EnumType.STRING)
    private TipoMezzo tipoMezzo;
    @Column(name = "transport_state")
    @Enumerated(EnumType.STRING)
    private StatoMezzo statoMezzo = StatoMezzo.IN_SERVIZIO;
    @Column(name = "total_seats")
    private int postiTotali;

    @OneToMany(mappedBy = "mezzo", cascade = CascadeType.REMOVE)
    private Set<Tratta_Mezzo> tratteMezzo = new HashSet<>();

    @OneToMany(mappedBy = "mezzo", cascade = CascadeType.REMOVE)
    private Set<Biglietti> biglietti = new HashSet<>();

    @OneToMany(mappedBy = "mezzo", cascade = CascadeType.REMOVE)
    private Set<Periodi> periodi = new HashSet<>();

    public Mezzi() {
    }

    public Mezzi(TipoMezzo tipoMezzo) {
        this.tipoMezzo = tipoMezzo;
        switch (tipoMezzo) {
            case TRAM -> this.postiTotali = 53;
            case AUTOBUS -> this.postiTotali = 89;
        }
    }

    public Mezzi(TipoMezzo tipoMezzo, StatoMezzo statoMezzo) {
        this.tipoMezzo = tipoMezzo;
        this.statoMezzo = statoMezzo;
        switch (tipoMezzo) {
            case TRAM -> this.postiTotali = 53;
            case AUTOBUS -> this.postiTotali = 89;
        }
    }

    @Override
    public String toString() {
        return "Mezzo{" +
                "id=" + id +
                ", tipoMezzo=" + tipoMezzo +
                ", statoMezzo=" + statoMezzo +
                ", postiTotali=" + postiTotali +
                '}';
    }

    public long getId() {
        return id;
    }

    public TipoMezzo getTipoMezzo() {
        return tipoMezzo;
    }

    public StatoMezzo getStatoMezzo() {
        return statoMezzo;
    }

    public int getPostiTotali() {
        return postiTotali;
    }

    public Set<Tratta_Mezzo> getTratteMezzo() {
        return tratteMezzo;
    }

    public Set<Biglietti> getBiglietti() {
        return biglietti;
    }

    public Set<Periodi> getPeriodi() {
        return periodi;
    }
}
