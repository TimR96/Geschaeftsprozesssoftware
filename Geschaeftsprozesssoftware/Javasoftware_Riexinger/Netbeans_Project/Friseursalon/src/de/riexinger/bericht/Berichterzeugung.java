/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.bericht;

import com.lowagie.text.pdf.PdfWriter;
import de.riexinger.dbconnector.DBConnector;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 *
 * @author Tim
 */
public class Berichterzeugung {
    public static DBConnector dBConnector; 

    public static void erzeugeBericht(String monat, String jahr, String monatJahr, String zeitstempel) throws ClassNotFoundException, SQLException, JRException, IOException {
        dBConnector.connect();    
        String reportPath = "jasper\\Vorlage\\Friseursalon.jrxml";            
        JasperDesign jd = JRXmlLoader.load(reportPath);
        JRDesignQuery query = new JRDesignQuery();
        query.setText("SELECT * FROM Abrechnungen WHERE Zeitstempel LIKE '" + monatJahr + "%';");
        jd.setQuery(query);
        JasperReport jr = JasperCompileManager.compileReport(jd);
        JasperPrint jp = JasperFillManager.fillReport(jr,null, dBConnector.getConnection());
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jp));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("jasper\\Berichte\\Monatsbericht_Friseursalon_Riexinger_" + zeitstempel + ".pdf"));
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setPermissions(PdfWriter.AllowCopy | PdfWriter.AllowPrinting);
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        File myFile = new File("jasper\\Berichte\\Monatsbericht_Friseursalon_Riexinger_" + zeitstempel + ".pdf");
        Desktop.getDesktop().open(myFile);

        dBConnector.disconnect();
    }   
}
