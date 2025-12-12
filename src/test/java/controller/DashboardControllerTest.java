package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import main.Main;
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

public class DashboardControllerTest extends ApplicationTest {

    private Connection testConnection;


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
        
        initDB();

        
        Main.stage = stage;

        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Dashboard.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    private void initDB() throws SQLException {
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdashboarddb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection;

        try (Statement stmt = testConnection.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS libri (" +
                    "isbn VARCHAR(20), titolo VARCHAR(100), editore VARCHAR(50), " +
                    "anno_pubblicazione INT, num_copie INT, url_immagine VARCHAR(255))"); 
            
           
            stmt.execute("CREATE TABLE IF NOT EXISTS utenti (" +
                    "matricola VARCHAR(20), nome VARCHAR(50), cognome VARCHAR(50), " +
                    "mail VARCHAR(50), Bloccato BOOLEAN)");

            
            stmt.execute("CREATE TABLE IF NOT EXISTS prestito (" +
                    "isbn VARCHAR(20), matricola VARCHAR(20), " +
                    "data_inizio DATE, data_restituzione DATE, " +
                    "stato_prestito VARCHAR(20), data_scadenza DATE)");
            
            
            stmt.execute("CREATE TABLE IF NOT EXISTS autori (id INT, nome VARCHAR(50), cognome VARCHAR(50), num_opere INT, data_nascita DATE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS scritto_da (isbn VARCHAR(20), id_autore INT)");


           
            
            
            stmt.execute("INSERT INTO libri VALUES ('111', 'Libro A', 'Ed', 2000, 5, '')");
            stmt.execute("INSERT INTO libri VALUES ('222', 'Libro B', 'Ed', 2000, 3, '')");

            
            stmt.execute("INSERT INTO utenti VALUES ('U1', 'A', 'B', 'c', false)");
            stmt.execute("INSERT INTO utenti VALUES ('U2', 'D', 'E', 'f', false)");
            stmt.execute("INSERT INTO utenti VALUES ('U3', 'G', 'H', 'i', true)");

            
            stmt.execute("INSERT INTO prestito VALUES ('111', 'U1', CURRENT_DATE, NULL, 'ATTIVO', CURRENT_DATE)");
            stmt.execute("INSERT INTO prestito VALUES ('222', 'U2', '2000-01-01', NULL, 'IN_RITARDO', '2020-02-01')");
            stmt.execute("INSERT INTO prestito VALUES ('111', 'U3', '2000-01-01', '2020-02-01', 'RESTITUITO', '2020-02-01')");
        }
    }

    // TEST STATISTICHE CORRETTE
    @Test
    public void testStatisticheDashboard() {

        verifyThat("#numLibri", (Label l) -> l.getText().equals("2"));
        verifyThat("#numUsers", (Label l) -> l.getText().equals("3"));
        verifyThat("#numLoanAttivi", (Label l) -> l.getText().equals("2"));
        verifyThat("#numScaduti", (Label l) -> l.getText().equals("1"));
    }

    //NAVIGAZIONE MENU
    @Test
    public void testNavigazioneCatalogo() {

        clickOn("#CatalogoLibriButton");


        Button btnCatalogo = lookup("#CatalogoLibriButton").query();
        assertTrue(btnCatalogo.getStyleClass().contains("sidebar-btn-active"), 
                   "Il bottone Catalogo deve evidenziarsi dopo il click");


        Button btnDashboard = lookup("#DashboardButton").query();
        assertFalse(btnDashboard.getStyleClass().contains("sidebar-btn-active"), 
                    "Il bottone Dashboard non deve più essere evidenziato");

    }

    //Logout
    @Test
    public void testLogout() {

        clickOn("#LogoutButton");

        verifyThat("#LoginButton", isVisible());
    }
    
    
    //Modifica Password(Apertura Finestra)
    @Test
    public void testAperturaModificaPassword() {

        clickOn("#modPassButton");

        assertNotNull(controller.DashboardController.PassRec, "Lo stage della modifica password deve essere stato creato");
        
   
        assertTrue(controller.DashboardController.PassRec.isShowing(), "La finestra Modifica Password deve essere aperta");
        

        assertEquals("Modifica Password", controller.DashboardController.PassRec.getTitle());



        javafx.application.Platform.runLater(() -> {
            controller.DashboardController.PassRec.close();
        });
        

        sleep(500);
    }

    // Bottone Backup
    @Test
    public void testBottoneBackup() {
        
        // Ci limitiamo a verificare che il bottone sia presente, visibile e cliccabile.
        verifyThat("#BackupButton", isVisible());
        verifyThat("#BackupButton", (Button b) -> !b.isDisabled());
        verifyThat("#BackupButton", (Button b) -> b.getText().equalsIgnoreCase("Esegui Backup dei dati"));
    }
    
}