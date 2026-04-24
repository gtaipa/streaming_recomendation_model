package streaming.model;

import java.time.LocalDateTime;

public class Interaction {

  // Atributos (privados por boas práticas)
  private User user;
  private Content content;
  private InteractionType type;
  private LocalDateTime timestamp;
  private double watchedPct;
  private double rating;
  private double weight;

  // Construtor para inicializar a interação corretamente
  public Interaction(User user, Content content, InteractionType type, LocalDateTime timestamp, double watchedPct, double rating, double weight) {
    this.user = user;
    this.content = content;
    this.type = type;
    this.timestamp = timestamp;
    this.watchedPct = watchedPct;
    this.rating = rating;
    this.weight = weight;
  }

  // --- Métodos exigidos no teu Diagrama de Classes ---

  public double getWeight() {
    return this.weight;
  }

  public LocalDateTime getTimestamp() {
    return this.timestamp;
  }

  @Override
  public String toString() {
    return "Interaction [" + type + "] - User: " + user.getId() + " | Content: " + content.getId() + " | Weight: " + weight;
  }

  // --- Getters e Setters extra (Essenciais para depois usares no Grafo) ---

  public User getUser() {
    return user;
  }

  public Content getContent() {
    return content;
  }

  public InteractionType getType() {
    return type;
  }

  public double getWatchedPct() {
    return watchedPct;
  }

  public double getRating() {
    return rating;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }
}