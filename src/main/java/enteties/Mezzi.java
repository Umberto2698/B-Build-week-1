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
    private TipoMezzo tipoMezzo;

    @Column(name = "transport_state")
    private StatoMezzo statoMezzo = StatoMezzo.IN_SERVIZIO;

    @Column(name = "total_seats")
    private int postiTotali;

    @OneToMany(mappedBy = "mezzi", cascade = CascadeType.REMOVE)
    private Set<Tratta_Mezzo> tratteMezzo = new HashSet<>();

    public Mezzi() {
    }

    public Mezzi(TipoMezzo tipoMezzo) {
        this.tipoMezzo = tipoMezzo;
        switch (tipoMezzo) {
            case TRAM -> this.postiTotali = 53;
            case AUTOBUS -> this.postiTotali = 89;
        }
    }
}
