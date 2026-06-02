package streaming.db; // Declara que esta classe pertence ao pacote streaming.db

import edu.princeton.cs.algs4.RedBlackBST; // Importa a arvore binaria de pesquisa equilibrada usada para pesquisas por ordem e intervalo
import edu.princeton.cs.algs4.SeparateChainingHashST; // Importa a tabela hash com listas separadas para lidar com colisoes
import streaming.model.Artist; // Importa a classe que representa um artista da plataforma
import streaming.model.Content; // Importa a classe abstrata base para todos os tipos de conteudo
import streaming.model.Genre; // Importa a classe que representa um genero de conteudo
import streaming.model.User; // Importa a classe que representa um utilizador da plataforma

import java.time.LocalDate; // Importa a classe que representa datas simples (apenas ano, mes e dia)
import java.time.chrono.ChronoLocalDate; // Importa o tipo de data comparavel usado como chave nas arvores por intervalo
import java.util.ArrayList; // Importa a classe que cria listas dinamicas que crescem conforme necessario
import java.util.List; // Importa a interface que define o comportamento de uma lista ordenada de elementos
import java.util.Locale; // Importa a classe usada para converter texto de forma neutra em relacao ao idioma

/**
 * Base de dados em memoria para a plataforma de streaming.
 *
 * Usa tabelas hash para acesso rapido por ID e arvores equilibradas para buscas por ordem e intervalo.
 */
/*
 * DICIONARIO:
 * - SeparateChainingHashST: tabela hash que resolve colisoes com listas ligadas; permite acesso em O(1)
 * - RedBlackBST: arvore binaria de pesquisa equilibrada; permite pesquisas por intervalo em O(log N)
 * - indice: estrutura de dados auxiliar que mapeia um atributo (ex: regiao) a uma lista de entidades
 * - null: valor especial que representa a ausencia de objeto
 * - archive: arquivo de utilizadores removidos mas guardados para consulta futura
 * - List<T>: colecao ordenada de elementos do tipo T
 * - LocalDate: data sem hora
 * - ChronoLocalDate: tipo de data comparavel usado como chave em arvores BST
 * - Locale.ROOT: modo neutro para converter texto sem depender do idioma do sistema
 */
public class StreamingDB { // Define a classe StreamingDB, a base de dados em memoria da plataforma

    /** Guarda os utilizadores ativos por ID. */
    private SeparateChainingHashST<String, User> users; // Tabela hash que mapeia IDs de utilizadores ativos para objetos User

    /** Guarda os utilizadores arquivados por ID. */
    private SeparateChainingHashST<String, User> archivedusers; // Tabela hash que mapeia IDs de utilizadores arquivados para objetos User

    /** Guarda os conteudos por ID. */
    private SeparateChainingHashST<String, Content> contents; // Tabela hash que mapeia IDs de conteudos para objetos Content

    /** Guarda os artistas por ID. */
    private SeparateChainingHashST<String, Artist> artists; // Tabela hash que mapeia IDs de artistas para objetos Artist

    /** Guarda os generos por ID. */
    private SeparateChainingHashST<String, Genre> genres; // Tabela hash que mapeia IDs de generos para objetos Genre

    /** Indexa utilizadores pela regiao. */
    private RedBlackBST<String, List<User>> usersByRegion; // Arvore que agrupa utilizadores por regiao geografica

    /** Indexa utilizadores pela data de registo. */
    private RedBlackBST<ChronoLocalDate, List<User>> usersByRegistrationDate; // Arvore que agrupa utilizadores por data de registo

    /** Indexa conteudos pelo nome do genero. */
    private RedBlackBST<String, List<Content>> contentsByGenre; // Arvore que agrupa conteudos por nome do genero

    /** Indexa conteudos pelo titulo. */
    private RedBlackBST<String, List<Content>> contentsByTitle; // Arvore que agrupa conteudos por titulo

    /** Indexa conteudos pela regiao. */
    private RedBlackBST<String, List<Content>> contentsByRegion; // Arvore que agrupa conteudos por regiao geografica

    /** Indexa artistas pela nacionalidade. */
    private RedBlackBST<String, List<Artist>> artistsByNationality; // Arvore que agrupa artistas por nacionalidade

    /** Indexa artistas pela data de nascimento. */
    private RedBlackBST<ChronoLocalDate, List<Artist>> artistsByBirthDate; // Arvore que agrupa artistas por data de nascimento

    /** Indexa conteudos pelo ano de lancamento. */
    private RedBlackBST<Integer, List<Content>> contentsByReleaseYear; // Arvore que agrupa conteudos por ano de lancamento

    /** Indexa conteudos pela nota media. */
    private RedBlackBST<Double, List<Content>> contentsByRating; // Arvore que agrupa conteudos por classificacao media

    /** Indexa conteudos pelo numero de visualizacoes. */
    private RedBlackBST<Integer, List<Content>> contentsByViews; // Arvore que agrupa conteudos por numero de visualizacoes

    /** Indexa conteudos pela duracao. */
    private RedBlackBST<Integer, List<Content>> contentsByDuration; // Arvore que agrupa conteudos por duracao em minutos

    /**
     * Cria uma base de dados nova e vazia, inicializando todas as tabelas e indices.
     */
    /*
     * DICIONARIO:
     * - construtor: metodo especial chamado quando se cria um novo objeto
     * - new SeparateChainingHashST<>(): cria uma nova tabela hash vazia
     * - new RedBlackBST<>(): cria uma nova arvore BST vazia
     * - this: referencia ao objeto que esta a ser criado
     */
    public StreamingDB() { // Construtor que cria a base de dados e inicializa todas as estruturas de dados
        this.users = new SeparateChainingHashST<>(); // Cria a tabela dos utilizadores ativos
        this.contents = new SeparateChainingHashST<>(); // Cria a tabela dos conteudos
        this.artists = new SeparateChainingHashST<>(); // Cria a tabela dos artistas
        this.genres = new SeparateChainingHashST<>(); // Cria a tabela dos generos
        this.archivedusers = new SeparateChainingHashST<>(); // Cria a tabela dos utilizadores arquivados

        this.usersByRegion = new RedBlackBST<>(); // Cria o indice dos utilizadores por regiao
        this.usersByRegistrationDate = new RedBlackBST<>(); // Cria o indice dos utilizadores por data de registo
        this.contentsByGenre = new RedBlackBST<>(); // Cria o indice dos conteudos por genero
        this.contentsByTitle = new RedBlackBST<>(); // Cria o indice dos conteudos por titulo
        this.contentsByRegion = new RedBlackBST<>(); // Cria o indice dos conteudos por regiao
        this.artistsByNationality = new RedBlackBST<>(); // Cria o indice dos artistas por nacionalidade
        this.artistsByBirthDate = new RedBlackBST<>(); // Cria o indice dos artistas por data de nascimento
        this.contentsByReleaseYear = new RedBlackBST<>(); // Cria o indice dos conteudos por ano de lancamento
        this.contentsByRating = new RedBlackBST<>(); // Cria o indice dos conteudos por nota
        this.contentsByViews = new RedBlackBST<>(); // Cria o indice dos conteudos por visualizacoes
        this.contentsByDuration = new RedBlackBST<>(); // Cria o indice dos conteudos por duracao
    } // Fim do construtor

