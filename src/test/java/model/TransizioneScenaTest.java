package model;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class) 
public class TransizioneScenaTest {

    private Stage stage;


    @Start
    public void start(Stage stage) {
        this.stage = stage;

        stage.setScene(new Scene(new StackPane(), 100, 100));
        stage.show();
    }

    @Test
    public void testSwitchSceneEffect_Successo(FxRobot robot) {

        assertTrue(stage.getWidth() < 1000, "La larghezza iniziale dovrebbe essere piccola");


        robot.interact(() -> {

            TransizioneScena.switchSceneEffect(stage, "/TestView/test.fxml");
        });


        assertEquals(1280, stage.getWidth(), 1.0, "La larghezza dovrebbe essere cambiata a 1280");
        
        
        assertEquals(835, stage.getHeight(), 1.0, "L'altezza dovrebbe essere cambiata a 835");
        
        
        assertNotNull(stage.getScene().getRoot(), "La nuova root non dovrebbe essere null");
    }

    @Test
    public void testSwitchSceneEffect_FileInesistente(FxRobot robot) {

        
        double larghezzaIniziale = stage.getWidth();

        robot.interact(() -> {
            TransizioneScena.switchSceneEffect(stage, "/file_che_non_esiste.fxml");
        });


        assertEquals(larghezzaIniziale, stage.getWidth(), "Se il file manca, le dimensioni non dovrebbero cambiare");
    }
}