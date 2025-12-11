/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

//importa le classi javafx 
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import main.Main;

/**
 *
 * @author gruppo22
 */


public class AccessoController {
    
    //Sliding fxml
    /** Il Pane di Login */
    //contiene campo pswd, checkbox mostra pswd login, bottone accedi 
    @FXML
    private Pane LoginPane; 
    
     /** Il Pane di Register */
    //ho pswd di registraz, coferma pswd, checkbox mostra pswd, bottoni registrati
    @FXML
    private Pane RegisterForm;
  
     /** Il Pane di Sliding */
    //pannello che scorre quando clicclo pulsante x passare tra login e registrazione
    @FXML
    private Pane Sliding;
    
     /** Il Button per lo Sliding */
    @FXML
    private Button SlidingButton;
    
    /** La label che indica l'azione che si eseguirà */
    @FXML
    private Label SwitchLabel;
    
    //sliding var
    boolean direction=true;   
    TranslateTransition ts;   
    TranslateTransition tr;   
    final double slidingTiming=1000;  
    
    /** Il button che eseguira il login */
    @FXML
    private Button LoginButton;
    
    /** Il button che eseguira la registrazione */
    @FXML
    private Button RegisterButton;
    
    //REGISTER FORM
    /** Campo per nascondere la password */
    @FXML
    private PasswordField PassRegister;
    
    /** Campo per mostrare la password */
    @FXML
    private TextField PassRegisterVisible;
    
    /** Campo per nascondere la password confirm */
    @FXML
    private PasswordField PassConRegister;
    
    /** Campo per mostrare la password confirm */
    @FXML
    private TextField PassConRegisterVisible;
    
    /** Casella per settare visibile o meno la password */
    @FXML 
    private CheckBox CheckShowPassRegister;
    
    
    //LOGIN FORM
    /** Campo per nascondere la password */
    @FXML
    private PasswordField PassLogin;
    /** Campo per mostrare la password */
    @FXML
    private TextField PassLoginVisible;
    /** Casella per settare visibile o meno la password */
    @FXML 
    private CheckBox CheckShowPassLogin;
   
    /*
    @FXML
    private Label passwordRecovery;
    */

    @FXML
    public void initialize(){
        buttonInitialize();
        checkboxInitialize();
    }
    //INIZIALIZZO FUNZIONI DI BOTTONI,CHECKBOX

    public void buttonInitialize(){
        
        //sliding setup
        setSliding(); //SERVE PER LANCIARE IL MIO SLIDING INIZIALE
        SlidingButton.setOnAction(eh->{
            cleanForm(!direction);
            ts.play();
            tr.play();
            String text,text1;
            
            if(direction){
                text="Non sei registrato?";
                text1="Registrati";
            }else{
                text="Sei gia registrato?";
                text1="Accedi";
            }
                
            
            typewriterEffectLabel(SwitchLabel,text);
            typewriterEffectButton(SlidingButton,text1);
        });
                
        //REGISTER
        RegisterButton.setOnAction(eh->{
 
            if(CheckShowPassRegister.isSelected()){
                PassRegister.setText(PassRegisterVisible.getText());
                PassConRegister.setText(PassConRegisterVisible.getText());
            }else{
                PassRegisterVisible.setText(PassRegister.getText());
                PassConRegisterVisible.setText(PassConRegister.getText());
            }
                     
            String password = PassRegister.getText();
            
            if(!model.servizi.ControlloFormato.controlloFormatoPassword(password)){
            
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Errore Validazione"); // Titolo della finestra
                al.setHeaderText("Password non sicura"); // Titolo interno (o mettilo a null per toglierlo)
                al.setContentText("La password deve avere:\n- Minimo 8 caratteri\n- Una maiuscola\n- Un numero\n- Un simbolo (@#$%^&+=!)");

                DialogPane dialogPane = al.getDialogPane();

                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");
                al.showAndWait();
                return;
            }
            
            String password1 = PassConRegister.getText();
            
            if(!password.equals(password1)){
            
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setContentText("Le password devono coincidere");
                al.setHeaderText("Password non corrispondenti");

                DialogPane dialogPane = al.getDialogPane();

                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");
                al.showAndWait();
                return;
                
            }
            
            if(model.servizi.DataBase.inserisciBibliotecario(password)){
                Alert al = new Alert(Alert.AlertType.INFORMATION);
                al.setTitle("Operazione Completata");
                al.setHeaderText("Registrazione effettuata 🚀"); // Ho aggiunto un'emoji per coerenza
                al.setContentText("Sei stato registrato correttamente nel sistema.");
           
                DialogPane dialogPane = al.getDialogPane();
            
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");

                al.showAndWait();

                //ACCESSO ALLA DASHBOARD PRINCIPALE
                model.TransizioneScena.switchSceneEffect(Main.stage, "/View/Dashboard.fxml");
                Main.stage.getProperties().remove("login");
                Main.stage.centerOnScreen();
                
                
                return;
                

            }else{
                
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setContentText("Esiste gia un bibliotecario");
                al.setHeaderText("Registrazione fallita");
           
                DialogPane dialogPane = al.getDialogPane();

               
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");

                al.showAndWait();
                return;     
            }               
            
        });
         
        LoginButton.setOnAction(e->{
            if(CheckShowPassLogin.isSelected()){
                PassLogin.setText(PassLoginVisible.getText());
                
            }else{
                PassLoginVisible.setText(PassLogin.getText());
                
            }
            
            
            String password = PassLogin.getText();
            
            
            
            if(!model.servizi.DataBase.controllaPasswordBibliotecario(password)){
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setContentText("Password sbagliata");
                al.setHeaderText("La password e sbagliata");
                
                   
                DialogPane dialogPane = al.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");

                
                
                al.showAndWait();
                return;
            }
            

                     //ACCESSO ALLA DASHBOARD PRINCIPALE
                model.TransizioneScena.switchSceneEffect(Main.stage, "/View/Dashboard.fxml");
                Main.stage.getProperties().remove("login");
                Main.stage.centerOnScreen();
                
        
        });
        
   
    }
    
