/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class Zeitbuchung {
    String nachname;
    String vorname;
    String datum;
    String uhrzeit;
    String zeiterfassungsart;
    int zeiterfassungenid;

    public Zeitbuchung(String nachname, String vorname, String datum, String uhrzeit, String zeiterfassungsart, int zeiterfassungenid) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.zeiterfassungsart = zeiterfassungsart;
        this.zeiterfassungenid = zeiterfassungenid;
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

    public String getZeiterfassungsart() {
        return zeiterfassungsart;
    }

    public void setZeiterfassungsart(String zeiterfassungsart) {
        this.zeiterfassungsart = zeiterfassungsart;
    }

    public int getZeiterfassungenid() {
        return zeiterfassungenid;
    }

    public void setZeiterfassungenid(int zeiterfassungenid) {
        this.zeiterfassungenid = zeiterfassungenid;
    }
    
    @Override
    public String toString() {
        return nachname + " - " + vorname + " - " + datum + " - " + uhrzeit + " - " + zeiterfassungsart;
    }   
}
