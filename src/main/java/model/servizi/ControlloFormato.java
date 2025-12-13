/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model.servizi;

import java.security.MessageDigest;

/**
 * @brief Classe di utilità per il controllo del formato di email e password.
 * 
 * Fornisce metodi statici per validare se una password rispetta i criteri di sicurezza
 * e se un indirizzo email è nel formato corretto.
 * 
 * Criteri password: almeno una maiuscola, una minuscola, un numero, un carattere speciale,
 * senza spazi e lunghezza minima di 8 caratteri.
 * 
 * Email: validazione semplice tramite espressione regolare.
 * 
 * @author gruppo22
 */
public class ControlloFormato {
    
     /**
     * @brief Controlla se una password rispetta il formato richiesto.
     * 
     * @param password password da controllare
     * @return true se la password rispetta i criteri di sicurezza, false altrimenti
     */
    public static boolean controlloFormatoPassword(String password){   
        if(password==null)
            return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"; // Espressione regolare per verificare il formato della password   
        return password.matches(regex);
    }
    
    /**
     * @brief Controlla se un indirizzo email ha un formato valido.
     * 
     * La validazione è semplice e basata su una regex che verifica la presenza
     * di un singolo '@' e di un dominio.
     * 
     * @param email indirizzo email da controllare
     * @return true se l'email è valida, false altrimenti
     */
    public static boolean controlloFormatoEmail(String email){  
        if (email == null)  // Evita NullPointerException se email è null
            return false;
        
        if(email.matches("[^@]{1,200}@.+{0,20}\\..+{0,10}"))// Espressione regolare per validazione semplice dell'email
            return true;
        
        else return false;                
    }
        
}
