package model.servizi;

import model.dataclass.Autore;
import model.dataclass.Libro;
import model.dataclass.Utente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DataBaseTest {

    private Connection testConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");        
        DataBase.conn = testConnection;
        try (Statement stmt = testConnection.createStatement()) {
            // Tabella Bibliotecario
            stmt.execute("CREATE TABLE bibliotecario (password_ VARCHAR(255))");           
            // Tabella Utenti
            stmt.execute("CREATE TABLE utenti (" +
                    "matricola VARCHAR(20) PRIMARY KEY, " +
                    "nome VARCHAR(50), cognome VARCHAR(50), " +
                    "mail VARCHAR(50), Bloccato BOOLEAN)");
            // Tabella Autori (ID auto-incrementante per simulare MySQL)
            stmt.execute("CREATE TABLE autori (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(50), cognome VARCHAR(50), " +
                    "num_opere INT, data_nascita DATE)");
            // Tabella Libri
            stmt.execute("CREATE TABLE libri (" +
                    "isbn VARCHAR(20) PRIMARY KEY, titolo VARCHAR(100), " +
                    "editore VARCHAR(50), " +
                    "anno_pubblicazione INT,num_copie INT, url_immagine VARCHAR(255))");            
            // Tabella Relazione Scritto_Da
            stmt.execute("CREATE TABLE scritto_da (" +
                    "isbn VARCHAR(20), id_autore INT)");                    
             // Tabella Prestito
            stmt.execute("CREATE TABLE prestito (" +
                    "isbn VARCHAR(20), matricola VARCHAR(20), " +
                    "data_inizio DATE, data_restituzione DATE, " +
                    "stato_prestito VARCHAR(20), data_scadenza DATE)");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        testConnection.createStatement().execute("DROP ALL OBJECTS");
        testConnection.close();
    }

    //TEST BIBLIOTECARIO 

    @Test
    public void testInserimentoEControlloPassword() {
        // Inseriamo una password (
        boolean inserito = DataBase.inserisciBibliotecario("password");
        assertTrue(inserito, "Il bibliotecario dovrebbe essere inserito correttamente");
        // Verifichiamo se esiste
        assertTrue(DataBase.controllaEsistenzaBibliotecario());
        // Proviamo il login con la password giusta
        assertTrue(DataBase.controllaPasswordBibliotecario("password"), 
                "Il login deve riuscire con la password corretta");
        // Proviamo il login con password sbagliata
        assertFalse(DataBase.controllaPasswordBibliotecario("sbagliata"), 
                "Il login deve fallire con password errata");
    }

    //TEST UTENTI

    @Test
    public void testGestioneUtenti() {
        Utente u = new Utente("1111111111", "Pasquale", "Mazzocchi", "pasquale@email.com", false);        
        // 1. Inserimento
        assertTrue(DataBase.aggiungiUtente(u));        
        // 2. Controllo Esistenza
        assertTrue(DataBase.isMatricolaPresent("1111111111"));
        assertEquals(1, DataBase.getNumUser());
        // 3. Ricerca
        Utente trovato = DataBase.cercaUtente("1111111111");
        assertNotNull(trovato);
        assertEquals("Pasquale", trovato.getNome());
        // 4. Rimozione
        DataBase.rimuoviUtente("1111111111");
        assertFalse(DataBase.isMatricolaPresent("1111111111"));
    }

    @Test
    public void testBlacklistUtente() {
        Utente u = new Utente("2", "Maicol", "Tambs", "Maicol@email.com", false);
        DataBase.aggiungiUtente(u);
        // Blocchiamo l'utente
        DataBase.setBlackListed("2");
        Utente bloccato = DataBase.cercaUtente("2");
        assertTrue(bloccato.isBloccato(), "L'utente dovrebbe risultare bloccato");
        // Sblocchiamo
        DataBase.unsetBlackListed("2");
        Utente sbloccato = DataBase.cercaUtente("2");
        assertFalse(sbloccato.isBloccato(), "L'utente dovrebbe essere sbloccato");
    }

    // TEST LIBRI E AUTORI

    @Test
    public void testFlussoInserimentoLibro() {   
        Autore autore = new Autore("J.K.", "Rowling", 10, LocalDate.of(1965, 7, 31));
        DataBase.aggiungiAutore(autore);      
        // Recuperiamo l'autore dal DB per avere il suo ID generato
        Autore autoreDb = DataBase.cercaAutoreByNames("J.K.", "Rowling");
        assertNotNull(autoreDb, "L'autore deve essere stato salvato");      
        ArrayList<Autore> listaAutori = new ArrayList<>();
        listaAutori.add(autoreDb);       
        Libro libro = new Libro("123", "Harry Potter", "", listaAutori, Year.of(1997), 5, "");         
        boolean esito = DataBase.aggiungiLibro(libro);
        assertTrue(esito, "Il libro deve essere salvato correttamente");       
        assertTrue(DataBase.isIsbnPresent("123"));
        assertEquals(5, DataBase.getNumCopieByIsbn("123"));
        // Verifica Autori collegati
        Libro libroRecuperato = DataBase.getCatalogo().cercaPerIsbn("123");
        assertNotNull(libroRecuperato);
        assertEquals("Harry Potter", libroRecuperato.getTitolo());
        assertEquals(1, libroRecuperato.getAutori().size()); 
    }

    @Test
    public void testModificaNumCopie() {
        // Setup rapido senza autori per testare solo le copie
        try {
            testConnection.createStatement().execute(
                "INSERT INTO libri (isbn, num_copie) VALUES ('111', 10)"
            );
        } catch (SQLException e) { fail("Setup fallito"); }
        // Test incremento
        DataBase.modificaNum_copie("111", true); 
        assertEquals(11, DataBase.getNumCopieByIsbn("111"));
        // Test decremento
        DataBase.modificaNum_copie("111", false);
        assertEquals(10, DataBase.getNumCopieByIsbn("111"));
    }
}