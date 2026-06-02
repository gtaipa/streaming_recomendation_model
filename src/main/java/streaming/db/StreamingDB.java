package streaming.db;

import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import streaming.model.Artist;
import streaming.model.Content;
import streaming.model.Genre;
import streaming.model.User;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @brief Dicionario simples dos termos tecnicos usados neste ficheiro.
 *
 * - package: nome da zona do projeto onde esta classe vive.
 * - import: traz outras classes para podermos usa-las aqui.
 * - hash table: estrutura para procurar dados muito depressa pela chave.
 * - SeparateChainingHashST: tabela hash com listas para lidar com colisoes.
 * - BST: arvore de pesquisa, usada para procurar por ordem.
 * - RedBlackBST: arvore BST equilibrada, boa para pesquisas por intervalo.
 * - List<T>: lista de elementos do mesmo tipo.
 * - LocalDate: data sem hora.
 * - ChronoLocalDate: tipo de data comparavel em arvores por intervalo.
 * - Locale.ROOT: modo neutro para converter texto sem depender do idioma.
 * - null: valor vazio, quando nao ha objeto.
 * - put: guarda ou atualiza um valor.
 * - get: vai buscar um valor.
 * - contains: verifica se uma chave existe.
 * - delete: apaga uma chave.
 * - keys(): devolve as chaves guardadas.
 * - removeIf: remove da lista os elementos que cumpram a condicao.
 * - addAll: junta uma lista inteira noutra lista.
 * - contains(texto): ve se um texto esta dentro de outro texto.
 * - O(1): custo quase fixo, muito rapido.
 * - O(log N): custo que cresce devagar quando os dados aumentam.
 *
 * Blocos da classe:
 * - Campos: guardam os dados principais e os indices.
 * - Construtor: cria todas as estruturas vazias.
 * - add*: insere dados e atualiza os indices.
 * - remove*: apaga dados e limpa os indices.
 * - update*: altera dados existentes sem perder a consistencia.
 * - search*: procura dados por chave, texto ou intervalo.
 * - validateConsistency: confirma se os indices continuam certos.
 */

/**
 * @brief Base de dados em memoria para a plataforma de streaming.
 *
 * Usa tabelas hash para acesso rapido por ID e arvores equilibradas para buscas por ordem e intervalo.
 */
public class StreamingDB {

    /** Guarda os users ativos por ID. */
    private SeparateChainingHashST<String, User> users;
    /** Guarda os users arquivados por ID. */
    private SeparateChainingHashST<String, User> archivedusers;
    /** Guarda os conteudos por ID. */
    private SeparateChainingHashST<String, Content> contents;
    /** Guarda os artistas por ID. */
    private SeparateChainingHashST<String, Artist> artists;
    /** Guarda os generos por ID. */
    private SeparateChainingHashST<String, Genre> genres;

    /** Indexa users pela regiao. */
    private RedBlackBST<String, List<User>> usersByRegion;
    /** Indexa users pela data de registo. */
    private RedBlackBST<ChronoLocalDate, List<User>> usersByRegistrationDate;
    /** Indexa conteudos pelo nome do genero. */
    private RedBlackBST<String, List<Content>> contentsByGenre;
    /** Indexa conteudos pelo titulo. */
    private RedBlackBST<String, List<Content>> contentsByTitle;
    /** Indexa conteudos pela regiao. */
    private RedBlackBST<String, List<Content>> contentsByRegion;
    /** Indexa artistas pela nacionalidade. */
    private RedBlackBST<String, List<Artist>> artistsByNationality;
    /** Indexa artistas pela data de nascimento. */
    private RedBlackBST<ChronoLocalDate, List<Artist>> artistsByBirthDate;
    /** Indexa conteudos pelo ano de lancamento. */
    private RedBlackBST<Integer, List<Content>> contentsByReleaseYear;
    /** Indexa conteudos pela nota. */
    private RedBlackBST<Double, List<Content>> contentsByRating;
    /** Indexa conteudos pelo numero de visualizacoes. */
    private RedBlackBST<Integer, List<Content>> contentsByViews;
    /** Indexa conteudos pela duracao. */
    private RedBlackBST<Integer, List<Content>> contentsByDuration;

