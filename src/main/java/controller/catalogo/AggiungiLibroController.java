/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.catalogo;

import model.servizi.DataBase;
import model.dataclass.Autore;
import model.dataclass.Libro;
import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Configurazione;

/**
 * @brief Controller per l'aggiunta di un nuovo libro al catalogo.
 * * Questa classe gestisce il form di inserimento dati per nuovi libri.
 * Include validazioni specifiche per l'ISBN, gestione dinamica degli autori
 * e caricamento immagine di copertina.
 * * @author gruppo22
 * @version 1.0
 */
public class AggiungiLibroController {
    
    // CAMPI TESTO
    @FXML
    private TextField txtTitolo;
    
    @FXML
    private MenuButton menuAutori;
    
    @FXML
    private TextField txtEditore;
    
    @FXML
    private TextField txtISBN;
    
    // SPINNER PER NUMERI
    @FXML
    private Spinner<Integer> spinAnno;
    
    @FXML
    private Spinner<Integer> spinCopie;
    
    // IMAGEVIEW PER COPERTINA
    @FXML
    private ImageView imgAnteprima;
    
    // BUTTON
    @FXML
    private Button ScegliFileButton;
    
    @FXML
    private Button AnnullaButton;
    
    @FXML
    private Button SalvaButton;
    @FXML
    private Button RimuoviCopButton;
    
    // COSTANTI
    public static final int MAX_AUTORS=Configurazione.getMaxAuthors(); //Numero massimo autori nel sistema
    public static final int MAX_WRITED=Configurazione.getMaxWrited(); //Numero massimo relazioni libro-autore
    
        
    // VARIABILI
    private String urlIM=null; // path della copertina (di default null)
   
    @FXML
     /**
     * @brief Inizializza il form.
     * Viene chiamato all'apertura della finestra.
     */
    public void initialize(){//Configura il form con immagine di default, autori, spinner e bottoni.
        settingForm();      
    }
    
     /**
     * @brief Imposta lo stato iniziale del form.
     * Carica l'immagine di default, popola il menu autori e inizializza gli spinner.
     */
    public void settingForm(){
        Image img = new Image(getClass().getResourceAsStream("/Images/default.jpg"));
        imgAnteprima.setImage(img);
        urlIM = "/Images/default.jpg";
        updateAutori(); //SETTING AUTORI
        spinnerInitialize(); //SETTING SPINNER
        buttonInitialize();   
    }
    
     /**
     * @brief Configura la logica di tutti i pulsanti.
     * Gestisce il caricamento file, il reset dell'immagine, l'annullamento e il salvataggio
     * con tutte le relative validazioni (ISBN, Autori, Limiti DB).
     */
    public void buttonInitialize(){
        ScegliFileButton.setOnAction(eh->{  // SELEZIONE FILE COPERTINA    
            FileChooser fc = new FileChooser();
            // Filtra per immagini (JPG, PNG)
            fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("Tutti i file", "*.*")
            );
            fc.setTitle("Scegli la copertina");         
            File f = fc.showOpenDialog((Stage) (ScegliFileButton.getScene().getWindow()));        
            if(f!=null){
                imgAnteprima.setImage(new Image(f.toURI().toString()));
                urlIM = f.toURI().toString();        
            }        
        });     
        RimuoviCopButton.setOnAction(eh->{ // RIMUOVI COPERTINA
            Image img = new Image(getClass().getResourceAsStream("/Images/default.jpg"));
            imgAnteprima.setImage(img);
            urlIM = "/Images/default.jpg";
        });
         
        AnnullaButton.setOnAction(eh->{Stage s =(Stage)AnnullaButton.getScene().getWindow();s.close();}); // ANNULLA FORM    
        
