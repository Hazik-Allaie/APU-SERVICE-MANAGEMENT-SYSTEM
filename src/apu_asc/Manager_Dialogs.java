package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_SetPricesDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_SetPricesDialog {

    private final ManagerService service = new ManagerService();

    public Manager_SetPricesDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "Set Service Prices", 520, 340);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("Set Service Prices", "set_price.png"), BorderLayout.NORTH);
        dialog.add(buildBody(dialog), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildBody(JDialog dialog) {
        JPanel outer = DarkTheme.darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(24, 32, 10, 32));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 0, 10, 16);
        g.anchor = GridBagConstraints.WEST;

        Double[] prices = service.getPrices();

        JTextField fNormal = darkField(String.format("%.2f", prices[0]));
        JTextField fMajor  = darkField(String.format("%.2f", prices[1]));

        g.gridx=0; g.gridy=0; g.weightx=0;
        form.add(DarkTheme.label("Normal Service (RM)", DarkTheme.FONT_BOLD, DarkTheme.TEXT_WHITE), g);
        g.gridx=1; g.weightx=1; form.add(fNormal, g);
        g.gridx=2; g.weightx=0;
        form.add(priceBadge("RM " + String.format("%.2f", prices[0])), g);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,30));
        g.gridx=0; g.gridy=1; g.gridwidth=3; g.insets=new Insets(4,0,4,0);
        form.add(sep, g); g.gridwidth=1; g.insets=new Insets(10,0,10,16);

        g.gridx=0; g.gridy=2; g.weightx=0;
        form.add(DarkTheme.label("Major Service (RM)", DarkTheme.FONT_BOLD, DarkTheme.TEXT_WHITE), g);
        g.gridx=1; g.weightx=1; form.add(fMajor, g);
        g.gridx=2; g.weightx=0;
        form.add(priceBadge("RM " + String.format("%.2f", prices[1])), g);

        JLabel status = DarkTheme.label(" ", DarkTheme.FONT_SMALL, DarkTheme.ERROR);
        g.gridx=0; g.gridy=3; g.gridwidth=3; g.insets=new Insets(2,0,0,0);
        form.add(status, g);

        JButton save   = DarkTheme.primaryButton("Save Prices");
        JButton cancel = DarkTheme.linkButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
    String error = Validator.validateAll(
        Validator.validatePrice(fNormal.getText()),
        Validator.validatePrice(fMajor.getText())
    );
    if (error != null) {
        status.setForeground(UITheme.ERROR);
        status.setText(error);
        return;
    }
    double normal = Double.parseDouble(fNormal.getText().trim());
    double major  = Double.parseDouble(fMajor.getText().trim());
    OperationResult r = service.savePrices(normal, major);
    if (r.getResult()) {
        JOptionPane.showMessageDialog(dialog, r.getMessage(),
            "Saved", JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    } else {
        status.setForeground(UITheme.ERROR);
        status.setText(r.getMessage());
    }
});

        outer.add(form, BorderLayout.CENTER);
        outer.add(DarkTheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        return outer;
    }

    private JTextField darkField(String val) {
        JTextField f = new JTextField(val);
        f.setFont(DarkTheme.FONT_REGULAR);
        f.setForeground(DarkTheme.TEXT_WHITE);
        f.setBackground(new Color(20, 35, 100));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.GLASS_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JPanel priceBadge(String text) {
        return DarkTheme.badge(text, DarkTheme.ACCENT_BLUE, DarkTheme.ACCENT_BLUE);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_ViewFeedbacksDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_ViewFeedbacksDialog {

    private final ManagerService service = new ManagerService();
    private DefaultTableModel    model;

    public Manager_ViewFeedbacksDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "Customer Feedbacks", 900, 580);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("Customer Feedbacks", "view_feedback.png"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel outer = DarkTheme.darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        filterRow.add(DarkTheme.label("Filter by Status:", DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED));

        JComboBox<String> statusFilter = DarkTheme.darkCombo(
            new String[]{"All", "Pending", "Completed"});
        statusFilter.setPreferredSize(new Dimension(160, 34));
        statusFilter.addActionListener(e -> loadData((String) statusFilter.getSelectedItem()));
        filterRow.add(statusFilter);

        JLabel countLabel = DarkTheme.label("", DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
        filterRow.add(Box.createRigidArea(new Dimension(12, 0)));
        filterRow.add(countLabel);

        String[] cols = {"Appointment ID", "Customer ID", "Technician ID",
                         "Date", "Status", "Comments"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        DarkTheme.styleTable(table);
        table.setRowHeight(48);

        int[] widths = {120, 110, 110, 100, 100, 280};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status badge renderer
        table.getColumnModel().getColumn(4).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                    p.setOpaque(false);
                    String s = v == null ? "" : v.toString();
                    Color c = s.equals("Completed") ? DarkTheme.SUCCESS : DarkTheme.WARNING;
                    p.add(DarkTheme.badge(s, c, c));
                    return p;
                }
            });

        model.addTableModelListener(e ->
            countLabel.setText("Showing " + model.getRowCount() + " record(s)"));

        outer.add(filterRow,                    BorderLayout.NORTH);
        outer.add(DarkTheme.darkScrollPane(table), BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton refresh = DarkTheme.outlineButton("Refresh");
        refresh.addActionListener(e -> loadData("All"));
        JButton close = DarkTheme.linkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return DarkTheme.buttonRow(refresh, close);
    }

    private void loadData(String status) {
        model.setRowCount(0);
        for (Appointment a : service.getFeedbacksByStatus(status))
            model.addRow(new Object[]{a.getAppointmentid(), a.getCustomerid(),
                a.getTechnicianid(), a.getDate(), a.getStatus(), a.getComments()});
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_ViewReportsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_ViewReportsDialog {

    private final ManagerService service = new ManagerService();

    public Manager_ViewReportsDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "Reports & Statistics", 820, 620);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("Reports & Statistics", "view_report.png"), BorderLayout.NORTH);
        dialog.add(buildBody(),                                    BorderLayout.CENTER);
        JButton close = DarkTheme.linkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        dialog.add(DarkTheme.buttonRow(close), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane buildBody() {
        int[]    apptStats    = service.getAppointmentStats();
        double[] revenueStats = service.getRevenueStats();
        int[]    svcCounts    = service.getServiceTypeCounts();
        int[]    userCounts   = service.getUserCounts();

        JPanel body = DarkTheme.darkPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        body.add(DarkTheme.sectionLabel("Appointment Summary"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel row1 = cardRow(3);
        row1.add(statCard("Total",     String.valueOf(apptStats[0]), new Color(96,165,250)));
        row1.add(statCard("Completed", String.valueOf(apptStats[1]), DarkTheme.SUCCESS));
        row1.add(statCard("Pending",   String.valueOf(apptStats[2]), DarkTheme.WARNING));
        body.add(row1);

        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(DarkTheme.sectionLabel("Revenue Summary"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel row2 = cardRow(3);
        row2.add(statCard("Total Revenue",
            "RM "+String.format("%.2f",revenueStats[0]), new Color(96,165,250)));
        row2.add(statCard("Normal ("+svcCounts[0]+")",
            "RM "+String.format("%.2f",revenueStats[1]), DarkTheme.SUCCESS));
        row2.add(statCard("Major ("+svcCounts[1]+")",
            "RM "+String.format("%.2f",revenueStats[2]), DarkTheme.PURPLE));
        body.add(row2);

        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(DarkTheme.sectionLabel("System Users"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel row3 = cardRow(4);
        row3.add(statCard("Managers",      String.valueOf(userCounts[0]), new Color(96,165,250)));
        row3.add(statCard("Counter Staff", String.valueOf(userCounts[1]), DarkTheme.SUCCESS));
        row3.add(statCard("Technicians",   String.valueOf(userCounts[2]), DarkTheme.WARNING));
        row3.add(statCard("Customers",     String.valueOf(userCounts[3]), DarkTheme.PURPLE));
        body.add(row3);

        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(DarkTheme.sectionLabel("Appointment Breakdown"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));

        String[] cols = {"Appointment ID","Customer","Technician",
                         "Service","Date","Status","Amount (RM)"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Appointment a : service.getAllAppointmentsForReport())
            tm.addRow(new Object[]{a.getAppointmentid(), a.getCustomerid(),
                a.getTechnicianid(), a.getServicetype(), a.getDate(),
                a.getStatus(), String.format("%.2f", a.getPrice())});

        JTable table = new JTable(tm);
        DarkTheme.styleTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(700, 160));
        JScrollPane tableScroll = DarkTheme.darkScrollPane(table);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        body.add(tableScroll);

        JScrollPane outer = new JScrollPane(body);
        outer.setBorder(null);
        outer.getViewport().setOpaque(false);
        outer.setOpaque(false);
        return outer;
    }

    private JPanel cardRow(int cols) {
        JPanel p = new JPanel(new GridLayout(1, cols, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = DarkTheme.glassCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel valLbl = DarkTheme.label(value,
            new Font("Segoe UI", Font.BOLD, 18), accent);
        JLabel lblLbl = DarkTheme.label(label, DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
        card.add(valLbl, BorderLayout.CENTER);
        card.add(lblLbl, BorderLayout.SOUTH);
        return card;
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_ViewAllAppointmentsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_ViewAllAppointmentsDialog {

    private final ManagerService service = new ManagerService();
    private DefaultTableModel    model;
    private String               activeStatus = "All";

    public Manager_ViewAllAppointmentsDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "All Appointments", 1000, 600);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("All Appointments", "all_appointments.png"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel outer = DarkTheme.darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        filterRow.add(DarkTheme.label("Filter by Status:", DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED));

        JComboBox<String> statusFilter = DarkTheme.darkCombo(
            new String[]{"All", "Pending", "In Progress", "Completed"});
        statusFilter.setPreferredSize(new Dimension(160, 34));
        statusFilter.addActionListener(e -> {
            activeStatus = (String) statusFilter.getSelectedItem();
            loadData(activeStatus);
        });
        filterRow.add(statusFilter);

        JLabel countLabel = DarkTheme.label("", DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
        filterRow.add(Box.createRigidArea(new Dimension(12, 0)));
        filterRow.add(countLabel);

        String[] cols = {"Appointment ID", "Customer ID", "Technician ID",
                         "Date", "Time", "Service Type", "Price (RM)", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        DarkTheme.styleTable(table);

        int[] widths = {110, 100, 110, 90, 70, 110, 90, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status badge renderer
        table.getColumnModel().getColumn(7).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                    p.setOpaque(false);
                    String s = v == null ? "" : v.toString();
                    Color c = switch (s) {
                        case "Completed"   -> DarkTheme.SUCCESS;
                        case "In Progress" -> DarkTheme.ACCENT_BLUE;
                        default            -> DarkTheme.WARNING;
                    };
                    p.add(DarkTheme.badge(s, c, c));
                    return p;
                }
            });

        model.addTableModelListener(e ->
            countLabel.setText("Showing " + model.getRowCount() + " appointment(s)"));

        outer.add(filterRow,                       BorderLayout.NORTH);
        outer.add(DarkTheme.darkScrollPane(table), BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton refresh = DarkTheme.outlineButton("Refresh");
        refresh.addActionListener(e -> loadData(activeStatus));
        JButton close = DarkTheme.linkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return DarkTheme.buttonRow(refresh, close);
    }

    private void loadData(String status) {
        model.setRowCount(0);
        for (Appointment a : service.getAppointmentsByStatus(status))
            model.addRow(new Object[]{
                a.getAppointmentid(), a.getCustomerid(), a.getTechnicianid(),
                a.getDate(), a.getTime(), a.getServicetype(),
                String.format("%.2f", a.getPrice()), a.getStatus()
            });
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_TechnicianPerformanceDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_TechnicianPerformanceDialog {

    private final ManagerService service = new ManagerService();
    private DefaultTableModel    model;
    private JTable               table;

    public Manager_TechnicianPerformanceDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "Technician Performance", 880, 580);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("Technician Performance", "technician_performance.png"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData();
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel outer = DarkTheme.darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JLabel hint = DarkTheme.label(
            "Click a technician row to view their appointments below.",
            DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Top table
        String[] cols = {"Technician ID", "Name", "Total Jobs",
                         "Completed", "Pending", "In Progress", "Revenue (RM)", "Progress"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        DarkTheme.styleTable(table);
        table.setRowHeight(52);

        int[] widths = {100, 140, 80, 90, 80, 90, 100, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Completed badge renderer
        table.getColumnModel().getColumn(3).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
                    p.setOpaque(false);
                    p.add(DarkTheme.badge(v+"", DarkTheme.SUCCESS, DarkTheme.SUCCESS));
                    return p;
                }
            });

        // Pending badge renderer
        table.getColumnModel().getColumn(4).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
                    p.setOpaque(false);
                    p.add(DarkTheme.badge(v+"", DarkTheme.WARNING, DarkTheme.WARNING));
                    return p;
                }
            });

        // In Progress badge renderer
        table.getColumnModel().getColumn(5).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
                    p.setOpaque(false);
                    p.add(DarkTheme.badge(v+"", DarkTheme.ACCENT_BLUE, DarkTheme.ACCENT_BLUE));
                    return p;
                }
            });

        // Revenue color renderer
        table.getColumnModel().getColumn(6).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel cell = new JPanel(new BorderLayout());
                    cell.setOpaque(false);
                    JLabel lbl = DarkTheme.label("RM "+v,
                        new Font("Segoe UI", Font.BOLD, 13), DarkTheme.SUCCESS);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                    cell.add(lbl, BorderLayout.CENTER);
                    return cell;
                }
            });

        // Progress bar renderer
        table.getColumnModel().getColumn(7).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel cell = new JPanel(new BorderLayout());
                    cell.setOpaque(false);
                    cell.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
                    if (v instanceof int[]) {
                        int[] data = (int[]) v;
                        cell.add(DarkTheme.progressBar(data[0], data[1],
                            DarkTheme.SUCCESS), BorderLayout.CENTER);
                    }
                    return cell;
                }
            });

        // Bottom detail table
        String[] detailCols = {"Appointment ID","Customer ID","Date",
                               "Service Type","Price (RM)","Status"};
        DefaultTableModel detailModel = new DefaultTableModel(detailCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable detailTable = new JTable(detailModel);
        DarkTheme.styleTable(detailTable);
        detailTable.setRowHeight(38);

        JLabel detailHeader = DarkTheme.label(
            "Select a technician above to view their appointments",
            DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED);
        detailHeader.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            String techId   = (String) model.getValueAt(row, 0);
            String techName = (String) model.getValueAt(row, 1);
            detailHeader.setText("Appointments — " + techName + " (" + techId + ")");
            detailModel.setRowCount(0);
            for (Appointment a : service.getAppointmentsByTechnician(techId))
                detailModel.addRow(new Object[]{
                    a.getAppointmentid(), a.getCustomerid(), a.getDate(),
                    a.getServicetype(),
                    String.format("%.2f", a.getPrice()), a.getStatus()
                });
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(hint, BorderLayout.NORTH);
        topPanel.add(DarkTheme.darkScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(detailHeader, BorderLayout.NORTH);
        bottomPanel.add(DarkTheme.darkScrollPane(detailTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        split.setDividerLocation(270);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(new Color(0,0,0,0));

        outer.add(split, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton refresh = DarkTheme.outlineButton("Refresh");
        refresh.addActionListener(e -> loadData());
        JButton close = DarkTheme.linkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return DarkTheme.buttonRow(refresh, close);
    }

    private void loadData() {
        model.setRowCount(0);
        for (User u : service.getAllTechnicians()) {
            int[]  stats   = service.getTechnicianStats(u.getUserid());
            double revenue = service.getTechnicianRevenue(u.getUserid());
            model.addRow(new Object[]{
                u.getUserid(), u.getName(),
                stats[0], stats[1], stats[2], stats[3],
                String.format("%.2f", revenue),
                new int[]{stats[1], stats[0]}  // completed, total for progress bar
            });
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_ManageStaffDialog  (dark themed)
// ══════════════════════════════════════════════════════════════════════════════

class Manager_ManageStaffDialog {

    private final JFrame         parent;
    private final ManagerService service = new ManagerService();
    private DefaultTableModel    model;
    private JTable               table;
    private JTextField           searchField;
    private String               activeRole = "All";

    public Manager_ManageStaffDialog(JFrame parent) {
        this.parent = parent;
        JDialog dialog = DarkTheme.createDialog(parent, "Staff Management", 980, 620);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("Staff Management", "manage_staff.png"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel outer = DarkTheme.darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Filter tabs
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        for (String role : new String[]{"All","Manager","CounterStaff","Technician"}) {
            JToggleButton tb = filterTab(role);
            if (role.equals("All")) tb.setSelected(true);
            tb.addActionListener(e -> { activeRole = role; loadData(role); });
            bg.add(tb); filterRow.add(tb);
        }

        JPanel actionRow = new JPanel(new BorderLayout(10, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));

        searchField = new JTextField();
        searchField.setFont(DarkTheme.FONT_REGULAR);
        searchField.setForeground(DarkTheme.TEXT_WHITE);
        searchField.setBackground(new Color(20, 35, 100));
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.GLASS_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filterByQuery(searchField.getText());
            }
        });

        JButton addBtn = DarkTheme.primaryButton("+ Add Staff");
        addBtn.setPreferredSize(new Dimension(140, 36));
        addBtn.addActionListener(e -> showAddDialog());

        actionRow.add(searchField, BorderLayout.WEST);
        actionRow.add(addBtn,      BorderLayout.EAST);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(filterRow);
        top.add(actionRow);

        String[] cols = {"Staff ID","Name","Age","Email","Username","Contact","Role"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        DarkTheme.styleTable(table);

        int[] widths = {90, 155, 50, 185, 130, 120, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Role badge renderer
        table.getColumnModel().getColumn(6).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f, int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                    p.setOpaque(false);
                    String s = v == null ? "" : v.toString();
                    Color c = switch (s) {
                        case "Manager"      -> DarkTheme.ACCENT_BLUE;
                        case "CounterStaff" -> DarkTheme.SUCCESS;
                        case "Technician"   -> DarkTheme.WARNING;
                        default             -> DarkTheme.TEXT_MUTED;
                    };
                    p.add(DarkTheme.badge(s, c, c));
                    return p;
                }
            });

        outer.add(top,                             BorderLayout.NORTH);
        outer.add(DarkTheme.darkScrollPane(table), BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton editBtn   = DarkTheme.outlineButton("Edit");
        JButton deleteBtn = DarkTheme.primaryButton("Delete");
        deleteBtn.setBackground(DarkTheme.ERROR);
        JButton closeBtn  = DarkTheme.linkButton("Close");

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { noSel(); return; }
            showEditDialog((String) model.getValueAt(row, 0));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { noSel(); return; }
            String id   = (String) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            if (JOptionPane.showConfirmDialog(dialog,
                    "Delete staff \"" + name + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                OperationResult r = service.deleteStaff(id);
                toast(r); if (r.getResult()) loadData(activeRole);
            }
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        return DarkTheme.buttonRow(editBtn, deleteBtn, closeBtn);
    }

    private void loadData(String role) {
        model.setRowCount(0);
        for (User u : service.getAllStaff()) {
            if (!role.equals("All") && !u.getRole().equals(role)) continue;
            model.addRow(toRow(u));
        }
    }

    private void filterByQuery(String q) {
        model.setRowCount(0);
        String lq = q.toLowerCase();
        for (User u : service.getAllStaff()) {
            if (!activeRole.equals("All") && !u.getRole().equals(activeRole)) continue;
            if (u.getName().toLowerCase().contains(lq)
                || u.getUserid().toLowerCase().contains(lq)
                || u.getUsername().toLowerCase().contains(lq))
                model.addRow(toRow(u));
        }
    }

    private Object[] toRow(User u) {
        return new Object[]{u.getUserid(), u.getName(), u.getAge(),
            u.getEmail(), u.getUsername(), u.getContact(), u.getRole()};
    }

    private void showAddDialog() {
        JDialog d = DarkTheme.createDialog(parent, "Add New Staff", 460, 600);
        d.setLayout(new BorderLayout());
        d.add(DarkTheme.dialogHeader("Add New Staff"), BorderLayout.NORTH);

        JPanel form = DarkTheme.darkPanel();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = formGbc();

        JComboBox<String> fRole = DarkTheme.darkCombo(
            new String[]{"Manager","CounterStaff","Technician"});
        JTextField     fId = df(), fName = df(), fAge = df(),
                       fEmail = df(), fUsername = df(), fContact = df();
        JPasswordField fPass = dpf();

        formRow(form, g, 0, "Role",      fRole);
        formRow(form, g, 1, "Staff ID",  fId);
        formRow(form, g, 2, "Full Name", fName);
        formRow(form, g, 3, "Age",       fAge);
        formRow(form, g, 4, "Email",     fEmail);
        formRow(form, g, 5, "Username",  fUsername);
        formRow(form, g, 6, "Password",  fPass);
        formRow(form, g, 7, "Contact",   fContact);

        JLabel status = DarkTheme.label(" ", DarkTheme.FONT_SMALL, DarkTheme.ERROR);
        g.gridx=0; g.gridy=16; g.gridwidth=2; form.add(status, g);

        JButton save   = DarkTheme.primaryButton("Save");
        JButton cancel = DarkTheme.outlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
    String error = Validator.validateAll(
        Validator.validateStaffId(fId.getText()),
        Validator.validateName(fName.getText()),
        Validator.validateAge(fAge.getText()),
        Validator.validateEmail(fEmail.getText()),
        Validator.validateUsername(fUsername.getText()),
        Validator.validatePassword(new String(fPass.getPassword())),
        Validator.validateContact(fContact.getText())
    );
    if (error != null) { status.setText(error); return; }

    int age = Integer.parseInt(fAge.getText().trim());
    OperationResult r = service.addStaff(
        (String) fRole.getSelectedItem(),
        fId.getText().trim(), fName.getText().trim(), age,
        fEmail.getText().trim(), fUsername.getText().trim(),
        new String(fPass.getPassword()), fContact.getText().trim());
    toast(r);
    if (r.getResult()) { d.dispose(); loadData(activeRole); }
    else status.setText(r.getMessage());
});

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        d.add(scroll, BorderLayout.CENTER);
        d.add(DarkTheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showEditDialog(String staffId) {
        User u = service.getStaffById(staffId);
        if (u == null) return;

        JDialog d = DarkTheme.createDialog(parent, "Edit Staff", 460, 560);
        d.setLayout(new BorderLayout());
        d.add(DarkTheme.dialogHeader("Edit Staff"), BorderLayout.NORTH);

        JPanel form = DarkTheme.darkPanel();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = formGbc();

        JTextField fRole = df(u.getRole());
        fRole.setEditable(false);
        fRole.setForeground(DarkTheme.TEXT_MUTED);
        JTextField fName    = df(u.getName());
        JTextField fAge     = df(String.valueOf(u.getAge()));
        JTextField fEmail   = df(u.getEmail());
        JTextField fUsername= df(u.getUsername());
        JTextField fContact = df(u.getContact());
        JPasswordField fPass = dpf();

        formRow(form, g, 0, "Role (locked)", fRole);
        formRow(form, g, 1, "Full Name",     fName);
        formRow(form, g, 2, "Age",           fAge);
        formRow(form, g, 3, "Email",         fEmail);
        formRow(form, g, 4, "Username",      fUsername);
        formRow(form, g, 5, "New Password",  fPass);
        formRow(form, g, 6, "Contact",       fContact);

        JLabel hint = DarkTheme.label("Leave password blank to keep current.",
            DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
        g.gridx=0; g.gridy=14; g.gridwidth=2; form.add(hint, g);
        JLabel status = DarkTheme.label(" ", DarkTheme.FONT_SMALL, DarkTheme.ERROR);
        g.gridy=15; form.add(status, g);

        JButton save   = DarkTheme.primaryButton("Save Changes");
        JButton cancel = DarkTheme.outlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
       save.addActionListener(e -> {
    String np = new String(fPass.getPassword());
    if (np.isEmpty()) np = u.getPassword();

    String error = Validator.validateAll(
        Validator.validateName(fName.getText()),
        Validator.validateAge(fAge.getText()),
        Validator.validateEmail(fEmail.getText()),
        Validator.validateUsername(fUsername.getText()),
        Validator.validateContact(fContact.getText()),
        np.equals(u.getPassword()) ? null : Validator.validatePassword(np)
    );
    if (error != null) { status.setText(error); return; }

    int age = Integer.parseInt(fAge.getText().trim());
    OperationResult r = service.updateStaff(staffId,
        fName.getText().trim(), age, fEmail.getText().trim(),
        fUsername.getText().trim(), np, fContact.getText().trim());
    toast(r);
    if (r.getResult()) { d.dispose(); loadData(activeRole); }
    else status.setText(r.getMessage());
});

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        d.add(scroll, BorderLayout.CENTER);
        d.add(DarkTheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTextField df() {
        JTextField f = new JTextField();
        f.setFont(DarkTheme.FONT_REGULAR);
        f.setForeground(DarkTheme.TEXT_WHITE);
        f.setBackground(new Color(20, 35, 100));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.GLASS_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JTextField df(String val) { JTextField f = df(); f.setText(val); return f; }

    private JPasswordField dpf() {
        JPasswordField f = new JPasswordField();
        f.setFont(DarkTheme.FONT_REGULAR);
        f.setForeground(DarkTheme.TEXT_WHITE);
        f.setBackground(new Color(20, 35, 100));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.GLASS_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private GridBagConstraints formGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 12);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private void formRow(JPanel p, GridBagConstraints g, int row,
                          String label, JComponent field) {
        g.gridx=0; g.gridy=row*2; g.weightx=0;
        p.add(DarkTheme.label(label, DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED), g);
        g.gridx=0; g.gridy=row*2+1; g.weightx=1; g.gridwidth=2;
        p.add(field, g);
        g.gridwidth=1;
    }

    private JToggleButton filterTab(String text) {
        JToggleButton tb = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(40, 80, 200),
                        0, getHeight(), new Color(70, 120, 230));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(getModel().isRollover()
                        ? new Color(255,255,255,20) : new Color(255,255,255,8));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(DarkTheme.GLASS_BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                    g2.setColor(DarkTheme.TEXT_MUTED);
                }
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        tb.setFont(DarkTheme.FONT_BOLD);
        tb.setPreferredSize(new Dimension(120, 34));
        tb.setBorderPainted(false);
        tb.setFocusPainted(false);
        tb.setContentAreaFilled(false);
        tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tb;
    }

    private void noSel() {
        JOptionPane.showMessageDialog(parent,
            "Select a staff member from the table first.",
            "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toast(OperationResult r) {
        JOptionPane.showMessageDialog(parent, r.getMessage(),
            r.getResult() ? "Success" : "Error",
            r.getResult() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}
// ══════════════════════════════════════════════════════════════════════════════
//  Manager_ViewAllPaymentsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_ViewAllPaymentsDialog {

    private final ManagerService  service = new ManagerService();
    private DefaultTableModel     model;
    private JLabel                totalRevLbl, cashLbl, cardLbl, onlineLbl;

    public Manager_ViewAllPaymentsDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "All Payments", 980, 620);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("All Payments", "view_payments.png"),
            BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel outer = DarkTheme.darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        totalRevLbl = DarkTheme.label(
            "RM " + String.format("%.2f", service.getTotalRevenue()),
            new Font("Segoe UI", Font.BOLD, 16), DarkTheme.SUCCESS);
        cashLbl = DarkTheme.label(
            "RM " + String.format("%.2f", service.getRevenueByMethod("Cash")),
            new Font("Segoe UI", Font.BOLD, 16), new Color(96, 165, 250));
        cardLbl = DarkTheme.label(
            "RM " + String.format("%.2f", service.getRevenueByMethod("Card")),
            new Font("Segoe UI", Font.BOLD, 16), DarkTheme.WARNING);
        onlineLbl = DarkTheme.label(
            "RM " + String.format("%.2f", service.getRevenueByMethod("Online")),
            new Font("Segoe UI", Font.BOLD, 16), DarkTheme.PURPLE);

        statsRow.add(summaryCard("Total Revenue",
            totalRevLbl, DarkTheme.SUCCESS));
        statsRow.add(summaryCard("Cash ("
            + service.getPaymentCountByMethod("Cash") + ")",
            cashLbl, new Color(96, 165, 250)));
        statsRow.add(summaryCard("Card ("
            + service.getPaymentCountByMethod("Card") + ")",
            cardLbl, DarkTheme.WARNING));
        statsRow.add(summaryCard("Online ("
            + service.getPaymentCountByMethod("Online") + ")",
            onlineLbl, DarkTheme.PURPLE));

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        filterRow.add(DarkTheme.label("Method:",
            DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED));
        JComboBox<String> methodFilter = DarkTheme.darkCombo(
            new String[]{"All", "Cash", "Card", "Online"});
        methodFilter.setPreferredSize(new Dimension(130, 34));
        filterRow.add(methodFilter);

        filterRow.add(DarkTheme.label("  From:",
            DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED));
        JTextField fromField = darkField();
        fromField.setPreferredSize(new Dimension(120, 34));
        UITheme.placeholder(fromField, "YYYY-MM-DD");
        filterRow.add(fromField);

        filterRow.add(DarkTheme.label("To:",
            DarkTheme.FONT_BOLD, DarkTheme.TEXT_MUTED));
        JTextField toField = darkField();
        toField.setPreferredSize(new Dimension(120, 34));
        UITheme.placeholder(toField, "YYYY-MM-DD");
        filterRow.add(toField);

        JButton filterBtn = DarkTheme.primaryButton("Filter");
        filterBtn.setPreferredSize(new Dimension(90, 34));
        JButton resetBtn  = DarkTheme.outlineButton("Reset");
        resetBtn.setPreferredSize(new Dimension(90, 34));
        filterRow.add(filterBtn);
        filterRow.add(resetBtn);

        JLabel countLabel = DarkTheme.label("",
            DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
        filterRow.add(Box.createRigidArea(new Dimension(12, 0)));
        filterRow.add(countLabel);

        // Table
        String[] cols = {"Payment ID", "Appointment ID", "Customer ID",
                         "Service Type", "Amount (RM)", "Date",
                         "Method", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        DarkTheme.styleTable(table);

        int[] widths = {100, 120, 100, 110, 100, 100, 90, 80};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Amount colour renderer
        table.getColumnModel().getColumn(4).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f,
                        int row, int col) {
                    JPanel cell = new JPanel(new BorderLayout());
                    cell.setOpaque(false);
                    JLabel lbl = DarkTheme.label("RM " + v,
                        new Font("Segoe UI", Font.BOLD, 13),
                        DarkTheme.SUCCESS);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                    cell.add(lbl, BorderLayout.CENTER);
                    return cell;
                }
            });

        // Method badge renderer
        table.getColumnModel().getColumn(6).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean f,
                        int row, int col) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                    p.setOpaque(false);
                    String s = v == null ? "" : v.toString();
                    Color c = switch (s) {
                        case "Cash"   -> new Color(96, 165, 250);
                        case "Card"   -> DarkTheme.WARNING;
                        case "Online" -> DarkTheme.PURPLE;
                        default       -> DarkTheme.TEXT_MUTED;
                    };
                    p.add(DarkTheme.badge(s, c, c));
                    return p;
                }
            });

        model.addTableModelListener(e ->
            countLabel.setText("Showing " + model.getRowCount() + " payment(s)"));

        filterBtn.addActionListener(e -> {
            String method = (String) methodFilter.getSelectedItem();
            String from   = fromField.getText().trim();
            String to     = toField.getText().trim();

            java.util.ArrayList<Payment> results;

            if (!from.isEmpty() && !to.isEmpty()
                    && !from.equals("YYYY-MM-DD")
                    && !to.equals("YYYY-MM-DD")) {
                results = service.getPaymentsByDateRange(from, to);
                if (!method.equals("All"))
                    results.removeIf(p ->
                        !p.getPaymentMethod().equals(method));
            } else {
                results = service.getPaymentsByMethod(method);
            }
            populateTable(results);
        });

        resetBtn.addActionListener(e -> {
            methodFilter.setSelectedIndex(0);
            fromField.setText("");
            toField.setText("");
            loadData("All");
        });

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(statsRow,  BorderLayout.NORTH);
        topSection.add(filterRow, BorderLayout.SOUTH);

        outer.add(topSection,                      BorderLayout.NORTH);
        outer.add(DarkTheme.darkScrollPane(table), BorderLayout.CENTER);
        return outer;
    }

    private void loadData(String method) {
        populateTable(service.getPaymentsByMethod(method));
    }

    private void populateTable(java.util.ArrayList<Payment> payments) {
        model.setRowCount(0);
        for (Payment p : payments)
            model.addRow(new Object[]{
                p.getPaymentId(), p.getAppointmentId(), p.getCustomerId(),
                p.getServiceType(),
                String.format("%.2f", p.getAmount()),
                p.getPaymentDate(), p.getPaymentMethod(), p.getStatus()
            });
    }

    private JPanel summaryCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = DarkTheme.glassCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        card.add(valueLabel,
            BorderLayout.CENTER);
        card.add(DarkTheme.label(label,
            DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED), BorderLayout.SOUTH);
        return card;
    }

    private JTextField darkField() {
        JTextField f = new JTextField();
        f.setFont(DarkTheme.FONT_REGULAR);
        f.setForeground(DarkTheme.TEXT_WHITE);
        f.setBackground(new Color(20, 35, 100));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.GLASS_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton close = DarkTheme.linkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return DarkTheme.buttonRow(close);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_StatisticsDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_StatisticsDialog {

    private final ManagerService service = new ManagerService();

    public Manager_StatisticsDialog(JFrame parent) {
        JDialog dialog = DarkTheme.createDialog(parent, "System Statistics", 980, 660);
        dialog.setLayout(new BorderLayout());
        dialog.add(DarkTheme.dialogHeader("System Statistics", "statistics.png"),
            BorderLayout.NORTH);
        dialog.add(buildBody(), BorderLayout.CENTER);
        JButton close = DarkTheme.linkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        dialog.add(DarkTheme.buttonRow(close), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane buildBody() {
        JPanel body = DarkTheme.darkPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // ── Row 1: Appointments by Month chart ────────────────────────────
        body.add(DarkTheme.sectionLabel("Appointments by Month"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildBarChart(
            service.getAppointmentsByMonth(),
            new Color(96, 165, 250), "Appointments"));
        body.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Row 2: Revenue by Month chart ─────────────────────────────────
        body.add(DarkTheme.sectionLabel("Revenue by Month (RM)"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildRevenueChart(service.getRevenueByMonth()));
        body.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Row 3: Appointments by Day of Week ────────────────────────────
        body.add(DarkTheme.sectionLabel("Busiest Days of Week"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildBarChart(
            service.getAppointmentsByDayOfWeek(),
            new Color(139, 92, 246), "Appointments"));
        body.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Row 4: Top Technicians ────────────────────────────────────────
        body.add(DarkTheme.sectionLabel("Top Technicians (Completed Jobs)"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        body.add(buildTechnicianChart());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        return scroll;
    }

    // ── Bar chart (integer values) ────────────────────────────────────────────

    private JPanel buildBarChart(
            java.util.LinkedHashMap<String, Integer> data,
            Color barColor, String unit) {

        int maxVal = data.values().stream()
            .mapToInt(Integer::intValue).max().orElse(1);
        if (maxVal == 0) maxVal = 1;

        JPanel chart = new JPanel(new BorderLayout());
        chart.setOpaque(false);
        chart.setAlignmentX(Component.LEFT_ALIGNMENT);
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel bars = new JPanel(new GridLayout(1, data.size(), 6, 0));
        bars.setOpaque(false);

        for (java.util.Map.Entry<String, Integer> entry : data.entrySet()) {
            bars.add(buildBar(entry.getKey(), entry.getValue(),
                maxVal, barColor));
        }

        chart.add(bars, BorderLayout.CENTER);
        return chart;
    }

    private JPanel buildBar(String label, int value, int maxVal, Color color) {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        // Value label
        JLabel valLbl = DarkTheme.label(String.valueOf(value),
            new Font("Segoe UI", Font.BOLD, 11), color);
        valLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bar
        double pct = (double) value / maxVal;
        int barH = (int)(100 * pct);
        if (barH < 2 && value > 0) barH = 2;

        JPanel barContainer = new JPanel();
        barContainer.setOpaque(false);
        barContainer.setLayout(new BoxLayout(barContainer, BoxLayout.Y_AXIS));
        barContainer.setPreferredSize(new Dimension(0, 110));

        // Spacer on top
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        int spaceH = 100 - barH;
        spacer.setMaximumSize(new Dimension(Integer.MAX_VALUE, spaceH));
        spacer.setPreferredSize(new Dimension(0, spaceH));

        // Actual bar
        Color barFill = color;
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, barFill.brighter(),
                    0, getHeight(), barFill);
                g2.setPaint(gp);
                g2.fillRoundRect(2, 0, getWidth()-4, getHeight(), 4, 4);
                // Shine
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(2, 0, getWidth()-4, getHeight()/3, 4, 4);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, barH));
        bar.setPreferredSize(new Dimension(0, barH));

        barContainer.add(spacer);
        barContainer.add(bar);

        // Label
        JLabel nameLbl = DarkTheme.label(label,
            new Font("Segoe UI", Font.PLAIN, 10), DarkTheme.TEXT_MUTED);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        col.add(valLbl);
        col.add(barContainer);
        col.add(nameLbl);
        return col;
    }

    // ── Revenue bar chart (double values) ────────────────────────────────────

    private JPanel buildRevenueChart(
            java.util.LinkedHashMap<String, Double> data) {

        double maxVal = data.values().stream()
            .mapToDouble(Double::doubleValue).max().orElse(1);
        if (maxVal == 0) maxVal = 1;

        JPanel chart = new JPanel(new BorderLayout());
        chart.setOpaque(false);
        chart.setAlignmentX(Component.LEFT_ALIGNMENT);
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel bars = new JPanel(new GridLayout(1, data.size(), 6, 0));
        bars.setOpaque(false);

        Color barColor = DarkTheme.SUCCESS;
        for (java.util.Map.Entry<String, Double> entry : data.entrySet()) {
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

            double pct = entry.getValue() / maxVal;
            int barH = (int)(100 * pct);
            if (barH < 2 && entry.getValue() > 0) barH = 2;

            String valStr = entry.getValue() == 0 ? "0"
                : String.format("%.0f", entry.getValue());

            JLabel valLbl = DarkTheme.label(valStr,
                new Font("Segoe UI", Font.BOLD, 10), barColor);
            valLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel barContainer = new JPanel();
            barContainer.setOpaque(false);
            barContainer.setLayout(new BoxLayout(barContainer, BoxLayout.Y_AXIS));
            barContainer.setPreferredSize(new Dimension(0, 110));

            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            int spaceH = 100 - barH;
            spacer.setMaximumSize(new Dimension(Integer.MAX_VALUE, spaceH));
            spacer.setPreferredSize(new Dimension(0, spaceH));

            Color fill = barColor;
            JPanel bar = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(
                        0, 0, fill.brighter(), 0, getHeight(), fill);
                    g2.setPaint(gp);
                    g2.fillRoundRect(2, 0, getWidth()-4, getHeight(), 4, 4);
                    g2.setColor(new Color(255,255,255,40));
                    g2.fillRoundRect(2, 0, getWidth()-4, getHeight()/3, 4, 4);
                    g2.dispose();
                }
            };
            bar.setOpaque(false);
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, barH));
            bar.setPreferredSize(new Dimension(0, barH));

            barContainer.add(spacer);
            barContainer.add(bar);

            JLabel nameLbl = DarkTheme.label(entry.getKey(),
                new Font("Segoe UI", Font.PLAIN, 10), DarkTheme.TEXT_MUTED);
            nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            col.add(valLbl);
            col.add(barContainer);
            col.add(nameLbl);
            bars.add(col);
        }

        chart.add(bars, BorderLayout.CENTER);
        return chart;
    }

    // ── Technician performance chart ──────────────────────────────────────────

    private JPanel buildTechnicianChart() {
        java.util.LinkedHashMap<String, Integer> data =
            service.getTopTechnicians();

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        if (data.isEmpty()) {
            JLabel empty = DarkTheme.label("No completed jobs yet.",
                DarkTheme.FONT_SMALL, DarkTheme.TEXT_MUTED);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(empty);
            return panel;
        }

        int maxVal = data.values().stream()
            .mapToInt(Integer::intValue).max().orElse(1);

        for (java.util.Map.Entry<String, Integer> entry : data.entrySet()) {
            String name  = service.getTechnicianName(entry.getKey());
            int    count = entry.getValue();
            double pct   = (double) count / maxVal;

            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Name label
            JLabel nameLbl = DarkTheme.label(
                name + " (" + entry.getKey() + ")",
                DarkTheme.FONT_REGULAR, DarkTheme.TEXT_WHITE);
            nameLbl.setPreferredSize(new Dimension(200, 30));

            // Progress bar track
            JPanel track = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    // Track
                    g2.setColor(new Color(255, 255, 255, 15));
                    g2.fillRoundRect(0, 8, getWidth(), 12, 6, 6);
                    // Fill
                    int fillW = (int)(getWidth() * pct);
                    if (fillW > 0) {
                        GradientPaint gp = new GradientPaint(
                            0, 0, DarkTheme.ACCENT_BLUE.brighter(),
                            fillW, 0, DarkTheme.ACCENT_BLUE);
                        g2.setPaint(gp);
                        g2.fillRoundRect(0, 8, fillW, 12, 6, 6);
                        g2.setColor(new Color(255,255,255,50));
                        g2.fillRoundRect(0, 8, fillW, 6, 6, 6);
                    }
                    g2.dispose();
                }
            };
            track.setOpaque(false);

            // Count label
            JLabel countLbl = DarkTheme.label(
                count + " jobs",
                new Font("Segoe UI", Font.BOLD, 12),
                DarkTheme.ACCENT_BLUE);
            countLbl.setPreferredSize(new Dimension(60, 30));

            row.add(nameLbl,  BorderLayout.WEST);
            row.add(track,    BorderLayout.CENTER);
            row.add(countLbl, BorderLayout.EAST);

            panel.add(row);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }
}