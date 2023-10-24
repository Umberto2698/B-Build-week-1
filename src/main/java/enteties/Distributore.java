package enteties;

import enums.StatoDistributore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Distributore extends Venditore {
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private StatoDistributore stato = StatoDistributore.ATTIVO;

    public Distributore() {
        super();
    }

    public Distributore(StatoDistributore stato) {
        this.stato = stato;
    }

    public StatoDistributore getStato() {
        return stato;
    }

    public void setStato(StatoDistributore stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {

        return "Distributore{" +
                "id=" + getId() +
                ", stato=" + stato +
                '}';
    }
}
