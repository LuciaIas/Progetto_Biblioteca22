/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;

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
import java.sql.SQLException;
import java.sql.Statement;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class InserisciPasswordModificaControllerTest extends ApplicationTest {
    private static final String H2_URL = "jdbc:h2:mem:testdbCheckPass;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";    
    private static Connection h2Connection;
    private static final String PASSWORD_ATTUALE = "PasswordAttuale123";
 
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
        DataBase.inserisciBibliotecario(PASSWORD_ATTUALE,"emaailprova@dsf.com");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/InserisciPasswordModifica.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Inserisci Password");
        stage.show();
    }


    @Test
    public void testMostraNascondiPassword() {     
        clickOn("#NewPass").write("Segreto");       
        clickOn("#CheckShowPass");       
        verifyThat("#NewPass", isInvisible());
        verifyThat("#NewPassVisible", isVisible());
        verifyThat("#NewPassVisible", hasText("Segreto"));     
        clickOn("#NewPassVisible").write("123");            
        clickOn("#CheckShowPass");           
        verifyThat("#NewPass", isVisible());
        verifyThat("#NewPass", hasText("Segreto123"));
    }

    @Test
    public void testCampoVuoto() {     
        clickOn("#BtnSalva");        
        verifyThat("Devi inserire password", isVisible());        
        clickOn("OK");
    }

    @Test
    public void testPasswordErrata() {     
        clickOn("#NewPass").write("Sbagliata123");       
        clickOn("#BtnSalva");      
        verifyThat("Password errata!", isVisible());        
        clickOn("OK");
    }


    
    @Test
    public void testAnnulla() {
        clickOn("#BtnAnnulla");

    }
}