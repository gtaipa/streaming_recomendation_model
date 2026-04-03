package streaming.db;

import streaming.model.List;
import streaming.model.Entity;
import streaming.model.LocalDate;
import java.util.List;
import streaming.model.void;
import HashMap;
import streaming.model.Interaction;
import streaming.model.boolean;
import String;
import streaming.model.Content;
import streaming.model.User;

public class StreamingGraph {

  public EdgeWeightedDigraph graph;

  public HashMap nodeIndex;

  public HashMap indexToEntity;

  public List interactions;

    public List<InteractionType> interactionType;
    public List<Interaction> interaction;

  public void addVertex(Entity e) {
  }

  public void addEdge(Interaction i) {
  }

  public List shortestPath(String a, String b) {
  return null;
  }

  public boolean isConnected() {
  return false;
  }

  public StreamingGraph subgraph(String filter) {
  return null;
  }

  public List recommend(User u) {
  return null;
  }

  public Map getViewStats(Content c, LocalDate d1, LocalDate d2) {
  return null;
  }

}