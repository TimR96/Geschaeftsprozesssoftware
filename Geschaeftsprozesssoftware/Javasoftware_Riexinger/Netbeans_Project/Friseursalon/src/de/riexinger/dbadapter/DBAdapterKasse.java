/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.dbadapter;

import de.riexinger.dbconnector.DBConnector;
import java.sql.*;
import de.riexinger.persistence.PersistenceAdapterKasse;
import de.riexinger.model.Kasse;
import java.math.BigDecimal;
/**
 *
 * @author Tim
 */
public class DBAdapterKasse extends PersistenceAdapterKasse{
       
    private final DBConnector dBConnector;

    public DBAdapterKasse(DBConnector dBConnector) {
        this.dBConnector=dBConnector;
    }

    @Override
    public Kasse get() throws SQLException, ClassNotFoundException {    
        String sql = "SELECT Kassenbestand FROM Kassenbestand WHERE KassenbestandID = 1;";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        double kassenbestand = 0.0;
        while(rs.next()){
            kassenbestand = rs.getDouble("Kassenbestand");
        }
        dBConnector.disconnect();
        return new Kasse(new BigDecimal(kassenbestand));  
    }

    @Override
    public boolean set(BigDecimal kassenbestand) throws SQLException, ClassNotFoundException {
        String dml = "UPDATE Kassenbestand SET Kassenbestand = " + kassenbestand + " WHERE KassenbestandID = 1;";
        dBConnector.connect();
        boolean result = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        return result;
    }
}