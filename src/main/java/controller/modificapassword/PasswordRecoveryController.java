/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author nicol
 */
public class PasswordRecoveryController {
    
    
    @FXML
    private TextField mailField;
    
    @FXML
    private Button RecoveryButton;
    
    public static String code;
    public static String Email;
    
    @FXML
    public void initialize(){
        
        setButtonFunction();
    }
    
    public void setButtonFunction(){
        RecoveryButton.setOnAction(e->{
        
            String email = mailField.getText();
            //RICORDA DI MODIFICARE ALLINSERT USER IL CheckEmailFormat E RENDERE EMAIL NON DUPLICABILI
            if(Model.FormatValidation.CheckEmailFormat(email)){
                
                if(BinaryDB.SearchUserByEmail(email)!=null){
                    
                    code = CodeGeneration();
                    Email = email;
                    
                    try {
                        ControllerAccess.getPas().setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/InsertCodePassRecovery.fxml"))));
                        ControllerAccess.getPas().setOnCloseRequest(eh->{code=null;Email=null;});
                        
                            Thread threadParallelo = new Thread(() -> {

                                 Model.EmailSender.sendEmail(email, "Password Recovery",code);

                            });

                            
                            threadParallelo.setDaemon(true); //CHIUDO IL TRHEAD PER PRECAUZIONE
                            threadParallelo.start();

                        
                        
                       
                    } catch (IOException ex) {
                        Logger.getLogger(PasswordRecoveryController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }
                else{
             Alert err = new Alert(Alert.AlertType.WARNING);
             err.setContentText("L'email inserita non e presente nel sistema");
             err.showAndWait();
            }
            }else{
             Alert err = new Alert(Alert.AlertType.WARNING);
             err.setContentText("L'email inserita non e valida");
             err.showAndWait();
            }
        
        });
        
    }
    
    
    public String CodeGeneration(){
        String code = "";
        int n;
        for(int i=0;i<6;i++){
            double n1 = Math.random()*9.999;
            n = (int) n1;
            code+=n;
        }
        return code;
        }
    
    
}
