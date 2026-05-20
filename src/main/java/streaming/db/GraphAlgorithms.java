package streaming.db;

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import streaming.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * R8 - Algoritmos e funcionalidades avançadas sobre o grafo.
 *
 * Alíneas implementadas:
 *   a) Caminhos mais curtos entre utilizadores
 *   b) Extração e análise de subgrafos
 *   c) Verificação de conectividade (BFS)
 *   d) Recomendações baseadas em proximidade estrutural
 *   e) Estatísticas de visualização entre datas / género
 *   f) Utilizadores que viram séries de um género num período
 *   g) Seguidores que viram o mesmo filme num intervalo
 */
public class GraphAlgorithms {
    private final StreamingGraph graph;
    private final StreamingDB db;

    /** @param graph grafo com entidades e relações (R7)
     *  @param db    base de dados com entidades (Fase 1) */
    public GraphAlgorithms(StreamingGraph graph, StreamingDB db) {
        if (graph == null || db == null)
            throw new IllegalArgumentException("graph e db não podem ser null");
        this.graph = graph;
        this.db = db;
    }

    // ========================= R8a) CAMINHOS MAIS CURTOS =========================

    /** R8a) Caminho mais curto entre dois vértices (Dijkstra). */
    public List<Entity> shortestPath(String fromId, String toId) {
        return dijkstraPath(graph.getGraph(), fromId, toId);
    }

    /** R8a) Custo do caminho mais curto. */
    public double shortestPathDistance(String fromId, String toId) {
        if (!graph.containsVertex(fromId) || !graph.containsVertex(toId))
            return Double.POSITIVE_INFINITY;
        DijkstraSP sp = new DijkstraSP(graph.getGraph(), graph.getIndex(fromId));
        return sp.distTo(graph.getIndex(toId));
    }

    /** R8a) Caminho mais curto apenas por arestas WATCH. */
    public List<Entity> shortestPathByWatch(String fromId, String toId) {
        return shortestPathByType(fromId, toId, InteractionType.WATCH);
    }

    /** R8a) Caminho mais curto apenas por arestas FOLLOW. */
    public List<Entity> shortestPathByFollow(String fromId, String toId) {
        return shortestPathByType(fromId, toId, InteractionType.FOLLOW);
    }

    /** Constrói subgrafo temporário com arestas de um tipo e corre Dijkstra. */
    private List<Entity> shortestPathByType(String fromId, String toId, InteractionType type) {
        if (!graph.containsVertex(fromId) || !graph.containsVertex(toId))
            return new ArrayList<>();
        EdgeWeightedDigraph temp = new EdgeWeightedDigraph(graph.getGraph().V());
        for (Interaction i : graph.getInteractions()) {
            if (i.getType() != type) continue;
            String fId = i.getUser().getId();
            Entity target = i.getTargetEntity();
            if (target == null) continue;
            String tId = target.getId();
            if (graph.containsVertex(fId) && graph.containsVertex(tId))
                temp.addEdge(new DirectedEdge(graph.getIndex(fId), graph.getIndex(tId), i.getWeight()));
        }
        return dijkstraPath(temp, fromId, toId);
    }

    /** Helper: corre Dijkstra num grafo e devolve o caminho como lista de Entity. */
    private List<Entity> dijkstraPath(EdgeWeightedDigraph g, String fromId, String toId) {
        List<Entity> path = new ArrayList<>();
        if (!graph.containsVertex(fromId) || !graph.containsVertex(toId)) return path;
        int from = graph.getIndex(fromId);
        int to = graph.getIndex(toId);
        DijkstraSP sp = new DijkstraSP(g, from);
        if (!sp.hasPathTo(to)) return path;
        path.add(graph.getEntityByIndex(from));
        for (DirectedEdge e : sp.pathTo(to)) {
            Entity entity = graph.getEntityByIndex(e.to());
            if (entity != null) path.add(entity);
        }
        return path;
    }

    // ========================= R8b) SUBGRAFOS =========================

    /** R8b) Subgrafo com conteúdos de apenas um género. */
    public StreamingGraph subgraphByGenre(String genreName) {
        StreamingGraph sub = new StreamingGraph();
        if (genreName == null) return sub;
        for (Interaction i : graph.getInteractions()) {
            Content c = i.getContent();
            if (c.getGenre() != null && genreName.equalsIgnoreCase(c.getGenre().getName())) {
                sub.addVertex(i.getUser());
                sub.addVertex(c);
                sub.addEdge(i);
            }
        }
        return sub;
    }