    /**
     * Adiciona um utilizador novo. Se o ID ja existir, troca o utilizador antigo pelo novo.
     *
     * @param newUser o utilizador a adicionar ou substituir
     */
    /*
     * DICIONARIO:
     * - contains: verifica se uma chave existe na tabela hash
     * - get: devolve o valor associado a uma chave na tabela hash
     * - put: insere ou substitui um valor na tabela hash
     * - removeUserFromIndices: metodo privado que remove o utilizador dos indices secundarios
     * - addUserToIndices: metodo privado que adiciona o utilizador aos indices secundarios
     */
    public void addUser(User newUser) { // Metodo que adiciona ou substitui um utilizador na base de dados
        if (newUser == null || newUser.getId() == null) return; // Ignora valores vazios ou sem ID

        if (users.contains(newUser.getId())) { // Verifica se ja existe um utilizador com este ID
            User storedUser = users.get(newUser.getId()); // Vai buscar o utilizador atualmente guardado

            User auxiliarOldUser = new User( // Cria um utilizador auxiliar com os dados antigos para remover dos indices
                storedUser.getId(),
                storedUser.getCreatedAt(),
                storedUser.getUsername(),
                storedUser.getEmail(),
                storedUser.getPasswordHash()
            );
            auxiliarOldUser.setRegion(storedUser.getRegion()); // Copia a regiao antiga para o auxiliar
            auxiliarOldUser.setRegistrationDate(storedUser.getRegistrationDate()); // Copia a data de registo antiga para o auxiliar

            removeUserFromIndices(auxiliarOldUser); // Remove o utilizador antigo dos indices usando o auxiliar
        } // Fim da verificacao de duplicado

        users.put(newUser.getId(), newUser); // Guarda o novo utilizador na tabela principal
        addUserToIndices(newUser); // Adiciona o novo utilizador aos indices secundarios
    } // Fim do metodo addUser

    /**
     * Adiciona um conteudo novo. Se o ID ja existir, troca o conteudo antigo pelo novo.
     *
     * @param newContent o conteudo a adicionar ou substituir
     */
    /*
     * DICIONARIO:
     * - removeContentFromIndices: metodo privado que remove o conteudo dos indices secundarios
     * - addContentToIndices: metodo privado que adiciona o conteudo aos indices secundarios
     */
    public void addContent(Content newContent) { // Metodo que adiciona ou substitui um conteudo na base de dados
        if (newContent == null || newContent.getId() == null) return; // Ignora entradas invalidas

        if (contents.contains(newContent.getId())) { // Se o conteudo ja existir...
            Content oldContent = contents.get(newContent.getId()); // Vai buscar o conteudo antigo
            removeContentFromIndices(oldContent); // Limpa o conteudo antigo dos indices
        } // Fim da verificacao de duplicado

        contents.put(newContent.getId(), newContent); // Guarda o conteudo na tabela principal
        addContentToIndices(newContent); // Regista o conteudo nos indices secundarios
    } // Fim do metodo addContent

    /**
     * Adiciona um artista novo. Se o ID ja existir, troca o artista antigo pelo novo.
     *
     * @param newArtist o artista a adicionar ou substituir
     */
    /*
     * DICIONARIO:
     * - removeArtistFromIndices: metodo privado que remove o artista dos indices secundarios
     * - addArtistToIndices: metodo privado que adiciona o artista aos indices secundarios
     */
    public void addArtist(Artist newArtist) { // Metodo que adiciona ou substitui um artista na base de dados
        if (newArtist == null || newArtist.getId() == null) return; // Ignora entradas invalidas

        if (artists.contains(newArtist.getId())) { // Se o artista ja existir...
            Artist oldArtist = artists.get(newArtist.getId()); // Vai buscar o artista antigo
            removeArtistFromIndices(oldArtist); // Remove o artista antigo dos indices
        } // Fim da verificacao de duplicado

        artists.put(newArtist.getId(), newArtist); // Guarda o artista na tabela principal
        addArtistToIndices(newArtist); // Adiciona o artista aos indices secundarios
    } // Fim do metodo addArtist

    /**
     * Adiciona um utilizador aos indices secundarios.
     *
     * @param user o utilizador a indexar
     */
    /*
     * DICIONARIO:
     * - indice: estrutura auxiliar que permite pesquisar rapidamente por um atributo especifico
     * - new ArrayList<>(): cria uma nova lista vazia para receber entidades
     */
    private void addUserToIndices(User user) { // Metodo privado que insere um utilizador nos indices secundarios
        if (user == null) return; // Protecao contra valores vazios

        String region = user.getRegion(); // Vai buscar a regiao do utilizador
        if (region != null) { // So cria indice se houver regiao
            if (!usersByRegion.contains(region)) usersByRegion.put(region, new ArrayList<>()); // Cria a lista da regiao se ainda nao existir
            usersByRegion.get(region).add(user); // Adiciona o utilizador a lista da sua regiao
        } // Fim da indexacao por regiao

        LocalDate regDate = user.getRegistrationDate(); // Vai buscar a data de registo
        if (regDate != null) { // So cria indice se houver data
            if (!usersByRegistrationDate.contains(regDate)) usersByRegistrationDate.put(regDate, new ArrayList<>()); // Cria a lista da data se ainda nao existir
            usersByRegistrationDate.get(regDate).add(user); // Adiciona o utilizador a lista da sua data de registo
        } // Fim da indexacao por data de registo
    } // Fim do metodo addUserToIndices

