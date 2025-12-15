/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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

/**
 * @brief Classe di utilità per gestire le transizioni tra scene in JavaFX.
 * 
 * Questa classe fornisce metodi statici per cambiare scena in uno Stage
 * applicando effetti visivi come la dissolvenza.
 * 
 * Viene utilizzata per gestire transizioni fluide tra diverse interfacce dell'applicazione.
 * 
 * @author gruppo22
 */
public class TransizioneScena {
    
    /**
     * @brief Cambia la scena corrente di un Stage con effetto di dissolvenza.
     * 
     * Il metodo carica il nuovo FXML, imposta l'opacità a 0,
     * sostituisce la root della scena, centra la finestra e applica
     * un effetto di fade-in per rendere visibile la nuova scena.
     * 
     * @param stage lo Stage su cui applicare la transizione
     * @param fxmlPath il percorso del file FXML da caricare
     */    
    public static void switchSceneEffect(Stage stage, String fxmlPath) {
    try {
        //Carico il nuovo FXML
        FXMLLoader loader = new FXMLLoader(TransizioneScena.class.getResource(fxmlPath));
        Parent newRoot = loader.load();
        //Lo rendo inizialmente invisibile (per fare l'effetto sorpresa)
        newRoot.setOpacity(0.0);
        //Cambio secco (Sostituisce subito la root, risolve tutti i bug di gerarchia)
        stage.getScene().setRoot(newRoot);
        //Imposto dimensioni e centro (1280x800)
        stage.setWidth(1280);
        stage.setHeight(835);
        stage.centerOnScreen(); 
        //Effetto Dissolvenza 
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
