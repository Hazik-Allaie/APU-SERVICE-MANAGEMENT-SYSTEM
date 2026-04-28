package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Manager_ManageStaffDialog — View, add, edit and delete all staff.
 */
public class Manager_ManageStaffDialog {

    private final JFrame          parent;
    private DefaultTableModel     model;
    private JTable                table;
    private JTextField            searchField;
    private String                activeRole = "All";

    public Manager_ManageStaffDialog(JFrame parent) {
        this.parent = parent;
        JDialog dialog = UITheme.createDialog(parent, "Staff Management", 960, 600);
        dialog.setLayout(new BorderLayout());
        dialog.add(UITheme.dialogHeader("Staff Management"), BorderLayout.NORTH);
        dialog.add(buildCenter(),        BorderLayout.CENTER);
        dialog.add(buildButtons(dialog), BorderLayout.SOUTH);
        loadData("All");
        dialog.setVisible(true);
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Role filter tabs
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        for (String role : new String[]{"All", "Manager", "CounterStaff", "Technician"}) {
            JToggleButton tb = filterTab(role);
            if (role.equals("All")) tb.setSelected(true);
            tb.addActionListener(e -> { activeRole = role; loadData(role); });
            bg.add(tb); filterRow.add(tb);
        }

        // Search + Add
        JPanel actionRow = new JPanel(new BorderLayout(10, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, UITheme.INPUT_H));
        UITheme.placeholder(searchField, "Search by name, ID or username…");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filterByQuery(searchField.getText()); }
        });
        JButton addBtn = UITheme.createPrimaryButton("+ Add Staff");
        addBtn.setPreferredSize(new Dimension(140, UITheme.INPUT_H));
        addBtn.addActionListener(e -> showAddDialog());
        actionRow.add(searchField, BorderLayout.WEST);
        actionRow.add(addBtn,      BorderLayout.EAST);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(filterRow); top.add(actionRow);

        // Table
        String[] cols = {"Staff ID", "Name", "Age", "Email", "Username", "Contact", "Role"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        int[] widths = {90, 155, 50, 185, 130, 120, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Colour-code Role column
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, f, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!sel) {
                    setBackground(row%2==0 ? UITheme.WHITE : UITheme.TABLE_ROW_ALT);
                    String role = v == null ? "" : v.toString();
                    setForeground(switch (role) {
                        case "Manager"      -> new Color(37, 82, 148);
                        case "CounterStaff" -> new Color(16, 150, 100);
                        case "Technician"   -> new Color(180, 100, 20);
                        default             -> UITheme.TEXT_PRIMARY;
                    });
                } else {
                    setBackground(UITheme.TABLE_SELECTED);
                    setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });

        p.add(top,                             BorderLayout.NORTH);
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
            String id   = (String) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            if (JOptionPane.showConfirmDialog(dialog,
                    "Delete staff \"" + name + "\" (ID: " + id + ")?\nThis cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                FileHandler.deleteuser(id);
                JOptionPane.showMessageDialog(parent, "Staff deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(activeRole);
            }
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        return UITheme.buttonRow(editBtn, deleteBtn, closeBtn);
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadData(String role) {
        model.setRowCount(0);
        for (User u : FileHandler.getallusers()) {
            if (u.getRole().equals("Customer")) continue;
            if (!role.equals("All") && !u.getRole().equals(role)) continue;
            model.addRow(row(u));
        }
    }

    private void filterByQuery(String q) {
        model.setRowCount(0);
        String lq = q.toLowerCase();
        for (User u : FileHandler.getallusers()) {
            if (u.getRole().equals("Customer")) continue;
            if (!activeRole.equals("All") && !u.getRole().equals(activeRole)) continue;
            if (u.getName().toLowerCase().contains(lq)
                || u.getUserid().toLowerCase().contains(lq)
                || u.getUsername().toLowerCase().contains(lq))
                model.addRow(row(u));
        }
    }

    private Object[] row(User u) {
        return new Object[]{u.getUserid(), u.getName(), u.getAge(),
            u.getEmail(), u.getUsername(), u.getContact(), u.getRole()};
    }

    // ── Add dialog ────────────────────────────────────────────────────────────

    private void showAddDialog() {
        JDialog d = UITheme.createDialog(parent, "Add New Staff", 460, 580);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Add New Staff"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = UITheme.formGbc();

        JComboBox<String> fRole = UITheme.createComboBox(
            new String[]{"Manager", "CounterStaff", "Technician"});
        JTextField     fId = tf(), fName = tf(), fAge = tf(),
                       fEmail = tf(), fUsername = tf(), fContact = tf();
        JPasswordField fPass = pf();

        UITheme.formRow(form, g, 0, "Role",      fRole);
        UITheme.formRow(form, g, 1, "Staff ID",  fId);
        UITheme.formRow(form, g, 2, "Full Name", fName);
        UITheme.formRow(form, g, 3, "Age",       fAge);
        UITheme.formRow(form, g, 4, "Email",     fEmail);
        UITheme.formRow(form, g, 5, "Username",  fUsername);
        UITheme.formRow(form, g, 6, "Password",  fPass);
        UITheme.formRow(form, g, 7, "Contact",   fContact);

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        g.gridx=0; g.gridy=16; g.gridwidth=2; form.add(status, g);

        JButton save = UITheme.createPrimaryButton("Save");
        JButton cancel = UITheme.createOutlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            if (fId.getText().isBlank()||fName.getText().isBlank()||fAge.getText().isBlank()
                ||fEmail.getText().isBlank()||fUsername.getText().isBlank()||fContact.getText().isBlank()) {
                status.setText("All fields are required."); return;
            }
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { status.setText("Age must be a number."); return; }
            String uid = fId.getText().trim();
            for (User u : FileHandler.getallusers()) {
                if (u.getUserid().equals(uid)) { status.setText("Staff ID already exists."); return; }
            }
            String role = (String) fRole.getSelectedItem();
            String nm   = fName.getText().trim(), em = fEmail.getText().trim();
            String un   = fUsername.getText().trim(), pw = new String(fPass.getPassword());
            String ct   = fContact.getText().trim();
            User newStaff = switch (role) {
                case "Manager"      -> new Manager(uid, nm, age, em, un, pw, ct);
                case "CounterStaff" -> new CounterStaff(uid, nm, age, em, un, pw, ct);
                default             -> new Technician(uid, nm, age, em, un, pw, ct);
            };
            FileHandler.saveuser(newStaff);
            JOptionPane.showMessageDialog(parent, "Staff added successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            d.dispose(); loadData(activeRole);
        });

        d.add(UITheme.formScrollPane(form), BorderLayout.CENTER);
        d.add(UITheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────

    private void showEditDialog(String staffId) {
        User existing = null;
        for (User u : FileHandler.getallusers())
            if (u.getUserid().equals(staffId)) { existing = u; break; }
        if (existing == null) return;
        final User u = existing;

        JDialog d = UITheme.createDialog(parent, "Edit Staff", 460, 520);
        d.setLayout(new BorderLayout());
        d.add(UITheme.dialogHeader("Edit Staff"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));
        GridBagConstraints g = UITheme.formGbc();

        JTextField fRole = tf(u.getRole());
        fRole.setEditable(false); fRole.setBackground(new Color(245, 245, 248));
        JTextField     fName = tf(u.getName()), fAge = tf(String.valueOf(u.getAge()));
        JTextField     fEmail = tf(u.getEmail()), fUsername = tf(u.getUsername());
        JTextField     fContact = tf(u.getContact());
        JPasswordField fPass = pf();

        UITheme.formRow(form, g, 0, "Role (locked)",  fRole);
        UITheme.formRow(form, g, 1, "Full Name",      fName);
        UITheme.formRow(form, g, 2, "Age",            fAge);
        UITheme.formRow(form, g, 3, "Email",          fEmail);
        UITheme.formRow(form, g, 4, "Username",       fUsername);
        UITheme.formRow(form, g, 5, "New Password",   fPass);
        UITheme.formRow(form, g, 6, "Contact",        fContact);

        JLabel hint = UITheme.createLabel("Leave password blank to keep current.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        g.gridx=0; g.gridy=14; g.gridwidth=2; form.add(hint, g);

        JLabel status = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        g.gridy=15; form.add(status, g);

        JButton save = UITheme.createPrimaryButton("Save Changes");
        JButton cancel = UITheme.createOutlineButton("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            int age; try { age = Integer.parseInt(fAge.getText().trim()); }
            catch (NumberFormatException ex) { status.setText("Age must be a number."); return; }
            String np = new String(fPass.getPassword());
            if (np.isEmpty()) np = u.getPassword();
            String nm = fName.getText().trim(), em = fEmail.getText().trim();
            String un = fUsername.getText().trim(), ct = fContact.getText().trim();
            User updated = switch (u.getRole()) {
                case "Manager"      -> new Manager(staffId, nm, age, em, un, np, ct);
                case "CounterStaff" -> new CounterStaff(staffId, nm, age, em, un, np, ct);
                default             -> new Technician(staffId, nm, age, em, un, np, ct);
            };
            FileHandler.updateUser(updated);
            JOptionPane.showMessageDialog(parent, "Staff updated successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            d.dispose(); loadData(activeRole);
        });

        d.add(UITheme.formScrollPane(form), BorderLayout.CENTER);
        d.add(UITheme.buttonRow(cancel, save), BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTextField tf()         { return UITheme.createTextField(); }
    private JTextField tf(String v) { JTextField f = tf(); f.setText(v); return f; }
    private JPasswordField pf()     { return UITheme.createPasswordField(); }

    private JToggleButton filterTab(String text) {
        JToggleButton tb = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.RADIUS, UITheme.RADIUS);
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(getModel().isRollover() ? UITheme.ACCENT_LIGHT : UITheme.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.RADIUS, UITheme.RADIUS);
                    g2.setColor(UITheme.ACCENT);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, UITheme.RADIUS, UITheme.RADIUS);
                }
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        tb.setFont(UITheme.FONT_BOLD);
        tb.setPreferredSize(new Dimension(120, 32));
        tb.setBorderPainted(false); tb.setFocusPainted(false); tb.setContentAreaFilled(false);
        tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tb;
    }

    private void noSel() {
        JOptionPane.showMessageDialog(parent, "Select a staff member from the table first.",
            "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }
}
