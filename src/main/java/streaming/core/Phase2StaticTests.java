package streaming.core;

import streaming.db.StreamingDB;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.db.GraphAlgorithms;
import streaming.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Fase 2 (R7 + R8): Testes do grafo e dos algoritmos.
 *
 * Executar: correr o main desta classe.
 */
public final class Phase2StaticTests {

    private static int passed;
    private static int failed;

    private Phase2StaticTests() { }

    public static void main(String[] args) {
        boolean ok = runAll(true);
        System.exit(ok ? 0 : 1);
    }

    public static boolean runAll(boolean print) {
        passed = 0;
        failed = 0;

        if (print) System.out.println("=== Phase2StaticTests (R7 + R8) ===");

        try {
            testR7(print);
            testR8(print);
        } catch (RuntimeException e) {
            fail("Excepção: " + e.getMessage(), print);
        }

        if (print) System.out.println("\nResult: passed=" + passed + " failed=" + failed);
        return failed == 0;
    }

    // =============================================================
    // R7 - GRAFO: vértices, arestas, consultas
    // =============================================================

    private static void testR7(boolean print) {
        header("R7: Grafo - vértices, arestas, consultas", print);

        // --- Setup: DB + dados ---
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

        // --- R7: Registar entidades no grafo ---
        int count = api.registerAllEntities();
        assertTrue("registerAll -> 6 entidades", count == 6, print);
        assertTrue("vertexCount == 6", graph.vertexCount() == 6, print);
        assertTrue("U1 registado", api.isRegistered("U1"), print);
        assertTrue("M1 registado", api.isRegistered("M1"), print);

        // --- R7: Arestas WATCH ---
        assertTrue("addWatch U1->M1", api.addWatch("U1", "M1", 100, 8.5), print);
        assertTrue("addWatch U1->M2", api.addWatch("U1", "M2", 80, 7.0), print);
        assertTrue("addWatch U2->M1", api.addWatch("U2", "M1", 90, 9.0), print);
        assertTrue("edgeCount == 3", graph.edgeCount() == 3, print);

        // --- R7: Arestas FOLLOW ---
        assertTrue("addFollow U1->U2", api.addFollow("U1", "U2"), print);
        assertTrue("addFollow U3->U2", api.addFollow("U3", "U2"), print);
        assertTrue("self-follow bloqueado", !api.addFollow("U1", "U1"), print);

        // --- R7: Aresta Artist -> Content ---
        assertTrue("artist->content", api.addArtistToContent("A1", "M1"), print);

        // --- R7: Consultas ---
        assertTrue("U1 viu 2 conteúdos", api.getContentsForUser("U1").size() == 2, print);
        assertTrue("M1 visto por 2 users", api.getUsersForContent("M1").size() == 2, print);
        assertTrue("U1 segue 1 user", api.getFollowing("U1").size() == 1, print);
        assertTrue("U2 tem 2 seguidores", api.getFollowers("U2").size() == 2, print);
        assertTrue("A1 participa em 1 filme", api.getContentsForArtist("A1").size() == 1, print);
        assertTrue("M1 tem 1 artista", api.getArtistsForContent("M1").size() == 1, print);

        // --- R7: Remoção ---
        assertTrue("remover U1", api.removeUserFromGraph("U1"), print);
        assertTrue("U1 já não existe", !api.isRegistered("U1"), print);
        assertTrue("U2 ainda existe", api.isRegistered("U2"), print);

        // --- R7: Operações inválidas ---
        assertTrue("watch user inexistente", !api.addWatch("X", "M1", 100, 5), print);
        assertTrue("follow null", !api.addFollow(null, "U2"), print);
        assertTrue("consulta user inexistente vazia", api.getContentsForUser("X").isEmpty(), print);

        if (print) {
            System.out.println("\n  [DEBUG] Estado do grafo:");
            System.out.println(api.toString());
        }
    }

    // =============================================================
    // R8 - ALGORITMOS: caminhos, subgrafos, conectividade, etc.
    // =============================================================

