package de.riexinger.persistence;

import de.riexinger.model.Abrechnung;
import de.riexinger.model.HeutigeAbrechnung;


/**
 *
 * @author Tim
 */
public abstract class PersistenceAdapterAbrechnung extends PersistenceAdapter{

    public abstract boolean insert(Abrechnung abrechnung) throws Exception;
    
    public abstract Abrechnung getAbrechnungWithMaxID() throws Exception;
    
    public abstract boolean deleteAbrechnungWithMaxID(int abrechnungsid) throws Exception;
    
    public abstract HeutigeAbrechnung[] getToday() throws Exception;
}