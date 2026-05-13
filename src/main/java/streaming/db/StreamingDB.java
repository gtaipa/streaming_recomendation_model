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
 * Base de dados em memoria para a plataforma de streaming.
 * <p>
 * Estruturas principais:
 * <ul>
 *   <li>{@code SeparateChainingHashST} para acesso por {@code id} (O(1) medio).</li>
 *   <li>{@code RedBlackBST} como indices ordenados para pesquisa por chaves (O(log n)).</li>
 * </ul>
 * Para suportar updates sem varrer indices, guarda-se um "snapshot" das chaves usadas na indexacao
 * (ex.: regiao, data, genero, titulo) e faz-se deindexacao + reindexacao em updates.
 */
public class StreamingDB {

    // Armazenamento Principal (R2)
    private SeparateChainingHashST<String, User> users;
    private SeparateChainingHashST<String, User> archivedusers;
    private SeparateChainingHashST<String, Content> contents;
    private SeparateChainingHashST<String, Artist> artists;
    private SeparateChainingHashST<String, Genre> genres;

    // Snapshots das chaves de indexação (evita varrer BSTs quando há updates in-place)
    private SeparateChainingHashST<String, UserIndexKeys> userIndexKeys;
    private SeparateChainingHashST<String, ArtistIndexKeys> artistIndexKeys;
    private SeparateChainingHashST<String, ContentIndexKeys> contentIndexKeys;

    // Índices Ordenados (R3) - Usamos List para permitir múltiplas entidades por
    // chave
    private RedBlackBST<String, List<User>> usersByRegion;
    private RedBlackBST<ChronoLocalDate, List<User>> usersByRegistrationDate;
    private RedBlackBST<String, List<Content>> contentsByGenre;
    private RedBlackBST<String, List<Content>> contentsByTitle;
    private RedBlackBST<String, List<Content>> contentsByRegion;
    private RedBlackBST<String, List<Artist>> artistsByNationality;
    private RedBlackBST<ChronoLocalDate, List<Artist>> artistsByBirthDate;
    private RedBlackBST<Integer, List<Content>> contentsByReleaseYear;
    private RedBlackBST<Double, List<Content>> contentsByRating;
    private RedBlackBST<Integer, List<Content>> contentsByViews;
    private RedBlackBST<Integer, List<Content>> contentsByDuration;

    private static final class UserIndexKeys {
        final String region;
        final ChronoLocalDate registrationDate;

        private UserIndexKeys(String region, ChronoLocalDate registrationDate) {
            this.region = region;
            this.registrationDate = registrationDate;
        }
    }

    private static final class ArtistIndexKeys {
        final String nationality;
        final ChronoLocalDate birthDate;

        private ArtistIndexKeys(String nationality, ChronoLocalDate birthDate) {
            this.nationality = nationality;
            this.birthDate = birthDate;
        }
    }

    private static final class ContentIndexKeys {
        final int releaseYear;
        String genreName;
        final String title;
        final String region;

        private ContentIndexKeys(int releaseYear, String genreName, String title, String region) {
            this.releaseYear = releaseYear;
            this.genreName = genreName;
            this.title = title;
            this.region = region;
        }
    }

    /**
     * Cria uma DB vazia com todas as estruturas e indices inicializados.
     */
    public StreamingDB() {
        this.users = new SeparateChainingHashST<>();
        this.contents = new SeparateChainingHashST<>();
        this.artists = new SeparateChainingHashST<>();
        this.genres = new SeparateChainingHashST<>();
        this.archivedusers = new SeparateChainingHashST<>();

        this.userIndexKeys = new SeparateChainingHashST<>();
        this.artistIndexKeys = new SeparateChainingHashST<>();
        this.contentIndexKeys = new SeparateChainingHashST<>();

        this.usersByRegion = new RedBlackBST<>();
        this.usersByRegistrationDate = new RedBlackBST<>();
        this.contentsByGenre = new RedBlackBST<>();
        this.contentsByTitle = new RedBlackBST<>();
        this.contentsByRegion = new RedBlackBST<>();
        this.artistsByNationality = new RedBlackBST<>();
        this.artistsByBirthDate = new RedBlackBST<>();
        this.contentsByReleaseYear = new RedBlackBST<>();
        this.contentsByRating = new RedBlackBST<>();
        this.contentsByViews = new RedBlackBST<>();
        this.contentsByDuration = new RedBlackBST<>();
    }

