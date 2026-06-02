package streaming.db; // Declara que esta classe pertence ao pacote streaming.db

import edu.princeton.cs.algs4.EdgeWeightedDigraph; // Importa o grafo pesado direcionado da biblioteca algs4
import edu.princeton.cs.algs4.DirectedEdge; // Importa a classe que representa uma aresta direcionada com peso

import streaming.model.Entity; // Importa a classe base de todas as entidades do sistema
import streaming.model.Interaction; // Importa a classe que representa uma interacao entre utilizador e conteudo
import streaming.model.InteractionType; // Importa o enum com os tipos de interacao possiveis
import streaming.model.User; // Importa a classe que representa um utilizador da plataforma

import java.util.ArrayList; // Importa a classe que cria listas dinamicas que crescem conforme necessario
import java.util.HashMap; // Importa a classe que implementa tabelas hash para mapear chaves a valores
import java.util.List; // Importa a interface que define o comportamento de uma lista ordenada de elementos
import java.util.Map; // Importa a interface que define o comportamento de um mapa chave-valor

/**
 * Representacao das entidades e relacoes atraves de um grafo pesado direcionado.
 */
/*
 * DICIONARIO:
 * - EdgeWeightedDigraph: grafo direcionado com pesos nas arestas da biblioteca algs4
 * - DirectedEdge: aresta com origem, destino e peso num grafo direcionado
 * - vertice: no do grafo que representa uma entidade (utilizador, conteudo, artista)
 * - aresta: ligacao entre dois vertices com um peso associado
 * - nodeIndex: mapa que associa o ID de uma entidade ao seu indice numerico no grafo
 * - indexToEntity: mapa inverso que associa um indice numerico a entidade correspondente
 * - capacity: capacidade atual do grafo; quando esgotada, o grafo e reconstruido com mais espaco
 * - rebuild: operacao que reconstroi o grafo com nova capacidade, mantendo todos os dados
 * - HashMap: tabela hash que mapeia chaves a valores com acesso em O(1)
 */
public class StreamingGraph { // Define a classe StreamingGraph que gere um grafo pesado direcionado de entidades

    /**
     * Representa uma aresta generica entre duas entidades que nao nasce diretamente de uma interacao.
     */
    /*
     * DICIONARIO:
     * - static: pertence a classe e nao a cada objeto individual
     * - class: modelo que define a estrutura de um objeto
     * - GenericEdge: aresta generica entre duas entidades com um peso
     */
    private static class GenericEdge { // Classe interna privada que representa uma aresta generica entre duas entidades

        /** Identificador da entidade origem. */
        String fromId; // Guarda o ID da entidade de onde parte a aresta

        /** Identificador da entidade destino. */
        String toId; // Guarda o ID da entidade para onde vai a aresta

        /** Peso associado a aresta. */
        double weight; // Guarda o peso numerico da aresta

        /**
         * Cria uma aresta generica com origem, destino e peso.
         *
         * @param fromId identificador da entidade origem
         * @param toId identificador da entidade destino
         * @param weight peso da aresta
         */
        /*
         * DICIONARIO:
         * - construtor: metodo especial que inicializa os atributos quando o objeto e criado
         * - this: referencia ao objeto atual
         */
        GenericEdge(String fromId, String toId, double weight) { // Construtor que cria uma aresta generica com todos os seus dados
            this.fromId = fromId; // Guarda o ID da entidade origem
            this.toId = toId; // Guarda o ID da entidade destino
            this.weight = weight; // Guarda o peso da aresta
        } // Fim do construtor de GenericEdge

    } // Fim da classe interna GenericEdge

    /** Grafo pesado direcionado usado para guardar as arestas. */
    private EdgeWeightedDigraph graph; // Estrutura de dados da algs4 que guarda as arestas com pesos

    /** Mapa entre identificadores de entidades e indices numericos no grafo. */
    private Map<String, Integer> nodeIndex; // Associa o ID de cada entidade ao seu indice numerico no grafo

    /** Mapa inverso entre indices numericos e entidades. */
    private Map<Integer, Entity> indexToEntity; // Associa cada indice numerico a entidade correspondente

    /** Interacoes registadas como arestas do grafo. */
    private List<Interaction> interactions; // Lista de todas as interacoes registadas no grafo