    /**
     * Cria uma base de dados nova e vazia.
     */
    public StreamingDB() {
        this.users = new SeparateChainingHashST<>(); // Cria a tabela dos users ativos.
        this.contents = new SeparateChainingHashST<>(); // Cria a tabela dos conteudos.
        this.artists = new SeparateChainingHashST<>(); // Cria a tabela dos artistas.
        this.genres = new SeparateChainingHashST<>(); // Cria a tabela dos generos.
        this.archivedusers = new SeparateChainingHashST<>(); // Cria a tabela dos users arquivados.

        this.usersByRegion = new RedBlackBST<>(); // Cria o indice dos users por regiao.
        this.usersByRegistrationDate = new RedBlackBST<>(); // Cria o indice dos users por data.
        this.contentsByGenre = new RedBlackBST<>(); // Cria o indice dos conteudos por genero.
        this.contentsByTitle = new RedBlackBST<>(); // Cria o indice dos conteudos por titulo.
        this.contentsByRegion = new RedBlackBST<>(); // Cria o indice dos conteudos por regiao.
        this.artistsByNationality = new RedBlackBST<>(); // Cria o indice dos artistas por nacionalidade.
        this.artistsByBirthDate = new RedBlackBST<>(); // Cria o indice dos artistas por nascimento.
        this.contentsByReleaseYear = new RedBlackBST<>(); // Cria o indice dos conteudos por ano.
        this.contentsByRating = new RedBlackBST<>(); // Cria o indice dos conteudos por nota.
        this.contentsByViews = new RedBlackBST<>(); // Cria o indice dos conteudos por visualizacoes.
        this.contentsByDuration = new RedBlackBST<>(); // Cria o indice dos conteudos por duracao.
    }

    /**
     * Adiciona um user novo. Se o ID ja existir, troca o user antigo pelo novo.
     */
    public void addUser(User newUser) {
        if (newUser == null || newUser.getId() == null) return; // Ignora valores vazios ou sem ID.

        // Se ja existir um utilizador com este ID na base de dados
        if (users.contains(newUser.getId())) {
            // Obtém o objeto utilizador armazenado.
            User storedUser = users.get(newUser.getId());
            
            // Cria um utilizador auxiliar com as propriedades ANTIGAS (antes de qualquer mudança)
            // Este auxiliar será usado apenas para remover dos índices.
            User auxiliarOldUser = new User(
                storedUser.getId(),
                storedUser.getCreatedAt(),
                storedUser.getUsername(),
                storedUser.getEmail(),
                storedUser.getPasswordHash()
            );
            // Copia as propriedades indexadas (antigas)
            auxiliarOldUser.setRegion(storedUser.getRegion());
            auxiliarOldUser.setRegistrationDate(storedUser.getRegistrationDate());
            
            // Remove o utilizador dos indices usando o auxiliar com as propriedades antigas.
            removeUserFromIndices(auxiliarOldUser);
        }

        // Agora, coloca o newUser no armazenamento principal e adiciona aos novos indices.
        users.put(newUser.getId(), newUser);
        addUserToIndices(newUser);
    }

    /**
     * Adiciona um conteudo novo. Se o ID ja existir, troca o conteudo antigo pelo novo.
     */
    public void addContent(Content newContent) {
        if (newContent == null || newContent.getId() == null) return; // Ignora entradas invalidas.

        if (contents.contains(newContent.getId())) { // Se o conteudo ja existir...
            Content oldContent = contents.get(newContent.getId()); // Vai buscar o conteudo antigo.
            removeContentFromIndices(oldContent); // Limpa o conteudo antigo dos indices.
        }

        contents.put(newContent.getId(), newContent); // Guarda o conteudo na tabela principal.
        addContentToIndices(newContent); // Regista o conteudo nos indices.
    }

    /**
     * Adiciona um artista novo. Se o ID ja existir, troca o artista antigo pelo novo.
     */
    public void addArtist(Artist newArtist) {
        if (newArtist == null || newArtist.getId() == null) return; // Ignora entradas invalidas.

        if (artists.contains(newArtist.getId())) { // Se o artista ja existir...
            Artist oldArtist = artists.get(newArtist.getId()); // Vai buscar o artista antigo.
            removeArtistFromIndices(oldArtist); // Remove o artista antigo dos indices.
        }

        artists.put(newArtist.getId(), newArtist); // Guarda o artista na tabela principal.
        addArtistToIndices(newArtist); // Adiciona o artista aos indices secundarios.
    }

