package controller.modificapassword;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;


import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javax.swing.SwingUtilities;
import java.lang.reflect.Field;

public class RecuperaPasswordControllerTest {

    private RecuperaPasswordController controller;

    @BeforeAll
    public static void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); 
        });
    }

    @BeforeEach
    public void setUp() {
        controller = new RecuperaPasswordController();
    }


    @Test
    public void testCodeGeneration() {
   
        String code = controller.codeGeneration();
        
        assertNotNull(code, "Il codice non deve essere nullo");
        assertEquals(6, code.length(), "Il codice deve essere di 6 caratteri");
        assertTrue(code.matches("[0-9]+"), "Il codice deve contenere solo numeri");
    }

 
    @Test
    public void testInitialize() throws Exception {
      
        TextField txt = new TextField();
        Button btn = new Button();

        injectPrivateField(controller, "mailField", txt);
        injectPrivateField(controller, "RecoveryButton", btn);

        controller.initialize();

        assertNotNull(btn.getOnAction(), "Il bottone RecoveryButton deve avere un'azione collegata");
    }


    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}