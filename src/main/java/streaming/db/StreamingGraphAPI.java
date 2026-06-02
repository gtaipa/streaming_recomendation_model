package streaming.db; // Declara que esta classe pertence ao pacote streaming.db

import edu.princeton.cs.algs4.DirectedEdge; // Importa a classe que representa uma aresta direcionada com peso
import edu.princeton.cs.algs4.DijkstraSP; // Importa o algoritmo de Dijkstra para caminhos mais curtos

import streaming.model.Artist; // Importa a classe que representa um artista da plataforma
import streaming.model.Content; // Importa a classe abstrata base para todos os tipos de conteudo
import streaming.model.Entity; // Importa a classe base de todas as entidades do sistema
import streaming.model.Interaction; // Importa a classe que representa uma interacao entre utilizador e conteudo
import streaming.model.InteractionType; // Importa o enum com os tipos de interacao possiveis
import streaming.model.User; // Importa a classe que representa um utilizador da plataforma

import java.time.LocalDateTime; // Importa a classe que representa data e hora juntas
import java.util.ArrayList; // Importa a classe que cria listas dinamicas que crescem conforme necessario
import java.util.List; // Importa a interface que define o comportamento de uma lista ordenada de elementos

/**
 * API publica para gestao de relacoes entre entidades no grafo.
 *
 * Esta classe serve de fachada (Facade pattern) entre o codigo da aplicacao
 * e o grafo interno (StreamingGraph).
 */
/*
 * DICIONARIO:
 * - Facade pattern: padrao de design que simplifica o acesso a um subsistema complexo
 * - final: modificador que impede a alteracao do valor de um atributo apos a inicializacao
 * - StreamingDB: base de dados em memoria com as entidades da plataforma
 * - StreamingGraph: grafo direcionado onde sao guardadas as relacoes entre entidades
 * - vertice: no do grafo que representa uma entidade
 * - aresta: ligacao entre dois vertices com um peso
 * - IllegalArgumentException: excecao lancada quando um argumento invalido e passado
 * - Interaction: registo de uma acao de um utilizador sobre um conteudo
 * - LocalDateTime: tipo que representa data e hora sem fuso horario
 */
public class StreamingGraphAPI { // Define a classe StreamingGraphAPI que serve de fachada para o grafo de entidades

    /** Referencia a base de dados da fase 1. */
    private final StreamingDB db; // Atributo que guarda a referencia a base de dados de entidades

    /** O grafo onde sao armazenadas as relacoes. */
    private final StreamingGraph graph; // Atributo que guarda a referencia ao grafo de relacoes

    /**
     * Cria a API do grafo ligada a uma base de dados e a um grafo.
     *
     * @param db a base de dados com as entidades
     * @param graph o grafo onde sao armazenadas as relacoes
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial chamado quando se cria um novo objeto
     * - throw: palavra-chave que lanca uma excecao e interrompe a execucao normal
     * - IllegalArgumentException: tipo de excecao para argumentos invalidos
     * - this: referencia ao objeto que esta a ser criado
     */
    public StreamingGraphAPI(StreamingDB db, StreamingGraph graph) { // Construtor que cria a API ligada a uma base de dados e a um grafo
        if (db == null || graph == null) { // Verifica se os argumentos obrigatorios foram fornecidos
            throw new IllegalArgumentException("StreamingDB e StreamingGraph nao podem ser null"); // Lanca excecao se algum for null
        } // Fim da verificacao
        this.db = db; // Guarda a referencia a base de dados
        this.graph = graph; // Guarda a referencia ao grafo
    } // Fim do construtor

    /**
     * Regista um utilizador no grafo como vertice.
     *
     * @param userId o ID do utilizador
     * @return {@code true} se foi registado com sucesso
     */
    /*
     * DICIONARIO:
     * - db.getUser: vai buscar o utilizador pelo ID na base de dados
     * - graph.addVertex: adiciona a entidade como vertice no grafo
     */
    public boolean registerUser(String userId) { // Metodo que regista um utilizador no grafo como vertice
        if (userId == null) return false; // Rejeita IDs nulos
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        if (u == null) return false; // Se nao existir na DB, nao pode ser registado
        graph.addVertex(u); // Adiciona o utilizador como vertice no grafo
        return true; // Indica que o registo foi bem sucedido
    } // Fim do metodo registerUser

