package controller.catalogo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.dataclass.Libro;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.DialogPane;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class AggiungiLibroControllerTest extends ApplicationTest {

    private Connection testConnection;

@AfterEach
    public void tearDown() throws Exception {
        if (testConnection != null && !testConnection.isClosed()) {
            try (Statement stmt = testConnection.createStatement()) {

                stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
                stmt.execute("DROP ALL OBJECTS");
                stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
            testConnection.close();
        }
        

        FxToolkit.hideStage();
        FxToolkit.cleanupStages(); 
        release(new KeyCode[]{});
        moveTo(0, 0);
    }

    @Override
    public void start(Stage stage) throws Exception {

        initDB();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AggiungiLibro.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

private void initDB() throws SQLException {
   
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testaggiungilibro;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection;

        try (Statement stmt = testConnection.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS autori (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " + 
                    "nome VARCHAR(50), cognome VARCHAR(50), " +
                    "num_opere INT DEFAULT 0, data_nascita DATE DEFAULT NULL, " +
                    "data_morte DATE DEFAULT NULL, nazionalita VARCHAR(50) DEFAULT NULL, " +
                    "biografia TEXT DEFAULT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS libri (" +
                    "isbn VARCHAR(20) PRIMARY KEY, titolo VARCHAR(100), editore VARCHAR(50), " +
                    "anno_pubblicazione INT, num_copie INT, url_immagine VARCHAR(255))");

            stmt.execute("CREATE TABLE IF NOT EXISTS scritto_da (" +
                    "isbn VARCHAR(20), id_autore INT, " +
                    "PRIMARY KEY(isbn, id_autore), " +
                    "FOREIGN KEY (isbn) REFERENCES libri(isbn) ON DELETE CASCADE, " +
                    "FOREIGN KEY (id_autore) REFERENCES autori(id) ON DELETE CASCADE)");


            stmt.execute("INSERT INTO autori (nome, cognome) VALUES ('Mario', 'Rossi')");
            
   
            stmt.execute("INSERT INTO libri VALUES ('1111111111111', 'Libro Vecchio', 'Ed', 2000, 1, '')");
        }
    }


    @Test
    public void testInserimentoLibroCorretto() {

        clickOn("#txtTitolo").write("Il Nuovo Libro");
        clickOn("#txtEditore").write("Mondadori");
        clickOn("#txtISBN").write("9788812345678"); 

   
        clickOn("#spinAnno").write("2023");
        clickOn("#spinCopie").write("5");


        interact(() -> {
            MenuButton menu = lookup("#menuAutori").query();

            CustomMenuItem item = (CustomMenuItem) menu.getItems().get(0);
            CheckBox cb = (CheckBox) item.getContent();
            cb.setSelected(true); 
        });


        clickOn("#SalvaButton");


        verifyThat(".dialog-pane", (DialogPane d) -> 
            d.getHeaderText().equals("Aggiornamento Catalogo") && 
            d.getContentText().equals("Libro aggiunto al catalogo")
        );
        
        clickOn("OK"); 

 
        Libro libroSalvato = DataBase.cercaLibro("9788812345678");
        assertNotNull(libroSalvato, "Il libro deve essere stato salvato nel DB");
        assertEquals("Il Nuovo Libro", libroSalvato.getTitolo());
        assertEquals(1, libroSalvato.getAutori().size(), "Deve esserci 1 autore collegato");
        assertEquals("Mario", libroSalvato.getAutori().get(0).getNome());
    }


    @Test
    public void testIsbnCorto() {
        clickOn("#txtTitolo").write("Test Error");
        clickOn("#txtISBN").write("123"); // Troppo corto

        clickOn("#SalvaButton");


        verifyThat(".dialog-pane", (DialogPane d) -> 
            d.getHeaderText().equals("Codice ISBN non valido") && 
            d.getContentText().contains("13 cifre")
        );
        
        clickOn("OK");
        assertNull(DataBase.cercaLibro("123"));
    }


    @Test
    public void testIsbnNonNumerico() {
        clickOn("#txtTitolo").write("Test Error");
        clickOn("#txtISBN").write("1234567890ABC"); 

        clickOn("#SalvaButton");


        verifyThat(".dialog-pane", (DialogPane d) -> 
            d.getHeaderText().equals("Codice ISBN non valido") &&
            d.getContentText().contains("solo numeri")
        );
        
        clickOn("OK");
    }


    @Test
    public void testIsbnDuplicato() {
        clickOn("#txtTitolo").write("Libro Clone");

        clickOn("#txtISBN").write("1111111111111"); 

        clickOn("#SalvaButton");


        verifyThat(".dialog-pane", (DialogPane d) -> 
            d.getHeaderText().equals("Operazione fallita") &&
            d.getContentText().contains("risulta gia registrato")
        );
        
        clickOn("OK");
    }


}