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
    private BorderPane HomeBorderPane;
    
    private List<Button> menuButtons;
    @FXML
    private Button LogoutButton;
    @FXML
    private Button modPassButton;
    @FXML
    private Button BackupButton;
    
 
    @FXML
    public void initialize(){
        
         menuButtons = Arrays.asList(CatalogoLibriButton,DashboardButton,BLButton,mailButton,utentiButton,PrestitiRestituzioniButton);
        
        buttonInitialize();
        labelInitialize();
        
    }
    
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
    public void buttonInitialize(){
        
        BackupButton.setOnAction(eh->{
        
            DirectoryChooser fc = new DirectoryChooser();
            // Filtra per immagini (JPG, PNG)
            
            
            fc.setTitle("Scegli la cartella di destinazione");
            
            File f = fc.showDialog((Stage) (BackupButton.getScene().getWindow()));
            
            if(f!=null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Backup");
                alert.setHeaderText("Eseguire il backup dei dati?");
                alert.setContentText("Potrebbe richiedere diverso tempo in base alla quantita dei dati");

                
                Optional<ButtonType> result = alert.showAndWait();

                // Controlliamo cosa ha cliccato
                if (result.isPresent() && result.get() == ButtonType.OK){
                    Backup.eseguiBackup(f.getAbsolutePath());
                } 
            }
        
        });
        
        
        
        modPassButton.setOnAction(eh->{
                 PassRec = new Stage();
                PassRec.setTitle("Modifica Password");
                PassRec.setResizable(false);
                PassRec.initModality(Modality.APPLICATION_MODAL);
            try {
                PassRec.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/InserisciPasswordPerModifica.fxml"))));
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
                PassRec.showAndWait();
            
            
        });
        
        LogoutButton.setOnAction(eh->{
        
            try {
                Parent root =FXMLLoader.load(getClass().getResource("/View/Access.fxml"));
                Scene s = new Scene(root,425,500);
                Main.stage.getProperties().put("login", "login");
                Main.stage.setScene(s);
                //main.stage.centerOnScreen();
                Main.stage.setWidth(437);
                Main.stage.setHeight(500);
                Main.stage.centerOnScreen();
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    
        DashboardButton.setOnAction(eh->{
            
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/dashboard.fxml"));
                 Scene s = DashboardButton.getScene();
                 s.setRoot(root);
                 evidenziaBottone(DashboardButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }      
        });      
        CatalogoLibriButton.setOnAction(eh->{     
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/CatalogoLibri.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(CatalogoLibriButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }          
        });  
        mailButton.setOnAction(eh->{    
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/mail.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(mailButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }          
        });       
        utentiButton.setOnAction(eh->{     
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/utenti.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(utentiButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }    
        });      
        PrestitiRestituzioniButton.setOnAction(eh->{    
            try {
                 Parent root = FXMLLoader.load(getClass().getResource("/View/PrestitoRestituzione.fxml"));
                 HomeBorderPane.setCenter(root);
                 evidenziaBottone(PrestitiRestituzioniButton);
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }      
        });
      
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
    
    private void evidenziaBottone(Button bottoneAttivo) {
       //CLEAN
        for (Button b : menuButtons) {
            b.getStyleClass().remove("sidebar-btn-active"); 
            if (!b.getStyleClass().contains("sidebar-btn")) {
                b.getStyleClass().add("sidebar-btn");
            }
        }
        //SET
        bottoneAttivo.getStyleClass().remove("sidebar-btn"); 
        bottoneAttivo.getStyleClass().add("sidebar-btn-active"); 
    }

}
