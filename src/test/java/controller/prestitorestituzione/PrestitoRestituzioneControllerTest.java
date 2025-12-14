package controller.prestitorestituzione;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class PrestitoRestituzioneControllerTest extends ApplicationTest {
    private static final String H2_URL = "jdbc:h2:mem:testdbPrestitoFinalFix;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    private static Connection h2Connection;

    private static final String ISBN_ERR = "1";    
    private static final String MATR_ERR = "21";    

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
            stmt.execute("CREATE TABLE autori (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "num_opere INT, " +
                    "data_nascita DATE)");
            stmt.execute("CREATE TABLE libri (" +
                    "isbn VARCHAR(20) PRIMARY KEY, " + 
                    "titolo VARCHAR(100), " +
                    "editore VARCHAR(100), " +
                    "anno_pubblicazione INT, " +
                    "num_copie INT, " +
                    "url_immagine VARCHAR(255))");
            stmt.execute("CREATE TABLE scritto_da (" +
                    "isbn VARCHAR(20), " +
                    "id_autore INT, " +
                    "FOREIGN KEY (isbn) REFERENCES libri(isbn) ON DELETE CASCADE, " +
                    "FOREIGN KEY (id_autore) REFERENCES autori(id) ON DELETE CASCADE, " +
                    "PRIMARY KEY (isbn, id_autore))");
            stmt.execute("CREATE TABLE utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "bloccato BOOLEAN DEFAULT FALSE)");            
            stmt.execute("CREATE TABLE prestito (" +
                    "isbn VARCHAR(20), " +
                    "matricola VARCHAR(20), " +
                    "data_prestito DATE, " +
                    "data_restituzione DATE, " +
                    "stato_prestito ENUM('ATTIVO', 'IN_RITARDO', 'PROROGATO', 'RESTITUITO'), " +
                    "data_scadenza DATE," +
                    "FOREIGN KEY (isbn) REFERENCES libri(isbn), " +
                    "FOREIGN KEY (matricola) REFERENCES utenti(matricola), " +
                    "PRIMARY KEY (isbn, matricola))");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/PrestitoRestituzione.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void insertTestData(Connection conn) throws SQLException {
        DataBase.conn = conn; 
        Statement stmt = conn.createStatement();       
        stmt.execute("DELETE FROM prestito");
        stmt.execute("DELETE FROM scritto_da");
        stmt.execute("DELETE FROM libri");
        stmt.execute("DELETE FROM utenti");
        stmt.execute("DELETE FROM autori");    
        stmt.execute("INSERT INTO utenti VALUES ('" + MATR_ERR + "', 'Mario', 'Rossi', 'mario@test.com', FALSE)");
        stmt.execute("INSERT INTO utenti VALUES ('99', 'Luigi', 'Verdi', 'luigi@test.com',FALSE)");          
        stmt.execute("INSERT INTO autori VALUES (1, 'Autore', 'Test', 1, '2000-01-01')");      
        stmt.execute("INSERT INTO libri VALUES ('" + ISBN_ERR + "', 'Libro Attivo', 'Editore A', 2020, 5, '')");
        stmt.execute("INSERT INTO libri VALUES ('2', 'Libro In Ritardo', 'Editore B', 2020, 5, '')");         
        stmt.execute("INSERT INTO scritto_da VALUES ('" + ISBN_ERR + "', 1)");
        stmt.execute("INSERT INTO scritto_da VALUES ('2', 1)"); 
        LocalDate oggi = LocalDate.now();
        stmt.execute("INSERT INTO prestito VALUES ('" + ISBN_ERR + "', '" + MATR_ERR + "', '" + Date.valueOf(oggi) + "', NULL, 'ATTIVO', '" + Date.valueOf(oggi.plusDays(30)) + "')");
        stmt.execute("INSERT INTO prestito VALUES ('2', '99', '" + Date.valueOf(oggi.minusDays(40)) + "', NULL, 'IN_RITARDO', '" + Date.valueOf(oggi.minusDays(10)) + "')");
    }

  
    @Test
    public void testCaricamentoIniziale() {
        verifyThat("#lblActiveLoans", hasText("Prestiti attivi: 2"));        
        VBox container = lookup("#loansContainer").query();
        assertEquals(2, container.getChildren().size());        
        verifyThat("Libro Attivo", isVisible());
        verifyThat("Libro In Ritardo", isVisible());
    }

    @Test
    public void testFiltroInRitardo() {
        clickOn("#FilterButton");
        clickOn("Solo in ritardo");
        waitForFxEvents();        
        VBox container = lookup("#loansContainer").query();
        assertEquals(1, container.getChildren().size());
        verifyThat("Libro In Ritardo", isVisible());
    }

    @Test
    public void testRicercaLibro() {
        clickOn("#searchLoan").write("Libro Attivo");       
        type(javafx.scene.input.KeyCode.ENTER);
        VBox container = lookup("#loansContainer").query();
        assertEquals(1, container.getChildren().size(), "La ricerca deve restituire esattamente 1 risultato");
        verifyThat("Libro Attivo", isVisible());
    }
    
    @Test
    public void testRestituzione() throws SQLException {
        Node btnRestituisci = lookup("RESTITUITO").query(); 
        clickOn(btnRestituisci);
        waitForFxEvents();       
        Statement stmt = h2Connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT data_restituzione FROM prestito WHERE isbn='" + ISBN_ERR + "'");        
        if (rs.next()) {
             assertTrue(rs.getDate(1) != null, "La data di restituzione deve essere impostata");
        } else {
             org.junit.jupiter.api.Assertions.fail("Prestito non trovato nel DB");
        }
    }

    @Test
    public void testApreNuovoPrestito() {
        clickOn("#NewLoanButton");
        sleep(1000); 
        Window finestra = window("Concedi Prestito");
        assertTrue(finestra != null && finestra.isShowing());
        interact(() -> ((Stage) finestra).close());
    }
    
    @Test
    public void testInvioEmail() {
        clickOn("📧"); 
        clickOn("OK");
    }
}