    /**
     * Mete um user nos indices que dependem dele.
     */
    private void addUserToIndices(User user) {
        if (user == null) return; // Protecao contra valores vazios.

        String region = user.getRegion(); // Vai buscar a regiao do user.
        if (region != null) { // So cria indice se houver regiao.
            if (!usersByRegion.contains(region)) usersByRegion.put(region, new ArrayList<>()); // Cria a lista da regiao se faltar.
            usersByRegion.get(region).add(user); // Junta o user a essa lista.
        }

        LocalDate regDate = user.getRegistrationDate(); // Vai buscar a data de registo.
        if (regDate != null) { // So cria indice se houver data.
            if (!usersByRegistrationDate.contains(regDate)) usersByRegistrationDate.put(regDate, new ArrayList<>()); // Cria a lista da data se faltar.
            usersByRegistrationDate.get(regDate).add(user); // Junta o user a essa lista.
        }
    }

    /**
     * Tira um user dos indices usando os valores atuais dele.
     */
    private void removeUserFromIndices(User user) {
        if (user == null) return; // Protecao contra null.

        String region = user.getRegion(); // Lê a regiao atual.
        if (region != null && usersByRegion.contains(region)) { // Se existir indice dessa regiao...
            List<User> list = usersByRegion.get(region); // Vai buscar a lista.
            list.removeIf(u -> u.getId().equals(user.getId())); // Remove o user com o mesmo ID.
            if (list.isEmpty()) usersByRegion.delete(region); // Se a lista ficar vazia, apaga a chave.
        }

        LocalDate regDate = user.getRegistrationDate(); // Lê a data atual.
        if (regDate != null && usersByRegistrationDate.contains(regDate)) { // Se existir indice dessa data...
            List<User> list = usersByRegistrationDate.get(regDate); // Vai buscar a lista.
            list.removeIf(u -> u.getId().equals(user.getId())); // Remove o user com o mesmo ID.
            if (list.isEmpty()) usersByRegistrationDate.delete(regDate); // Se ficar vazia, apaga a chave.
        }
    }

    /**
     * Mete um conteudo nos indices que dependem dele.
     */
    private void addContentToIndices(Content content) {
        if (content == null) return; // Protecao contra null.

        int year = content.getReleaseYear(); // Vai buscar o ano de lancamento.
        if (!contentsByReleaseYear.contains(year)) contentsByReleaseYear.put(year, new ArrayList<>()); // Cria lista se faltar.
        contentsByReleaseYear.get(year).add(content); // Junta o conteudo ao ano certo.

        Genre genre = content.getGenre(); // Vai buscar o genero.
        if (genre != null && genre.getName() != null) { // So indexa se o genero tiver nome.
            String gName = genre.getName(); // Guarda o nome do genero.
            if (!contentsByGenre.contains(gName)) contentsByGenre.put(gName, new ArrayList<>()); // Cria lista se faltar.
            contentsByGenre.get(gName).add(content); // Junta o conteudo ao genero.
        }

        String title = content.getTitle(); // Vai buscar o titulo.
        if (title != null) { // So indexa se houver titulo.
            if (!contentsByTitle.contains(title)) contentsByTitle.put(title, new ArrayList<>()); // Cria lista se faltar.
            contentsByTitle.get(title).add(content); // Junta o conteudo ao titulo.
        }

        String region = content.getRegion(); // Vai buscar a regiao.
        if (region != null) { // So indexa se houver regiao.
            if (!contentsByRegion.contains(region)) contentsByRegion.put(region, new ArrayList<>()); // Cria lista se faltar.
            contentsByRegion.get(region).add(content); // Junta o conteudo a essa regiao.
        }
    }

    /**
     * Tira um conteudo dos indices usando os valores atuais dele.
     */
    private void removeContentFromIndices(Content content) {
        if (content == null) return; // Protecao contra null.

        int year = content.getReleaseYear(); // Lê o ano.
        if (contentsByReleaseYear.contains(year)) { // Se houver lista desse ano...
            List<Content> list = contentsByReleaseYear.get(year); // Vai buscar a lista.
            list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID.
            if (list.isEmpty()) contentsByReleaseYear.delete(year); // Se ficar vazia, apaga a chave.
        }

        Genre genre = content.getGenre(); // Lê o genero.
        if (genre != null && genre.getName() != null) { // So tenta se o genero existir.
            String gName = genre.getName(); // Guarda o nome do genero.
            if (contentsByGenre.contains(gName)) { // Se houver lista desse genero...
                List<Content> list = contentsByGenre.get(gName); // Vai buscar a lista.
                list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID.
                if (list.isEmpty()) contentsByGenre.delete(gName); // Se ficar vazia, apaga a chave.
            }
        }

        String title = content.getTitle(); // Lê o titulo.
        if (title != null && contentsByTitle.contains(title)) { // Se houver lista desse titulo...
            List<Content> list = contentsByTitle.get(title); // Vai buscar a lista.
            list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID.
            if (list.isEmpty()) contentsByTitle.delete(title); // Se ficar vazia, apaga a chave.
        }

        String region = content.getRegion(); // Lê a regiao.
        if (region != null && contentsByRegion.contains(region)) { // Se houver lista dessa regiao...
            List<Content> list = contentsByRegion.get(region); // Vai buscar a lista.
            list.removeIf(c -> c.getId().equals(content.getId())); // Remove o conteudo com o mesmo ID.
            if (list.isEmpty()) contentsByRegion.delete(region); // Se ficar vazia, apaga a chave.
        }
    }

