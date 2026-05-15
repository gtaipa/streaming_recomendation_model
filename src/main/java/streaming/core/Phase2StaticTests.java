package streaming.core;

import streaming.db.StreamingDB;
import streaming.db.GraphAlgorithms;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Fase 2 (R7): Testes do grafo pesado direcionado e da API.
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

        if (print) System.out.println("=== Phase2StaticTests (R7 - Grafo) ===");

        try {
            testGraphAndAPI(print);
            testAlgorithmsR8(print);
        } catch (RuntimeException e) {
            fail("Excepção: " + e.getMessage(), print);
        }

        if (print) System.out.println("\nResult: passed=" + passed + " failed=" + failed);
        return failed == 0;
    }

    private static void testGraphAndAPI(boolean print) {
        header("Grafo: vértices, arestas, consultas", print);

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

        // --- Debug: mostrar grafo ---
        if (print) {
            System.out.println("\n  [DEBUG] Estado do grafo:");
            System.out.println(api.toString());
        }
    }

    private static void testAlgorithmsR8(boolean print) {
        header("Algoritmos (R8): caminhos, conectividade, recomendacao", print);

        StreamingDB db = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);

        Genre action = new Genre("G1", "Action");
        Genre drama = new Genre("G2", "Drama");
        db.addGenre(action);
        db.addGenre(drama);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Artist 1", "US", LocalDate.of(1980, 1, 1), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Artist 2", "US", LocalDate.of(1985, 1, 1), "F");
        db.addArtist(a1);
        db.addArtist(a2);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "Movie 1", 2000, action, "US", 120, a1);
        Movie m2 = new Movie("M2", LocalDateTime.now(), "Movie 2", 2001, drama, "US", 110, a1);
        db.addContent(m1);
        db.addContent(m2);

        User u1 = new User("U1", LocalDateTime.now(), "u1", "u1@m.com", "h");
        User u2 = new User("U2", LocalDateTime.now(), "u2", "u2@m.com", "h");
        User u3 = new User("U3", LocalDateTime.now(), "u3", "u3@m.com", "h");
        db.addUser(u1);
        db.addUser(u2);
        db.addUser(u3);

        api.registerAllEntities();

        // Relacoes para caminhos e recomendacoes
        assertTrue("follow U1->U2", api.addFollow("U1", "U2"), print);
        assertTrue("watch U2->M1", api.addWatch("U2", "M1", 100, 8.0), print);
        assertTrue("watch U1->M2", api.addWatch("U1", "M2", 100, 7.0), print);

        // Relacoes Artist -> Content (para shortestPathBetweenArtists)
        assertTrue("artist A1->M1", api.addArtistToContent("A1", "M1"), print);
        assertTrue("artist A2->M1", api.addArtistToContent("A2", "M1"), print);

        GraphAlgorithms alg = new GraphAlgorithms(graph, db);

        // R8a: caminho por follow
        assertTrue("shortestPathByFollow U1->U2 (>=2 nos)",
                alg.shortestPathByFollow("U1", "U2").size() >= 2, print);

        // R8c: conectividade nao deve crashar com grafo vazio
        StreamingGraph empty = new StreamingGraph();
        GraphAlgorithms algEmpty = new GraphAlgorithms(empty, db);
        assertTrue("isConnected(empty) == true", algEmpty.isConnected(), print);

        // R8d: recomendacao deve sugerir M1 a U1 (visto por U2, e U1 ainda nao viu)
        assertTrue("recommend(U1) contem M1",
                alg.recommend("U1").stream().anyMatch(c -> "M1".equals(c.getId())), print);

        // R8b: shortestPathBetweenArtists deve existir via M1
        java.util.List<Entity> artistPath = alg.shortestPathBetweenArtists("A1", "A2");
        assertTrue("shortestPathBetweenArtists A1->A2 nao vazio", !artistPath.isEmpty(), print);
        assertTrue("shortestPathBetweenArtists comeca em A1", "A1".equals(artistPath.get(0).getId()), print);
        assertTrue("shortestPathBetweenArtists termina em A2", "A2".equals(artistPath.get(artistPath.size() - 1).getId()), print);

        // R8b: subgrafo por genero
        StreamingGraph subAction = alg.subgraphByGenre("Action");
        assertTrue("subgraphByGenre(Action) tem pelo menos 1 aresta", subAction.edgeCount() >= 1, print);

        // sanity: conectividade com vertices isolados deve ser false
        StreamingGraph disconnected = new StreamingGraph();
        disconnected.addVertex(u1);
        disconnected.addVertex(u2);
        GraphAlgorithms algDisc = new GraphAlgorithms(disconnected, db);
        assertTrue("isConnected(disconnected) == false", !algDisc.isConnected(), print);
    }

    // --- Helpers (mesmo estilo da Phase1) ---

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
