package streaming.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Filme (conteudo com duracao e realizador).
 */
public class Movie extends Content {

  /** Duracao em minutos. */
  private int duration;

  /** Realizador do filme. */
  private Artist director;

  /** Elenco (cast) do filme. */
  private List<Artist> cast;

  /**
   * Cria um filme.
   *
   * @param id identificador unico
   * @param createdAt data/hora de criacao
   * @param title titulo
   * @param releaseYear ano de lancamento
   * @param genre genero
   * @param region regiao
   * @param duration duracao em minutos
   * @param director realizador
   */
  public Movie(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region,
      int duration, Artist director) {
super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // 0.0 = rating, 0 = views, new ArrayList<>() = artists
    this.duration = duration;
    this.director = director;
    this.cast = new ArrayList<>();
    

  }

  /** @return duracao em minutos */
  public int getDuration() {
    return duration;
  }

  /** @return realizador */
  public Artist getDirector() {
    return director;
  }

  /** @return elenco */
  public List<Artist> getCast() {
    return cast;
  }

  /** @return tipo de conteudo ("Movie") */
  @Override
  public String getContentType() {
    return "Movie";
  }

  /** @return detalhes do filme para exibicao */
  @Override
  public String getDetails() {
    String directorName = (director == null) ? "Unknown" : director.getName();
    return "Duration: " + duration + " min, Director: " + directorName;
  }

  /**
   * Adiciona um artista ao elenco.
   *
   * @param artist artista a adicionar
   */
  public void addCast(Artist artist) {
    this.cast.add(artist);
  }

  @Override
  public String toString() {
    return "Movie: " + getTitle() + " (" + getReleaseYear() + ")";
  }

}
