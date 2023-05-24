/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class HeutigeAbrechnung {
    
    private int abrechnungsid;
    private String bezeichnung;
    private double betrag;
    private String zahlungsart;
    private String kategorie; 
    private String vorname;
    private String nachname;

    public HeutigeAbrechnung(int abrechnungsid, String bezeichnng, double betrag, String zahlungsart, String kategorie, String vorname, String nachname) {
        this.abrechnungsid = abrechnungsid;
        this.bezeichnung = bezeichnng;
        this.betrag = betrag;
        this.zahlungsart = zahlungsart;
        this.kategorie = kategorie;
        this.vorname = vorname;
        this.nachname = nachname;
    }

    public int getAbrechnungsid() {
        return abrechnungsid;
    }

    public void setAbrechnungsid(int abrechnungsid) {
        this.abrechnungsid = abrechnungsid;
    }

    public String getBezeichnng() {
        return bezeichnung;
    }

    public void setBezeichnng(String bezeichnng) {
        this.bezeichnung = bezeichnng;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public String getZahlungsart() {
        return zahlungsart;
    }

    public void setZahlungsart(String zahlungsart) {
        this.zahlungsart = zahlungsart;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }
    
    @Override
    public String toString() {
        return bezeichnung + " - " + betrag + " € - " + zahlungsart + " - " + kategorie + " - " + vorname + " " + nachname;
    } 
}
