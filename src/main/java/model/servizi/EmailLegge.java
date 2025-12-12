/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.servizi;

/**
 *
 * @authorgruppo22
 */
import model.dataclass.EmailInfo;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import model.Configurazione;

public class EmailLegge {

    private static final String username = Configurazione.getEmailUsername();  
    private static final String password = Configurazione.getPasswordReceiver(); 

    private static String IMAP_HOST = "imap.gmail.com";
    
public static ArrayList<EmailInfo> leggiPostaInviata() {
    
    ArrayList<EmailInfo> listaEmail = new ArrayList<>(); 
    try {     
        // Configurazione del protocollo IMAPS (necessario per Gmail)
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.trust", "imap.gmail.com");

        // Crea una sessione senza autenticatore (IMAPS usa lo Store)
        Session session = Session.getInstance(props);
        
        // Connessione alla casella di posta tramite protocollo IMAPS
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", username, password); 

        // Apertura della cartella "Sent Mail" (nome inglese)
        Folder sentFolder = store.getFolder("[Gmail]/Sent Mail");
        
        // In alcuni account Gmail la cartella può avere nome diverso (es. italiano)
        if (!sentFolder.exists()) sentFolder = store.getFolder("[Gmail]/Posta inviata");
        
        sentFolder.open(Folder.READ_ONLY); // Apertura in sola lettura
    
        // Determina quanti messaggi leggere (max 40 più recenti)
        int totaleMessaggi = sentFolder.getMessageCount();
        int numeroDaLeggere = 40; 
        int start = Math.max(1, totaleMessaggi - numeroDaLeggere + 1);
        int end = totaleMessaggi;
   
        // Recupera i messaggi solo se la cartella non è vuota
        Message[] messages;
        if (totaleMessaggi > 0) {
            messages = sentFolder.getMessages(start, end);
        } else {
            messages = new Message[0];
        }

        // Ordina cronologicamente dal più recente al più vecchio
        for (int i = messages.length - 1; i >= 0; i--) {
            Message msg = messages[i];
            
            String sogg = msg.getSubject();
            String dest = "Sconosciuto";
            // Estrae il destinatario principale, se presente
            try {     
                if (msg.getRecipients(Message.RecipientType.TO) != null) {
                    dest = msg.getRecipients(Message.RecipientType.TO)[0].toString();
                }
            } catch (Exception ex) { 
            // Caso raro: destinatario non leggibile o formattazione anomala
            }
      
            // Crea un oggetto semplificato con le info principali dell'email
            EmailInfo info = new EmailInfo(sogg, dest, msg.getSentDate());
            listaEmail.add(info);
        }
      
        // Chiude cartella e connessione
        sentFolder.close(false);
        store.close();

    } catch (Exception e) {
        // In caso di errori di connessione o permessi
        e.printStackTrace();
    }  
    return listaEmail;
}

}