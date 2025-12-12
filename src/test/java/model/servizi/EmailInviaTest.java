package model.servizi;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import model.Configurazione;

import static org.junit.jupiter.api.Assertions.*;

public class EmailInviaTest {

    // Avvia un server SMTP finto sulla porta 3025 del tuo PC
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser(Configurazione.getEmailUsername(), Configurazione.getPasswordSender()));

    @BeforeEach
    public void setUp() {
        // Prima di ogni test, diciamo alla tua classe di usare il server finto (localhost:3025)
        // invece di quello vero di Gmail.
        EmailInvia.setTestConfiguration("localhost", String.valueOf(greenMail.getSmtp().getPort()));
    }

    @AfterEach
    public void tearDown() {
        // Ripristiniamo la configurazione originale per sicurezza
        EmailInvia.setTestConfiguration("smtp.gmail.com", "587");
    }

    @Test
    public void testInviaAvvisoLibroSingolo() throws Exception {
        // DATI DI PROVA
        String destinatario = "studente@test.com";
        String nome = "Mario";
        String cognome = "Rossi";
        String titoloLibro = "Java Programming";
        LocalDate dataPrestito = LocalDate.of(2023, 1, 1);

        // 1. ESECUZIONE
        // Chiamiamo il metodo. Nota: il metodo usa un Thread separato!
        EmailInvia.inviaAvviso(destinatario, titoloLibro, nome, cognome, dataPrestito);

        // 2. ATTESA (Fondamentale per i thread)
        // Poiché l'invio è asincrono (new Thread), dobbiamo aspettare che la mail arrivi al server finto.
        // Aspettiamo fino a 5 secondi che arrivi 1 messaggio.
        boolean mailArrivata = greenMail.waitForIncomingEmail(5000, 1);
        
        assertTrue(mailArrivata, "L'email dovrebbe essere stata ricevuta dal server finto");

        // 3. VERIFICA CONTENUTO
        // Prendiamo le email ricevute dal server finto
        MimeMessage[] messaggiRicevuti = greenMail.getReceivedMessages();
        assertEquals(1, messaggiRicevuti.length);

        MimeMessage email = messaggiRicevuti[0];
        
        // Controlliamo l'oggetto
        assertEquals("Mancata Restituzione del libro", email.getSubject());
        
        // Controlliamo il corpo del testo
        String corpo = email.getContent().toString().trim(); // .trim() toglie spazi vuoti extra
        assertTrue(corpo.contains("Mario Rossi"), "Il corpo deve contenere il nome");
        assertTrue(corpo.contains("Java Programming"), "Il corpo deve contenere il titolo del libro");
    }

    @Test
    public void testInviaAvvisoMultiplo() throws Exception {
        // Testiamo il ramo "else" (quando titolo è null)
        String destinatario = "studente@test.com";
        
        // Passiamo null come titolo
        EmailInvia.inviaAvviso(destinatario, null, "Luigi", "Verdi", LocalDate.now());

        // Aspettiamo l'email
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));

        MimeMessage email = greenMail.getReceivedMessages()[0];
        
        // Verifichiamo che l'oggetto sia quello generico per più libri
        assertEquals("Mancata Restituzione del/dei libro/i", email.getSubject());
        
        String corpo = email.getContent().toString();
        assertTrue(corpo.contains("restituire la/le copia/copie"), "Deve contenere il messaggio generico");
    }
}