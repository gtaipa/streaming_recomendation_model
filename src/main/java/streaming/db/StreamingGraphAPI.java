package streaming.db;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.DijkstraSP;

import streaming.model.Artist;
import streaming.model.Content;
import streaming.model.Entity;
import streaming.model.Interaction;
import streaming.model.InteractionType;
import streaming.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * R7 - API pública para gestão de relações entre entidades no grafo.
 *
 * Esta classe serve de "fachada" (Facade pattern) entre o código da aplicação
 * e o grafo interno (StreamingGraph).
 *
 * RESPONSABILIDADES:
 * - Registar entidades no grafo (vértices)
 * - Criar relações entre entidades (arestas): watch, rate, follow, participação de artistas
 * - Consultar relações (quem viu o quê, quem segue quem, etc.)
 * - Remover entidades e relações
 * - Listar informação do grafo
 *
 * PORQUÊ SEPARAR DO StreamingGraph?
 * - O StreamingGraph lida com a mecânica interna (índices, algs4, rebuild, etc.)
 * - Esta API lida com a lógica de negócio (validações, integração com StreamingDB, etc.)
 * - Na defesa, mostra que sabes aplicar separação de responsabilidades
 *
 * COMO USAR:
 * StreamingDB db = new StreamingDB();
 * StreamingGraph graph = new StreamingGraph();
 * StreamingGraphAPI api = new StreamingGraphAPI(db, graph);
 *
 * // Registar entidades
 * api.registerUser(user);
 * api.registerContent(movie);
 *
 * // Criar relações
 * api.addWatch(user.getId(), movie.getId(), 100.0, 8.5);
 * api.addFollow(user1.getId(), user2.getId());
 */
public class StreamingGraphAPI {

    // ATRIBUTOS


    /**
     * Referência à base de dados (Fase 1).
     * Usamos para ir buscar entidades pelos IDs e garantir consistência.
     */
    private final StreamingDB db;

    /**
     * O grafo onde armazenamos as relações.
     */
    private final StreamingGraph graph;

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    /**
     * Cria a API do grafo ligada a uma base de dados e a um grafo.
     *
     * @param db    a base de dados com as entidades (Users, Contents, Artists, etc.)
     * @param graph o grafo onde vamos armazenar as relações
     */
    public StreamingGraphAPI(StreamingDB db, StreamingGraph graph) {
        if (db == null || graph == null) {
            throw new IllegalArgumentException("StreamingDB e StreamingGraph não podem ser null");
        }
        this.db = db;
        this.graph = graph;
    }

    // =====================================================
    // REGISTO DE ENTIDADES (Vértices)
    // =====================================================

    /**
     * Regista um User no grafo como vértice.
     * O User deve já existir na StreamingDB.
     *
     * @param userId o ID do utilizador
     * @return true se foi registado com sucesso, false se não existe na DB ou já está no grafo
     */
    public boolean registerUser(String userId) {
        if (userId == null) return false;
        User u = db.getUser(userId);
        if (u == null) return false;
        graph.addVertex(u);
        return true;
    }

    /**
     * Regista um Content (Movie, Series, Documentary) no grafo como vértice.
     *
     * @param contentId o ID do conteúdo
     * @return true se foi registado com sucesso
     */
    public boolean registerContent(String contentId) {
        if (contentId == null) return false;
        Content c = db.getContent(contentId);
        if (c == null) return false;
        graph.addVertex(c);
        return true;
    }

    /**
     * Regista um Artist no grafo como vértice.
     *
     * @param artistId o ID do artista
     * @return true se foi registado com sucesso
     */
    public boolean registerArtist(String artistId) {
        if (artistId == null) return false;
        Artist a = db.getArtist(artistId);
        if (a == null) return false;
        graph.addVertex(a);
        return true;
    }

    /**
     * Regista todas as entidades da DB no grafo de uma só vez.
     * Útil para inicializar o grafo com dados já existentes.
     *
     * @return o número total de entidades registadas
     */
    public int registerAllEntities() {
        int count = 0;

        for (User u : db.listUsers()) {
            graph.addVertex(u);
            count++;
        }
        for (Content c : db.listContents()) {
            graph.addVertex(c);
            count++;
        }
        for (Artist a : db.listArtists()) {
            graph.addVertex(a);
            count++;
        }

        return count;
    }

    // =====================================================
    // CRIAR RELAÇÕES (Arestas) - Interações User → Content
    // =====================================================

