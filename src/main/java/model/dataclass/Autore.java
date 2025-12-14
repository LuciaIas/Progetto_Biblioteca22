/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.time.LocalDate;

/**
 * @brief Classe che rappresenta un autore con le sue informazioni principali.
 * 
 * Ogni autore ha un identificativo univoco, nome, cognome, numero di opere scritte
 * e data di nascita.
 * 
 * @author gruppo22
 */
public class Autore {
    private int id;
    private String nome;
    private String cognome;
    private int opere_scritte;
    private LocalDate data_nascita;

    /**
     * @brief Costruisce un nuovo oggetto Autore.
     * 
     * @param nome nome dell'autore
     * @param cognome cognome dell'autore
     * @param opere_scritte numero di opere scritte dall'autore
     * @param data_nascita data di nascita dell'autore
     */
    public Autore(String nome, String cognome, int opere_scritte, LocalDate data_nascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.opere_scritte = opere_scritte;
        this.data_nascita = data_nascita;
    }

    /**
     * @brief Restituisce l'identificativo dell'autore.
     * @return id dell'autore
     */
    public int getId() {
        return id;
    }

    /**
     * @brief Imposta l'identificativo dell'autore.
     * @param id nuovo id dell'autore
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @brief Restituisce il nome dell'autore.
     * @return nome dell'autore
     */
    public String getNome() {
        return nome;
    }

    /**
     * @brief Imposta il nome dell'autore.
     * @param nome nuovo nome dell'autore
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @brief Restituisce il cognome dell'autore.
     * @return cognome dell'autore
     */
    public String getCognome() {
        return cognome;
    }
    
   /**
     * @brief Imposta il cognome dell'autore.
     * @param cognome nuovo cognome dell'autore
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
     /**
     * @brief Restituisce il numero di opere scritte dall'autore.
     * @return numero di opere scritte
     */
    public int getOpere_scritte() {
        return opere_scritte;
    }
   
   /**
     * @brief Imposta il numero di opere scritte dall'autore.
     * @param opere_scritte nuovo numero di opere
     */
    public void setOpere_scritte(int opere_scritte) {
        this.opere_scritte = opere_scritte;
    }

    /**
     * @brief Restituisce la data di nascita dell'autore.
     * @return data di nascita
     */
    public LocalDate getData_nascita() {
        return data_nascita;
    }
    
    /**
     * @brief Imposta la data di nascita dell'autore.
     * @param data_nascita nuova data di nascita
     */
    public void setData_nascita(LocalDate data_nascita) {
        this.data_nascita = data_nascita;
    }

     /**
     * @brief Restituisce una rappresentazione testuale dell'autore.
     * @return stringa descrittiva dell'autore
     */
    @Override
    public String toString() {
        return "Autore{" + "nome=" + nome + ", cognome=" + cognome + ", opere_scritte=" + opere_scritte + ", data_nascita=" + data_nascita + '}';
    }   
}
