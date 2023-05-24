package de.riexinger.appcontroller;

import de.riexinger.bericht.Berichterzeugung;
import de.riexinger.dbadapter.DBAdapterKasse;
import de.riexinger.dbconnector.DBConnector;
import de.riexinger.dbconnector.DBConnectorMySQL;
import de.riexinger.persistence.PersistenceAdapterKasse;
import de.riexinger.gui.GUIFriseursalon;
import de.riexinger.persistence.PersistenceAdapterAbrechnung;
import de.riexinger.persistence.PersistenceAdapterKategorie;
import de.riexinger.persistence.PersistenceAdapterMitarbeiter;
import de.riexinger.persistence.PersistenceAdapterZahlungsart;
import de.riexinger.persistence.PersistenceAdapterZeiterfassung;
import de.riexinger.dbadapter.DBAdapterAbrechnung;
import de.riexinger.dbadapter.DBAdapterKategorie;
import de.riexinger.dbadapter.DBAdapterMitarbeiter;
import de.riexinger.dbadapter.DBAdapterZahlungsart;
import de.riexinger.dbadapter.DBAdapterZeiterfassung;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Tim
 */
public class Appcontroller {
    
    public Appcontroller() throws Exception {
        DBConnector dBConnector = new DBConnectorMySQL("root", "", "dbfriseurriexinger", "localhost", "3306");  
       
        PersistenceAdapterKasse persistenceAdapterKasse = new DBAdapterKasse(dBConnector);
        PersistenceAdapterMitarbeiter persistenceAdapterMitarbeiter = new DBAdapterMitarbeiter(dBConnector);
        PersistenceAdapterZeiterfassung persistenceAdapterZeiterfassung = new DBAdapterZeiterfassung(dBConnector);
        PersistenceAdapterZahlungsart persistenceAdapterZahlungsart = new DBAdapterZahlungsart(dBConnector);
        PersistenceAdapterKategorie persistenceAdapterKategorie = new DBAdapterKategorie(dBConnector);
        PersistenceAdapterAbrechnung persistenceAdapterAbrechnung = new DBAdapterAbrechnung(dBConnector);
        
        GUIFriseursalon gUIFriseursalon = new GUIFriseursalon(persistenceAdapterKasse, persistenceAdapterMitarbeiter, persistenceAdapterZeiterfassung, persistenceAdapterZahlungsart, persistenceAdapterKategorie, persistenceAdapterAbrechnung);
        Berichterzeugung.dBConnector = dBConnector;
        
        persistenceAdapterKasse.addPropertyChangeListener(gUIFriseursalon);
        persistenceAdapterMitarbeiter.addPropertyChangeListener(gUIFriseursalon);
        persistenceAdapterZeiterfassung.addPropertyChangeListener(gUIFriseursalon);
        persistenceAdapterZahlungsart.addPropertyChangeListener(gUIFriseursalon);
        persistenceAdapterKategorie.addPropertyChangeListener(gUIFriseursalon);
        persistenceAdapterAbrechnung.addPropertyChangeListener(gUIFriseursalon);
        
        gUIFriseursalon.setVisible(true);
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIFriseursalon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() { 
                try {
                    Appcontroller appcontroller = new Appcontroller();
                } catch (Exception ex) {
                    Logger.getLogger(Appcontroller.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        });
    }
}
