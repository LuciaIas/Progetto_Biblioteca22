/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.time.Year;
import java.util.List;

/**
 * @brief Classe che rappresenta un libro della biblioteca.
 * 
 * La classe contiene informazioni principali sul libro, come:
 * - ISBN
 * - Titolo
 * - Editore
 * - Lista degli autori
 * - Anno di pubblicazione
 * - Numero di copie disponibili
 * - Percorso dell'immagine di copertina
 * 
 * Implementa Comparable per permettere l'ordinamento dei libri per titolo.
 * 
 * @author gruppo22
 */
public class Libro implements Comparable<Libro>{
    
    private String isbn;
    private String titolo;
    private String editore;
    private List<Autore> autori;
    private Year anno_pubblicazione;
    private int numero_copieDisponibili;
    private String url; //percorso dell'immagine di copertina

   /**
     * @brief Costruisce un nuovo oggetto Libro.
     * 
     * @param isbn codice ISBN del libro
     * @param titolo titolo del libro
     * @param editore editore del libro
     * @param autori lista degli autori del libro
     * @param anno_pubblicazione anno di pubblicazione
     * @param numero_copieDisponibili numero di copie disponibili
     * @param url percorso dell'immagine di copertina
     */
    public Libro(String isbn, String titolo, String editore, List<Autore> autori, Year anno_pubblicazione, int numero_copieDisponibili, String url) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.editore = editore;
        this.autori = autori;
        this.anno_pubblicazione = anno_pubblicazione;
        this.numero_copieDisponibili = numero_copieDisponibili;
        this.url = url;
    }

    
    /**
     * @brief Restituisce l'ISBN del libro.
     * @return codice ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @brief Imposta l'ISBN del libro.
     * @param isbn nuovo codice ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

   /**
     * @brief Restituisce il titolo del libro.
     * @return titolo del libro
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * @brief Imposta il titolo del libro.
     * @param titolo nuovo titolo
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

   /**
     * @brief Restituisce l'editore del libro.
     * @return editore del libro
     */
    public String getEditore() {
        return editore;
    }

    /**
     * @brief Imposta l'editore del libro.
     * @param editore nuovo editore
     */
    public void setEditore(String editore) {
        this.editore = editore;
    }

    
    /**
     * @brief Restituisce la lista degli autori del libro.
     * @return lista degli autori
     */
    public List<Autore> getAutori() {
        return autori;
    }

   /**
     * @brief Imposta la lista degli autori del libro.
     * @param autori nuova lista di autori
     */
    public void setAutori(List<Autore> autori) {
        this.autori = autori;
    }

   /**
     * @brief Restituisce l'anno di pubblicazione del libro.
     * @return anno di pubblicazione
     */
    public Year getAnno_pubblicazione() {
        return anno_pubblicazione;
    }

    /**
     * @brief Imposta l'anno di pubblicazione del libro.
     * @param anno_pubblicazione nuovo anno di pubblicazione
     */
    public void setAnno_pubblicazione(Year anno_pubblicazione) {
        this.anno_pubblicazione = anno_pubblicazione;
    }

    /**
     * @brief Restituisce il numero di copie disponibili.
     * @return numero di copie disponibili
     */
    public int getNumero_copieDisponibili() {
        return numero_copieDisponibili;
    }

    /**
     * @brief Imposta il numero di copie disponibili.
     * @param numero_copieDisponibili nuovo numero di copie
     */
    public void setNumero_copieDisponibili(int numero_copieDisponibili) {
        this.numero_copieDisponibili = numero_copieDisponibili;
    }

   /**
     * @brief Restituisce il percorso dell'immagine di copertina.
     * @return percorso dell'immagine
     */
    public String getUrl() {
        return url;
    }

   /**
     * @brief Imposta il percorso dell'immagine di copertina.
     * @param url nuovo percorso dell'immagine
     */
    public void setUrl(String url) {
        this.url = url;
    }

   /**
     * @brief Restituisce una rappresentazione testuale del libro.
     * @return stringa descrittiva del libro
     */
    @Override
    public String toString() {
        return "Libro{" + "isbn=" + isbn + ", titolo=" + titolo + ", editore=" + editore + ", autori=" + autori + ", anno_pubblicazione=" + anno_pubblicazione + ", numero_copieDisponibili=" + numero_copieDisponibili + ", url=" + url + '}';
    }

    
    /**
     * @brief Confronta due libri in base al titolo.
     * 
     * Metodo necessario per implementare Comparable.
     * 
     * @param o libro da confrontare
     * @return valore negativo se questo titolo precede l'altro, 0 se uguale, positivo se segue
     */
    @Override
    public int compareTo(Libro o) {
        return titolo.compareTo(o.titolo);
    }
      
}
