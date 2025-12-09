/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

/**
 *
 * @author lucia
 */
import java.util.Date;

public class EmailInfo {
    private String oggetto;
    private String destinatario;
    private Date dataInvio;

    public EmailInfo(String oggetto, String destinatario, Date dataInvio) {
        this.oggetto = oggetto;
        this.destinatario = destinatario;
        this.dataInvio = dataInvio;
    }

    // Getter per leggerli dopo
    public String getOggetto() { return oggetto; }
    public String getDestinatario() { return destinatario; }
    public Date getDataInvio() { return dataInvio; }
    
    @Override
    public String toString() {
        return dataInvio + " - A: " + destinatario + " - Oggetto: " + oggetto;
    }
}