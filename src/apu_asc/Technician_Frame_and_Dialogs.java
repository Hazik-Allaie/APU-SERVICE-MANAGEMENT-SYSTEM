package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_Frame
// ══════════════════════════════════════════════════════════════════════════════

class Technician_Frame extends JFrame {

    private final Technician        technician;
    private final TechnicianService service = new TechnicianService();

    public Technician_Frame(Technician technician) {
        this.technician = technician;
        setTitle("Technician Dashboard - APU-ASC");
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

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(80, 40, 10));
        sidebar.setPreferredSize(new Dimension(220, 760));

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

        JLabel role = new JLabel("Technician Portal");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(255, 200, 150));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoLbl);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        logoPanel.add(appName);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        logoPanel.add(role);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(180, 1));

        int[] stats = service.getMyStats(technician.getUserid());

        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        profileRow.setOpaque(false);
        profileRow.setMaximumSize(new Dimension(220, 60));
        JLabel techImg = new JLabel(scaledIcon("technician_icon.png", 32, 32));
        JPanel techInfo = new JPanel();
        techInfo.setOpaque(false);
        techInfo.setLayout(new BoxLayout(techInfo, BoxLayout.Y_AXIS));
        JLabel techName = new JLabel(technician.getName());
        techName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        techName.setForeground(Color.WHITE);
        JLabel techId = new JLabel(technician.getUserid());
        techId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        techId.setForeground(new Color(255, 200, 150));
        techInfo.add(techName);
        techInfo.add(techId);
        profileRow.add(techImg);
        profileRow.add(techInfo);

        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        statsPanel.setMaximumSize(new Dimension(220, 120));

        JPanel miniStats = new JPanel(new GridLayout(1, 3, 6, 0));
        miniStats.setOpaque(false);
        miniStats.setMaximumSize(new Dimension(200, 50));
        miniStats.add(miniStatCard(String.valueOf(stats[0]), "Total",
            new Color(255, 180, 80)));
        miniStats.add(miniStatCard(String.valueOf(stats[1]), "Done",
            new Color(100, 220, 120)));
        miniStats.add(miniStatCard(String.valueOf(stats[2]), "Pending",
            new Color(255, 120, 80)));
        statsPanel.add(miniStats);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 30));
        sep2.setMaximumSize(new Dimension(180, 1));

        JLabel menuLbl = new JLabel("  MENU");
        menuLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuLbl.setForeground(new Color(200, 160, 100));
        menuLbl.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 0));
        menuLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logoPanel);
        sidebar.add(sep);
        sidebar.add(profileRow);
        sidebar.add(statsPanel);
        sidebar.add(sep2);
        sidebar.add(menuLbl);
        sidebar.add(navItem("my_appointments.png",
            "My Appointments",
            () -> new Technician_AppointmentsDialog(this, technician)));
        sidebar.add(navItem("update_status.png",
            "Update Job Status",
            () -> new Technician_UpdateStatusDialog(this, technician)));
        sidebar.add(navItem("submit_feedback.png",
            "Submit Feedback",
            () -> new Technician_FeedbackDialog(this, technician)));
        sidebar.add(navItem("job_details.png",
            "View Job Details",
            () -> new Technician_JobDetailsDialog(this, technician)));
        sidebar.add(navItem("my_schedule.png",
            "My Schedule",
            () -> new Technician_ScheduleDialog(this, technician)));
        sidebar.add(navItem("job_history.png",
            "Job History",
            () -> new Technician_JobHistoryDialog(this, technician)));
        sidebar.add(navItem("edit_profile.png",
            "Edit My Profile",
            () -> new Technician_EditProfileDialog(this, technician)));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(navItem("Sign_out.png", "Sign Out", this::signOut));
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        return sidebar;
    }

    private JPanel miniStatCard(String value, String label, Color color) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,30)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valLbl.setForeground(color);
        valLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblLbl.setForeground(new Color(200, 170, 130));
        lblLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(valLbl);
        card.add(lblLbl);
        return card;
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
        textLbl.setForeground(new Color(255, 220, 170));
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
                textLbl.setForeground(new Color(255, 220, 170));
                item.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return item;
    }

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(255, 248, 240));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 220, 200)),
            BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JLabel pageTitle = new JLabel("Technician Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(80, 40, 10));

        JLabel welcome = new JLabel("Welcome back, " + technician.getName());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(140, 90, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(pageTitle);
        titlePanel.add(welcome);

        topBar.add(titlePanel, BorderLayout.WEST);
        main.add(topBar, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        body.add(sectionLabel("My Performance", new Color(80, 40, 10)));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildStatsRow());
        body.add(Box.createRigidArea(new Dimension(0, 28)));
        body.add(sectionLabel("Quick Actions", new Color(80, 40, 10)));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildCardsGrid());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        main.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 220, 200)),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        JLabel footerLbl = new JLabel(
            "APU Automotive Service Centre  •  Technician Module");
        footerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLbl.setForeground(new Color(140, 90, 40));
        JLabel idLbl = new JLabel("ID: " + technician.getUserid());
        idLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        idLbl.setForeground(new Color(140, 90, 40));
        footer.add(footerLbl, BorderLayout.WEST);
        footer.add(idLbl,     BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);

        return main;
    }

    private JPanel buildStatsRow() {
        int[] stats    = service.getMyStats(technician.getUserid());
        double revenue = service.getTechnicianRevenue(technician.getUserid());

        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(statCard("Total Jobs",  String.valueOf(stats[0]),
            "my_appointments.png",  new Color(180, 100, 20)));
        row.add(statCard("Completed",   String.valueOf(stats[1]),
            "update_status.png",    new Color(34, 197, 94)));
        row.add(statCard("Pending",     String.valueOf(stats[2]),
            "job_details.png",      new Color(220, 80, 30)));
        row.add(statCard("Revenue",
            "RM " + String.format("%.2f", revenue),
            "generate_receipt.png", new Color(80, 40, 10)));

        return row;
    }

    private JPanel statCard(String label, String value,
                             String iconFile, Color accent) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 220, 200)),
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
        labelLbl.setForeground(new Color(140, 90, 40));
        info.add(valueLbl);
        info.add(labelLbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(info,    BorderLayout.CENTER);
        return card;
    }

    private JPanel buildCardsGrid() {
        JPanel grid = new JPanel(new GridLayout(3, 3, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(actionCard("my_appointments_card.png",
            "My Appointments",
            "View all appointments\nassigned to you.",
            new Color(180, 100, 20),
            () -> new Technician_AppointmentsDialog(this, technician)));

        grid.add(actionCard("update_status_card.png",
            "Update Job Status",
            "Mark appointments as\nIn Progress or Completed.",
            new Color(34, 150, 80),
            () -> new Technician_UpdateStatusDialog(this, technician)));

        grid.add(actionCard("submit_feedback_card.png",
            "Submit Feedback",
            "Add notes on a\ncompleted job.",
            new Color(100, 60, 180),
            () -> new Technician_FeedbackDialog(this, technician)));

        grid.add(actionCard("job_details_card.png",
            "View Job Details",
            "View detailed info\nfor a specific job.",
            new Color(20, 120, 160),
            () -> new Technician_JobDetailsDialog(this, technician)));

        grid.add(actionCard("my_schedule_card.png",
            "My Schedule",
            "View your weekly\nappointment schedule.",
            new Color(160, 40, 120),
            () -> new Technician_ScheduleDialog(this, technician)));

        grid.add(actionCard("job_history_card.png",
            "Job History",
            "View all completed\njobs and earnings.",
            new Color(40, 120, 80),
            () -> new Technician_JobHistoryDialog(this, technician)));

        grid.add(actionCard("edit_profile_card.png",
            "Edit My Profile",
            "Update your personal\ninformation and password.",
            new Color(120, 40, 100),
            () -> new Technician_EditProfileDialog(this, technician)));

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
                BorderFactory.createLineBorder(new Color(240, 220, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20))));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(scaledIcon(iconFile, 48, 48));
        iconLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(new Color(80, 40, 10));

        JLabel descLbl = new JLabel(
            "<html><div style='width:150px;color:#8a5a28;font-size:11px;'>"
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
                card.setBackground(new Color(255, 250, 240));
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
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 220, 200)),
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

        String[] cols = {"Appointment ID", "Customer ID", "Date", "Time",
                         "Service Type", "Price (RM)", "Vehicle", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {120, 100, 90, 75, 110, 90, 140, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

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
                    setBackground(UITheme.TABLE_SELECTED);
                    setForeground(UITheme.TEXT_PRIMARY);
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
                String.format("%.2f", a.getPrice()),
                a.getVehicleDetails(), a.getStatus()});
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

        String[] cols = {"Appointment ID", "Customer ID", "Date", "Time",
                         "Service Type", "Status"};
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
            if (a.getStatus().equals("Completed")) continue;
            model.addRow(new Object[]{a.getAppointmentid(), a.getCustomerid(),
                a.getDate(), a.getTime(), a.getServicetype(), a.getStatus()});
        }
    }

    private void updateStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(parent,
                "Select an appointment from the table first.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String apptId = (String) model.getValueAt(row, 0);

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
        g.insets = new Insets(6, 0, 6, 16);
        g.anchor = GridBagConstraints.WEST;

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

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        g.gridx=0; g.gridy=3; g.gridwidth=4; g.insets=new Insets(14,0,4,0);
        body.add(sep, g);
        g.gridy=4;
        body.add(UITheme.createLabel("Change Password",
            UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
        g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        addPair(body, g, 5, "Current Password *", fCurrent, "New Password", fNew);

        JLabel hint = UITheme.createLabel(
            "Current password is required to save changes.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=6; g.gridwidth=4; g.insets=new Insets(2,0,10,0);
        body.add(hint, g); g.gridwidth=1; g.insets=new Insets(6,0,6,16);

        statusLabel = UITheme.createLabel(" ",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=7; g.gridwidth=4;
        body.add(statusLabel, g);

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

    String np = new String(fNew.getPassword());
    if (np.isEmpty()) np = technician.getPassword();

    String error = Validator.validateAll(
        Validator.validateName(fName.getText()),
        Validator.validateAge(fAge.getText()),
        Validator.validateEmail(fEmail.getText()),
        Validator.validateUsername(fUsername.getText()),
        Validator.validateContact(fContact.getText()),
        np.equals(technician.getPassword()) ? null : Validator.validatePassword(np)
    );
    if (error != null) { setStatus(error, false); return; }

    int age = Integer.parseInt(fAge.getText().trim());
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
            fName.setText(technician.getName());
            fAge.setText(String.valueOf(technician.getAge()));
            fEmail.setText(technician.getEmail());
            fUsername.setText(technician.getUsername());
            fContact.setText(technician.getContact());
            fCurrent.setText(""); fNew.setText("");
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
//  Technician_FeedbackDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_FeedbackDialog {

    private final JFrame            parent;
    private final Technician        technician;
    private final TechnicianService service = new TechnicianService();

    public Technician_FeedbackDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "Submit Job Feedback", 540, 400);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Submit Job Feedback"), BorderLayout.NORTH);
        dialog.add(buildBody(dialog), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildBody(JDialog dialog) {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 28, 10, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0);
        g.anchor = GridBagConstraints.WEST;

        JLabel hint = UITheme.createLabel(
            "Feedback can only be submitted for Completed appointments.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);

        JTextField fApptId = UITheme.createTextField();
        fApptId.setPreferredSize(new Dimension(400, UITheme.INPUT_H));
        UITheme.placeholder(fApptId, "Enter Appointment ID...");

        JTextArea fFeedback = new JTextArea(5, 30);
        fFeedback.setFont(UITheme.FONT_REGULAR);
        fFeedback.setLineWrap(true);
        fFeedback.setWrapStyleWord(true);
        fFeedback.setCaretColor(UITheme.ACCENT);
        JScrollPane feedbackScroll = new JScrollPane(fFeedback);
        feedbackScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JLabel status = UITheme.createLabel(" ",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);

        g.gridx=0; g.gridy=0; g.gridwidth=2; body.add(hint, g);
        g.gridy=1;
        body.add(UITheme.createLabel("Appointment ID",
            UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridy=2; body.add(fApptId, g);
        g.gridy=3;
        body.add(UITheme.createLabel("Job Feedback / Notes",
            UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridy=4; body.add(feedbackScroll, g);
        g.gridy=5; body.add(status, g);

        JButton submit = UITheme.createPrimaryButton("Submit Feedback");
        JButton cancel = UITheme.createLinkButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        submit.addActionListener(e -> {
    String apptId   = fApptId.getText().trim();
    String feedback = fFeedback.getText().trim();

    String error = Validator.validateAll(
        Validator.validateAppointmentId(apptId),
        Validator.validateFeedback(feedback)
    );
    if (error != null) {
        status.setForeground(UITheme.ERROR);
        status.setText(error);
        return;
    }
    OperationResult r = service.submitJobFeedback(
        apptId, technician.getUserid(), feedback);
    if (r.getResult()) {
        JOptionPane.showMessageDialog(parent, r.getMessage(),
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    } else {
        status.setForeground(UITheme.ERROR);
        status.setText(r.getMessage());
    }
});

        g.gridy=6;
        body.add(UITheme.buttonRow(cancel, submit), g);
        return body;
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_JobDetailsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_JobDetailsDialog {

    private final JFrame            parent;
    private final Technician        technician;
    private final TechnicianService service = new TechnicianService();

    public Technician_JobDetailsDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "View Job Details", 560, 500);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("View Job Details"), BorderLayout.NORTH);
        dialog.add(buildBody(dialog), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildBody(JDialog dialog) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UITheme.WHITE);
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel lookupRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        lookupRow.setOpaque(false);
        lookupRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JTextField fApptId = UITheme.createTextField();
        fApptId.setPreferredSize(new Dimension(200, UITheme.INPUT_H));
        UITheme.placeholder(fApptId, "Appointment ID...");

        JButton loadBtn = UITheme.createPrimaryButton("Load Details");
        loadBtn.setPreferredSize(new Dimension(130, UITheme.INPUT_H));

        lookupRow.add(UITheme.createLabel("Appointment ID:",
            UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        lookupRow.add(fApptId);
        lookupRow.add(loadBtn);

        JLabel statusLabel = UITheme.createLabel(" ",
            UITheme.FONT_SMALL, UITheme.ERROR);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));

        JPanel detailCard = new JPanel(new GridBagLayout());
        detailCard.setBackground(new Color(248, 249, 252));
        detailCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        detailCard.setVisible(false);

       loadBtn.addActionListener(e -> {
    String apptId = fApptId.getText().trim();
    String error  = Validator.validateAppointmentId(apptId);
    if (error != null) {
        statusLabel.setText(error);
        detailCard.setVisible(false);
        return;
    }
    Appointment a = service.getJobDetails(apptId, technician.getUserid());
    if (a == null) {
        statusLabel.setText("Appointment not found or not assigned to you.");
        detailCard.setVisible(false);
        return;
    }

            statusLabel.setText(" ");
            detailCard.removeAll();
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL;
            g.insets = new Insets(5, 0, 5, 20);
            g.anchor = GridBagConstraints.WEST;

            String customerName = service.getCustomerNameForAppointment(a.getCustomerid());

            String[][] fields = {
                {"Appointment ID", a.getAppointmentid()},
                {"Customer",       customerName + " (" + a.getCustomerid() + ")"},
                {"Date",           a.getDate()},
                {"Time",           a.getTime()},
                {"Service Type",   a.getServicetype()},
                {"Price",          "RM " + String.format("%.2f", a.getPrice())},
                {"Vehicle",        a.getVehicleDetails()},
                {"Status",         a.getStatus()},
                {"Comments",       a.getComments() == null ? "—" : a.getComments()}
            };

            for (int i = 0; i < fields.length; i++) {
                g.gridx=0; g.gridy=i; g.weightx=0;
                detailCard.add(UITheme.createLabel(
                    fields[i][0], UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
                g.gridx=1; g.weightx=1;
                JLabel valLbl = UITheme.createLabel(
                    fields[i][1], UITheme.FONT_REGULAR, UITheme.TEXT_PRIMARY);
                if (fields[i][0].equals("Status")) {
                    valLbl.setFont(UITheme.FONT_BOLD);
                    valLbl.setForeground(switch (fields[i][1]) {
                        case "Completed"   -> UITheme.SUCCESS;
                        case "In Progress" -> new Color(37, 82, 148);
                        case "Pending"     -> UITheme.WARNING;
                        default            -> UITheme.TEXT_PRIMARY;
                    });
                }
                detailCard.add(valLbl, g);
            }

            detailCard.setVisible(true);
            detailCard.revalidate();
            detailCard.repaint();
        });

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(lookupRow);
        topSection.add(statusLabel);

        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());

        outer.add(topSection, BorderLayout.NORTH);
        outer.add(UITheme.formScrollPane(detailCard), BorderLayout.CENTER);
        outer.add(UITheme.buttonRow(close), BorderLayout.SOUTH);
        return outer;
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_ScheduleDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_ScheduleDialog {

    private final JFrame            parent;
    private final Technician        technician;
    private final TechnicianService service = new TechnicianService();

    public Technician_ScheduleDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "My Weekly Schedule", 900, 580);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("My Weekly Schedule"), BorderLayout.NORTH);
        dialog.add(buildBody(),          BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane buildBody() {
        java.util.LinkedHashMap<String, java.util.ArrayList<Appointment>> schedule =
            service.getWeeklySchedule(technician.getUserid());

        JPanel body = new JPanel();
        body.setBackground(UITheme.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        java.time.LocalDate today  = java.time.LocalDate.now();
        java.time.LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        java.time.LocalDate sunday = monday.plusDays(6);

        JLabel weekLbl = UITheme.createLabel(
            "Week: " + monday + "  to  " + sunday,
            UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        weekLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        weekLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(weekLbl);

        JPanel grid = new JPanel(new GridLayout(1, 7, 8, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        for (java.util.Map.Entry<String, java.util.ArrayList<Appointment>> entry
                : schedule.entrySet()) {
            String date = entry.getKey();
            java.util.ArrayList<Appointment> appts = entry.getValue();
            boolean isToday = date.equals(today.toString());
            grid.add(buildDayCard(date, appts, isToday));
        }

        body.add(grid);
        body.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        legend.setOpaque(false);
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);
        legend.add(legendItem("Completed",   UITheme.SUCCESS));
        legend.add(legendItem("In Progress", new Color(37, 82, 148)));
        legend.add(legendItem("Pending",     UITheme.WARNING));
        body.add(legend);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.WHITE);
        return scroll;
    }

    private JPanel buildDayCard(String date,
                                 java.util.ArrayList<Appointment> appts,
                                 boolean isToday) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(isToday ? new Color(240, 248, 255) : UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                isToday ? new Color(37, 82, 148) : UITheme.BORDER,
                isToday ? 2 : 1),
            BorderFactory.createEmptyBorder(10, 8, 10, 8)));

        String dayName = service.getDayName(date);
        JLabel dayLbl  = UITheme.createLabel(
            dayName.substring(0, 3).toUpperCase(),
            UITheme.FONT_BOLD,
            isToday ? new Color(37, 82, 148) : UITheme.TEXT_SECONDARY);
        dayLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        String dayNum  = date.substring(8);
        JLabel dateLbl = new JLabel(dayNum);
        dateLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        dateLbl.setForeground(isToday ? new Color(37, 82, 148) : UITheme.TEXT_PRIMARY);
        dateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel countBadge = new JPanel();
        countBadge.setOpaque(false);
        countBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
        JLabel countLbl = new JLabel(
            appts.size() + " job" + (appts.size() != 1 ? "s" : ""));
        countLbl.setFont(UITheme.FONT_SMALL);
        countLbl.setForeground(appts.isEmpty()
            ? UITheme.TEXT_SECONDARY : new Color(37, 82, 148));
        countBadge.add(countLbl);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        card.add(dayLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(dateLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(countBadge);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(sep);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        if (appts.isEmpty()) {
            JLabel freeLbl = UITheme.createLabel("Free",
                UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
            freeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(freeLbl);
        } else {
            for (Appointment a : appts) {
                card.add(buildApptChip(a));
                card.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel buildApptChip(Appointment a) {
        Color color = switch (a.getStatus()) {
            case "Completed"   -> UITheme.SUCCESS;
            case "In Progress" -> new Color(37, 82, 148);
            default            -> UITheme.WARNING;
        };

        JPanel chip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel timeLbl = new JLabel(a.getTime());
        timeLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        timeLbl.setForeground(color);
        timeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLbl = new JLabel(a.getServicetype());
        typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        typeLbl.setForeground(UITheme.TEXT_PRIMARY);
        typeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLbl = new JLabel(a.getStatus());
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        statusLbl.setForeground(color);
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        chip.add(timeLbl);
        chip.add(typeLbl);
        chip.add(statusLbl);
        return chip;
    }

    private JPanel legendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 10));
        JLabel lbl = UITheme.createLabel(label,
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(close);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Technician_JobHistoryDialog
// ══════════════════════════════════════════════════════════════════════════════

class Technician_JobHistoryDialog {

    private final JFrame            parent;
    private final Technician        technician;
    private final TechnicianService service = new TechnicianService();
    private DefaultTableModel       model;
    private JLabel                  totalRevLbl, totalJobsLbl, normalLbl, majorLbl;

    public Technician_JobHistoryDialog(JFrame parent, Technician technician) {
        this.parent = parent; this.technician = technician;
        JDialog dialog = UITheme.createDialog(parent, "Job History", 920, 600);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Job History"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadAll();
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UITheme.WHITE);
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        totalJobsLbl = statVal("0");
        normalLbl    = statVal("0");
        majorLbl     = statVal("0");
        totalRevLbl  = statVal("RM 0.00");
        totalRevLbl.setForeground(UITheme.SUCCESS);
        totalRevLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        statsRow.add(miniCard("Total Jobs",     totalJobsLbl, new Color(37, 82, 148)));
        statsRow.add(miniCard("Normal Service", normalLbl,    new Color(16, 150, 100)));
        statsRow.add(miniCard("Major Service",  majorLbl,     new Color(139, 92, 246)));
        statsRow.add(miniCard("Total Revenue",  totalRevLbl,  UITheme.SUCCESS));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        filterRow.add(UITheme.createLabel("From:",
            UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY));
        JTextField fromField = UITheme.createTextField();
        fromField.setPreferredSize(new Dimension(120, UITheme.INPUT_H));
        UITheme.placeholder(fromField, "YYYY-MM-DD");
        filterRow.add(fromField);

        filterRow.add(UITheme.createLabel("To:",
            UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY));
        JTextField toField = UITheme.createTextField();
        toField.setPreferredSize(new Dimension(120, UITheme.INPUT_H));
        UITheme.placeholder(toField, "YYYY-MM-DD");
        filterRow.add(toField);

        JButton filterBtn = UITheme.createPrimaryButton("Filter");
        filterBtn.setPreferredSize(new Dimension(90, UITheme.INPUT_H));
        JButton resetBtn  = UITheme.createOutlineButton("Reset");
        resetBtn.setPreferredSize(new Dimension(90, UITheme.INPUT_H));
        filterRow.add(filterBtn);
        filterRow.add(resetBtn);

        filterBtn.addActionListener(e -> {
            String from = fromField.getText().trim();
            String to   = toField.getText().trim();
            if (from.isEmpty() || to.isEmpty()
                    || from.equals("YYYY-MM-DD") || to.equals("YYYY-MM-DD")) {
                JOptionPane.showMessageDialog(parent,
                    "Please enter both From and To dates.",
                    "Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.util.ArrayList<Appointment> filtered =
                service.getJobHistoryByDateRange(technician.getUserid(), from, to);
            loadData(filtered);
        });

        resetBtn.addActionListener(e -> {
            fromField.setText("");
            toField.setText("");
            loadAll();
        });

        String[] cols = {"Appointment ID", "Customer ID", "Date", "Time",
                         "Service Type", "Price (RM)", "Vehicle", "Comments"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowHeight(44);

        int[] widths = {110, 100, 90, 70, 110, 90, 130, 180};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(7).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f,
                        int row, int col) {
                    JTextArea area = new JTextArea(v == null ? "—" : v.toString());
                    area.setFont(UITheme.FONT_REGULAR);
                    area.setWrapStyleWord(true);
                    area.setLineWrap(true);
                    area.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
                    area.setBackground(sel ? UITheme.TABLE_SELECTED
                        : row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    return area;
                }
            });

        table.getColumnModel().getColumn(4).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f,
                        int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, f, row, col);
                    setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                    if (!sel) {
                        setBackground(row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                        String s = v == null ? "" : v.toString();
                        setForeground(s.equalsIgnoreCase("Normal")
                            ? new Color(16, 150, 100) : new Color(139, 92, 246));
                        setFont(UITheme.FONT_BOLD);
                    } else {
                        setBackground(UITheme.TABLE_SELECTED);
                        setForeground(UITheme.TEXT_PRIMARY);
                    }
                    return this;
                }
            });

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(statsRow,  BorderLayout.NORTH);
        topSection.add(filterRow, BorderLayout.SOUTH);

        outer.add(topSection,                      BorderLayout.NORTH);
        outer.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return outer;
    }

    private void loadAll() {
        loadData(service.getJobHistory(technician.getUserid()));
    }

    private void loadData(java.util.ArrayList<Appointment> jobs) {
        model.setRowCount(0);
        for (Appointment a : jobs)
            model.addRow(new Object[]{
                a.getAppointmentid(), a.getCustomerid(),
                a.getDate(), a.getTime(), a.getServicetype(),
                String.format("%.2f", a.getPrice()),
                a.getVehicleDetails(),
                a.getComments() == null ? "—" : a.getComments()
            });

        int[]  stats = service.getJobHistoryStats(jobs);
        double rev   = service.getJobHistoryRevenue(jobs);
        totalJobsLbl.setText(String.valueOf(stats[0]));
        normalLbl.setText(String.valueOf(stats[1]));
        majorLbl.setText(String.valueOf(stats[2]));
        totalRevLbl.setText("RM " + String.format("%.2f", rev));
    }

    private JPanel miniCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12))));
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(UITheme.createLabel(label,
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), BorderLayout.SOUTH);
        return card;
    }

    private JLabel statVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(UITheme.TEXT_PRIMARY);
        return l;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(close);
    }
}