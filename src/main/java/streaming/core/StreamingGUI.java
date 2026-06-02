package streaming.core;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import streaming.db.GraphAlgorithms;
import streaming.db.StreamingDB;
import streaming.db.StreamingGraph;
import streaming.db.StreamingGraphAPI;
import streaming.model.*;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface grafica (R9) com duas vistas: Admin e Utilizador.
 * <p>
 * <b>Admin</b> (acesso direto): gere o catalogo (adicionar/remover filmes,
 * series, documentarios) e visualiza o grafo de relacoes.
 * <p>
 * <b>Utilizador</b> (login com username+password): ve o catalogo, interage
 * (watch/rate/click), segue outros utilizadores, ve recomendacoes e pesquisa.
 * <p>
 * Toda a logica passa pelo backend real: {@link StreamingDB}, {@link StreamingGraph},
 * {@link StreamingGraphAPI} e {@link GraphAlgorithms}.
 */
public class StreamingGUI extends Application {

    // --- Backend ---
    private StreamingDB db;
    private StreamingGraph graph;
    private StreamingGraphAPI api;
    private GraphAlgorithms alg;
    private IdGenerator idGen;

    // --- Estado da sessao ---
    private User currentUser;          // utilizador autenticado (null se ninguem)
    private BorderPane root;           // raiz; o centro troca entre vistas
    private Label lblStatus;

    // --- Componentes partilhados ---
    private Pane graphPane;
    private ComboBox<String> interactionFilter;
    private TableView<Content> adminContentTable;
    private TableView<Content> userCatalogTable;

    private static final String CSS =
            ".root { -fx-background-color: #141414; -fx-font-family: 'Arial'; }" +
                    ".tab-pane .tab-header-area .tab-header-background { -fx-background-color: #141414; }" +
                    ".tab { -fx-background-color: #1F1F1F; -fx-padding: 10 20 10 20; -fx-background-radius: 4 4 0 0; }" +
                    ".tab:selected { -fx-background-color: #E50914; }" +
                    ".tab .tab-label { -fx-text-fill: #A3A3A3; }" +
                    ".tab:selected .tab-label { -fx-text-fill: #FFFFFF; -fx-font-weight: bold; }" +
                    ".button { -fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 8 15 8 15; -fx-cursor: hand; }" +
                    ".button:hover { -fx-background-color: #B81D24; }" +
                    ".secondary-button { -fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; }" +
                    ".secondary-button:hover { -fx-background-color: #444444; }" +
                    ".nav-button { -fx-background-color: #1F1F1F; -fx-text-fill: #A3A3A3; -fx-font-weight: bold; -fx-background-radius: 0; -fx-padding: 12 25 12 25; }" +
                    ".nav-active { -fx-background-color: #E50914; -fx-text-fill: white; }" +
                    ".table-view { -fx-background-color: #1F1F1F; -fx-control-inner-background: #1F1F1F; -fx-background-radius: 6px; }" +
                    ".table-view .column-header, .table-view .filler { -fx-background-color: #262626; -fx-size: 35px; }" +
                    ".table-view .column-header .label { -fx-text-fill: #FFFFFF; -fx-font-weight: bold; }" +
                    ".table-row-cell { -fx-background-color: #1F1F1F; }" +
                    ".table-row-cell:odd { -fx-background-color: #1A1A1A; }" +
                    ".table-cell { -fx-text-fill: #FFFFFF; -fx-font-size: 13px; }" +
                    ".table-row-cell:filled:selected { -fx-background-color: #E50914; }" +
                    ".text-field, .password-field, .combo-box, .date-picker { -fx-background-color: #333333; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #A3A3A3; -fx-border-color: #444444; -fx-border-radius: 4px; -fx-background-radius: 4px; }" +
                    ".text-field, .password-field { -fx-padding: 8; }" +
                    ".text-field:focused, .password-field:focused { -fx-border-color: #E50914; }" +
                    ".combo-box .list-cell { -fx-text-fill: #FFFFFF; -fx-background-color: #333333; }" +
                    ".list-view { -fx-background-color: #1F1F1F; -fx-control-inner-background: #1F1F1F; }" +
                    ".list-cell { -fx-background-color: #1F1F1F; -fx-text-fill: #DDDDDD; -fx-font-size: 12px; }" +
                    ".list-cell:odd { -fx-background-color: #1A1A1A; }" +
                    ".label { -fx-text-fill: white; }" +
                    ".text-area { -fx-control-inner-background: #333333; -fx-text-fill: white; }";

