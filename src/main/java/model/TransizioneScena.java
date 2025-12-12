/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author gruppo22
 */

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TransizioneScena {
public static void switchSceneEffect(Stage stage, String fxmlPath) {
    try {
        // 1. Carico il nuovo FXML
        FXMLLoader loader = new FXMLLoader(TransizioneScena.class.getResource(fxmlPath));
        Parent newRoot = loader.load();

        // 2. Lo rendo inizialmente invisibile (per fare l'effetto sorpresa)
        newRoot.setOpacity(0.0);

        // 3. Cambio secco (Sostituisce subito la root, risolve tutti i bug di gerarchia)
        stage.getScene().setRoot(newRoot);

        // 4. Imposto dimensioni e centro (1280x800)
        stage.setWidth(1280);
        stage.setHeight(835);
        stage.centerOnScreen(); // Metodo nativo di JavaFX per centrare, più affidabile

        // 5. Effetto Dissolvenza 
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

    } catch (IOException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
