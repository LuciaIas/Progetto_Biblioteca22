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
 * @brief Classe principale dell'applicazione JavaFX.
 *
 * Avvia la GUI, esegue le operazioni preliminari e gestisce l'apertura e chiusura del servizio.
 * Inizializza il database e i task automatici di controllo.
 * Controlla se l'orario corrente rientra nell'orario di servizio.
 * 
 * @author gruppo22
 */
public class Main extends Application{
    
    public static Scene s;
    public static Stage stage;  
    
    /** Orario di apertura del servizio */
    public static int[] open_time = Configurazione.getTimeOpen();
    /** Orario di chiusura del servizio */
    public static int[] close_time = Configurazione.getTimeClose();
    
    
     /**
     * @brief Punto di ingresso dell'applicazione.
     * 
     * @param args argomenti da linea di comando (non utilizzati)
     */
    public static void main(String[] args){
        launch(args);
    }
    
    /**
     * @brief Avvia la GUI principale.
     * 
     * Imposta la scena iniziale e mostra il login.
     */
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
    
      /**
     * @brief Esegue operazioni preliminari all'avvio.
     * 
     * Include: controllo orario servizio, inizializzazione database, avvio task automatici.
     */
    public static void PreliminaryFunctions(){
        checkClosed();
        model.servizi.DataBase.initializeDB();
        model.servizi.OperazioniGiornaliere.avviaTaskDiMezzanotte();
        model.servizi.OperazioniGiornaliere.eseguiControlliAutomatici(false);
    }
    
     /**
     * @brief Metodo chiamato alla chiusura dell'applicazione.
     */
    @Override
    public void stop() throws Exception {
        OperazioniGiornaliere.stop(); 
        super.stop();
        System.exit(0); // Termina forzatamente il programma
}
    
    /**
     * @brief Verifica se il servizio è aperto.
     * 
     * Mostra un alert se l'orario corrente è fuori orario e termina l'applicazione.
     */
    public static void checkClosed(){  
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
