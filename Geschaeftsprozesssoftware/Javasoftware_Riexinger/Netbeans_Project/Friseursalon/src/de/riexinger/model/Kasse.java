/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.riexinger.model;
import java.math.BigDecimal;

/**
 *
 * @author Tim
 */
public class Kasse {

    private BigDecimal Kassenbestand;  
    
    public Kasse(BigDecimal Kassenbestand) {
        this.Kassenbestand = Kassenbestand;
    }
    
    public BigDecimal getKassenbestand() {
        return Kassenbestand;
    }

    public void setKassenbestand(BigDecimal Kassenbestand) {
        this.Kassenbestand = Kassenbestand;
    }   
}
