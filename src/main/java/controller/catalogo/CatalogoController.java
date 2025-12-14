/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.catalogo;

import controller.DashboardController;
import model.servizi.Catalogo;
import model.servizi.DataBase;
import model.dataclass.Libro;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Configurazione;
 

/**
 * @brief Controller per la gestione del Catalogo Libri.
 *
 * Gestisce:
 * - Visualizzazione a griglia dei libri.
 * - Ricerca libri per ISBN o titolo.
 * - Animazioni al passaggio del mouse sulle copertine.
 * - Aggiunta, modifica e cancellazione dei libri.
 * 
 * @author gruppo22
 */
public class CatalogoController {
    @FXML
    private ScrollPane LibriScrollPane;
    
    @FXML
    private GridPane containerLibri;
    
    @FXML
    private Button btnCerca;
    
    @FXML
    private TextField searchBar;
    
    @FXML
    private Button addButton;
    
    public static final int MAX_BOOKS = Configurazione.getMaxBooks();  // Numero massimo di libri consentiti nel sistema
    
    
     /**
     * @brief Inizializza il controller.
     *
     * Carica il catalogo completo all'avvio e configura listener per:
     * - Ricerca al click sul bottone.
     * - Ricerca premendo ENTER.
     * - Reset automatico se la barra di ricerca viene svuotata.
     * - Bottone per aggiungere un nuovo libro.
     */
    @FXML
    public void initialize(){  
       updateCatalogo(DataBase.getCatalogo());// Visualizza tutti i libri presenti nel database all'avvio
       btnCerca.setOnAction(eh->{ // Bottone cerca: avvia la funzione di ricerca          
            searchFunction();           
       });
         searchBar.textProperty().addListener((a,b,c)->{ // Aggiornamento catalogo se il campo di ricerca viene svuotato        
             if(searchBar.getText().trim().equals(""))
                updateCatalogo(DataBase.getCatalogo());
         });
        searchBar.setOnKeyPressed(eh->{  // Cerca anche premendo ENTER nella searchBar       
            if(eh.getCode()==KeyCode.ENTER) 
                searchFunction();
        });
        addButton.setOnAction(eh->{ // Bottone per aggiungere un nuovo libro      
            launchAggiungiLibroForm();            
        });
        
        
    }
    
    /**
     * @brief Esegue la logica di ricerca nel catalogo.
     *
     * Cerca prima per ISBN (corrispondenza esatta).
     * Se non trovato, cerca per titolo (corrispondenza parziale).
     * Aggiorna la griglia dei libri con i risultati trovati.
     */
    public void searchFunction(){   
        Catalogo libri = new Catalogo();
           String text = searchBar.getText().trim();           
           // Cerco prima per ISBN
           Libro l = DataBase.cercaLibro(text);
           if(l!=null){
               libri.aggiungiLibro(l);
               updateCatalogo(libri);
               return;
           }
            // Cerco per titolo
            for(Libro l1 : DataBase.getLibriByTitolo(text))
                libri.aggiungiLibro(l1);            
               updateCatalogo(libri);
               return;       
    }
        
    