    /**
     * Ponto de entrada da aplicacao.
     * @param args argumentos da linha de comandos (ignorados)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Inicializa backend, popula dados demo e mostra a vista Admin por defeito.
     * @param stage palco principal
     */
    @Override
    public void start(Stage stage) {
        db = new StreamingDB();
        graph = new StreamingGraph();
        api = new StreamingGraphAPI(db, graph);
        alg = new GraphAlgorithms(graph, db);
        idGen = new IdGenerator();
        new FileManager().importTxt("streaming_db.txt", db, api);
        api.registerAllEntities();

        stage.setTitle("Streaming Recommendation & Graph Engine");
        root = new BorderPane();
        root.setTop(buildNavBar());

        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #1F1F1F; -fx-border-color: #262626; -fx-border-width: 1 0 0 0;");
        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: #A3A3A3; -fx-font-size: 11px;");
        statusBar.getChildren().add(lblStatus);
        root.setBottom(statusBar);

        showAdminView();   // arranca na vista admin
        updateStatus();

        Scene scene = new Scene(root, 1150, 740);
        scene.getStylesheets().add("data:text/css," + CSS.replaceAll(" ", "%20"));
        stage.setScene(scene);
        stage.show();
    }

    // =====================================================================
    // BARRA DE NAVEGAÇÃO (alternar Admin / Utilizador)
    // =====================================================================

    private Button navAdmin, navUser;

    /**
     * Constroi a barra de navegacao com os botoes Admin e Utilizador.
     * @return barra de navegacao
     */
    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setStyle("-fx-background-color: #1F1F1F; -fx-border-color: #262626; -fx-border-width: 0 0 1 0;");

        Label logo = new Label("  STREAMING ENGINE  ");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        logo.setStyle("-fx-text-fill: #E50914;");
        logo.setAlignment(Pos.CENTER_LEFT);

        navAdmin = new Button("Admin");
        navAdmin.getStyleClass().add("nav-button");
        navAdmin.setOnAction(e -> showAdminView());

