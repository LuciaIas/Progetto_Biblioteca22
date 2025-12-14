package controller.utenti;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

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
    
    private static final String NOME_BLOCCATO = "Luigi Verdi";
    private static final String MATR_BLOCCATO = "999";

    @BeforeAll
    public static void initDbInfrastructure() {
        try {
            h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            DataBase.conn = h2Connection;

            Statement stmt = h2Connection.createStatement();
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            
         
            stmt.execute("DROP TABLE IF EXISTS prestito");
            stmt.execute("DROP TABLE IF EXISTS prestiti");
            stmt.execute("DROP TABLE IF EXISTS scritto_da");
            stmt.execute("DROP TABLE IF EXISTS libri");
            stmt.execute("DROP TABLE IF EXISTS utenti");
            stmt.execute("DROP TABLE IF EXISTS autori");
            
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

     
            stmt.execute("CREATE TABLE autori (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), cognome VARCHAR(100), num_opere INT, data_nascita DATE)");
            stmt.execute("CREATE TABLE libri (isbn VARCHAR(20) PRIMARY KEY, titolo VARCHAR(100), editore VARCHAR(100), anno_pubblicazione INT, num_copie INT, url_immagine VARCHAR(255))");
            
       
            stmt.execute("CREATE TABLE utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "bloccato BOOLEAN DEFAULT FALSE)");
         
            stmt.execute("CREATE TABLE scritto_da (isbn VARCHAR(20), id_autore INT, FOREIGN KEY (isbn) REFERENCES libri(isbn), FOREIGN KEY (id_autore) REFERENCES autori(id), PRIMARY KEY (isbn, id_autore))");
            stmt.execute("CREATE TABLE prestito (isbn VARCHAR(20), matricola VARCHAR(20), inizio_prestito DATE, data_restituzione DATE, stato ENUM('ATTIVO', 'IN_RITARDO', 'PROROGATO', 'RESTITUITO'), data_scadenza DATE, FOREIGN KEY (isbn) REFERENCES libri(isbn), FOREIGN KEY (matricola) REFERENCES utenti(matricola), PRIMARY KEY (isbn, matricola, inizio_prestito))");

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
        
        
        stmt.execute("DELETE FROM prestito"); 
        stmt.execute("DELETE FROM utenti");

       
        stmt.execute("INSERT INTO utenti VALUES ('" + MATR_ATTIVO + "', 'Mario', 'Rossi', 'mario@test.com', FALSE)");
        
      
        stmt.execute("INSERT INTO utenti VALUES ('" + MATR_BLOCCATO + "', 'Luigi', 'Verdi', 'luigi@test.com', TRUE)");
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
        press(javafx.scene.input.KeyCode.ENTER).release(javafx.scene.input.KeyCode.ENTER);
        sleep(500); 

     
        verifyThat(NOME_ATTIVO, isVisible());

      
        assertTrue(lookup(NOME_BLOCCATO).queryAll().isEmpty(), "Luigi non dovrebbe essere visibile");
    }

    @Test
    public void testRicercaPerMatricola() {
        clickOn("#searchUser").write(MATR_BLOCCATO);
        press(javafx.scene.input.KeyCode.ENTER).release(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        
        verifyThat(NOME_BLOCCATO, isVisible());

      
        assertTrue(lookup(NOME_ATTIVO).queryAll().isEmpty(), "Mario non dovrebbe essere visibile");
    }

    @Test
    public void testRicercaReset() {

        clickOn("#searchUser").write("Mario");
        press(javafx.scene.input.KeyCode.ENTER).release(javafx.scene.input.KeyCode.ENTER);
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
    public void testFiltroTutti() {

        clickOn("#FilterButton");
        clickOn("Solo attivi");
        sleep(200);

        clickOn("#FilterButton");
        clickOn("Tutti gli utenti");
        sleep(200);
        
  
        verifyThat(NOME_ATTIVO, isVisible());
        verifyThat(NOME_BLOCCATO, isVisible());
    }

   

    @Test
    public void testBloccoUtente() throws SQLException {
   
        Node btnBlocca = lookup("BLOCCA").query(); 
        clickOn(btnBlocca);
        sleep(500);

      
        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT bloccato FROM utenti WHERE matricola='" + MATR_ATTIVO + "'");
        rs.next();
        assertTrue(rs.getBoolean("bloccato"), "Utente dovrebbe risultare bloccato nel DB");


        verifyThat("SBLOCCA", isVisible());
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
        

        verifyThat("Utente rimosso", isVisible());
        clickOn("OK");
        sleep(500);


        assertTrue(lookup(NOME_ATTIVO).queryAll().isEmpty(), "Mario dovrebbe essere sparito");
        
 
        verifyThat(NOME_BLOCCATO, isVisible());


        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT count(*) FROM utenti");
        rs.next();
        int count = rs.getInt(1);
        
        if (count != 1) {
             throw new AssertionError("Il DB deve contenere esattamente 1 utente. Trovati: " + count);
        }
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