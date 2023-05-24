/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.riexinger.persistence;

import de.riexinger.model.Zahlungsart;

/**
 *
 * @author Tim
 */
public abstract class PersistenceAdapterZahlungsart extends PersistenceAdapter{
    
    public abstract Zahlungsart[] get() throws Exception;
    
    public abstract Zahlungsart[] getAlleAusserBar() throws Exception;
    
    public abstract boolean addZahlungsart(Zahlungsart zahlungsart) throws Exception;  
    
    public abstract boolean aktivNullen(Zahlungsart zahlungsart) throws Exception;  
}

/* 
Abstrakte Klasse: Kann abstrakte Methoden aber auch normale Methoden enthalten und kann Klassen extenden.
Interface: Kann nur abstrakte Methoden enthalten, kann nur von anderen Interfaces extended werden.
-----------------------------------------------------------
extends: normale Klassen oder abstrakte Klassen (alle Klassen)
implements: Nur bei Interfaces
*/