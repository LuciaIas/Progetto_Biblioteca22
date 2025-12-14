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
 * @brief Controller per la gestione della finestra di modifica dei dati di un utente esistente.
 *
 * Questa classe gestisce l'interfaccia grafica per aggiornare le informazioni personali
 * di un utente già registrato (Nome, Cognome e Email). La matricola non è modificabile
 * e viene passata come variabile statica prima dell'apertura della finestra.
 *
 * La logica include:
 * - Validazione dei campi Nome e Cognome (non vuoti)
 * - Controllo del formato della mail tramite la classe ControlloFormato
 * - Aggiornamento dei dati dell'utente nel database
 * - Notifica all'utente tramite alert grafici in caso di errore o conferma
 *
 * 
 * @author gruppo22
 */
public class ModificaUtenteController {
    
    @FXML
    private Label lblMatricola; 
    
    @FXML
    private TextField txtNome; 
    
    @FXML
    private TextField txtCognome; 
    
    @FXML
    private TextField txtEmail; 
    
    @FXML
    private Button btnSalva; 
    
    @FXML
    private Button btnAnnulla; 
    
    public static String matricola; //serve per sapere quale utente aggiornare dopo aver premuto modifica nella tabella utenti 
 
    
     /**
     * @brief Metodo chiamato all'apertura della finestra per inizializzare il controller.
     *
     * Questo metodo esegue le seguenti operazioni:
     * 1. Imposta il testo della label matricola usando la variabile statica.
     * 2. Configura il pulsante "Annulla" per chiudere lo stage senza salvare modifiche.
     * 3. Configura il pulsante "Salva" con la logica di validazione e aggiornamento:
     *    - Verifica che nome e cognome non siano vuoti.
     *    - Controlla che l'email abbia un formato valido.
     *    - Aggiorna i dati nel database se tutte le verifiche sono superate.
     *    - Mostra un alert di conferma e chiude la finestra.
     */
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
                Stage s = (Stage)btnSalva.getScene().getWindow(); 
                s.close();
            });   
    }   
}
