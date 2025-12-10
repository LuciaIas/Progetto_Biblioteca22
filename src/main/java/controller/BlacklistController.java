
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import Model.DataBase;
import Model.DataClass.User;
import Model.EmailSender;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * @author nicol
 */
public class BlacklistController {
    
    @FXML
    private VBox blacklistContainer;
    
    @FXML
    private Label lblTotalBlocked;
    
    @FXML
    private Button UnLockAllButton;
    
    @FXML
    private TextField searchUser;
    
    @FXML
    public void initialize(){
        ArrayList<User> us =User.getUsersBlackListed(DataBase.getUtenti());
        updateUtentiList(us);
        UnLockAllButton.setOnAction(eh->{
        
            for(User u: us)
                if(u.isBloccato())
                    DataBase.UnsetBlackListed(u.getMatricola());
            updateUtentiList(new ArrayList<User>());
        });
        
        searchUser.setOnKeyPressed(eh->{
        
            if(eh.getCode()==KeyCode.ENTER)
                SearchFunction();
        });
        searchUser.textProperty().addListener((a,b,c)->{
        
            if(searchUser.getText().trim().equals(""))
                updateUtentiList(User.getUsersBlackListed(DataBase.getUtenti()));
            
        });
        
    }
    
     public void searchFunction(){
    
        ArrayList<User> utenti = new ArrayList<>(),app= new ArrayList<>();
           String text = searchUser.getText().trim();
       
           //CERCA PER ISBN PRIMA
           User u = DataBase.searchUser(text);
           if(u!=null){
               if(u.isBloccato()){
               utenti.add(u);
               updateUtentiList(utenti);
               return;
               }
           }
           utenti = User.getUsersBlackListed(DataBase.getUtenti());
        
           for(User u1 : utenti)
               if(u1.getNome().equals(text) || u1.getCognome().equals(text) || u1.getMail().equals(text))
                   app.add(u1);
           updateUtentiList(app);
           
    }
        
    public void updateUtentiList(ArrayList<User> utenti){
        blacklistContainer.getChildren().clear();
        ArrayList<User> us =User.getUsersBlackListed(DataBase.getUtenti());
        lblTotalBlocked.setText(us.size()+" Utenti Bloccati");
        
        for(User u : utenti){
            aggiungiCardUtente(u.getNome(),u.getCognome(),u.getMatricola(),u.getMail(),u.isBloccato());
        }
    }
    
    /**
     * Crea una riga utente e la aggiunge alla lista.
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param matricola Matricola univoca
     * @param email Email istituzionale
     * @param isBlacklisted Se true, l'utente appare rosso (bloccato)
     */
    private void aggiungiCardUtente(String nome, String cognome, String matricola, String email, boolean isBlacklisted) {
        
        // 1. CREAZIONE RIGA PRINCIPALE (HBox)
        HBox riga = new HBox();
        riga.setAlignment(Pos.CENTER_LEFT);
        riga.setSpacing(20);
        riga.setPrefHeight(80);
        riga.setPadding(new Insets(0, 20, 0, 20));
        
        // Stile condizionale: Se è bloccato usa lo stile rosso, altrimenti bianco
        if (isBlacklisted) {
            riga.getStyleClass().add("user-row-blocked");
        } else {
            riga.getStyleClass().add("user-row");
        }

        // 2. ICONA
        StackPane iconContainer = new StackPane();

        // Definisci la grandezza del cerchio (es. 45px)
        double size = 45;

        // BLOCCO LE DIMENSIONI: Larghezza e Altezza DEVONO essere uguali
        iconContainer.setMinWidth(size);
        iconContainer.setMinHeight(size);
        iconContainer.setPrefSize(size, size);
        iconContainer.setMaxSize(size, size); // Impedisce che si allarghi e diventi ovale

        // Stile (assicurati che nel CSS ci sia -fx-background-radius: 50%;)
        iconContainer.getStyleClass().add("icon-container-red");

        Label iconLabel = new Label("🚫");
        iconLabel.setStyle("-fx-font-size: 20px;"); // Aumentato un po' per centrare visivamente meglio

        iconContainer.getChildren().add(iconLabel);

        // 3. DATI PRINCIPALI (Nome e Email)
        VBox boxNomi = new VBox();
        boxNomi.setAlignment(Pos.CENTER_LEFT);
        boxNomi.setPrefWidth(250);
        
        Label lblNome = new Label(nome + " " + cognome);
        lblNome.getStyleClass().add("row-title");
        
        Label lblEmail = new Label(email);
        lblEmail.getStyleClass().add("row-subtitle");
        
        boxNomi.getChildren().addAll(lblNome, lblEmail);

        // 4. MATRICOLA
        VBox boxMatricola = new VBox();
        boxMatricola.setAlignment(Pos.CENTER_LEFT);
        boxMatricola.setPrefWidth(150);
        
        Label lblMatTitle = new Label("MATRICOLA");
        lblMatTitle.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        Label lblMatValue = new Label(matricola);
        lblMatValue.setStyle("-fx-text-fill: #2d3436; -fx-font-weight: bold;");
        
        boxMatricola.getChildren().addAll(lblMatTitle, lblMatValue);

        // 5. SPAZIATORE (Pane vuoto che spinge tutto a destra)
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 6. ETICHETTA STATO (Attivo / Blacklist)
        Label lblStato = new Label(isBlacklisted ? "Blacklist" : "Attivo");
        lblStato.getStyleClass().add(isBlacklisted ? "tag-danger" : "tag-success");

      

        Button btnEmail = new Button("✉️");
        btnEmail.getStyleClass().add("icon-button"); // Usa lo stile trasparente/grigio
        
        // Aggiungiamo un tooltip per far capire cosa fa
        Tooltip tooltip = new Tooltip("Invia email a " + email);
        btnEmail.setTooltip(tooltip);
        tooltip.setShowDelay(Duration.millis(100));
        
        btnEmail.setOnAction(e -> {
            
            if(EmailSender.SendAvviso(email, null, nome, cognome, null)){
            
            Alert IsbnAlert = new Alert(Alert.AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Avviso inviato");
                IsbnAlert.setContentText("Il sistema ha inviato un email all'utente");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            }
            else{
            
                 Alert IsbnAlert = new Alert(Alert.AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Errore nell'invio dell'avviso");
                IsbnAlert.setContentText("L'utente potrebbe aver inserito una mail inesistente");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            
            }
        });
        
        
        // Bottone Blocca/Sblocca
        Button btnAction = new Button("SBLOCCA");
        btnAction.getStyleClass().add("button-outline-success");
        btnAction.setOnAction(e -> {
            //System.out.println(isBlacklisted ? "Sblocco utente..." : "Blocco utente...");
            // logicaBloccoSblocco(matricola);
           
            Model.DataBase.UnsetBlackListed(matricola);
            updateUtentiList(User.getUsersBlackListed(DataBase.getUtenti()));
            
        });

        // 8. ASSEMBLAGGIO FINALE
        riga.getChildren().addAll(
            iconContainer, 
            boxNomi, 
            boxMatricola, 
            spacer, 
            lblStato,
            btnEmail,
            btnAction
        );

        // Aggiungi la riga al contenitore verticale dell'interfaccia
        blacklistContainer.getChildren().add(riga);
    }
    
}
