package streaming.model;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Documentario (conteudo com duracao, topico e narrador).
 */
public class Documentary extends Content {

  /** Duracao em minutos. */
  private int duration;

  /** Topico/tema principal. */
  private String topic;

  /** Narrador do documentario. */
  private Artist narrator;

  /**
   * Cria um documentario.
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
   * @param duration duracao em minutos
   * @param topic topico
   * @param narrator narrador
   */
  public Documentary(
      String id,
      LocalDateTime createdAt,
      String title,
      int releaseYear,
      Genre genre,
      String region,
      double avgRating,
      int totalViews,
      List<Artist> artists,
      int duration,
      String topic,
      Artist narrator
  ) {
    super(id, createdAt, title, releaseYear, genre, region, avgRating, totalViews, artists);
    this.duration = duration;
    this.topic = topic;
    this.narrator = narrator;
  }

  /** @return topico/tema do documentario */
  public String getTopic() {
    return topic;
  }

  /** @return narrador do documentario */
  public Artist getNarrator() {
    return narrator;
  }

  /** @return duracao em minutos */
  public int getDuration() {
    return duration;
  }

  /** @return tipo de conteudo ("Documentary") */
  @Override
  public String getContentType() {
    return "Documentary";
  }

  /** @return detalhes do documentario para exibicao */
  @Override
  public String getDetails() {
    String narratorName = (narrator == null) ? "Unknown" : narrator.getName();
    String safeTopic = (topic == null) ? "Unknown" : topic;
    return "Topic: " + safeTopic + ", Duration: " + duration + " min, Narrator: " + narratorName;
  }

}