    /**
     * Regista que um utilizador VIU um conteúdo.
     *
     * Cria uma aresta: User → Content com tipo WATCH.
     * O peso da aresta é o rating dado pelo utilizador.
     *
     * @param userId     o ID do utilizador
     * @param contentId  o ID do conteúdo visto
     * @param watchedPct percentagem vista (0-100)
     * @param rating     classificação dada (ex: 8.5)
     * @return true se a relação foi criada com sucesso
     */
    public boolean addWatch(String userId, String contentId, double watchedPct, double rating) {
        User u = db.getUser(userId);
        Content c = db.getContent(contentId);
        if (u == null || c == null) return false;

        Interaction interaction = new Interaction(
                u, c, InteractionType.WATCH,
                LocalDateTime.now(),
                watchedPct, rating, rating  // peso = rating
        );

        graph.addEdge(interaction);
        return true;
    }

    /**
     * Regista que um utilizador VIU um conteúdo com timestamp específico.
     * Útil para importar dados históricos de ficheiros.
     *
     * @param userId     o ID do utilizador
     * @param contentId  o ID do conteúdo
     * @param watchedPct percentagem vista
     * @param rating     classificação
     * @param timestamp  data/hora da visualização
     * @return true se a relação foi criada com sucesso
     */
    public boolean addWatch(String userId, String contentId, double watchedPct,
                            double rating, LocalDateTime timestamp) {
        User u = db.getUser(userId);
        Content c = db.getContent(contentId);
        if (u == null || c == null) return false;

        Interaction interaction = new Interaction(
                u, c, InteractionType.WATCH,
                timestamp,
                watchedPct, rating, rating
        );

        graph.addEdge(interaction);
        return true;
    }

    /**
     * Regista que um utilizador CLASSIFICOU (rated) um conteúdo.
     *
     * @param userId    o ID do utilizador
     * @param contentId o ID do conteúdo
     * @param rating    a classificação (ex: 9.0)
     * @return true se a relação foi criada com sucesso
     */
    public boolean addRating(String userId, String contentId, double rating) {
        User u = db.getUser(userId);
        Content c = db.getContent(contentId);
        if (u == null || c == null) return false;

        Interaction interaction = new Interaction(
                u, c, InteractionType.RATE,
                LocalDateTime.now(),
                0, rating, rating
        );

        graph.addEdge(interaction);
        return true;
    }

    /**
     * Regista que um utilizador CLASSIFICOU um conteúdo com timestamp específico.
     * Útil para importar dados históricos de ficheiros.
     *
     * @param userId    o ID do utilizador
     * @param contentId o ID do conteúdo
     * @param rating    a classificação
     * @param timestamp data/hora da classificação
     * @return true se a relação foi criada com sucesso
     */
    public boolean addRating(String userId, String contentId, double rating, LocalDateTime timestamp) {
        User u = db.getUser(userId);
        Content c = db.getContent(contentId);
        if (u == null || c == null) return false;

        Interaction interaction = new Interaction(
                u, c, InteractionType.RATE,
                timestamp,
                0, rating, rating
        );

        graph.addEdge(interaction);
        return true;
    }

    /**
     * Regista que um utilizador CLICOU num conteúdo (mostrou interesse).
     *
     * @param userId    o ID do utilizador
     * @param contentId o ID do conteúdo
     * @return true se a relação foi criada com sucesso
     */
    public boolean addClick(String userId, String contentId) {
        User u = db.getUser(userId);
        Content c = db.getContent(contentId);
        if (u == null || c == null) return false;

        Interaction interaction = new Interaction(
                u, c, InteractionType.CLICK,
                LocalDateTime.now(),
                0, 0, 1.0  // peso fixo de 1.0 para clicks
        );

        graph.addEdge(interaction);
        return true;
    }

    /**
     * Regista que um utilizador CLICOU num conteúdo com timestamp específico.
     * Útil para importar dados históricos de ficheiros.
     *
     * @param userId    o ID do utilizador
     * @param contentId o ID do conteúdo
     * @param timestamp data/hora do clique
     * @return true se a relação foi criada com sucesso
     */
    public boolean addClick(String userId, String contentId, LocalDateTime timestamp) {
        User u = db.getUser(userId);
        Content c = db.getContent(contentId);
        if (u == null || c == null) return false;

        Interaction interaction = new Interaction(
                u, c, InteractionType.CLICK,
                timestamp,
                0, 0, 1.0  // peso fixo de 1.0 para clicks
        );

        graph.addEdge(interaction);
        return true;
    }