    /** Arestas genericas registadas entre entidades. */
    private List<GenericEdge> genericEdges; // Lista de todas as arestas genericas registadas no grafo

    /** Proximo indice livre para inserir um vertice. */
    private int nextIndex; // Contador do proximo indice disponivel para um novo vertice

    /** Capacidade atual do grafo interno. */
    private int capacity; // Numero maximo de vertices que o grafo pode ter antes de ser reconstruido

    /** Capacidade inicial do grafo interno. */
    private static final int DEFAULT_CAPACITY = 128; // Capacidade inicial padrao; o grafo comeca com espaco para 128 vertices

    /**
     * Cria um grafo vazio com a capacidade inicial por defeito.
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial chamado quando se cria um novo objeto
     * - DEFAULT_CAPACITY: constante que define a capacidade inicial do grafo
     * - new HashMap<>(): cria um novo mapa vazio
     * - new ArrayList<>(): cria uma nova lista vazia
     */
    public StreamingGraph() { // Construtor que cria um grafo vazio com capacidade inicial padrao
        this.capacity = DEFAULT_CAPACITY; // Define a capacidade inicial como 128 vertices
        this.graph = new EdgeWeightedDigraph(capacity); // Cria o grafo da algs4 com a capacidade inicial
        this.nodeIndex = new HashMap<>(); // Cria o mapa de IDs para indices numericos
        this.indexToEntity = new HashMap<>(); // Cria o mapa de indices numericos para entidades
        this.interactions = new ArrayList<>(); // Cria a lista de interacoes vazia
        this.genericEdges = new ArrayList<>(); // Cria a lista de arestas genericas vazia
        this.nextIndex = 0; // O primeiro vertice a ser inserido tera o indice 0
    } // Fim do construtor

    /**
     * Adiciona uma entidade como vertice do grafo.
     *
     * @param e entidade a adicionar
     */
    /*
     * DICIONARIO:
     * - containsKey: metodo de Map que verifica se uma chave existe no mapa
     * - put: metodo de Map que associa uma chave a um valor
     * - rebuild: reconstroi o grafo com o dobro da capacidade quando esta esgotada
     */
    public void addVertex(Entity e) { // Metodo que adiciona uma entidade como vertice do grafo
        if (e == null || e.getId() == null) return; // Ignora entidades invalidas ou sem ID
        if (nodeIndex.containsKey(e.getId())) return; // Se o vertice ja existir, nao faz nada
        if (nextIndex >= capacity) { // Se a capacidade estiver esgotada...
            rebuild(capacity * 2); // Reconstroi o grafo com o dobro da capacidade
        } // Fim da verificacao de capacidade
        nodeIndex.put(e.getId(), nextIndex); // Associa o ID da entidade ao proximo indice livre
        indexToEntity.put(nextIndex, e); // Associa o indice a entidade
        nextIndex++; // Incrementa o contador de indices
    } // Fim do metodo addVertex

    /**
     * Remove um vertice e todas as arestas associadas ao respetivo identificador.
     *
     * @param entityId identificador da entidade a remover
     * @return {@code true} se o vertice existia e foi removido
     */
    /*
     * DICIONARIO:
     * - remove: metodo de Map que remove a entrada com a chave indicada
     * - removeIf: metodo de List que remove elementos que satisfacam um predicado
     * - lambda: expressao anonima no formato (parametros) -> corpo
     */
    public boolean removeVertex(String entityId) { // Metodo que remove um vertice e todas as suas arestas do grafo
        if (entityId == null || !nodeIndex.containsKey(entityId)) return false; // Se o vertice nao existir, nao ha nada a remover

        int idx = nodeIndex.get(entityId); // Vai buscar o indice numerico do vertice
        nodeIndex.remove(entityId); // Remove o ID do mapa de IDs para indices
        indexToEntity.remove(idx); // Remove o indice do mapa de indices para entidades

        interactions.removeIf(inter -> { // Remove todas as interacoes que envolvam este vertice
            if (inter == null || inter.getUser() == null) return false; // Ignora interacoes invalidas
            String fromId = inter.getUser().getId(); // Vai buscar o ID da origem da interacao
            Entity target = inter.getTargetEntity(); // Vai buscar a entidade destino da interacao
            String toId = target == null ? null : target.getId(); // Vai buscar o ID do destino, ou null se nao existir
            return entityId.equals(fromId) || entityId.equals(toId); // Remove se o vertice for origem ou destino
        }); // Fim da remocao de interacoes
        genericEdges.removeIf(ge -> entityId.equals(ge.fromId) || entityId.equals(ge.toId)); // Remove todas as arestas genericas que envolvam este vertice

        rebuild(capacity); // Reconstroi o grafo com a mesma capacidade para remover os indices obsoletos
        return true; // Indica que o vertice foi removido com sucesso
    } // Fim do metodo removeVertex

