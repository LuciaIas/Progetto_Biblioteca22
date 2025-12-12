package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import main.Main; 
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class AccessoControllerTest extends ApplicationTest {

    private Connection testConnection;

    @BeforeEach
    public void setUpDB() throws SQLException {
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testaccessodb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection; 

        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS bibliotecario (password_ VARCHAR(255))");
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (testConnection != null && !testConnection.isClosed()) {
            try (Statement stmt = testConnection.createStatement()) {
                stmt.execute("DROP ALL OBJECTS");
            }
            testConnection.close();
        }
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        moveTo(0, 0);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Main.stage = stage; 
       

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Accesso.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testLoginFallito() {
        clickOn("#PassLogin").write("passwordSbagliata");
        clickOn("#LoginButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Password sbagliata"));
        clickOn("OK"); 
    }
    
 
    @Test
    public void testLoginRiuscito() {

        String passwordCorretta = "PasswordSegreta1!";
        DataBase.inserisciBibliotecario(passwordCorretta);

        
        clickOn("#PassLogin").write(passwordCorretta);
        clickOn("#LoginButton");

        
        // Se il login fallisse, apparirebbe un Alert con classe ".dialog-pane".
        // Noi verifichiamo che la lista di dialog-pane trovati sia VUOTA.
        
       
        
        boolean nessunErrore = lookup(".dialog-pane").queryAll().isEmpty();
        assertTrue(nessunErrore, "Il login dovrebbe riuscire senza mostrare finestre di errore");
        

    }
    

    @Test
    public void testRegistrazioneRiuscita() {
       
        clickOn("#SlidingButton");
        sleep(2000); 

       
        String passwordForte = "PasswordSicura1!";
        
     
        clickOn("#PassRegister").write(passwordForte);
        clickOn("#PassConRegister").write(passwordForte);

        
        clickOn("#RegisterButton");


        verifyThat("Registrazione effettuata 🚀", isVisible());


        clickOn("OK");

        //VERIFICA DATABASE
 
        assertTrue(DataBase.controllaEsistenzaBibliotecario(), 
            "Dopo la registrazione, il bibliotecario deve esistere nel database");
            

    }
    

    @Test
    public void testRegistrazioneSuccesso() {
        clickOn("#SlidingButton");
        sleep(2000); 

        String passValida = "Password123!";
        clickOn("#PassRegister").write(passValida);
        clickOn("#PassConRegister").write(passValida);

        clickOn("#RegisterButton");


        verifyThat("Registrazione effettuata 🚀", isVisible());
        
        
        clickOn("OK");

        assertTrue(DataBase.controllaEsistenzaBibliotecario(), "Il bibliotecario dovrebbe essere nel DB finto");
    }

    @Test
    public void testRegistrazionePasswordDebole() {
        clickOn("#SlidingButton");
        sleep(2000);

        clickOn("#PassRegister").write("ciao"); 
        clickOn("#RegisterButton");

        verifyThat(".dialog-pane", isVisible());

        verifyThat("Password non sicura", isVisible());
        
        clickOn("OK");
    }
}