    /**
     * Adiciona um utilizador a DB (ou substitui o existente com o mesmo {@code id}).
     * <p>
     * Complexidade: O(1) medio para hash + O(log n) para atualizar indices.
     *
     * @param u utilizador a adicionar
     */
    public void addUser(User u) {
        if (u == null || u.getId() == null) return;

        if (this.users.contains(u.getId())) {

            String id = u.getId();
            UserIndexKeys prevKeys = this.userIndexKeys.get(id);
            if (prevKeys != null) {
                deindexUserByKeys(id, prevKeys);
            } else {
                // Fallback for inconsistent state.
                purgeUserFromIndices(id);
            }
            this.userIndexKeys.delete(id);
        }
        this.users.put(u.getId(), u);
        indexUser(u);
    }

    /**
     * Adiciona um conteudo a DB (ou substitui o existente com o mesmo {@code id}).
     * <p>
     * Complexidade: O(1) medio para hash + O(log n) para atualizar indices.
     *
     * @param c conteudo a adicionar
     */
    public void addContent(Content c) {
        if (c == null || c.getId() == null) return;

        if (this.contents.contains(c.getId())) {
            String id = c.getId();
            ContentIndexKeys prevKeys = this.contentIndexKeys.get(id);
            if (prevKeys != null) {
                deindexContentByKeys(id, prevKeys);
            } else {
                purgeContentFromIndices(id);
            }
            this.contentIndexKeys.delete(id);
        }
        this.contents.put(c.getId(), c);
        indexContent(c);
    }

    /**
     * Adiciona um artista a DB (ou substitui o existente com o mesmo {@code id}).
     *
     * @param a artista a adicionar
     */
    public void addArtist(Artist a) {
        if (a == null || a.getId() == null) return;

        if (this.artists.contains(a.getId())) {
            String id = a.getId();
            ArtistIndexKeys prevKeys = this.artistIndexKeys.get(id);
            if (prevKeys != null) {
                deindexArtistByKeys(id, prevKeys);
            } else {
                purgeArtistFromIndices(id);
            }
            this.artistIndexKeys.delete(id);
        }
        this.artists.put(a.getId(), a);
        indexArtist(a);
    }

    /**
     * Adiciona um genero a DB (ou substitui o existente com o mesmo {@code id}).
     *
     * @param g genero a adicionar
     */
    public void addGenre(Genre g) {
        if (g == null || g.getId() == null) return;
        this.genres.put(g.getId(), g);
    }

    // ---------------------------
    // GET / LIST (R2)
    // ---------------------------

    /**
     * Procura um utilizador pelo {@code id}.
     *
     * @param userId id do utilizador
     * @return utilizador ou {@code null} se nao existir
     */
    public User getUser(String userId) {
        if (userId == null) return null;
        return this.users.get(userId);
    }

    /**
     * Procura um conteudo pelo {@code id}.
     *
     * @param contentId id do conteudo
     * @return conteudo ou {@code null} se nao existir
     */
    public Content getContent(String contentId) {
        if (contentId == null) return null;
        return this.contents.get(contentId);
    }

    /**
     * Procura um artista pelo {@code id}.
     *
     * @param artistId id do artista
     * @return artista ou {@code null} se nao existir
     */
    public Artist getArtist(String artistId) {
        if (artistId == null) return null;
        return this.artists.get(artistId);
    }

    /**
     * Procura um genero pelo {@code id}.
     *
     * @param genreId id do genero
     * @return genero ou {@code null} se nao existir
     */
    public Genre getGenre(String genreId) {
        if (genreId == null) return null;
        return this.genres.get(genreId);
    }

    public User getArchivedUser(String userId) {
        if (userId == null) return null;
        return this.archivedusers.get(userId);
    }

    public List<User> listUsers() {
        List<User> out = new ArrayList<>();
        for (String id : this.users.keys()) {
            User u = this.users.get(id);
            if (u != null) out.add(u);
        }
        return out;
    }

    public List<User> listArchivedUsers() {
        List<User> out = new ArrayList<>();
        for (String id : this.archivedusers.keys()) {
            User u = this.archivedusers.get(id);
            if (u != null) out.add(u);
        }
        return out;
    }

    public boolean updateArchivedUser(User u) {
        if (u == null || u.getId() == null) return false;
        if (!this.archivedusers.contains(u.getId())) return false;
        this.archivedusers.put(u.getId(), u);
        return true;
    }

    public boolean removeArchivedUser(String userId) {
        if (userId == null) return false;
        if (!this.archivedusers.contains(userId)) return false;
        this.archivedusers.delete(userId);
        return true;
    }

