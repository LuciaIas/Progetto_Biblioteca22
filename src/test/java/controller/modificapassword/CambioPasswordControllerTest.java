package controller.modificapassword;
import controller.modificapassword.CambioPasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class CambioPasswordControllerTest extends ApplicationTest {  
    private static final String H2_URL = "jdbc:h2:mem:testdbPassFinal;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";   
    private static Connection h2Connection;
 
    @BeforeAll
    public static void initDbInfrastructure() {
        try {
            h2Connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            DataBase.conn = h2Connection; 
            Statement stmt = h2Connection.createStatement();                      
            stmt.execute("DROP TABLE IF EXISTS bibliotecario");
            stmt.execute("CREATE TABLE bibliotecario (password_ VARCHAR(100))");
        } catch (SQLException e) {
            throw new RuntimeException("Errore critico initDB: " + e.getMessage());
        }
    }


    @BeforeEach
    public void resetData() throws SQLException {
        DataBase.conn = h2Connection; 
        Statement stmt = h2Connection.createStatement();        
        stmt.execute("DELETE FROM bibliotecario");     
        stmt.execute("INSERT INTO bibliotecario VALUES ('VecchiaPass123')");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CambioPassword.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void testMostraNascondiPassword() {       
        clickOn("#NewPass").write("Prova123");
        clickOn("#ConfirmPass").write("Prova123");    
        clickOn("#CheckShowPass");
        waitForFxEvents();      
        verifyThat("#NewPass", isInvisible());
        verifyThat("#NewPassVisible", isVisible());
        verifyThat("#NewPassVisible", hasText("Prova123"));       
        clickOn("#NewPassVisible").write("!");           
        clickOn("#CheckShowPass");
        verifyThat("#NewPass", isVisible());
        verifyThat("#NewPass", hasText("Prova123!"));
    }

    @Test
    public void testCampiVuoti() {   
        clickOn("#BtnSalva");
        verifyThat("Completa entrambi i campi delle password", isVisible());        
        clickOn("OK");
    }

    @Test
    public void testPasswordNonCorrispondono() {
        clickOn("#NewPass").write("PasswordA");
        clickOn("#ConfirmPass").write("PasswordB");       
        clickOn("#BtnSalva");
        verifyThat("Le password non corrispodono", isVisible());       
        clickOn("OK");
    }

    @Test
    public void testFormatoNonValido() {   
        clickOn("#NewPass").write("ciao");
        clickOn("#ConfirmPass").write("ciao");        
        clickOn("#BtnSalva");
        verifyThat("Password non sicura 🔒", isVisible());        
        clickOn("OK");
    }

    @Test
    public void testBottoneAnnulla() {
        clickOn("#BtnAnnulla");
    }
}