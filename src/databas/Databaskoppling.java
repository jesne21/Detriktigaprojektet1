/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package databas;

import gui.LoginFönster;
import oru.inf.InfDB;
import oru.inf.InfException;


/**
 *
 * @author Filip
 */
public class Databaskoppling {

    private static InfDB idb;

    public static void main(String[] args) throws InfException {

        try {
            idb = new InfDB("SDGSweden", "3306", "dbAdmin2024", "medlemskey");
            new LoginFönster(idb).setVisible(true);
            System.out.println("Databasen fungerar");

        } catch (InfException e) {
            System.out.println("Kunde inte ansluta till databasen" + e.getMessage());

        }

    }
// test
}
