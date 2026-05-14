package streaming.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Conteudo disponivel na plataforma (filme, serie, documentario, ...).
 * <p>
 * Cada subclasse deve indicar o tipo e detalhes especificos.
 */
public abstract class Content extends Entity {

  /** Titulo do conteudo. */
  private String title;

  /** Ano de lancamento. */
  private int releaseYear;

  /** Genero associado ao conteudo. */
  private Genre genre;

  /** Regiao associada ao conteudo (ex.: "PT", "US"). */
  private String region;

  /** Classificacao media (0..10), se aplicavel. */
  private double avgRating;

  /** Total de visualizacoes (numero inteiro). */
  private int totalViews;

  /** Lista de artistas associados ao conteudo (pode estar vazia). */
  private List<Artist> artists;

  /**
   * Construtor base para conteudos.
   *
   * @param id identificador unico
   * @param createdAt data/hora de criacao
   * @param title titulo
   * @param releaseYear ano de lancamento
   * @param genre genero
   * @param region regiao
   * @param avgRating rating medio
   * @param totalViews total de visualizacoes
   * @param artists artistas associados
   */
  protected Content(String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region,
      double avgRating, int totalViews, List<Artist> artists) {
    super(id, createdAt);
    this.title = title;
    this.releaseYear = releaseYear;
    this.genre = genre;
    this.region = region;
    this.avgRating = avgRating;
    this.totalViews = totalViews;
    this.artists = artists;
  }

  /** @return titulo do conteudo */
  public String getTitle() {
    return title;
  }

  /** @return ano de lancamento */
  public int getReleaseYear() {
    return releaseYear;
  }

  /** @return genero associado */
  public Genre getGenre() {
    return genre;
  }

  /** @return regiao do conteudo */
  public String getRegion() {
    return region;
  }

  /** @return rating medio */
  public double getAvgRating() {
    return avgRating;
  }

  /** @return total de visualizacoes */
  public int getTotalViews() {
    return totalViews;
  }

  /** @return lista de artistas associados */
  public List<Artist> getArtists() {
    return artists;
  }

  /**
   * Detalhes especificos do conteudo (ex.: duracao, realizador, status, etc.).
   *
   * @return descricao curta com detalhes do conteudo
   */
  public abstract String getDetails();

  /**
   * Tipo do conteudo para exibicao (ex.: "Movie", "Series", "Documentary").
   *
   * @return tipo do conteudo
   */
  public abstract String getContentType();
}
