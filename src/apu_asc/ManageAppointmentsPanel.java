package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * ManageAppointmentsPanel — Create a new appointment.
 *
 * Flow:
 *  1. Staff enters Customer ID, date, time, service type.
 *  2. "Check Availability" fetches available technicians.
 *  3. Staff picks a technician, adds vehicle details & comments.
 *  4. "Confirm Appointment" saves via service layer.
 */
public class ManageAppointmentsPanel extends JPanel {

    private final CounterStaffFrame   frame;
    private final CounterStaff        loggedInUser;
    private final CounterStaffService service = new CounterStaffService();

    // Step 1 fields
    private JTextField customerIdField;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<String> serviceTypeCombo;

    // Step 2 — technician selection
    private JPanel     techPanel;
    private JComboBox<String> techCombo;
    private ArrayList<Technician> availableTechs = new ArrayList<>();

    // Step 3 fields
    private JTextField vehicleField;
    private JTextField commentsField;

    // Confirm button
    private JButton confirmBtn;

    // Status message
    private JLabel statusLabel;

    public ManageAppointmentsPanel(CounterStaffFrame frame, CounterStaff loggedInUser) {
        this.frame        = frame;
        this.loggedInUser = loggedInUser;
        setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Appointments", "Create a new service appointment for a customer."),
            BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
    }

    // ── Form ──────────────────────────────────────────────────────────────────

