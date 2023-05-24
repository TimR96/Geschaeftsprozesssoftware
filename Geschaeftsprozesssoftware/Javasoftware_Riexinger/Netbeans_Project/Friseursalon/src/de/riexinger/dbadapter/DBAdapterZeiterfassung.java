/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.dbadapter;

import de.riexinger.model.Zeitbuchung;
import de.riexinger.model.Zeiterfassung;
import de.riexinger.persistence.PersistenceAdapterZeiterfassung;
import de.riexinger.dbconnector.DBConnector;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 *
 * @author Tim
 */
public class DBAdapterZeiterfassung extends PersistenceAdapterZeiterfassung{

    private final DBConnector dBConnector;
    
    public DBAdapterZeiterfassung(DBConnector dBConnector) {
        this.dBConnector=dBConnector;
    }

    @Override
    public Zeitbuchung[] getToday() throws Exception {
        Date date = new Date();            //Datum extrahieren
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //Datum formatieren
        String datum = dateFormat.format(date);        
        
        ArrayList<Zeitbuchung> zeitbuchungAL = new ArrayList<>();
        
        String sql =    "SELECT Nachname, Vorname, Datum, Uhrzeit, Zeiterfassungsart, ZeiterfassungenID\n" +
                        "FROM Mitarbeiter\n" +
                        "INNER JOIN Zeiterfassungen USING (MitarbeiterID)\n" +
                        "INNER JOIN Zeiterfassungsarten USING (ZeiterfassungsartenID) WHERE Datum LIKE '" + datum + "';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            Zeitbuchung zeitbuchung = new Zeitbuchung(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("Datum"), rs.getString("Uhrzeit"), rs.getString("Zeiterfassungsart"), rs.getInt("ZeiterfassungenID"));
            zeitbuchungAL.add(zeitbuchung);
        }
        dBConnector.disconnect();
        Zeitbuchung[] alleZeitbuchungen = new Zeitbuchung[zeitbuchungAL.size()];
        alleZeitbuchungen = zeitbuchungAL.toArray(alleZeitbuchungen);
        return alleZeitbuchungen;
    }    
    
    @Override
    public boolean insert(Zeiterfassung zeiterfassung) throws SQLException, ClassNotFoundException { 
        String dml = "INSERT INTO Zeiterfassungen (Datum, Uhrzeit, MitarbeiterID, ZeiterfassungsartenID)"
                    + " VALUES ('"+zeiterfassung.getDatum() + "','" + zeiterfassung.getUhrzeit() + "','" +
                    zeiterfassung.getMitarbeiterid() + "','" + zeiterfassung.getZeiterfassungsartenid() +
                    "');";
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("insert", "null", zeiterfassung);
        return success;
    }

    @Override
    public boolean delete(Zeitbuchung zeitbuchung) throws SQLException, ClassNotFoundException {
        String dml = "DELETE FROM Zeiterfassungen WHERE ZeiterfassungenID = " + zeitbuchung.getZeiterfassungenid() + ";";
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("delete", "null", zeitbuchung);     
        return success;  
    }   
}
