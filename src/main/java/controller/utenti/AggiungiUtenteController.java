/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.utenti;

import controller.utenti.UtentiController;
import model.servizi.DataBase;
import model.dataclass.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author gruppo22
 */

//controller associato alla finestra "Aggiungi Utente"
public class AggiungiUtenteController {
    
    @FXML
    private TextField txtMatricola; //campo di testo per la matricola
    
    @FXML
    private TextField txtNome; //campo di input per nome
    
    @FXML
    private TextField txtCognome; //campo di input per cognome
    
    @FXML
    private TextField txtEmail; //campo di input per email
    
    @FXML
    private Button AnnullaButton; //pulsante "Annulla"
    
    @FXML
    private Button SalvaButton; //pulsante "Salva"
    
    @FXML
    public void initialize(){
        buttonInitialize(); //richiama la configurazione dei pulsanti 
    }
    
    //configurazione dei bottoni
    public void buttonInitialize(){
        AnnullaButton.setOnAction(eh->{Stage s = (Stage) AnnullaButton.getScene().getWindow();s.close();});
        SalvaButton.setOnAction(eh->{  //pulsante salva
            String matricola = txtMatricola.getText().trim(); //controllo matricola
            
            //CONTROLLI SUI CAMPI
            if(matricola.trim().length()!=10){
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Matricola non valida");
                IsbnAlert.setContentText("La matricola deve essere a 10 cifre");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            }else if(!matricola.trim().matches("\\d+")){
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Matricola non valida");
                IsbnAlert.setContentText("La matricola deve contenere solo numeri");
                DialogPane dialogPane = IsbnAlert.getDialogPane(); 
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
            return;
            }
            
            //controllo nome e cognome
            String nome = txtNome.getText().trim();
            String cognome = txtCognome.getText().trim();
            
            //nessun nome e cognome
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
                
            //nome mancante
            }else if(nome.equals("")){
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
                
            //cognome mancante
            }else if(cognome.equals("")){
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
            
            //xontrollo email
            String mail = txtEmail.getText().trim();
            if(!model.servizi.ControlloFormato.controlloFormatoEmail(mail)){ //verifica formato
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
            
            //controllo unicità matricola
            if(DataBase.isMatricolaPresent(matricola)){
                Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Operazione fallita");
                IsbnAlert.setContentText("Utente con la matricola "+matricola+" risulta gia registrato nel database");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");           
                IsbnAlert.showAndWait();
            return;
            }else{
                //salvataggio utente, crea nuovo utente e lo aggiunge al database
                DataBase.aggiungiUtente(new Utente(matricola,nome,cognome,mail,false));
                
                //conferma operazione riuscita
                Alert IsbnAlert = new Alert(Alert.AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Utente aggiunto");
                IsbnAlert.setContentText("Utente con la matricola "+matricola+" e stato correttamente inserito");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");    
                IsbnAlert.showAndWait();
                
                //controllo limite massimo utenti
                if(DataBase.getNumUser()>=UtentiController.MAX_USERS){
                Alert rt = new Alert(Alert.AlertType.ERROR);//mostra errore
                rt.setHeaderText("Chiusura Pannello");
                rt.setContentText("Troppi utenti registrati al sistema");
                DialogPane dialogPane1 = rt.getDialogPane();
                dialogPane1.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane1.getStyleClass().add("my-alert");       
                rt.showAndWait();
                Stage opp = (Stage)SalvaButton.getScene().getWindow(); //chiude la finestra di aggiunta utente
                opp.close();
                }
            return;       
            }
        });
    }
}
