module org.example.proj_lp2_aed2_stream {
    requires javafx.controls;
    requires javafx.fxml;
    requires edu.princeton.cs.algs4;


    opens org.example.proj_lp2_aed2_stream to javafx.fxml;
    exports org.example.proj_lp2_aed2_stream;

    opens streaming.db to javafx.fxml;
    exports streaming.db;

    opens streaming.model to javafx.fxml;
    exports streaming.model;

    opens streaming.core to javafx.fxml;
    exports streaming.core;
}
