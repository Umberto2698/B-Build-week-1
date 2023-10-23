package enteties;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
public class Rivenditore extends Venditore {
    @Column(name = "adress")
    private String adress;


    public Rivenditore() {
        super();
    }

    public Rivenditore(String adress) {
        super();
        this.adress = adress;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    @Override
    public String toString() {

        return "enteties.Rivenditore{" +
                "id=" + getId() +
                "adress" + adress +
                '}';
    }
}
