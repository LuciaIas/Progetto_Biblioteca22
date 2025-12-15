/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;


import controller.DashboardController;
import static controller.DashboardController.PassRec;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static model.servizi.DataBase.conn;


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
            
             ResultSet rs = null;
                String query = "Select email from bibliotecario";  
                try {
                    PreparedStatement stat = conn.prepareStatement(query);           
                     rs = stat.executeQuery();           
                     rs.next();
                     
                     
                     String email = rs.getString(1);
                     
                     if(!email.equals(mailField.getText())){
                     
                                     Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Errore"); // Titolo della finestra
                al.setHeaderText("Errore Email"); // Titolo interno 
                al.setContentText("Email non presente nel sistema");
                DialogPane dialogPane = al.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/CSS/StyleAccess.css").toExternalForm());
                dialogPane.getStyleClass().add("my-alert");
                al.showAndWait();
                return;      
                     
                     }
                     code = codeGeneration();
                     
                     model.servizi.EmailInvia.inviaEmail(email, "Recupero password", "Il codice per procedere al recupero della password : "+code);
                
                PassRec = new Stage(); 
                PassRec.setTitle("Recupero Password");
                PassRec.setResizable(false);
                PassRec.initModality(Modality.APPLICATION_MODAL); //blocco finestra principale                
            try {
                //carico la schermata di inserimento nuova password 
                PassRec.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/InserimentoCodiceRecupero.fxml"))));
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
            PassRec.show();
                Stage s = (Stage) RecoveryButton.getScene().getWindow();
                s.close();
                     
                }catch(Exception exe){
                
                
                }
            
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
