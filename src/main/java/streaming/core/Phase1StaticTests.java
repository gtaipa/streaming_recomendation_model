package streaming.core;

import streaming.db.StreamingDB;
import streaming.model.Artist;
import streaming.model.Documentary;
import streaming.model.Genre;
import streaming.model.Movie;
import streaming.model.Series;
import streaming.model.SeriesStatus;
import streaming.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Fase 1 (R5): Suite de testes estaticos organizados por requisito, com output na consola.
 *
 * <ul>
 *   <li>{@link #testR2_SymbolTableCrud} - R2: CRUD nas Symbol Tables (insert/remove/edit/list)</li>
 *   <li>{@link #testR3_BSTSearches}     - R3: Pesquisas ordenadas via Red-Black BST</li>
 *   <li>{@link #testR4_Consistency}     - R4: Validacao de consistencia e arquivo de utilizadores</li>
 * </ul>
 *
 * Executar:
 * <ul>
 *   <li>Pelo IDE: correr o {@code main} desta classe.</li>
 *   <li>Linha de comandos (Maven): {@code mvn exec:java -Dexec.mainClass=streaming.core.Phase1StaticTests}</li>
 * </ul>
 */
public final class Phase1StaticTests {

    private static int passed;
    private static int failed;

    private Phase1StaticTests() { }

    /**
     * Ponto de entrada standalone. Termina com codigo 0 se todos os testes passarem.
     *
     * @param args argumentos da linha de comandos (ignorados)
     */
    public static void main(String[] args) {
        boolean ok = runAll(true);
        System.exit(ok ? 0 : 1);
    }

    /**
     * Corre todos os grupos de testes e devolve {@code true} se todos passaram.
     *
     * @param printToConsole se {@code true}, imprime o resultado de cada assert na consola
     * @return {@code true} se nenhum teste falhou
     */
    public static boolean runAll(boolean printToConsole) {
        passed = 0;
        failed = 0;

        if (printToConsole) {
            System.out.println("=== Phase1StaticTests (R5) ===");
        }

        try {
            testR2_SymbolTableCrud(printToConsole);
            testR3_BSTSearches(printToConsole);
            testR4_Consistency(printToConsole);
        } catch (RuntimeException e) {
            fail("Excecao nao tratada: " + e.getClass().getSimpleName() + " - " + e.getMessage(), printToConsole);
        }

        if (printToConsole) {
            System.out.println();
            System.out.println("Resultado: passed=" + passed + "  failed=" + failed);
        }
        return failed == 0;
    }

    // -------------------------------------------------------------------------
    // R2 - Symbol Table: insert, remove, edit, list para todas as entidades
    // -------------------------------------------------------------------------

    /**
     * R2 - Valida as operacoes CRUD basicas nas Symbol Tables (SeparateChainingHashST)
     * para Utilizadores, Artistas, Generos e Conteudos.
     * Nao depende de ordenacao; verifica apenas presenca/ausencia nas STs primarias.
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

        assertTrue("R2 | addUser: listUsers size==2", db.listUsers().size() == 2, print);
        assertTrue("R2 | addUser: getUser U1 != null", db.getUser("U1") != null, print);
        assertTrue("R2 | addUser: getUser U2 != null", db.getUser("U2") != null, print);

        u1.setEmail("alice_new@ex.com");
        assertTrue("R2 | updateUser: devolve true", db.updateUser(u1), print);
        assertTrue("R2 | updateUser: email atualizado", "alice_new@ex.com".equals(db.getUser("U1").getEmail()), print);

        assertTrue("R2 | removeUser: devolve true", db.removeUser("U2", false), print);
        assertTrue("R2 | removeUser: getUser U2 == null", db.getUser("U2") == null, print);
        assertTrue("R2 | removeUser: listUsers size==1", db.listUsers().size() == 1, print);

        // --- Artistas ---
        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan", "UK", LocalDate.of(1970, 7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10, 3), "M");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "Nair", "IN", LocalDate.of(1957, 2, 18), "F");

        db.addArtist(a1);
        db.addArtist(a2);
        db.addArtist(a3);

        assertTrue("R2 | addArtist: listArtists size==3", db.listArtists().size() == 3, print);
        assertTrue("R2 | addArtist: getArtist A2 != null", db.getArtist("A2") != null, print);

        assertTrue("R2 | removeArtist: devolve true", db.removeArtist("A3"), print);
        assertTrue("R2 | removeArtist: getArtist A3 == null", db.getArtist("A3") == null, print);
        assertTrue("R2 | removeArtist: listArtists size==2", db.listArtists().size() == 2, print);

        // --- Generos ---
        Genre g1 = new Genre("G1", "Acao");
        Genre g2 = new Genre("G2", "Drama");

        db.addGenre(g1);
        db.addGenre(g2);

        assertTrue("R2 | addGenre: listGenres size==2", db.listGenres().size() == 2, print);
        assertTrue("R2 | addGenre: getGenre G1 != null", db.getGenre("G1") != null, print);

        assertTrue("R2 | updateGenreName: devolve true", db.updateGenreName("G2", "Comedia"), print);
        assertTrue("R2 | updateGenreName: nome atualizado", "Comedia".equals(db.getGenre("G2").getName()), print);

        // So e possivel remover se nenhum conteudo referenciar o genero
        assertTrue("R2 | removeGenre (sem conteudos): devolve true", db.removeGenre("G2"), print);
        assertTrue("R2 | removeGenre: getGenre G2 == null", db.getGenre("G2") == null, print);

        // --- Conteudos ---
        Movie m1 = new Movie("C1", LocalDateTime.now(), "Filme Alpha", 2022, g1, "PT", 110, a1);
        Series s1 = new Series("C2", LocalDateTime.now(), "Serie Beta", 2021, g1, "PT", 3, SeriesStatus.ONGOING);

        db.addContent(m1);
        db.addContent(s1);

        assertTrue("R2 | addContent: listContents size==2", db.listContents().size() == 2, print);
        assertTrue("R2 | addContent: getContent C1 != null", db.getContent("C1") != null, print);

        db.removeContent("C2");
        assertTrue("R2 | removeContent: getContent C2 == null", db.getContent("C2") == null, print);

        assertTrue("R2 | removeContentIfExists C1: devolve true",  db.removeContentIfExists("C1"), print);
        assertTrue("R2 | removeContentIfExists C1 (ja removido): devolve false", !db.removeContentIfExists("C1"), print);
    }

    // -------------------------------------------------------------------------
    // R3 - Red-Black BST: pesquisas ordenadas
    // -------------------------------------------------------------------------

    /**
     * R3 - Valida todas as pesquisas ordenadas suportadas pelos indices Red-Black BST,
     * cobrindo as alineas a) a g) do requisito:
     * <ul>
     *   <li>R3a) Utilizadores por regiao e/ou data de registo e/ou preferencias</li>
     *   <li>R3b) Utilizadores por substring no nome, combinada com regiao/data</li>
     *   <li>R3c) Artistas por nacionalidade e/ou genero e/ou intervalo de nascimento</li>
     *   <li>R3d) Artistas por substring no nome, combinada com nacionalidade/genero</li>
     *   <li>R3e) Conteudos por tipo, genero e/ou ano de publicacao</li>
     *   <li>R3f) Conteudos por substring no titulo, tipo e/ou genero e/ou ano</li>
     *   <li>R3g) Conteudos por duracao, rating e/ou visualizacoes</li>
     * </ul>
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR3_BSTSearches(boolean print) {
        header("R3 - Red-Black BST: pesquisas ordenadas (alineas a-g)", print);

        StreamingDB db = new StreamingDB();

        // Dados de base
        Genre gAcao  = new Genre("G1", "Acao");
        Genre gDrama = new Genre("G2", "Drama");
        db.addGenre(gAcao);
        db.addGenre(gDrama);

        Artist a1 = new Artist("A1", LocalDateTime.now(), "Nolan",      "UK", LocalDate.of(1970,  7, 30), "M");
        Artist a2 = new Artist("A2", LocalDateTime.now(), "Villeneuve", "CA", LocalDate.of(1967, 10,  3), "M");
        Artist a3 = new Artist("A3", LocalDateTime.now(), "Nair",       "IN", LocalDate.of(1957,  2, 18), "F");
        db.addArtist(a1);
        db.addArtist(a2);
        db.addArtist(a3);

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

        db.addUser(u1);
        db.addUser(u2);
        db.addUser(u3);

        // C1: Movie, duracao=106, rating=7.5
        Movie  m1 = new Movie("C1", LocalDateTime.now(), "Dunkirk",   2017, gAcao,  "UK", 106, a1);
        // C2: Movie, duracao=156, rating=8.5
        Movie  m2 = new Movie("C2", LocalDateTime.now(), "Dune",      2021, gAcao,  "US", 156, a2);
        // C3: Series, sem duracao, rating=0.0
        Series s1 = new Series("C3", LocalDateTime.now(), "Drama Series", 2019, gDrama, "PT", 2, SeriesStatus.ENDED);
        // C4: Movie, duracao=148, rating=9.0
        Movie  m3 = new Movie("C4", LocalDateTime.now(), "Inception", 2010, gAcao,  "UK", 148, a1);
        // C5: Documentary, duracao=90, rating=8.0
        Documentary doc = new Documentary("C5", LocalDateTime.now(), "Cosmos", 2014, gDrama, "US",
                8.0, 500, new java.util.ArrayList<>(), 90, "Science", a3);
        db.addContent(m1);
        db.addContent(m2);
        db.addContent(s1);
        db.addContent(m3);
        db.addContent(doc);

        // ---- R3a) Utilizadores por regiao ----
        List<User> lisboa = db.searchUsersByRegion("Lisboa");
        assertTrue("R3a | searchUsersByRegion Lisboa tem U1", containsUserId(lisboa, "U1"), print);
        assertTrue("R3a | searchUsersByRegion Lisboa tem U3", containsUserId(lisboa, "U3"), print);
        assertTrue("R3a | searchUsersByRegion Lisboa nao tem U2", !containsUserId(lisboa, "U2"), print);

        // ---- R3a) Utilizadores por data de registo (range BST) ----
        List<User> regJan2025 = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        assertTrue("R3a | searchUsersRegisteredBetween jan2025 tem U1",    containsUserId(regJan2025, "U1"), print);
        assertTrue("R3a | searchUsersRegisteredBetween jan2025 nao tem U2",!containsUserId(regJan2025, "U2"), print);

        List<User> reg2024 = db.searchUsersRegisteredBetween(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertTrue("R3a | searchUsersRegisteredBetween 2024 tem U3", containsUserId(reg2024, "U3"), print);

        // ---- R3a) Utilizadores por preferencia de genero ----
        List<User> prefAcao = db.searchUsersByPreference("Acao");
        assertTrue("R3a | searchUsersByPreference Acao tem U1", containsUserId(prefAcao, "U1"), print);
        assertTrue("R3a | searchUsersByPreference Acao tem U3", containsUserId(prefAcao, "U3"), print);
        assertTrue("R3a | searchUsersByPreference Acao nao tem U2", !containsUserId(prefAcao, "U2"), print);

        List<User> prefDrama = db.searchUsersByPreference("Drama");
        assertTrue("R3a | searchUsersByPreference Drama tem U2", containsUserId(prefDrama, "U2"), print);

        // ---- R3b) Utilizadores por substring no nome ----
        List<User> subBo = db.searchUsersByUsernameSubstring("bo");
        assertTrue("R3b | searchUsersByUsernameSubstring 'bo' tem U2", containsUserId(subBo, "U2"), print);
        assertTrue("R3b | searchUsersByUsernameSubstring 'bo' nao tem U1", !containsUserId(subBo, "U1"), print);

        // R3b) Combinacao: substring + regiao (intersecao manual)
        List<User> subLi  = db.searchUsersByUsernameSubstring("li");
        List<User> lisbUsers = db.searchUsersByRegion("Lisboa");
        long lisbLiCount = subLi.stream().filter(u -> containsUserId(lisbUsers, u.getId())).count();
        assertTrue("R3b | substring 'li' AND regiao Lisboa: so U1 (alice)", lisbLiCount == 1, print);

        // R3b) Combinacao: substring + data de registo
        List<User> subAl   = db.searchUsersByUsernameSubstring("al");
        List<User> jan2025Users = db.searchUsersRegisteredBetween(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        long alJanCount = subAl.stream().filter(u -> containsUserId(jan2025Users, u.getId())).count();
        assertTrue("R3b | substring 'al' AND jan2025: so U1 (alice)", alJanCount == 1, print);

        // ---- Reindexacao BST apos updateUser ----
        u1.setRegion("Porto");
        db.updateUser(u1);
        assertTrue("R3a | updateUser: Lisboa ja nao tem U1", !containsUserId(db.searchUsersByRegion("Lisboa"), "U1"), print);
        assertTrue("R3a | updateUser: Porto agora tem U1",    containsUserId(db.searchUsersByRegion("Porto"),  "U1"), print);

        // ---- R3c) Artistas por nacionalidade ----
        assertTrue("R3c | searchArtistsByNationality UK tem A1",
                containsArtistId(db.searchArtistsByNationality("UK"), "A1"), print);
        assertTrue("R3c | searchArtistsByNationality CA tem A2",
                containsArtistId(db.searchArtistsByNationality("CA"), "A2"), print);
        assertTrue("R3c | searchArtistsByNationality UK nao tem A2",
                !containsArtistId(db.searchArtistsByNationality("UK"), "A2"), print);

        // ---- R3c) Artistas por genero (gender) ----
        List<Artist> masc = db.searchArtistsByGender("M");
        assertTrue("R3c | searchArtistsByGender M tem A1", containsArtistId(masc, "A1"), print);
        assertTrue("R3c | searchArtistsByGender M tem A2", containsArtistId(masc, "A2"), print);
        assertTrue("R3c | searchArtistsByGender M nao tem A3", !containsArtistId(masc, "A3"), print);

        List<Artist> fem = db.searchArtistsByGender("F");
        assertTrue("R3c | searchArtistsByGender F tem A3", containsArtistId(fem, "A3"), print);

        // ---- R3c) Artistas por intervalo de nascimento ----
        List<Artist> born60s70s = db.searchArtistsBornBetween(
                LocalDate.of(1960, 1, 1), LocalDate.of(1979, 12, 31));
        assertTrue("R3c | searchArtistsBornBetween 1960-1979 tem A1", containsArtistId(born60s70s, "A1"), print);
        assertTrue("R3c | searchArtistsBornBetween 1960-1979 tem A2", containsArtistId(born60s70s, "A2"), print);
        assertTrue("R3c | searchArtistsBornBetween 1960-1979 nao tem A3 (1957)", !containsArtistId(born60s70s, "A3"), print);

        // ---- R3d) Artistas por substring no nome ----
        List<Artist> subNol = db.searchArtistsByNameSubstring("nol");
        assertTrue("R3d | searchArtistsByNameSubstring 'nol' tem A1", containsArtistId(subNol, "A1"), print);
        assertTrue("R3d | searchArtistsByNameSubstring 'nol' nao tem A2", !containsArtistId(subNol, "A2"), print);

        // R3d) Combinacao: substring + nacionalidade
        List<Artist> subVil  = db.searchArtistsByNameSubstring("vil");
        List<Artist> natCA   = db.searchArtistsByNationality("CA");
        long vilCACount = subVil.stream().filter(a -> containsArtistId(natCA, a.getId())).count();
        assertTrue("R3d | substring 'vil' AND nacionalidade CA: so A2", vilCACount == 1, print);

        // R3d) Combinacao: substring + genero
        List<Artist> subNa   = db.searchArtistsByNameSubstring("na");
        List<Artist> genF    = db.searchArtistsByGender("F");
        long naFCount = subNa.stream().filter(a -> containsArtistId(genF, a.getId())).count();
        assertTrue("R3d | substring 'na' AND gender F: so A3 (Nair)", naFCount == 1, print);

        // ---- R3e) Conteudos por genero ----
        List<?> byAcao = db.searchByGenre("Acao");
        assertTrue("R3e | searchByGenre Acao tem C1", containsContentId(byAcao, "C1"), print);
        assertTrue("R3e | searchByGenre Acao tem C2", containsContentId(byAcao, "C2"), print);
        assertTrue("R3e | searchByGenre Acao tem C4", containsContentId(byAcao, "C4"), print);
        assertTrue("R3e | searchByGenre Acao nao tem C3 (Drama)", !containsContentId(byAcao, "C3"), print);

        // ---- R3e) Conteudos por ano de publicacao ----
        List<?> by2017_2021 = db.searchContentsReleasedBetween(2017, 2021);
        assertTrue("R3e | searchContentsReleasedBetween 2017-2021 tem C1", containsContentId(by2017_2021, "C1"), print);
        assertTrue("R3e | searchContentsReleasedBetween 2017-2021 tem C2", containsContentId(by2017_2021, "C2"), print);
        assertTrue("R3e | searchContentsReleasedBetween 2017-2021 tem C3", containsContentId(by2017_2021, "C3"), print);
        assertTrue("R3e | searchContentsReleasedBetween 2017-2021 nao tem C4 (2010)", !containsContentId(by2017_2021, "C4"), print);

        // ---- R3e) Conteudos por tipo ----
        List<?> movies = db.searchContentsByType("movie");
        assertTrue("R3e | searchContentsByType movie tem C1",  containsContentId(movies, "C1"), print);
        assertTrue("R3e | searchContentsByType movie nao tem C3 (series)", !containsContentId(movies, "C3"), print);

        List<?> seriesList = db.searchContentsByType("series");
        assertTrue("R3e | searchContentsByType series tem C3", containsContentId(seriesList, "C3"), print);

        List<?> docs = db.searchContentsByType("documentary");
        assertTrue("R3e | searchContentsByType documentary tem C5", containsContentId(docs, "C5"), print);

        // ---- R3f) Conteudos por substring no titulo ----
        List<?> subDun = db.searchContentsByTitleSubstring("dun");
        assertTrue("R3f | searchContentsByTitleSubstring 'dun' tem C1 (Dunkirk)", containsContentId(subDun, "C1"), print);
        assertTrue("R3f | searchContentsByTitleSubstring 'dun' nao tem C2 (Dune)", !containsContentId(subDun, "C2"), print);

        // R3f) Combinacao: substring + tipo
        List<?> subDu    = db.searchContentsByTitleSubstring("du");
        List<?> mvType   = db.searchContentsByType("movie");
        long duMovieCount = subDu.stream().filter(o -> containsContentId(mvType, extractId(o))).count();
        assertTrue("R3f | substring 'du' AND tipo movie: C1 e C2 (Dunkirk, Dune)", duMovieCount == 2, print);

        // R3f) Combinacao: substring + genero
        List<?> subDr    = db.searchContentsByTitleSubstring("dra");
        List<?> genDrama = db.searchByGenre("Drama");
        long drDramaCount = subDr.stream().filter(o -> containsContentId(genDrama, extractId(o))).count();
        assertTrue("R3f | substring 'dra' AND genero Drama: so C3 (Drama Series)", drDramaCount == 1, print);

        // R3f) Combinacao: substring + ano
        List<?> subIn    = db.searchContentsByTitleSubstring("in");
        List<?> pre2015  = db.searchContentsReleasedBetween(2000, 2015);
        long inPre2015Count = subIn.stream().filter(o -> containsContentId(pre2015, extractId(o))).count();
        assertTrue("R3f | substring 'in' AND ano<=2015: so C4 (Inception 2010)", inPre2015Count == 1, print);

        // ---- R3g) Conteudos por duracao (BST) ----
        // C1=106min, C2=156min, C4=148min, C5=90min, C3=Series(sem duracao)
        List<streaming.model.Content> dur100_160 = db.searchContentsByDurationRange(100, 160);
        assertTrue("R3g | searchContentsByDurationRange 100-160 tem C1 (106)", containsContentId(dur100_160, "C1"), print);
        assertTrue("R3g | searchContentsByDurationRange 100-160 tem C2 (156)", containsContentId(dur100_160, "C2"), print);
        assertTrue("R3g | searchContentsByDurationRange 100-160 tem C4 (148)", containsContentId(dur100_160, "C4"), print);
        assertTrue("R3g | searchContentsByDurationRange 100-160 nao tem C5 (90)", !containsContentId(dur100_160, "C5"), print);
        assertTrue("R3g | searchContentsByDurationRange 100-160 nao tem C3 (Series)", !containsContentId(dur100_160, "C3"), print);

        List<streaming.model.Content> dur80_100 = db.searchContentsByDurationRange(80, 100);
        assertTrue("R3g | searchContentsByDurationRange 80-100 tem C5 (90)", containsContentId(dur80_100, "C5"), print);
        assertTrue("R3g | searchContentsByDurationRange 80-100 nao tem C1 (106)", !containsContentId(dur80_100, "C1"), print);

        // ---- R3g) Conteudos por rating (BST) ----
        // C5 foi criado com avgRating=8.0, os restantes com avgRating=0.0 (default Movie)
        List<streaming.model.Content> rating7_9 = db.searchContentsByRatingRange(7.0, 9.0);
        assertTrue("R3g | searchContentsByRatingRange 7.0-9.0 tem C5 (8.0)", containsContentId(rating7_9, "C5"), print);

        List<streaming.model.Content> rating0_0 = db.searchContentsByRatingRange(0.0, 0.0);
        assertTrue("R3g | searchContentsByRatingRange 0.0-0.0 tem C1 (movie sem rating)", containsContentId(rating0_0, "C1"), print);

        // ---- R3g) Conteudos por visualizacoes (BST) ----
        // C5 tem totalViews=500, restantes=0
        List<streaming.model.Content> views400_600 = db.searchContentsByViewsRange(400, 600);
        assertTrue("R3g | searchContentsByViewsRange 400-600 tem C5 (500 views)", containsContentId(views400_600, "C5"), print);
        assertTrue("R3g | searchContentsByViewsRange 400-600 nao tem C1 (0 views)", !containsContentId(views400_600, "C1"), print);

        // ---- updateGenreName: indice BST de conteudos deve ser atualizado ----
        db.updateGenreName("G1", "Action");
        assertTrue("R3e | updateGenreName: chave antiga 'Acao' vazia", db.searchByGenre("Acao").isEmpty(), print);
        assertTrue("R3e | updateGenreName: nova chave 'Action' tem C1", containsContentId(db.searchByGenre("Action"), "C1"), print);
        assertTrue("R3e | updateGenreName: nova chave 'Action' tem C2", containsContentId(db.searchByGenre("Action"), "C2"), print);
    }

    // -------------------------------------------------------------------------
    // R4 - Consistencia: arquivo de utilizadores e integridade referencial
    // -------------------------------------------------------------------------

    /**
     * R4 - Valida os mecanismos de consistencia entre estruturas de dados:
     * arquivo/restauro de utilizadores, limpeza do arquivo, integridade referencial
     * (impede remover genero referenciado por conteudos) e {@code validateConsistency()}.
     *
     * @param print se {@code true}, imprime cada assert na consola
     */
    private static void testR4_Consistency(boolean print) {
        header("R4 - Consistencia: arquivo, restauro e integridade referencial", print);

        StreamingDB db = new StreamingDB();

        // ---- Arquivo e restauro de utilizador ----
        User u1 = new User("U1", LocalDateTime.now(), "alice", "a@ex.com", "h1");
        u1.setRegion("Lisboa");
        u1.setRegistrationDate(LocalDate.of(2025, 1, 10));
        db.addUser(u1);

        assertTrue("R4 | removeUser(archive=true): devolve true", db.removeUser("U1", true), print);
        assertTrue("R4 | removeUser: utilizador ja nao esta ativo", db.getUser("U1") == null, print);
        assertTrue("R4 | removeUser: utilizador esta no arquivo",   db.getArchivedUser("U1") != null, print);
        assertTrue("R4 | listArchivedUsers: tamanho==1", db.listArchivedUsers().size() == 1, print);

        assertTrue("R4 | restoreArchivedUser: devolve true",           db.restoreArchivedUser("U1"), print);
        assertTrue("R4 | restoreArchivedUser: utilizador ativo novamente", db.getUser("U1") != null, print);
        assertTrue("R4 | restoreArchivedUser: saiu do arquivo",        db.getArchivedUser("U1") == null, print);

        // Arquivar de novo para testar clearArchivedUsers
        db.removeUser("U1", true);
        db.clearArchivedUsers();
        assertTrue("R4 | clearArchivedUsers: arquivo vazio", db.listArchivedUsers().isEmpty(), print);

        // ---- Integridade referencial: removeGenre bloqueado por conteudo ----
        Genre g1 = new Genre("G1", "Suspense");
        db.addGenre(g1);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Director", "PT", LocalDate.of(1980, 1, 1), "X");
        db.addArtist(dir);

        Movie m1 = new Movie("C1", LocalDateTime.now(), "Thriller", 2023, g1, "PT", 95, dir);
        db.addContent(m1);

        assertTrue("R4 | removeGenre referenciado: devolve false", !db.removeGenre("G1"), print);

        // Apos remover o conteudo, deve ser possivel remover o genero
        db.removeContent("C1");
        assertTrue("R4 | removeGenre apos remover conteudo: devolve true", db.removeGenre("G1"), print);
        assertTrue("R4 | removeGenre: getGenre G1 == null", db.getGenre("G1") == null, print);

        // ---- validateConsistency: ST e BST sincronizadas ----
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

    // -------------------------------------------------------------------------
    // Utilitarios
    // -------------------------------------------------------------------------

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

    private static String extractId(Object o) {
        if (o instanceof streaming.model.Content c) return c.getId();
        return null;
    }
}
