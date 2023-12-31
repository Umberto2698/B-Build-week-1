package enteties;

import enums.TipoUser;

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
    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private TipoUser tipoUser = TipoUser.CUSTOMER;
    @OneToOne(mappedBy = "user")
    private Tessera tessera;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Biglietti> biglietti;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Abbonamenti> abbonamenti;

    public User(String nome, String cognome, LocalDate dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
    }

    public User(String nome, String cognome, LocalDate dataNascita, TipoUser tipoUser) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.tipoUser = tipoUser;
    }

    public User() {
    }

    public long getId() {
        return id;
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

    public TipoUser getTipoUser() {
        return tipoUser;
    }

    public List<Biglietti> getBiglietti() {
        return biglietti;
    }

    public List<Abbonamenti> getAbbonamenti() {
        return abbonamenti;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", dataNascita=" + dataNascita +
                ", tessera=" + tessera +
                '}';
    }
}
