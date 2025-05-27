/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo2024;

import oru.inf.InfDB;
import oru.inf.InfException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Filip
 */
class StatistikDialog extends JDialog {

    private InfDB idb;
    private int anvandarID;

    public StatistikDialog(InfDB idb, int anvandarID) {
        this.idb = idb;
        this.anvandarID = anvandarID;

        setTitle("Projektstatistik");
        setModal(true);
        setSize(500, 500);
        setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        StringBuilder statistik = new StringBuilder();

        try {
            // 1. Totalkostnad
            String kostnad = idb.fetchSingle("SELECT SUM(kostnad) AS total FROM projekt WHERE projektchef = '" + anvandarID + "'");
            statistik.append("Totalkostnad: ").append(kostnad != null ? kostnad : "0").append(" kr\n\n");

            // 2. Länder
            ArrayList<HashMap<String, String>> länder = idb.fetchRows(
                    "SELECT DISTINCT l.namn FROM projekt pr JOIN land l ON pr.land = l.lid WHERE projektchef = '" + anvandarID + "'");
            statistik.append("Länder:\n");
            for (HashMap<String, String> land : länder) {
                statistik.append("- ").append(land.get("namn")).append("\n");
            }
            statistik.append("\n");

            // 3. Partners
            ArrayList<HashMap<String, String>> partners = idb.fetchRows(
                    "SELECT DISTINCT pa.namn FROM projekt pr JOIN projekt_partner pp ON pr.pid = pp.pid JOIN partner pa ON pp.partner_pid = pa.pid WHERE pr.projektchef = '" + anvandarID + "'");
            statistik.append("Partners:\n");
            for (HashMap<String, String> partner : partners) {
                statistik.append("- ").append(partner.get("namn")).append("\n");
            }
            statistik.append("\n");

            // 4. Kostnad per land
            ArrayList<HashMap<String, String>> kostnadPerLand = idb.fetchRows(
                    "SELECT l.namn, SUM(pr.kostnad) AS kostnad FROM projekt pr JOIN land l ON pr.land = l.lid WHERE projektchef = '" + anvandarID + "' GROUP BY l.namn");
            statistik.append("Kostnad per land:\n");
            for (HashMap<String, String> rad : kostnadPerLand) {
                statistik.append(rad.get("namn")).append(": ").append(rad.get("kostnad")).append(" kr\n");
            }
        } catch (InfException e) {
            statistik.append("Fel vid hämtning av statistik: ").append(e.getMessage());
        }

        textArea.setText(statistik.toString());
    }
}
