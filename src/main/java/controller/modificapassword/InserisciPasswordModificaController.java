/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;


import controller.DashboardController;
import model.servizi.ControlloFormato;
import model.servizi.DataBase;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;



/**
 * @brief Controller per la verifica della password attuale.
 * * Questa classe gestisce la finestra che richiede al bibliotecario di inserire
 * la propria password corrente prima di poter accedere alla schermata di modifica password.
 * * @author gruppo22
 * @version 1.0
 */
public class InserisciPasswordModificaController {
    
    @FXML
    private PasswordField NewPass;
    
    @FXML
    private TextField NewPassVisible;  
    
    @FXML
    private CheckBox CheckShowPass;
    
    @FXML
    private Button BtnSalva;
    
    @FXML 
    private Label BtnAnnulla;
    
    @FXML
     /**
     * @brief Inizializza il controller.
     * Imposta i listener per la checkbox e per i bottoni.
     */
    public void initialize(){       
        setCheckBox();
        setButtonFunction();       
    }
    
    
     /**
     * @brief Configura le funzioni dei pulsanti Salva e Annulla.
     * Controlla se la password è vuota o errata tramite Database.
     * Se corretta, apre la finestra di cambio password.
     */
    public void setButtonFunction(){   
        BtnSalva.setOnAction(eh->{
            String pass;
            if(!CheckShowPass.isSelected()) // Se password nascosta
             pass = NewPass.getText();// Prendo testo dal PasswordField         
            else
            pass = NewPassVisible.getText();// Altrimenti prendo testo dal TextField
                       
            //Controllo password e conferma password
            if(pass.equals("")){// Controllo se campo vuoto
                Alert err = new Alert(Alert.AlertType.WARNING);// Alert warning
                err.setContentText("Devi inserire password");
                err.showAndWait();
                return;// Termino funzione se campo vuoto
            }    
                if(DataBase.controllaPasswordBibliotecario(pass)){// Controllo se la password inserita corrisponde a quella del bibliotecario           
            Stage PassRec = new Stage();// Creo nuova finestra per cambio password
                PassRec.setTitle("Modifica Password");
                PassRec.setResizable(false);// Non ridimensionabile
                PassRec.initModality(Modality.APPLICATION_MODAL);// Finestra modale
            try {
                 // Imposto la scena della nuova finestra
                PassRec.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/CambioPassword.fxml"))));// Carico l'interfaccia grafica del cambio password
            } catch (IOException ex) {// Se il file FXML non viene caricato correttamente
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);// Registro l'errore nel log
            }                
                PassRec.showAndWait();// Mostro finestra e aspetto chiusura
                Stage u = (Stage) BtnSalva.getScene().getWindow();// Ottengo finestra corrente
                u.close();// Chiudo finestra corrente dopo aver aperto la finestra CambioPassword
                }else{
                    
                Alert err = new Alert(Alert.AlertType.WARNING);
                err.setContentText("Password errata!");
                err.showAndWait();
                return;           
                }
        });       
         BtnAnnulla.setOnMouseClicked(eh->{        
            Stage f =  (Stage) BtnAnnulla.getScene().getWindow();
                f.close();// Chiudo finestra senza modificare nulla         
        });
    }
    
    
     /**
     * @brief Configura la CheckBox per mostrare/nascondere la password.
     */
    public void setCheckBox(){
    showPassword(false);
        CheckShowPass.setOnAction(eh->{       
        if(CheckShowPass.isSelected())
            showPassword(true);
        else
            showPassword(false);        
        });        
    }
       
     /**
     * @brief Gestisce la visibilità dei campi password.
     * Alterna tra PasswordField e TextField copiando il contenuto.
     * @param yes True per mostrare la password, False per nasconderla.
     */
    public void showPassword(boolean yes){        
            if(yes){
                NewPassVisible.setText(NewPass.getText());
                NewPassVisible.setVisible(true);
                NewPass.setVisible(false);
            }else{
                NewPass.setText(NewPassVisible.getText());
                NewPassVisible.setVisible(false);
                NewPass.setVisible(true);        
            }         
    }    
}
