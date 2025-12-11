/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gruppo22
 */
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
import model.Configurazione;


public class OperazioniGiornaliere {
    
    private static long ultimoResetSessione = System.currentTimeMillis(); 
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private static int durataSessione = Configurazione.getSessionDuration();
     // Avvia i task automatici (Ccntrolli a mezzanotte (es. aggiornamento stato prestiti) e monitoraggio sessione utente ogni 2 secondi per timeout e scadenza).
    public static void avviaTaskDiMezzanotte() {
        
        // Calcola quanto manca alla prossima mezzanotte per il primo task
        long ritardoIniziale = calcolaRitardoVersoMezzanotte();    
        long periodo = 24 * 60 * 60; // Periodo di esecuzione: 24 ore (in secondi)  
        
        // Task giornaliero eseguito a mezzanotte: esegue controlli automatici sui prestiti
        scheduler.scheduleAtFixedRate(() -> {
            try {           
                eseguiControlliAutomatici(false);               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ritardoIniziale, periodo, TimeUnit.SECONDS);
        
        long durataSessioneMillis = durataSessione*60*60*1000; // Durata massima della sessione in millisecondi (1 ora) 
        
        // Task di monitoraggio sessione utente eseguito ogni 2 secondi (polling rapido)
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                try {
                    Main.checkClosed(); // Controllo la chiusura della finestra principale
                    if (Main.stage == null || Main.stage.getScene() == null) {// Se lo stage principale non esiste o non ha scene, resetta il timer
                        ultimoResetSessione = System.currentTimeMillis(); 
                        return;
                    }
                    
                    Parent currentRoot = Main.stage.getScene().getRoot();   
                    
                    if (currentRoot.getProperties().get("login") != null) {// Se siamo sulla schermata di login, resetta il timer                   
                        ultimoResetSessione = System.currentTimeMillis();
                        return;
                    }                  
                    long tempoPassato = System.currentTimeMillis() - ultimoResetSessione;
                    
                    if (tempoPassato > durataSessioneMillis) { // Se la sessione è scaduta               
                        main.Main.checkClosed();
                        Platform.setImplicitExit(false);
                        // Mostra alert di sessione scaduta
                        Alert al = new Alert(AlertType.WARNING);
                        al.setHeaderText("Sessione scaduta");
                        al.setContentText("La sessione è scaduta. Rifai il login.");
                        al.showAndWait();
                        // Carica finestra di login
                        Stage loginStage = new Stage();
                        FXMLLoader loader = new FXMLLoader(OperazioniGiornaliere.class.getResource("/View/Accesso.fxml"));
                        Parent root = loader.load();
                                     
                        root.getProperties().put("login", "login");
                        
                        Scene s = new Scene(root, 425, 500);
                        main.Main.stage = loginStage;
                        
                        loginStage.setScene(s);
                        loginStage.setResizable(false);
                        loginStage.centerOnScreen();
                        loginStage.show(); 
                        loginStage.setOnCloseRequest(eh->{Platform.exit();System.exit(0);});

                         // Chiude tutte le altre finestre aperte
                        try {
                            javafx.collections.ObservableList<Stage> stages = com.sun.javafx.stage.StageHelper.getStages();
                            for (int i = 0; i < stages.size(); i++) {
                                Stage st = stages.get(i);
                                if (st != loginStage) {
                                    st.close();
                                }
                            }
                        } catch (Exception ex) { }
                        
                         // Reset del timer di sessione 
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

   // Aggiorna lo stato dei prestiti in ritardo e mostra notifiche se ci sono prestiti scaduti
    public static void eseguiControlliAutomatici(boolean SkipNotify) { 
         
       ArrayList<Prestito> prest = DataBase.getPrestiti();
       boolean ritardi=false;
       
       // Controlla tutti i prestiti per identificare quelli in ritardo
       for(Prestito p : prest){
           if(p.getData_scadenza().isBefore(LocalDate.now()) && p.getRestituzione()==null)                   
               DataBase.setStatoPrestito(p.getIsbn(), p.getMatricola(), Stato.IN_RITARDO);
               
           if(p.getStato()==Stato.IN_RITARDO && !ritardi)
               ritardi=true;
       }
       
       // Mostra notifica se ci sono prestiti in ritardo e SkipNotify è false
       if(ritardi && !SkipNotify){    
           Platform.runLater(() -> {
       Stage stage = new Stage();
                stage.setTitle("Avviso");
                stage.setResizable(false);
                stage.setX(1550);
                stage.setY(250);
        try {
            stage.setScene(new Scene(FXMLLoader.load(OperazioniGiornaliere.class.getResource("/View/Notifica.fxml"))));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(OperazioniGiornaliere.class.getName()).log(Level.SEVERE, null, ex);
        }

                   });
       }

    }

    // Ferma tutti i task schedulati dal scheduler. Da usare in chiusura dell'applicazione per liberare le risorse. 
    public static void stop() {
        scheduler.shutdown();
    }
    
}
