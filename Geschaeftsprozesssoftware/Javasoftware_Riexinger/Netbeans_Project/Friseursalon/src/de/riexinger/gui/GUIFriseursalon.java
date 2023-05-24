/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package de.riexinger.gui;

import de.riexinger.model.Abrechnung;
import de.riexinger.model.HeutigeAbrechnung;
import de.riexinger.model.Kasse;
import de.riexinger.model.Kategorie;
import de.riexinger.model.Mitarbeiter;
import de.riexinger.RFID.RFID;
import de.riexinger.bericht.Berichterzeugung;
import de.riexinger.file.Konfigurationsdatei;
import de.riexinger.model.Zahlungsart;
import de.riexinger.model.Zeitbuchung;
import de.riexinger.model.Zeiterfassung;
import de.riexinger.persistence.PersistenceAdapterAbrechnung;
import de.riexinger.persistence.PersistenceAdapterKasse;
import de.riexinger.persistence.PersistenceAdapterKategorie;
import de.riexinger.persistence.PersistenceAdapterMitarbeiter;
import de.riexinger.persistence.PersistenceAdapterZahlungsart;
import de.riexinger.persistence.PersistenceAdapterZeiterfassung;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Tim
 */
public class GUIFriseursalon extends javax.swing.JFrame implements PropertyChangeListener {

    private final PersistenceAdapterKasse persistenceAdapterKasse;
    private final PersistenceAdapterMitarbeiter persistenceAdapterMitarbeiter;
    private final PersistenceAdapterZeiterfassung persistenceAdapterZeiterfassung;
    private final PersistenceAdapterZahlungsart persistenceAdapterZahlungsart;
    private final PersistenceAdapterKategorie persistenceAdapterKategorie;
    private final PersistenceAdapterAbrechnung persistenceAdapterAbrechnung;  
    
    private Mitarbeiter[] mitarbeiter;
    private Kategorie[] kategorien;
    private Zahlungsart[] zahlungsarten;  
    private Kasse kasse;
     
    public GUIFriseursalon  (PersistenceAdapterKasse persistenceAdapterKasse, PersistenceAdapterMitarbeiter persistenceAdapterMitarbeiter,
                            PersistenceAdapterZeiterfassung persistenceAdapterZeiterfassung, 
                            PersistenceAdapterZahlungsart persistenceAdapterZahlungsart, 
                            PersistenceAdapterKategorie persistenceAdapterKategorie, PersistenceAdapterAbrechnung persistenceAdapterAbrechnung) throws Exception { 
        
        ImageIcon img = new ImageIcon("pictures\\Logo_Salon_Riexinger_100x100.jpg");
        this.setIconImage(img.getImage());
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);    //GUI soll als Vollbild angezeigt werden
        UIManager.put("OptionPane.yesButtonText", "Ja");
        UIManager.put("OptionPane.noButtonText", "Nein");
        Date date = new Date();
        DateChooserZeitDatum.setDate(date);     //aktuelles Datum soll im DateChooser (Register Zeiterfassung) als default-Wert gesetzt werden
        
        this.persistenceAdapterKasse = persistenceAdapterKasse;
        this.persistenceAdapterMitarbeiter = persistenceAdapterMitarbeiter;
        this.persistenceAdapterZeiterfassung = persistenceAdapterZeiterfassung;
        this.persistenceAdapterZahlungsart = persistenceAdapterZahlungsart;
        this.persistenceAdapterKategorie = persistenceAdapterKategorie;
        this.persistenceAdapterAbrechnung = persistenceAdapterAbrechnung;
        
