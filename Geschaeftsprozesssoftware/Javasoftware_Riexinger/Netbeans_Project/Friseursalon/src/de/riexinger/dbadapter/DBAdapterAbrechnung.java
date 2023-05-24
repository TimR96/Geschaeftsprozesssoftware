/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.dbadapter;

import de.riexinger.model.Abrechnung;
import de.riexinger.model.HeutigeAbrechnung;
import de.riexinger.persistence.PersistenceAdapterAbrechnung;
import de.riexinger.dbconnector.DBConnector;
import java.util.ArrayList;
import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Tim
 */
public class DBAdapterAbrechnung extends PersistenceAdapterAbrechnung {
    private final DBConnector dBConnector;
    
    public DBAdapterAbrechnung(DBConnector dBConnector) {
        this.dBConnector=dBConnector;
    }     

    @Override
    public boolean insert(Abrechnung abrechnung) throws Exception {
        String dml = "INSERT INTO Abrechnungen (Bezeichnung, Betrag, Zeitstempel, MitarbeiterID, ZahlungsartenID, KategorienID) VALUES"
                     + " ('"+abrechnung.getBezeichnung() + "','"+abrechnung.getBetrag()+ "','" + abrechnung.getZeitstempel()+ "','" 
                     + abrechnung.getMitarbeiterid() + "','" + abrechnung.getZahlungsartid() +
                     "','" + abrechnung.getKategorieid() + "');";
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        propertyChangeSupport.firePropertyChange("insert", "null", abrechnung);
        return success;  
    }

    @Override
    public Abrechnung getAbrechnungWithMaxID() throws Exception {
        Abrechnung abrechnung = null;
        String sql = "SELECT * FROM Abrechnungen WHERE Zeitstempel = (SELECT max(Zeitstempel) FROM Abrechnungen);";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            abrechnung = new Abrechnung(rs.getInt("Abrechnungenid"), rs.getString("Bezeichnung"), rs.getDouble("Betrag"), rs.getString("Zeitstempel"), rs.getInt("MitarbeiterID"), rs.getInt("ZahlungsartenID"), rs.getInt("KategorienID"));
        }
        dBConnector.disconnect();
        return abrechnung;        
    }   
    
    @Override
    public boolean deleteAbrechnungWithMaxID(int abrechnungsid) throws Exception {
        String dml = "DELETE FROM Abrechnungen WHERE AbrechnungenID = " + abrechnungsid +";";
        dBConnector.connect();
        boolean success = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();   
        return success;  
    }

    @Override
    public HeutigeAbrechnung[] getToday() throws Exception {
        Date date = new Date();            //Datum extrahieren
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //Datum formatieren
        String datum = dateFormat.format(date);        
        
        ArrayList<HeutigeAbrechnung> heutigeAbrechnungenAL = new ArrayList<>();
        
        String sql =    "SELECT AbrechnungenID, Bezeichnung, Betrag, Zahlungsart, Kategorie, Vorname, Nachname"
                      + " FROM Abrechnungen "
                      + "INNER JOIN Zahlungsarten USING (ZahlungsartenID) "
                      + "INNER JOIN Kategorien USING (KategorienID) "
                      + "INNER JOIN Mitarbeiter USING (MitarbeiterID) "
                      + "WHERE Zeitstempel LIKE '" + datum + "%';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            HeutigeAbrechnung heutigeAbrechnung = new HeutigeAbrechnung(rs.getInt("AbrechnungenID"), rs.getString("Bezeichnung"), rs.getDouble("Betrag"), rs.getString("Zahlungsart"), rs.getString("Kategorie"), rs.getString("Vorname"), rs.getString("Nachname"));
            heutigeAbrechnungenAL.add(heutigeAbrechnung);
        }
        dBConnector.disconnect();
        HeutigeAbrechnung[] alleHeutigenAbrechnungen = new HeutigeAbrechnung[heutigeAbrechnungenAL.size()];
        alleHeutigenAbrechnungen = heutigeAbrechnungenAL.toArray(alleHeutigenAbrechnungen);
        
        return alleHeutigenAbrechnungen;
    }  
}
