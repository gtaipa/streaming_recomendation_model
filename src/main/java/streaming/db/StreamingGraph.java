package streaming.db;

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;

import streaming.model.Entity;
import streaming.model.Interaction;
import streaming.model.InteractionType;
import streaming.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * R7 - Representacao das entidades e relacoes atraves de um grafo pesado direcionado.
 */
public class StreamingGraph {

  private static class GenericEdge {
    String fromId;
    String toId;
    double weight;
    GenericEdge(String fromId, String toId, double weight) {
      this.fromId = fromId;
      this.toId = toId;
      this.weight = weight;
    }
  }

  private EdgeWeightedDigraph graph;
  private Map<String, Integer> nodeIndex;
  private Map<Integer, Entity> indexToEntity;
  private List<Interaction> interactions;
  private List<GenericEdge> genericEdges;
  private int nextIndex;
  private int capacity;
  private static final int DEFAULT_CAPACITY = 128;

  public StreamingGraph() {
    this.capacity = DEFAULT_CAPACITY;
    this.graph = new EdgeWeightedDigraph(capacity);
    this.nodeIndex = new HashMap<>();
    this.indexToEntity = new HashMap<>();
    this.interactions = new ArrayList<>();
    this.genericEdges = new ArrayList<>();
    this.nextIndex = 0;
  }

  public void addVertex(Entity e) {
    if (e == null || e.getId() == null) return;
    if (nodeIndex.containsKey(e.getId())) return;
    if (nextIndex >= capacity) {
      rebuild(capacity * 2);
    }
    nodeIndex.put(e.getId(), nextIndex);
    indexToEntity.put(nextIndex, e);
    nextIndex++;
  }

  public boolean removeVertex(String entityId) {
    if (entityId == null || !nodeIndex.containsKey(entityId)) return false;

    int idx = nodeIndex.get(entityId);
    nodeIndex.remove(entityId);
    indexToEntity.remove(idx);

    interactions.removeIf(inter -> {
      if (inter == null || inter.getUser() == null) return false;
      String fromId = inter.getUser().getId();
      Entity target = inter.getTargetEntity();
      String toId = target == null ? null : target.getId();
      return entityId.equals(fromId) || entityId.equals(toId);
    });
    genericEdges.removeIf(ge -> entityId.equals(ge.fromId) || entityId.equals(ge.toId));

    rebuild(capacity);
    return true;
  }

  public void addEdge(Interaction interaction) {
    if (interaction == null) return;
    if (interaction.getUser() == null) return;
    Entity target = interaction.getTargetEntity();
    if (target == null) return;

    addVertex(interaction.getUser());
    addVertex(target);

    int from = nodeIndex.get(interaction.getUser().getId());
    int to = nodeIndex.get(target.getId());

    DirectedEdge edge = new DirectedEdge(from, to, interaction.getWeight());
    graph.addEdge(edge);

    interactions.add(interaction);
  }

  public void addEdge(Entity from, Entity to, double weight) {
    if (from == null || to == null) return;
    addVertex(from);
    addVertex(to);

    int fromIdx = nodeIndex.get(from.getId());
    int toIdx = nodeIndex.get(to.getId());

    DirectedEdge edge = new DirectedEdge(fromIdx, toIdx, weight);
    graph.addEdge(edge);
    genericEdges.add(new GenericEdge(from.getId(), to.getId(), weight));
  }

  public void addFollow(User follower, User followed) {
    if (follower == null || followed == null) return;
    addEdge(follower, followed, 1.0);
  }

  public boolean containsVertex(String entityId) {
    return entityId != null && nodeIndex.containsKey(entityId);
  }

  public int vertexCount() {
    return nodeIndex.size();
  }

  public int edgeCount() {
    return graph.E();
  }

  public Entity getEntity(String entityId) {
    if (entityId == null || !nodeIndex.containsKey(entityId)) return null;
    int idx = nodeIndex.get(entityId);
    return indexToEntity.get(idx);
  }

  public int getIndex(String entityId) {
    if (entityId == null || !nodeIndex.containsKey(entityId)) return -1;
    return nodeIndex.get(entityId);
  }

