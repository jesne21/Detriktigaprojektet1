/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo2024;

/**
 *
 * @author Filip
 */
 
public class Validering {

    // Gör en kontroll ifall ett fält är tomt eller bara har mellanslag
    public static boolean textFaltArTomt(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Enkel e-postkontroll (kollar om eposten har @ och punkt)
    public static boolean arEpost(String epost) {
        if (textFaltArTomt(epost)) {
            return false;
        }
        return epost.matches(".+@.+\\..+");
    }

    // Kontroll av lösenord (minst 8 tecken, minst en bokstav och en siffra)
    public static boolean arStarktLosenord(String losenord) {
        if (textFaltArTomt(losenord)) {
            return false;
        }
        return losenord.length() >= 8 && losenord.matches(".*[A-Za-z].*") && losenord.matches(".*\\d.*");
    }

    // Kontroll av bara bokstäver (t.ex. namn)
    public static boolean baraBokstaver(String text) {
        if (textFaltArTomt(text)) {
            return false;
        }
        return text.matches("[a-zA-ZåäöÅÄÖ ]+");
    }

    // Kontroll av siffror (t.ex. ID-nummer, telefonnummer)
    public static boolean baraSiffror(String text) {
        if (textFaltArTomt(text)) {
            return false;
        }
        return text.matches("\\d+");
    }
}
