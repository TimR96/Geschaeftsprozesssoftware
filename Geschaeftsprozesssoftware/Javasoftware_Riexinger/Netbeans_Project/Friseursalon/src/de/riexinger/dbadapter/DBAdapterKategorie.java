/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.dbadapter;

import de.riexinger.model.Kategorie;
import de.riexinger.persistence.PersistenceAdapterKategorie;
import de.riexinger.dbconnector.DBConnector;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Tim
 */
public class DBAdapterKategorie extends PersistenceAdapterKategorie {

    private final DBConnector dBConnector;
  
    public DBAdapterKategorie(DBConnector dBConnector) {
        this.dBConnector=dBConnector;
    }       
    
    @Override
    public Kategorie[] get() throws SQLException, ClassNotFoundException { 
        ArrayList<Kategorie> kategorieAL = new ArrayList<>();
        String sql = "SELECT KategorienID, Kategorie FROM Kategorien WHERE Aktiv = '1';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            Kategorie kategorie = new Kategorie(rs.getInt("KategorienID"), rs.getString("Kategorie"));
            kategorieAL.add(kategorie);
        }
        dBConnector.disconnect();
        Kategorie[] alleKategorien = new Kategorie[kategorieAL.size()];
        alleKategorien = kategorieAL.toArray(alleKategorien);
        return alleKategorien;
    }
    
    @Override
    public Kategorie[] getAlleAusserAusgabe() throws SQLException, ClassNotFoundException {
        ArrayList<Kategorie> kategorieAL = new ArrayList<>();
        String sql = "SELECT KategorienID, Kategorie FROM dbfriseurriexinger.Kategorien WHERE NOT KategorienID = '6' AND Aktiv = '1';";
        dBConnector.connect();
        ResultSet rs = dBConnector.executeQuery(sql);
        while(rs.next()){
            Kategorie kategorie = new Kategorie(rs.getInt("KategorienID"), rs.getString("Kategorie"));
            kategorieAL.add(kategorie);
        }
        dBConnector.disconnect();
        Kategorie[] alleKategorien = new Kategorie[kategorieAL.size()];
        alleKategorien = kategorieAL.toArray(alleKategorien);     
        return alleKategorien;
    } 
    
    @Override
    public boolean addKategorie(Kategorie kategorie) throws Exception {
        String dml = "INSERT INTO Kategorien (Kategorie, Aktiv) VALUES ('" + kategorie.getKategorie() + "', 1);";  
        dBConnector.connect();
        boolean result = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        return result; 
    }    
    
    @Override
    public boolean aktivNullen(Kategorie kategorie) throws Exception {
        String dml = "UPDATE Kategorien SET Aktiv = '0' WHERE (KategorienID = '" + kategorie.getKategorienid() + "');";  
        dBConnector.connect();
        boolean result = dBConnector.executeDMLQuery(dml);
        dBConnector.disconnect();
        return result; 
    }    
}