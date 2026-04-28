package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_Frame
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Technician_Frame — Technician main dashboard.
 */
class Technician_Frame extends JFrame {

    private final Technician technician;

    public Technician_Frame(Technician technician) {
        this.technician = technician;
        setTitle("Technician Dashboard - APU-ASC");
        setSize(860, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.dashboardHeader("Technician Dashboard", technician.getName(), e -> signOut()), BorderLayout.NORTH);
        root.add(buildCards(),  BorderLayout.CENTER);
        root.add(UITheme.dashboardFooter("Technician Module", technician.getUserid()), BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildCards() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));

        JLabel section = UITheme.createLabel("Quick Actions", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(1, 3, 16, 16));
        grid.setOpaque(false);

        grid.add(UITheme.dashboardCard("📅", "My Appointments",
            "View all appointments\nassigned to you.",
            e -> new Technician_AppointmentsDialog(this, technician)));

        grid.add(UITheme.dashboardCard("🔧", "Update Job Status",
            "Mark appointments as\nIn Progress or Completed.",
            e -> new Technician_UpdateStatusDialog(this, technician)));

        grid.add(UITheme.dashboardCard("✏", "Edit My Profile",
            "Update your personal\ninformation and password.",
            e -> new Technician_EditProfileDialog(this, technician)));

