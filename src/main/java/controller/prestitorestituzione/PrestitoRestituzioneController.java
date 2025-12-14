/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.prestitorestituzione;

import controller.catalogo.CatalogoController;
import model.servizi.DataBase;
import model.dataclass.Libro;
import model.dataclass.Stato;
import static model.dataclass.Stato.ATTIVO;
import static model.dataclass.Stato.IN_RITARDO;
import static model.dataclass.Stato.PROROGATO;
import static model.dataclass.Stato.RESTITUITO;
import model.dataclass.Utente;
import model.servizi.EmailInvia;
import model.servizi.Prestito;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
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
 * @brief Controller per la gestione dei Prestiti e delle Restituzioni.
 *
 * Gestisce la visualizzazione dello storico prestiti e le azioni su ciascun prestito.
 * Funzionalità principali:
 * - Filtraggio prestiti per stato (In corso, In ritardo, Storico/Restituiti)
 * - Ricerca prestiti per ISBN, titolo libro, matricola, nome o cognome utente
 * - Creazione di nuovi prestiti con gestione del limite massimo
 * - Azioni sui prestiti esistenti: Proroga, Restituzione, Invio Email di sollecito
 * 
 * @author gruppo22
 */
public class PrestitoRestituzioneController {   
    @FXML
    private VBox loansContainer;
    
    @FXML
    private Button NewLoanButton;
    
    @FXML
    private MenuButton FilterButton;
    
    @FXML
    private Label lblActiveLoans;
    
    @FXML
    private TextField searchLoan;
    
    public static final int MAX_LOAN = Configurazione.getMaxLoans();
    