    /**
     * Remove um utilizador dos indices secundarios com base nos seus valores atuais.
     *
     * @param user o utilizador a remover dos indices
     */
    /*
     * DICIONARIO:
     * - removeIf: metodo de List que remove elementos que satisfacam uma condicao (predicado)
     * - delete: metodo de RedBlackBST que remove uma chave da arvore
     * - lambda: expressao anonima no formato (parametros) -> corpo
     */
    private void removeUserFromIndices(User user) { // Metodo privado que remove um utilizador dos indices secundarios
        if (user == null) return; // Protecao contra null

        String region = user.getRegion(); // Le a regiao atual do utilizador
        if (region != null && usersByRegion.contains(region)) { // Se existir indice para essa regiao...
            List<User> list = usersByRegion.get(region); // Vai buscar a lista de utilizadores dessa regiao
            list.removeIf(u -> u.getId().equals(user.getId())); // Remove o utilizador com o mesmo ID
            if (list.isEmpty()) usersByRegion.delete(region); // Se a lista ficar vazia, apaga a chave da arvore
        } // Fim da remocao do indice por regiao

        LocalDate regDate = user.getRegistrationDate(); // Le a data de registo atual
        if (regDate != null && usersByRegistrationDate.contains(regDate)) { // Se existir indice para essa data...
            List<User> list = usersByRegistrationDate.get(regDate); // Vai buscar a lista de utilizadores dessa data
            list.removeIf(u -> u.getId().equals(user.getId())); // Remove o utilizador com o mesmo ID
            if (list.isEmpty()) usersByRegistrationDate.delete(regDate); // Se ficar vazia, apaga a chave da arvore
        } // Fim da remocao do indice por data de registo
    } // Fim do metodo removeUserFromIndices

    /**
     * Adiciona um conteudo aos indices secundarios.
     *
     * @param content o conteudo a indexar
     */
    /*
     * DICIONARIO:
     * - getReleaseYear: metodo que devolve o ano de lancamento do conteudo
     * - getGenre: metodo que devolve o genero do conteudo
     * - getTitle: metodo que devolve o titulo do conteudo
     * - getRegion: metodo que devolve a regiao do conteudo
     */
    private void addContentToIndices(Content content) { // Metodo privado que insere um conteudo nos indices secundarios
        if (content == null) return; // Protecao contra null

        int year = content.getReleaseYear(); // Vai buscar o ano de lancamento
        if (!contentsByReleaseYear.contains(year)) contentsByReleaseYear.put(year, new ArrayList<>()); // Cria lista para esse ano se ainda nao existir
        contentsByReleaseYear.get(year).add(content); // Adiciona o conteudo a lista do ano correto

        Genre genre = content.getGenre(); // Vai buscar o genero do conteudo
        if (genre != null && genre.getName() != null) { // So indexa se o genero tiver nome
            String gName = genre.getName(); // Guarda o nome do genero
            if (!contentsByGenre.contains(gName)) contentsByGenre.put(gName, new ArrayList<>()); // Cria lista se ainda nao existir
            contentsByGenre.get(gName).add(content); // Adiciona o conteudo a lista do genero
        } // Fim da indexacao por genero

        String title = content.getTitle(); // Vai buscar o titulo do conteudo
        if (title != null) { // So indexa se houver titulo
            if (!contentsByTitle.contains(title)) contentsByTitle.put(title, new ArrayList<>()); // Cria lista se ainda nao existir
            contentsByTitle.get(title).add(content); // Adiciona o conteudo a lista do titulo
        } // Fim da indexacao por titulo

        String region = content.getRegion(); // Vai buscar a regiao do conteudo
        if (region != null) { // So indexa se houver regiao
            if (!contentsByRegion.contains(region)) contentsByRegion.put(region, new ArrayList<>()); // Cria lista se ainda nao existir
            contentsByRegion.get(region).add(content); // Adiciona o conteudo a lista da regiao
        } // Fim da indexacao por regiao
    } // Fim do metodo addContentToIndices

    /**
     * Remove um conteudo dos indices secundarios com base nos seus valores atuais.
     *
     * @param content o conteudo a remover dos indices
     */
    private void removeContentFromIndices(Content content) { // Metodo privado que remove um conteudo dos indices secundarios
        if (content == null) return; // Protecao contra null

        int year = content.getReleaseYear(); // Le o ano de lancamento
        if (contentsByReleaseYear.contains(year)) { // Se houver lista para esse ano...
            List<Content> list = contentsByReleaseYear.get(year); // Vai buscar a lista
            list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID
            if (list.isEmpty()) contentsByReleaseYear.delete(year); // Se ficar vazia, apaga a chave
        } // Fim da remocao do indice por ano

        Genre genre = content.getGenre(); // Le o genero
        if (genre != null && genre.getName() != null) { // So tenta se o genero existir
            String gName = genre.getName(); // Guarda o nome do genero
            if (contentsByGenre.contains(gName)) { // Se houver lista para esse genero...
                List<Content> list = contentsByGenre.get(gName); // Vai buscar a lista
                list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID
                if (list.isEmpty()) contentsByGenre.delete(gName); // Se ficar vazia, apaga a chave
            } // Fim da verificacao do genero
        } // Fim da remocao do indice por genero

        String title = content.getTitle(); // Le o titulo
        if (title != null && contentsByTitle.contains(title)) { // Se houver lista para esse titulo...
            List<Content> list = contentsByTitle.get(title); // Vai buscar a lista
            list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID
            if (list.isEmpty()) contentsByTitle.delete(title); // Se ficar vazia, apaga a chave
        } // Fim da remocao do indice por titulo

        String region = content.getRegion(); // Le a regiao
        if (region != null && contentsByRegion.contains(region)) { // Se houver lista para essa regiao...
            List<Content> list = contentsByRegion.get(region); // Vai buscar a lista
            list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID
            if (list.isEmpty()) contentsByRegion.delete(region); // Se ficar vazia, apaga a chave
        } // Fim da remocao do indice por regiao
    } // Fim do metodo removeContentFromIndices

