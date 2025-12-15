/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.modificapassword;



import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class InsertCodePassRecoveryController {

    @FXML private TextField digit1;
    @FXML private TextField digit2;
    @FXML private TextField digit3;
    @FXML private TextField digit4;
    @FXML private TextField digit5;
    @FXML private TextField digit6;
    
    @FXML private Button VerifyButton;
    @FXML private Label ResendLabel;

   
    
    @FXML
    public void initialize() {
        SetEffect();
        SetButtonFunction();
       
    }
    
    public void SetButtonFunction(){
    
  
        
    
    }
    
    public void SetEffect(){
    // Mettiamo i campi in un array per gestirli facilmente
        TextField[] otpFields = {digit1, digit2, digit3, digit4, digit5, digit6};

        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            TextField field = otpFields[i];

            // 1. LIMITAZIONE INPUT: Solo numeri e max 1 carattere
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1) {
                    field.setText(newVal.substring(0, 1)); // Taglia se scrivi troppo
                }
                if (!newVal.matches("\\d*")) {
                    field.setText(newVal.replaceAll("[^\\d]", "")); // Cancella se non è numero
                }

                // 2. AUTO-AVANZAMENTO: Se ho scritto un numero, vai al prossimo
                if (field.getText().length() == 1 && currentIndex < otpFields.length - 1) {
                    otpFields[currentIndex+1].requestFocus();
                }
                if(getFullCode().length()==6)
                        VerifyButton.setDisable(false);
                     else
                        VerifyButton.setDisable(true);
            });

            // 3. BACKSPACE: Se cancello, torna al precedente
            field.setOnKeyPressed((KeyEvent event) -> {
                //DIABILITO IL BOTTONE SE NON HO DIGITATO TUTTE LE CIFRE
                
          
                
                
                switch (event.getCode()) {
                    case BACK_SPACE:
                        if (field.getText().isEmpty() && currentIndex > 0) {
                            otpFields[currentIndex - 1].requestFocus();
                        }
                        break;
                    case LEFT: // Supporto frecce tastiera
                        if (currentIndex > 0) otpFields[currentIndex - 1].requestFocus();
                        break;
                    case RIGHT:
                         if (currentIndex < otpFields.length - 1) otpFields[currentIndex + 1].requestFocus();
                        break;
                    default:
                        break;
                }
            });
        }
    }
    
    // Metodo per ottenere il codice completo come stringa unica
    public String getFullCode() {
        return digit1.getText() + digit2.getText() + digit3.getText() + 
               digit4.getText() + digit5.getText() + digit6.getText();
    }
}
