package model.servizi;

import java.time.Year;
import model.dataclass.Autore;
import model.dataclass.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogoTest {
    private Catalogo catalogo;
    private Libro libro1;
    private Libro libro2;
    private Libro libro3;
    private Autore autore1;
    private Autore autore2;

    @BeforeEach
    public void setUp() {
        catalogo = new Catalogo();
        autore1 = new Autore("Dante","Alighieri",0,null); 
        autore1.setId(1); 
        autore2 = new Autore("William","Shakespeare",0,null);
        autore2.setId(2);
        libro1 = new Libro("1111111111111","Harry Potter","",null,Year.of(2005),0,"");
        libro1.setAutori(new ArrayList<>(Arrays.asList(autore1))); 
        libro2 = new Libro("2222222222222","Michele Spancher","",null,Year.of(2004),0,"");
        libro2.setAutori(new ArrayList<>(Arrays.asList(autore2)));
        libro3 = new Libro("3333333333333","La divina commedia","",null,Year.of(1993),0,"");       
        libro3.setAutori(new ArrayList<>(Arrays.asList(autore1)));       
               
    }

    @Test
    public void testAggiungiLibro() {
        catalogo.aggiungiLibro(libro1);       
        assertEquals(1, catalogo.getLibri().size(), "Il catalogo dovrebbe contenere 1 libro");
        assertEquals(libro1, catalogo.getLibri().get(0), "Il libro inserito deve corrispondere");
    }

    @Test
    public void testRimuoviLibro() {
        catalogo.aggiungiLibro(libro1);
        catalogo.aggiungiLibro(libro2);        
        catalogo.rimuoviLibro(libro1);        
        assertEquals(1, catalogo.getLibri().size(), "Dopo la rimozione dovrebbe restare 1 libro");
        assertEquals(libro2, catalogo.getLibri().get(0), "Il libro rimanente dovrebbe essere il secondo");
    }

    @Test
    public void testSortAlfabetico() {
        // Inseriamo in ordine sparso
        Libro lA = new Libro("1111111111112","Abc","",null,Year.of(2005),0,"");
        Libro lZ = new Libro("1111111111113","Zorro","",null,Year.of(2005),0,"");
        Libro lM = new Libro("1111111111114","MelaVerde","",null,Year.of(2005),0,"");
        catalogo.aggiungiLibro(lZ);
        catalogo.aggiungiLibro(lA);
        catalogo.aggiungiLibro(lM);       
        catalogo.sort();       
        List<Libro> libri = catalogo.getLibri();
        assertEquals("Abc", libri.get(0).getTitolo());
        assertEquals("MelaVerde", libri.get(1).getTitolo());
        assertEquals("Zorro", libri.get(2).getTitolo());
    }
    
    @Test
    public void testSortCaseInsensitive() {       
        Libro l1 = new Libro("1111111111115","banane","",null,Year.of(2005),0,"");
        Libro l2 = new Libro("1111111111116","ALBERO","",null,Year.of(2005),0,"");        
        catalogo.aggiungiLibro(l1);
        catalogo.aggiungiLibro(l2);        
        catalogo.sort();        
        assertEquals("ALBERO", catalogo.getLibri().get(0).getTitolo(), "ALBERO deve venire prima di banane");
    }

    @Test
    public void testCercaPerIsbn_Trovato() {
        catalogo.aggiungiLibro(libro1);
        catalogo.aggiungiLibro(libro2);
        Libro risultato = catalogo.cercaPerIsbn("2222222222222");         
        assertNotNull(risultato, "Il libro dovrebbe essere trovato");
        assertEquals("Michele Spancher", risultato.getTitolo());
    }

    @Test
    public void testCercaPerIsbn_NonTrovato() {
        catalogo.aggiungiLibro(libro1);       
        Libro risultato = catalogo.cercaPerIsbn("99999");        
        assertNull(risultato, "Se l'ISBN non esiste, deve ritornare null");
    }

    @Test
    public void testCercaPerTitolo_Parziale() {
        catalogo.aggiungiLibro(libro1); 
        catalogo.aggiungiLibro(libro2); 
        catalogo.aggiungiLibro(libro3); 
        // Cerchiamo "Harry"
        List<Libro> risultati = catalogo.cercaPerTitolo("Michele");
        assertEquals(1, risultati.size(), "Dovrebbe trovare 1 libro di MaicolTambs");
    }

    @Test
    public void testCercaPerTitolo_CaseInsensitive() {
        catalogo.aggiungiLibro(libro2); // "Il Signore degli Anelli"
        // Cerchiamo "signore" tutto minuscolo
        List<Libro> risultati = catalogo.cercaPerTitolo("MicHELE");
        assertEquals(1, risultati.size(), "La ricerca deve ignorare maiuscole/minuscole");
        assertEquals(libro2, risultati.get(0));
    }
    
    @Test
    public void testCercaPerTitolo_NessunRisultato() {
        catalogo.aggiungiLibro(libro1);
        List<Libro> risultati = catalogo.cercaPerTitolo("Don Chisciotte");       
        assertTrue(risultati.isEmpty(), "La lista deve essere vuota se non trova nulla");
    }

    @Test
    public void testCercaPerAutore() {
        catalogo.aggiungiLibro(libro1); 
        catalogo.aggiungiLibro(libro2); 
        catalogo.aggiungiLibro(libro3); 
        // Cerchiamo tutti i libri di Rowling (ID 1)
        List<Libro> libriShake = catalogo.cercaPerAutore(autore1);
        assertEquals(2, libriShake.size(), "Dovrebbe trovare 2 libri per l'autore 1");       
        // Verifichiamo che siano quelli giusti
        assertTrue(libriShake.contains(libro1));
        assertTrue(libriShake.contains(libro3));
        assertFalse(libriShake.contains(libro2));
    }
    
    @Test
    public void testCercaPerAutore_NessunLibro() {
        catalogo.aggiungiLibro(libro2);         
        List<Libro> risultati = catalogo.cercaPerAutore(autore1);         
        assertTrue(risultati.isEmpty(), "Dovrebbe ritornare lista vuota se l'autore non ha libri");
    }
}