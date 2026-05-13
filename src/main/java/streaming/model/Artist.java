package streaming.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Artista associado a conteudos (ator, realizador, narrador, ...).
 */
public class Artist extends Entity {

    /** Nome do artista. */
    private String name;
    /** Nacionalidade do artista. */
    private String nationality;
    /** Data de nascimento. */
    private LocalDate birthDate;
    /** Genero (texto livre). */
    private String gender;

    /** Lista de papeis exercidos pelo artista. */
    private List<ArtistRole> artistRoles;
    /** Lista de filmes associados ao artista (se aplicavel). */
    private List<Movie> movies;

    /**
     * Cria um artista.
     *
     * @param id identificador unico
     * @param createdAt data/hora de criacao
     * @param name nome
     * @param nationality nacionalidade
     * @param birthDate data de nascimento
     * @param gender genero
     */
    public Artist(String id, LocalDateTime createdAt, String name, String nationality, LocalDate birthDate, String gender) {
        super(id, createdAt);
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
        this.gender = gender;
        this.artistRoles = new ArrayList<>();
        this.movies = new ArrayList<>();
    }

    /** @return nome do artista */
    public String getName() {
        return name;
    }

    /** @return nacionalidade */
    public String getNationality() {
        return nationality;
    }

    /** @return data de nascimento */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /** @return genero */
    public String getGender() {
        return gender;
    }

    /**
     * Calcula a idade do artista em anos.
     *
     * @return idade em anos; 0 se {@code birthDate} for null
     */
    public int getAge() {
        if (birthDate != null) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
        return 0;
    }

    /** @return lista de papeis do artista */
    public List<ArtistRole> getArtistRoles() {
        return artistRoles;
    }

    /** @return lista de filmes associados */
    public List<Movie> getMovies() {
        return movies;
    }

    /** @return representacao textual do artista */
    @Override
    public String toString() {
        return "Artist: " + name;
    }
}