    public boolean restoreArchivedUser(String userId) {
        if (userId == null) return false;
        User u = this.archivedusers.get(userId);
        if (u == null) return false;
        if (this.users.contains(userId)) return false;

        this.archivedusers.delete(userId);
        addUser(u);
        return true;
    }

    public void clearArchivedUsers() {
        List<String> ids = new ArrayList<>();
        for (String id : this.archivedusers.keys()) ids.add(id);
        for (String id : ids) this.archivedusers.delete(id);
    }

    public List<Content> listContents() {
        List<Content> out = new ArrayList<>();
        for (String id : this.contents.keys()) {
            Content c = this.contents.get(id);
            if (c != null) out.add(c);
        }
        return out;
    }

    public List<Artist> listArtists() {
        List<Artist> out = new ArrayList<>();
        for (String id : this.artists.keys()) {
            Artist a = this.artists.get(id);
            if (a != null) out.add(a);
        }
        return out;
    }

    public List<Genre> listGenres() {
        List<Genre> out = new ArrayList<>();
        for (String id : this.genres.keys()) {
            Genre g = this.genres.get(id);
            if (g != null) out.add(g);
        }
        return out;
    }

    // ---------------------------
    // UPDATE (R2)
    // ---------------------------

    public boolean updateUser(User u) {
        if (u == null || u.getId() == null) return false;
        if (!this.users.contains(u.getId())) return false;
        addUser(u); // reindexes as needed
        return true;
    }

    public boolean updateContent(Content c) {
        if (c == null || c.getId() == null) return false;
        if (!this.contents.contains(c.getId())) return false;
        addContent(c); // reindexes as needed
        return true;
    }

    public boolean updateArtist(Artist a) {
        if (a == null || a.getId() == null) return false;
        if (!this.artists.contains(a.getId())) return false;
        addArtist(a); // reindexes as needed
        return true;
    }

    public boolean updateGenreName(String genreId, String newName) {
        if (genreId == null || newName == null) return false;
        Genre g = this.genres.get(genreId);
        if (g == null) return false;

        String oldName = g.getName();
        if (oldName == null) oldName = "";
        g.setName(newName);

        // Keep contentsByGenre consistent: the key is the genre name (string).
        if (this.contentsByGenre.contains(oldName)) {
            List<Content> moved = this.contentsByGenre.get(oldName);
            this.contentsByGenre.delete(oldName);

            if (moved != null && !moved.isEmpty()) {
                if (!this.contentsByGenre.contains(newName)) this.contentsByGenre.put(newName, new ArrayList<>());
                this.contentsByGenre.get(newName).addAll(moved);

                // Update snapshot keys so future updates/removals remain O(1).
                for (Content c : moved) {
                    if (c == null || c.getId() == null) continue;
                    ContentIndexKeys keys = this.contentIndexKeys.get(c.getId());
                    if (keys != null) keys.genreName = newName;
                }
            }
        }
        return true;
    }

    // ---------------------------
    // REMOVE (R2) + CONSISTENCY (R4)
    // ---------------------------

    public boolean removeUser(String userId, boolean archive) {
        if (userId == null) return false;
        User u = this.users.get(userId);
        if (u == null) return false;

        this.users.delete(userId);
        UserIndexKeys keys = this.userIndexKeys.get(userId);
        if (keys != null) deindexUserByKeys(userId, keys);
        else purgeUserFromIndices(userId);
        this.userIndexKeys.delete(userId);
        if (archive) this.archivedusers.put(userId, u);
        return true;
    }

    public boolean removeArtist(String artistId) {
        if (artistId == null) return false;
        Artist a = this.artists.get(artistId);
        if (a == null) return false;

        this.artists.delete(artistId);
        ArtistIndexKeys keys = this.artistIndexKeys.get(artistId);
        if (keys != null) deindexArtistByKeys(artistId, keys);
        else purgeArtistFromIndices(artistId);
        this.artistIndexKeys.delete(artistId);
        return true;
    }

