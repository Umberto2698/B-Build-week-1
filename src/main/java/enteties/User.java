package enteties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Entity
public class User {
    @Id
    private long id = new Random().nextLong(1000000000000L, 10000000000000L);
    @Column(name = "name")
    private String nome;
    @Column(name = "surname")
    private String cognome;
    @Column(name = "birthday")
    private LocalDate dataNascita;
    @OneToOne(mappedBy = "user")
    private Tessera tessera;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Abbonamenti> abbonamenti;


    public User(String nome, String cognome, LocalDate dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
    }


    public User() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public Tessera getTessera() {
        return tessera;
    }

    public void setTessera(Tessera tessera) {
        this.tessera = tessera;
    }
}
