package streaming.core;

import streaming.db.StreamingDB;
import streaming.model.Artist;
import streaming.model.Content;
import streaming.model.Documentary;
import streaming.model.Genre;
import streaming.model.Movie;
import streaming.model.Series;
import streaming.model.SeriesStatus;
import streaming.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fase 1 (R5): Testes estaticos com um metodo dedicado por requisito/alinea,
 * com output na consola.
 *
 * <ul>
 *   <li>{@link #testR2_SymbolTableCrud}              - R2 : CRUD nas Symbol Tables</li>
 *   <li>{@link #testR3a_UsersByRegionDatePreference} - R3a: Utilizadores por regiao/data/preferencia</li>
 *   <li>{@link #testR3b_UsersBySubstringCombined}    - R3b: Utilizadores por substring e combinacoes</li>
 *   <li>{@link #testR3c_ArtistsByNatGenderAge}       - R3c: Artistas por nacionalidade/genero/idade</li>
 *   <li>{@link #testR3d_ArtistsBySubstringCombined}  - R3d: Artistas por substring e combinacoes</li>
 *   <li>{@link #testR3e_ContentsByTypeGenreDate}     - R3e: Conteudos por tipo/genero/data</li>
 *   <li>{@link #testR3f_ContentsByTitleCombined}     - R3f: Conteudos por substring no titulo e combinacoes</li>
 *   <li>{@link #testR3g_ContentsByDurationRatingViews} - R3g: Conteudos por duracao/rating/visualizacoes</li>
 *   <li>{@link #testR4_Consistency}                  - R4 : Consistencia, arquivo e integridade</li>
 * </ul>
 *
 * Executar:
 * <ul>
 *   <li>IDE: correr o {@code main} desta classe.</li>
 *   <li>Maven: {@code mvn exec:java -Dexec.mainClass=streaming.core.Phase1StaticTests}</li>
 * </ul>
 */
public final class Phase1StaticTests {

    private static int passed;
    private static int failed;

    private Phase1StaticTests() { }

    /**
     * Ponto de entrada standalone.
     *
     * @param args argumentos da linha de comandos (ignorados)
     */
    public static void main(String[] args) {
        boolean ok = runAll(true);
        System.exit(ok ? 0 : 1);
    }

    /**
     * Corre todos os testes e devolve {@code true} se todos passaram.
     *
     * @param print se {@code true}, imprime cada assert na consola
     * @return {@code true} se nenhum teste falhou
     */
    public static boolean runAll(boolean print) {
        passed = 0;
        failed = 0;

        if (print) System.out.println("=== Phase1StaticTests (R5) ===");

        try {
            testR2_SymbolTableCrud(print);
            testR3a_UsersByRegionDatePreference(print);
            testR3b_UsersBySubstringCombined(print);
            testR3c_ArtistsByNatGenderAge(print);
            testR3d_ArtistsBySubstringCombined(print);
            testR3e_ContentsByTypeGenreDate(print);
            testR3f_ContentsByTitleCombined(print);
            testR3g_ContentsByDurationRatingViews(print);
            testR4_Consistency(print);
        } catch (RuntimeException e) {
            fail("Excecao: " + e.getClass().getSimpleName() + " - " + e.getMessage(), print);
        }

        if (print) {
            System.out.println();
            System.out.println("Resultado: passed=" + passed + "  failed=" + failed);
        }
        return failed == 0;
    }

    // =========================================================================
    // R2 - Symbol Table: insert / remove / edit / list para todas as entidades
    // =========================================================================

    /**
     * R2 - Valida as operacoes CRUD basicas nas Symbol Tables para Utilizadores,
     * Artistas, Generos e Conteudos (insert, remove, edit, list).
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR2_SymbolTableCrud(boolean print) {
        header("R2 - Symbol Table: insert / remove / edit / list", print);

        StreamingDB db = new StreamingDB();

        // --- Utilizadores ---
        User u1 = new User("U1", LocalDateTime.now(), "alice", "alice@ex.com", "h1");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        User u2 = new User("U2", LocalDateTime.now(), "bob", "bob@ex.com", "h2");
        u2.setRegion("Porto");
        u2.setRegistrationDate(LocalDate.of(2025, 3, 20));

        db.addUser(u1);
        db.addUser(u2);

        assertTrue("R2 | addUser: size==2",        db.listUsers().size() == 2, print);
        assertTrue("R2 | getUser U1 != null",       db.getUser("U1") != null, print);
        assertTrue("R2 | getUser U2 != null",       db.getUser("U2") != null, print);

        u1.setEmail("alice_new@ex.com");
        assertTrue("R2 | updateUser: devolve true",            db.updateUser(u1), print);
        assertTrue("R2 | updateUser: email atualizado",
                "alice_new@ex.com".equals(db.getUser("U1").getEmail()), print);

        assertTrue("R2 | removeUser: devolve true",            db.removeUser("U2", false), print);
        assertTrue("R2 | removeUser: getUser U2 == null",      db.getUser("U2") == null, print);
        assertTrue("R2 | removeUser: size==1",                 db.listUsers().size() == 1, print);

        // --- Artistas ---
        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan",      "UK", LocalDate.of(1970, 7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10, 3), "M");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "Nair",       "IN", LocalDate.of(1957, 2, 18), "F");

        db.addArtist(a1); db.addArtist(a2); db.addArtist(a3);

        assertTrue("R2 | addArtist: size==3",           db.listArtists().size() == 3, print);
        assertTrue("R2 | getArtist A2 != null",         db.getArtist("A2") != null, print);
        assertTrue("R2 | removeArtist: devolve true",   db.removeArtist("A3"), print);
        assertTrue("R2 | removeArtist: A3 == null",     db.getArtist("A3") == null, print);
        assertTrue("R2 | removeArtist: size==2",        db.listArtists().size() == 2, print);

        // --- Generos ---
        Genre g1 = new Genre("G1", "Acao");
        Genre g2 = new Genre("G2", "Drama");

        db.addGenre(g1); db.addGenre(g2);

        assertTrue("R2 | addGenre: size==2",                   db.listGenres().size() == 2, print);
        assertTrue("R2 | getGenre G1 != null",                 db.getGenre("G1") != null, print);
        assertTrue("R2 | updateGenreName: devolve true",       db.updateGenreName("G2", "Comedia"), print);
        assertTrue("R2 | updateGenreName: nome atualizado",
                "Comedia".equals(db.getGenre("G2").getName()), print);
        assertTrue("R2 | removeGenre (sem conteudos): true",   db.removeGenre("G2"), print);
        assertTrue("R2 | removeGenre: G2 == null",             db.getGenre("G2") == null, print);

        // --- Conteudos ---
        Movie  m1 = new Movie("C1",  LocalDateTime.now(), "Filme Alpha", 2022, g1, "PT", 110, a1);
        Series s1 = new Series("C2", LocalDateTime.now(), "Serie Beta",  2021, g1, "PT",   3, SeriesStatus.ONGOING);

        db.addContent(m1); db.addContent(s1);

        assertTrue("R2 | addContent: size==2",                  db.listContents().size() == 2, print);
        assertTrue("R2 | getContent C1 != null",                db.getContent("C1") != null, print);

        db.removeContent("C2");
        assertTrue("R2 | removeContent: C2 == null",            db.getContent("C2") == null, print);
        assertTrue("R2 | removeContentIfExists C1: true",       db.removeContentIfExists("C1"), print);
        assertTrue("R2 | removeContentIfExists C1 repetido: false", !db.removeContentIfExists("C1"), print);
    }

    // =========================================================================
    // R3a - Utilizadores por regiao, data de registo e preferencias
    // =========================================================================

    /**
     * R3a - Pesquisas de utilizadores por regiao e/ou data de registo e/ou preferencias
     * de conteudos, usando os indices BST respetivos.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3a_UsersByRegionDatePreference(boolean print) {
        header("R3a - Utilizadores por regiao, data de registo e preferencias", print);

        StreamingDB db = new StreamingDB();

        Genre gAcao  = new Genre("G1", "Acao");
        Genre gDrama = new Genre("G2", "Drama");
        db.addGenre(gAcao);
        db.addGenre(gDrama);

        User u1 = new User("U1", LocalDateTime.now(), "alice",   "a@ex.com", "h1");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        u1.getPreferences().add(gAcao);

        User u2 = new User("U2", LocalDateTime.now(), "bob",     "b@ex.com", "h2");
        u2.setRegion("Porto");
        u2.setRegistrationDate(LocalDate.of(2025, 3, 20));
        u2.getPreferences().add(gDrama);

        User u3 = new User("U3", LocalDateTime.now(), "charlie", "c@ex.com", "h3");
        u3.setRegion("Lisboa");
        u3.setRegistrationDate(LocalDate.of(2024, 11, 5));
        u3.getPreferences().add(gAcao);

        db.addUser(u1); db.addUser(u2); db.addUser(u3);

        // Por regiao
        List<User> lisboa = db.searchUsersByRegion("Lisboa");
        assertTrue("R3a | regiao Lisboa tem U1",       containsUserId(lisboa, "U1"), print);
        assertTrue("R3a | regiao Lisboa tem U3",       containsUserId(lisboa, "U3"), print);
        assertTrue("R3a | regiao Lisboa nao tem U2",   !containsUserId(lisboa, "U2"), print);
        assertTrue("R3a | regiao Porto tem U2",        containsUserId(db.searchUsersByRegion("Porto"), "U2"), print);

        // Por data de registo (range BST)
        List<User> jan2025 = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        assertTrue("R3a | registo jan2025 tem U1",       containsUserId(jan2025, "U1"), print);
        assertTrue("R3a | registo jan2025 nao tem U2",   !containsUserId(jan2025, "U2"), print);

        List<User> y2024 = db.searchUsersRegisteredBetween(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertTrue("R3a | registo 2024 tem U3", containsUserId(y2024, "U3"), print);

        // Por preferencia de genero
        List<User> prefAcao = db.searchUsersByPreference("Acao");
        assertTrue("R3a | preferencia Acao tem U1",      containsUserId(prefAcao, "U1"), print);
        assertTrue("R3a | preferencia Acao tem U3",      containsUserId(prefAcao, "U3"), print);
        assertTrue("R3a | preferencia Acao nao tem U2",  !containsUserId(prefAcao, "U2"), print);

        List<User> prefDrama = db.searchUsersByPreference("Drama");
        assertTrue("R3a | preferencia Drama tem U2", containsUserId(prefDrama, "U2"), print);
    }

    // =========================================================================
    // R3b - Utilizadores por substring no nome, combinado com regiao/data/preferencia
    // =========================================================================

    /**
     * R3b - Pesquisas de utilizadores por substring no nome, isolada e combinada
     * com regiao, data de registo e preferencias.
     * Inclui verificacao de que o indice BST e atualizado apos {@code updateUser}.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3b_UsersBySubstringCombined(boolean print) {
        header("R3b - Utilizadores por substring no nome (e combinacoes)", print);

        StreamingDB db = new StreamingDB();

        Genre gAcao = new Genre("G1", "Acao");
        db.addGenre(gAcao);

        User u1 = new User("U1", LocalDateTime.now(), "alice",  "a@ex.com", "h1");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        u1.getPreferences().add(gAcao);

        User u2 = new User("U2", LocalDateTime.now(), "bob",    "b@ex.com", "h2");
        u2.setRegion("Porto");
        u2.setRegistrationDate(LocalDate.of(2025, 3, 20));

        User u3 = new User("U3", LocalDateTime.now(), "albert", "c@ex.com", "h3");
        u3.setRegion("Lisboa");
        u3.setRegistrationDate(LocalDate.of(2024, 11, 5));

        db.addUser(u1); db.addUser(u2); db.addUser(u3);

        // Substring isolada
        List<User> subAl = db.searchUsersByUsernameSubstring("al");
        assertTrue("R3b | substring 'al' tem U1 (alice)",  containsUserId(subAl, "U1"), print);
        assertTrue("R3b | substring 'al' tem U3 (albert)", containsUserId(subAl, "U3"), print);
        assertTrue("R3b | substring 'al' nao tem U2",      !containsUserId(subAl, "U2"), print);

        List<User> subBo = db.searchUsersByUsernameSubstring("bo");
        assertTrue("R3b | substring 'bo' tem U2",          containsUserId(subBo, "U2"), print);

        // Substring AND regiao
        List<User> lisbUsers = db.searchUsersByRegion("Lisboa");
        long alLisbCount = subAl.stream().filter(u -> containsUserId(lisbUsers, u.getId())).count();
        assertTrue("R3b | substring 'al' AND Lisboa: U1 e U3 (count==2)", alLisbCount == 2, print);

        // Substring AND data de registo
        List<User> jan2025 = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        long alJanCount = subAl.stream().filter(u -> containsUserId(jan2025, u.getId())).count();
        assertTrue("R3b | substring 'al' AND jan2025: so U1 (count==1)", alJanCount == 1, print);

        // Substring AND preferencia
        List<User> prefAcao = db.searchUsersByPreference("Acao");
        long alAcaoCount = subAl.stream().filter(u -> containsUserId(prefAcao, u.getId())).count();
        assertTrue("R3b | substring 'al' AND pref Acao: so U1 (count==1)", alAcaoCount == 1, print);

        // BST reindexado apos updateUser
        u1.setRegion("Porto");
        db.updateUser(u1);
        assertTrue("R3b | updateUser: Lisboa ja nao tem U1",
                !containsUserId(db.searchUsersByRegion("Lisboa"), "U1"), print);
        assertTrue("R3b | updateUser: Porto agora tem U1",
                containsUserId(db.searchUsersByRegion("Porto"), "U1"), print);
    }

    // =========================================================================
    // R3c - Artistas por nacionalidade, genero e intervalo de idade/nascimento
    // =========================================================================

    /**
     * R3c - Pesquisas de artistas por nacionalidade, genero e intervalo de datas
     * de nascimento (via BST), isoladas.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3c_ArtistsByNatGenderAge(boolean print) {
        header("R3c - Artistas por nacionalidade, genero e intervalo de nascimento", print);

        StreamingDB db = new StreamingDB();

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan",      "UK", LocalDate.of(1970,  7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10,  3), "M");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "Nair",       "IN", LocalDate.of(1957,  2, 18), "F");
        Artist a4 = new Artist("A4", LocalDateTime.now(), "Bigelow",    "US", LocalDate.of(1951, 11, 27), "F");

        db.addArtist(a1); db.addArtist(a2); db.addArtist(a3); db.addArtist(a4);

        // Por nacionalidade (BST)
        assertTrue("R3c | UK tem A1",      containsArtistId(db.searchArtistsByNationality("UK"), "A1"), print);
        assertTrue("R3c | CA tem A2",      containsArtistId(db.searchArtistsByNationality("CA"), "A2"), print);
        assertTrue("R3c | UK nao tem A2",  !containsArtistId(db.searchArtistsByNationality("UK"), "A2"), print);

        // Por genero
        List<Artist> masc = db.searchArtistsByGender("M");
        assertTrue("R3c | gender M tem A1",      containsArtistId(masc, "A1"), print);
        assertTrue("R3c | gender M tem A2",      containsArtistId(masc, "A2"), print);
        assertTrue("R3c | gender M nao tem A3",  !containsArtistId(masc, "A3"), print);

        List<Artist> fem = db.searchArtistsByGender("F");
        assertTrue("R3c | gender F tem A3",      containsArtistId(fem, "A3"), print);
        assertTrue("R3c | gender F tem A4",      containsArtistId(fem, "A4"), print);
        assertTrue("R3c | gender F nao tem A1",  !containsArtistId(fem, "A1"), print);

        // Por intervalo de nascimento (BST de datas)
        List<Artist> born60s70s = db.searchArtistsBornBetween(
                LocalDate.of(1960, 1, 1), LocalDate.of(1979, 12, 31));
        assertTrue("R3c | nascidos 1960-1979 tem A1",          containsArtistId(born60s70s, "A1"), print);
        assertTrue("R3c | nascidos 1960-1979 tem A2",          containsArtistId(born60s70s, "A2"), print);
        assertTrue("R3c | nascidos 1960-1979 nao tem A3",      !containsArtistId(born60s70s, "A3"), print);
        assertTrue("R3c | nascidos 1960-1979 nao tem A4",      !containsArtistId(born60s70s, "A4"), print);

        List<Artist> born50s = db.searchArtistsBornBetween(
                LocalDate.of(1950, 1, 1), LocalDate.of(1959, 12, 31));
        assertTrue("R3c | nascidos 1950-1959 tem A3", containsArtistId(born50s, "A3"), print);
        assertTrue("R3c | nascidos 1950-1959 tem A4", containsArtistId(born50s, "A4"), print);
    }

    // =========================================================================
    // R3d - Artistas por substring no nome, combinado com nacionalidade/genero/idade
    // =========================================================================

    /**
     * R3d - Pesquisas de artistas por substring no nome, isolada e combinada
     * com nacionalidade, genero e intervalo de datas de nascimento.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3d_ArtistsBySubstringCombined(boolean print) {
        header("R3d - Artistas por substring no nome (e combinacoes)", print);

        StreamingDB db = new StreamingDB();

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan",      "UK", LocalDate.of(1970,  7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10,  3), "M");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "Nair",       "IN", LocalDate.of(1957,  2, 18), "F");

        db.addArtist(a1); db.addArtist(a2); db.addArtist(a3);

        // Substring isolada
        List<Artist> subN = db.searchArtistsByNameSubstring("n");
        assertTrue("R3d | substring 'n' tem A1 (Nolan)", containsArtistId(subN, "A1"), print);
        assertTrue("R3d | substring 'n' tem A3 (Nair)",  containsArtistId(subN, "A3"), print);

        List<Artist> subVil = db.searchArtistsByNameSubstring("vil");
        assertTrue("R3d | substring 'vil' tem A2",       containsArtistId(subVil, "A2"), print);
        assertTrue("R3d | substring 'vil' nao tem A1",   !containsArtistId(subVil, "A1"), print);

        // Substring AND nacionalidade
        List<Artist> natCA = db.searchArtistsByNationality("CA");
        long vilCACount = subVil.stream().filter(a -> containsArtistId(natCA, a.getId())).count();
        assertTrue("R3d | substring 'vil' AND CA: so A2 (count==1)", vilCACount == 1, print);

        // Substring AND genero
        List<Artist> genF = db.searchArtistsByGender("F");
        long nFCount = subN.stream().filter(a -> containsArtistId(genF, a.getId())).count();
        assertTrue("R3d | substring 'n' AND gender F: so A3 (count==1)", nFCount == 1, print);

        // Substring AND intervalo de nascimento ("nol" so apanha A1=Nolan; Villeneuve nao contem "nol")
        List<Artist> subNol = db.searchArtistsByNameSubstring("nol");
        List<Artist> born60s70s = db.searchArtistsBornBetween(
                LocalDate.of(1960, 1, 1), LocalDate.of(1979, 12, 31));
        long nolBornCount = subNol.stream().filter(a -> containsArtistId(born60s70s, a.getId())).count();
        assertTrue("R3d | substring 'nol' AND 1960-1979: so A1 (Nolan 1970)", nolBornCount == 1, print);
    }

    // =========================================================================
    // R3e - Conteudos por tipo, genero e data de publicacao
    // =========================================================================

    /**
     * R3e - Pesquisas de conteudos por tipo, genero e data de publicacao,
     * incluindo verificacao de que o indice BST e atualizado apos {@code updateGenreName}.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3e_ContentsByTypeGenreDate(boolean print) {
        header("R3e - Conteudos por tipo, genero e data de publicacao", print);

        StreamingDB db = new StreamingDB();

        Genre gAcao  = new Genre("G1", "Acao");
        Genre gDrama = new Genre("G2", "Drama");
        db.addGenre(gAcao); db.addGenre(gDrama);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Dir", "PT", LocalDate.of(1970, 1, 1), "M");
        Artist nar = new Artist("A2", LocalDateTime.now(), "Nar", "PT", LocalDate.of(1965, 1, 1), "F");
        db.addArtist(dir); db.addArtist(nar);

        Movie       m1  = new Movie("C1", LocalDateTime.now(), "Dunkirk",      2017, gAcao,  "UK", 106, dir);
        Movie       m2  = new Movie("C2", LocalDateTime.now(), "Dune",         2021, gAcao,  "US", 156, dir);
        Series      s1  = new Series("C3", LocalDateTime.now(), "Drama Series",2019, gDrama, "PT",   2, SeriesStatus.ENDED);
        Movie       m3  = new Movie("C4", LocalDateTime.now(), "Inception",    2010, gAcao,  "UK", 148, dir);
        Documentary doc = new Documentary("C5", LocalDateTime.now(), "Cosmos", 2014, gDrama, "US",
                8.0, 500, new ArrayList<>(), 90, "Science", nar);

        db.addContent(m1); db.addContent(m2); db.addContent(s1); db.addContent(m3); db.addContent(doc);

        // Por genero (BST)
        List<?> byAcao = db.searchByGenre("Acao");
        assertTrue("R3e | genero Acao tem C1",          containsContentId(byAcao, "C1"), print);
        assertTrue("R3e | genero Acao tem C2",          containsContentId(byAcao, "C2"), print);
        assertTrue("R3e | genero Acao tem C4",          containsContentId(byAcao, "C4"), print);
        assertTrue("R3e | genero Acao nao tem C3",      !containsContentId(byAcao, "C3"), print);

        List<?> byDrama = db.searchByGenre("Drama");
        assertTrue("R3e | genero Drama tem C3",         containsContentId(byDrama, "C3"), print);
        assertTrue("R3e | genero Drama tem C5 (doc)",   containsContentId(byDrama, "C5"), print);

        // Por ano de publicacao (BST range)
        List<?> y2017_2021 = db.searchContentsReleasedBetween(2017, 2021);
        assertTrue("R3e | ano 2017-2021 tem C1",        containsContentId(y2017_2021, "C1"), print);
        assertTrue("R3e | ano 2017-2021 tem C2",        containsContentId(y2017_2021, "C2"), print);
        assertTrue("R3e | ano 2017-2021 tem C3",        containsContentId(y2017_2021, "C3"), print);
        assertTrue("R3e | ano 2017-2021 nao tem C4 (2010)", !containsContentId(y2017_2021, "C4"), print);

        List<?> y2010_2015 = db.searchContentsReleasedBetween(2010, 2015);
        assertTrue("R3e | ano 2010-2015 tem C4",        containsContentId(y2010_2015, "C4"), print);
        assertTrue("R3e | ano 2010-2015 tem C5 (2014)", containsContentId(y2010_2015, "C5"), print);

        // Por tipo
        List<?> movies = db.searchContentsByType("movie");
        assertTrue("R3e | tipo movie tem C1",           containsContentId(movies, "C1"), print);
        assertTrue("R3e | tipo movie nao tem C3",       !containsContentId(movies, "C3"), print);

        List<?> seriesList = db.searchContentsByType("series");
        assertTrue("R3e | tipo series tem C3",          containsContentId(seriesList, "C3"), print);
        assertTrue("R3e | tipo series nao tem C1",      !containsContentId(seriesList, "C1"), print);

        List<?> docs = db.searchContentsByType("documentary");
        assertTrue("R3e | tipo documentary tem C5",     containsContentId(docs, "C5"), print);

        // updateGenreName reindexado no BST
        db.updateGenreName("G1", "Action");
        assertTrue("R3e | updateGenreName: chave antiga vazia", db.searchByGenre("Acao").isEmpty(), print);
        assertTrue("R3e | updateGenreName: nova chave tem C1",  containsContentId(db.searchByGenre("Action"), "C1"), print);
        assertTrue("R3e | updateGenreName: nova chave tem C2",  containsContentId(db.searchByGenre("Action"), "C2"), print);
    }

    // =========================================================================
    // R3f - Conteudos por substring no titulo, combinado com tipo/genero/data
    // =========================================================================

    /**
     * R3f - Pesquisas de conteudos por substring no titulo, isolada e combinada
     * com tipo, genero e data de publicacao.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3f_ContentsByTitleCombined(boolean print) {
        header("R3f - Conteudos por substring no titulo (e combinacoes)", print);

        StreamingDB db = new StreamingDB();

        Genre gAcao  = new Genre("G1", "Acao");
        Genre gDrama = new Genre("G2", "Drama");
        db.addGenre(gAcao); db.addGenre(gDrama);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Dir", "PT", LocalDate.of(1970, 1, 1), "M");
        db.addArtist(dir);

        Movie  m1 = new Movie("C1", LocalDateTime.now(), "Dunkirk",      2017, gAcao,  "UK", 106, dir);
        Movie  m2 = new Movie("C2", LocalDateTime.now(), "Dune",         2021, gAcao,  "US", 156, dir);
        Series s1 = new Series("C3", LocalDateTime.now(), "Drama Series",2019, gDrama, "PT",   2, SeriesStatus.ENDED);
        Movie  m3 = new Movie("C4", LocalDateTime.now(), "Inception",    2010, gAcao,  "UK", 148, dir);

        db.addContent(m1); db.addContent(m2); db.addContent(s1); db.addContent(m3);

        // Substring isolada
        List<?> subDu = db.searchContentsByTitleSubstring("du");
        assertTrue("R3f | titulo 'du' tem C1 (Dunkirk)", containsContentId(subDu, "C1"), print);
        assertTrue("R3f | titulo 'du' tem C2 (Dune)",    containsContentId(subDu, "C2"), print);
        assertTrue("R3f | titulo 'du' nao tem C4",       !containsContentId(subDu, "C4"), print);

        List<?> subIn = db.searchContentsByTitleSubstring("in");
        assertTrue("R3f | titulo 'in' tem C4 (Inception)", containsContentId(subIn, "C4"), print);

        // Substring AND tipo
        List<?> mvType = db.searchContentsByType("movie");
        long duMovieCount = subDu.stream().filter(o -> containsContentId(mvType, extractId(o))).count();
        assertTrue("R3f | titulo 'du' AND tipo movie: C1 e C2 (count==2)", duMovieCount == 2, print);

        List<?> srType = db.searchContentsByType("series");
        List<?> subDra = db.searchContentsByTitleSubstring("dra");
        long draSrCount = subDra.stream().filter(o -> containsContentId(srType, extractId(o))).count();
        assertTrue("R3f | titulo 'dra' AND tipo series: so C3 (count==1)", draSrCount == 1, print);

        // Substring AND genero
        List<?> byDrama = db.searchByGenre("Drama");
        long draDramaCount = subDra.stream().filter(o -> containsContentId(byDrama, extractId(o))).count();
        assertTrue("R3f | titulo 'dra' AND genero Drama: so C3 (count==1)", draDramaCount == 1, print);

        // Substring AND ano de publicacao
        List<?> pre2015 = db.searchContentsReleasedBetween(2000, 2015);
        long inPre2015Count = subIn.stream().filter(o -> containsContentId(pre2015, extractId(o))).count();
        assertTrue("R3f | titulo 'in' AND ano 2000-2015: so C4 (Inception 2010)", inPre2015Count == 1, print);
    }

    // =========================================================================
    // R3g - Conteudos por duracao, rating e visualizacoes
    // =========================================================================

    /**
     * R3g - Pesquisas de conteudos com criterios variados: duracao, rating e
     * total de visualizacoes, usando os indices Red-Black BST respetivos.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3g_ContentsByDurationRatingViews(boolean print) {
        header("R3g - Conteudos por duracao, rating e visualizacoes (BST)", print);

        StreamingDB db = new StreamingDB();

        Genre g = new Genre("G1", "Acao");
        db.addGenre(g);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Dir", "PT", LocalDate.of(1970, 1, 1), "M");
        Artist nar = new Artist("A2", LocalDateTime.now(), "Nar", "PT", LocalDate.of(1965, 1, 1), "F");
        db.addArtist(dir); db.addArtist(nar);

        // C1: Movie 106min, rating=0.0, views=0
        Movie       m1  = new Movie("C1", LocalDateTime.now(), "Dunkirk",     2017, g, "UK", 106, dir);
        // C2: Movie 156min, rating=0.0, views=0
        Movie       m2  = new Movie("C2", LocalDateTime.now(), "Dune",        2021, g, "US", 156, dir);
        // C3: Movie 148min, rating=0.0, views=0
        Movie       m3  = new Movie("C3", LocalDateTime.now(), "Inception",   2010, g, "UK", 148, dir);
        // C4: Documentary 90min, rating=8.5, views=1200
        Documentary doc = new Documentary("C4", LocalDateTime.now(), "Planet Earth", 2006, g, "UK",
                8.5, 1200, new ArrayList<>(), 90, "Nature", nar);
        // C5: Documentary 75min, rating=9.2, views=3500
        Documentary doc2 = new Documentary("C5", LocalDateTime.now(), "Cosmos", 2014, g, "US",
                9.2, 3500, new ArrayList<>(), 75, "Science", nar);

        db.addContent(m1); db.addContent(m2); db.addContent(m3); db.addContent(doc); db.addContent(doc2);

        // --- Duracao (BST range) ---
        List<Content> dur100_160 = db.searchContentsByDurationRange(100, 160);
        assertTrue("R3g | duracao 100-160 tem C1 (106)",   containsContentId(dur100_160, "C1"), print);
        assertTrue("R3g | duracao 100-160 tem C2 (156)",   containsContentId(dur100_160, "C2"), print);
        assertTrue("R3g | duracao 100-160 tem C3 (148)",   containsContentId(dur100_160, "C3"), print);
        assertTrue("R3g | duracao 100-160 nao tem C4 (90)",!containsContentId(dur100_160, "C4"), print);

        List<Content> dur70_95 = db.searchContentsByDurationRange(70, 95);
        assertTrue("R3g | duracao 70-95 tem C4 (90)",      containsContentId(dur70_95, "C4"), print);
        assertTrue("R3g | duracao 70-95 tem C5 (75)",      containsContentId(dur70_95, "C5"), print);
        assertTrue("R3g | duracao 70-95 nao tem C1 (106)", !containsContentId(dur70_95, "C1"), print);

        // --- Rating (BST range) ---
        List<Content> rat8_9 = db.searchContentsByRatingRange(8.0, 9.0);
        assertTrue("R3g | rating 8.0-9.0 tem C4 (8.5)",   containsContentId(rat8_9, "C4"), print);
        assertTrue("R3g | rating 8.0-9.0 nao tem C5 (9.2)", !containsContentId(rat8_9, "C5"), print);
        assertTrue("R3g | rating 8.0-9.0 nao tem C1 (0.0)",!containsContentId(rat8_9, "C1"), print);

        List<Content> rat9_10 = db.searchContentsByRatingRange(9.0, 10.0);
        assertTrue("R3g | rating 9.0-10.0 tem C5 (9.2)",  containsContentId(rat9_10, "C5"), print);

        List<Content> rat0 = db.searchContentsByRatingRange(0.0, 0.0);
        assertTrue("R3g | rating 0.0 tem C1 (movie)",     containsContentId(rat0, "C1"), print);
        assertTrue("R3g | rating 0.0 tem C2 (movie)",     containsContentId(rat0, "C2"), print);

        // --- Visualizacoes (BST range) ---
        List<Content> views1000_2000 = db.searchContentsByViewsRange(1000, 2000);
        assertTrue("R3g | views 1000-2000 tem C4 (1200)",      containsContentId(views1000_2000, "C4"), print);
        assertTrue("R3g | views 1000-2000 nao tem C5 (3500)",  !containsContentId(views1000_2000, "C5"), print);
        assertTrue("R3g | views 1000-2000 nao tem C1 (0)",     !containsContentId(views1000_2000, "C1"), print);

        List<Content> views3000_5000 = db.searchContentsByViewsRange(3000, 5000);
        assertTrue("R3g | views 3000-5000 tem C5 (3500)",      containsContentId(views3000_5000, "C5"), print);

        List<Content> views0 = db.searchContentsByViewsRange(0, 0);
        assertTrue("R3g | views 0 tem C1",                     containsContentId(views0, "C1"), print);
    }

    // =========================================================================
    // R4 - Consistencia: arquivo de utilizadores e integridade referencial
    // =========================================================================

    /**
     * R4 - Valida os mecanismos de consistencia entre estruturas de dados:
     * arquivo/restauro de utilizadores, limpeza do arquivo, integridade referencial
     * (impede remover genero referenciado) e {@code validateConsistency()}.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR4_Consistency(boolean print) {
        header("R4 - Consistencia: arquivo, restauro e integridade referencial", print);

        StreamingDB db = new StreamingDB();

        // --- Arquivo e restauro ---
        User u1 = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "h1");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        db.addUser(u1);

        assertTrue("R4 | removeUser(archive=true): true",     db.removeUser("U1", true), print);
        assertTrue("R4 | utilizador ja nao esta ativo",       db.getUser("U1") == null, print);
        assertTrue("R4 | utilizador esta no arquivo",         db.getArchivedUser("U1") != null, print);
        assertTrue("R4 | listArchivedUsers: size==1",         db.listArchivedUsers().size() == 1, print);

        assertTrue("R4 | restoreArchivedUser: true",          db.restoreArchivedUser("U1"), print);
        assertTrue("R4 | utilizador ativo apos restauro",     db.getUser("U1") != null, print);
        assertTrue("R4 | utilizador saiu do arquivo",         db.getArchivedUser("U1") == null, print);

        // Arquivar de novo para testar clearArchivedUsers
        db.removeUser("U1", true);
        db.clearArchivedUsers();
        assertTrue("R4 | clearArchivedUsers: arquivo vazio",  db.listArchivedUsers().isEmpty(), print);

        // --- Integridade referencial: genero nao pode ser removido se conteudo o referenciar ---
        Genre g1 = new Genre("G1", "Suspense");
        db.addGenre(g1);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Dir", "PT", LocalDate.of(1980, 1, 1), "X");
        db.addArtist(dir);

        Movie m1 = new Movie("C1", LocalDateTime.now(), "Thriller", 2023, g1, "PT", 95, dir);
        db.addContent(m1);

        assertTrue("R4 | removeGenre referenciado: false",    !db.removeGenre("G1"), print);

        db.removeContent("C1");
        assertTrue("R4 | removeGenre apos remover conteudo: true", db.removeGenre("G1"), print);
        assertTrue("R4 | G1 == null apos remocao",            db.getGenre("G1") == null, print);

        // --- validateConsistency: ST e BST sincronizados ---
        StreamingDB db2 = new StreamingDB();
        Genre g2  = new Genre("G2", "Drama");
        db2.addGenre(g2);

        Artist dir2 = new Artist("A2", LocalDateTime.now(), "Cineasta", "BR", LocalDate.of(1975, 6, 15), "M");
        db2.addArtist(dir2);

        Movie m2 = new Movie("C2", LocalDateTime.now(), "Classico", 2018, g2, "BR", 130, dir2);
        db2.addContent(m2);

        User u2 = new User("U2", LocalDateTime.now(), "carol", "carol@ex.com", "h3");
        u2.setRegion("Braga");
        u2.setRegistrationDate(LocalDate.of(2024, 6, 1));
        db2.addUser(u2);

        assertTrue("R4 | validateConsistency: base de dados consistente", db2.validateConsistency(), print);
    }

    // =========================================================================
    // Utilitarios
    // =========================================================================

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
        for (Object o : list) {
            if (o instanceof Content c && id.equals(c.getId())) return true;
        }
        return false;
    }

    private static String extractId(Object o) {
        if (o instanceof Content c) return c.getId();
        return null;
    }
}
