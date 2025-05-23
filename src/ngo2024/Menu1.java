/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package ngo2024;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import oru.inf.InfDB;
import oru.inf.InfException;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Jonas
 */
public class Menu1 extends javax.swing.JInternalFrame {
    
    private InfDB idb;
    private int anvandarID;

    /**
     * Creates new form Menu1
     */
    public Menu1(InfDB idb, int anvandarID) {
        this.idb = idb;
        this.anvandarID = anvandarID;
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI ui = (BasicInternalFrameUI)this.getUI();
        ui.setNorthPane(null);
        
        visaMinaProjekt(); // kör direkt 
        highlightKnapp(btnMinaProjekt); // Gör "mina projekt" knappen aktiv
        
        
        
    }
    
    private void visaMinaProjekt() {
        try {
            // 1. Skapa SQL-frågan
            String sql
                    = "SELECT pr.projektnamn, "
                    + "  (SELECT pa.namn "
                    + "   FROM projekt_partner pp "
                    + "   JOIN partner pa ON pp.partner_pid = pa.pid "
                    + "   WHERE pp.pid = pr.pid "
                    + "   LIMIT 1) AS partner, "
                    + "  l.namn AS land, "
                    + "  pr.status, pr.startdatum, pr.slutdatum "
                    + "FROM projekt pr "
                    + "JOIN ans_proj ap ON pr.pid = ap.pid "
                    + "JOIN land l ON pr.land = l.lid "
                    + "WHERE ap.aid = '" + anvandarID + "';";

            // 2. Hämta resultat
            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);

            // 3. Bygg tabellmodell
            DefaultTableModel modell = new DefaultTableModel();
            modell.setColumnIdentifiers(new Object[]{
                "Projektnamn", "Projektpartner", "Land", "Status", "Startdatum", "Slutdatum"
            });

            for (HashMap<String, String> rad : resultat) {
                modell.addRow(new Object[]{
                    rad.get("projektnamn"),
                    rad.get("partner"),
                    rad.get("land"),
                    rad.get("status"),
                    rad.get("startdatum"),
                    rad.get("slutdatum")
                });
            }

            // 4. Sätt modellen till tabellen
            tblProjekt.setModel(modell);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Kunde inte hämta mina projekt:\n" + e.getMessage());
        }
    }





     private void visaAvdelningensProjekt() {
        try {
            // 1. Hämta handläggarens avdelning
            String avdSql = "SELECT avdelning FROM anstalld WHERE aid = '" + anvandarID + "'";
            String avdelning = idb.fetchSingle(avdSql);

            // 2. Skapa SQL-fråga för att hämta projekt där någon från avdelningen deltar
            String sql
                    = "SELECT pr.pid, pr.projektnamn, "
                    + "       (SELECT pa.namn FROM projekt_partner pp "
                    + "        JOIN partner pa ON pp.partner_pid = pa.pid "
                    + "        WHERE pp.pid = pr.pid LIMIT 1) AS partner, "
                    + "       l.namn AS land, "
                    + "       pr.status, pr.startdatum, pr.slutdatum "
                    + "FROM projekt pr "
                    + "JOIN ans_proj ap ON pr.pid = ap.pid "
                    + "JOIN anstalld a ON ap.aid = a.aid "
                    + "LEFT JOIN projekt_partner pp ON pr.pid = pp.pid "
                    + "LEFT JOIN partner pa ON pp.partner_pid = pa.pid "
                    + "JOIN land l ON pr.land = l.lid "
                    + "WHERE a.avdelning = '" + avdelning + "'";

            // 3. Hämta resultat
            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);
            System.out.println("Antal rader (inkl. ev. dubbletter): " + resultat.size());

            // 4. Undvik att visa samma projekt flera gånger
            HashSet<String> visadeProjekt = new HashSet<>();

            // 5. Skapa tabellmodell
            DefaultTableModel modell = new DefaultTableModel();
            modell.setColumnIdentifiers(new Object[]{
                "Projektnamn", "Projektpartner", "Land", "Status", "Startdatum", "Slutdatum"
            });

            // 6. Lägg till rader
            for (HashMap<String, String> rad : resultat) {
                String pid = rad.get("pid");
                if (!visadeProjekt.contains(pid)) {
                    visadeProjekt.add(pid);
                    modell.addRow(new Object[]{
                        rad.get("projektnamn"),
                        rad.get("partner"),
                        rad.get("land"),
                        rad.get("status"),
                        rad.get("startdatum"),
                        rad.get("slutdatum")
                    });
                }
            }

            // 7. Visa modellen i tabellen
            tblProjekt.setModel(modell);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Kunde inte hämta avdelningens projekt:\n" + e.getMessage());
        }
    }





    
    private void highlightKnapp(JButton aktivKnapp) {
        // Återställ båda knappar
        btnMinaProjekt.setBackground(null);
        btnAvdelningensProjekt.setBackground(null);

        // Markera vald
        aktivKnapp.setBackground(new java.awt.Color(200, 230, 255)); // ljusblå
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl1 = new javax.swing.JLabel();
        btnAvdelningensProjekt = new javax.swing.JButton();
        btnMinaProjekt = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProjekt = new javax.swing.JTable();

        setBackground(new java.awt.Color(153, 255, 204));
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

        jButton4.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton4.setText("Visa partners i mina projekt");

        jButton5.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton5.setText("Sök projekt efter datum");

        tblProjekt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblProjekt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Projektnamn", "Projektpartner", "Land", "Status", "Startdatum"
            }
        ));
        tblProjekt.setGridColor(new java.awt.Color(0, 0, 0));
        tblProjekt.setRowHeight(25);
        tblProjekt.setRowMargin(1);
        tblProjekt.setShowGrid(false);
        tblProjekt.setShowHorizontalLines(true);
        jScrollPane4.setViewportView(tblProjekt);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAvdelningensProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMinaProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(301, 301, 301)
                                .addComponent(jButton5))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4)
                                    .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 167, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(btnMinaProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAvdelningensProjekt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
  
    private void btnMinaProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinaProjektActionPerformed

        visaMinaProjekt();
        highlightKnapp(btnMinaProjekt);
        
    }//GEN-LAST:event_btnMinaProjektActionPerformed

    private void btnAvdelningensProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvdelningensProjektActionPerformed
     
        visaAvdelningensProjekt();
        highlightKnapp(btnAvdelningensProjekt);
        
    }//GEN-LAST:event_btnAvdelningensProjektActionPerformed

    private void btnMinaProjektMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMinaProjektMouseClicked

    }//GEN-LAST:event_btnMinaProjektMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAvdelningensProjekt;
    private javax.swing.JButton btnMinaProjekt;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lbl1;
    private javax.swing.JTable tblProjekt;
    // End of variables declaration//GEN-END:variables
}
