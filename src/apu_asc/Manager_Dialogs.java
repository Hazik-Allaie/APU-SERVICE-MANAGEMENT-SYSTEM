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
        JDialog dialog = UITheme.createDialog(parent, "Set Service Prices", 500, 320);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Set Service Prices"), BorderLayout.NORTH);
        dialog.add(buildBody(dialog),                          BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildBody(JDialog dialog) {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 10, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 16); g.anchor = GridBagConstraints.WEST;

        Double[] prices = service.getPrices();

        JTextField fNormal = UITheme.createTextField();
        fNormal.setText(String.format("%.2f", prices[0]));
        fNormal.setPreferredSize(new Dimension(280, UITheme.INPUT_H));

        JTextField fMajor = UITheme.createTextField();
        fMajor.setText(String.format("%.2f", prices[1]));
        fMajor.setPreferredSize(new Dimension(280, UITheme.INPUT_H));

        g.gridx=0; g.gridy=0; g.weightx=0;
        body.add(UITheme.createLabel("Normal Service (RM)", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1; body.add(fNormal, g);
        g.gridx=2; g.weightx=0;
        body.add(badge("Current: RM " + String.format("%.2f", prices[0])), g);

        JSeparator sep = new JSeparator(); sep.setForeground(UITheme.BORDER);
        g.gridx=0; g.gridy=1; g.gridwidth=3; g.insets=new Insets(4,0,4,0);
        body.add(sep, g); g.gridwidth=1; g.insets=new Insets(8,0,8,16);

        g.gridx=0; g.gridy=2; g.weightx=0;
        body.add(UITheme.createLabel("Major Service (RM)", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=1; g.weightx=1; body.add(fMajor, g);
        g.gridx=2; g.weightx=0;
        body.add(badge("Current: RM " + String.format("%.2f", prices[1])), g);

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=3; g.gridwidth=3; g.insets=new Insets(2,0,0,0);
        body.add(status, g);

        JButton save   = UITheme.createPrimaryButton("Save Prices");
        JButton cancel = UITheme.createLinkButton("Cancel");

        save.addActionListener(e -> {
            double normal, major;
            try {
                normal = Double.parseDouble(fNormal.getText().trim());
                major  = Double.parseDouble(fMajor.getText().trim());
            } catch (NumberFormatException ex) {
                status.setForeground(UITheme.ERROR);
                status.setText("Please enter valid numeric values."); return;
            }
            OperationResult r = service.savePrices(normal, major);
            if (r.getResult()) {
                JOptionPane.showMessageDialog(dialog,
                    "Prices saved!\nNormal: RM " + String.format("%.2f", normal)
                    + "  |  Major: RM " + String.format("%.2f", major),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                status.setForeground(UITheme.ERROR);
                status.setText(r.getMessage());
            }
        });
        cancel.addActionListener(e -> dialog.dispose());

        g.gridx=0; g.gridy=4; g.gridwidth=3; g.insets=new Insets(0,0,0,0);
        body.add(UITheme.buttonRow(cancel, save), g);
        return body;
    }

    private JPanel badge(String text) {
        JPanel p = new JPanel();
        p.setBackground(UITheme.ACCENT_LIGHT);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 240)),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        p.add(UITheme.createLabel(text, UITheme.FONT_SMALL, UITheme.ACCENT));
        return p;
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  Manager_ViewFeedbacksDialog
// ══════════════════════════════════════════════════════════════════════════════

class Manager_ViewFeedbacksDialog {

    private final ManagerService  service = new ManagerService();
    private DefaultTableModel     model;

    public Manager_ViewFeedbacksDialog(JFrame parent) {
        JDialog dialog = UITheme.createDialog(parent, "Customer Feedbacks", 880, 560);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Customer Feedbacks"), BorderLayout.NORTH);
        dialog.add(buildCenter(),          BorderLayout.CENTER);
        dialog.add(buildButtons(dialog),   BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        filterRow.add(UITheme.createLabel("Filter by Status:", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY));

        JComboBox<String> statusFilter = UITheme.createComboBox(
            new String[]{"All", "Pending", "Completed"});
        statusFilter.setPreferredSize(new Dimension(160, UITheme.INPUT_H));
        statusFilter.addActionListener(e -> loadData((String) statusFilter.getSelectedItem()));
        filterRow.add(statusFilter);

        JLabel countLabel = UITheme.createLabel("", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        filterRow.add(Box.createRigidArea(new Dimension(12, 0)));
        filterRow.add(countLabel);

        String[] cols = {"Appointment ID", "Customer ID", "Technician ID", "Date", "Status", "Comments"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowHeight(48);

        int[] widths = {120, 110, 110, 100, 100, 280};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int row, int col) {
                JTextArea area = new JTextArea(v == null ? "" : v.toString());
                area.setFont(UITheme.FONT_REGULAR);
                area.setWrapStyleWord(true); area.setLineWrap(true);
                area.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
                area.setBackground(sel ? UITheme.TABLE_SELECTED
                    : row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                return area;
            }
        });

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, f, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!sel) {
                    setBackground(row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    setForeground("Completed".equals(v) ? UITheme.SUCCESS : UITheme.TEXT_SECONDARY);
                } else {
                    setBackground(UITheme.TABLE_SELECTED); setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });

        model.addTableModelListener(e ->
            countLabel.setText("Showing " + model.getRowCount() + " record(s)"));

        p.add(filterRow,                       BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton refresh = UITheme.createOutlineButton("Refresh");
        refresh.addActionListener(e -> loadData("All"));
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(refresh, close);
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
        JDialog dialog = UITheme.createDialog(parent, "Reports", 800, 600);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Reports & Statistics"), BorderLayout.NORTH);
        dialog.add(buildBody(),                                  BorderLayout.CENTER);
        JButton close = UITheme.createLinkButton("Close");
        close.addActionListener(e -> dialog.dispose());
        dialog.add(UITheme.buttonRow(close), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane buildBody() {
        int[]    apptStats    = service.getAppointmentStats();
        double[] revenueStats = service.getRevenueStats();
        int[]    svcCounts    = service.getServiceTypeCounts();
        int[]    userCounts   = service.getUserCounts();

        JPanel body = new JPanel();
        body.setBackground(UITheme.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        body.add(sectionLabel("Appointment Summary"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel row1 = cardRow();
        row1.add(statCard("Total Appointments", String.valueOf(apptStats[0]), new Color(37, 82, 148)));
        row1.add(statCard("Completed",          String.valueOf(apptStats[1]), new Color(22, 163, 74)));
        row1.add(statCard("Pending",            String.valueOf(apptStats[2]), new Color(180, 100, 20)));
        body.add(row1);

        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(sectionLabel("Revenue Summary"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel row2 = cardRow();
        row2.add(statCard("Total Revenue",
            "RM " + String.format("%.2f", revenueStats[0]), new Color(37, 82, 148)));
        row2.add(statCard("Normal Service",
            "RM " + String.format("%.2f", revenueStats[1]) + " (" + svcCounts[0] + ")",
            new Color(16, 150, 100)));
        row2.add(statCard("Major Service",
            "RM " + String.format("%.2f", revenueStats[2]) + " (" + svcCounts[1] + ")",
            new Color(139, 92, 246)));
        body.add(row2);

        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(sectionLabel("System Users"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel row3 = new JPanel(new GridLayout(1, 4, 16, 0));
        row3.setOpaque(false); row3.setAlignmentX(Component.LEFT_ALIGNMENT);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row3.add(statCard("Managers",      String.valueOf(userCounts[0]), new Color(37, 82, 148)));
        row3.add(statCard("Counter Staff", String.valueOf(userCounts[1]), new Color(16, 150, 100)));
        row3.add(statCard("Technicians",   String.valueOf(userCounts[2]), new Color(180, 100, 20)));
        row3.add(statCard("Customers",     String.valueOf(userCounts[3]), new Color(139, 92, 246)));
        body.add(row3);

        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(sectionLabel("Appointment Breakdown"));
        body.add(Box.createRigidArea(new Dimension(0, 12)));

        String[] cols = {"Appointment ID", "Customer", "Technician", "Service", "Date", "Status", "Amount (RM)"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Appointment a : service.getAllAppointmentsForReport())
            tm.addRow(new Object[]{a.getAppointmentid(), a.getCustomerid(), a.getTechnicianid(),
                a.getServicetype(), a.getDate(), a.getStatus(),
                String.format("%.2f", a.getPrice())});

        JTable table = new JTable(tm);
        UITheme.styleTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(700, 160));
        int[] widths = {110, 100, 110, 80, 90, 90, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane tableScroll = UITheme.createScrollPane(table);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        body.add(tableScroll);

        JScrollPane outer = new JScrollPane(body);
        outer.setBorder(null); outer.getViewport().setBackground(UITheme.WHITE);
        return outer;
    }

    private JPanel cardRow() {
        JPanel p = new JPanel(new GridLayout(1, 3, 16, 0));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16))));
        card.add(UITheme.createLabel(value, new Font("Segoe UI", Font.BOLD, 18), accent), BorderLayout.CENTER);
        card.add(UITheme.createLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), BorderLayout.SOUTH);
        return card;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = UITheme.createLabel(text, UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 0, 6, 0)));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return l;
    }
}
