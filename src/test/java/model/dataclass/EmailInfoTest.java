package model.dataclass;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class EmailInfoTest {


    @Test
    public void testCostruttoreEGetters() {
        Date adesso = new Date();
        String oggetto = "Oggetto";
        String destinatario = "test@mail.com";

        EmailInfo email = new EmailInfo(oggetto, destinatario, adesso);

        assertEquals(oggetto, email.getOggetto());
        assertEquals(destinatario, email.getDestinatario());
        assertEquals(adesso, email.getDataInvio());
    }


    @Test
    public void testToString() {
        Date data = new Date();
        EmailInfo email = new EmailInfo("Avviso Scadenza", "studenti@unisa.it", data);

        String risultato = email.toString();


        assertTrue(risultato.contains("Avviso Scadenza"), "Deve contenere l'oggetto");
        assertTrue(risultato.contains("studenti@unisa.it"), "Deve contenere il destinatario");
        assertTrue(risultato.contains(" - A: "), "Deve rispettare il separatore di formattazione");
    }


    @Test
    public void testValoriNull() {
        EmailInfo email = new EmailInfo(null, null, null);

        assertNull(email.getOggetto());
        assertNull(email.getDestinatario());
        assertNull(email.getDataInvio());

     
        String risultato = email.toString();
        assertNotNull(risultato);
        assertTrue(risultato.contains("null"), "Il toString deve gestire i null stampandoli come stringa");
    }
}