package de.riexinger.model;

/**
 *
 * @author Tim
 */
public class Kategorie {

    int kategorienid;
    String kategorie;

    public Kategorie(int kategorienid, String kategorie) {
        this.kategorienid = kategorienid;
        this.kategorie = kategorie;
    }

    public Kategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public int getKategorienid() {
        return kategorienid;
    }

    public void setKategorienid(int kategorienid) {
        this.kategorienid = kategorienid;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }
    
    @Override
    public String toString() {
        return kategorie;
    } 
}