    public boolean removeGenre(String genreId) {
        if (genreId == null) return false;
        Genre g = this.genres.get(genreId);
        if (g == null) return false;

        // Conservative approach: do not remove genres that are still referenced by contents.
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) continue;
            Genre cg = c.getGenre();
            if (cg != null && genreId.equals(cg.getId())) return false;
        }

        this.genres.delete(genreId);
        return true;
    }

    public boolean removeContentIfExists(String contentId) {
        if (contentId == null) return false;
        if (!this.contents.contains(contentId)) return false;
        removeContent(contentId);
        return true;
    }

    // PESQUISAS OTIMIZADAS (Usando as árvores do R3 em vez de percorrer tudo)
    /**
     * Pesquisa utilizadores por regiao (chave exata).
     * <p>
     * Complexidade: O(log n) para localizar a chave na BST + O(k) para devolver a lista.
     *
     * @param r regiao (ex.: "PT")
     * @return lista (possivelmente vazia) de utilizadores dessa regiao
     */
    public List<User> searchUsersByRegion(String r) {
        // Verifica se a região existe na árvore
        if (r == null || !this.usersByRegion.contains(r)) {
            // Se não existir, retorna uma lista vazia
            return new ArrayList<>();
        }
        // Se existir, retorna a lista de usuários da região
        return this.usersByRegion.get(r);
    }

    /**
     * Pesquisa utilizadores cuja data de registo esta entre {@code start} e {@code end} (inclusive).
     *
     * @param start data inicial
     * @param end data final
     * @return lista (possivelmente vazia) de utilizadores no intervalo
     */
    public List<User> searchUsersRegisteredBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return new ArrayList<>();
        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        List<User> out = new ArrayList<>();
        for (ChronoLocalDate d : this.usersByRegistrationDate.keys(start, end)) {
            List<User> dayUsers = this.usersByRegistrationDate.get(d);
            if (dayUsers != null) out.addAll(dayUsers);
        }
        return out;
    }

    public List<User> searchUsersByUsernameSubstring(String substring) {
        if (substring == null) return new ArrayList<>();
        String needle = substring.toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return new ArrayList<>();

        List<User> out = new ArrayList<>();
        for (String userId : this.users.keys()) {
            User u = this.users.get(userId);
            if (u == null) continue;
            String username = u.getUsername();
            if (username != null && username.toLowerCase(Locale.ROOT).contains(needle)) out.add(u);
        }
        return out;
    }

    /**
     * Pesquisa conteudos por nome de genero (chave exata).
     * <p>
     * Complexidade: O(log n) para localizar a chave na BST + O(k) para devolver a lista.
     *
     * @param genreName nome do genero
     * @return lista (possivelmente vazia) de conteudos desse genero
     */
    public List<Content> searchByGenre(String genreName) {
        // verifica se existe esse genero
        if (genreName == null || !this.contentsByGenre.contains(genreName)) {
            // se não existir retorna uma lista vazia
            return new ArrayList<>();
        }
        // se existir retorna a lista de conteúdos desse genero
        return this.contentsByGenre.get(genreName);
    }

    public List<Content> searchContentsReleasedBetween(int startYear, int endYear) {
        if (startYear > endYear) {
            int tmp = startYear;
            startYear = endYear;
            endYear = tmp;
        }

        List<Content> out = new ArrayList<>();
        for (Integer year : this.contentsByReleaseYear.keys(startYear, endYear)) {
            List<Content> yearContents = this.contentsByReleaseYear.get(year);
            if (yearContents != null) out.addAll(yearContents);
        }
        return out;
    }

    public List<Content> searchContentsByTitleSubstring(String substring) {
        if (substring == null) return new ArrayList<>();
        String needle = substring.toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return new ArrayList<>();

        List<Content> out = new ArrayList<>();
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) continue;
            String title = c.getTitle();
            if (title != null && title.toLowerCase(Locale.ROOT).contains(needle)) out.add(c);
        }
        return out;
    }

    public List<Content> searchContentsByType(String contentType) {
        if (contentType == null) return new ArrayList<>();
        String needle = contentType.toLowerCase(Locale.ROOT).trim();
        if (needle.isEmpty()) return new ArrayList<>();

        List<Content> out = new ArrayList<>();
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) continue;

            String type = c.getContentType();
            if (type != null && type.toLowerCase(Locale.ROOT).equals(needle)) {
                out.add(c);
            }
        }
        return out;
    }

    public List<Artist> searchArtistsByNationality(String nationality) {
        if (nationality == null || !this.artistsByNationality.contains(nationality)) return new ArrayList<>();
        return this.artistsByNationality.get(nationality);
    }

    public List<Artist> searchArtistsBornBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return new ArrayList<>();
        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        List<Artist> out = new ArrayList<>();
        for (ChronoLocalDate d : this.artistsByBirthDate.keys(start, end)) {
            List<Artist> dayArtists = this.artistsByBirthDate.get(d);
            if (dayArtists != null) out.addAll(dayArtists);
        }
        return out;
    }

    public List<Artist> searchArtistsByNameSubstring(String substring) {
        if (substring == null) return new ArrayList<>();
        String needle = substring.toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return new ArrayList<>();

        List<Artist> out = new ArrayList<>();
        for (String artistId : this.artists.keys()) {
            Artist a = this.artists.get(artistId);
            if (a == null) continue;
            String name = a.getName();
            if (name != null && name.toLowerCase(Locale.ROOT).contains(needle)) out.add(a);
        }
        return out;
    }

    // Exemplo de Pesquisa por Intervalo (Exigência do R3)
    public Iterable<Integer> getYearsWithContent(int startYear, int endYear) {
        if (startYear > endYear) {
            int tmp = startYear;
            startYear = endYear;
            endYear = tmp;
        }
        return this.contentsByReleaseYear.keys(startYear, endYear);
    }

    public void removeContent(String contentId) {
        if (contentId == null) return;

        Content c = this.contents.get(contentId);
        if (c == null) return;

        this.contents.delete(contentId);
        ContentIndexKeys keys = this.contentIndexKeys.get(contentId);
        if (keys != null) deindexContentByKeys(contentId, keys);
        else purgeContentFromIndices(contentId);
        this.contentIndexKeys.delete(contentId);
    }

    /**
     * Verifica consistencia interna: referencia a followings, conteudos e artistas existentes na DB.
     * <p>
     * Complexidade: O(U + C + A) na ordem de grandeza (varre estruturas principais e valida ligacoes).
     *
     * @return {@code true} se a DB estiver consistente; {@code false} caso contrario
     */
    public boolean validateConsistency() {
        // Contents: every content in the main ST must be reachable from the indices.
        for (String contentId : this.contents.keys()) {
            Content c = this.contents.get(contentId);
            if (c == null) return false;

            int year = c.getReleaseYear();
            if (!this.contentsByReleaseYear.contains(year)) return false;
            if (!containsContentId(this.contentsByReleaseYear.get(year), contentId)) return false;

            Genre g = c.getGenre();
            String genreName = (g == null) ? null : g.getName();
            if (genreName != null) {
                if (!this.contentsByGenre.contains(genreName)) return false;
                if (!containsContentId(this.contentsByGenre.get(genreName), contentId)) return false;
            }

            String title = c.getTitle();
            if (title != null) {
                if (!this.contentsByTitle.contains(title)) return false;
                if (!containsContentId(this.contentsByTitle.get(title), contentId)) return false;
            }

            String region = c.getRegion();
            if (region != null) {
                if (!this.contentsByRegion.contains(region)) return false;
                if (!containsContentId(this.contentsByRegion.get(region), contentId)) return false;
            }
        }

        // Contents: every content referenced in the indices must exist in the main ST.
        for (Integer year : this.contentsByReleaseYear.keys()) {
            List<Content> list = this.contentsByReleaseYear.get(year);
            if (list == null) return false;
            for (Content c : list) {
                if (c == null || !this.contents.contains(c.getId())) return false;
            }
        }

        for (String genreName : this.contentsByGenre.keys()) {
            List<Content> list = this.contentsByGenre.get(genreName);
            if (list == null) return false;
            for (Content c : list) {
                if (c == null || !this.contents.contains(c.getId())) return false;
            }
        }

        for (String title : this.contentsByTitle.keys()) {
            List<Content> list = this.contentsByTitle.get(title);
            if (list == null) return false;
            for (Content c : list) {
                if (c == null || !this.contents.contains(c.getId())) return false;
            }
        }

        for (String region : this.contentsByRegion.keys()) {
            List<Content> list = this.contentsByRegion.get(region);
            if (list == null) return false;
            for (Content c : list) {
                if (c == null || !this.contents.contains(c.getId())) return false;
            }
        }

        // Users: main ST -> indices.
        for (String userId : this.users.keys()) {
            User u = this.users.get(userId);
            if (u == null) return false;
            String region = u.getRegion();
            if (region != null) {
                if (!this.usersByRegion.contains(region)) return false;
                if (!containsUserId(this.usersByRegion.get(region), userId)) return false;
            }
            LocalDate reg = u.getRegistrationDate();
            if (reg != null) {
                if (!this.usersByRegistrationDate.contains(reg)) return false;
                if (!containsUserId(this.usersByRegistrationDate.get(reg), userId)) return false;
            }
        }

        // Users: indices -> main ST.
        for (String region : this.usersByRegion.keys()) {
            List<User> list = this.usersByRegion.get(region);
            if (list == null) return false;
            for (User u : list) {
                if (u == null || !this.users.contains(u.getId())) return false;
            }
        }
        for (ChronoLocalDate reg : this.usersByRegistrationDate.keys()) {
            List<User> list = this.usersByRegistrationDate.get(reg);
            if (list == null) return false;
            for (User u : list) {
                if (u == null || !this.users.contains(u.getId())) return false;
            }
        }

        // Artists: main ST -> indices.
        for (String artistId : this.artists.keys()) {
            Artist a = this.artists.get(artistId);
            if (a == null) return false;
            String nat = a.getNationality();
            if (nat != null) {
                if (!this.artistsByNationality.contains(nat)) return false;
                if (!containsArtistId(this.artistsByNationality.get(nat), artistId)) return false;
            }
            LocalDate birth = a.getBirthDate();
            if (birth != null) {
                if (!this.artistsByBirthDate.contains(birth)) return false;
                if (!containsArtistId(this.artistsByBirthDate.get(birth), artistId)) return false;
            }
        }

        // Artists: indices -> main ST.
        for (String nat : this.artistsByNationality.keys()) {
            List<Artist> list = this.artistsByNationality.get(nat);
            if (list == null) return false;
            for (Artist a : list) {
                if (a == null || !this.artists.contains(a.getId())) return false;
            }
        }
        for (ChronoLocalDate birth : this.artistsByBirthDate.keys()) {
            List<Artist> list = this.artistsByBirthDate.get(birth);
            if (list == null) return false;
            for (Artist a : list) {
                if (a == null || !this.artists.contains(a.getId())) return false;
            }
        }

        return true;
    }

    private void purgeUserFromIndices(String userId) {
        if (userId == null) return;

        List<String> regions = new ArrayList<>();
        for (String k : this.usersByRegion.keys()) regions.add(k);
        for (String region : regions) {
            List<User> list = this.usersByRegion.get(region);
            removeUserById(list, userId);
            if (list != null && list.isEmpty()) this.usersByRegion.delete(region);
        }

        List<ChronoLocalDate> dates = new ArrayList<>();
        for (ChronoLocalDate k : this.usersByRegistrationDate.keys()) dates.add(k);
        for (ChronoLocalDate d : dates) {
            List<User> list = this.usersByRegistrationDate.get(d);
            removeUserById(list, userId);
            if (list != null && list.isEmpty()) this.usersByRegistrationDate.delete(d);
        }
    }

    private void purgeArtistFromIndices(String artistId) {
        if (artistId == null) return;

        List<String> nats = new ArrayList<>();
        for (String k : this.artistsByNationality.keys()) nats.add(k);
        for (String nat : nats) {
            List<Artist> list = this.artistsByNationality.get(nat);
            removeArtistById(list, artistId);
            if (list != null && list.isEmpty()) this.artistsByNationality.delete(nat);
        }

        List<ChronoLocalDate> births = new ArrayList<>();
        for (ChronoLocalDate k : this.artistsByBirthDate.keys()) births.add(k);
        for (ChronoLocalDate d : births) {
            List<Artist> list = this.artistsByBirthDate.get(d);
            removeArtistById(list, artistId);
            if (list != null && list.isEmpty()) this.artistsByBirthDate.delete(d);
        }
    }

    private void purgeContentFromIndices(String contentId) {
        if (contentId == null) return;

        List<Integer> years = new ArrayList<>();
        for (Integer k : this.contentsByReleaseYear.keys()) years.add(k);
        for (Integer y : years) {
            List<Content> list = this.contentsByReleaseYear.get(y);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByReleaseYear.delete(y);
        }

        List<String> genres = new ArrayList<>();
        for (String k : this.contentsByGenre.keys()) genres.add(k);
        for (String g : genres) {
            List<Content> list = this.contentsByGenre.get(g);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByGenre.delete(g);
        }

        List<String> titles = new ArrayList<>();
        for (String k : this.contentsByTitle.keys()) titles.add(k);
        for (String t : titles) {
            List<Content> list = this.contentsByTitle.get(t);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByTitle.delete(t);
        }

        List<String> regions = new ArrayList<>();
        for (String k : this.contentsByRegion.keys()) regions.add(k);
        for (String r : regions) {
            List<Content> list = this.contentsByRegion.get(r);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByRegion.delete(r);
        }
    }

    private void indexUser(User u) {
        if (u == null || u.getId() == null) return;

        String region = u.getRegion();
        if (region != null) {
            if (!this.usersByRegion.contains(region)) this.usersByRegion.put(region, new ArrayList<>());
            List<User> list = this.usersByRegion.get(region);
            if (!containsUserId(list, u.getId())) list.add(u);
        }

        LocalDate registrationDate = u.getRegistrationDate();
        if (registrationDate != null) {
            if (!this.usersByRegistrationDate.contains(registrationDate))
                this.usersByRegistrationDate.put(registrationDate, new ArrayList<>());
            List<User> list = this.usersByRegistrationDate.get(registrationDate);
            if (!containsUserId(list, u.getId())) list.add(u);
        }

        this.userIndexKeys.put(u.getId(), new UserIndexKeys(region, registrationDate));
    }

    private void deindexUser(User u) {
        if (u == null || u.getId() == null) return;
        String userId = u.getId();

        String region = u.getRegion();
        if (region != null && this.usersByRegion.contains(region)) {
            List<User> list = this.usersByRegion.get(region);
            removeUserById(list, userId);
            if (list != null && list.isEmpty()) this.usersByRegion.delete(region);
        }

        LocalDate reg = u.getRegistrationDate();
        if (reg != null && this.usersByRegistrationDate.contains(reg)) {
            List<User> list = this.usersByRegistrationDate.get(reg);
            removeUserById(list, userId);
            if (list != null && list.isEmpty()) this.usersByRegistrationDate.delete(reg);
        }
    }

    private void indexArtist(Artist a) {
        if (a == null || a.getId() == null) return;

        String nationality = a.getNationality();
        if (nationality != null) {
            if (!this.artistsByNationality.contains(nationality))
                this.artistsByNationality.put(nationality, new ArrayList<>());
            List<Artist> list = this.artistsByNationality.get(nationality);
            if (!containsArtistId(list, a.getId())) list.add(a);
        }

        LocalDate birthDate = a.getBirthDate();
        if (birthDate != null) {
            if (!this.artistsByBirthDate.contains(birthDate))
                this.artistsByBirthDate.put(birthDate, new ArrayList<>());
            List<Artist> list = this.artistsByBirthDate.get(birthDate);
            if (!containsArtistId(list, a.getId())) list.add(a);
        }

        this.artistIndexKeys.put(a.getId(), new ArtistIndexKeys(nationality, birthDate));
    }

    private void deindexArtist(Artist a) {
        if (a == null || a.getId() == null) return;
        String artistId = a.getId();

        String nationality = a.getNationality();
        if (nationality != null && this.artistsByNationality.contains(nationality)) {
            List<Artist> list = this.artistsByNationality.get(nationality);
            removeArtistById(list, artistId);
            if (list != null && list.isEmpty()) this.artistsByNationality.delete(nationality);
        }

        LocalDate birthDate = a.getBirthDate();
        if (birthDate != null && this.artistsByBirthDate.contains(birthDate)) {
            List<Artist> list = this.artistsByBirthDate.get(birthDate);
            removeArtistById(list, artistId);
            if (list != null && list.isEmpty()) this.artistsByBirthDate.delete(birthDate);
        }
    }

    private void indexContent(Content c) {
        if (c == null || c.getId() == null) return;

        int year = c.getReleaseYear();
        if (!this.contentsByReleaseYear.contains(year))
            this.contentsByReleaseYear.put(year, new ArrayList<>());
        List<Content> yearList = this.contentsByReleaseYear.get(year);
        if (!containsContentId(yearList, c.getId())) yearList.add(c);

        Genre g = c.getGenre();
        String genreName = (g == null) ? null : g.getName();
        if (genreName != null) {
            if (!this.contentsByGenre.contains(genreName)) this.contentsByGenre.put(genreName, new ArrayList<>());
            List<Content> list = this.contentsByGenre.get(genreName);
            if (!containsContentId(list, c.getId())) list.add(c);
        }

        String title = c.getTitle();
        if (title != null) {
            if (!this.contentsByTitle.contains(title)) this.contentsByTitle.put(title, new ArrayList<>());
            List<Content> list = this.contentsByTitle.get(title);
            if (!containsContentId(list, c.getId())) list.add(c);
        }

        String region = c.getRegion();
        if (region != null) {
            if (!this.contentsByRegion.contains(region)) this.contentsByRegion.put(region, new ArrayList<>());
            List<Content> list = this.contentsByRegion.get(region);
            if (!containsContentId(list, c.getId())) list.add(c);
        }

        // (rating/views/duration indices can be added later if/when required)
        this.contentIndexKeys.put(
                c.getId(),
                new ContentIndexKeys(c.getReleaseYear(), genreName, title, region)
        );
    }

    private void deindexUserByKeys(String userId, UserIndexKeys keys) {
        if (userId == null || keys == null) return;

        if (keys.region != null && this.usersByRegion.contains(keys.region)) {
            List<User> list = this.usersByRegion.get(keys.region);
            removeUserById(list, userId);
            if (list != null && list.isEmpty()) this.usersByRegion.delete(keys.region);
        }

        if (keys.registrationDate != null && this.usersByRegistrationDate.contains(keys.registrationDate)) {
            List<User> list = this.usersByRegistrationDate.get(keys.registrationDate);
            removeUserById(list, userId);
            if (list != null && list.isEmpty()) this.usersByRegistrationDate.delete(keys.registrationDate);
        }
    }

    private void deindexArtistByKeys(String artistId, ArtistIndexKeys keys) {
        if (artistId == null || keys == null) return;

        if (keys.nationality != null && this.artistsByNationality.contains(keys.nationality)) {
            List<Artist> list = this.artistsByNationality.get(keys.nationality);
            removeArtistById(list, artistId);
            if (list != null && list.isEmpty()) this.artistsByNationality.delete(keys.nationality);
        }

        if (keys.birthDate != null && this.artistsByBirthDate.contains(keys.birthDate)) {
            List<Artist> list = this.artistsByBirthDate.get(keys.birthDate);
            removeArtistById(list, artistId);
            if (list != null && list.isEmpty()) this.artistsByBirthDate.delete(keys.birthDate);
        }
    }

    private void deindexContentByKeys(String contentId, ContentIndexKeys keys) {
        if (contentId == null || keys == null) return;

        if (this.contentsByReleaseYear.contains(keys.releaseYear)) {
            List<Content> list = this.contentsByReleaseYear.get(keys.releaseYear);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByReleaseYear.delete(keys.releaseYear);
        }

        if (keys.genreName != null && this.contentsByGenre.contains(keys.genreName)) {
            List<Content> list = this.contentsByGenre.get(keys.genreName);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByGenre.delete(keys.genreName);
        }

        if (keys.title != null && this.contentsByTitle.contains(keys.title)) {
            List<Content> list = this.contentsByTitle.get(keys.title);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByTitle.delete(keys.title);
        }

        if (keys.region != null && this.contentsByRegion.contains(keys.region)) {
            List<Content> list = this.contentsByRegion.get(keys.region);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByRegion.delete(keys.region);
        }
    }

    private void deindexContent(Content c) {
        if (c == null || c.getId() == null) return;
        String contentId = c.getId();

        int year = c.getReleaseYear();
        if (this.contentsByReleaseYear.contains(year)) {
            List<Content> list = this.contentsByReleaseYear.get(year);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByReleaseYear.delete(year);
        }

        Genre g = c.getGenre();
        String genreName = (g == null) ? null : g.getName();
        if (genreName != null && this.contentsByGenre.contains(genreName)) {
            List<Content> list = this.contentsByGenre.get(genreName);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByGenre.delete(genreName);
        }

        String title = c.getTitle();
        if (title != null && this.contentsByTitle.contains(title)) {
            List<Content> list = this.contentsByTitle.get(title);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByTitle.delete(title);
        }

        String region = c.getRegion();
        if (region != null && this.contentsByRegion.contains(region)) {
            List<Content> list = this.contentsByRegion.get(region);
            removeContentById(list, contentId);
            if (list != null && list.isEmpty()) this.contentsByRegion.delete(region);
        }
    }

    private static void removeUserById(List<User> list, String userId) {
        if (list == null || userId == null) return;
        for (int i = list.size() - 1; i >= 0; i--) {
            User u = list.get(i);
            if (u != null && userId.equals(u.getId())) list.remove(i);
        }
    }

    private static boolean containsUserId(List<User> list, String userId) {
        if (list == null || userId == null) return false;
        for (User u : list) {
            if (u != null && userId.equals(u.getId())) return true;
        }
        return false;
    }

    private static void removeArtistById(List<Artist> list, String artistId) {
        if (list == null || artistId == null) return;
        for (int i = list.size() - 1; i >= 0; i--) {
            Artist a = list.get(i);
            if (a != null && artistId.equals(a.getId())) list.remove(i);
        }
    }

    private static boolean containsArtistId(List<Artist> list, String artistId) {
        if (list == null || artistId == null) return false;
        for (Artist a : list) {
            if (a != null && artistId.equals(a.getId())) return true;
        }
        return false;
    }

    private static void removeContentById(List<Content> list, String contentId) {
        if (list == null || contentId == null) return;
        for (int i = list.size() - 1; i >= 0; i--) {
            Content c = list.get(i);
            if (c != null && contentId.equals(c.getId())) {
                list.remove(i);
            }
        }
    }

    private static boolean containsContentId(List<Content> list, String contentId) {
        if (list == null || contentId == null) return false;
        for (Content c : list) {
            if (c != null && contentId.equals(c.getId())) return true;
        }
        return false;
    }
}
