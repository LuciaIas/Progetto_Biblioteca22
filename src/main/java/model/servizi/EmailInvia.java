/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.servizi;

import java.time.LocalDate;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javafx.application.Platform;
import model.Configurazione;

/**
 * @brief Classe di utilità per inviare email tramite SMTP.
 * Fornisce metodi statici per inviare email singole e avvisi di mancata restituzione
 * dei libri della biblioteca. La classe utilizza le credenziali definite nella classe
 * Configurazione.
 * 
 * Supporta configurazioni di test modificabili tramite host e porta SMTP.
 * L'invio di avvisi avviene in modalità asincrona tramite thread.
 * 
 * @author gruppo22
 */
public class EmailInvia {   
    /** Username email mittente, letto dalla configurazione */    
    private static final String username = Configurazione.getEmailUsername();
    /** Password email mittente, letta dalla configurazione */
    private static final String password = Configurazione.getPasswordSender();    
    
// CONFIGURAZIONE SERVER     
    /** Host SMTP di default */
    private static String SMTP_HOST = "smtp.gmail.com";
     /** Porta SMTP di default */
    private static String SMTP_PORT = "587";   
    /** Flag per l'esito dell'invio email */
    private static boolean ret = false;
    
    /**
     * @brief Permette di modificare le configurazioni SMTP per i test.
     * 
     * @param host nuovo host SMTP
     * @param port nuova porta SMTP
     */
    static void setTestConfiguration(String host, String port) {
        SMTP_HOST = host;
        SMTP_PORT = port;
    }

      /**
     * @brief Invia un'email singola al destinatario specificato.
     * Il metodo costruisce la sessione SMTP, crea il messaggio e lo invia.
     * La configurazione cambia automaticamente se si utilizza un server locale.
     * 
     * @param recipientEmail email del destinatario
     * @param subject oggetto dell'email
     * @param body corpo del messaggio
     */
    public static void inviaEmail(String recipientEmail, String subject, String body) {    
        Properties props = new Properties();// Oggetto per configurazione SMTP
        props.put("mail.smtp.auth", "true");// Abilito autenticazione
        props.put("mail.smtp.starttls.enable", "true");// Abilito STARTTLS
        // Qui usiamo le variabili al posto di stringhe fisse
        props.put("mail.smtp.host", SMTP_HOST); 
        props.put("mail.smtp.port", SMTP_PORT);        
        // Questo serve per evitare errori di certificati nei test
        if (SMTP_HOST.equals("localhost")) {// Se si usa un server locale
            props.put("mail.smtp.ssl.trust", "*");// Accetto tutti i certificati
            props.put("mail.smtp.starttls.required", "false");// Disabilito TLS obbligatorio
            props.put("mail.smtp.checkserveridentity", "false");// Disabilito verifica identità server
        } else {// Se si usa un server reale (Gmail)
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Protocollo TLS       
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");// Host attendibile
        }        
        // Creazione della sessione con autenticazione
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            // Creazione del messaggio email da inviare
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Mittente
            message.setRecipients(
                Message.RecipientType.TO, 
                InternetAddress.parse(recipientEmail)       // Destinatario
            );
            message.setSubject(subject);                    // Oggetto
            message.setText(body);                          // Corpo del testo        
            Transport.send(message);
        } catch (MessagingException e) {
            // L'eccezione è silenziata
        }
    }
    
    /**
     * @brief Invia un avviso di mancata restituzione di un libro.
     * L'invio avviene in modalità asincrona tramite thread. Se il titolo del libro
     * è noto, viene incluso nel messaggio; altrimenti il messaggio è generico.
     * 
     * @param recipientEmail email del destinatario
     * @param titolo titolo del libro (può essere null)
     * @param nome nome del destinatario
     * @param cognome cognome del destinatario
     * @param inizioPrestito data di inizio prestito
     */
    public static void inviaAvviso(String recipientEmail,String titolo,String nome,String cognome,LocalDate inizioPrestito){        
       Thread t = new Thread(() -> {
        try {       
            if(titolo!=null)
            EmailInvia.inviaEmail(recipientEmail, "Mancata Restituzione del libro", "Carissimo "+nome+" "+cognome+
                    " le chiedo gentilmente di restituire la copia di "+titolo+" presa in prestito il "+inizioPrestito);
            else
                EmailInvia.inviaEmail(recipientEmail, "Mancata Restituzione del/dei libro/i", "Carissimo "+nome+" "+cognome+
                        " le chiedo gentilmente di restituire la/le copia/copie che ha preso in prestito dalla nostra biblioteca ");
            
            ret=true;// Segnalazione del successo dell'invio 
        } catch (Exception ev) {
            ret=false;// In caso di errore nell'invio, il flag viene impostato a falso        
        }
    });
       t.start();
    }    
}