/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.servizi;

import model.dataclass.Autore;
import model.dataclass.Libro;
import model.dataclass.Stato;
import model.dataclass.Utente;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicol
 */
public class DataBase {
    
    static Connection conn;
    static String DB_name="Biblioteca";
    
    
    public static void DBInitialize(){
         Connection c;
        
        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost/"+DB_name, "root", "");
            
            conn=c;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    
    
    //FUNZIONI PER INSERIRE IL BIBLIOTECARIO
    public static boolean InsertBibliotecario(String password){
        if(CheckIfExistsBibliotecario())
            return false;
        StringBuilder hexString;
        try {
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            
            hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                
                
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            

        } catch (NoSuchAlgorithmException e) {
            
            throw new RuntimeException("Errore critico: Algoritmo SHA-256 non trovato.", e);
        }
        
        String query = "Insert into bibliotecario values(?)";
        try {
            PreparedStatement stat= conn.prepareStatement(query);
            stat.setString(1, hexString.toString());
            stat.execute();
            
        } catch (SQLException ex) {
            return false;
        }
        
        
        return true;
    }

    public static boolean CheckIfExistsBibliotecario(){
        String query = "Select * from bibliotecario";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            
            ResultSet rs = stat.executeQuery();
            
            if(rs.next())
                return true;
            else
                return false;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    //FUNZIONE PER CANCELLARE L'ACCOUNT DEL BIBLIOTECARIO
    public static boolean RemoveBibliotecario(){
        if(!CheckIfExistsBibliotecario())
            return false;
        
        String query = "delete from bibliotecario";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.execute();
            return true;
        } catch (SQLException ex) {
            return false;
        }
        
    }
    
    
    //FUNZIONE PER PRENDERE LA PASSWORD DAL DATABASE
    public static boolean CheckPasswordBibliotecario(String password){
        if(!CheckIfExistsBibliotecario())
            return false;
        
        StringBuilder hexString;
        try {
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            
            hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                
                
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            

        } catch (NoSuchAlgorithmException e) {
            
            throw new RuntimeException("Errore critico: Algoritmo SHA-256 non trovato.", e);
        }
        
        String query = "Select * from bibliotecario where password_=?";
        
        PreparedStatement stat;
        try {
            stat = conn.prepareStatement(query);
            stat.setString(1, hexString.toString());
            ResultSet rs = stat.executeQuery();
            
            if(rs.next())
                return true;
            
            
        } catch (SQLException ex) {
            return false;
        }
        return false;
        
    
    }
    
    
    //SELECT DEI LIBRI
    public static Catalogo GetCatalogo(){
        
        Catalogo libri = new Catalogo();
        List<Autore> autori = new ArrayList<>(); 
        
        String queryLibri = "Select * from Libri";
        String queryAutori = "Select * from autori";
        String queryScrittoda = "Select * from scritto_da where isbn=?";
        try {
            PreparedStatement stat = conn.prepareStatement(queryLibri);
            ResultSet rs = stat.executeQuery();
            
            while(rs.next())
            //isbn,titolo,editore,null,anno pubblicazione,num_copie,url
                libri.aggiungiLibro(new Libro(rs.getString(1),rs.getString(2),rs.getString(3),null,Year.of(rs.getInt(5)),rs.getInt(4),rs.getString(6)));
            
            stat = conn.prepareStatement(queryAutori);
            rs = stat.executeQuery();
            //nome cognome opere dadtanascita
            while(rs.next()){
                Autore a;
                if(rs.getDate(5)!=null)
                    
                a = new Autore(rs.getString(2),rs.getString(3),rs.getInt(4),(rs.getDate(5)).toLocalDate());
                else
                    a = new Autore(rs.getString(2),rs.getString(3),rs.getInt(4),null);
                a.setId(rs.getInt(1));
                autori.add(a);
            }
            
            for(Libro l : libri.getLibri()){
            
                 List<Autore> aut = new ArrayList<>();
                 List<Integer> id = new ArrayList<>();
                 stat = conn.prepareStatement(queryScrittoda);
                 stat.setString(1, l.getIsbn());
                 rs = stat.executeQuery();
                 while(rs.next())
                     id.add(rs.getInt(2));
                 
                 for(Autore a : autori)
                     if(id.contains(a.getId()))
                         aut.add(a);
                 
                 l.setAutori(aut);
                 
            }
            libri.sort();
            return libri;
            
        } catch (SQLException ex) {
            System.out.println("Errore sql");
            return null;
        }
        
    }
    
    //SELECT DEGLI AUTORI
    public static ArrayList<Autore> getAutori(){
        
        ArrayList<Autore> autori = new ArrayList<>();
        String queryAutori = "Select * from autori";
        
        PreparedStatement stat;
        try {
            stat = conn.prepareStatement(queryAutori);
            ResultSet rs = stat.executeQuery();
            
            while(rs.next()){
                Autore a;
                if(rs.getDate(5)!=null)
                a = new Autore(rs.getString(2),rs.getString(3),rs.getInt(4),(rs.getDate(5)).toLocalDate());
                else
                    a = new Autore(rs.getString(2),rs.getString(3),rs.getInt(4),null);
                a.setId(rs.getInt(1));
                autori.add(a);
            }
            return autori;
            
        } catch (SQLException ex) {
            //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
    //AGGIUNGERE IL LIBRO
    public static boolean addBook(Libro l){
        
        String query = "INSERT INTO libri values(?,?,?,?,?,?)";
       boolean ret = false;
        try {
            System.out.println("aggiungo libro");
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, l.getIsbn());
            stat.setString(2, l.getTitolo());
            stat.setString(3, l.getEditore());
            stat.setInt(5, l.getNumero_copieDisponibili());
            stat.setInt(4, l.getAnno_pubblicazione().getValue());
            if(l.getUrl()!=null)
                stat.setString(6, l.getUrl());
            else
                stat.setNull(6, Types.VARCHAR);
            
            stat.execute();
            ret=true;
            //SETTO LE RELAZIONI CON GLI AUTORI
            String queryR = "Insert Into scritto_da values(?,?)";
            for(Autore a: l.getAutori()){
            
           PreparedStatement stat1 =conn.prepareStatement(queryR);
            
            stat1.setString(1,l.getIsbn());
            stat1.setInt(2, a.getId());
            
            stat1.execute();
            //ret=true;
            
            }
        } catch (SQLException ex) {
           // ex.printStackTrace();
            System.out.println("eccezioneee super");
            ret=false;
        }
        
        
        
        return ret;
        
        
    }
    
    public static Libro searchBook(String isbn){
    
        Catalogo c = GetCatalogo();
        return c.cercaPerIsbn(isbn);
    }
    
    //AGGIUNGERE AUTORE
    public static boolean addAutore(Autore a){
        String query = "INSERT INTO autori(nome, cognome, num_opere, data_nascita) values(?,?,?,?)";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, a.getNome());
            stat.setString(2, a.getCognome());
            stat.setInt(3, a.getOpere_scritte());
            
            if(a.getData_nascita()!=null)
                stat.setDate(4, Date.valueOf(a.getData_nascita()));
            else
                stat.setNull(4, Types.DATE);
            
            stat.execute();
            return true;
        } catch (SQLException ex) {
            return false;
        }
        
    }
    
