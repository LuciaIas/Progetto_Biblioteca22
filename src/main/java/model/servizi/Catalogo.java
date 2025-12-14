/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model.servizi;

import model.dataclass.Libro;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import model.dataclass.Autore;

/**
 * @brief Classe che rappresenta il catalogo dei libri della biblioteca.
 * La classe gestisce una lista di oggetti  Libro e fornisce metodi
 * per aggiungere, rimuovere, cercare e ordinare i libri.
 * Permette ricerche per ISBN, titolo e autore, e supporta l'ordinamento
 * alfabetico dei titoli ignorando maiuscole e minuscole.
 * 
 * @author gruppo22
 */
public class Catalogo {
    private List<Libro> libri;

     /**
     * @brief Costruisce un nuovo catalogo vuoto.
     */
    public Catalogo() {
        this.libri = new ArrayList<>();
    }

    /**
     * @brief Aggiunge un libro al catalogo.
     * @param libro oggetto Libro da aggiungere
     */
    public void aggiungiLibro(Libro libro) {
        this.libri.add(libro);
    }

    /**
     * @brief Rimuove un libro dal catalogo.
     * @param libro oggetto Libro da rimuovere
     */
    public void rimuoviLibro(Libro libro) {
        this.libri.remove(libro);
    }

    /**
     * @brief Restituisce la lista dei libri presenti nel catalogo.
     * @return ArrayList di libri
     */
    public ArrayList<Libro> getLibri() {
        return (ArrayList<Libro>) libri;
    }

    /**
     * @brief Ordina i libri del catalogo in ordine alfabetico per titolo.
     * L'ordinamento ignora la differenza tra maiuscole e minuscole.
     */
    public void sort(){  
        libri.sort(new Comparator<Libro>() {
            @Override
            public int compare(Libro o1, Libro o2) {
                return o1.getTitolo().toUpperCase().compareTo(o2.getTitolo().toUpperCase()); 
            }
        });
        
    }
    
    /**
     * @brief Cerca un libro nel catalogo in base all'ISBN.
     * @param isbn codice ISBN del libro da cercare
     * @return oggetto Libro corrispondente all'ISBN, oppure null se non trovato
     */
    public Libro cercaPerIsbn(String isbn) {
        for (Libro l : libri) {
            if (l.getIsbn().equals(isbn)) {
                return l;
            }
        }
        return null;
    }

    /**
     * @brief Cerca libri che contengono una specifica stringa nel titolo.
     * La ricerca non è case-sensitive.
     * 
     * @param titolo stringa da cercare nel titolo
     * @return lista di libri che contengono la stringa nel titolo
     */
    public List<Libro> cercaPerTitolo(String titolo) { 
        return libri.stream()
                .filter(l -> l.getTitolo().toLowerCase().contains(titolo.toLowerCase())) 
                .collect(Collectors.toList());
    }

    /**
     * @brief Cerca libri scritti da un determinato autore.
     * Confronta gli ID degli autori dei libri con l'autore fornito.
     * 
     * @param autore autore da cercare
     * @return lista di libri scritti dall'autore
     */
    public List<Libro> cercaPerAutore(Autore autore) {
        List<Libro> lib = new ArrayList<Libro>();
            for(Libro l : libri)
                for(Autore a : l.getAutori())
                    if(a.getId()==autore.getId())
                        lib.add(l);
            
           return lib;        
    }
    
}
