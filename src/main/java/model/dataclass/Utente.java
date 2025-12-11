/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.util.ArrayList;

/**
 *
 * @author gruppo22
 */

public class Utente {
    
    private String matricola;
    private String nome;
    private String cognome;
    private String mail;
    private boolean bloccato;
   

    public Utente(String matricola, String nome, String cognome, String mail,boolean bloccato) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.bloccato = bloccato;
    }

    //Getter e Setter
    
    public static ArrayList<Utente> getUtentiBlackListed(ArrayList<Utente> utenti){
    
        ArrayList<Utente> us = new ArrayList<>();
        for(Utente u : utenti)
            if(u.isBloccato())
                us.add(u);
        return us;
    
    }
    
    public String getMatricola() {
        return matricola;
    }

    public boolean isBloccato() {
        return bloccato;
    }

    public void setBloccato(boolean bloccato) {
        this.bloccato = bloccato;
    }

    
    
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "Utente{" + "matricola=" + matricola + ", nome=" + nome + ", cognome=" + cognome + ", mail=" + mail + '}';
    }
    
}