    /**
     * @brief Inizializza il controller.
     *
     * Calcola il numero di prestiti attivi, carica la lista iniziale e configura pulsanti e filtri.
     */
    @FXML
    public void initialize(){
      int n=  Prestito.getPrestitiByStato(DataBase.getPrestiti(), ATTIVO).size()+Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.IN_RITARDO).size()
              +Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.PROROGATO).size();
      //n rappresenta il totale dei prestiti “non ancora restituiti” (attivi, in ritardo o prorogati)
    lblActiveLoans.setText("Prestiti attivi: "+n);
    updatePrestiti(DataBase.getPrestiti()); 
    buttonInitialize();
    menuButtonInitialize();
    }
    
    
    /**
     * @brief Configura il MenuButton e la barra di ricerca.
     *
     * Filtri disponibili:
     * - Tutti i prestiti
     * - Solo in corso
     * - Solo in ritardo
     * - Restituiti (storico)
     */
    public void menuButtonInitialize(){   
        FilterButton.getItems().clear();// Pulizia menu
        MenuItem m1 = new MenuItem("Tutti i prestiti");// Filtri menu
        MenuItem m2 = new MenuItem("Solo in corso");
        MenuItem m3 = new MenuItem("Solo in ritardo");
        MenuItem m4 = new MenuItem("Restituiti(Storico)");
        FilterButton.getItems().addAll(m1,m2,m3,m4);// Aggiungo filtri al menu
        
        m1.setOnAction(eh->{// Mostra tutti i prestiti
            FilterButton.setText("Tutti i prestiti");
            updatePrestiti(DataBase.getPrestiti());           
        });
        
        m2.setOnAction(eh->{// Mostra prestiti in corso
            FilterButton.setText("Solo in corso");
            ArrayList<Prestito> p = new ArrayList<>();
            
            p.addAll(Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.ATTIVO));
            p.addAll(Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.IN_RITARDO));
            p.addAll(Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.PROROGATO));
            
            updatePrestiti(p);            
        });
        
        m3.setOnAction(eh->{ // Mostra prestiti in ritardo
  FilterButton.setText("Solo in ritardo");
            updatePrestiti(Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.IN_RITARDO));            
        });
        
        m4.setOnAction(eh->{// Mostra prestiti restituiti (storico)
        FilterButton.setText("Restituiti(Storico)");
            updatePrestiti(Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.RESTITUITO));
        });
        
        searchLoan.textProperty().addListener((a,b,c)->{// Se campo ricerca vuoto, mostra tutti         
             if(searchLoan.getText().trim().equals(""))
                updatePrestiti(DataBase.getPrestiti());
         });
        
        searchLoan.setOnKeyPressed(eh->{ // Premendo Enter, esegue ricerca        
            if(eh.getCode()==KeyCode.ENTER) 
                searchFunction();
        });    
    }
    
    
    /**
     * @brief Esegue la logica di ricerca sui prestiti.
     *
     * Filtra prestiti per ISBN, titolo libro, matricola, nome o cognome utente.
     */
    public void searchFunction(){
    
        ArrayList<Prestito> prestiti = DataBase.getPrestiti(),app = new ArrayList<>();
        String text = searchLoan.getText();
        
        for(Prestito p : prestiti){
            String isbn = p.getIsbn(),matricola = p.getMatricola();
            Libro l = DataBase.cercaLibro(isbn);
            Utente u = DataBase.cercaUtente(matricola);
        
            // Confronto input con ISBN, titolo, matricola, nome o cognome
            if(isbn.equals(text.trim()) || text.trim().equals(l.getTitolo()) || text.trim().equals(matricola) || text.trim().equals(u.getNome()) 
                    || text.trim().equals(u.getCognome()) )
                app.add(p);         
        }           
        updatePrestiti(app);          
    }
    
    
    /**
     * @brief Configura il pulsante "Nuovo Prestito".
     *
     * Gestisce il limite massimo di record in memoria (MAX_LOAN).
     * Apre il form per aggiungere un nuovo prestito se possibile.
     */
    public void buttonInitialize(){  
        NewLoanButton.setOnAction(eh->{        
            int totale_prestiti = DataBase.getPrestiti().size();
            
            if(totale_prestiti>=MAX_LOAN){// Controllo limite massimo prestiti            
            int i = 0;
            ArrayList<Prestito> pre = DataBase.getPrestiti();
            for(Prestito p : pre)
                if(p.getStato()==Stato.RESTITUITO)
                    i+=1;// Conto prestiti già restituiti
                
            if(totale_prestiti-i<MAX_LOAN){// Se necessario, rimuovo prestiti vecchi                
                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setHeaderText("Azione necessaria");
                conf.setContentText("Per aggiungere prestiti e necessario svuotare i prestiti memorizzati in memoria, vuoi che rimuovo gli ultimi prestiti che risultano gia restituiti partendo dal piu vecchio?");
                Optional<ButtonType> confirm = conf.showAndWait();
                if(confirm.isPresent() && confirm.get() == ButtonType.OK){
                pre.sort(new Comparator<Prestito>() {// Ordino per data restituzione
                    @Override
                    public int compare(Prestito o1, Prestito o2) {
                        LocalDate d1 = o1.getRestituzione();
                        LocalDate d2 = o2.getRestituzione();
                        
                        if (d1 == null && d2 == null) return 0;
                        if (d1 == null) return 1;
                        if (d2 == null) return -1;
                        return d1.compareTo(d2);
                    }
                });            
                for(int f=0;f<totale_prestiti-(MAX_LOAN-1);f++){
                        Prestito p = pre.get(f);
                        DataBase.rimuoviPrestito(p.getIsbn(), p.getMatricola());// Rimuovo prestiti vecchi
                }
                }               
            }else{// Se non è possibile aggiungere           
             Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Impossibile concedere un altro prestito");
                IsbnAlert.setContentText("Ci sono troppe copie dei nostri libri prestate");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                
                return;    
            }     
            }
        // Apro finestra AggiungiPrestito   
        Stage stage = new Stage();
        stage.setTitle("Concedi Prestito");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        try {// Se si verifica un errore di I/O durante il caricamento del FXML
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/View/AggiungiPrestito.fxml"))));            
        } catch (IOException ex) {
            Logger.getLogger(CatalogoController.class.getName()).log(Level.SEVERE, null, ex);// Scrivo l'errore nei log come messaggio grave
        }
        stage.showAndWait();
        // Aggiorno contatore prestiti attivi e lista
        int n=  Prestito.getPrestitiByStato(DataBase.getPrestiti(), ATTIVO).size()+Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.IN_RITARDO).size()
                +Prestito.getPrestitiByStato(DataBase.getPrestiti(), Stato.PROROGATO).size();
        // n contiene il numero totale di prestiti “attivi” o non ancora restituiti
        lblActiveLoans.setText("Prestiti attivi: "+n);
        updatePrestiti(DataBase.getPrestiti());
        });
    
    }
    
    /**
     * @brief Aggiorna la lista visuale dei prestiti.
     *
     * @param p1 Lista dei prestiti da visualizzare.
     */
    public void updatePrestiti(ArrayList<Prestito> p1){
    loansContainer.getChildren().clear(); // Pulizia container
        for(Prestito p : p1){       
            Libro l = DataBase.cercaLibro(p.getIsbn());
            Utente u = DataBase.cercaUtente(p.getMatricola());           
            aggiungiRigaPrestito(l.getTitolo(),l.getIsbn(),u.getNome(),u.getMatricola(),p.getData_scadenza().toString(),p.getStato());
        }  
    }
    
    
    
    /**
     * @brief Crea una card per un singolo prestito con tutte le azioni disponibili.
     *
     * @param titoloLibro Titolo del libro.
     * @param isbn ISBN del libro.
     * @param nomeUtente Nome utente.
     * @param matricola Matricola utente.
     * @param dataScadenza Data scadenza.
     * @param statoEnum Stato del prestito.
     */
