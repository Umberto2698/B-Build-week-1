package enteties;

import enums.StatoDistributore;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Distributore extends Venditore {
    @Column(name = "state")
    private StatoDistributore stato = StatoDistributore.ATTIVO;

    public Distributore() {
        super();
    }

    public StatoDistributore getStato() {
        return stato;
    }

    public void setStato(StatoDistributore stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {

        return "enteties.Distributore{" +
                "id=" + getId() +
                "stato=" + stato +
                '}';
    }
}
