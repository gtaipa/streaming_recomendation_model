package streaming.core;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import streaming.db.GraphAlgorithms;
import streaming.db.StreamingDB;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * R9 - Interface grafica da plataforma de streaming.
 *
 * Tres abas principais:
 * - Utilizadores: inserir, listar, pesquisar, remover
 * - Conteudos: inserir, listar, pesquisar por genero/titulo/ano
 * - Grafo: visualizar relacoes, recomendacoes, caminhos mais curtos
 */
public class StreamingGUI extends Application {

    // -- Estado partilhado -----------------------------------------------------
    private final StreamingDB db = new StreamingDB();
    private final StreamingGraph graph = new StreamingGraph();
    private final StreamingGraphAPI api = new StreamingGraphAPI(db, graph);

    // -- Tabelas observaveis ---------------------------------------------------
    private final ObservableList<UserRow> userRows = FXCollections.observableArrayList();
    private final ObservableList<ContentRow> contentRows = FXCollections.observableArrayList();
    private final ObservableList<String> graphLines = FXCollections.observableArrayList();
    private final ObservableList<String> searchHistory = FXCollections.observableArrayList();

    // -- Referencias para ligacao entre tabs (R9b/c) ---------------------------
    private TabPane tabPane;
    private ListView<String> searchHistoryView;
    private Label graphContextLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        seedData();

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                buildUsersTab(),
                buildContentsTab(),
                buildGraphTab()
        );

        Scene scene = new Scene(tabPane, 1050, 700);
        URL css = getClass().getResource("style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("Streaming Recommendation Engine");
        stage.setScene(scene);
        stage.show();
    }

    // =========================================================================
    // TAB 1 - USERS
    // =========================================================================
    private Tab buildUsersTab() {
        Tab tab = new Tab("Users");

        TableView<UserRow> table = new TableView<>(userRows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<UserRow, String> cId = col("ID", "id");
        TableColumn<UserRow, String> cUsername = col("Username", "username");
        TableColumn<UserRow, String> cEmail = col("Email", "email");
        TableColumn<UserRow, String> cRegion = col("Region", "region");
        TableColumn<UserRow, String> cDate = col("Registered", "registrationDate");
        table.getColumns().addAll(cId, cUsername, cEmail, cRegion, cDate);

        // R9b: ao selecionar um user, preparar automaticamente o painel do grafo com as relacoes desse user
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) return;
            showUserRelationsOnGraph(sel.getId(), true);
        });

        TextField fId = field("ID (ex: U1)");
        TextField fUsername = field("Username");
        TextField fEmail = field("Email");
        TextField fRegion = field("Region (ex: PT)");
        DatePicker fDate = new DatePicker(LocalDate.now());
        fDate.getStyleClass().add("field");

        Button btnAdd = btn("Add", "btn-primary");
        Label lblMsg = new Label();
        lblMsg.getStyleClass().add("msg-label");

        btnAdd.setOnAction(e -> {
            String id = fId.getText().trim();
            if (id.isEmpty() || fUsername.getText().trim().isEmpty()) {
                lblMsg.setText("ID and Username are required.");
                lblMsg.getStyleClass().setAll("msg-label", "msg-error");
                return;
            }
            User u = new User(id, LocalDateTime.now(),
                    fUsername.getText().trim(),
                    fEmail.getText().trim(), "");
            u.setRegion(fRegion.getText().trim());
            u.setRegistrationDate(fDate.getValue());
            db.addUser(u);
            api.registerUser(id);
            refreshUsers();
            lblMsg.setText("User " + id + " added.");
            lblMsg.getStyleClass().setAll("msg-label", "msg-ok");
            clearFields(fId, fUsername, fEmail, fRegion);
        });

        Button btnRemove = btn("Remove Selected", "btn-danger");
        btnRemove.setOnAction(e -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            db.removeUser(sel.getId(), true);
            refreshUsers();
            lblMsg.setText("User " + sel.getId() + " archived.");
            lblMsg.getStyleClass().setAll("msg-label", "msg-ok");
        });

        TextField fSearch = field("Search by region or username...");
        Button btnSearch = btn("Search", "btn-secondary");
        btnSearch.setOnAction(e -> {
            String q = fSearch.getText().trim();
            userRows.clear();
            List<User> byRegion = db.searchUsersByRegion(q);
            List<User> byUsername = db.searchUsersByUsernameSubstring(q);
            byRegion.stream().map(UserRow::new).forEach(userRows::add);
            byUsername.stream()
                    .filter(u -> byRegion.stream().noneMatch(r -> r.getId().equals(u.getId())))
                    .map(UserRow::new)
                    .forEach(userRows::add);
            lblMsg.setText("Search: " + userRows.size() + " result(s) for \"" + q + "\".");
            lblMsg.getStyleClass().setAll("msg-label", "msg-info");
            logSearch("Users", q, userRows.size());
        });

        Button btnReset = btn("Reset", "btn-secondary");
        btnReset.setOnAction(e -> {
            refreshUsers();
            lblMsg.setText("");
        });

        GridPane form = formGrid(
                "ID", fId,
                "Username", fUsername,
                "Email", fEmail,
                "Region", fRegion,
                "Registered", fDate
        );

        HBox formBtns = hbox(btnAdd, btnRemove);
        HBox searchRow = hbox(fSearch, btnSearch, btnReset);
        VBox left = vbox(sectionTitle("Add User"), form, formBtns, lblMsg,
                sectionTitle("Search"), searchRow,
                sectionTitle("Search History"), buildSearchHistoryPanel());
        left.setPrefWidth(320);

        HBox root = new HBox(16, left, table);
        HBox.setHgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("tab-content");

        tab.setContent(root);
        return tab;
    }

    // =========================================================================
    // TAB 2 - CONTENT
    // =========================================================================
    private Tab buildContentsTab() {
        Tab tab = new Tab("Content");

        TableView<ContentRow> table = new TableView<>(contentRows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ContentRow, String> cId = col("ID", "id");
        TableColumn<ContentRow, String> cTitle = col("Title", "title");
        TableColumn<ContentRow, String> cType = col("Type", "type");
        TableColumn<ContentRow, String> cGenre = col("Genre", "genre");
        TableColumn<ContentRow, String> cYear = col("Year", "year");
        TableColumn<ContentRow, String> cRegion = col("Region", "region");
        table.getColumns().addAll(cId, cTitle, cType, cGenre, cYear, cRegion);

        TextField fId = field("ID (ex: C1)");
        TextField fTitle = field("Title");
        TextField fGenre = field("Genre (ex: Action)");
        TextField fYear = field("Year (ex: 2023)");
        TextField fRegion = field("Region (ex: US)");
        ComboBox<String> fType = new ComboBox<>(
                FXCollections.observableArrayList("Movie", "Series", "Documentary"));
        fType.setValue("Movie");
        fType.getStyleClass().add("field");

        Button btnAdd = btn("Add", "btn-primary");
        Label lblMsg = new Label();
        lblMsg.getStyleClass().add("msg-label");

        btnAdd.setOnAction(e -> {
            String id = fId.getText().trim();
            if (id.isEmpty() || fTitle.getText().trim().isEmpty()) {
                lblMsg.setText("ID and Title are required.");
                lblMsg.getStyleClass().setAll("msg-label", "msg-error");
                return;
            }
            try {
                int year = Integer.parseInt(fYear.getText().trim());
                Genre g = new Genre("G_" + fGenre.getText().trim(), fGenre.getText().trim());
                db.addGenre(g);

                Content c;
                switch (fType.getValue()) {
                    case "Series" -> c = new Series(id, LocalDateTime.now(),
                            fTitle.getText().trim(), year, g, fRegion.getText().trim(),
                            1, SeriesStatus.ONGOING);
                    case "Documentary" -> c = new Documentary(id, LocalDateTime.now(),
                            fTitle.getText().trim(), year, g, fRegion.getText().trim(),
                            0, 0, List.of(), 90, "General", null);
                    default -> c = new Movie(id, LocalDateTime.now(),
                            fTitle.getText().trim(), year, g, fRegion.getText().trim(),
                            120, null);
                }

                db.addContent(c);
                api.registerContent(id);
                refreshContents();
                lblMsg.setText("Content " + id + " added.");
                lblMsg.getStyleClass().setAll("msg-label", "msg-ok");
                clearFields(fId, fTitle, fGenre, fYear, fRegion);
            } catch (NumberFormatException ex) {
                lblMsg.setText("Invalid year.");
                lblMsg.getStyleClass().setAll("msg-label", "msg-error");
            }
        });

        Button btnRemove = btn("Remove Selected", "btn-danger");
        btnRemove.setOnAction(e -> {
            ContentRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            db.removeContent(sel.getId());
            refreshContents();
            lblMsg.setText("Content " + sel.getId() + " removed.");
            lblMsg.getStyleClass().setAll("msg-label", "msg-ok");
        });

        TextField fSearch = field("Title...");
        TextField fSearchGenre = field("Genre...");
        TextField fSearchYearFrom = field("Year from");
        TextField fSearchYearTo = field("Year to");
        Button btnSearch = btn("Search", "btn-secondary");
        Button btnReset = btn("Reset", "btn-secondary");

        btnSearch.setOnAction(e -> {
            contentRows.clear();
            String title = fSearch.getText().trim();
            String genre = fSearchGenre.getText().trim();
            String yFrom = fSearchYearFrom.getText().trim();
            String yTo = fSearchYearTo.getText().trim();

            List<Content> results = db.listContents();

            if (!title.isEmpty()) results = db.searchContentsByTitleSubstring(title);
            if (!genre.isEmpty()) results = db.searchByGenre(genre);
            if (!yFrom.isEmpty() && !yTo.isEmpty()) {
                try {
                    results = db.searchContentsReleasedBetween(
                            Integer.parseInt(yFrom), Integer.parseInt(yTo));
                } catch (NumberFormatException ignored) {
                }
            }

            results.stream().map(ContentRow::new).forEach(contentRows::add);
            lblMsg.setText("Search: " + contentRows.size() + " result(s).");
            lblMsg.getStyleClass().setAll("msg-label", "msg-info");
            logSearch("Content", "title=" + title + " genre=" + genre + " years=" + yFrom + "-" + yTo, contentRows.size());
        });
        btnReset.setOnAction(e -> {
            refreshContents();
            lblMsg.setText("");
        });

        GridPane form = formGrid(
                "ID", fId,
                "Title", fTitle,
                "Type", fType,
                "Genre", fGenre,
                "Year", fYear,
                "Region", fRegion
        );

        HBox formBtns = hbox(btnAdd, btnRemove);
        HBox searchRow = hbox(fSearch, fSearchGenre, fSearchYearFrom, fSearchYearTo, btnSearch, btnReset);

        VBox left = vbox(sectionTitle("Add Content"), form, formBtns, lblMsg,
                sectionTitle("Search"), searchRow,
                sectionTitle("Search History"), buildSearchHistoryPanel());
        left.setPrefWidth(340);

        HBox root = new HBox(16, left, table);
        HBox.setHgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("tab-content");

        tab.setContent(root);
        return tab;
    }

    // =========================================================================
    // TAB 3 - GRAPH
    // =========================================================================
    private Tab buildGraphTab() {
        Tab tab = new Tab("Graph");

        ListView<String> output = new ListView<>(graphLines);
        output.getStyleClass().add("graph-list");

        graphContextLabel = new Label("Select a user in the Users tab to view their relations here.");
        graphContextLabel.getStyleClass().addAll("msg-label", "msg-info");

        TextField fWatchUser = field("User ID");
        TextField fWatchContent = field("Content ID");
        TextField fWatchRating = field("Rating (0-10)");
        Button btnWatch = btn("Add Watch", "btn-primary");
        Label lblWatch = new Label();
        lblWatch.getStyleClass().add("msg-label");

        btnWatch.setOnAction(e -> {
            try {
                boolean ok = api.addWatch(
                        fWatchUser.getText().trim(),
                        fWatchContent.getText().trim(),
                        100,
                        Double.parseDouble(fWatchRating.getText().trim())
                );
                lblWatch.setText(ok ? "Watch registered." : "User or Content not found.");
                lblWatch.getStyleClass().setAll("msg-label", ok ? "msg-ok" : "msg-error");
                refreshGraph();
            } catch (NumberFormatException ex) {
                lblWatch.setText("Invalid rating.");
                lblWatch.getStyleClass().setAll("msg-label", "msg-error");
            }
        });

        TextField fFollower = field("Follower ID");
        TextField fFollowed = field("Followed ID");
        Button btnFollow = btn("Add Follow", "btn-primary");
        Label lblFollow = new Label();
        lblFollow.getStyleClass().add("msg-label");

        btnFollow.setOnAction(e -> {
            boolean ok = api.addFollow(
                    fFollower.getText().trim(),
                    fFollowed.getText().trim()
            );
            lblFollow.setText(ok ? "Follow registered." : "Invalid operation.");
            lblFollow.getStyleClass().setAll("msg-label", ok ? "msg-ok" : "msg-error");
            refreshGraph();
        });

        TextField fRecommend = field("User ID");
        Button btnRecommend = btn("Recommend", "btn-secondary");
        btnRecommend.setOnAction(e -> {
            GraphAlgorithms alg = new GraphAlgorithms(graph, db);
            List<Content> recs = alg.recommend(fRecommend.getText().trim());
            graphLines.clear();
            graphLines.add("-- Recommendations for " + fRecommend.getText().trim() + " --");
            if (recs.isEmpty()) graphLines.add("  (no recommendations)");
            recs.forEach(c -> graphLines.add("  " + c.getTitle() + " (" + c.getContentType() + ")"));
        });

        TextField fPathFrom = field("From (ID)");
        TextField fPathTo = field("To (ID)");
        Button btnPath = btn("Shortest Path", "btn-secondary");
        btnPath.setOnAction(e -> {
            GraphAlgorithms alg = new GraphAlgorithms(graph, db);
            List<Entity> path = alg.shortestPath(
                    fPathFrom.getText().trim(),
                    fPathTo.getText().trim()
            );
            graphLines.clear();
            graphLines.add("-- Path: " + fPathFrom.getText().trim() + " -> " + fPathTo.getText().trim() + " --");
            if (path.isEmpty()) graphLines.add("  (no path)");
            path.forEach(en -> graphLines.add("  -> " + en.getId() + "  " + en));
        });

        Button btnShow = btn("Show Graph State", "btn-secondary");
        btnShow.setOnAction(e -> refreshGraph());

        Button btnConnect = btn("Connectivity", "btn-secondary");
        btnConnect.setOnAction(e -> {
            GraphAlgorithms alg = new GraphAlgorithms(graph, db);
            boolean connected = alg.isConnected();
            graphLines.add("-- Connectivity --");
            graphLines.add(connected ? "  Connected." : "  NOT connected.");
        });

        VBox watchBox = vbox(
                sectionTitle("Register Watch"),
                hbox(fWatchUser, fWatchContent, fWatchRating, btnWatch),
                lblWatch
        );
        VBox followBox = vbox(
                sectionTitle("Register Follow"),
                hbox(fFollower, fFollowed, btnFollow),
                lblFollow
        );
        VBox recBox = vbox(
                sectionTitle("Recommendations"),
                hbox(fRecommend, btnRecommend)
        );
        VBox pathBox = vbox(
                sectionTitle("Shortest Path (Dijkstra)"),
                hbox(fPathFrom, fPathTo, btnPath)
        );
        VBox utilBox = vbox(
                sectionTitle("Utilities"),
                hbox(btnShow, btnConnect)
        );

        VBox controls = vbox(graphContextLabel, watchBox, followBox, recBox, pathBox, utilBox);
        controls.setPrefWidth(420);

        HBox root = new HBox(16, controls, output);
        HBox.setHgrow(output, Priority.ALWAYS);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("tab-content");

        tab.setContent(root);
        return tab;
    }

    // =========================================================================
    // R9b - Relacoes do Grafo (drill-down por user)
    // =========================================================================
    private void showUserRelationsOnGraph(String userId, boolean clearFirst) {
        if (userId == null || userId.isBlank() || !graph.containsVertex(userId)) return;
        if (clearFirst) graphLines.clear();

        if (graphContextLabel != null) {
            graphContextLabel.setText("Relations for user: " + userId);
            graphContextLabel.getStyleClass().setAll("msg-label", "msg-info");
        }

        graphLines.add("-- User Relations: " + userId + " --");

        // Following / followers
        List<Entity> neighbors = graph.getNeighbors(userId);
        graphLines.add("Following:");
        boolean anyFollow = false;
        for (Entity n : neighbors) {
            if (n instanceof User) {
                anyFollow = true;
                graphLines.add("  -> " + n.getId() + " " + n);
            }
        }
        if (!anyFollow) graphLines.add("  (none)");

        graphLines.add("Watched / interacted content:");
        boolean anyContent = false;
        for (var e : graph.getOutEdges(userId)) {
            Entity t = graph.getEntityByIndex(e.to());
            if (t instanceof Content) {
                anyContent = true;
                graphLines.add("  -> " + t.getId() + " " + ((Content) t).getTitle());
            }
        }
        if (!anyContent) graphLines.add("  (none)");

        graphLines.add("");

        // Se a aba do grafo ainda nao estiver selecionada, nao forcar; so preparar o output.
    }

    // =========================================================================
    // R9c - Historico de Pesquisas
    // =========================================================================
    private Node buildSearchHistoryPanel() {
        if (searchHistoryView == null) {
            searchHistoryView = new ListView<>(searchHistory);
            searchHistoryView.setPrefHeight(160);
            searchHistoryView.getStyleClass().add("graph-list");
        }

        Button clear = btn("Clear", "btn-secondary");
        clear.setOnAction(e -> searchHistory.clear());

        VBox box = new VBox(8, searchHistoryView, clear);
        return box;
    }

    private void logSearch(String scope, String query, int results) {
        String ts = LocalDateTime.now().withNano(0).toString().replace('T', ' ');
        String q = (query == null ? "" : query.trim());
        if (q.length() > 120) q = q.substring(0, 120) + "...";
        searchHistory.add(0, ts + " [" + scope + "] " + q + " -> " + results + " result(s)");
        // manter so as ultimas 25 para nao crescer infinito
        if (searchHistory.size() > 25) searchHistory.remove(searchHistory.size() - 1);
    }

    // =========================================================================
    // REFRESH
    // =========================================================================
    private void refreshUsers() {
        userRows.clear();
        db.listUsers().stream().map(UserRow::new).forEach(userRows::add);
    }

    private void refreshContents() {
        contentRows.clear();
        db.listContents().stream().map(ContentRow::new).forEach(contentRows::add);
    }

    private void refreshGraph() {
        graphLines.clear();
        graphLines.add("-- Graph State --");
        graphLines.add("  Vertices: " + graph.vertexCount());
        graphLines.add("  Edges:    " + graph.edgeCount());
        graphLines.add("");
        graphLines.add("-- Vertices --");
        graph.getAllVertexIds().forEach(id -> {
            Entity en = graph.getEntity(id);
            graphLines.add("  [" + id + "] " + (en != null ? en : "?"));
        });
        graphLines.add("");
        graphLines.add("-- Interactions --");
        graph.getInteractions().forEach(i -> graphLines.add("  " + i));
    }

    // =========================================================================
    // SAMPLE DATA
    // =========================================================================
    private void seedData() {
        Genre action = new Genre("G1", "Action");
        Genre drama = new Genre("G2", "Drama");
        db.addGenre(action);
        db.addGenre(drama);

        Artist dir = new Artist("A1", LocalDateTime.now(), "Christopher Nolan", "UK",
                LocalDate.of(1970, 7, 30), "M");
        db.addArtist(dir);

        Movie m1 = new Movie("M1", LocalDateTime.now(), "Inception", 2010, action, "US", 148, dir);
        Movie m2 = new Movie("M2", LocalDateTime.now(), "Interstellar", 2014, drama, "US", 169, dir);
        Series s1 = new Series("S1", LocalDateTime.now(), "Breaking Bad", 2008, drama, "US",
                5, SeriesStatus.ENDED);
        db.addContent(m1);
        db.addContent(m2);
        db.addContent(s1);

        User u1 = new User("U1", LocalDateTime.now(), "alice", "alice@ex.com", "");
        u1.setRegion("PT");
        u1.setRegistrationDate(LocalDate.of(2024, 1, 15));
        User u2 = new User("U2", LocalDateTime.now(), "bob", "bob@ex.com", "");
        u2.setRegion("US");
        u2.setRegistrationDate(LocalDate.of(2024, 3, 10));
        User u3 = new User("U3", LocalDateTime.now(), "carol", "carol@ex.com", "");
        u3.setRegion("PT");
        u3.setRegistrationDate(LocalDate.of(2024, 5, 20));
        db.addUser(u1);
        db.addUser(u2);
        db.addUser(u3);

        api.registerAllEntities();
        api.addWatch("U1", "M1", 100, 9.0);
        api.addWatch("U2", "M1", 80, 7.5);
        api.addWatch("U2", "M2", 100, 8.0);
        api.addFollow("U1", "U2");
        api.addFollow("U3", "U1");
        api.addArtistToContent("A1", "M1");
        api.addArtistToContent("A1", "M2");

        refreshUsers();
        refreshContents();
    }

    // =========================================================================
    // UI HELPERS
    // =========================================================================
    private <T> TableColumn<T, String> col(String label, String prop) {
        TableColumn<T, String> c = new TableColumn<>(label);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("field");
        return tf;
    }

    private Button btn(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().addAll("btn", styleClass);
        return b;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-title");
        return l;
    }

    private HBox hbox(javafx.scene.Node... nodes) {
        HBox hb = new HBox(8, nodes);
        hb.setAlignment(Pos.CENTER_LEFT);
        return hb;
    }

    private VBox vbox(javafx.scene.Node... nodes) {
        return new VBox(10, nodes);
    }

    private GridPane formGrid(Object... pairs) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.getStyleClass().add("form-grid");
        for (int i = 0; i < pairs.length; i += 2) {
            Label lbl = new Label((String) pairs[i]);
            lbl.getStyleClass().add("form-label");
            grid.add(lbl, 0, i / 2);
            grid.add((javafx.scene.Node) pairs[i + 1], 1, i / 2);
        }
        return grid;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    // =========================================================================
    // ROW MODELS (for JavaFX tables)
    // =========================================================================

    public static class UserRow {
        private final String id, username, email, region, registrationDate;

        public UserRow(User u) {
            this.id = u.getId();
            this.username = u.getUsername();
            this.email = u.getEmail();
            this.region = u.getRegion() != null ? u.getRegion() : "";
            this.registrationDate = u.getRegistrationDate() != null ? u.getRegistrationDate().toString() : "";
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRegion() { return region; }
        public String getRegistrationDate() { return registrationDate; }
    }

    public static class ContentRow {
        private final String id, title, type, genre, year, region;

        public ContentRow(Content c) {
            this.id = c.getId();
            this.title = c.getTitle();
            this.type = c.getContentType();
            this.genre = c.getGenre() != null ? c.getGenre().getName() : "";
            this.year = String.valueOf(c.getReleaseYear());
            this.region = c.getRegion() != null ? c.getRegion() : "";
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getGenre() { return genre; }
        public String getYear() { return year; }
        public String getRegion() { return region; }
    }
}
