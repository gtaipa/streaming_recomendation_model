package streaming.db; // Declara que esta classe pertence ao pacote streaming.db

import edu.princeton.cs.algs4.EdgeWeightedDigraph; // Importa o grafo pesado direcionado da biblioteca algs4
import edu.princeton.cs.algs4.DirectedEdge; // Importa a classe que representa uma aresta direcionada com peso
import edu.princeton.cs.algs4.DijkstraSP; // Importa o algoritmo de Dijkstra para caminhos mais curtos
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths; // Importa o algoritmo BFS para caminhos em grafos direcionados
import edu.princeton.cs.algs4.Digraph; // Importa o grafo direcionado simples (sem pesos) da algs4
import streaming.model.*; // Importa todas as classes do pacote streaming.model (Entity, User, Content, etc.)
import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas
import java.util.*; // Importa as classes de colecoes Java (List, Map, Set, HashMap, HashSet, etc.)

/**
 * Algoritmos e funcionalidades avancadas sobre o grafo de entidades da plataforma.
 */
/*
 * DICIONARIO:
 * - Dijkstra: algoritmo que calcula o caminho mais curto num grafo pesado com pesos nao negativos
 * - BFS (Breadth-First Search): algoritmo de pesquisa em largura, explora todos os vizinhos antes de avancar
 * - subgrafo: parte de um grafo maior, com um subconjunto dos seus vertices e arestas
 * - conectividade: propriedade de um grafo onde existe caminho entre qualquer par de vertices
 * - conectividade fraca: como a conectividade mas ignorando a direcao das arestas
 * - recomendacao: conteudo sugerido a um utilizador com base nas suas relacoes
 * - predicado: expressao que avalia uma condicao e devolve true ou false
 * - Predicate<T>: interface funcional Java para testar uma condicao sobre um objeto do tipo T
 * - HashSet: conjunto sem duplicados que permite verificacoes de pertenca em O(1)
 * - HashMap: mapa de chave-valor com acesso em O(1)
 * - Double.POSITIVE_INFINITY: valor especial que representa infinito positivo
 */
public class GraphAlgorithms { // Define a classe GraphAlgorithms com os algoritmos avancados sobre o grafo

    /** Grafo com entidades e relacoes usado pelos algoritmos. */
    private final StreamingGraph graph; // Atributo que guarda o grafo com as entidades e relacoes

    /** Base de dados com as entidades da plataforma. */
    private final StreamingDB db; // Atributo que guarda a base de dados com as entidades

    /**
     * Cria o motor de algoritmos ligado a um grafo e a uma base de dados.
     *
     * @param graph grafo com entidades e relacoes
     * @param db base de dados com entidades
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial chamado quando se cria um novo objeto
     * - throw: palavra-chave que lanca uma excecao e interrompe a execucao normal
     * - IllegalArgumentException: tipo de excecao para argumentos invalidos
     * - this: referencia ao objeto que esta a ser criado
     */
    public GraphAlgorithms(StreamingGraph graph, StreamingDB db) { // Construtor que cria o motor de algoritmos com grafo e base de dados
        if (graph == null || db == null) // Verifica se os argumentos obrigatorios foram fornecidos
            throw new IllegalArgumentException("graph e db nao podem ser null"); // Lanca excecao se algum for null
        this.graph = graph; // Guarda a referencia ao grafo
        this.db = db; // Guarda a referencia a base de dados
    } // Fim do construtor

    /**
     * Calcula o caminho mais curto entre duas entidades usando Dijkstra.
     *
     * @param fromId identificador da entidade origem
     * @param toId identificador da entidade destino
     * @return lista de entidades no caminho mais curto
     */
    /*
     * DICIONARIO:
     * - Dijkstra: algoritmo que calcula caminhos mais curtos em grafos com pesos nao negativos
     * - dijkstraPath: metodo privado auxiliar que executa Dijkstra e devolve o caminho
     */
    public List<Entity> shortestPath(String fromId, String toId) { // Metodo que calcula o caminho mais curto entre duas entidades
        return dijkstraPath(graph.getGraph(), fromId, toId); // Delega ao metodo auxiliar que executa Dijkstra
    } // Fim do metodo shortestPath

