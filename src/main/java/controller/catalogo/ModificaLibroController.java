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
    

    public static String isbn;
    private Libro lib;
    private String urlIM=null;
   
    @FXML
    public void initialize(){
        lib = DataBase.cercaLibro(isbn);
        settingForm();
        
    }
    
    public void settingForm(){
        //File f = new File();
        Image img = null;
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
            // Assicurati di avere un'immagine "no_cover.png" nelle tue risorse
            // Se non ce l'hai, metti img = null; ma gestiscilo nella ImageView
            try {
                img = new Image(getClass().getResourceAsStream("/Images/default.png"));
            } catch (Exception ex) {
                img = null; // Arresi, nessuna immagine
            }
        }

        // Imposta l'immagine (gestendo anche il caso null finale)
        imgAnteprima.setImage(img);
        urlIM = lib.getUrl();
        txtTitolo.setText(lib.getTitolo());
        txtEditore.setText(lib.getEditore());
        ArrayList<Autore> aut = new ArrayList(); aut.addAll(lib.getAutori());
        updateAutori(aut); //SETTING AUTORI
        spinnerInitialize(); //SETTING SPINNER
        buttonInitialize();
        
    }
    
    public void buttonInitialize(){
    
        ScegliFileButton.setOnAction(eh->{
        
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
        
        RimuoviCopButton.setOnAction(eh->{
            Image img = new Image(getClass().getResourceAsStream("/Images/default.jpg"));
            imgAnteprima.setImage(img);
            urlIM = "/Images/default.jpg";
        });
        
        
        
        AnnullaButton.setOnAction(eh->{Stage s =(Stage)AnnullaButton.getScene().getWindow();s.close();});
        
        SalvaButton.setOnAction(eh->{
            
            //CONTROLLI SUI CAMPI
  
            
            Libro l;
            
            ArrayList<Autore> autori = new ArrayList<>();
            Iterator<MenuItem> it = menuAutori.getItems().iterator();
            
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
                //System.out.println(a);
                model.servizi.DataBase.aggiungiAutore(a);
                autori.add(a);
                }
                
            }
             
             l = new Libro(isbn,txtTitolo.getText().trim(),txtEditore.getText().trim(),autori,Year.of(spinAnno.getValue()),spinCopie.getValue(),urlIM);   
                boolean modified = model.servizi.DataBase.modificaLibro(l.getIsbn(), l.getTitolo(), l.getEditore(), (l.getAnno_pubblicazione()).getValue(), l.getNumero_copieDisponibili(), urlIM, autori);
                //System.out.println(added);
              if(modified){
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
                
             Stage s = (Stage) SalvaButton.getScene().getWindow();
             s.close();
                
                
            
        });
        
        
    }
    
    //AGGIORNAMENTO AUTORI
    public void updateAutori(ArrayList<Autore> aut){
        ArrayList<Autore> autori = model.servizi.DataBase.getAutori();
        menuAutori.getItems().clear();
        
        for(Autore a : autori){
            CheckBox checkBox = new CheckBox(a.getNome()+" "+a.getCognome());
            for(Autore a1 : aut)
                if(a1.getId()==a.getId()){
                    checkBox.setSelected(true);break;
                }
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
        //System.out.println();
                
        
    }

    private void spinnerInitialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1500, 2100, 2024, 1);

        spinAnno.setValueFactory(valueFactory);
        
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 0, 1);
        spinCopie.setValueFactory(valueFactory);
    }
    
    
}
