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
import java.io.InputStream;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * @brief Controller per la modifica di un libro esistente.
 *
 * Gestisce il form per aggiornare i dati di un libro già presente nel database.
 * Include:
 * - Pre-caricamento dei dati esistenti (titolo, editore, anno, copie, autori, copertina).
 * - Gestione complessa delle immagini da risorse o file system.
 * - Modifica autori esistenti o aggiunta di nuovi autori.
 * - Salvataggio delle modifiche nel database con conferma tramite Alert.
 * 
 * @author gruppo22
 */
public class ModificaLibroController {
    @FXML
    private TextField txtTitolo;
    
    @FXML
    private MenuButton menuAutori;
    
    @FXML
    private TextField txtEditore;
    
    @FXML
    private Spinner<Integer> spinAnno;
    
    @FXML
    private Spinner<Integer> spinCopie;
    
    @FXML
    private ImageView imgAnteprima;
    
    @FXML
    private Button ScegliFileButton;
    
    @FXML
    private Button AnnullaButton;
    
    @FXML
    private Button SalvaButton;
    
    @FXML
    private Button RimuoviCopButton;
    
   // VARIABILI DI CONTROLLO
    public static String isbn;// ISBN del libro da modificare (settato da altro controller)
    private Libro lib;// Oggetto libro corrente
    private String urlIM=null;// Percorso immagine copertina
   
     /**
     * @brief Inizializza il controller.
     *
     * Recupera il libro dal database usando l'ISBN statico e popola il form con i dati correnti
     * tramite il metodo `settingForm`.
     */
    @FXML
    public void initialize(){
        lib = DataBase.cercaLibro(isbn); 
        settingForm();       
    }
    
    /**
     * @brief Popola il form con i dati del libro caricato.
     *
     * Esegue le seguenti operazioni:
     * 1. Caricamento immagine robusto da risorse interne o file system; fallback default.
     * 2. Impostazione testi (titolo, editore).
     * 3. Gestione autori con `updateAutori`.
     * 4. Inizializzazione degli Spinner (anno e copie).
     */
    public void settingForm(){
        Image img = null;// Imposto immagine copertina
        String path = lib.getUrl();

        try {
            // TENTATIVO 1: Caricamento da Risorse (dentro il JAR/Progetto)
            // Funziona se il path è tipo "/Images/copertina.png"
            InputStream is = getClass().getResourceAsStream(path);

            if (is != null) {
                img = new Image(is);
            } else {
                // TENTATIVO 2: Caricamento da File System (Disco Fisso)
                // Se is è null, vuol dire che non è nelle risorse. Proviamo a cercarlo sul disco.
                // JavaFX vuole "file:" davanti ai percorsi disco per funzionare nel costruttore stringa
                String externalPath = path;

                // Se non inizia già con un protocollo, aggiungiamo "file:"
                if (!path.startsWith("file:") && !path.startsWith("http")) {
                    externalPath = "file:" + path;
                }
                img = new Image(externalPath);
            }
        } catch (Exception e) {
            // TENTATIVO 3: Fallback (Tutto è fallito)
            System.err.println("Impossibile caricare immagine: " + path + ". Uso default.");
            try {
                img = new Image(getClass().getResourceAsStream("/Images/default.png"));
            } catch (Exception ex) {
                img = null; // Arresi, nessuna immagine
            }
        }

        // Imposto l'immagine (gestendo anche il caso null finale)
        imgAnteprima.setImage(img);
        urlIM = lib.getUrl();
        txtTitolo.setText(lib.getTitolo());
        txtEditore.setText(lib.getEditore());
        ArrayList<Autore> aut = new ArrayList(); aut.addAll(lib.getAutori());
        updateAutori(aut); //SETTING AUTORI
        spinnerInitialize(); //SETTING SPINNER
        buttonInitialize();       
    }
    
