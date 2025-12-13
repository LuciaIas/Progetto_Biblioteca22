package controller;
 
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
 * @brief Controller per la gestione dell'autenticazione.
 * * Questa classe gestisce l'interfaccia di Login e Registrazione.
 * Implementa un meccanismo di "Sliding" (scorrimento) per passare da un form all'altro
 * all'interno della stessa finestra. Gestisce inoltre:
 * - Validazione delle password (complessità e corrispondenza).
 * - Visibilità della password (show/hide).
 * - Connessione al Database per login e registrazione.
 * - Transizione verso la Dashboard principale.
 * * @author gruppo22
 * @version 1.0
 */
public class AccessoController {//Controller per eseguire login e registrazione
    
    //Sliding fxml
    @FXML
    private Pane LoginPane; //Contiene campo pswd, checkbox mostra pswd login, bottone accedi 
    
    @FXML
    private Pane RegisterForm;//Contiene pswd di registraz, coferma pswd, checkbox mostra pswd, bottoni registrati
  
    @FXML
    private Pane Sliding; //Pannello che scorre quando clicco pulsante per passare tra login e registrazione
    
    @FXML
    private Button SlidingButton; 
    
    @FXML
    private Label SwitchLabel;// La label che indica l'azione che si eseguirà
    
    // Variabili per animazioni sliding
    boolean direction=true;    // true = verso registrazione, false = verso login
    TranslateTransition ts;   // animazione sliding pane principale
    TranslateTransition tr;   // animazione sliding form registrazione
    final double slidingTiming=1000;  // durata animazione in millisecondi

    @FXML
    private Button LoginButton;

    @FXML
    private Button RegisterButton;
    
    //REGISTER FORM
    @FXML
    private PasswordField PassRegister;// campo password nascosto
    
    @FXML
    private TextField PassRegisterVisible; // campo password visibile
    
    @FXML
    private PasswordField PassConRegister; // campo conferma password nascosto

    @FXML
    private TextField PassConRegisterVisible; // campo conferma password visibile

    @FXML 
    private CheckBox CheckShowPassRegister;// checkbox per mostrare/nascondere la password
   
    
    //LOGIN FORM
    @FXML
    private PasswordField PassLogin;// password login nascosta

    @FXML
    private TextField PassLoginVisible; // password login visibile

    @FXML 
    private CheckBox CheckShowPassLogin;// checkbox per mostrare/nascondere password

   
    @FXML
     /** * @brief Metodo di inizializzazione del controller.
     * Viene chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * Inizializza la logica dei bottoni e delle checkbox.
     */
    public void initialize(){ // Inizializzazione controller. Configura bottoni e checkbox
        buttonInitialize();
        checkboxInitialize();
    }

     // --- INITIALIZATION BUTTON AND CHECKBOX ACTION ---
    
