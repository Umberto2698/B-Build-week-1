package enteties;

import enums.StatoDistributore;

import javax.persistence.Entity;

@Entity
public class Distributore extends Venditore{
private StatoDistributore stato = StatoDistributore.ATTIVO;

    public Distributore() {
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
