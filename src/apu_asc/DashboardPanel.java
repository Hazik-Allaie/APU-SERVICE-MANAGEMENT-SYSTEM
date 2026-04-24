package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * DashboardPanel — the first screen after login.
 * Shows a greeting and 5 large clickable feature cards.
 */
public class DashboardPanel extends JPanel {

    private final CounterStaffFrame frame;
    private final CounterStaff      user;

    public DashboardPanel(CounterStaffFrame frame, CounterStaff user) {
        this.frame = frame;
        this.user  = user;
        setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCards(),  BorderLayout.CENTER);
    }

    // ── Header ─────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(36, 40, 8, 40));

        String hour = java.time.LocalTime.now().getHour() < 12 ? "Good morning"
                    : java.time.LocalTime.now().getHour() < 17 ? "Good afternoon"
                    : "Good evening";

        JLabel greeting = UITheme.createLabel(hour + ", " + user.getName() + " 👋",
                UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        greeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        String dateStr = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        JLabel dateLabel = UITheme.createLabel(dateStr, UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(greeting);
        header.add(Box.createRigidArea(new Dimension(0, 6)));
        header.add(dateLabel);
        header.add(Box.createRigidArea(new Dimension(0, 28)));

        // Section label
        JLabel sectionLabel = UITheme.createLabel("Quick Actions", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(sectionLabel);

        return header;
    }

    // ── Feature cards grid ─────────────────────────────────────────────────────

    private JPanel buildCards() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 40, 40, 40));
        wrapper.setLayout(new GridBagLayout());

        Object[][] cards = {
            { "👥", "Manage Customers",
              "Add, search, update and delete customer records.",
              UITheme.CARD_CUSTOMERS,    new Color(37, 99, 235) },
            { "📅", "Manage Appointments",
              "Create new service appointments for customers.",
              UITheme.CARD_APPOINTMENTS, new Color(16, 185, 129) },
            { "💳", "Collect Payment",
              "Process payment for a completed appointment.",
              UITheme.CARD_PAYMENT,      new Color(245, 158, 11) },
            { "🧾", "Generate Receipt",
              "Print or display a receipt for any payment.",
              UITheme.CARD_RECEIPT,      new Color(139, 92, 246) },
            { "✏",  "Edit My Profile",
              "Update your personal information and password.",
              UITheme.CARD_PROFILE,      new Color(236, 72, 153) },
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        int col = 0;
        int row = 0;
        for (Object[] card : cards) {
            gbc.gridx = col;
            gbc.gridy = row;
            wrapper.add(buildFeatureCard(
                (String) card[0],
                (String) card[1],
                (String) card[2],
                (String) card[3],
                (Color)  card[4]
            ), gbc);
            col++;
            if (col == 3) { col = 0; row++; }
        }

        // Fill remaining cell in last row to keep alignment
        if (col > 0) {
            while (col < 3) {
                gbc.gridx = col;
                JPanel spacer = new JPanel();
                spacer.setOpaque(false);
                wrapper.add(spacer, gbc);
                col++;
            }
        }

        return wrapper;
    }

    private JPanel buildFeatureCard(String icon, String title, String desc, String cardTarget, Color accent) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top: icon circle + title
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        // Icon circle
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(52, 52));
        iconCircle.setMaximumSize(new Dimension(52, 52));
        iconCircle.setMinimumSize(new Dimension(52, 52));
        iconCircle.setLayout(new GridBagLayout());
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconCircle.add(iconLabel);

        JLabel titleLabel = UITheme.createLabel(title, UITheme.FONT_CARD_TITLE, UITheme.TEXT_PRIMARY);
        JLabel descLabel  = UITheme.createLabel(
            "<html><body style='width:140px'>" + desc + "</body></html>",
            UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY
        );

        // Bottom arrow
        JLabel arrow = UITheme.createLabel("→", UITheme.FONT_BOLD, accent);

        top.add(iconCircle);
        top.add(Box.createRigidArea(new Dimension(0, 14)));
        top.add(titleLabel);
        top.add(Box.createRigidArea(new Dimension(0, 6)));
        top.add(descLabel);

        card.add(top,   BorderLayout.CENTER);
        card.add(arrow, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new UITheme.RoundedBorder(accent, UITheme.RADIUS_CARD),
                    BorderFactory.createEmptyBorder(23, 23, 23, 23)
                ));
                arrow.setFont(new Font("Segoe UI", Font.BOLD, 16));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
                arrow.setFont(UITheme.FONT_BOLD);
                card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                frame.navigateTo(cardTarget);
            }
        });

        return card;
    }
}
