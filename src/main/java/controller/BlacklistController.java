
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.servizi.DataBase;
import model.dataclass.Utente;
import model.servizi.EmailInvia;
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
 * @author gruppo22
 */

//controller per la gestione della blacklist degli utenti
public class BlacklistController {
    
    //contenitore grafico della lista utenti bloccati
    @FXML
    private VBox blacklistContainer;
    
    //etichetta che mostra il numero totale di utenti bloccati
    @FXML
    private Label lblTotalBlocked;
    
    //bottone per sbloccare tutti gli utenti
    @FXML
    private Button UnLockAllButton;
    
    //campo di ricerca utenti
    @FXML
    private TextField searchUser;
    
    //inizializzazione del controller
    @FXML
    public void initialize(){
        //recupera utenti bloccati e aggiorna la lista
        ArrayList<Utente> us =Utente.getUtentiBlackListed(DataBase.getUtenti());
        updateUtentiList(us);
        
        //azione pulsante: sblocca tutti gli utenti
        UnLockAllButton.setOnAction(eh->{
        
            for(Utente u: us)
                if(u.isBloccato())
                    DataBase.unsetBlackListed(u.getMatricola());
            updateUtentiList(new ArrayList<Utente>());
        });
        
        //esegui ricerca premendo INVIO
        searchUser.setOnKeyPressed(eh->{
        
            if(eh.getCode()==KeyCode.ENTER)
                searchFunction();
        });
        
        //reset lista se campo ricerca vuoto
        searchUser.textProperty().addListener((a,b,c)->{
        
            if(searchUser.getText().trim().equals(""))
                updateUtentiList(Utente.getUtentiBlackListed(DataBase.getUtenti()));
            
        });
        
    }
    
    //funzione di ricerca utente
     public void searchFunction(){
        ArrayList<Utente> utenti = new ArrayList<>(),app= new ArrayList<>();
           String text = searchUser.getText().trim();
       
           //cerca per matricola (o identificatore)
           Utente u = DataBase.cercaUtente(text);
           if(u!=null){
               if(u.isBloccato()){
               utenti.add(u);
               updateUtentiList(utenti);
               return;
               }
           }
           //filtra utenti bloccati
           utenti = Utente.getUtentiBlackListed(DataBase.getUtenti());
        
           //filtra per nome, cognome o email
           for(Utente u1 : utenti)
               if(u1.getNome().equals(text) || u1.getCognome().equals(text) || u1.getMail().equals(text))
                   app.add(u1);
           
           //aggiorna lista risultati ricerca
           updateUtentiList(app);
           
    }
        
    //aggiorna la lista grafica degli utenti
    public void updateUtentiList(ArrayList<Utente> utenti){
        blacklistContainer.getChildren().clear();
        
        //mostra numero totale bloccati
        ArrayList<Utente> us =Utente.getUtentiBlackListed(DataBase.getUtenti());
        lblTotalBlocked.setText(us.size()+" Utenti Bloccati");
        
        //crea card per ogni utente
        for(Utente u : utenti){
            aggiungiCardUtente(u.getNome(),u.getCognome(),u.getMatricola(),u.getMail(),u.isBloccato());
        }
    }
    
    //crea card grafica per singolo utente
    private void aggiungiCardUtente(String nome, String cognome, String matricola, String email, boolean isBlacklisted) {
        
        // 1. Creazione riga principale (HBox)
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

        // 2. Icona utente bloccato
        StackPane iconContainer = new StackPane();

        // Definisce la grandezza del cerchio
        double size = 45;

        // BLOCCO LE DIMENSIONI: Larghezza e Altezza DEVONO essere uguali
        iconContainer.setMinWidth(size);
        iconContainer.setMinHeight(size);
        iconContainer.setPrefSize(size, size);
        iconContainer.setMaxSize(size, size); // Impedisce che si allarghi e diventi ovale

        // Stile 
        iconContainer.getStyleClass().add("icon-container-red");

        Label iconLabel = new Label("🚫");
        iconLabel.setStyle("-fx-font-size: 20px;"); // Aumentato un po' per centrare visivamente meglio

        iconContainer.getChildren().add(iconLabel);

        // 3. dati utente (Nome e Email)
        VBox boxNomi = new VBox();
        boxNomi.setAlignment(Pos.CENTER_LEFT);
        boxNomi.setPrefWidth(250);
        
        Label lblNome = new Label(nome + " " + cognome);
        lblNome.getStyleClass().add("row-title");
        
        Label lblEmail = new Label(email);
        lblEmail.getStyleClass().add("row-subtitle");
        
        boxNomi.getChildren().addAll(lblNome, lblEmail);

        // 4.Matricola utente
        VBox boxMatricola = new VBox();
        boxMatricola.setAlignment(Pos.CENTER_LEFT);
        boxMatricola.setPrefWidth(150);
        
        Label lblMatTitle = new Label("MATRICOLA");
        lblMatTitle.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        Label lblMatValue = new Label(matricola);
        lblMatValue.setStyle("-fx-text-fill: #2d3436; -fx-font-weight: bold;");
        
        boxMatricola.getChildren().addAll(lblMatTitle, lblMatValue);

        // 5. Spaziatore per allineamento (Pane vuoto che spinge tutto a destra)
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 6. Stato (Attivo / Blacklist)
        Label lblStato = new Label(isBlacklisted ? "Blacklist" : "Attivo");
        lblStato.getStyleClass().add(isBlacklisted ? "tag-danger" : "tag-success");

        //Bottone invio email
        Button btnEmail = new Button("✉️");
        btnEmail.getStyleClass().add("icon-button"); // Usa lo stile trasparente/grigio
        
        // Aggiungiamo un tooltip per far capire cosa fa
        Tooltip tooltip = new Tooltip("Invia email a " + email);
        btnEmail.setTooltip(tooltip);

        
        btnEmail.setOnAction(e -> {
            
            if(EmailInvia.inviaAvviso(email, null, nome, cognome, null)){
            //Alert invio riuscito
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
                //Alert errore invio
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
        
        // Bottone sblocca utente
        Button btnAction = new Button("SBLOCCA");
        btnAction.getStyleClass().add("button-outline-success");
        btnAction.setOnAction(e -> {
            //System.out.println(isBlacklisted ? "Sblocco utente..." : "Blocco utente...");
            // logicaBloccoSblocco(matricola);
           
            model.servizi.DataBase.unsetBlackListed(matricola);
            updateUtentiList(Utente.getUtentiBlackListed(DataBase.getUtenti()));
            
        });

        // 8. Aggiunta elementi alla riga
        riga.getChildren().addAll(
            iconContainer, 
            boxNomi, 
            boxMatricola, 
            spacer, 
            lblStato,
            btnEmail,
            btnAction
        );

        // Aggiunge la riga al contenitore verticale dell'interfaccia
        blacklistContainer.getChildren().add(riga);
    }
    
}
