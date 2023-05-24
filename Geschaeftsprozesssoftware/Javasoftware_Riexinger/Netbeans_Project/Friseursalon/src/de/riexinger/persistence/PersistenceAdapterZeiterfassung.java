/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.persistence;

import de.riexinger.model.Zeitbuchung;
import de.riexinger.model.Zeiterfassung;

/**
 *
 * @author Tim
 */
public abstract class PersistenceAdapterZeiterfassung extends PersistenceAdapter {
    
    public abstract Zeitbuchung[] getToday() throws Exception;    
    
    public abstract boolean insert(Zeiterfassung zeiterfassung) throws Exception;  
    
    public abstract boolean delete(Zeitbuchung zeitbuchung) throws Exception;  
}
