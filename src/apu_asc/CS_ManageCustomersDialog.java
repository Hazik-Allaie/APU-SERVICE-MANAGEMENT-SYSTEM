package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * CS_ManageCustomersDialog — Customer CRUD for Counter Staff.
 */
public class CS_ManageCustomersDialog {

    private final JFrame              parent;
    private final CounterStaffService service = new CounterStaffService();
    private       DefaultTableModel   model;
    private       JTable              table;
    private       JTextField          searchField;

    public CS_ManageCustomersDialog(JFrame parent) {
        this.parent = parent;
        JDialog dialog = UITheme.createDialog(parent, "Customer Management", 900, 600);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Customer Management"), BorderLayout.NORTH);
        dialog.add(buildCenter(),          BorderLayout.CENTER);
        dialog.add(buildButtons(dialog),   BorderLayout.SOUTH);
        loadData();
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, UITheme.INPUT_H));
        UITheme.placeholder(searchField, "Search by name, ID or email…");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filter(searchField.getText()); }
        });

        JButton addBtn = UITheme.createPrimaryButton("+ Add Customer");
        addBtn.setPreferredSize(new Dimension(160, UITheme.INPUT_H));
        addBtn.addActionListener(e -> showAddDialog());

        topBar.add(searchField, BorderLayout.WEST);
        topBar.add(addBtn,      BorderLayout.EAST);

        String[] cols = {"Customer ID", "Name", "Age", "Email", "Username", "Contact"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        int[] widths = {110, 160, 55, 200, 140, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        p.add(topBar,                         BorderLayout.NORTH);
        p.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildButtons(JDialog dialog) {
        JButton editBtn   = UITheme.createOutlineButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        JButton closeBtn  = UITheme.createLinkButton("Close");

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { noSel(); return; }
            showEditDialog((String) model.getValueAt(row, 0));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { noSel(); return; }
            String id = (String) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            if (JOptionPane.showConfirmDialog(dialog,
                    "Delete customer \"" + name + "\"?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                OperationResult r = service.deleteCustomer(id);
                toast(r); if (r.getResult()) loadData();
            }
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(editBtn, deleteBtn, closeBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Customer c : service.getAllCustomers())
            model.addRow(new Object[]{c.getUserid(), c.getName(), c.getAge(),
                c.getEmail(), c.getUsername(), c.getContact()});
    }

    private void filter(String q) {
        model.setRowCount(0);
        String lq = q.toLowerCase();
        for (Customer c : service.getAllCustomers())
            if (c.getName().toLowerCase().contains(lq)
                || c.getUserid().toLowerCase().contains(lq)
                || c.getEmail().toLowerCase().contains(lq))
                model.addRow(new Object[]{c.getUserid(), c.getName(), c.getAge(),
                    c.getEmail(), c.getUsername(), c.getContact()});
    }

    private void showAddDialog() {
        JDialog d = UITheme.createDialog(parent, "Add New Customer", 460, 520);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Add New Customer"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = UITheme.formGbc();

        JTextField     fName = tf(), fAge = tf(), fEmail = tf(), fUsername = tf(), fContact = tf();
        JPasswordField fPass = pf();

        UITheme.formRow(form, g, 0, "Full Name",  fName);
        UITheme.formRow(form, g, 1, "Age",        fAge);
        UITheme.formRow(form, g, 2, "Email",      fEmail);
        UITheme.formRow(form, g, 3, "Username",   fUsername);
        UITheme.formRow(form, g, 4, "Password",   fPass);
        UITheme.formRow(form, g, 5, "Contact",    fContact);

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        g.gridx=0; g.gridy=12; g.gridwidth=2; form.add(status, g);

        JButton save = UITheme.createPrimaryButton("Save");
        JButton cancel = UITheme.createOutlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            if (fName.getText().isBlank()||fAge.getText().isBlank()||fEmail.getText().isBlank()
                ||fUsername.getText().isBlank()||fContact.getText().isBlank()) {
                status.setText("All fields are required."); return;
            }
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { status.setText("Age must be a number."); return; }
            OperationResult r = service.createCustomer(fName.getText().trim(), age,
                fEmail.getText().trim(), fUsername.getText().trim(),
                new String(fPass.getPassword()), fContact.getText().trim());
            toast(r); if (r.getResult()) { d.dispose(); loadData(); }
            else status.setText(r.getMessage());
        });

        d.add(UITheme.formScrollPane(form), BorderLayout.CENTER);
        d.add(UITheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showEditDialog(String id) {
        Customer c = service.searchCustomerById(id);
        if (c == null) return;

        JDialog d = UITheme.createDialog(parent, "Edit Customer", 460, 460);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Edit Customer"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = UITheme.formGbc();

        JTextField fName = tf(c.getName()), fAge = tf(String.valueOf(c.getAge())),
                   fEmail = tf(c.getEmail()), fContact = tf(c.getContact());
        JTextField fUser = tf(c.getUsername());
        fUser.setEditable(false); fUser.setBackground(new Color(245,245,248));

        UITheme.formRow(form, g, 0, "Full Name",        fName);
        UITheme.formRow(form, g, 1, "Age",              fAge);
        UITheme.formRow(form, g, 2, "Email",            fEmail);
        UITheme.formRow(form, g, 3, "Contact",          fContact);
        UITheme.formRow(form, g, 4, "Username (locked)", fUser);

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        g.gridx=0; g.gridy=10; g.gridwidth=2; form.add(status, g);

        JButton save = UITheme.createPrimaryButton("Save Changes");
        JButton cancel = UITheme.createOutlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { status.setText("Age must be a number."); return; }
            OperationResult r = service.updateCustomer(id, fName.getText().trim(), age,
                fEmail.getText().trim(), fContact.getText().trim());
            toast(r); if (r.getResult()) { d.dispose(); loadData(); }
            else status.setText(r.getMessage());
        });

        d.add(UITheme.formScrollPane(form), BorderLayout.CENTER);
        d.add(UITheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private JTextField tf()          { return styled(new JTextField()); }
    private JTextField tf(String v)  { JTextField f = tf(); f.setText(v); return f; }
    private JPasswordField pf()      { return (JPasswordField) styled(new JPasswordField()); }

    private JTextField styled(JTextField f) {
        f.setFont(UITheme.FONT_REGULAR); f.setForeground(UITheme.TEXT_PRIMARY);
        f.setBackground(UITheme.WHITE); f.setCaretColor(UITheme.ACCENT);
        UITheme.setInputBorder(f, UITheme.BORDER);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { UITheme.setInputBorder(f, UITheme.BORDER_FOCUS); }
            @Override public void focusLost(FocusEvent e)   { UITheme.setInputBorder(f, UITheme.BORDER); }
        });
        return f;
    }

    private void noSel() {
        JOptionPane.showMessageDialog(parent, "Select a customer from the table first.",
            "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }
    private void toast(OperationResult r) {
        JOptionPane.showMessageDialog(parent, r.getMessage(),
            r.getResult() ? "Success" : "Error",
            r.getResult() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}
