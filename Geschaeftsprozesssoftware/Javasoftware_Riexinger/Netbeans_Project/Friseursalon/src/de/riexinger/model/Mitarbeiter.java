/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class Mitarbeiter {
   
    private int mitarbeiterID;    
    private String nachname;    
    private String vorname;
    private String RFID;

    public Mitarbeiter(int mitarbeiterID, String nachname, String vorname, String RFID) {
        this.mitarbeiterID = mitarbeiterID;
        this.nachname = nachname;
        this.vorname = vorname;
        this.RFID = RFID;
    }    
    
    public Mitarbeiter(int mitarbeiterID, String nachname, String vorname) {
        this.mitarbeiterID = mitarbeiterID;
        this.nachname = nachname;        
        this.vorname = vorname;
    }
    
    public Mitarbeiter(String nachname, String vorname) {
        this.nachname = nachname;        
        this.vorname = vorname;
    }    

    public int getMitarbeiterID() {
        return mitarbeiterID;
    }

    public void setMitarbeiterID(int MitarbeiterID) {
        this.mitarbeiterID = MitarbeiterID;
    }    

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }     
    
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    @Override
    public String toString() {
        return vorname + " " + nachname;
    }
}
