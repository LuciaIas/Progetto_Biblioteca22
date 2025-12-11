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
    
    public static String matricola;


    
    
    
    @FXML
    public void initialize(){
    
        lblMatricola.setText(matricola);
        
        btnAnnulla.setOnAction(eh->{Stage s =(Stage) btnAnnulla.getScene().getWindow();s.close();});
        
        btnSalva.setOnAction(eh->{
        //CONTROLLI SUI CAMPI
           
            
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
            
            model.servizi.DataBase.modificaUtente(matricola, nome, cognome, mail);
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
