package controller.catalogo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class CatalogoControllerTest extends ApplicationTest {
    private CatalogoController controller;
    private static final String H2_URL = "jdbc:h2:mem:testdbCatalogo;DB_CLOSE_DELAY=-1;MODE=MySQL"; 
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
                    "num_copie INT, " +          
                    "anno_pubblicazione INT, " +   
                    "url_immagine VARCHAR(255))");

            stmt.execute("CREATE TABLE scritto_da (" +
                    "isbn VARCHAR(20), " +
                    "id_autore INT, " +
                    "FOREIGN KEY (isbn) REFERENCES libri(isbn) ON DELETE CASCADE, " + 
                    "FOREIGN KEY (id_autore) REFERENCES autori(id))");
        } catch (SQLException e) {
            throw new RuntimeException("Errore critico setup H2: " + e.getMessage());
        }
    }
    
    @AfterAll
    public static void tearDownDb() throws SQLException {
        if (h2Connection != null && !h2Connection.isClosed()) {
            h2Connection.close();
        }
    }

 
    @Override
    public void start(Stage stage) throws IOException, SQLException {   
        if (h2Connection == null || h2Connection.isClosed()) {
             h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        }
        DataBase.conn = h2Connection;
        resetAndInsertData(h2Connection);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Catalogo.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        stage.setScene(scene);
        stage.show();
    }
    

    private void resetAndInsertData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();       
 
        stmt.execute("DELETE FROM scritto_da");
        stmt.execute("DELETE FROM libri");
        stmt.execute("DELETE FROM autori");       
     
        stmt.execute("INSERT INTO autori (id, nome, cognome, num_opere, data_nascita) VALUES " +
                "(1, 'J.R.R.', 'Tolkien', 50, '1892-01-03')");

        stmt.execute("INSERT INTO libri VALUES " +
                "('111', 'Il Signore degli Anelli', 'Bompiani', 10, 1954, ''), " +
                "('222', 'Lo Hobbit', 'Adelphi', 5, 1937, ''), " +
                "('333', 'Silmarillion', 'Bompiani', 2, 1977, '')");

        stmt.execute("INSERT INTO scritto_da VALUES ('111', 1), ('222', 1), ('333', 1)");
    }
    


    @Test
    public void testVisualizzazioneIniziale() {
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);       
        assertEquals(4, grid.getChildren().size(), "La griglia deve contenere 3 libri + il tasto aggiungi");
    }

    @Test
    public void testRicercaPerIsbn() {
        clickOn("#searchBar").write("111");
        clickOn("#btnCerca");
        waitForFxEvents(); 
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);
        assertEquals(2, grid.getChildren().size());
    }

    @Test
    public void testRicercaPerTitolo() {
        clickOn("#searchBar").write("Hobbit");
        type(KeyCode.ENTER);
        waitForFxEvents();
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);
        assertEquals(2, grid.getChildren().size());
    }
    
    @Test
    public void testRicercaVuotaReset() {
        clickOn("#searchBar").write("111");
        clickOn("#btnCerca");
        assertEquals(2, lookup("#containerLibri").queryAs(GridPane.class).getChildren().size());        
        clickOn("#searchBar").eraseText(3);
        waitForFxEvents();        
        assertEquals(4, lookup("#containerLibri").queryAs(GridPane.class).getChildren().size());
    }
    
    @Test
    public void testAggiungiCopia() {
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);          
        Node cardTarget = null;
        for(Node n : grid.getChildren()){
            if(from(n).lookup("Il Signore degli Anelli").tryQuery().isPresent()){
                cardTarget = n;
                break;
            }
        }
        if(cardTarget==null) throw new AssertionError("Libro non trovato");
        moveTo(cardTarget);        
        Node btnPiu = from(cardTarget).lookup(".button").match(hasText("+")).query();
        clickOn(btnPiu);        
        waitForFxEvents();         
        int copieNelDb = DataBase.getNumCopieByIsbn("111");       
        assertEquals(11, copieNelDb, "Le copie dovrebbero essere passate da 10 a 11");
    }

    @Test
    public void testRimuoviCopia() {
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);              
        Node cardTarget = null;
        for(Node n : grid.getChildren()){
            if(from(n).lookup("Lo Hobbit").tryQuery().isPresent()){
                cardTarget = n;
                break;
            }
        }
        if(cardTarget==null) throw new AssertionError("Libro non trovato");
        moveTo(cardTarget);     
        Node btnMeno = from(cardTarget).lookup(".button").match(hasText("-")).query();
        clickOn(btnMeno);       
        waitForFxEvents();
        int copieNelDb = DataBase.getNumCopieByIsbn("222");      
        assertEquals(4, copieNelDb, "Le copie dovrebbero essere passate da 5 a 4");
    }

@Test
    public void testEliminaLibro() {
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);
        int sizeIniziale = grid.getChildren().size();
        assertEquals(4, sizeIniziale);
        
        Node cardTarget = null;
        for(Node n : grid.getChildren()){
            if(from(n).lookup("Silmarillion").tryQuery().isPresent()){
                cardTarget = n;
                break;
            }
        }
        
        if(cardTarget == null) throw new AssertionError("Libro non trovato");
        
        moveTo(cardTarget);
        Node btnElimina = from(cardTarget).lookup(".button").match(hasText("Elimina")).query();
        

        clickOn(btnElimina); 
        

        clickOn("OK");         
        

        sleep(500); 
        clickOn("OK"); 

        waitForFxEvents();        
        
        int sizeFinale = grid.getChildren().size();          
        assertEquals(false, DataBase.isIsbnPresent("333"), "Il libro 333 deve essere rimosso dal DB");
        assertEquals(sizeIniziale - 1, sizeFinale, "Dovrebbe esserci un libro in meno nella griglia");
    }
    
    @Test
    public void testAperturaFinestraModifica() {
        GridPane grid = lookup("#containerLibri").queryAs(GridPane.class);            
        Node cardTarget = null;
        for(Node n : grid.getChildren()){
             if(from(n).lookup(".button").match(hasText("Modifica")).tryQuery().isPresent()){
                 cardTarget = n;
                 break;
             }
        }        
        moveTo(cardTarget);
        Node btnModifica = from(cardTarget).lookup(".button").match(hasText("Modifica")).query();
        clickOn(btnModifica);
        Window finestraModifica = window("Modifica Libro");
        verifyThat(finestraModifica, isShowing());
        interact(() -> ((Stage) finestraModifica).close());
    }
}