    /**
     * Mete um artista nos indices que dependem dele.
     */
    private void addArtistToIndices(Artist artist) {
        if (artist == null) return; // Protecao contra null.

        String nationality = artist.getNationality(); // Vai buscar a nacionalidade.
        if (nationality != null) { // So indexa se houver nacionalidade.
            if (!artistsByNationality.contains(nationality)) artistsByNationality.put(nationality, new ArrayList<>()); // Cria lista se faltar.
            artistsByNationality.get(nationality).add(artist); // Junta o artista a essa nacionalidade.
        }

        LocalDate birthDate = artist.getBirthDate(); // Vai buscar a data de nascimento.
        if (birthDate != null) { // So indexa se houver data.
            if (!artistsByBirthDate.contains(birthDate)) artistsByBirthDate.put(birthDate, new ArrayList<>()); // Cria lista se faltar.
            artistsByBirthDate.get(birthDate).add(artist); // Junta o artista a essa data.
        }
    }

    /**
     * Tira um artista dos indices usando os valores atuais dele.
     */
    private void removeArtistFromIndices(Artist artist) {
        if (artist == null) return; // Protecao contra null.

        String nationality = artist.getNationality(); // Lê a nacionalidade.
        if (nationality != null && artistsByNationality.contains(nationality)) { // Se houver lista dessa nacionalidade...
            List<Artist> list = artistsByNationality.get(nationality); // Vai buscar a lista.
            list.removeIf(a -> a.getId().equals(artist.getId())); // Remove o artista com o mesmo ID.
            if (list.isEmpty()) artistsByNationality.delete(nationality); // Se ficar vazia, apaga a chave.
        }

        LocalDate birthDate = artist.getBirthDate(); // Lê a data de nascimento.
        if (birthDate != null && artistsByBirthDate.contains(birthDate)) { // Se houver lista dessa data...
            List<Artist> list = artistsByBirthDate.get(birthDate); // Vai buscar a lista.
            list.removeIf(a -> a.getId().equals(artist.getId())); // Remove o artista com o mesmo ID.
            if (list.isEmpty()) artistsByBirthDate.delete(birthDate); // Se ficar vazia, apaga a chave.
        }
    }

    /**
     * Adiciona um genero na base de dados.
     */
    public void addGenre(Genre g) {
        if (g == null || g.getId() == null) return; // Ignora entradas invalidas.
        this.genres.put(g.getId(), g); // Guarda o genero pela chave ID.
    }

    /**
     * Vai buscar um user pelo ID.
     */
    public User getUser(String userId) {
        if (userId == null) return null; // Sem ID nao ha nada para procurar.
        return this.users.get(userId); // Devolve o user se existir.
    }

    /**
     * Vai buscar um conteudo pelo ID.
     */
    public Content getContent(String contentId) {
        if (contentId == null) return null; // Sem ID nao ha nada para procurar.
        return this.contents.get(contentId); // Devolve o conteudo se existir.
    }

    /**
     * Vai buscar um artista pelo ID.
     */
    public Artist getArtist(String artistId) {
        if (artistId == null) return null; // Sem ID nao ha nada para procurar.
        return this.artists.get(artistId); // Devolve o artista se existir.
    }

    /**
     * Vai buscar um genero pelo ID.
     */
    public Genre getGenre(String genreId) {
        if (genreId == null) return null; // Sem ID nao ha nada para procurar.
        return this.genres.get(genreId); // Devolve o genero se existir.
    }

    /**
     * Vai buscar um user arquivado pelo ID.
     */
    public User getArchivedUser(String userId) {
        if (userId == null) return null; // Sem ID nao ha nada para procurar.
        return this.archivedusers.get(userId); // Devolve o user arquivado se existir.
    }

