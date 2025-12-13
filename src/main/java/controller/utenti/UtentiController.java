/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.utenti;

import controller.catalogo.CatalogoController;
import model.servizi.DataBase;
import model.dataclass.Utente;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Configurazione;


/**
 * @brief Controller per la gestione della lista Utenti.
 * * Questa classe gestisce la visualizzazione e la gestione degli utenti iscritti al sistema.
 * Permette di:
 * - Visualizzare l'elenco completo degli utenti.
 * - Filtrare per stato (Attivi, Bloccati/Blacklist).
 * - Cercare utenti per nome, cognome, email o matricola.
 * - Aggiungere nuovi utenti.
 * - Modificare, Eliminare o Bloccare/Sbloccare utenti esistenti direttamente dalla lista.
 * * @author gruppo22
 * @version 1.0
 */
public class UtentiController {//controller principale che gestisce la schermata di amministrazione utenti
    @FXML
    private VBox usersContainer; //Contenitore verticale dove vengono aggiunte le card degli utenti
    
    @FXML
    private Button btnAddUser; //pulsante per aprire la finestra di aggiunta di un nuovo utente
    
    @FXML
    private Label lblTotalUsers; //label che mostra il numero totale di utenti registrati
    
    @FXML
    private MenuButton FilterButton; //menu a tendina per filtrare gli utenti (tutti/attivi/bloccati)
    
    @FXML
    private TextField searchUser; //campo per ricercare un utente per nome, cognome, email o matricola
    
    public static final int MAX_USERS=Configurazione.getMaxUsers(); //costante limite max utenti consentito
    
    @FXML
     /**
     * @brief Inizializza il controller.
     * Carica la lista completa degli utenti, inizializza i filtri, i bottoni e la barra di ricerca.
     */
    public void initialize(){ //eseguito quando la schermata viene caricata
        updateUtentiList(DataBase.getUtenti());
        buttonInitialize();
        labelInitialize();
        menuButtonInitialize();
        searchUser.setOnKeyPressed(eh->{
            if(eh.getCode()==KeyCode.ENTER)
                searchFunction();
        });
        searchUser.textProperty().addListener((a,b,c)->{
            if(searchUser.getText().trim().equals(""))
                updateUtentiList(DataBase.getUtenti());
            
        });
    }
    
      /**
     * @brief Esegue la logica di ricerca utenti.
     * Cerca prima per corrispondenza esatta della matricola. Se non trova nulla,
     * cerca per corrispondenza parziale su nome, cognome o email.
     */
        public void searchFunction(){ //funzione di ricerca utenti
        ArrayList<Utente> utenti = new ArrayList<>(),app= new ArrayList<>();
           String text = searchUser.getText().trim();
           
            //Cerca per matricola 
           Utente u = DataBase.cercaUtente(text);
           if(u!=null){
               utenti.add(u);
               updateUtentiList(utenti);
               return;
           }
           
           //cerco per nome,cognome o email
           utenti = DataBase.getUtenti();
           for(Utente u1 : utenti)
               if(u1.getNome().equals(text) || u1.getCognome().equals(text) || u1.getMail().equals(text))
                   app.add(u1);
           updateUtentiList(app);    
    }
    
    //inizializzazione MenuBotton filtri
      /**
     * @brief Configura il MenuButton per i filtri.
     * * Opzioni disponibili:
     * - "Tutti gli utenti": Mostra l'elenco completo.
     * - "Solo attivi": Mostra solo gli utenti non in blacklist.
     * - "Solo bloccati": Mostra solo gli utenti in blacklist.
     */
    public void menuButtonInitialize(){
        FilterButton.getItems().clear(); 
        MenuItem m1 = new MenuItem("Tutti gli utenti");
        MenuItem m2 = new MenuItem("Solo attivi");
        MenuItem m3 = new MenuItem("Solo bloccati");
        FilterButton.getItems().addAll(m1,m2,m3);
        
        m1.setOnAction(eh->{ //azione del filtro "Tutti"
            updateUtentiList(DataBase.getUtenti());
            FilterButton.setText(m1.getText());
        });
        
        
        m2.setOnAction(eh->{ //azione del filtro "Solo attivi"
            ArrayList<Utente> us = DataBase.getUtenti();
            us.removeIf(u -> u.isBloccato());
            FilterButton.setText(m2.getText());
            updateUtentiList(us);
        
        });
        
        m3.setOnAction(eh->{//azione del filtro "Solo bloccati"
            ArrayList<Utente> us = DataBase.getUtenti();
            us.removeIf(u -> !u.isBloccato());
            FilterButton.setText(m3.getText());
            updateUtentiList(us);
        
        });    
    }
    
     /**
     * @brief Aggiorna la label con il numero totale di iscritti.
     */
    public void labelInitialize(){ //inizializzazione label totale utenti
        lblTotalUsers.setText( DataBase.getNumUser() + " iscritti totali");
    }
    
