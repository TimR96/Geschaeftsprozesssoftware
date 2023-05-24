/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Tim
 */
public class Konfigurationsdatei {
    /**
    *Schreibt eine "1" in die Konfigurationsdatei "ConfigfileRFID.txt", was 
    *bedeutet, dass die RFID-Erkennung aktiviert ist
    */
    public static void aktiviereRFID() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("ConfigfileRFID.txt"));
        writer.write("1");
        writer.close();
    }
    
    /**
    *Schreibt eine "0" in die Konfigurationsdatei "ConfigfileRFID.txt", was 
    *bedeutet, dass die RFID-Erkennung deaktiviert ist
    */
    public static void deaktiviereRFID() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("ConfigfileRFID.txt"));
        writer.write("0");
        writer.close();
    }    
    /**
    *Liest die Konfigurationsdatei "ConfigfileRFID.txt" aus.
    */
    public static String leseRFIDStatus() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("ConfigfileRFID.txt"));
        String status = reader.readLine();
        reader.close();
        return status;
    }       
}
