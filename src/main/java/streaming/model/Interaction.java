package streaming.model;

import java.time.LocalDateTime;

/**
 * Interacao entre um {@link User} e um {@link Content} (ex.: watch, rate, click).
 */

public class Interaction {

  // Atributos (privados por boas práticas)
  /** Utilizador que realizou a interacao. */
  private User user;
  /** Conteudo associado a interacao. */
  private Content content;
  /** Utilizador alvo (apenas para interacoes do tipo FOLLOW). */
  private User targetUser;
  /** Tipo de interacao. */
  private InteractionType type;
  /** Momento em que ocorreu a interacao. */
  private LocalDateTime timestamp;
  /** Percentagem vista (0..100), se aplicavel. */
  private double watchedPct;
  /** Rating atribuido, se aplicavel. */
  private double rating;
  /** Peso/preferencia calculada para recomendacao. */
  private double weight;

  // Construtor para inicializar a interação corretamente
  /**
   * Cria uma interacao.
   *
   * @param user utilizador
   * @param content conteudo
   * @param type tipo de interacao
   * @param timestamp instante da interacao
   * @param watchedPct percentagem vista (0..100)
   * @param rating rating atribuido
   * @param weight peso da interacao
   */
  public Interaction(User user, Content content, InteractionType type, LocalDateTime timestamp, double watchedPct, double rating, double weight) {
    this.user = user;
    this.content = content;
    this.targetUser = null;
    this.type = type;
    this.timestamp = timestamp;
    this.watchedPct = watchedPct;
    this.rating = rating;
    this.weight = weight;
  }

  /**
   * Cria uma interacao de FOLLOW entre utilizadores.
   *
   * @param from utilizador que segue
   * @param to utilizador seguido
   * @param timestamp instante da interacao
   * @param weight peso da interacao (tipicamente 1.0)
   */
  public Interaction(User from, User to, LocalDateTime timestamp, double weight) {
    this.user = from;
    this.content = null;
    this.targetUser = to;
    this.type = InteractionType.FOLLOW;
    this.timestamp = timestamp;
    this.watchedPct = 0.0;
    this.rating = 0.0;
    this.weight = weight;
  }

  // --- Métodos exigidos no teu Diagrama de Classes ---

  /** @return peso da interacao */
  public double getWeight() {
    return this.weight;
  }

  /** @return instante da interacao */
  public LocalDateTime getTimestamp() {
    return this.timestamp;
  }

  /** @return representacao textual resumida da interacao */
  @Override
  public String toString() {
    String target;
    if (content != null) target = "Content: " + content.getId();
    else if (targetUser != null) target = "User: " + targetUser.getId();
    else target = "Target: null";
    return "Interaction [" + type + "] - User: " + (user == null ? "null" : user.getId()) + " | " + target + " | Weight: " + weight;
  }

  // --- Getters e Setters extra (Essenciais para depois usares no Grafo) ---

  /** @return utilizador */
  public User getUser() {
    return user;
  }

  /** @return conteudo */
  public Content getContent() {
    return content;
  }

  /** @return utilizador seguido (apenas em FOLLOW); pode ser null noutros tipos */
  public User getTargetUser() {
    return targetUser;
  }

  /**
   * Entidade alvo da interacao:
   * - WATCH/RATE/CLICK: o {@link Content}
   * - FOLLOW: o utilizador seguido
   */
  public Entity getTargetEntity() {
    if (content != null) return content;
    return targetUser;
  }

  /** @return tipo de interacao */
  public InteractionType getType() {
    return type;
  }

  /** @return percentagem vista */
  public double getWatchedPct() {
    return watchedPct;
  }

  /** @return rating */
  public double getRating() {
    return rating;
  }

  /** @param weight novo peso */
  public void setWeight(double weight) {
    this.weight = weight;
  }
}
