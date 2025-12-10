
package model.servizi;

import javafx.scene.Parent;
import model.dataclass.Stato;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import main.Main;


public class OperazioniGiornaliere {
    private static long ultimoResetSessione = System.currentTimeMillis(); 
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void avviaTaskDiMezzanotte() {
        // 1. Calcola quanto manca alla prossima mezzanotte
        long ritardoIniziale = calcolaRitardoVersoMezzanotte();    
        long periodo = 24 * 60 * 60; //MI CALCOLO LA GIORNATA IN SECONDI    
        scheduler.scheduleAtFixedRate(() -> {
            try {           
                eseguiControlliAutomatici(false);               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ritardoIniziale, periodo, TimeUnit.SECONDS);
        long durataSessioneMillis = 3600000;      
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                try {
                    Main.checkClosed(); // Controllo la chiusura
                    if (Main.stage == null || Main.stage.getScene() == null) {
                        ultimoResetSessione = System.currentTimeMillis(); 
                        return;
                    }
                    Parent currentRoot = Main.stage.getScene().getRoot();                  
                    if (currentRoot.getProperties().get("login") != null) {                      
                        ultimoResetSessione = System.currentTimeMillis();
                        return;
                    }                  
                    long tempoPassato = System.currentTimeMillis() - ultimoResetSessione;
                    if (tempoPassato > durataSessioneMillis) {                 
                        main.Main.checkClosed();
                        Platform.setImplicitExit(false); 
                        Alert al = new Alert(AlertType.WARNING);
                        al.setHeaderText("Sessione scaduta");
                        al.setContentText("La sessione è scaduta. Rifai il login.");
                        al.showAndWait();                       
                        Stage loginStage = new Stage();
                        FXMLLoader loader = new FXMLLoader(OperazioniGiornaliere.class.getResource("/View/Access.fxml"));
                        Parent root = loader.load();
                                     
                        root.getProperties().put("login", "login");
                        
                        Scene s = new Scene(root, 425, 500);
                        main.Main.stage = loginStage;
                        
                        loginStage.setScene(s);
                        loginStage.setResizable(false);
                        loginStage.centerOnScreen();
                        loginStage.show(); 
                        loginStage.setOnCloseRequest(eh->{Platform.exit();System.exit(0);});

                        
                        try {
                            javafx.collections.ObservableList<Stage> stages = com.sun.javafx.stage.StageHelper.getStages();
                            for (int i = 0; i < stages.size(); i++) {
                                Stage st = stages.get(i);
                                if (st != loginStage) {
                                    st.close();
                                }
                            }
                        } catch (Exception ex) { }
                        
                        
                        ultimoResetSessione = System.currentTimeMillis();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
        }, 0, 2, TimeUnit.SECONDS); // Controllo ogni 2 secondi (polling rapido)
            
      
    }
    
    private static long calcolaSecondiAllaProssimaOra() {
        LocalDateTime now = LocalDateTime.now();
        // Prende l'ora successiva, minuto 0, secondo 0
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        
        return Duration.between(now, nextHour).getSeconds();
    }
    
    private static long calcolaRitardoVersoMezzanotte() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.getZone());
        return Duration.between(now, nextMidnight).getSeconds();
    }

   
    public static void eseguiControlliAutomatici(boolean SkipNotify) {
        
       ArrayList<Prestito> prest = DataBase.getPrestiti();
       boolean ritardi=false;
       for(Prestito p : prest){
        
           if(p.getData_scadenza().isBefore(LocalDate.now()) && p.getRestituzione()==null)
                        
               DataBase.setStatoPrestito(p.getIsbn(), p.getMatricola(), Stato.IN_RITARDO);
               
           if(p.getStato()==Stato.IN_RITARDO && !ritardi)
               ritardi=true;
       }
       //NOTIFICA
       if(ritardi && !SkipNotify){
           
           Platform.runLater(() -> {
       Stage stage = new Stage();
                stage.setTitle("Avviso");
                stage.setResizable(false);
                stage.setX(1550);
                stage.setY(250);
        try {
            stage.setScene(new Scene(FXMLLoader.load(OperazioniGiornaliere.class.getResource("/View/notifica.fxml"))));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(OperazioniGiornaliere.class.getName()).log(Level.SEVERE, null, ex);
        }
                
       
                   });
       }
       
       
    }

  
    public static void stop() {
        scheduler.shutdown();
    }
    
}
