package model.servizi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ControlloFormatoTest {

    // TEST PASSWORD
    // Regole: 
    // 1. Almeno 1 Numero
    // 2. Almeno 1 Minuscola
    // 3. Almeno 1 Maiuscola
    // 4. Almeno 1 Speciale TRA QUESTI ESATTI: @ # $ % ^ & + = !
    // 5. Niente spazi
    // 6. Minimo 8 caratteri

    @Test
    public void testPasswordValida() {
        assertTrue(ControlloFormato.controlloFormatoPassword("Password123!"));
    }
    
    @Test
    public void testPasswordValidaAltroSimbolo() {
        assertTrue(ControlloFormato.controlloFormatoPassword("Password123#"));
    }

    @Test
    public void testPasswordNull() {
        assertFalse(ControlloFormato.controlloFormatoPassword(null), 
            "La password null deve restituire false");
    }

    @Test
    public void testPasswordTroppoCorta() {
        assertFalse(ControlloFormato.controlloFormatoPassword("Pass1!"), 
            "La password deve essere di almeno 8 caratteri");
    }

    @Test
    public void testPasswordMancaMaiuscola() {
        assertFalse(ControlloFormato.controlloFormatoPassword("password123!"), 
            "Manca la maiuscola quindi deve fallire");
    }

    @Test
    public void testPasswordMancaMinuscola() {
        assertFalse(ControlloFormato.controlloFormatoPassword("PASSWORD123!"), 
            "Manca la minuscola quindi deve fallire");
    }

    @Test
    public void testPasswordMancaNumero() {
        assertFalse(ControlloFormato.controlloFormatoPassword("Password!"), 
            "Manca il numero quindi deve fallire");
    }

    @Test
    public void testPasswordMancaCarattereSpeciale() {
        assertFalse(ControlloFormato.controlloFormatoPassword("Password123"), 
            "Manca il carattere speciale quindi deve fallire");
    }

    @Test
    public void testPasswordConCarattereNonAmmesso() {
        assertFalse(ControlloFormato.controlloFormatoPassword("Password123*"), 
            "Deve fallire perché * non è un carattere speciale accettato");
    }

    @Test
    public void testPasswordConSpazi() {
        assertFalse(ControlloFormato.controlloFormatoPassword("Pass word1!"), 
            "Gli spazi non sono ammessi");
    }

    // TEST EMAIL

    @Test
    public void testEmailValida() {
        assertTrue(ControlloFormato.controlloFormatoEmail("test@unisa.com"));
    }
    
    @Test
    public void testEmailValidaComplessa() {
        assertTrue(ControlloFormato.controlloFormatoEmail("pasquale.mazzocchi@studenti.unisa.it"));
    }

    @Test
    public void testEmailNull() {
        assertFalse(ControlloFormato.controlloFormatoEmail(null), 
            "L'email null deve restituire false");
    }

    @Test
    public void testEmailSenzaChiocciola() {
        assertFalse(ControlloFormato.controlloFormatoEmail("pasqualemazzocchi.it"), 
            "Senza @ deve fallire");
    }
    
    @Test
    public void testEmailSenzaPunto() {
        assertFalse(ControlloFormato.controlloFormatoEmail("pasquale@mazzocchi"), 
            "Senza il punto deve fallire");
    }

    @Test
    public void testEmailVuota() {
        assertFalse(ControlloFormato.controlloFormatoEmail(""), 
            "Stringa vuota quindi deve fallire");
    }
}