    public void checkboxInitialize(){
        CheckShowPassLogin.setOnAction(eh->{if(CheckShowPassLogin.isSelected())  showPassword(true,true); else showPassword(false,true); });
        CheckShowPassRegister.setOnAction(eh->{if(CheckShowPassRegister.isSelected()) showPassword(true,false); else showPassword(false,false); });
    }
    
    
    public void setSliding(){
        
        double moving = 325;
        
         if(direction)
            moving*=-1;
        
        
        ts = new TranslateTransition();
        tr = new TranslateTransition();
    
        ts.setByX(moving);
        tr.setByX(moving);
        
        ts.setNode(Sliding);
        tr.setNode(RegisterForm);
        
        ts.setDuration(Duration.millis(slidingTiming));
        tr.setDuration(Duration.millis(slidingTiming));
        
        direction=!direction;
        ts.setOnFinished(e->setSliding());
        
    }
    
    
    //PASSWORDBOX
    
        /** funzione per mostrare o nascondere la password */
    public void showPassword(boolean yes,boolean login){
        if(login){
            if(yes){
                PassLoginVisible.setText(PassLogin.getText());
                PassLoginVisible.setVisible(true);
                PassLogin.setVisible(false);
            }else{
                PassLogin.setText(PassLoginVisible.getText());
                PassLoginVisible.setVisible(false);
                PassLogin.setVisible(true);
            }
        }else{
            if(yes){
                PassRegisterVisible.setText(PassRegister.getText());
                PassRegisterVisible.setVisible(true);
                PassRegister.setVisible(false);
                
                PassConRegisterVisible.setText(PassConRegister.getText());
                PassConRegisterVisible.setVisible(true);
                PassConRegister.setVisible(false);
        
            }else{
                PassRegister.setText(PassRegisterVisible.getText());
                PassRegisterVisible.setVisible(false);
                PassRegister.setVisible(true);
                
                PassConRegister.setText(PassConRegisterVisible.getText());
                PassConRegisterVisible.setVisible(false);
                PassConRegister.setVisible(true);
            
                
            }
                        
            }
            
            
        
    }
    
    
    //FUNZIONE DI CLEAN DEL FORM
        /** pulizia dei form */
    public void cleanForm(boolean login){
        
        if(login){
            PassRegister.clear();
            PassRegisterVisible.clear();
            PassConRegister.clear();
            PassConRegisterVisible.clear();
            CheckShowPassRegister.setSelected(false);
        }else{
            PassLogin.clear();
            PassLoginVisible.clear();
            CheckShowPassLogin.setSelected(false);
        
        }
        
    }
      

      //FUNZIONI UTILI PER ESTETICA
   
    private void typewriterEffectLabel(Label label, String text) {
    
    label.setText("");
    
    
    Timeline timeline = new Timeline();
    
    for (int i = 0; i < text.length(); i++) {
    
        final int index = i;
        final String partialText = text.substring(0, index + 1);
        
       
        KeyFrame kf = new KeyFrame(
            Duration.millis(50 * (i + 1)), 
            e -> label.setText(partialText)
        );
        timeline.getKeyFrames().add(kf);
    }
    
    timeline.play();
}
 
    private void typewriterEffectButton(Button b, String text) {

    b.setText("");
    

    Timeline timeline = new Timeline();
    
    for (int i = 0; i < text.length(); i++) {

        final int index = i;
        final String partialText = text.substring(0, index + 1);

        KeyFrame kf = new KeyFrame(
            Duration.millis(50 * (i + 1)), 
            e -> b.setText(partialText)
        );
        timeline.getKeyFrames().add(kf);
    }
    
    timeline.play();
}
    
}
