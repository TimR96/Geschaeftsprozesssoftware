package de.riexinger.persistence;

import de.riexinger.model.Kasse;
import java.math.BigDecimal;

/**
 *
 * @author Tim
 */
public abstract class PersistenceAdapterKasse extends PersistenceAdapter {

    public abstract Kasse get() throws Exception;
    
    public abstract boolean set(BigDecimal kassenbestand) throws Exception;
}