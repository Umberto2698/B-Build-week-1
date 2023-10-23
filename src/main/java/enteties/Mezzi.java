package enteties;

import enums.StatoMezzo;
import enums.TipoMezzo;

import javax.persistence.*;
import java.util.Random;

@Entity
@Table(name = "transport")
public class Mezzi {
    @OneToMany(mappedBy = "mezzo")
    Biglietti biglietto;
    @OneToMany(mappedBy = "mezzoPeriodo")
    Periodi periodo;
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "transport_type")
    private TipoMezzo tipoMezzo;
    @Column(name = "transport_state")
    private StatoMezzo statoMezzo = StatoMezzo.IN_SERVIZIO;
    @Column(name = "total_seats")
    private int postiTotali;

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
