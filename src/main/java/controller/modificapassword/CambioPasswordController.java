/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;


import model.servizi.ControlloFormato;
import Model.servizi.DataBase;

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
 * @author nicol
 */
public class PasswordChargeController {
    
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
        
        SetCheckBox();
        SetButtonFunction();
        
        
    }
    
    public void setButtonFunction(){
    
        BtnSalva.setOnAction(eh->{
            String pass,pass1;
            if(!CheckShowPass.isSelected()){
             pass = NewPass.getText();
             pass1 = ConfirmPass.getText();
            }else{
            pass = NewPassVisible.getText();
             pass1 = ConfirmPassVisible.getText();
            }
            
            //Controllo password e conferma password
            if(pass.equals("") || pass1.equals("")){
                Alert err = new Alert(Alert.AlertType.WARNING);
                err.setContentText("Completa entrambi i campi delle password");
                err.showAndWait();
                return;
            }else if(!pass.equalsIgnoreCase(pass1)){
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setContentText("Le password non corrispodono");
                err.showAndWait();
                return;
            }else if(!CheckFormat.CheckPasswordFormat(pass)){
            
                Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Errore Validazione"); // Titolo della finestra
                al.setHeaderText("Password non sicura 🔒"); // Titolo interno (o mettilo a null per toglierlo)
                al.setContentText("La password deve avere:\n- Minimo 8 caratteri\n- Una maiuscola\n- Un numero\n- Un simbolo (@#$%^&+=!)");

                DialogPane dialogPane = al.getDialogPane();

                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");


                al.showAndWait();
                return;
                
            }
            
          
                DataBase.RemoveBibliotecario();
                DataBase.InsertBibliotecario(pass);
                 Alert IsbnAlert = new Alert(AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Password aggiornata");
                IsbnAlert.setContentText("Modifica effettuata con successo");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                
                Stage f =  (Stage) BtnSalva.getScene().getWindow();
                f.close();
            
            
        });
        BtnAnnulla.setOnMouseClicked(eh->{
        
            Stage f =  (Stage) BtnAnnulla.getScene().getWindow();
                f.close();
            
            
        });
        
    }
    
    
    public void setCheckBox(){
    ShowPassword(false);
        CheckShowPass.setOnAction(eh->{
        
        if(CheckShowPass.isSelected())
            ShowPassword(true);
        else
            ShowPassword(false);
        
        });
        
    }
    
    
    public void showPassword(boolean yes){
        
            if(yes){
                NewPassVisible.setText(NewPass.getText());
                NewPassVisible.setVisible(true);
                NewPass.setVisible(false);
                
                ConfirmPassVisible.setText(ConfirmPass.getText());
                ConfirmPassVisible.setVisible(true);
                ConfirmPass.setVisible(false);
        
            }else{
                NewPass.setText(NewPassVisible.getText());
                NewPassVisible.setVisible(false);
                NewPass.setVisible(true);
                
                ConfirmPass.setText(ConfirmPassVisible.getText());
                ConfirmPassVisible.setVisible(false);
                ConfirmPass.setVisible(true);
            
                
            
                        
            }
            
            
        
    }
    
    
}
