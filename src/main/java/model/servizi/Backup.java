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
 *
 * @author gruppo22
 */
public class Backup {
    
    // --- CONFIGURAZIONE ---
    private static final String DB_NAME = "biblioteca";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; 
    private static String MYSQL_DUMP_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe"; 

    public static void setDumpPath(String path) { // Metodo per cambiare il percorso di mysqldump
        MYSQL_DUMP_PATH = path;
    }
    
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
