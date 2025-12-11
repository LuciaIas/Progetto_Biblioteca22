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

import model.dataclass.Libro;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Catalogo {

    private List<Libro> libri;// Lista che contiene gli oggetti Libro presenti nel catalogo

    public Catalogo() {
        this.libri = new ArrayList<>();
    }

    public void aggiungiLibro(Libro libro) {
        this.libri.add(libro);
    }

    public void rimuoviLibro(Libro libro) {
        this.libri.remove(libro);
    }

    public ArrayList<Libro> getLibri() {
        return (ArrayList<Libro>) libri;
    }

    public void sort(){  //Ordina i libri in ordine alfabetico in base al titolo.
        libri.sort(new Comparator<Libro>() {
            @Override
            public int compare(Libro o1, Libro o2) {
                return o1.getTitolo().toUpperCase().compareTo(o2.getTitolo().toUpperCase()); //La comparazione ignora maiuscole e minuscole.
            }
        });
        
    }
    
    public Libro cercaPerIsbn(String isbn) {
        for (Libro l : libri) {
            if (l.getIsbn().equals(isbn)) {
                return l;
            }
        }
        return null;
    }

    public List<Libro> cercaPerTitolo(String titolo) { //Cerca tutti i libri che contengono nel titolo la stringa specificata.
        return libri.stream()
                .filter(l -> l.getTitolo().toLowerCase().contains(titolo.toLowerCase())) //La ricerca non è case-sensitive.
                .collect(Collectors.toList());
    }

    public List<Libro> cercaPerAutore(String autore) {
        return libri.stream()
                .filter(l -> l.getAutori().contains(autore))
                .collect(Collectors.toList());
    }
    
}
