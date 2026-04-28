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
 * R7 - Representação das entidades e relações através de um grafo pesado direcionado.
 *
 * CONCEITO FUNDAMENTAL:
 * A biblioteca algs4 usa INTEIROS (0, 1, 2, ...) para identificar os vértices do grafo.
 * Mas as nossas entidades têm IDs tipo "U1", "M1", "A1" (Strings).
 *
 * Solução: criar dois mapas de tradução:
 *   - nodeIndex:      "U1" → 0,  "M1" → 1,  "A1" → 2  (String → int)
 *   - indexToEntity:   0 → User("U1"),  1 → Movie("M1")  (int → Entity)
 *
 * Assim podemos usar as estruturas da algs4 internamente,
 * mas a API pública trabalha com os nossos IDs (Strings).
 *
 * SOBRE O GRAFO:
 * - EdgeWeightedDigraph = grafo com arestas direcionadas e com peso
 * - DirectedEdge = uma aresta de v → w com peso (weight)
 * - Exemplo: User "U1" viu Movie "M1" com rating 8.5
 *   → addVertex(user)   → nodeIndex["U1"] = 0
 *   → addVertex(movie)  → nodeIndex["M1"] = 1
 *   → addEdge(interaction) → aresta 0 → 1 com peso 8.5
 *
 * PROBLEMA DO TAMANHO FIXO:
 * O EdgeWeightedDigraph da algs4 exige saber o número de vértices no construtor.
 * Como não sabemos quantas entidades vamos ter, usamos uma capacidade inicial
 * e recriamos o grafo (rebuild) quando ficamos sem espaço.
 */
public class StreamingGraph {

  // =====================================================
  // ATRIBUTOS
  // =====================================================

  /**
   * O grafo em si (da biblioteca algs4).
   * Internamente usa inteiros como vértices.
   */
  private EdgeWeightedDigraph graph;

  /**
   * Mapa: ID da entidade (String) → índice inteiro no grafo.
   * Exemplo: "U1" → 0, "M1" → 1
   */
  private Map<String, Integer> nodeIndex;

  /**
   * Mapa inverso: índice inteiro → Entity.
   * Exemplo: 0 → User("U1"), 1 → Movie("M1")
   */
  private Map<Integer, Entity> indexToEntity;

  /**
   * Lista de todas as interações adicionadas ao grafo.
   * Guardamos isto para:
   *  1) poder reconstruir o grafo se precisarmos de mais espaço (rebuild)
   *  2) poder fazer pesquisas por tipo de interação, timestamp, etc. (R8)
   */
  private List<Interaction> interactions;

  /**
   * Contador do próximo índice disponível.
   * Cada vez que adicionamos um vértice novo, usamos este valor e incrementamos.
   */
  private int nextIndex;

  /**
   * Capacidade atual do grafo (número máximo de vértices antes de rebuild).
   */
  private int capacity;

  /**
   * Capacidade inicial por defeito.
   * Se precisarmos de mais, o grafo é recriado com o dobro do tamanho.
   */
  private static final int DEFAULT_CAPACITY = 128;

  // =====================================================
  // CONSTRUTOR
  // =====================================================

  /**
   * Cria um StreamingGraph vazio com capacidade inicial de 128 vértices.
   */
  public StreamingGraph() {
    this.capacity = DEFAULT_CAPACITY;
    this.graph = new EdgeWeightedDigraph(capacity);
    this.nodeIndex = new HashMap<>();
    this.indexToEntity = new HashMap<>();
    this.interactions = new ArrayList<>();
    this.nextIndex = 0;
  }

  // =====================================================
  // ADICIONAR VÉRTICES (Entidades)
  // =====================================================

