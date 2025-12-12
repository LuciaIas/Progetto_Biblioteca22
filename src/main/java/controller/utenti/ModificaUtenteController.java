/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.utenti;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author gruppo22
 */

//controller che gestisce la finestra per modificare i dati di un utente
public class ModificaUtenteController {
    
    @FXML
    private Label lblMatricola; //label che mostra la matricola dell'utente selezionato
    
    @FXML
    private TextField txtNome; //input nome
    
    @FXML
    private TextField txtCognome; //input cognome
    
    @FXML
    private TextField txtEmail; //input email
    
    @FXML
    private Button btnSalva; //bottone per salvare le modifiche
    
    @FXML
    private Button btnAnnulla; //bottone per annullare l'operazione
    
    public static String matricola; //serve per sapere quale utente aggiornare dopo aver premuto modifica nella tabella utenti 
 
    @FXML
    public void initialize(){
        lblMatricola.setText(matricola);
        btnAnnulla.setOnAction(eh->{Stage s =(Stage) btnAnnulla.getScene().getWindow();s.close();}); //bottone "Annulla" chiude la finestra
        btnSalva.setOnAction(eh->{ //bottone salva
        //CONTROLLI SUI CAMPI nome e cognome
            String nome = txtNome.getText().trim();
            String cognome = txtCognome.getText().trim();
            
            //caso entrambi vuoti
            if(nome.equals("") && cognome.equals("")){
                Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Campi vuoti");
                IsbnAlert.setContentText("Per favore inserisci un nome e un cognome");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
            return;
                
            }else if(nome.equals("")){ //nome vuoto
                Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Campi vuoti");
                IsbnAlert.setContentText("Per favore inserisci un nome");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
            return;
                
            }else if(cognome.equals("")){ //cognome vuoto
                Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Campi vuoti");
                IsbnAlert.setContentText("Per favore inserisci un cognome");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
            return;
            }
            
            //controllo formato email
            String mail = txtEmail.getText().trim();
            if(!model.servizi.ControlloFormato.controlloFormatoEmail(mail)){
            Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Email non valida");
                IsbnAlert.setContentText("L'e-mail deve essere del formato \"xxx@xx.xxx\" ");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
            return;   
            }
            
            //modifica effettiva nel database
            model.servizi.DataBase.modificaUtente(matricola, nome, cognome, mail);
            
            //conferma modifica
            Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Operazione eseguita");
                IsbnAlert.setContentText("Utente modificato");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                
                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
                Stage s = (Stage)btnSalva.getScene().getWindow(); ///chiusura finestra
                s.close();
            });
            
            
        
        
    }
    
}