        wrapper.add(section, BorderLayout.NORTH);
        wrapper.add(grid,    BorderLayout.CENTER);
        return wrapper;
    }

    private void signOut() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose(); new Login_Frame(); }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_AppointmentsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_AppointmentsDialog {

    private final JFrame      parent;
    private final Technician  technician;
    private DefaultTableModel model;
    private JTable            table;
    private String            activeFilter = "All";

    public Technician_AppointmentsDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "My Appointments", 860, 540);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("My Appointments"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Status filter
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        filterRow.add(UITheme.createLabel("Filter:", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY));
        JComboBox<String> filter = UITheme.createComboBox(
            new String[]{"All", "Pending", "In Progress", "Completed"});
        filter.setPreferredSize(new Dimension(160, UITheme.INPUT_H));
        filter.addActionListener(e -> {
            activeFilter = (String) filter.getSelectedItem();
            loadData(activeFilter);
        });
        filterRow.add(filter);

        String[] cols = {"Appointment ID", "Customer ID", "Date", "Time", "Service Type", "Price (RM)", "Vehicle", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {120, 100, 90, 75, 110, 90, 140, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Colour-code Status column
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, f, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!sel) {
                    setBackground(row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    String s = v == null ? "" : v.toString();
                    setForeground(switch (s) {
                        case "Completed"   -> UITheme.SUCCESS;
                        case "In Progress" -> new Color(37, 82, 148);
                        case "Pending"     -> UITheme.WARNING;
                        default            -> UITheme.TEXT_PRIMARY;
                    });
                } else {
                    setBackground(UITheme.TABLE_SELECTED); setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });

        p.add(filterRow,                       BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton closeBtn = UITheme.createLinkButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(closeBtn);
    }

    private void loadData(String status) {
        model.setRowCount(0);
        for (Appointment a : FileHandler.getAllAppointments()) {
            if (!a.getTechnicianid().equals(technician.getUserid())) continue;
            if (!status.equals("All") && !a.getStatus().equals(status)) continue;
            model.addRow(new Object[]{a.getAppointmentid(), a.getCustomerid(),
                a.getDate(), a.getTime(), a.getServicetype(),
                String.format("%.2f", a.getPrice()), a.getVehicleDetails(), a.getStatus()});
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_UpdateStatusDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_UpdateStatusDialog {

    private final JFrame      parent;
    private final Technician  technician;
    private DefaultTableModel model;
    private JTable            table;

    public Technician_UpdateStatusDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "Update Job Status", 820, 500);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Update Job Status"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData();
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JLabel hint = UITheme.createLabel(
            "Select an appointment and click a status button to update it.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String[] cols = {"Appointment ID", "Customer ID", "Date", "Time", "Service Type", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {120, 100, 100, 80, 120, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        p.add(hint,                            BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton inProgressBtn = UITheme.createOutlineButton("Mark In Progress");
        JButton completedBtn  = UITheme.createPrimaryButton("Mark Completed");
        JButton closeBtn      = UITheme.createLinkButton("Close");

        inProgressBtn.setPreferredSize(new Dimension(180, UITheme.BUTTON_H));
        completedBtn.setPreferredSize(new Dimension(180, UITheme.BUTTON_H));

        inProgressBtn.addActionListener(e -> updateStatus("In Progress"));
        completedBtn.addActionListener(e  -> updateStatus("Completed"));
        closeBtn.addActionListener(e      -> dialog.dispose());

        return UITheme.buttonRow(closeBtn, inProgressBtn, completedBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Appointment a : FileHandler.getAllAppointments()) {
            if (!a.getTechnicianid().equals(technician.getUserid())) continue;
            if (a.getStatus().equals("Completed")) continue; // hide already done
            model.addRow(new Object[]{a.getAppointmentid(), a.getCustomerid(),
                a.getDate(), a.getTime(), a.getServicetype(), a.getStatus()});
        }
    }

    private void updateStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(parent, "Select an appointment from the table first.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE); return;
        }
        String apptId = (String) model.getValueAt(row, 0);

        // Find and update the appointment
        Appointment target = null;
        for (Appointment a : FileHandler.getAllAppointments())
            if (a.getAppointmentid().equals(apptId)) { target = a; break; }

        if (target == null) return;
        target.setStatus(newStatus);
        FileHandler.updateAppointment(target);

        JOptionPane.showMessageDialog(parent,
            "Appointment " + apptId + " marked as \"" + newStatus + "\".",
            "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_EditProfileDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_EditProfileDialog {

    private final JFrame      parent;
    private final Technician  technician;

    private JTextField     fName, fAge, fEmail, fUsername, fContact;
    private JPasswordField fCurrent, fNew;
    private JLabel         statusLabel;

    public Technician_EditProfileDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "Edit Profile", 560, 540);
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

        fName     = UITheme.createTextField(); fName.setText(technician.getName());
        fAge      = UITheme.createTextField(); fAge.setText(String.valueOf(technician.getAge()));
        fEmail    = UITheme.createTextField(); fEmail.setText(technician.getEmail());
        fUsername = UITheme.createTextField(); fUsername.setText(technician.getUsername());
        fContact  = UITheme.createTextField(); fContact.setText(technician.getContact());
        fCurrent  = UITheme.createPasswordField();
        fNew      = UITheme.createPasswordField();

        addPair(body, g, 0, "Full Name", fName,    "Age",     fAge);
        addPair(body, g, 1, "Email",     fEmail,   "Contact", fContact);

        g.gridx=0; g.gridy=2; g.weightx=0;
        body.add(UITheme.createLabel("Username", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1; g.gridwidth=3;
        body.add(fUsername, g); g.gridwidth=1;

        JSeparator sep = new JSeparator(); sep.setForeground(UITheme.BORDER);
        g.gridx=0; g.gridy=3; g.gridwidth=4; g.insets=new Insets(14,0,4,0);
        body.add(sep, g);
        g.gridy=4;
        body.add(UITheme.createLabel("Change Password", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
        g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        addPair(body, g, 5, "Current Password *", fCurrent, "New Password", fNew);

        JLabel hint = UITheme.createLabel("Current password is required to save changes.",
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
            if (!technician.getPassword().equals(cur)) {
                setStatus("Current password is incorrect.", false); return;
            }
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { setStatus("Age must be a number.", false); return; }
            String np = new String(fNew.getPassword());
            if (np.isEmpty()) np = technician.getPassword();

            technician.setName(fName.getText().trim());
            technician.setAge(age);
            technician.setEmail(fEmail.getText().trim());
            technician.setUsername(fUsername.getText().trim());
            technician.setContact(fContact.getText().trim());
            technician.setPassword(np);
            FileHandler.updateUser(technician);

            setStatus("Profile updated successfully.", true);
            fCurrent.setText(""); fNew.setText("");
        });

        reset.addActionListener(e -> {
            fName.setText(technician.getName()); fAge.setText(String.valueOf(technician.getAge()));
            fEmail.setText(technician.getEmail()); fUsername.setText(technician.getUsername());
            fContact.setText(technician.getContact()); fCurrent.setText(""); fNew.setText("");
            setStatus("Fields reset.", true);
        });

        cancel.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(cancel, reset, save);
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
