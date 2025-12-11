/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author gruppo22
 */

public class Configurazione {
    private static final Properties properties = new Properties();

    static {
        // Usa getResourceAsStream perché il file è dentro "src/main/resources"
        try (InputStream input = Configurazione.class.getResourceAsStream("/config.properties")) {
            if (input != null) {
                properties.load(input);
            } 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // --- EMAIL (Stringhe) ---
    
    public static String getEmailUsername() {
        return properties.getProperty("mail.username");
    }

    public static String getPasswordSender() {
        return properties.getProperty("mail.password.sender");
    }

    public static String getPasswordReceiver() {
        return properties.getProperty("mail.password.receiver");
    }

    // --- LIMITI (Interi) ---
    
    public static int getMaxUsers() {
        return parseInteger("app.max_users", 5000);
    }
    
    public static int getMaxBooks() {
        return parseInteger("app.max_books", 2000);
    }
    
    public static int getMaxLoans() {
        return parseInteger("app.max_loans", 10000);
    }
    
    public static int getMaxAuthors() {
        return parseInteger("app.max_authors", 1000);
    }
    
    public static int getMaxWrited() {
        return parseInteger("app.max_writers", 5000);
    }
    
    public static int[] getTimeOpen(){
        int[] time = new int[2];
        time[0] = parseInteger("time.open.hour",7);
        time[1] = parseInteger("time.open.minute",0);
        return time;
    }
    
    public static int[] getTimeClose(){
        int[] time = new int[2];
        time[0] = parseInteger("time.close.hour",20);
        time[1] = parseInteger("time.close.minute",0);
        return time;
    }

    public static int getSessionDuration() {
        return parseInteger("time.session", 1);
    }

    private static int parseInteger(String key, int defaultValue) {
        String val = properties.getProperty(key);
        if (val != null)
                return Integer.parseInt(val);
            
        return defaultValue;
    }
}
