package enteties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transport_route")
public class Tratta_Mezzo implements Serializable {
    @Column(name = "actual_time")
    private double tempoEffettivo;
    @Id
    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Mezzi mezzo;
    @Id
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Tratta tratta;

    public Tratta_Mezzo() {
    }

    public Tratta_Mezzo(double tempoEffettivo, Mezzi mezzo, Tratta tratta) {
        this.tempoEffettivo = tempoEffettivo;
        this.mezzo = mezzo;
        this.tratta = tratta;
    }

    @Override
    public String toString() {
        return "Tratta_Mezzo{" +
                "tempoEffettivo=" + tempoEffettivo +
                ", mezzo=" + mezzo +
                ", tratta=" + tratta +
                '}';
    }

    public double getTempoEffettivo() {
        return tempoEffettivo;
    }

    public Mezzi getMezzo() {
        return mezzo;
    }

    public Tratta getTratta() {
        return tratta;
    }

}
