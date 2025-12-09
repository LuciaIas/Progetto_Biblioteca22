/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.time.Year;
import java.util.List;

/**
 *
 * @author lucia
 */
public class Libro implements Comparable<Libro>{
       // lista degli autori (nome e cognome), anno di pubblicazione, codice identificativo univoco (ISBN) e numero di copie disponibili.
    private String isbn;
    private String titolo;
    private String editore;
    private List<Autore> autori;
    private Year anno_pubblicazione;
    private int numero_copieDisponibili;
    private String url;

    public Libro(String isbn, String titolo, String editore, List<Autore> autori, Year anno_pubblicazione, int numero_copieDisponibili, String url) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.editore = editore;
        this.autori = autori;
        this.anno_pubblicazione = anno_pubblicazione;
        this.numero_copieDisponibili = numero_copieDisponibili;
        this.url = url;
    }


    
    
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getEditore() {
        return editore;
    }

    public void setEditore(String editore) {
        this.editore = editore;
    }

    public List<Autore> getAutori() {
        return autori;
    }

    public void setAutori(List<Autore> autori) {
        this.autori = autori;
    }

    public Year getAnno_pubblicazione() {
        return anno_pubblicazione;
    }

    public void setAnno_pubblicazione(Year anno_pubblicazione) {
        this.anno_pubblicazione = anno_pubblicazione;
    }

    public int getNumero_copieDisponibili() {
        return numero_copieDisponibili;
    }

    public void setNumero_copieDisponibili(int numero_copieDisponibili) {
        this.numero_copieDisponibili = numero_copieDisponibili;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Libro{" + "isbn=" + isbn + ", titolo=" + titolo + ", editore=" + editore + ", autori=" + autori + ", anno_pubblicazione=" + anno_pubblicazione + ", numero_copieDisponibili=" + numero_copieDisponibili + ", url=" + url + '}';
    }

    @Override
    public int compareTo(Libro o) {
        return titolo.compareTo(o.titolo);
    }
    
    
    
    
}
