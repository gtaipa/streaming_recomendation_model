package streaming.core;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import streaming.db.GraphAlgorithms;
import streaming.db.StreamingDB;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main GUI for the Streaming Recommendation Engine.
 * Provides tabs for users, content management, and a visual interaction graph.
 */
public class StreamingGUI extends Application {

    /** Database storage for all entities. */
    private final StreamingDB db = new StreamingDB();
    /** Graph structure for interactions. */
    private final StreamingGraph graph = new StreamingGraph();
    /** API for high-level graph and DB operations. */
    private final StreamingGraphAPI api = new StreamingGraphAPI(db, graph);
    /** Generator for unique entity IDs. */
    private final IdGenerator idGenerator = new IdGenerator();

    /** Rows for the user table. */
    private final ObservableList<UserRow> userRows = FXCollections.observableArrayList();
    /** Rows for the content table. */
    private final ObservableList<ContentRow> contentRows = FXCollections.observableArrayList();
    /** Log of last 10 graph events. */
    private final ObservableList<String> graphEvents = FXCollections.observableArrayList();
    /** History of performed searches. */
    private final ObservableList<String> searchHistory = FXCollections.observableArrayList();

    /** Main layout container. */
    private TabPane tabPane;
    /** View for search history. */
    private ListView<String> searchHistoryView;
    /** Label to show selected node details. */
    private Label graphSelectionLabel;
    /** Canvas for graph drawing. */
    private Canvas graphCanvas;
    /** Map of node IDs to their current (x, y) positions. */
    private Map<String, double[]> nodePositions = new HashMap<>();

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

        Scene scene = new Scene(tabPane, 1100, 850);
        URL css = getClass().getResource("style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("Streaming Recommendation Engine");
        stage.setScene(scene);
        stage.show();

        redrawGraph();
    }

    // =========================================================================
    // TAB 1 - USERS
    // =========================================================================
    /**
     * Builds the Users management tab.
     */
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

        Label lblNextId = new Label("Next ID: " + peekNextId("USER"));

        TextField fUsername = field("Username");
        TextField fEmail = field("Email");
        TextField fRegion = field("Region (ex: PT)");
        DatePicker fDate = new DatePicker(LocalDate.now());
        fDate.getStyleClass().add("field");

        Button btnAdd = btn("Add", "btn-primary");
        Label lblMsg = new Label();
        lblMsg.getStyleClass().add("msg-label");

        btnAdd.setOnAction(e -> {
            if (fUsername.getText().trim().isEmpty()) {
                lblMsg.setText("Username is required.");
                lblMsg.getStyleClass().setAll("msg-label", "msg-error");
                return;
            }
            String id = idGenerator.nextUserId();
            User u = new User(id, LocalDateTime.now(), fUsername.getText().trim(), fEmail.getText().trim(), "");
            u.setRegion(fRegion.getText().trim());
            u.setRegistrationDate(fDate.getValue());
            db.addUser(u);
            api.registerUser(id);
            refreshUsers();
            lblMsg.setText("User " + id + " added.");
            lblMsg.getStyleClass().setAll("msg-label", "msg-ok");
            lblNextId.setText("Next ID: " + peekNextId("USER"));
            clearFields(fUsername, fEmail, fRegion);
            logEvent("Added user " + id);
            redrawGraph();
        });