        SalvaButton.setOnAction(eh->{// SALVA LIBRO       
            //CONTROLLI SUI CAMPI
            if(txtISBN.getText().trim().length()!=13){
            
                Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Codice ISBN non valido");
                IsbnAlert.setContentText("Il codice isbn deve essere a 13 cifre");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
   
                dialogPane.getStyleClass().add("my-alert");
        
                IsbnAlert.showAndWait();
                return;
            }else if(!txtISBN.getText().trim().matches("\\d+")){          
                Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Codice ISBN non valido");
                IsbnAlert.setContentText("Il codice isbn deve contenere solo numeri");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
    
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
            return;
            }
            
            // CREAZIONE OGGETTO LIBRO
            Libro l;            
            ArrayList<Autore> autori = new ArrayList<>();
            
            // SCORRO MENU AUTORI
            Iterator<MenuItem> it = menuAutori.getItems().iterator();
            while(it.hasNext()){
                CustomMenuItem i = (CustomMenuItem) it.next();
                
                
                if(i.getContent() instanceof CheckBox){ // Se l'autore è selezionato
                CheckBox ck = (CheckBox) i.getContent();
                if(!ck.isSelected()) continue;
                String[] parti = ck.getText().split(" ");
                String nome = parti[0];
                String cognome = parti[1];
                
                Autore a = model.servizi.DataBase.cercaAutoreByNames(nome, cognome);
                
                autori.add(a);                
                }
                else if(i.getContent() instanceof TextField){// Nuovo autore da inserire
                TextField txt = (TextField) i.getContent();
                if(DataBase.getNum_Autori()>=MAX_AUTORS && !txt.getText().trim().equals("")){
                
                     Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Errore creazione libro");
                IsbnAlert.setContentText("Ci sono troppi autori inseriti nel sistema");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
              
                dialogPane.getStyleClass().add("my-alert");               
                IsbnAlert.showAndWait();                
                return;
                }else{
                                   
                if(txt.getText().trim().equals(""))continue;
                String[] parti = txt.getText().trim().split(" ");
                String nome = parti[0];
                String cognome = parti[1];
                if(nome.equals("") & cognome.equals("")) continue;
                
                Autore a = new Autore(nome,cognome,0,null);
                a.setId(model.servizi.DataBase.getNum_Autori()+1);
                model.servizi.DataBase.aggiungiAutore(a);
                autori.add(a);        
                }
                }                
            }
            
             // Creazione oggetto libro con tutti i dati
             l = new Libro(txtISBN.getText().trim(),txtTitolo.getText().trim(),txtEditore.getText().trim(),autori,Year.of(spinAnno.getValue()),spinCopie.getValue(),urlIM);
             
             if((DataBase.getNumRelationsScritto_Da() + autori.size())>MAX_WRITED){ // Controllo numero massimo relazioni libro-autore            
                  Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Errore creazione libro");
                IsbnAlert.setContentText("Ci sono troppe relazioni tra libri e autori nel sistema");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );                
                dialogPane.getStyleClass().add("my-alert");               
                IsbnAlert.showAndWait();                
                return;            
             }
             
             if(DataBase.isIsbnPresent(txtISBN.getText().trim())){ // Controllo ISBN già presente
                Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Operazione fallita");
                IsbnAlert.setContentText("Libro con ISBN: "+txtISBN.getText().trim()+" risulta gia registrato nel database");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                
                dialogPane.getStyleClass().add("my-alert");               
                IsbnAlert.showAndWait();
            return;                                  
              }               
               
              if(model.servizi.DataBase.aggiungiLibro(l)){// Aggiunta libro al database
                Alert AL = new Alert(AlertType.ERROR);
                AL.setHeaderText("Aggiornamento Catalogo");
                AL.setContentText("Libro aggiunto al catalogo");
                
                DialogPane dialogPane = AL.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
             
                dialogPane.getStyleClass().add("my-alert");
                
                AL.showAndWait();
              }                            
              updateAutori();// aggiorno menu autori
              if(DataBase.getCatalogo().getLibri().size()>=CatalogoController.MAX_BOOKS){
            
                 Alert IsbnAlert = new Alert(AlertType.ERROR);
                IsbnAlert.setHeaderText("Chiusura Pannello");
                IsbnAlert.setContentText("Ci sono troppi Libri nel sistema");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                Stage tu = (Stage)SalvaButton.getScene().getWindow();
                tu.close();
                return;
            }                           
        });
                
    }
    
     /**
     * @brief Aggiorna la lista degli autori nel MenuButton.
     * Crea checkbox per gli autori esistenti e campi di testo per i nuovi.
     */
    public void updateAutori(){ //AGGIORNAMENTO AUTORI
        ArrayList<Autore> autori = model.servizi.DataBase.getAutori();
        menuAutori.getItems().clear();
        
        for(Autore a : autori){ // Autori esistenti
            CustomMenuItem it = new CustomMenuItem(new CheckBox(a.getNome()+" "+a.getCognome()));
            it.setHideOnClick(false);
            menuAutori.getItems().add(it);       
        }
        CustomMenuItem[] altro = new CustomMenuItem[4];
        for(int i=0;i<4;i++){// Campi per inserire nuovi autori (max 4)
        TextField altroAut = new TextField();
        altroAut.setPromptText("Nome Cognome");
        altro[i] = new CustomMenuItem(altroAut);
        altro[i].setHideOnClick(false);
        }
        menuAutori.getItems().addAll(altro);     
    }

    
     /**
     * @brief Inizializza i valori e i range degli Spinner (Anno tra 1500-2100 e Copie tra 0-500).
     */
    private void spinnerInitialize() { 
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1500, 2100, 2024, 1);//spinAnno: da 1500 a 2100, default 2024

        spinAnno.setValueFactory(valueFactory);
        
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 0, 1);//spinCopie: da 0 a 500, default 0
        spinCopie.setValueFactory(valueFactory);
    }
    
    
}
