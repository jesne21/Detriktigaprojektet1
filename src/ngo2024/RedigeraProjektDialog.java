/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo2024;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import oru.inf.InfDB;

public class RedigeraProjektDialog extends JDialog {

    private InfDB idb;
    private String pid;
    private int anvandarID;
    private String roll;

    private JTextField tfNamn, tfStatus, tfStartdatum, tfSlutdatum, tfKostnad;

    private DefaultListModel<String> listModel;
    private JList<String> lstHandlaggare;

    private JComboBox<String> cbTillgangligaHandlaggare;
    private JButton btnTaBortHandlaggare, btnLaggTillHandlaggare;
    private HashMap<String, String> namnTillAid = new HashMap<>();

    public RedigeraProjektDialog(InfDB idb, String pid, int anvandarID, String roll) {
        super((Frame) null, "Redigera projekt", true);
        this.idb = idb;
        this.pid = pid;
        this.anvandarID = anvandarID;
        this.roll = roll;

        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ==== FORMULÄRDEL ====
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Projektuppgifter"));

        pnlForm.add(new JLabel("Projektnamn:"));
        tfNamn = new JTextField();
        pnlForm.add(tfNamn);

        pnlForm.add(new JLabel("Status:"));
        tfStatus = new JTextField();
        pnlForm.add(tfStatus);

        pnlForm.add(new JLabel("Startdatum (YYYY-MM-DD):"));
        tfStartdatum = new JTextField();
        pnlForm.add(tfStartdatum);

        pnlForm.add(new JLabel("Slutdatum (YYYY-MM-DD):"));
        tfSlutdatum = new JTextField();
        pnlForm.add(tfSlutdatum);

        pnlForm.add(new JLabel("Kostnad:"));
        tfKostnad = new JTextField();
        pnlForm.add(tfKostnad);

        add(pnlForm, BorderLayout.CENTER);

        // ==== KNAPPANEL ====
        JPanel pnlKnapp = new JPanel();
        JButton btnSpara = new JButton("Spara ändringar");
        JButton btnAvbryt = new JButton("Avbryt");

        btnSpara.addActionListener(e -> sparaAndringar());
        btnAvbryt.addActionListener(e -> dispose());

        pnlKnapp.add(btnSpara);
        pnlKnapp.add(btnAvbryt);

        add(pnlKnapp, BorderLayout.SOUTH);

        // ==== HANDLÄGGAREDEL ====
        if (anvandarÄrProjektansvarig(pid)) {
            JPanel pnlHandlaggare = new JPanel(new BorderLayout());
            pnlHandlaggare.setBorder(BorderFactory.createTitledBorder("Handläggare i projektet"));

            // Visa befintliga handläggare
            listModel = new DefaultListModel<>();
            lstHandlaggare = new JList<>(listModel);
            pnlHandlaggare.add(new JScrollPane(lstHandlaggare), BorderLayout.CENTER);

            // Ta bort-knapp
            btnTaBortHandlaggare = new JButton("Ta bort vald handläggare");
            btnTaBortHandlaggare.addActionListener(e -> taBortHandlaggare());
            pnlHandlaggare.add(btnTaBortHandlaggare, BorderLayout.SOUTH);

            // Lägg till-sektion
            JPanel pnlLaggTill = new JPanel(new BorderLayout());
            cbTillgangligaHandlaggare = new JComboBox<>();
            btnLaggTillHandlaggare = new JButton("Lägg till handläggare");
            btnLaggTillHandlaggare.addActionListener(e -> laggTillHandlaggare());

            pnlLaggTill.add(cbTillgangligaHandlaggare, BorderLayout.CENTER);
            pnlLaggTill.add(btnLaggTillHandlaggare, BorderLayout.EAST);
            pnlHandlaggare.add(pnlLaggTill, BorderLayout.NORTH);

            add(pnlHandlaggare, BorderLayout.EAST);

            fyllHandlaggare();
            fyllTillgangligaHandlaggare();
        }

        fyllProjektData();
    }

