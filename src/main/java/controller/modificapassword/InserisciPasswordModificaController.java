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
 *
 * @author gruppo22
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
    public void initialize(){
        
        setCheckBox();
        setButtonFunction();
        
        
    }
    
    public void setButtonFunction(){
    
        BtnSalva.setOnAction(eh->{
            String pass;
            if(!CheckShowPass.isSelected())
             pass = NewPass.getText();
          
            else
            pass = NewPassVisible.getText();
            
            
            //Controllo password e conferma password
            if(pass.equals("")){
                Alert err = new Alert(Alert.AlertType.WARNING);
                err.setContentText("Devi inserire password");
                err.showAndWait();
                return;
            }
            
          
                if(DataBase.controllaPasswordBibliotecario(pass)){
                        
                
            Stage PassRec = new Stage();
                PassRec.setTitle("Modifica Password");
                PassRec.setResizable(false);
                PassRec.initModality(Modality.APPLICATION_MODAL);
            try {
                PassRec.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/CambioPassword.fxml"))));
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
                
                PassRec.showAndWait();
                Stage u = (Stage) BtnSalva.getScene().getWindow();
                u.close();
                }else{
                    
                Alert err = new Alert(Alert.AlertType.WARNING);
                err.setContentText("Password errata!");
                err.showAndWait();
                return;
                    
                
                }
        });
        
         BtnAnnulla.setOnMouseClicked(eh->{
        
            Stage f =  (Stage) BtnAnnulla.getScene().getWindow();
                f.close();
            
            
        });
    }
    
    
    public void setCheckBox(){
    showPassword(false);
        CheckShowPass.setOnAction(eh->{
        
        if(CheckShowPass.isSelected())
            showPassword(true);
        else
            showPassword(false);
        
        });
        
    }
    
    
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