        ladeKategorienUndZahlungsarten();
        getKassenbestand();
        ladeMitarbeiterInComboboxes();
        ladeZeitbuchungenInListe();
        ladeHeutigeAbrechnungen();
        setAllCheckboxesInvisible();
        showZahlungsartenCheckboxes();
        showKategorieCheckboxes();
        ladeZahlungsartenInComboboxesEinstellungen();
        ladeKategorienInComboboxesEinstellungen();
        RFIDErkennung();     
    }
    
    private void ladeKategorienUndZahlungsarten() {
        try{
            kategorien = persistenceAdapterKategorie.get();
            zahlungsarten = persistenceAdapterZahlungsart.get();        
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Kategorien und/oder Zahlungsarten konnte nicht geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public void ladeZahlungsartenInComboboxesEinstellungen() {
        try{
            Zahlungsart[] alleZahlungsarten = persistenceAdapterZahlungsart.getAlleAusserBar();
            cbxEinstZahlungsarten.removeAllItems();
            for (int i = 0; i < alleZahlungsarten.length; i++) {     
                cbxEinstZahlungsarten.addItem(alleZahlungsarten[i]);
            }      
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Zahlungsarten konnten nicht in Comboboxen geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    } 
    
    public void ladeKategorienInComboboxesEinstellungen() {
        try{
            Kategorie[] alleKategorien = persistenceAdapterKategorie.getAlleAusserAusgabe();
            cbxEinstKategorien.removeAllItems();
            for (int i = 0; i < alleKategorien.length; i++) {        
                cbxEinstKategorien.addItem(alleKategorien[i]);
            }      
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Kategorien konnten nicht in Comboboxen geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }     
    
    private void nurZahlen(java.awt.event.KeyEvent evt) {
        String key = String.valueOf(evt.getKeyChar());
        String erlaubteZeichen = "0123456789";                 // nur diese Zeichen sind erlaubt
        if(!erlaubteZeichen.contains(key)) {
            evt.consume();
        }
    }
    
    private BigDecimal bigDecimalZweiNachkommastellen(BigDecimal wert) {
        if (wert == null) {
            wert = new BigDecimal(0);
        }
        BigDecimal bigDecimal = new BigDecimal(wert.doubleValue()).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }
    
    private void showZahlungsartenCheckboxes() {
        int minimum = 1;
        int maximum = 6;
        JCheckBox[] checkboxesZahlungsarten = {chbAbrZahlungsart1, chbAbrZahlungsart2,
                                               chbAbrZahlungsart3, chbAbrZahlungsart4,
                                               chbAbrZahlungsart5, chbAbrZahlungsart6};
        if (zahlungsarten.length >= minimum && zahlungsarten.length <= maximum) {
            for (int i = 0; i < zahlungsarten.length && i < checkboxesZahlungsarten.length; i++) {
                checkboxesZahlungsarten[i].setVisible(true);
                checkboxesZahlungsarten[i].setText(zahlungsarten[i].getZahlungsart());
            }                
        } 
        else {
            JOptionPane.showMessageDialog(this, "Zu viele oder keine Zahlungsarten. Mindestens eine, maximal 6 erlaubt.\nBitte anpassen und das Programm neu starten.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }    
    }
    
    private void showKategorieCheckboxes() {
        int minimum = 1;
        int maximum = 9;
        JCheckBox[] checkboxesKategorien = {chbAbrKategorie1, chbAbrKategorie2,
                                            chbAbrKategorie3, chbAbrKategorie4,
                                            chbAbrKategorie5, chbAbrKategorie6,
                                            chbAbrKategorie7, chbAbrKategorie8,
                                            chbAbrKategorie9};
        if (kategorien.length >= minimum && kategorien.length <= maximum) {
            for (int i = 0; i < kategorien.length && i < checkboxesKategorien.length; i++) {
                checkboxesKategorien[i].setVisible(true);
                checkboxesKategorien[i].setText(kategorien[i].getKategorie());
            }                
        } 
        else {
            JOptionPane.showMessageDialog(this, "Zu viele oder keine Kategorien. Mindestens eine, maximal 9 erlaubt.\nBitte anpassen und das Programm neu starten.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }    
    }   
    
    private int getSelectedZahlungsartenIDCheckbox() {
        int zahlungsartid = 99;
        JCheckBox[] checkboxesZahlungsarten = {chbAbrZahlungsart1, chbAbrZahlungsart2,
                                               chbAbrZahlungsart3, chbAbrZahlungsart4,
                                               chbAbrZahlungsart5, chbAbrZahlungsart6};
        for (int i = 0; i < checkboxesZahlungsarten.length; i++) {
            if (checkboxesZahlungsarten[i].isSelected()) {
                zahlungsartid = zahlungsarten[i].getZahlungsartenid();
            }
        }
        return zahlungsartid;
    }
    
    private int getSelectedKategorienIDCheckbox() {
        int kategorienid = 99;
        JCheckBox[] checkboxesKategorien = {chbAbrKategorie1, chbAbrKategorie2,
                                            chbAbrKategorie3, chbAbrKategorie4,
                                            chbAbrKategorie5, chbAbrKategorie6,
                                            chbAbrKategorie7, chbAbrKategorie8,
                                            chbAbrKategorie9};
        for (int i = 0; i < checkboxesKategorien.length; i++) {
            if (checkboxesKategorien[i].isSelected()) {
                kategorienid = kategorien[i].getKategorienid();
            }
        }
        return kategorienid;
    }    
    
    private void setAllCheckboxesInvisible() {
        chbAbrZahlungsart1.setVisible(false);
        chbAbrZahlungsart2.setVisible(false);
        chbAbrZahlungsart3.setVisible(false);
        chbAbrZahlungsart4.setVisible(false);
        chbAbrZahlungsart5.setVisible(false);
        chbAbrZahlungsart6.setVisible(false);
        
        chbAbrKategorie1.setVisible(false);
        chbAbrKategorie2.setVisible(false);
        chbAbrKategorie3.setVisible(false);
        chbAbrKategorie4.setVisible(false);
        chbAbrKategorie5.setVisible(false);
        chbAbrKategorie6.setVisible(false);
        chbAbrKategorie7.setVisible(false);
        chbAbrKategorie8.setVisible(false);
        chbAbrKategorie9.setVisible(false);
    }
    
    private void setZahlungsartenCheckboxesInvisible() {
        chbAbrZahlungsart1.setVisible(false);
        chbAbrZahlungsart2.setVisible(false);
        chbAbrZahlungsart3.setVisible(false);
        chbAbrZahlungsart4.setVisible(false);
        chbAbrZahlungsart5.setVisible(false);
        chbAbrZahlungsart6.setVisible(false);
    }
    
    private void setKategorienCheckboxesInvisible() {
        chbAbrKategorie1.setVisible(false);
        chbAbrKategorie2.setVisible(false);
        chbAbrKategorie3.setVisible(false);
        chbAbrKategorie4.setVisible(false);
        chbAbrKategorie5.setVisible(false);
        chbAbrKategorie6.setVisible(false);
        chbAbrKategorie7.setVisible(false);
        chbAbrKategorie8.setVisible(false);
        chbAbrKategorie9.setVisible(false);
    }    
    
    private void getKassenbestand() {
        try {
            kasse = persistenceAdapterKasse.get(); //Kassenbestand aus DB lesen
            kasse.setKassenbestand(bigDecimalZweiNachkommastellen(kasse.getKassenbestand()));
            String betrag = kasse.getKassenbestand().toString(); 
            kassenbestandFormatieren(betrag);           
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Kassenbestand konnte nicht geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setKassenbestand(BigDecimal kassenbestand) {
        this.kasse.setKassenbestand(kassenbestand);
    }

    public void kassenbestandVerrechnen(BigDecimal betrag) throws Exception {
        BigDecimal alterKassenbestand = kasse.getKassenbestand();
        BigDecimal neuerKassenbestand = alterKassenbestand.add(betrag);
        BigDecimal neuerBetrag = bigDecimalZweiNachkommastellen(neuerKassenbestand);
        persistenceAdapterKasse.set(neuerBetrag);
        kasse.setKassenbestand(neuerBetrag);
    }
    
    public void kassenbestandFormatieren(String kassenbestand) throws Exception {
        String[] euroCentString = kassenbestand.split("\\.");
        lblAbrechnungKassenbestand.setText(euroCentString[0] + "," + euroCentString[1] + " €");
    }
    
    public void ladeMitarbeiterInComboboxes() {
        try{
            mitarbeiter = persistenceAdapterMitarbeiter.get();
            cbxAbrMitarbeiter.removeAllItems();
            cbxMitMitarbeiter.removeAllItems();
            cbxZeitMitarbeiter.removeAllItems();
            cbxMitMitarbeiterRFID.removeAllItems();
            int anzahlMitarbeiter = mitarbeiter.length;
            if(anzahlMitarbeiter > 0) {
                for (int i = 0; i < anzahlMitarbeiter; i++) {
                    Mitarbeiter mitarbeiterObj = new Mitarbeiter(mitarbeiter[i].getMitarbeiterID(), mitarbeiter[i].getNachname(), mitarbeiter[i].getVorname(), mitarbeiter[i].getRFID());
                    cbxAbrMitarbeiter.addItem(mitarbeiter[i]);
                    cbxMitMitarbeiter.addItem(mitarbeiter[i]);
                    cbxZeitMitarbeiter.addItem(mitarbeiter[i]);
                    cbxMitMitarbeiterRFID.addItem(mitarbeiter[i]);
                }
            }
            cbxAbrMitarbeiter.setSelectedItem(null);
            cbxMitMitarbeiter.setSelectedItem(null);
            cbxZeitMitarbeiter.setSelectedItem(null);
            cbxMitMitarbeiterRFID.setSelectedItem(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Mitarbeiter konnten nicht geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void ladeZeitbuchungenInListe() {
        try{
            Zeitbuchung[] zeitbuchungen = persistenceAdapterZeiterfassung.getToday();
            lstZeitBuchungskorrektur.setListData(zeitbuchungen);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Heutige Buchungen konnten nicht in die Liste geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }  
    
    private void aktualisiereZahlungsarten() {
        try{
            zahlungsarten = persistenceAdapterZahlungsart.get();        
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Zahlungsarten konnte nicht geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }    
        setZahlungsartenCheckboxesInvisible();
        showZahlungsartenCheckboxes();
        ladeZahlungsartenInComboboxesEinstellungen();
    }
    
    private void aktualisiereKategorien()
    {
        try{
            kategorien = persistenceAdapterKategorie.get();        
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Kategorien konnte nicht geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }    
        setKategorienCheckboxesInvisible();
        showKategorieCheckboxes();
        ladeKategorienInComboboxesEinstellungen();
    }    
    
    public void ladeHeutigeAbrechnungen()
    {
        try{
        HeutigeAbrechnung[] heutigeAbrechnungen = persistenceAdapterAbrechnung.getToday();
        lstTagesbericht.setListData(heutigeAbrechnungen);
        Double tagesumsatz = 0.0;
        for (int i = 0; i < heutigeAbrechnungen.length; i++) {
            tagesumsatz += heutigeAbrechnungen[i].getBetrag();
        }
        lblTagesberichtSumme.setText(tagesumsatz+" €");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Heutige Abrechnungen konnten nicht in die Liste geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }      
    
    public boolean kategorieSelected() {
        boolean selected = false;
        if (chbAbrKategorie1.isSelected() || chbAbrKategorie2.isSelected() ||
            chbAbrKategorie3.isSelected() || chbAbrKategorie4.isSelected() ||
            chbAbrKategorie5.isSelected() || chbAbrKategorie6.isSelected() || 
            chbAbrKategorie7.isSelected() || chbAbrKategorie8.isSelected() || 
            chbAbrKategorie9.isSelected()) {
            selected = true;
        } 
        return selected;
    }
    
    public boolean zahlungsartSelected() {
        boolean selected = false;
        if (chbAbrZahlungsart1.isSelected() || chbAbrZahlungsart2.isSelected() ||
            chbAbrZahlungsart3.isSelected() || chbAbrZahlungsart4.isSelected() ||
            chbAbrZahlungsart5.isSelected() || chbAbrZahlungsart6.isSelected()) {
            selected = true;
        }
        return selected;
    }
    
    public boolean kategorienAusserAusgabe() {
        boolean selected = false;
        if (chbAbrKategorie1.isSelected() || chbAbrKategorie2.isSelected() ||
            chbAbrKategorie3.isSelected() || chbAbrKategorie4.isSelected() ||
            chbAbrKategorie5.isSelected() || chbAbrKategorie7.isSelected() || 
            chbAbrKategorie8.isSelected() || chbAbrKategorie9.isSelected()) {
            selected = true;
        } 
        return selected;
    }

    private String erzeugeZeitstempel() {
        Date dateTime = new Date();                                     //jetziges Datum erzeugen
        DateFormat dateFormatAbr = new SimpleDateFormat("yyyy-MM-dd");  //Datum formatieren
        String datumAbrFor = dateFormatAbr.format(dateTime);
        DateFormat uhrzeitFormatAbr = new SimpleDateFormat("HH:mm:ss"); //Uhrzeit formatieren
        String uhrzeitAbrFor = uhrzeitFormatAbr.format(dateTime);
        String zeitstempel = datumAbrFor + " " +uhrzeitAbrFor;   
        return  zeitstempel;
    }
    
    private String erzeugeZeitstempelFuerBericht() {
        Date dateTime = new Date();                                             //jetziges Datum erzeugen
        DateFormat dateFormatAbr = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss"); //Datum formatieren
        String datumAbrFor = dateFormatAbr.format(dateTime);
        String zeitstempel = datumAbrFor;   
        return  zeitstempel;
    }    
    
    private BigDecimal holeAbrechnungsbetrag() {
        String betrag = txtAbrEuro.getText() + "." + txtAbrCent.getText();
        BigDecimal betragD = new BigDecimal(betrag);
        return bigDecimalZweiNachkommastellen(betragD);
    }
    
    /**
    * Diese Methode speichert den RFID-Code in der Mitarbeitertabelle der 
    * Datenbank und verknüpft ihn mit dem ausgewählten Mitarbeiter. Es wird
    * überprüft, ob der Code bereits einem anderen Mitarbeiter zugewiesen
    * wurde. Ist dies der Fall, wird bei diesem Mitarbeiter der Eintrag auf
    * null gesetzt, anschließend wird dem ausgewählten Mitarbeiter der Code
    * zugeteilt.
     * @param code RFID-Code
     */
    private void speichereChip(String code) {
        try {
            Mitarbeiter mit = (Mitarbeiter)cbxMitMitarbeiterRFID.getSelectedItem();
            int mitarbeiterid = persistenceAdapterMitarbeiter.getMitarbeiterIDMitDoppelterRFID(code);
            if (mitarbeiterid != 0) {
                persistenceAdapterMitarbeiter.deleteDoppelteRFID(mitarbeiterid);
            }
            persistenceAdapterMitarbeiter.setRFID(mit.getMitarbeiterID(), code);
            btnMitVerknuepfen.setText("Verknüpfen");
            ladeMitarbeiterInComboboxes();
            JOptionPane.showMessageDialog(this, "Chip " + code + " mit " + mit.toString() + " verknüpft", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Zuordnen des Chips\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
    *Setzt alle Einträge in Mitarbeiter-Comboboxes auf null bzw. nicht 
    *ausgewählt
    */     
    private void comboboxesDefaultNull() {
        cbxAbrMitarbeiter.setSelectedItem(null);
        cbxMitMitarbeiter.setSelectedItem(null);
        cbxZeitMitarbeiter.setSelectedItem(null);
    }
    
    /**
    *Die Methode lässt die RFID-Konfigurationsdatei durch die statische Klasse
    *"Konfigurationsdatei" auslesen und setzt den enstprechenden Status.
    */    
    private void RFIDErkennung() {
        try {
            String status = Konfigurationsdatei.leseRFIDStatus();
            if (Integer.parseInt(status) == 1) {
                rbtnEinstRFIDAktiviert.setSelected(true);
                RFIDaktiviert();
            }
            else {
                rbtnEinstRFIDDeaktiviert.setSelected(true);
                RFIDdeaktiviert();
            }   
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Auslesen der RFID-Einstellungen\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
    *Wenn RFID aktiviert wurde, kann der Benutzer Einträge in den Mitarbeiter-
    *Comboboxes nicht mehr selbst auswählen, sondern muss sich mittels RFID-
    *Chip authentifizieren. Die dazu nötigen Buttons werden sichtbar. 
    */    
    private void RFIDaktiviert() {
        btnAbrRFID.setVisible(true);
        btnMitRFID.setVisible(true);
        btnZeitRFID.setVisible(true);
        comboboxesDefaultNull();
        cbxAbrMitarbeiter.setEnabled(false);
        cbxMitMitarbeiter.setEnabled(false);
        cbxZeitMitarbeiter.setEnabled(false); 
    }
    
    /**
    *Wenn RFID deaktiviert wurde, kann der Benutzer Einträge in den Mitarbeiter-
    *Comboboxes selbst auswählen. Die RFID-Buttons werden ausgeblendet.
    */ 
    private void RFIDdeaktiviert() {
        btnAbrRFID.setVisible(false);
        btnMitRFID.setVisible(false);
        btnZeitRFID.setVisible(false);
        cbxAbrMitarbeiter.setEnabled(true);
        cbxMitMitarbeiter.setEnabled(true);
        cbxZeitMitarbeiter.setEnabled(true); 
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupZahlungsart = new javax.swing.ButtonGroup();
        buttonGroupKategorie = new javax.swing.ButtonGroup();
        buttonGroupRFID = new javax.swing.ButtonGroup();
        tbpRegisterkarte = new javax.swing.JTabbedPane();
        pnlAbrechnung = new javax.swing.JPanel();
        tpnlAbrechnung = new javax.swing.JPanel();
        lblAbrBezeichnung = new javax.swing.JLabel();
        txtAbrBezeichnung = new javax.swing.JTextField();
        lblAbrBetrag = new javax.swing.JLabel();
        txtAbrEuro = new javax.swing.JTextField();
        lblAbrZahlungsart = new javax.swing.JLabel();
        chbAbrZahlungsart1 = new javax.swing.JCheckBox();
        chbAbrZahlungsart2 = new javax.swing.JCheckBox();
        lblAbrKategorie = new javax.swing.JLabel();
        chbAbrKategorie3 = new javax.swing.JCheckBox();
        chbAbrKategorie1 = new javax.swing.JCheckBox();
        chbAbrKategorie2 = new javax.swing.JCheckBox();
        chbAbrKategorie6 = new javax.swing.JCheckBox();
        chbAbrKategorie4 = new javax.swing.JCheckBox();
        chbAbrKategorie5 = new javax.swing.JCheckBox();
        btnAbrSpeichern = new javax.swing.JButton();
        btnAbrRueckgaengig = new javax.swing.JButton();
        lblAbrMitarbeiter = new javax.swing.JLabel();
        cbxAbrMitarbeiter = new javax.swing.JComboBox<>();
        chbAbrZahlungsart3 = new javax.swing.JCheckBox();
        chbAbrKategorie7 = new javax.swing.JCheckBox();
        chbAbrKategorie8 = new javax.swing.JCheckBox();
        chbAbrKategorie9 = new javax.swing.JCheckBox();
        chbAbrZahlungsart4 = new javax.swing.JCheckBox();
        chbAbrZahlungsart5 = new javax.swing.JCheckBox();
        chbAbrZahlungsart6 = new javax.swing.JCheckBox();
        lblAbrEuro = new javax.swing.JLabel();
        txtAbrCent = new javax.swing.JTextField();
        lblAbrCent = new javax.swing.JLabel();
        btnAbrRFID = new javax.swing.JButton();
        tpnlKassenbestand = new javax.swing.JPanel();
        lblAbrechnungAktuellerKassenbestand = new javax.swing.JLabel();
        lblAbrechnungKassenbestand = new javax.swing.JLabel();
        btnAbrechnungKassenbestandAendern = new javax.swing.JButton();
        pnlMitarbeiter = new javax.swing.JPanel();
        pnlUser = new javax.swing.JPanel();
        tpnlMitarbeiterHinzufuegen = new javax.swing.JPanel();
        lblMitVornameHinzu = new javax.swing.JLabel();
        lblMitNachnameHinzu = new javax.swing.JLabel();
        txtMitVornameHinzu = new javax.swing.JTextField();
        txtMitNachnameHinzu = new javax.swing.JTextField();
        btnMitHinzufuegen = new javax.swing.JButton();
        tpnlMitarbeiterLoeschen = new javax.swing.JPanel();
        lblMitMitarbeiterLoe = new javax.swing.JLabel();
        btnMitLoeschen = new javax.swing.JButton();
        cbxMitMitarbeiter = new javax.swing.JComboBox<>();
        btnMitRFID = new javax.swing.JButton();
        tpnlMitarbeiterMitRFIDVerknuepfen = new javax.swing.JPanel();
        cbxMitMitarbeiterRFID = new javax.swing.JComboBox<>();
        btnMitVerknuepfen = new javax.swing.JButton();
        pnlTagesbericht = new javax.swing.JPanel();
        tpnlTagesbericht = new javax.swing.JPanel();
        jScrollPaneTagesbericht = new javax.swing.JScrollPane();
        lstTagesbericht = new javax.swing.JList<>();
        lblTagesbericht = new javax.swing.JLabel();
        lblTagesberichtSumme = new javax.swing.JLabel();
        pnlZeiterfassung = new javax.swing.JPanel();
        tpnlZeiterfassung = new javax.swing.JPanel();
        DateChooserZeitDatum = new com.toedter.calendar.JDateChooser();
        lblZeitMitarbeiter = new javax.swing.JLabel();
        cbxZeitMitarbeiter = new javax.swing.JComboBox<>();
        lblZeitDatum = new javax.swing.JLabel();
        lblZeitUhrzeit = new javax.swing.JLabel();
        cbxZeitStunde = new javax.swing.JComboBox<>();
        cbxZeitMinute = new javax.swing.JComboBox<>();
        lblZeitDoppelpunkt = new javax.swing.JLabel();
        lblZeitUhr = new javax.swing.JLabel();
        btnZeitKommen = new javax.swing.JButton();
        btnZeitGehen = new javax.swing.JButton();
        btnZeitAktuelleUhrzeit = new javax.swing.JButton();
        btnZeitRFID = new javax.swing.JButton();
        tpnlZeitbuchungKorrigieren = new javax.swing.JPanel();
        btnZeitLoeschen = new javax.swing.JButton();
        jspZeitpuchungen = new javax.swing.JScrollPane();
        lstZeitBuchungskorrektur = new javax.swing.JList<>();
        pnlEinstellungen = new javax.swing.JPanel();
        pnlEinstellungenBeschreibung = new javax.swing.JPanel();
        lblEinstBeschreibung = new javax.swing.JLabel();
        tpnlEinstZahlungsartAendern = new javax.swing.JPanel();
        lblEinstZahlartAuswaehlen = new javax.swing.JLabel();
        cbxEinstZahlungsarten = new javax.swing.JComboBox<>();
        lblEinstMax6Zahlarten = new javax.swing.JLabel();
        lblEinstZahlartBezAendern = new javax.swing.JLabel();
        txtEinstZahlartAendern = new javax.swing.JTextField();
        btnEinstZahlartAendern = new javax.swing.JButton();
        btnEinstZahlartLoeschen = new javax.swing.JButton();
        tpnlEinstKategorieAendern = new javax.swing.JPanel();
        lblEinstMax9Kategorien = new javax.swing.JLabel();
        lblEinstKategorieAuswaehlen = new javax.swing.JLabel();
        cbxEinstKategorien = new javax.swing.JComboBox<>();
        lblEinstKategorieBezAendern = new javax.swing.JLabel();
        txtEinstKategorieAendern = new javax.swing.JTextField();
        btnEinstKategorieAendern = new javax.swing.JButton();
        btnEinstKategorieLoeschen = new javax.swing.JButton();
        tpnlEinstRFIDErkennung = new javax.swing.JPanel();
        rbtnEinstRFIDAktiviert = new javax.swing.JRadioButton();
        rbtnEinstRFIDDeaktiviert = new javax.swing.JRadioButton();
        pnlMonatsbericht = new javax.swing.JPanel();
        tpnlMonatsbericht = new javax.swing.JPanel();
        lblMonatsberichtErzeuge = new javax.swing.JLabel();
        lblMonatsberichtMonat = new javax.swing.JLabel();
        cbxMonatsberichtMonat = new javax.swing.JComboBox<>();
        lblMonatsberichtJahr = new javax.swing.JLabel();
        MonatsberichtYearChooser = new com.toedter.calendar.JYearChooser();
        btnMonatsberichteErzeuge = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Friseursalon Riexinger");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        pnlAbrechnung.setPreferredSize(new java.awt.Dimension(1300, 627));

        tpnlAbrechnung.setBorder(javax.swing.BorderFactory.createTitledBorder("Abrechnung"));
        tpnlAbrechnung.setPreferredSize(new java.awt.Dimension(500, 460));

        lblAbrBezeichnung.setText("Bezeichnung:");

        lblAbrBetrag.setText("Betrag:");

        txtAbrEuro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAbrEuroKeyTyped(evt);
            }
        });

        lblAbrZahlungsart.setText("Zahlungsart:");

        buttonGroupZahlungsart.add(chbAbrZahlungsart1);
        chbAbrZahlungsart1.setText("Z1");

        buttonGroupZahlungsart.add(chbAbrZahlungsart2);
        chbAbrZahlungsart2.setText("Z2");

        lblAbrKategorie.setText("Kategorie:");

        buttonGroupKategorie.add(chbAbrKategorie3);
        chbAbrKategorie3.setText("K3");

        buttonGroupKategorie.add(chbAbrKategorie1);
        chbAbrKategorie1.setText("K1");

        buttonGroupKategorie.add(chbAbrKategorie2);
        chbAbrKategorie2.setText("K2");

        buttonGroupKategorie.add(chbAbrKategorie6);
        chbAbrKategorie6.setText("K6");

        buttonGroupKategorie.add(chbAbrKategorie4);
        chbAbrKategorie4.setText("K4");

        buttonGroupKategorie.add(chbAbrKategorie5);
        chbAbrKategorie5.setText("K5");

        btnAbrSpeichern.setText("Speichern");
        btnAbrSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrSpeichernActionPerformed(evt);
            }
        });

        btnAbrRueckgaengig.setText("Rückgängig");
        btnAbrRueckgaengig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrRueckgaengigActionPerformed(evt);
            }
        });

        lblAbrMitarbeiter.setText("Mitarbeiter:");

        cbxAbrMitarbeiter.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        buttonGroupZahlungsart.add(chbAbrZahlungsart3);
        chbAbrZahlungsart3.setText("Z3");

        buttonGroupKategorie.add(chbAbrKategorie7);
        chbAbrKategorie7.setText("K7");

        buttonGroupKategorie.add(chbAbrKategorie8);
        chbAbrKategorie8.setText("K8");

        buttonGroupKategorie.add(chbAbrKategorie9);
        chbAbrKategorie9.setText("K9");

        buttonGroupZahlungsart.add(chbAbrZahlungsart4);
        chbAbrZahlungsart4.setText("Z4");

        buttonGroupZahlungsart.add(chbAbrZahlungsart5);
        chbAbrZahlungsart5.setText("Z5");

        buttonGroupZahlungsart.add(chbAbrZahlungsart6);
        chbAbrZahlungsart6.setText("Z6");

        lblAbrEuro.setText("Euro");

        txtAbrCent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAbrCentActionPerformed(evt);
            }
        });
        txtAbrCent.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAbrCentKeyTyped(evt);
            }
        });

        lblAbrCent.setText("Cent");

        btnAbrRFID.setText("RFID-Chip scannen");
        btnAbrRFID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnAbrRFIDFocusLost(evt);
            }
        });
        btnAbrRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrRFIDActionPerformed(evt);
            }
        });
        btnAbrRFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAbrRFIDKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout tpnlAbrechnungLayout = new javax.swing.GroupLayout(tpnlAbrechnung);
        tpnlAbrechnung.setLayout(tpnlAbrechnungLayout);
        tpnlAbrechnungLayout.setHorizontalGroup(
            tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                        .addComponent(btnAbrSpeichern, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 381, Short.MAX_VALUE))
                    .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                        .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblAbrBezeichnung)
                            .addComponent(lblAbrBetrag)
                            .addComponent(lblAbrZahlungsart))
                        .addGap(8, 8, 8)
                        .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtAbrBezeichnung, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                                        .addComponent(txtAbrEuro, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblAbrEuro)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtAbrCent, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblAbrCent)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(chbAbrKategorie4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrKategorie7, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrZahlungsart1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrZahlungsart4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrKategorie1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chbAbrKategorie2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrKategorie5, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrKategorie8, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrZahlungsart5, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrZahlungsart2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chbAbrKategorie6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chbAbrKategorie9, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(chbAbrKategorie3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrZahlungsart6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chbAbrZahlungsart3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(76, Short.MAX_VALUE))))
                    .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                        .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                                .addComponent(lblAbrMitarbeiter)
                                .addGap(18, 18, 18)
                                .addComponent(cbxAbrMitarbeiter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblAbrKategorie)
                                .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                                    .addGap(173, 173, 173)
                                    .addComponent(btnAbrRueckgaengig, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAbrRFID)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        tpnlAbrechnungLayout.setVerticalGroup(
            tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlAbrechnungLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbrBezeichnung)
                    .addComponent(txtAbrBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbrBetrag)
                    .addComponent(txtAbrEuro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAbrEuro)
                    .addComponent(txtAbrCent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAbrCent))
                .addGap(18, 18, 18)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbrZahlungsart)
                    .addComponent(chbAbrZahlungsart1)
                    .addComponent(chbAbrZahlungsart2)
                    .addComponent(chbAbrZahlungsart3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chbAbrZahlungsart4)
                    .addComponent(chbAbrZahlungsart5)
                    .addComponent(chbAbrZahlungsart6))
                .addGap(18, 18, 18)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbrKategorie)
                    .addComponent(chbAbrKategorie3)
                    .addComponent(chbAbrKategorie1)
                    .addComponent(chbAbrKategorie2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chbAbrKategorie4)
                    .addComponent(chbAbrKategorie5)
                    .addComponent(chbAbrKategorie6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chbAbrKategorie7)
                    .addComponent(chbAbrKategorie8)
                    .addComponent(chbAbrKategorie9))
                .addGap(43, 43, 43)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbrMitarbeiter)
                    .addComponent(cbxAbrMitarbeiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAbrRFID))
                .addGap(48, 48, 48)
                .addGroup(tpnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAbrRueckgaengig, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAbrSpeichern, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(183, Short.MAX_VALUE))
        );

        tpnlKassenbestand.setBorder(javax.swing.BorderFactory.createTitledBorder("Kassenbestand"));
        tpnlKassenbestand.setPreferredSize(new java.awt.Dimension(520, 616));

        lblAbrechnungAktuellerKassenbestand.setText("aktueller Kassenbestand:");

        lblAbrechnungKassenbestand.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblAbrechnungKassenbestand.setText("Bitte eintragen");

        btnAbrechnungKassenbestandAendern.setText("Kassenbestand anpassen");
        btnAbrechnungKassenbestandAendern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrechnungKassenbestandAendernActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tpnlKassenbestandLayout = new javax.swing.GroupLayout(tpnlKassenbestand);
        tpnlKassenbestand.setLayout(tpnlKassenbestandLayout);
        tpnlKassenbestandLayout.setHorizontalGroup(
            tpnlKassenbestandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlKassenbestandLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlKassenbestandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tpnlKassenbestandLayout.createSequentialGroup()
                        .addComponent(lblAbrechnungAktuellerKassenbestand)
                        .addGap(18, 18, 18)
                        .addComponent(lblAbrechnungKassenbestand, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAbrechnungKassenbestandAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(201, Short.MAX_VALUE))
        );
        tpnlKassenbestandLayout.setVerticalGroup(
            tpnlKassenbestandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlKassenbestandLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(tpnlKassenbestandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbrechnungKassenbestand)
                    .addComponent(lblAbrechnungAktuellerKassenbestand))
                .addGap(32, 32, 32)
                .addComponent(btnAbrechnungKassenbestandAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlAbrechnungLayout = new javax.swing.GroupLayout(pnlAbrechnung);
        pnlAbrechnung.setLayout(pnlAbrechnungLayout);
        pnlAbrechnungLayout.setHorizontalGroup(
            pnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAbrechnungLayout.createSequentialGroup()
                .addComponent(tpnlAbrechnung, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnlKassenbestand, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(334, Short.MAX_VALUE))
        );
        pnlAbrechnungLayout.setVerticalGroup(
            pnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAbrechnungLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAbrechnungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpnlAbrechnung, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAbrechnungLayout.createSequentialGroup()
                        .addComponent(tpnlKassenbestand, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        tbpRegisterkarte.addTab("Abrechnung", pnlAbrechnung);

        tpnlMitarbeiterHinzufuegen.setBorder(javax.swing.BorderFactory.createTitledBorder("Mitarbeiter hinzufügen"));

        lblMitVornameHinzu.setText("Vorname:");

        lblMitNachnameHinzu.setText("Nachname:");

        btnMitHinzufuegen.setText("Hinzufügen");
        btnMitHinzufuegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMitHinzufuegenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tpnlMitarbeiterHinzufuegenLayout = new javax.swing.GroupLayout(tpnlMitarbeiterHinzufuegen);
        tpnlMitarbeiterHinzufuegen.setLayout(tpnlMitarbeiterHinzufuegenLayout);
        tpnlMitarbeiterHinzufuegenLayout.setHorizontalGroup(
            tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMitarbeiterHinzufuegenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tpnlMitarbeiterHinzufuegenLayout.createSequentialGroup()
                        .addGroup(tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMitVornameHinzu)
                            .addComponent(lblMitNachnameHinzu))
                        .addGap(38, 38, 38)
                        .addGroup(tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMitNachnameHinzu, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                            .addComponent(txtMitVornameHinzu)))
                    .addComponent(btnMitHinzufuegen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(249, Short.MAX_VALUE))
        );
        tpnlMitarbeiterHinzufuegenLayout.setVerticalGroup(
            tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMitarbeiterHinzufuegenLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMitVornameHinzu)
                    .addComponent(txtMitVornameHinzu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(tpnlMitarbeiterHinzufuegenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMitNachnameHinzu)
                    .addComponent(txtMitNachnameHinzu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(btnMitHinzufuegen, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tpnlMitarbeiterLoeschen.setBorder(javax.swing.BorderFactory.createTitledBorder("Mitarbeiter löschen"));

        lblMitMitarbeiterLoe.setText("Mitarbeiter:");

        btnMitLoeschen.setText("Löschen");
        btnMitLoeschen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMitLoeschenActionPerformed(evt);
            }
        });

        cbxMitMitarbeiter.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        btnMitRFID.setText("RFID-Chip scannen");
        btnMitRFID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnMitRFIDFocusLost(evt);
            }
        });
        btnMitRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMitRFIDActionPerformed(evt);
            }
        });
        btnMitRFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnMitRFIDKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout tpnlMitarbeiterLoeschenLayout = new javax.swing.GroupLayout(tpnlMitarbeiterLoeschen);
        tpnlMitarbeiterLoeschen.setLayout(tpnlMitarbeiterLoeschenLayout);
        tpnlMitarbeiterLoeschenLayout.setHorizontalGroup(
            tpnlMitarbeiterLoeschenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tpnlMitarbeiterLoeschenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlMitarbeiterLoeschenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tpnlMitarbeiterLoeschenLayout.createSequentialGroup()
                        .addComponent(lblMitMitarbeiterLoe)
                        .addGap(18, 18, 18)
                        .addComponent(cbxMitMitarbeiter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnMitLoeschen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMitRFID)
                .addContainerGap(652, Short.MAX_VALUE))
        );
        tpnlMitarbeiterLoeschenLayout.setVerticalGroup(
            tpnlMitarbeiterLoeschenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMitarbeiterLoeschenLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(tpnlMitarbeiterLoeschenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMitMitarbeiterLoe)
                    .addComponent(cbxMitMitarbeiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMitRFID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
                .addComponent(btnMitLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tpnlMitarbeiterMitRFIDVerknuepfen.setBorder(javax.swing.BorderFactory.createTitledBorder("Mitarbeiter mit RFID-Chip verknüpfen"));

        btnMitVerknuepfen.setText("Verknüpfen");
        btnMitVerknuepfen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnMitVerknuepfenFocusLost(evt);
            }
        });
        btnMitVerknuepfen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMitVerknuepfenActionPerformed(evt);
            }
        });
        btnMitVerknuepfen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnMitVerknuepfenKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout tpnlMitarbeiterMitRFIDVerknuepfenLayout = new javax.swing.GroupLayout(tpnlMitarbeiterMitRFIDVerknuepfen);
        tpnlMitarbeiterMitRFIDVerknuepfen.setLayout(tpnlMitarbeiterMitRFIDVerknuepfenLayout);
        tpnlMitarbeiterMitRFIDVerknuepfenLayout.setHorizontalGroup(
            tpnlMitarbeiterMitRFIDVerknuepfenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMitarbeiterMitRFIDVerknuepfenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlMitarbeiterMitRFIDVerknuepfenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMitVerknuepfen, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(cbxMitMitarbeiterRFID, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tpnlMitarbeiterMitRFIDVerknuepfenLayout.setVerticalGroup(
            tpnlMitarbeiterMitRFIDVerknuepfenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMitarbeiterMitRFIDVerknuepfenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbxMitMitarbeiterRFID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMitVerknuepfen, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlUserLayout = new javax.swing.GroupLayout(pnlUser);
        pnlUser.setLayout(pnlUserLayout);
        pnlUserLayout.setHorizontalGroup(
            pnlUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tpnlMitarbeiterLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlUserLayout.createSequentialGroup()
                        .addComponent(tpnlMitarbeiterHinzufuegen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tpnlMitarbeiterMitRFIDVerknuepfen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(330, Short.MAX_VALUE))
        );
        pnlUserLayout.setVerticalGroup(
            pnlUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpnlMitarbeiterMitRFIDVerknuepfen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tpnlMitarbeiterHinzufuegen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnlMitarbeiterLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlMitarbeiterLayout = new javax.swing.GroupLayout(pnlMitarbeiter);
        pnlMitarbeiter.setLayout(pnlMitarbeiterLayout);
        pnlMitarbeiterLayout.setHorizontalGroup(
            pnlMitarbeiterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlMitarbeiterLayout.setVerticalGroup(
            pnlMitarbeiterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMitarbeiterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        tbpRegisterkarte.addTab("Mitarbeiter bearbeiten", pnlMitarbeiter);

        tpnlTagesbericht.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jScrollPaneTagesbericht.setViewportView(lstTagesbericht);

        lblTagesbericht.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblTagesbericht.setText("Tagesumsatz:");

        lblTagesberichtSumme.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblTagesberichtSumme.setText("€");

        javax.swing.GroupLayout tpnlTagesberichtLayout = new javax.swing.GroupLayout(tpnlTagesbericht);
        tpnlTagesbericht.setLayout(tpnlTagesberichtLayout);
        tpnlTagesberichtLayout.setHorizontalGroup(
            tpnlTagesberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlTagesberichtLayout.createSequentialGroup()
                .addComponent(jScrollPaneTagesbericht, javax.swing.GroupLayout.PREFERRED_SIZE, 1060, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 336, Short.MAX_VALUE))
            .addGroup(tpnlTagesberichtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTagesbericht)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTagesberichtSumme)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tpnlTagesberichtLayout.setVerticalGroup(
            tpnlTagesberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlTagesberichtLayout.createSequentialGroup()
                .addComponent(jScrollPaneTagesbericht, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tpnlTagesberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTagesbericht)
                    .addComponent(lblTagesberichtSumme))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlTagesberichtLayout = new javax.swing.GroupLayout(pnlTagesbericht);
        pnlTagesbericht.setLayout(pnlTagesberichtLayout);
        pnlTagesberichtLayout.setHorizontalGroup(
            pnlTagesberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnlTagesbericht, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlTagesberichtLayout.setVerticalGroup(
            pnlTagesberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTagesberichtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnlTagesbericht, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbpRegisterkarte.addTab("Tagesbericht", pnlTagesbericht);

        tpnlZeiterfassung.setBorder(javax.swing.BorderFactory.createTitledBorder("Zeitbuchung"));

        lblZeitMitarbeiter.setText("Mitarbeiter:");

        lblZeitDatum.setText("Datum:");

        lblZeitUhrzeit.setText("Uhrzeit:");

        cbxZeitStunde.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));

        cbxZeitMinute.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));

        lblZeitDoppelpunkt.setText(":");

        lblZeitUhr.setText("Uhr");

        btnZeitKommen.setText("Kommen");
        btnZeitKommen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZeitKommenActionPerformed(evt);
            }
        });

        btnZeitGehen.setText("Gehen");
        btnZeitGehen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZeitGehenActionPerformed(evt);
            }
        });

        btnZeitAktuelleUhrzeit.setText("aktuelle Uhrzeit");
        btnZeitAktuelleUhrzeit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZeitAktuelleUhrzeitActionPerformed(evt);
            }
        });

        btnZeitRFID.setText("RFID-Chip scannen");
        btnZeitRFID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnZeitRFIDFocusLost(evt);
            }
        });
        btnZeitRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZeitRFIDActionPerformed(evt);
            }
        });
        btnZeitRFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnZeitRFIDKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout tpnlZeiterfassungLayout = new javax.swing.GroupLayout(tpnlZeiterfassung);
        tpnlZeiterfassung.setLayout(tpnlZeiterfassungLayout);
        tpnlZeiterfassungLayout.setHorizontalGroup(
            tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlZeiterfassungLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblZeitDatum)
                    .addGroup(tpnlZeiterfassungLayout.createSequentialGroup()
                        .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblZeitMitarbeiter)
                            .addComponent(lblZeitUhrzeit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DateChooserZeitDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tpnlZeiterfassungLayout.createSequentialGroup()
                                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(tpnlZeiterfassungLayout.createSequentialGroup()
                                        .addComponent(cbxZeitStunde, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblZeitDoppelpunkt)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cbxZeitMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblZeitUhr, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cbxZeitMitarbeiter, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnZeitAktuelleUhrzeit, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnZeitRFID)))))
                    .addGroup(tpnlZeiterfassungLayout.createSequentialGroup()
                        .addComponent(btnZeitKommen, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnZeitGehen, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        tpnlZeiterfassungLayout.setVerticalGroup(
            tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlZeiterfassungLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZeitMitarbeiter)
                    .addComponent(cbxZeitMitarbeiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnZeitRFID))
                .addGap(46, 46, 46)
                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblZeitDatum)
                    .addComponent(DateChooserZeitDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZeitUhrzeit)
                    .addComponent(cbxZeitStunde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxZeitMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblZeitDoppelpunkt)
                    .addComponent(lblZeitUhr)
                    .addComponent(btnZeitAktuelleUhrzeit))
                .addGap(23, 23, 23)
                .addGroup(tpnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnZeitKommen, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnZeitGehen, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpnlZeitbuchungKorrigieren.setBorder(javax.swing.BorderFactory.createTitledBorder("Heutige Zeitbuchungen korrigieren"));

        btnZeitLoeschen.setText("Löschen");
        btnZeitLoeschen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZeitLoeschenActionPerformed(evt);
            }
        });

        jspZeitpuchungen.setViewportView(lstZeitBuchungskorrektur);

        javax.swing.GroupLayout tpnlZeitbuchungKorrigierenLayout = new javax.swing.GroupLayout(tpnlZeitbuchungKorrigieren);
        tpnlZeitbuchungKorrigieren.setLayout(tpnlZeitbuchungKorrigierenLayout);
        tpnlZeitbuchungKorrigierenLayout.setHorizontalGroup(
            tpnlZeitbuchungKorrigierenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlZeitbuchungKorrigierenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlZeitbuchungKorrigierenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspZeitpuchungen, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                    .addGroup(tpnlZeitbuchungKorrigierenLayout.createSequentialGroup()
                        .addComponent(btnZeitLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tpnlZeitbuchungKorrigierenLayout.setVerticalGroup(
            tpnlZeitbuchungKorrigierenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tpnlZeitbuchungKorrigierenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jspZeitpuchungen, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnZeitLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(330, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlZeiterfassungLayout = new javax.swing.GroupLayout(pnlZeiterfassung);
        pnlZeiterfassung.setLayout(pnlZeiterfassungLayout);
        pnlZeiterfassungLayout.setHorizontalGroup(
            pnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlZeiterfassungLayout.createSequentialGroup()
                .addComponent(tpnlZeiterfassung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnlZeitbuchungKorrigieren, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(348, Short.MAX_VALUE))
        );
        pnlZeiterfassungLayout.setVerticalGroup(
            pnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlZeiterfassungLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlZeiterfassungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpnlZeitbuchungKorrigieren, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tpnlZeiterfassung, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        tbpRegisterkarte.addTab("Zeiterfassung", pnlZeiterfassung);

        lblEinstBeschreibung.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblEinstBeschreibung.setText("Hier können die Zahlungsarten und Kategorien geändert und neue hinzugefügt werden. Zahlungsart \"Bar\" und Kategorie \"Ausgabe\" können aus technischen Gründen nicht gelöscht werden.");

        javax.swing.GroupLayout pnlEinstellungenBeschreibungLayout = new javax.swing.GroupLayout(pnlEinstellungenBeschreibung);
        pnlEinstellungenBeschreibung.setLayout(pnlEinstellungenBeschreibungLayout);
        pnlEinstellungenBeschreibungLayout.setHorizontalGroup(
            pnlEinstellungenBeschreibungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEinstellungenBeschreibungLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEinstBeschreibung, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
                .addGap(378, 378, 378))
        );
        pnlEinstellungenBeschreibungLayout.setVerticalGroup(
            pnlEinstellungenBeschreibungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblEinstBeschreibung, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        tpnlEinstZahlungsartAendern.setBorder(javax.swing.BorderFactory.createTitledBorder("Zahlungsarten ändern"));

        lblEinstZahlartAuswaehlen.setText("Zahlungsart auswählen:");

        lblEinstMax6Zahlarten.setText("Es sind maximal 6 Zahlungsarten möglich");

        lblEinstZahlartBezAendern.setText("Bezeichnung ändern:");

        btnEinstZahlartAendern.setText("Hinzufügen/Ändern");
        btnEinstZahlartAendern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEinstZahlartAendernActionPerformed(evt);
            }
        });

        btnEinstZahlartLoeschen.setText("Löschen");
        btnEinstZahlartLoeschen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEinstZahlartLoeschenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tpnlEinstZahlungsartAendernLayout = new javax.swing.GroupLayout(tpnlEinstZahlungsartAendern);
        tpnlEinstZahlungsartAendern.setLayout(tpnlEinstZahlungsartAendernLayout);
        tpnlEinstZahlungsartAendernLayout.setHorizontalGroup(
            tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlEinstZahlungsartAendernLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblEinstMax6Zahlarten)
                    .addGroup(tpnlEinstZahlungsartAendernLayout.createSequentialGroup()
                        .addComponent(btnEinstZahlartAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnEinstZahlartLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tpnlEinstZahlungsartAendernLayout.createSequentialGroup()
                        .addGroup(tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEinstZahlartAuswaehlen)
                            .addComponent(lblEinstZahlartBezAendern))
                        .addGap(18, 18, 18)
                        .addGroup(tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEinstZahlartAendern)
                            .addComponent(cbxEinstZahlungsarten, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(111, Short.MAX_VALUE))
        );
        tpnlEinstZahlungsartAendernLayout.setVerticalGroup(
            tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlEinstZahlungsartAendernLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEinstMax6Zahlarten)
                .addGap(18, 18, 18)
                .addGroup(tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEinstZahlartAuswaehlen)
                    .addComponent(cbxEinstZahlungsarten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEinstZahlartBezAendern)
                    .addComponent(txtEinstZahlartAendern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(tpnlEinstZahlungsartAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEinstZahlartAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEinstZahlartLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(364, 364, 364))
        );

        tpnlEinstKategorieAendern.setBorder(javax.swing.BorderFactory.createTitledBorder("Kategorien ändern"));

        lblEinstMax9Kategorien.setText("Es sind maximal 9 Kategorien möglich");

        lblEinstKategorieAuswaehlen.setText("Kategorie auswählen:");

        lblEinstKategorieBezAendern.setText("Bezeichnung ändern:");

        btnEinstKategorieAendern.setText("Hinzufügen/Ändern");
        btnEinstKategorieAendern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEinstKategorieAendernActionPerformed(evt);
            }
        });

        btnEinstKategorieLoeschen.setText("Löschen");
        btnEinstKategorieLoeschen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEinstKategorieLoeschenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tpnlEinstKategorieAendernLayout = new javax.swing.GroupLayout(tpnlEinstKategorieAendern);
        tpnlEinstKategorieAendern.setLayout(tpnlEinstKategorieAendernLayout);
        tpnlEinstKategorieAendernLayout.setHorizontalGroup(
            tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlEinstKategorieAendernLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tpnlEinstKategorieAendernLayout.createSequentialGroup()
                        .addComponent(btnEinstKategorieAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEinstKategorieLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblEinstMax9Kategorien)
                        .addGroup(tpnlEinstKategorieAendernLayout.createSequentialGroup()
                            .addComponent(lblEinstKategorieAuswaehlen)
                            .addGap(18, 18, 18)
                            .addComponent(cbxEinstKategorien, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(tpnlEinstKategorieAendernLayout.createSequentialGroup()
                            .addComponent(lblEinstKategorieBezAendern)
                            .addGap(18, 18, 18)
                            .addComponent(txtEinstKategorieAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        tpnlEinstKategorieAendernLayout.setVerticalGroup(
            tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlEinstKategorieAendernLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEinstMax9Kategorien)
                .addGap(18, 18, 18)
                .addGroup(tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEinstKategorieAuswaehlen)
                    .addComponent(cbxEinstKategorien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEinstKategorieBezAendern)
                    .addComponent(txtEinstKategorieAendern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(tpnlEinstKategorieAendernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEinstKategorieAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEinstKategorieLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(366, 366, 366))
        );

        tpnlEinstRFIDErkennung.setBorder(javax.swing.BorderFactory.createTitledBorder("RFID-Erkennung aktivieren/deaktivieren"));

        buttonGroupRFID.add(rbtnEinstRFIDAktiviert);
        rbtnEinstRFIDAktiviert.setSelected(true);
        rbtnEinstRFIDAktiviert.setText("RFID aktiviert");
        rbtnEinstRFIDAktiviert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnEinstRFIDAktiviertActionPerformed(evt);
            }
        });

        buttonGroupRFID.add(rbtnEinstRFIDDeaktiviert);
        rbtnEinstRFIDDeaktiviert.setText("RFID deaktiviert");
        rbtnEinstRFIDDeaktiviert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnEinstRFIDDeaktiviertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tpnlEinstRFIDErkennungLayout = new javax.swing.GroupLayout(tpnlEinstRFIDErkennung);
        tpnlEinstRFIDErkennung.setLayout(tpnlEinstRFIDErkennungLayout);
        tpnlEinstRFIDErkennungLayout.setHorizontalGroup(
            tpnlEinstRFIDErkennungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlEinstRFIDErkennungLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnEinstRFIDAktiviert, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnEinstRFIDDeaktiviert, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tpnlEinstRFIDErkennungLayout.setVerticalGroup(
            tpnlEinstRFIDErkennungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlEinstRFIDErkennungLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpnlEinstRFIDErkennungLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnEinstRFIDAktiviert)
                    .addComponent(rbtnEinstRFIDDeaktiviert))
                .addContainerGap(244, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlEinstellungenLayout = new javax.swing.GroupLayout(pnlEinstellungen);
        pnlEinstellungen.setLayout(pnlEinstellungenLayout);
        pnlEinstellungenLayout.setHorizontalGroup(
            pnlEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlEinstellungenBeschreibung, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlEinstellungenLayout.createSequentialGroup()
                .addGroup(pnlEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tpnlEinstRFIDErkennung, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tpnlEinstZahlungsartAendern, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnlEinstKategorieAendern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEinstellungenLayout.setVerticalGroup(
            pnlEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEinstellungenLayout.createSequentialGroup()
                .addComponent(pnlEinstellungenBeschreibung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tpnlEinstZahlungsartAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tpnlEinstKategorieAendern, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnlEinstRFIDErkennung, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tbpRegisterkarte.addTab("Einstellungen", pnlEinstellungen);

        tpnlMonatsbericht.setBorder(javax.swing.BorderFactory.createTitledBorder("Monatsbericht erzeugen"));

        lblMonatsberichtErzeuge.setText("Erzeuge Bericht für:");

        lblMonatsberichtMonat.setText("Monat:");

        cbxMonatsberichtMonat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        lblMonatsberichtJahr.setText("Jahr:");

        btnMonatsberichteErzeuge.setText("Bericht erzeugen");
        btnMonatsberichteErzeuge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonatsberichteErzeugeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tpnlMonatsberichtLayout = new javax.swing.GroupLayout(tpnlMonatsbericht);
        tpnlMonatsbericht.setLayout(tpnlMonatsberichtLayout);
        tpnlMonatsberichtLayout.setHorizontalGroup(
            tpnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMonatsberichtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMonatsberichtErzeuge)
                .addGap(84, 84, 84)
                .addGroup(tpnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tpnlMonatsberichtLayout.createSequentialGroup()
                        .addComponent(lblMonatsberichtMonat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxMonatsberichtMonat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblMonatsberichtJahr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(MonatsberichtYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnMonatsberichteErzeuge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(653, Short.MAX_VALUE))
        );
        tpnlMonatsberichtLayout.setVerticalGroup(
            tpnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpnlMonatsberichtLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(tpnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tpnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMonatsberichtErzeuge)
                        .addComponent(lblMonatsberichtMonat)
                        .addComponent(cbxMonatsberichtMonat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMonatsberichtJahr))
                    .addComponent(MonatsberichtYearChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnMonatsberichteErzeuge, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(484, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlMonatsberichtLayout = new javax.swing.GroupLayout(pnlMonatsbericht);
        pnlMonatsbericht.setLayout(pnlMonatsberichtLayout);
        pnlMonatsberichtLayout.setHorizontalGroup(
            pnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMonatsberichtLayout.createSequentialGroup()
                .addComponent(tpnlMonatsbericht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 360, Short.MAX_VALUE))
        );
        pnlMonatsberichtLayout.setVerticalGroup(
            pnlMonatsberichtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMonatsberichtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnlMonatsbericht, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbpRegisterkarte.addTab("Monatsbericht", pnlMonatsbericht);

        getContentPane().add(tbpRegisterkarte);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEinstKategorieLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEinstKategorieLoeschenActionPerformed
        Kategorie kategorie = (Kategorie) cbxEinstKategorien.getSelectedItem();
        try {
            persistenceAdapterKategorie.aktivNullen(kategorie);
            aktualisiereKategorien();
            JOptionPane.showMessageDialog(this, "Kategorie wurde gelöscht", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Kategorie konnte nicht gelöscht werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }      
    }//GEN-LAST:event_btnEinstKategorieLoeschenActionPerformed

    private void btnEinstKategorieAendernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEinstKategorieAendernActionPerformed
        if (kategorien.length == 9) {
            Kategorie kategorie = (Kategorie) cbxEinstKategorien.getSelectedItem();
            String bezeichnung = txtEinstKategorieAendern.getText();
            Kategorie kategorieNeu = new Kategorie(kategorie.getKategorienid(), bezeichnung);
            try {
                persistenceAdapterKategorie.aktivNullen(kategorie);
                persistenceAdapterKategorie.addKategorie(kategorieNeu);
                aktualisiereKategorien();
                JOptionPane.showMessageDialog(this, "Kategorie wurde geändert", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Kategorie konnte nicht geändert werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            Kategorie kategorie = new Kategorie(txtEinstKategorieAendern.getText());
            try {
                persistenceAdapterKategorie.addKategorie(kategorie);
                aktualisiereKategorien();
                JOptionPane.showMessageDialog(this, "Kategorie wurde hinzugefügt", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Kategorie konnte nicht hinzugefügt werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEinstKategorieAendernActionPerformed

    private void btnEinstZahlartLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEinstZahlartLoeschenActionPerformed
        Zahlungsart zahlungsart = (Zahlungsart) cbxEinstZahlungsarten.getSelectedItem();
        try {
            persistenceAdapterZahlungsart.aktivNullen(zahlungsart);
            aktualisiereZahlungsarten();
            JOptionPane.showMessageDialog(this, "Zahlungsart wurde gelöscht", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Zahlungsart konnte nicht gelöscht werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnEinstZahlartLoeschenActionPerformed

    private void btnEinstZahlartAendernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEinstZahlartAendernActionPerformed
        if (zahlungsarten.length == 6) {
            Zahlungsart zahlungsart = (Zahlungsart) cbxEinstZahlungsarten.getSelectedItem();
            String bezeichnung = txtEinstZahlartAendern.getText();
            Zahlungsart zahlungsartNeu = new Zahlungsart(zahlungsart.getZahlungsartenid(), bezeichnung);
            try {
                persistenceAdapterZahlungsart.aktivNullen(zahlungsart);
                persistenceAdapterZahlungsart.addZahlungsart(zahlungsartNeu);
                aktualisiereZahlungsarten();
                JOptionPane.showMessageDialog(this, "Zahlungsart wurde geändert", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Zahlungsart konnte nicht geändert werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            Zahlungsart zahlungsart = new Zahlungsart(txtEinstZahlartAendern.getText());
            try {
                persistenceAdapterZahlungsart.addZahlungsart(zahlungsart);
                aktualisiereZahlungsarten();
                JOptionPane.showMessageDialog(this, "Zahlungsart wurde hinzugefügt", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Zahlungsart konnte nicht hinzugefügt werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEinstZahlartAendernActionPerformed

    private void btnZeitLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZeitLoeschenActionPerformed
        Zeitbuchung zeitbuchung = (Zeitbuchung) lstZeitBuchungskorrektur.getSelectedValue();
        try {
            persistenceAdapterZeiterfassung.delete(zeitbuchung);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Löschen der Buchung. Eintrag angeklickt?\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnZeitLoeschenActionPerformed

    private void btnZeitAktuelleUhrzeitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZeitAktuelleUhrzeitActionPerformed
        Date datum = new Date();
        String timedate = datum.toString();
        String[] timedates = timedate.split(" ");
        String uhrzeit = timedates[3];
        String[] uhrzeiten = uhrzeit.split(":");
        cbxZeitStunde.setSelectedItem(uhrzeiten[0]);
        cbxZeitMinute.setSelectedItem(uhrzeiten[1]);
    }//GEN-LAST:event_btnZeitAktuelleUhrzeitActionPerformed

    private void btnZeitGehenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZeitGehenActionPerformed
        try {
            Date datumNeu = DateChooserZeitDatum.getDate();             //Datum extrahieren
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //Datum formatieren
            String datum = dateFormat.format(datumNeu);

            String stunde = cbxZeitStunde.getSelectedItem()+"";
            String minute = cbxZeitMinute.getSelectedItem()+"";
            String uhrzeit = stunde + ":" + minute;                     //Uhrzeit extrahieren

            Mitarbeiter mit = (Mitarbeiter)cbxZeitMitarbeiter.getSelectedItem();
            int zeiterfassungartenID = 2;
            Zeiterfassung zeiterfassung = new Zeiterfassung(datum, uhrzeit, mit.getMitarbeiterID(), zeiterfassungartenID);
            persistenceAdapterZeiterfassung.insert(zeiterfassung);
            cbxZeitMitarbeiter.setSelectedItem(null);
            JOptionPane.showMessageDialog(this, "Zeit gebucht", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Buchung. Mitarbeiter ausgewählt?\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnZeitGehenActionPerformed

    private void btnZeitKommenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZeitKommenActionPerformed
        try {
            Date datumNeu = DateChooserZeitDatum.getDate();             //Datum extrahieren
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //Datum formatieren
            String datum = dateFormat.format(datumNeu);
            
            String stunde = cbxZeitStunde.getSelectedItem()+"";
            String minute = cbxZeitMinute.getSelectedItem()+"";
            String uhrzeit = stunde + ":" + minute;                     //Uhrzeit extrahieren
            
            Mitarbeiter mit = (Mitarbeiter)cbxZeitMitarbeiter.getSelectedItem();
            
            int zeiterfassungartenID = 1;
            
            Zeiterfassung zeiterfassung = new Zeiterfassung(datum, uhrzeit, mit.getMitarbeiterID(), zeiterfassungartenID);
            persistenceAdapterZeiterfassung.insert(zeiterfassung);
            cbxZeitMitarbeiter.setSelectedItem(null);
            JOptionPane.showMessageDialog(this, "Zeit gebucht", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Buchung. Mitarbeiter ausgewählt?\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }      
    }//GEN-LAST:event_btnZeitKommenActionPerformed

    private void btnMitLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMitLoeschenActionPerformed
        Mitarbeiter mit = (Mitarbeiter)cbxMitMitarbeiter.getSelectedItem();         //type casting
        try {
            persistenceAdapterMitarbeiter.delete(mit);
            comboboxesDefaultNull();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Mitarbeiter konnte nicht gelöscht werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnMitLoeschenActionPerformed

    private void btnMitHinzufuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMitHinzufuegenActionPerformed
        if(!txtMitNachnameHinzu.getText().isBlank()|| !txtMitVornameHinzu.getText().isBlank()) {
            Mitarbeiter mitarbeiter = new Mitarbeiter(txtMitNachnameHinzu.getText()+"", txtMitVornameHinzu.getText()+"");
            txtMitNachnameHinzu.setText("");
            txtMitVornameHinzu.setText("");
            try {
                persistenceAdapterMitarbeiter.insert(mitarbeiter);
                comboboxesDefaultNull();
                JOptionPane.showMessageDialog(this, "Mitarbeiter wurde hinzugefügt", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                if (ex instanceof Exception) {
                    JOptionPane.showMessageDialog(this, "Fehler beim Hinzufügen des Mitarbeiters \n\n Fehlercode: "+ ex, "", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "Es wurde kein Name eingetragen", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnMitHinzufuegenActionPerformed

    private void btnMonatsberichteErzeugeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonatsberichteErzeugeActionPerformed
        try {
            String monat = cbxMonatsberichtMonat.getSelectedItem()+"";
            String jahr = MonatsberichtYearChooser.getYear()+"";
            String monatJahr = jahr + "-" + monat;
            String zeitstempel = erzeugeZeitstempelFuerBericht();
            
            Berichterzeugung.erzeugeBericht(monat, jahr, monatJahr, zeitstempel);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Bericht konnte nicht erzeugt werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnMonatsberichteErzeugeActionPerformed

    private void btnAbrechnungKassenbestandAendernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrechnungKassenbestandAendernActionPerformed
        GUIKassenbestandAnpassen gUIKassenbestandAnpassen = new GUIKassenbestandAnpassen(persistenceAdapterKasse, this);
        gUIKassenbestandAnpassen.setVisible(true);
    }//GEN-LAST:event_btnAbrechnungKassenbestandAendernActionPerformed

    private void txtAbrCentKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAbrCentKeyTyped
        //Es sollen nur Zahlen und davon höchstens 2 ins Cent-Textfeld eingetragen werden können
        nurZahlen(evt);
        if(txtAbrCent.getText().length() >= 2) {
            evt.consume();
        }        
    }//GEN-LAST:event_txtAbrCentKeyTyped

    private void txtAbrCentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAbrCentActionPerformed
        //Durch das Drücken von Enter wird der Speichern-Button gedrückt
        btnAbrSpeichern.doClick();
    }//GEN-LAST:event_txtAbrCentActionPerformed

    private void btnAbrRueckgaengigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrRueckgaengigActionPerformed
        Abrechnung abrechnung = null;
        try {
            abrechnung = persistenceAdapterAbrechnung.getAbrechnungWithMaxID();
            String[] dateTime = abrechnung.getZeitstempel().split(" ");
            int result = JOptionPane.showConfirmDialog(this, abrechnung.getBezeichnung() + ", "+ abrechnung.getBetrag() + " Euro, gespeichert am "+ dateTime[0] + " um " + dateTime[1] + "\n\nEintrag löschen?", "Bestätigen", JOptionPane.YES_NO_OPTION);
            try {
                switch (result) {
                    case JOptionPane.YES_OPTION:
                    persistenceAdapterAbrechnung.deleteAbrechnungWithMaxID(abrechnung.getAbrechnungsid());
                    ladeHeutigeAbrechnungen();
                    if (abrechnung.getZahlungsartid() == 1 && abrechnung.getKategorieid() != 6) {
                        BigDecimal betrag = new BigDecimal(0);
                        betrag = BigDecimal.valueOf(abrechnung.getBetrag());
                        kassenbestandVerrechnen(betrag.negate());
                        kassenbestandFormatieren(kasse.getKassenbestand()+"");
                    }
                    if (abrechnung.getZahlungsartid() == 1 && abrechnung.getKategorieid() == 6) {           
                        BigDecimal betrag = new BigDecimal(0);
                        betrag = BigDecimal.valueOf(abrechnung.getBetrag());
                        kassenbestandVerrechnen(betrag.negate());
                        kassenbestandFormatieren(kasse.getKassenbestand()+"");
                    }
                    break;
                    case JOptionPane.NO_OPTION:
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Letzter Eintrag konnte nicht gelöscht werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Letzter Eintrag konnte nicht aus Datenbank geladen werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAbrRueckgaengigActionPerformed

    private void btnAbrSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrSpeichernActionPerformed
        if(cbxAbrMitarbeiter.getSelectedItem() != null) {
            if (txtAbrBezeichnung.getText().isBlank() == false) {
                if (txtAbrEuro.getText().isBlank() == false || txtAbrCent.getText().isBlank() == false) {
                    BigDecimal betrag = holeAbrechnungsbetrag();
                    if (!betrag.equals(0)) {
                        if (zahlungsartSelected() == true) {
                            if (kategorieSelected() == true) {
                                String bezeichnung = txtAbrBezeichnung.getText();
                                String zeitstempel = erzeugeZeitstempel();
                                Mitarbeiter mitarbeiterAbr = (Mitarbeiter) cbxAbrMitarbeiter.getSelectedItem();
                                int zahlungsartenid = getSelectedZahlungsartenIDCheckbox();
                                int kategorieid = getSelectedKategorienIDCheckbox();
                                if (zahlungsartenid == 1 && kategorieid != 6) {
                                    try {
                                        kassenbestandVerrechnen(betrag);
                                        kassenbestandFormatieren(kasse.getKassenbestand()+"");
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(this, "Fehler beim Anpassen des Kassenbestands. Fehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                if (zahlungsartenid == 1 && kategorieid == 6) {
                                    try {
                                        kassenbestandVerrechnen(betrag.negate());
                                        kassenbestandFormatieren(kasse.getKassenbestand()+"");
                                        betrag = betrag.negate();
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(this, "Fehler beim Anpassen des Kassenbestands. Fehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                Abrechnung abrechnung = new Abrechnung(bezeichnung, betrag.doubleValue(), zeitstempel, mitarbeiterAbr.getMitarbeiterID(), zahlungsartenid, kategorieid);
                                try {
                                    cbxAbrMitarbeiter.setSelectedItem(null);
                                    persistenceAdapterAbrechnung.insert(abrechnung);
                                    txtAbrBezeichnung.setText("");
                                    txtAbrEuro.setText("");
                                    txtAbrCent.setText("");
                                    JOptionPane.showMessageDialog(this, "Eintrag wurde gespeichert", "OK", JOptionPane.INFORMATION_MESSAGE);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(this, "Abrechnung konnte nicht in Datenbank gespeichert werden\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(this, "Bitte Kategorie auswählen und erneut auf Speichern drücken", "Fehler", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "Bitte Zahlungsart auswählen und erneut auf Speichern drücken", "Fehler", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Betrag darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Bitte Betrag eingeben und erneut auf Speichern drücken", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "Bitte Bezeichnung eingeben und erneut auf Speichern drücken", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "Bitte Mitarbeiter auswählen und erneut auf Speichern drücken", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAbrSpeichernActionPerformed

    private void txtAbrEuroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAbrEuroKeyTyped
        //Es sollen nur Zahlen ins Euro-Textfeld eingetragen werden können
        String key = String.valueOf(evt.getKeyChar());
        String jumpKey = ",.";
        if(jumpKey.contains(key) || key.equals("\n")) {  // key.equals("\n") bedeutet, dass die Enter-Taste gedrückt wurde
            evt.consume();
            txtAbrCent.requestFocus();
        }
        nurZahlen(evt);
    }//GEN-LAST:event_txtAbrEuroKeyTyped

    private void btnMitVerknuepfenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnMitVerknuepfenKeyPressed
        String code = RFID.leseRFIDChip(String.valueOf(evt.getKeyChar()), System.currentTimeMillis());
        if (code.length() == 10) {
            speichereChip(code);
        }

    }//GEN-LAST:event_btnMitVerknuepfenKeyPressed

    private void btnMitVerknuepfenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMitVerknuepfenActionPerformed
        btnMitVerknuepfen.setText("Chip nähern");
    }//GEN-LAST:event_btnMitVerknuepfenActionPerformed

    private void btnMitVerknuepfenFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnMitVerknuepfenFocusLost
        btnMitVerknuepfen.setText("Verknüpfen");
    }//GEN-LAST:event_btnMitVerknuepfenFocusLost

    private void rbtnEinstRFIDAktiviertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnEinstRFIDAktiviertActionPerformed
        String password = "Friseur";
        String eingabe = JOptionPane.showInputDialog(this, "Bitte Passwort eingeben", "Passwort erforderlich ", JOptionPane.QUESTION_MESSAGE);
        if (password.equals(eingabe)) {
            try {
                RFIDaktiviert();
                Konfigurationsdatei.aktiviereRFID();
            } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Ändern der RFID-Einstellungen\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            } 
        }
        if (!password.equals(eingabe) && eingabe != null) {
            rbtnEinstRFIDDeaktiviert.setSelected(true);
            JOptionPane.showMessageDialog(this, "Das Passwort ist falsch", "Falsches Passwort", JOptionPane.ERROR_MESSAGE);
        }
        if (eingabe == null) {  //Wenn Cancel gedrückt wird soll kein Fehler-Fenster kommen
            rbtnEinstRFIDDeaktiviert.setSelected(true);
        }
    }//GEN-LAST:event_rbtnEinstRFIDAktiviertActionPerformed

    private void rbtnEinstRFIDDeaktiviertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnEinstRFIDDeaktiviertActionPerformed
        String password = "Friseur";
        String eingabe = JOptionPane.showInputDialog(this, "Bitte Passwort eingeben", "Passwort erforderlich ", JOptionPane.QUESTION_MESSAGE);
        if (password.equals(eingabe)) { 
            try {
                RFIDdeaktiviert();
                Konfigurationsdatei.deaktiviereRFID();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Ändern der RFID-Einstellungen\n\nFehlercode: "+ex, "Fehler", JOptionPane.ERROR_MESSAGE);
            }
            if (!password.equals(eingabe) && eingabe != null) {  
                rbtnEinstRFIDAktiviert.setSelected(true);
                JOptionPane.showMessageDialog(this, "Das Passwort ist falsch", "Falsches Passwort", JOptionPane.ERROR_MESSAGE);
            } 
            if (eingabe == null) {  //Wenn Cancel gedrückt wird soll kein Fehler-Fenster kommen
                rbtnEinstRFIDAktiviert.setSelected(true);
            } 
        }
    }//GEN-LAST:event_rbtnEinstRFIDDeaktiviertActionPerformed

    private void btnAbrRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrRFIDActionPerformed
        btnAbrRFID.setText("Chip nähern");
        cbxAbrMitarbeiter.setSelectedItem(null);
    }//GEN-LAST:event_btnAbrRFIDActionPerformed

    private void btnMitRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMitRFIDActionPerformed
        btnMitRFID.setText("Chip nähern");
        cbxMitMitarbeiter.setSelectedItem(null);
    }//GEN-LAST:event_btnMitRFIDActionPerformed

    private void btnZeitRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZeitRFIDActionPerformed
        btnZeitRFID.setText("Chip nähern");
        cbxZeitMitarbeiter.setSelectedItem(null);
    }//GEN-LAST:event_btnZeitRFIDActionPerformed

    private void btnAbrRFIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnAbrRFIDFocusLost
        btnAbrRFID.setText("RFID-Chip scannen");
    }//GEN-LAST:event_btnAbrRFIDFocusLost

    private void btnMitRFIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnMitRFIDFocusLost
        btnMitRFID.setText("RFID-Chip scannen");
    }//GEN-LAST:event_btnMitRFIDFocusLost

    private void btnZeitRFIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnZeitRFIDFocusLost
        btnZeitRFID.setText("RFID-Chip scannen");
    }//GEN-LAST:event_btnZeitRFIDFocusLost

    private void btnAbrRFIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAbrRFIDKeyPressed
        String code = RFID.leseRFIDChip(String.valueOf(evt.getKeyChar()), System.currentTimeMillis());
        if (code.length() == 10) {
            for (int i = 0; i < mitarbeiter.length; i++) {
                if (mitarbeiter[i].getRFID() != null) { //nötig sonst Null-Pointer-Exception
                    if (mitarbeiter[i].getRFID().equals(code)) {
                        cbxAbrMitarbeiter.setSelectedItem(mitarbeiter[i]);
                    }
                }
            }
            if (cbxAbrMitarbeiter.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Fehler: RFID-Chip ist mit keinem Mitarbeiter verknüpft", "Fehler", JOptionPane.ERROR_MESSAGE);
            } 
        }    
    }//GEN-LAST:event_btnAbrRFIDKeyPressed

    private void btnMitRFIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnMitRFIDKeyPressed
        String code = RFID.leseRFIDChip(String.valueOf(evt.getKeyChar()), System.currentTimeMillis());
        if (code.length() == 10) {
            for (int i = 0; i < mitarbeiter.length; i++) {
                if (mitarbeiter[i].getRFID() != null) { //nötig sonst Null-Pointer-Exception
                    if (mitarbeiter[i].getRFID().equals(code)) {
                        cbxMitMitarbeiter.setSelectedItem(mitarbeiter[i]);
                    }
                }
            }
            if (cbxMitMitarbeiter.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Fehler: RFID-Chip ist mit keinem Mitarbeiter verknüpft", "Fehler", JOptionPane.ERROR_MESSAGE);
            } 
        }    
    }//GEN-LAST:event_btnMitRFIDKeyPressed

    private void btnZeitRFIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnZeitRFIDKeyPressed
        String code = RFID.leseRFIDChip(String.valueOf(evt.getKeyChar()), System.currentTimeMillis());
        if (code.length() == 10) {
            for (int i = 0; i < mitarbeiter.length; i++) {
                if (mitarbeiter[i].getRFID() != null) { //nötig sonst Null-Pointer-Exception
                    if (mitarbeiter[i].getRFID().equals(code)) {
                        cbxZeitMitarbeiter.setSelectedItem(mitarbeiter[i]);
                    }
                }
            }
            if (cbxZeitMitarbeiter.getSelectedItem() == null) 
            {
                JOptionPane.showMessageDialog(this, "Fehler: RFID-Chip ist mit keinem Mitarbeiter verknüpft", "Fehler", JOptionPane.ERROR_MESSAGE);
            } 
        }    
    }//GEN-LAST:event_btnZeitRFIDKeyPressed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                GUIFriseursalon gUIFriseursalon = new GUIFriseursalon();
//                gUIFriseursalon.setVisible(true);
//                new GUIFriseursalon().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DateChooserZeitDatum;
    private com.toedter.calendar.JYearChooser MonatsberichtYearChooser;
    private javax.swing.JButton btnAbrRFID;
    private javax.swing.JButton btnAbrRueckgaengig;
    private javax.swing.JButton btnAbrSpeichern;
    private javax.swing.JButton btnAbrechnungKassenbestandAendern;
    private javax.swing.JButton btnEinstKategorieAendern;
    private javax.swing.JButton btnEinstKategorieLoeschen;
    private javax.swing.JButton btnEinstZahlartAendern;
    private javax.swing.JButton btnEinstZahlartLoeschen;
    private javax.swing.JButton btnMitHinzufuegen;
    private javax.swing.JButton btnMitLoeschen;
    private javax.swing.JButton btnMitRFID;
    private javax.swing.JButton btnMitVerknuepfen;
    private javax.swing.JButton btnMonatsberichteErzeuge;
    private javax.swing.JButton btnZeitAktuelleUhrzeit;
    private javax.swing.JButton btnZeitGehen;
    private javax.swing.JButton btnZeitKommen;
    private javax.swing.JButton btnZeitLoeschen;
    private javax.swing.JButton btnZeitRFID;
    private javax.swing.ButtonGroup buttonGroupKategorie;
    private javax.swing.ButtonGroup buttonGroupRFID;
    private javax.swing.ButtonGroup buttonGroupZahlungsart;
    private javax.swing.JComboBox<Mitarbeiter> cbxAbrMitarbeiter;
    private javax.swing.JComboBox<Kategorie> cbxEinstKategorien;
    private javax.swing.JComboBox<Zahlungsart> cbxEinstZahlungsarten;
    private javax.swing.JComboBox<Mitarbeiter> cbxMitMitarbeiter;
    private javax.swing.JComboBox<Mitarbeiter> cbxMitMitarbeiterRFID;
    private javax.swing.JComboBox<String> cbxMonatsberichtMonat;
    private javax.swing.JComboBox<String> cbxZeitMinute;
    private javax.swing.JComboBox<Mitarbeiter> cbxZeitMitarbeiter;
    private javax.swing.JComboBox<String> cbxZeitStunde;
    private javax.swing.JCheckBox chbAbrKategorie1;
    private javax.swing.JCheckBox chbAbrKategorie2;
    private javax.swing.JCheckBox chbAbrKategorie3;
    private javax.swing.JCheckBox chbAbrKategorie4;
    private javax.swing.JCheckBox chbAbrKategorie5;
    private javax.swing.JCheckBox chbAbrKategorie6;
    private javax.swing.JCheckBox chbAbrKategorie7;
    private javax.swing.JCheckBox chbAbrKategorie8;
    private javax.swing.JCheckBox chbAbrKategorie9;
    private javax.swing.JCheckBox chbAbrZahlungsart1;
    private javax.swing.JCheckBox chbAbrZahlungsart2;
    private javax.swing.JCheckBox chbAbrZahlungsart3;
    private javax.swing.JCheckBox chbAbrZahlungsart4;
    private javax.swing.JCheckBox chbAbrZahlungsart5;
    private javax.swing.JCheckBox chbAbrZahlungsart6;
    private javax.swing.JScrollPane jScrollPaneTagesbericht;
    private javax.swing.JScrollPane jspZeitpuchungen;
    private javax.swing.JLabel lblAbrBetrag;
    private javax.swing.JLabel lblAbrBezeichnung;
    private javax.swing.JLabel lblAbrCent;
    private javax.swing.JLabel lblAbrEuro;
    private javax.swing.JLabel lblAbrKategorie;
    private javax.swing.JLabel lblAbrMitarbeiter;
    private javax.swing.JLabel lblAbrZahlungsart;
    private javax.swing.JLabel lblAbrechnungAktuellerKassenbestand;
    private javax.swing.JLabel lblAbrechnungKassenbestand;
    private javax.swing.JLabel lblEinstBeschreibung;
    private javax.swing.JLabel lblEinstKategorieAuswaehlen;
    private javax.swing.JLabel lblEinstKategorieBezAendern;
    private javax.swing.JLabel lblEinstMax6Zahlarten;
    private javax.swing.JLabel lblEinstMax9Kategorien;
    private javax.swing.JLabel lblEinstZahlartAuswaehlen;
    private javax.swing.JLabel lblEinstZahlartBezAendern;
    private javax.swing.JLabel lblMitMitarbeiterLoe;
    private javax.swing.JLabel lblMitNachnameHinzu;
    private javax.swing.JLabel lblMitVornameHinzu;
    private javax.swing.JLabel lblMonatsberichtErzeuge;
    private javax.swing.JLabel lblMonatsberichtJahr;
    private javax.swing.JLabel lblMonatsberichtMonat;
    private javax.swing.JLabel lblTagesbericht;
    private javax.swing.JLabel lblTagesberichtSumme;
    private javax.swing.JLabel lblZeitDatum;
    private javax.swing.JLabel lblZeitDoppelpunkt;
    private javax.swing.JLabel lblZeitMitarbeiter;
    private javax.swing.JLabel lblZeitUhr;
    private javax.swing.JLabel lblZeitUhrzeit;
    private javax.swing.JList<HeutigeAbrechnung> lstTagesbericht;
    private javax.swing.JList<Zeitbuchung> lstZeitBuchungskorrektur;
    private javax.swing.JPanel pnlAbrechnung;
    private javax.swing.JPanel pnlEinstellungen;
    private javax.swing.JPanel pnlEinstellungenBeschreibung;
    private javax.swing.JPanel pnlMitarbeiter;
    private javax.swing.JPanel pnlMonatsbericht;
    private javax.swing.JPanel pnlTagesbericht;
    private javax.swing.JPanel pnlUser;
    private javax.swing.JPanel pnlZeiterfassung;
    private javax.swing.JRadioButton rbtnEinstRFIDAktiviert;
    private javax.swing.JRadioButton rbtnEinstRFIDDeaktiviert;
    private javax.swing.JTabbedPane tbpRegisterkarte;
    private javax.swing.JPanel tpnlAbrechnung;
    private javax.swing.JPanel tpnlEinstKategorieAendern;
    private javax.swing.JPanel tpnlEinstRFIDErkennung;
    private javax.swing.JPanel tpnlEinstZahlungsartAendern;
    private javax.swing.JPanel tpnlKassenbestand;
    private javax.swing.JPanel tpnlMitarbeiterHinzufuegen;
    private javax.swing.JPanel tpnlMitarbeiterLoeschen;
    private javax.swing.JPanel tpnlMitarbeiterMitRFIDVerknuepfen;
    private javax.swing.JPanel tpnlMonatsbericht;
    private javax.swing.JPanel tpnlTagesbericht;
    private javax.swing.JPanel tpnlZeitbuchungKorrigieren;
    private javax.swing.JPanel tpnlZeiterfassung;
    private javax.swing.JTextField txtAbrBezeichnung;
    private javax.swing.JTextField txtAbrCent;
    private javax.swing.JTextField txtAbrEuro;
    private javax.swing.JTextField txtEinstKategorieAendern;
    private javax.swing.JTextField txtEinstZahlartAendern;
    private javax.swing.JTextField txtMitNachnameHinzu;
    private javax.swing.JTextField txtMitVornameHinzu;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof PersistenceAdapterMitarbeiter) {
            ladeMitarbeiterInComboboxes();
        }    
        if (evt.getSource() instanceof PersistenceAdapterZeiterfassung) {
            ladeZeitbuchungenInListe();
        }   
        if (evt.getSource() instanceof PersistenceAdapterAbrechnung) {
            ladeHeutigeAbrechnungen();
        }  
    }
}
