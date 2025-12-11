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


public class EmailInvia {
    
private static final String username = "progettoGruppo22@gmail.com";
private static final String password = "fzxw ejrj caqq huez"; 
private static boolean ret=false;// Indica se l'invio dell'email è andato a buon fine.

public static void inviaEmail(String recipientEmail, String subject, String body) {   

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");           // Autenticazione richiesta
        props.put("mail.smtp.starttls.enable", "true"); // Usa TLS per sicurezza
        props.put("mail.smtp.host", "smtp.gmail.com");  // Server SMTP di Gmail
        props.put("mail.smtp.port", "587");             // Porta per TLS
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");        
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
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