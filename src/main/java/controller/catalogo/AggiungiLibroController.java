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

/**
 *
 * @author nicol
 */
public class AggiungiLibroController {
    
    @FXML
    private TextField txtTitolo;
    
    @FXML
    private MenuButton menuAutori;
    
    @FXML
    private TextField txtEditore;
    
    @FXML
    private TextField txtISBN;
    
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
    
    public static final short MAX_AUTORS=1000;
    public static final short MAX_WRITED=5000;
    
    private String urlIM=null;
    @FXML
    public void initialize(){

        
        SettingForm();
        
    }
    
    public void settingForm(){
        //File f = new File();
        Image img = new Image(getClass().getResourceAsStream("/Images/default.jpg"));
        imgAnteprima.setImage(img);
        urlIM = "/Images/default.jpg";
        UpdateAutori(); //SETTING AUTORI
        SpinnerInitialize(); //SETTING SPINNER
        ButtonInitialize();
        
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
                
                Autore a = Model.DataBase.SearchAutorByNames(nome, cognome);
                
                autori.add(a);
                
                }
                else if(i.getContent() instanceof TextField){
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
                a.setId(Model.DataBase.getNum_Autori()+1);
                //System.out.println(a);
                Model.DataBase.addAutore(a);
                autori.add(a);
                
                
                }
                }
                
            }
             
             l = new Libro(txtISBN.getText().trim(),txtTitolo.getText().trim(),txtEditore.getText().trim(),autori,Year.of(spinAnno.getValue()),spinCopie.getValue(),urlIM);
             
             if((DataBase.GetNumRelationsScritto_Da() + autori.size())>MAX_WRITED){
             
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
             if(DataBase.isIsbnPresent(txtISBN.getText().trim())){
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
                
                //System.out.println(added);
              if(Model.DataBase.addBook(l)){
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
              
                
              UpdateAutori();
              if(DataBase.GetCatalogo().getLibri().size()>=CatalogoController.MAX_BOOKS){
            
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
    
    //AGGIORNAMENTO AUTORI
    public void updateAutori(){
        ArrayList<Autore> autori = Model.DataBase.getAutori();
        menuAutori.getItems().clear();
        
        for(Autore a : autori){
            CustomMenuItem it = new CustomMenuItem(new CheckBox(a.getNome()+" "+a.getCognome()));
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
