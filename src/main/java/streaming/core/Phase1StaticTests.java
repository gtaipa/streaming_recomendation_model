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
 * Fase 1 (R5): Suite de testes com funcoes static, com output na consola.
 *
 * Executar:
 * - Pelo IDE: correr o main desta classe
 * - Pela linha de comandos (Maven): usar o plugin exec/IDE, ou correr a classe diretamente.
 */
public final class Phase1StaticTests {

    private static int passed;
    private static int failed;

    private Phase1StaticTests() { }

    public static void main(String[] args) {
        boolean ok = runAll(true);
        System.exit(ok ? 0 : 1);
    }

    /**
     * Corre todos os testes e devolve true se todos passaram.
     */
    public static boolean runAll(boolean printToConsole) {
        passed = 0;
        failed = 0;

        if (printToConsole) {
            System.out.println("=== Phase1StaticTests (R5) ===");
        }

        try {
            testUsersCrudAndIndexes(printToConsole);
            testArtistsCrudAndIndexes(printToConsole);
            testGenresAndContentsCrud(printToConsole);
            testArchiveAndRestoreUser(printToConsole);
            testConsistencyValidation(printToConsole);
        } catch (RuntimeException e) {
            fail("Unhandled exception: " + e.getClass().getSimpleName() + " - " + e.getMessage(), printToConsole);
        }

        if (printToConsole) {
            System.out.println();
            System.out.println("Result: passed=" + passed + " failed=" + failed);
        }
        return failed == 0;
    }

