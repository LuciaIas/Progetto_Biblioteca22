/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.servizi;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @brief Classe di utilità per eseguire il backup del database della biblioteca.
 * 
 * Questa classe permette di generare un dump SQL del database specificato
 * utilizzando l'eseguibile `mysqldump`. Include metodi per impostare il percorso
 * dell'eseguibile e per eseguire il backup in una cartella di destinazione.
 * 
 * La classe gestisce la costruzione del comando e la lettura
 * dell'output del processo, segnalando eventuali errori.
 * 
 * @author gruppo22
*/
public class Backup {
    
    // --- CONFIGURAZIONE ---
    private static final String DB_NAME = "biblioteca";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; 
    private static String MYSQL_DUMP_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe"; 

    /**
     * @brief Imposta il percorso dell'eseguibile mysqldump.
     * Utile se l'eseguibile si trova in una cartella diversa da quella di default.
     * 
     * @param path nuovo percorso completo dell'eseguibile mysqldump
     */
    public static void setDumpPath(String path) {
        MYSQL_DUMP_PATH = path;
    }
    
     /**
     * @brief Esegue il backup del database in una cartella di destinazione.
     * 
     * Il backup viene salvato come file SQL con nome che include la data e l'ora
     * corrente. Il metodo costruisce il comando in modo sicuro e legge l'output
     * del processo per rilevare errori.
     * 
     * @param cartellaDestinazione percorso della cartella in cui salvare il backup
     * @return true se il backup è stato eseguito correttamente, false altrimenti
     */
    public static boolean eseguiBackup(String cartellaDestinazione) {
        if(cartellaDestinazione==null)
            return false;// Backup non eseguito
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String nomeFile = "Backup_" + DB_NAME + "_" + sdf.format(new Date()) + ".sql";
            
            File fileDestinazione = new File(cartellaDestinazione, nomeFile);
            
            // COSTRUZIONE COMANDO SICURA (LISTA)
            // Invece di una stringa unica, usiamo una lista. 
            // Java gestirà gli spazi nei percorsi (come "Desktop") automaticamente.
            List<String> command = new ArrayList<>();// Lista dei parametri del comando
            command.add(MYSQL_DUMP_PATH); // 1. L'eseguibile
            command.add("-u");            // 2. Utente flag
            command.add(DB_USER);         // 3. Nome utente
            
            if (!DB_PASS.isEmpty()) {
                command.add("-p" + DB_PASS); // Password attaccata al flag -p
            }
            
            command.add(DB_NAME);         // 4. Nome Database
            command.add("-r");            // 5. Flag per output file
            command.add(fileDestinazione.getAbsolutePath()); // 6. Dove salvare

            // Stampa di debug (per vedere cosa stiamo lanciando)
            System.out.println("Eseguendo comando: " + command);

            // Avvio processo DIRETTAMENTE (senza passare dal prompt dei comandi e da cmd.exe) 
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); 
            Process process = pb.start();

            // --- LETTURA OUTPUT DEL PROCESSO ---
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("OUTPUT: " + line);
            }
            int processComplete = process.waitFor();// Attende la fine del processo

            if (processComplete == 0) {
                System.out.println("Backup riuscito: " + fileDestinazione.getAbsolutePath());
                return true; // Backup riuscito
            } else {
                System.err.println("Errore Backup. Codice uscita: " + processComplete);
                return false;// Backup fallito
            }
        } catch (Exception ex) { // Gestione di eventuali eccezioni
            ex.printStackTrace();
            return false;
        }
    }
}
