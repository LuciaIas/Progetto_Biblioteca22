package controller.catalogo;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.Node;
import javafx.stage.Window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(ApplicationExtension.class)
public class CatalogoControllerTest {

    private CatalogoController controller;
    

    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL"; 
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    
    private static Connection h2Connection;


    @BeforeAll
    public static void setupDatabase() {
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
                    "data_nascita DATE" +
                    ")");

            stmt.execute("CREATE TABLE libri (" +
                    "isbn VARCHAR(20) PRIMARY KEY, " +
                    "titolo VARCHAR(100), " +
                    "editore VARCHAR(100), " +
                    "num_copie INT, " +          
                    "anno_pubblicazione INT, " +   
                    "url_immagine VARCHAR(255)" + 
                    ")");

            
            stmt.execute("CREATE TABLE scritto_da (" +
                    "isbn VARCHAR(20), " +
                    "id_autore INT, " +
                    
                    "FOREIGN KEY (isbn) REFERENCES libri(isbn) ON DELETE CASCADE, " + 
                    "FOREIGN KEY (id_autore) REFERENCES autori(id)" +
                    ")");

        
            
 
            stmt.execute("INSERT INTO autori (id, nome, cognome, num_opere, data_nascita) VALUES " +
                    "(1, 'J.R.R.', 'Tolkien', 50, '1892-01-03')");


            stmt.execute("INSERT INTO libri VALUES " +
                    "('111', 'Il Signore degli Anelli', 'Bompiani', 10, 1954, ''), " +
                    "('222', 'Lo Hobbit', 'Adelphi', 5, 1937, ''), " +
                    "('333', 'Silmarillion', 'Bompiani', 2, 1977, '')");


            stmt.execute("INSERT INTO scritto_da VALUES ('111', 1), ('222', 1), ('333', 1)");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico setup H2: " + e.getMessage());
        }
    }
    
    @AfterAll
    public static void tearDownDb() throws SQLException {

        if (h2Connection != null && !h2Connection.isClosed()) {
            h2Connection.close();
        }
    }

    @Start
    private void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Catalogo.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        stage.setScene(scene);
        stage.show();
    }
    


    @Test
    public void testVisualizzazioneIniziale(FxRobot robot) {

        
        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);
        
        assertEquals(4, grid.getChildren().size(), "La griglia deve contenere 3 libri + il tasto aggiungi");
    }

    @Test
    public void testRicercaPerIsbn(FxRobot robot) {

        TextField searchBar = robot.lookup("#searchBar").queryAs(TextField.class);
        robot.clickOn(searchBar).write("111");
        

        robot.clickOn("#btnCerca");
        waitForFxEvents(); 

        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);
        

        assertEquals(2, grid.getChildren().size());
    }

    @Test
    public void testRicercaPerTitolo(FxRobot robot) {

        TextField searchBar = robot.lookup("#searchBar").queryAs(TextField.class);
        robot.clickOn(searchBar).write("Hobbit");
        

        robot.type(KeyCode.ENTER);
        waitForFxEvents();

        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);
        

        assertEquals(2, grid.getChildren().size());
    }
    
    @Test
    public void testRicercaVuotaReset(FxRobot robot) {

        robot.clickOn("#searchBar").write("111");
        robot.clickOn("#btnCerca");
        assertEquals(2, robot.lookup("#containerLibri").queryAs(GridPane.class).getChildren().size());
        

        robot.clickOn("#searchBar").eraseText(3);
        waitForFxEvents();
        

        assertEquals(4, robot.lookup("#containerLibri").queryAs(GridPane.class).getChildren().size());
    }
    


    
    @Test
    public void testAggiungiCopia(FxRobot robot) {
       

        
        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);
        

        Node cardLibro = grid.getChildren().get(0);

        robot.moveTo(cardLibro);
        

        Node n = robot.from(cardLibro).lookup(".button").match(hasText("+")).query();
        robot.clickOn(n);
        
        waitForFxEvents(); 
        

        int copieNelDb = DataBase.getNumCopieByIsbn("111");
        assertEquals(11, copieNelDb, "Le copie dovrebbero essere passate da 10 a 11");
    }


    @Test
    public void testRimuoviCopia(FxRobot robot) {

        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);
        

        Node cardLibro = grid.getChildren().get(1);

        
        robot.moveTo(cardLibro);

      
        Node n = robot.from(cardLibro).lookup(".button").match(hasText("-")).query();
        robot.clickOn(n);
        waitForFxEvents();

       
        int copieNelDb = DataBase.getNumCopieByIsbn("222");
        assertEquals(4, copieNelDb, "Le copie dovrebbero essere passate da 5 a 4");
    }


    @Test
    public void testEliminaLibro(FxRobot robot) {

        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);


        int sizeIniziale = grid.getChildren().size();
        assertEquals(4, sizeIniziale);


        Node cardLibro = grid.getChildren().get(2);

       
        robot.moveTo(cardLibro);

        
        Node n = robot.from(cardLibro).lookup(".button").match(hasText("Elimina")).query();
        robot.clickOn(n);


        robot.clickOn("OK"); 
        
        waitForFxEvents();
        
      
        int sizeFinale = grid.getChildren().size();
         
        assertEquals(false, DataBase.isIsbnPresent("333"), "Il libro 333 deve essere rimosso dal DB");
        assertEquals(sizeIniziale - 1, sizeFinale, "Dovrebbe esserci un libro in meno nella griglia");

       
    }
    
    @Test
    public void testAperturaFinestraModifica(FxRobot robot) {

        GridPane grid = robot.lookup("#containerLibri").queryAs(GridPane.class);
        Node cardLibro = grid.getChildren().get(0); 

       
        robot.moveTo(cardLibro);


        Node btnModifica = robot.from(cardLibro).lookup(".button").match(hasText("Modifica")).query();
        

        robot.clickOn(btnModifica);


        Window finestraModifica = robot.window("Modifica Libro");

        verifyThat(finestraModifica, isShowing());

        //mi serve per show anda wait
        robot.interact(() -> ((Stage) finestraModifica).close());
    }
    
    
}