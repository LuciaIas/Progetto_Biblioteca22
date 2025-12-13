/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.util.ArrayList;

/**
 * @brief Classe che rappresenta un utente della biblioteca.
 * 
 * Contiene informazioni principali sull'utente:
 * matricola, nome, cognome, email e stato di blocco.
 * 
 * @author gruppo22
 */
public class Utente {
    
    private String matricola;
    private String nome;
    private String cognome;
    private String mail;
    private boolean bloccato;
   
   /**
     * @brief Costruisce un nuovo oggetto Utente.
     * 
     * @param matricola identificativo univoco dell'utente
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param mail email dell'utente
     * @param bloccato stato di blocco dell'utente (true se bloccato)
     */
    public Utente(String matricola, String nome, String cognome, String mail,boolean bloccato) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.bloccato = bloccato;
    }

   /**
     * @brief Restituisce la lista degli utenti bloccati da una lista di utenti.
     * 
     * @param utenti lista di utenti da filtrare
     * @return lista contenente solo gli utenti bloccati
     */
    public static ArrayList<Utente> getUtentiBlackListed(ArrayList<Utente> utenti){    
        ArrayList<Utente> us = new ArrayList<>();
        for(Utente u : utenti)
            if(u.isBloccato())
                us.add(u);
        return us;   
    }
    
   /**
     * @brief Restituisce la matricola dell'utente.
     * @return matricola dell'utente
     */
    public String getMatricola() {
        return matricola;
    }

    /**
     * @brief Verifica se l'utente è bloccato.
     * @return true se l'utente è bloccato, false altrimenti
     */
    public boolean isBloccato() {
        return bloccato;
    }

     /**
     * @brief Imposta lo stato di blocco dell'utente.
     * @param bloccato true per bloccare l'utente, false per sbloccarlo
     */
    public void setBloccato(boolean bloccato) {
        this.bloccato = bloccato;
    }

    
        /**
     * @brief Imposta la matricola dell'utente.
     * @param matricola nuova matricola
     */
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }
    
    /**
     * @brief Restituisce il nome dell'utente.
     * @return nome dell'utente
     */
    public String getNome() {
        return nome;
    }
    
   /**
     * @brief Imposta il nome dell'utente.
     * @param nome nuovo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @brief Restituisce il cognome dell'utente.
     * @return cognome dell'utente
     */
    public String getCognome() {
        return cognome;
    }

   /**
     * @brief Imposta il cognome dell'utente.
     * @param cognome nuovo cognome
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @brief Restituisce l'email dell'utente.
     * @return email dell'utente
     */
    public String getMail() {
        return mail;
    }

    /**
     * @brief Imposta l'email dell'utente.
     * @param mail nuova email
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @brief Restituisce una rappresentazione testuale dell'utente.
     * @return stringa descrittiva dell'utente
     */
    @Override
    public String toString() {
        return "Utente{" + "matricola=" + matricola + ", nome=" + nome + ", cognome=" + cognome + ", mail=" + mail + '}';
    }
    
}
