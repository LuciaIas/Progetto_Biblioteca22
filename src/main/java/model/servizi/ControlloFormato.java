/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.servizi;

import java.security.MessageDigest;

/**
 *
 * @author nicol
 */
public class ControlloFormato {
    
    public static boolean CheckPasswordFormat(String password){
    
        if(password==null)
            return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    
        return password.matches(regex);
    }
    
    public static boolean ControlloFormato(String email){
        if(email.matches("[^@]{1,200}@.+{0,20}\\..+{0,10}"))
            return true;
        else return false;
                 
    }
    
    
}
