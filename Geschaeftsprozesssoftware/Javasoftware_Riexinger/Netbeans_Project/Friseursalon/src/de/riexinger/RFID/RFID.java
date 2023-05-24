/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.RFID;

/**
 * Diese Klasse sorgt dafür, dass nur Eingaben vom Kartenlesegerät akzeptiert
 * werden.
 * @author Tim
 */
public class RFID {
    static String RFIDinput = "";
    static int maxZeitZwTasten = 40;    //Höchstzeit zw. zwei Tasteneingaben
    static long letzteZeit = 0;
    static String code = "";
    
    /**
    * Diese Methode sorgt dafür, dass nur Eingaben vom Kartenlesegerät akzeptiert
    * werden. Das Lesegerät funktioniert wie eine Tastatur und liest Eingaben
    * sehr schnell ein, während ein Mensch
    * länger benötigt. Diese Zeitdifferenzen ermöglichen die 
    * Unterscheidung zwischen Lesegerät und Mensch. Zusätzlich werden die erste 
    * und vierte Stelle der Chipcodes überprüft, diese sind bei allen Chips 
    * gleich. Der Chip-Code wird als String zurückgegeben.
     * @param key gedrückte Taste
     * @param zeit Zeit in Millisekunden, zu der gedrückt wurde.
     */
    
    public static String leseRFIDChip(String key, long zeit) {
        if (zeit - letzteZeit > maxZeitZwTasten) {
            RFIDinput = "";
            code = "";
        }       
        letzteZeit = zeit;
        RFIDinput += key;
        if (RFIDinput.length() == 10) {
            char eins = RFIDinput.charAt(0);
            char vier = RFIDinput.charAt(3);
            if (Character.getNumericValue(eins) == 0 && Character.getNumericValue(vier) == 6) {
                code = RFIDinput;
                RFIDinput = "";
            }
        }
        return code;        
    } 
}

