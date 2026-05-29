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

    private final Customer        customer;
    private final CustomerService service = new CustomerService();

    public Customer_Frame(Customer customer) {
        this.customer = customer;
        setTitle("Customer Dashboard - APU-ASC");
        setSize(1100, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(new ImageIcon("APU_ASC_LOGO.png").getImage());

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(50, 20, 100));
        sidebar.setPreferredSize(new Dimension(220, 660));

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon rawLogo = new ImageIcon("APU_ASC_LOGO.png");
        int lw = 70, lh = (int)((double) rawLogo.getIconHeight()
            / rawLogo.getIconWidth() * lw);
        JLabel logoLbl = new JLabel(new ImageIcon(
            rawLogo.getImage().getScaledInstance(lw, lh, Image.SCALE_SMOOTH)));
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("APU-ASC");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel role = new JLabel("Customer Portal");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(200, 180, 255));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoLbl);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        logoPanel.add(appName);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        logoPanel.add(role);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(180, 1));

        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        profileRow.setOpaque(false);
        profileRow.setMaximumSize(new Dimension(220, 60));
        JLabel custImg = new JLabel(scaledIcon("customer_icon.png", 32, 32));
        JPanel custInfo = new JPanel();
        custInfo.setOpaque(false);
        custInfo.setLayout(new BoxLayout(custInfo, BoxLayout.Y_AXIS));
        JLabel custName = new JLabel(customer.getName());
        custName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        custName.setForeground(Color.WHITE);
        JLabel custId = new JLabel(customer.getUserid());
        custId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        custId.setForeground(new Color(200, 180, 255));
        custInfo.add(custName);
        custInfo.add(custId);
        profileRow.add(custImg);
        profileRow.add(custInfo);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 30));
        sep2.setMaximumSize(new Dimension(180, 1));

        JLabel menuLbl = new JLabel("  MENU");
        menuLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuLbl.setForeground(new Color(180, 160, 220));
        menuLbl.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 0));
        menuLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logoPanel);
        sidebar.add(sep);
        sidebar.add(profileRow);
        sidebar.add(sep2);
        sidebar.add(menuLbl);
        sidebar.add(navItem("my_appointments.png",
            "My Appointments",
            () -> new Customer_AppointmentsDialog(this, customer)));
        sidebar.add(navItem("submit_feedback.png",
            "Submit Feedback",
            () -> new Customer_FeedbackDialog(this, customer)));
        sidebar.add(navItem("payment_history.png",
            "Payment History",
            () -> new Customer_PaymentHistoryDialog(this, customer)));
        sidebar.add(navItem("cancel_appointment.png",
            "Cancel Appointment",
            () -> new Customer_CancelAppointmentDialog(this, customer)));
        sidebar.add(navItem("service_history.png",
            "Service History",
            () -> new Customer_ServiceHistoryDialog(this, customer)));
        sidebar.add(navItem("view_receipt.png",
            "View Receipt",
            () -> new Customer_ViewReceiptDialog(this, customer)));
        sidebar.add(navItem("edit_profile.png",
            "Edit My Profile",
            () -> new Customer_EditProfileDialog(this, customer)));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(navItem("Sign_out.png", "Sign Out", this::signOut));
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        return sidebar;
    }

    private JPanel navItem(String iconFile, String label, Runnable action) {
        final boolean[] hovered = {false};

        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10)) {
            @Override protected void paintComponent(Graphics g) {
                if (hovered[0]) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(220, 46));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(scaledIcon(iconFile, 20, 20));
        iconLbl.setOpaque(false);
        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(new Color(220, 200, 255));
        textLbl.setOpaque(false);

        item.add(iconLbl);
        item.add(textLbl);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered[0] = true;
                textLbl.setForeground(Color.WHITE);
                item.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered[0] = false;
                textLbl.setForeground(new Color(220, 200, 255));
                item.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return item;
    }

    // ── Main content ──────────────────────────────────────────────────────────

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(248, 245, 255));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 210, 240)),
            BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JLabel pageTitle = new JLabel("Customer Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(50, 20, 100));

        JLabel welcome = new JLabel("Welcome back, " + customer.getName());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(100, 80, 140));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(pageTitle);
        titlePanel.add(welcome);

        topBar.add(titlePanel, BorderLayout.WEST);
        main.add(topBar, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Stats
        body.add(sectionLabel("My Overview", new Color(50, 20, 100)));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildStatsRow());
        body.add(Box.createRigidArea(new Dimension(0, 28)));

        // Cards
        body.add(sectionLabel("Quick Actions", new Color(50, 20, 100)));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildCardsGrid());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        main.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 210, 240)),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        JLabel footerLbl = new JLabel("APU Automotive Service Centre  •  Customer Module");
        footerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLbl.setForeground(new Color(100, 80, 140));
        JLabel idLbl = new JLabel("ID: " + customer.getUserid());
        idLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        idLbl.setForeground(new Color(100, 80, 140));
        footer.add(footerLbl, BorderLayout.WEST);
        footer.add(idLbl,     BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);

        return main;
    }

    private JPanel buildStatsRow() {
        java.util.ArrayList<Appointment> myAppts =
            service.getMyAppointments(customer.getUserid());
        int total = myAppts.size();
        int completed = 0, pending = 0;
        for (Appointment a : myAppts) {
            if (a.getStatus().equals("Completed")) completed++;
            else if (a.getStatus().equals("Pending")) pending++;
        }
        int payments = service.getMyPayments(customer.getUserid()).size();

        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(statCard("Total Appointments", String.valueOf(total),
            "my_appointments.png",    new Color(100, 60, 200)));
        row.add(statCard("Completed",          String.valueOf(completed),
            "service_history.png",    new Color(34, 197, 94)));
        row.add(statCard("Pending",            String.valueOf(pending),
            "cancel_appointment.png", new Color(220, 100, 30)));
        row.add(statCard("Total Payments",     String.valueOf(payments),
            "payment_history.png",    new Color(50, 20, 100)));

        return row;
    }

    private JPanel statCard(String label, String value,
                             String iconFile, Color accent) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 210, 240)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16))));

        JLabel iconLbl = new JLabel(scaledIcon(iconFile, 36, 36));
        iconLbl.setVerticalAlignment(SwingConstants.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLbl.setForeground(accent);
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLbl.setForeground(new Color(100, 80, 140));
        info.add(valueLbl);
        info.add(labelLbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(info,    BorderLayout.CENTER);
        return card;
    }

    private JPanel buildCardsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(actionCard("my_appointments_card.png",
            "My Appointments",
            "View all your service\nappointments.",
            new Color(100, 60, 200),
            () -> new Customer_AppointmentsDialog(this, customer)));

        grid.add(actionCard("submit_feedback_card.png",
            "Submit Feedback",
            "Leave feedback on a\ncompleted appointment.",
            new Color(34, 197, 94),
            () -> new Customer_FeedbackDialog(this, customer)));

        grid.add(actionCard("payment_history_card.png",
            "Payment History",
            "View all your past\npayment records.",
            new Color(50, 20, 100),
            () -> new Customer_PaymentHistoryDialog(this, customer)));

        grid.add(actionCard("cancel_appointment_card.png",
            "Cancel Appointment",
            "Cancel your pending\nappointments.",
            new Color(220, 50, 50),
            () -> new Customer_CancelAppointmentDialog(this, customer)));

        grid.add(actionCard("service_history_card.png",
            "Service History",
            "View detailed completed\nservice records.",
            new Color(16, 150, 130),
            () -> new Customer_ServiceHistoryDialog(this, customer)));

        grid.add(actionCard("view_receipt_card.png",
            "View Receipt",
            "View receipt for a\nspecific payment.",
            new Color(180, 100, 20),
            () -> new Customer_ViewReceiptDialog(this, customer)));

        return grid;
    }

    private JPanel actionCard(String iconFile, String title,
                               String desc, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 210, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20))));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(scaledIcon(iconFile, 48, 48));
        iconLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(new Color(50, 20, 100));

        JLabel descLbl = new JLabel(
            "<html><div style='width:150px;color:#6b5090;font-size:11px;'>"
            + desc.replace("\n", "<br>") + "</div></html>");

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLbl);
        text.add(Box.createRigidArea(new Dimension(0, 6)));
        text.add(descLbl);

        card.add(iconLbl, BorderLayout.NORTH);
        card.add(text,    BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 245, 255));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }

    private ImageIcon scaledIcon(String file, int w, int h) {
        try {
            ImageIcon raw = new ImageIcon(file);
            return new ImageIcon(raw.getImage()
                .getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return new ImageIcon(); }
    }

    private JLabel sectionLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 210, 240)),
            BorderFactory.createEmptyBorder(0, 0, 6, 0)));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return l;
    }

    private void signOut() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to sign out?",
            "Sign Out", JOptionPane.YES_NO_OPTION);
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
        JDialog dialog = UITheme.createDialog(parent, "Submit Feedback", 500, 420);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Submit Feedback"), BorderLayout.NORTH);
        dialog.add(buildBody(),    BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 28, 10, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0);
        g.anchor = GridBagConstraints.WEST;

        JTextField fApptId = UITheme.createTextField();
        fApptId.setPreferredSize(new Dimension(400, UITheme.INPUT_H));

        JTextArea fFeedback = new JTextArea(5, 30);
        fFeedback.setFont(UITheme.FONT_REGULAR);
        fFeedback.setLineWrap(true);
        fFeedback.setWrapStyleWord(true);
        fFeedback.setCaretColor(UITheme.ACCENT);
        JScrollPane feedbackScroll = new JScrollPane(fFeedback);
        feedbackScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        feedbackScroll.setPreferredSize(new Dimension(400, 100));

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);

        g.gridx=0; g.gridy=0; g.gridwidth=2;
        body.add(UITheme.createLabel("Appointment ID",
            UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridy=1; body.add(fApptId, g);
        g.gridy=2;
        body.add(UITheme.createLabel("Your Feedback",
            UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridy=3; body.add(feedbackScroll, g);
        g.gridy=4; body.add(status, g);

        // Store fields for use in button panel
        body.putClientProperty("fApptId", fApptId);
        body.putClientProperty("fFeedback", fFeedback);
        body.putClientProperty("status", status);

        return body;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton submit = UITheme.createPrimaryButton("Submit");
        JButton cancel = UITheme.createLinkButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        submit.addActionListener(e -> {
            // Get the body panel to access fields
            JPanel body = (JPanel) ((JPanel) dialog.getContentPane()
                .getComponent(1));
            JTextField fApptId = (JTextField) body.getClientProperty("fApptId");
            JTextArea fFeedback = (JTextArea) body.getClientProperty("fFeedback");
            JLabel status = (JLabel) body.getClientProperty("status");

            String apptId = fApptId.getText().trim();
            String fb     = fFeedback.getText().trim();

            String error = Validator.validateAll(
                Validator.validateAppointmentId(apptId),
                Validator.validateFeedback(fb)
            );
            if (error != null) {
                status.setForeground(UITheme.ERROR);
                status.setText(error);
                return;
            }
            OperationResult r = service.submitFeedback(
                apptId, customer.getUserid(), fb);
            if (r.getResult()) {
                JOptionPane.showMessageDialog(parent, r.getMessage(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                status.setForeground(UITheme.ERROR);
                status.setText(r.getMessage());
            }
        });
        return UITheme.buttonRow(cancel, submit);
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

    String np = new String(fNew.getPassword());
    if (np.isEmpty()) np = customer.getPassword();

    String error = Validator.validateAll(
        Validator.validateName(fName.getText()),
        Validator.validateAge(fAge.getText()),
        Validator.validateEmail(fEmail.getText()),
        Validator.validateUsername(fUsername.getText()),
        Validator.validateContact(fContact.getText()),
        np.equals(customer.getPassword()) ? null : Validator.validatePassword(np)
    );
    if (error != null) { setStatus(error, false); return; }

    int age = Integer.parseInt(fAge.getText().trim());
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
// ══════════════════════════════════════════════════════════════════════════════
//  Customer_CancelAppointmentDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_CancelAppointmentDialog {

    private final JFrame          parent;
    private final Customer        customer;
    private final CustomerService service = new CustomerService();
    private DefaultTableModel     model;
    private JTable                table;

    public Customer_CancelAppointmentDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
        JDialog dialog = UITheme.createDialog(parent, "Cancel Appointment", 820, 500);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Cancel Appointment"), BorderLayout.NORTH);
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
            "Only Pending appointments can be cancelled. Select one and click Cancel.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String[] cols = {"Appointment ID", "Date", "Time",
                         "Service Type", "Price (RM)", "Vehicle", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {120, 90, 70, 110, 90, 160, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status colour renderer
        table.getColumnModel().getColumn(6).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f,
                        int row, int col) {
                    super.getTableCellRendererComponent(t,v,sel,f,row,col);
                    setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
                    if (!sel) {
                        setBackground(row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                        String s = v == null ? "" : v.toString();
                        setForeground(switch (s) {
                            case "Completed"  -> UITheme.SUCCESS;
                            case "Pending"    -> UITheme.WARNING;
                            case "Cancelled"  -> UITheme.ERROR;
                            default           -> UITheme.TEXT_PRIMARY;
                        });
                    } else {
                        setBackground(UITheme.TABLE_SELECTED);
                        setForeground(UITheme.TEXT_PRIMARY);
                    }
                    return this;
                }
            });

        p.add(hint,                            BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton cancelBtn = UITheme.createDangerButton("Cancel Appointment");
        JButton closeBtn  = UITheme.createLinkButton("Close");

        cancelBtn.setPreferredSize(new Dimension(180, UITheme.BUTTON_H));
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(parent,
                    "Select an appointment to cancel.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String apptId = (String) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to cancel appointment " + apptId + "?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                OperationResult r = service.cancelAppointment(
                    apptId, customer.getUserid());
                JOptionPane.showMessageDialog(parent, r.getMessage(),
                    r.getResult() ? "Success" : "Error",
                    r.getResult() ? JOptionPane.INFORMATION_MESSAGE
                                  : JOptionPane.ERROR_MESSAGE);
                if (r.getResult()) loadData();
            }
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(cancelBtn, closeBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Appointment a : service.getMyAppointments(customer.getUserid())) {
            if (a.getStatus().equals("Completed")) continue;
            model.addRow(new Object[]{
                a.getAppointmentid(), a.getDate(), a.getTime(),
                a.getServicetype(),
                String.format("%.2f", a.getPrice()),
                a.getVehicleDetails(), a.getStatus()
            });
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Customer_ServiceHistoryDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_ServiceHistoryDialog {

    private final JFrame          parent;
    private final Customer        customer;
    private final CustomerService service = new CustomerService();

    public Customer_ServiceHistoryDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
        JDialog dialog = UITheme.createDialog(parent, "Service History", 860, 540);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Service History"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        java.util.ArrayList<Appointment> history =
            service.getServiceHistory(customer.getUserid());

        JLabel countLbl = UITheme.createLabel(
            "Total completed services: " + history.size(),
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        countLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String[] cols = {"Appointment ID", "Date", "Time",
                         "Service Type", "Price (RM)",
                         "Vehicle", "Technician ID", "Comments"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowHeight(44);

        int[] widths = {110, 90, 70, 110, 90, 130, 110, 180};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Comments wrap renderer
        table.getColumnModel().getColumn(7).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f,
                        int row, int col) {
                    JTextArea area = new JTextArea(
                        v == null ? "—" : v.toString());
                    area.setFont(UITheme.FONT_REGULAR);
                    area.setWrapStyleWord(true);
                    area.setLineWrap(true);
                    area.setBorder(BorderFactory.createEmptyBorder(4,14,4,14));
                    area.setBackground(sel ? UITheme.TABLE_SELECTED
                        : row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    return area;
                }
            });

        for (Appointment a : history)
            model.addRow(new Object[]{
                a.getAppointmentid(), a.getDate(), a.getTime(),
                a.getServicetype(),
                String.format("%.2f", a.getPrice()),
                a.getVehicleDetails(), a.getTechnicianid(),
                a.getComments() == null ? "—" : a.getComments()
            });

        p.add(countLbl,                        BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(close);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Customer_ViewReceiptDialog
// ══════════════════════════════════════════════════════════════════════════════

class Customer_ViewReceiptDialog {

    private final JFrame          parent;
    private final Customer        customer;
    private final CustomerService service = new CustomerService();

    public Customer_ViewReceiptDialog(JFrame parent, Customer customer) {
        this.parent = parent; this.customer = customer;
        JDialog dialog = UITheme.createDialog(parent, "View Receipt", 620, 540);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("View Receipt"), BorderLayout.NORTH);
        dialog.add(buildBody(),          BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel buildBody() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UITheme.WHITE);
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel lookupRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        lookupRow.setOpaque(false);
        lookupRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JTextField fPaymentId = UITheme.createTextField();
        fPaymentId.setPreferredSize(new Dimension(200, UITheme.INPUT_H));
        UITheme.placeholder(fPaymentId, "Enter Payment ID...");

        JButton loadBtn = UITheme.createPrimaryButton("Load Receipt");
        loadBtn.setPreferredSize(new Dimension(140, UITheme.INPUT_H));

        lookupRow.add(UITheme.createLabel("Payment ID:",
            UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        lookupRow.add(fPaymentId);
        lookupRow.add(loadBtn);

        JLabel statusLabel = UITheme.createLabel(" ",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));

        JTextArea receiptArea = new JTextArea();
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        receiptArea.setForeground(new Color(20, 30, 60));
        receiptArea.setBackground(new Color(248, 249, 252));
        receiptArea.setEditable(false);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        receiptArea.setText("Enter a Payment ID and click Load Receipt...");

        JScrollPane scroll = new JScrollPane(receiptArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

       loadBtn.addActionListener(e -> {
    String pid = fPaymentId.getText().trim();
    String error = Validator.validatePaymentId(pid);
    if (error != null) {
        statusLabel.setForeground(UITheme.ERROR);
        statusLabel.setText(error);
        return;
    }
    String receipt = service.generateCustomerReceipt(
        pid, customer.getUserid());
    if (receipt.startsWith("Error:")) {
        statusLabel.setForeground(UITheme.ERROR);
        statusLabel.setText(receipt);
        receiptArea.setText("");
    } else {
        statusLabel.setForeground(UITheme.SUCCESS);
        statusLabel.setText("Receipt loaded successfully.");
        receiptArea.setText(receipt);
        receiptArea.setCaretPosition(0);
    }
});

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(lookupRow);
        topSection.add(statusLabel);

        outer.add(topSection, BorderLayout.NORTH);
        outer.add(scroll,     BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(close);
    }
}