    private JScrollPane buildForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(0, 36, 36, 36));

        // ── Section 1: Customer & Schedule ──────────────────────────────────────
        form.add(sectionCard("1  Customer & Schedule", buildSection1()));

        form.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Section 2: Technician ────────────────────────────────────────────────
        techPanel = buildSection2Wrapper();
        form.add(techPanel);

        form.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Section 3: Vehicle & Notes ─────────────────────────────────────────
        form.add(sectionCard("3  Vehicle & Notes", buildSection3()));

        form.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Status + Confirm ───────────────────────────────────────────────────
        statusLabel = UITheme.createLabel(" ", UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmBtn = UITheme.createPrimaryButton("Confirm Appointment");
        confirmBtn.setPreferredSize(new Dimension(220, UITheme.BUTTON_H));
        confirmBtn.setMaximumSize(new Dimension(220, UITheme.BUTTON_H));
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> handleConfirm());

        form.add(statusLabel);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(confirmBtn);

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        return sp;
    }

    private JPanel buildSection1() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 0, 6, 16);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        customerIdField  = UITheme.createTextField();
        dateField        = UITheme.createTextField();
        timeField        = UITheme.createTextField();
        serviceTypeCombo = UITheme.createComboBox(new String[]{"Normal", "Major"});

        setPlaceholder(dateField, "YYYY-MM-DD");
        setPlaceholder(timeField, "HH:MM");

        // Row 0: Customer ID + Date
        addRow(p, g, 0, "Customer ID",  customerIdField,  "Appointment Date", dateField);
        // Row 1: Time + Service type
        addRow(p, g, 1, "Time",         timeField,         "Service Type",    serviceTypeCombo);

        // Check availability button
        JButton checkBtn = UITheme.createOutlineButton("Check Availability");
        checkBtn.setPreferredSize(new Dimension(200, UITheme.BUTTON_H));
        checkBtn.addActionListener(e -> handleCheckAvailability());

        g.gridx = 0; g.gridy = 2; g.gridwidth = 4; g.insets = new Insets(14, 0, 4, 0);
        p.add(checkBtn, g);
        g.gridwidth = 1;

        return p;
    }

    private JPanel buildSection2Wrapper() {
        UITheme.RoundedPanel wrapper = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        wrapper.setLayout(new BorderLayout());
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        JLabel lbl = UITheme.createLabel("2  Select Technician", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        header.add(lbl, BorderLayout.WEST);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));
        inner.setLayout(new BoxLayout(inner, BoxLayout.X_AXIS));

        techCombo = UITheme.createComboBox(new String[]{"— Check availability first —"});
        techCombo.setMaximumSize(new Dimension(360, UITheme.INPUT_H));
        techCombo.setEnabled(false);

        inner.add(techCombo);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(inner,  BorderLayout.CENTER);

        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(wrapper);
        return outer;
    }

    private JPanel buildSection3() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 0, 6, 16);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        vehicleField  = UITheme.createTextField();
        commentsField = UITheme.createTextField();
        setPlaceholder(commentsField, "Optional");

        addRow(p, g, 0, "Vehicle Details", vehicleField, "Comments", commentsField);
        return p;
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    private void handleCheckAvailability() {
        String date        = getClean(dateField);
        String time        = getClean(timeField);
        String serviceType = (String) serviceTypeCombo.getSelectedItem();

        if (date.isEmpty() || time.isEmpty()) {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Please enter date and time before checking availability.");
            return;
        }

        availableTechs = service.getAvailableTechnicians(date, time, serviceType);
        techCombo.removeAllItems();

        if (availableTechs.isEmpty()) {
            techCombo.addItem("— No technicians available for this slot —");
            techCombo.setEnabled(false);
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("No technicians are free at this time. Try a different slot.");
        } else {
            for (Technician t : availableTechs) {
                techCombo.addItem("[" + t.getUserid() + "]  " + t.getName());
            }
            techCombo.setEnabled(true);
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText(availableTechs.size() + " technician(s) available. Select one below.");
        }
    }

    private void handleConfirm() {
        String customerId  = getClean(customerIdField);
        String date        = getClean(dateField);
        String time        = getClean(timeField);
        String serviceType = (String) serviceTypeCombo.getSelectedItem();
        String vehicle     = getClean(vehicleField);
        String comments    = getClean(commentsField);

        if (customerId.isEmpty() || date.isEmpty() || time.isEmpty() || vehicle.isEmpty()) {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Customer ID, date, time, and vehicle details are all required.");
            return;
        }

        if (availableTechs.isEmpty() || !techCombo.isEnabled()) {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Please check availability and select a technician first.");
            return;
        }

        String technicianId = availableTechs.get(techCombo.getSelectedIndex()).getUserid();

        OperationResult res = service.createAppointment(
            customerId, technicianId, date, time,
            serviceType, vehicle, comments, loggedInUser
        );

        if (res.getResult()) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("✓  " + res.getMessage());
            clearForm();
        } else {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("✕  " + res.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void clearForm() {
        clearField(customerIdField, "");
        clearField(dateField, "YYYY-MM-DD");
        clearField(timeField, "HH:MM");
        clearField(vehicleField, "");
        clearField(commentsField, "Optional");
        serviceTypeCombo.setSelectedIndex(0);
        techCombo.removeAllItems();
        techCombo.addItem("— Check availability first —");
        techCombo.setEnabled(false);
        availableTechs.clear();
    }

    private void clearField(JTextField f, String placeholder) {
        if (placeholder.isEmpty()) { f.setText(""); return; }
        f.setText(placeholder);
        f.setForeground(UITheme.TEXT_SECONDARY);
    }

    private String getClean(JTextField f) {
        String t = f.getText().trim();
        if (t.equals("YYYY-MM-DD") || t.equals("HH:MM") || t.equals("Optional")) return "";
        return t;
    }

    /** Two-column row helper */
    private void addRow(JPanel p, GridBagConstraints g, int row,
                        String lbl1, Component c1, String lbl2, Component c2) {
        g.weightx = 0; g.gridx = 0; g.gridy = row * 2;
        p.add(UITheme.createLabel(lbl1, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.weightx = 1; g.gridx = 1;
        p.add(c1, g);
        g.weightx = 0; g.gridx = 2;
        p.add(UITheme.createLabel(lbl2, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.weightx = 1; g.gridx = 3;
        p.add(c2, g);
    }

    /** Wrap content in a titled rounded card */
    private JPanel sectionCard(String title, JPanel content) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        header.add(UITheme.createLabel(title, UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), BorderLayout.WEST);

        content.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));

        card.add(header,  BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(card);
        return wrapper;
    }

    private void setPlaceholder(JTextField f, String hint) {
        f.setForeground(UITheme.TEXT_SECONDARY);
        f.setText(hint);
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
