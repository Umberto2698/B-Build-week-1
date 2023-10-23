package enteties;

import javax.persistence.Entity;

@Entity
public class Rivenditore extends Venditore{
    String adress;

    public Rivenditore() {
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
                "id=" + id +
                "adress" + adress +
                '}';
    }
}