  /**
   * Adiciona uma entidade como vértice do grafo.
   *
   * Se a entidade já existir (mesmo ID), não faz nada.
   * Se o grafo estiver cheio, faz rebuild com o dobro da capacidade.
   *
   * Exemplo:
   *   User u1 = new User("U1", ...);
   *   graph.addVertex(u1);
   *   // Agora "U1" está mapeado para o índice 0
   *
   * @param e a entidade a adicionar (User, Movie, Artist, etc.)
   */
  public void addVertex(Entity e) {
    if (e == null || e.getId() == null) return;

    // Se já existe, não adicionamos outra vez
    if (nodeIndex.containsKey(e.getId())) return;

    // Se atingimos a capacidade, precisamos de um grafo maior
    if (nextIndex >= capacity) {
      rebuild(capacity * 2);
    }

    // Mapear: ID → índice e índice → Entity
    nodeIndex.put(e.getId(), nextIndex);
    indexToEntity.put(nextIndex, e);
    nextIndex++;
  }

  /**
   * Remove um vértice do grafo.
   *
   * NOTA: O EdgeWeightedDigraph da algs4 não suporta remoção de vértices.
   * Por isso, marcamos o vértice como removido nos nossos mapas
   * e fazemos rebuild para limpar as arestas órfãs.
   *
   * @param entityId o ID da entidade a remover
   * @return true se a entidade existia e foi removida
   */
  public boolean removeVertex(String entityId) {
    if (entityId == null || !nodeIndex.containsKey(entityId)) return false;

    int idx = nodeIndex.get(entityId);
    nodeIndex.remove(entityId);
    indexToEntity.remove(idx);

    // Remover todas as interações que envolvem esta entidade
    // (como fonte ou destino)
    interactions.removeIf(inter -> {
      String fromId = inter.getUser().getId();
      String toId = inter.getContent().getId();
      return entityId.equals(fromId) || entityId.equals(toId);
    });

    // Reconstruir o grafo sem este vértice
    rebuild(capacity);
    return true;
  }

  // =====================================================
  // ADICIONAR ARESTAS (Interações/Relações)
  // =====================================================

  /**
   * Adiciona uma aresta (relação) ao grafo a partir de uma Interaction.
   *
   * Uma Interaction liga um User a um Content (ex: "U1 viu M1 com rating 8.5").
   * A aresta é DIRECIONADA: vai do User para o Content.
   * O PESO da aresta é o weight da Interaction.
   *
   * IMPORTANTE: Ambas as entidades (User e Content) devem já estar no grafo.
   * Se não estiverem, são adicionadas automaticamente.
   *
   * Exemplo:
   *   Interaction i = new Interaction(user, movie, WATCH, now, 100.0, 8.5, 8.5);
   *   graph.addEdge(i);
   *   // Cria aresta: U1 → M1 com peso 8.5
   *
   * @param interaction a interação a adicionar
   */
  public void addEdge(Interaction interaction) {
    if (interaction == null) return;
    if (interaction.getUser() == null || interaction.getContent() == null) return;

    // Garantir que ambos os vértices existem
    addVertex(interaction.getUser());
    addVertex(interaction.getContent());

    // Obter os índices inteiros
    int from = nodeIndex.get(interaction.getUser().getId());
    int to = nodeIndex.get(interaction.getContent().getId());

    // Criar a aresta direcionada com peso
    DirectedEdge edge = new DirectedEdge(from, to, interaction.getWeight());
    graph.addEdge(edge);

    // Guardar a interação para consultas futuras
    interactions.add(interaction);
  }

  /**
   * Adiciona uma aresta genérica entre duas entidades quaisquer.
   *
   * Isto é útil para relações que não são User→Content, como:
   *   - User segue User (FOLLOW)
   *   - Artist participou em Movie
   *
   * @param from   a entidade de origem
   * @param to     a entidade de destino
   * @param weight o peso da aresta
   */
  public void addEdge(Entity from, Entity to, double weight) {
    if (from == null || to == null) return;

    // Garantir que ambos estão no grafo
    addVertex(from);
    addVertex(to);

    int fromIdx = nodeIndex.get(from.getId());
    int toIdx = nodeIndex.get(to.getId());

    DirectedEdge edge = new DirectedEdge(fromIdx, toIdx, weight);
    graph.addEdge(edge);
  }

