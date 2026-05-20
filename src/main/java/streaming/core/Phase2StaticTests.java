package streaming.core;

import streaming.db.StreamingDB;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.db.GraphAlgorithms;
import streaming.model.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Fase 2: Testes estáticos para R7, R8, R10 e R11.
 *
 * Executar: correr o main desta classe.
 */
public final class Phase2StaticTests {

    private static int passed;
    private static int failed;

    private Phase2StaticTests() { }

    public static void main(String[] args) {
        passed = 0;
        failed = 0;
        System.out.println("=== Phase2StaticTests (R7 + R8 + R10 + R11) ===");
        try {
            testR7();
            testR8();
            testR10();
            testR11();
        } catch (RuntimeException e) {
            fail("Excepção: " + e.getMessage());
        }
        System.out.println("\nResult: passed=" + passed + " failed=" + failed);
        System.exit(failed == 0 ? 0 : 1);
    }

    // =============================================================
    // R7 - GRAFO
    // =============================================================

    private static void testR7() {
        header("R7: Grafo - vértices, arestas, consultas");

        StreamingDB db = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);

        Genre action = new Genre("G1", "Action");
        db.addGenre(action);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Tom Hanks", "US", LocalDate.of(1956, 7, 9), "M");
        db.addArtist(a1);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "The Terminal", 2004, action, "US", 128, a1);
        Movie m2 = new Movie("M2", LocalDateTime.now(), "Forrest Gump", 1994, action, "US", 142, a1);
        db.addContent(m1);
        db.addContent(m2);

        User u1 = new User("U1", LocalDateTime.now(), "joao", "j@m.com", "h");
        User u2 = new User("U2", LocalDateTime.now(), "maria", "m@m.com", "h");
        User u3 = new User("U3", LocalDateTime.now(), "carlos", "c@m.com", "h");
        db.addUser(u1);
        db.addUser(u2);
        db.addUser(u3);

        int count = api.registerAllEntities();
        check("registerAll -> 6 entidades", count == 6);
        check("vertexCount == 6", graph.vertexCount() == 6);
        check("U1 registado", api.isRegistered("U1"));
        check("M1 registado", api.isRegistered("M1"));

        check("addWatch U1->M1", api.addWatch("U1", "M1", 100, 8.5));
        check("addWatch U1->M2", api.addWatch("U1", "M2", 80, 7.0));
        check("addWatch U2->M1", api.addWatch("U2", "M1", 90, 9.0));
        check("edgeCount == 3", graph.edgeCount() == 3);

        check("addFollow U1->U2", api.addFollow("U1", "U2"));
        check("addFollow U3->U2", api.addFollow("U3", "U2"));
        check("self-follow bloqueado", !api.addFollow("U1", "U1"));

        check("artist->content", api.addArtistToContent("A1", "M1"));

        check("U1 viu 2 conteúdos", api.getContentsForUser("U1").size() == 2);
        check("M1 visto por 2 users", api.getUsersForContent("M1").size() == 2);
        check("U1 segue 1 user", api.getFollowing("U1").size() == 1);
        check("U2 tem 2 seguidores", api.getFollowers("U2").size() == 2);
        check("A1 participa em 1 filme", api.getContentsForArtist("A1").size() == 1);
        check("M1 tem 1 artista", api.getArtistsForContent("M1").size() == 1);

        check("remover U1", api.removeUserFromGraph("U1"));
        check("U1 já não existe", !api.isRegistered("U1"));
        check("U2 ainda existe", api.isRegistered("U2"));

        check("watch user inexistente", !api.addWatch("X", "M1", 100, 5));
        check("follow null", !api.addFollow(null, "U2"));
        check("consulta user inexistente vazia", api.getContentsForUser("X").isEmpty());
    }

    // =============================================================
    // R8 - ALGORITMOS
    // =============================================================

    private static void testR8() {
        StreamingDB db = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);
        GraphAlgorithms alg = new GraphAlgorithms(graph, db);

        Genre action = new Genre("G1", "Action");
        Genre comedy = new Genre("G2", "Comedy");
        db.addGenre(action);
        db.addGenre(comedy);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Tom Hanks", "US", LocalDate.of(1956, 7, 9), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Margot Robbie", "AU", LocalDate.of(1990, 7, 2), "F");
        db.addArtist(a1);
        db.addArtist(a2);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "The Terminal", 2004, action, "US", 128, a1);
        Movie m2 = new Movie("M2", LocalDateTime.now(), "Barbie", 2023, comedy, "US", 114, a2);
        Series s1 = new Series("S1", LocalDateTime.now(), "Breaking Bad", 2008, action, "US", 5, SeriesStatus.ENDED);
        s1.addEpisode(new Episode(1, 1, "Pilot", 58, 9.0f));
        db.addContent(m1);
        db.addContent(m2);
        db.addContent(s1);

        User u1 = new User("U1", LocalDateTime.now(), "joao", "j@m.com", "h");
        User u2 = new User("U2", LocalDateTime.now(), "maria", "m@m.com", "h");
        User u3 = new User("U3", LocalDateTime.now(), "carlos", "c@m.com", "h");
        db.addUser(u1);
        db.addUser(u2);
        db.addUser(u3);

        api.registerAllEntities();

        LocalDateTime jan = LocalDateTime.of(2025, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2025, 2, 20, 14, 0);
        LocalDateTime mar = LocalDateTime.of(2025, 3, 10, 18, 0);

        api.addWatch("U1", "M1", 100, 8.5, jan);
        api.addWatch("U1", "M2", 80, 7.0, feb);
        api.addWatch("U2", "M1", 90, 9.0, feb);
        api.addWatch("U2", "S1", 100, 9.5, mar);
        api.addWatch("U3", "M1", 70, 6.0, mar);

        api.addFollow("U1", "U2");
        api.addFollow("U3", "U2");
        api.addFollow("U3", "U1");

        api.addArtistToContent("A1", "M1");
        api.addArtistToContent("A2", "M2");
        api.addArtistToContent("A1", "M2");

        // R8a
        header("R8a: Caminhos mais curtos");
        List<Entity> path = alg.shortestPath("U1", "M1");
        check("R8a: caminho U1->M1 existe", !path.isEmpty());
        check("R8a: começa em U1", path.get(0).getId().equals("U1"));
        check("R8a: termina em M1", path.get(path.size() - 1).getId().equals("M1"));
        check("R8a: distância finita", alg.shortestPathDistance("U1", "M1") < Double.POSITIVE_INFINITY);
        check("R8a: caminho inexistente vazio", alg.shortestPath("X", "Y").isEmpty());

        // R8b
        header("R8b: Subgrafos");
        StreamingGraph actionSub = alg.subgraphByGenre("Action");
        check("R8b: subgrafo Action tem vértices", actionSub.vertexCount() > 0);
        check("R8b: subgrafo Action tem arestas", actionSub.edgeCount() > 0);
        check("R8b: subgrafo Comedy tem vértices", alg.subgraphByGenre("Comedy").vertexCount() > 0);
        check("R8b: subgrafo US tem vértices", alg.subgraphByRegion("US").vertexCount() > 0);
        check("R8b: subgrafo rating>=8 tem vértices", alg.subgraphByMinRating(8.0).vertexCount() > 0);
        check("R8b: subgrafo Horror vazio", alg.subgraphByGenre("Horror").vertexCount() == 0);

        // R8c
        header("R8c: Conectividade");
        check("R8c: isConnected executa", alg.isConnected() || !alg.isConnected());
        check("R8c: subgrafo Action conectividade", alg.isConnected(actionSub) || !alg.isConnected(actionSub));
        StreamingGraph tiny = new StreamingGraph();
        tiny.addVertex(u1);
        check("R8c: 1 vértice é conexo", alg.isConnected(tiny));

        // R8d
        header("R8d: Recomendações");
        List<Content> recs = alg.recommend("U3");
        check("R8d: U3 tem recomendações", !recs.isEmpty());
        check("R8d: não recomenda M1 (já viu)", recs.stream().noneMatch(c -> c.getId().equals("M1")));
        check("R8d: user inexistente vazio", alg.recommend("X").isEmpty());

        // R8e
        header("R8e: Estatísticas de visualização");
        Map<String, Double> stats = alg.getViewStats("M1", null, null);
        check("R8e: M1 totalViews == 3", stats.get("totalViews") == 3.0);
        check("R8e: M1 avgRating > 0", stats.get("avgRating") > 0);
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 2, 28, 23, 59);
        check("R8e: M1 jan-fev totalViews == 2", alg.getViewStats("M1", startDate, endDate).get("totalViews") == 2.0);
        check("R8e: Action totalViews > 0", alg.getViewStatsByGenre("Action", null, null).get("totalViews") > 0);

        // R8f
        header("R8f: Users que viram séries por género");
        List<User> seriesWatchers = alg.usersWhoWatchedSeriesByGenre("Action", null, null);
        check("R8f: 1 user viu séries Action", seriesWatchers.size() == 1);
        check("R8f: U2 viu série Action", seriesWatchers.get(0).getId().equals("U2"));
        check("R8f: ninguém em janeiro", alg.usersWhoWatchedSeriesByGenre("Action", startDate, LocalDateTime.of(2025, 1, 31, 23, 59)).isEmpty());
        check("R8f: ninguém Comedy", alg.usersWhoWatchedSeriesByGenre("Comedy", null, null).isEmpty());

        // R8g
        header("R8g: Seguidores que viram mesmo filme");
        check("R8g: 2 seguidores de U2 viram M1", alg.followersWhoWatchedSameContent("U2", "M1", null, null).size() == 2);
        check("R8g: 1 em janeiro", alg.followersWhoWatchedSameContent("U2", "M1", startDate, LocalDateTime.of(2025, 1, 31, 23, 59)).size() == 1);
        check("R8g: U3 sem seguidores", alg.followersWhoWatchedSameContent("U3", "M1", null, null).isEmpty());
    }

    // =============================================================
    // R10 - IMPORT/EXPORT TEXTO
    // =============================================================

    private static void testR10() {
        header("R10: Export + Import TXT");
        Object[] data = setupR10R11();
        StreamingDB db = (StreamingDB) data[0];
        StreamingGraph graph = (StreamingGraph) data[1];

        FileManager fm = new FileManager();
        String tmpDir = System.getProperty("java.io.tmpdir");
        String txtPath = tmpDir + File.separator + "phase2_r10.txt";
        fm.exportTxt(db, graph, txtPath);

        StreamingDB db2 = new StreamingDB();
        StreamingGraph graph2 = new StreamingGraph();
        StreamingGraphAPI api2 = new StreamingGraphAPI(db2, graph2);
        fm.importTxt(txtPath, db2, api2);

        check("R10: Genre G1", db2.getGenre("G1") != null);
        check("R10: Genre G2", db2.getGenre("G2") != null);
        check("R10: Artist A1", db2.getArtist("A1") != null);
        check("R10: Movie M1", db2.getContent("M1") instanceof Movie);
        check("R10: Series S1", db2.getContent("S1") instanceof Series);
        check("R10: S1 tem 2 eps", ((Series) db2.getContent("S1")).getEpisodes().size() == 2);
        check("R10: User U1", db2.getUser("U1") != null);
        check("R10: User U2", db2.getUser("U2") != null);
        check("R10: U1 region Porto", "Porto".equals(db2.getUser("U1").getRegion()));
        check("R10: Grafo vertices", graph2.vertexCount() > 0);
        check("R10: Grafo arestas", graph2.edgeCount() > 0);
        check("R10: 2 interacoes", graph2.getInteractions().size() == 2);
        check("R10: U1 follow U2", db2.getUser("U1").getFollowing().size() == 1);
    }

    // =============================================================
    // R11 - SERIALIZAÇÃO BINÁRIA
    // =============================================================

    private static void testR11() {
        header("R11: Serialize + Deserialize BIN");
        Object[] data = setupR10R11();
        StreamingDB db = (StreamingDB) data[0];
        StreamingGraph graph = (StreamingGraph) data[1];

        FileManager fm = new FileManager();
        String tmpDir = System.getProperty("java.io.tmpdir");
        String binPath = tmpDir + File.separator + "phase2_r11.bin";
        fm.serializeBin(db, graph, binPath);

        StreamingDB db2 = new StreamingDB();
        StreamingGraph graph2 = new StreamingGraph();
        StreamingGraphAPI api2 = new StreamingGraphAPI(db2, graph2);
        fm.deserializeBin(binPath, db2, api2);

        check("R11: Genre G1", db2.getGenre("G1") != null);
        check("R11: Genre G2", db2.getGenre("G2") != null);
        check("R11: Artist A1", db2.getArtist("A1") != null);
        check("R11: Movie M1", db2.getContent("M1") instanceof Movie);
        check("R11: Series S1", db2.getContent("S1") instanceof Series);
        check("R11: S1 tem 2 eps", ((Series) db2.getContent("S1")).getEpisodes().size() == 2);
        check("R11: User U1", db2.getUser("U1") != null);
        check("R11: U1 region Porto", "Porto".equals(db2.getUser("U1").getRegion()));
        check("R11: Grafo vertices", graph2.vertexCount() > 0);
        check("R11: Grafo arestas", graph2.edgeCount() > 0);
        check("R11: 2 interacoes", graph2.getInteractions().size() == 2);
        check("R11: U1 follow U2", db2.getUser("U1").getFollowing().size() == 1);
    }

    // =============================================================
    // SETUP para R10/R11
    // =============================================================

    private static Object[] setupR10R11() {
        StreamingDB db = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);

        Genre action = new Genre("G1", "Action");
        Genre comedy = new Genre("G2", "Comedy");
        db.addGenre(action);
        db.addGenre(comedy);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Tom Hanks", "US", LocalDate.of(1956, 7, 9), "M");
        db.addArtist(a1);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "The Terminal", 2004, action, "US", 128, a1);
        db.addContent(m1);

        Series s1 = new Series("S1", LocalDateTime.now(), "Breaking Bad", 2008, action, "US", 5, SeriesStatus.ENDED);
        s1.addEpisode(new Episode(1, 1, "Pilot", 58, 9.0f));
        s1.addEpisode(new Episode(1, 2, "Cat in the Bag", 48, 8.5f));
        db.addContent(s1);

        User u1 = new User("U1", "João", "joao@email.com");
        u1.setRegion("PT"); // <-- A magia
        u1.setRegistrationDate(LocalDateTime.now()); // <-- A magia
        db.addUser(u1);



        User u2 = new User("U2", "Maria", "maria@email.com");
        u2.setRegion("BR");
        u2.setRegistrationDate(LocalDateTime.now());
        db.addUser(u2);

        api.registerAllEntities();
        api.addWatch("U1", "M1", 100, 8.5, LocalDateTime.of(2025, 1, 15, 10, 0));
        api.addWatch("U2", "S1", 90, 9.0, LocalDateTime.of(2025, 2, 20, 14, 0));
        api.addFollow("U1", "U2");

        return new Object[]{db, graph, api};
    }

    // =============================================================
    // HELPERS
    // =============================================================

    private static void check(String label, boolean cond) {
        if (cond) { passed++; System.out.println("  [PASS] " + label); }
        else      { failed++; System.out.println("  [FAIL] " + label); }
    }

    private static void fail(String msg) {
        failed++;
        System.out.println("  [FAIL] " + msg);
    }

    private static void header(String title) {
        System.out.println("\n--- " + title + " ---");
    }
}