    private void fyllProjektData() {
        try {
            String sql = "SELECT * FROM projekt WHERE pid = '" + pid + "'";
            HashMap<String, String> data = idb.fetchRow(sql);

            if (data != null) {
                tfNamn.setText(data.get("projektnamn"));
                tfStatus.setText(data.get("status"));
                tfStartdatum.setText(data.get("startdatum"));
                tfSlutdatum.setText(data.get("slutdatum"));
                tfKostnad.setText(data.get("kostnad"));
            } else {
                JOptionPane.showMessageDialog(this, "Kunde inte hämta projektdata.");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fel vid hämtning: " + e.getMessage());
            dispose();
        }
    }

    private void sparaAndringar() {
        try {
            String namn = tfNamn.getText();
            String status = tfStatus.getText();
            String startdatum = tfStartdatum.getText();
            String slutdatum = tfSlutdatum.getText();
            String kostnad = tfKostnad.getText();

            String sql = "UPDATE projekt SET "
                    + "projektnamn = '" + namn + "', "
                    + "status = '" + status + "', "
                    + "startdatum = '" + startdatum + "', "
                    + "slutdatum = '" + slutdatum + "', "
                    + "kostnad = '" + kostnad + "' "
                    + "WHERE pid = '" + pid + "'";

            idb.update(sql);
            JOptionPane.showMessageDialog(this, "Projektet har uppdaterats!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte spara ändringar:\n" + e.getMessage());
        }
    }

    private boolean anvandarÄrProjektansvarig(String pid) {
        try {
            String sql = "SELECT projektansvarig FROM projekt WHERE pid = '" + pid + "'";
            String ansvarigID = idb.fetchSingle(sql);
            return ansvarigID != null && ansvarigID.equals(String.valueOf(anvandarID));
        } catch (Exception e) {
            return false;
        }
    }

    private void fyllHandlaggare() {
        try {
            listModel.clear();
            String sql = "SELECT a.aid, a.fnamn, a.enamn FROM ans_proj ap JOIN anstalld a ON ap.aid = a.aid WHERE ap.pid = '" + pid + "'";
            var handlaggare = idb.fetchRows(sql);

            for (var h : handlaggare) {
                String namn = h.get("aid") + " - " + h.get("fnamn") + " " + h.get("enamn");
                listModel.addElement(namn);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte hämta handläggare:\n" + e.getMessage());
        }
    }

    private void taBortHandlaggare() {
        String vald = lstHandlaggare.getSelectedValue();
        if (vald == null) {
            JOptionPane.showMessageDialog(this, "Välj en handläggare att ta bort.");
            return;
        }

        String aid = vald.split(" - ")[0];

        try {
            String sql = "DELETE FROM ans_proj WHERE pid = '" + pid + "' AND aid = '" + aid + "'";
            idb.delete(sql);
            JOptionPane.showMessageDialog(this, "Handläggaren har tagits bort.");
            fyllHandlaggare();
            fyllTillgangligaHandlaggare();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte ta bort handläggare:\n" + e.getMessage());
        }
    }

    private void fyllTillgangligaHandlaggare() {
        try {
            cbTillgangligaHandlaggare.removeAllItems();
            namnTillAid.clear();

            String sql = """
                SELECT a.aid, a.fnamn, a.enamn
                FROM anstalld a
                WHERE a.aid NOT IN (
                    SELECT aid FROM ans_proj WHERE pid = '%s'
                )
            """.formatted(pid);

            var resultat = idb.fetchRows(sql);

            for (var rad : resultat) {
                String aid = rad.get("aid");
                String namn = rad.get("fnamn") + " " + rad.get("enamn");
                cbTillgangligaHandlaggare.addItem(namn);
                namnTillAid.put(namn, aid);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte hämta tillgängliga handläggare:\n" + e.getMessage());
        }
    }

    private void laggTillHandlaggare() {
        String namn = (String) cbTillgangligaHandlaggare.getSelectedItem();
        if (namn == null) {
            JOptionPane.showMessageDialog(this, "Välj en handläggare att lägga till.");
            return;
        }

        String aid = namnTillAid.get(namn);

        try {
            String sql = "INSERT INTO ans_proj (aid, pid) VALUES ('" + aid + "', '" + pid + "')";
            idb.insert(sql);
            JOptionPane.showMessageDialog(this, "Handläggaren har lagts till.");
            fyllHandlaggare();
            fyllTillgangligaHandlaggare();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte lägga till handläggare:\n" + e.getMessage());
        }
    }
}