    /**
     * Adiciona um artista aos indices secundarios.
     *
     * @param artist o artista a indexar
     */
    private void addArtistToIndices(Artist artist) { // Metodo privado que insere um artista nos indices secundarios
        if (artist == null) return; // Protecao contra null

        String nationality = artist.getNationality(); // Vai buscar a nacionalidade do artista
        if (nationality != null) { // So indexa se houver nacionalidade
            if (!artistsByNationality.contains(nationality)) artistsByNationality.put(nationality, new ArrayList<>()); // Cria lista se ainda nao existir
            artistsByNationality.get(nationality).add(artist); // Adiciona o artista a lista da sua nacionalidade
        } // Fim da indexacao por nacionalidade

        LocalDate birthDate = artist.getBirthDate(); // Vai buscar a data de nascimento do artista
        if (birthDate != null) { // So indexa se houver data
            if (!artistsByBirthDate.contains(birthDate)) artistsByBirthDate.put(birthDate, new ArrayList<>()); // Cria lista se ainda nao existir
            artistsByBirthDate.get(birthDate).add(artist); // Adiciona o artista a lista da sua data de nascimento
        } // Fim da indexacao por data de nascimento
    } // Fim do metodo addArtistToIndices

    /**
     * Remove um artista dos indices secundarios com base nos seus valores atuais.
     *
     * @param artist o artista a remover dos indices
     */
    private void removeArtistFromIndices(Artist artist) { // Metodo privado que remove um artista dos indices secundarios
        if (artist == null) return; // Protecao contra null

        String nationality = artist.getNationality(); // Le a nacionalidade do artista
        if (nationality != null && artistsByNationality.contains(nationality)) { // Se houver lista para essa nacionalidade...
            List<Artist> list = artistsByNationality.get(nationality); // Vai buscar a lista
            list.removeIf(a -> a.getId().equals(artist.getId())); // Remove o artista com o mesmo ID
            if (list.isEmpty()) artistsByNationality.delete(nationality); // Se ficar vazia, apaga a chave
        } // Fim da remocao do indice por nacionalidade

        LocalDate birthDate = artist.getBirthDate(); // Le a data de nascimento
        if (birthDate != null && artistsByBirthDate.contains(birthDate)) { // Se houver lista para essa data...
            List<Artist> list = artistsByBirthDate.get(birthDate); // Vai buscar a lista
            list.removeIf(a -> a.getId().equals(artist.getId())); // Remove o artista com o mesmo ID
            if (list.isEmpty()) artistsByBirthDate.delete(birthDate); // Se ficar vazia, apaga a chave
        } // Fim da remocao do indice por data de nascimento
    } // Fim do metodo removeArtistFromIndices

    /**
     * Adiciona um genero na base de dados.
     *
     * @param g o genero a adicionar
     */
    /*
     * DICIONARIO:
     * - put: insere ou substitui um valor na tabela hash pela chave indicada
     */
    public void addGenre(Genre g) { // Metodo que adiciona um genero na tabela de generos
        if (g == null || g.getId() == null) return; // Ignora entradas invalidas
        this.genres.put(g.getId(), g); // Guarda o genero na tabela usando o ID como chave
    } // Fim do metodo addGenre

    /**
     * Vai buscar um utilizador pelo ID.
     *
     * @param userId o ID do utilizador a procurar
     * @return o utilizador encontrado, ou {@code null} se nao existir
     */
    /*
     * DICIONARIO:
     * - return: devolve um valor ao codigo que chamou este metodo
     * - null: valor especial que indica que nao existe objeto
     */
    public User getUser(String userId) { // Metodo que procura e devolve um utilizador pelo seu ID
        if (userId == null) return null; // Sem ID nao ha nada para procurar
        return this.users.get(userId); // Devolve o utilizador se existir
    } // Fim do metodo getUser

    /**
     * Vai buscar um conteudo pelo ID.
     *
     * @param contentId o ID do conteudo a procurar
     * @return o conteudo encontrado, ou {@code null} se nao existir
     */
    public Content getContent(String contentId) { // Metodo que procura e devolve um conteudo pelo seu ID
        if (contentId == null) return null; // Sem ID nao ha nada para procurar
        return this.contents.get(contentId); // Devolve o conteudo se existir
    } // Fim do metodo getContent

    /**
     * Vai buscar um artista pelo ID.
     *
     * @param artistId o ID do artista a procurar
     * @return o artista encontrado, ou {@code null} se nao existir
     */
    public Artist getArtist(String artistId) { // Metodo que procura e devolve um artista pelo seu ID
        if (artistId == null) return null; // Sem ID nao ha nada para procurar
        return this.artists.get(artistId); // Devolve o artista se existir
    } // Fim do metodo getArtist

    /**
     * Vai buscar um genero pelo ID.
     *
     * @param genreId o ID do genero a procurar
     * @return o genero encontrado, ou {@code null} se nao existir
     */
    public Genre getGenre(String genreId) { // Metodo que procura e devolve um genero pelo seu ID
        if (genreId == null) return null; // Sem ID nao ha nada para procurar
        return this.genres.get(genreId); // Devolve o genero se existir
    } // Fim do metodo getGenre

    /**
     * Vai buscar um utilizador arquivado pelo ID.
     *
     * @param userId o ID do utilizador arquivado a procurar
     * @return o utilizador arquivado encontrado, ou {@code null} se nao existir
     */
    public User getArchivedUser(String userId) { // Metodo que procura e devolve um utilizador arquivado pelo seu ID
        if (userId == null) return null; // Sem ID nao ha nada para procurar
        return this.archivedusers.get(userId); // Devolve o utilizador arquivado se existir
    } // Fim do metodo getArchivedUser

