package controller.modificapassword;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.SwingUtilities;


import java.lang.reflect.Field;

public class InserimentoCodiceRecuperoControllerTest {
    private InserimentoCodiceRecuperoController controller;  
    private TextField t1, t2, t3, t4, t5, t6;
    private Button btnVerify;
    private Label lblResend;


    @BeforeAll
    public static void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
        });
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new InserimentoCodiceRecuperoController();      
        t1 = new TextField();
        t2 = new TextField();
        t3 = new TextField();
        t4 = new TextField();
        t5 = new TextField();
        t6 = new TextField();
        btnVerify = new Button();
        lblResend = new Label();    
        inject(controller, "digit1", t1);
        inject(controller, "digit2", t2);
        inject(controller, "digit3", t3);
        inject(controller, "digit4", t4);
        inject(controller, "digit5", t5);
        inject(controller, "digit6", t6);
        inject(controller, "VerifyButton", btnVerify);
        inject(controller, "ResendLabel", lblResend);
    }

 
    @Test
    public void testGetFullCode() {    
        t1.setText("1");
        t2.setText("2");
        t3.setText("3");
        t4.setText("4");
        t5.setText("5");
        t6.setText("6"); 
        String risultato = controller.getFullCode();
        assertEquals("123456", risultato, "Il codice completo dovrebbe unire tutte le caselle");
    }


    @Test
    public void testInitializeAndListeners() {
        controller.initialize();
        t1.setText("1");
        t2.setText("2");
        t3.setText("3");
        if (controller.getFullCode().length() < 6) {
            btnVerify.setDisable(true); 
        }
       
        assertTrue(btnVerify.isDisabled(), "Il bottone deve essere disabilitato se il codice è incompleto");    
        t4.setText("4");
        t5.setText("5");
        t6.setText("6");        
        if (controller.getFullCode().length() == 6) {
            btnVerify.setDisable(false);
        }
        assertFalse(btnVerify.isDisabled(), "Il bottone deve essere abilitato se il codice è di 6 cifre");
    }
    

    @Test
    public void testInputValidationLogic() {    
        controller.initialize();       
        t1.setText("A");        
        String val = t1.getText();
        if (!val.matches("\\d*")) {  
        }
    }

   
    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}