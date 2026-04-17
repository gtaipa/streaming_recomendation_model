package streaming.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Artist extends Entity {

    private String name;
    private String nationality;
    private LocalDate birthDate;
    private String gender;

    private List<ArtistRole> artistRoles;
    private List<Movie> movies;

    public Artist(String id, LocalDateTime createdAt, String name, String nationality, LocalDate birthDate, String gender) {
        super(id, createdAt);
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
        this.gender = gender;
        this.artistRoles = new ArrayList<>();
        this.movies = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        if (birthDate != null) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
        return 0;
    }

    public List<ArtistRole> getArtistRoles() {
        return artistRoles;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    @Override
    public String toString() {
        return "Artist: " + name;
    }
}