  /**
   * Adiciona uma relação de "follow" entre dois utilizadores.
   *
   * Cria uma aresta: follower → followed com peso 1.0
   * e guarda como Interaction do tipo FOLLOW.
   *
   * @param follower quem segue
   * @param followed quem é seguido
   */
  public void addFollow(User follower, User followed) {
    if (follower == null || followed == null) return;

    addVertex(follower);
    addVertex(followed);

    int fromIdx = nodeIndex.get(follower.getId());
    int toIdx = nodeIndex.get(followed.getId());

    DirectedEdge edge = new DirectedEdge(fromIdx, toIdx, 1.0);
    graph.addEdge(edge);

    // Guardar como interação do tipo FOLLOW (sem content, então usamos null-safe)
    // Nota: a Interaction original espera User+Content, mas para follows
    // entre users, guardamos a relação separadamente
  }

  // =====================================================
  // CONSULTAS BÁSICAS
  // =====================================================

  /**
   * Verifica se uma entidade está no grafo.
   *
   * @param entityId o ID da entidade
   * @return true se a entidade é um vértice do grafo
   */
  public boolean containsVertex(String entityId) {
    return entityId != null && nodeIndex.containsKey(entityId);
  }

  /**
   * Devolve o número de vértices atualmente no grafo.
   */
  public int vertexCount() {
    return nodeIndex.size();
  }

  /**
   * Devolve o número total de arestas no grafo.
   */
  public int edgeCount() {
    return graph.E();
  }

  /**
   * Devolve a entidade associada a um ID.
   *
   * @param entityId o ID da entidade
   * @return a Entity, ou null se não existir
   */
  public Entity getEntity(String entityId) {
    if (entityId == null || !nodeIndex.containsKey(entityId)) return null;
    int idx = nodeIndex.get(entityId);
    return indexToEntity.get(idx);
  }

  /**
   * Devolve o índice inteiro de uma entidade no grafo.
   * Retorna -1 se não existir.
   *
   * @param entityId o ID da entidade
   * @return o índice inteiro, ou -1
   */
  public int getIndex(String entityId) {
    if (entityId == null || !nodeIndex.containsKey(entityId)) return -1;
    return nodeIndex.get(entityId);
  }

  /**
   * Devolve a entidade associada a um índice inteiro.
   *
   * @param index o índice
   * @return a Entity, ou null
   */
  public Entity getEntityByIndex(int index) {
    return indexToEntity.get(index);
  }

  /**
   * Devolve todas as arestas que SAEM de uma entidade (arestas de saída).
   *
   * Exemplo: se "U1" viu "M1" e "M2", devolve as arestas U1→M1 e U1→M2.
   *
   * @param entityId o ID da entidade
   * @return lista de DirectedEdge que saem desta entidade
   */
  public List<DirectedEdge> getOutEdges(String entityId) {
    List<DirectedEdge> result = new ArrayList<>();
    if (entityId == null || !nodeIndex.containsKey(entityId)) return result;

    int idx = nodeIndex.get(entityId);
    for (DirectedEdge e : graph.adj(idx)) {
      // Só incluímos arestas cujo destino ainda existe no grafo
      if (indexToEntity.containsKey(e.to())) {
        result.add(e);
      }
    }
    return result;
  }

  /**
   * Devolve todas as entidades "vizinhas" de uma entidade
   * (i.e., entidades ligadas por arestas de saída).
   *
   * @param entityId o ID da entidade
   * @return lista de Entities para as quais esta entidade aponta
   */
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

  /**
   * Devolve todas as interações registadas no grafo.
   *
   * @return lista imutável de interações
   */
  public List<Interaction> getInteractions() {
    return new ArrayList<>(interactions);
  }

  /**
   * Devolve as interações filtradas por tipo (WATCH, RATE, CLICK, FOLLOW).
   *
   * @param type o tipo de interação
   * @return lista de interações desse tipo
   */
  public List<Interaction> getInteractionsByType(InteractionType type) {
    List<Interaction> result = new ArrayList<>();
    if (type == null) return result;
    for (Interaction i : interactions) {
      if (i.getType() == type) {
        result.add(i);
      }
    }
    return result;
  }

  /**
   * Devolve todos os IDs das entidades no grafo.
   *
   * @return lista de IDs
   */
  public List<String> getAllVertexIds() {
    return new ArrayList<>(nodeIndex.keySet());
  }

