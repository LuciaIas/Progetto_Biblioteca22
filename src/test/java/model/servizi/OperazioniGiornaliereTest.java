package model.servizi;


import model.dataclass.Stato;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class OperazioniGiornaliereTest {
    private Connection testConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        //Setup Database in Memoria (H2)
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdbop;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection; // Iniettiamo la connessione finta
        try (Statement stmt = testConnection.createStatement()) {
            // Creiamo solo la tabella prestiti necessaria per il test
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

    //abbiamo saltato il test notifica (considerandolo banale e effort inutile)
    @Test
    public void testControlliAutomatici_RilevaRitardo() throws SQLException {
        String isbn = "1111111111111";
        String matricola = "1111111111";
        LocalDate ieri = LocalDate.now().minusDays(1); // Scaduto ieri        
        String sql = "INSERT INTO prestito (isbn, matricola, data_inizio, stato_prestito, data_scadenza) VALUES " +
                     "('1111111111111', '1111111111', CURRENT_DATE, 'ATTIVO', ?)";            
        PreparedStatement pstmt = testConnection.prepareStatement(sql);
        pstmt.setDate(1, Date.valueOf(ieri));
        pstmt.execute();       
        OperazioniGiornaliere.eseguiControlliAutomatici(true);
        // VERIFICA
        ArrayList<Prestito> prestiti = DataBase.getPrestiti();
        assertNotNull(prestiti);
        assertEquals(1, prestiti.size());        
        Prestito p = prestiti.get(0);
        assertEquals(Stato.IN_RITARDO, p.getStato(), "Il prestito scaduto doveva passare a IN_RITARDO");
    }

@Test
    public void testControlliAutomatici_IgnoraNonScaduti() throws SQLException {
        //Inseriamo un prestito NON scaduto (scade domani)
        LocalDate domani = LocalDate.now().plusDays(1);       
        String sql = "INSERT INTO prestito (isbn, matricola, data_inizio, stato_prestito, data_scadenza) VALUES " +
                     "('1111111111111', '1111111111', CURRENT_DATE, 'ATTIVO', ?)";               
        PreparedStatement pstmt = testConnection.prepareStatement(sql);
        pstmt.setDate(1, Date.valueOf(domani));
        pstmt.execute();       
        OperazioniGiornaliere.eseguiControlliAutomatici(true);
        // VERIFICA
        ArrayList<Prestito> prestiti = DataBase.getPrestiti();
        assertEquals(Stato.ATTIVO, prestiti.get(0).getStato(), "Il prestito non scaduto deve rimanere ATTIVO");
    }

    @Test
    public void testCalcolaRitardoVersoMezzanotte() throws Exception { 
        Method method = OperazioniGiornaliere.class.getDeclaredMethod("calcolaRitardoVersoMezzanotte");
        method.setAccessible(true);
        long secondi = (long) method.invoke(null); // Metodo statico, quindi null come oggetto
        assertTrue(secondi >= 0, "I secondi non possono essere negativi");
        assertTrue(secondi <= 86400, "I secondi non possono superare le 24 ore");
    }
    
    @Test
    public void testStopScheduler() {
        // Verifica semplice che il metodo stop non lanci eccezioni
        assertDoesNotThrow(() -> OperazioniGiornaliere.stop());
    }
}