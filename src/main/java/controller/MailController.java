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
 *
 * @author gruppo22
 */

//controller gestisce la grafica delle email inviate
public class MailController {
    @FXML
    private VBox emailContainer; //contenitore verticale dove verranno inserite le card delle email
    
    @FXML private Label lblTotalUsers; //mostra il numero totale di email inviate

    //appena la schermata si apre, parte il caricamento delle email inviate
    @FXML
    public void initialize() {
        caricaEmailInviate();
    }
    
    //inizia il metodo che recupera e mostra la lista email
    private void caricaEmailInviate() {
        // 1. Mostra un caricamento mentre scarica le mail
        emailContainer.getChildren().clear(); //pulisce il contenitore
        ProgressIndicator loading = new ProgressIndicator();
        emailContainer.getChildren().add(loading); //mostra la rotellina di caricamento
        lblTotalUsers.setText("Sincronizzazione in corso..."); //aggiorna la label per dire che sta scaricando le email

        // 2. Avvia un thread separato (Per non bloccare l'app)
        new Thread(() -> {
            
            // Scarica le mail (operazione lenta)
            List<EmailInfo> listaEmail = EmailLegge.leggiPostaInviata();

            // 3. Torna al thread grafico per mostrare i risultati
            Platform.runLater(() -> {
                emailContainer.getChildren().clear(); // Rimuovi caricamento loading
                
                //se non ci sono email
                if (listaEmail == null || listaEmail.isEmpty()) {
                    lblTotalUsers.setText("Nessuna email inviata trovata.");
                    return;
                }
                
                //mostra il numero totale
                lblTotalUsers.setText(listaEmail.size() + " Email Inviate");

                // Aggiunge una card per ogni email
                for (EmailInfo mail : listaEmail) {
                    aggiungiCardEmail(mail);
                }
            });
            
        }).start();
    }

    //metodo che crea una riga grafica (una mail)
    private void aggiungiCardEmail(EmailInfo mail) {
        // Riga card
        HBox riga = new HBox(); //elemen disposti in orizzontale
        riga.setAlignment(Pos.CENTER_LEFT); //allineati a sinistra
        riga.setSpacing(20); //spaziatura orizzontale tra gli elementi
        riga.setPrefHeight(80); //altezza della card
        riga.setPadding(new Insets(0, 20, 0, 20)); //margini interni
        riga.getStyleClass().add("email-row"); 

        //Icona (busta)
        StackPane iconContainer = new StackPane(); // Usa StackPane per centrare automaticamente l'icona

        // Blocca le dimensioni per avere un cerchio perfetto (non ovale)
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

        // StackPane centra automaticamente il figlio.

        //aggiunta dell'icona al cerchio
        iconContainer.getChildren().add(iconLabel);

        //Box informazioni email
        VBox boxInfo = new VBox();
        boxInfo.setAlignment(Pos.CENTER_LEFT);
        boxInfo.setPrefWidth(400); // Spazio abbondante
        
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

        //Aggiunta degli elementi all'HBox finale
        riga.getChildren().addAll(iconContainer, boxInfo, boxData, spacer);
        
        //aggiunta al cointainer principale
        emailContainer.getChildren().add(riga);
    }
}
