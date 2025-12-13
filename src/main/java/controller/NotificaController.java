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
 *
 * @author gruppo22
 */

public class NotificaController {//controlla la schermata che mostra le notifiche dei ritardi
    @FXML
    private Label numRit; //label usato per mostrare il numero di prestiti in ritardo
    
    @FXML
    public void initialize(){//metodo chiamato automaticamente quando la finestra viene caricata 
            int i=0;         
            for(Prestito p : DataBase.getPrestiti()) //scorro tutti i prestiti
                if(p.getStato()==Stato.IN_RITARDO) //controllo lo stato del prestito
                    i+=1;        
            numRit.setText("Ci sono "+i+" prestiti scaduti dove non sono state\nrestituite le copie, si suggerisci di inviare\n avvisi agli interessati");
    } 
}
