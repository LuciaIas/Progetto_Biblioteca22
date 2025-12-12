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

import java.time.LocalDate;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import model.Configurazione;


public class EmailInvia {
    
private static final String username = Configurazione.getEmailUsername();
private static final String password = Configurazione.getPasswordSender(); 
    
    // CONFIGURAZIONE SERVER (Modificata per essere testabile)
    // Non sono più stringhe fisse dentro il metodo, ma variabili statiche
    private static String SMTP_HOST = "smtp.gmail.com";
    private static String SMTP_PORT = "587";
    
    // Metodo per i TEST (Package-private, non visibile da fuori)
    static void setTestConfiguration(String host, String port) {
        SMTP_HOST = host;
        SMTP_PORT = port;
    }

    private static boolean ret = false;

    public static void inviaEmail(String recipientEmail, String subject, String body) {    
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        // QUI USIAMO LE VARIABILI INVECE DELLE STRINGHE FISSE
        props.put("mail.smtp.host", SMTP_HOST); 
        props.put("mail.smtp.port", SMTP_PORT);
        
        // Questo serve per evitare errori di certificati nei test
        if (SMTP_HOST.equals("localhost")) {
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.starttls.required", "false");
            props.put("mail.smtp.checkserveridentity", "false");
        } else {
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");        
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        }
        // Autenticatore per fornire username e password alla sessione
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
            //e.printStackTrace();
            // L'eccezione è silenziata, ma si potrebbe loggare in ambiente di produzione
        }
    }
    
    public static boolean inviaAvviso(String recipientEmail,String titolo,String nome,String cognome,LocalDate inizioPrestito){
        //Invia un avviso di mancata restituzione del libro in modo asincrono.
        
        // Avvio dell'invio email su un nuovo thread per evitare blocchi dell'interfaccia o della logica principale
        new Thread(() -> {
        try {       
            // Email costruita diversamente se è noto il titolo del libro o se si tratta di più libri non restituiti
            if(titolo!=null)
            EmailInvia.inviaEmail(recipientEmail, "Mancata Restituzione del libro", "Carissimo "+nome+" "+cognome+
                    " le chiedo gentilmente di restituire la copia di "+titolo+" presa in prestito il "+inizioPrestito);
            else
                EmailInvia.inviaEmail(recipientEmail, "Mancata Restituzione del/dei libro/i", "Carissimo "+nome+" "+cognome+
                        " le chiedo gentilmente di restituire la/le copia/copie che ha preso in prestito dalla nostra biblioteca ");
            
            ret=true;// Segnalazione del successo dell'invio (non thread-safe)
        } catch (Exception ev) {
            ret=false;// In caso di errore nell'invio, il flag viene impostato a falso
            //ev.printStackTrace();
           
        }
    }).start(); 
        
        return ret;
    }
    
}