  // =====================================================
  // ACESSO AO GRAFO INTERNO (para usar nos algoritmos do R8)
  // =====================================================

  /**
   * Devolve o EdgeWeightedDigraph interno da algs4.
   * Isto vai ser necessário no R8 para correr algoritmos como Dijkstra,
   * BFS, DFS, etc.
   *
   * @return o grafo interno
   */
  public EdgeWeightedDigraph getGraph() {
    return graph;
  }

  /**
   * Devolve o mapa de ID → índice (read-only copy).
   */
  public Map<String, Integer> getNodeIndex() {
    return new HashMap<>(nodeIndex);
  }

  /**
   * Devolve o mapa de índice → Entity (read-only copy).
   */
  public Map<Integer, Entity> getIndexToEntity() {
    return new HashMap<>(indexToEntity);
  }

  // =====================================================
  // REBUILD - Reconstrução do grafo
  // =====================================================

  /**
   * Reconstrói o grafo com uma nova capacidade.
   *
   * PORQUÊ: O EdgeWeightedDigraph da algs4 tem tamanho fixo.
   * Se adicionarmos mais vértices do que a capacidade, precisamos
   * de criar um grafo novo e readicionar todas as arestas.
   *
   * COMO:
   * 1) Criar um novo grafo com a nova capacidade
   * 2) Re-mapear todas as entidades com novos índices contíguos (0, 1, 2, ...)
   * 3) Re-adicionar todas as arestas das interações guardadas
   *
   * @param newCapacity a nova capacidade
   */
  private void rebuild(int newCapacity) {
    this.capacity = newCapacity;

    // Guardar as entidades atuais
    List<Entity> entities = new ArrayList<>(indexToEntity.values());

    // Limpar os mapas
    nodeIndex.clear();
    indexToEntity.clear();
    nextIndex = 0;

    // Criar grafo novo com mais espaço
    this.graph = new EdgeWeightedDigraph(newCapacity);

    // Re-adicionar todos os vértices (com novos índices contíguos)
    for (Entity e : entities) {
      if (e != null && e.getId() != null) {
        nodeIndex.put(e.getId(), nextIndex);
        indexToEntity.put(nextIndex, e);
        nextIndex++;
      }
    }

    // Re-adicionar todas as arestas a partir das interações guardadas
    for (Interaction inter : interactions) {
      if (inter.getUser() == null || inter.getContent() == null) continue;
      String fromId = inter.getUser().getId();
      String toId = inter.getContent().getId();

      if (nodeIndex.containsKey(fromId) && nodeIndex.containsKey(toId)) {
        int from = nodeIndex.get(fromId);
        int to = nodeIndex.get(toId);
        DirectedEdge edge = new DirectedEdge(from, to, inter.getWeight());
        graph.addEdge(edge);
      }
    }
  }

  // =====================================================
  // toString (para debug)
  // =====================================================

  /**
   * Representação textual do grafo para debug.
   * Mostra os vértices e as arestas com os IDs das entidades.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("StreamingGraph: ").append(vertexCount()).append(" vértices, ")
            .append(edgeCount()).append(" arestas\n");

    sb.append("\nVértices:\n");
    for (Map.Entry<String, Integer> entry : nodeIndex.entrySet()) {
      Entity e = indexToEntity.get(entry.getValue());
      sb.append("  [").append(entry.getValue()).append("] ")
              .append(entry.getKey()).append(" → ").append(e).append("\n");
    }

    sb.append("\nArestas:\n");
    for (int v = 0; v < nextIndex; v++) {
      Entity fromEntity = indexToEntity.get(v);
      if (fromEntity == null) continue;

      for (DirectedEdge e : graph.adj(v)) {
        Entity toEntity = indexToEntity.get(e.to());
        if (toEntity == null) continue;
        sb.append("  ").append(fromEntity.getId())
                .append(" → ").append(toEntity.getId())
                .append(" (peso: ").append(e.weight()).append(")\n");
      }
    }

    return sb.toString();
  }
}
