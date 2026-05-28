package apu_asc;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.RenderingHints;

public class Manager_Frame extends JFrame {

    private final Manager manager;
    private final ManagerService service = new ManagerService();
    private JPanel contentPanel;

    public Manager_Frame(Manager manager) {
        this.manager = manager;
        setTitle("Manager Dashboard - APU-ASC");
        setSize(1100, 760);
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
        sidebar.setBackground(new Color(15, 30, 70));
        sidebar.setPreferredSize(new Dimension(220, 660));

        // Logo + app name
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon rawLogo = new ImageIcon("APU_ASC_LOGO.png");
        int lw = 70, lh = (int)((double) rawLogo.getIconHeight() / rawLogo.getIconWidth() * lw);
        JLabel logoLbl = new JLabel(new ImageIcon(rawLogo.getImage().getScaledInstance(lw, lh, Image.SCALE_SMOOTH)));
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("APU-ASC");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel role = new JLabel("Manager Portal");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(160, 185, 220));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoLbl);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        logoPanel.add(appName);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        logoPanel.add(role);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(180, 1));

        // Manager profile row
        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        profileRow.setOpaque(false);
        profileRow.setMaximumSize(new Dimension(220, 60));
        ImageIcon mgrIcon = scaledIcon("Manager_icon.png", 32, 32);
        JLabel mgrImg = new JLabel(mgrIcon);
        JPanel mgrInfo = new JPanel();
        mgrInfo.setOpaque(false);
        mgrInfo.setLayout(new BoxLayout(mgrInfo, BoxLayout.Y_AXIS));
        JLabel mgrName = new JLabel(manager.getName());
        mgrName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mgrName.setForeground(Color.WHITE);
        JLabel mgrId = new JLabel(manager.getUserid());
        mgrId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        mgrId.setForeground(new Color(160, 185, 220));
        mgrInfo.add(mgrName);
        mgrInfo.add(mgrId);
        profileRow.add(mgrImg);
        profileRow.add(mgrInfo);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 30));
        sep2.setMaximumSize(new Dimension(180, 1));

        // Menu label
        JLabel menuLbl = new JLabel("  MENU");
        menuLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuLbl.setForeground(new Color(120, 150, 190));
        menuLbl.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 0));
        menuLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nav items
        sidebar.add(logoPanel);
        sidebar.add(sep);
        sidebar.add(profileRow);
        sidebar.add(sep2);
        sidebar.add(menuLbl);
        sidebar.add(navItem("manage_staff.png",          "Manage Staff",            () -> new Manager_ManageStaffDialog(this)));
        sidebar.add(navItem("set_price.png",             "Set Service Prices",      () -> new Manager_SetPricesDialog(this)));
        sidebar.add(navItem("view_feedback.png",         "View Feedbacks",          () -> new Manager_ViewFeedbacksDialog(this)));
        sidebar.add(navItem("view_report.png",           "View Reports",            () -> new Manager_ViewReportsDialog(this)));
        sidebar.add(navItem("all_appointments.png",      "All Appointments",        () -> new Manager_ViewAllAppointmentsDialog(this)));
        sidebar.add(navItem("technician_performance.png","Technician Performance",  () -> new Manager_TechnicianPerformanceDialog(this)));
        sidebar.add(navItem("view_payments.png",         "View All Payments",       () -> new Manager_ViewAllPaymentsDialog(this)));
        sidebar.add(navItem("statistics.png",            "System Statistics",       () -> new Manager_StatisticsDialog(this)));
        sidebar.add(Box.createVerticalGlue());



        // Sign out at bottom
        sidebar.add(navItem("Sign_out.png", "Sign Out", this::signOut));
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        return sidebar;
    }

