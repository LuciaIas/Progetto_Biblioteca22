/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.dataclass.EmailInfo;
import model.servizi.EmailLegge;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


/**
 * @brief Controller per la visualizzazione della posta inviata.
 *
 * Questa classe gestisce l'interfaccia che mostra lo storico delle email inviate dal sistema.
 * Utilizza un caricamento asincrono (Multithreading) per evitare di bloccare l'interfaccia
 * durante la lettura dei file di log o del database.
 *
 * @author gruppo22
 */
public class MailController {
    @FXML
    private VBox emailContainer; //contenitore verticale dove verranno inserite le card delle email
    
    @FXML private Label lblTotalUsers; //mostra il numero totale di email inviate
    
/**
 * @brief Metodo di inizializzazione del controller.
 *
 * Viene chiamato automaticamente al caricamento della view.
 * Avvia immediatamente la procedura di caricamento delle email inviate.
 */
    @FXML
    public void initialize() { 
        caricaEmailInviate();
    }
    
/**
 * @brief Recupera le email inviate e aggiorna l'interfaccia grafica.
 *
 * Esegue le seguenti operazioni:
 * 1. Mostra un `ProgressIndicator` nel contenitore.
 * 2. Avvia un thread separato per leggere i dati (operazione I/O pesante).
 * 3. Una volta ottenuti i dati, utilizza `Platform.runLater` per aggiornare la UI
 *    sul thread grafico principale, popolando la lista o mostrando un messaggio se vuota.
 */
    private void caricaEmailInviate() {
        // 1. Mostro un caricamento mentre scarica le mail
        emailContainer.getChildren().clear(); //pulisco il contenitore
        ProgressIndicator loading = new ProgressIndicator();
        emailContainer.getChildren().add(loading); //mostro la rotellina di caricamento
        lblTotalUsers.setText("Sincronizzazione in corso..."); 

        // 2. Avvio un thread separato (Per non bloccare l'app)
        new Thread(() -> {            
            // Scarica le mail 
            List<EmailInfo> listaEmail = EmailLegge.leggiPostaInviata();

            // 3. Torno al thread grafico per mostrare i risultati
            Platform.runLater(() -> {
                emailContainer.getChildren().clear(); // Rimuovo caricamento loading
                
                //se non ci sono email
                if (listaEmail == null || listaEmail.isEmpty()) {
                    lblTotalUsers.setText("Nessuna email inviata trovata.");
                    return;
                }
                
                //mostro il numero totale
                lblTotalUsers.setText(listaEmail.size() + " Email Inviate");

                // Aggiungo una card per ogni email
                for (EmailInfo mail : listaEmail) {
                    aggiungiCardEmail(mail);
                }
            });
            
        }).start();
    }

/**
 * @brief Crea e aggiunge la card di una singola email alla lista.
 *
 * Costruisce programmaticamente un HBox contenente:
 * - Icona (StackPane con cerchio e emoji)
 * - Oggetto e Destinatario dell'email
 * - Data di invio formattata
 *
 * @param mail Oggetto `EmailInfo` contenente i dati dell'email da visualizzare.
 */
    private void aggiungiCardEmail(EmailInfo mail) {
        // Riga card
        HBox riga = new HBox(); 
        riga.setAlignment(Pos.CENTER_LEFT); 
        riga.setSpacing(20); 
        riga.setPrefHeight(80); 
        riga.setPadding(new Insets(0, 20, 0, 20)); 
        riga.getStyleClass().add("email-row"); 

        //Icona (busta)
        StackPane iconContainer = new StackPane(); // Uso StackPane per centrare automaticamente l'icona

        // Blocco le dimensioni per avere un cerchio perfetto (non ovale)
        double size = 45;
        iconContainer.setMinWidth(size);
        iconContainer.setMinHeight(size);
        iconContainer.setPrefSize(size, size);
        iconContainer.setMaxSize(size, size);

        // Stile 
        iconContainer.getStyleClass().add("icon-container-purple");

        //Icona della mail inviata
        Label iconLabel = new Label("📤");
        iconLabel.setStyle("-fx-font-size: 20px;"); // Aumentato leggermente per riempire meglio

        //aggiunta dell'icona al cerchio
        iconContainer.getChildren().add(iconLabel);

        //Box informazioni email
        VBox boxInfo = new VBox();
        boxInfo.setAlignment(Pos.CENTER_LEFT);
        boxInfo.setPrefWidth(400); 
        
        //Oggetto (Titolo) dell'email
        String oggetto = (mail.getOggetto() != null) ? mail.getOggetto() : "(Nessun Oggetto)";
        Label lblOggetto = new Label(oggetto);
        lblOggetto.getStyleClass().add("row-title");
        
        // Destinatario
        Label lblDestinatario = new Label("A: " + mail.getDestinatario());
        lblDestinatario.getStyleClass().add("row-subtitle");
        
        boxInfo.getChildren().addAll(lblOggetto, lblDestinatario);

        //Box della data
        VBox boxData = new VBox();
        boxData.setAlignment(Pos.CENTER_LEFT);
        boxData.setPrefWidth(150);
        
        //formattazione data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dataStr = (mail.getDataInvio() != null) ? sdf.format(mail.getDataInvio()) : "--/--/----";
        
        //label "inviata il"
        Label lblDataTitle = new Label("INVIATA IL");
        lblDataTitle.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 9px; -fx-font-weight: bold;");
        
        //valore della data
        Label lblDataValue = new Label(dataStr);
        lblDataValue.setStyle("-fx-text-fill: #2d3436; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        boxData.getChildren().addAll(lblDataTitle, lblDataValue);

        //Spaziatore
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        riga.getChildren().addAll(iconContainer, boxInfo, boxData, spacer);
        
        emailContainer.getChildren().add(riga);
    }
}
