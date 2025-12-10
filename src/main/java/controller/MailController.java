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
 * @author nicol
 */
public class mailController {
    
    
    @FXML
    private VBox emailContainer;
    

    @FXML private Label lblTotalUsers; // In realtà qui mostreremo il totale email

    @FXML
    public void initialize() {
        caricaEmailInviate();
   
    }

    private void caricaEmailInviate() {
        // 1. Mostra un caricamento mentre scarica le mail
        emailContainer.getChildren().clear();
        ProgressIndicator loading = new ProgressIndicator();
        emailContainer.getChildren().add(loading);
        lblTotalUsers.setText("Sincronizzazione in corso...");

        // 2. AVVIA THREAD SEPARATO (Per non bloccare l'app)
        new Thread(() -> {
            
            // Scarica le mail (operazione lenta)
            List<EmailInfo> listaEmail = EmailReader.leggiPostaInviata();

            // 3. TORNA AL THREAD GRAFICO per mostrare i risultati
            Platform.runLater(() -> {
                emailContainer.getChildren().clear(); // Rimuovi caricamento
                
                if (listaEmail == null || listaEmail.isEmpty()) {
                    lblTotalUsers.setText("Nessuna email inviata trovata.");
                    return;
                }

                lblTotalUsers.setText(listaEmail.size() + " Email Inviate");

                // Aggiungi le card
                for (EmailInfo mail : listaEmail) {
                    aggiungiCardEmail(mail);
                }
            });
            
        }).start();
    }

    private void aggiungiCardEmail(EmailInfo mail) {
        
        // --- 1. RIGA CARD ---
        HBox riga = new HBox();
        riga.setAlignment(Pos.CENTER_LEFT);
        riga.setSpacing(20);
        riga.setPrefHeight(80);
        riga.setPadding(new Insets(0, 20, 0, 20));
        riga.getStyleClass().add("email-row"); // Classe CSS specifica

        // --- 2. ICONA (Busta) ---
        StackPane iconContainer = new StackPane(); // Usa StackPane per centrare

        // Blocca le dimensioni per avere un cerchio perfetto (non ovale)
        double size = 45;
        iconContainer.setMinWidth(size);
        iconContainer.setMinHeight(size);
        iconContainer.setPrefSize(size, size);
        iconContainer.setMaxSize(size, size);

        // Stile (assumendo che icon-container-purple abbia il radius 50%)
        iconContainer.getStyleClass().add("icon-container-purple");

        // Etichetta
        Label iconLabel = new Label("📤");
        iconLabel.setStyle("-fx-font-size: 20px;"); // Aumentato leggermente per riempire meglio

        // NOTA: Rimossi setLayoutX e setLayoutY.
        // StackPane centra automaticamente il figlio.

        iconContainer.getChildren().add(iconLabel);

        // --- 3. OGGETTO E DESTINATARIO ---
        VBox boxInfo = new VBox();
        boxInfo.setAlignment(Pos.CENTER_LEFT);
        boxInfo.setPrefWidth(400); // Spazio abbondante
        
        // Oggetto (Titolo)
        String oggetto = (mail.getOggetto() != null) ? mail.getOggetto() : "(Nessun Oggetto)";
        Label lblOggetto = new Label(oggetto);
        lblOggetto.getStyleClass().add("row-title");
        
        // Destinatario
        Label lblDestinatario = new Label("A: " + mail.getDestinatario());
        lblDestinatario.getStyleClass().add("row-subtitle");
        
        boxInfo.getChildren().addAll(lblOggetto, lblDestinatario);

        // --- 4. DATA ---
        VBox boxData = new VBox();
        boxData.setAlignment(Pos.CENTER_LEFT);
        boxData.setPrefWidth(150);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dataStr = (mail.getDataInvio() != null) ? sdf.format(mail.getDataInvio()) : "--/--/----";
        
        Label lblDataTitle = new Label("INVIATA IL");
        lblDataTitle.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 9px; -fx-font-weight: bold;");
        Label lblDataValue = new Label(dataStr);
        lblDataValue.setStyle("-fx-text-fill: #2d3436; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        boxData.getChildren().addAll(lblDataTitle, lblDataValue);

        // --- 5. SPAZIATORE ---
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- 6. BOTTONE "LEGGI" ---


        // --- 7. ASSEMBLAGGIO ---
        riga.getChildren().addAll(iconContainer, boxInfo, boxData, spacer);
        
        emailContainer.getChildren().add(riga);
    }
}
