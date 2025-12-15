package model.servizi;

import model.dataclass.Stato; 
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PrestitoTest {

//TEST COSTRUTTORE, GETTER
    @Test
    public void testCostruttoreEGetter() {
        LocalDate oggi = LocalDate.now();
        LocalDate scadenza = oggi.plusDays(30);        
        Prestito p = new Prestito("1111111111111", "1111111111", oggi, null, Stato.ATTIVO, scadenza);
        assertEquals("1111111111111", p.getIsbn());
        assertEquals("1111111111", p.getMatricola());
        assertEquals(oggi, p.getInizio_prestito());
        assertNull(p.getRestituzione(), "La data restituzione dovrebbe essere null inizialmente");
        assertEquals(Stato.ATTIVO, p.getStato());
        assertEquals(scadenza, p.getData_scadenza());
    }

    @Test
    public void testFiltroPrestitiByStato() {
        ArrayList<Prestito> listaMista = new ArrayList<>();
        LocalDate data = LocalDate.now();       
        Prestito p1 = new Prestito("ISBN1", "M1", data, null, Stato.ATTIVO, data);
        Prestito p2 = new Prestito("ISBN2", "M2", data, data, Stato.RESTITUITO, data);
        Prestito p3 = new Prestito("ISBN3", "M3", data, null, Stato.IN_RITARDO, data);
        Prestito p4 = new Prestito("ISBN4", "M4", data, null, Stato.ATTIVO, data);
        listaMista.add(p1);
        listaMista.add(p2);
        listaMista.add(p3);
        listaMista.add(p4);
        //Cerchiamo solo quelli ATTIVI
        ArrayList<Prestito> risultati = Prestito.getPrestitiByStato(listaMista, Stato.ATTIVO);
        // VERIFICA
        assertEquals(2, risultati.size(), "Dovrebbe trovare esattamente 2 prestiti ATTIVI");
        assertTrue(risultati.contains(p1));
        assertTrue(risultati.contains(p4));
        assertFalse(risultati.contains(p2), "Non deve contenere prestiti restituiti");
        assertFalse(risultati.contains(p3), "Non deve contenere prestiti in ritardo");
    }

    @Test
    public void testFiltroPrestiti_NessunRisultato() {
        ArrayList<Prestito> lista = new ArrayList<>();
        lista.add(new Prestito("1111111111111", "1111111111", LocalDate.now(), null, Stato.ATTIVO, LocalDate.now()));
        // Cerchiamo uno stato che non c'è
        ArrayList<Prestito> risultati = Prestito.getPrestitiByStato(lista, Stato.RESTITUITO);
        assertNotNull(risultati);
        assertTrue(risultati.isEmpty(), "La lista filtrata deve essere vuota se non ci sono corrispondenze");
    }

    @Test
    public void testFiltroPrestiti_ListaVuota() {
        ArrayList<Prestito> vuota = new ArrayList<>();        
        ArrayList<Prestito> risultati = Prestito.getPrestitiByStato(vuota, Stato.ATTIVO);       
        assertNotNull(risultati);
        assertTrue(risultati.isEmpty(), "Filtrare una lista vuota deve ritornare una lista vuota");
    }
    
    @Test
    public void testModificaStato() {
        // Testiamo il flusso di vita di un prestito: Attivo -> Restituito
        Prestito p = new Prestito("1111111111111", "1111111111", LocalDate.now(), null, Stato.ATTIVO, LocalDate.now());       
        p.setStato(Stato.RESTITUITO);
        p.setRestituzione(LocalDate.now());        
        assertEquals(Stato.RESTITUITO, p.getStato());
        assertNotNull(p.getRestituzione());
    }
}