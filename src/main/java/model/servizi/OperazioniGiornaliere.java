/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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


/**
 * @brief Classe che gestisce le operazioni giornaliere automatiche dell'applicazione.
 * Gestisce:
 * - Task automatici a mezzanotte per controlli sui prestiti.
 * - Monitoraggio sessione utente con scadenza e timeout.
 * - Notifiche per prestiti in ritardo.
 * 
 * Utilizza uno scheduler per eseguire periodicamente i task e JavaFX per aggiornare l'interfaccia.
 * 
 * @author gruppo22
 */
public class OperazioniGiornaliere {
    /** Timestamp dell'ultimo reset della sessione utente */
    private static long ultimoResetSessione = System.currentTimeMillis();
    /** Scheduler per eseguire task periodici */
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    /** Durata della sessione in ore, letta dalla configurazione */
    private static int durataSessione = Configurazione.getSessionDuration();
    
    /**
     * @brief Avvia i task automatici giornalieri e di monitoraggio sessione.
     * 
     * Il task giornaliero viene eseguito a mezzanotte per aggiornare lo stato dei prestiti.
     * Il task di monitoraggio sessione verifica ogni 2 secondi la scadenza della sessione
     * e mostra un alert se la sessione è scaduta, caricando la schermata di login.
     */
    public static void avviaTaskDiMezzanotte() {
        long ritardoIniziale = calcolaRitardoVersoMezzanotte();    
        long periodo = 24 * 60 * 60; // Periodo di esecuzione: 24 ore (in secondi)  
        
        scheduler.scheduleAtFixedRate(() -> {
            try {           
                eseguiControlliAutomatici(false);               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ritardoIniziale, periodo, TimeUnit.SECONDS);
        
        long durataSessioneMillis = durataSessione*60*60*1000; // Durata massima della sessione in millisecondi (1 ora) 

        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                try {
                    Main.checkClosed(); 
                    if (Main.stage == null || Main.stage.getScene() == null) {// Se lo stage principale non esiste o non ha scene, resetto il timer
                        ultimoResetSessione = System.currentTimeMillis(); 
                        return;
                    }
                    
                    Parent currentRoot = Main.stage.getScene().getRoot();   
                    
                    if (currentRoot.getProperties().get("login") != null) {// Se siamo sulla schermata di login, resetto il timer                   
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

                         // Chiudo tutte le altre finestre aperte
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
        }, 0, 2, TimeUnit.SECONDS); 
                 
    }
    
     /**
     * @brief Calcola i secondi mancanti alla prossima ora intera.
     * @return numero di secondi fino all'ora successiva
     */
    private static long calcolaSecondiAllaProssimaOra() {
        LocalDateTime now = LocalDateTime.now();
        // Prendo l'ora successiva, minuto 0, secondo 0
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);       
        return Duration.between(now, nextHour).getSeconds();
    }
    
    /**
     * @brief Calcola i secondi mancanti alla mezzanotte successiva.
     * @return numero di secondi fino alla mezzanotte
     */
    private static long calcolaRitardoVersoMezzanotte() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.getZone());
        return Duration.between(now, nextMidnight).getSeconds();
    }

    /**
     * @brief Esegue i controlli automatici sui prestiti.
     * Aggiorna lo stato dei prestiti in ritardo e mostra notifiche se ci sono prestiti scaduti.
     * 
     * @param SkipNotify true se non si vogliono mostrare notifiche
     */
    public static void eseguiControlliAutomatici(boolean SkipNotify) {          
       ArrayList<Prestito> prest = DataBase.getPrestiti();
       boolean ritardi=false;      
       // Controllo tutti i prestiti per identificare quelli in ritardo
       for(Prestito p : prest){
           if(p.getData_scadenza().isBefore(LocalDate.now()) && p.getRestituzione()==null)                   
               DataBase.setStatoPrestito(p.getIsbn(), p.getMatricola(), Stato.IN_RITARDO);
               
           if(p.getStato()==Stato.IN_RITARDO && !ritardi)
               ritardi=true;
       }
       
       // Mostro notifica se ci sono prestiti in ritardo e SkipNotify è false
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

    /**
     * @brief Ferma tutti i task schedulati.
     * Da usare in chiusura dell'applicazione per liberare le risorse.
     */
    public static void stop() {
        scheduler.shutdown();
    }
    
}