    /** R8b) Subgrafo com conteúdos de uma região. */
    public StreamingGraph subgraphByRegion(String region) {
        StreamingGraph sub = new StreamingGraph();
        if (region == null) return sub;
        for (Interaction i : graph.getInteractions()) {
            if (region.equalsIgnoreCase(i.getContent().getRegion())) {
                sub.addVertex(i.getUser());
                sub.addVertex(i.getContent());
                sub.addEdge(i);
            }
        }
        return sub;
    }

    /** R8b) Subgrafo com interações acima de um dado rating. */
    public StreamingGraph subgraphByMinRating(double minRating) {
        StreamingGraph sub = new StreamingGraph();
        for (Interaction i : graph.getInteractions()) {
            if (i.getRating() >= minRating) {
                sub.addVertex(i.getUser());
                sub.addVertex(i.getContent());
                sub.addEdge(i);
            }
        }
        return sub;
    }

    /** R8b) Caminho mais curto entre dois artistas via conteúdos partilhados. */
    public List<Entity> shortestPathBetweenArtists(String artistId1, String artistId2) {
        if (!graph.containsVertex(artistId1) || !graph.containsVertex(artistId2))
            return new ArrayList<>();

        // Enunciado: via conteudos onde participaram.
        // Construir subgrafo bipartido Artist <-> Content e tratar ligacoes como nao-direcionadas
        // para permitir Artist -> Content -> Artist.
        EdgeWeightedDigraph temp = new EdgeWeightedDigraph(graph.getGraph().V());
        for (int v = 0; v < graph.getGraph().V(); v++) {
            Entity from = graph.getEntityByIndex(v);
            if (from == null) continue;
            for (DirectedEdge e : graph.getGraph().adj(v)) {
                Entity to = graph.getEntityByIndex(e.to());
                if (to == null) continue;

                boolean artistToContent = (from instanceof Artist) && (to instanceof Content);
                boolean contentToArtist = (from instanceof Content) && (to instanceof Artist);
                if (!artistToContent && !contentToArtist) continue;

                temp.addEdge(new DirectedEdge(e.from(), e.to(), e.weight()));
                temp.addEdge(new DirectedEdge(e.to(), e.from(), e.weight()));
            }
        }

        return dijkstraPath(temp, artistId1, artistId2);
    }

    // ========================= R8c) CONECTIVIDADE =========================

    /** R8c) Verifica se o grafo completo é fracamente conexo. */
    public boolean isConnected() {
        return isConnected(graph);
    }

    /** R8c) Verifica se um subgrafo é fracamente conexo (BFS bidirecional). */
    public boolean isConnected(StreamingGraph subgraph) {
        if (subgraph.vertexCount() <= 1) return true;
        Map<Integer, Entity> entities = subgraph.getIndexToEntity();
        if (entities.isEmpty()) return true;
        // Criar dígrafo bidirecional (ignorar direções = conectividade fraca)
        Digraph undirected = new Digraph(subgraph.getGraph().V());
        for (int v = 0; v < subgraph.getGraph().V(); v++) {
            for (DirectedEdge e : subgraph.getGraph().adj(v)) {
                undirected.addEdge(e.from(), e.to());
                undirected.addEdge(e.to(), e.from());
            }
        }
        // BFS a partir do primeiro vértice com entidade
        int start = entities.keySet().iterator().next();
        BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(undirected, start);
        for (int idx : entities.keySet()) {
            if (!bfs.hasPathTo(idx)) return false;
        }
        return true;
    }

    // ========================= R8d) RECOMENDAÇÕES =========================

