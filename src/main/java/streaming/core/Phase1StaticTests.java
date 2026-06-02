package streaming.core;

import streaming.db.StreamingDB;
import streaming.model.Artist;
import streaming.model.Genre;
import streaming.model.Movie;
import streaming.model.Series;
import streaming.model.SeriesStatus;
import streaming.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Fase 1 (R5): Testes estáticos organizados por requisito.
 *
 * R2 - Symbol Tables: inserir, remover, editar, listar (HashMap)
 * R3 - BST balanceada: pesquisas ordenadas, por intervalo, por substring
 * R4 - Validação de consistência entre estruturas
 */
public final class Phase1StaticTests {

    private static int passed;
    private static int failed;

    private Phase1StaticTests() { }

    public static void main(String[] args) {
        boolean ok = runAll(true);
        System.exit(ok ? 0 : 1);
    }

    public static boolean runAll(boolean print) {
        passed = 0;
        failed = 0;
        if (print) System.out.println("=== Phase1StaticTests ===");
        try {
            testR2(print);
            testR3(print);
            testR4(print);
        } catch (RuntimeException e) {
            fail("Excepção: " + e.getClass().getSimpleName() + " - " + e.getMessage(), print);
        }
        if (print) System.out.println("\nResult: passed=" + passed + " failed=" + failed);
        return failed == 0;
    }

    // =============================================================
    // R2 - SYMBOL TABLES: inserir, remover, editar, listar
    // =============================================================

    private static void testR2(boolean print) {
        header("R2: Symbol Tables - Users CRUD", print);
        testR2Users(print);
        header("R2: Symbol Tables - Artists CRUD", print);
        testR2Artists(print);
        header("R2: Symbol Tables - Genres CRUD", print);
        testR2Genres(print);
        header("R2: Symbol Tables - Contents CRUD", print);
        testR2Contents(print);
    }

    private static void testR2Users(boolean print) {
        StreamingDB db = new StreamingDB();

        User u1 = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "hash");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        User u2 = new User("U2", LocalDateTime.now(), "bob", "b@ex.com", "hash");
        u2.setRegion("Porto");
        u2.setRegistrationDate(LocalDate.of(2025, 2, 5));

        // Inserir
        db.addUser(u1);
        db.addUser(u2);
        assertTrue("R2: addUser - listUsers size==2", db.listUsers().size() == 2, print);
        assertTrue("R2: addUser - getUser U1 != null", db.getUser("U1") != null, print);
        assertTrue("R2: addUser - getUser U2 != null", db.getUser("U2") != null, print);

        // Editar (criar objeto novo para evitar mutação in-place)
        User u1Updated = new User("U1", u1.getCreatedAt(), "alice_updated", "a@ex.com", "hash");
        u1Updated.setRegion("Porto");
        u1Updated.setRegistrationDate(LocalDate.of(2025, 1, 10));
        assertTrue("R2: updateUser returns true", db.updateUser(u1Updated), print);
        assertTrue("R2: updateUser - username atualizado", "alice_updated".equals(db.getUser("U1").getUsername()), print);