    /**
     * Calcula o custo do caminho mais curto entre duas entidades.
     *
     * @param fromId identificador da entidade origem
     * @param toId identificador da entidade destino
     * @return distancia do caminho mais curto, ou infinito se nao houver caminho
     */
    /*
     * DICIONARIO:
     * - DijkstraSP: classe da algs4 que implementa o algoritmo de Dijkstra
     * - distTo: metodo de DijkstraSP que devolve o custo do caminho para um vertice destino
     * - Double.POSITIVE_INFINITY: valor usado quando nao existe caminho entre os vertices
     */
    public double shortestPathDistance(String fromId, String toId) { // Metodo que calcula o custo do caminho mais curto
        if (!graph.containsVertex(fromId) || !graph.containsVertex(toId)) // Verifica se ambos os vertices existem
            return Double.POSITIVE_INFINITY; // Se algum nao existir, devolve infinito
        DijkstraSP sp = new DijkstraSP(graph.getGraph(), graph.getIndex(fromId)); // Cria o objeto Dijkstra a partir do vertice origem
        return sp.distTo(graph.getIndex(toId)); // Devolve o custo do caminho para o vertice destino
    } // Fim do metodo shortestPathDistance

    /**
     * Calcula o caminho mais curto usando apenas interacoes WATCH.
     *
     * @param fromId identificador da entidade origem
     * @param toId identificador da entidade destino
     * @return lista de entidades no caminho filtrado por WATCH
     */
    /*
     * DICIONARIO:
     * - WATCH: tipo de interacao que representa a visualizacao de um conteudo
     * - shortestPathByType: metodo privado que filtra arestas por tipo e executa Dijkstra
     */
    public List<Entity> shortestPathByWatch(String fromId, String toId) { // Metodo que calcula o caminho mais curto usando apenas arestas de visualizacao
        return shortestPathByType(fromId, toId, InteractionType.WATCH); // Filtra por WATCH e calcula o caminho
    } // Fim do metodo shortestPathByWatch

    /**
     * Calcula o caminho mais curto usando apenas interacoes FOLLOW.
     *
     * @param fromId identificador da entidade origem
     * @param toId identificador da entidade destino
     * @return lista de entidades no caminho filtrado por FOLLOW
     */
    /*
     * DICIONARIO:
     * - FOLLOW: tipo de interacao que representa o seguimento de um utilizador por outro
     */
    public List<Entity> shortestPathByFollow(String fromId, String toId) { // Metodo que calcula o caminho mais curto usando apenas arestas de seguimento
        return shortestPathByType(fromId, toId, InteractionType.FOLLOW); // Filtra por FOLLOW e calcula o caminho
    } // Fim do metodo shortestPathByFollow

