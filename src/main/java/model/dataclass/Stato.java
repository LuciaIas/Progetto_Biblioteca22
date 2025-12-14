/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dataclass;

/**
 * @brief Tipo enumerativo che rappresenta lo stato di un prestito.
 * 
 * I possibili stati di un prestito sono:
 * - ATTIVO: il prestito è in corso
 * - RESTITUITO: il libro è stato restituito
 * - PROROGATO: il prestito è stato prorogato
 * - IN_RITARDO: il prestito non è stato restituito entro la scadenza

 * 
 * @author gruppo22
 */
public enum Stato {
    ATTIVO,RESTITUITO,PROROGATO,IN_RITARDO
}
