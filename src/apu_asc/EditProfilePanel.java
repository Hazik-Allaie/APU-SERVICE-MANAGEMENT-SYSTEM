package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * EditProfilePanel — Counter Staff edits their own profile.
 *
 * Displays current values pre-filled.
 * Requires current password before any change is saved.
 */
public class EditProfilePanel extends JPanel {

    private final CounterStaffFrame   frame;
    private final CounterStaff        user;
    private final CounterStaffService service = new CounterStaffService();

    // Form fields
    private JTextField     fName, fAge, fEmail, fUsername, fContact;
    private JPasswordField fCurrentPass, fNewPass;

    // Feedback
    private JLabel statusLabel;

    public EditProfilePanel(CounterStaffFrame frame, CounterStaff user) {
        this.frame = frame;
        this.user  = user;
        setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Edit Profile", "Update your personal information and password."),
            BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
    }

    // ── Form ──────────────────────────────────────────────────────────────────

    private JScrollPane buildForm() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(0, 36, 40, 36));

        // ── Profile card ───────────────────────────────────────────────────────
        UITheme.RoundedPanel profileCard = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        profileCard.setLayout(new BorderLayout());
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card header with avatar
        JPanel cardHeader = buildCardHeader();
        profileCard.add(cardHeader, BorderLayout.NORTH);
        profileCard.add(UITheme.hDivider(), BorderLayout.CENTER); // thin line

        // ── Personal info section ──────────────────────────────────────────────
        JPanel infoSection = new JPanel(new GridBagLayout());
        infoSection.setOpaque(false);
        infoSection.setBorder(BorderFactory.createEmptyBorder(20, 28, 8, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(8, 0, 8, 16);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        fName     = UITheme.createTextField(); fName.setText(user.getName());
        fAge      = UITheme.createTextField(); fAge.setText(String.valueOf(user.getAge()));
        fEmail    = UITheme.createTextField(); fEmail.setText(user.getEmail());
        fUsername = UITheme.createTextField(); fUsername.setText(user.getUsername());
        fContact  = UITheme.createTextField(); fContact.setText(user.getContact());

        addRow(infoSection, g, 0, "Full Name",  fName,     "Age",      fAge);
        addRow(infoSection, g, 1, "Email",      fEmail,    "Contact",  fContact);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        infoSection.add(UITheme.createLabel("Username", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.gridx = 1; g.weightx = 1;
        infoSection.add(fUsername, g);

        // Section divider
        JPanel sectionDivider = new JPanel();
        sectionDivider.setOpaque(false);
        sectionDivider.setBorder(BorderFactory.createEmptyBorder(8, 28, 0, 28));
        sectionDivider.setLayout(new BoxLayout(sectionDivider, BoxLayout.Y_AXIS));
        sectionDivider.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel passHeader = UITheme.createLabel("Change Password", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        passHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionDivider.add(UITheme.hDivider());
        sectionDivider.add(Box.createRigidArea(new Dimension(0, 16)));
        sectionDivider.add(passHeader);

        // ── Password section ───────────────────────────────────────────────────
        JPanel passSection = new JPanel(new GridBagLayout());
        passSection.setOpaque(false);
        passSection.setBorder(BorderFactory.createEmptyBorder(12, 28, 24, 28));

        GridBagConstraints gp = new GridBagConstraints();
        gp.insets  = new Insets(8, 0, 8, 16);
        gp.anchor  = GridBagConstraints.WEST;
        gp.fill    = GridBagConstraints.HORIZONTAL;

        fCurrentPass = UITheme.createPasswordField();
        fNewPass     = UITheme.createPasswordField();

        // Hint label
        JLabel hint = UITheme.createLabel(
            "Current password is required to save any changes.",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);

        gp.gridx = 0; gp.gridy = 0; gp.weightx = 0;
        passSection.add(UITheme.createLabel("Current Password *", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), gp);
        gp.gridx = 1; gp.weightx = 1;
        passSection.add(fCurrentPass, gp);

        gp.gridx = 2; gp.weightx = 0;
        passSection.add(UITheme.createLabel("New Password", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), gp);
        gp.gridx = 3; gp.weightx = 1;
        passSection.add(fNewPass, gp);

        gp.gridx = 0; gp.gridy = 1; gp.gridwidth = 4; gp.insets = new Insets(2, 0, 0, 0);
        passSection.add(hint, gp);

        profileCard.add(infoSection, BorderLayout.SOUTH); // overridden below

        // Rebuild card layout with all sections
        UITheme.RoundedPanel fullCard = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        fullCard.setLayout(new BoxLayout(fullCard, BoxLayout.Y_AXIS));
        fullCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        fullCard.add(cardHeader);
        fullCard.add(UITheme.hDivider());
        fullCard.add(infoSection);
        fullCard.add(sectionDivider);
        fullCard.add(passSection);

        // ── Footer: status + save button ─────────────────────────────────────
        statusLabel = UITheme.createLabel(" ", UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = UITheme.createPrimaryButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(180, UITheme.BUTTON_H));
        saveBtn.setMaximumSize(new Dimension(180, UITheme.BUTTON_H));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> handleSave());

        JButton resetBtn = UITheme.createOutlineButton("Reset");
        resetBtn.setPreferredSize(new Dimension(120, UITheme.BUTTON_H));
        resetBtn.setMaximumSize(new Dimension(120, UITheme.BUTTON_H));
        resetBtn.addActionListener(e -> resetFields());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(saveBtn);
        btnRow.add(resetBtn);

        body.add(fullCard);
        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(statusLabel);
        body.add(Box.createRigidArea(new Dimension(0, 10)));
        body.add(btnRow);

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        return sp;
    }

    // ── Card header with avatar ───────────────────────────────────────────────

    private JPanel buildCardHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Large avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, 56, 56);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String init = user.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(init, (56 - fm.stringWidth(init)) / 2,
                        (56 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setMaximumSize(new Dimension(56, 56));
        avatar.setMinimumSize(new Dimension(56, 56));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));

        JLabel nameLabel = UITheme.createLabel(user.getName(), UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        JLabel roleLabel = UITheme.createLabel("Counter Staff  •  ID: " + user.getUserid(),
                UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(nameLabel);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(roleLabel);

        header.add(avatar);
        header.add(info);
        return header;
    }

    // ── Save handler ──────────────────────────────────────────────────────────

    private void handleSave() {
        String currentPass = new String(fCurrentPass.getPassword());
        if (currentPass.isEmpty()) {
            setStatus("Current password is required to save changes.", false);
            return;
        }

        String name     = fName.getText().trim();
        String email    = fEmail.getText().trim();
        String username = fUsername.getText().trim();
        String contact  = fContact.getText().trim();
        String newPass  = new String(fNewPass.getPassword());
        if (newPass.isEmpty()) newPass = user.getPassword();

        int age;
        try { age = Integer.parseInt(fAge.getText().trim()); }
        catch (NumberFormatException ex) {
            setStatus("Age must be a valid number.", false);
            return;
        }

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || contact.isEmpty()) {
            setStatus("Name, email, username, and contact cannot be empty.", false);
            return;
        }

        OperationResult res = service.updateProfile(
            user, name, age, email, username, currentPass, newPass, contact
        );

        if (res.getResult()) {
            setStatus("✓  " + res.getMessage(), true);
            fCurrentPass.setText("");
            fNewPass.setText("");
        } else {
            setStatus("✕  " + res.getMessage(), false);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void resetFields() {
        fName.setText(user.getName());
        fAge.setText(String.valueOf(user.getAge()));
        fEmail.setText(user.getEmail());
        fUsername.setText(user.getUsername());
        fContact.setText(user.getContact());
        fCurrentPass.setText("");
        fNewPass.setText("");
        setStatus("Fields reset to current values.", true);
    }

    private void setStatus(String msg, boolean success) {
        statusLabel.setForeground(success ? UITheme.SUCCESS : UITheme.ERROR);
        statusLabel.setText(msg);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row,
                        String lbl1, Component c1, String lbl2, Component c2) {
        g.weightx = 0; g.gridx = 0; g.gridy = row;
        p.add(UITheme.createLabel(lbl1, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.weightx = 1; g.gridx = 1;
        p.add(c1, g);
        g.weightx = 0; g.gridx = 2;
        p.add(UITheme.createLabel(lbl2, UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.weightx = 1; g.gridx = 3;
        p.add(c2, g);
    }
}