    private static void testUsersCrudAndIndexes(boolean print) {
        header("Users CRUD + indices (ST/BST sync)", print);

        StreamingDB db = new StreamingDB();

        User u1 = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "hash");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));

        User u2 = new User("U2", LocalDateTime.now(), "bob", "b@ex.com", "hash");
        u2.setRegion("Porto");
        u2.setRegistrationDate(LocalDate.of(2025, 2, 5));

        db.addUser(u1);
        db.addUser(u2);

        assertTrue("listUsers size==2", db.listUsers().size() == 2, print);
        assertTrue("getUser U1 != null", db.getUser("U1") != null, print);

        List<User> lisboa = db.searchUsersByRegion("Lisboa");
        assertTrue("searchUsersByRegion Lisboa has U1", containsUserId(lisboa, "U1"), print);

        List<User> regBetween = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );
        assertTrue("searchUsersRegisteredBetween contains U1", containsUserId(regBetween, "U1"), print);

        // In-place mutation scenario: change region, then update using the same instance.
        u1.setRegion("Porto");
        assertTrue("updateUser returns true", db.updateUser(u1), print);

        assertTrue("Lisboa no longer contains U1", !containsUserId(db.searchUsersByRegion("Lisboa"), "U1"), print);
        assertTrue("Porto now contains U1", containsUserId(db.searchUsersByRegion("Porto"), "U1"), print);
    }

    private static void testArtistsCrudAndIndexes(boolean print) {
        header("Artists CRUD + indices", print);

        StreamingDB db = new StreamingDB();

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan", "UK", LocalDate.of(1970, 7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10, 3), "M");
        db.addArtist(a1);
        db.addArtist(a2);

        assertTrue("listArtists size==2", db.listArtists().size() == 2, print);
        assertTrue("searchArtistsByNationality UK contains A1",
                containsArtistId(db.searchArtistsByNationality("UK"), "A1"),
                print);

        List<Artist> born = db.searchArtistsBornBetween(LocalDate.of(1960, 1, 1), LocalDate.of(1975, 12, 31));
        assertTrue("searchArtistsBornBetween contains A1 and A2",
                containsArtistId(born, "A1") && containsArtistId(born, "A2"),
                print);

        assertTrue("removeArtist returns true", db.removeArtist("A2"), print);
        assertTrue("getArtist A2 == null", db.getArtist("A2") == null, print);
        assertTrue("Nationality CA no longer contains A2",
                !containsArtistId(db.searchArtistsByNationality("CA"), "A2"),
                print);
    }

    private static void testGenresAndContentsCrud(boolean print) {
        header("Genres + Contents CRUD + searches", print);

        StreamingDB db = new StreamingDB();

        Genre g1 = new Genre("G1", "Acao");
        db.addGenre(g1);
        assertTrue("listGenres size==1", db.listGenres().size() == 1, print);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Director", "PT", LocalDate.of(1980, 1, 1), "X");
        db.addArtist(dir);

        Movie m1 = new Movie("C1", LocalDateTime.now(), "Filme Um", 2024, g1, "PT", 120, dir);
        Series s1 = new Series("C2", LocalDateTime.now(), "Serie Um", 2020, g1, "PT", 2, SeriesStatus.ONGOING);

        db.addContent(m1);
        db.addContent(s1);

        assertTrue("listContents size==2", db.listContents().size() == 2, print);
        assertTrue("searchByGenre Acao contains C1,C2",
                containsContentId(db.searchByGenre("Acao"), "C1") && containsContentId(db.searchByGenre("Acao"), "C2"),
                print);
        assertTrue("searchContentsReleasedBetween 2019-2024 contains both",
                containsContentId(db.searchContentsReleasedBetween(2019, 2024), "C1") &&
                        containsContentId(db.searchContentsReleasedBetween(2019, 2024), "C2"),
                print);
        assertTrue("searchContentsByType movie contains C1",
                containsContentId(db.searchContentsByType("movie"), "C1"),
                print);

        // Update genre name should move the genre index key.
        assertTrue("updateGenreName returns true", db.updateGenreName("G1", "Action"), print);
        assertTrue("old genre key returns empty", db.searchByGenre("Acao").isEmpty(), print);
        assertTrue("new genre key contains both", containsContentId(db.searchByGenre("Action"), "C1")
                && containsContentId(db.searchByGenre("Action"), "C2"), print);

        // Removing a genre referenced by content should fail (conservative integrity rule).
        assertTrue("removeGenre referenced returns false", !db.removeGenre("G1"), print);

        db.removeContent("C2");
        assertTrue("removeContent removed series", db.getContent("C2") == null, print);

        assertTrue("removeContentIfExists returns true", db.removeContentIfExists("C1"), print);
        assertTrue("removeContentIfExists returns false when missing", !db.removeContentIfExists("C1"), print);
    }

    private static void testArchiveAndRestoreUser(boolean print) {
        header("Archived users CRUD", print);

        StreamingDB db = new StreamingDB();
        User u = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "hash");
        u.setRegion("Lisboa");
        u.setRegistrationDate(LocalDate.of(2025, 1, 10));
        db.addUser(u);

        assertTrue("removeUser archive=true returns true", db.removeUser("U1", true), print);
        assertTrue("getUser U1 == null", db.getUser("U1") == null, print);
        assertTrue("getArchivedUser U1 != null", db.getArchivedUser("U1") != null, print);

        assertTrue("restoreArchivedUser returns true", db.restoreArchivedUser("U1"), print);
        assertTrue("getUser U1 != null after restore", db.getUser("U1") != null, print);
        assertTrue("getArchivedUser U1 == null after restore", db.getArchivedUser("U1") == null, print);

        assertTrue("clearArchivedUsers ok", true, print);
        db.clearArchivedUsers();
        assertTrue("archived list is empty", db.listArchivedUsers().isEmpty(), print);
    }

    private static void testConsistencyValidation(boolean print) {
        header("validateConsistency()", print);

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

        assertTrue("validateConsistency returns true", db.validateConsistency(), print);
    }

    private static void header(String name, boolean print) {
        if (!print) return;
        System.out.println();
        System.out.println("-- " + name);
    }

    private static void assertTrue(String name, boolean condition, boolean print) {
        if (condition) {
            passed++;
            if (print) System.out.println("[OK]   " + name);
        } else {
            fail(name, print);
        }
    }

    private static void fail(String name, boolean print) {
        failed++;
        if (print) System.out.println("[FAIL] " + name);
    }

    private static boolean containsUserId(List<User> list, String id) {
        if (list == null || id == null) return false;
        for (User u : list) {
            if (u != null && id.equals(u.getId())) return true;
        }
        return false;
    }

    private static boolean containsArtistId(List<Artist> list, String id) {
        if (list == null || id == null) return false;
        for (Artist a : list) {
            if (a != null && id.equals(a.getId())) return true;
        }
        return false;
    }

    private static boolean containsContentId(List<?> list, String id) {
        if (list == null || id == null) return false;
        for (Object o : list) {
            if (o instanceof streaming.model.Content c && id.equals(c.getId())) return true;
        }
        return false;
    }
}

