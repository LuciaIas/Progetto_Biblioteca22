package model.dataclass;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibroTest {

    /**
     * Test Base: Verifica costruttore e getter.
     */
    @Test
    public void testCostruttoreEGetters() {
        Autore autore = new Autore("Dante", "Alighieri", 5, LocalDate.of(1956, 5, 8));
        List<Autore> listaAutori = new ArrayList<>();
        listaAutori.add(autore);
        
        Year anno = Year.of(1980);
        Libro libro = new Libro("1111111111111", "La Divina Comeddia", "Editore Sistemato", listaAutori, anno, 4, "img.jpg");

        assertEquals("1111111111111", libro.getIsbn());
        assertEquals("La Divina Comeddia", libro.getTitolo());
        assertEquals("Editore Sistemato", libro.getEditore());
        assertEquals(listaAutori, libro.getAutori());
        assertEquals(anno, libro.getAnno_pubblicazione());
        assertEquals(4, libro.getNumero_copieDisponibili());
        assertEquals("img.jpg", libro.getUrl());
    }


    @Test
    public void testSetters() {
        Libro libro = new Libro("1", "Alibali", "EditBell", null, Year.of(1900), 1, null);

        // Aggiorniamo tutto
        libro.setIsbn("2");
        libro.setTitolo("Nuovo");
        libro.setNumero_copieDisponibili(50);
        
        assertEquals("2", libro.getIsbn());
        assertEquals("Nuovo", libro.getTitolo());
        assertEquals(50, libro.getNumero_copieDisponibili());
    }

    
    @Test
    public void testCompareTo() {
        Libro libroA = new Libro("1", "A", "", null, null, 0, "");
        Libro libroB = new Libro("2", "B", "", null, null, 0, "");
        Libro libroC = new Libro("3", "A", "", null, null, 0, ""); // Titolo uguale 

        // A deve venire prima di B (risultato negativo)
        assertTrue(libroA.compareTo(libroB) < 0, "Libro A deve essere minore di Libro B");

        // B deve venire dopo A (risultato positivo)
        assertTrue(libroB.compareTo(libroA) > 0, "Libro B deve essere maggiore di Libro A");

        // Titoli uguali devono dare 0
        assertEquals(0, libroA.compareTo(libroC), "Libri con lo stesso titolo devono ritornare 0");
    }


    @Test
    public void testGestioneListaAutori() {
        Libro libro = new Libro("111", "Test", "Ed", new ArrayList<>(), Year.of(2000), 1, "");
        
        Autore a1 = new Autore("Noci", "diCocco", 1, null);
        Autore a2 = new Autore("Pasquale", "Froc", 2, null);

       
        libro.getAutori().add(a1);
        libro.getAutori().add(a2);

        assertEquals(2, libro.getAutori().size());
        assertEquals("diCocco", libro.getAutori().get(0).getCognome());
    }
    

    @Test
    public void testCompareToConTitoloNull() {
        Libro l1 = new Libro("1", null, "", null, null, 0, "");
        Libro l2 = new Libro("2", "Titolo", "", null, null, 0, "");

        assertThrows(NullPointerException.class, () -> {
            l1.compareTo(l2);
        }, "Se il titolo è null, compareTo dovrebbe lanciare eccezione");
    }
}