  public Entity getEntityByIndex(int index) {
    return indexToEntity.get(index);
  }

  public List<DirectedEdge> getOutEdges(String entityId) {
    List<DirectedEdge> result = new ArrayList<>();
    if (entityId == null || !nodeIndex.containsKey(entityId)) return result;

    int idx = nodeIndex.get(entityId);
    for (DirectedEdge e : graph.adj(idx)) {
      if (indexToEntity.containsKey(e.to())) {
        result.add(e);
      }
    }
    return result;
  }

  public List<Entity> getNeighbors(String entityId) {
    List<Entity> result = new ArrayList<>();
    for (DirectedEdge e : getOutEdges(entityId)) {
      Entity neighbor = indexToEntity.get(e.to());
      if (neighbor != null) {
        result.add(neighbor);
      }
    }
    return result;
  }

  public List<Interaction> getInteractions() {
    return new ArrayList<>(interactions);
  }

  /**
   * Devolve uma cópia do mapa interno índice -> entidade.
   * Útil para algoritmos (ex: BFS) sem expor o estado mutável do grafo.
   */
  public Map<Integer, Entity> getIndexToEntity() {
    return new HashMap<>(indexToEntity);
  }

  /**
   * Devolve uma lista com as interações filtradas por tipo.
   * Mantém a lógica de filtragem encapsulada no grafo.
   */
  public List<Interaction> getInteractionsByType(InteractionType type) {
    List<Interaction> result = new ArrayList<>();
    if (type == null) return result;
    for (Interaction i : interactions) {
      if (i != null && i.getType() == type) result.add(i);
    }
    return result;
  }

  public List<String> getAllVertexIds() {
    return new ArrayList<>(nodeIndex.keySet());
  }

  public EdgeWeightedDigraph getGraph() {
    return graph;
  }

  private void rebuild(int newCapacity) {
    this.capacity = newCapacity;

    List<Entity> entities = new ArrayList<>(indexToEntity.values());

    nodeIndex.clear();
    indexToEntity.clear();
    nextIndex = 0;

    this.graph = new EdgeWeightedDigraph(newCapacity);

    for (Entity e : entities) {
      if (e != null && e.getId() != null) {
        nodeIndex.put(e.getId(), nextIndex);
        indexToEntity.put(nextIndex, e);
        nextIndex++;
      }
    }

    for (Interaction inter : interactions) {
      if (inter == null || inter.getUser() == null) continue;
      Entity target = inter.getTargetEntity();
      if (target == null) continue;
      String fromId = inter.getUser().getId();
      String toId = target.getId();
      if (nodeIndex.containsKey(fromId) && nodeIndex.containsKey(toId)) {
        int from = nodeIndex.get(fromId);
        int to = nodeIndex.get(toId);
        graph.addEdge(new DirectedEdge(from, to, inter.getWeight()));
      }
    }

    for (GenericEdge ge : genericEdges) {
      if (nodeIndex.containsKey(ge.fromId) && nodeIndex.containsKey(ge.toId)) {
        int from = nodeIndex.get(ge.fromId);
        int to = nodeIndex.get(ge.toId);
        graph.addEdge(new DirectedEdge(from, to, ge.weight));
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("StreamingGraph: ").append(vertexCount()).append(" vertices, ")
            .append(edgeCount()).append(" arestas\n");

    sb.append("\nvertices:\n");
    for (Map.Entry<String, Integer> entry : nodeIndex.entrySet()) {
      Entity e = indexToEntity.get(entry.getValue());
      sb.append("  [").append(entry.getValue()).append("] ")
              .append(entry.getKey()).append(" -> ").append(e).append("\n");
    }

    sb.append("\nArestas:\n");
    for (int v = 0; v < nextIndex; v++) {
      Entity fromEntity = indexToEntity.get(v);
      if (fromEntity == null) continue;

      for (DirectedEdge e : graph.adj(v)) {
        Entity toEntity = indexToEntity.get(e.to());
        if (toEntity == null) continue;
        sb.append("  ").append(fromEntity.getId())
                .append(" -> ").append(toEntity.getId())
                .append(" (peso: ").append(e.weight()).append(")\n");
      }
    }

    return sb.toString();
  }
}
