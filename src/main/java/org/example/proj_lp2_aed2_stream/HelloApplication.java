package org.example.proj_lp2_aed2_stream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Aplicacao JavaFX de exemplo gerada pelo template inicial do projeto.
 */
public class HelloApplication extends Application {
    /**
     * Inicializa a janela JavaFX de exemplo.
     *
     * @param stage palco principal da aplicacao
     * @throws IOException se o ficheiro FXML nao puder ser carregado
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
