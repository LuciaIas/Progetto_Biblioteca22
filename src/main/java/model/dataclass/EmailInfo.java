/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.util.Date;

/**
 * @brief Classe che rappresenta le informazioni di un'email.
 * 
 * Questa classe contiene i dati principali di un'email:
 * oggetto, destinatario e data di invio.
 * Viene utilizzata per memorizzare informazioni sulle email inviate.
 * 
 * @author gruppo22
 */
public class EmailInfo {
    private String oggetto;
    private String destinatario;
    private Date dataInvio;

    
   /**
     * @brief Costruisce un nuovo oggetto EmailInfo.
     * 
     * @param oggetto oggetto dell'email
     * @param destinatario destinatario dell'email
     * @param dataInvio data di invio dell'email
     */
    public EmailInfo(String oggetto, String destinatario, Date dataInvio) {
        this.oggetto = oggetto;
        this.destinatario = destinatario;
        this.dataInvio = dataInvio;
    }  
    
    /**
     * @brief Restituisce l'oggetto dell'email.
     * @return oggetto dell'email
     */
    public String getOggetto() { 
        return oggetto; 
    }
    
    /**
     * @brief Restituisce il destinatario dell'email.
     * @return destinatario dell'email
     */
    public String getDestinatario() { 
        return destinatario; 
    }
    
    /**
     * @brief Restituisce la data di invio dell'email.
     * @return data di invio dell'email
     */
    public Date getDataInvio() { 
        return dataInvio; 
    }
    
    /**
     * @brief Restituisce una rappresentazione testuale dell'email.
     * La stringa contiene data, destinatario e oggetto.
     * 
     * @return stringa descrittiva dell'email
     */
    @Override
    public String toString() {
        return dataInvio + " - A: " + destinatario + " - Oggetto: " + oggetto;
    }
}