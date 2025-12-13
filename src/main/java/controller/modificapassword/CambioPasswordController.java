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
 * @brief Controller per l'impostazione della nuova password.
 * * Questa classe gestisce la schermata finale del processo di cambio password,
 * dove l'utente inserisce e conferma la nuova password di accesso.
 * Si occupa della validazione (formato, corrispondenza campi) e del salvataggio nel database.
 * * @author gruppo22
 * @version 1.0
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
     /**
     * @brief Inizializza il controller.
     * Configura lo stato iniziale della checkbox e assegna le funzioni ai pulsanti.
     */
    public void initialize(){       
        setCheckBox();// Inizializza checkbox mostra/nascondi password
        setButtonFunction();// Inizializza pulsanti Salva e Annulla       
    }
    
     /**
     * @brief Configura la logica dei pulsanti "Salva" e "Annulla".
     * * - **BtnSalva:**
     * 1. Recupera il testo dai campi (gestendo la visibilità).
     * 2. Verifica che i campi non siano vuoti.
     * 3. Verifica che le password coincidano.
     * 4. Verifica il formato di sicurezza (tramite `CheckFormat`).
     * 5. Se tutto è valido, aggiorna il database (`RemoveBibliotecario` + `InsertBibliotecario`), mostra conferma e chiude.
     * - **BtnAnnulla:** Chiude la finestra senza salvare.
     */
    public void setButtonFunction(){   
        BtnSalva.setOnAction(eh->{
            String pass,pass1;
            if(!CheckShowPass.isSelected()){// Se la password non è visibile
             pass = NewPass.getText();// Prendo testo campo password
             pass1 = ConfirmPass.getText();
            }else{// Se la password è visibile
            pass = NewPassVisible.getText();// Prendo testo campo visibile
             pass1 = ConfirmPassVisible.getText();
            }
            
            //Controllo password e conferma password
            if(pass.equals("") || pass1.equals("")){// Controllo se i campi sono vuoti
                Alert err = new Alert(Alert.AlertType.WARNING);
                err.setContentText("Completa entrambi i campi delle password");
                err.showAndWait();
                return;// Termino la funzione se campi vuoti
                
            }else if(!pass.equalsIgnoreCase(pass1)){ // Controllo corrispondenza password
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
                // Aggiorno password nel database     
                DataBase.rimuoviBibliotecario();// Rimuovo vecchia password
                DataBase.inserisciBibliotecario(pass);// Inserisco nuova password
                 Alert IsbnAlert = new Alert(AlertType.INFORMATION); // Alert informativo
                IsbnAlert.setHeaderText("Password aggiornata");// Alert interno
                IsbnAlert.setContentText("Modifica effettuata con successo");// Contenuto alert
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );                
                dialogPane.getStyleClass().add("my-alert");                
                IsbnAlert.showAndWait();// Mostro alert conferma
                
                Stage f =  (Stage) BtnSalva.getScene().getWindow();// Ottengo finestra corrente
                f.close();// Chiudo finestra                     
        });
        BtnAnnulla.setOnMouseClicked(eh->{       
            Stage f =  (Stage) BtnAnnulla.getScene().getWindow();// Ottengo finestra corrente
                f.close();// Chiudo finestra senza salvare                       
        });        
    }
       
     /**
     * @brief Configura il listener per la CheckBox "Mostra Password".
     * Imposta lo stato iniziale a "nascosto" e definisce l'azione al click.
     */
    public void setCheckBox(){
    showPassword(false);// Imposto password inizialmente nascosta
        CheckShowPass.setOnAction(eh->{       
        if(CheckShowPass.isSelected())
            showPassword(true);// Mostra password se selezionato
        else
            showPassword(false);// Nasconde password se deselezionato       
        });        
    }
        
     /**
     * @brief Gestisce la sincronizzazione e visibilità di entrambi i campi password (Nuova e Conferma).
     * Copia il testo dai campi nascosti a quelli visibili (e viceversa) e ne alterna la visualizzazione.
     * * @param yes Se true, mostra le password in chiaro. Se false, le nasconde.
     */
    public void showPassword(boolean yes){       
            if(yes){// Mostra password
                NewPassVisible.setText(NewPass.getText());// Sincronizzo testo
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