    /**
     * Devolve todos os users ativos.
     */
    public List<User> listUsers() {
        List<User> out = new ArrayList<>(); // Cria a lista de saida.
        for (String id : this.users.keys()) { // Percorre todos os IDs guardados.
            User u = this.users.get(id); // Vai buscar o user desse ID.
            if (u != null) out.add(u); // Se existir, junta-o ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Devolve todos os users arquivados.
     */
    public List<User> listArchivedUsers() {
        List<User> out = new ArrayList<>(); // Cria a lista de saida.
        for (String id : this.archivedusers.keys()) { // Percorre todos os IDs arquivados.
            User u = this.archivedusers.get(id); // Vai buscar o user arquivado.
            if (u != null) out.add(u); // Se existir, junta-o ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Atualiza um user arquivado.
     */
    public boolean updateArchivedUser(User u) {
        if (u == null || u.getId() == null) return false; // Rejeita dados invalidos.
        if (!this.archivedusers.contains(u.getId())) return false; // So atualiza se ja estiver arquivado.
        this.archivedusers.put(u.getId(), u); // Substitui pelo novo valor.
        return true; // Diz que correu bem.
    }

    /**
     * Remove um user arquivado de forma definitiva.
     */
    public boolean removeArchivedUser(String userId) {
        if (userId == null) return false; // Sem ID nao ha nada para apagar.
        if (!this.archivedusers.contains(userId)) return false; // So apaga se existir.
        this.archivedusers.delete(userId); // Apaga da tabela arquivada.
        return true; // Diz que correu bem.
    }

    /**
     * Tira um user do arquivo e devolve-o aos ativos.
     */
    public boolean restoreArchivedUser(String userId) {
        if (userId == null) return false; // Sem ID nao ha nada para restaurar.
        User u = this.archivedusers.get(userId); // Vai buscar o user arquivado.
        if (u == null) return false; // Se nao existir, falha.
        if (this.users.contains(userId)) return false; // Se ja estiver ativo, nao faz sentido restaurar.

        this.archivedusers.delete(userId); // Remove-o do arquivo.
        addUser(u); // Adiciona-o de novo aos ativos e aos indices.
        return true; // Diz que correu bem.
    }

    /**
     * Apaga todos os users arquivados.
     */
    public void clearArchivedUsers() {
        List<String> ids = new ArrayList<>(); // Cria uma lista temporaria de IDs.
        for (String id : this.archivedusers.keys()) ids.add(id); // Copia os IDs para evitar mexer na estrutura enquanto percorremos.
        for (String id : ids) this.archivedusers.delete(id); // Apaga um a um.
    }

    /**
     * Devolve todos os conteudos.
     */
    public List<Content> listContents() {
        List<Content> out = new ArrayList<>(); // Cria a lista de saida.
        for (String id : this.contents.keys()) { // Percorre todos os IDs de conteudos.
            Content c = this.contents.get(id); // Vai buscar o conteudo.
            if (c != null) out.add(c); // Se existir, junta-o ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Devolve todos os artistas.
     */
    public List<Artist> listArtists() {
        List<Artist> out = new ArrayList<>(); // Cria a lista de saida.
        for (String id : this.artists.keys()) { // Percorre todos os IDs de artistas.
            Artist a = this.artists.get(id); // Vai buscar o artista.
            if (a != null) out.add(a); // Se existir, junta-o ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Devolve todos os generos.
     */
    public List<Genre> listGenres() {
        List<Genre> out = new ArrayList<>(); // Cria a lista de saida.
        for (String id : this.genres.keys()) { // Percorre todos os IDs de generos.
            Genre g = this.genres.get(id); // Vai buscar o genero.
            if (g != null) out.add(g); // Se existir, junta-o ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Atualiza um user existente.
     */
    public boolean updateUser(User u) {
        if (u == null || u.getId() == null) return false; // Rejeita dados invalidos.
        if (!this.users.contains(u.getId())) return false; // So atualiza se o user ja existir.
        addUser(u); // Reusa a logica de insercao para manter os indices certos.
        return true; // Diz que correu bem.
    }

    /**
     * Atualiza um conteudo existente.
     */
    public boolean updateContent(Content c) {
        if (c == null || c.getId() == null) return false; // Rejeita dados invalidos.
        if (!this.contents.contains(c.getId())) return false; // So atualiza se o conteudo ja existir.
        addContent(c); // Reusa a logica de insercao para manter os indices certos.
        return true; // Diz que correu bem.
    }

    /**
     * Atualiza um artista existente.
     */
    public boolean updateArtist(Artist a) {
        if (a == null || a.getId() == null) return false; // Rejeita dados invalidos.
        if (!this.artists.contains(a.getId())) return false; // So atualiza se o artista ja existir.
        addArtist(a); // Reusa a logica de insercao para manter os indices certos.
        return true; // Diz que correu bem.
    }

    /**
     * Muda o nome de um genero e atualiza o indice dos conteudos por genero.
     */
    public boolean updateGenreName(String genreId, String newName) {
        if (genreId == null || newName == null) return false; // Rejeita valores invalidos.
        Genre g = this.genres.get(genreId); // Vai buscar o genero.
        if (g == null) return false; // Se nao existir, falha.

        String oldName = g.getName(); // Guarda o nome antigo.
        if (oldName == null) oldName = ""; // Evita null na pesquisa do indice.
        g.setName(newName); // Atualiza o nome no objeto genero.

        if (this.contentsByGenre.contains(oldName)) { // Se houver conteudos no nome antigo...
            List<Content> moved = this.contentsByGenre.get(oldName); // Vai buscar a lista antiga.
            this.contentsByGenre.delete(oldName); // Apaga a chave antiga.

            if (moved != null && !moved.isEmpty()) { // Se houver conteudos para mover...
                if (!this.contentsByGenre.contains(newName)) this.contentsByGenre.put(newName, new ArrayList<>()); // Cria a nova lista se faltar.
                this.contentsByGenre.get(newName).addAll(moved); // Move todos os conteudos para o novo nome.
            }
        }
        return true; // Diz que correu bem.
    }

    /**
     * Remove um user e pode arquiva-lo.
     */
    public boolean removeUser(String userId, boolean archive) {
        if (userId == null) return false; // Sem ID nao ha nada para remover.
        User u = this.users.get(userId); // Vai buscar o user.
        if (u == null) return false; // Se nao existir, falha.

        removeUserFromIndices(u); // Tira o user dos indices secundarios.
        this.users.delete(userId); // Tira o user da tabela principal.
        if (archive) this.archivedusers.put(userId, u); // Se for pedido, guarda-o no arquivo.
        return true; // Diz que correu bem.
    }

    /**
     * Remove um artista.
     */
    public boolean removeArtist(String artistId) {
        if (artistId == null) return false; // Sem ID nao ha nada para remover.
        Artist a = this.artists.get(artistId); // Vai buscar o artista.
        if (a == null) return false; // Se nao existir, falha.

        removeArtistFromIndices(a); // Tira o artista dos indices secundarios.
        this.artists.delete(artistId); // Tira o artista da tabela principal.
        return true; // Diz que correu bem.
    }

    /**
     * Remove um genero so se nenhum conteudo o estiver a usar.
     */
    public boolean removeGenre(String genreId) {
        if (genreId == null) return false; // Sem ID nao ha nada para remover.
        Genre g = this.genres.get(genreId); // Vai buscar o genero.
        if (g == null) return false; // Se nao existir, falha.

        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos.
            Content c = this.contents.get(contentId); // Vai buscar cada conteudo.
            if (c == null) continue; // Ignora entradas vazias.
            Genre cg = c.getGenre(); // Vai buscar o genero do conteudo.
            if (cg != null && genreId.equals(cg.getId())) return false; // Se algum conteudo usar este genero, nao remove.
        }

        this.genres.delete(genreId); // Se nao houver referencias, apaga o genero.
        return true; // Diz que correu bem.
    }

    /**
     * Remove um conteudo apenas se ele existir.
     */
    public boolean removeContentIfExists(String contentId) {
        if (contentId == null) return false; // Sem ID nao ha nada para remover.
        if (!this.contents.contains(contentId)) return false; // So remove se o conteudo existir.
        removeContent(contentId); // Chama a remocao completa.
        return true; // Diz que correu bem.
    }

    /**
     * Procura users pela regiao.
     */
    public List<User> searchUsersByRegion(String r) {
        if (r == null || !this.usersByRegion.contains(r)) { // Se a regiao nao existir no indice...
            return new ArrayList<>(); // Devolve lista vazia.
        }
        return this.usersByRegion.get(r); // Devolve a lista dessa regiao.
    }

    /**
     * Procura users entre duas datas de registo.
     */
    public List<User> searchUsersRegisteredBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return new ArrayList<>(); // Sem datas nao ha pesquisa.
        if (start.isAfter(end)) { // Se vierem trocadas...
            LocalDate tmp = start; // Guarda a primeira.
            start = end; // Troca a ordem.
            end = tmp; // Troca a ordem.
        }

        List<User> out = new ArrayList<>(); // Cria a lista de resultados.
        for (ChronoLocalDate d : this.usersByRegistrationDate.keys(start, end)) { // Percorre as datas dentro do intervalo.
            List<User> dayUsers = this.usersByRegistrationDate.get(d); // Vai buscar os users desse dia.
            if (dayUsers != null) out.addAll(dayUsers); // Junta todos os users ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Procura users pelo nome de utilizador dentro de um texto.
     */
    public List<User> searchUsersByUsernameSubstring(String substring) {
        if (substring == null) return new ArrayList<>(); // Sem texto nao ha pesquisa.
        String needle = substring.toLowerCase(Locale.ROOT); // Passa para minusculas para comparar sem diferencas de maiusculas.
        if (needle.isEmpty()) return new ArrayList<>(); // Se o texto ficar vazio, devolve vazio.

        List<User> out = new ArrayList<>(); // Cria a lista de resultados.
        for (String userId : this.users.keys()) { // Percorre todos os users.
            User u = this.users.get(userId); // Vai buscar o user.
            if (u == null) continue; // Ignora valores vazios.
            String username = u.getUsername(); // Vai buscar o nome de utilizador.
            if (username != null && username.toLowerCase(Locale.ROOT).contains(needle)) out.add(u); // Se o nome conter o texto, adiciona ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Procura conteudos pelo nome do genero.
     */
    public List<Content> searchByGenre(String genreName) {
        if (genreName == null || !this.contentsByGenre.contains(genreName)) { // Se nao houver esse genero...
            return new ArrayList<>(); // Devolve lista vazia.
        }
        return this.contentsByGenre.get(genreName); // Devolve os conteudos desse genero.
    }

    /**
     * Procura conteudos entre dois anos.
     */
    public List<Content> searchContentsReleasedBetween(int startYear, int endYear) {
        if (startYear > endYear) { // Se os anos vierem trocados...
            int tmp = startYear; // Guarda o primeiro.
            startYear = endYear; // Troca a ordem.
            endYear = tmp; // Troca a ordem.
        }

        List<Content> out = new ArrayList<>(); // Cria a lista de resultados.
        for (Integer year : this.contentsByReleaseYear.keys(startYear, endYear)) { // Percorre os anos dentro do intervalo.
            List<Content> yearContents = this.contentsByReleaseYear.get(year); // Vai buscar os conteudos desse ano.
            if (yearContents != null) out.addAll(yearContents); // Junta os conteudos ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Procura conteudos pelo titulo dentro de um texto.
     */
    public List<Content> searchContentsByTitleSubstring(String substring) {
        if (substring == null) return new ArrayList<>(); // Sem texto nao ha pesquisa.
        String needle = substring.toLowerCase(Locale.ROOT); // Passa para minusculas para comparar melhor.
        if (needle.isEmpty()) return new ArrayList<>(); // Se o texto ficar vazio, devolve vazio.

        List<Content> out = new ArrayList<>(); // Cria a lista de resultados.
        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos.
            Content c = this.contents.get(contentId); // Vai buscar o conteudo.
            if (c == null) continue; // Ignora valores vazios.
            String title = c.getTitle(); // Vai buscar o titulo.
            if (title != null && title.toLowerCase(Locale.ROOT).contains(needle)) out.add(c); // Se o titulo conter o texto, adiciona ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Procura conteudos por tipo.
     */
    public List<Content> searchContentsByType(String contentType) {
        if (contentType == null) return new ArrayList<>(); // Sem tipo nao ha pesquisa.
        String needle = contentType.toLowerCase(Locale.ROOT).trim(); // Normaliza o texto.
        if (needle.isEmpty()) return new ArrayList<>(); // Se ficar vazio, devolve vazio.

        List<Content> out = new ArrayList<>(); // Cria a lista de resultados.
        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos.
            Content c = this.contents.get(contentId); // Vai buscar o conteudo.
            if (c == null) continue; // Ignora valores vazios.
            String type = c.getContentType(); // Vai buscar o tipo.
            if (type != null && type.toLowerCase(Locale.ROOT).equals(needle)) { // Se o tipo bater certo...
                out.add(c); // Adiciona ao resultado.
            }
        }
        return out; // Devolve a lista final.
    }

    /**
     * Procura artistas pela nacionalidade.
     */
    public List<Artist> searchArtistsByNationality(String nationality) {
        if (nationality == null || !this.artistsByNationality.contains(nationality)) return new ArrayList<>(); // Se nao existir, devolve vazio.
        return this.artistsByNationality.get(nationality); // Devolve os artistas dessa nacionalidade.
    }

    /**
     * Procura artistas entre duas datas de nascimento.
     */
    public List<Artist> searchArtistsBornBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return new ArrayList<>(); // Sem datas nao ha pesquisa.
        if (start.isAfter(end)) { // Se vierem trocadas...
            LocalDate tmp = start; // Guarda a primeira.
            start = end; // Troca a ordem.
            end = tmp; // Troca a ordem.
        }

        List<Artist> out = new ArrayList<>(); // Cria a lista de resultados.
        for (ChronoLocalDate d : this.artistsByBirthDate.keys(start, end)) { // Percorre as datas no intervalo.
            List<Artist> dayArtists = this.artistsByBirthDate.get(d); // Vai buscar os artistas desse dia.
            if (dayArtists != null) out.addAll(dayArtists); // Junta todos ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Procura artistas pelo nome dentro de um texto.
     */
    public List<Artist> searchArtistsByNameSubstring(String substring) {
        if (substring == null) return new ArrayList<>(); // Sem texto nao ha pesquisa.
        String needle = substring.toLowerCase(Locale.ROOT); // Passa para minusculas para comparar melhor.
        if (needle.isEmpty()) return new ArrayList<>(); // Se o texto ficar vazio, devolve vazio.

        List<Artist> out = new ArrayList<>(); // Cria a lista de resultados.
        for (String artistId : this.artists.keys()) { // Percorre todos os artistas.
            Artist a = this.artists.get(artistId); // Vai buscar o artista.
            if (a == null) continue; // Ignora valores vazios.
            String name = a.getName(); // Vai buscar o nome.
            if (name != null && name.toLowerCase(Locale.ROOT).contains(needle)) out.add(a); // Se o nome conter o texto, adiciona ao resultado.
        }
        return out; // Devolve a lista final.
    }

    /**
     * Devolve os anos que tiveram conteudos dentro do intervalo pedido.
     */
    public Iterable<Integer> getYearsWithContent(int startYear, int endYear) {
        if (startYear > endYear) { // Se os anos vierem trocados...
            int tmp = startYear; // Guarda o primeiro.
            startYear = endYear; // Troca a ordem.
            endYear = tmp; // Troca a ordem.
        }
        return this.contentsByReleaseYear.keys(startYear, endYear); // Devolve os anos dentro do intervalo.
    }

    /**
     * Remove um conteudo da base de dados e dos indices.
     */
    public void removeContent(String contentId) {
        if (contentId == null) return; // Sem ID nao ha nada para remover.
        Content c = this.contents.get(contentId); // Vai buscar o conteudo.
        if (c == null) return; // Se nao existir, nao faz nada.
        removeContentFromIndices(c); // Tira o conteudo dos indices.
        this.contents.delete(contentId); // Apaga o conteudo da tabela principal.
    }

    /**
     * Confirma se os indices continuam alinhados com os dados principais.
     */
    public boolean validateConsistency() {
        for (String contentId : this.contents.keys()) { // Percorre todos os conteudos guardados.
            Content c = this.contents.get(contentId); // Vai buscar o conteudo.
            if (c == null) return false; // Se houver um vazio, algo esta errado.
            int year = c.getReleaseYear(); // Lê o ano.
            if (!this.contentsByReleaseYear.contains(year)) return false; // O ano tem de existir no indice.
            if (!containsContentId(this.contentsByReleaseYear.get(year), contentId)) return false; // O conteudo tem de estar na lista desse ano.
        }
        for (String userId : this.users.keys()) { // Percorre todos os users guardados.
            User u = this.users.get(userId); // Vai buscar o user.
            if (u == null) return false; // Se houver um vazio, algo esta errado.
            String region = u.getRegion(); // Lê a regiao.
            if (region != null && !this.usersByRegion.contains(region)) return false; // A regiao tem de existir no indice.
        }
        return true; // Se chegou aqui, a consistencia esta OK.
    }

    /**
     * Vê se uma lista de conteudos tem um ID especifico.
     */
    private static boolean containsContentId(List<Content> list, String contentId) {
        if (list == null || contentId == null) return false; // Protecao contra null.
        for (Content c : list) if (c != null && contentId.equals(c.getId())) return true; // Se encontrar o ID, devolve true.
        return false; // Se nao encontrar, devolve false.
    }

    /**
     * Remove um user de uma lista pelo ID.
     */
    private void removeUserById(List<User> list, String userId) {
        if (list == null || userId == null) return; // Protecao contra null.
        list.removeIf(u -> u.getId().equals(userId)); // Remove o user com esse ID.
    }
}