    /**
     * Regista um conteudo no grafo como vertice.
     *
     * @param contentId o ID do conteudo
     * @return {@code true} se foi registado com sucesso
     */
    public boolean registerContent(String contentId) { // Metodo que regista um conteudo no grafo como vertice
        if (contentId == null) return false; // Rejeita IDs nulos
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (c == null) return false; // Se nao existir na DB, nao pode ser registado
        graph.addVertex(c); // Adiciona o conteudo como vertice no grafo
        return true; // Indica que o registo foi bem sucedido
    } // Fim do metodo registerContent

    /**
     * Regista um artista no grafo como vertice.
     *
     * @param artistId o ID do artista
     * @return {@code true} se foi registado com sucesso
     */
    public boolean registerArtist(String artistId) { // Metodo que regista um artista no grafo como vertice
        if (artistId == null) return false; // Rejeita IDs nulos
        Artist a = db.getArtist(artistId); // Vai buscar o artista na base de dados
        if (a == null) return false; // Se nao existir na DB, nao pode ser registado
        graph.addVertex(a); // Adiciona o artista como vertice no grafo
        return true; // Indica que o registo foi bem sucedido
    } // Fim do metodo registerArtist

    /**
     * Regista todas as entidades da base de dados no grafo de uma so vez.
     *
     * @return o numero total de entidades registadas
     */
    /*
     * DICIONARIO:
     * - db.listUsers: devolve todos os utilizadores ativos
     * - db.listContents: devolve todos os conteudos
     * - db.listArtists: devolve todos os artistas
     * - count: contador de entidades registadas
     */
    public int registerAllEntities() { // Metodo que regista todas as entidades da base de dados no grafo
        int count = 0; // Inicializa o contador de entidades registadas

        for (User u : db.listUsers()) { // Percorre todos os utilizadores ativos
            graph.addVertex(u); // Adiciona cada utilizador como vertice
            count++; // Incrementa o contador
        } // Fim do ciclo de utilizadores
        for (Content c : db.listContents()) { // Percorre todos os conteudos
            graph.addVertex(c); // Adiciona cada conteudo como vertice
            count++; // Incrementa o contador
        } // Fim do ciclo de conteudos
        for (Artist a : db.listArtists()) { // Percorre todos os artistas
            graph.addVertex(a); // Adiciona cada artista como vertice
            count++; // Incrementa o contador
        } // Fim do ciclo de artistas

        return count; // Devolve o total de entidades registadas
    } // Fim do metodo registerAllEntities

