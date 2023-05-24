/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.persistence;

import de.riexinger.model.Mitarbeiter;


/**
 *
 * @author Tim
 */
public abstract class PersistenceAdapterMitarbeiter extends PersistenceAdapter {

    public abstract Mitarbeiter[] get() throws Exception;
    
    public abstract boolean insert(Mitarbeiter mitarbeiter) throws Exception;  
    
    public abstract boolean delete(Mitarbeiter mitarbeiter) throws Exception;
    
    public abstract boolean setRFID(int mitarbeiterid, String code) throws Exception; 
    
    public abstract int getMitarbeiterIDMitDoppelterRFID(String code) throws Exception;
    
    public abstract boolean deleteDoppelteRFID(int mitarbeiterid) throws Exception;  
}
