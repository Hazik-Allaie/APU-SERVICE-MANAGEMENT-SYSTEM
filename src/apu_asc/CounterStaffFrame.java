package apu_asc;

import java.awt.*;
import javax.swing.*;

/**
 * CounterStaffFrame — the root window for the Counter Staff role.
 *
 * Architecture:
 *  - Dark sidebar (persistent navigation)
 *  - Right-side CardLayout content area (swaps panels on nav click)
 *
 * Every panel receives a reference to this frame so it can call
 * navigateTo(String) to switch screens.
 */
public class CounterStaffFrame extends JFrame {

    private final CardLayout    cardLayout   = new CardLayout();
    private final JPanel        contentArea  = new JPanel(cardLayout);
    private final CounterStaff  loggedInUser;

    // Panel keys — defined in UITheme, aliased here for readability
    public static final String CARD_DASHBOARD    = UITheme.CARD_DASHBOARD;
    public static final String CARD_CUSTOMERS    = UITheme.CARD_CUSTOMERS;
    public static final String CARD_APPOINTMENTS = UITheme.CARD_APPOINTMENTS;
    public static final String CARD_PAYMENT      = UITheme.CARD_PAYMENT;
    public static final String CARD_RECEIPT      = UITheme.CARD_RECEIPT;
    public static final String CARD_PROFILE      = UITheme.CARD_PROFILE;

    // Sidebar nav buttons — kept as fields so we can toggle active state
    private final SidebarButton[] navButtons = new SidebarButton[5];

    public CounterStaffFrame(CounterStaff loggedInUser) {
        this.loggedInUser = loggedInUser;

        setTitle("APU-ASC  |  Counter Staff");
        setSize(UITheme.FRAME_WIDTH, UITheme.FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);
        add(root);

        setVisible(true);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right edge shadow
                GradientPaint shadow = new GradientPaint(
                    getWidth() - 8, 0, new Color(0, 0, 0, 40),
                    getWidth(),     0, new Color(0, 0, 0, 0)
                );
                g2.setPaint(shadow);
                g2.fillRect(getWidth() - 8, 0, 8, getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setOpaque(false);

        // ── Logo area ──────────────────────────────────────────────────────────
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(28, 22, 24, 22));

        JLabel logoIcon = new JLabel("🔧");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoText = UITheme.createLabel("APU-ASC", UITheme.FONT_LOGO, Color.WHITE);
        logoText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleTag = UITheme.createLabel("Counter Staff", UITheme.FONT_SMALL,
                new Color(100, 116, 139));
        roleTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoIcon);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        logoPanel.add(logoText);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        logoPanel.add(roleTag);

        // ── Nav items ─────────────────────────────────────────────────────────
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        String[][] items = {
            { "⊞",  "Dashboard",    CARD_DASHBOARD    },
            { "👤", "Customers",    CARD_CUSTOMERS    },
            { "📅", "Appointments", CARD_APPOINTMENTS },
            { "💳", "Payment",      CARD_PAYMENT      },
            { "🧾", "Receipt",      CARD_RECEIPT      },
        };