private void aggiungiRigaPrestito(String titoloLibro, String isbn, String nomeUtente, String matricola, String dataScadenza, Stato statoEnum) {    
    Utente u = DataBase.cercaUtente(matricola);
    Prestito prest = null;
    for(Prestito p : DataBase.getPrestiti())
        if(p.getIsbn().equals(isbn) && p.getMatricola().equals(matricola))
            prest = p;// Trovo prestito corrispondente   
    HBox riga = new HBox();
    riga.setAlignment(Pos.CENTER_LEFT);
    riga.setSpacing(20);
    riga.setPrefHeight(90);
    riga.setPadding(new Insets(0, 20, 0, 20));
    
    // Stili e icone in base allo stato del prestito 
    String rowStyle = "";
    String iconStyle = "";
    String tagStyle = "";
    String iconText = "";
    String statoText = "";
      
    switch (statoEnum) {
        case IN_RITARDO:
            rowStyle = "loan-row-late";
            iconStyle = "icon-container-red";
            tagStyle = "tag-danger";
            iconText = "⚠️";
            statoText = "In Ritardo";
            break;
            
        case PROROGATO:
            rowStyle = "loan-row-extended";
            iconStyle = "icon-container-purple";
            tagStyle = "tag-extended";
            iconText = "⏳"; 
            statoText = "Prorogato";
            break;
            
        case RESTITUITO:
            rowStyle = "loan-row-returned";
            iconStyle = "icon-container-green";
            tagStyle = "tag-returned";
            iconText = "✅";
            statoText = "Restituito";
            break;
            
        case ATTIVO:
        default:
            rowStyle = "loan-row-active";
            iconStyle = "icon-container-blue";
            tagStyle = "tag-active";
            iconText = "📖";
            statoText = "In Corso";
            break;
    }   
    riga.getStyleClass().add(rowStyle);
    
    // Icona stato prestito
    StackPane iconContainer = new StackPane();
    double size = 45;
    iconContainer.setMinWidth(size);
    iconContainer.setMinHeight(size);
    iconContainer.setPrefSize(size, size);
    iconContainer.setMaxSize(size, size);
    iconContainer.getStyleClass().add(iconStyle);
    Label iconLabel = new Label(iconText);
    iconLabel.setStyle("-fx-font-size: 20px;");
    iconContainer.getChildren().add(iconLabel);

      
    // Box libro
    VBox boxLibro = new VBox();
    boxLibro.setAlignment(Pos.CENTER_LEFT);
    boxLibro.setPrefWidth(220);
    Label lblTitolo = new Label(titoloLibro);
    lblTitolo.getStyleClass().add("row-title");
    Label lblIsbn = new Label("ISBN: " + isbn);
    lblIsbn.getStyleClass().add("row-subtitle");
    boxLibro.getChildren().addAll(lblTitolo, lblIsbn);

    // Box utente
    VBox boxUtente = new VBox();
    boxUtente.setAlignment(Pos.CENTER_LEFT);
    boxUtente.setPrefWidth(180);
    Label lblPrestator = new Label("PRESTATO A");
    lblPrestator.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 10px; -fx-font-weight: bold;");
    Label lblNomeUtente = new Label(nomeUtente + " (" + matricola + ")");
    lblNomeUtente.setStyle("-fx-text-fill: #2d3436; -fx-font-weight: bold;");
    boxUtente.getChildren().addAll(lblPrestator, lblNomeUtente);

    // Box data scadenza
    VBox boxData = new VBox();
    boxData.setAlignment(Pos.CENTER_LEFT);
    boxData.setPrefWidth(150);
    Label lblScadenzaTitle = new Label(statoEnum == Stato.RESTITUITO ? "SCADE IL" : "SCADENZA");
    lblScadenzaTitle.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 10px; -fx-font-weight: bold;");
    Label lblData = new Label(dataScadenza);
    
    lblData.setStyle(statoEnum == Stato.IN_RITARDO ? "-fx-text-fill: #c0392b; -fx-font-weight: 900;" : "-fx-text-fill: #2d3436; -fx-font-weight: bold;");
    boxData.getChildren().addAll(lblScadenzaTitle, lblData);
  
    Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);
   
    Label lblStato = new Label(statoText);
    lblStato.getStyleClass().add(tagStyle);
 
    HBox actionsBox = new HBox(6);
    actionsBox.setAlignment(Pos.CENTER_RIGHT);

    // Pulsante proroga se necessario
     if (statoEnum != Stato.RESTITUITO && LocalDate.now().isAfter(prest.getData_scadenza().minusDays(8))) {       
        Button btnProroga = new Button("⏳"); 
        btnProroga.getStyleClass().add("icon-button"); 
        
        // Tooltip per spiegare
        Tooltip ttProroga = new Tooltip("Proroga Scadenza di 15 giorni a partire da adesso");

        btnProroga.setTooltip(ttProroga);

        btnProroga.setOnAction(e -> {       
            DataBase.prorogaPrestito(isbn, matricola);        
            DataBase.setStatoPrestito(isbn, matricola, Stato.PROROGATO);     
            updatePrestiti(DataBase.getPrestiti());       
        });
        actionsBox.getChildren().add(btnProroga);
    }
    
    // Pulsante restituzione
    if (statoEnum != Stato.RESTITUITO) {       
        Button btnRestituisci = new Button("RESTITUITO");
        btnRestituisci.getStyleClass().add("button-outline-success");
        
        btnRestituisci.setOnAction(e -> {
            DataBase.restituisci(isbn, matricola);
            DataBase.modificaNum_copie(isbn, true);
            updatePrestiti(DataBase.getPrestiti());
        });
        actionsBox.getChildren().add(btnRestituisci);
    }
    
    // Pulsante email se ritardo
    if (statoEnum == Stato.IN_RITARDO) {      
        Button btnMail = new Button("📧");
        btnMail.getStyleClass().add("icon-button-red"); 
        
        Tooltip tooltip = new Tooltip("Ricorda a " + DataBase.cercaUtente(matricola).getNome() + " di riconsegnare il libro");
        btnMail.setTooltip(tooltip);

        
        btnMail.setOnAction(eh->{
            ArrayList<Prestito> prestiti = DataBase.getPrestiti();           
            Prestito p = null;
            for(Prestito p1: prestiti)
                if(p1.getIsbn().equals(isbn) && p1.getMatricola().equals(matricola))
                    p = p1;           
            if(EmailInvia.inviaAvviso(u.getMail(), titoloLibro, u.getNome(), u.getCognome(),p.getInizio_prestito())){            
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
            }else{            
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
        actionsBox.getChildren().add(btnMail);
    }  
    riga.getChildren().addAll(iconContainer, boxLibro, boxUtente, boxData, spacer, lblStato, actionsBox);
    loansContainer.getChildren().add(riga);
}
    
}
