/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import model.servizi.OperazioniGiornaliere;
import java.time.LocalTime;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author gruppo22
 */
public class Main extends Application{
    
    public static Scene s;
    public static Stage stage;  
    //nicolaM1@  
    public static void main(String[] args){launch(args);}

    @Override
    public void start(Stage stage) throws Exception {
             
        this.stage=stage;
 
        //ArrayList<EmailInfo> m = EmailReader.leggiPostaInviata();
        //System.out.println(m.get(0).getOggetto());
        //EmailSender.sendEmail("nicolamiranda81@gmail.com", "ciao", "prova");
        //System.out.println(Model.DataBase.getUtenti());
        //System.out.println(Model.DataBase.getPrestiti());
        //Model.DataBase.GetCatalogo();
        Parent root = FXMLLoader.load(getClass().getResource("/View/Accesso.fxml"));
        s = new Scene(root,425,500);
        root.getProperties().put("login", "login");
        //s = new Scene(FXMLLoader.load(getClass().getResource("/View/dashboard.fxml")),1280,800);
        stage.setScene(s);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
        PreliminaryFunctions();
        //BackupService.eseguiBackup("C:\\Users\\nicol\\Desktop\\");
    }
    
    public static void PreliminaryFunctions(){
        checkClosed();
        model.servizi.DataBase.initializeDB();
        model.servizi.OperazioniGiornaliere.avviaTaskDiMezzanotte();
        model.servizi.OperazioniGiornaliere.eseguiControlliAutomatici(false);
    }
    
    @Override
    public void stop() throws Exception {
        OperazioniGiornaliere.stop(); 

        super.stop();
        System.exit(0); 
}
    
    public static void checkClosed(){
   
        LocalTime ora_attuale = LocalTime.now();
        LocalTime orario_apertura=LocalTime.of(7, 0);
        LocalTime orario_chiusura=LocalTime.of(20, 0);
        boolean ServiceIsOpen = ora_attuale.isAfter(orario_apertura) && ora_attuale.isBefore(orario_chiusura);
        if(!ServiceIsOpen){
            
            Alert al = new Alert(AlertType.WARNING);
            al.setHeaderText("Servizio chiuso");
            al.setHeaderText("Il servizio resta aperto dalle "+orario_apertura + " fino alle " + orario_chiusura);
            al.showAndWait();
            System.exit(0);
            Platform.exit();
        }
    
    }
    
    
}