        for (int i = 0; i < items.length; i++) {
            final String card = items[i][2];
            final int idx = i;
            SidebarButton btn = new SidebarButton(items[i][0], items[i][1]);
            btn.addActionListener(e -> {
                navigateTo(card);
                setActiveNav(idx);
            });
            navButtons[i] = btn;
            nav.add(btn);
            nav.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        // ── Bottom: user card + logout ─────────────────────────────────────────
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 12, 20, 12));

        // Divider line
        JPanel divider = new JPanel();
        divider.setOpaque(true);
        divider.setBackground(UITheme.SIDEBAR_DIVIDER);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // User info chip
        JPanel userChip = new JPanel();
        userChip.setOpaque(false);
        userChip.setLayout(new BoxLayout(userChip, BoxLayout.X_AXIS));
        userChip.setBorder(BorderFactory.createEmptyBorder(14, 10, 10, 10));
        userChip.setAlignmentX(Component.LEFT_ALIGNMENT);
        userChip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Avatar circle (painted)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, 34, 34);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String initial = loggedInUser.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (34 - fm.stringWidth(initial)) / 2,
                        (34 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setMaximumSize(new Dimension(34, 34));
        avatar.setMinimumSize(new Dimension(34, 34));

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        String displayName = loggedInUser.getName().length() > 14
                ? loggedInUser.getName().substring(0, 13) + "…"
                : loggedInUser.getName();
        JLabel nameLabel = UITheme.createLabel(displayName, UITheme.FONT_BOLD, Color.WHITE);
        JLabel idLabel   = UITheme.createLabel(loggedInUser.getUserid(), UITheme.FONT_SMALL,
                new Color(100, 116, 139));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfo.add(nameLabel);
        userInfo.add(Box.createRigidArea(new Dimension(0, 2)));
        userInfo.add(idLabel);

        userChip.add(avatar);
        userChip.add(userInfo);

        // Edit profile shortcut
        SidebarButton profileBtn = new SidebarButton("✏", "Edit Profile");
        profileBtn.addActionListener(e -> {
            navigateTo(CARD_PROFILE);
            setActiveNav(-1);
        });

        // Logout
        SidebarButton logoutBtn = new SidebarButton("⏻", "Logout");
        logoutBtn.setForeground(new Color(248, 113, 113));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                CounterStaffFrame.this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        bottom.add(divider);
        bottom.add(userChip);
        bottom.add(Box.createRigidArea(new Dimension(0, 4)));
        bottom.add(profileBtn);
        bottom.add(Box.createRigidArea(new Dimension(0, 4)));
        bottom.add(logoutBtn);

        // Assemble sidebar
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        // Thin accent line under logo
        JPanel accentLine = new JPanel();
        accentLine.setOpaque(true);
        accentLine.setBackground(UITheme.SIDEBAR_DIVIDER);
        accentLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        accentLine.setPreferredSize(new Dimension(1, 1));

        topSection.add(logoPanel);
        topSection.add(accentLine);
        topSection.add(Box.createRigidArea(new Dimension(0, 16)));
        topSection.add(nav);

        sidebar.add(topSection, BorderLayout.NORTH);
        sidebar.add(bottom,     BorderLayout.SOUTH);

        // Default active
        setActiveNav(0);
        return sidebar;
    }

    // ── Content area (CardLayout) ─────────────────────────────────────────────

    private JPanel buildContentArea() {
        contentArea.setBackground(UITheme.CONTENT_BG);

        contentArea.add(new DashboardPanel(this, loggedInUser),    CARD_DASHBOARD);
        contentArea.add(new ManageCustomersPanel(this, loggedInUser),    CARD_CUSTOMERS);
        contentArea.add(new ManageAppointmentsPanel(this, loggedInUser), CARD_APPOINTMENTS);
        contentArea.add(new CollectPaymentPanel(this, loggedInUser),     CARD_PAYMENT);
        contentArea.add(new GenerateReceiptPanel(this, loggedInUser),    CARD_RECEIPT);
        contentArea.add(new EditProfilePanel(this, loggedInUser),        CARD_PROFILE);

        cardLayout.show(contentArea, CARD_DASHBOARD);
        return contentArea;
    }

    // ── Navigation API ────────────────────────────────────────────────────────

    public void navigateTo(String cardName) {
        cardLayout.show(contentArea, cardName);
    }

    private void setActiveNav(int activeIdx) {
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setActive(i == activeIdx);
        }
    }

    // ── SidebarButton inner class ─────────────────────────────────────────────

    static class SidebarButton extends JButton {
        private boolean active = false;

        SidebarButton(String icon, String label) {
            setText("  " + icon + "   " + label);
            setFont(UITheme.FONT_SIDEBAR);
            setForeground(UITheme.SIDEBAR_TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setMinimumSize(new Dimension(0, 40));
            setPreferredSize(new Dimension(0, 40));
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
        }

        public void setActive(boolean active) {
            this.active = active;
            setFont(active ? UITheme.FONT_SIDEBAR_BOLD : UITheme.FONT_SIDEBAR);
            setForeground(active ? Color.WHITE : UITheme.SIDEBAR_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(UITheme.SIDEBAR_ACTIVE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Left accent bar
                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(0, 6, 3, getHeight() - 12, 3, 3);
            } else if (getModel().isRollover()) {
                g2.setColor(UITheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