    /**
     * Regista que um utilizador viu um conteudo.
     *
     * @param userId o ID do utilizador
     * @param contentId o ID do conteudo visto
     * @param watchedPct percentagem vista (0-100)
     * @param rating classificacao dada pelo utilizador
     * @return {@code true} se a relacao foi criada com sucesso
     */
    /*
     * DICIONARIO:
     * - WATCH: tipo de interacao que representa a visualizacao de um conteudo
     * - Interaction: objeto que encapsula todos os dados de uma interacao
     * - LocalDateTime.now(): devolve a data e hora atuais
     * - weight: peso da aresta no grafo; aqui igual ao rating
     */
    public boolean addWatch(String userId, String contentId, double watchedPct, double rating) { // Metodo que regista a visualizacao de um conteudo por um utilizador
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (u == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        Interaction interaction = new Interaction( // Cria a interacao com todos os dados da visualizacao
                u, c, InteractionType.WATCH,
                LocalDateTime.now(), // Usa o momento atual como timestamp
                watchedPct, rating, rating // O peso da aresta e igual ao rating
        );

        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addWatch

    /**
     * Regista que um utilizador viu um conteudo com timestamp especifico.
     *
     * @param userId o ID do utilizador
     * @param contentId o ID do conteudo
     * @param watchedPct percentagem vista
     * @param rating classificacao dada
     * @param timestamp data e hora da visualizacao
     * @return {@code true} se a relacao foi criada com sucesso
     */
    public boolean addWatch(String userId, String contentId, double watchedPct, // Metodo que regista a visualizacao com um timestamp especifico (util para importar dados historicos)
                            double rating, LocalDateTime timestamp) {
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (u == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        Interaction interaction = new Interaction( // Cria a interacao com o timestamp indicado
                u, c, InteractionType.WATCH,
                timestamp, // Usa o timestamp fornecido em vez do momento atual
                watchedPct, rating, rating // O peso da aresta e igual ao rating
        );

        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addWatch (com timestamp)

    /**
     * Regista que um utilizador classificou um conteudo.
     *
     * @param userId o ID do utilizador
     * @param contentId o ID do conteudo
     * @param rating a classificacao atribuida
     * @return {@code true} se a relacao foi criada com sucesso
     */
    /*
     * DICIONARIO:
     * - RATE: tipo de interacao que representa a classificacao de um conteudo
     */
    public boolean addRating(String userId, String contentId, double rating) { // Metodo que regista a classificacao de um conteudo por um utilizador
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (u == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        Interaction interaction = new Interaction( // Cria a interacao de classificacao
                u, c, InteractionType.RATE,
                LocalDateTime.now(), // Usa o momento atual como timestamp
                0, rating, rating // watchedPct e 0 pois e so uma classificacao
        );

        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addRating

    /**
     * Regista que um utilizador classificou um conteudo com timestamp especifico.
     *
     * @param userId o ID do utilizador
     * @param contentId o ID do conteudo
     * @param rating a classificacao atribuida
     * @param timestamp data e hora da classificacao
     * @return {@code true} se a relacao foi criada com sucesso
     */
    public boolean addRating(String userId, String contentId, double rating, LocalDateTime timestamp) { // Metodo que regista a classificacao com um timestamp especifico
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (u == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        Interaction interaction = new Interaction( // Cria a interacao de classificacao com o timestamp indicado
                u, c, InteractionType.RATE,
                timestamp, // Usa o timestamp fornecido em vez do momento atual
                0, rating, rating // watchedPct e 0 pois e so uma classificacao
        );

        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addRating (com timestamp)

    /**
     * Regista que um utilizador clicou num conteudo.
     *
     * @param userId o ID do utilizador
     * @param contentId o ID do conteudo
     * @return {@code true} se a relacao foi criada com sucesso
     */
    /*
     * DICIONARIO:
     * - CLICK: tipo de interacao que representa o clique num conteudo (demonstracao de interesse)
     * - peso 1.0: valor padrao fixo para interacoes de clique
     */
    public boolean addClick(String userId, String contentId) { // Metodo que regista o clique de um utilizador num conteudo
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (u == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        Interaction interaction = new Interaction( // Cria a interacao de clique
                u, c, InteractionType.CLICK,
                LocalDateTime.now(), // Usa o momento atual como timestamp
                0, 0, 1.0 // Peso fixo de 1.0 para cliques
        );

        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addClick

    /**
     * Regista que um utilizador clicou num conteudo com timestamp especifico.
     *
     * @param userId o ID do utilizador
     * @param contentId o ID do conteudo
     * @param timestamp data e hora do clique
     * @return {@code true} se a relacao foi criada com sucesso
     */
    public boolean addClick(String userId, String contentId, LocalDateTime timestamp) { // Metodo que regista o clique com um timestamp especifico
        User u = db.getUser(userId); // Vai buscar o utilizador na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (u == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        Interaction interaction = new Interaction( // Cria a interacao de clique com o timestamp indicado
                u, c, InteractionType.CLICK,
                timestamp, // Usa o timestamp fornecido em vez do momento atual
                0, 0, 1.0 // Peso fixo de 1.0 para cliques
        );

        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addClick (com timestamp)

    /**
     * Regista que um utilizador segue outro utilizador.
     *
     * @param followerId o ID de quem segue
     * @param followedId o ID de quem e seguido
     * @return {@code true} se a relacao foi criada com sucesso
     */
    /*
     * DICIONARIO:
     * - FOLLOW: tipo de interacao que representa o seguimento de um utilizador por outro
     * - follower.follow(followed): actualiza tambem a lista de seguidos no objeto User
     * - equals: verifica se dois IDs sao iguais para evitar que um utilizador se siga a si proprio
     */
    public boolean addFollow(String followerId, String followedId) { // Metodo que regista que um utilizador passa a seguir outro
        User follower = db.getUser(followerId); // Vai buscar o utilizador que vai seguir
        User followed = db.getUser(followedId); // Vai buscar o utilizador a ser seguido
        if (follower == null || followed == null) return false; // Se algum nao existir, nao cria a relacao
        if (followerId.equals(followedId)) return false; // Um utilizador nao pode seguir-se a si proprio

        follower.follow(followed); // Atualiza a lista de seguidos no objeto User (consistencia com a base de dados)

        Interaction interaction = new Interaction(follower, followed, LocalDateTime.now(), 1.0); // Cria a interacao de seguimento com peso 1.0
        graph.addEdge(interaction); // Adiciona a interacao como aresta no grafo

        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addFollow

    /**
     * Regista que um artista participou num conteudo.
     *
     * @param artistId o ID do artista
     * @param contentId o ID do conteudo
     * @param weight o peso da relacao
     * @return {@code true} se a relacao foi criada com sucesso
     */
    /*
     * DICIONARIO:
     * - participacao: relacao entre um artista e um conteudo (ex: ator num filme)
     * - weight: peso da aresta; pode representar a importancia do papel do artista
     */
    public boolean addArtistToContent(String artistId, String contentId, double weight) { // Metodo que regista a participacao de um artista num conteudo
        Artist a = db.getArtist(artistId); // Vai buscar o artista na base de dados
        Content c = db.getContent(contentId); // Vai buscar o conteudo na base de dados
        if (a == null || c == null) return false; // Se algum nao existir, nao cria a relacao

        graph.addEdge(a, c, weight); // Adiciona a aresta generica entre o artista e o conteudo com o peso indicado
        return true; // Indica que a relacao foi criada com sucesso
    } // Fim do metodo addArtistToContent

    /**
     * Versao simplificada de addArtistToContent com peso 1.0.
     *
     * @param artistId o ID do artista
     * @param contentId o ID do conteudo
     * @return {@code true} se a relacao foi criada com sucesso
     */
    public boolean addArtistToContent(String artistId, String contentId) { // Metodo simplificado que regista a participacao com peso padrao 1.0
        return addArtistToContent(artistId, contentId, 1.0); // Delega ao metodo completo com peso padrao
    } // Fim do metodo addArtistToContent (simplificado)

    /**
     * Remove um utilizador do grafo e todas as suas relacoes.
     *
     * @param userId o ID do utilizador
     * @return {@code true} se foi removido
     */
    /*
     * DICIONARIO:
     * - removeVertex: metodo de StreamingGraph que remove o vertice e as suas arestas
     */
    public boolean removeUserFromGraph(String userId) { // Metodo que remove um utilizador do grafo
        return graph.removeVertex(userId); // Delega a remocao ao grafo interno
    } // Fim do metodo removeUserFromGraph

    /**
     * Remove um conteudo do grafo e todas as suas relacoes.
     *
     * @param contentId o ID do conteudo
     * @return {@code true} se foi removido
     */
    public boolean removeContentFromGraph(String contentId) { // Metodo que remove um conteudo do grafo
        return graph.removeVertex(contentId); // Delega a remocao ao grafo interno
    } // Fim do metodo removeContentFromGraph

    /**
     * Remove um artista do grafo e todas as suas relacoes.
     *
     * @param artistId o ID do artista
     * @return {@code true} se foi removido
     */
    public boolean removeArtistFromGraph(String artistId) { // Metodo que remove um artista do grafo
        return graph.removeVertex(artistId); // Delega a remocao ao grafo interno
    } // Fim do metodo removeArtistFromGraph

    /**
     * Devolve todos os conteudos com que um utilizador interagiu.
     *
     * @param userId o ID do utilizador
     * @return lista de conteudos associados a este utilizador
     */
    /*
     * DICIONARIO:
     * - instanceof: operador que verifica se um objeto e de um determinado tipo
     * - getOutEdges: devolve as arestas de saida de um vertice
     * - getEntityByIndex: devolve a entidade associada a um indice numerico
     */
    public List<Content> getContentsForUser(String userId) { // Metodo que devolve todos os conteudos com que um utilizador interagiu
        List<Content> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (userId == null || !graph.containsVertex(userId)) return result; // Se o utilizador nao existir no grafo, devolve lista vazia

        for (DirectedEdge e : graph.getOutEdges(userId)) { // Percorre as arestas de saida do utilizador
            Entity target = graph.getEntityByIndex(e.to()); // Vai buscar a entidade destino de cada aresta
            if (target instanceof Content c) { // Se o destino for um conteudo...
                result.add(c); // Adiciona ao resultado
            } // Fim da verificacao do tipo
        } // Fim do ciclo
        return result; // Devolve a lista de conteudos
    } // Fim do metodo getContentsForUser

    /**
     * Devolve todos os utilizadores que interagiram com um conteudo.
     *
     * @param contentId o ID do conteudo
     * @return lista de utilizadores que interagiram com este conteudo
     */
    /*
     * DICIONARIO:
     * - getAllVertexIds: devolve os IDs de todos os vertices do grafo
     * - getIndex: devolve o indice numerico de um vertice
     * - e.to(): metodo de DirectedEdge que devolve o indice do vertice destino
     */
    public List<User> getUsersForContent(String contentId) { // Metodo que devolve todos os utilizadores que interagiram com um conteudo
        List<User> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (contentId == null || !graph.containsVertex(contentId)) return result; // Se o conteudo nao existir no grafo, devolve lista vazia

        int contentIdx = graph.getIndex(contentId); // Vai buscar o indice numerico do conteudo

        for (String id : graph.getAllVertexIds()) { // Percorre todos os vertices do grafo
            for (DirectedEdge e : graph.getOutEdges(id)) { // Percorre as arestas de saida de cada vertice
                if (e.to() == contentIdx) { // Se a aresta aponta para o conteudo procurado...
                    Entity source = graph.getEntity(id); // Vai buscar a entidade origem
                    if (source instanceof User u) { // Se a origem for um utilizador...
                        result.add(u); // Adiciona ao resultado
                    } // Fim da verificacao do tipo
                } // Fim da verificacao do destino
            } // Fim do ciclo de arestas
        } // Fim do ciclo de vertices
        return result; // Devolve a lista de utilizadores
    } // Fim do metodo getUsersForContent

    /**
     * Devolve todos os utilizadores que um utilizador segue.
     *
     * @param userId o ID do utilizador
     * @return lista de utilizadores seguidos
     */
    public List<User> getFollowing(String userId) { // Metodo que devolve todos os utilizadores que um utilizador segue
        List<User> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (userId == null || !graph.containsVertex(userId)) return result; // Se o utilizador nao existir no grafo, devolve lista vazia

        for (DirectedEdge e : graph.getOutEdges(userId)) { // Percorre as arestas de saida do utilizador
            Entity target = graph.getEntityByIndex(e.to()); // Vai buscar a entidade destino de cada aresta
            if (target instanceof User u) { // Se o destino for um utilizador...
                result.add(u); // Adiciona ao resultado
            } // Fim da verificacao do tipo
        } // Fim do ciclo
        return result; // Devolve a lista de utilizadores seguidos
    } // Fim do metodo getFollowing

    /**
     * Devolve todos os seguidores de um utilizador.
     *
     * @param userId o ID do utilizador
     * @return lista de utilizadores que seguem este utilizador
     */
    /*
     * DICIONARIO:
     * - seguidor: utilizador que tem uma aresta a apontar para o utilizador indicado
     * - break: interrompe o ciclo quando o resultado ja foi encontrado
     */
    public List<User> getFollowers(String userId) { // Metodo que devolve todos os seguidores de um utilizador
        List<User> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (userId == null || !graph.containsVertex(userId)) return result; // Se o utilizador nao existir no grafo, devolve lista vazia

        int userIdx = graph.getIndex(userId); // Vai buscar o indice numerico do utilizador

        for (String id : graph.getAllVertexIds()) { // Percorre todos os vertices do grafo
            Entity source = graph.getEntity(id); // Vai buscar a entidade de cada vertice
            if (!(source instanceof User)) continue; // Ignora vertices que nao sejam utilizadores

            for (DirectedEdge e : graph.getOutEdges(id)) { // Percorre as arestas de saida do vertice
                if (e.to() == userIdx) { // Se a aresta aponta para o utilizador procurado...
                    result.add((User) source); // Adiciona o utilizador ao resultado
                    break; // Ja encontramos a aresta, nao e necessario continuar
                } // Fim da verificacao do destino
            } // Fim do ciclo de arestas
        } // Fim do ciclo de vertices
        return result; // Devolve a lista de seguidores
    } // Fim do metodo getFollowers

    /**
     * Devolve todos os conteudos em que um artista participou.
     *
     * @param artistId o ID do artista
     * @return lista de conteudos do artista
     */
    public List<Content> getContentsForArtist(String artistId) { // Metodo que devolve todos os conteudos em que um artista participou
        List<Content> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (artistId == null || !graph.containsVertex(artistId)) return result; // Se o artista nao existir no grafo, devolve lista vazia

        for (DirectedEdge e : graph.getOutEdges(artistId)) { // Percorre as arestas de saida do artista
            Entity target = graph.getEntityByIndex(e.to()); // Vai buscar a entidade destino de cada aresta
            if (target instanceof Content c) { // Se o destino for um conteudo...
                result.add(c); // Adiciona ao resultado
            } // Fim da verificacao do tipo
        } // Fim do ciclo
        return result; // Devolve a lista de conteudos do artista
    } // Fim do metodo getContentsForArtist

    /**
     * Devolve todos os artistas que participaram num conteudo.
     *
     * @param contentId o ID do conteudo
     * @return lista de artistas do conteudo
     */
    public List<Artist> getArtistsForContent(String contentId) { // Metodo que devolve todos os artistas que participaram num conteudo
        List<Artist> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (contentId == null || !graph.containsVertex(contentId)) return result; // Se o conteudo nao existir no grafo, devolve lista vazia

        int contentIdx = graph.getIndex(contentId); // Vai buscar o indice numerico do conteudo

        for (String id : graph.getAllVertexIds()) { // Percorre todos os vertices do grafo
            for (DirectedEdge e : graph.getOutEdges(id)) { // Percorre as arestas de saida de cada vertice
                if (e.to() == contentIdx) { // Se a aresta aponta para o conteudo procurado...
                    Entity source = graph.getEntity(id); // Vai buscar a entidade origem
                    if (source instanceof Artist a) { // Se a origem for um artista...
                        result.add(a); // Adiciona ao resultado
                    } // Fim da verificacao do tipo
                } // Fim da verificacao do destino
            } // Fim do ciclo de arestas
        } // Fim do ciclo de vertices
        return result; // Devolve a lista de artistas do conteudo
    } // Fim do metodo getArtistsForContent

    /**
     * Devolve todas as interacoes de um utilizador.
     *
     * @param userId o ID do utilizador
     * @return lista de interacoes deste utilizador
     */
    /*
     * DICIONARIO:
     * - graph.getInteractions: devolve todas as interacoes registadas no grafo
     * - i.getUser().getId(): devolve o ID do utilizador da interacao
     */
    public List<Interaction> getInteractionsForUser(String userId) { // Metodo que devolve todas as interacoes de um utilizador
        List<Interaction> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (userId == null) return result; // Sem ID nao ha pesquisa

        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (userId.equals(i.getUser().getId())) { // Se a interacao for do utilizador procurado...
                result.add(i); // Adiciona ao resultado
            } // Fim da verificacao do utilizador
        } // Fim do ciclo
        return result; // Devolve a lista de interacoes do utilizador
    } // Fim do metodo getInteractionsForUser

    /**
     * Devolve todas as interacoes com um conteudo.
     *
     * @param contentId o ID do conteudo
     * @return lista de interacoes com este conteudo
     */
    /*
     * DICIONARIO:
     * - i.getContent(): devolve o conteudo da interacao
     */
    public List<Interaction> getInteractionsForContent(String contentId) { // Metodo que devolve todas as interacoes com um conteudo
        List<Interaction> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (contentId == null) return result; // Sem ID nao ha pesquisa

        for (Interaction i : graph.getInteractions()) { // Percorre todas as interacoes do grafo
            if (i.getContent() != null && contentId.equals(i.getContent().getId())) { // Se a interacao for com o conteudo procurado...
                result.add(i); // Adiciona ao resultado
            } // Fim da verificacao do conteudo
        } // Fim do ciclo
        return result; // Devolve a lista de interacoes com o conteudo
    } // Fim do metodo getInteractionsForContent

    /**
     * Devolve o numero total de vertices no grafo.
     *
     * @return numero total de vertices
     */
    /*
     * DICIONARIO:
     * - vertexCount: metodo de StreamingGraph que devolve o numero de vertices
     */
    public int totalVertices() { // Metodo que devolve o numero total de vertices no grafo
        return graph.vertexCount(); // Delega ao grafo interno
    } // Fim do metodo totalVertices

    /**
     * Devolve o numero total de arestas no grafo.
     *
     * @return numero total de arestas
     */
    public int totalEdges() { // Metodo que devolve o numero total de arestas no grafo
        return graph.edgeCount(); // Delega ao grafo interno
    } // Fim do metodo totalEdges

    /**
     * Devolve o numero total de interacoes registadas.
     *
     * @return numero total de interacoes
     */
    public int totalInteractions() { // Metodo que devolve o numero total de interacoes registadas
        return graph.getInteractions().size(); // Devolve o tamanho da lista de interacoes
    } // Fim do metodo totalInteractions

    /**
     * Verifica se uma entidade esta registada no grafo.
     *
     * @param entityId o ID da entidade
     * @return {@code true} se estiver no grafo
     */
    public boolean isRegistered(String entityId) { // Metodo que verifica se uma entidade esta registada no grafo
        return graph.containsVertex(entityId); // Delega a verificacao ao grafo interno
    } // Fim do metodo isRegistered

    /**
     * Devolve o StreamingGraph interno.
     *
     * @return o grafo interno
     */
    /*
     * DICIONARIO:
     * - getter: metodo que devolve o valor de um atributo privado ou final
     */
    public StreamingGraph getGraph() { // Metodo getter que devolve o grafo interno
        return graph; // Devolve a referencia ao grafo
    } // Fim do metodo getGraph

    /**
     * Devolve uma representacao textual da API, delegando ao grafo.
     *
     * @return texto descritivo do estado da API e do grafo
     */
    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a redefinir o metodo toString herdado de Object
     * - delega: chama o metodo correspondente noutro objeto em vez de implementar logica propria
     */
    @Override // Indica que estamos a redefinir o metodo toString herdado da classe Object
    public String toString() { // Metodo que devolve uma representacao textual da API
        return "StreamingGraphAPI:\n" + graph.toString(); // Delega a representacao ao grafo interno
    } // Fim do metodo toString

} // Fim da classe StreamingGraphAPI
