package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterEach;

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

public class BlacklistControllerTest extends ApplicationTest {
    private Connection testConnection;

    public void setUpDB() throws SQLException {        
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testblacklistdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection;
        try (Statement stmt = testConnection.createStatement()) {           
            stmt.execute("CREATE TABLE IF NOT EXISTS utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(50), " +
                    "cognome VARCHAR(50), " +
                    "mail VARCHAR(50), " +
                    "Bloccato BOOLEAN)");
            stmt.execute("INSERT INTO utenti VALUES ('1', 'Pasquale', 'Giovane', 'mail1@test.com', true)");           
            stmt.execute("INSERT INTO utenti VALUES ('2', 'Sistemone', 'Pazzo', 'mail2@test.com', true)");            
            stmt.execute("INSERT INTO utenti VALUES ('3', 'Giuseppe', 'Nebbia', 'mail3@test.com', false)");
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
        setUpDB();
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testblacklistdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection;
        try (Statement stmt = testConnection.createStatement()) {
            // Creiamo la tabella utenti
            stmt.execute("CREATE TABLE IF NOT EXISTS utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(50), " +
                    "cognome VARCHAR(50), " +
                    "mail VARCHAR(50), " +
                    "Bloccato BOOLEAN)");}        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/BlackList.fxml")); 
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    //CARICAMENTO INIZIALE
    @Test
    public void testInizializzazioneLista() {        
        // Cerchiamo il contenitore VBox (fx:id="blacklistContainer")
        VBox container = lookup("#blacklistContainer").query();       
        // Deve contenere 2 card
        assertEquals(2, container.getChildren().size(), "La lista deve mostrare solo gli utenti bloccati (2)");       
        // Verifica che la Label del totale sia corretta 
        Label lblTotale = lookup("#lblTotalBlocked").query();
        assertTrue(lblTotale.getText().contains("2"), "Il contatore totale deve segnare 2");
    }

    //RICERCA 
    @Test
    public void testRicercaUtente() {        
        clickOn("#searchUser").write("Sistemone");      
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        // Ora la lista deve contenere solo 1 elemento 
        VBox container = lookup("#blacklistContainer").query();
        assertEquals(1, container.getChildren().size(), "La ricerca deve filtrare e mostrare solo 1 utente");
        verifyThat("Sistemone Pazzo", isVisible());
    }

    //SBLOCCO SINGOLO UTENTE
    @Test
    public void testSbloccaSingolo() {        
        clickOn("#searchUser").write("Pasquale");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn("SBLOCCA");
        model.dataclass.Utente utenteDb = DataBase.cercaUtente("1");
        assertFalse(utenteDb.isBloccato(), "L'utente Pasquale deve essere stato sbloccato nel DB");
        VBox container = lookup("#blacklistContainer").query();
        assertEquals(1, container.getChildren().size(), "La lista deve aggiornarsi mostrando gli altri utenti bloccati");
    }

    //SBLOCCA TUTTI
    @Test
    public void testSbloccaTutti() {
        // Clicchiamo sul bottone "Sblocca tutti" (fx:id="UnLockAllButton")
        clickOn("#UnLockAllButton");
        // VERIFICA DATABASE
        assertFalse(DataBase.cercaUtente("1").isBloccato());
        assertFalse(DataBase.cercaUtente("2").isBloccato());
        // Il contenitore deve essere vuoto
        VBox container = lookup("#blacklistContainer").query();
        assertEquals(0, container.getChildren().size(), "La lista deve essere vuota dopo 'Sblocca Tutti'");
    }
}