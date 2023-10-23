package enteties;

import java.util.Random;

public class Tratta {
    long id;
    String zonaPartenza;
    String capolinea;
    double tempoMedio;

    public Tratta() {
    }

    public Tratta(String zonaPartenza, String capolinea, double tempoMedio) {
        this.id =(new Random().nextLong(1000000000000L, 10000000000000L));
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
        return "enteties.Tratta{" +
                "id=" + id +
                ", zonaPartenza='" + zonaPartenza + '\'' +
                ", capolinea='" + capolinea + '\'' +
                ", tempoMedio=" + tempoMedio +
                '}';
    }
}
