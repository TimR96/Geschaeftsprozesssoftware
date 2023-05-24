/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class Zahlungsart {
    
    int zahlungsartenid;
    String zahlungsart;
    int aktiv;

    public Zahlungsart(int zahlungsartenid, String zahlungsart) {
        this.zahlungsartenid = zahlungsartenid;
        this.zahlungsart = zahlungsart;
    }

    public Zahlungsart(String zahlungsart, int aktiv) {
        this.zahlungsart = zahlungsart;
        this.aktiv = aktiv;
    }

    public Zahlungsart(String zahlungsart) {
        this.zahlungsart = zahlungsart;
    }
    
    public int getZahlungsartenid() {
        return zahlungsartenid;
    }

    public void setZahlungsartenid(int zahlungsartenid) {
        this.zahlungsartenid = zahlungsartenid;
    }

    public String getZahlungsart() {
        return zahlungsart;
    }

    public void setZahlungsart(String zahlungsart) {
        this.zahlungsart = zahlungsart;
    }

    public int getAktiv() {
        return aktiv;
    }

    public void setAktiv(int aktiv) {
        this.aktiv = aktiv;
    }
        
    @Override
    public String toString() {
        return zahlungsart;
    }      
}