private JPanel navItem(String iconFile, String label, Runnable action) {
    final boolean[] hovered = {false};

    JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10)) {
        @Override
        protected void paintComponent(Graphics g) {
            if (hovered[0]) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 8, 8);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    };

    item.setOpaque(false);
    item.setMaximumSize(new Dimension(220, 46));
    item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    ImageIcon icon = scaledIcon(iconFile, 20, 20);
    JLabel iconLbl = new JLabel(icon);
    iconLbl.setOpaque(false);

    JLabel textLbl = new JLabel(label);
    textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    textLbl.setForeground(new Color(200, 215, 240));
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
            textLbl.setForeground(new Color(200, 215, 240));
            item.repaint();
        }
        @Override public void mouseClicked(MouseEvent e) { action.run(); }
    });

    return item;
}

    // ── Main content ──────────────────────────────────────────────────────────

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 252));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JLabel pageTitle = new JLabel("Manager Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(20, 30, 60));

        JLabel welcome = new JLabel("Welcome back, " + manager.getName());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(100, 110, 130));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(pageTitle);
        titlePanel.add(welcome);

        topBar.add(titlePanel, BorderLayout.WEST);
        main.add(topBar, BorderLayout.NORTH);

        // Scrollable body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Stats row
        body.add(sectionLabel("Overview"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildStatsRow());
        body.add(Box.createRigidArea(new Dimension(0, 28)));

        // Quick actions
        body.add(sectionLabel("Quick Actions"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildCardsRow());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        main.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        JLabel footerLbl = new JLabel("APU Automotive Service Centre  •  Manager Module");
        footerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLbl.setForeground(new Color(140, 150, 170));
        JLabel idLbl = new JLabel("ID: " + manager.getUserid());
        idLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        idLbl.setForeground(new Color(140, 150, 170));
        footer.add(footerLbl, BorderLayout.WEST);
        footer.add(idLbl,     BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);

        return main;
    }

    // ── Stats row ─────────────────────────────────────────────────────────────

    private JPanel buildStatsRow() {
        int[]    userCounts = service.getUserCounts();
        int[]    apptStats  = service.getAppointmentStats();
        double[] revenue    = service.getRevenueStats();

        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(statCard("Total Staff",        String.valueOf(userCounts[1] + userCounts[2]),
            "Total_staff.png",       new Color(37, 82, 148)));
        row.add(statCard("Total Appointments", String.valueOf(apptStats[0]),
            "total_view_appointment.png", new Color(16, 150, 100)));
        row.add(statCard("Total Revenue",      "RM " + String.format("%.2f", revenue[0]),
            "Total_Revenue_icon.png", new Color(139, 92, 246)));
        row.add(statCard("Pending",            String.valueOf(apptStats[2]),
            "pending_appointment.png", new Color(220, 100, 30)));

        return row;
    }

    private JPanel statCard(String label, String value, String iconFile, Color accent) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16))));

        ImageIcon icon = scaledIcon(iconFile, 36, 36);
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setVerticalAlignment(SwingConstants.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLbl.setForeground(accent);
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLbl.setForeground(new Color(100, 110, 130));
        info.add(valueLbl);
        info.add(labelLbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(info,    BorderLayout.CENTER);
        return card;
    }

    // ── Quick action cards ────────────────────────────────────────────────────

    private JPanel buildCardsRow() {
       JPanel grid = new JPanel(new GridLayout(3, 3, 16, 16));;
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(actionCard("Manage_staff_card.png",          "Manage Staff",
            "Add, view, update and delete\nstaff accounts.",
            new Color(37, 82, 148),   () -> new Manager_ManageStaffDialog(this)));

        grid.add(actionCard("set_price_card.png",             "Set Service Prices",
            "Configure Normal and\nMajor service prices.",
            new Color(16, 150, 100),  () -> new Manager_SetPricesDialog(this)));

        grid.add(actionCard("view_feedback_card.png",         "View Feedbacks",
            "Review all customer\nappointment comments.",
            new Color(139, 92, 246),  () -> new Manager_ViewFeedbacksDialog(this)));

        grid.add(actionCard("view_repord_card.png",           "View Reports",
            "See appointment stats\nand revenue summary.",
            new Color(220, 100, 30),  () -> new Manager_ViewReportsDialog(this)));

        grid.add(actionCard("all_appointments_card.png",      "All Appointments",
            "View and filter all\nappointments in the system.",
            new Color(20, 160, 180),  () -> new Manager_ViewAllAppointmentsDialog(this)));

        grid.add(actionCard("technician_performance_card.png","Technician Performance",
            "View technician stats\nand appointment breakdown.",
            new Color(180, 50, 100),  () -> new Manager_TechnicianPerformanceDialog(this)));
        grid.add(actionCard("view_payments_card.png",
            "View All Payments",
    "See all payments and\nrevenue breakdown.",
    new Color(16, 120, 80),() -> new Manager_ViewAllPaymentsDialog(this)));

       grid.add(actionCard("statistics_card.png",
    "System Statistics",
    "Charts and trends for\nappointments and revenue.",
    new Color(139, 60, 200),
    () -> new Manager_StatisticsDialog(this)));

// filler
JPanel filler = new JPanel(); filler.setOpaque(false);
grid.add(filler);

        return grid;
    }

    private JPanel actionCard(String iconFile, String title, String desc,
                               Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20))));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon
        ImageIcon icon = scaledIcon(iconFile, 48, 48);
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Title
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(new Color(20, 30, 60));

        // Description
        JLabel descLbl = new JLabel(
            "<html><div style='width:160px;color:#6b7280;font-size:11px;'>"
            + desc.replace("\n", "<br>") + "</div></html>");

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLbl);
        text.add(Box.createRigidArea(new Dimension(0, 6)));
        text.add(descLbl);

        card.add(iconLbl, BorderLayout.NORTH);
        card.add(text,    BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 250, 255));
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
            return new ImageIcon(raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(100, 110, 130));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(0, 0, 6, 0)));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return l;
    }

    private void signOut() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose(); new Login_Frame(); }
    }
}