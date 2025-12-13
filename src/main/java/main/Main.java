/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import model.servizi.OperazioniGiornaliere;
import java.time.LocalTime;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Configurazione;

/**
 *
 * @author gruppo22
 */
public class Main extends Application{
    
    public static Scene s;
    public static Stage stage;  
    
    //APERTURA E CHIUSURA
    public static int[] open_time = Configurazione.getTimeOpen();
    public static int[] close_time = Configurazione.getTimeClose();
    
    public static void main(String[] args){// Metodo chiamato all'avvio dell'app JavaFX
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
             
        this.stage=stage;

        Parent root = FXMLLoader.load(getClass().getResource("/View/Accesso.fxml"));
        s = new Scene(root,425,500);
        root.getProperties().put("login", "login");
        stage.setScene(s);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
        PreliminaryFunctions();
    }
    
    public static void PreliminaryFunctions(){// Metodo di inizializzazione iniziale
        checkClosed();
        model.servizi.DataBase.initializeDB();
        model.servizi.OperazioniGiornaliere.avviaTaskDiMezzanotte();
        model.servizi.OperazioniGiornaliere.eseguiControlliAutomatici(false);
    }
    
    @Override
    public void stop() throws Exception {// Metodo chiamato alla chiusura dell'applicazione
        OperazioniGiornaliere.stop(); 
        super.stop();
        System.exit(0); // Termina forzatamente il programma
}
    
    public static void checkClosed(){   // Metodo per verificare se il servizio è aperto
        LocalTime ora_attuale = LocalTime.now();
        LocalTime orario_apertura=LocalTime.of(open_time[0], open_time[1]);
        LocalTime orario_chiusura=LocalTime.of(close_time[0], close_time[1]);
        boolean ServiceIsOpen = ora_attuale.isAfter(orario_apertura) && ora_attuale.isBefore(orario_chiusura);
        if(!ServiceIsOpen){
            
            Alert al = new Alert(AlertType.WARNING);
            al.setHeaderText("Servizio chiuso");
            al.setHeaderText("Il servizio resta aperto dalle "+orario_apertura + " fino alle " + orario_chiusura);
            al.showAndWait();
            System.exit(0);
            Platform.exit();// Chiude JavaFX correttamente
        }
    
    }
   
}
