package model.dataclass;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class UtenteTest {


    @Test
    public void testCostruttoreEGetters() {
        Utente u = new Utente("1111111111", "Kilo", "Pascal", "kilo.pascal@email.it", false);

        assertEquals("1111111111", u.getMatricola());
        assertEquals("Kilo", u.getNome());
        assertEquals("Pascal", u.getCognome());
        assertEquals("kilo.pascal@email.it", u.getMail());
        assertFalse(u.isBloccato(), "L'utente non dovrebbe essere bloccato all'inizio");
    }


    @Test
    public void testFiltroBlacklist() {
        ArrayList<Utente> lista = new ArrayList<>();
        
        Utente u1 = new Utente("1", "A", "A", "a@a.it", true);  // BLOCCATO
        Utente u2 = new Utente("2", "B", "B", "b@b.it", false); // ATTIVO
        Utente u3 = new Utente("3", "C", "C", "c@c.it", true);  // BLOCCATO
        Utente u4 = new Utente("4", "D", "D", "d@d.it", false); // ATTIVO

        lista.add(u1);
        lista.add(u2);
        lista.add(u3);
        lista.add(u4);

       
        ArrayList<Utente> bloccati = Utente.getUtentiBlackListed(lista);

        // VERIFICA
        assertEquals(2, bloccati.size(), "Dovrebbe trovare esattamente 2 utenti bloccati");
        assertTrue(bloccati.contains(u1));
        assertTrue(bloccati.contains(u3));
        assertFalse(bloccati.contains(u2), "Non deve contenere utenti attivi");
    }


    @Test
    public void testModificaStatoBlocco() {
        Utente u = new Utente("1111111111", "Maicol", "Spancher", "maicol@email.it", false);
        
        // Blocchiamo l'utente
        u.setBloccato(true);
        assertTrue(u.isBloccato(), "L'utente dovrebbe risultare bloccato dopo il set");
        
        // Sblocchiamo l'utente
        u.setBloccato(false);
        assertFalse(u.isBloccato(), "L'utente dovrebbe risultare attivo dopo lo sblocco");
    }


    @Test
    public void testAggiornamentoProfilo() {
        Utente u = new Utente("1", "N", "C", "NC@mail.it", false);
        
        u.setMatricola("2");
        u.setNome("N1");
        u.setMail("NC1@mail.it");
        
        assertEquals("2", u.getMatricola());
        assertEquals("N1", u.getNome());
        assertEquals("NC1@mail.it", u.getMail());
    }


    @Test
    public void testBlacklistVuota() {
        ArrayList<Utente> lista = new ArrayList<>();
        lista.add(new Utente("1", "A", "A", "a", false)); // Utente attivo
        
        ArrayList<Utente> risultati = Utente.getUtentiBlackListed(lista);
        
        assertNotNull(risultati);
        assertTrue(risultati.isEmpty(), "Se non ci sono utenti bloccati, la lista deve essere vuota");
    }
}