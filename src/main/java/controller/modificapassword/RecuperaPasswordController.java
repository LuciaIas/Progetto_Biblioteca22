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


public class RecuperaPasswordController {
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
        });
        
    }
    
    
    public String codeGeneration(){
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
