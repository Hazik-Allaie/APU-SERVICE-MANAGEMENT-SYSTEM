package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Login_Frame — entry point for all roles.
 * Left panel: branding. Right panel: login form.
 * Routes to the correct role frame after authentication.
 */
public class Login_Frame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JButton        loginButton;

    public Login_Frame() {
        setTitle("APU-ASC  |  Sign In");
        setSize(880, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildBrand());
        root.add(buildForm());
        setContentPane(root);
        setVisible(true);
    }

    // ── Left branding panel ────────────────────────────────────────────────────

    private JPanel buildBrand() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(15, 30, 70),
                    getWidth(), getHeight(), new Color(30, 55, 120));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-40, -40, 220, 220);
                g2.fillOval(getWidth()-100, getHeight()-180, 260, 260);
                // Grid lines
                g2.setColor(new Color(255, 255, 255, 5));
                g2.setStroke(new BasicStroke(1f));
                for (int x = 0; x < getWidth(); x += 40) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40) g2.drawLine(0, y, getWidth(), y);
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel carIcon = new JLabel("🚗");
        carIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        carIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("APU-ASC");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 34));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = UITheme.createLabel("Automotive Service Centre",
            new Font("Segoe UI", Font.PLAIN, 15), new Color(160, 185, 220));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider
        JPanel div = new JPanel();
        div.setOpaque(true); div.setBackground(new Color(255,255,255,50));
        div.setMaximumSize(new Dimension(100, 1)); div.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = UITheme.createLabel(
            "<html><div style='text-align:center;width:180px;'>"
            + "Manage appointments,<br>customers and payments<br>with ease."
            + "</div></html>",
            UITheme.FONT_REGULAR, new Color(120, 155, 200));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(carIcon);
        inner.add(Box.createRigidArea(new Dimension(0, 14)));
        inner.add(appName);
        inner.add(Box.createRigidArea(new Dimension(0, 6)));
        inner.add(tagline);
        inner.add(Box.createRigidArea(new Dimension(0, 28)));
        inner.add(div);
        inner.add(Box.createRigidArea(new Dimension(0, 28)));
        inner.add(desc);

        panel.add(inner);
        return panel;
    }

    // ── Right form panel ───────────────────────────────────────────────────────

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(320, 360));

        JLabel title = UITheme.createLabel("Welcome back", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = UITheme.createLabel("Sign in to your account",
            UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel userLbl = UITheme.createLabel("Username", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = UITheme.createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.INPUT_H));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLbl = UITheme.createLabel("Password", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.INPUT_H));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addActionListener(e -> handleLogin());

        // Error label
        errorLabel = UITheme.createLabel(" ", UITheme.FONT_SMALL, UITheme.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        loginButton = UITheme.createPrimaryButton("Sign In");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.BUTTON_H));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        form.add(title);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(sub);
        form.add(Box.createRigidArea(new Dimension(0, 40)));
        form.add(userLbl);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(usernameField);
        form.add(Box.createRigidArea(new Dimension(0, 18)));
        form.add(passLbl);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(passwordField);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(errorLabel);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(loginButton);

        panel.add(form);
        return panel;
    }

    // ── Login logic ────────────────────────────────────────────────────────────

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required."); return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in…");

        User user = FileHandler.getUserbyUsername(username, password);

        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
            loginButton.setEnabled(true);
            loginButton.setText("Sign In");
            return;
        }

        dispose();

        switch (user.getRole()) {
            case "CounterStaff" -> new CS_Frame((CounterStaff) user);
            case "Manager"      -> new Manager_Frame((Manager) user);
            case "Customer"     -> new Customer_Frame((Customer) user);
            case "Technician"   -> new Technician_Frame((Technician) user);
            default -> {
                JOptionPane.showMessageDialog(null,
                    "Unknown role: " + user.getRole(), "Error", JOptionPane.ERROR_MESSAGE);
                new Login_Frame();
            }
        }
    }
}