    public static int getNum_Autori(){
    
        int n=-1;
        
        String query = "SELECT COUNT(*) FROM autori;";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rs =stat.executeQuery();
            rs.next();
            n=rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }
    
    public static Autore SearchAutorByNames(String nome,String cognome){
    
        String query = "Select * from autori where nome=? and cognome=?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, nome);
            stat.setString(2, cognome);
            
            ResultSet rs = stat.executeQuery();
            rs.next();
            Autore a;
            if(rs.getDate(5)!=null)
            a = new Autore(nome,cognome,rs.getInt(4),(rs.getDate(5)).toLocalDate());
            else
                a = new Autore(nome,cognome,rs.getInt(4),null);
            a.setId(rs.getInt(1));
            return a;
        } catch (SQLException ex) {
            System.out.println("Eccezione sql");
            return null;
        }
        
    }
    
    //FUNZIONI PER GLI UTENTI
    public static ArrayList<User> getUtenti(){
    
        ArrayList<User> us = new ArrayList<>();
        String query = "Select * from utenti";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rs = stat.executeQuery();
            while(rs.next()){
            
                us.add(new User(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getBoolean(5)));
            
            }
            us.sort(new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    int a = o1.getCognome().toUpperCase().compareTo(o2.getCognome().toUpperCase());
                    if(a==0)
                        return o1.getNome().toUpperCase().compareTo(o2.getNome().toUpperCase());
                    else return a;
                }
            });
                return us;
                  
        } catch (SQLException ex) {
            return null;
        }
        
    }
    public static boolean addUser(User u){
    
        String query = "INSERT INTO utenti values(?,?,?,?,?)";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, u.getMatricola());
            stat.setString(2, u.getNome());
            stat.setString(3, u.getCognome());
            stat.setString(4, u.getMail());
            stat.setBoolean(5, u.isBloccato());
            
            stat.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
    
    public static int getNumUser(){
        int n=0;
        String query = "Select COUNT(*) from utenti";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rs = stat.executeQuery();
            rs.next();
            n=rs.getInt(1);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
          
        }
        return n;
    }
    
    public static User searchUser(String matricola){
    
        ArrayList<User> us = getUtenti();
        for(User u: us)
            if(u.getMatricola().equals(matricola))
                return u;
        return null;
    }
    
    
    public static boolean setBlackListed(String matricola){
    
        String query = "UPDATE utenti SET Bloccato = true WHERE matricola = ?";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, matricola);
            stat.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
    
    public static boolean UnsetBlackListed(String matricola){
    
        String query = "UPDATE utenti SET Bloccato = false WHERE matricola = ?";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, matricola);
            stat.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    
    }
    
     public static boolean modifyBook(String isbn,String titolo,String editore,int anno_pubblicazione,int num_copie, String url,ArrayList<Autore> autori){
    
        String query = "UPDATE libri SET titolo = ?,editore = ?,anno_pubblicazione=?,num_copie=?,url_immagine=? WHERE isbn = ?";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, titolo);
            stat.setString(2, editore);
            stat.setInt(3, anno_pubblicazione);
            stat.setInt(4, num_copie);
            if(url==null)
                stat.setNull(5, Types.VARCHAR);
            else
            stat.setString(5, url);
            stat.setString(6, isbn);
            stat.execute();
            //ORA RIMUOVO GLI AUTORI
            String queryAutori = "DELETE FROM scritto_da WHERE isbn = ? ";
            PreparedStatement stat1 = conn.prepareStatement(queryAutori);
            stat1.setString(1, isbn);
            stat1.execute();
            
            //SETTO LE RELAZIONI CON GLI AUTORI
            String queryR = "Insert Into scritto_da values(?,?)";
            for(Autore a: autori){
            
           PreparedStatement stat2 =conn.prepareStatement(queryR);
            
            stat2.setString(1,isbn);
            stat2.setInt(2, a.getId());
            
            stat2.execute();
            //ret=true;
            
            }
            
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
        
    }
    
    public static boolean modifyUser(String matricola,String nome,String cognome,String mail){
    
        String query = "UPDATE utenti SET nome = ?,cognome = ?,mail=? WHERE matricola = ?";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, nome);
            stat.setString(2, cognome);
            stat.setString(3, mail);
            stat.setString(4, matricola);
            stat.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
        
    }
    
    //PRESITI
    public static ArrayList<Prestito> getPrestiti(){
    
            String query = "Select * from prestito";
            ArrayList<Prestito> p = new ArrayList<>();
            try {
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rs = stat.executeQuery();
            
            while(rs.next()){
                Stato stato;
            String app = rs.getString(5);
            switch(app){
            
                case "ATTIVO":
                    stato=Stato.ATTIVO;
                    break;
                case "RESTITUITO":
                    stato = Stato.RESTITUITO;
                    break;
                case "PROROGATO":
                    stato = Stato.PROROGATO;
                    break;
                case "IN_RITARDO":
                    stato = Stato.IN_RITARDO;
                    break;
                default:
                    stato = null;
                    break;
            
            }
            LocalDate scadenza=null,restituzione=null;
            if(rs.getDate(6)!=null)
                scadenza = rs.getDate(6).toLocalDate();
            if(rs.getDate(4)!=null)
                restituzione=rs.getDate(4).toLocalDate();
                p.add(new Prestito(rs.getString(1),rs.getString(2),rs.getDate(3).toLocalDate(),restituzione,stato,scadenza));
            }
            
            return p;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
            
            
    }
    
    
    public static boolean addPrestito(Prestito p){
    
        
        String query = "Insert into prestito values(?,?,?,?,?,?)";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, p.getIsbn());
            stat.setString(2, p.getMatricola());
            stat.setDate(3, Date.valueOf(p.getInizio_prestito()));
            if(p.getRestituzione()==null)
                stat.setNull(4, Types.DATE);
            else
                stat.setDate(4, Date.valueOf(p.getRestituzione()));
            boolean stNull=false;
            String s1="";
            Stato s = p.getStato();
            if(s.equals(Stato.ATTIVO))
                s1 = "ATTIVO";
            else if(s.equals(Stato.IN_RITARDO))
                s1 = "IN_RITARDO";
            else if(s.equals(Stato.PROROGATO))
                s1 = "PROROGATO";
            else if(s.equals(Stato.RESTITUITO))
                s1 = "RESTITUITO";
            else
                stNull=true;
            if(stNull)
                stat.setNull(5, Types.VARCHAR);
            else
                stat.setString(5, s1);
            
            stat.setDate(6, Date.valueOf(p.getData_scadenza()));
            
            stat.execute();
            
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static int getNumLoan(){
        int n=0;
        String query = "Select COUNT(*) from prestito";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rs = stat.executeQuery();
            rs.next();
            n=rs.getInt(1);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
          
        }
        return n;
    }
    
    public static boolean Restituisci(String isbn,String matricola){
    
        String query = "Update prestito set data_restituzione = CURRENT_DATE,stato_prestito = 'Restituito' where isbn=? and matricola=? and data_restituzione is null";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
            stat.setString(2, matricola);
            stat.execute();
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
          return false;
        }
    }
    
    
    public static boolean CheckPrestito(String isbn,String matricola){
    
        String query = "Select * from prestito where isbn=? and matricola=?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
            stat.setString(2, matricola);
            ResultSet rs = stat.executeQuery();
            
            return rs.next();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
          return false;
        }
    
    }
    
    public static boolean RemovePrestito(String isbn,String matricola){
        String query = "DELETE FROM prestito WHERE isbn = ? AND matricola = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
            stat.setString(2, matricola);
            stat.execute();
            
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
          return false;
        }
    }
    
    //FUNZIONI PER LE COPIE
    public static int getNumCopieByIsbn(String isbn){
        int n = -1;
        String query = "select num_copie from libri where isbn=?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
            ResultSet rs = stat.executeQuery();
            rs.next();
            n = rs.getInt(1);
            
            
            
        } catch (SQLException ex) {
            ex.printStackTrace();
         
        }
        return n;
    }
    
    public static boolean modifyNum_copie(String isbn,boolean add){
    
        String query = "UPDATE libri SET num_copie=? WHERE isbn = ?";
        int num_copie = getNumCopieByIsbn(isbn);
        if(add)
            num_copie+=1;
        else    
            num_copie-=1;
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(2, isbn);
            stat.setInt(1, num_copie);
            stat.execute();
            
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
    
    public static boolean setStatoPrestito(String isbn,String matricola, Stato stato){
     
        String query = "UPDATE prestito SET stato_prestito = ? WHERE isbn = ?  AND matricola = ?";

        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(2, isbn);
            stat.setString(3, matricola);
            
            boolean stNull =false;
            String s1="";
            Stato s = stato;
            if(s.equals(Stato.ATTIVO))
                s1 = "ATTIVO";
            else if(s.equals(Stato.IN_RITARDO))
                s1 = "IN_RITARDO";
            else if(s.equals(Stato.PROROGATO))
                s1 = "PROROGATO";
            else if(s.equals(Stato.RESTITUITO))
                s1 = "RESTITUITO";
            else
                stNull=true;
            if(stNull)
                stat.setNull(1, Types.VARCHAR);
            else
                stat.setString(1, s1);
            
            
            
            stat.execute();
            
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
        
    }
    
    public static boolean ProrogaPrestito(String isbn,String matricola){
    
        String query = "UPDATE prestito SET data_scadenza = DATE_ADD(current_date, INTERVAL 15 DAY) WHERE isbn = ?   AND matricola = ? AND data_restituzione IS NULL";
    
         try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
            stat.setString(2, matricola);
 
            
            stat.execute();
            
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
    
    public static ArrayList<Libro> SearchUserByTitle(String titolo){
    
        Catalogo c = GetCatalogo();
        ArrayList<Libro> ar = new ArrayList<>();
        for(Libro l : c.cercaPerTitolo(titolo))
            ar.add(l);
        
        return ar;
    
    }
    
    public static boolean RemoveBook(String isbn){
    
        String query = "DELETE FROM libri WHERE isbn = ? ";
        
         try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
          
            stat.execute();
            
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
    }

    public static boolean RemoveUser(String matricola){
    
        String query = "DELETE FROM utenti WHERE matricola = ? ";
        
         try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, matricola);
          
            stat.execute();
            
            return true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
    
    public static int GetNumRelationsScritto_Da(){
        int n=0;
        String query = "SELECT COUNT(*) FROM scritto_da";
        
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            
            ResultSet rs = stat.executeQuery();
            rs.next();
            n=rs.getInt(1);
            return n;
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
        return n;
    }
    
    
    public static boolean isIsbnPresent(String isbn){
    
        String query = "SELECT * FROM libri where isbn=?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, isbn);
            ResultSet rs = stat.executeQuery();
            
            
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean isMatricolaPresent(String matricola){
    
        String query = "SELECT * FROM utenti where matricola=?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, matricola);
            ResultSet rs = stat.executeQuery();
            
            
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    
}
