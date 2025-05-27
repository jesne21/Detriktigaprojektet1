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

public class RedigeraProjektDialog extends JDialog {

    private InfDB idb;
    private String pid;
    private int anvandarID;
    private String roll;

    private JTextField tfNamn, tfStatus, tfStart, tfSlut, tfBeskrivning, tfPrioritet;

    public RedigeraProjektDialog(InfDB idb, String pid, int anvandarID, String roll) {
        this.idb = idb;
        this.pid = pid;
        this.anvandarID = anvandarID;
        this.roll = roll;
        setTitle("Redigera projekt");
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);

        initComponents();
        fyllIFalt();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));

        tfNamn = new JTextField();
        tfStatus = new JTextField();
        tfStart = new JTextField();
        tfSlut = new JTextField();
        tfBeskrivning = new JTextField();
        tfPrioritet = new JTextField();

        panel.add(new JLabel("Projektnamn:"));
        panel.add(tfNamn);
        panel.add(new JLabel("Status:"));
        panel.add(tfStatus);
        panel.add(new JLabel("Startdatum (ÅÅÅÅ-MM-DD):"));
        panel.add(tfStart);
        panel.add(new JLabel("Slutdatum (ÅÅÅÅ-MM-DD):"));
        panel.add(tfSlut);
        panel.add(new JLabel("Beskrivning:"));
        panel.add(tfBeskrivning);
        panel.add(new JLabel("Prioritet:"));
        panel.add(tfPrioritet);

        JButton btnSpara = new JButton("Spara");
        btnSpara.addActionListener(e -> sparaProjekt());

        JButton btnAvbryt = new JButton("Avbryt");
        btnAvbryt.addActionListener(e -> dispose());

        panel.add(btnSpara);
        panel.add(btnAvbryt);

        add(panel);
    }

    private void fyllIFalt() {
        try {
            HashMap<String, String> projekt = idb.fetchRow("SELECT * FROM projekt WHERE pid = '" + pid + "'");
            tfNamn.setText(projekt.get("projektnamn"));
            tfStatus.setText(projekt.get("status"));
            tfStart.setText(projekt.get("startdatum"));
            tfSlut.setText(projekt.get("slutdatum"));
            tfBeskrivning.setText(projekt.get("beskrivning"));
            tfPrioritet.setText(projekt.get("prioritet"));
        } catch (InfException e) {
            JOptionPane.showMessageDialog(this, "Kunde inte hämta projektdata: " + e.getMessage());
        }
    }

    private void sparaProjekt() {
        try {
            String sql = String.format("UPDATE projekt SET projektnamn='%s', status='%s', startdatum='%s', slutdatum='%s', beskrivning='%s', prioritet='%s' WHERE pid='%s'",
                    tfNamn.getText().trim(), tfStatus.getText().trim(), tfStart.getText().trim(),
                    tfSlut.getText().trim(), tfBeskrivning.getText().trim(), tfPrioritet.getText().trim(), pid);
            idb.update(sql);
            JOptionPane.showMessageDialog(this, "Projektet uppdaterat!");
            dispose();
        } catch (InfException e) {
            JOptionPane.showMessageDialog(this, "Fel vid uppdatering: " + e.getMessage());
        }
    }
}
