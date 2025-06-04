/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package ngo2024;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import oru.inf.InfDB;
import oru.inf.InfException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jonas
 */
public class Menu1 extends javax.swing.JInternalFrame {

    private InfDB idb;
    private boolean minaProjektValda = true;
    private int anvandarID;
    private String roll;
    private boolean redigeringsläge = false;

    /**
     * Creates new form Menu1
     */
    public Menu1(InfDB idb, int anvandarID, String roll) {
        this.idb = idb;
        this.anvandarID = anvandarID;
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Design
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI(); // Design
        ui.setNorthPane(null); // Design
        this.roll = roll;
        System.out.println("Inloggad som: " + roll); // säger vilken roll man är inloggad som, använt denna för att kunna se att rätt roll ser rätt saker
        cbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Alla", "Planerat", "Pågående", "Avslutat" // sätter in alternativen i comboboxen för filtrering
        }));
        btnStatistik.setVisible(RollValidering.ärProjektchef(roll));
        btnRedigeraUppgifter.setVisible(RollValidering.ärProjektchef(roll));
        btnLaggTillPartner.setVisible(RollValidering.ärProjektchef(roll));
        btnTaBortPartner.setVisible(RollValidering.ärProjektchef(roll));
        btnLaggTillHandlaggare.setVisible(RollValidering.ärProjektchef(roll));
        btnTaBortHandlaggare.setVisible(RollValidering.ärProjektchef(roll));
       // knapparna ovan kan bara projektchefer se  
       
        visaMinaProjekt(); // gör så att så fort användaren kommer in på meny 1 klickas btnvisaminaprojekt i direkt
        highlightKnapp(btnMinaProjekt); // Gör "mina projekt" knappen aktiv

    }