        // Remover
        assertTrue("R2: removeUser returns true", db.removeUser("U2", false), print);
        assertTrue("R2: removeUser - getUser U2 == null", db.getUser("U2") == null, print);
        assertTrue("R2: removeUser - listUsers size==1", db.listUsers().size() == 1, print);
    }

    private static void testR2Artists(boolean print) {
        StreamingDB db = new StreamingDB();

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan", "UK", LocalDate.of(1970, 7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10, 3), "M");

        // Inserir
        db.addArtist(a1);
        db.addArtist(a2);
        assertTrue("R2: addArtist - listArtists size==2", db.listArtists().size() == 2, print);
        assertTrue("R2: addArtist - getArtist A1 != null", db.getArtist("A1") != null, print);

        // Remover
        assertTrue("R2: removeArtist returns true", db.removeArtist("A2"), print);
        assertTrue("R2: removeArtist - getArtist A2 == null", db.getArtist("A2") == null, print);
        assertTrue("R2: removeArtist - listArtists size==1", db.listArtists().size() == 1, print);
    }

    private static void testR2Genres(boolean print) {
        StreamingDB db = new StreamingDB();

        Genre g1 = new Genre("G1", "Acao");
        Genre g2 = new Genre("G2", "Drama");

        // Inserir
        db.addGenre(g1);
        db.addGenre(g2);
        assertTrue("R2: addGenre - listGenres size==2", db.listGenres().size() == 2, print);
        assertTrue("R2: addGenre - getGenre G1 != null", db.getGenre("G1") != null, print);

        // Editar nome
        assertTrue("R2: updateGenreName returns true", db.updateGenreName("G1", "Action"), print);
        assertTrue("R2: updateGenreName - nome atualizado", "Action".equals(db.getGenre("G1").getName()), print);

        // Remover (sem conteúdos associados)
        assertTrue("R2: removeGenre sem referências returns true", db.removeGenre("G2"), print);
        assertTrue("R2: removeGenre - getGenre G2 == null", db.getGenre("G2") == null, print);
    }

    private static void testR2Contents(boolean print) {
        StreamingDB db = new StreamingDB();
        Genre g1 = new Genre("G1", "Acao");
        db.addGenre(g1);
        Artist dir = new Artist("A1", LocalDateTime.now(), "Director", "PT", LocalDate.of(1980, 1, 1), "X");
        db.addArtist(dir);

        Movie m1 = new Movie("C1", LocalDateTime.now(), "Filme Um", 2024, g1, "PT", 120, dir);
        Series s1 = new Series("C2", LocalDateTime.now(), "Serie Um", 2020, g1, "PT", 2, SeriesStatus.ONGOING);

        // Inserir
        db.addContent(m1);
        db.addContent(s1);
        assertTrue("R2: addContent - listContents size==2", db.listContents().size() == 2, print);
        assertTrue("R2: addContent - getContent C1 != null", db.getContent("C1") != null, print);

        // Remover
        db.removeContent("C2");
        assertTrue("R2: removeContent - getContent C2 == null", db.getContent("C2") == null, print);
        assertTrue("R2: removeContentIfExists returns true", db.removeContentIfExists("C1"), print);
        assertTrue("R2: removeContentIfExists inexistente returns false", !db.removeContentIfExists("C1"), print);

        // Remover género referenciado deve falhar
        db.addContent(new Movie("C3", LocalDateTime.now(), "Teste", 2024, g1, "PT", 90, dir));
        assertTrue("R2: removeGenre referenciado returns false", !db.removeGenre("G1"), print);
    }

    // =============================================================
    // R3 - BST BALANCEADA: pesquisas ordenadas e por intervalo
    // =============================================================

    private static void testR3(boolean print) {
        header("R3a/b: Pesquisas de Users por região, data, substring", print);
        testR3Users(print);
        header("R3c/d: Pesquisas de Artists por nacionalidade, data, substring", print);
        testR3Artists(print);
        header("R3e/f/g: Pesquisas de Contents por tipo, género, ano, título", print);
        testR3Contents(print);
    }

    private static void testR3Users(boolean print) {
        StreamingDB db = new StreamingDB();

        User u1 = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "hash");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        User u2 = new User("U2", LocalDateTime.now(), "bob", "b@ex.com", "hash");
        u2.setRegion("Porto");
        u2.setRegistrationDate(LocalDate.of(2025, 2, 5));
        User u3 = new User("U3", LocalDateTime.now(), "alice_porto", "c@ex.com", "hash");
        u3.setRegion("Porto");
        u3.setRegistrationDate(LocalDate.of(2025, 3, 15));
        db.addUser(u1);
        db.addUser(u2);
        db.addUser(u3);

        // R3a) Pesquisa por região
        assertTrue("R3a: searchUsersByRegion Lisboa has U1",
                containsUserId(db.searchUsersByRegion("Lisboa"), "U1"), print);
        assertTrue("R3a: searchUsersByRegion Porto has U2 e U3",
                containsUserId(db.searchUsersByRegion("Porto"), "U2")
                        && containsUserId(db.searchUsersByRegion("Porto"), "U3"), print);
        assertTrue("R3a: searchUsersByRegion inexistente é vazia",
                db.searchUsersByRegion("Faro").isEmpty(), print);

        // R3a) Pesquisa por data de registo (intervalo)
        List<User> janUsers = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        assertTrue("R3a: users registados em janeiro contém U1", containsUserId(janUsers, "U1"), print);
        assertTrue("R3a: users registados em janeiro não contém U2", !containsUserId(janUsers, "U2"), print);

        List<User> allUsers = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        assertTrue("R3a: users registados em 2025 contém os 3", allUsers.size() == 3, print);

        // R3b) Pesquisa por substring no username
        assertTrue("R3b: substring 'alice' encontra U1 e U3",
                containsUserId(db.searchUsersByUsernameSubstring("alice"), "U1")
                        && containsUserId(db.searchUsersByUsernameSubstring("alice"), "U3"), print);
        assertTrue("R3b: substring 'bob' encontra U2",
                containsUserId(db.searchUsersByUsernameSubstring("bob"), "U2"), print);
        assertTrue("R3b: substring 'xyz' é vazia",
                db.searchUsersByUsernameSubstring("xyz").isEmpty(), print);

        // R3a) Após update da região, índices atualizam
        User u1Updated = new User("U1", u1.getCreatedAt(), "alice", "a@ex.com", "hash");
        u1Updated.setRegion("Porto");
        u1Updated.setRegistrationDate(LocalDate.of(2025, 1, 10));
        db.updateUser(u1Updated);
        assertTrue("R3a: após update, Lisboa não contém U1",
                !containsUserId(db.searchUsersByRegion("Lisboa"), "U1"), print);
        assertTrue("R3a: após update, Porto contém U1",
                containsUserId(db.searchUsersByRegion("Porto"), "U1"), print);
    }

    private static void testR3Artists(boolean print) {
        StreamingDB db = new StreamingDB();

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan", "UK", LocalDate.of(1970, 7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10, 3), "M");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "Spielberg", "US", LocalDate.of(1946, 12, 18), "M");
        db.addArtist(a1);
        db.addArtist(a2);
        db.addArtist(a3);

        // R3c) Pesquisa por nacionalidade
        assertTrue("R3c: searchArtistsByNationality UK contém A1",
                containsArtistId(db.searchArtistsByNationality("UK"), "A1"), print);
        assertTrue("R3c: searchArtistsByNationality PT é vazia",
                db.searchArtistsByNationality("PT").isEmpty(), print);

        // R3c) Pesquisa por intervalo de datas de nascimento
        List<Artist> born60s70s = db.searchArtistsBornBetween(
                LocalDate.of(1960, 1, 1), LocalDate.of(1975, 12, 31));
        assertTrue("R3c: nascidos 1960-1975 contém A1 e A2",
                containsArtistId(born60s70s, "A1") && containsArtistId(born60s70s, "A2"), print);
        assertTrue("R3c: nascidos 1960-1975 não contém A3 (1946)",
                !containsArtistId(born60s70s, "A3"), print);

        // R3d) Pesquisa por substring no nome
        assertTrue("R3d: substring 'nol' encontra Nolan",
                containsArtistId(db.searchArtistsByNameSubstring("nol"), "A1"), print);
        assertTrue("R3d: substring 'berg' encontra Spielberg",
                containsArtistId(db.searchArtistsByNameSubstring("berg"), "A3"), print);
        assertTrue("R3d: substring 'xyz' é vazia",
                db.searchArtistsByNameSubstring("xyz").isEmpty(), print);
    }

    private static void testR3Contents(boolean print) {
        StreamingDB db = new StreamingDB();
        Genre action = new Genre("G1", "Acao");
        Genre drama = new Genre("G2", "Drama");
        db.addGenre(action);
        db.addGenre(drama);
        Artist dir = new Artist("A1", LocalDateTime.now(), "Director", "PT", LocalDate.of(1980, 1, 1), "X");
        db.addArtist(dir);

        Movie m1 = new Movie("C1", LocalDateTime.now(), "Filme de Acao", 2024, action, "PT", 120, dir);
        Movie m2 = new Movie("C2", LocalDateTime.now(), "Drama Intenso", 2020, drama, "PT", 90, dir);
        Series s1 = new Series("C3", LocalDateTime.now(), "Serie de Acao", 2022, action, "US", 3, SeriesStatus.ONGOING);
        db.addContent(m1);
        db.addContent(m2);
        db.addContent(s1);

        // R3e) Pesquisa por género
        List<?> acaoContents = db.searchByGenre("Acao");
        assertTrue("R3e: searchByGenre Acao contém C1 e C3",
                containsContentId(acaoContents, "C1") && containsContentId(acaoContents, "C3"), print);
        assertTrue("R3e: searchByGenre Acao não contém C2",
                !containsContentId(acaoContents, "C2"), print);

        // R3e) Pesquisa por intervalo de anos
        List<?> contents2020_2022 = db.searchContentsReleasedBetween(2020, 2022);
        assertTrue("R3e: 2020-2022 contém C2 e C3",
                containsContentId(contents2020_2022, "C2") && containsContentId(contents2020_2022, "C3"), print);
        assertTrue("R3e: 2020-2022 não contém C1 (2024)",
                !containsContentId(contents2020_2022, "C1"), print);

        // R3e) Pesquisa por tipo
        assertTrue("R3g: searchContentsByType 'movie' contém C1",
                containsContentId(db.searchContentsByType("movie"), "C1"), print);
        assertTrue("R3g: searchContentsByType 'series' contém C3",
                containsContentId(db.searchContentsByType("series"), "C3"), print);

        // R3f) Pesquisa por substring no título
        assertTrue("R3f: substring 'Acao' encontra C1 e C3",
                containsContentId(db.searchContentsByTitleSubstring("Acao"), "C1")
                        && containsContentId(db.searchContentsByTitleSubstring("Acao"), "C3"), print);
        assertTrue("R3f: substring 'Intenso' encontra C2",
                containsContentId(db.searchContentsByTitleSubstring("Intenso"), "C2"), print);
        assertTrue("R3f: substring 'xyz' é vazia",
                db.searchContentsByTitleSubstring("xyz").isEmpty(), print);

        // R3e) Após updateGenreName, índice atualiza
        db.updateGenreName("G1", "Action");
        assertTrue("R3e: após rename, 'Acao' vazio", db.searchByGenre("Acao").isEmpty(), print);
        assertTrue("R3e: após rename, 'Action' contém C1 e C3",
                containsContentId(db.searchByGenre("Action"), "C1")
                        && containsContentId(db.searchByGenre("Action"), "C3"), print);
    }

    // =============================================================
    // R4 - CONSISTÊNCIA ENTRE ESTRUTURAS
    // =============================================================

    private static void testR4(boolean print) {
        header("R4: Validação de consistência e arquivo", print);

        StreamingDB db = new StreamingDB();
        Genre g1 = new Genre("G1", "Drama");
        db.addGenre(g1);
        Artist dir = new Artist("A1", LocalDateTime.now(), "Director", "PT", LocalDate.of(1980, 1, 1), "X");
        db.addArtist(dir);
        Movie m1 = new Movie("C1", LocalDateTime.now(), "Filme", 2024, g1, "PT", 90, dir);
        db.addContent(m1);

        User u1 = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "hash");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        db.addUser(u1);

        // R4: validateConsistency
        assertTrue("R4: validateConsistency returns true", db.validateConsistency(), print);

        // R4: Remoção com arquivo
        assertTrue("R4: removeUser com archive=true", db.removeUser("U1", true), print);
        assertTrue("R4: user removido dos ativos", db.getUser("U1") == null, print);
        assertTrue("R4: user existe no arquivo", db.getArchivedUser("U1") != null, print);
        assertTrue("R4: região limpa dos índices",
                db.searchUsersByRegion("Lisboa").isEmpty(), print);

        // R4: Restaurar do arquivo
        assertTrue("R4: restoreArchivedUser returns true", db.restoreArchivedUser("U1"), print);
        assertTrue("R4: user de volta nos ativos", db.getUser("U1") != null, print);
        assertTrue("R4: user saiu do arquivo", db.getArchivedUser("U1") == null, print);
        assertTrue("R4: região re-indexada",
                containsUserId(db.searchUsersByRegion("Lisboa"), "U1"), print);

        // R4: Limpar arquivo
        db.removeUser("U1", true);
        db.clearArchivedUsers();
        assertTrue("R4: arquivo vazio após clear", db.listArchivedUsers().isEmpty(), print);

        // R4: Consistência mantida após operações
        assertTrue("R4: consistência OK após todas as operações", db.validateConsistency(), print);
    }

    // =============================================================
    // HELPERS
    // =============================================================

    private static void header(String name, boolean print) {
        if (print) System.out.println("\n--- " + name + " ---");
    }

    private static void assertTrue(String name, boolean cond, boolean print) {
        if (cond) { passed++; if (print) System.out.println("  [PASS] " + name); }
        else fail(name, print);
    }

    private static void fail(String name, boolean print) {
        failed++;
        if (print) System.out.println("  [FAIL] " + name);
    }

    private static boolean containsUserId(List<User> list, String id) {
        if (list == null || id == null) return false;
        for (User u : list) if (u != null && id.equals(u.getId())) return true;
        return false;
    }

    private static boolean containsArtistId(List<Artist> list, String id) {
        if (list == null || id == null) return false;
        for (Artist a : list) if (a != null && id.equals(a.getId())) return true;
        return false;
    }

    private static boolean containsContentId(List<?> list, String id) {
        if (list == null || id == null) return false;
        for (Object o : list)
            if (o instanceof streaming.model.Content c && id.equals(c.getId())) return true;
        return false;
    }
}