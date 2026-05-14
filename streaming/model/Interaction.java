package streaming.model;

import java.util.List;
import String;

public class Interaction {

  public User user;

  public Content content;

  public InteractionType type;

  public LocalDateTime timestamp;

  public double watchedPct;

  public double rating;

  public double weight;

    public List<InteractionType> interactionType;
    public List<StreamingGraph> streamingGraph;

  public double getWeight() {
  return 0.0;
  }

  public LocalDateTime getTimestamp() {
  return null;
  }

  public String toString() {
  return null;
  }

}