// ska visa projekt den inloggade personen är involverad i !
private void visaMinaProjekt() {
    try {
        String sql = "SELECT DISTINCT pr.pid, pr.projektnamn, "
                + "(SELECT pa.namn FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                + "l.namn AS land_namn, pr.status, pr.startdatum, pr.slutdatum, pr.beskrivning, pr.prioritet "
                + "FROM projekt pr "
                + "JOIN land l ON pr.land = l.lid "
                + "LEFT JOIN ans_proj ap ON pr.pid = ap.pid "
                + "WHERE ap.aid = '" + anvandarID + "' OR pr.projektchef = " + anvandarID;

        ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);

        DefaultTableModel modell = new DefaultTableModel();
        modell.setColumnIdentifiers(new Object[]{
            "Projektnamn", "Projektpartner", "Land", "Status", "Startdatum", "Slutdatum", "Beskrivning", "Prioritet"
        });

        HashSet<String> visadeProjekt = new HashSet<>();

        for (HashMap<String, String> rad : resultat) {
            String pid = rad.get("pid");
            if (visadeProjekt.contains(pid)) continue;
            visadeProjekt.add(pid);

            // Hittar landets namn från rätt kolumn och ser till så att nyckeln inte har nåt annat alias än det vi skriver
            String land = "Okänt";
            for (String key : rad.keySet()) {
            if ((key.toLowerCase().contains("namn") || key.toLowerCase().contains("land")) && !key.equalsIgnoreCase("projektnamn") && !key.equalsIgnoreCase("partner")) {
            land = rad.get(key);
            break;
    }
}


            // Hittar partner
            String partner = rad.get("partner");
            if (partner == null) partner = "Ingen";

            modell.addRow(new Object[]{
                rad.get("projektnamn"),
                partner,
                land,
                rad.get("status"),
                rad.get("startdatum"),
                rad.get("slutdatum"),
                rad.get("beskrivning"),
                rad.get("prioritet")
            });
        }

        tblProjekt.setModel(modell);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Kunde inte hämta mina projekt:\n" + e.getMessage());
    }
}


    // Metoden ska visa projekt där någon från den inloggade användarens avdelning är involverad i
    private void visaAvdelningensProjekt() {
    try {
        // Hämta användarens avdelning
        String avdelning = idb.fetchSingle("SELECT avdelning FROM anstalld WHERE aid = '" + anvandarID + "'");

        // SQL med JOIN till partner och land
        String sql = "SELECT DISTINCT pr.pid, pr.projektnamn, "
                + "(SELECT pa.namn FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                + "l.namn AS land_namn, pr.status, pr.startdatum, pr.slutdatum, pr.beskrivning, pr.prioritet "
                + "FROM projekt pr "
                + "JOIN ans_proj ap ON pr.pid = ap.pid "
                + "JOIN anstalld a ON ap.aid = a.aid "
                + "JOIN land l ON pr.land = l.lid "
                + "WHERE a.avdelning = '" + avdelning + "'";

        ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);
        HashSet<String> visadeProjekt = new HashSet<>();

        DefaultTableModel modell = new DefaultTableModel();
        modell.setColumnIdentifiers(new Object[]{"Projektnamn", "Projektpartner", "Land", "Status", "Startdatum", "Slutdatum", "Beskrivning", "Prioritet"});

        for (HashMap<String, String> rad : resultat) {
            String pid = rad.get("pid");
            if (visadeProjekt.contains(pid)) continue;
            visadeProjekt.add(pid);

            // Hittar partner
            String partner = rad.get("partner");
            if (partner == null) partner = "Ingen";

            // // Hittar landets namn från rätt kolumn och ser till så att nyckeln inte har nåt annat alias än det vi skriver
            String land = "Okänt";
            for (String key : rad.keySet()) {
                if ((key.toLowerCase().contains("namn") || key.toLowerCase().contains("land")) 
                        && !key.equalsIgnoreCase("projektnamn") && !key.equalsIgnoreCase("partner")) {
                    land = rad.get(key);
                    break;
                }
            }

            modell.addRow(new Object[]{
                rad.get("projektnamn"),
                partner,
                land,
                rad.get("status"),
                rad.get("startdatum"),
                rad.get("slutdatum"),
                rad.get("beskrivning"),
                rad.get("prioritet")
            });
        }

        tblProjekt.setModel(modell);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Kunde inte hämta avdelningens projekt:\n" + e.getMessage());
    }
}

    // Ändrar färgen på knapparna för att visa vilken som är vald
    private void highlightKnapp(JButton aktivKnapp) {
        // Återställ båda knapparnas bakgrundsfärg
        btnMinaProjekt.setBackground(null);
        btnAvdelningensProjekt.setBackground(null);

        // Markera vald knapp med ljusblå färg
        aktivKnapp.setBackground(new java.awt.Color(200, 230, 255)); // ljusblå
    }

    // Metoden ska visa projekt inom ett valt datumintervall (start- och slutdatum)
    private void visaProjektInomDatum() {
        try {
            System.out.println("Metod visaProjektInomDatum körs");

            Date startDatum = jDateChooserStart.getDate();
            Date slutDatum = jDateChooserSlut.getDate();

            if (startDatum == null || slutDatum == null) {
                JOptionPane.showMessageDialog(null, "Välj både start- och slutdatum.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startStr = sdf.format(startDatum);
            String slutStr = sdf.format(slutDatum);

            boolean minaProjektAktiv = minaProjektValda;

            String sql;

            if (minaProjektAktiv) {
                sql = "SELECT pr.pid AS pid, pr.projektnamn, "
                        + "(SELECT pa.namn FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid "
                        + "WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                        + "l.namn AS land_namn, pr.status, pr.startdatum, pr.slutdatum "
                        + "FROM projekt pr "
                        + "JOIN ans_proj ap ON pr.pid = ap.pid "
                        + "JOIN land l ON pr.land = l.lid "
                        + "WHERE ap.aid = '" + anvandarID + "' "
                        + "AND pr.startdatum >= '" + startStr + "' "
                        + "AND pr.slutdatum <= '" + slutStr + "'";
            } else {
                String avdelning = idb.fetchSingle("SELECT avdelning FROM anstalld WHERE aid = '" + anvandarID + "'");
                System.out.println("Din avdelning: " + avdelning);
                sql = "SELECT DISTINCT pr.pid, pr.projektnamn, "
                        + "(SELECT pa.namn FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid "
                        + "WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                        + "l.namn AS land_namn, pr.status, pr.startdatum, pr.slutdatum "
                        + "FROM projekt pr "
                        + "JOIN ans_proj ap ON pr.pid = ap.pid "
                        + "JOIN anstalld a ON ap.aid = a.aid "
                        + "JOIN land l ON pr.land = l.lid "
                        + "WHERE a.avdelning = '" + avdelning + "' "
                        + "AND pr.startdatum >= '" + startStr + "' "
                        + "AND pr.slutdatum <= '" + slutStr + "'";
            }

            System.out.println("SQL-fråga: " + sql);
            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);
            System.out.println("Antal träffar: " + resultat.size());

            HashSet<String> visadeProjekt = new HashSet<>();
            DefaultTableModel modell = new DefaultTableModel();
            modell.setColumnIdentifiers(new Object[]{
                "Projektnamn", "Projektpartner", "Land", "Status", "Startdatum", "Slutdatum"
            });

            for (HashMap<String, String> rad : resultat) {
                String pid = rad.get("pid");
                if (visadeProjekt.contains(pid)) {
                    continue;
                }
                visadeProjekt.add(pid);

                modell.addRow(new Object[]{
                    rad.get("projektnamn"),
                    rad.get("partner") != null ? rad.get("partner") : "Ingen",
                    rad.get("land") != null ? rad.get("land") : "Okänt",
                    rad.get("status"),
                    rad.get("startdatum"),
                    rad.get("slutdatum")
                });
            }

            tblProjekt.setModel(modell);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Kunde inte filtrera projekt:\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    // hämtar projektID från projekt tabellen
    private String getProjektID(String projektnamn) {
        try {
            return idb.fetchSingle("SELECT pid FROM projekt WHERE projektnamn = '" + projektnamn + "'");
        } catch (Exception e) {
            return null;
        }
    }

    private boolean anvandarÄrProjektchef(String pid) {
        try {
            String chefID = idb.fetchSingle("SELECT projektchef FROM projekt WHERE pid = '" + pid + "'");
            return chefID != null && chefID.equals(String.valueOf(anvandarID));
        } catch (Exception e) {
            return false;
        }
    }


    
    private void filtreraProjektEfterStatus() {
        String valdStatus = (String) cbStatusFilter.getSelectedItem();

        try {
            String sql;

            if (minaProjektValda) {
                sql = "SELECT pr.pid, pr.projektnamn, "
                        + "(SELECT pa.namn FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                        + "l.namn AS land, pr.status, pr.startdatum, pr.slutdatum, pr.beskrivning, pr.prioritet "
                        + "FROM projekt pr "
                        + "JOIN ans_proj ap ON pr.pid = ap.pid "
                        + "JOIN land l ON pr.land = l.lid "
                        + "WHERE ap.aid = '" + anvandarID + "'";
            } else {
                String avdelning = idb.fetchSingle("SELECT avdelning FROM anstalld WHERE aid = '" + anvandarID + "'");
                sql = "SELECT DISTINCT pr.pid, pr.projektnamn, "
                        + "(SELECT pa.namn FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                        + "l.namn AS land, pr.status, pr.startdatum, pr.slutdatum, pr.beskrivning, pr.prioritet "
                        + "FROM projekt pr "
                        + "JOIN ans_proj ap ON pr.pid = ap.pid "
                        + "JOIN anstalld a ON ap.aid = a.aid "
                        + "JOIN land l ON pr.land = l.lid "
                        + "WHERE a.avdelning = '" + avdelning + "'";
            }

            if (!valdStatus.equals("Alla")) {
                sql += " AND pr.status = '" + valdStatus + "'";
            }

            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);
            DefaultTableModel modell = new DefaultTableModel();
            modell.setColumnIdentifiers(new Object[]{"Projektnamn", "Projektpartner", "Land", "Status", "Startdatum", "Slutdatum", "Beskrivning", "Prioritet"});

            for (HashMap<String, String> rad : resultat) {
                modell.addRow(new Object[]{
                    rad.get("projektnamn"),
                    rad.get("partner"),
                    rad.get("land"),
                    rad.get("status"),
                    rad.get("startdatum"),
                    rad.get("slutdatum"),
                    rad.get("beskrivning"),
                    rad.get("prioritet")
                });
            }

            tblProjekt.setModel(modell);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Kunde inte filtrera efter status:\n" + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        btnAvdelningensProjekt = new javax.swing.JButton();
        btnMinaProjekt = new javax.swing.JButton();
        btnVisaPartners = new javax.swing.JButton();
        btnSokDatum = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProjekt = new javax.swing.JTable();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        btnRedigeraUppgifter = new javax.swing.JButton();
        btnStatistik = new javax.swing.JButton();
        btnLaggTillPartner = new javax.swing.JButton();
        btnTaBortHandlaggare = new javax.swing.JButton();
        btnTaBortPartner = new javax.swing.JButton();
        btnLaggTillHandlaggare = new javax.swing.JButton();
        cbStatusFilter = new javax.swing.JComboBox<>();
        btnFiltreraStatus = new javax.swing.JButton();
        jDateChooserStart = new com.toedter.calendar.JDateChooser();
        jDateChooserSlut = new com.toedter.calendar.JDateChooser();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(0, 102, 102));
        setPreferredSize(new java.awt.Dimension(860, 610));

        lbl1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl1.setText("Mina projekt");

        btnAvdelningensProjekt.setBackground(new java.awt.Color(153, 255, 204));
        btnAvdelningensProjekt.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        btnAvdelningensProjekt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/projekt_ikon.gif"))); // NOI18N
        btnAvdelningensProjekt.setText("Avdelningens projekt");
        btnAvdelningensProjekt.setBorder(null);
        btnAvdelningensProjekt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvdelningensProjektActionPerformed(evt);
            }
        });

        btnMinaProjekt.setBackground(new java.awt.Color(153, 255, 204));
        btnMinaProjekt.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        btnMinaProjekt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/projekt_ikon.gif"))); // NOI18N
        btnMinaProjekt.setText("Mina projekt");
        btnMinaProjekt.setBorder(null);
        btnMinaProjekt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnMinaProjektMouseClicked(evt);
            }
        });
        btnMinaProjekt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinaProjektActionPerformed(evt);
            }
        });

        btnVisaPartners.setBackground(new java.awt.Color(0, 128, 128));
        btnVisaPartners.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnVisaPartners.setText("Visa alla partners");
        btnVisaPartners.setBorder(new javax.swing.border.MatteBorder(null));
        btnVisaPartners.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisaPartnersActionPerformed(evt);
            }
        });

        btnSokDatum.setBackground(new java.awt.Color(153, 153, 153));
        btnSokDatum.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        btnSokDatum.setText("Sök projekt inom tidsperiod");
        btnSokDatum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSokDatumActionPerformed(evt);
            }
        });

        tblProjekt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblProjekt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Projektnamn", "Projektpartner", "Prioritet", "Land", "Status", "Beskrivning", "Startdatum", "Slutdatum"
            }
        ));
        tblProjekt.setGridColor(new java.awt.Color(0, 0, 0));
        tblProjekt.setRowHeight(25);
        tblProjekt.setRowMargin(1);
        tblProjekt.setShowGrid(false);
        tblProjekt.setShowHorizontalLines(true);
        jScrollPane4.setViewportView(tblProjekt);

        lbl2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl2.setText("Startdatum:");

        lbl3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl3.setText("Slutdatum:");

        btnRedigeraUppgifter.setBackground(new java.awt.Color(0, 128, 128));
        btnRedigeraUppgifter.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        btnRedigeraUppgifter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/edit_icon.png"))); // NOI18N
        btnRedigeraUppgifter.setText("Redigera uppgifter");
        btnRedigeraUppgifter.setBorder(new javax.swing.border.MatteBorder(null));
        btnRedigeraUppgifter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRedigeraUppgifterActionPerformed(evt);
            }
        });

        btnStatistik.setBackground(new java.awt.Color(0, 128, 128));
        btnStatistik.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        btnStatistik.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/icon_statistik.png"))); // NOI18N
        btnStatistik.setText("Statistik");
        btnStatistik.setBorder(new javax.swing.border.MatteBorder(null));
        btnStatistik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStatistikActionPerformed(evt);
            }
        });

        btnLaggTillPartner.setBackground(new java.awt.Color(0, 128, 128));
        btnLaggTillPartner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        btnLaggTillPartner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/add_icon.png"))); // NOI18N
        btnLaggTillPartner.setText("Lägg till partner");
        btnLaggTillPartner.setBorder(new javax.swing.border.MatteBorder(null));
        btnLaggTillPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaggTillPartnerActionPerformed(evt);
            }
        });

        btnTaBortHandlaggare.setBackground(new java.awt.Color(0, 128, 128));
        btnTaBortHandlaggare.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        btnTaBortHandlaggare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/icon_tabort.png"))); // NOI18N
        btnTaBortHandlaggare.setText("Ta bort handläggare");
        btnTaBortHandlaggare.setBorder(new javax.swing.border.MatteBorder(null));
        btnTaBortHandlaggare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaBortHandlaggareActionPerformed(evt);
            }
        });

        btnTaBortPartner.setBackground(new java.awt.Color(0, 128, 128));
        btnTaBortPartner.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        btnTaBortPartner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/icon_tabort.png"))); // NOI18N
        btnTaBortPartner.setText("Ta bort partner");
        btnTaBortPartner.setBorder(new javax.swing.border.MatteBorder(null));
        btnTaBortPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaBortPartnerActionPerformed(evt);
            }
        });

        btnLaggTillHandlaggare.setBackground(new java.awt.Color(0, 128, 128));
        btnLaggTillHandlaggare.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        btnLaggTillHandlaggare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ngo2024/Bilder/add_icon.png"))); // NOI18N
        btnLaggTillHandlaggare.setText("Lägg till handläggare");
        btnLaggTillHandlaggare.setBorder(new javax.swing.border.MatteBorder(null));
        btnLaggTillHandlaggare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaggTillHandlaggareActionPerformed(evt);
            }
        });

        cbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnFiltreraStatus.setText("Filtrera status");
        btnFiltreraStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltreraStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnRedigeraUppgifter, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnStatistik, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLaggTillPartner, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnTaBortPartner, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnTaBortHandlaggare, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLaggTillHandlaggare)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMinaProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAvdelningensProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnVisaPartners, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(78, 78, 78)
                        .addComponent(btnFiltreraStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 848, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnSokDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lbl2)
                            .addGap(18, 18, 18)
                            .addComponent(lbl3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jDateChooserStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jDateChooserSlut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(btnMinaProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnRedigeraUppgifter, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnStatistik, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLaggTillPartner, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLaggTillHandlaggare, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnTaBortHandlaggare, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnTaBortPartner, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lbl3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDateChooserStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDateChooserSlut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSokDatum)
                            .addComponent(cbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFiltreraStatus))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAvdelningensProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnVisaPartners, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMinaProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinaProjektActionPerformed
        minaProjektValda = true;
        visaMinaProjekt();
        highlightKnapp(btnMinaProjekt);

    }//GEN-LAST:event_btnMinaProjektActionPerformed

    private void btnAvdelningensProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvdelningensProjektActionPerformed
        minaProjektValda = false;
        visaAvdelningensProjekt();
        highlightKnapp(btnAvdelningensProjekt);

    }//GEN-LAST:event_btnAvdelningensProjektActionPerformed

    private void btnMinaProjektMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMinaProjektMouseClicked


    }//GEN-LAST:event_btnMinaProjektMouseClicked

    private void btnVisaPartnersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisaPartnersActionPerformed
