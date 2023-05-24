/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.dbadapter;

import de.riexinger.model.Mitarbeiter;
import de.riexinger.persistence.PersistenceAdapterMitarbeiter;
import de.riexinger.dbconnector.DBConnector;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Tim
 */
public class DBAdapterMitarbeiter extends PersistenceAdapterMitarbeiter{

    private final DBConnector dBConnector;
    
    public DBAdapterMitarbeiter(DBConnector dBConnector) {
        this.dBConnector=dBConnector;
    }    
    
    @Override
    public Mitarbeiter[] get() throws SQLException, ClassNotFoundException {
        ArrayList<Mitarbeiter> mitarbeiterAL = new ArrayList<>();
        String sql = "SELECT MitarbeiterID, Nachname, Vorname, RFID_Code FROM Mitarbeiter WHERE Aktiv = '1';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            Mitarbeiter mitarbeiter = new Mitarbeiter(rs.getInt("MitarbeiterID"), rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("RFID_Code"));
            mitarbeiterAL.add(mitarbeiter);
        }
        dBConnector.disconnect();
        Mitarbeiter[] alleMitarbeiter = new Mitarbeiter[mitarbeiterAL.size()];
        alleMitarbeiter = mitarbeiterAL.toArray(alleMitarbeiter);
        return alleMitarbeiter;
    }

    @Override
    public boolean insert(Mitarbeiter mitarbeiter) throws SQLException, ClassNotFoundException {
        String dml = "INSERT INTO Mitarbeiter (Nachname, Vorname, Aktiv) VALUES ('"+mitarbeiter.getNachname()
                        +"','"+mitarbeiter.getVorname() + "','1');";
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("insert", "null", mitarbeiter);
        return success;
    }

    @Override
    public boolean delete(Mitarbeiter mitarbeiter) throws SQLException, ClassNotFoundException {
        String dml = "UPDATE Mitarbeiter SET Aktiv = '0' WHERE (MitarbeiterID = '" + mitarbeiter.getMitarbeiterID() + "');"; 
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("delete", "null", mitarbeiter);     
        return success;  
    }   

    @Override
    public boolean setRFID(int mitarbeiterid, String code) throws Exception {
        String dml = "UPDATE Mitarbeiter SET RFID_Code = '"+code+"' WHERE MitarbeiterID = '"+mitarbeiterid+"';";
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("insert", "null", "null");
        return success;
    }
    @Override
    public int getMitarbeiterIDMitDoppelterRFID(String code) throws SQLException, ClassNotFoundException {
        String sql = "SELECT MitarbeiterID FROM Mitarbeiter WHERE RFID_Code = '"+code+"';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        int mitarbeiterid=0;
        while(rs.next()){
            mitarbeiterid = rs.getInt("MitarbeiterID");
        }
        dBConnector.disconnect();
        return mitarbeiterid;      
    }    
    
    @Override
    public boolean deleteDoppelteRFID(int mitarbeiterid) throws SQLException, ClassNotFoundException {
        String dml = "UPDATE Mitarbeiter SET RFID_Code = NULL WHERE (MitarbeiterID = '" + mitarbeiterid + "');"; 
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("delete", "null", null);     
        return success;  
    }     
}
