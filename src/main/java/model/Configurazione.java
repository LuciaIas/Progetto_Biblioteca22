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
 * @brief Classe di utilità per leggere le configurazioni dell'applicazione.
 *
 * La classe fornisce metodi statici per ottenere:
 * - credenziali email
 * - limiti massimi di utenti, libri, prestiti, autori e scrittori
 * - orari di apertura/chiusura
 * - durata della sessione
 * 
 * @author gruppo22
 */
public class Configurazione {
    private static final Properties properties = new Properties();

    static {
        // Uso getResourceAsStream perché il file è dentro "src/main/resources"
        try (InputStream input = Configurazione.class.getResourceAsStream("/config.properties")) {
            if (input != null) {
                properties.load(input);
            } 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * @brief Restituisce lo username dell'account email per l'invio.
     * @return username dell'email
     */
    public static String getEmailUsername() {
        return properties.getProperty("mail.username");
    }
    
    /**
     * @brief Restituisce la password dell'account email per l'invio.
     * @return password dell'email mittente
     */
    public static String getPasswordSender() {
        return properties.getProperty("mail.password.sender");
    }
    
    /**
     * @brief Restituisce la password dell'account email per la ricezione.
     * @return password dell'email destinatario
     */
    public static String getPasswordReceiver() {
        return properties.getProperty("mail.password.receiver");
    }

     /**
     * @brief Restituisce il numero massimo di utenti consentiti.
     * @return numero massimo di utenti
     */
    public static int getMaxUsers() {
        return parseInteger("app.max_users", 5000);
    }
    
    /**
     * @brief Restituisce il numero massimo di libri consentiti.
     * @return numero massimo di libri
     */
    public static int getMaxBooks() {
        return parseInteger("app.max_books", 2000);
    }
    
     /**
     * @brief Restituisce il numero massimo di prestiti consentiti.
     * @return numero massimo di prestiti
     */
    public static int getMaxLoans() {
        return parseInteger("app.max_loans", 10000);
    }
    
      /**
     * @brief Restituisce il numero massimo di autori consentiti.
     * @return numero massimo di autori
     */
    public static int getMaxAuthors() {
        return parseInteger("app.max_authors", 1000);
    }
    
     /**
     * @brief Restituisce il numero massimo di scrittori consentiti.
     * @return numero massimo di scrittori
     */
    public static int getMaxWrited() {
        return parseInteger("app.max_writers", 5000);
    }
    
      /**
     * @brief Restituisce l'orario di apertura della biblioteca.
     * @return array di 2 interi: [ora, minuto]
     */
    public static int[] getTimeOpen(){
        int[] time = new int[2];
        time[0] = parseInteger("time.open.hour",7);
        time[1] = parseInteger("time.open.minute",0);
        return time;
    }
    
    /**
     * @brief Restituisce l'orario di chiusura della biblioteca.
     * @return array di 2 interi: [ora, minuto]
     */
    public static int[] getTimeClose(){
        int[] time = new int[2];
        time[0] = parseInteger("time.close.hour",20);
        time[1] = parseInteger("time.close.minute",0);
        return time;
    }
    
     /**
     * @brief Restituisce la durata della sessione utente in ore.
     * @return durata della sessione
     */
    public static int getSessionDuration() {
        return parseInteger("time.session", 1);
    }
    
   /**
     * @brief Converte il valore di una proprietà in intero, usando un valore di default
     * se la proprietà non è definita o è nulla.
     * 
     * @param key chiave della proprietà
     * @param defaultValue valore di default se la proprietà non esiste
     * @return valore intero della proprietà o default
     */
    private static int parseInteger(String key, int defaultValue) {
        String val = properties.getProperty(key);
        if (val != null)
                return Integer.parseInt(val);
            
        return defaultValue;
    }
}
