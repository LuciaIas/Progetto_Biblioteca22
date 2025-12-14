package model.dataclass;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class AutoreTest {
 
    @Test
    public void testCostruttoreEGetter() {
        LocalDate nascita = LocalDate.of(1865, 2, 21);
        Autore a = new Autore("Dante", "Alighieri", 21, nascita);

        assertEquals("Dante", a.getNome(), "Il nome deve corrispondere");
        assertEquals("Alighieri", a.getCognome(), "Il cognome deve corrispondere");
        assertEquals(21, a.getOpere_scritte(), "Il numero di opere deve corrispondere");
        assertEquals(nascita, a.getData_nascita(), "La data di nascita deve corrispondere");        
        // Test default id 
        assertEquals(0, a.getId(), "L'ID di default dovrebbe essere 0");
    }

//TEST SETTER
    @Test
    public void testSetters() {
        Autore a = new Autore("Nome", "Cognome", 0, LocalDate.now());
        // Modifichiamo i dati
        a.setNome("Alessandro");
        a.setCognome("Manzoni");
        a.setOpere_scritte(10);
        a.setId(100);
        LocalDate nuovaData = LocalDate.of(1988, 2, 14);
        a.setData_nascita(nuovaData);
        // Verifichiamo che siano cambiati
        assertEquals("Alessandro", a.getNome());
        assertEquals("Manzoni", a.getCognome());
        assertEquals(10, a.getOpere_scritte());
        assertEquals(100, a.getId());
        assertEquals(nuovaData, a.getData_nascita());
    }

    @Test
    public void testValoriNull() {
        // Creiamo un autore con data di nascita null
        Autore a = new Autore("Pasquale", null, 2, null);
        assertEquals("Pasquale", a.getNome());
        assertNull(a.getCognome(), "Il cognome dovrebbe essere null");
        assertNull(a.getData_nascita(), "La data di nascita dovrebbe essere null");       
        // Verifa toString (anche se mai usato nel codice ma per prassi testiamo)
        assertDoesNotThrow(() -> a.toString(), "Il metodo toString non deve crashare anche se ci sono null");
    }

    @Test
    public void testToString() {
        Autore a = new Autore("Dante", "Alighieri", 1, LocalDate.of(1722, 10, 5));
        String risultato = a.toString();
        // Controlliamo che la stringa contenga le informazioni chiave
        assertTrue(risultato.contains("Dante"));
        assertTrue(risultato.contains("Alighieri"));
        assertTrue(risultato.contains("1722")); 
    }
}