        navUser = new Button("Utilizador");
        navUser.getStyleClass().add("nav-button");
        navUser.setOnAction(e -> showUserEntry());

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        nav.getChildren().addAll(logo, spacer, navAdmin, navUser);
        nav.setAlignment(Pos.CENTER_LEFT);
        return nav;
    }

    /** Marca visualmente qual o botao de navegacao ativo. */
    private void setActiveNav(Button active) {
        navAdmin.getStyleClass().remove("nav-active");
        navUser.getStyleClass().remove("nav-active");
        active.getStyleClass().add("nav-active");
    }

    // =====================================================================
    // VISTA ADMIN: catalogo + grafo
    // =====================================================================

    /** Mostra a vista de administrador (gestao de catalogo + grafo). */
    private void showAdminView() {
        setActiveNav(navAdmin);
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Gestão de Catálogo", buildCatalogTab()),
                new Tab("Visualização do Grafo", buildGraphTab())
        );
        root.setCenter(tabs);
        refreshGraph();
    }

    /**
     * Separador de gestao de catalogo: adicionar/remover conteudos.
     * @return painel do separador
     */
    private SplitPane buildCatalogTab() {
        SplitPane split = new SplitPane();

        VBox left = new VBox(15);
        left.setPadding(new Insets(15));
        Label lbl = title("Catálogo de Conteúdos");
        adminContentTable = new TableView<>();
        adminContentTable.getColumns().add(strCol("ID", Content::getId));
        adminContentTable.getColumns().add(strCol("Título", Content::getTitle));
        adminContentTable.getColumns().add(strCol("Tipo", Content::getContentType));
        adminContentTable.getColumns().add(strCol("Género", c -> c.getGenre() == null ? "" : c.getGenre().getName()));
        adminContentTable.getColumns().add(strCol("Ano", c -> String.valueOf(c.getReleaseYear())));
        adminContentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        left.getChildren().addAll(lbl, adminContentTable);
        VBox.setVgrow(adminContentTable, Priority.ALWAYS);

        VBox right = new VBox(15);
        right.setPadding(new Insets(15));
        right.setMaxWidth(330);
        right.setStyle("-fx-background-color: #1F1F1F;");

        Label form = formTitle("Adicionar Conteúdo");
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("Filme", "Série", "Documentário");
        cbType.setValue("Filme");
        cbType.setMaxWidth(Double.MAX_VALUE);
        TextField txtTitle = new TextField(); txtTitle.setPromptText("Título");
        TextField txtYear = new TextField(); txtYear.setPromptText("Ano (ex: 2024)");
        ComboBox<String> cbGenre = new ComboBox<>();
        cbGenre.setPromptText("Género");
        cbGenre.setMaxWidth(Double.MAX_VALUE);
        for (Genre g : db.listGenres()) cbGenre.getItems().add(g.getName());

        Button btnAdd = wideButton("Adicionar");
        btnAdd.setOnAction(e -> {
            if (txtTitle.getText().isEmpty() || txtYear.getText().isEmpty()) return;
            int year;
            try { year = Integer.parseInt(txtYear.getText()); }
            catch (NumberFormatException ex) { return; }
            Genre g = findGenreByName(cbGenre.getValue());
            String id = idGen.nextContentId();
            Content c;
            switch (cbType.getValue()) {
                case "Série" -> c = new Series(id, LocalDateTime.now(), txtTitle.getText(),
                        year, g, "Global", 1, SeriesStatus.ONGOING);
                case "Documentário" -> c = new Documentary(id, LocalDateTime.now(), txtTitle.getText(),
                        year, g, "Global", 0.0, 0, new ArrayList<>(), 90, "Geral", null);
                default -> c = new Movie(id, LocalDateTime.now(), txtTitle.getText(),
                        year, g, "Global", 120, null);
            }
            db.addContent(c);
            api.registerContent(id);
            refreshCatalog(); refreshGraph(); updateStatus();
            txtTitle.clear(); txtYear.clear();
        });

        Button btnRemove = new Button("Remover Selecionado");
        btnRemove.getStyleClass().add("secondary-button");
        btnRemove.setMaxWidth(Double.MAX_VALUE);
        btnRemove.setOnAction(e -> {
            Content sel = adminContentTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                db.removeContent(sel.getId());
                api.removeContentFromGraph(sel.getId());
                refreshCatalog(); refreshGraph(); updateStatus();
            }
        });

        right.getChildren().addAll(form, cbType, txtTitle, txtYear, cbGenre, btnAdd, new Separator(), btnRemove);
        split.getItems().addAll(left, right);
        split.setDividerPositions(0.7);
        refreshCatalog();
        return split;
    }

    /** Recarrega a tabela de catalogo do admin. */
    private void refreshCatalog() {
        if (adminContentTable != null) adminContentTable.getItems().setAll(db.listContents());
    }

    // =====================================================================
    // VISTA UTILIZADOR: login -> dashboard
    // =====================================================================

    /** Mostra o ecra de login do utilizador (ou o dashboard se ja autenticado). */
    private void showUserEntry() {
        setActiveNav(navUser);
        if (currentUser != null) {
            showUserDashboard();
        } else {
            root.setCenter(buildLoginScreen());
        }
    }

    /**
     * Constroi o ecra de login (username + password).
     * @return painel de login
     */
    private VBox buildLoginScreen() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));

        Label lbl = new Label("Iniciar Sessão");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");
        txtUser.setMaxWidth(280);
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.setMaxWidth(280);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #E50914;");

        Button btnLogin = new Button("Entrar");
        btnLogin.setMaxWidth(280);
        btnLogin.setOnAction(e -> {
            User u = authenticate(txtUser.getText(), txtPass.getText());
            if (u != null) {
                currentUser = u;
                showUserDashboard();
            } else {
                lblError.setText("Credenciais inválidas.");
            }
        });

        Label hint = new Label("Demo: tiago_silva / 1234");
        hint.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");

        box.getChildren().addAll(lbl, txtUser, txtPass, btnLogin, lblError, hint);
        return box;
    }

    /**
     * Autentica um utilizador por username e password.
     * A password introduzida e transformada em hash (SHA-256) antes de
     * ser comparada com o hash guardado, garantindo que a password em
     * texto simples nunca e armazenada nem comparada diretamente.
     *
     * @param username nome introduzido
     * @param password password introduzida (texto simples)
     * @return o {@link User} se as credenciais baterem certo, senao {@code null}
     */
    private User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        String hashed = hashPassword(password);
        for (User u : db.listUsers()) {
            if (username.equals(u.getUsername()) && hashed.equals(u.getPasswordHash())) {
                return u;
            }
        }
        return null;
    }

    /** Mostra o painel principal do utilizador autenticado. */
    private void showUserDashboard() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Catálogo & Interações", buildCatalogInteractTab()),
                new Tab("Social & Recomendações", buildSocialTab()),
                new Tab("Pesquisas", buildSearchTab())
        );
        root.setCenter(tabs);
        updateStatus();
    }

    /**
     * Separador onde o utilizador ve o catalogo e interage (watch/rate/click).
     * @return painel do separador
     */
    private VBox buildCatalogInteractTab() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(15));

        Label welcome = title("Bem-vindo, " + currentUser.getUsername());
        Button logout = new Button("Terminar Sessão");
        logout.getStyleClass().add("secondary-button");
        logout.setOnAction(e -> { currentUser = null; showUserEntry(); });
        HBox header = new HBox(15, welcome, logout);
        header.setAlignment(Pos.CENTER_LEFT);

        userCatalogTable = new TableView<>();
        userCatalogTable.getColumns().add(strCol("ID", Content::getId));
        userCatalogTable.getColumns().add(strCol("Título", Content::getTitle));
        userCatalogTable.getColumns().add(strCol("Tipo", Content::getContentType));
        userCatalogTable.getColumns().add(strCol("Género", c -> c.getGenre() == null ? "" : c.getGenre().getName()));
        userCatalogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userCatalogTable.getItems().setAll(db.listContents());

        // Barra de interacao com o conteudo selecionado
        HBox actionBar = new HBox(12);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        actionBar.setPadding(new Insets(12));
        actionBar.setStyle("-fx-background-color: #1F1F1F; -fx-background-radius: 6px;");

        Button btnWatch = new Button("Ver (Watch)");
        Button btnClick = new Button("Clicar (Click)");
        TextField txtRating = new TextField(); txtRating.setPromptText("Nota 0-10"); txtRating.setMaxWidth(100);
        Button btnRate = new Button("Classificar (Rate)");
        Label lblFeedback = new Label();
        lblFeedback.setStyle("-fx-text-fill: #00E676;");

        btnWatch.setOnAction(e -> {
            Content c = userCatalogTable.getSelectionModel().getSelectedItem();
            if (c != null && api.addWatch(currentUser.getId(), c.getId(), 100, 0)) {
                lblFeedback.setText("Viu: " + c.getTitle());
                updateStatus();
            }
        });
        btnClick.setOnAction(e -> {
            Content c = userCatalogTable.getSelectionModel().getSelectedItem();
            if (c != null && api.addClick(currentUser.getId(), c.getId())) {
                lblFeedback.setText("Clicou: " + c.getTitle());
                updateStatus();
            }
        });
        btnRate.setOnAction(e -> {
            Content c = userCatalogTable.getSelectionModel().getSelectedItem();
            if (c == null) return;
            try {
                double r = Double.parseDouble(txtRating.getText());
                if (api.addRating(currentUser.getId(), c.getId(), r)) {
                    lblFeedback.setText("Classificou " + c.getTitle() + " com " + r);
                    updateStatus();
                }
            } catch (NumberFormatException ignored) { }
        });

        actionBar.getChildren().addAll(btnWatch, btnClick, txtRating, btnRate, lblFeedback);

        pane.getChildren().addAll(header, userCatalogTable, actionBar);
        VBox.setVgrow(userCatalogTable, Priority.ALWAYS);
        return pane;
    }

    /**
     * Separador social: seguir utilizadores e ver recomendacoes.
     * @return painel do separador
     */
    private VBox buildSocialTab() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(15));

        Label lblFollow = title("Seguir Utilizadores");
        ComboBox<String> cbUsers = new ComboBox<>();
        for (User u : db.listUsers())
            if (!u.getId().equals(currentUser.getId())) cbUsers.getItems().add(u.getUsername());
        cbUsers.setPromptText("Escolhe um utilizador");
        Button btnFollow = new Button("Seguir");
        Label lblFollowMsg = new Label();
        lblFollowMsg.setStyle("-fx-text-fill: #00E676;");
        btnFollow.setOnAction(e -> {
            User target = findUserByUsername(cbUsers.getValue());
            if (target != null && api.addFollow(currentUser.getId(), target.getId())) {
                lblFollowMsg.setText("Agora segues " + target.getUsername());
                updateStatus();
            }
        });
        HBox followBar = new HBox(12, cbUsers, btnFollow, lblFollowMsg);
        followBar.setAlignment(Pos.CENTER_LEFT);

        Label lblFollowing = title("Quem segues");
        ListView<String> followingList = new ListView<>();
        followingList.setPrefHeight(120);
        for (User u : api.getFollowing(currentUser.getId()))
            followingList.getItems().add(u.getUsername());

        Label lblRec = title("Recomendações para ti (R8d)");
        ListView<String> recList = new ListView<>();
        recList.setPrefHeight(150);
        Button btnRec = new Button("Gerar Recomendações");
        btnRec.setOnAction(e -> {
            recList.getItems().clear();
            List<Content> recs = alg.recommend(currentUser.getId());
            if (recs.isEmpty()) recList.getItems().add("Sem recomendações (segue alguém e vê conteúdos primeiro).");
            else for (Content c : recs) recList.getItems().add(c.getTitle() + "  [" + c.getContentType() + "]");
        });

        pane.getChildren().addAll(lblFollow, followBar, lblFollowing, followingList, lblRec, btnRec, recList);
        return pane;
    }

    /**
     * Separador de pesquisas para o utilizador.
     * @return painel do separador
     */
    private VBox buildSearchTab() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(15));

        Label lbl = title("Pesquisar Conteúdos");
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        TextField txtQuery = new TextField(); txtQuery.setPromptText("Título contém...");
        Button btnSearch = new Button("Pesquisar");
        Label lblCount = new Label();
        lblCount.setStyle("-fx-text-fill: #A3A3A3;");
        bar.getChildren().addAll(txtQuery, btnSearch, lblCount);

        TableView<Content> results = new TableView<>();
        results.getColumns().add(strCol("ID", Content::getId));
        results.getColumns().add(strCol("Título", Content::getTitle));
        results.getColumns().add(strCol("Tipo", Content::getContentType));
        results.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label lblHist = title("Histórico de Pesquisas");
        ListView<String> history = new ListView<>();
        history.setPrefHeight(120);

        btnSearch.setOnAction(e -> {
            List<Content> found = db.searchContentsByTitleSubstring(txtQuery.getText());
            results.getItems().setAll(found);
            lblCount.setText(found.size() + " resultados");
            history.getItems().add(0, String.format("[%s] '%s' -> %d",
                    LocalDateTime.now().toString().substring(11, 19), txtQuery.getText(), found.size()));
        });

        pane.getChildren().addAll(lbl, bar, results, lblHist, history);
        VBox.setVgrow(results, Priority.ALWAYS);
        return pane;
    }

    // =====================================================================
    // GRAFO (vista admin)
    // =====================================================================

    /**
     * Separador de visualizacao do grafo (vista admin).
     * @return painel do separador
     */
    private BorderPane buildGraphTab() {
        BorderPane pane = new BorderPane();

        HBox top = new HBox(12);
        top.setPadding(new Insets(12));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #1F1F1F;");
        interactionFilter = new ComboBox<>();
        interactionFilter.getItems().addAll("Mostrar Tudo", "WATCH", "RATE", "CLICK", "FOLLOW");
        interactionFilter.setValue("Mostrar Tudo");
        interactionFilter.setOnAction(e -> refreshGraph());
        Button btnRefresh = new Button("Recalcular");
        btnRefresh.getStyleClass().add("secondary-button");
        btnRefresh.setOnAction(e -> refreshGraph());
        Button btnConnect = new Button("Verificar Conectividade");
        btnConnect.getStyleClass().add("secondary-button");
        btnConnect.setOnAction(e -> {
            boolean conn = alg.isConnected();
            lblStatus.setText("Grafo conexo: " + (conn ? "SIM" : "NÃO")
                    + " | " + graph.vertexCount() + " vértices, " + graph.edgeCount() + " arestas");
        });
        top.getChildren().addAll(new Label("Filtrar relações:"), interactionFilter, btnRefresh, btnConnect);

        graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #0d0d0d;");
        graphPane.setPrefSize(1200, 800);
        ScrollPane scroll = new ScrollPane(graphPane);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: #0d0d0d; -fx-border-color: transparent;");

        pane.setTop(top);
        pane.setCenter(scroll);
        return pane;
    }

    /** Redesenha o grafo a partir das interacoes reais do backend. */
    private void refreshGraph() {
        if (graphPane == null) return;
        graphPane.getChildren().clear();
        String filtro = interactionFilter != null ? interactionFilter.getValue() : "Mostrar Tudo";

        double colUsers = 150, colContents = 500, colArtists = 850;
        List<User> users = db.listUsers();
        List<Content> contents = db.listContents();
        List<Artist> artists = db.listArtists();
        Map<String, double[]> pos = new HashMap<>();

        Text tU = new Text(colUsers - 40, 30, "UTILIZADORES");
        tU.setFill(Color.web("#00E5FF")); tU.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text tC = new Text(colContents - 30, 30, "CONTEÚDOS");
        tC.setFill(Color.web("#00E676")); tC.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text tA = new Text(colArtists - 25, 30, "ARTISTAS");
        tA.setFill(Color.web("#FF1744")); tA.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        graphPane.getChildren().addAll(tU, tC, tA);

        for (int i = 0; i < users.size(); i++) {
            double y = 70 + i * 75; User u = users.get(i);
            pos.put(u.getId(), new double[]{colUsers, y});
            drawNode(colUsers, y, u.getId(), u.getUsername(), Color.web("#00E5FF"));
        }
        for (int i = 0; i < contents.size(); i++) {
            double y = 70 + i * 75; Content c = contents.get(i);
            pos.put(c.getId(), new double[]{colContents, y});
            drawNode(colContents, y, c.getId(), c.getTitle(), Color.web("#00E676"));
        }
        for (int i = 0; i < artists.size(); i++) {
            double y = 70 + i * 75; Artist a = artists.get(i);
            pos.put(a.getId(), new double[]{colArtists, y});
            drawNode(colArtists, y, a.getId(), a.getName(), Color.web("#FF1744"));
        }

        for (Interaction it : graph.getInteractions()) {
            if (it.getUser() == null) continue;
            Entity target = it.getTargetEntity();
            if (target == null) continue;
            String type = it.getType() != null ? it.getType().toString() : "WATCH";
            if (!filtro.equals("Mostrar Tudo") && !filtro.equals(type)) continue;
            double[] from = pos.get(it.getUser().getId());
            double[] to = pos.get(target.getId());
            if (from == null || to == null) continue;
            if (type.equals("FOLLOW")) {
                drawCurve(from[0], from[1], to[1], Color.web("#00E5FF"));
            } else {
                Color c = type.equals("RATE") ? Color.web("#E50914")
                        : type.equals("CLICK") ? Color.web("#FFB300")
                        : Color.web("rgba(255,255,255,0.35)");
                double w = type.equals("RATE") ? 4 : 2;
                drawArrow(from[0], from[1], to[0], to[1], c, w);
            }
        }
    }

    private void drawNode(double x, double y, String id, String name, Color cor) {
        Circle circle = new Circle(x, y, 20);
        circle.setFill(cor);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(1.5);
        circle.setOnMouseEntered(e -> circle.setRadius(24));
        circle.setOnMouseExited(e -> circle.setRadius(20));
        Tooltip.install(circle, new Tooltip("ID: " + id + "\nNome: " + name));
        String shown = name == null ? id : (name.length() > 10 ? name.substring(0, 8) + ".." : name);
        Text label = new Text(shown);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        label.setFill(Color.WHITE);
        label.setX(x - 18); label.setY(y + 33);
        graphPane.getChildren().addAll(circle, label);
    }

    private void drawArrow(double fromX, double fromY, double toX, double toY, Color cor, double esp) {
        Line line = new Line(fromX + 20, fromY, toX - 20, toY);
        line.setStroke(cor); line.setStrokeWidth(esp);
        double angle = Math.atan2(toY - fromY, toX - fromX);
        double len = 12, tipX = toX - 20, tipY = toY;
        double x1 = tipX - len * Math.cos(angle - Math.toRadians(20));
        double y1 = tipY - len * Math.sin(angle - Math.toRadians(20));
        double x2 = tipX - len * Math.cos(angle + Math.toRadians(20));
        double y2 = tipY - len * Math.sin(angle + Math.toRadians(20));
        Polygon arrow = new Polygon(tipX, tipY, x1, y1, x2, y2);
        arrow.setFill(cor);
        graphPane.getChildren().addAll(line, arrow);
    }

    private void drawCurve(double x, double fromY, double toY, Color cor) {
        QuadCurve curve = new QuadCurve();
        curve.setStartX(x - 20); curve.setStartY(fromY);
        curve.setEndX(x - 20); curve.setEndY(toY);
        curve.setControlX(x - 70); curve.setControlY((fromY + toY) / 2);
        curve.setFill(null); curve.setStroke(cor); curve.setStrokeWidth(2);
        curve.getStrokeDashArray().addAll(4d, 4d);
        graphPane.getChildren().add(curve);
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    /** Interface funcional para extrair uma String de uma entidade. */
    private interface StrFn<T> { String apply(T t); }

    /**
     * Cria uma coluna de tabela que mostra uma String extraida da entidade.
     * @param header titulo da coluna
     * @param fn     funcao que extrai o valor
     * @return coluna configurada
     */
    private <T> TableColumn<T, String> strCol(String header, StrFn<T> fn) {
        TableColumn<T, String> col = new TableColumn<>(header);
        col.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(fn.apply(c.getValue())));
        return col;
    }

    private Genre findGenreByName(String name) {
        if (name == null) return null;
        for (Genre g : db.listGenres()) if (name.equals(g.getName())) return g;
        return null;
    }

    private User findUserByUsername(String username) {
        if (username == null) return null;
        for (User u : db.listUsers()) if (username.equals(u.getUsername())) return u;
        return null;
    }

    /**
     * Transforma uma password em texto simples no seu hash SHA-256 (em hexadecimal).
     * O mesmo input produz sempre o mesmo output, o que permite comparar
     * passwords sem nunca as guardar em texto simples.
     *
     * @param password a password em texto simples
     * @return o hash SHA-256 em hexadecimal
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 existe sempre em qualquer JVM; fallback defensivo
            return password;
        }
    }

    private void updateStatus() {
        lblStatus.setText("Vértices: " + graph.vertexCount()
                + " | Arestas: " + graph.edgeCount()
                + " | Interações: " + graph.getInteractions().size()
                + " | Consistência: " + (db.validateConsistency() ? "OK" : "FALHA")
                + (currentUser != null ? " | Sessão: " + currentUser.getUsername() : ""));
    }

    private Label title(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        return l;
    }

    private Label formTitle(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        l.setStyle("-fx-text-fill: #E50914;");
        return l;
    }

    private Button wideButton(String t) {
        Button b = new Button(t);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }
}
