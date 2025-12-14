/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.prestitorestituzione;
import controller.prestitorestituzione.AggiungiPrestitoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class AggiungiPrestitoControllerTest extends ApplicationTest {

    private static final String H2_URL = "jdbc:h2:mem:testdbNewLoan;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    private static Connection h2Connection;

    // Dati corretti per i test (Lunghezze: ISBN=13, Matricola=10)
    private static final String ISBN_VALIDO = "1234567890123";
    private static final String ISBN_NO_COPIE = "1234567890000"; // Libro con 0 copie
    private static final String MATRICOLA_VALIDA = "1234567890";
    private static final String MATRICOLA_BLOCCATA = "0000000000";

    @BeforeAll
    public static void initDbInfrastructure() {
        try {
            h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            DataBase.conn = h2Connection;

            Statement stmt = h2Connection.createStatement();
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            
            stmt.execute("DROP TABLE IF EXISTS prestito");
            stmt.execute("DROP TABLE IF EXISTS prestiti"); // Per sicurezza
            stmt.execute("DROP TABLE IF EXISTS scritto_da");
            stmt.execute("DROP TABLE IF EXISTS libri");
            stmt.execute("DROP TABLE IF EXISTS utenti");
            stmt.execute("DROP TABLE IF EXISTS autori");
            
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

            // Creazione Tabelle
            stmt.execute("CREATE TABLE autori (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), cognome VARCHAR(100), num_opere INT, data_nascita DATE)");
            
            stmt.execute("CREATE TABLE libri (isbn VARCHAR(20) PRIMARY KEY, titolo VARCHAR(100), editore VARCHAR(100), anno_pubblicazione INT, num_copie INT, url_immagine VARCHAR(255))");
            
            stmt.execute("CREATE TABLE utenti (matricola VARCHAR(20) PRIMARY KEY, nome VARCHAR(100), cognome VARCHAR(100), email VARCHAR(100), bloccato BOOLEAN DEFAULT FALSE)");
            
            stmt.execute("CREATE TABLE scritto_da (isbn VARCHAR(20), id_autore INT, FOREIGN KEY (isbn) REFERENCES libri(isbn), FOREIGN KEY (id_autore) REFERENCES autori(id), PRIMARY KEY (isbn, id_autore))");

            stmt.execute("CREATE TABLE prestito (" +
                    "isbn VARCHAR(20), " +
                    "matricola VARCHAR(20), " +
                    "inizio_prestito DATE, " +
                    "data_restituzione DATE, " +
                    "stato ENUM('ATTIVO', 'IN_RITARDO', 'PROROGATO', 'RESTITUITO'), " +
                    "data_scadenza DATE," +
                    "FOREIGN KEY (isbn) REFERENCES libri(isbn), " +
                    "FOREIGN KEY (matricola) REFERENCES utenti(matricola), " +
                    "PRIMARY KEY (isbn, matricola, inizio_prestito))");

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AggiungiPrestito.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void insertTestData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM prestito");
        stmt.execute("DELETE FROM scritto_da");
        stmt.execute("DELETE FROM libri");
        stmt.execute("DELETE FROM utenti");
        stmt.execute("DELETE FROM autori");

        // Autore dummy
        stmt.execute("INSERT INTO autori VALUES (1, 'Autore', 'Test', 1, '2000-01-01')");

        // 1. Libro Valido (5 copie)
        stmt.execute("INSERT INTO libri VALUES ('" + ISBN_VALIDO + "', 'Libro Disponibile', 'Editore A', 2020, 5, '')");
        stmt.execute("INSERT INTO scritto_da VALUES ('" + ISBN_VALIDO + "', 1)");

        // 2. Libro Esaurito (0 copie)
        stmt.execute("INSERT INTO libri VALUES ('" + ISBN_NO_COPIE + "', 'Libro Esaurito', 'Editore B', 2020, 0, '')");
        stmt.execute("INSERT INTO scritto_da VALUES ('" + ISBN_NO_COPIE + "', 1)");

        // 3. Utente Valido
        stmt.execute("INSERT INTO utenti VALUES ('" + MATRICOLA_VALIDA + "', 'Mario', 'Rossi', 'mario@test.com', FALSE)");
        
        // 4. Utente Bloccato
        stmt.execute("INSERT INTO utenti VALUES ('" + MATRICOLA_BLOCCATA + "', 'Luigi', 'Neri', 'luigi@test.com', TRUE)");
    }

    // --- TEST DI VALIDAZIONE ISBN E MATRICOLA ---

    @Test
    public void testValidazioneIsbnErrato() {
        // Lunghezza errata
        clickOn("#txtIsbn").write("123");
        clickOn("#IsbnCheckButton");
        verifyThat("Codice ISBN non valido", isVisible());
        clickOn("OK");

        // Caratteri non numerici
        doubleClickOn("#txtIsbn").write("abcde12345678");
        clickOn("#IsbnCheckButton");
        verifyThat("Codice ISBN non valido", isVisible());
        clickOn("OK");

        // ISBN non esistente
        doubleClickOn("#txtIsbn").write("9999999999999");
        clickOn("#IsbnCheckButton");
        verifyThat("Non e stato trovato alcun libro con questo codice nel nostro sistema", isVisible());
        clickOn("OK");
    }

    @Test
    public void testValidazioneMatricolaErrata() {
        // Lunghezza errata
        clickOn("#txtMatricola").write("123");
        clickOn("#MatricolaCheckButton");
        verifyThat("Matricola non valida", isVisible());
        clickOn("OK");

        // Non numerica
        doubleClickOn("#txtMatricola").write("abcdefghil");
        clickOn("#MatricolaCheckButton");
        verifyThat("La matricola deve contenere solo numeri", isVisible());
        clickOn("OK");

        // Utente inesistente
        doubleClickOn("#txtMatricola").write("9999999999");
        clickOn("#MatricolaCheckButton");
        verifyThat("La matricola inserita non e associata ad alcun studente nel database", isVisible());
        clickOn("OK");
    }

    @Test
    public void testValidazioneOk() {
        // Testiamo il caso felice di validazione
        clickOn("#txtIsbn").write(ISBN_VALIDO);
        clickOn("#IsbnCheckButton");
        
        // La label di check deve mostrare l'ISBN (conferma visiva)
        verifyThat("#IsbnCheck", hasText(ISBN_VALIDO));

        clickOn("#txtMatricola").write(MATRICOLA_VALIDA);
        clickOn("#MatricolaCheckButton");
        
        // La label di check deve mostrare la matricola
        verifyThat("#matricolaCheck", hasText(MATRICOLA_VALIDA));
    }
    
    // --- TEST DI LOGICA DATE ---

    @Test
    public void testDateErrate() {
        // Setup validazione preliminare
        validaCampi();

        // 1. Data Scadenza precedente a Data Inizio
        // Usiamo interact per settare le date senza problemi di locale
        DatePicker dScadenza = lookup("#dateScadenza").query();
        DatePicker dInizio = lookup("#dateInizio").query();
        
        interact(() -> {
            dInizio.setValue(LocalDate.now());
            dScadenza.setValue(LocalDate.now().minusDays(5));
        });
        
        clickOn("#SalvaButton");
        verifyThat("Hai impostato una data di scadenza che viene prima in ordine cronologico della data di inizio prestito", isVisible());
        clickOn("OK");

        // 2. Data Inizio nel passato
        interact(() -> {
            dInizio.setValue(LocalDate.now().minusDays(10));
            dScadenza.setValue(LocalDate.now().plusDays(10));
        });
        
        clickOn("#SalvaButton");
        verifyThat("Hai impostato una data di inizio prestito antecedente a quella odierna", isVisible());
        clickOn("OK");
    }

    // --- TEST LOGICA DI BUSINESS ---

    @Test
    public void testSalvataggioSenzaValidazione() {
        // Provo a salvare senza aver cliccato i bottoni di check
        clickOn("#SalvaButton");
        verifyThat("Controlli non completati", isVisible());
        clickOn("OK");
    }

    @Test
    public void testLibroEsaurito() {
        clickOn("#txtIsbn").write(ISBN_NO_COPIE);
        clickOn("#IsbnCheckButton");
        
        clickOn("#txtMatricola").write(MATRICOLA_VALIDA);
        clickOn("#MatricolaCheckButton");
        
        impostaDateValide();

        clickOn("#SalvaButton");
        
        // Deve dire che le copie sono terminate
        verifyThat("Operazione fallita", isVisible());
        // verifyThat(hasText(containsString("Copie terminate")), isVisible()); // Opzionale
        clickOn("OK");
    }

    @Test
    public void testUtenteBloccato() {
        clickOn("#txtIsbn").write(ISBN_VALIDO);
        clickOn("#IsbnCheckButton");
        
        clickOn("#txtMatricola").write(MATRICOLA_BLOCCATA);
        clickOn("#MatricolaCheckButton");
        
        impostaDateValide();

        clickOn("#SalvaButton");
        verifyThat("L'utente risulta bloccato", isVisible());
        clickOn("OK");
    }

    @Test
    public void testSalvataggioConSuccesso() throws SQLException {
        
        // 1. Validazione Campi
        validaCampi();
        
        // 2. Impostazione Date
        impostaDateValide();

        // 3. Click Salva
        clickOn("#SalvaButton");

        // 4. Verifica Messaggio Successo

        verifyThat("Operazione eseguita", isVisible());
        clickOn("OK");

        // 5. Verifica Database (Il prestito deve esistere e le copie devono essere 4)
        Statement stmt = h2Connection.createStatement();
        
        // Check Prestito
        ResultSet rs = stmt.executeQuery("SELECT * FROM prestito WHERE isbn='" + ISBN_VALIDO + "' AND matricola='" + MATRICOLA_VALIDA + "'");
        assertTrue(rs.next(), "Il prestito deve essere stato salvato nel DB");
        assertEquals("ATTIVO", rs.getString("stato"));

        // Check Copie Decrementate (erano 5, ora devono essere 4)
        ResultSet rsLibro = stmt.executeQuery("SELECT num_copie FROM libri WHERE isbn='" + ISBN_VALIDO + "'");
        rsLibro.next();
        assertEquals(4, rsLibro.getInt("num_copie"), "Il numero di copie deve essere diminuito");
    }
    
    // --- METODI DI AIUTO PER NON RIPETERE CODICE ---
    
    private void validaCampi() {
        clickOn("#txtIsbn").write(ISBN_VALIDO);
        clickOn("#IsbnCheckButton");
        clickOn("#txtMatricola").write(MATRICOLA_VALIDA);
        clickOn("#MatricolaCheckButton");
    }

    private void impostaDateValide() {
        DatePicker dInizio = lookup("#dateInizio").query();
        DatePicker dScadenza = lookup("#dateScadenza").query();
        
        interact(() -> {
            dInizio.setValue(LocalDate.now());
            dScadenza.setValue(LocalDate.now().plusDays(30));
        });
    }
}