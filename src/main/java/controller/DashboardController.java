/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.servizi.Backup;
import model.servizi.DataBase;
import model.dataclass.Stato;
import model.servizi.Prestito;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;


/**
 * @brief Controller principale della Dashboard.
 *
 * Questa classe gestisce la schermata principale dell'applicazione (Dashboard).
 * Si occupa di:
 * - Navigazione tra le varie sezioni (Catalogo, Utenti, Prestiti, Blacklist, Mail)
 * - Visualizzazione delle statistiche in tempo reale (numero libri, prestiti attivi, utenti, ritardi)
 * - Gestione delle funzionalità di sistema (Logout, Backup, Modifica Password)
 * - Gestione del caricamento delle varie sezioni nel BorderPane centrale
 *
 * @author gruppo22
 */
public class DashboardController {    
    @FXML
    private Button CatalogoLibriButton;     
    @FXML
    private Button mailButton;    
    @FXML
    private Button BLButton;     
    @FXML
    private Button DashboardButton;     
    @FXML
    private Button utentiButton;
    @FXML
    private Button PrestitiRestituzioniButton;     
    @FXML
    private Label numLibri;     
    @FXML
    private Label numLoanAttivi;     
    @FXML
    private Label numUsers;    
    @FXML
    private Label numScaduti;            
    @FXML
    private VBox DashboardBox;    
    @FXML
    private ScrollPane DashboardScrollPane;    
    @FXML
    private BorderPane HomeBorderPane; //usato per cambiare dinamicamente la vista centrale quando si clcca un bottone del menu
    
    private List<Button> menuButtons; //lista che contiene tutti i bottoni principali del menu   
    @FXML
    private Button LogoutButton; 
    @FXML
    private Button modPassButton;     
    @FXML
    private Button BackupButton; 
    
    public static Stage PassRec;// finestra usata per modifica password
    
/**
 * @brief Metodo di inizializzazione del controller.
 *
 * Viene chiamato automaticamente al caricamento della view.
 * Inizializza la lista dei bottoni, configura le azioni dei pulsanti (buttonInitialize)
 * e carica i dati nelle label (labelInitialize).
 */
    @FXML
    public void initialize(){ 
        menuButtons = Arrays.asList(CatalogoLibriButton,DashboardButton,BLButton,mailButton,utentiButton,PrestitiRestituzioniButton); 
        buttonInitialize();
        labelInitialize(); 
    }
    

/**
 * @brief Carica e visualizza le statistiche principali della Dashboard.
 *
 * Interroga il `DataBase` per ottenere:
 * - Numero totale libri
 * - Prestiti attivi (Stato ATTIVO, PROROGATO, IN_RITARDO)
 * - Numero utenti totali
 * - Prestiti in ritardo
 */
    public void labelInitialize(){
        numLibri.setText(""+DataBase.getCatalogo().getLibri().size());
        int i=0;
        for(Prestito p : DataBase.getPrestiti())
            if(p.getStato()==Stato.ATTIVO || p.getStato()==Stato.PROROGATO || p.getStato()==Stato.IN_RITARDO)
                i+=1;
        numLoanAttivi.setText(""+i);
        numUsers.setText(""+DataBase.getNumUser());
        i=0;
        for(Prestito p : DataBase.getPrestiti())
            if(p.getStato()==Stato.IN_RITARDO)
                i+=1;
        numScaduti.setText(""+i);   
    }
       
    
/**
 * @brief Configura le azioni dei pulsanti principali della Dashboard.
 *
 * Gestisce:
 * - Backup: apre DirectoryChooser e chiama `Backup.eseguiBackup`
 * - Modifica Password: apre un nuovo Stage per inserire la nuova password
 * - Logout: torna alla schermata di login
 * - Navigazione: carica le varie sezioni nel BorderPane centrale e evidenzia il pulsante attivo
 */
    public void buttonInitialize(){        
        //backup dati
        BackupButton.setOnAction(eh->{
            DirectoryChooser fc = new DirectoryChooser(); //apre selettore cartelle
            // Filtra per immagini (JPG, PNG)    
            fc.setTitle("Scegli la cartella di destinazione");            
            File f = fc.showDialog((Stage) (BackupButton.getScene().getWindow())); //ottiene cartella scelta            
            if(f!=null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //popup conferma
                alert.setTitle("Backup");
                alert.setHeaderText("Eseguire il backup dei dati?");
                alert.setContentText("Potrebbe richiedere diverso tempo in base alla quantità dei dati");              
                Optional<ButtonType> result = alert.showAndWait(); //attendo risposta bibliotecario
                // Controlliamo cosa ha cliccato
                if (result.isPresent() && result.get() == ButtonType.OK){
                    Backup.eseguiBackup(f.getAbsolutePath()); //avvio backup
                } 
            }
        });       
        //modifica password
        modPassButton.setOnAction(eh->{
                PassRec = new Stage(); 
                PassRec.setTitle("Modifica Password");
                PassRec.setResizable(false);
                PassRec.initModality(Modality.APPLICATION_MODAL); //blocco finestra principale                
            try {
                //carico la schermata di inserimento nuova password 
                PassRec.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/InserisciPasswordModifica.fxml"))));
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
                PassRec.showAndWait();  
        });        
        //logout
        LogoutButton.setOnAction(eh->{
            try {
                Parent root =FXMLLoader.load(getClass().getResource("/View/Accesso.fxml"));
                Scene s = new Scene(root,425,500);
                Main.stage.getProperties().put("login", "login");
                Main.stage.setScene(s);               
                //reset dimensioni finestra
                Main.stage.setWidth(437);
                Main.stage.setHeight(500);
                Main.stage.centerOnScreen();
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });    
        //dashboard
        DashboardButton.setOnAction(eh->{  
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/Dashboard.fxml"));
                 Scene s = DashboardButton.getScene();
                 s.setRoot(root); //sostituisco la scena corrente
                 evidenziaBottone(DashboardButton); 
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }      
        });         
        //catalogo libri
        CatalogoLibriButton.setOnAction(eh->{     
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/Catalogo.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(CatalogoLibriButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }          
        });          
        //mail
        mailButton.setOnAction(eh->{    
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/Mail.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(mailButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }          
        });        
        //gestione utenti
        utentiButton.setOnAction(eh->{     
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/Utenti.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(utentiButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }    
        });        
        //prestiti e restituzioni
        PrestitiRestituzioniButton.setOnAction(eh->{    
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/PrestitoRestituzione.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(PrestitiRestituzioniButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }      
        });      
        //blacklist
        BLButton.setOnAction(eh->{     
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/BlackList.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(BLButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    

/**
 * @brief Evidenzia il pulsante attivo della sidebar.
 *
 * Rimuove la classe CSS "active" da tutti i pulsanti e la assegna solo
 * al pulsante cliccato. Fornisce un feedback visivo al bibliotecario su quale
 * sezione è attualmente visualizzata.
 *
 * @param bottoneAttivo il pulsante appena cliccato da evidenziare
 */
    private void evidenziaBottone(Button bottoneAttivo) {        
       //rimuovo evidenziazione da tutti i bottoni
        for (Button b : menuButtons) {
            b.getStyleClass().remove("sidebar-btn-active");             
            //garantisco che mantengano lo stile base
            if (!b.getStyleClass().contains("sidebar-btn")) {
                b.getStyleClass().add("sidebar-btn");
            }
        }        
        //applico stile evidenziato al bottone cliccato
        bottoneAttivo.getStyleClass().remove("sidebar-btn"); 
        bottoneAttivo.getStyleClass().add("sidebar-btn-active"); 
    }

}