    /**
     * @brief Aggiorna la griglia visuale con la lista dei libri fornita.
     *
     * Pulisce la griglia esistente e rigenera le card per ogni libro.
     * Gestisce il layout a colonne (max 4 per riga) e aggiunge in fondo
     * una card speciale "Aggiungi Libro".
     *
     * @param libri Catalogo contenente la lista di libri da mostrare.
     */
    public void updateCatalogo(Catalogo libri){
      containerLibri.getChildren().clear();
            int colonna = 0;
            int riga = 0;      
        for(Libro l : libri.getLibri()){
            containerLibri.add(creaLibroAnimato(l), colonna, riga); // Card speciale per aggiungere un nuovo libro            
            colonna++; 
            if (colonna == 4) {
                colonna = 0; 
                riga++;      
            }
        }        
        containerLibri.add(creaLibroAnimato(new Libro(null,"",null,null,null,0,"/Images/aggiungiLibro.jpg")), colonna, riga);       
    }
        
    
    /**
     * @brief Genera l'elemento grafico (Card) per un singolo libro.
     *
     * Costruisce un VBox contenente:
     * - Immagine della copertina.
     * - Controlli per modificare copie, Modifica/Elimina.
     * - Titolo del libro.
     * - Animazioni al passaggio del mouse.
     *
     * @param libro Libro da visualizzare nella card.
     * @return VBox contenente la card interattiva pronta per essere aggiunta alla griglia.
     */
    private VBox creaLibroAnimato(Libro libro) {
    
    // 1. IMMAGINE (Livello Base)
    ImageView imageView = new ImageView();
    try {
        String imagePath = (libro.getUrl() != null && !libro.getUrl().isEmpty()) ? libro.getUrl() : "/Images/default.jpg";
        Image img;
        try {
            // Logica per caricare immagini da disco o da risorse
            if (imagePath.contains(":") || imagePath.startsWith("file:")) {
                if (!imagePath.startsWith("file:") && !imagePath.startsWith("http")) {
                    imagePath = "file:" + imagePath;
                }
                img = new Image(imagePath);
            } else {
                img = new Image(getClass().getResourceAsStream(imagePath));
            }
        } catch (Exception e) {
            // Fallback se l'immagine non esiste
            img = new Image(getClass().getResourceAsStream("/img/placeholder_book.png"));
        }
        imageView.setImage(img);
    } catch (Exception e) { e.printStackTrace(); }   
    imageView.setFitWidth(200); 
    imageView.setFitHeight(300); 
    imageView.setPreserveRatio(true);
    imageView.getStyleClass().add("book-cover-static");
    // 2. CONTROLLI OVERLAY (Label Copie + Bottoni)    
    // Label Copie
    Label lblCopie = new Label();
    if (libro.getIsbn() != null) {
        lblCopie.setText("Copie: " + DataBase.getNumCopieByIsbn(libro.getIsbn())); 
    }
    lblCopie.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-background-radius: 15; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 12; -fx-font-size: 12px;");    
    // Bottoni
    Button btnMinus = new Button("-");
    Button btnPlus = new Button("+");
    String btnStyle = "-fx-background-color: rgba(255,255,255,0.95); -fx-text-fill: #1A2980; -fx-font-weight: 900; -fx-background-radius: 50; -fx-min-width: 35px; -fx-min-height: 35px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);";
    btnMinus.setStyle(btnStyle);
    btnPlus.setStyle(btnStyle);
    // Logica Bottoni
    if (libro.getIsbn() != null) {
        btnPlus.setOnAction(e -> {
            // Consumo l'evento per non attivare il click sulla card
            e.consume(); 
            DataBase.modificaNum_copie(libro.getIsbn(), true);
            updateCatalogo(DataBase.getCatalogo()); 
        });
        btnMinus.setOnAction(e -> {
            e.consume();
            if( !(DataBase.getNumCopieByIsbn(libro.getIsbn())<=0) ){
            DataBase.modificaNum_copie(libro.getIsbn(), false);
            updateCatalogo(DataBase.getCatalogo());
            }
        });
    }
    HBox buttonsBox = new HBox(15, btnMinus, btnPlus);
    buttonsBox.setAlignment(Pos.CENTER);    
    Button Modifica = new Button("Modifica");
    Modifica.setStyle(btnStyle);
    Modifica.setOnAction(eh->{    
        ModificaLibroController.isbn = libro.getIsbn();       
            Stage s = new Stage();
                s.setTitle("Modifica Libro");
                s.setResizable(false);
                s.initModality(Modality.APPLICATION_MODAL);
            try {
                s.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/ModificaLibro.fxml"))));
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
                s.showAndWait();
                updateCatalogo(DataBase.getCatalogo());
                ModificaLibroController.isbn ="";
    });   
    Button Elimina = new Button("Elimina");
    Elimina.setStyle(btnStyle);
    Elimina.setOnAction(eh->{    
        DataBase.rimuoviLibro(libro.getIsbn());
        Alert IsbnAlert = new Alert(AlertType.INFORMATION);
                IsbnAlert.setHeaderText("Operazione eseguita");
                IsbnAlert.setContentText("Libro rimosso");               
                DialogPane dialogPane = IsbnAlert.getDialogPane();              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );              
                dialogPane.getStyleClass().add("my-alert");               
                IsbnAlert.showAndWait();
                updateCatalogo(DataBase.getCatalogo());        
    });   
    HBox opME = new HBox(5,Modifica,Elimina);
    opME.setAlignment(Pos.CENTER);   
    // Contenitore controlli (centrato sopra l'immagine)
    VBox overlayControls = new VBox(15); 
    overlayControls.setAlignment(Pos.CENTER); 
    overlayControls.getChildren().addAll(lblCopie, buttonsBox,opME);    
    // Nascondo controlli di default
    overlayControls.setVisible(false); 
    if (libro.getIsbn() == null) overlayControls.setVisible(false);
    // 3. STACKPANE (Immagine + Controlli)
    StackPane bookStack = new StackPane();
    bookStack.getChildren().addAll(imageView, overlayControls);    
    // Effetto Ombra sull'immagine
    DropShadow shadow = new DropShadow(10, Color.rgb(0, 0, 0, 0.3));
    imageView.setEffect(shadow);    
    bookStack.setDepthTest(javafx.scene.DepthTest.DISABLE); 
    // 4. TITOLO E CONTENITORE FINALE (VBox)
    Label lblTitolo = new Label(libro.getTitolo() != null ? libro.getTitolo() : "Nuovo Libro");
    lblTitolo.setWrapText(true);
    lblTitolo.setMaxWidth(200);
    lblTitolo.setAlignment(Pos.CENTER);
    lblTitolo.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: 800; -fx-text-fill: #1A2980; -fx-font-size: 14px; -fx-text-alignment: center;");
    VBox mainContainer = new VBox(10);
    mainContainer.setAlignment(Pos.TOP_CENTER);
    mainContainer.getChildren().addAll(bookStack, lblTitolo);    
    // 5. ANIMAZIONI (Applicate al mainContainer per evitare sovrapposizioni)
    Duration speed = Duration.millis(300);
    // Zoom su TUTTA la card (così il titolo non viene coperto)
    ScaleTransition scaleUp = new ScaleTransition(speed, mainContainer);
    scaleUp.setToX(1.08); 
    scaleUp.setToY(1.08);
    // Rotazione solo sul libro (effetto 3D copertina)
    RotateTransition rotateOpen = new RotateTransition(speed, bookStack);
    rotateOpen.setAxis(Rotate.Y_AXIS); 
    rotateOpen.setToAngle(-10);
    ParallelTransition openAnim = new ParallelTransition(scaleUp, rotateOpen);
    // Chiusura
    ScaleTransition scaleDown = new ScaleTransition(speed, mainContainer);
    scaleDown.setToX(1.0); 
    scaleDown.setToY(1.0);   
    RotateTransition rotateClose = new RotateTransition(speed, bookStack);
    rotateClose.setAxis(Rotate.Y_AXIS); 
    rotateClose.setToAngle(0);
    ParallelTransition closeAnim = new ParallelTransition(scaleDown, rotateClose);
    // EVENTI MOUSE
    mainContainer.setOnMouseEntered(e -> {
        closeAnim.stop();
        openAnim.play();       
        // Effetti Ombra
        shadow.setRadius(25); shadow.setOffsetY(10);       
        // Mostra controlli
        if (libro.getIsbn() != null) overlayControls.setVisible(true);        
        mainContainer.toFront(); 
    });
    mainContainer.setOnMouseExited(e -> {
        openAnim.stop();
        closeAnim.play();        
        shadow.setRadius(10); shadow.setOffsetY(0);
        if (libro.getIsbn() != null) overlayControls.setVisible(false);
    });    
    // 6. GESTIONE CLICK (Aggiungi Libro)
    if (libro.getIsbn() == null) {
        mainContainer.setCursor(Cursor.HAND);
        mainContainer.setOnMouseClicked(eh -> {
            launchAggiungiLibroForm();
        });
    }
    return mainContainer;
}

    
    /**
     * @brief Apre il form per l'aggiunta di un nuovo libro.
     *
     * Controlla prima se è stato raggiunto il limite massimo di libri (MAX_BOOKS).
     * Se non superato, apre la finestra modale `aggiungiLibro.fxml`.
     * Al termine dell'inserimento, aggiorna il catalogo.
     */
    public void launchAggiungiLibroForm(){    
        if(DataBase.getCatalogo().getLibri().size()>=MAX_BOOKS){            
                 Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Impossibile aggiungere libro");
                IsbnAlert.setContentText("Ci sono troppi Libri nel sistema");               
                DialogPane dialogPane = IsbnAlert.getDialogPane();              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );               
                dialogPane.getStyleClass().add("my-alert");                
                IsbnAlert.showAndWait();
                return;            
            }else
            try {
                Stage stage = new Stage();
                stage.setTitle("Aggiungi Libro");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/AggiungiLibro.fxml"))));
                stage.showAndWait();
                updateCatalogo(DataBase.getCatalogo());
            } catch (IOException ex) { ex.printStackTrace(); }       
    }
    
}
    

