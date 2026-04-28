package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

// ══════════════════════════════════════════════════════════════════════════════
//  CS_CollectPaymentDialog
// ══════════════════════════════════════════════════════════════════════════════

class CS_CollectPaymentDialog {

    private final JFrame              parent;
    private final CounterStaffService service = new CounterStaffService();

    private JTextField        fApptId;
    private JComboBox<String> fMethod;
    private JLabel            lblApptId, lblCustomer, lblService, lblAmount, lblDate, lblStatus;
    private JPanel            summaryPanel;
    private JLabel            statusLabel;
    private JButton           confirmBtn;

    public CS_CollectPaymentDialog(JFrame parent) {
        this.parent = parent;
        JDialog dialog = UITheme.createDialog(parent, "Collect Payment", 680, 500);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Collect Payment"), BorderLayout.NORTH);
        dialog.add(buildBody(),            BorderLayout.CENTER);
        dialog.add(buildButtons(dialog),   BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(UITheme.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 16, 28));

        // Input row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        inputRow.setOpaque(false); inputRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        fApptId = UITheme.createTextField();
        fApptId.setPreferredSize(new Dimension(200, UITheme.INPUT_H));
        fMethod = UITheme.createComboBox(new String[]{"Cash", "Card", "Online"});
        fMethod.setPreferredSize(new Dimension(150, UITheme.INPUT_H));
        JButton loadBtn = UITheme.createOutlineButton("Load Details");
        loadBtn.addActionListener(e -> handleLoad());
        inputRow.add(UITheme.createLabel("Appointment ID:", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        inputRow.add(fApptId);
        inputRow.add(UITheme.createLabel("Payment Method:", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        inputRow.add(fMethod);
        inputRow.add(loadBtn);

        // Summary card
        summaryPanel = buildSummaryCard();
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setVisible(false);

        statusLabel = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        body.add(inputRow);
        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(summaryPanel);
        body.add(statusLabel);
        return body;
    }

    private JPanel buildSummaryCard() {
        JPanel card = new JPanel(new GridLayout(2, 3, 16, 12));
        card.setBackground(new Color(248, 249, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        lblApptId   = val(); lblCustomer = val(); lblService = val();
        lblAmount   = val(); lblAmount.setForeground(UITheme.ACCENT);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDate     = val(); lblStatus = val();

        card.add(pair("Appointment ID", lblApptId));
        card.add(pair("Customer ID",    lblCustomer));
        card.add(pair("Service Type",   lblService));
        card.add(pair("Amount Due",     lblAmount));
        card.add(pair("Appt Date",      lblDate));
        card.add(pair("Status",         lblStatus));
        return card;
    }

    private JPanel pair(String label, JLabel value) {
        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(UITheme.createLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY));
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        p.add(value); return p;
    }

    private JLabel val() {
        return UITheme.createLabel("—", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
    }

    private JPanel buildButtons(JDialog dialog) {
        confirmBtn = UITheme.createPrimaryButton("Confirm Payment");
        confirmBtn.setPreferredSize(new Dimension(180, UITheme.BUTTON_H));
        confirmBtn.setEnabled(false);
        confirmBtn.addActionListener(e -> handleConfirm(dialog));
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(close, confirmBtn);
    }

    private void handleLoad() {
        String id = fApptId.getText().trim();
        if (id.isEmpty()) { setStatus("Enter an Appointment ID.", false); return; }

        Appointment appt = null;
        for (Appointment a : FileHandler.getAllAppointments())
            if (a.getAppointmentid().equalsIgnoreCase(id)) { appt = a; break; }

        if (appt == null) {
            setStatus("Appointment not found: " + id, false);
            summaryPanel.setVisible(false); confirmBtn.setEnabled(false); return;
        }

        lblApptId.setText(appt.getAppointmentid());
        lblCustomer.setText(appt.getCustomerid());
        lblService.setText(appt.getServicetype());
        lblAmount.setText("RM " + String.format("%.2f", appt.getPrice()));
        lblDate.setText(appt.getDate());
        boolean done = appt.getStatus().equals("Completed");
        lblStatus.setText(appt.getStatus());
        lblStatus.setForeground(done ? UITheme.SUCCESS : UITheme.ERROR);
        summaryPanel.setVisible(true);
        summaryPanel.revalidate(); summaryPanel.repaint();

        if (done) {
            setStatus("Appointment completed. Ready to collect payment.", true);
            confirmBtn.setEnabled(true);
        } else {
            setStatus("Payment cannot be collected — appointment not yet Completed.", false);
            confirmBtn.setEnabled(false);
        }
    }

    private void handleConfirm(JDialog dialog) {
        OperationResult r = service.collectPayment(
            fApptId.getText().trim(), (String) fMethod.getSelectedItem());
        if (r.getResult()) {
            JOptionPane.showMessageDialog(parent,
                r.getMessage() + "\nYou can now generate a receipt from the Receipt screen.",
                "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        } else setStatus(r.getMessage(), false);
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? UITheme.SUCCESS : UITheme.ERROR);
        statusLabel.setText(msg);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  CS_GenerateReceiptDialog
// ══════════════════════════════════════════════════════════════════════════════

class CS_GenerateReceiptDialog {

    private final JFrame              parent;
    private final CounterStaffService service = new CounterStaffService();

    private JTextField fPaymentId;
    private JPanel     receiptArea;
    private JLabel     statusLabel;
    private JButton    saveBtn;

    public CS_GenerateReceiptDialog(JFrame parent) {
        this.parent = parent;
        JDialog dialog = UITheme.createDialog(parent, "Generate Receipt", 680, 560);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Generate Receipt"), BorderLayout.NORTH);
        dialog.add(buildBody(),          BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(UITheme.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));

        JPanel lookup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        lookup.setOpaque(false); lookup.setAlignmentX(Component.LEFT_ALIGNMENT);
        fPaymentId = UITheme.createTextField();
        fPaymentId.setPreferredSize(new Dimension(200, UITheme.INPUT_H));
        JButton loadBtn = UITheme.createPrimaryButton("Load Receipt");
        loadBtn.addActionListener(e -> handleLoad());
        lookup.add(UITheme.createLabel("Payment ID:", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        lookup.add(fPaymentId);
        lookup.add(loadBtn);

        statusLabel = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        receiptArea = new JPanel();
        receiptArea.setOpaque(false);
        receiptArea.setLayout(new BoxLayout(receiptArea, BoxLayout.Y_AXIS));
        receiptArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        receiptArea.setVisible(false);

        body.add(lookup);
        body.add(statusLabel);
        body.add(receiptArea);
        return body;
    }

    private JPanel buildButtons(JDialog dialog) {
        saveBtn = UITheme.createOutlineButton("💾  Save to File");
        saveBtn.setPreferredSize(new Dimension(160, UITheme.BUTTON_H));
        saveBtn.setVisible(false);
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(parent,
            "Receipt saved to receipt.txt", "Saved", JOptionPane.INFORMATION_MESSAGE));
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(close, saveBtn);
    }

    private void handleLoad() {
        String pid = fPaymentId.getText().trim();
        if (pid.isEmpty()) { setStatus("Enter a Payment ID.", false); return; }

        String receipt = service.generateReceipt(pid);
        if (receipt.startsWith("Error:")) {
            setStatus(receipt, false); receiptArea.setVisible(false); saveBtn.setVisible(false); return;
        }

        receiptArea.removeAll();
        receiptArea.add(buildReceiptCard(receipt));
        receiptArea.setVisible(true);
        receiptArea.revalidate(); receiptArea.repaint();
        setStatus("Receipt loaded successfully.", true);
        saveBtn.setVisible(true);
    }

    private JPanel buildReceiptCard(String raw) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(248, 249, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST; g.insets = new Insets(4, 0, 4, 20);

        int row = 0;
        for (String line : raw.split("\n")) {
            if (line.startsWith("=") || line.isBlank()) continue;
            int ci = line.indexOf(":");
            if (ci < 0) continue;
            String key = line.substring(0, ci).trim();
            String val = line.substring(ci + 1).trim();
            g.gridx=0; g.gridy=row; g.weightx=0;
            card.add(UITheme.createLabel(key, UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
            g.gridx=1; g.weightx=1;
            JLabel vl = UITheme.createLabel(val, UITheme.FONT_REGULAR, UITheme.TEXT_PRIMARY);
            if (key.equalsIgnoreCase("Amount")) {
                vl.setFont(new Font("Segoe UI", Font.BOLD, 15));
                vl.setForeground(UITheme.ACCENT);
            }
            card.add(vl, g);
            row++;
        }
        return card;
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? UITheme.SUCCESS : UITheme.ERROR);
        statusLabel.setText(msg);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  CS_EditProfileDialog
// ══════════════════════════════════════════════════════════════════════════════

class CS_EditProfileDialog {

    private final JFrame              parent;
    private final CounterStaff        user;
    private final CounterStaffService service = new CounterStaffService();

    private JTextField     fName, fAge, fEmail, fUsername, fContact;
    private JPasswordField fCurrent, fNew;
    private JLabel         statusLabel;

    public CS_EditProfileDialog(JFrame parent, CounterStaff user) {
        this.parent = parent; this.user = user;
        JDialog dialog = UITheme.createDialog(parent, "Edit Profile", 560, 560);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Edit Profile"), BorderLayout.NORTH);
        dialog.add(buildBody(),          BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 16); g.anchor = GridBagConstraints.WEST;

        fName     = field(user.getName());
        fAge      = field(String.valueOf(user.getAge()));
        fEmail    = field(user.getEmail());
        fUsername = field(user.getUsername());
        fContact  = field(user.getContact());
        fCurrent  = pass(); fNew = pass();

        addPair(body, g, 0, "Full Name",  fName,    "Age",     fAge);
        addPair(body, g, 1, "Email",      fEmail,   "Contact", fContact);

        g.gridx=0; g.gridy=2; g.weightx=0;
        body.add(UITheme.createLabel("Username", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1; g.gridwidth=3;
        body.add(fUsername, g); g.gridwidth=1;

        // Password section label
        JSeparator sep = new JSeparator(); sep.setForeground(UITheme.BORDER);
        g.gridx=0; g.gridy=3; g.gridwidth=4; g.insets=new Insets(14,0,4,0);
        body.add(sep, g);
        g.gridy=4;
        body.add(UITheme.createLabel("Change Password", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
        g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        addPair(body, g, 5, "Current Password *", fCurrent, "New Password", fNew);

        JLabel hint = UITheme.createLabel("Current password is required to save any changes.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=6; g.gridwidth=4; g.insets=new Insets(2,0,10,0);
        body.add(hint, g); g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        statusLabel = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=7; g.gridwidth=4; body.add(statusLabel, g);

        return UITheme.formScrollPane(body);
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton save   = UITheme.createPrimaryButton("Save Changes");
        JButton reset  = UITheme.createOutlineButton("Reset");
        JButton cancel = UITheme.createLinkButton("Cancel");
        save.setPreferredSize(new Dimension(150, UITheme.BUTTON_H));

        save.addActionListener(e -> {
            String cur = new String(fCurrent.getPassword());
            if (cur.isEmpty()) { setStatus("Current password is required.", false); return; }
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { setStatus("Age must be a number.", false); return; }
            String np = new String(fNew.getPassword());
            if (np.isEmpty()) np = user.getPassword();
            OperationResult r = service.updateProfile(user,
                fName.getText().trim(), age, fEmail.getText().trim(),
                fUsername.getText().trim(), cur, np, fContact.getText().trim());
            setStatus(r.getMessage(), r.getResult());
            if (r.getResult()) { fCurrent.setText(""); fNew.setText(""); }
        });

        reset.addActionListener(e -> {
            fName.setText(user.getName()); fAge.setText(String.valueOf(user.getAge()));
            fEmail.setText(user.getEmail()); fUsername.setText(user.getUsername());
            fContact.setText(user.getContact()); fCurrent.setText(""); fNew.setText("");
            setStatus("Fields reset to current values.", true);
        });

        cancel.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(cancel, reset, save);
    }

    private JTextField field(String val) {
        JTextField f = UITheme.createTextField(); f.setText(val); return f;
    }

    private JPasswordField pass() {
        JPasswordField f = UITheme.createPasswordField();
        f.setPreferredSize(new Dimension(200, UITheme.INPUT_H)); return f;
    }

    private void addPair(JPanel p, GridBagConstraints g, int row,
                         String l1, JComponent c1, String l2, JComponent c2) {
        g.gridx=0; g.gridy=row; g.weightx=0;
        p.add(UITheme.createLabel(l1, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1; p.add(c1, g);
        g.gridx=2; g.weightx=0;
        p.add(UITheme.createLabel(l2, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=3; g.weightx=1; p.add(c2, g);
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? UITheme.SUCCESS : UITheme.ERROR);
        statusLabel.setText(msg);
    }
}
