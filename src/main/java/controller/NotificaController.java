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

//controlla la schermata che mostra le notifiche dei ritardi
public class NotificaController {
    @FXML
    private Label numRit; //label usato per mostrare il numero di prestiti in ritardo
    
    //metodo chiamato automaticamente quando la finestra viene caricata 
    @FXML
    public void initialize(){
            int i=0;         
            for(Prestito p : DataBase.getPrestiti()) //scorre tutti i prestiti
                if(p.getStato()==Stato.IN_RITARDO) //controlla lo stato del prestito
                    i+=1;        
            numRit.setText("Ci sono "+i+" prestiti scaduti dove non sono state\nrestituite le copie, si suggerisci di inviare\n avvisi agli interessati");
    } 
}
