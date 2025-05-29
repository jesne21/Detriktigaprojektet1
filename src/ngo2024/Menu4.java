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
 
            // VALIDERING (via Valideringsklassen)
            if (!Validering.baraBokstaver(namn)|| !Validering.baraBokstaver(kontaktperson)) {
                JOptionPane.showMessageDialog(null, "Namn och kontaktperson får bara innehålla bokstäver.");
                return;
            }
            
            if(!Validering.arEpost(epost)){
                JOptionPane.showMessageDialog(null, "ogiltig e-postadress.");
                return;
            }
   
            if (!Validering.arTelefonnummer(telefon)) {
                JOptionPane.showMessageDialog(null, "Telefonnumret måste vara 7–15 siffror.");
                return;
            }
            
            if (Validering.textFaltArTomt(adress) || Validering.textFaltArTomt(branch)) {
                JOptionPane.showMessageDialog(null, "Adress och branch får inte vara tomma.");
                return;
            }
            
            if (cbStad.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en stad.");
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
            //  Hämta alla avdelningar
            List<HashMap<String, String>> avdelningar = idb.fetchRows("SELECT * FROM avdelning");
            if (avdelningar == null || avdelningar.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inga avdelningar finns i databasen.");
                return;
            }

            //  Fyll dropdown med avdelningsnamn
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
            if (val != JOptionPane.OK_OPTION) {
                return;
            }

            //  Hämta vald avdelningsinformation
            String valtNamn = (String) cbAvdelning.getSelectedItem();
            String valtAvdID = avdNamnTillID.get(valtNamn);
            HashMap<String, String> avd = idb.fetchRow("SELECT * FROM avdelning WHERE avdid = " + valtAvdID);

            //  Skapa textfält och fyll med befintliga värden
            JTextField tfNamn = new JTextField(avd.get("namn"));
            JTextField tfBeskrivning = new JTextField(avd.get("beskrivning"));
            JTextField tfAdress = new JTextField(avd.get("adress"));
            JTextField tfEpost = new JTextField(avd.get("epost"));
            JTextField tfTelefon = new JTextField(avd.get("telefon"));

            //  Fyll stad-combobox
            List<HashMap<String, String>> stader = idb.fetchRows("SELECT * FROM stad");
            JComboBox<String> cbStad = new JComboBox<>();
            Map<String, String> stadTillSID = new HashMap<>();
            for (HashMap<String, String> stad : stader) {
                cbStad.addItem(stad.get("namn"));
                stadTillSID.put(stad.get("namn"), stad.get("sid"));
            }
            cbStad.setSelectedItem(idb.fetchSingle("SELECT namn FROM stad WHERE sid = " + avd.get("stad")));

            //  Fyll chef-combobox
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

            //  Skapa inmatningsformulär
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
            if (spara != JOptionPane.OK_OPTION) {
                return;
            }

            // 8. VALIDERING (via Valideringsklassen)
            if (!Validering.baraBokstaver(tfNamn.getText())) {
                JOptionPane.showMessageDialog(null, "Namn får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfBeskrivning.getText())) {
                JOptionPane.showMessageDialog(null, "Beskrivning får inte vara tom.");
                return;
            }
            if (Validering.textFaltArTomt(tfAdress.getText())) {
                JOptionPane.showMessageDialog(null, "Adress får inte vara tom.");
                return;
            }
            if (!Validering.arEpost(tfEpost.getText())) {
                JOptionPane.showMessageDialog(null, "Ogiltig e-postadress.");
                return;
            }
            if (!Validering.arTelefonnummer(tfTelefon.getText())) {
                JOptionPane.showMessageDialog(null, "Telefonnummer måste vara 7–15 siffror.");
                return;
            }
            if (cbStad.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en stad.");
                return;
            }
            if (cbChef.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en chef.");
                return;
            }

            //  Skapa SQL och uppdatera
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

        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid uppdatering: " + e.getMessage());
        }
    }

    private void laggTillAvdelning() {
        try {
            //  Generera nytt ID
            String maxAvdID = idb.fetchSingle("SELECT MAX(avdid) FROM avdelning");
            int nyAvdID = (maxAvdID != null) ? Integer.parseInt(maxAvdID) + 1 : 1;

            //  Skapa inmatningsfält
            JTextField tfNamn = new JTextField();
            JTextField tfBeskrivning = new JTextField();
            JTextField tfAdress = new JTextField();
            JTextField tfEpost = new JTextField();
            JTextField tfTelefon = new JTextField();

            //  Hämta städer
            List<HashMap<String, String>> stader = idb.fetchRows("SELECT * FROM stad");
            JComboBox<String> cbStad = new JComboBox<>();
            Map<String, String> stadTillSID = new HashMap<>();
            for (HashMap<String, String> stad : stader) {
                cbStad.addItem(stad.get("namn"));
                stadTillSID.put(stad.get("namn"), stad.get("sid"));
            }

            //  Hämta chefer
            List<HashMap<String, String>> chefer = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
            JComboBox<String> cbChef = new JComboBox<>();
            Map<String, String> chefTillAID = new HashMap<>();
            for (HashMap<String, String> chef : chefer) {
                String chefNamn = chef.get("fornamn") + " " + chef.get("efternamn");
                cbChef.addItem(chefNamn);
                chefTillAID.put(chefNamn, chef.get("aid"));
            }

            //  Dialoginmatning
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
            if (val != JOptionPane.OK_OPTION) {
                return;
            }

            // VALIDERING via Valideringsklassen
            if (!Validering.baraBokstaver(tfNamn.getText())) {
                JOptionPane.showMessageDialog(null, "Namn får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfBeskrivning.getText())) {
                JOptionPane.showMessageDialog(null, "Beskrivning får inte vara tom.");
                return;
            }
            if (Validering.textFaltArTomt(tfAdress.getText())) {
                JOptionPane.showMessageDialog(null, "Adress får inte vara tom.");
                return;
            }
            if (!Validering.arEpost(tfEpost.getText())) {
                JOptionPane.showMessageDialog(null, "Ogiltig e-postadress.");
                return;
            }
            if (!Validering.arTelefonnummer(tfTelefon.getText())) {
                JOptionPane.showMessageDialog(null, "Telefonnummer måste vara 7–15 siffror.");
                return;
            }
            if (cbStad.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en stad.");
                return;
            }
            if (cbChef.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en chef.");
                return;
            }

            //  SQL-insättning
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
            JOptionPane.showMessageDialog(null, "Avdelningen tillagd!");

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
            if (val != JOptionPane.OK_OPTION) {
                return;
            }

            String valtNamn = (String) cbLand.getSelectedItem();
            String valtLID = namnTillLID.get(valtNamn);
            HashMap<String, String> land = idb.fetchRow("SELECT * FROM land WHERE lid = " + valtLID);

            // Fyll textfält med befintliga värden
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
            if (spara != JOptionPane.OK_OPTION) {
                return;
            }

            // VALIDERING
            if (!Validering.baraBokstaver(tfNamn.getText())) {
                JOptionPane.showMessageDialog(null, "Namnet får endast innehålla bokstäver.");
                return;
            }
            if (!Validering.baraBokstaver(tfSprak.getText())) {
                JOptionPane.showMessageDialog(null, "Språket får endast innehålla bokstäver.");
                return;
            }
            if (!Validering.baraBokstaver(tfValuta.getText())) {
                JOptionPane.showMessageDialog(null, "Valutan får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfTidszon.getText())) {
                JOptionPane.showMessageDialog(null, "Tidszon får inte vara tom.");
                return;
            }
            if (!Validering.baraBokstaver(tfPolitik.getText())) {
                JOptionPane.showMessageDialog(null, "Politisk struktur får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfEkonomi.getText())) {
                JOptionPane.showMessageDialog(null, "Ekonomi får inte vara tom.");
                return;
            }

            // SQL-uppdatering
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

        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid uppdatering: " + e.getMessage());
        }
    }


    private void laggTillLand() {
        try {
            // Generera nytt lid
            String maxLid = idb.fetchSingle("SELECT MAX(lid) FROM land");
            int nyLid = (maxLid != null) ? Integer.parseInt(maxLid) + 1 : 1;

            // Skapa inmatningsfält
            JTextField tfNamn = new JTextField();
            JTextField tfSprak = new JTextField();
            JTextField tfValuta = new JTextField();
            JTextField tfTidszon = new JTextField();
            JTextField tfPolitik = new JTextField();
            JTextField tfEkonomi = new JTextField();

            // Skapa formulärinnehåll
            Object[] fält = {
                "Namn:", tfNamn,
                "Språk:", tfSprak,
                "Valuta:", tfValuta,
                "Tidszon:", tfTidszon,
                "Politisk struktur:", tfPolitik,
                "Ekonomi:", tfEkonomi
            };

            int val = JOptionPane.showConfirmDialog(null, fält, "Lägg till land", JOptionPane.OK_CANCEL_OPTION);
            if (val != JOptionPane.OK_OPTION) {
                return;
            }

            // VALIDERING
            if (!Validering.baraBokstaver(tfNamn.getText())) {
                JOptionPane.showMessageDialog(null, "Namnet får endast innehålla bokstäver.");
                return;
            }
            if (!Validering.baraBokstaver(tfSprak.getText())) {
                JOptionPane.showMessageDialog(null, "Språket får endast innehålla bokstäver.");
                return;
            }
            if (!Validering.baraBokstaver(tfValuta.getText())) {
                JOptionPane.showMessageDialog(null, "Valutan får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfTidszon.getText())) {
                JOptionPane.showMessageDialog(null, "Tidszon får inte vara tom.");
                return;
            }
            if (!Validering.baraBokstaver(tfPolitik.getText())) {
                JOptionPane.showMessageDialog(null, "Politisk struktur får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfEkonomi.getText())) {
                JOptionPane.showMessageDialog(null, "Ekonomi får inte vara tom.");
                return;
            }

            // Skapa SQL-sats
            String sql = String.format(
                    "INSERT INTO land (lid, namn, sprak, valuta, tidszon, politisk_struktur, ekonomi) "
                    + "VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s')",
                    nyLid,
                    tfNamn.getText().trim(),
                    tfSprak.getText().trim(),
                    tfValuta.getText().trim(),
                    tfTidszon.getText().trim(),
                    tfPolitik.getText().trim(),
                    tfEkonomi.getText().trim()
            );

            // Kör insättning
            idb.insert(sql);
            JOptionPane.showMessageDialog(null, "Land tillagt!");

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
                String avid = avdelningar.get(i).get("avdid");
                avdelningsNamn[i] = namn;
                namnTillAvdID.put(namn, avid);
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
            if (resultat != JOptionPane.OK_OPTION) {
                return;
            }

            // VALIDERING
            if (!Validering.baraBokstaver(tfFornamn.getText())) {
                JOptionPane.showMessageDialog(null, "Förnamn får endast innehålla bokstäver.");
                return;
            }
            if (!Validering.baraBokstaver(tfEfternamn.getText())) {
                JOptionPane.showMessageDialog(null, "Efternamn får endast innehålla bokstäver.");
                return;
            }
            if (Validering.textFaltArTomt(tfAdress.getText())) {
                JOptionPane.showMessageDialog(null, "Adress får inte vara tom.");
                return;
            }
            if (!Validering.arEpost(tfEpost.getText())) {
                JOptionPane.showMessageDialog(null, "Ogiltig e-postadress.");
                return;
            }
            if (!Validering.arTelefonnummer(tfTelefon.getText())) {
                JOptionPane.showMessageDialog(null, "Telefonnumret måste vara 7–15 siffror.");
                return;
            }
            if (Validering.textFaltArTomt(tfAnstallningsdatum.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i anställningsdatum.");
                return;
            }
            if (cbAvdelning.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en avdelning.");
                return;
            }

            // Hämta nytt aid
            String maxAid = idb.fetchSingle("SELECT MAX(aid) FROM anstalld");
            int nyAid = (maxAid != null) ? Integer.parseInt(maxAid) + 1 : 1;

            // Skapa slumpmässigt lösenord
            String genereratLosenord = UUID.randomUUID().toString().substring(0, 8);

            // Skapa SQL
            String sql = String.format(
                    "INSERT INTO anstalld (aid, fornamn, efternamn, adress, epost, telefon, anstallningsdatum, losenord, avdelning) "
                    + "VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s)",
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
            if (val != JOptionPane.OK_OPTION) {
                return;
            }

            String valtNamn = (String) cbAnstalld.getSelectedItem();
            String aid = namnTillAID.get(valtNamn);

            // BEKRÄFTELSE INNAN RADERING
            int bekrafta = JOptionPane.showConfirmDialog(null,
                    "Är du säker på att du vill ta bort " + valtNamn + "?",
                    "Bekräfta borttagning", JOptionPane.YES_NO_OPTION);
            if (bekrafta != JOptionPane.YES_OPTION) {
                return;
            }

            // (VALFRITT) Kontrollera beroenden
            int kopplingar = Integer.parseInt(idb.fetchSingle("SELECT COUNT(*) FROM projekt WHERE ansvarig = " + aid));
            if (kopplingar > 0) {
                JOptionPane.showMessageDialog(null, "Anställd är projektansvarig och kan inte tas bort.");
                return;
            }

            idb.delete("DELETE FROM anstalld WHERE aid = " + aid);
            JOptionPane.showMessageDialog(null, "Anställd borttagen!");

        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid borttagning: " + e.getMessage());
        }
    }


    private void laggTillProjekt() {
        try {
            // Nytt projekt-ID
            String maxPidStr = idb.fetchSingle("SELECT MAX(pid) FROM projekt");
            int nyPid = (maxPidStr != null) ? Integer.parseInt(maxPidStr) + 1 : 1;

            // Hämta projektchefer
            List<HashMap<String, String>> chefer = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
            Map<String, String> chefTillAid = new HashMap<>();
            String[] chefAlternativ = new String[chefer.size()];
            for (int i = 0; i < chefer.size(); i++) {
                String namn = chefer.get(i).get("fornamn") + " " + chefer.get(i).get("efternamn");
                String aid = chefer.get(i).get("aid");
                chefAlternativ[i] = namn;
                chefTillAid.put(namn, aid);
            }

            // Hämta länder
            List<HashMap<String, String>> lander = idb.fetchRows("SELECT lid, namn FROM land");
            Map<String, String> landTillLid = new HashMap<>();
            String[] landAlternativ = new String[lander.size()];
            for (int i = 0; i < lander.size(); i++) {
                String namn = lander.get(i).get("namn");
                String lid = lander.get(i).get("lid");
                landAlternativ[i] = namn;
                landTillLid.put(namn, lid);
            }

            JComboBox<String> cbChef = new JComboBox<>(chefAlternativ);
            JComboBox<String> cbLand = new JComboBox<>(landAlternativ);

            // Textfält
            JTextField tfProjektnamn = new JTextField();
            JTextField tfBeskrivning = new JTextField();
            JTextField tfStartdatum = new JTextField();
            JTextField tfSlutdatum = new JTextField();
            JTextField tfKostnad = new JTextField();
            JTextField tfStatus = new JTextField();
            JTextField tfPrioritet = new JTextField();

            Object[] fält = {
                "Projektnamn:", tfProjektnamn,
                "Beskrivning:", tfBeskrivning,
                "Startdatum (YYYY-MM-DD):", tfStartdatum,
                "Slutdatum (YYYY-MM-DD):", tfSlutdatum,
                "Kostnad:", tfKostnad,
                "Status:", tfStatus,
                "Prioritet:", tfPrioritet,
                "Projektchef:", cbChef,
                "Land:", cbLand
            };

            int resultat = JOptionPane.showConfirmDialog(null, fält, "Lägg till projekt", JOptionPane.OK_CANCEL_OPTION);
            if (resultat != JOptionPane.OK_OPTION) {
                return;
            }

            // 🔍 VALIDERING
            if (Validering.textFaltArTomt(tfProjektnamn.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i projektnamn.");
                return;
            }
            if (Validering.textFaltArTomt(tfBeskrivning.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i beskrivning.");
                return;
            }
            if (Validering.textFaltArTomt(tfStartdatum.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i startdatum.");
                return;
            }
            if (Validering.textFaltArTomt(tfSlutdatum.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i slutdatum.");
                return;
            }
            if (!Validering.baraSiffror(tfKostnad.getText())) {
                JOptionPane.showMessageDialog(null, "Kostnad måste vara en siffra.");
                return;
            }
            if (Validering.textFaltArTomt(tfStatus.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i status.");
                return;
            }
            if (Validering.textFaltArTomt(tfPrioritet.getText())) {
                JOptionPane.showMessageDialog(null, "Fyll i prioritet.");
                return;
            }
            if (cbChef.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en projektchef.");
                return;
            }
            if (cbLand.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj ett land.");
                return;
            }

            // SQL
            String sql = String.format("INSERT INTO projekt (pid, projektnamn, beskrivning, startdatum, slutdatum, kostnad, status, prioritet, projektchef, land) "
                    + "VALUES (%d, '%s', '%s', '%s', '%s', %s, '%s', '%s', %s, %s)",
                    nyPid,
                    tfProjektnamn.getText().trim(),
                    tfBeskrivning.getText().trim(),
                    tfStartdatum.getText().trim(),
                    tfSlutdatum.getText().trim(),
                    tfKostnad.getText().trim(),
                    tfStatus.getText().trim(),
                    tfPrioritet.getText().trim(),
                    chefTillAid.get(cbChef.getSelectedItem()),
                    landTillLid.get(cbLand.getSelectedItem())
            );

            idb.insert(sql);
            JOptionPane.showMessageDialog(null, "Projekt tillagt!");

        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid tillägg: " + e.getMessage());
        }
    }


private void taBortProjekt() {
    try {
        List<HashMap<String, String>> projekt = idb.fetchRows("SELECT pid, projektnamn FROM projekt");
        if (projekt == null || projekt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inga projekt finns i databasen.");
            return;
        }

        Map<String, String> namnTillPid = new HashMap<>();
        String[] projektNamnArray = new String[projekt.size()];
        for (int i = 0; i < projekt.size(); i++) {
            String namn = projekt.get(i).get("projektnamn");
            String pid = projekt.get(i).get("pid");
            projektNamnArray[i] = namn;
            namnTillPid.put(namn, pid);
        }

        JComboBox<String> cbProjekt = new JComboBox<>(projektNamnArray);
        int val = JOptionPane.showConfirmDialog(null, cbProjekt, "Välj projekt att ta bort", JOptionPane.OK_CANCEL_OPTION);
        if (val == JOptionPane.OK_OPTION) {
            String valtNamn = (String) cbProjekt.getSelectedItem();
            String pid = namnTillPid.get(valtNamn);

            idb.delete("DELETE FROM projekt WHERE pid = " + pid);
            JOptionPane.showMessageDialog(null, "Projekt borttaget!");
        }
    } catch (InfException e) {
        JOptionPane.showMessageDialog(null, "Fel vid borttagning: " + e.getMessage());
    }
}

    

    private void redigeraProjekt() {
        try {
            List<HashMap<String, String>> projektLista = idb.fetchRows("SELECT pid, projektnamn FROM projekt");
            if (projektLista == null || projektLista.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inga projekt finns att redigera.");
                return;
            }

            Map<String, String> namnTillPID = new HashMap<>();
            String[] projektAlternativ = new String[projektLista.size()];
            for (int i = 0; i < projektLista.size(); i++) {
                String namn = projektLista.get(i).get("projektnamn");
                String pid = projektLista.get(i).get("pid");
                projektAlternativ[i] = namn;
                namnTillPID.put(namn, pid);
            }

            JComboBox<String> cbProjekt = new JComboBox<>(projektAlternativ);
            int val = JOptionPane.showConfirmDialog(null, cbProjekt, "Välj projekt att redigera", JOptionPane.OK_CANCEL_OPTION);
            if (val != JOptionPane.OK_OPTION) {
                return;
            }

            String valtNamn = (String) cbProjekt.getSelectedItem();
            String pid = namnTillPID.get(valtNamn);

            HashMap<String, String> projekt = idb.fetchRow("SELECT * FROM projekt WHERE pid = " + pid);

            // Projektchefer
            List<HashMap<String, String>> chefer = idb.fetchRows("SELECT aid, fornamn, efternamn FROM anstalld");
            Map<String, String> namnTillAID = new HashMap<>();
            String[] chefAlternativ = new String[chefer.size()];
            for (int i = 0; i < chefer.size(); i++) {
                String namn = chefer.get(i).get("fornamn") + " " + chefer.get(i).get("efternamn");
                String aid = chefer.get(i).get("aid");
                chefAlternativ[i] = namn;
                namnTillAID.put(namn, aid);
            }
            JComboBox<String> cbChef = new JComboBox<>(chefAlternativ);
            cbChef.setSelectedItem(projekt.get("projektchef"));

            // Länder
            List<HashMap<String, String>> länder = idb.fetchRows("SELECT lid, namn FROM land");
            Map<String, String> namnTillLID = new HashMap<>();
            String[] landAlternativ = new String[länder.size()];
            for (int i = 0; i < länder.size(); i++) {
                String namn = länder.get(i).get("namn");
                String lid = länder.get(i).get("lid");
                landAlternativ[i] = namn;
                namnTillLID.put(namn, lid);
            }
            JComboBox<String> cbLand = new JComboBox<>(landAlternativ);
            cbLand.setSelectedItem(projekt.get("land"));

            JTextField tfNamn = new JTextField(projekt.get("projektnamn"));
            JTextField tfBeskrivning = new JTextField(projekt.get("beskrivning"));
            JTextField tfStartdatum = new JTextField(projekt.get("startdatum"));
            JTextField tfSlutdatum = new JTextField(projekt.get("slutdatum"));
            JTextField tfKostnad = new JTextField(projekt.get("kostnad"));
            JTextField tfStatus = new JTextField(projekt.get("status"));
            JTextField tfPrioritet = new JTextField(projekt.get("prioritet"));

            Object[] fält = {
                "Projektnamn:", tfNamn,
                "Beskrivning:", tfBeskrivning,
                "Startdatum:", tfStartdatum,
                "Slutdatum:", tfSlutdatum,
                "Kostnad:", tfKostnad,
                "Status:", tfStatus,
                "Prioritet:", tfPrioritet,
                "Projektchef:", cbChef,
                "Land:", cbLand
            };

            int resultat = JOptionPane.showConfirmDialog(null, fält, "Redigera projekt", JOptionPane.OK_CANCEL_OPTION);
            if (resultat != JOptionPane.OK_OPTION) {
                return;
            }

            // Hämta gamla värden
            String gammaltNamn = projekt.get("projektnamn");
            String gammalBeskrivning = projekt.get("beskrivning");
            String gammaltStartdatum = projekt.get("startdatum");
            String gammaltSlutdatum = projekt.get("slutdatum");
            String gammalKostnad = projekt.get("kostnad");
            String gammalStatus = projekt.get("status");
            String gammalPrioritet = projekt.get("prioritet");
            String gammalChef = projekt.get("projektchef");
            String gammaltLand = projekt.get("land");

            String nyttNamn = tfNamn.getText().trim();
            String nyBeskrivning = tfBeskrivning.getText().trim();
            String nyttStartdatum = tfStartdatum.getText().trim();
            String nyttSlutdatum = tfSlutdatum.getText().trim();
            String nyKostnad = tfKostnad.getText().trim();
            String nyStatus = tfStatus.getText().trim();
            String nyPrioritet = tfPrioritet.getText().trim();
            String nyChef = namnTillAID.get(cbChef.getSelectedItem());
            String nyttLand = namnTillLID.get(cbLand.getSelectedItem());

            // Validera endast ändrade fält
            if (!nyttNamn.equals(gammaltNamn) && Validering.textFaltArTomt(nyttNamn)) {
                JOptionPane.showMessageDialog(null, "Projektnamn får inte vara tomt.");
                return;
            }
            if (!nyBeskrivning.equals(gammalBeskrivning) && Validering.textFaltArTomt(nyBeskrivning)) {
                JOptionPane.showMessageDialog(null, "Beskrivning får inte vara tom.");
                return;
            }
            if (!nyttStartdatum.equals(gammaltStartdatum) && Validering.textFaltArTomt(nyttStartdatum)) {
                JOptionPane.showMessageDialog(null, "Startdatum får inte vara tomt.");
                return;
            }
            if (!nyttSlutdatum.equals(gammaltSlutdatum) && Validering.textFaltArTomt(nyttSlutdatum)) {
                JOptionPane.showMessageDialog(null, "Slutdatum får inte vara tomt.");
                return;
            }
            if (!nyKostnad.equals(gammalKostnad) && !Validering.baraSiffror(nyKostnad)) {
                JOptionPane.showMessageDialog(null, "Kostnad måste vara en siffra.");
                return;
            }
            if (!nyStatus.equals(gammalStatus) && Validering.textFaltArTomt(nyStatus)) {
                JOptionPane.showMessageDialog(null, "Status får inte vara tomt.");
                return;
            }
            if (!nyPrioritet.equals(gammalPrioritet) && Validering.textFaltArTomt(nyPrioritet)) {
                JOptionPane.showMessageDialog(null, "Prioritet får inte vara tom.");
                return;
            }
            if (!nyChef.equals(gammalChef) && cbChef.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj en projektchef.");
                return;
            }
            if (!nyttLand.equals(gammaltLand) && cbLand.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Välj ett land.");
                return;
            }

            // Uppdatera databasen
            String sql = String.format("UPDATE projekt SET projektnamn='%s', beskrivning='%s', startdatum='%s', slutdatum='%s', kostnad='%s', status='%s', prioritet='%s', projektchef='%s', land='%s' WHERE pid=%s",
                    nyttNamn, nyBeskrivning, nyttStartdatum, nyttSlutdatum, nyKostnad, nyStatus, nyPrioritet, nyChef, nyttLand, pid);

            idb.update(sql);
            JOptionPane.showMessageDialog(null, "Projekt uppdaterat!");

        } catch (InfException e) {
            JOptionPane.showMessageDialog(null, "Fel vid redigering: " + e.getMessage());
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
        jBtnLaggTillProjekt = new javax.swing.JButton();
        jBtnTaBortProjekt = new javax.swing.JButton();
        jBtnRedigeraProjekt = new javax.swing.JButton();

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

        jBtnTaBortAnstalld.setText("Ta bort anställd");
        jBtnTaBortAnstalld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnTaBortAnstalldActionPerformed(evt);
            }
        });

        jBtnLaggTillProjekt.setText("Lägg till projekt");
        jBtnLaggTillProjekt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLaggTillProjektActionPerformed(evt);
            }
        });

        jBtnTaBortProjekt.setText("Ta bort projekt");
        jBtnTaBortProjekt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnTaBortProjektActionPerformed(evt);
            }
        });

        jBtnRedigeraProjekt.setText("Redigera projekt");
        jBtnRedigeraProjekt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRedigeraProjektActionPerformed(evt);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jBtnLaggTillProjekt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnTaBortAnstalld, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnLaggTillAnstalld, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                            .addComponent(jBtnTaBortProjekt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnRedigeraProjekt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(67, 67, 67)
                .addComponent(jBtnLaggTillProjekt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBtnTaBortProjekt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBtnRedigeraProjekt)
                .addContainerGap(102, Short.MAX_VALUE))
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

    private void jBtnLaggTillProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLaggTillProjektActionPerformed
        laggTillProjekt();
    }//GEN-LAST:event_jBtnLaggTillProjektActionPerformed

    private void jBtnTaBortProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnTaBortProjektActionPerformed
        taBortProjekt();
    }//GEN-LAST:event_jBtnTaBortProjektActionPerformed

    private void jBtnRedigeraProjektActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRedigeraProjektActionPerformed
        redigeraProjekt();
    }//GEN-LAST:event_jBtnRedigeraProjektActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnLaggTillAnstalld;
    private javax.swing.JButton jBtnLaggTillLand;
    private javax.swing.JButton jBtnLaggTillNyAvdelning;
    private javax.swing.JButton jBtnLaggTillPartner;
    private javax.swing.JButton jBtnLaggTillProjekt;
    private javax.swing.JButton jBtnLaggaTillAndraEllerTaBortUppgifterOmPartner;
    private javax.swing.JButton jBtnRedigeraAvdelning;
    private javax.swing.JButton jBtnRedigeraLand;
    private javax.swing.JButton jBtnRedigeraProjekt;
    private javax.swing.JButton jBtnTaBortAnstalld;
    private javax.swing.JButton jBtnTaBortPartner;
    private javax.swing.JButton jBtnTaBortProjekt;
    private javax.swing.JLabel jLblRedigeringForAdmin;
    // End of variables declaration//GEN-END:variables
}
