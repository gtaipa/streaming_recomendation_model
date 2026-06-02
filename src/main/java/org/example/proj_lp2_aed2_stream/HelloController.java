package org.example.proj_lp2_aed2_stream;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador JavaFX de exemplo associado ao ficheiro FXML inicial.
 */
public class HelloController {
    /** Texto apresentado na interface de exemplo. */
    @FXML
    private Label welcomeText;

    /**
     * Atualiza o texto quando o botao de exemplo e acionado.
     */
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