    /**
     * Adiciona uma aresta ao grafo a partir de uma interacao.
     *
     * @param interaction interacao a registar
     */
    /*
     * DICIONARIO:
     * - addVertex: metodo que garante que os vertices existem antes de criar a aresta
     * - DirectedEdge: aresta direcionada com origem, destino e peso
     * - graph.addEdge: metodo da algs4 que adiciona a aresta ao grafo interno
     */
    public void addEdge(Interaction interaction) { // Metodo que adiciona uma aresta ao grafo a partir de uma interacao
        if (interaction == null) return; // Ignora interacoes nulas
        if (interaction.getUser() == null) return; // Ignora se nao houver utilizador
        Entity target = interaction.getTargetEntity(); // Vai buscar a entidade destino da interacao
        if (target == null) return; // Ignora se nao houver entidade destino

        addVertex(interaction.getUser()); // Garante que o utilizador esta registado como vertice
        addVertex(target); // Garante que a entidade destino esta registada como vertice

        int from = nodeIndex.get(interaction.getUser().getId()); // Vai buscar o indice do utilizador
        int to = nodeIndex.get(target.getId()); // Vai buscar o indice da entidade destino

        DirectedEdge edge = new DirectedEdge(from, to, interaction.getWeight()); // Cria a aresta direcionada com o peso da interacao
        graph.addEdge(edge); // Adiciona a aresta ao grafo interno

        interactions.add(interaction); // Guarda a interacao na lista de interacoes
    } // Fim do metodo addEdge (interacao)

    /**
     * Adiciona uma aresta generica entre duas entidades.
     *
     * @param from entidade origem
     * @param to entidade destino
     * @param weight peso da aresta
     */
    public void addEdge(Entity from, Entity to, double weight) { // Metodo que adiciona uma aresta generica entre duas entidades
        if (from == null || to == null) return; // Ignora se alguma das entidades for nula
        addVertex(from); // Garante que a entidade origem esta registada como vertice
        addVertex(to); // Garante que a entidade destino esta registada como vertice

        int fromIdx = nodeIndex.get(from.getId()); // Vai buscar o indice da entidade origem
        int toIdx = nodeIndex.get(to.getId()); // Vai buscar o indice da entidade destino

        DirectedEdge edge = new DirectedEdge(fromIdx, toIdx, weight); // Cria a aresta direcionada com o peso indicado
        graph.addEdge(edge); // Adiciona a aresta ao grafo interno
        genericEdges.add(new GenericEdge(from.getId(), to.getId(), weight)); // Guarda a aresta generica na lista de arestas genericas
    } // Fim do metodo addEdge (generica)

    /**
     * Adiciona uma relacao de seguimento entre dois utilizadores.
     *
     * @param follower utilizador que segue
     * @param followed utilizador seguido
     */
    /*
     * DICIONARIO:
     * - follow: relacao social em que um utilizador passa a receber atualizacoes de outro
     * - peso 1.0: valor padrao para arestas de seguimento
     */
    public void addFollow(User follower, User followed) { // Metodo que cria uma aresta de seguimento entre dois utilizadores
        if (follower == null || followed == null) return; // Ignora se algum dos utilizadores for nulo
        addEdge(follower, followed, 1.0); // Cria a aresta com peso padrao de 1.0
    } // Fim do metodo addFollow

    /**
     * Verifica se um vertice existe no grafo.
     *
     * @param entityId identificador da entidade
     * @return {@code true} se a entidade estiver registada no grafo
     */
    public boolean containsVertex(String entityId) { // Metodo que verifica se uma entidade existe como vertice no grafo
        return entityId != null && nodeIndex.containsKey(entityId); // Devolve true se o ID nao for null e existir no mapa
    } // Fim do metodo containsVertex