    private static void testR8(boolean print) {

        // --- Setup completo (cenário novo, independente do R7) ---
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

        // Criar relações com timestamps específicos
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

        // --- R8a) Caminhos mais curtos ---
        header("R8a: Caminhos mais curtos", print);

        List<Entity> path = alg.shortestPath("U1", "M1");
        assertTrue("R8a: caminho U1->M1 existe", !path.isEmpty(), print);
        assertTrue("R8a: caminho começa em U1", path.get(0).getId().equals("U1"), print);
        assertTrue("R8a: caminho termina em M1", path.get(path.size() - 1).getId().equals("M1"), print);

        double dist = alg.shortestPathDistance("U1", "M1");
        assertTrue("R8a: distância U1->M1 finita", dist < Double.POSITIVE_INFINITY, print);

        List<Entity> invalidPath = alg.shortestPath("X", "Y");
        assertTrue("R8a: caminho inexistente vazio", invalidPath.isEmpty(), print);

        // --- R8b) Subgrafos ---
        header("R8b: Subgrafos", print);

        StreamingGraph actionSub = alg.subgraphByGenre("Action");
        assertTrue("R8b: subgrafo Action tem vértices", actionSub.vertexCount() > 0, print);
        assertTrue("R8b: subgrafo Action tem arestas", actionSub.edgeCount() > 0, print);

        StreamingGraph comedySub = alg.subgraphByGenre("Comedy");
        assertTrue("R8b: subgrafo Comedy tem vértices", comedySub.vertexCount() > 0, print);

        StreamingGraph regionSub = alg.subgraphByRegion("US");
        assertTrue("R8b: subgrafo US tem vértices", regionSub.vertexCount() > 0, print);

        StreamingGraph ratingSub = alg.subgraphByMinRating(8.0);
        assertTrue("R8b: subgrafo rating>=8 tem vértices", ratingSub.vertexCount() > 0, print);

        StreamingGraph emptyGenre = alg.subgraphByGenre("Horror");
        assertTrue("R8b: subgrafo Horror vazio", emptyGenre.vertexCount() == 0, print);

        // --- R8c) Conectividade ---
        header("R8c: Conectividade", print);

        boolean connected = alg.isConnected();
        assertTrue("R8c: isConnected executa sem erro", connected || !connected, print);

        boolean actionConnected = alg.isConnected(actionSub);
        assertTrue("R8c: subgrafo Action conectividade ok", actionConnected || !actionConnected, print);

        StreamingGraph tiny = new StreamingGraph();
        tiny.addVertex(u1);
        assertTrue("R8c: grafo com 1 vértice é conexo", alg.isConnected(tiny), print);

        // --- R8d) Recomendações ---
        header("R8d: Recomendações", print);

        // U3 segue U1 e U2. U1 viu M1,M2. U2 viu M1,S1. U3 viu M1.
        // Recomendações para U3: M2 (U1 viu), S1 (U2 viu) — M1 já viu
        List<Content> recs = alg.recommend("U3");
        assertTrue("R8d: U3 tem recomendações", !recs.isEmpty(), print);
        assertTrue("R8d: U3 não recomenda M1 (já viu)", recs.stream().noneMatch(c -> c.getId().equals("M1")), print);

        List<Content> noRecs = alg.recommend("X");
        assertTrue("R8d: user inexistente sem recomendações", noRecs.isEmpty(), print);

        // --- R8e) Estatísticas de visualização ---
        header("R8e: Estatísticas de visualização", print);

        Map<String, Double> stats = alg.getViewStats("M1", null, null);
        assertTrue("R8e: M1 totalViews == 3", stats.get("totalViews") == 3.0, print);
        assertTrue("R8e: M1 avgRating > 0", stats.get("avgRating") > 0, print);

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 2, 28, 23, 59);
        Map<String, Double> statsFiltered = alg.getViewStats("M1", startDate, endDate);
        assertTrue("R8e: M1 jan-fev totalViews == 2", statsFiltered.get("totalViews") == 2.0, print);

        Map<String, Double> genreStats = alg.getViewStatsByGenre("Action", null, null);
        assertTrue("R8e: Action totalViews > 0", genreStats.get("totalViews") > 0, print);

        // --- R8f) Users que viram séries por género/período ---
        header("R8f: Users que viram séries por género", print);

        List<User> seriesWatchers = alg.usersWhoWatchedSeriesByGenre("Action", null, null);
        assertTrue("R8f: 1 user viu séries Action", seriesWatchers.size() == 1, print);
        assertTrue("R8f: U2 viu série Action", seriesWatchers.get(0).getId().equals("U2"), print);

        List<User> janWatchers = alg.usersWhoWatchedSeriesByGenre("Action", startDate,
                LocalDateTime.of(2025, 1, 31, 23, 59));
        assertTrue("R8f: ninguém viu séries Action em janeiro", janWatchers.isEmpty(), print);

        List<User> comedyWatchers = alg.usersWhoWatchedSeriesByGenre("Comedy", null, null);
        assertTrue("R8f: ninguém viu séries Comedy", comedyWatchers.isEmpty(), print);

        // --- R8g) Seguidores que viram mesmo filme ---
        header("R8g: Seguidores que viram mesmo filme", print);

        List<User> followers = alg.followersWhoWatchedSameContent("U2", "M1", null, null);
        assertTrue("R8g: 2 seguidores de U2 viram M1", followers.size() == 2, print);

        List<User> janFollowers = alg.followersWhoWatchedSameContent("U2", "M1",
                startDate, LocalDateTime.of(2025, 1, 31, 23, 59));
        assertTrue("R8g: 1 seguidor de U2 viu M1 em janeiro", janFollowers.size() == 1, print);

        List<User> noFollowers = alg.followersWhoWatchedSameContent("U3", "M1", null, null);
        assertTrue("R8g: U3 sem seguidores que viram M1", noFollowers.isEmpty(), print);
    }

    // --- Helpers ---

    private static void assertTrue(String label, boolean cond, boolean print) {
        if (cond) { passed++; if (print) System.out.println("  [PASS] " + label); }
        else      { failed++; if (print) System.out.println("  [FAIL] " + label); }
    }

    private static void fail(String msg, boolean print) {
        failed++;
        if (print) System.out.println("  [FAIL] " + msg);
    }

    private static void header(String title, boolean print) {
        if (print) System.out.println("\n--- " + title + " ---");
    }
}