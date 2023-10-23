package enteties;

import javax.persistence.*;

@Entity
@Table(name = "trnsport_route")
public class Tratta_Mezzo {
    @Column(name = "actual_time")
    private double tempoEffettivo;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Mezzi mezzo;

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
