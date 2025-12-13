/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.servizi;

import model.dataclass.Stato;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @brief Rappresenta un prestito di un libro a un utente.
 * 
 * Contiene informazioni sul libro prestato, l'utente, le date del prestito,
 * la data di restituzione e lo stato del prestito.
 * 
 * @author gruppo22
 */
public class Prestito {
    private String isbn;
    private String matricola;
    private LocalDate inizio_prestito;
    private LocalDate restituzione;
    private LocalDate data_scadenza;
    private Stato stato;

    
    /**
     * @brief Costruttore della classe Prestito.
     * @param isbn ISBN del libro
     * @param matricola Matricola dell'utente
     * @param inizio_prestito Data di inizio prestito
     * @param restituzione Data di restituzione (null se non ancora restituito)
     * @param stato Stato del prestito
     * @param data_scadenza Data di scadenza del prestito
     */
    public Prestito(String isbn, String matricola, LocalDate inizio_prestito, LocalDate restituzione, Stato stato,LocalDate data_scadenza) {
        this.isbn = isbn;
        this.matricola = matricola;
        this.inizio_prestito = inizio_prestito;
        this.restituzione = restituzione;
        this.stato = stato;
        this.data_scadenza = data_scadenza;
    }

 
    /**
     * @brief Restituisce una lista di prestiti filtrata per stato.
     * @param p Lista di prestiti da filtrare
     * @param s Stato da cercare
     * @return Lista di prestiti che hanno lo stato specificato
     */
    public static ArrayList<Prestito> getPrestitiByStato(ArrayList<Prestito> p, Stato s){
       ArrayList<Prestito> ret = new ArrayList<>();
       for(Prestito p1 : p)
           if(p1.getStato().equals(s))
               ret.add(p1);
        return ret;
    }
    
      /**
     * @return Data di scadenza del prestito
     */
    public LocalDate getData_scadenza() {
        return data_scadenza;
    }
    
    /**
     * @param data_scadenza Imposta la data di scadenza del prestito
     */
    public void setData_scadenza(LocalDate data_scadenza) {
        this.data_scadenza = data_scadenza;
    }

   /** @return ISBN del libro prestato */
    public String getIsbn() {
        return isbn;
    }
    
    /** @param isbn Imposta l'ISBN del libro */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /** @return Matricola dell'utente */
    public String getMatricola() {
        return matricola;
    }
    
    /** @param matricola Imposta la matricola dell'utente */
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    /** @return Data di inizio prestito */
    public LocalDate getInizio_prestito() {
        return inizio_prestito;
    }

    /** @param inizio_prestito Imposta la data di inizio prestito */
    public void setInizio_prestito(LocalDate inizio_prestito) {
        this.inizio_prestito = inizio_prestito;
    }
    
    /** @return Data di restituzione, null se non ancora restituito */
    public LocalDate getRestituzione() {
        return restituzione;
    }

    /** @param restituzione Imposta la data di restituzione */
    public void setRestituzione(LocalDate restituzione) {
        this.restituzione = restituzione;
    }

    /** @return Stato del prestito */
    public Stato getStato() {
        return stato;
    }
    
    /** @param stato Imposta lo stato del prestito */
    public void setStato(Stato stato) {
        this.stato = stato;
    }

     /**
     * @brief Restituisce una rappresentazione testuale del prestito.
     * @return Stringa contenente tutte le informazioni del prestito
     */
    @Override
    public String toString() {
        return "Prestito{" + "isbn=" + isbn + ", matricola=" + matricola + ", inizio_prestito=" + inizio_prestito + ", restituzione=" + restituzione + ", data_scadenza=" + data_scadenza + ", stato=" + stato + '}';
    }
  
}
