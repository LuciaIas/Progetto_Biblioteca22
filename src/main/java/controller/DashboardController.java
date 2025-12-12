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
 *
 * @author gruppo22
 */

//controller per la dashboard: gestisce navigazione, pulsanti menu, backup e logout
public class DashboardController {
     
    @FXML
    private Button CatalogoLibriButton; //bottone catalogo libri
    
    @FXML
    private Button mailButton; //bottone mail
    
    @FXML
    private Button BLButton; //bottone blacklist
    
    @FXML
    private Button DashboardButton; //bottone dashboard (home)
    @FXML
    private Button utentiButton; //bottone utenti
    @FXML
    private Button PrestitiRestituzioniButton; //bottone prestiti/restituzioni
    
    @FXML
    private Label numLibri; //mostra numero totale di libri presenti nel cataloog
    
    @FXML
    private Label numLoanAttivi; //mostra numero di prestiti attivi
    
    @FXML
    private Label numUsers; //mostra il numero di utenti registrati nel sistema
    
    @FXML
    private Label numScaduti; //mostra il numero di prestiti scaduti o in ritardo

            
    @FXML
    private VBox DashboardBox; //contenitore verticale in cui possono essere inseriti altri nodi GUI
    
    @FXML
    private ScrollPane DashboardScrollPane; //contenitore che permette lo scroll orizzontale e verticale
    
    @FXML
    private BorderPane HomeBozrderPane; //usato per acmbiare dinamicamente la vista centrale quando si clcca un bottone del menu
    
    private List<Button> menuButtons; //lista che contiene tutti i bottoni principali del menu
    
    @FXML
    private Button LogoutButton; //bottone per uscire dalla dashboard e tornare alla schermata di login
    @FXML
    private Button modPassButton; //bottone per modificare la password dell'utente/bibliotecario
    @FXML
    private Button BackupButton; //bottone per eseguire il backup dei dati della biblioteca
    
 
    //inizializza la dashboard impostando lista bottoni e aggiornando testi delle statistiche
    @FXML
    public void initialize(){
        menuButtons = Arrays.asList(CatalogoLibriButton,DashboardButton,BLButton,mailButton,utentiButton,PrestitiRestituzioniButton); 
        buttonInitialize();
        labelInitialize(); 
    }
    
    //aggiorna i valori delle etichette (libri, prestiti,utenti,scaduti) leggendo i dati dal database
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
    public static Stage PassRec;
    
    
    //inizializza e configura tutti i bottoni della dashboard (backup, cambio password, logout e navigazione)
    //associa a ciascun pulsante il caricamento della pagina corrispondente
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
                alert.setContentText("Potrebbe richiedere diverso tempo in base alla quantita dei dati");

                
                Optional<ButtonType> result = alert.showAndWait(); //attende risposta utente

                // Controlliamo cosa ha cliccato
                if (result.isPresent() && result.get() == ButtonType.OK){
                    Backup.eseguiBackup(f.getAbsolutePath()); //avvia backup
                } 
            }
        });
        
        //modifica password
        modPassButton.setOnAction(eh->{
                 PassRec = new Stage //nuova finestra modale
                PassRec.setTitle("Modifica Password");
                PassRec.setResizable(false);
                PassRec.initModality(Modality.APPLICATION_MODAL); //blocca finestra principale
                
            try {
                //carica la schermata di inserimento nuova password 
                PassRec.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/InserisciPasswordModifica.fxml"))));
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
                PassRec.showAndWait();  //mostra la finestra e aspetta chiusura 
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
                 s.setRoot(root); //sostituisce la scena corrente
                 evidenziaBottone(DashboardButton); //evidenzia bottone
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }      
        });  
        
        //catalogo libri
        CatalogoLibriButton.setOnAction(eh->{     
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/Catalogo.fxml"));
                 HomeBorderPane.setCenter(root); //carica la pagina nel BorderPane
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
    
    //evidenzia il bottone attivo nel menu laterale rimuovendo gli stili dagli altri pulsanti
    private void evidenziaBottone(Button bottoneAttivo) {
        
       //rimuove evidenziazione da tutti i bottoni
        for (Button b : menuButtons) {
            b.getStyleClass().remove("sidebar-btn-active"); 
            
            //garantisce che mantengano lo stile base
            if (!b.getStyleClass().contains("sidebar-btn")) {
                b.getStyleClass().add("sidebar-btn");
            }
        }
        
        //applica stile evidenziato al bottone cliccato
        bottoneAttivo.getStyleClass().remove("sidebar-btn"); 
        bottoneAttivo.getStyleClass().add("sidebar-btn-active"); 
    }

}
