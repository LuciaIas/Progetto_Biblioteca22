package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.servizi.DataBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class NotificaControllerTest extends ApplicationTest {

    private Connection testConnection;

    @AfterEach
    public void tearDown() throws Exception {
        if (testConnection != null && !testConnection.isClosed()) {
            try (Statement stmt = testConnection.createStatement()) {
                stmt.execute("DROP ALL OBJECTS");
            }
            testConnection.close();
        }
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        moveTo(0, 0);
    }

    @Override
    public void start(Stage stage) throws Exception {

        initDB();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Notifica.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    private void initDB() throws SQLException {
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testnotificadb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DataBase.conn = testConnection;

        try (Statement stmt = testConnection.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS prestito (" +
                    "isbn VARCHAR(20), matricola VARCHAR(20), " +
                    "data_inizio DATE, data_restituzione DATE, " +
                    "stato_prestito VARCHAR(20), data_scadenza DATE)");


            stmt.execute("INSERT INTO prestito VALUES ('111', 'U1', CURRENT_DATE, NULL, 'IN_RITARDO', CURRENT_DATE)");
            

            stmt.execute("INSERT INTO prestito VALUES ('222', 'U2', CURRENT_DATE, NULL, 'IN_RITARDO', CURRENT_DATE)");
            

            stmt.execute("INSERT INTO prestito VALUES ('333', 'U3', CURRENT_DATE, NULL, 'ATTIVO', CURRENT_DATE)");
        }
    }


    @Test
    public void testMessaggioNotifica() {

        verifyThat("#numRit", (Label label) -> {
            String testo = label.getText();
            System.out.println("Testo trovato nella notifica: " + testo);
            

            return testo.contains("Ci sono 2 prestiti scaduti");
        });
    }
}