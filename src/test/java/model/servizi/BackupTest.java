package model.servizi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class BackupTest {

    @TempDir
    Path tempDir;


    private static final String ORIGINAL_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe";

    @AfterEach
    public void cleanup() {
       
        Backup.setDumpPath(ORIGINAL_PATH);
    }

    @Test
    public void testBackupFallisceSeEseguibileManca() {
        
        Backup.setDumpPath("C:\\Percorso\\Che\\Non\\Esiste\\fake_dump.exe");

        
        boolean risultato = Backup.eseguiBackup(tempDir.toString());

        
        assertFalse(risultato, "Il backup deve fallire se l'eseguibile è sbagliato");
    }

    @Test
    public void testBackupGestisceCartellaNull() {
        
        boolean risultato = Backup.eseguiBackup(null);

        assertFalse(risultato, "Il backup deve fallire se la cartella è null");
    }

    @Test
    public void testBackupGestisceCartellaInesistente() {
        
        boolean risultato = Backup.eseguiBackup("Z:\\CartellaImpossibile");

        assertFalse(risultato, "Il backup deve fallire se la cartella di output non esiste");
    }
}