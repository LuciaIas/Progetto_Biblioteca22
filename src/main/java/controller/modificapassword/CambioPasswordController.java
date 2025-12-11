/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;


import model.servizi.ControlloFormato;
import model.servizi.DataBase;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author gruppo22
 */
public class CambioPasswordController {
    
    @FXML
    private PasswordField NewPass;
    
    @FXML
    private PasswordField ConfirmPass;
    
    @FXML
    private TextField NewPassVisible;
    
    @FXML
    private TextField ConfirmPassVisible;
    
    @FXML
    private CheckBox CheckShowPass;
    
    @FXML
    private Button BtnSalva;
    
    @FXML 
    private Label BtnAnnulla;
    
    @FXML
    public void initialize(){       
        setCheckBox();// Inizializza checkbox mostra/nascondi password
        setButtonFunction();// Inizializza pulsanti Salva e Annulla       
    }
    
    public void setButtonFunction(){   
        BtnSalva.setOnAction(eh->{
            String pass,pass1;
            if(!CheckShowPass.isSelected()){// Se la password non è visibile
             pass = NewPass.getText();// Prende testo campo password
             pass1 = ConfirmPass.getText();
            }else{// Se la password è visibile
            pass = NewPassVisible.getText();// Prende testo campo visibile
             pass1 = ConfirmPassVisible.getText();
            }
            
            //Controllo password e conferma password
            if(pass.equals("") || pass1.equals("")){// Controllo se i campi sono vuoti
                Alert err = new Alert(Alert.AlertType.WARNING);
                err.setContentText("Completa entrambi i campi delle password");
                err.showAndWait();
                return;// Termina la funzione se campi vuoti
                
            }else if(!pass.equalsIgnoreCase(pass1)){ // Controlla corrispondenza password
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setContentText("Le password non corrispodono");
                err.showAndWait();
                return;
                
            }else if(!ControlloFormato.controlloFormatoPassword(pass)){           
                Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Errore Validazione"); // Titolo della finestra
                al.setHeaderText("Password non sicura 🔒"); // Titolo interno 
                al.setContentText("La password deve avere:\n- Minimo 8 caratteri\n- Una maiuscola\n- Un numero\n- Un simbolo (@#$%^&+=!)");

                DialogPane dialogPane = al.getDialogPane();

                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");

                al.showAndWait();
                return;                
            }
                // Aggiorna password nel database     
                DataBase.rimuoviBibliotecario();// Rimuove vecchia password
                DataBase.inserisciBibliotecario(pass);// Inserisce nuova password
                 Alert IsbnAlert = new Alert(AlertType.INFORMATION); // Alert informativo
                IsbnAlert.setHeaderText("Password aggiornata");// Alert interno
                IsbnAlert.setContentText("Modifica effettuata con successo");// Contenuto alert
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );                
                dialogPane.getStyleClass().add("my-alert");                
                IsbnAlert.showAndWait();// Mostra alert conferma
                
                Stage f =  (Stage) BtnSalva.getScene().getWindow();// Ottiene finestra corrente
                f.close();// Chiude finestra                     
        });
        BtnAnnulla.setOnMouseClicked(eh->{       
            Stage f =  (Stage) BtnAnnulla.getScene().getWindow();// Ottiene finestra corrente
                f.close();// Chiude finestra senza salvare                       
        });        
    }
       
    public void setCheckBox(){
    showPassword(false);// Imposta password inizialmente nascosta
        CheckShowPass.setOnAction(eh->{       
        if(CheckShowPass.isSelected())
            showPassword(true);// Mostra password se selezionato
        else
            showPassword(false);// Nasconde password se deselezionato       
        });        
    }
        
    public void showPassword(boolean yes){       
            if(yes){// Mostra password
                NewPassVisible.setText(NewPass.getText());// Sincronizza testo
                NewPassVisible.setVisible(true);// Mostra campo visibile
                NewPass.setVisible(false);// Nasconde campo password
                
                ConfirmPassVisible.setText(ConfirmPass.getText());
                ConfirmPassVisible.setVisible(true);
                ConfirmPass.setVisible(false);
        
            }else{// Nasconde password
                NewPass.setText(NewPassVisible.getText());
                NewPassVisible.setVisible(false);// Nasconde campo visibile
                NewPass.setVisible(true); // Mostra campo password
                
                ConfirmPass.setText(ConfirmPassVisible.getText());
                ConfirmPassVisible.setVisible(false);
                ConfirmPass.setVisible(true);               
            }     
    }
   
}
