package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;

/**
 * ManageCustomersPanel — CRUD operations for Customer records.
 *
 * Layout:
 *   NORTH  — page header + search bar + Add button
 *   CENTER — scrollable customer table
 *   SOUTH  — action buttons (Edit, Delete) activated on row selection
 */
public class ManageCustomersPanel extends JPanel {

    private final CounterStaffFrame   frame;
    private final CounterStaffService service = new CounterStaffService();

    // Table
    private DefaultTableModel tableModel;
    private JTable            table;

    // Search
    private JTextField searchField;

    // Form dialog fields (reused for Add & Edit)
    private JTextField fName, fAge, fEmail, fUsername, fPassword, fContact;

    public ManageCustomersPanel(CounterStaffFrame frame, CounterStaff user) {
        this.frame = frame;
        setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildTable(),    BorderLayout.CENTER);
        add(buildActionBar(), BorderLayout.SOUTH);
    }

    // ── Top bar: header + search + add ────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(30, 36, 16, 36));

        // Left: title block
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = UITheme.createLabel("Customers", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel sub   = UITheme.createLabel("View and manage all registered customers.",
                UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBlock.add(sub);

        // Right: search + add button
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(220, UITheme.INPUT_H));
        searchField.setMaximumSize(new Dimension(220, UITheme.INPUT_H));
        // Placeholder text
        setPlaceholder(searchField, "Search by name or ID…");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filterTable(searchField.getText()); }
        });

        JButton addBtn = UITheme.createPrimaryButton("+ Add Customer");
        addBtn.setPreferredSize(new Dimension(150, UITheme.INPUT_H));
        addBtn.setMaximumSize(new Dimension(150, UITheme.INPUT_H));
        addBtn.addActionListener(e -> showAddDialog());

        controls.add(searchField);
        controls.add(Box.createRigidArea(new Dimension(12, 0)));
        controls.add(addBtn);

        bar.add(titleBlock, BorderLayout.WEST);
        bar.add(controls,   BorderLayout.EAST);
        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JScrollPane buildTable() {
        String[] cols = { "Customer ID", "Name", "Age", "Email", "Username", "Contact" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Column widths
        int[] widths = { 110, 170, 60, 200, 150, 130 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Alternating row colours via custom renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (sel) {
                    setBackground(UITheme.TABLE_SELECTED);
                    setForeground(UITheme.TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });

        JScrollPane sp = UITheme.createScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 36, 0, 36));
        loadTableData();
        return sp;
    }

    // ── Action bar ────────────────────────────────────────────────────────────

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 36, 20, 36));

        JButton editBtn   = UITheme.createOutlineButton("✏  Edit");
        JButton deleteBtn = UITheme.createDangerButton("🗑  Delete");
        editBtn.setPreferredSize(new Dimension(120, UITheme.BUTTON_H));
        deleteBtn.setPreferredSize(new Dimension(120, UITheme.BUTTON_H));

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            showEditDialog((String) tableModel.getValueAt(row, 0));
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showNoSelection(); return; }
            String id   = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Delete customer \"" + name + "\" (ID: " + id + ")?\nThis cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                OperationResult res = service.deleteCustomer(id);
                showResultToast(res);
                loadTableData();
            }
        });

        bar.add(editBtn);
        bar.add(deleteBtn);
        return bar;
    }

    // ── Data loading / filtering ──────────────────────────────────────────────

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (Customer c : service.getAllCustomers()) {
            tableModel.addRow(new Object[]{
                c.getUserid(), c.getName(), c.getAge(),
                c.getEmail(), c.getUsername(), c.getContact()
            });
        }
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0);
        String q = query.toLowerCase();
        for (Customer c : service.getAllCustomers()) {
            if (c.getName().toLowerCase().contains(q)
                || c.getUserid().toLowerCase().contains(q)
                || c.getEmail().toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{
                    c.getUserid(), c.getName(), c.getAge(),
                    c.getEmail(), c.getUsername(), c.getContact()
                });
            }
        }
    }

    // ── Add dialog ────────────────────────────────────────────────────────────

    private void showAddDialog() {
        JDialog dialog = buildDialog("Add New Customer");

        JPanel form = buildFormPanel(
                new String[]{"Full Name", "Age", "Email", "Username", "Password", "Contact"},
                null
        );

        JButton save = UITheme.createPrimaryButton("Save Customer");
        save.addActionListener(e -> {
            if (!validateForm()) return;
            OperationResult res = service.createCustomer(
                fName.getText().trim(),
                parseAge(fAge.getText()),
                fEmail.getText().trim(),
                fUsername.getText().trim(),
                fPassword.getText().trim(),
                fContact.getText().trim()
            );
            showResultToast(res);
            if (res.getResult()) { dialog.dispose(); loadTableData(); }
        });

        layoutDialog(dialog, form, save);
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────

    private void showEditDialog(String customerId) {
        Customer c = service.searchCustomerById(customerId);
        if (c == null) return;

        JDialog dialog = buildDialog("Edit Customer");

        JPanel form = buildFormPanel(
            new String[]{"Full Name", "Age", "Email", "Username", "Password (locked)", "Contact"},
            new String[]{ c.getName(), String.valueOf(c.getAge()), c.getEmail(),
                          c.getUsername(), "••••••••", c.getContact() }
        );
        fPassword.setEditable(false);
        fPassword.setBackground(UITheme.TABLE_HEADER_BG);

        JButton save = UITheme.createPrimaryButton("Save Changes");
        save.addActionListener(e -> {
            OperationResult res = service.updateCustomer(
                customerId,
                fName.getText().trim(),
                parseAge(fAge.getText()),
                fEmail.getText().trim(),
                fContact.getText().trim()
            );
            showResultToast(res);
            if (res.getResult()) { dialog.dispose(); loadTableData(); }
        });

        layoutDialog(dialog, form, save);
    }

    // ── Dialog builder helpers ────────────────────────────────────────────────

    private JDialog buildDialog(String title) {
        JDialog d = new JDialog(frame, title, true);
        d.setSize(440, 520);
        d.setLocationRelativeTo(frame);
        d.setResizable(false);
        return d;
    }

    private JPanel buildFormPanel(String[] labels, String[] defaults) {
        JPanel p = new JPanel();
        p.setBackground(UITheme.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(28, 32, 10, 32));

        JTextField[] fields = { null, null, null, null, null, null };
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = UITheme.createLabel(labels[i], UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField tf = i == 4
                ? UITheme.createPasswordField()
                : UITheme.createTextField();
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.INPUT_H));
            tf.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (defaults != null) tf.setText(defaults[i]);
            fields[i] = tf;

            p.add(lbl);
            p.add(Box.createRigidArea(new Dimension(0, 6)));
            p.add(tf);
            p.add(Box.createRigidArea(new Dimension(0, 16)));
        }

        // Map to instance fields
        fName     = fields[0];
        fAge      = fields[1];
        fEmail    = fields[2];
        fUsername = fields[3];
        fPassword = (JPasswordField) fields[4];
        fContact  = fields[5];

        return p;
    }

    private void layoutDialog(JDialog dialog, JPanel form, JButton actionBtn) {
        JButton cancelBtn = UITheme.createOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(UITheme.WHITE);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 32, 24, 32));
        btnRow.add(cancelBtn);
        btnRow.add(actionBtn);

        dialog.getContentPane().setBackground(UITheme.WHITE);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(form) {{
            setBorder(null);
            getViewport().setBackground(UITheme.WHITE);
        }}, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean validateForm() {
        if (fName.getText().isBlank() || fAge.getText().isBlank()
                || fEmail.getText().isBlank() || fUsername.getText().isBlank()
                || fContact.getText().isBlank()) {
            JOptionPane.showMessageDialog(frame, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try { Integer.parseInt(fAge.getText().trim()); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Age must be a number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private int parseAge(String s) {
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return 0; }
    }

    private void showNoSelection() {
        JOptionPane.showMessageDialog(frame, "Please select a customer from the table first.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showResultToast(OperationResult res) {
        JOptionPane.showMessageDialog(frame, res.getMessage(),
                res.getResult() ? "Success" : "Error",
                res.getResult() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
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