    /**
     * Devolve o numero de vertices registados.
     *
     * @return numero de vertices
     */
    /*
     * DICIONARIO:
     * - size(): metodo de Map que devolve o numero de entradas no mapa
     */
    public int vertexCount() { // Metodo que devolve o numero total de vertices no grafo
        return nodeIndex.size(); // Devolve o numero de entradas no mapa de IDs
    } // Fim do metodo vertexCount

    /**
     * Devolve o numero de arestas registadas.
     *
     * @return numero de arestas
     */
    /*
     * DICIONARIO:
     * - graph.E(): metodo da algs4 que devolve o numero total de arestas no grafo
     */
    public int edgeCount() { // Metodo que devolve o numero total de arestas no grafo
        return graph.E(); // Devolve o numero de arestas do grafo interno da algs4
    } // Fim do metodo edgeCount

    /**
     * Devolve a entidade associada a um identificador.
     *
     * @param entityId identificador da entidade
     * @return entidade correspondente, ou {@code null} se nao existir
     */
    public Entity getEntity(String entityId) { // Metodo que devolve a entidade associada a um ID
        if (entityId == null || !nodeIndex.containsKey(entityId)) return null; // Se o ID nao existir, devolve null
        int idx = nodeIndex.get(entityId); // Vai buscar o indice numerico da entidade
        return indexToEntity.get(idx); // Devolve a entidade associada a esse indice
    } // Fim do metodo getEntity

    /**
     * Devolve o indice interno de uma entidade.
     *
     * @param entityId identificador da entidade
     * @return indice interno, ou {@code -1} se a entidade nao existir
     */
    public int getIndex(String entityId) { // Metodo que devolve o indice numerico interno de uma entidade
        if (entityId == null || !nodeIndex.containsKey(entityId)) return -1; // Se o ID nao existir, devolve -1
        return nodeIndex.get(entityId); // Devolve o indice numerico da entidade
    } // Fim do metodo getIndex

    /**
     * Devolve a entidade associada a um indice interno.
     *
     * @param index indice interno
     * @return entidade correspondente, ou {@code null} se nao existir
     */
    public Entity getEntityByIndex(int index) { // Metodo que devolve a entidade associada a um indice numerico
        return indexToEntity.get(index); // Devolve a entidade do mapa ou null se o indice nao existir
    } // Fim do metodo getEntityByIndex

    /**
     * Devolve as arestas de saida de uma entidade.
     *
     * @param entityId identificador da entidade origem
     * @return lista de arestas de saida validas
     */
    /*
     * DICIONARIO:
     * - graph.adj(idx): metodo da algs4 que devolve as arestas de saida de um vertice
     * - e.to(): metodo de DirectedEdge que devolve o indice do vertice destino
     */
    public List<DirectedEdge> getOutEdges(String entityId) { // Metodo que devolve as arestas que partem de uma entidade
        List<DirectedEdge> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (entityId == null || !nodeIndex.containsKey(entityId)) return result; // Se o vertice nao existir, devolve lista vazia

        int idx = nodeIndex.get(entityId); // Vai buscar o indice numerico da entidade
        for (DirectedEdge e : graph.adj(idx)) { // Percorre todas as arestas de saida desse vertice
            if (indexToEntity.containsKey(e.to())) { // So inclui a aresta se o vertice destino ainda existir
                result.add(e); // Adiciona a aresta valida ao resultado
            } // Fim da verificacao do vertice destino
        } // Fim do ciclo
        return result; // Devolve a lista de arestas de saida validas
    } // Fim do metodo getOutEdges

    /**
     * Devolve os vizinhos diretamente alcancaveis a partir de uma entidade.
     *
     * @param entityId identificador da entidade origem
     * @return lista de entidades vizinhas
     */
    /*
     * DICIONARIO:
     * - vizinhos: entidades diretamente alcancaveis por uma aresta de saida
     */
    public List<Entity> getNeighbors(String entityId) { // Metodo que devolve as entidades diretamente alcancaveis a partir de uma entidade
        List<Entity> result = new ArrayList<>(); // Cria a lista de resultados vazia
        for (DirectedEdge e : getOutEdges(entityId)) { // Percorre todas as arestas de saida
            Entity neighbor = indexToEntity.get(e.to()); // Vai buscar a entidade destino de cada aresta
            if (neighbor != null) { // So inclui se a entidade existir
                result.add(neighbor); // Adiciona o vizinho ao resultado
            } // Fim da verificacao do vizinho
        } // Fim do ciclo
        return result; // Devolve a lista de entidades vizinhas
    } // Fim do metodo getNeighbors

