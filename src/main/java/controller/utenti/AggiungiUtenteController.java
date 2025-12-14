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
 * @brief Controller per la registrazione di un nuovo Utente.
 *
 * Gestisce il form per l'inserimento di un nuovo utente nel sistema.
 * Validazioni effettuate:
 * - Matricola (10 cifre, numerica, unica)
 * - Nome e Cognome non vuoti
 * - Email formato valido
 * - Limite massimo utenti registrabili
 * 
 * @author GRUPPO22
 */
public class AggiungiUtenteController {
    
    @FXML
    private TextField txtMatricola; 
    
    @FXML
    private TextField txtNome; 
    
    @FXML
    private TextField txtCognome; 
    
    @FXML
    private TextField txtEmail; 
    
    @FXML
    private Button AnnullaButton; 
    
    @FXML
    private Button SalvaButton; 
    
    
    /**
     * @brief Metodo chiamato automaticamente all'inizializzazione del controller.
     *
     * Qui vengono configurati i listener dei pulsanti "Salva" e "Annulla",
     * associando a ciascuno la logica appropriata.
     */
    @FXML
    public void initialize(){
        buttonInitialize(); 
    }
    
    //configurazione dei bottoni
    /**
     * @brief Configura i pulsanti della finestra "Aggiungi Utente".
     *
     * - AnnullaButton: chiude la finestra senza salvare nulla.
     * - SalvaButton: avvia una serie di controlli sui campi inseriti e, se tutti
     *   passano, crea un nuovo utente e lo aggiunge al database.
     *
     * La logica del pulsante Salva include:
     * 1. Validazione della matricola (lunghezza e formato numerico)
     * 2. Controllo che nome e cognome non siano vuoti
     * 3. Verifica del formato della mail
     * 4. Controllo che la matricola non sia già presente nel database
     * 5. Inserimento del nuovo utente nel database
     * 6. Controllo se il numero massimo di utenti è stato raggiunto e gestione dell'alert finale
     */
    public void buttonInitialize(){
        AnnullaButton.setOnAction(eh->{Stage s = (Stage) AnnullaButton.getScene().getWindow();s.close();});
        SalvaButton.setOnAction(eh->{  //pulsante salva
            String matricola = txtMatricola.getText().trim(); 
            
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
            
            //controllo email
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
                //salvataggio utente, creo nuovo utente e lo aggiungo al database
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
                Alert rt = new Alert(Alert.AlertType.ERROR);//mostro errore
                rt.setHeaderText("Chiusura Pannello");
                rt.setContentText("Troppi utenti registrati al sistema");
                DialogPane dialogPane1 = rt.getDialogPane();
                dialogPane1.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane1.getStyleClass().add("my-alert");       
                rt.showAndWait();
                Stage opp = (Stage)SalvaButton.getScene().getWindow(); 
                opp.close();//chiudo la finestra di aggiunta utente
                }
            return;       
            }
        });
    }
}
