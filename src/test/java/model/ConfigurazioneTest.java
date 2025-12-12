package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurazioneTest {


    @BeforeEach
    public void pulisciConfigurazione() throws Exception {
        // Accediamo al campo privato "properties" della classe Configurazione
        Field field = Configurazione.class.getDeclaredField("properties");
        field.setAccessible(true);
        
        // Otteniamo l'oggetto reale e lo svuotiamo
        Properties props = (Properties) field.get(null);
        props.clear();
    }


    private void impostaValoreFinto(String chiave, String valore) throws Exception {
        Field field = Configurazione.class.getDeclaredField("properties");
        field.setAccessible(true);
        Properties props = (Properties) field.get(null);
        props.setProperty(chiave, valore);
    }



    @Test
    public void testDefaultValoriNumerici() {

        assertEquals(5000, Configurazione.getMaxUsers(), "Default max users dovrebbe essere 5000");
        assertEquals(2000, Configurazione.getMaxBooks(), "Default max books dovrebbe essere 2000");
        assertEquals(10000, Configurazione.getMaxLoans(), "Default max loans dovrebbe essere 10000");
        assertEquals(1000, Configurazione.getMaxAuthors(), "Default max authors dovrebbe essere 1000");
        assertEquals(5000, Configurazione.getMaxWrited(), "Default max writers dovrebbe essere 5000");
        assertEquals(1, Configurazione.getSessionDuration(), "Default session duration dovrebbe essere 1");
    }

    @Test
    public void testDefaultOrari() {

        int[] open = Configurazione.getTimeOpen();
        assertEquals(7, open[0]);
        assertEquals(0, open[1]);


        int[] close = Configurazione.getTimeClose();
        assertEquals(20, close[0]);
        assertEquals(0, close[1]);
    }

    @Test
    public void testDefaultEmailNull() {

        assertNull(Configurazione.getEmailUsername());
        assertNull(Configurazione.getPasswordSender());
        assertNull(Configurazione.getPasswordReceiver());
    }



    @Test
    public void testValoriCaricatiCorrettamente() throws Exception {

        impostaValoreFinto("app.max_users", "100");
        impostaValoreFinto("app.max_books", "50");


        assertEquals(100, Configurazione.getMaxUsers());
        assertEquals(50, Configurazione.getMaxBooks());

        assertEquals(10000, Configurazione.getMaxLoans()); 
    }

    @Test
    public void testConfigurazioneEmail() throws Exception {
        impostaValoreFinto("mail.username", "biblioteca@test.com");
        impostaValoreFinto("mail.password.sender", "password123");

        assertEquals("biblioteca@test.com", Configurazione.getEmailUsername());
        assertEquals("password123", Configurazione.getPasswordSender());
    }

    @Test
    public void testOrariPersonalizzati() throws Exception {
 
        impostaValoreFinto("time.open.hour", "9");
        impostaValoreFinto("time.open.minute", "30");

        int[] orario = Configurazione.getTimeOpen();
        assertEquals(9, orario[0]);
        assertEquals(30, orario[1]);
    }



    @Test
    public void testErroreFormatoNumero() throws Exception {

        impostaValoreFinto("app.max_users", "ciao");


        assertThrows(NumberFormatException.class, () -> {
            Configurazione.getMaxUsers();
        });
    }
}