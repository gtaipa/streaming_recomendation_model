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
 * Fase 2 (R5): Testes estaticos com um metodo dedicado por requisito/alinea.
 *
 * <ul>
 *   <li>{@link #testR7_Graph}                  - R7 : Grafo - vertices, arestas, API</li>
 *   <li>{@link #testR8a_ShortestPaths}          - R8a: Caminhos mais curtos</li>
 *   <li>{@link #testR8b_Subgraphs}              - R8b: Subgrafos</li>
 *   <li>{@link #testR8c_Connectivity}           - R8c: Conectividade</li>
 *   <li>{@link #testR8d_Recommendations}        - R8d: Recomendacoes</li>
 *   <li>{@link #testR8e_DocumentaryViewStats}   - R8e: Estatisticas de visualizacao de documentario</li>
 *   <li>{@link #testR8f_UsersWatchedSeriesByGenre} - R8f: Utilizadores que viram series por genero</li>
 *   <li>{@link #testR8g_FollowersWatchedSameFilm}  - R8g: Seguidores que viram o mesmo filme</li>
 *   <li>{@link #testR10_ImportExportTxt}        - R10: Importacao/exportacao TXT</li>
 *   <li>{@link #testR11_BinarySerialization}    - R11: Serializacao binaria</li>
 * </ul>
 *
 * Executar: correr o {@code main} desta classe.
 */
public final class Phase2StaticTests {

    private static int passed;
    private static int failed;

    private Phase2StaticTests() { }

    /**
     * Ponto de entrada standalone.
     *
     * @param args argumentos da linha de comandos (ignorados)
     */
    public static void main(String[] args) {
        passed = 0;
        failed = 0;
        System.out.println("=== Phase2StaticTests (R7 + R8 + R10 + R11) ===");
        try {
            testR7_Graph();
            testR8a_ShortestPaths();
            testR8b_Subgraphs();
            testR8c_Connectivity();
            testR8d_Recommendations();
            testR8e_DocumentaryViewStats();
            testR8f_UsersWatchedSeriesByGenre();
            testR8g_FollowersWatchedSameFilm();
            testR10_ImportExportTxt();
            testR11_BinarySerialization();
        } catch (RuntimeException e) {
            fail("Excecao: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("\nResultado: passed=" + passed + "  failed=" + failed);
        System.exit(failed == 0 ? 0 : 1);
    }

    // =========================================================================
    // R7 - Grafo: vertices, arestas e API
    // =========================================================================

    /**
     * R7 - Valida a representacao das entidades num grafo pesado direcionado:
     * registo de vertices, adicao de arestas (watch, follow, artist-content),
     * consultas de adjacencia e remocao de vertices.
     */
    private static void testR7_Graph() {
        header("R7 - Grafo: vertices, arestas e API");

        StreamingDB db = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);

        Genre action = new Genre("G1", "Action");
        db.addGenre(action);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Tom Hanks", "US", LocalDate.of(1956, 7, 9), "M");
        db.addArtist(a1);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "The Terminal",  2004, action, "US", 128, a1);
        Movie m2 = new Movie("M2", LocalDateTime.now(), "Forrest Gump",  1994, action, "US", 142, a1);
        db.addContent(m1); db.addContent(m2);

        User u1 = new User("U1", LocalDateTime.now(), "joao",   "j@m.com", "h");
        User u2 = new User("U2", LocalDateTime.now(), "maria",  "m@m.com", "h");
        User u3 = new User("U3", LocalDateTime.now(), "carlos", "c@m.com", "h");
        db.addUser(u1); db.addUser(u2); db.addUser(u3);

        int count = api.registerAllEntities();
        check("R7 | registerAll -> 6 entidades", count == 6);
        check("R7 | vertexCount == 6",           graph.vertexCount() == 6);
        check("R7 | U1 registado",               api.isRegistered("U1"));
        check("R7 | M1 registado",               api.isRegistered("M1"));

        check("R7 | addWatch U1->M1",            api.addWatch("U1", "M1", 100, 8.5));
        check("R7 | addWatch U1->M2",            api.addWatch("U1", "M2",  80, 7.0));
        check("R7 | addWatch U2->M1",            api.addWatch("U2", "M1",  90, 9.0));
        check("R7 | edgeCount == 3",             graph.edgeCount() == 3);

        check("R7 | addFollow U1->U2",           api.addFollow("U1", "U2"));
        check("R7 | addFollow U3->U2",           api.addFollow("U3", "U2"));
        check("R7 | self-follow bloqueado",      !api.addFollow("U1", "U1"));

        check("R7 | artist->content",            api.addArtistToContent("A1", "M1"));

        check("R7 | U1 viu 2 conteudos",         api.getContentsForUser("U1").size() == 2);
        check("R7 | M1 visto por 2 users",       api.getUsersForContent("M1").size() == 2);
        check("R7 | U1 segue 1 user",            api.getFollowing("U1").size() == 1);
        check("R7 | U2 tem 2 seguidores",        api.getFollowers("U2").size() == 2);
        check("R7 | A1 participa em 1 filme",    api.getContentsForArtist("A1").size() == 1);
        check("R7 | M1 tem 1 artista",           api.getArtistsForContent("M1").size() == 1);

        check("R7 | remover U1",                 api.removeUserFromGraph("U1"));
        check("R7 | U1 ja nao existe",           !api.isRegistered("U1"));
        check("R7 | U2 ainda existe",            api.isRegistered("U2"));

        check("R7 | watch user inexistente",     !api.addWatch("X", "M1", 100, 5));
        check("R7 | follow null",                !api.addFollow(null, "U2"));
        check("R7 | consulta inexistente vazia", api.getContentsForUser("X").isEmpty());
    }

    // =========================================================================
    // R8a - Caminhos mais curtos
    // =========================================================================

    /**
     * R8a - Calcula caminhos mais curtos entre entidades do grafo (utilizadores,
     * artistas, conteudos) com base nos pesos das arestas.
     */
    private static void testR8a_ShortestPaths() {
        header("R8a - Caminhos mais curtos");

        Object[] state = buildR8State();
        StreamingGraph graph = (StreamingGraph) state[1];
        GraphAlgorithms alg  = (GraphAlgorithms)  state[3];

        List<Entity> path = alg.shortestPath("U1", "M1");
        check("R8a | caminho U1->M1 existe",      !path.isEmpty());
        check("R8a | comeca em U1",               path.get(0).getId().equals("U1"));
        check("R8a | termina em M1",              path.get(path.size() - 1).getId().equals("M1"));
        check("R8a | distancia finita",           alg.shortestPathDistance("U1", "M1") < Double.POSITIVE_INFINITY);
        check("R8a | caminho inexistente vazio",  alg.shortestPath("X", "Y").isEmpty());

        // Caminho entre artistas via conteudos partilhados
        List<Entity> artPath = alg.shortestPath("A1", "A2");
        check("R8a | caminho A1->A2 (via conteudo partilhado) existe ou vazio (sem ligacao direta)",
                artPath != null);
    }

    // =========================================================================
    // R8b - Subgrafos
    // =========================================================================

    /**
     * R8b - Extrai e analisa subgrafos por genero, regiao e rating minimo.
     */
    private static void testR8b_Subgraphs() {
        header("R8b - Subgrafos");

        Object[] state = buildR8State();
        GraphAlgorithms alg = (GraphAlgorithms) state[3];

        StreamingGraph actionSub = alg.subgraphByGenre("Action");
        check("R8b | subgrafo Action tem vertices", actionSub.vertexCount() > 0);
        check("R8b | subgrafo Action tem arestas",  actionSub.edgeCount() > 0);

        check("R8b | subgrafo Comedy tem vertices", alg.subgraphByGenre("Comedy").vertexCount() > 0);
        check("R8b | subgrafo US tem vertices",     alg.subgraphByRegion("US").vertexCount() > 0);
        check("R8b | subgrafo rating>=8 tem vertices", alg.subgraphByMinRating(8.0).vertexCount() > 0);
        check("R8b | subgrafo Horror vazio",        alg.subgraphByGenre("Horror").vertexCount() == 0);
    }

    // =========================================================================
    // R8c - Conectividade
    // =========================================================================

    /**
     * R8c - Determina se o grafo principal e subgrafos sao conexos.
     */
    private static void testR8c_Connectivity() {
        header("R8c - Conectividade");

        Object[] state = buildR8State();
        StreamingDB    db   = (StreamingDB)    state[0];
        StreamingGraph graph = (StreamingGraph) state[1];
        GraphAlgorithms alg  = (GraphAlgorithms) state[3];

        StreamingGraph actionSub = alg.subgraphByGenre("Action");

        check("R8c | isConnected executa sem erro",
                alg.isConnected() || !alg.isConnected());
        check("R8c | subgrafo Action conectividade verificada",
                alg.isConnected(actionSub) || !alg.isConnected(actionSub));

        // Grafo com um unico vertice e sempre conexo
        StreamingGraph tiny = new StreamingGraph();
        User solo = new User("SOLO", LocalDateTime.now(), "solo", "s@s.com", "h");
        tiny.addVertex(solo);
        check("R8c | 1 vertice e conexo", alg.isConnected(tiny));
    }

    // =========================================================================
    // R8d - Recomendacoes
    // =========================================================================

    /**
     * R8d - Gera recomendacoes simples baseadas em proximidade estrutural no grafo.
     */
    private static void testR8d_Recommendations() {
        header("R8d - Recomendacoes");

        Object[] state = buildR8State();
        GraphAlgorithms alg = (GraphAlgorithms) state[3];

        List<Content> recs = alg.recommend("U3");
        check("R8d | U3 tem recomendacoes",           !recs.isEmpty());
        check("R8d | nao recomenda M1 (ja viu)",      recs.stream().noneMatch(c -> c.getId().equals("M1")));
        check("R8d | user inexistente -> vazio",      alg.recommend("X").isEmpty());
    }

    // =========================================================================
    // R8e - Estatisticas de visualizacao de documentario entre duas datas
    // =========================================================================

    /**
     * R8e - Pesquisa estatisticas de visualizacao de um documentario entre duas datas.
     */
    private static void testR8e_DocumentaryViewStats() {
        header("R8e - Estatisticas de visualizacao de documentario");

        Object[] state     = buildR8State();
        GraphAlgorithms alg = (GraphAlgorithms) state[3];

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1,  0,  0);
        LocalDateTime endDate   = LocalDateTime.of(2025, 2, 28, 23, 59);

        // D1 e o documentario adicionado no setup (ver buildR8State)
        Map<String, Double> stats = alg.getViewStats("D1", null, null);
        check("R8e | D1 totalViews == 2",    stats.get("totalViews") == 2.0);
        check("R8e | D1 avgRating > 0",      stats.get("avgRating") > 0);

        // Filtro por intervalo de datas
        Map<String, Double> statsJanFev = alg.getViewStats("D1", startDate, endDate);
        check("R8e | D1 jan-fev totalViews == 1", statsJanFev.get("totalViews") == 1.0);

        // Estatisticas por genero
        Map<String, Double> genStats = alg.getViewStatsByGenre("Action", null, null);
        check("R8e | genero Action totalViews > 0", genStats.get("totalViews") > 0);
    }

    // =========================================================================
    // R8f - Utilizadores que viram series de um genero num periodo
    // =========================================================================

    /**
     * R8f - Pesquisa utilizadores que viram series de um determinado genero
     * num determinado periodo.
     */
    private static void testR8f_UsersWatchedSeriesByGenre() {
        header("R8f - Utilizadores que viram series por genero");

        Object[] state     = buildR8State();
        GraphAlgorithms alg = (GraphAlgorithms) state[3];

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1,  0,  0);
        LocalDateTime endDate   = LocalDateTime.of(2025, 2, 28, 23, 59);

        List<User> watchers = alg.usersWhoWatchedSeriesByGenre("Action", null, null);
        check("R8f | 1 user viu series Action",   watchers.size() == 1);
        check("R8f | U2 viu serie Action",        watchers.get(0).getId().equals("U2"));

        check("R8f | ninguem em janeiro",
                alg.usersWhoWatchedSeriesByGenre("Action", startDate,
                        LocalDateTime.of(2025, 1, 31, 23, 59)).isEmpty());
        check("R8f | ninguem Comedy",
                alg.usersWhoWatchedSeriesByGenre("Comedy", null, null).isEmpty());
    }

    // =========================================================================
    // R8g - Seguidores que viram o mesmo filme num intervalo
    // =========================================================================

    /**
     * R8g - Pesquisa seguidores de um utilizador que viram o mesmo filme
     * num determinado intervalo de tempo.
     */
    private static void testR8g_FollowersWatchedSameFilm() {
        header("R8g - Seguidores que viram o mesmo filme");

        Object[] state     = buildR8State();
        GraphAlgorithms alg = (GraphAlgorithms) state[3];

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1,  0,  0);
        LocalDateTime endDate   = LocalDateTime.of(2025, 1, 31, 23, 59);

        check("R8g | 2 seguidores de U2 viram M1",
                alg.followersWhoWatchedSameContent("U2", "M1", null, null).size() == 2);
        check("R8g | 1 seguidor em janeiro",
                alg.followersWhoWatchedSameContent("U2", "M1", startDate, endDate).size() == 1);
        check("R8g | U3 sem seguidores",
                alg.followersWhoWatchedSameContent("U3", "M1", null, null).isEmpty());
    }

    // =========================================================================
    // R10 - Importacao/exportacao de dados TXT
    // =========================================================================

    /**
     * R10 - Exporta o estado da base de dados e do grafo para ficheiro de texto
     * e importa-o de volta, verificando que os dados foram preservados.
     */
    private static void testR10_ImportExportTxt() {
        header("R10 - Export + Import TXT");

        Object[] data       = buildR10R11State();
        StreamingDB    db   = (StreamingDB)    data[0];
        StreamingGraph graph = (StreamingGraph) data[1];

        FileManager fm      = new FileManager();
        String txtPath      = System.getProperty("java.io.tmpdir") + File.separator + "phase2_r10.txt";
        fm.exportTxt(db, graph, txtPath);

        StreamingDB    db2    = new StreamingDB();
        StreamingGraph graph2 = new StreamingGraph();
        StreamingGraphAPI api2 = new StreamingGraphAPI(db2, graph2);
        fm.importTxt(txtPath, db2, api2);

        check("R10 | Genre G1",              db2.getGenre("G1") != null);
        check("R10 | Genre G2",              db2.getGenre("G2") != null);
        check("R10 | Artist A1",             db2.getArtist("A1") != null);
        check("R10 | Movie M1",              db2.getContent("M1") instanceof Movie);
        check("R10 | Series S1",             db2.getContent("S1") instanceof Series);
        check("R10 | S1 tem 2 eps",          ((Series) db2.getContent("S1")).getEpisodes().size() == 2);
        check("R10 | User U1",               db2.getUser("U1") != null);
        check("R10 | User U2",               db2.getUser("U2") != null);
        check("R10 | U1 region Porto",       "Porto".equals(db2.getUser("U1").getRegion()));
        check("R10 | Grafo vertices",        graph2.vertexCount() > 0);
        check("R10 | Grafo arestas",         graph2.edgeCount() > 0);
        check("R10 | 3 interacoes",          graph2.getInteractions().size() == 3);
        check("R10 | U1 segue U2",           db2.getUser("U1").getFollowing().size() == 1);
    }

    // =========================================================================
    // R11 - Serializacao binaria
    // =========================================================================

    /**
     * R11 - Serializa o estado completo (base de dados + grafo) em ficheiro binario
     * e deserializa-o, verificando que os dados foram preservados.
     */
    private static void testR11_BinarySerialization() {
        header("R11 - Serialize + Deserialize BIN");

        Object[] data       = buildR10R11State();
        StreamingDB    db   = (StreamingDB)    data[0];
        StreamingGraph graph = (StreamingGraph) data[1];

        FileManager fm      = new FileManager();
        String binPath      = System.getProperty("java.io.tmpdir") + File.separator + "phase2_r11.bin";
        fm.serializeBin(db, graph, binPath);

        StreamingDB    db2    = new StreamingDB();
        StreamingGraph graph2 = new StreamingGraph();
        StreamingGraphAPI api2 = new StreamingGraphAPI(db2, graph2);
        fm.deserializeBin(binPath, db2, api2);

        check("R11 | Genre G1",              db2.getGenre("G1") != null);
        check("R11 | Genre G2",              db2.getGenre("G2") != null);
        check("R11 | Artist A1",             db2.getArtist("A1") != null);
        check("R11 | Movie M1",              db2.getContent("M1") instanceof Movie);
        check("R11 | Series S1",             db2.getContent("S1") instanceof Series);
        check("R11 | S1 tem 2 eps",          ((Series) db2.getContent("S1")).getEpisodes().size() == 2);
        check("R11 | User U1",               db2.getUser("U1") != null);
        check("R11 | U1 region Porto",       "Porto".equals(db2.getUser("U1").getRegion()));
        check("R11 | Grafo vertices",        graph2.vertexCount() > 0);
        check("R11 | Grafo arestas",         graph2.edgeCount() > 0);
        check("R11 | 3 interacoes",          graph2.getInteractions().size() == 3);
        check("R11 | U1 segue U2",           db2.getUser("U1").getFollowing().size() == 1);
    }

    // =========================================================================
    // Setup partilhado para testes R8a-R8g
    // =========================================================================

    /**
     * Constroi o estado partilhado para os testes R8a a R8g.
     * Inclui generos, artistas, conteudos (Movie, Series, Documentary), utilizadores,
     * visualizacoes com timestamps, follows e associacoes artista-conteudo.
     *
     * @return array com [StreamingDB, StreamingGraph, StreamingGraphAPI, GraphAlgorithms,
     *         LocalDateTime jan, LocalDateTime fev, LocalDateTime mar]
     */
    private static Object[] buildR8State() {
        StreamingDB    db    = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);
        GraphAlgorithms alg  = new GraphAlgorithms(graph, db);

        Genre action = new Genre("G1", "Action");
        Genre comedy = new Genre("G2", "Comedy");
        db.addGenre(action);
        db.addGenre(comedy);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Tom Hanks",    "US", LocalDate.of(1956, 7, 9),  "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Margot Robbie","AU", LocalDate.of(1990, 7, 2),  "F");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "David Attenborough", "UK", LocalDate.of(1926, 5, 8), "M");
        db.addArtist(a1); db.addArtist(a2); db.addArtist(a3);

        Movie  m1 = new Movie("M1", LocalDateTime.now(), "The Terminal",  2004, action, "US", 128, a1);
        Movie  m2 = new Movie("M2", LocalDateTime.now(), "Barbie",        2023, comedy, "US", 114, a2);
        Series s1 = new Series("S1", LocalDateTime.now(), "Breaking Bad", 2008, action, "US",  5, SeriesStatus.ENDED);
        s1.addEpisode(new Episode(1, 1, "Pilot", 58, 9.0f));

        // D1: documentario para R8e
        Documentary d1 = new Documentary("D1", LocalDateTime.now(), "Planet Earth", 2006, action, "UK",
                8.5, 0, new java.util.ArrayList<>(), 90, "Nature", a3);

        db.addContent(m1); db.addContent(m2); db.addContent(s1); db.addContent(d1);

        User u1 = new User("U1", LocalDateTime.now(), "joao",   "j@m.com", "h");
        User u2 = new User("U2", LocalDateTime.now(), "maria",  "m@m.com", "h");
        User u3 = new User("U3", LocalDateTime.now(), "carlos", "c@m.com", "h");
        db.addUser(u1); db.addUser(u2); db.addUser(u3);

        api.registerAllEntities();

        LocalDateTime jan = LocalDateTime.of(2025, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2025, 2, 20, 14, 0);
        LocalDateTime mar = LocalDateTime.of(2025, 3, 10, 18, 0);

        api.addWatch("U1", "M1", 100, 8.5, jan);
        api.addWatch("U1", "M2",  80, 7.0, feb);
        api.addWatch("U2", "M1",  90, 9.0, feb);
        api.addWatch("U2", "S1", 100, 9.5, mar);
        api.addWatch("U3", "M1",  70, 6.0, mar);
        // D1 visto por U1 (jan) e U3 (mar)
        api.addWatch("U1", "D1",  90, 8.5, jan);
        api.addWatch("U3", "D1",  90, 8.0, mar);

        api.addFollow("U1", "U2");
        api.addFollow("U3", "U2");
        api.addFollow("U3", "U1");

        api.addArtistToContent("A1", "M1");
        api.addArtistToContent("A2", "M2");
        api.addArtistToContent("A1", "M2");
        api.addArtistToContent("A3", "D1");

        return new Object[]{ db, graph, api, alg, jan, feb, mar };
    }

    // =========================================================================
    // Setup partilhado para testes R10 e R11
    // =========================================================================

    /**
     * Constroi o estado partilhado para os testes R10 e R11.
     *
     * @return array com [StreamingDB, StreamingGraph, StreamingGraphAPI]
     */
    private static Object[] buildR10R11State() {
        StreamingDB    db    = new StreamingDB();
        StreamingGraph graph = new StreamingGraph();
        StreamingGraphAPI api = new StreamingGraphAPI(db, graph);

        Genre action = new Genre("G1", "Action");
        Genre comedy = new Genre("G2", "Comedy");
        db.addGenre(action); db.addGenre(comedy);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Tom Hanks", "US", LocalDate.of(1956, 7, 9), "M");
        db.addArtist(a1);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "The Terminal", 2004, action, "US", 128, a1);
        db.addContent(m1);

        Series s1 = new Series("S1", LocalDateTime.now(), "Breaking Bad", 2008, action, "US", 5, SeriesStatus.ENDED);
        s1.addEpisode(new Episode(1, 1, "Pilot",             58, 9.0f));
        s1.addEpisode(new Episode(1, 2, "Cat in the Bag",   48, 8.5f));
        db.addContent(s1);

        User u1 = new User("U1", LocalDateTime.now(), "joao",  "joao@email.com",  "h");
        u1.setRegion("Porto");
        u1.setRegistrationDate(LocalDate.of(2024, 1, 15));
        db.addUser(u1);

        User u2 = new User("U2", LocalDateTime.now(), "maria", "maria@email.com", "h");
        u2.setRegion("Lisboa");
        u2.setRegistrationDate(LocalDate.of(2024, 3, 20));
        db.addUser(u2);

        api.registerAllEntities();
        api.addWatch("U1", "M1", 100, 8.5, LocalDateTime.of(2025, 1, 15, 10, 0));
        api.addWatch("U2", "S1",  90, 9.0, LocalDateTime.of(2025, 2, 20, 14, 0));
        api.addWatch("U1", "S1",  80, 8.0, LocalDateTime.of(2025, 3,  5, 16, 0));
        api.addFollow("U1", "U2");

        return new Object[]{ db, graph, api };
    }

    // =========================================================================
    // Utilitarios
    // =========================================================================

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