    /**
     * Devolve todos os utilizadores ativos.
     *
     * @return lista com todos os utilizadores ativos
     */
    /*
     * DICIONARIO:
     * - keys(): metodo de SeparateChainingHashST que devolve todas as chaves guardadas
     * - for-each: ciclo que percorre todos os elementos de uma colecao um a um
     */
    public List<User> listUsers() { // Metodo que devolve uma lista com todos os utilizadores ativos
        List<User> out = new ArrayList<>(); // Cria a lista de saida vazia
        for (String id : this.users.keys()) { // Percorre todos os IDs de utilizadores guardados
            User u = this.users.get(id); // Vai buscar o utilizador desse ID
            if (u != null) out.add(u); // Se existir, adiciona-o ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista completa de utilizadores
    } // Fim do metodo listUsers

    /**
     * Devolve todos os utilizadores arquivados.
     *
     * @return lista com todos os utilizadores arquivados
     */
    public List<User> listArchivedUsers() { // Metodo que devolve uma lista com todos os utilizadores arquivados
        List<User> out = new ArrayList<>(); // Cria a lista de saida vazia
        for (String id : this.archivedusers.keys()) { // Percorre todos os IDs arquivados
            User u = this.archivedusers.get(id); // Vai buscar o utilizador arquivado
            if (u != null) out.add(u); // Se existir, adiciona-o ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista completa de utilizadores arquivados
    } // Fim do metodo listArchivedUsers

    /**
     * Atualiza um utilizador arquivado.
     *
     * @param u o utilizador com os novos dados
     * @return {@code true} se a atualizacao foi bem sucedida
     */
    /*
     * DICIONARIO:
     * - boolean: tipo que so pode ser true ou false
     */
    public boolean updateArchivedUser(User u) { // Metodo que atualiza os dados de um utilizador arquivado
        if (u == null || u.getId() == null) return false; // Rejeita dados invalidos
        if (!this.archivedusers.contains(u.getId())) return false; // So atualiza se ja estiver arquivado
        this.archivedusers.put(u.getId(), u); // Substitui o utilizador arquivado pelo novo valor
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo updateArchivedUser

    /**
     * Remove um utilizador arquivado de forma definitiva.
     *
     * @param userId o ID do utilizador a remover do arquivo
     * @return {@code true} se a remocao foi bem sucedida
     */
    public boolean removeArchivedUser(String userId) { // Metodo que remove permanentemente um utilizador do arquivo
        if (userId == null) return false; // Sem ID nao ha nada para apagar
        if (!this.archivedusers.contains(userId)) return false; // So apaga se existir no arquivo
        this.archivedusers.delete(userId); // Apaga da tabela de arquivados
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo removeArchivedUser

    /**
     * Tira um utilizador do arquivo e devolve-o aos utilizadores ativos.
     *
     * @param userId o ID do utilizador a restaurar
     * @return {@code true} se o restauro foi bem sucedido
     */
    public boolean restoreArchivedUser(String userId) { // Metodo que restaura um utilizador do arquivo para os ativos
        if (userId == null) return false; // Sem ID nao ha nada para restaurar
        User u = this.archivedusers.get(userId); // Vai buscar o utilizador arquivado
        if (u == null) return false; // Se nao existir, falha
        if (this.users.contains(userId)) return false; // Se ja estiver ativo, nao faz sentido restaurar

        this.archivedusers.delete(userId); // Remove-o do arquivo
        addUser(u); // Adiciona-o de novo aos ativos e aos indices
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo restoreArchivedUser

    /**
     * Apaga todos os utilizadores arquivados.
     */
    public void clearArchivedUsers() { // Metodo que remove todos os utilizadores do arquivo de forma definitiva
        List<String> ids = new ArrayList<>(); // Cria uma lista temporaria de IDs a apagar
        for (String id : this.archivedusers.keys()) ids.add(id); // Copia os IDs para evitar modificar a estrutura durante o ciclo
        for (String id : ids) this.archivedusers.delete(id); // Apaga cada utilizador arquivado um a um
    } // Fim do metodo clearArchivedUsers

    /**
     * Devolve todos os conteudos.
     *
     * @return lista com todos os conteudos
     */
    public List<Content> listContents() { // Metodo que devolve uma lista com todos os conteudos
        List<Content> out = new ArrayList<>(); // Cria a lista de saida vazia
        for (String id : this.contents.keys()) { // Percorre todos os IDs de conteudos
            Content c = this.contents.get(id); // Vai buscar o conteudo
            if (c != null) out.add(c); // Se existir, adiciona-o ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista completa de conteudos
    } // Fim do metodo listContents

    /**
     * Devolve todos os artistas.
     *
     * @return lista com todos os artistas
     */
    public List<Artist> listArtists() { // Metodo que devolve uma lista com todos os artistas
        List<Artist> out = new ArrayList<>(); // Cria a lista de saida vazia
        for (String id : this.artists.keys()) { // Percorre todos os IDs de artistas
            Artist a = this.artists.get(id); // Vai buscar o artista
            if (a != null) out.add(a); // Se existir, adiciona-o ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista completa de artistas
    } // Fim do metodo listArtists

    /**
     * Devolve todos os generos.
     *
     * @return lista com todos os generos
     */
    public List<Genre> listGenres() { // Metodo que devolve uma lista com todos os generos
        List<Genre> out = new ArrayList<>(); // Cria a lista de saida vazia
        for (String id : this.genres.keys()) { // Percorre todos os IDs de generos
            Genre g = this.genres.get(id); // Vai buscar o genero
            if (g != null) out.add(g); // Se existir, adiciona-o ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista completa de generos
    } // Fim do metodo listGenres

    /**
     * Atualiza um utilizador existente.
     *
     * @param u o utilizador com os novos dados
     * @return {@code true} se a atualizacao foi bem sucedida
     */
    public boolean updateUser(User u) { // Metodo que atualiza os dados de um utilizador ativo
        if (u == null || u.getId() == null) return false; // Rejeita dados invalidos
        if (!this.users.contains(u.getId())) return false; // So atualiza se o utilizador ja existir
        addUser(u); // Reutiliza a logica de insercao para manter os indices consistentes
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo updateUser

    /**
     * Atualiza um conteudo existente.
     *
     * @param c o conteudo com os novos dados
     * @return {@code true} se a atualizacao foi bem sucedida
     */
    public boolean updateContent(Content c) { // Metodo que atualiza os dados de um conteudo
        if (c == null || c.getId() == null) return false; // Rejeita dados invalidos
        if (!this.contents.contains(c.getId())) return false; // So atualiza se o conteudo ja existir
        addContent(c); // Reutiliza a logica de insercao para manter os indices consistentes
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo updateContent

    /**
     * Atualiza um artista existente.
     *
     * @param a o artista com os novos dados
     * @return {@code true} se a atualizacao foi bem sucedida
     */
    public boolean updateArtist(Artist a) { // Metodo que atualiza os dados de um artista
        if (a == null || a.getId() == null) return false; // Rejeita dados invalidos
        if (!this.artists.contains(a.getId())) return false; // So atualiza se o artista ja existir
        addArtist(a); // Reutiliza a logica de insercao para manter os indices consistentes
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo updateArtist

    /**
     * Muda o nome de um genero e atualiza o indice dos conteudos por genero.
     *
     * @param genreId o ID do genero a atualizar
     * @param newName o novo nome para o genero
     * @return {@code true} se a atualizacao foi bem sucedida
     */
    /*
     * DICIONARIO:
     * - addAll: metodo de List que adiciona todos os elementos de outra lista
     */
    public boolean updateGenreName(String genreId, String newName) { // Metodo que atualiza o nome de um genero e reorganiza o indice
        if (genreId == null || newName == null) return false; // Rejeita valores invalidos
        Genre g = this.genres.get(genreId); // Vai buscar o genero pelo ID
        if (g == null) return false; // Se nao existir, falha

        String oldName = g.getName(); // Guarda o nome antigo antes de alterar
        if (oldName == null) oldName = ""; // Evita null na pesquisa do indice
        g.setName(newName); // Atualiza o nome no objeto genero

        if (this.contentsByGenre.contains(oldName)) { // Se houver conteudos indexados com o nome antigo...
            List<Content> moved = this.contentsByGenre.get(oldName); // Vai buscar a lista de conteudos com o nome antigo
            this.contentsByGenre.delete(oldName); // Apaga a entrada com o nome antigo do indice

            if (moved != null && !moved.isEmpty()) { // Se houver conteudos para mover...
                if (!this.contentsByGenre.contains(newName)) this.contentsByGenre.put(newName, new ArrayList<>()); // Cria a nova entrada no indice se ainda nao existir
                this.contentsByGenre.get(newName).addAll(moved); // Move todos os conteudos para a nova chave
            } // Fim da verificacao de conteudos a mover
        } // Fim da atualizacao do indice por genero
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo updateGenreName

    /**
     * Remove um utilizador e opcionalmente arquiva-o.
     *
     * @param userId o ID do utilizador a remover
     * @param archive {@code true} para guardar no arquivo antes de remover
     * @return {@code true} se a remocao foi bem sucedida
     */
    /*
     * DICIONARIO:
     * - archive: quando true, o utilizador e guardado no arquivo antes de ser apagado dos ativos
     */
    public boolean removeUser(String userId, boolean archive) { // Metodo que remove um utilizador dos ativos, guardando-o no arquivo se pedido
        if (userId == null) return false; // Sem ID nao ha nada para remover
        User u = this.users.get(userId); // Vai buscar o utilizador
        if (u == null) return false; // Se nao existir, falha

        removeUserFromIndices(u); // Remove o utilizador dos indices secundarios
        this.users.delete(userId); // Remove o utilizador da tabela principal
        if (archive) this.archivedusers.put(userId, u); // Se for pedido, guarda-o no arquivo
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo removeUser

    /**
     * Remove um artista.
     *
     * @param artistId o ID do artista a remover
     * @return {@code true} se a remocao foi bem sucedida
     */
    public boolean removeArtist(String artistId) { // Metodo que remove um artista da base de dados
        if (artistId == null) return false; // Sem ID nao ha nada para remover
        Artist a = this.artists.get(artistId); // Vai buscar o artista
        if (a == null) return false; // Se nao existir, falha

        removeArtistFromIndices(a); // Remove o artista dos indices secundarios
        this.artists.delete(artistId); // Remove o artista da tabela principal
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo removeArtist

    /**
     * Remove um genero so se nenhum conteudo o estiver a usar.
     *
     * @param genreId o ID do genero a remover
     * @return {@code true} se a remocao foi bem sucedida
     */
    public boolean removeGenre(String genreId) { // Metodo que remove um genero apenas se nenhum conteudo o referenciar
        if (genreId == null) return false; // Sem ID nao ha nada para remover
        Genre g = this.genres.get(genreId); // Vai buscar o genero
        if (g == null) return false; // Se nao existir, falha

        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos para verificar dependencias
            Content c = this.contents.get(contentId); // Vai buscar cada conteudo
            if (c == null) continue; // Ignora entradas vazias
            Genre cg = c.getGenre(); // Vai buscar o genero do conteudo
            if (cg != null && genreId.equals(cg.getId())) return false; // Se algum conteudo usar este genero, nao remove
        } // Fim da verificacao de dependencias

        this.genres.delete(genreId); // Se nao houver referencias, apaga o genero
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo removeGenre

    /**
     * Remove um conteudo apenas se ele existir.
     *
     * @param contentId o ID do conteudo a remover
     * @return {@code true} se a remocao foi bem sucedida
     */
    public boolean removeContentIfExists(String contentId) { // Metodo que remove um conteudo se ele existir na base de dados
        if (contentId == null) return false; // Sem ID nao ha nada para remover
        if (!this.contents.contains(contentId)) return false; // So remove se o conteudo existir
        removeContent(contentId); // Chama a remocao completa do conteudo
        return true; // Indica que a operacao foi bem sucedida
    } // Fim do metodo removeContentIfExists

    /**
     * Procura utilizadores pela regiao.
     *
     * @param r a regiao a pesquisar
     * @return lista de utilizadores dessa regiao
     */
    public List<User> searchUsersByRegion(String r) { // Metodo que pesquisa utilizadores por regiao geografica
        if (r == null || !this.usersByRegion.contains(r)) { // Se a regiao nao existir no indice...
            return new ArrayList<>(); // Devolve lista vazia
        } // Fim da verificacao
        return this.usersByRegion.get(r); // Devolve a lista de utilizadores dessa regiao
    } // Fim do metodo searchUsersByRegion

    /**
     * Procura utilizadores registados entre duas datas.
     *
     * @param start data inicial do intervalo
     * @param end data final do intervalo
     * @return lista de utilizadores registados nesse periodo
     */
    /*
     * DICIONARIO:
     * - isAfter: metodo de LocalDate que verifica se uma data e posterior a outra
     * - keys(lo, hi): metodo de RedBlackBST que devolve as chaves dentro do intervalo [lo, hi]
     */
    public List<User> searchUsersRegisteredBetween(LocalDate start, LocalDate end) { // Metodo que pesquisa utilizadores registados entre duas datas
        if (start == null || end == null) return new ArrayList<>(); // Sem datas nao ha pesquisa
        if (start.isAfter(end)) { // Se as datas vierem trocadas...
            LocalDate tmp = start; // Guarda a data inicial temporariamente
            start = end; // Troca a ordem
            end = tmp; // Completa a troca
        } // Fim da normalizacao do intervalo

        List<User> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (ChronoLocalDate d : this.usersByRegistrationDate.keys(start, end)) { // Percorre as datas dentro do intervalo
            List<User> dayUsers = this.usersByRegistrationDate.get(d); // Vai buscar os utilizadores dessa data
            if (dayUsers != null) out.addAll(dayUsers); // Adiciona todos os utilizadores ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista final de utilizadores
    } // Fim do metodo searchUsersRegisteredBetween

    /**
     * Procura utilizadores cujo nome contem um determinado texto.
     *
     * @param substring o texto a pesquisar
     * @return lista de utilizadores cujo nome contem o texto
     */
    /*
     * DICIONARIO:
     * - toLowerCase: metodo de String que converte o texto para letras minusculas
     * - contains: metodo de String que verifica se um texto esta dentro de outro texto
     * - Locale.ROOT: modo neutro para converter texto sem depender do idioma do sistema
     */
    public List<User> searchUsersByUsernameSubstring(String substring) { // Metodo que pesquisa utilizadores pelo nome de utilizador
        if (substring == null) return new ArrayList<>(); // Sem texto nao ha pesquisa
        String needle = substring.toLowerCase(Locale.ROOT); // Converte para minusculas para comparar sem diferencas de maiusculas
        if (needle.isEmpty()) return new ArrayList<>(); // Se o texto ficar vazio, devolve lista vazia

        List<User> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (String userId : this.users.keys()) { // Percorre todos os utilizadores
            User u = this.users.get(userId); // Vai buscar o utilizador
            if (u == null) continue; // Ignora valores vazios
            String username = u.getUsername(); // Vai buscar o nome de utilizador
            if (username != null && username.toLowerCase(Locale.ROOT).contains(needle)) out.add(u); // Se o nome contiver o texto, adiciona ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista final de utilizadores
    } // Fim do metodo searchUsersByUsernameSubstring

    /**
     * Procura conteudos pelo nome do genero.
     *
     * @param genreName o nome do genero a pesquisar
     * @return lista de conteudos desse genero
     */
    public List<Content> searchByGenre(String genreName) { // Metodo que pesquisa conteudos pelo nome do genero
        if (genreName == null || !this.contentsByGenre.contains(genreName)) { // Se nao houver conteudos com esse genero...
            return new ArrayList<>(); // Devolve lista vazia
        } // Fim da verificacao
        return this.contentsByGenre.get(genreName); // Devolve a lista de conteudos desse genero
    } // Fim do metodo searchByGenre

    /**
     * Procura conteudos lancados entre dois anos.
     *
     * @param startYear ano inicial do intervalo
     * @param endYear ano final do intervalo
     * @return lista de conteudos lancados nesse intervalo
     */
    public List<Content> searchContentsReleasedBetween(int startYear, int endYear) { // Metodo que pesquisa conteudos por intervalo de anos de lancamento
        if (startYear > endYear) { // Se os anos vierem trocados...
            int tmp = startYear; // Guarda o ano inicial temporariamente
            startYear = endYear; // Troca a ordem
            endYear = tmp; // Completa a troca
        } // Fim da normalizacao do intervalo

        List<Content> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (Integer year : this.contentsByReleaseYear.keys(startYear, endYear)) { // Percorre os anos dentro do intervalo
            List<Content> yearContents = this.contentsByReleaseYear.get(year); // Vai buscar os conteudos desse ano
            if (yearContents != null) out.addAll(yearContents); // Adiciona todos os conteudos ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista final de conteudos
    } // Fim do metodo searchContentsReleasedBetween

    /**
     * Procura conteudos cujo titulo contem um determinado texto.
     *
     * @param substring o texto a pesquisar no titulo
     * @return lista de conteudos cujo titulo contem o texto
     */
    public List<Content> searchContentsByTitleSubstring(String substring) { // Metodo que pesquisa conteudos pelo titulo
        if (substring == null) return new ArrayList<>(); // Sem texto nao ha pesquisa
        String needle = substring.toLowerCase(Locale.ROOT); // Converte para minusculas para melhor comparacao
        if (needle.isEmpty()) return new ArrayList<>(); // Se o texto ficar vazio, devolve lista vazia

        List<Content> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos
            Content c = this.contents.get(contentId); // Vai buscar o conteudo
            if (c == null) continue; // Ignora valores vazios
            String title = c.getTitle(); // Vai buscar o titulo do conteudo
            if (title != null && title.toLowerCase(Locale.ROOT).contains(needle)) out.add(c); // Se o titulo contiver o texto, adiciona ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista final de conteudos
    } // Fim do metodo searchContentsByTitleSubstring

    /**
     * Procura conteudos por tipo.
     *
     * @param contentType o tipo de conteudo a pesquisar (ex: "Movie", "Series", "Documentary")
     * @return lista de conteudos desse tipo
     */
    /*
     * DICIONARIO:
     * - trim: metodo de String que remove espacos em branco no inicio e no fim do texto
     * - getContentType: metodo de Content que devolve o tipo do conteudo como texto
     */
    public List<Content> searchContentsByType(String contentType) { // Metodo que pesquisa conteudos pelo tipo
        if (contentType == null) return new ArrayList<>(); // Sem tipo nao ha pesquisa
        String needle = contentType.toLowerCase(Locale.ROOT).trim(); // Normaliza o texto para comparacao
        if (needle.isEmpty()) return new ArrayList<>(); // Se ficar vazio, devolve lista vazia

        List<Content> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos
            Content c = this.contents.get(contentId); // Vai buscar o conteudo
            if (c == null) continue; // Ignora valores vazios
            String type = c.getContentType(); // Vai buscar o tipo do conteudo
            if (type != null && type.toLowerCase(Locale.ROOT).equals(needle)) { // Se o tipo corresponder ao pesquisado...
                out.add(c); // Adiciona ao resultado
            } // Fim da verificacao do tipo
        } // Fim do ciclo
        return out; // Devolve a lista final de conteudos
    } // Fim do metodo searchContentsByType

    /**
     * Procura artistas pela nacionalidade.
     *
     * @param nationality a nacionalidade a pesquisar
     * @return lista de artistas dessa nacionalidade
     */
    public List<Artist> searchArtistsByNationality(String nationality) { // Metodo que pesquisa artistas pela nacionalidade
        if (nationality == null || !this.artistsByNationality.contains(nationality)) return new ArrayList<>(); // Se nao existir, devolve lista vazia
        return this.artistsByNationality.get(nationality); // Devolve a lista de artistas dessa nacionalidade
    } // Fim do metodo searchArtistsByNationality

    /**
     * Procura artistas nascidos entre duas datas.
     *
     * @param start data inicial do intervalo
     * @param end data final do intervalo
     * @return lista de artistas nascidos nesse periodo
     */
    public List<Artist> searchArtistsBornBetween(LocalDate start, LocalDate end) { // Metodo que pesquisa artistas por intervalo de datas de nascimento
        if (start == null || end == null) return new ArrayList<>(); // Sem datas nao ha pesquisa
        if (start.isAfter(end)) { // Se as datas vierem trocadas...
            LocalDate tmp = start; // Guarda a data inicial temporariamente
            start = end; // Troca a ordem
            end = tmp; // Completa a troca
        } // Fim da normalizacao do intervalo

        List<Artist> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (ChronoLocalDate d : this.artistsByBirthDate.keys(start, end)) { // Percorre as datas dentro do intervalo
            List<Artist> dayArtists = this.artistsByBirthDate.get(d); // Vai buscar os artistas dessa data
            if (dayArtists != null) out.addAll(dayArtists); // Adiciona todos os artistas ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista final de artistas
    } // Fim do metodo searchArtistsBornBetween

    /**
     * Procura artistas cujo nome contem um determinado texto.
     *
     * @param substring o texto a pesquisar no nome
     * @return lista de artistas cujo nome contem o texto
     */
    public List<Artist> searchArtistsByNameSubstring(String substring) { // Metodo que pesquisa artistas pelo nome
        if (substring == null) return new ArrayList<>(); // Sem texto nao ha pesquisa
        String needle = substring.toLowerCase(Locale.ROOT); // Converte para minusculas para melhor comparacao
        if (needle.isEmpty()) return new ArrayList<>(); // Se o texto ficar vazio, devolve lista vazia

        List<Artist> out = new ArrayList<>(); // Cria a lista de resultados vazia
        for (String artistId : this.artists.keys()) { // Percorre todos os artistas
            Artist a = this.artists.get(artistId); // Vai buscar o artista
            if (a == null) continue; // Ignora valores vazios
            String name = a.getName(); // Vai buscar o nome do artista
            if (name != null && name.toLowerCase(Locale.ROOT).contains(needle)) out.add(a); // Se o nome contiver o texto, adiciona ao resultado
        } // Fim do ciclo
        return out; // Devolve a lista final de artistas
    } // Fim do metodo searchArtistsByNameSubstring

    /**
     * Devolve os anos que tiveram conteudos dentro do intervalo pedido.
     *
     * @param startYear ano inicial do intervalo
     * @param endYear ano final do intervalo
     * @return sequencia de anos com conteudos nesse intervalo
     */
    public Iterable<Integer> getYearsWithContent(int startYear, int endYear) { // Metodo que devolve os anos com conteudos dentro de um intervalo
        if (startYear > endYear) { // Se os anos vierem trocados...
            int tmp = startYear; // Guarda o ano inicial temporariamente
            startYear = endYear; // Troca a ordem
            endYear = tmp; // Completa a troca
        } // Fim da normalizacao do intervalo
        return this.contentsByReleaseYear.keys(startYear, endYear); // Devolve os anos dentro do intervalo com conteudos
    } // Fim do metodo getYearsWithContent

    /**
     * Remove um conteudo da base de dados e de todos os indices.
     *
     * @param contentId o ID do conteudo a remover
     */
    public void removeContent(String contentId) { // Metodo que remove um conteudo da base de dados e dos seus indices
        if (contentId == null) return; // Sem ID nao ha nada para remover
        Content c = this.contents.get(contentId); // Vai buscar o conteudo
        if (c == null) return; // Se nao existir, nao faz nada
        removeContentFromIndices(c); // Remove o conteudo dos indices secundarios
        this.contents.delete(contentId); // Apaga o conteudo da tabela principal
    } // Fim do metodo removeContent

    /**
     * Confirma se os indices continuam alinhados com os dados principais.
     *
     * @return {@code true} se os indices estiverem consistentes
     */
    /*
     * DICIONARIO:
     * - consistencia: garantia de que os dados nos indices correspondem aos dados principais
     * - containsContentId: metodo privado que verifica se um ID existe numa lista de conteudos
     */
    public boolean validateConsistency() { // Metodo que verifica se todos os indices estao alinhados com os dados principais
        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos guardados
            Content c = this.contents.get(contentId); // Vai buscar o conteudo
            if (c == null) return false; // Se houver uma entrada vazia, algo esta errado
            int year = c.getReleaseYear(); // Le o ano de lancamento
            if (!this.contentsByReleaseYear.contains(year)) return false; // O ano tem de existir no indice
            if (!containsContentId(this.contentsByReleaseYear.get(year), contentId)) return false; // O conteudo tem de estar na lista desse ano
        } // Fim do ciclo de verificacao de conteudos
        for (String userId : this.users.keys()) { // Percorre todos os utilizadores guardados
            User u = this.users.get(userId); // Vai buscar o utilizador
            if (u == null) return false; // Se houver uma entrada vazia, algo esta errado
            String region = u.getRegion(); // Le a regiao do utilizador
            if (region != null && !this.usersByRegion.contains(region)) return false; // A regiao tem de existir no indice
        } // Fim do ciclo de verificacao de utilizadores
        return true; // Se chegou aqui, a consistencia esta OK
    } // Fim do metodo validateConsistency

    /**
     * Verifica se uma lista de conteudos contem um ID especifico.
     *
     * @param list lista de conteudos a verificar
     * @param contentId o ID a procurar
     * @return {@code true} se o ID for encontrado na lista
     */
    private static boolean containsContentId(List<Content> list, String contentId) { // Metodo privado que verifica se um ID de conteudo existe numa lista
        if (list == null || contentId == null) return false; // Protecao contra valores null
        for (Content c : list) if (c != null && contentId.equals(c.getId())) return true; // Se encontrar o ID, devolve true
        return false; // Se nao encontrar, devolve false
    } // Fim do metodo containsContentId

    /**
     * Remove um utilizador de uma lista pelo ID.
     *
     * @param list lista de utilizadores
     * @param userId o ID do utilizador a remover
     */
    private void removeUserById(List<User> list, String userId) { // Metodo privado que remove um utilizador de uma lista pelo seu ID
        if (list == null || userId == null) return; // Protecao contra valores null
        list.removeIf(u -> u.getId().equals(userId)); // Remove o utilizador com esse ID da lista
    } // Fim do metodo removeUserById

} // Fim da classe StreamingDB