    // =====================================================
    // CRIAR RELAÇÕES (Arestas) - Follow entre Users
    // =====================================================

    /**
     * Regista que um utilizador SEGUE outro utilizador.
     *
     * Cria uma aresta direcionada: follower → followed com peso 1.0
     * Também atualiza a lista de following no objeto User (Fase 1).
     *
     * @param followerId o ID de quem segue
     * @param followedId o ID de quem é seguido
     * @return true se a relação foi criada com sucesso
     */
    public boolean addFollow(String followerId, String followedId) {
        User follower = db.getUser(followerId);
        User followed = db.getUser(followedId);
        if (follower == null || followed == null) return false;
        if (followerId.equals(followedId)) return false;  // não pode seguir a si próprio

        // Adicionar no grafo
        // (FOLLOW passa a ser registado como Interaction, ver abaixo)

        // Também atualizar a lista de following no User (consistência com Fase 1)
        follower.follow(followed);

        // Guardar como Interaction do tipo FOLLOW (para ficar visivel em graph.getInteractions())
        Interaction interaction = new Interaction(follower, followed, LocalDateTime.now(), 1.0);
        graph.addEdge(interaction);

        return true;
    }

    // =====================================================
    // CRIAR RELAÇÕES (Arestas) - Artist ↔ Content
    // =====================================================

    /**
     * Regista que um artista PARTICIPOU num conteúdo.
     *
     * Cria uma aresta: Artist → Content com peso 1.0
     * Útil para ligar atores/realizadores aos filmes/séries.
     *
     * @param artistId  o ID do artista
     * @param contentId o ID do conteúdo
     * @param weight    o peso da relação (pode representar importância do papel)
     * @return true se a relação foi criada com sucesso
     */
    public boolean addArtistToContent(String artistId, String contentId, double weight) {
        Artist a = db.getArtist(artistId);
        Content c = db.getContent(contentId);
        if (a == null || c == null) return false;

        graph.addEdge(a, c, weight);
        return true;
    }

    /**
     * Versão simplificada com peso 1.0.
     */
    public boolean addArtistToContent(String artistId, String contentId) {
        return addArtistToContent(artistId, contentId, 1.0);
    }

    // =====================================================
    // REMOVER ENTIDADES DO GRAFO
    // =====================================================

    /**
     * Remove um utilizador do grafo (e todas as suas relações).
     *
     * NOTA: Isto NÃO remove o User da StreamingDB (Fase 1).
     * Remove apenas do grafo.
     *
     * @param userId o ID do utilizador
     * @return true se foi removido
     */
    public boolean removeUserFromGraph(String userId) {
        return graph.removeVertex(userId);
    }

    /**
     * Remove um conteúdo do grafo (e todas as suas relações).
     *
     * @param contentId o ID do conteúdo
     * @return true se foi removido
     */
    public boolean removeContentFromGraph(String contentId) {
        return graph.removeVertex(contentId);
    }

    /**
     * Remove um artista do grafo (e todas as suas relações).
     *
     * @param artistId o ID do artista
     * @return true se foi removido
     */
    public boolean removeArtistFromGraph(String artistId) {
        return graph.removeVertex(artistId);
    }

    // =====================================================
    // CONSULTAS DE RELAÇÕES
    // =====================================================

