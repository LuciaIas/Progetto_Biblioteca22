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

import java.security.MessageDigest;

/**
 *
 * @author gruppo22
 */


public class ControlloFormato {
    
    public static boolean controlloFormatoPassword(String password){
    
        if(password==null)
            return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"; // Espressione regolare per verificare il formato della password
    
        return password.matches(regex);
    }
    
    public static boolean controlloFormatoEmail(String email){
   
        if (email == null)  // Evita NullPointerException se email è null
            return false;
        
        if(email.matches("[^@]{1,200}@.+{0,20}\\..+{0,10}"))// Espressione regolare per validazione semplice dell'email
            return true;
        
        else return false;
                 
    }
    
    
}