    /**
     * Constroi um subgrafo temporario com arestas de um tipo e executa Dijkstra.
     *
     * @param fromId identificador da entidade origem
     * @param toId identificador da entidade destino
     * @param type tipo de interacao usado no filtro
     * @return lista de entidades no caminho filtrado
     */
    /*
     * DICIONARIO:
     * - subgrafo temporario: grafo criado apenas para este calculo, com apenas as arestas do tipo pedido
     * - EdgeWeightedDigraph: grafo pesado direcionado da algs4
     * - temp.addEdge: adiciona uma aresta ao subgrafo temporario
     * - i.getWeight(): devolve o peso da interacao
     */
    private List<Entity> shortestPathByType(String fromId, String toId, InteractionType type) { // Metodo privado que filtra arestas por tipo e calcula o caminho mais curto
        if (!graph.containsVertex(fromId) || !graph.containsVertex(toId)) // Verifica se ambos os vertices existem
            return new ArrayList<>(); // Se algum nao existir, devolve lista vazia
        EdgeWeightedDigraph temp = new EdgeWeightedDigraph(graph.getGraph().V()); // Cria um subgrafo temporario com a mesma capacidade do grafo principal
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getType() != type) continue; // Ignora as interacoes que nao sao do tipo pedido
            String fId = i.getUser().getId(); // Vai buscar o ID do utilizador da interacao
            String tId = i.getContent().getId(); // Vai buscar o ID do conteudo da interacao
            if (graph.containsVertex(fId) && graph.containsVertex(tId)) // Verifica se ambos os vertices existem no grafo
                temp.addEdge(new DirectedEdge(graph.getIndex(fId), graph.getIndex(tId), i.getWeight())); // Adiciona a aresta filtrada ao subgrafo temporario
        } // Fim do ciclo de filtragem de arestas
        return dijkstraPath(temp, fromId, toId); // Executa Dijkstra no subgrafo filtrado e devolve o caminho
    } // Fim do metodo shortestPathByType

    /**
     * Executa Dijkstra num grafo e devolve o caminho como lista de entidades.
     *
     * @param g grafo onde o algoritmo e executado
     * @param fromId identificador da entidade origem
     * @param toId identificador da entidade destino
     * @return lista de entidades no caminho mais curto
     */
    /*
     * DICIONARIO:
     * - DijkstraSP: objeto que executa Dijkstra e guarda os resultados
     * - hasPathTo: metodo de DijkstraSP que verifica se existe caminho para um vertice
     * - pathTo: metodo de DijkstraSP que devolve as arestas do caminho mais curto
     * - sp.pathTo(to): devolve as arestas do caminho mais curto ate ao vertice de indice 'to'
     */
    private List<Entity> dijkstraPath(EdgeWeightedDigraph g, String fromId, String toId) { // Metodo privado que executa Dijkstra e devolve o caminho como lista de entidades
        List<Entity> path = new ArrayList<>(); // Cria a lista do caminho vazia
        if (!graph.containsVertex(fromId) || !graph.containsVertex(toId)) return path; // Se algum vertice nao existir, devolve lista vazia
        int from = graph.getIndex(fromId); // Vai buscar o indice numerico da entidade origem
        int to = graph.getIndex(toId); // Vai buscar o indice numerico da entidade destino
        DijkstraSP sp = new DijkstraSP(g, from); // Executa Dijkstra a partir do vertice origem
        if (!sp.hasPathTo(to)) return path; // Se nao existir caminho, devolve lista vazia
        path.add(graph.getEntityByIndex(from)); // Adiciona a entidade origem ao inicio do caminho
        for (DirectedEdge e : sp.pathTo(to)) { // Percorre as arestas do caminho mais curto
            Entity entity = graph.getEntityByIndex(e.to()); // Vai buscar a entidade destino de cada aresta
            if (entity != null) path.add(entity); // Adiciona cada entidade do caminho ao resultado
        } // Fim do ciclo de construcao do caminho
        return path; // Devolve o caminho como lista de entidades
    } // Fim do metodo dijkstraPath

    /**
     * Extrai um subgrafo com conteudos de um genero especifico.
     *
     * @param genreName nome do genero a filtrar
     * @return subgrafo filtrado por genero
     */
    /*
     * DICIONARIO:
     * - subgrafo: novo grafo criado com apenas um subconjunto dos vertices e arestas do grafo principal
     * - equalsIgnoreCase: comparacao de texto sem diferenciar maiusculas de minusculas
     * - sub.addVertex: adiciona um vertice ao subgrafo
     * - sub.addEdge: adiciona uma aresta ao subgrafo
     */
    public StreamingGraph subgraphByGenre(String genreName) { // Metodo que extrai um subgrafo com conteudos de um genero especifico
        StreamingGraph sub = new StreamingGraph(); // Cria um novo subgrafo vazio
        if (genreName == null) return sub; // Se o nome for null, devolve subgrafo vazio
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            Content c = i.getContent(); // Vai buscar o conteudo da interacao
            if (c == null) continue; // Ignora interacoes sem conteudo
            if (c.getGenre() != null && genreName.equalsIgnoreCase(c.getGenre().getName())) { // Se o genero corresponder...
                sub.addVertex(i.getUser()); // Adiciona o utilizador ao subgrafo
                sub.addVertex(c); // Adiciona o conteudo ao subgrafo
                sub.addEdge(i); // Adiciona a interacao como aresta no subgrafo
            } // Fim da verificacao do genero
        } // Fim do ciclo
        return sub; // Devolve o subgrafo filtrado por genero
    } // Fim do metodo subgraphByGenre

    /**
     * Extrai um subgrafo com conteudos de uma regiao especifica.
     *
     * @param region regiao a filtrar
     * @return subgrafo filtrado por regiao
     */
    public StreamingGraph subgraphByRegion(String region) { // Metodo que extrai um subgrafo com conteudos de uma regiao especifica
        StreamingGraph sub = new StreamingGraph(); // Cria um novo subgrafo vazio
        if (region == null) return sub; // Se a regiao for null, devolve subgrafo vazio
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getContent() == null) continue; // Ignora interacoes sem conteudo
            if (region.equalsIgnoreCase(i.getContent().getRegion())) { // Se a regiao do conteudo corresponder...
                sub.addVertex(i.getUser()); // Adiciona o utilizador ao subgrafo
                sub.addVertex(i.getContent()); // Adiciona o conteudo ao subgrafo
                sub.addEdge(i); // Adiciona a interacao como aresta no subgrafo
            } // Fim da verificacao da regiao
        } // Fim do ciclo
        return sub; // Devolve o subgrafo filtrado por regiao
    } // Fim do metodo subgraphByRegion

    /**
     * Extrai um subgrafo com interacoes acima de um rating minimo.
     *
     * @param minRating rating minimo aceite
     * @return subgrafo filtrado por rating minimo
     */
    /*
     * DICIONARIO:
     * - i.getRating(): devolve a classificacao da interacao
     * - >=: operador de comparacao que verifica se um valor e maior ou igual a outro
     */
    public StreamingGraph subgraphByMinRating(double minRating) { // Metodo que extrai um subgrafo com interacoes com rating acima do minimo
        StreamingGraph sub = new StreamingGraph(); // Cria um novo subgrafo vazio
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getContent() == null) continue; // Ignora interacoes sem conteudo
            if (i.getRating() >= minRating) { // Se o rating for igual ou superior ao minimo...
                sub.addVertex(i.getUser()); // Adiciona o utilizador ao subgrafo
                sub.addVertex(i.getContent()); // Adiciona o conteudo ao subgrafo
                sub.addEdge(i); // Adiciona a interacao como aresta no subgrafo
            } // Fim da verificacao do rating
        } // Fim do ciclo
        return sub; // Devolve o subgrafo filtrado por rating minimo
    } // Fim do metodo subgraphByMinRating

    /**
     * Calcula o caminho mais curto entre dois artistas.
     *
     * @param artistId1 identificador do primeiro artista
     * @param artistId2 identificador do segundo artista
     * @return lista de entidades no caminho entre os artistas
     */
    public List<Entity> shortestPathBetweenArtists(String artistId1, String artistId2) { // Metodo que calcula o caminho mais curto entre dois artistas
        return dijkstraPath(graph.getGraph(), artistId1, artistId2); // Executa Dijkstra no grafo principal entre os dois artistas
    } // Fim do metodo shortestPathBetweenArtists

    /**
     * Verifica se o grafo principal e fracamente conexo.
     *
     * @return {@code true} se o grafo for fracamente conexo
     */
    /*
     * DICIONARIO:
     * - conectividade fraca: existe caminho entre qualquer par de vertices ignorando a direcao das arestas
     */
    public boolean isConnected() { // Metodo que verifica se o grafo principal e fracamente conexo
        return isConnected(graph); // Delega ao metodo que aceita um subgrafo
    } // Fim do metodo isConnected

    /**
     * Verifica se um subgrafo e fracamente conexo.
     *
     * @param subgraph subgrafo a analisar
     * @return {@code true} se o subgrafo for fracamente conexo
     */
    /*
     * DICIONARIO:
     * - Digraph: grafo direcionado simples (sem pesos) da algs4, usado para BFS
     * - BFS: pesquisa em largura que explora todos os vertices alcancaveis
     * - undirected: grafo bidirecional criado adicionando arestas nos dois sentidos para ignorar direcoes
     * - bfs.hasPathTo: verifica se existe caminho BFS para um vertice especifico
     * - getIndexToEntity: devolve o mapa de indices para entidades do subgrafo
     */
    public boolean isConnected(StreamingGraph subgraph) { // Metodo que verifica se um subgrafo e fracamente conexo usando BFS
        if (subgraph.vertexCount() <= 1) return true; // Um grafo com 0 ou 1 vertices e sempre conexo
        Map<Integer, Entity> entities = subgraph.getIndexToEntity(); // Vai buscar o mapa de indices para entidades
        Digraph undirected = new Digraph(subgraph.getGraph().V()); // Cria um grafo direcionado para simular conectividade fraca
        for (int v = 0; v < subgraph.getGraph().V(); v++) { // Percorre todos os vertices do subgrafo
            for (DirectedEdge e : subgraph.getGraph().adj(v)) { // Percorre as arestas de saida de cada vertice
                undirected.addEdge(e.from(), e.to()); // Adiciona a aresta na direcao original
                undirected.addEdge(e.to(), e.from()); // Adiciona a aresta na direcao inversa (para ignorar direcoes)
            } // Fim do ciclo de arestas
        } // Fim do ciclo de vertices
        int start = entities.keySet().iterator().next(); // Vai buscar o indice do primeiro vertice com entidade
        BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(undirected, start); // Executa BFS a partir do primeiro vertice
        for (int idx : entities.keySet()) { // Percorre todos os indices com entidades
            if (!bfs.hasPathTo(idx)) return false; // Se nao existir caminho para algum vertice, o grafo nao e conexo
        } // Fim do ciclo de verificacao
        return true; // Se chegou aqui, o grafo e fracamente conexo
    } // Fim do metodo isConnected (subgrafo)

    /**
     * Recomenda conteudos com base no que os utilizadores seguidos viram.
     *
     * @param userId identificador do utilizador
     * @return lista de conteudos recomendados ordenados por popularidade
     */
    /*
     * DICIONARIO:
     * - HashSet: conjunto sem duplicados para guardar IDs ja vistos
     * - HashMap: mapa para contar quantos seguidos viram cada conteudo
     * - merge: metodo de Map que soma 1 ao contador de cada conteudo
     * - sorted: lista ordenada por numero de utilizadores seguidos que viram cada conteudo
     */
    public List<Content> recommend(String userId) { // Metodo que recomenda conteudos ao utilizador com base nos seus seguidos
        List<Content> recommendations = new ArrayList<>(); // Cria a lista de recomendacoes vazia
        if (userId == null || !graph.containsVertex(userId)) return recommendations; // Se o utilizador nao existir no grafo, devolve lista vazia

        Set<String> alreadyWatched = new HashSet<>(); // Cria o conjunto de conteudos ja vistos pelo utilizador
        for (DirectedEdge e : graph.getOutEdges(userId)) { // Percorre as arestas de saida do utilizador
            Entity target = graph.getEntityByIndex(e.to()); // Vai buscar a entidade destino de cada aresta
            if (target instanceof Content) alreadyWatched.add(target.getId()); // Se for um conteudo, adiciona ao conjunto de ja vistos
        } // Fim do ciclo de conteudos ja vistos

        Map<String, Integer> contentCount = new HashMap<>(); // Mapa para contar quantos seguidos viram cada conteudo
        Map<String, Content> contentMap = new HashMap<>(); // Mapa de IDs para objectos Content para construir o resultado
        for (Entity neighbor : graph.getNeighbors(userId)) { // Percorre os vizinhos diretos do utilizador (os seus seguidos)
            if (!(neighbor instanceof User)) continue; // Ignora vizinhos que nao sejam utilizadores
            for (DirectedEdge e : graph.getOutEdges(neighbor.getId())) { // Percorre os conteudos vistos pelo seguido
                Entity target = graph.getEntityByIndex(e.to()); // Vai buscar a entidade destino
                if (target instanceof Content c && !alreadyWatched.contains(c.getId())) { // Se for um conteudo que o utilizador ainda nao viu...
                    contentCount.merge(c.getId(), 1, Integer::sum); // Incrementa o contador deste conteudo
                    contentMap.put(c.getId(), c); // Guarda a referencia ao conteudo
                } // Fim da verificacao do conteudo
            } // Fim do ciclo de conteudos do seguido
        } // Fim do ciclo de seguidos

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(contentCount.entrySet()); // Cria a lista de pares ID-contador
        sorted.sort((a, b) -> b.getValue() - a.getValue()); // Ordena por popularidade decrescente
        for (Map.Entry<String, Integer> entry : sorted) // Percorre os conteudos ordenados
            recommendations.add(contentMap.get(entry.getKey())); // Adiciona cada conteudo ao resultado
        return recommendations; // Devolve a lista de recomendacoes ordenadas
    } // Fim do metodo recommend

    /**
     * Calcula estatisticas de visualizacao de um conteudo entre duas datas.
     *
     * @param contentId identificador do conteudo
     * @param start inicio do intervalo, ou {@code null} para sem limite
     * @param end fim do intervalo, ou {@code null} para sem limite
     * @return mapa com totalViews, avgRating e avgWatchedPct
     */
    /*
     * DICIONARIO:
     * - computeStats: metodo privado que calcula estatisticas para interacoes que satisfacam um predicado
     * - lambda: expressao anonima que define o predicado de filtragem
     * - inRange: metodo privado que verifica se um timestamp esta dentro do intervalo
     */
    public Map<String, Double> getViewStats(String contentId, LocalDateTime start, LocalDateTime end) { // Metodo que calcula estatisticas de visualizacao de um conteudo num intervalo de datas
        return computeStats(i -> // Define o predicado de filtragem
                contentId != null
                        && contentId.equals(i.getContent().getId()) // Filtra pelo ID do conteudo
                        && i.getType() == InteractionType.WATCH // So conta visualizacoes
                        && inRange(i.getTimestamp(), start, end)); // So conta dentro do intervalo
    } // Fim do metodo getViewStats

    /**
     * Calcula estatisticas de visualizacao filtradas por genero e datas.
     *
     * @param genreName nome do genero a filtrar
     * @param start inicio do intervalo, ou {@code null} para sem limite
     * @param end fim do intervalo, ou {@code null} para sem limite
     * @return mapa com estatisticas agregadas
     */
    public Map<String, Double> getViewStatsByGenre(String genreName, LocalDateTime start, LocalDateTime end) { // Metodo que calcula estatisticas de visualizacao por genero e datas
        return computeStats(i -> // Define o predicado de filtragem por genero
                genreName != null
                        && i.getType() == InteractionType.WATCH // So conta visualizacoes
                        && i.getContent() != null
                        && i.getContent().getGenre() != null
                        && genreName.equalsIgnoreCase(i.getContent().getGenre().getName()) // Filtra pelo nome do genero
                        && inRange(i.getTimestamp(), start, end)); // So conta dentro do intervalo
    } // Fim do metodo getViewStatsByGenre

    /**
     * Calcula estatisticas para interacoes que satisfacam um predicado.
     *
     * @param filter predicado usado para selecionar interacoes
     * @return mapa com totalViews, avgRating e avgWatchedPct
     */
    /*
     * DICIONARIO:
     * - Predicate: interface funcional Java que representa uma condicao sobre um objeto
     * - filter.test(i): aplica o predicado a interacao i e devolve true ou false
     * - sumRating: soma total dos ratings para calcular a media
     * - sumWatched: soma total das percentagens vistas para calcular a media
     * - stats.put: guarda uma estatistica no mapa de resultados
     */
    private Map<String, Double> computeStats(java.util.function.Predicate<Interaction> filter) { // Metodo privado que calcula estatisticas para interacoes filtradas
        double total = 0, sumRating = 0, sumWatched = 0; // Inicializa os acumuladores de estatisticas
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getContent() == null) continue; // Ignora interacoes sem conteudo
            if (!filter.test(i)) continue; // Aplica o predicado e ignora as que nao passam
            total++; // Incrementa o contador de visualizacoes
            sumRating += i.getRating(); // Acumula o rating desta interacao
            sumWatched += i.getWatchedPct(); // Acumula a percentagem vista desta interacao
        } // Fim do ciclo
        Map<String, Double> stats = new HashMap<>(); // Cria o mapa de estatisticas
        stats.put("totalViews", total); // Guarda o total de visualizacoes
        stats.put("avgRating", total > 0 ? sumRating / total : 0.0); // Guarda o rating medio (0 se nao houver visualizacoes)
        stats.put("avgWatchedPct", total > 0 ? sumWatched / total : 0.0); // Guarda a percentagem media vista (0 se nao houver visualizacoes)
        return stats; // Devolve o mapa de estatisticas
    } // Fim do metodo computeStats

    /**
     * Devolve utilizadores que viram series de um genero num periodo.
     *
     * @param genreName nome do genero a filtrar
     * @param start inicio do periodo, ou {@code null} para sem limite
     * @param end fim do periodo, ou {@code null} para sem limite
     * @return lista de utilizadores encontrados
     */
    /*
     * DICIONARIO:
     * - instanceof: verifica se um objeto e de um determinado tipo
     * - seen: conjunto para evitar duplicados no resultado
     * - seen.add: adiciona o ID ao conjunto e devolve true se nao existia
     */
    public List<User> usersWhoWatchedSeriesByGenre(String genreName, LocalDateTime start, LocalDateTime end) { // Metodo que devolve utilizadores que viram series de um genero num periodo
        Set<String> seen = new HashSet<>(); // Conjunto para evitar adicionar o mesmo utilizador duas vezes
        List<User> result = new ArrayList<>(); // Cria a lista de resultados vazia
        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getType() != InteractionType.WATCH) continue; // Ignora interacoes que nao sejam visualizacoes
            if (!(i.getContent() instanceof Series)) continue; // Ignora conteudos que nao sejam series
            Content c = i.getContent(); // Vai buscar o conteudo da interacao
            if (c.getGenre() == null || !genreName.equalsIgnoreCase(c.getGenre().getName())) continue; // Ignora se o genero nao corresponder
            if (!inRange(i.getTimestamp(), start, end)) continue; // Ignora se nao estiver dentro do intervalo de datas
            if (seen.add(i.getUser().getId())) result.add(i.getUser()); // Adiciona o utilizador ao resultado se ainda nao foi adicionado
        } // Fim do ciclo
        return result; // Devolve a lista de utilizadores encontrados
    } // Fim do metodo usersWhoWatchedSeriesByGenre

    /**
     * Devolve seguidores de um utilizador que viram o mesmo conteudo num intervalo.
     *
     * @param userId identificador do utilizador seguido
     * @param contentId identificador do conteudo
     * @param start inicio do intervalo, ou {@code null} para sem limite
     * @param end fim do intervalo, ou {@code null} para sem limite
     * @return lista de seguidores que viram o conteudo
     */
    /*
     * DICIONARIO:
     * - followerIds: conjunto com os IDs dos seguidores do utilizador indicado
     * - seguidor: utilizador que tem uma aresta a apontar para o utilizador indicado
     */
    public List<User> followersWhoWatchedSameContent(String userId, String contentId, // Metodo que devolve seguidores que viram o mesmo conteudo num intervalo
                                                     LocalDateTime start, LocalDateTime end) {
        List<User> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (userId == null || contentId == null || !graph.containsVertex(userId)) return result; // Valida os parametros

        Set<String> followerIds = new HashSet<>(); // Conjunto para guardar os IDs dos seguidores
        for (String id : graph.getAllVertexIds()) { // Percorre todos os vertices do grafo
            if (!(graph.getEntity(id) instanceof User)) continue; // Ignora vertices que nao sejam utilizadores
            for (DirectedEdge e : graph.getOutEdges(id)) { // Percorre as arestas de saida do vertice
                if (graph.getEntityByIndex(e.to()) != null
                        && graph.getEntityByIndex(e.to()).getId().equals(userId)) { // Se a aresta aponta para o utilizador indicado...
                    followerIds.add(id); // Adiciona o ID do seguidor ao conjunto
                    break; // Ja encontramos a aresta, nao e necessario continuar
                } // Fim da verificacao do destino
            } // Fim do ciclo de arestas
        } // Fim do ciclo de vertices

        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getType() != InteractionType.WATCH) continue; // Ignora interacoes que nao sejam visualizacoes
            if (i.getContent() == null) continue; // Ignora interacoes sem conteudo
            if (!contentId.equals(i.getContent().getId())) continue; // Ignora se nao for o conteudo indicado
            if (!followerIds.contains(i.getUser().getId())) continue; // Ignora se o utilizador nao for seguidor
            if (!inRange(i.getTimestamp(), start, end)) continue; // Ignora se nao estiver dentro do intervalo
            result.add(i.getUser()); // Adiciona o seguidor ao resultado
        } // Fim do ciclo de verificacao de interacoes
        return result; // Devolve a lista de seguidores que viram o conteudo
    } // Fim do metodo followersWhoWatchedSameContent

    /**
     * Verifica se um timestamp esta dentro do intervalo indicado.
     *
     * @param ts timestamp a verificar
     * @param start inicio do intervalo, ou {@code null} para sem limite inferior
     * @param end fim do intervalo, ou {@code null} para sem limite superior
     * @return {@code true} se o timestamp estiver dentro do intervalo
     */
    /*
     * DICIONARIO:
     * - isBefore: metodo de LocalDateTime que verifica se uma data-hora e anterior a outra
     * - isAfter: metodo de LocalDateTime que verifica se uma data-hora e posterior a outra
     * - null: quando start ou end sao null, o limite correspondente e ignorado
     */
    private boolean inRange(LocalDateTime ts, LocalDateTime start, LocalDateTime end) { // Metodo privado que verifica se um timestamp esta dentro do intervalo
        if (start != null && ts.isBefore(start)) return false; // Se o timestamp for anterior ao inicio, esta fora do intervalo
        if (end != null && ts.isAfter(end)) return false; // Se o timestamp for posterior ao fim, esta fora do intervalo
        return true; // Se chegou aqui, o timestamp esta dentro do intervalo
    } // Fim do metodo inRange

} // Fim da classe GraphAlgorithms