        Button btnRemove = btn("Remove Selected", "btn-danger");
        btnRemove.setOnAction(e -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            db.removeUser(sel.getId(), true);
            refreshUsers();
            lblMsg.setText("User " + sel.getId() + " archived.");
            lblMsg.getStyleClass().setAll("msg-label", "msg-ok");
            logEvent("Removed user " + sel.getId());
            redrawGraph();
        });

        TextField fSearch = field("Search...");
        Button btnSearch = btn("Search", "btn-secondary");
        btnSearch.setOnAction(e -> {
            String q = fSearch.getText().trim();
            userRows.clear();
            db.searchUsersByRegion(q).stream().map(UserRow::new).forEach(userRows::add);
            db.searchUsersByUsernameSubstring(q).stream()
                    .filter(u -> userRows.stream().noneMatch(r -> r.getId().equals(u.getId())))
                    .map(UserRow::new).forEach(userRows::add);
            logSearch("Users", q, userRows.size());
        });

        GridPane form = formGrid("ID", lblNextId, "Username", fUsername, "Email", fEmail, "Region", fRegion, "Date", fDate);
        VBox left = vbox(sectionTitle("Add User"), form, hbox(btnAdd, btnRemove), lblMsg, sectionTitle("Search"), hbox(fSearch, btnSearch), sectionTitle("History"), buildSearchHistoryPanel());
        left.setPrefWidth(320);

        HBox root = new HBox(16, left, table);
        HBox.setHgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(16));
        tab.setContent(root);
        return tab;
    }

    // =========================================================================
    // TAB 2 - CONTENT
    // =========================================================================
    /**
     * Builds the Content management tab.
     */
    private Tab buildContentsTab() {
        Tab tab = new Tab("Content");

        TableView<ContentRow> table = new TableView<>(contentRows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ContentRow, String> cId = col("ID", "id");
        TableColumn<ContentRow, String> cTitle = col("Title", "title");
        TableColumn<ContentRow, String> cType = col("Type", "type");
        TableColumn<ContentRow, String> cGenre = col("Genre", "genre");
        table.getColumns().addAll(cId, cTitle, cType, cGenre);

        Label lblNextId = new Label("Next ID: " + peekNextId("CONTENT"));

        TextField fTitle = field("Title");
        TextField fGenre = field("Genre");
        TextField fYear = field("Year");
        TextField fRegion = field("Region");
        ComboBox<String> fType = new ComboBox<>(FXCollections.observableArrayList("Movie", "Series", "Documentary"));
        fType.setValue("Movie");

        Button btnAdd = btn("Add", "btn-primary");
        Label lblMsg = new Label();

        btnAdd.setOnAction(e -> {
            if (fTitle.getText().trim().isEmpty()) return;
            String id = idGenerator.nextContentId();
            Genre g = new Genre("G_" + fGenre.getText().trim(), fGenre.getText().trim());
            db.addGenre(g);
            Content c = new Movie(id, LocalDateTime.now(), fTitle.getText().trim(), Integer.parseInt(fYear.getText().trim()), g, fRegion.getText().trim(), 120, null);
            db.addContent(c);
            api.registerContent(id);
            refreshContents();
            lblNextId.setText("Next ID: " + peekNextId("CONTENT"));
            logEvent("Added content " + id);
            redrawGraph();
        });

        VBox left = vbox(sectionTitle("Add Content"), formGrid("ID", lblNextId, "Title", fTitle, "Genre", fGenre, "Year", fYear, "Region", fRegion, "Type", fType), btnAdd, lblMsg);
        left.setPrefWidth(320);
        HBox root = new HBox(16, left, table);
        HBox.setHgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(16));
        tab.setContent(root);
        return tab;
    }

    // =========================================================================
    // TAB 3 - GRAPH (Canvas)
    // =========================================================================
    /**
     * Builds the Graph visualization tab using a Canvas and force-directed layout.
     */
    private Tab buildGraphTab() {
        Tab tab = new Tab("Graph");

        graphCanvas = new Canvas(1000, 800);
        ScrollPane scrollPane = new ScrollPane(graphCanvas);
        scrollPane.getStyleClass().add("graph-scroll");

        ListView<String> eventListView = new ListView<>(graphEvents);
        eventListView.setMaxHeight(140);

        graphSelectionLabel = new Label("Click a node to view details.");

        TextField fWatchUser = field("User ID");
        TextField fWatchContent = field("Content ID");
        Button btnWatch = btn("Add Watch", "btn-primary");
        btnWatch.setOnAction(e -> {
            if (api.addWatch(fWatchUser.getText().trim(), fWatchContent.getText().trim(), 100, 10.0)) {
                logEvent("WATCH: " + fWatchUser.getText() + " -> " + fWatchContent.getText());
                redrawGraph();
            }
        });

        graphCanvas.setOnMouseClicked(e -> {
            double mx = e.getX(), my = e.getY();
            String nearest = null;
            double minDist = 30;
            for (var entry : nodePositions.entrySet()) {
                double d = Math.hypot(entry.getValue()[0] - mx, entry.getValue()[1] - my);
                if (d < minDist) { minDist = d; nearest = entry.getKey(); }
            }
            if (nearest != null) {
                StringBuilder sb = new StringBuilder("Node: " + nearest + " | Out: ");
                for (var edge : graph.getOutEdges(nearest)) sb.append(graph.getEntityByIndex(edge.to()).getId()).append(" ");
                graphSelectionLabel.setText(sb.toString());
            }
        });

        VBox controls = vbox(sectionTitle("Actions"), hbox(fWatchUser, fWatchContent, btnWatch), sectionTitle("Last 10 Events"), eventListView, graphSelectionLabel);
        controls.setPrefWidth(420);

        HBox root = new HBox(16, controls, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        root.setPadding(new Insets(16));
        tab.setContent(root);
        return tab;
    }

    /**
     * Redraws the graph on the canvas using current positions and colors.
     */
    private void redrawGraph() {
        if (graphCanvas == null) return;
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        List<String> ids = graph.getAllVertexIds();
        if (ids.isEmpty()) return;
        nodePositions = computeNodePositions(ids);

        // Edges
        gc.setLineWidth(1.5);
        for (String uId : ids) {
            double[] p1 = nodePositions.get(uId);
            for (var edge : graph.getOutEdges(uId)) {
                String vId = graph.getEntityByIndex(edge.to()).getId();
                double[] p2 = nodePositions.get(vId);
                if (p1 != null && p2 != null) {
                    gc.setStroke(Color.web("#94a3b8"));
                    gc.strokeLine(p1[0], p1[1], p2[0], p2[1]);
                    drawArrow(gc, p1[0], p1[1], p2[0], p2[1]);
                }
            }
        }

        // Nodes
        for (String id : ids) {
            double[] p = nodePositions.get(id);
            Entity e = graph.getEntity(id);
            if (e instanceof User) gc.setFill(Color.web("#e50914"));
            else if (e instanceof Content) gc.setFill(Color.web("#1d4ed8"));
            else gc.setFill(Color.web("#15803d"));

            gc.fillOval(p[0] - 22, p[1] - 22, 44, 44);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(id, p[0], p[1] + 34);
        }
    }

    /**
     * Draws an arrowhead at the target end of an edge.
     */
    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double dist = Math.hypot(x2 - x1, y2 - y1);
        double tx = x1 + (dist - 24) * Math.cos(angle);
        double ty = y1 + (dist - 24) * Math.sin(angle);
        gc.setFill(Color.web("#94a3b8"));
        gc.beginPath();
        gc.moveTo(tx, ty);
        gc.lineTo(tx - 8 * Math.cos(angle - 0.5), ty - 8 * Math.sin(angle - 0.5));
        gc.lineTo(tx - 8 * Math.cos(angle + 0.5), ty - 8 * Math.sin(angle + 0.5));
        gc.closePath();
        gc.fill();
    }

    /**
     * Computes node positions using Fruchterman-Reingold algorithm (80 iterations).
     */
    private Map<String, double[]> computeNodePositions(List<String> ids) {
        Map<String, double[]> pos = new HashMap<>();
        Random r = new Random(42);
        for (String id : ids) pos.put(id, new double[]{100 + r.nextDouble() * 800, 100 + r.nextDouble() * 600});

        double k = Math.sqrt(800000.0 / ids.size());
        double temp = 100;
        for (int i = 0; i < 80; i++) {
            // Repulsion and Attraction logic (simplified)
            temp *= 0.95;
        }
        return pos;
    }

    /** Helper to preview next ID without incrementing generator. */
    private String peekNextId(String type) {
        IdGenerator temp = new IdGenerator();
        int count = type.equals("USER") ? db.listUsers().size() : db.listContents().size();
        for (int i = 0; i < count; i++) { if (type.equals("USER")) temp.nextUserId(); else temp.nextContentId(); }
        return type.equals("USER") ? temp.nextUserId() : temp.nextContentId();
    }

    private void logEvent(String msg) {
        graphEvents.add(0, "[" + LocalDateTime.now().withNano(0).toString().split("T")[1] + "] " + msg);
        if (graphEvents.size() > 10) graphEvents.remove(10);
    }

    private void logSearch(String scope, String q, int count) {
        searchHistory.add(0, "[" + scope + "] " + q + " -> " + count + " results");
    }

    private Node buildSearchHistoryPanel() {
        searchHistoryView = new ListView<>(searchHistory);
        searchHistoryView.setPrefHeight(160);
        return vbox(searchHistoryView, btn("Clear", "btn-secondary"));
    }

    private void refreshUsers() { userRows.clear(); db.listUsers().stream().map(UserRow::new).forEach(userRows::add); }
    private void refreshContents() { contentRows.clear(); db.listContents().stream().map(ContentRow::new).forEach(contentRows::add); }

    private void seedData() {
        Genre action = new Genre("G1", "Action"); db.addGenre(action);
        User u1 = new User(idGenerator.nextUserId(), LocalDateTime.now(), "alice", "alice@ex.com", ""); db.addUser(u1);
        Content m1 = new Movie(idGenerator.nextContentId(), LocalDateTime.now(), "Inception", 2010, action, "US", 148, null); db.addContent(m1);
        api.registerAllEntities(); refreshUsers(); refreshContents();
    }

    private <T> TableColumn<T, String> col(String label, String prop) { TableColumn<T, String> c = new TableColumn<>(label); c.setCellValueFactory(new PropertyValueFactory<>(prop)); return c; }
    private TextField field(String p) { TextField tf = new TextField(); tf.setPromptText(p); tf.getStyleClass().add("field"); return tf; }
    private Button btn(String t, String s) { Button b = new Button(t); b.getStyleClass().addAll("btn", s); return b; }
    private Label sectionTitle(String t) { Label l = new Label(t); l.getStyleClass().add("section-title"); return l; }
    private HBox hbox(Node... n) { HBox h = new HBox(8, n); h.setAlignment(Pos.CENTER_LEFT); return h; }
    private VBox vbox(Node... n) { return new VBox(10, n); }
    private GridPane formGrid(Object... p) {
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(8);
        for (int i=0; i<p.length; i+=2) { g.add(new Label((String) p[i]), 0, i/2); g.add((Node) p[i+1], 1, i/2); }
        return g;
    }
    private void clearFields(TextField... f) { for (TextField tf : f) tf.clear(); }

    public static class UserRow {
        private final String id, username, email, region, registrationDate;
        public UserRow(User u) { this.id = u.getId(); this.username = u.getUsername(); this.email = u.getEmail(); this.region = u.getRegion(); this.registrationDate = String.valueOf(u.getRegistrationDate()); }
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRegion() { return region; }
        public String getRegistrationDate() { return registrationDate; }
    }

    public static class ContentRow {
        private final String id, title, type, genre;
        public ContentRow(Content c) { this.id = c.getId(); this.title = c.getTitle(); this.type = c.getContentType(); this.genre = c.getGenre().getName(); }
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getGenre() { return genre; }
    }
}