    /**
     * Devolve uma copia das interacoes registadas.
     *
     * @return lista de interacoes
     */
    /*
     * DICIONARIO:
     * - new ArrayList<>(colecao): cria uma copia de uma lista existente
     */
    public List<Interaction> getInteractions() { // Metodo que devolve uma copia da lista de interacoes
        return new ArrayList<>(interactions); // Devolve uma copia para evitar modificacoes externas
    } // Fim do metodo getInteractions

    /**
     * Devolve uma copia do mapa interno indice para entidade.
     *
     * @return mapa de indices para entidades
     */
    /*
     * DICIONARIO:
     * - new HashMap<>(mapa): cria uma copia de um mapa existente
     */
    public Map<Integer, Entity> getIndexToEntity() { // Metodo que devolve uma copia do mapa de indices para entidades
        return new HashMap<>(indexToEntity); // Devolve uma copia para evitar modificacoes externas
    } // Fim do metodo getIndexToEntity

    /**
     * Devolve uma lista com as interacoes filtradas por tipo.
     *
     * @param type tipo de interacao a filtrar
     * @return lista de interacoes do tipo indicado
     */
    /*
     * DICIONARIO:
     * - getType: metodo de Interaction que devolve o tipo da interacao
     * - ==: operador de comparacao usado para comparar valores de enum
     */
    public List<Interaction> getInteractionsByType(InteractionType type) { // Metodo que devolve as interacoes filtradas por tipo
        List<Interaction> result = new ArrayList<>(); // Cria a lista de resultados vazia
        if (type == null) return result; // Se o tipo for null, devolve lista vazia
        for (Interaction i : interactions) { // Percorre todas as interacoes
            if (i != null && i.getType() == type) result.add(i); // Se o tipo corresponder, adiciona ao resultado
        } // Fim do ciclo
        return result; // Devolve a lista de interacoes filtradas
    } // Fim do metodo getInteractionsByType

    /**
     * Devolve todos os identificadores de vertices registados.
     *
     * @return lista de identificadores
     */
    /*
     * DICIONARIO:
     * - keySet(): metodo de Map que devolve o conjunto de todas as chaves
     */
    public List<String> getAllVertexIds() { // Metodo que devolve a lista de IDs de todos os vertices
        return new ArrayList<>(nodeIndex.keySet()); // Devolve uma copia da lista de IDs
    } // Fim do metodo getAllVertexIds

    /**
     * Devolve o grafo pesado direcionado interno.
     *
     * @return grafo interno da algs4
     */
    public EdgeWeightedDigraph getGraph() { // Metodo que devolve o grafo interno da algs4
        return graph; // Devolve o grafo pesado direcionado
    } // Fim do metodo getGraph

