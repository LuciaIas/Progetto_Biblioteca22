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
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class AccessoControllerTest extends ApplicationTest {

    private Connection testConnection;

    @Override
    public void start(Stage stage) throws Exception {
        Main.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Accesso.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @BeforeEach
    public void setUpDB() throws SQLException {

        testConnection = DriverManager.getConnection("jdbc:h2:mem:testaccessodb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection; 

        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS bibliotecario");

       
            stmt.execute("CREATE TABLE bibliotecario (" +
                         "password_ VARCHAR(255) PRIMARY KEY, " + 
                         "email VARCHAR(255))");
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
    }

    @Test
    public void testLoginRiuscito() throws SQLException {
        System.out.println("TEST: Login Riuscito");
        
        String email = "admin@test.it";
        String pass = "PasswordSegreta1!";

        DataBase.inserisciBibliotecario(pass, email);

  
        clickOn("#PassLogin").write(pass);
        

        try {
            clickOn("#LoginButton");
        } catch (Exception e) {
          
        }
        
     
        boolean erroreVisibile = !lookup(".dialog-pane").queryAll().isEmpty();
        
        if (erroreVisibile) {
            String testo = lookup(".dialog-pane .content").queryLabeled().getText();
            System.err.println("FALLITO: È apparso l'Alert -> " + testo);
        }

        assertFalse(erroreVisibile, "Il login è fallito (Alert visibile)");
    }

    @Test
    public void testLoginFallito() {
        System.out.println("TEST: Login Fallito");
        clickOn("#PassLogin").write("sbagliata");
        clickOn("#LoginButton");
        

        verifyThat(".dialog-pane", isVisible());
        clickOn("OK"); 
    }

    @Test
    public void testRegistrazioneRiuscita() throws SQLException {
        System.out.println("TEST: Registrazione Riuscita");
        
        clickOn("#SlidingButton");
        sleep(1200); 

        clickOn("#emailField").write("nuovo@test.it");
        String pass = "PasswordSicura1!";
        clickOn("#PassRegister").write(pass);
        clickOn("#PassConRegister").write(pass);
        
    
        try {
            clickOn("#RegisterButton");
        } catch (Exception e) {}

  
        verifyThat("Registrazione effettuata 🚀", isVisible());
        clickOn("OK");
    }
}