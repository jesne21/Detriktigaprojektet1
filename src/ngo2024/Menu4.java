package ngo2024;

import javax.swing.plaf.basic.BasicInternalFrameUI;
import oru.inf.InfDB;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import oru.inf.InfException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.swing.*;
import java.util.UUID;





public class Menu4 extends javax.swing.JInternalFrame {

    private InfDB idb;
    private int anvandarID;
    private String roll;
    
    
    public Menu4(InfDB idb, int anvandarID, String roll) {
        initComponents();
        this.idb = idb;
        this.roll = roll;
        this.anvandarID = anvandarID;
    if (roll.equals("admin")) {
        initComponents(); // visa GUI endast om admin
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
    } else {
        JOptionPane.showMessageDialog(null, "Åtkomst nekad – endast admin kan se denna vy.");
        this.setVisible(false); // eller dispose(); om du vill stänga fönstret helt
    }
        
    }
    
    // --- Lägg till partner funktion
private void laggTillPartner() {
    try {
        List<HashMap<String, String>> stader = idb.fetchRows("SELECT sid, namn FROM stad");
        if (stader == null || stader.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inga städer finns i databasen.");
            return;
        }

        Map<String, String> stadNamnTillSID = new HashMap<>();
        String[] stadAlternativ = new String[stader.size() + 1]; // +1 för "Ny stad"

        for (int i = 0; i < stader.size(); i++) {
            String namn = stader.get(i).get("namn");
            String sid = stader.get(i).get("sid");
            stadAlternativ[i] = namn;
            stadNamnTillSID.put(namn, sid);
        }

        stadAlternativ[stadAlternativ.length - 1] = "Ny stad";
        JComboBox<String> cbStad = new JComboBox<>(stadAlternativ);

        JTextField tfNamn = new JTextField();
        JTextField tfKontaktperson = new JTextField();
        JTextField tfEpost = new JTextField();
        JTextField tfTelefon = new JTextField();
        JTextField tfAdress = new JTextField();
        JTextField tfBranch = new JTextField();

        Object[] fält = {
            "Namn:", tfNamn,
            "Kontaktperson:", tfKontaktperson,
            "Kontaktepost:", tfEpost,
            "Telefon:", tfTelefon,
            "Adress:", tfAdress,
            "Branch:", tfBranch,
            "Stad:", cbStad
        };

        int resultat = JOptionPane.showConfirmDialog(null, fält, "Lägg till ny partner", JOptionPane.OK_CANCEL_OPTION);
        if (resultat == JOptionPane.OK_OPTION) {
            String namn = tfNamn.getText().trim();
            String kontaktperson = tfKontaktperson.getText().trim();
            String epost = tfEpost.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String adress = tfAdress.getText().trim();
            String branch = tfBranch.getText().trim();
            String valdStadNamn = (String) cbStad.getSelectedItem();

            String stadSID;
            if (valdStadNamn.equals("Ny stad")) {
                String nyStad = JOptionPane.showInputDialog(null, "Ange namn på ny stad:");
                if (nyStad == null || nyStad.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingen stad angavs. Avbryter.");
                    return;
                }

                String maxSidStr = idb.fetchSingle("SELECT MAX(sid) FROM stad");
                int nySid = (maxSidStr != null) ? Integer.parseInt(maxSidStr) + 1 : 1;

                String insertStadSql = String.format("INSERT INTO stad (sid, namn, land) VALUES (%d, '%s', 1)", nySid, nyStad);
                idb.insert(insertStadSql);

                stadSID = String.valueOf(nySid);
            } else {
                stadSID = stadNamnTillSID.get(valdStadNamn);
            }

            if (namn.isEmpty() || kontaktperson.isEmpty() || epost.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fyll i alla obligatoriska fält.");
                return;
            }

            String maxPidStr = idb.fetchSingle("SELECT MAX(pid) FROM partner");
            int nyPid = (maxPidStr != null) ? Integer.parseInt(maxPidStr) + 1 : 1;

            String sql = String.format(
                "INSERT INTO partner (pid, namn, kontaktperson, kontaktepost, telefon, adress, branch, stad) " +
                "VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s', %s)",
                nyPid, namn, kontaktperson, epost, telefon, adress, branch, stadSID
            );
            idb.insert(sql);

            JOptionPane.showMessageDialog(null, "Partner tillagd!");
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Databasfel: " + e.getMessage());
    }
}


private void taBortPartner() {
    try {
        // Hämta alla partners
        List<HashMap<String, String>> partners = idb.fetchRows("SELECT pid, namn FROM partner");

        if (partners == null || partners.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Det finns inga partners att ta bort.");
            return;
        }

        // Skapa en dropdown med alla partnernamn
        JComboBox<String> comboBox = new JComboBox<>();
        HashMap<String, String> namnTillPID = new HashMap<>();

        for (HashMap<String, String> partner : partners) {
            String pid = partner.get("pid");
            String namn = partner.get("namn");
            comboBox.addItem(namn);
            namnTillPID.put(namn, pid);
        }

        // Visa popup
        int val = JOptionPane.showConfirmDialog(null, comboBox, "Välj partner att ta bort", JOptionPane.OK_CANCEL_OPTION);

        if (val == JOptionPane.OK_OPTION) {
            String valtNamn = (String) comboBox.getSelectedItem();
            String valtPID = namnTillPID.get(valtNamn);

            // Bekräfta borttagning
            int bekrafta = JOptionPane.showConfirmDialog(null,
                    "Är du säker på att du vill ta bort partnern \"" + valtNamn + "\"?",
                    "Bekräfta borttagning", JOptionPane.YES_NO_OPTION);

            if (bekrafta == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM partner WHERE pid = " + valtPID;
                idb.delete(sql);
                JOptionPane.showMessageDialog(null, "Partnern har tagits bort.");
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Fel vid borttagning av partner: " + e.getMessage());
    }
}


private void redigeraAvdelning() {
    try {
        // Hämta alla avdelningar
        List<HashMap<String, String>> avdelningar = idb.fetchRows("SELECT * FROM avdelning");
        if (avdelningar == null || avdelningar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inga avdelningar finns i databasen.");
            return;
        }

        // Dropdown med avdelningsnamn
        Map<String, String> avdNamnTillID = new HashMap<>();
        String[] avdNamnArray = new String[avdelningar.size()];
        for (int i = 0; i < avdelningar.size(); i++) {
            String namn = avdelningar.get(i).get("namn");
            String id = avdelningar.get(i).get("avdid");
            avdNamnArray[i] = namn;
            avdNamnTillID.put(namn, id);
        }

        JComboBox<String> cbAvdelning = new JComboBox<>(avdNamnArray);

        int val = JOptionPane.showConfirmDialog(null, cbAvdelning, "Välj avdelning att redigera", JOptionPane.OK_CANCEL_OPTION);
        if (val != JOptionPane.OK_OPTION) return;

        String valtNamn = (String) cbAvdelning.getSelectedItem();
        String valtAvdID = avdNamnTillID.get(valtNamn);
        HashMap<String, String> avd = idb.fetchRow("SELECT * FROM avdelning WHERE avdid = " + valtAvdID);

        JTextField tfNamn = new JTextField(avd.get("namn"));
        JTextField tfBeskrivning = new JTextField(avd.get("beskrivning"));
        JTextField tfAdress = new JTextField(avd.get("adress"));
        JTextField tfEpost = new JTextField(avd.get("epost"));
        JTextField tfTelefon = new JTextField(avd.get("telefon"));

        // Hämta städer
        List<HashMap<String, String>> stader = idb.fetchRows("SELECT * FROM stad");
        JComboBox<String> cbStad = new JComboBox<>();
        Map<String, String> stadTillSID = new HashMap<>();
        for (HashMap<String, String> stad : stader) {
            cbStad.addItem(stad.get("namn"));
            stadTillSID.put(stad.get("namn"), stad.get("sid"));
        }
        cbStad.setSelectedItem(idb.fetchSingle("SELECT namn FROM stad WHERE sid = " + avd.get("stad")));

        // Hämta chefer (anställda)
        List<HashMap<String, String>> chefer = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
        JComboBox<String> cbChef = new JComboBox<>();
        Map<String, String> chefTillAID = new HashMap<>();
        for (HashMap<String, String> chef : chefer) {
            String chefNamn = chef.get("fornamn") + " " + chef.get("efternamn");
            cbChef.addItem(chefNamn);
            chefTillAID.put(chefNamn, chef.get("aid"));
        }
        String nuvarandeChef = idb.fetchSingle("SELECT fornamn || ' ' || efternamn FROM anstalld WHERE aid = " + avd.get("chef"));
        cbChef.setSelectedItem(nuvarandeChef);

        Object[] meddelande = {
            "Namn:", tfNamn,
            "Beskrivning:", tfBeskrivning,
            "Adress:", tfAdress,
            "E-post:", tfEpost,
            "Telefon:", tfTelefon,
            "Stad:", cbStad,
            "Chef:", cbChef
        };

        int spara = JOptionPane.showConfirmDialog(null, meddelande, "Redigera avdelning", JOptionPane.OK_CANCEL_OPTION);
        if (spara == JOptionPane.OK_OPTION) {
            String sql = String.format(
                "UPDATE avdelning SET namn='%s', beskrivning='%s', adress='%s', epost='%s', telefon='%s', stad=%s, chef=%s WHERE avdid=%s",
                tfNamn.getText().trim(),
                tfBeskrivning.getText().trim(),
                tfAdress.getText().trim(),
                tfEpost.getText().trim(),
                tfTelefon.getText().trim(),
                stadTillSID.get(cbStad.getSelectedItem()),
                chefTillAID.get(cbChef.getSelectedItem()),
                valtAvdID
            );

            idb.update(sql);
            JOptionPane.showMessageDialog(null, "Avdelningen har uppdaterats!");
        }

    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid uppdatering: " + e.getMessage());
    }
}

private void laggTillAvdelning() {
    try {
        String maxAvdID = idb.fetchSingle("SELECT MAX(avdid) FROM avdelning");
        int nyAvdID = (maxAvdID != null) ? Integer.parseInt(maxAvdID) + 1 : 1;

        JTextField tfNamn = new JTextField();
        JTextField tfBeskrivning = new JTextField();
        JTextField tfAdress = new JTextField();
        JTextField tfEpost = new JTextField();
        JTextField tfTelefon = new JTextField();

        List<HashMap<String, String>> stader = idb.fetchRows("SELECT * FROM stad");
        JComboBox<String> cbStad = new JComboBox<>();
        Map<String, String> stadTillSID = new HashMap<>();
        for (HashMap<String, String> stad : stader) {
            cbStad.addItem(stad.get("namn"));
            stadTillSID.put(stad.get("namn"), stad.get("sid"));
        }

        List<HashMap<String, String>> chefer = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
        JComboBox<String> cbChef = new JComboBox<>();
        Map<String, String> chefTillAID = new HashMap<>();
        for (HashMap<String, String> chef : chefer) {
            String chefNamn = chef.get("fornamn") + " " + chef.get("efternamn");
            cbChef.addItem(chefNamn);
            chefTillAID.put(chefNamn, chef.get("aid"));
        }

        Object[] fält = {
            "Namn:", tfNamn,
            "Beskrivning:", tfBeskrivning,
            "Adress:", tfAdress,
            "E-post:", tfEpost,
            "Telefon:", tfTelefon,
            "Stad:", cbStad,
            "Chef:", cbChef
        };

        int val = JOptionPane.showConfirmDialog(null, fält, "Lägg till avdelning", JOptionPane.OK_CANCEL_OPTION);
        if (val == JOptionPane.OK_OPTION) {
            String sql = String.format(
                "INSERT INTO avdelning (avdid, namn, beskrivning, adress, epost, telefon, stad, chef) VALUES (%d, '%s', '%s', '%s', '%s', '%s', %s, %s)",
                nyAvdID,
                tfNamn.getText().trim(),
                tfBeskrivning.getText().trim(),
                tfAdress.getText().trim(),
                tfEpost.getText().trim(),
                tfTelefon.getText().trim(),
                stadTillSID.get(cbStad.getSelectedItem()),
                chefTillAID.get(cbChef.getSelectedItem())
            );

            idb.insert(sql);
            JOptionPane.showMessageDialog(null, "Avdelning tillagd!");
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid tillägg: " + e.getMessage());
    }
}

private void redigeraLand() {
    try {
        List<HashMap<String, String>> landLista = idb.fetchRows("SELECT * FROM land");
        if (landLista == null || landLista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inga länder finns i databasen.");
            return;
        }

        Map<String, String> namnTillLID = new HashMap<>();
        String[] landNamnArray = new String[landLista.size()];
        for (int i = 0; i < landLista.size(); i++) {
            String namn = landLista.get(i).get("namn");
            String lid = landLista.get(i).get("lid");
            landNamnArray[i] = namn;
            namnTillLID.put(namn, lid);
        }

        JComboBox<String> cbLand = new JComboBox<>(landNamnArray);
        int val = JOptionPane.showConfirmDialog(null, cbLand, "Välj land att redigera", JOptionPane.OK_CANCEL_OPTION);
        if (val != JOptionPane.OK_OPTION) return;

        String valtNamn = (String) cbLand.getSelectedItem();
        String valtLID = namnTillLID.get(valtNamn);
        HashMap<String, String> land = idb.fetchRow("SELECT * FROM land WHERE lid = " + valtLID);

        JTextField tfNamn = new JTextField(land.get("namn"));
        JTextField tfSprak = new JTextField(land.get("sprak"));
        JTextField tfValuta = new JTextField(land.get("valuta"));
        JTextField tfTidszon = new JTextField(land.get("tidszon"));
        JTextField tfPolitik = new JTextField(land.get("politisk_struktur"));
        JTextField tfEkonomi = new JTextField(land.get("ekonomi"));

        Object[] meddelande = {
            "Namn:", tfNamn,
            "Språk:", tfSprak,
            "Valuta:", tfValuta,
            "Tidszon:", tfTidszon,
            "Politisk struktur:", tfPolitik,
            "Ekonomi:", tfEkonomi
        };

        int spara = JOptionPane.showConfirmDialog(null, meddelande, "Redigera land", JOptionPane.OK_CANCEL_OPTION);
        if (spara == JOptionPane.OK_OPTION) {
            String sql = String.format(
                "UPDATE land SET namn='%s', sprak='%s', valuta='%s', tidszon='%s', politisk_struktur='%s', ekonomi='%s' WHERE lid=%s",
                tfNamn.getText().trim(),
                tfSprak.getText().trim(),
                tfValuta.getText().trim(),
                tfTidszon.getText().trim(),
                tfPolitik.getText().trim(),
                tfEkonomi.getText().trim(),
                valtLID
            );
            idb.update(sql);
            JOptionPane.showMessageDialog(null, "Landet har uppdaterats!");
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid uppdatering: " + e.getMessage());
    }
}

private void laggTillLand() {
    try {
        String maxLid = idb.fetchSingle("SELECT MAX(lid) FROM land");
        int nyLid = (maxLid != null) ? Integer.parseInt(maxLid) + 1 : 1;

        JTextField tfNamn = new JTextField();
        JTextField tfSprak = new JTextField();
        JTextField tfValuta = new JTextField();
        JTextField tfTidszon = new JTextField();
        JTextField tfPolitik = new JTextField();
        JTextField tfEkonomi = new JTextField();

        Object[] fält = {
            "Namn:", tfNamn,
            "Språk:", tfSprak,
            "Valuta:", tfValuta,
            "Tidszon:", tfTidszon,
            "Politisk struktur:", tfPolitik,
            "Ekonomi:", tfEkonomi
        };

        int val = JOptionPane.showConfirmDialog(null, fält, "Lägg till land", JOptionPane.OK_CANCEL_OPTION);
        if (val == JOptionPane.OK_OPTION) {
            String sql = String.format(
                "INSERT INTO land (lid, namn, sprak, valuta, tidszon, politisk_struktur, ekonomi) VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s')",
                nyLid,
                tfNamn.getText().trim(),
                tfSprak.getText().trim(),
                tfValuta.getText().trim(),
                tfTidszon.getText().trim(),
                tfPolitik.getText().trim(),
                tfEkonomi.getText().trim()
            );
            idb.insert(sql);
            JOptionPane.showMessageDialog(null, "Land tillagt!");
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid tillägg: " + e.getMessage());
    }
}

private void laggTillAnstalld() {
    try {
        // Hämta alla avdelningar
        List<HashMap<String, String>> avdelningar = idb.fetchRows("SELECT avdid, namn FROM avdelning");
        if (avdelningar == null || avdelningar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inga avdelningar finns i databasen.");
            return;
        }

        Map<String, String> namnTillAvdID = new HashMap<>();
        String[] avdelningsNamn = new String[avdelningar.size()];
        for (int i = 0; i < avdelningar.size(); i++) {
            String namn = avdelningar.get(i).get("namn");
            String avdid = avdelningar.get(i).get("avdid");
            avdelningsNamn[i] = namn;
            namnTillAvdID.put(namn, avdid);
        }

        JComboBox<String> cbAvdelning = new JComboBox<>(avdelningsNamn);

        JTextField tfFornamn = new JTextField();
        JTextField tfEfternamn = new JTextField();
        JTextField tfAdress = new JTextField();
        JTextField tfEpost = new JTextField();
        JTextField tfTelefon = new JTextField();
        JTextField tfAnstallningsdatum = new JTextField();

        Object[] fält = {
            "Förnamn:", tfFornamn,
            "Efternamn:", tfEfternamn,
            "Adress:", tfAdress,
            "E-post:", tfEpost,
            "Telefon:", tfTelefon,
            "Anställningsdatum (ÅÅÅÅ-MM-DD):", tfAnstallningsdatum,
            "Avdelning:", cbAvdelning
        };

        int resultat = JOptionPane.showConfirmDialog(null, fält, "Lägg till anställd", JOptionPane.OK_CANCEL_OPTION);
        if (resultat == JOptionPane.OK_OPTION) {
            String maxAid = idb.fetchSingle("SELECT MAX(aid) FROM anstalld");
            int nyAid = (maxAid != null) ? Integer.parseInt(maxAid) + 1 : 1;

            // Generera ett slumpmässigt lösenord
            String genereratLosenord = UUID.randomUUID().toString().substring(0, 8);

            String sql = String.format(
                "INSERT INTO anstalld (aid, fornamn, efternamn, adress, epost, telefon, anstallningsdatum, losenord, avdelning) " +
                "VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s)",
                nyAid,
                tfFornamn.getText().trim(),
                tfEfternamn.getText().trim(),
                tfAdress.getText().trim(),
                tfEpost.getText().trim(),
                tfTelefon.getText().trim(),
                tfAnstallningsdatum.getText().trim(),
                genereratLosenord,
                namnTillAvdID.get(cbAvdelning.getSelectedItem())
            );
            idb.insert(sql);
            JOptionPane.showMessageDialog(null, "Anställd tillagd!\nGenererat lösenord: " + genereratLosenord);
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid tillägg: " + e.getMessage());
    }
}

private void taBortAnstalld() {
    try {
        List<HashMap<String, String>> anstallda = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
        if (anstallda == null || anstallda.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inga anställda finns i databasen.");
            return;
        }

        Map<String, String> namnTillAID = new HashMap<>();
        String[] anstalldNamnArray = new String[anstallda.size()];
        for (int i = 0; i < anstallda.size(); i++) {
            String namn = anstallda.get(i).get("fornamn") + " " + anstallda.get(i).get("efternamn");
            String aid = anstallda.get(i).get("aid");
            anstalldNamnArray[i] = namn;
            namnTillAID.put(namn, aid);
        }

        JComboBox<String> cbAnstalld = new JComboBox<>(anstalldNamnArray);
        int val = JOptionPane.showConfirmDialog(null, cbAnstalld, "Välj anställd att ta bort", JOptionPane.OK_CANCEL_OPTION);
        if (val == JOptionPane.OK_OPTION) {
            String valtNamn = (String) cbAnstalld.getSelectedItem();
            String aid = namnTillAID.get(valtNamn);

            idb.delete("DELETE FROM anstalld WHERE aid = " + aid);
            JOptionPane.showMessageDialog(null, "Anställd borttagen!");
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid borttagning: " + e.getMessage());
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

        jLblRedigeringForAdmin = new javax.swing.JLabel();
        jBtnLaggTillAnstalld = new javax.swing.JButton();
        jBtnRedigeraAvdelning = new javax.swing.JButton();
        jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner = new javax.swing.JButton();
        jBtnRedigeraLand = new javax.swing.JButton();
        jBtnLaggTillPartner = new javax.swing.JButton();
        jBtnTaBortPartner = new javax.swing.JButton();
        jBtnLaggTillNyAvdelning = new javax.swing.JButton();
        jBtnLaggTillLand = new javax.swing.JButton();
        jBtnTaBortAnstalld = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 102));

        jLblRedigeringForAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLblRedigeringForAdmin.setText("Redigering för Admin");

        jBtnLaggTillAnstalld.setText("Lägg till anställd");
        jBtnLaggTillAnstalld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLaggTillAnstalldActionPerformed(evt);
            }
        });

        jBtnRedigeraAvdelning.setText("Redigera avdelning");
        jBtnRedigeraAvdelning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRedigeraAvdelningActionPerformed(evt);
            }
        });

        jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner.setText("Ändra uppgifter om en partner");
        jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLaggaTillAndraEllerTaBortUppgifterOmPartnerActionPerformed(evt);
            }
        });

        jBtnRedigeraLand.setText("Redigera land");
        jBtnRedigeraLand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRedigeraLandActionPerformed(evt);
            }
        });

        jBtnLaggTillPartner.setText("Lägg till partner");
        jBtnLaggTillPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLaggTillPartnerActionPerformed(evt);
            }
        });

        jBtnTaBortPartner.setText("Ta bort partner");
        jBtnTaBortPartner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnTaBortPartnerActionPerformed(evt);
            }
        });

        jBtnLaggTillNyAvdelning.setText("Lägg till ny avdelning");
        jBtnLaggTillNyAvdelning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLaggTillNyAvdelningActionPerformed(evt);
            }
        });

        jBtnLaggTillLand.setText("Lägg till nytt land");
        jBtnLaggTillLand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLaggTillLandActionPerformed(evt);
            }
        });

        jBtnTaBortAnstalld.setText("Ta bor anställd");
        jBtnTaBortAnstalld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnTaBortAnstalldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(292, 292, 292)
                .addComponent(jLblRedigeringForAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnLaggTillPartner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnTaBortPartner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBtnTaBortAnstalld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnLaggTillAnstalld, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                        .addGap(211, 211, 211)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jBtnLaggTillNyAvdelning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnRedigeraLand, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnRedigeraAvdelning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnLaggTillLand, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(168, 168, 168))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLblRedigeringForAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtnRedigeraAvdelning)
                    .addComponent(jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtnLaggTillPartner)
                    .addComponent(jBtnLaggTillNyAvdelning))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBtnTaBortPartner)
                .addGap(76, 76, 76)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBtnLaggTillAnstalld)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBtnTaBortAnstalld))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBtnRedigeraLand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBtnLaggTillLand)))
                .addContainerGap(262, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnLaggTillAnstalldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLaggTillAnstalldActionPerformed
        laggTillAnstalld();
    }//GEN-LAST:event_jBtnLaggTillAnstalldActionPerformed

    private void jBtnLaggaTillAndraEllerTaBortUppgifterOmPartnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLaggaTillAndraEllerTaBortUppgifterOmPartnerActionPerformed
    try {
        // Hämta alla partners
        var partners = idb.fetchColumn("SELECT namn FROM partner");

        if (partners == null || partners.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inga partners hittades i databasen.");
            return;
        }

        // Skapa en dropdown
        JComboBox<String> comboBox = new JComboBox<>(partners.toArray(new String[0]));
        int val = JOptionPane.showConfirmDialog(this, comboBox, "Välj en partner att redigera", JOptionPane.OK_CANCEL_OPTION);

        if (val == JOptionPane.OK_OPTION) {
            String valdPartner = (String) comboBox.getSelectedItem();

            // Hämta partnerns nuvarande uppgifter
            var partnerInfo = idb.fetchRow("SELECT * FROM partner WHERE namn = '" + valdPartner + "'");

            if (partnerInfo == null) {
                JOptionPane.showMessageDialog(this, "Kunde inte hämta partnerinfo.");
                return;
            }

            JTextField fNamn = new JTextField(partnerInfo.get("namn"));
            JTextField fKontaktperson = new JTextField(partnerInfo.get("kontaktperson"));
            JTextField fKontaktEpost = new JTextField(partnerInfo.get("kontaktepost"));
            JTextField fTelefon = new JTextField(partnerInfo.get("telefon"));
            JTextField fAdress = new JTextField(partnerInfo.get("adress"));
            JTextField fBranch = new JTextField(partnerInfo.get("branch"));

            Object[] redigeringsForm = {
                "Namn:", fNamn,
                "Kontaktperson:", fKontaktperson,
                "Kontakt e-post:", fKontaktEpost,
                "Telefon:", fTelefon,
                "Adress:", fAdress,
                "Branch:", fBranch
            };

            int valRedigering = JOptionPane.showConfirmDialog(this, redigeringsForm, "Redigera uppgifter", JOptionPane.OK_CANCEL_OPTION);

            if (valRedigering == JOptionPane.OK_OPTION) {
                String sqlUpdate = "UPDATE partner SET " +
                        "namn = '" + fNamn.getText().trim() + "', " +
                        "kontaktperson = '" + fKontaktperson.getText().trim() + "', " +
                        "kontaktepost = '" + fKontaktEpost.getText().trim() + "', " +
                        "telefon = '" + fTelefon.getText().trim() + "', " +
                        "adress = '" + fAdress.getText().trim() + "', " +
                        "branch = '" + fBranch.getText().trim() + "' " +
                        "WHERE namn = '" + valdPartner + "'";

                idb.update(sqlUpdate);
                JOptionPane.showMessageDialog(this, "Partnern har uppdaterats.");
            }
        }

    } catch (InfException e) {
        JOptionPane.showMessageDialog(this, "Fel: " + e.getMessage());
    }
    }//GEN-LAST:event_jBtnLaggaTillAndraEllerTaBortUppgifterOmPartnerActionPerformed

    private void jBtnLaggTillPartnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLaggTillPartnerActionPerformed
        laggTillPartner();
    }//GEN-LAST:event_jBtnLaggTillPartnerActionPerformed

    private void jBtnTaBortPartnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnTaBortPartnerActionPerformed
        taBortPartner();
    }//GEN-LAST:event_jBtnTaBortPartnerActionPerformed

    private void jBtnRedigeraAvdelningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRedigeraAvdelningActionPerformed
        redigeraAvdelning();
    }//GEN-LAST:event_jBtnRedigeraAvdelningActionPerformed

    private void jBtnLaggTillNyAvdelningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLaggTillNyAvdelningActionPerformed
        laggTillAvdelning();
    }//GEN-LAST:event_jBtnLaggTillNyAvdelningActionPerformed

    private void jBtnTaBortAnstalldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnTaBortAnstalldActionPerformed
         taBortAnstalld();
    }//GEN-LAST:event_jBtnTaBortAnstalldActionPerformed

    private void jBtnRedigeraLandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRedigeraLandActionPerformed
         redigeraLand();
    }//GEN-LAST:event_jBtnRedigeraLandActionPerformed

    private void jBtnLaggTillLandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLaggTillLandActionPerformed
        laggTillLand();
    }//GEN-LAST:event_jBtnLaggTillLandActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnLaggTillAnstalld;
    private javax.swing.JButton jBtnLaggTillLand;
    private javax.swing.JButton jBtnLaggTillNyAvdelning;
    private javax.swing.JButton jBtnLaggTillPartner;
    private javax.swing.JButton jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner;
    private javax.swing.JButton jBtnRedigeraAvdelning;
    private javax.swing.JButton jBtnRedigeraLand;
    private javax.swing.JButton jBtnTaBortAnstalld;
    private javax.swing.JButton jBtnTaBortPartner;
    private javax.swing.JLabel jLblRedigeringForAdmin;
    // End of variables declaration//GEN-END:variables
}
