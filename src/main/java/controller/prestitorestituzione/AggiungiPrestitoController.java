/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.prestitorestituzione;

import model.servizi.DataBase;
import model.dataclass.Stato;
import model.DataClass.User;
import model.servizi.Prestito;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author nicol
 */
public class AggiungiPrestitoController {
    @FXML
    private TextField txtIsbn;
    
    @FXML
    private TextField txtMatricola;
    
    @FXML
    private Label IsbnCheck;
    
    @FXML
    private Label matricolaCheck;
    
    @FXML
    private Button IsbnCheckButton;
    
    @FXML
    private Button MatricolaCheckButton;
    
    @FXML
    private DatePicker dateInizio;
   
    @FXML
    private DatePicker dateScadenza;
    
    @FXML
    private Button AnnullaButton;
    @FXML
    private Button SalvaButton;
    

    
    private boolean CompletedCheckIsbn=false,CompletedCheckMatricola=false;
    
    @FXML
    public void initialize(){
    dateInizio.setValue(LocalDate.now());
    ButtonInitialize();
    ButtonCheckingInitialize();
    InitializeProperty();
    }
    
     
    
    public void initializeProperty(){
    
        txtIsbn.textProperty().addListener( (a,b,c) ->{
        
            CompletedCheckIsbn=false;
            IsbnCheck.setText("");
        } );
        
        
        txtMatricola.textProperty().addListener( (a,b,c) ->{
        
            CompletedCheckMatricola=false;
            matricolaCheck.setText("");
        } );
        
        
    
    
    }
    
