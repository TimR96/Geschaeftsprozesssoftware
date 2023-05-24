/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.persistence;

import de.riexinger.model.Kategorie;


/**
 *
 * @author Tim
 */
public abstract class PersistenceAdapterKategorie extends PersistenceAdapter {
    
    public abstract Kategorie[] get() throws Exception;
    
    public abstract Kategorie[] getAlleAusserAusgabe() throws Exception;   
    
    public abstract boolean addKategorie(Kategorie kategorie) throws Exception;   
    
    public abstract boolean aktivNullen(Kategorie kategorie) throws Exception;      
}
