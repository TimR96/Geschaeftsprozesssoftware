/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.dbadapter;

import de.riexinger.model.Zahlungsart;
import de.riexinger.persistence.PersistenceAdapterZahlungsart;
import de.riexinger.dbconnector.DBConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
/**
 *
 * @author Tim
 */
public class DBAdapterZahlungsart extends PersistenceAdapterZahlungsart {

    private final DBConnector dBConnector;
   
    public DBAdapterZahlungsart(DBConnector dBConnector) {
        this.dBConnector=dBConnector;
    }       
    
    @Override
    public Zahlungsart[] get() throws SQLException, ClassNotFoundException {
        ArrayList<Zahlungsart> zahlungsartAL = new ArrayList<>();
        String sql = "SELECT ZahlungsartenID, Zahlungsart FROM Zahlungsarten WHERE Aktiv = '1';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            Zahlungsart zahlungsart = new Zahlungsart(rs.getInt("ZahlungsartenID"), rs.getString("Zahlungsart"));
            zahlungsartAL.add(zahlungsart);
        }
        dBConnector.disconnect();
        Zahlungsart[] alleZahlungsarten = new Zahlungsart[zahlungsartAL.size()];
        alleZahlungsarten = zahlungsartAL.toArray(alleZahlungsarten);
        return alleZahlungsarten;
    }
    
    @Override
    public Zahlungsart[] getAlleAusserBar() throws SQLException, ClassNotFoundException {
        ArrayList<Zahlungsart> zahlungsartAL = new ArrayList<>();
        String sql = "SELECT ZahlungsartenID, Zahlungsart FROM Zahlungsarten WHERE ZahlungsartenID > 1 AND Aktiv = '1';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            Zahlungsart zahlungsart = new Zahlungsart(rs.getInt("ZahlungsartenID"), rs.getString("Zahlungsart"));
            zahlungsartAL.add(zahlungsart);
        }
        dBConnector.disconnect();
        Zahlungsart[] alleZahlungsarten = new Zahlungsart[zahlungsartAL.size()];
        alleZahlungsarten = zahlungsartAL.toArray(alleZahlungsarten);    
        return alleZahlungsarten;
    }    

    @Override
    public boolean addZahlungsart(Zahlungsart zahlungsart) throws Exception {
        String dml = "INSERT INTO Zahlungsarten (Zahlungsart, Aktiv) VALUES ('" + zahlungsart.getZahlungsart() + "', 1);";  
        dBConnector.connect();
        boolean result = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        return result; 
    }

    @Override
    public boolean aktivNullen(Zahlungsart zahlungsart) throws Exception {
        String dml = "UPDATE Zahlungsarten SET Aktiv = '0' WHERE (ZahlungsartenID = '" + zahlungsart.getZahlungsartenid() + "');";  
        dBConnector.connect();
        boolean result = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        return result; 
    }   
}
