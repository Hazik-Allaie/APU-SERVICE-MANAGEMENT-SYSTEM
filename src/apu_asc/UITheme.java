package apu_asc;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;

public class UITheme {

    // ── Card name constants ────────────────────────────────────────────────────
    public static final String CARD_CUSTOMERS    = "customers";
    public static final String CARD_APPOINTMENTS = "appointments";
    public static final String CARD_PAYMENT      = "payment";
    public static final String CARD_RECEIPT      = "receipt";
    public static final String CARD_PROFILE      = "profile";

    // ── Colours ────────────────────────────────────────────────────────────────
    public static final Color BG                 = new Color(245, 246, 250);
    public static final Color WHITE              = Color.WHITE;
    public static final Color ACCENT             = new Color(37,  82, 148);
    public static final Color ACCENT_HOVER       = new Color(28,  65, 120);
    public static final Color ACCENT_LIGHT       = new Color(235, 241, 251);
    public static final Color TEXT_PRIMARY       = new Color(20,  20,  30);
    public static final Color TEXT_SECONDARY     = new Color(100, 110, 130);
    public static final Color BORDER             = new Color(220, 224, 232);
    public static final Color BORDER_FOCUS       = new Color(37,  82, 148);
    public static final Color TABLE_HEADER_BG    = new Color(52,  73, 103);
    public static final Color TABLE_HEADER_FG    = Color.WHITE;
    public static final Color TABLE_ROW_ALT      = new Color(248, 249, 252);
    public static final Color TABLE_SELECTED     = new Color(219, 231, 250);
    public static final Color TABLE_GRID         = new Color(232, 236, 242);
    public static final Color BTN_DANGER         = new Color(192,  40,  40);
    public static final Color BTN_DANGER_HOVER   = new Color(160,  30,  30);
    public static final Color SUCCESS            = new Color(22,  163,  74);
    public static final Color ERROR              = new Color(192,   40,  40);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public static final Font FONT_SMALL          = new Font("Segoe UI", Font.PLAIN,  12);
    public static final Font FONT_REGULAR        = new Font("Segoe UI", Font.PLAIN,  14);
    public static final Font FONT_BOLD           = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font FONT_HEADING        = new Font("Segoe UI", Font.BOLD,   22);
    public static final Font FONT_TITLE          = new Font("Segoe UI", Font.BOLD,   26);
    public static final Font FONT_CARD_TITLE     = new Font("Segoe UI", Font.BOLD,   15);
    public static final Font FONT_TABLE_HEADER   = new Font("Segoe UI", Font.BOLD,   13);

    // ── Dimensions ─────────────────────────────────────────────────────────────
    public static final int BUTTON_H             = 38;
    public static final int INPUT_H              = 36;
    public static final int RADIUS               = 6;

    // ── Buttons ────────────────────────────────────────────────────────────────

    public static JButton createPrimaryButton(String text) {
        return makeSolid(text, ACCENT, ACCENT_HOVER, WHITE);
    }

    public static JButton createDangerButton(String text) {
        return makeSolid(text, BTN_DANGER, BTN_DANGER_HOVER, WHITE);
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(ACCENT_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
                }
                g2.setColor(getModel().isPressed() ? ACCENT_HOVER : ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, RADIUS, RADIUS);
                g2.setFont(getFont()); g2.setColor(getModel().isPressed() ? ACCENT_HOVER : ACCENT);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        base(btn); return btn;
    }

    public static JButton createLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD); btn.setForeground(ACCENT);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static JButton makeSolid(String text, Color bg, Color hover, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()||getModel().isPressed() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
                g2.setColor(fg); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        base(btn); return btn;
    }

    private static void base(JButton b) {
        b.setFont(FONT_BOLD);
        b.setPreferredSize(new Dimension(130, BUTTON_H));
        b.setBorderPainted(false); b.setFocusPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Inputs ─────────────────────────────────────────────────────────────────

    public static JTextField createTextField() { JTextField f = new JTextField(); styleInput(f); return f; }
    public static JPasswordField createPasswordField() { JPasswordField f = new JPasswordField(); styleInput(f); return f; }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_REGULAR); cb.setBackground(WHITE); cb.setForeground(TEXT_PRIMARY);
        cb.setPreferredSize(new Dimension(200, INPUT_H));
        return cb;
    }

    private static void styleInput(JTextField f) {
        f.setFont(FONT_REGULAR); f.setForeground(TEXT_PRIMARY);
        f.setBackground(WHITE); f.setCaretColor(ACCENT);
        f.setPreferredSize(new Dimension(200, INPUT_H));
        setBorder(f, BORDER);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { setBorder(f, BORDER_FOCUS); }
            @Override public void focusLost(java.awt.event.FocusEvent e)   { setBorder(f, BORDER); }
        });
    }

    private static void setBorder(JTextField f, Color c) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(c),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    }

    // ── Labels ─────────────────────────────────────────────────────────────────

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text); l.setFont(font); l.setForeground(color); return l;
    }

    // ── Table ──────────────────────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR); table.setRowHeight(40);
        table.setShowGrid(true); table.setGridColor(TABLE_GRID);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SELECTED); table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(WHITE); table.setForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(FONT_TABLE_HEADER); hdr.setBackground(TABLE_HEADER_BG);
        hdr.setForeground(TABLE_HEADER_FG);
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0, TABLE_HEADER_BG));
        hdr.setPreferredSize(new Dimension(0, 44));
        hdr.setReorderingAllowed(false);
        hdr.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setBackground(TABLE_HEADER_BG); l.setForeground(TABLE_HEADER_FG);
                l.setFont(FONT_TABLE_HEADER); l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0,14,0,14)); return l;
            }
        });
        table.setDefaultRenderer(Object.class, altRowRenderer());
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(WHITE); return sp;
    }

    public static javax.swing.table.DefaultTableCellRenderer altRowRenderer() {
        return new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t,v,sel,f,row,col);
                setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
                if (sel) { setBackground(TABLE_SELECTED); setForeground(TEXT_PRIMARY); }
                else     { setBackground(row%2==0 ? WHITE : TABLE_ROW_ALT); setForeground(TEXT_PRIMARY); }
                return this;
            }
        };
    }

    // ── Dialog helpers ─────────────────────────────────────────────────────────

    public static JDialog createDialog(JFrame parent, String title, int w, int h) {
        JDialog d = new JDialog(parent, title, true);
        d.setSize(w, h); d.setLocationRelativeTo(parent);
        d.setResizable(false); d.getContentPane().setBackground(WHITE); return d;
    }

    public static JPanel dialogHeader(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,BORDER),
            BorderFactory.createEmptyBorder(18,24,16,24)));
        JPanel accent = new JPanel(); accent.setBackground(ACCENT);
        accent.setPreferredSize(new Dimension(4, 0));
        p.add(accent, BorderLayout.WEST);
        JLabel lbl = createLabel("  " + title, FONT_HEADING, TEXT_PRIMARY);
        p.add(lbl, BorderLayout.CENTER); return p;
    }

    public static JPanel buttonRow(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        for (JButton b : buttons) p.add(b); return p;
    }
}
