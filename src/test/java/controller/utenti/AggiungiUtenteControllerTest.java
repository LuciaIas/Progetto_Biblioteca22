
package controller.utenti;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class AggiungiUtenteControllerTest extends ApplicationTest {

    private static final String H2_URL = "jdbc:h2:mem:testdbAddUser;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    private static Connection h2Connection;

 
    private static final String MATR_VALIDA = "1234567890";
    private static final String NOME = "Mario";
    private static final String COGNOME = "Rossi";
    private static final String EMAIL_VALIDA = "mario@test.com";

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

            stmt.execute("CREATE TABLE utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "bloccato BOOLEAN DEFAULT FALSE)");

        } catch (SQLException e) {
            throw new RuntimeException("Errore initDB: " + e.getMessage());
        }
    }

    @BeforeEach
    public void resetData() throws SQLException {
        DataBase.conn = h2Connection;
        Statement stmt = h2Connection.createStatement();
        stmt.execute("DELETE FROM utenti");
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AggiungiUtente.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }



    @Test
    public void testMatricolaLunghezzaErrata() {
      
        clickOn("#txtMatricola").write("123");
        clickOn("#SalvaButton");

  
        verifyThat("Matricola non valida", isVisible());
        verifyThat("La matricola deve essere a 10 cifre", isVisible());
        clickOn("OK");
    }

    @Test
    public void testMatricolaNonNumerica() {
   
        clickOn("#txtMatricola").write("abcdefghil");
        clickOn("#SalvaButton");

        verifyThat("Matricola non valida", isVisible());
        verifyThat("La matricola deve contenere solo numeri", isVisible());
        clickOn("OK");
    }

    @Test
    public void testCampiVuoti() {

        clickOn("#txtMatricola").write(MATR_VALIDA);
        
        clickOn("#SalvaButton");
        verifyThat("Campi vuoti", isVisible());
        clickOn("OK");

        clickOn("#txtNome").write(NOME);
        clickOn("#SalvaButton");
        verifyThat("Per favore inserisci un cognome", isVisible());
        clickOn("OK");
    }

    @Test
    public void testEmailNonValida() {
        compilaForm(MATR_VALIDA, NOME, COGNOME, "email_sbagliata"); // Senza @ e .

        clickOn("#SalvaButton");
        verifyThat("Email non valida", isVisible());
        clickOn("OK");
    }



    @Test
    public void testMatricolaDuplicata() throws SQLException {
     
        Statement stmt = h2Connection.createStatement();
        stmt.execute("INSERT INTO utenti VALUES ('" + MATR_VALIDA + "', 'Gia', 'Esiste', 'test@test.com', FALSE)");

 
        compilaForm(MATR_VALIDA, NOME, COGNOME, EMAIL_VALIDA);
        clickOn("#SalvaButton");

  
        verifyThat("Operazione fallita", isVisible());

        clickOn("OK");
    }

    @Test
    public void testInserimentoCorretto() throws SQLException {
   
        compilaForm(MATR_VALIDA, NOME, COGNOME, EMAIL_VALIDA);

       
        clickOn("#SalvaButton");

        verifyThat("Utente aggiunto", isVisible());
        clickOn("OK");

        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM utenti WHERE matricola='" + MATR_VALIDA + "'");
        
        assertTrue(rs.next(), "L'utente deve essere presente nel DB");
        assertEquals(NOME, rs.getString("nome"));
        assertEquals(COGNOME, rs.getString("cognome"));
        assertEquals(EMAIL_VALIDA, rs.getString("email"));
        assertEquals(false, rs.getBoolean("bloccato"));
    }

    @Test
    public void testAnnullaChiudeFinestra() {
        clickOn("#AnnullaButton");

    }

    private void compilaForm(String matr, String nome, String cognome, String email) {
        clickOn("#txtMatricola").write(matr);
        clickOn("#txtNome").write(nome);
        clickOn("#txtCognome").write(cognome);
        clickOn("#txtEmail").write(email);
    }
}