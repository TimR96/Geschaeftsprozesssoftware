/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class Zeiterfassung {
    
    private int zeiterfassungenid;
    private String datum;
    private String uhrzeit;
    private int mitarbeiterid;
    private int zeiterfassungsartenid;
    
    public Zeiterfassung(int zeiterfassungenid, String datum, String uhrzeit, int mitarbeiterid, int zeiterfassungsartenid)
    {
        this.zeiterfassungenid = zeiterfassungenid;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.mitarbeiterid = mitarbeiterid;
        this.zeiterfassungsartenid = zeiterfassungsartenid;
    }    
    
    public Zeiterfassung(String datum, String uhrzeit, int mitarbeiterid, int zeiterfassungsartenid)
    {
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.mitarbeiterid = mitarbeiterid;
        this.zeiterfassungsartenid = zeiterfassungsartenid;
    }
    
    public int getMitarbeiterid() {
        return mitarbeiterid;
    }

    public void setMitarbeiterid(int mitarbeiterid) {
        this.mitarbeiterid = mitarbeiterid;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(String uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    public int getZeiterfassungsartenid() {
        return zeiterfassungsartenid;
    }

    public void setZeiterfassungsartenid(int zeiterfassungsartenid) {
        this.zeiterfassungsartenid = zeiterfassungsartenid;
    }    
}