    public void buttonInitialize(){
    
        AnnullaButton.setOnAction(eh->{
        
            Stage s = (Stage) AnnullaButton.getScene().getWindow();
            s.close();
        });
        
        SalvaButton.setOnAction(eh->{
        
            String isbn = txtIsbn.getText().trim();
            String matricola = txtMatricola.getText().trim();
            //CONTROLLI PER MATRICOLA E ISBN
            if(!CompletedCheckIsbn && !CompletedCheckMatricola){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Controlli non completati");
                IsbnAlert.setContentText("Devi verificare che la matricola e l'isbn esistano nel sistema cliccando i rispettivi pulsanti di fianco ai loro form");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            }else if(!CompletedCheckIsbn){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Controlli non completati");
                IsbnAlert.setContentText("Devi verificare che l'isbn esista nel sistema cliccando il rispettivo pulsante di fianco al suo form");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            
            }else if(!CompletedCheckMatricola){
            
                 Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Controlli non completati");
                IsbnAlert.setContentText("Devi verificare che la matricola esista nel sistema cliccando il rispettivo pulsante di fianco al suo form");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            
            }
            
            LocalDate inizio = dateInizio.getValue();
            
            if(inizio==null)
                inizio = LocalDate.now();//SE NON ISERISCI LA DATA, VIENE IMPOSTATA QUELLA ATTUALE
            
            LocalDate scadenza = dateScadenza.getValue();
            if(scadenza==null){
            
                  Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Campo vuoto");
                IsbnAlert.setContentText("Inserisci la data di scadenza del prestito");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            
            
            }
            
            if(scadenza.isBefore(inizio)){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione impossibile");
                IsbnAlert.setContentText("Hai impostato una data di scadenza che viene prima in ordine cronologico della data di inizio prestito");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            
            }
            
            if(inizio.isBefore(LocalDate.now())){
                
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione impossibile");
                IsbnAlert.setContentText("Hai impostato una data di inizio prestito antecedente a quella odierna");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                            
            
            }
            
            if(DataBase.CheckPrestito(isbn, matricola)){
                boolean passed=false;
                for(Prestito p : DataBase.getPrestiti())
                    if(p.getStato().equals(Stato.RESTITUITO) && p.getIsbn().equals(isbn) && p.getMatricola().equals(matricola)){
                        DataBase.RemovePrestito(isbn, matricola);
                        passed=true;
                        break;
                    }     
                if(!passed){
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione non eseguita");
                IsbnAlert.setContentText("L'utente ha gia preso in prestito il libro richiesto");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                }
            }
            
            if(DataBase.getNumCopieByIsbn(isbn)<=0){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione fallita");
                IsbnAlert.setContentText("Copie terminate di "+DataBase.searchBook(isbn).getTitolo());
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            
            }
            
            
            
            if(DataBase.searchUser(matricola).isBloccato()){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione fallita");
                IsbnAlert.setContentText("L'utente risulta bloccato");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
                
            
            }
            
            //CONTROLLO SE L'UTENTE HA gia 3 libri in prestito
            ArrayList<Prestito> prestiti = DataBase.getPrestiti();
            String mat1 = matricola;
            int count=0;
            for(Prestito p : prestiti)
                if(p.getMatricola().equals(mat1) && p.getStato()!=Stato.RESTITUITO)
                    count+=1;
            if(count>=3){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione fallita");
                IsbnAlert.setContentText("L'utente risulta avere ancora 3 libri da restituire");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            
            }
            
            if(DataBase.addPrestito(new Prestito(isbn,matricola,inizio,null,Stato.ATTIVO,scadenza))){
                DataBase.modifyNum_copie(isbn, false);
                 Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione eseguita");
                User s = DataBase.searchUser(matricola);
                IsbnAlert.setContentText("Prestito di"+DataBase.searchBook(isbn).getTitolo()+ " confermato all'utente "+s.getNome()+" "+s.getCognome());
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                
                
                int totale_prestiti = DataBase.getPrestiti().size();
            
            if(totale_prestiti>=PrestitoRestituzioneController.MAX_LOAN){
            
            int i = 0;
            ArrayList<Prestito> pre = DataBase.getPrestiti();
            for(Prestito p : pre)
                if(p.getStato()==Stato.RESTITUITO)
                    i+=1;
                
            if(totale_prestiti-i<PrestitoRestituzioneController.MAX_LOAN){
            
                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setHeaderText("Azione necessaria");
                conf.setContentText("Per aggiungere prestiti e necessario svuotare i prestiti memorizzati in memoria, vuoi che rimuovo gli ultimi prestiti che risultano gia restituiti partendo dal piu vecchio?");
                Optional<ButtonType> confirm = conf.showAndWait();
                if(confirm.isPresent() && confirm.get() == ButtonType.OK){
                
                pre.sort(new Comparator<Prestito>() {
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
            
                for(int f=0;f<totale_prestiti-(PrestitoRestituzioneController.MAX_LOAN-1);f++){
                        Prestito p = pre.get(f);
                        DataBase.RemovePrestito(p.getIsbn(), p.getMatricola());
                }
                }
                
            }else{
            
             Alert rt = new Alert(Alert.AlertType.ERROR);
                rt.setHeaderText("Chiusura pannello");
                rt.setContentText("Ci sono troppe copie dei nostri libri prestate");
                
                DialogPane dialogPane1 = rt.getDialogPane();

              
                dialogPane1.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane1.getStyleClass().add("my-alert");
                
                rt.showAndWait();
                
                Stage il = (Stage) SalvaButton.getScene().getWindow();
                il.close();
                return;
            
            }
            
            
                
                
            
            }
                
                
                
                return;
                
            
            }else{
            
                   Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Operazione non eseguita");
                IsbnAlert.setContentText("Prestito fallito");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            
            }
            
            
        });
        
    
    }
        
    
    public void buttonCheckingInitialize(){
    
        IsbnCheckButton.setOnAction(eh->{
        
            //CONTROLLO ISBN
            String isbn = txtIsbn.getText().trim();
            if(isbn.length()!=13){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Codice ISBN non valido");
                IsbnAlert.setContentText("Il codice isbn deve essere a 13 cifre");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            }else if(!isbn.matches("\\d+")){
                
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
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
            if(DataBase.searchBook(isbn)==null){
                
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Codice ISBN non valido");
                IsbnAlert.setContentText("Non e stato trovato alcun libro con questo codice nel nostro sistema");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
            return;
            }
                
            CompletedCheckIsbn=true;
            IsbnCheck.setText(isbn);
            
        
        });
        
        MatricolaCheckButton.setOnAction(eh->{
        
            String matricola = txtMatricola.getText().trim();
            //CONTROLLO MATRICOLA
            if(matricola.length()!=10){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Matricola non valida");
                IsbnAlert.setContentText("La matricola deve essere a 10 cifre");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
                return;
            }else if(!matricola.matches("\\d+")){
                
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Matricola non valida");
                IsbnAlert.setContentText("La matricola deve contenere solo numeri");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
            return;
            }
            
            if(DataBase.searchUser(matricola)==null){
            
                Alert IsbnAlert = new Alert(Alert.AlertType.ERROR);
                IsbnAlert.setHeaderText("Matricola non valida");
                IsbnAlert.setContentText("La matricola inserita non e associata ad alcun studente nel database");
                
                DialogPane dialogPane = IsbnAlert.getDialogPane();

              
                dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/StyleAccess.css").toExternalForm()
                );

                
                dialogPane.getStyleClass().add("my-alert");
                
                IsbnAlert.showAndWait();
            return;
                
            
            }
            CompletedCheckMatricola=true;
            matricolaCheck.setText(matricola);
        });
        
        
        
        
    
    }
    
}
