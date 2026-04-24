package apu_asc;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;

/**
 * Central design system for APU-ASC.
 * All colours, fonts, sizes, and component factory methods live here.
 * To restyle the entire app, change values in this one file.
 */
public class UITheme {

    // ── Card name constants (shared across frame and panels) ───────────────────

    public static final String CARD_DASHBOARD    = "dashboard";
    public static final String CARD_CUSTOMERS    = "customers";
    public static final String CARD_APPOINTMENTS = "appointments";
    public static final String CARD_PAYMENT      = "payment";
    public static final String CARD_RECEIPT      = "receipt";
    public static final String CARD_PROFILE      = "profile";

    // ── Colours ────────────────────────────────────────────────────────────────

    public static final Color SIDEBAR_BG         = new Color(13, 17, 23);
    public static final Color SIDEBAR_HOVER       = new Color(30, 37, 50);
    public static final Color SIDEBAR_ACTIVE_BG   = new Color(37, 99, 235, 28);
    public static final Color SIDEBAR_TEXT        = new Color(140, 155, 175);
    public static final Color SIDEBAR_DIVIDER     = new Color(35, 45, 65);

    public static final Color CONTENT_BG          = new Color(246, 248, 252);
    public static final Color WHITE               = Color.WHITE;

    public static final Color ACCENT              = new Color(37, 99, 235);
    public static final Color ACCENT_HOVER        = new Color(29, 78, 216);
    public static final Color ACCENT_PRESSED      = new Color(22, 61, 180);
    public static final Color ACCENT_LIGHT        = new Color(219, 234, 254);

    public static final Color TEXT_PRIMARY        = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY      = new Color(100, 116, 139);
    public static final Color BORDER              = new Color(226, 232, 240);
    public static final Color BORDER_FOCUS        = new Color(147, 197, 253);

    public static final Color SUCCESS             = new Color(22, 163, 74);
    public static final Color SUCCESS_BG          = new Color(220, 252, 231);
    public static final Color ERROR               = new Color(220, 38, 38);
    public static final Color ERROR_BG            = new Color(254, 226, 226);

    public static final Color TABLE_HEADER_BG     = new Color(241, 245, 249);
    public static final Color TABLE_ROW_ALT       = new Color(249, 251, 253);
    public static final Color TABLE_SELECTED      = new Color(219, 234, 254);

    // ── Fonts ──────────────────────────────────────────────────────────────────

    public static final Font FONT_SMALL           = new Font("Segoe UI", Font.PLAIN,  12);
    public static final Font FONT_REGULAR         = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_MEDIUM          = new Font("Segoe UI", Font.PLAIN,  15);
    public static final Font FONT_BOLD            = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font FONT_HEADING         = new Font("Segoe UI", Font.BOLD,   20);
    public static final Font FONT_TITLE           = new Font("Segoe UI", Font.BOLD,   26);
    public static final Font FONT_CARD_TITLE      = new Font("Segoe UI", Font.BOLD,   15);
    public static final Font FONT_SIDEBAR         = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_SIDEBAR_BOLD    = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font FONT_LOGO            = new Font("Segoe UI", Font.BOLD,   18);
    public static final Font FONT_MONO            = new Font("Courier New", Font.PLAIN, 13);

    // ── Dimensions ─────────────────────────────────────────────────────────────

    public static final int SIDEBAR_WIDTH         = 235;
    public static final int FRAME_WIDTH           = 1200;
    public static final int FRAME_HEIGHT          = 760;
    public static final int BUTTON_H              = 40;
    public static final int INPUT_H               = 42;
    public static final int RADIUS_CARD           = 14;
    public static final int RADIUS_BTN            = 8;
    public static final int RADIUS_INPUT          = 8;

    // ── Button factories ───────────────────────────────────────────────────────

    /** Solid blue primary button */
    public static JButton createPrimaryButton(String text) {
        return makeButton(text, ACCENT, ACCENT_HOVER, ACCENT_PRESSED, Color.WHITE);
    }

    /** Solid red danger button */
    public static JButton createDangerButton(String text) {
        Color base  = new Color(220, 38, 38);
        Color hover = new Color(185, 28, 28);
        Color press = new Color(153, 27, 27);
        return makeButton(text, base, hover, press, Color.WHITE);
    }

    /** Outlined (ghost) button in accent colour */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background fill on hover
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_PRESSED);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_BTN, RADIUS_BTN);
                    g2.setColor(Color.WHITE);
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_BTN, RADIUS_BTN);
                    g2.setColor(ACCENT);
                } else {
                    g2.setColor(ACCENT);
                }
                // Border
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, RADIUS_BTN, RADIUS_BTN);
                // Label
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        styleBtnBase(btn);
        return btn;
    }

    private static JButton makeButton(String text, Color base, Color hover, Color pressed, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? pressed
                         : getModel().isRollover() ? hover : base;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_BTN, RADIUS_BTN);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        styleBtnBase(btn);
        return btn;
    }

    private static void styleBtnBase(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setPreferredSize(new Dimension(140, BUTTON_H));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Input factories ────────────────────────────────────────────────────────

    public static JTextField createTextField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField f = new JPasswordField();
        styleInput(f);
        return f;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_REGULAR);
        cb.setBackground(WHITE);
        cb.setForeground(TEXT_PRIMARY);
        cb.setPreferredSize(new Dimension(200, INPUT_H));
        return cb;
    }

    private static void styleInput(JTextField f) {
        f.setFont(FONT_REGULAR);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(WHITE);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER, RADIUS_INPUT),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setPreferredSize(new Dimension(200, INPUT_H));
        // Blue border on focus
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDER_FOCUS, RADIUS_INPUT),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDER, RADIUS_INPUT),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    // ── Label factory ──────────────────────────────────────────────────────────

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ── Table styling ──────────────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(WHITE);
        table.setForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 46));
        header.setReorderingAllowed(false);
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(new RoundedBorder(BORDER, RADIUS_CARD));
        sp.getViewport().setBackground(WHITE);
        sp.setBackground(WHITE);
        return sp;
    }

    // ── Shared components ──────────────────────────────────────────────────────

    /** Panel with painted rounded-rect background */
    public static class RoundedPanel extends JPanel {
        private final Color bg;
        private final int   radius;
        private boolean     hasShadow;

        public RoundedPanel(Color bg, int radius) {
            this(bg, radius, false);
        }

        public RoundedPanel(Color bg, int radius, boolean hasShadow) {
            this.bg       = bg;
            this.radius   = radius;
            this.hasShadow = hasShadow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (hasShadow) {
                g2.setColor(new Color(0, 0, 0, 14));
                g2.fillRoundRect(3, 4, getWidth() - 4, getHeight() - 4, radius, radius);
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - (hasShadow ? 3 : 0), getHeight() - (hasShadow ? 3 : 0), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Custom border that paints a rounded rectangle */
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int   radius;

        public RoundedBorder(Color color, int radius) {
            this.color  = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c)                      { return new Insets(4, 4, 4, 4); }
        @Override public Insets getBorderInsets(Component c, Insets i)            { i.set(4, 4, 4, 4); return i; }
    }

    // ── Separator helper ───────────────────────────────────────────────────────

    public static JPanel hDivider() {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(BORDER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(1, 1));
        return p;
    }

    /** Standard page header: stacked title + subtitle */
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(30, 36, 18, 36));
        JLabel t = createLabel(title,    FONT_HEADING,  TEXT_PRIMARY);
        JLabel s = createLabel(subtitle, FONT_REGULAR,  TEXT_SECONDARY);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        p.add(s);
        return p;
    }
}
