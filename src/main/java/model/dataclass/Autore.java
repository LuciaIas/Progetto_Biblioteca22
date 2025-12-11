/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

import java.time.LocalDate;

/**
 *
 * @author gruppo22
 */
public class Autore {
    
    private int id;
    private String nome;
    private String cognome;
    private int opere_scritte;
    private LocalDate data_nascita;

    public Autore(String nome, String cognome, int opere_scritte, LocalDate data_nascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.opere_scritte = opere_scritte;
        this.data_nascita = data_nascita;
    }

    //Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getOpere_scritte() {
        return opere_scritte;
    }

    public void setOpere_scritte(int opere_scritte) {
        this.opere_scritte = opere_scritte;
    }

    public LocalDate getData_nascita() {
        return data_nascita;
    }

    public void setData_nascita(LocalDate data_nascita) {
        this.data_nascita = data_nascita;
    }

    @Override
    public String toString() {
        return "Autore{" + "nome=" + nome + ", cognome=" + cognome + ", opere_scritte=" + opere_scritte + ", data_nascita=" + data_nascita + '}';
    }
    
    
    
    
}
