package streaming.model;


/**
 * Episodio de uma serie.
 */
public class Episode {

  /** Numero da temporada (season). */
  private int seasonNumber;

  /** Numero do episodio dentro da temporada. */
  private int episodeNumber;

  /** Titulo do episodio. */
  private String title;

  /** Duracao em minutos. */
  private int duration;
  /** Rating do episodio (0..10). */
  private float rating;

     
      /**
       * Cria um episodio.
       *
       * @param seasonNumber numero da temporada
       * @param episodeNumber numero do episodio
       * @param title titulo
       * @param duration duracao em minutos
       * @param rating rating (0..10)
       */
      public Episode (int seasonNumber, int episodeNumber, String title, int duration, float rating) {
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.duration = duration;
        this.rating = rating;
      }

  /** @return numero da temporada */
  public int getSeasonNumber() {
  return seasonNumber;
  }

  /** @return numero do episodio */
  public int getEpisodeNumber() {
  return episodeNumber;
  }

  /** @return titulo */
  public String getTitle() {
  return title;
  }

  /** @return duracao em minutos */
  public int getDuration() {
  return duration;
  }

  /** @return rating (0..10) */
  public float getRating() {
  return rating;
  }

  /**
   * Atualiza o rating.
   *
   * @param rating novo rating (0..10)
   * @throws IllegalArgumentException se estiver fora do intervalo
   */
  public void setRating(float rating) {
    if (rating < 0 || rating > 10) {
      throw new IllegalArgumentException("Rating must be between 0 and 10");
    }
  this.rating = rating;
  }
@Override
  public String toString() {
  return "Season"+seasonNumber+"Episode"+episodeNumber+"-"+title+"("+duration+"min)";
  }

}
