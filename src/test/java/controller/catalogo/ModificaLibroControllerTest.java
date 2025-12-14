package controller.catalogo;
import controller.catalogo.ModificaLibroController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.dataclass.Autore;
import model.dataclass.Libro;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class ModificaLibroControllerTest extends ApplicationTest {

    private ModificaLibroController controller;
    
   
    private static final String H2_URL = "jdbc:h2:mem:testdbModificaCrashFixed;DB_CLOSE_DELAY=-1;MODE=MySQL"; 
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    
    private static Connection h2Connection; 


    @BeforeAll
    public static void initDbInfrastructure() {
        try {
            h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            DataBase.conn = h2Connection; 

            Statement stmt = h2Connection.createStatement();
            
            stmt.execute("DROP TABLE IF EXISTS scritto_da");
            stmt.execute("DROP TABLE IF EXISTS libri");
            stmt.execute("DROP TABLE IF EXISTS autori");

            stmt.execute("CREATE TABLE autori (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), cognome VARCHAR(100), num_opere INT, data_nascita DATE)");
            stmt.execute("CREATE TABLE libri (isbn VARCHAR(20) PRIMARY KEY, titolo VARCHAR(100), editore VARCHAR(100), num_copie INT, anno_pubblicazione INT, url_immagine VARCHAR(255))");
            stmt.execute("CREATE TABLE scritto_da (isbn VARCHAR(20), id_autore INT, FOREIGN KEY (isbn) REFERENCES libri(isbn) ON DELETE CASCADE, FOREIGN KEY (id_autore) REFERENCES autori(id))");

        } catch (SQLException e) {
            throw new RuntimeException("Errore critico initDB: " + e.getMessage());
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
      
        ModificaLibroController.isbn = "111"; 

        if (h2Connection == null || h2Connection.isClosed()) {
            h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        }
        DataBase.conn = h2Connection;


        insertTestData(h2Connection);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ModificaLibro.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        stage.setScene(scene);
        stage.show();
    }

 
    private void insertTestData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
   
        stmt.execute("DELETE FROM scritto_da");
        stmt.execute("DELETE FROM libri");
        stmt.execute("DELETE FROM autori");
        stmt.execute("ALTER TABLE autori ALTER COLUMN id RESTART WITH 1");

 
        stmt.execute("INSERT INTO autori (id, nome, cognome, num_opere) VALUES (1, 'J.R.R.', 'Tolkien', 50)");
        stmt.execute("INSERT INTO autori (id, nome, cognome, num_opere) VALUES (2, 'George', 'Martin', 20)");

        stmt.execute("INSERT INTO libri VALUES ('111', 'Il Signore degli Anelli', 'Bompiani', 10, 1954, '/Images/default.jpg')");
        stmt.execute("INSERT INTO scritto_da VALUES ('111', 1)");
    }



    @Test
    public void testCaricamentoDatiIniziali() {
        verifyThat("#txtTitolo", hasText("Il Signore degli Anelli"));
        verifyThat("#txtEditore", hasText("Bompiani"));
        
        int anno = (int) lookup("#spinAnno").queryAs(javafx.scene.control.Spinner.class).getValueFactory().getValue();
        assertEquals(1954, (int) anno);
    }

    @Test
    public void testModificaDatiBase() {
        doubleClickOn("#txtTitolo").eraseText(13).doubleClickOn("#txtTitolo").eraseText(13).write("Lo Hobbit 2");
        
        
        doubleClickOn("#txtEditore").write("Adelphi");
        
        clickOn("#spinAnno").eraseText(4).write("2000");
        type(javafx.scene.input.KeyCode.ENTER); 

        clickOn("#SalvaButton");
        clickOn("OK");
        waitForFxEvents();

        Libro libroModificato = DataBase.cercaLibro("111");
        assertEquals("Lo Hobbit 2", libroModificato.getTitolo());
    }

    @Test
    public void testRimuoviCopertina() {
        clickOn("#RimuoviCopButton");
        clickOn("#SalvaButton");
        clickOn("OK");
        waitForFxEvents();

        Libro l = DataBase.cercaLibro("111");
        assertEquals("/Images/default.jpg", l.getUrl());
    }


    @Test
    public void testAggiungiNuovoAutore() {
        clickOn("#menuAutori");
        
        Node txtAutore = lookup(".text-field").match(node -> node instanceof TextField && ((TextField)node).getText().isEmpty()).query();
        
        clickOn(txtAutore).write("Mario Rossi");
        
        clickOn("#SalvaButton");
        clickOn("OK");
        waitForFxEvents();
        
        Autore nuovo = DataBase.cercaAutoreByNames("Mario", "Rossi");
        assertTrue(nuovo != null);
    }
}