
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
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class ModificaUtenteControllerTest extends ApplicationTest {

    private static final String H2_URL = "jdbc:h2:mem:testdbModificaUtente;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    private static Connection h2Connection;

    private static final String MATRICOLA_TEST = "1234567890";
    private static final String NOME_OLD = "VecchioNome";
    private static final String COGNOME_OLD = "VecchioCognome";
    private static final String EMAIL_OLD = "vecchia@test.com";

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
                    "matricola VARCHAR(10) PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "mail VARCHAR(100), " +
                    "bloccato BOOLEAN DEFAULT FALSE)");

        } catch (SQLException e) {
            throw new RuntimeException("Errore initDB: " + e.getMessage());
        }
    }

    @BeforeEach
    public void resetData() throws SQLException {
        DataBase.conn = h2Connection;
        
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

    
        ModificaUtenteController.matricola = MATRICOLA_TEST;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ModificaUtente.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void insertTestData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM utenti");
        stmt.execute("INSERT INTO utenti VALUES ('" + MATRICOLA_TEST + "', '" + NOME_OLD + "', '" + COGNOME_OLD + "', '" + EMAIL_OLD + "', FALSE)");
    }



    @Test
    public void testInizializzazioneCorretta() {
    
        verifyThat("#lblMatricola", hasText(MATRICOLA_TEST));
    }



    @Test
    public void testCampiVuoti() {

        clickOn("#txtNome").eraseText(20);
        clickOn("#txtCognome").eraseText(20);
      
        clickOn("#btnSalva");
        verifyThat("Per favore inserisci un nome e un cognome", isVisible());
        clickOn("OK");

     
        clickOn("#txtCognome").write("CognomeOk");
        clickOn("#btnSalva");
        verifyThat("Per favore inserisci un nome", isVisible());
        clickOn("OK");

        clickOn("#txtNome").write("NomeOk");
        clickOn("#txtCognome").eraseText(20);
        clickOn("#btnSalva");
        verifyThat("Per favore inserisci un cognome", isVisible());
        clickOn("OK");
    }

    @Test
    public void testEmailNonValida() {
        clickOn("#txtNome").write("NomeOk");
        clickOn("#txtCognome").write("CognomeOk");
        
   
        clickOn("#txtEmail").eraseText(20).write("email_senza_chiocciola");
        
        clickOn("#btnSalva");
        verifyThat("Email non valida", isVisible());
        clickOn("OK");
    }



    @Test
    public void testModificaConSuccesso() throws SQLException {
        String nuovoNome = "Mario";
        String nuovoCognome = "Draghi";
        String nuovaEmail = "mario.draghi@bce.eu";


        clickOn("#txtNome").write(nuovoNome);
        clickOn("#txtCognome").write(nuovoCognome);
        clickOn("#txtEmail").write(nuovaEmail);

        
        clickOn("#btnSalva");

      
        verifyThat("Operazione eseguita", isVisible());
        verifyThat("Utente modificato", isVisible());
        clickOn("OK");
        sleep(3000);
       
        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM utenti WHERE matricola='" + MATRICOLA_TEST + "'");
        
        assertTrue(rs.next(), "L'utente deve esistere");
        assertEquals(nuovoNome, rs.getString("nome"));
        assertEquals(nuovoCognome, rs.getString("cognome"));
        assertEquals(nuovaEmail, rs.getString("mail"));
    }

    @Test
    public void testAnnulla() {

        clickOn("#btnAnnulla");

    }
}