package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * CounterStaffFrame — main dashboard window.
 *
 * Shows a header with the staff's name and a grid of feature cards.
 * Each card opens its own modal dialog.
 */
public class CounterStaffFrame extends JFrame {

    private final CounterStaff user;

    public CounterStaffFrame(CounterStaff user) {
        this.user = user;
        setTitle("Counter Staff Dashboard - APU-ASC");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildCardGrid(),  BorderLayout.CENTER);
        root.add(buildFooter(),    BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    // ── Header ─────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(18, 28, 16, 28)
        ));

        // Left: title + welcome
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        JPanel accentBar = new JPanel();
        accentBar.setBackground(UITheme.ACCENT);
        accentBar.setPreferredSize(new Dimension(4, 28));
        titleRow.add(accentBar);
        titleRow.add(Box.createRigidArea(new Dimension(12, 0)));
        JLabel title = UITheme.createLabel("Counter Staff Dashboard", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        titleRow.add(title);

        JLabel welcome = UITheme.createLabel("  Welcome back, " + user.getName(),
                UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        welcome.setBorder(BorderFactory.createEmptyBorder(4, 16, 0, 0));

        left.add(titleRow);
        left.add(welcome);

        // Right: sign out button
        JButton signOut = UITheme.createOutlineButton("⏻  Sign Out");
        signOut.setPreferredSize(new Dimension(130, UITheme.BUTTON_H));
        signOut.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?", "Sign Out",
                JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0); // teammate will replace with new LoginFrame()
            }
        });

        header.add(left,     BorderLayout.WEST);
        header.add(signOut,  BorderLayout.EAST);
        return header;
    }

    // ── Card grid ──────────────────────────────────────────────────────────────

    private JPanel buildCardGrid() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));

        JLabel sectionLabel = UITheme.createLabel("Quick Actions", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        grid.add(makeCard("👥", "Manage Customers",
            "Add, search, update and\ndelete customer records.",
            e -> new ManageCustomersDialog(this)));

        grid.add(makeCard("📅", "Manage Appointments",
            "Create new service\nappointments for customers.",
            e -> new ManageAppointmentsDialog(this, user)));

        grid.add(makeCard("💳", "Collect Payment",
            "Process payment for a\ncompleted appointment.",
            e -> new CollectPaymentDialog(this)));

        grid.add(makeCard("🧾", "Generate Receipt",
            "Display and save a receipt\nfor any payment.",
            e -> new GenerateReceiptDialog(this)));

        grid.add(makeCard("✏", "Edit My Profile",
            "Update your personal\ninformation and password.",
            e -> new EditProfileDialog(this, user)));

        // Empty filler for 6th cell
        JPanel filler = new JPanel(); filler.setOpaque(false);
        grid.add(filler);

        wrapper.add(sectionLabel, BorderLayout.NORTH);
        wrapper.add(grid,         BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel makeCard(String icon, String title, String desc, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout()) {
            private boolean hovered = false;
            {
                setBackground(UITheme.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UITheme.ACCENT, 2),
                            BorderFactory.createEmptyBorder(19, 19, 19, 19)));
                        repaint();
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        hovered = false;
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UITheme.BORDER),
                            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                        repaint();
                    }
                    @Override public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
                });
            }
        };

        // Icon in circle
        JPanel iconCircle = new JPanel(new GridBagLayout());
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(44, 44));
        iconCircle.setMaximumSize(new Dimension(44, 44));
        iconCircle.setBackground(UITheme.ACCENT_LIGHT);
        iconCircle.setBorder(null);

        // Paint circle background
        JPanel iconWrap = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(44, 44));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconWrap.add(iconLbl);

        // Text content
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JLabel titleLbl = UITheme.createLabel(title, UITheme.FONT_CARD_TITLE, UITheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Multiline desc
        String[] lines = desc.split("\n");
        JLabel descLbl = UITheme.createLabel(
            "<html>" + lines[0] + "<br>" + (lines.length > 1 ? lines[1] : "") + "</html>",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(titleLbl);
        text.add(Box.createRigidArea(new Dimension(0, 4)));
        text.add(descLbl);

        card.add(iconWrap, BorderLayout.NORTH);
        card.add(text,     BorderLayout.CENTER);
        return card;
    }

    // ── Footer ─────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel f = new JPanel(new BorderLayout());
        f.setBackground(UITheme.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(10, 28, 10, 28)
        ));
        f.add(UITheme.createLabel("APU Automotive Service Centre  •  Counter Staff Module",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), BorderLayout.WEST);
        f.add(UITheme.createLabel("ID: " + user.getUserid(),
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), BorderLayout.EAST);
        return f;
    }
}