int rad = tblProjekt.getSelectedRow();
if (rad == -1) {
    JOptionPane.showMessageDialog(null, "Välj ett projekt först.");
    return;
}

String projektnamn = tblProjekt.getValueAt(rad, 0).toString();
try {
    String pid = idb.fetchSingle("SELECT pid FROM projekt WHERE projektnamn = '" + projektnamn + "'");

    String sql =
        "SELECT pa.namn, pa.kontaktperson, pa.kontaktepost, pa.telefon, pa.adress, pa.branch " +
        "FROM projekt_partner pp " +
        "JOIN partner pa ON pp.partner_pid = pa.pid " +
        "WHERE pp.pid = " + pid;

    ArrayList<HashMap<String, String>> partners = idb.fetchRows(sql);

    StringBuilder sb = new StringBuilder("Partners i projektet \"" + projektnamn + "\":\n\n");
    if (partners.isEmpty()) {
        sb.append("Inga partners registrerade.");
    } else {
        for (HashMap<String, String> p : partners) {
            sb.append("- Partner Namn: ").append(p.get("namn")).append("\n")
              .append("  Kontaktperson: ").append(p.get("kontaktperson")).append("\n")
              .append("  E-post: ").append(p.get("kontaktepost")).append("\n")
              .append("  Telefon: ").append(p.get("telefon")).append("\n")
              .append("  Adress: ").append(p.get("adress")).append("\n")
              .append("  Branch: ").append(p.get("branch")).append("\n\n");
        }
    }

    JOptionPane.showMessageDialog(null, sb.toString(), "Projektpartners", JOptionPane.INFORMATION_MESSAGE);

} catch (InfException ex) {
    JOptionPane.showMessageDialog(null, "Kunde inte hämta partners:\n" + ex.getMessage());
}

    }//GEN-LAST:event_btnVisaPartnersActionPerformed

    private void btnSokDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSokDatumActionPerformed

       visaProjektInomDatum();

    }//GEN-LAST:event_btnSokDatumActionPerformed

    private void btnRedigeraUppgifterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedigeraUppgifterActionPerformed

        int rad = tblProjekt.getSelectedRow();
        if (rad == -1) {
            JOptionPane.showMessageDialog(null, "Välj ett projekt att redigera.");
            return;
        }

        String projektnamn = tblProjekt.getValueAt(rad, 0).toString();
        String pid = getProjektID(projektnamn);
        if (!anvandarÄrProjektchef(pid)) {
            JOptionPane.showMessageDialog(null, "Du är inte projektchef för detta projekt.");
            return;
        }

        new RedigeraProjektDialog(idb, pid, anvandarID, roll).setVisible(true);

    }//GEN-LAST:event_btnRedigeraUppgifterActionPerformed

    private void btnStatistikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatistikActionPerformed
        
    new StatistikDialog(idb, anvandarID).setVisible(true);
        
    }//GEN-LAST:event_btnStatistikActionPerformed

    private void btnLaggTillPartnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaggTillPartnerActionPerformed
  
        int rad = tblProjekt.getSelectedRow();
        if (rad == -1) {
            JOptionPane.showMessageDialog(null, "Välj ett projekt först.");
            return;
        }

        String projektnamn = tblProjekt.getValueAt(rad, 0).toString();
        String pid = getProjektID(projektnamn);
        if (!anvandarÄrProjektchef(pid)) {
            JOptionPane.showMessageDialog(null, "Du är inte projektchef för detta projekt.");
            return;
        }

        try {
            ArrayList<HashMap<String, String>> partners = idb.fetchRows("SELECT * FROM partner");
            String[] partnerNamn = partners.stream().map(p -> p.get("namn")).toArray(String[]::new);
            JComboBox<String> cb = new JComboBox<>(partnerNamn);

            int val = JOptionPane.showConfirmDialog(null, cb, "Välj partner att lägga till", JOptionPane.OK_CANCEL_OPTION);
            if (val == JOptionPane.OK_OPTION) {
                String namn = (String) cb.getSelectedItem();
                String partnerPid = idb.fetchSingle("SELECT pid FROM partner WHERE namn = '" + namn + "'");
                idb.insert("INSERT INTO projekt_partner (pid, partner_pid) VALUES ('" + pid + "', '" + partnerPid + "')");
                JOptionPane.showMessageDialog(null, "Partner tillagd.");
            }
        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid tillägg av partner:\n" + e.getMessage());
        }
        
    }//GEN-LAST:event_btnLaggTillPartnerActionPerformed

    private void btnTaBortHandlaggareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaBortHandlaggareActionPerformed
          
        int rad = tblProjekt.getSelectedRow();
        if (rad == -1) {
            JOptionPane.showMessageDialog(null, "Välj ett projekt först.");
            return;
        }

        String projektnamn = tblProjekt.getValueAt(rad, 0).toString();
        String pid = getProjektID(projektnamn);
        if (!anvandarÄrProjektchef(pid)) {
            JOptionPane.showMessageDialog(null, "Du är inte projektchef för detta projekt.");
            return;
        }

        try {
            ArrayList<HashMap<String, String>> handlaggare = idb.fetchRows(
                    "SELECT a.aid, a.fornamn, a.efternamn FROM ans_proj ap JOIN anstalld a ON ap.aid = a.aid WHERE ap.pid = '" + pid + "'");
            if (handlaggare.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inga handläggare finns kopplade till projektet.");
                return;
            }

            String[] namnArray = handlaggare.stream()
                    .map(h -> h.get("fornamn") + " " + h.get("efternamn")).toArray(String[]::new);
            JComboBox<String> cb = new JComboBox<>(namnArray);
            int val = JOptionPane.showConfirmDialog(null, cb, "Välj handläggare att ta bort", JOptionPane.OK_CANCEL_OPTION);

            if (val == JOptionPane.OK_OPTION) {
                String valtNamn = (String) cb.getSelectedItem();
                String aid = handlaggare.stream()
                        .filter(h -> (h.get("fornamn") + " " + h.get("efternamn")).equals(valtNamn))
                        .findFirst().get().get("aid");

                idb.delete("DELETE FROM ans_proj WHERE aid = '" + aid + "' AND pid = '" + pid + "'");
                JOptionPane.showMessageDialog(null, "Handläggare borttagen.");
            }
        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid borttagning av handläggare:\n" + e.getMessage());
        }
     
    }//GEN-LAST:event_btnTaBortHandlaggareActionPerformed

    private void btnTaBortPartnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaBortPartnerActionPerformed

        
        int rad = tblProjekt.getSelectedRow();
        if (rad == -1) {
            JOptionPane.showMessageDialog(null, "Välj ett projekt först.");
            return;
        }

        String projektnamn = tblProjekt.getValueAt(rad, 0).toString();
        String pid = getProjektID(projektnamn);
        if (!anvandarÄrProjektchef(pid)) {
            JOptionPane.showMessageDialog(null, "Du är inte projektchef för detta projekt.");
            return;
        }

        try {
            ArrayList<HashMap<String, String>> partners = idb.fetchRows(
                    "SELECT pa.namn, pa.pid FROM projekt_partner pp JOIN partner pa ON pp.partner_pid = pa.pid WHERE pp.pid = '" + pid + "'");

            if (partners.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Projektet har inga partners.");
                return;
            }

            String[] partnerNamn = partners.stream().map(p -> p.get("namn")).toArray(String[]::new);
            JComboBox<String> cb = new JComboBox<>(partnerNamn);
            int val = JOptionPane.showConfirmDialog(null, cb, "Välj partner att ta bort", JOptionPane.OK_CANCEL_OPTION);

            if (val == JOptionPane.OK_OPTION) {
                String namn = (String) cb.getSelectedItem();
                String partnerPid = idb.fetchSingle("SELECT pid FROM partner WHERE namn = '" + namn + "'");
                idb.delete("DELETE FROM projekt_partner WHERE pid = '" + pid + "' AND partner_pid = '" + partnerPid + "'");
                JOptionPane.showMessageDialog(null, "Partner borttagen.");
            }
        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid borttagning av partner:\n" + e.getMessage());
        }


        
    }//GEN-LAST:event_btnTaBortPartnerActionPerformed

    private void btnLaggTillHandlaggareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaggTillHandlaggareActionPerformed
       
            int rad = tblProjekt.getSelectedRow();
        if (rad == -1) {
            JOptionPane.showMessageDialog(null, "Välj ett projekt först.");
            return;
        }

        String projektnamn = tblProjekt.getValueAt(rad, 0).toString();
        String pid = getProjektID(projektnamn);
        if (!anvandarÄrProjektchef(pid)) {
            JOptionPane.showMessageDialog(null, "Du är inte projektchef för detta projekt.");
            return;
        }

        try {
            ArrayList<HashMap<String, String>> handlaggare = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
            String[] namnArray = handlaggare.stream()
                    .map(a -> a.get("fornamn") + " " + a.get("efternamn")).toArray(String[]::new);
            JComboBox<String> cb = new JComboBox<>(namnArray);
            int val = JOptionPane.showConfirmDialog(null, cb, "Välj handläggare att lägga till", JOptionPane.OK_CANCEL_OPTION);

            if (val == JOptionPane.OK_OPTION) {
                String valtNamn = (String) cb.getSelectedItem();
                String aid = handlaggare.stream()
                        .filter(a -> (a.get("fornamn") + " " + a.get("efternamn")).equals(valtNamn))
                        .findFirst().get().get("aid");

                idb.insert("INSERT INTO ans_proj (aid, pid) VALUES ('" + aid + "', '" + pid + "')");
                JOptionPane.showMessageDialog(null, "Handläggare tillagd.");
            }
        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid tillägg av handläggare:\n" + e.getMessage());
        }      
        
    }//GEN-LAST:event_btnLaggTillHandlaggareActionPerformed

    private void btnFiltreraStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltreraStatusActionPerformed
       
        filtreraProjektEfterStatus();
        
    }//GEN-LAST:event_btnFiltreraStatusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAvdelningensProjekt;
    private javax.swing.JButton btnFiltreraStatus;
    private javax.swing.JButton btnLaggTillHandlaggare;
    private javax.swing.JButton btnLaggTillPartner;
    private javax.swing.JButton btnMinaProjekt;
    private javax.swing.JButton btnRedigeraUppgifter;
    private javax.swing.JButton btnSokDatum;
    private javax.swing.JButton btnStatistik;
    private javax.swing.JButton btnTaBortHandlaggare;
    private javax.swing.JButton btnTaBortPartner;
    private javax.swing.JButton btnVisaPartners;
    private javax.swing.JComboBox<String> cbStatusFilter;
    private com.toedter.calendar.JDateChooser jDateChooserSlut;
    private com.toedter.calendar.JDateChooser jDateChooserStart;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JTable tblProjekt;
    // End of variables declaration//GEN-END:variables
}
