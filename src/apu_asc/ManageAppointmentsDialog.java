package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class ManageAppointmentsDialog {

    private final JFrame              parent;
    private final CounterStaff        loggedInUser;
    private final CounterStaffService service = new CounterStaffService();

    private JTextField        fCustomerId, fDate, fTime, fVehicle, fComments;
    private JComboBox<String> fServiceType, fTechnician;
    private ArrayList<Technician> availableTechs = new ArrayList<>();
    private JLabel            statusLabel;
    private JButton           confirmBtn;

    public ManageAppointmentsDialog(JFrame parent, CounterStaff loggedInUser) {
        this.parent       = parent;
        this.loggedInUser = loggedInUser;

        JDialog dialog = UITheme.createDialog(parent, "Create Appointment", 700, 580);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Create Appointment"), BorderLayout.NORTH);
        dialog.add(buildForm(),                                BorderLayout.CENTER);
        dialog.add(buildButtons(dialog),                       BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 16, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 16);
        g.anchor = GridBagConstraints.WEST;

        fCustomerId  = UITheme.createTextField();
        fDate        = UITheme.createTextField(); setPlaceholder(fDate, "YYYY-MM-DD");
        fTime        = UITheme.createTextField(); setPlaceholder(fTime, "HH:MM");
        fServiceType = UITheme.createComboBox(new String[]{"Normal", "Major"});
        fVehicle     = UITheme.createTextField();
        fComments    = UITheme.createTextField(); setPlaceholder(fComments, "Optional");
        fTechnician  = UITheme.createComboBox(new String[]{"— Check availability first —"});
        fTechnician.setEnabled(false);

        // Row 0
        addPair(form, g, 0, "Customer ID", fCustomerId, "Appointment Date", fDate);
        // Row 1
        addPair(form, g, 1, "Time (HH:MM)", fTime, "Service Type", fServiceType);

        // Check availability button
        JButton checkBtn = UITheme.createOutlineButton("Check Availability");
        checkBtn.setPreferredSize(new Dimension(200, UITheme.BUTTON_H));
        checkBtn.addActionListener(e -> checkAvailability());
        g.gridx=0; g.gridy=2; g.gridwidth=4; g.insets=new Insets(12,0,8,0);
        form.add(checkBtn, g); g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        // Row 3 — technician
        g.gridx=0; g.gridy=3; g.weightx=0;
        form.add(UITheme.createLabel("Available Technician", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1; g.gridwidth=3;
        form.add(fTechnician, g); g.gridwidth=1;

        // Row 4
        addPair(form, g, 4, "Vehicle Details", fVehicle, "Comments", fComments);

        JSeparator div = new JSeparator(); div.setForeground(UITheme.BORDER);
        g.gridx=0; g.gridy=5; g.gridwidth=4; g.insets=new Insets(12,0,4,0);
        form.add(div, g); g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        // Status
        statusLabel = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=6; g.gridwidth=4; form.add(statusLabel, g);

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null); sp.getViewport().setBackground(UITheme.WHITE);
        return sp;
    }

    private JPanel buildButtons(JDialog dialog) {
        confirmBtn = UITheme.createPrimaryButton("Confirm Appointment");
        confirmBtn.setPreferredSize(new Dimension(200, UITheme.BUTTON_H));
        confirmBtn.addActionListener(e -> handleConfirm(dialog));

        JButton cancelBtn = UITheme.createLinkButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(cancelBtn, confirmBtn);
    }

    private void checkAvailability() {
        String date = clean(fDate, "YYYY-MM-DD");
        String time = clean(fTime, "HH:MM");
        String stype = (String) fServiceType.getSelectedItem();

        if (date.isEmpty() || time.isEmpty()) {
            setStatus("Enter date and time before checking.", false); return;
        }

        availableTechs = service.getAvailableTechnicians(date, time, stype);
        fTechnician.removeAllItems();

        if (availableTechs.isEmpty()) {
            fTechnician.addItem("— No technicians available —");
            fTechnician.setEnabled(false);
            setStatus("No technicians available for this slot. Try a different time.", false);
        } else {
            for (Technician t : availableTechs)
                fTechnician.addItem("[" + t.getUserid() + "]  " + t.getName());
            fTechnician.setEnabled(true);
            setStatus(availableTechs.size() + " technician(s) available.", true);
        }
    }

    private void handleConfirm(JDialog dialog) {
        String cid    = fCustomerId.getText().trim();
        String date   = clean(fDate, "YYYY-MM-DD");
        String time   = clean(fTime, "HH:MM");
        String stype  = (String) fServiceType.getSelectedItem();
        String vehicle = clean(fVehicle, "");
        String comments = clean(fComments, "Optional");

        if (cid.isEmpty() || date.isEmpty() || time.isEmpty() || vehicle.isEmpty()) {
            setStatus("Customer ID, date, time, and vehicle details are required.", false); return;
        }
        if (availableTechs.isEmpty() || !fTechnician.isEnabled()) {
            setStatus("Please check availability and select a technician first.", false); return;
        }

        String techId = availableTechs.get(fTechnician.getSelectedIndex()).getUserid();
        OperationResult r = service.createAppointment(cid, techId, date, time, stype,
            vehicle, comments, loggedInUser);

        if (r.getResult()) {
            JOptionPane.showMessageDialog(parent, r.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        } else {
            setStatus(r.getMessage(), false);
        }
    }

    private void addPair(JPanel p, GridBagConstraints g, int row,
                         String l1, JComponent c1, String l2, JComponent c2) {
        g.gridx=0; g.gridy=row*2;   g.weightx=0;
        p.add(UITheme.createLabel(l1, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1;
        p.add(c1, g);
        g.gridx=2; g.weightx=0;
        p.add(UITheme.createLabel(l2, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=3; g.weightx=1;
        p.add(c2, g);
        // Spacer row
        g.gridx=0; g.gridy=row*2+1; g.gridwidth=4;
        p.add(Box.createRigidArea(new Dimension(0,2)), g); g.gridwidth=1;
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? UITheme.SUCCESS : UITheme.ERROR);
        statusLabel.setText(msg);
    }

    private String clean(JTextField f, String placeholder) {
        String t = f.getText().trim();
        return t.equals(placeholder) ? "" : t;
    }

    private void setPlaceholder(JTextField f, String hint) {
        f.setForeground(UITheme.TEXT_SECONDARY); f.setText(hint);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(UITheme.TEXT_PRIMARY); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isBlank()) { f.setText(hint); f.setForeground(UITheme.TEXT_SECONDARY); }
            }
        });
    }
}