     /**
     * @brief Configura il bottone "Aggiungi Utente".
     * Verifica il limite massimo di utenti (`MAX_USERS`) prima di aprire il form `addUtente.fxml`.
     */
    public void buttonInitialize(){ //inizializzazione pulsante Aggiungi Utente
        btnAddUser.setOnAction(eh->{
            if(DataBase.getNumUser()>=MAX_USERS){
                 Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Impossibile aggiungere un nuovo utente");
                IsbnAlert.setContentText("Troppi utenti registrati al sistema");
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");
                IsbnAlert.showAndWait();
                return;
            }else{
                //apro finestra aggiunta utente
            Stage stage = new Stage();
            stage.setTitle("Aggiungi Utente");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/AggiungiUtente.fxml")))); 
            } catch (IOException ex) {
                Logger.getLogger(CatalogoController.class.getName()).log(Level.SEVERE, null, ex);
            }

            stage.showAndWait();
            updateUtentiList(DataBase.getUtenti());
            labelInitialize();
                }
            });
        }
    
      /**
     * @brief Aggiorna la lista degli utenti.
     * Svuota il contenitore e rigenera le card per ogni utente nella lista fornita.
     * * @param utenti Lista degli oggetti User da visualizzare.
     */
    public void updateUtentiList(ArrayList<Utente> utenti){ //aggiornamento lista utenti nella UI
        usersContainer.getChildren().clear();
        for(Utente u : utenti){
            aggiungiCardUtente(u.getNome(),u.getCognome(),u.getMatricola(),u.getMail(),u.isBloccato());
        }
    }
    
     /**
     * @brief Crea e aggiunge la card di un singolo utente.
     * * Costruisce un HBox contenente:
     * - Icona colorata (Blu = Attivo, Rossa = Bloccato).
     * - Dati anagrafici (Nome, Cognome, Email).
     * - Matricola.
     * - Label di stato.
     * - Pulsanti di azione: Modifica, Elimina, Blocca/Sblocca.
     * * @param nome Nome dell'utente.
     * @param cognome Cognome dell'utente.
     * @param matricola Matricola.
     * @param email Indirizzo email.
     * @param isBlacklisted Stato dell'utente (true se bloccato).
     */
    private void aggiungiCardUtente(String nome, String cognome, String matricola, String email, boolean isBlacklisted) {
        //creazione card utente
        
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
        iconContainer.getStyleClass().add(isBlacklisted ? "icon-container-red" : "icon-container-blue");

        Label iconLabel = new Label(isBlacklisted ? "🚫" : "👤");
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

        // 7. BOTTONI AZIONE
        // Bottone Modifica (Matita)
        Button btnEdit = new Button("✏️");
        btnEdit.getStyleClass().add("icon-button");
        Tooltip tt1= new Tooltip("Modifica");

        btnEdit.setTooltip(tt1);
        btnEdit.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Modifica Utente");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            ModificaUtenteController.matricola = matricola;

            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/ModificaUtente.fxml"))));

            } catch (IOException ex) {
                Logger.getLogger(CatalogoController.class.getName()).log(Level.SEVERE, null, ex);
            }

            stage.showAndWait();
            updateUtentiList(DataBase.getUtenti());            
        });
        
        Button btnDelete = new Button("🗑");
        btnDelete.getStyleClass().add("tag-danger");
        Tooltip tt= new Tooltip("Elimina");

        btnDelete.setTooltip(new Tooltip("Elimina"));
        btnDelete.setTooltip(tt);
        btnDelete.setOnAction(e -> { 
            DataBase.rimuoviUtente(matricola);
            
            Alert IsbnAlert = new Alert(Alert.AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Operazione eseguita");
                IsbnAlert.setContentText("Utente rimosso");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                dialogPane.getStyleClass().add("my-alert");       
                IsbnAlert.showAndWait(); 
            updateUtentiList(DataBase.getUtenti());           
        });


        // Bottone Blocca/Sblocca
        Button btnAction = new Button(isBlacklisted ? "SBLOCCA" : "BLOCCA");
        btnAction.getStyleClass().add(isBlacklisted ? "button-outline-success" : "button-outline-danger");
        btnAction.setOnAction(e -> {
            if(!isBlacklisted)
            model.servizi.DataBase.setBlackListed(matricola);
            else
                model.servizi.DataBase.unsetBlackListed(matricola);
            updateUtentiList(DataBase.getUtenti());
            
        });

        // 8. ASSEMBLAGGIO FINALE
        riga.getChildren().addAll(
            iconContainer, 
            boxNomi, 
            boxMatricola, 
            spacer, 
            lblStato, 
            btnEdit,
            btnDelete,
            btnAction
        );
        usersContainer.getChildren().add(riga);// Aggiungo la riga al contenitore verticale dell'interfaccia
    }
}