    /**
     * Reconstroi o grafo interno com uma nova capacidade, mantendo todos os dados.
     *
     * @param newCapacity nova capacidade do grafo
     */
    /*
     * DICIONARIO:
     * - rebuild: reconstroi o grafo com nova capacidade, re-inserindo todos os vertices e arestas
     * - clear: metodo de Map/List que remove todos os elementos
     * - values(): metodo de Map que devolve todos os valores guardados
     */
    private void rebuild(int newCapacity) { // Metodo privado que reconstroi o grafo com nova capacidade
        this.capacity = newCapacity; // Actualiza a capacidade do grafo

        List<Entity> entities = new ArrayList<>(indexToEntity.values()); // Guarda todas as entidades antes de limpar os mapas

        nodeIndex.clear(); // Limpa o mapa de IDs para indices
        indexToEntity.clear(); // Limpa o mapa de indices para entidades
        nextIndex = 0; // Reinicia o contador de indices

        this.graph = new EdgeWeightedDigraph(newCapacity); // Cria um novo grafo com a nova capacidade

        for (Entity e : entities) { // Re-insere todos os vertices no novo grafo
            if (e != null && e.getId() != null) { // So re-insere entidades validas
                nodeIndex.put(e.getId(), nextIndex); // Associa o ID ao novo indice
                indexToEntity.put(nextIndex, e); // Associa o novo indice a entidade
                nextIndex++; // Incrementa o contador de indices
            } // Fim da verificacao da entidade
        } // Fim do ciclo de re-insercao de vertices

        for (Interaction inter : interactions) { // Re-insere todas as arestas de interacao no novo grafo
            if (inter == null || inter.getUser() == null) continue; // Ignora interacoes invalidas
            Entity target = inter.getTargetEntity(); // Vai buscar a entidade destino
            if (target == null) continue; // Ignora se nao houver destino
            String fromId = inter.getUser().getId(); // Vai buscar o ID da origem
            String toId = target.getId(); // Vai buscar o ID do destino
            if (nodeIndex.containsKey(fromId) && nodeIndex.containsKey(toId)) { // So cria a aresta se ambos os vertices existirem
                int from = nodeIndex.get(fromId); // Vai buscar o novo indice da origem
                int to = nodeIndex.get(toId); // Vai buscar o novo indice do destino
                graph.addEdge(new DirectedEdge(from, to, inter.getWeight())); // Cria a aresta no novo grafo
            } // Fim da verificacao dos vertices
        } // Fim do ciclo de re-insercao de arestas de interacao

        for (GenericEdge ge : genericEdges) { // Re-insere todas as arestas genericas no novo grafo
            if (nodeIndex.containsKey(ge.fromId) && nodeIndex.containsKey(ge.toId)) { // So cria a aresta se ambos os vertices existirem
                int from = nodeIndex.get(ge.fromId); // Vai buscar o novo indice da origem
                int to = nodeIndex.get(ge.toId); // Vai buscar o novo indice do destino
                graph.addEdge(new DirectedEdge(from, to, ge.weight)); // Cria a aresta generica no novo grafo
            } // Fim da verificacao dos vertices
        } // Fim do ciclo de re-insercao de arestas genericas
    } // Fim do metodo rebuild

    /**
     * Devolve uma representacao textual do grafo, incluindo vertices e arestas.
     *
     * @return texto descritivo do grafo
     */
    /*
     * DICIONARIO:
     * - Override: anotacao que indica que estamos a redefinir o metodo toString herdado de Object
     * - StringBuilder: classe eficiente para construir textos longos por concatenacao
     * - append: metodo de StringBuilder que adiciona texto ao final
     * - entrySet: metodo de Map que devolve o conjunto de pares chave-valor
     */
    @Override // Indica que estamos a redefinir o metodo toString herdado da classe Object
    public String toString() { // Metodo que devolve uma representacao textual do grafo
        StringBuilder sb = new StringBuilder(); // Cria um construtor de texto eficiente
        sb.append("StreamingGraph: ").append(vertexCount()).append(" vertices, ")
                .append(edgeCount()).append(" arestas\n"); // Adiciona o cabecalho com o numero de vertices e arestas

        sb.append("\nvertices:\n"); // Adiciona o titulo da secao de vertices
        for (Map.Entry<String, Integer> entry : nodeIndex.entrySet()) { // Percorre todos os vertices do grafo
            Entity e = indexToEntity.get(entry.getValue()); // Vai buscar a entidade do vertice
            sb.append("  [").append(entry.getValue()).append("] ")
                    .append(entry.getKey()).append(" -> ").append(e).append("\n"); // Adiciona informacao do vertice ao texto
        } // Fim do ciclo de vertices

        sb.append("\nArestas:\n"); // Adiciona o titulo da secao de arestas
        for (int v = 0; v < nextIndex; v++) { // Percorre todos os indices de vertices
            Entity fromEntity = indexToEntity.get(v); // Vai buscar a entidade origem
            if (fromEntity == null) continue; // Ignora indices sem entidade associada

            for (DirectedEdge e : graph.adj(v)) { // Percorre as arestas de saida do vertice
                Entity toEntity = indexToEntity.get(e.to()); // Vai buscar a entidade destino
                if (toEntity == null) continue; // Ignora arestas para destinos inexistentes
                sb.append("  ").append(fromEntity.getId())
                        .append(" -> ").append(toEntity.getId())
                        .append(" (peso: ").append(e.weight()).append(")\n"); // Adiciona informacao da aresta ao texto
            } // Fim do ciclo de arestas
        } // Fim do ciclo de vertices

        return sb.toString(); // Devolve o texto completo do grafo
    } // Fim do metodo toString

} // Fim da classe StreamingGraph
