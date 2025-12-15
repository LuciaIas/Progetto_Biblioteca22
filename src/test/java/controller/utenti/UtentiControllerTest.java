package controller.utenti;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class UtentiControllerTest extends ApplicationTest {


    private static final String H2_URL = "jdbc:h2:mem:testdbUtentiFixed;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    private static Connection h2Connection;

    
    private static final String NOME_ATTIVO = "Mario Rossi";
    private static final String MATR_ATTIVO = "100";
    private static final String MAIL_ATTIVO = "mario@test.com";
    
    private static final String NOME_BLOCCATO = "Luigi Verdi";
    private static final String MATR_BLOCCATO = "999";
    private static final String MAIL_BLOCCATO = "luigi@test.com";

    @BeforeAll
    public static void initDbInfrastructure() {
        try {
          
            h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            DataBase.conn = h2Connection; 
            Statement stmt = h2Connection.createStatement();    
            stmt.execute("DROP TABLE IF EXISTS utenti");     
            stmt.execute("CREATE TABLE utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "bloccato BOOLEAN DEFAULT FALSE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS libri (isbn VARCHAR(20) PRIMARY KEY)"); 
            stmt.execute("CREATE TABLE IF NOT EXISTS prestito (isbn VARCHAR(20), matricola VARCHAR(20))"); 

        } catch (SQLException e) {
            throw new RuntimeException("Errore initDB: " + e.getMessage());
        }
    }

    @BeforeEach
    public void resetData() throws SQLException {
        insertTestData(h2Connection);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (h2Connection != null) h2Connection.close();
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        if (h2Connection == null || h2Connection.isClosed()) {
             h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        }
        DataBase.conn = h2Connection; 
        insertTestData(h2Connection);      
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Utenti.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void insertTestData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();      
        stmt.execute("DELETE FROM utenti");       
        stmt.execute("INSERT INTO utenti VALUES ('" + MATR_ATTIVO + "', 'Mario', 'Rossi', '" + MAIL_ATTIVO + "', FALSE)");              
        stmt.execute("INSERT INTO utenti VALUES ('" + MATR_BLOCCATO + "', 'Luigi', 'Verdi', '" + MAIL_BLOCCATO + "', TRUE)");
    }

 

    @Test
    public void testCaricamentoIniziale() {    
        verifyThat(NOME_ATTIVO, isVisible());
        verifyThat(NOME_BLOCCATO, isVisible());             
        verifyThat("Attivo", isVisible());
        verifyThat("Blacklist", isVisible());               
        verifyThat("#lblTotalUsers", hasText("2 iscritti totali"));
    }

    @Test
    public void testRicercaPerNome() {
        clickOn("#searchUser").write("Mario");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(500);         
        verifyThat(NOME_ATTIVO, isVisible());             
        assertTrue(lookup(NOME_BLOCCATO).queryAll().isEmpty(), "Luigi non dovrebbe essere visibile");
    }

    @Test
    public void testRicercaPerMatricola() {      
        clickOn("#searchUser").write(MATR_BLOCCATO);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(500);               
        verifyThat(NOME_BLOCCATO, isVisible());      
        assertTrue(lookup(NOME_ATTIVO).queryAll().isEmpty(), "Mario non dovrebbe essere visibile");
    }

    @Test
    public void testRicercaReset() {      
        clickOn("#searchUser").write("Mario");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(200);       
        clickOn("#searchUser").eraseText(10);
        sleep(500);       
        verifyThat(NOME_ATTIVO, isVisible());
        verifyThat(NOME_BLOCCATO, isVisible());
    }

    @Test
    public void testFiltroSoloAttivi() {
        clickOn("#FilterButton");
        clickOn("Solo attivi");
        sleep(200);                            
        verifyThat(NOME_ATTIVO, isVisible());      
        assertTrue(lookup(NOME_BLOCCATO).queryAll().isEmpty(), "Utenti bloccati non dovrebbero apparire");
    }

    @Test
    public void testFiltroSoloBloccati() {
        clickOn("#FilterButton");
        clickOn("Solo bloccati");
        sleep(200);                              
        verifyThat(NOME_BLOCCATO, isVisible());      
        assertTrue(lookup(NOME_ATTIVO).queryAll().isEmpty(), "Utenti attivi non dovrebbero apparire");
    }



    @Test
    public void testSbloccoUtente() throws SQLException {
       
        Node btnSblocca = lookup("SBLOCCA").query();
        clickOn(btnSblocca);
        sleep(500);              
        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT bloccato FROM utenti WHERE matricola='" + MATR_BLOCCATO + "'");
        rs.next();
        assertFalse(rs.getBoolean("bloccato"), "Utente dovrebbe risultare sbloccato nel DB");                  
        verifyThat("BLOCCA", isVisible());
    }

    @Test
    public void testEliminaUtente() throws SQLException {            
        Node btnElimina = lookup("🗑").query(); 
        clickOn(btnElimina);        
        verifyThat("Operazione eseguita", isVisible()); 
        clickOn("OK"); 
        sleep(200);
        verifyThat("Operazione eseguita", isVisible()); 
        clickOn("OK");        
        sleep(500);      
        assertTrue(lookup(NOME_ATTIVO).queryAll().isEmpty(), "Mario dovrebbe essere sparito");        
        verifyThat(NOME_BLOCCATO, isVisible());        
        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT count(*) FROM utenti");
        rs.next();
        int count = rs.getInt(1);        
        assertEquals(1, count, "Il DB deve contenere esattamente 1 utente.");
    }

    @Test
    public void testAperturaAggiungiUtente() {
        clickOn("#btnAddUser");
        sleep(1000);     
        Window finestra = window("Aggiungi Utente");
        assertTrue(finestra != null && finestra.isShowing());       
        interact(() -> ((Stage) finestra).close());
    }
}