    /**
     * @brief Configura le azioni dei pulsanti del form.
     *
     * - `ScegliFileButton`: seleziona una nuova copertina dal file system.
     * - `RimuoviCopButton`: ripristina immagine default.
     * - `AnnullaButton`: chiude il form senza salvare.
     * - `SalvaButton`: salva le modifiche nel database, creando eventualmente nuovi autori.
     */
    public void buttonInitialize(){ 
        
        ScegliFileButton.setOnAction(eh->{  // Bottone per scegliere copertina        
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
        
        RimuoviCopButton.setOnAction(eh->{// Bottone per rimuovere copertina (imposta immagine default)
            Image img = new Image(getClass().getResourceAsStream("/Images/default.jpg"));
            imgAnteprima.setImage(img);
            urlIM = "/Images/default.jpg";
        });
               
        // Bottone Annulla chiude la finestra senza salvare
        AnnullaButton.setOnAction(eh->{Stage s =(Stage)AnnullaButton.getScene().getWindow();s.close();});
        
        SalvaButton.setOnAction(eh->{// Bottone Salva salva le modifiche nel database           
            //Controlli sui campi
            Libro l;           
            ArrayList<Autore> autori = new ArrayList<>();
            Iterator<MenuItem> it = menuAutori.getItems().iterator();
            
            // Controllo quali autori sono selezionati
            while(it.hasNext()){
                CustomMenuItem i = (CustomMenuItem) it.next();
                               
                if(i.getContent() instanceof CheckBox){
                CheckBox ck = (CheckBox) i.getContent();
                if(!ck.isSelected()) continue;
                String[] parti = ck.getText().split(" ");
                String nome = parti[0];
                String cognome = parti[1];
                
                Autore a = model.servizi.DataBase.cercaAutoreByNames(nome, cognome);
                
                autori.add(a);                
                }
                else if(i.getContent() instanceof TextField){                
                TextField txt = (TextField) i.getContent();
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
             
            // Creo nuovo oggetto libro aggiornato
             l = new Libro(isbn,txtTitolo.getText().trim(),txtEditore.getText().trim(),autori,Year.of(spinAnno.getValue()),spinCopie.getValue(),urlIM);   
            // Salvo modifiche nel database
             boolean modified = model.servizi.DataBase.modificaLibro(l.getIsbn(), l.getTitolo(), l.getEditore(), 
                     (l.getAnno_pubblicazione()).getValue(), l.getNumero_copieDisponibili(), urlIM, autori);
            
              if(modified){// Messaggi di conferma o errore
                Alert AL = new Alert(Alert.AlertType.INFORMATION);
                AL.setHeaderText("Aggiornamento Catalogo");
                AL.setContentText("Libro modificato");
                
                DialogPane dialogPane = AL.getDialogPane();
              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );
               
                dialogPane.getStyleClass().add("my-alert");
                
                AL.showAndWait();
              }else{
                Alert IsbnAlert = new Alert(Alert.AlertType.WARNING);
                IsbnAlert.setHeaderText("Operazione fallita");
                IsbnAlert.setContentText("Modifiche non completate");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();
             
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );               
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
            return;                                   
              }
             
            // Chiudo la finestra dopo il salvataggio   
             Stage s = (Stage) SalvaButton.getScene().getWindow();
             s.close();       
        });
     
    }
    
    //AGGIORNAMENTO AUTORI

    /**
     * @brief Aggiorna il menu degli autori.
     *
     * Popola il MenuButton con:
     * 1. CheckBox per tutti gli autori presenti nel database (pre-selezionando quelli del libro corrente).
     * 2. TextField vuoti per inserire nuovi autori.
     *
     * @param aut Lista degli autori già associati al libro (per selezionare le checkbox corrette)
     */
    public void updateAutori(ArrayList<Autore> aut){  
        ArrayList<Autore> autori = model.servizi.DataBase.getAutori();
        menuAutori.getItems().clear();
        
        for(Autore a : autori){
            CheckBox checkBox = new CheckBox(a.getNome()+" "+a.getCognome());
            for(Autore a1 : aut)
                if(a1.getId()==a.getId()){
                    checkBox.setSelected(true);break;
                }
            // Aggiungo 4 TextField vuoti per nuovi autori
            CustomMenuItem it = new CustomMenuItem(checkBox);
            it.setHideOnClick(false);
            menuAutori.getItems().add(it);        
        }
        CustomMenuItem[] altro = new CustomMenuItem[4];
        for(int i=0;i<4;i++){
        TextField altroAut = new TextField();
        altroAut.setPromptText("Nome Cognome");
        altro[i] = new CustomMenuItem(altroAut);
        altro[i].setHideOnClick(false);
        }
        menuAutori.getItems().addAll(altro);
    }


    /**
     * @brief Inizializza gli Spinner del form.
     *
     * - Anno di pubblicazione: 1500-2100
     * - Numero di copie: 0-500
     */
    private void spinnerInitialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1500, 2100, 2024, 1);
        spinAnno.setValueFactory(valueFactory);
        
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 0, 1);
        spinCopie.setValueFactory(valueFactory);
    }
    
}