    /**
     * Devolve todos os conteúdos que um utilizador viu/interagiu.
     *
     * @param userId o ID do utilizador
     * @return lista de Contents ligados a este User
     */
    public List<Content> getContentsForUser(String userId) {
        List<Content> result = new ArrayList<>();
        if (userId == null || !graph.containsVertex(userId)) return result;

        for (DirectedEdge e : graph.getOutEdges(userId)) {
            Entity target = graph.getEntityByIndex(e.to());
            if (target instanceof Content c) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Devolve todos os utilizadores que interagiram com um conteúdo.
     *
     * NOTA: Como o grafo é direcionado (User → Content), precisamos de
     * percorrer todos os vértices e ver quais apontam para este conteúdo.
     *
     * @param contentId o ID do conteúdo
     * @return lista de Users que interagiram com este conteúdo
     */
    public List<User> getUsersForContent(String contentId) {
        List<User> result = new ArrayList<>();
        if (contentId == null || !graph.containsVertex(contentId)) return result;

        int contentIdx = graph.getIndex(contentId);

        // Percorrer todos os vértices e ver quais têm aresta para este conteúdo
        for (String id : graph.getAllVertexIds()) {
            for (DirectedEdge e : graph.getOutEdges(id)) {
                if (e.to() == contentIdx) {
                    Entity source = graph.getEntity(id);
                    if (source instanceof User u) {
                        result.add(u);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Devolve todos os utilizadores que um User segue.
     *
     * @param userId o ID do utilizador
     * @return lista de Users seguidos
     */
    public List<User> getFollowing(String userId) {
        List<User> result = new ArrayList<>();
        if (userId == null || !graph.containsVertex(userId)) return result;

        for (DirectedEdge e : graph.getOutEdges(userId)) {
            Entity target = graph.getEntityByIndex(e.to());
            if (target instanceof User u) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Devolve todos os seguidores de um User.
     *
     * (Quem tem aresta a apontar PARA este User)
     *
     * @param userId o ID do utilizador
     * @return lista de Users que seguem este User
     */
    public List<User> getFollowers(String userId) {
        List<User> result = new ArrayList<>();
        if (userId == null || !graph.containsVertex(userId)) return result;

        int userIdx = graph.getIndex(userId);

        for (String id : graph.getAllVertexIds()) {
            Entity source = graph.getEntity(id);
            if (!(source instanceof User)) continue;

            for (DirectedEdge e : graph.getOutEdges(id)) {
                if (e.to() == userIdx) {
                    result.add((User) source);
                    break;  // já encontrámos a aresta, não precisamos de continuar
                }
            }
        }
        return result;
    }

    /**
     * Devolve todos os conteúdos em que um artista participou.
     *
     * @param artistId o ID do artista
     * @return lista de Contents
     */
    public List<Content> getContentsForArtist(String artistId) {
        List<Content> result = new ArrayList<>();
        if (artistId == null || !graph.containsVertex(artistId)) return result;

        for (DirectedEdge e : graph.getOutEdges(artistId)) {
            Entity target = graph.getEntityByIndex(e.to());
            if (target instanceof Content c) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Devolve todos os artistas que participaram num conteúdo.
     *
     * @param contentId o ID do conteúdo
     * @return lista de Artists
     */
    public List<Artist> getArtistsForContent(String contentId) {
        List<Artist> result = new ArrayList<>();
        if (contentId == null || !graph.containsVertex(contentId)) return result;

        int contentIdx = graph.getIndex(contentId);

        for (String id : graph.getAllVertexIds()) {
            for (DirectedEdge e : graph.getOutEdges(id)) {
                if (e.to() == contentIdx) {
                    Entity source = graph.getEntity(id);
                    if (source instanceof Artist a) {
                        result.add(a);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Devolve todas as interações de um utilizador.
     *
     * @param userId o ID do utilizador
     * @return lista de Interactions deste User
     */
    public List<Interaction> getInteractionsForUser(String userId) {
        List<Interaction> result = new ArrayList<>();
        if (userId == null) return result;

        for (Interaction i : graph.getInteractions()) {
            if (userId.equals(i.getUser().getId())) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Devolve todas as interações com um conteúdo.
     *
     * @param contentId o ID do conteúdo
     * @return lista de Interactions com este Content
     */
    public List<Interaction> getInteractionsForContent(String contentId) {
        List<Interaction> result = new ArrayList<>();
        if (contentId == null) return result;

        for (Interaction i : graph.getInteractions()) {
            if (i.getContent() != null && contentId.equals(i.getContent().getId())) {
                result.add(i);
            }
        }
        return result;
    }

    // =====================================================
    // INFORMAÇÃO GERAL DO GRAFO
    // =====================================================

    /**
     * Número total de vértices no grafo.
     */
    public int totalVertices() {
        return graph.vertexCount();
    }

    /**
     * Número total de arestas no grafo.
     */
    public int totalEdges() {
        return graph.edgeCount();
    }

    /**
     * Número total de interações registadas.
     */
    public int totalInteractions() {
        return graph.getInteractions().size();
    }

    /**
     * Verifica se uma entidade está registada no grafo.
     *
     * @param entityId o ID da entidade
     * @return true se está no grafo
     */
    public boolean isRegistered(String entityId) {
        return graph.containsVertex(entityId);
    }

    /**
     * Devolve o StreamingGraph interno.
     * Necessário para o R8 (algoritmos de caminhos, subgrafos, etc.)
     *
     * @return o grafo
     */
    public StreamingGraph getGraph() {
        return graph;
    }

    /**
     * Representação textual da API (delega ao grafo).
     */
    @Override
    public String toString() {
        return "StreamingGraphAPI:\n" + graph.toString();
    }
}