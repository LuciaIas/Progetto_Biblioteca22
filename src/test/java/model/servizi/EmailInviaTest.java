package model.servizi;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmailInviaTest {

    // Avvio server SMTP falso su localhost
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@mail.com", "password"));

    @BeforeEach
    public void setUp() throws Exception {
        // Configura EmailInvia per puntare al localhost che useremo come server per testare
        // greenMail.getSmtp().getPort() recupera la porta dinamica assegnata dal test
        EmailInvia.setTestConfiguration("localhost", String.valueOf(greenMail.getSmtp().getPort()));

        // INIEZIONE DELLE CREDENZIALI FINTE
        injectPrivateStaticField("username", "test@mail.com");
        injectPrivateStaticField("password", "password");
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Reset
        EmailInvia.setTestConfiguration("smtp.gmail.com", "587");
    }

    
    // Metodo helper per modificare i campi private static final di EmailInvia
     
    private void injectPrivateStaticField(String fieldName, String value) throws Exception {
        Field field = EmailInvia.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        
        // Rimuove il modificatore final per permettere la modifica
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);


        field.set(null, value);
    }

    @Test
    public void testInviaAvvisoLibroSingolo() {
        // Dati di prova
        String destinatario = "studente@unisa.it";
        String nome = "Pecco";
        String cognome = "Mattarella";
        String titolo = "Ingegneria del Software miglior corso possibile";
        LocalDate data = LocalDate.of(2023, 10, 1);

        // Esecuzione
        EmailInvia.inviaAvviso(destinatario, titolo, nome, cognome, data);

        // Verifica: Aspettiamo fino a 2 secondi che arrivi 1 email
        assertTrue(greenMail.waitForIncomingEmail(2000, 1), 
            "L'email non è arrivata al server (timeout)");

        // Controllo contenuto
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        
        try {
            String subject = receivedMessages[0].getSubject();
            String body = receivedMessages[0].getContent().toString();
            
            assertEquals("Mancata Restituzione del libro", subject);
            assertTrue(body.contains(titolo), "Il corpo deve contenere il titolo del libro");
            assertTrue(body.contains(nome), "Il corpo deve contenere il nome dello studente");
        } catch (Exception e) {
            fail("Errore nella lettura del messaggio: " + e.getMessage());
        }
    }

    @Test
    public void testInviaAvvisoMultiplo() {
        // Esecuzione con titolo NULL 
        EmailInvia.inviaAvviso("studente@unisa.it", null, "Matteo", "Politano", LocalDate.now());

        // Verifica ricezione
        assertTrue(greenMail.waitForIncomingEmail(2000, 1));

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        
        try {
            String subject = receivedMessages[0].getSubject();
            assertEquals("Mancata Restituzione del/dei libro/i", subject);
        } catch (Exception e) {
            fail("Errore nella lettura del messaggio");
        }
    }
}