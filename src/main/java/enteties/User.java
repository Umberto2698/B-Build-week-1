package enteties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Entity
public class User {
    @Id
    private long user_id= new Random().nextLong(500000L,100000000000L);
    @Column(name = "name")
    private String nome;
    @Column(name = "surname")
    private String cognome;
    @Column(name = "data_nascita")
    private LocalDate dataNascita;
    @OneToOne
    @JoinColumn(name = "tessera_id")
    private Tessera tessera;



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

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }
}
