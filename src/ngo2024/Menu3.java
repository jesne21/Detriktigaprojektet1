/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package ngo2024;

import javax.swing.plaf.basic.BasicInternalFrameUI;
import oru.inf.InfDB;
import oru.inf.InfException;
import javax.swing.DefaultListModel;


/**
 *
 * @author Filip
 */
public class Menu3 extends javax.swing.JInternalFrame {

     private InfDB idb;
     private int anvandarID;
     private String roll;
     
    /**
     * Creates new form Menu1
     */
    public Menu3(InfDB idb, int anvandarID, String roll) {
        initComponents();
        this.idb = idb;
        this.anvandarID = anvandarID;
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI ui = (BasicInternalFrameUI)this.getUI();
        ui.setNorthPane(null);
        visaPersonalLista();
        this.roll = roll;
        
    }
    
private void visaPersonalLista() {
    try {

        // Hämta avdelning
        String sqlAvd = "SELECT avdelning FROM anstalld WHERE aid = " + anvandarID;
        String avdelningId = idb.fetchSingle(sqlAvd);
        System.out.println("Hämtad avdelning: " + avdelningId);

        if (avdelningId == null) {
            System.err.println("Ingen avdelning hittades för aid: " + anvandarID);
            return;
        }

        // Hämta personal i samma avdelning
        String sqlPersonal = "SELECT fornamn, efternamn, epost FROM anstalld WHERE avdelning = " + avdelningId;
        var resultat = idb.fetchRows(sqlPersonal);

        if (resultat == null || resultat.isEmpty()) {
            System.out.println("Inga personer hittades för avdelning: " + avdelningId);
            return;
        }

        DefaultListModel<String> modell = new DefaultListModel<>();

        for (var person : resultat) {
            String namn = person.get("fornamn") + " " + person.get("efternamn");
            String epost = person.get("epost");
            String rad = namn + " - " + epost;
            System.out.println("Lägger till i listan: " + rad);
            modell.addElement(rad);
        }

        jList1.setModel(modell);

    } catch (InfException e) {
        System.err.println("Fel vid hämtning: " + e.getMessage());
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jTextField1 = new javax.swing.JTextField();
        txtSok = new javax.swing.JTextField();
        btnSok = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();

        setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Min avdelning");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        jTextField1.setText("Sök efter handläggare");
        jTextField1.setEnabled(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        txtSok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSokActionPerformed(evt);
            }
        });

        btnSok.setText("Sök");
        btnSok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSokActionPerformed(evt);
            }
        });

        jTextField2.setText("Ange namn eller e-post nedan");
        jTextField2.setEnabled(false);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(121, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(txtSok)
                            .addComponent(btnSok)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(62, 62, 62))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSok)))
                .addContainerGap(221, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void txtSokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSokActionPerformed

    private void btnSokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSokActionPerformed
                                         
    String sokterm = txtSok.getText().toLowerCase().trim();

    if (sokterm.isEmpty()) {
        visaPersonalLista(); // Visa alla om fältet är tomt
        return;
    }

    try {
        // Hämta avdelning för inloggad användare
        String sqlAvd = "SELECT avdelning FROM anstalld WHERE aid = " + anvandarID;
        String avdelningId = idb.fetchSingle(sqlAvd);

        // Sök bland handläggare i samma avdelning
        String sql = 
            "SELECT a.fornamn, a.efternamn, a.epost " +
            "FROM anstalld a " +
            "JOIN handlaggare h ON a.aid = h.aid " +
            "WHERE a.avdelning = " + avdelningId + " AND " +
            "(LOWER(a.fornamn) LIKE '%" + sokterm + "%' " +
            "OR LOWER(a.efternamn) LIKE '%" + sokterm + "%' " +
            "OR LOWER(a.epost) LIKE '%" + sokterm + "%')";

        var resultat = idb.fetchRows(sql);
        DefaultListModel<String> modell = new DefaultListModel<>();

        if (resultat != null && !resultat.isEmpty()) {
            for (var person : resultat) {
                String namn = person.get("fornamn") + " " + person.get("efternamn");
                String epost = person.get("epost");
                modell.addElement(namn + " - " + epost);
            }
        } else {
            modell.addElement("Ingen matchning hittades.");
        }

        jList1.setModel(modell);

    } catch (InfException e) {
        System.err.println("Fel vid sökning: " + e.getMessage());
    }


    }//GEN-LAST:event_btnSokActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField txtSok;
    // End of variables declaration//GEN-END:variables
}