    /** * @brief Configura le azioni dei pulsanti principali.
     * * Questo metodo contiene la logica più della classe:
     * 1. **SlidingButton:** Gestisce l'animazione, la pulizia dei form e l'effetto macchina da scrivere della label.
     * 2. **RegisterButton:** * - Gestisce i campi testo/password.
     * - Sincronizza i campi per mostrare/nascondere la password
     * - Valida il formato della password (CheckPasswordFormat).
     * - Controlla che le password coincidano.
     * - Tenta l'inserimento nel DB e gestisce gli Alert di successo/errore.
     * 3. **LoginButton:**
     * - Sincronizza i campi per mostrare/nascondere la password.
     * - Verifica le credenziali nel DB.
     * - Se corretto, effettua il cambio scena verso la Dashboard.
     */
    public void buttonInitialize(){ //Configura le azioni dei bottoni e lo sliding tra i form
 
        setSliding(); // Imposta sliding iniziale
        
        SlidingButton.setOnAction(eh->{  // Azione bottone sliding
            cleanForm(!direction);
            ts.play();
            tr.play();
            String text,text1;
            
            if(direction){ // Aggiorna testo label e bottone in base alla direzione dello sliding
                text="Non sei registrato?";
                text1="Registrati";
            }else{
                text="Sei gia registrato?";
                text1="Accedi";
            }
                           
            typewriterEffectLabel(SwitchLabel,text);
            typewriterEffectButton(SlidingButton,text1);
        });
               
        RegisterButton.setOnAction(eh->{ // Azione bottone registrazione
            if(CheckShowPassRegister.isSelected()){// Sincronizza password visibile e nascosta
                PassRegister.setText(PassRegisterVisible.getText());
                PassConRegister.setText(PassConRegisterVisible.getText());
            }else{
                PassRegisterVisible.setText(PassRegister.getText());
                PassConRegisterVisible.setText(PassConRegister.getText());
            }
                     
            String password = PassRegister.getText();

            if(!model.servizi.ControlloFormato.controlloFormatoPassword(password)){// Controllo formato password
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Errore Validazione"); // Titolo della finestra
                al.setHeaderText("Password non sicura"); // Titolo interno 
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
            if(!password.equals(password1)){ // Controllo che password e conferma coincidano          
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
            
            if(model.servizi.DataBase.inserisciBibliotecario(password)){// Inserisco bibliotecario nel DB
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

                model.TransizioneScena.switchSceneEffect(Main.stage, "/View/Dashboard.fxml");// Passa alla dashboard

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
        
        LoginButton.setOnAction(e->{   // Azione bottone login
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

                model.TransizioneScena.switchSceneEffect(Main.stage, "/View/Dashboard.fxml");// Passa alla dashboard
                Main.stage.getProperties().remove("login");
                Main.stage.centerOnScreen();
        }); 
    }
    
     /** * @brief Inizializza i listener per le CheckBox "Mostra Password".
     * Collega l'evento di selezione al metodo `ShowPassword` per alternare
     * la visibilità tra TextField e PasswordField.
     */
    public void checkboxInitialize(){// Configura le checkbox per mostrare/nascondere password
        CheckShowPassLogin.setOnAction(eh->{if(CheckShowPassLogin.isSelected())  showPassword(true,true); else showPassword(false,true); });
        CheckShowPassRegister.setOnAction(eh->{if(CheckShowPassRegister.isSelected()) showPassword(true,false); else showPassword(false,false); });
    }
    
        /** * @brief Configura le transizioni per l'effetto Sliding.
     * Prepara gli oggetti `TranslateTransition`(effetto sliding) per spostare i pannelli `Sliding`
     * e `RegisterForm`. Viene richiamato ricorsivamente al termine di ogni animazione per reimpostare
     * la direzione.
     */
    public void setSliding(){//Imposta le animazioni di sliding dei Pane
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
        
        direction=!direction; // cambia direzione per prossimo sliding
        ts.setOnFinished(e->setSliding());// loop animazione
        
    }
    
        /** * @brief Alterna la visibilità della password.
     * Nasconde il `PasswordField` e mostra il `TextField` (o viceversa) copiando il testo contenuto.
     * * @param yes Se true, mostra la password in chiaro. Se false, la nasconde.
     * @param login Se true, agisce sui campi del Login. Se false, su quelli della Registrazione.
     */
    public void showPassword(boolean yes,boolean login){//Mostra o nasconde la password nei form login o registrazione
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
    
        /** * @brief Pulisce i campi del form.
     * Viene chiamato quando si passa dalla schermata Login a Register (e viceversa)
     * per evitare che rimangano dati inseriti precedentemente.
     * * @param login Se true, pulisce il form di Registrazione (perché stiamo andando al Register form).
     * Se false, pulisce il form di Login (perché stiamo andando al Login form).
     */
    public void cleanForm(boolean login){//Pulisce i form di login o registrazione     
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
      
    
     /** * @brief Applica un effetto "macchina da scrivere" a una Label.
     * Scrive il testo carattere per carattere utilizzando una Timeline.
     * * @param label La label a cui applicare l'effetto.
     * @param text Il testo da visualizzare.
     */
    private void typewriterEffectLabel(Label label, String text) {//Effetto "macchina da scrivere" per label
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
 
    /** * @brief Applica un effetto "macchina da scrivere" a una Label.
     * Scrive il testo carattere per carattere utilizzando una Timeline.
     * * @param label La label a cui applicare l'effetto.
     * @param text Il testo da visualizzare.
     */
    private void typewriterEffectButton(Button b, String text) {//Effetto "macchina da scrivere" per button
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
