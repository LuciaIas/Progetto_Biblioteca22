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
 *
 * @author nicol
 */
public class Prestito {
    private String isbn;
    private String matricola;
    private LocalDate inizio_prestito;
    private LocalDate restituzione;
    private LocalDate data_scadenza;
    private Stato stato;

    public Prestito(String isbn, String matricola, LocalDate inizio_prestito, LocalDate restituzione, Stato stato,LocalDate data_scadenza) {
        this.isbn = isbn;
        this.matricola = matricola;
        this.inizio_prestito = inizio_prestito;
        this.restituzione = restituzione;
        this.stato = stato;
        this.data_scadenza = data_scadenza;
    }

    
    public static ArrayList<Prestito> getPrestitiByStato(ArrayList<Prestito> p, Stato s){
    
       ArrayList<Prestito> ret = new ArrayList<>();
       
       for(Prestito p1 : p)
           if(p1.getStato().equals(s))
               ret.add(p1);
        return ret;
    }
    
    
    
    
    public LocalDate getData_scadenza() {
        return data_scadenza;
    }

    public void setData_scadenza(LocalDate data_scadenza) {
        this.data_scadenza = data_scadenza;
    }

    
    
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public LocalDate getInizio_prestito() {
        return inizio_prestito;
    }

    public void setInizio_prestito(LocalDate inizio_prestito) {
        this.inizio_prestito = inizio_prestito;
    }

    public LocalDate getRestituzione() {
        return restituzione;
    }

    public void setRestituzione(LocalDate restituzione) {
        this.restituzione = restituzione;
    }

  

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {
        return "Prestito{" + "isbn=" + isbn + ", matricola=" + matricola + ", inizio_prestito=" + inizio_prestito + ", restituzione=" + restituzione + ", data_scadenza=" + data_scadenza + ", stato=" + stato + '}';
    }

    
    
    
    
}
