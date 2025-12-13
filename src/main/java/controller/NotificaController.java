/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.servizi.DataBase;
import model.dataclass.Stato;
import model.servizi.Prestito;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @brief Controller per la visualizzazione delle notifiche di ritardo.
 *
 * Questa classe gestisce una semplice notifica (view) che calcola e mostra all'utente
 * il numero totale di prestiti che si trovano attualmente nello stato "IN_RITARDO".
 * Aggiorna dinamicamente una Label con il conteggio e un messaggio di suggerimento.
 * 
 * @author gruppo22
 */
public class NotificaController {
    @FXML
    private Label numRit; //label usato per mostrare il numero di prestiti in ritardo
    
    
     /**
     * @brief Metodo di inizializzazione del controller.
     *
     * Viene chiamato automaticamente al caricamento della view.
     * Scorre la lista dei prestiti dal `DataBase`, conta quanti sono in stato `IN_RITARDO`
     * e aggiorna il testo della Label con il conteggio e un messaggio di suggerimento.
     */
    @FXML
    public void initialize(){
            int i=0;         
            for(Prestito p : DataBase.getPrestiti()) //scorro tutti i prestiti
                if(p.getStato()==Stato.IN_RITARDO) //controllo lo stato del prestito
                    i+=1;        
            numRit.setText("Ci sono "+i+" prestiti scaduti dove non sono state\nrestituite le copie, si suggerisci di inviare\n avvisi agli interessati");
    } 
}
