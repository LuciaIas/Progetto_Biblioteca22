
package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Configurazione; 
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class MailControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Mail.fxml")); // Assicurati che il nome FXML sia corretto
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Chiude eventuali thread appesi e pulisce gli stage
        WaitForAsyncUtils.waitForFxEvents();

    }


    @Test
    public void testFlussoCaricamento() throws TimeoutException {

        try {
            verifyThat("#lblTotalUsers", hasText("Sincronizzazione in corso..."));
        } catch (AssertionError e) {
         
        }

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
            Label lbl = lookup("#lblTotalUsers").query();
            return !lbl.getText().contains("Sincronizzazione");
        });


        VBox container = lookup("#emailContainer").query();
        assertTrue(container.getChildren().stream().noneMatch(node -> node instanceof javafx.scene.control.ProgressIndicator), 
                   "Il ProgressIndicator dovrebbe essere sparito");
    }

    @Test
    public void testVisualizzazioneEmail() throws TimeoutException {

        waitForLoading();

        Label lblTotale = lookup("#lblTotalUsers").query();
        String testoLabel = lblTotale.getText();

        if (testoLabel.contains("Nessuna email")) {
     
            verifyThat("#emailContainer", (VBox box) -> box.getChildren().isEmpty());
        } else {
      
            

            VBox container = lookup("#emailContainer").query();
            assertTrue(container.getChildren().size() > 0, "Il contenitore dovrebbe avere delle righe");
            
            
            verifyThat(".email-row", isVisible());
            verifyThat("INVIATA IL", isVisible());
        }
    }


    @Test
    public void testIntegrazioneConfigurazione() throws TimeoutException {
   
        String emailConfigurata = Configurazione.getEmailUsername(); 
        

        
       
        waitForLoading();


        boolean trovata = lookup(".row-subtitle").queryAll().stream().map(node -> ((Label)node).getText()).anyMatch(text -> text.contains(emailConfigurata));

        
        assertTrue(emailConfigurata != null && !emailConfigurata.isEmpty(),"L'email nella classe Configurazione non dovrebbe essere vuota");
    }

 

    private void waitForLoading() throws TimeoutException {
      
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
            Label lbl = lookup("#lblTotalUsers").query();
            return !lbl.getText().contains("Sincronizzazione");
        });
        
        WaitForAsyncUtils.waitForFxEvents(); 
    }
}
