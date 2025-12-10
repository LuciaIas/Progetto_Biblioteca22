/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.utenti.UtentiController;

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
 * @author nicol
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
    
    @FXML
    public void initialize(){
        ButtonInitialize();
    }
    
    public void buttonInitialize(){
    
        AnnullaButton.setOnAction(eh->{Stage s = (Stage) AnnullaButton.getScene().getWindow();s.close();});
        
        SalvaButton.setOnAction(eh->{
        
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
            
            String nome = txtNome.getText().trim();
            String cognome = txtCognome.getText().trim();
            
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
            
            //CONTROLLO MAIL
            String mail = txtEmail.getText().trim();
            if(!Model.CheckFormat.CheckEmailFormat(mail)){
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
                DataBase.addUser(new User(matricola,nome,cognome,mail,false));
            Alert IsbnAlert = new Alert(Alert.AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Utente aggiunto");
                IsbnAlert.setContentText("Utente con la matricola "+matricola+" e stato correttamente inserito");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                
                if(DataBase.getNumUser()>=UtentiController.MAX_USERS){
                
                     Alert rt = new Alert(Alert.AlertType.ERROR);
                rt.setHeaderText("Chiusura Pannello");
                rt.setContentText("Troppi utenti registrati al sistema");
                
                DialogPane dialogPane1 = rt.getDialogPane();

              
                dialogPane1.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane1.getStyleClass().add("my-alert");
                
                rt.showAndWait();
                
                Stage opp = (Stage)SalvaButton.getScene().getWindow();
                opp.close();
                }
                
            return;
            
            }
            
            
        });
        
    
    }
    
}
