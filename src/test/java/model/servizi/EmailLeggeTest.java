package model.servizi;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import model.dataclass.EmailInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class EmailLeggeTest {

    // Avvio server IMAPS finto
@RegisterExtension
static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP) 
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@mail.com", "password"));

    @BeforeEach
    public void setUp() throws Exception {
        injectPrivateStaticField("IMAP_HOST", "localhost");
        injectPrivateStaticField("username", "test@mail.com");
        injectPrivateStaticField("password", "password");
        
        // GreenMail è vuoto, quindi gliela creiamo noi artificialmente la cartella di gmail
        GreenMailUser user = greenMail.getManagers().getUserManager().getUser("test@mail.com");
        try {
            greenMail.getManagers().getImapHostManager().createMailbox(user, "[Gmail]/Sent Mail");
        } catch (FolderException e) {
            
        }
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        // Reset
        injectPrivateStaticField("IMAP_HOST", "imap.gmail.com");
    }

    // Metodo helper per modificare le variabili private statiche
    private void injectPrivateStaticField(String fieldName, String value) throws Exception {
        Field field = EmailLegge.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }

    @Test
    public void testLeggiPostaInviata() {
        // Usiamo GreenMailUtil per inviare un messaggio
        GreenMailUtil.sendTextEmailTest("napoli@campione.it", "test@mail.com", 
                                        "Vincim tutt cos", "Pur a champions leugue");
        ArrayList<EmailInfo> risultato = EmailLegge.leggiPostaInviata();
        // VERIFICA
        assertNotNull(risultato, "La lista non deve essere null");

    }
}