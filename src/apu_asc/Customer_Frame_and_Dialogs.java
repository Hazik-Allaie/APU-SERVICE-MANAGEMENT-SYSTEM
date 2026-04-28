package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;

// ══════════════════════════════════════════════════════════════════════════════
//  Customer_Frame
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Customer_Frame — Customer main dashboard.
 */
class Customer_Frame extends JFrame {

    private final Customer customer;

    public Customer_Frame(Customer customer) {
        this.customer = customer;
        setTitle("Customer Dashboard - APU-ASC");
        setSize(860, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.dashboardHeader("Customer Dashboard", customer.getName(), e -> signOut()), BorderLayout.NORTH);
        root.add(buildCards(),  BorderLayout.CENTER);
        root.add(UITheme.dashboardFooter("Customer Module", customer.getUserid()), BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildCards() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));

        JLabel section = UITheme.createLabel("Quick Actions", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 16));
        grid.setOpaque(false);

        grid.add(UITheme.dashboardCard("📅", "My Appointments",
            "View all your service\nappointments.",
            e -> new Customer_AppointmentsDialog(this, customer)));

        grid.add(UITheme.dashboardCard("💬", "Submit Feedback",
            "Leave feedback on a\ncompleted appointment.",
            e -> new Customer_FeedbackDialog(this, customer)));

        grid.add(UITheme.dashboardCard("💳", "Payment History",
            "View all your past\npayment records.",
            e -> new Customer_PaymentHistoryDialog(this, customer)));

        grid.add(UITheme.dashboardCard("✏", "Edit My Profile",
            "Update your personal\ninformation and password.",
            e -> new Customer_EditProfileDialog(this, customer)));

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
//  Customer_AppointmentsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_AppointmentsDialog {

    private final JFrame           parent;
    private final Customer         customer;
    private final CustomerService  service = new CustomerService();
    private       DefaultTableModel model;
    private       JTable            table;

    public Customer_AppointmentsDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
        JDialog dialog = UITheme.createDialog(parent, "My Appointments", 820, 520);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("My Appointments"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData();
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Summary row
        JLabel summaryLabel = UITheme.createLabel("", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String[] cols = {"Appointment ID", "Date", "Time", "Service Type", "Price (RM)", "Vehicle", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {120, 100, 80, 110, 90, 160, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Colour-code Status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, f, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!sel) {
                    setBackground(row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    String s = v == null ? "" : v.toString();
                    setForeground(switch (s) {
                        case "Completed" -> UITheme.SUCCESS;
                        case "Pending"   -> UITheme.WARNING;
                        default          -> UITheme.TEXT_PRIMARY;
                    });
                } else {
                    setBackground(UITheme.TABLE_SELECTED); setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });

        model.addTableModelListener(e ->
            summaryLabel.setText("Total: " + model.getRowCount() + " appointment(s)"));

        p.add(summaryLabel,                    BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton viewBtn = UITheme.createOutlineButton("View Details");
        JButton closeBtn = UITheme.createLinkButton("Close");
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(parent, "Select an appointment to view details.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE); return;
            }
            showDetails((String) model.getValueAt(row, 0));
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(viewBtn, closeBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Appointment a : service.getMyAppointments(customer.getUserid()))
            model.addRow(new Object[]{a.getAppointmentid(), a.getDate(), a.getTime(),
                a.getServicetype(), String.format("%.2f", a.getPrice()),
                a.getVehicleDetails(), a.getStatus()});
    }

    private void showDetails(String apptId) {
        Appointment a = service.getAppointmentById(apptId, customer.getUserid());
        if (a == null) return;

        JDialog d = UITheme.createDialog(parent, "Appointment Details", 460, 420);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Appointment Details"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 16); g.anchor = GridBagConstraints.WEST;

        String[][] fields = {
            {"Appointment ID", a.getAppointmentid()},
            {"Date",           a.getDate()},
            {"Time",           a.getTime()},
            {"Service Type",   a.getServicetype()},
            {"Price",          "RM " + String.format("%.2f", a.getPrice())},
            {"Vehicle",        a.getVehicleDetails()},
            {"Technician ID",  a.getTechnicianid()},
            {"Status",         a.getStatus()},
            {"Comments",       a.getComments() == null ? "—" : a.getComments()},
        };

        for (int i = 0; i < fields.length; i++) {
            g.gridx=0; g.gridy=i; g.weightx=0;
            body.add(UITheme.createLabel(fields[i][0], UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
            g.gridx=1; g.weightx=1;
            JLabel vl = UITheme.createLabel(fields[i][1], UITheme.FONT_REGULAR, UITheme.TEXT_PRIMARY);
            if (fields[i][0].equals("Status")) {
                vl.setForeground("Completed".equals(fields[i][1]) ? UITheme.SUCCESS : UITheme.WARNING);
                vl.setFont(UITheme.FONT_BOLD);
            }
            body.add(vl, g);
        }

        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> d.dispose());
        d.add(UITheme.formScrollPane(body), BorderLayout.CENTER);
        d.add(UITheme.buttonRow(close),     BorderLayout.SOUTH);
        d.setVisible(true);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Customer_FeedbackDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_FeedbackDialog {

    private final JFrame          parent;
    private final Customer        customer;
    private final CustomerService service = new CustomerService();

    public Customer_FeedbackDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
        JDialog dialog = UITheme.createDialog(parent, "Submit Feedback", 500, 340);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Submit Feedback"), BorderLayout.NORTH);
        dialog.add(buildBody(dialog),    BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildBody(JDialog dialog) {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 28, 10, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0); g.anchor = GridBagConstraints.WEST;

        JTextField fApptId = UITheme.createTextField();
        fApptId.setPreferredSize(new Dimension(400, UITheme.INPUT_H));

        JTextArea fFeedback = new JTextArea(4, 30);
        fFeedback.setFont(UITheme.FONT_REGULAR); fFeedback.setLineWrap(true);
        fFeedback.setWrapStyleWord(true); fFeedback.setCaretColor(UITheme.ACCENT);
        JScrollPane feedbackScroll = new JScrollPane(fFeedback);
        feedbackScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);

        g.gridx=0; g.gridy=0; g.gridwidth=2;
        body.add(UITheme.createLabel("Appointment ID", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridy=1; body.add(fApptId, g);
        g.gridy=2;
        body.add(UITheme.createLabel("Your Feedback", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridy=3; body.add(feedbackScroll, g);
        g.gridy=4; body.add(status, g);

        JButton submit = UITheme.createPrimaryButton("Submit");
        JButton cancel = UITheme.createLinkButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        submit.addActionListener(e -> {
            String apptId = fApptId.getText().trim();
            String fb = fFeedback.getText().trim();
            if (apptId.isEmpty() || fb.isEmpty()) {
                status.setForeground(UITheme.ERROR);
                status.setText("Both fields are required."); return;
            }
            OperationResult r = service.submitFeedback(apptId, customer.getUserid(), fb);
            if (r.getResult()) {
                JOptionPane.showMessageDialog(parent, r.getMessage(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                status.setForeground(UITheme.ERROR); status.setText(r.getMessage());
            }
        });

        g.gridy=5; body.add(UITheme.buttonRow(cancel, submit), g);
        return body;
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Customer_PaymentHistoryDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_PaymentHistoryDialog {

    private final JFrame          parent;
    private final Customer        customer;
    private final CustomerService service = new CustomerService();

    public Customer_PaymentHistoryDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
        JDialog dialog = UITheme.createDialog(parent, "Payment History", 780, 500);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Payment History"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        dialog.add(UITheme.buttonRow(close), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        String[] cols = {"Payment ID", "Appointment ID", "Service Type", "Amount (RM)", "Date", "Method", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {100, 120, 110, 100, 100, 90, 80};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        for (Payment pay : service.getMyPayments(customer.getUserid()))
            model.addRow(new Object[]{pay.getPaymentId(), pay.getAppointmentId(),
                pay.getServiceType(), String.format("%.2f", pay.getAmount()),
                pay.getPaymentDate(), pay.getPaymentMethod(), pay.getStatus()});

        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Customer_EditProfileDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_EditProfileDialog {

    private final JFrame          parent;
    private final Customer        customer;
    private final CustomerService service = new CustomerService();

    private JTextField     fName, fAge, fEmail, fUsername, fContact;
    private JPasswordField fCurrent, fNew;
    private JLabel         statusLabel;

    public Customer_EditProfileDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
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

        fName     = UITheme.createTextField(); fName.setText(customer.getName());
        fAge      = UITheme.createTextField(); fAge.setText(String.valueOf(customer.getAge()));
        fEmail    = UITheme.createTextField(); fEmail.setText(customer.getEmail());
        fUsername = UITheme.createTextField(); fUsername.setText(customer.getUsername());
        fContact  = UITheme.createTextField(); fContact.setText(customer.getContact());
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
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { setStatus("Age must be a number.", false); return; }
            String np = new String(fNew.getPassword());
            if (np.isEmpty()) np = customer.getPassword();
            OperationResult r = service.updateProfile(customer,
                fName.getText().trim(), age, fEmail.getText().trim(),
                fUsername.getText().trim(), cur, np, fContact.getText().trim());
            setStatus(r.getMessage(), r.getResult());
            if (r.getResult()) { fCurrent.setText(""); fNew.setText(""); }
        });

        reset.addActionListener(e -> {
            fName.setText(customer.getName()); fAge.setText(String.valueOf(customer.getAge()));
            fEmail.setText(customer.getEmail()); fUsername.setText(customer.getUsername());
            fContact.setText(customer.getContact()); fCurrent.setText(""); fNew.setText("");
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
