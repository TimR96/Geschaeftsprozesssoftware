/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class Abrechnung {
    
    private int abrechnungsid;
    private String bezeichnung;
    private Double betrag;
    private String zeitstempel;
    private int mitarbeiterid;
    private int zahlungsartid;
    private int kategorieid;

    public Abrechnung(int abrechnungsid, String bezeichnung, Double betrag, String zeitstempel, int mitarbeiterid, int zahlungsartid, int kategorieid) {
        this.abrechnungsid = abrechnungsid;
        this.bezeichnung = bezeichnung;
        this.betrag = betrag;
        this.zeitstempel = zeitstempel;
        this.mitarbeiterid = mitarbeiterid;
        this.zahlungsartid = zahlungsartid;
        this.kategorieid = kategorieid;
    }

    public Abrechnung(String bezeichnung, Double betrag, String zeitstempel, int mitarbeiterid, int zahlungsartid, int kategorieid) {
        this.bezeichnung = bezeichnung;
        this.betrag = betrag;
        this.zeitstempel = zeitstempel;
        this.mitarbeiterid = mitarbeiterid;
        this.zahlungsartid = zahlungsartid;
        this.kategorieid = kategorieid;
    }

    public int getAbrechnungsid() {
        return abrechnungsid;
    }

    public void setAbrechnungsid(int abrechnungsid) {
        this.abrechnungsid = abrechnungsid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getZeitstempel() {
        return zeitstempel;
    }

    public void setZeitstempel(String zeitstempel) {
        this.zeitstempel = zeitstempel;
    }

    public int getMitarbeiterid() {
        return mitarbeiterid;
    }

    public void setMitarbeiterid(int mitarbeiterid) {
        this.mitarbeiterid = mitarbeiterid;
    }

    public int getZahlungsartid() {
        return zahlungsartid;
    }

    public void setZahlungsartid(int zahlungsartid) {
        this.zahlungsartid = zahlungsartid;
    }

    public int getKategorieid() {
        return kategorieid;
    }

    public void setKategorieid(int kategorieid) {
        this.kategorieid = kategorieid;
    }   
}
