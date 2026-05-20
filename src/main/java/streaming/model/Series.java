package streaming.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serie (conteudo com temporadas, episodios e estado).
 */
public class Series extends Content {

  /** Numero de temporadas. */
  private int seasons;

  /** Episodios da serie. */
  private List<Episode> episodes;

  /** Estado da serie (em exibicao, terminada, cancelada). */
  private SeriesStatus status;

  /**
   * Cria uma serie.
   *
   * @param id identificador unico
   * @param createdAt data/hora de criacao
   * @param title titulo
   * @param releaseYear ano de lancamento
   * @param genre genero
   * @param region regiao
   * @param seasons numero de temporadas
   * @param status estado da serie
   */
  public Series (String id, LocalDateTime createdAt, String title, int releaseYear, Genre genre, String region, int seasons, SeriesStatus status) {
    super(id, createdAt, title, releaseYear, genre, region, 0.0, 0, new ArrayList<>()); // 0.0 = rating, 0 = views, new ArrayList<>() = artists
    this.seasons = seasons;
    this.status = status;
    this.episodes = new ArrayList<>();
  }

  /** @return numero de temporadas */
  public int getSeasons() {
    return seasons;
  }

  /** @return o estado da serie */
  public SeriesStatus getStatus() {
    return status;
  }

  /** @return lista de episodios */
  public List<Episode> getEpisodes() {
    return episodes;
  }

  /**
   * Adiciona um episodio (se ainda nao existir um episodio com o mesmo numero).
   *
   * @param e episodio a adicionar
   */
  public void addEpisode(Episode e) {
    for (Episode episode : episodes) {
      if (episode.getEpisodeNumber() == e.getEpisodeNumber()) {
        return;
      }
    }
    episodes.add(e);
  }

  /** @return tipo de conteudo ("Series") */
  @Override
  public String getContentType() {
    return "Series";
  }

  /** @return detalhes da serie para exibicao */
  @Override
  public String getDetails() {
    int epCount = (episodes == null) ? 0 : episodes.size();
    String safeStatus = (status == null) ? "Unknown" : status.toString();
    return "Seasons: " + seasons + ", Episodes: " + epCount + ", Status: " + safeStatus;
  }

}