    /** R8d) Recomenda conteúdos via o que os seguidos viram (e o user ainda não). */
    public List<Content> recommend(String userId) {
        List<Content> recommendations = new ArrayList<>();
        if (userId == null || !graph.containsVertex(userId)) return recommendations;
        // 1. Conteúdos que o user já viu
        Set<String> alreadyWatched = new HashSet<>();
        for (DirectedEdge e : graph.getOutEdges(userId)) {
            Entity target = graph.getEntityByIndex(e.to());
            if (target instanceof Content) alreadyWatched.add(target.getId());
        }
        // 2. Contar quantos seguidos viram cada conteúdo novo
        Map<String, Integer> contentCount = new HashMap<>();
        Map<String, Content> contentMap = new HashMap<>();
        for (Entity neighbor : graph.getNeighbors(userId)) {
            if (!(neighbor instanceof User)) continue;
            for (DirectedEdge e : graph.getOutEdges(neighbor.getId())) {
                Entity target = graph.getEntityByIndex(e.to());
                if (target instanceof Content c && !alreadyWatched.contains(c.getId())) {
                    contentCount.merge(c.getId(), 1, Integer::sum);
                    contentMap.put(c.getId(), c);
                }
            }
        }
        // 3. Ordenar por popularidade
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(contentCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        for (Map.Entry<String, Integer> entry : sorted)
            recommendations.add(contentMap.get(entry.getKey()));
        return recommendations;
    }

    // ========================= R8e) ESTATÍSTICAS =========================

    /** R8e) Estatísticas de visualização de um conteúdo entre duas datas. */
    public Map<String, Double> getViewStats(String contentId, LocalDateTime start, LocalDateTime end) {
        return computeStats(i ->
                contentId != null
                        && contentId.equals(i.getContent().getId())
                        && i.getType() == InteractionType.WATCH
                        && inRange(i.getTimestamp(), start, end));
    }

    /** R8e) Estatísticas de visualização filtradas por género e datas. */
    public Map<String, Double> getViewStatsByGenre(String genreName, LocalDateTime start, LocalDateTime end) {
        return computeStats(i ->
                genreName != null
                        && i.getType() == InteractionType.WATCH
                        && i.getContent().getGenre() != null
                        && genreName.equalsIgnoreCase(i.getContent().getGenre().getName())
                        && inRange(i.getTimestamp(), start, end));
    }

    /** Helper: calcula stats (totalViews, avgRating, avgWatchedPct) para interações filtradas. */
    private Map<String, Double> computeStats(java.util.function.Predicate<Interaction> filter) {
        double total = 0, sumRating = 0, sumWatched = 0;
        for (Interaction i : graph.getInteractions()) {
            if (!filter.test(i)) continue;
            total++;
            sumRating += i.getRating();
            sumWatched += i.getWatchedPct();
        }
        Map<String, Double> stats = new HashMap<>();
        stats.put("totalViews", total);
        stats.put("avgRating", total > 0 ? sumRating / total : 0.0);
        stats.put("avgWatchedPct", total > 0 ? sumWatched / total : 0.0);
        return stats;
    }

    // ========================= R8f) SÉRIES POR GÉNERO/PERÍODO =========================

    /** R8f) Utilizadores que viram séries de um género num período. */
    public List<User> usersWhoWatchedSeriesByGenre(String genreName, LocalDateTime start, LocalDateTime end) {
        Set<String> seen = new HashSet<>();
        List<User> result = new ArrayList<>();
        for (Interaction i : graph.getInteractions()) {
            if (i.getType() != InteractionType.WATCH) continue;
            if (!(i.getContent() instanceof Series)) continue;
            Content c = i.getContent();
            if (c.getGenre() == null || !genreName.equalsIgnoreCase(c.getGenre().getName())) continue;
            if (!inRange(i.getTimestamp(), start, end)) continue;
            if (seen.add(i.getUser().getId())) result.add(i.getUser());
        }
        return result;
    }

    // ========================= R8g) SEGUIDORES MESMO FILME =========================

    /** R8g) Seguidores de um user que viram o mesmo conteúdo num intervalo. */
    public List<User> followersWhoWatchedSameContent(String userId, String contentId,
                                                     LocalDateTime start, LocalDateTime end) {
        List<User> result = new ArrayList<>();
        if (userId == null || contentId == null || !graph.containsVertex(userId)) return result;
        // Encontrar seguidores (quem tem aresta a apontar PARA este user)
        Set<String> followerIds = new HashSet<>();
        for (String id : graph.getAllVertexIds()) {
            if (!(graph.getEntity(id) instanceof User)) continue;
            for (DirectedEdge e : graph.getOutEdges(id)) {
                if (graph.getEntityByIndex(e.to()) != null
                        && graph.getEntityByIndex(e.to()).getId().equals(userId)) {
                    followerIds.add(id);
                    break;
                }
            }
        }
        // Verificar quais viram o conteúdo no intervalo
        for (Interaction i : graph.getInteractions()) {
            if (i.getType() != InteractionType.WATCH) continue;
            if (!contentId.equals(i.getContent().getId())) continue;
            if (!followerIds.contains(i.getUser().getId())) continue;
            if (!inRange(i.getTimestamp(), start, end)) continue;
            result.add(i.getUser());
        }
        return result;
    }

    /** Verifica se um timestamp está dentro do intervalo [start, end]. Nulls são ignorados. */
    private boolean inRange(LocalDateTime ts, LocalDateTime start, LocalDateTime end) {
        if (start != null && ts.isBefore(start)) return false;
        if (end != null && ts.isAfter(end)) return false;
        return true;
    }
}
