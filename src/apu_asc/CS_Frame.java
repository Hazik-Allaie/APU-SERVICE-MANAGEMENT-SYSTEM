package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CS_Frame extends JFrame {

    private final CounterStaff       user;
    private final CounterStaffService service = new CounterStaffService();

    public CS_Frame(CounterStaff user) {
        this.user = user;
        setTitle("Counter Staff Dashboard - APU-ASC");
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
        sidebar.setBackground(new Color(8, 60, 70));
        sidebar.setPreferredSize(new Dimension(220, 660));

        // Logo
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

        JLabel role = new JLabel("Counter Staff Portal");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(160, 220, 220));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoLbl);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        logoPanel.add(appName);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        logoPanel.add(role);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(180, 1));

        // Profile row
        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        profileRow.setOpaque(false);
        profileRow.setMaximumSize(new Dimension(220, 60));
        JLabel csImg = new JLabel(scaledIcon("cs_icon.png", 32, 32));
        JPanel csInfo = new JPanel();
        csInfo.setOpaque(false);
        csInfo.setLayout(new BoxLayout(csInfo, BoxLayout.Y_AXIS));
        JLabel csName = new JLabel(user.getName());
        csName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        csName.setForeground(Color.WHITE);
        JLabel csId = new JLabel(user.getUserid());
        csId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        csId.setForeground(new Color(160, 220, 220));
        csInfo.add(csName);
        csInfo.add(csId);
        profileRow.add(csImg);
        profileRow.add(csInfo);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 30));
        sep2.setMaximumSize(new Dimension(180, 1));

        JLabel menuLbl = new JLabel("  MENU");
        menuLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuLbl.setForeground(new Color(120, 200, 200));
        menuLbl.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 0));
        menuLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logoPanel);
        sidebar.add(sep);
        sidebar.add(profileRow);
        sidebar.add(sep2);
        sidebar.add(menuLbl);
        sidebar.add(navItem("manage_customers.png",    "Manage Customers",
            () -> new CS_ManageCustomersDialog(this)));
        sidebar.add(navItem("manage_appointments.png", "Manage Appointments",
            () -> new CS_ManageAppointmentsDialog(this, user)));
        sidebar.add(navItem("collect_payment.png",     "Collect Payment",
            () -> new CS_CollectPaymentDialog(this)));
        sidebar.add(navItem("generate_receipt.png",    "Generate Receipt",
            () -> new CS_GenerateReceiptDialog(this)));
        sidebar.add(navItem("search_appointment.png",  "Search Appointments",
            () -> new CS_SearchAppointmentsDialog(this)));
        sidebar.add(navItem("daily_summary.png",       "Daily Summary",
            () -> new CS_DailySummaryDialog(this)));
        sidebar.add(navItem("edit_profile.png",        "Edit My Profile",
            () -> new CS_EditProfileDialog(this, user)));
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
        textLbl.setForeground(new Color(200, 235, 235));
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
                textLbl.setForeground(new Color(200, 235, 235));
                item.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return item;
    }

    // ── Main content ──────────────────────────────────────────────────────────

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(240, 250, 252));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 230, 230)),
            BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JLabel pageTitle = new JLabel("Counter Staff Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(8, 60, 70));

        JLabel welcome = new JLabel("Welcome back, " + user.getName());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(80, 120, 120));

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

        // Stats row
        body.add(sectionLabel("Overview", new Color(8, 60, 70)));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildStatsRow());
        body.add(Box.createRigidArea(new Dimension(0, 28)));

        // Cards
        body.add(sectionLabel("Quick Actions", new Color(8, 60, 70)));
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
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 230, 230)),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        JLabel footerLbl = new JLabel("APU Automotive Service Centre  •  Counter Staff Module");
        footerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLbl.setForeground(new Color(100, 140, 140));
        JLabel idLbl = new JLabel("ID: " + user.getUserid());
        idLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        idLbl.setForeground(new Color(100, 140, 140));
        footer.add(footerLbl, BorderLayout.WEST);
        footer.add(idLbl,     BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);

        return main;
    }

    // ── Stats row ─────────────────────────────────────────────────────────────

    private JPanel buildStatsRow() {
        int totalCustomers    = service.getAllCustomers().size();
        int totalAppointments = service.getAllAppointmentsForCS().size();
        int pendingPayments   = service.getPendingPaymentsCount();
        int todayAppointments = service.getTodayAppointmentsCount();

        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(statCard("Total Customers",
            String.valueOf(totalCustomers),
            "manage_customers.png", new Color(8, 150, 160)));
        row.add(statCard("Total Appointments",
            String.valueOf(totalAppointments),
            "manage_appointments.png", new Color(16, 100, 120)));
        row.add(statCard("Today's Appointments",
            String.valueOf(todayAppointments),
            "search_appointment.png", new Color(0, 130, 100)));
        row.add(statCard("Pending Payments",
            String.valueOf(pendingPayments),
            "collect_payment.png", new Color(180, 100, 20)));

        return row;
    }

    private JPanel statCard(String label, String value,
                             String iconFile, Color accent) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 230, 230)),
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
        labelLbl.setForeground(new Color(80, 120, 120));
        info.add(valueLbl);
        info.add(labelLbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(info,    BorderLayout.CENTER);
        return card;
    }

    // ── Cards grid ────────────────────────────────────────────────────────────

    private JPanel buildCardsGrid() {
        JPanel grid = new JPanel(new GridLayout(3, 3, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(actionCard("manage_customers_card.png",    "Manage Customers",
            "Add, search, update\nand delete customers.",
            new Color(8, 150, 160),
            () -> new CS_ManageCustomersDialog(this)));

        grid.add(actionCard("manage_appointments_card.png", "Manage Appointments",
            "Create new service\nappointments for customers.",
            new Color(16, 100, 120),
            () -> new CS_ManageAppointmentsDialog(this, user)));

        grid.add(actionCard("collect_payment_card.png",     "Collect Payment",
            "Process payment for\na completed appointment.",
            new Color(0, 130, 100),
            () -> new CS_CollectPaymentDialog(this)));

        grid.add(actionCard("generate_receipt_card.png",    "Generate Receipt",
            "Display and save a\nreceipt for any payment.",
            new Color(20, 80, 160),
            () -> new CS_GenerateReceiptDialog(this)));

        grid.add(actionCard("search_appointment_card.png",  "Search Appointments",
            "Search by date or\ncustomer ID.",
            new Color(100, 60, 180),
            () -> new CS_SearchAppointmentsDialog(this)));

        grid.add(actionCard("daily_summary_card.png",       "Daily Summary",
            "Export today's appointment\nsummary to a file.",
            new Color(160, 80, 20),
            () -> new CS_DailySummaryDialog(this)));

        grid.add(actionCard("edit_profile_card.png",        "Edit My Profile",
            "Update your personal\ninformation and password.",
            new Color(120, 40, 100),
            () -> new CS_EditProfileDialog(this, user)));

        // Empty fillers
        JPanel f1 = new JPanel(); f1.setOpaque(false); grid.add(f1);
        JPanel f2 = new JPanel(); f2.setOpaque(false); grid.add(f2);

        return grid;
    }

    private JPanel actionCard(String iconFile, String title,
                               String desc, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 230, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20))));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(scaledIcon(iconFile, 48, 48));
        iconLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(new Color(8, 60, 70));

        JLabel descLbl = new JLabel(
            "<html><div style='width:150px;color:#4a8080;font-size:11px;'>"
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
                card.setBackground(new Color(240, 252, 252));
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

    // ── Helpers ───────────────────────────────────────────────────────────────

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
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 230, 230)),
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