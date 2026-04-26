package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;

public class ManageCustomersDialog {

    private final JFrame             parent;
    private final CounterStaffService service = new CounterStaffService();
    private       DefaultTableModel  model;
    private       JTable             table;
    private       JTextField         searchField;

    public ManageCustomersDialog(JFrame parent) {
        this.parent = parent;
        JDialog dialog = UITheme.createDialog(parent, "Customer Management", 900, 600);
        dialog.setLayout(new BorderLayout());

        dialog.add(UITheme.dialogHeader("Customer Management"), BorderLayout.NORTH);
        dialog.add(buildCenter(),                               BorderLayout.CENTER);
        dialog.add(buildButtonRow(dialog),                      BorderLayout.SOUTH);

        loadData();
        dialog.setVisible(true);
    }

    // ── Centre: search bar + table ────────────────────────────────────────────

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Summary row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel countLabel = UITheme.createLabel("", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
        countLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)));

        // Search bar
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        searchRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, UITheme.INPUT_H));
        placeholder(searchField, "Search by name, ID or email…");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filter(searchField.getText()); }
        });

        JButton addBtn = UITheme.createPrimaryButton("+ Add Customer");
        addBtn.setPreferredSize(new Dimension(160, UITheme.INPUT_H));
        addBtn.addActionListener(e -> showAddDialog());

        searchRow.add(searchField, BorderLayout.WEST);
        searchRow.add(addBtn,      BorderLayout.EAST);

        // Table
        String[] cols = {"Customer ID", "Name", "Age", "Email", "Username", "Contact"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        int[] widths = {110, 160, 55, 200, 140, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane sp = UITheme.createScrollPane(table);

        // Update count when data loads
        model.addTableModelListener(e2 -> countLabel.setText(
            "Total Records: " + model.getRowCount()));

        statsRow.add(countLabel);

        p.add(statsRow,   BorderLayout.NORTH);
        p.add(searchRow,  BorderLayout.NORTH); // replaced below
        // Use BoxLayout for cleaner stacking
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(statsRow);
        stack.add(Box.createRigidArea(new Dimension(0, 10)));
        stack.add(searchRow);

        p.removeAll();
        p.add(stack, BorderLayout.NORTH);
        p.add(sp,    BorderLayout.CENTER);
        return p;
    }

    // ── Bottom button row ─────────────────────────────────────────────────────

    private JPanel buildButtonRow(JDialog dialog) {
        JButton editBtn   = UITheme.createOutlineButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        JButton closeBtn  = UITheme.createLinkButton("Close");

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { noSelection(); return; }
            showEditDialog((String) model.getValueAt(row, 0));
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { noSelection(); return; }
            String id   = (String) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            int ok = JOptionPane.showConfirmDialog(dialog,
                "Delete customer \"" + name + "\" (ID: " + id + ")?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                OperationResult r = service.deleteCustomer(id);
                toast(r); if (r.getResult()) loadData();
            }
        });

        closeBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(editBtn, deleteBtn, closeBtn);
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadData() {
        model.setRowCount(0);
        for (Customer c : service.getAllCustomers())
            model.addRow(new Object[]{c.getUserid(), c.getName(), c.getAge(),
                c.getEmail(), c.getUsername(), c.getContact()});
    }

    private void filter(String q) {
        model.setRowCount(0);
        String lq = q.toLowerCase();
        for (Customer c : service.getAllCustomers()) {
            if (c.getName().toLowerCase().contains(lq)
                || c.getUserid().toLowerCase().contains(lq)
                || c.getEmail().toLowerCase().contains(lq))
                model.addRow(new Object[]{c.getUserid(), c.getName(), c.getAge(),
                    c.getEmail(), c.getUsername(), c.getContact()});
        }
    }

    // ── Add dialog ────────────────────────────────────────────────────────────

    private void showAddDialog() {
        JDialog d = UITheme.createDialog(parent, "Add New Customer", 480, 500);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Add New Customer"), BorderLayout.NORTH);

        JTextField fName, fAge, fEmail, fUsername, fContact;
        JPasswordField fPass;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = formGbc();

        fName     = addRow(form, g, 0, "Full Name",  new JTextField());
        fAge      = addRow(form, g, 1, "Age",        new JTextField());
        fEmail    = addRow(form, g, 2, "Email",      new JTextField());
        fUsername = addRow(form, g, 3, "Username",   new JTextField());
        fPass     = (JPasswordField) addRowRaw(form, g, 4, "Password",  new JPasswordField());
        fContact  = addRow(form, g, 5, "Contact",    new JTextField());

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        g.gridx=0; g.gridy=6; g.gridwidth=2;
        form.add(status, g);

        JButton save   = UITheme.createPrimaryButton("Save");
        JButton cancel = UITheme.createOutlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            if (fName.getText().isBlank() || fAge.getText().isBlank()
                || fEmail.getText().isBlank() || fUsername.getText().isBlank()
                || fContact.getText().isBlank()) {
                status.setText("All fields are required."); return;
            }
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { status.setText("Age must be a number."); return; }
            OperationResult r = service.createCustomer(fName.getText().trim(), age,
                fEmail.getText().trim(), fUsername.getText().trim(),
                new String(fPass.getPassword()), fContact.getText().trim());
            toast(r);
            if (r.getResult()) { d.dispose(); loadData(); }
            else status.setText(r.getMessage());
        });

        d.add(new JScrollPane(form) {{ setBorder(null); getViewport().setBackground(UITheme.WHITE); }}, BorderLayout.CENTER);
        d.add(UITheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────

    private void showEditDialog(String id) {
        Customer c = service.searchCustomerById(id);
        if (c == null) return;

        JDialog d = UITheme.createDialog(parent, "Edit Customer", 480, 460);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Edit Customer"), BorderLayout.NORTH);

        JTextField fName, fAge, fEmail, fContact;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = formGbc();

        fName    = addRow(form, g, 0, "Full Name", new JTextField(c.getName()));
        fAge     = addRow(form, g, 1, "Age",       new JTextField(String.valueOf(c.getAge())));
        fEmail   = addRow(form, g, 2, "Email",     new JTextField(c.getEmail()));
        fContact = addRow(form, g, 3, "Contact",   new JTextField(c.getContact()));

        // Read-only username
        JTextField fUser = new JTextField(c.getUsername());
        fUser.setEditable(false); fUser.setBackground(new Color(245,245,248));
        addRowRaw(form, g, 4, "Username (locked)", fUser);

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        g.gridx=0; g.gridy=5; g.gridwidth=2; form.add(status, g);

        JButton save   = UITheme.createPrimaryButton("Save Changes");
        JButton cancel = UITheme.createOutlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { status.setText("Age must be a number."); return; }
            OperationResult r = service.updateCustomer(id, fName.getText().trim(), age,
                fEmail.getText().trim(), fContact.getText().trim());
            toast(r);
            if (r.getResult()) { d.dispose(); loadData(); }
            else status.setText(r.getMessage());
        });

        d.add(new JScrollPane(form) {{ setBorder(null); getViewport().setBackground(UITheme.WHITE); }}, BorderLayout.CENTER);
        d.add(UITheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ── Shared form helpers ───────────────────────────────────────────────────

    private JTextField addRow(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        styleInputField(field);
        addRowRaw(p, g, row, label, field);
        return field;
    }

    private JComponent addRowRaw(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        if (field instanceof JTextField) styleInputField((JTextField) field);
        g.gridx=0; g.gridy=row*2;     g.gridwidth=2; g.weightx=1;
        p.add(UITheme.createLabel(label, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx=0; g.gridy=row*2+1;   g.gridwidth=2; g.weightx=1;
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.INPUT_H));
        if (field instanceof JTextField) ((JTextField)field).setPreferredSize(new Dimension(400, UITheme.INPUT_H));
        p.add(field, g);
        // Spacing
        g.gridx=0; g.gridy=row*2+2;
        p.add(Box.createRigidArea(new Dimension(0,6)), g);
        return field;
    }

    private void styleInputField(JTextField f) {
        f.setFont(UITheme.FONT_REGULAR); f.setForeground(UITheme.TEXT_PRIMARY);
        f.setBackground(UITheme.WHITE); f.setCaretColor(UITheme.ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(4,10,4,10)));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_FOCUS),
                    BorderFactory.createEmptyBorder(4,10,4,10)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER),
                    BorderFactory.createEmptyBorder(4,10,4,10)));
            }
        });
    }

    private GridBagConstraints formGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        g.insets=new Insets(2,0,2,0); return g;
    }

    private void noSelection() {
        JOptionPane.showMessageDialog(parent, "Please select a customer from the table first.",
            "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toast(OperationResult r) {
        JOptionPane.showMessageDialog(parent, r.getMessage(),
            r.getResult() ? "Success" : "Error",
            r.getResult() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void placeholder(JTextField f, String hint) {
        f.setForeground(UITheme.TEXT_SECONDARY); f.setText(hint);
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
