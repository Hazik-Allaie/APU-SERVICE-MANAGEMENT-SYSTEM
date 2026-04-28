package apu_asc;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;

/**
 * UITheme — shared design system used by every role's UI.
 * All colours, fonts, sizes and component factories live here.
 */
public class UITheme {

    // ── Colours ────────────────────────────────────────────────────────────────
    public static final Color BG               = new Color(245, 246, 250);
    public static final Color WHITE            = Color.WHITE;
    public static final Color ACCENT           = new Color(37,  82, 148);
    public static final Color ACCENT_HOVER     = new Color(28,  65, 120);
    public static final Color ACCENT_LIGHT     = new Color(235, 241, 251);
    public static final Color TEXT_PRIMARY     = new Color(20,  20,  30);
    public static final Color TEXT_SECONDARY   = new Color(100, 110, 130);
    public static final Color BORDER           = new Color(220, 224, 232);
    public static final Color BORDER_FOCUS     = new Color(37,  82, 148);
    public static final Color TABLE_HEADER_BG  = new Color(52,  73, 103);
    public static final Color TABLE_HEADER_FG  = Color.WHITE;
    public static final Color TABLE_ROW_ALT    = new Color(248, 249, 252);
    public static final Color TABLE_SELECTED   = new Color(219, 231, 250);
    public static final Color TABLE_GRID       = new Color(232, 236, 242);
    public static final Color BTN_DANGER       = new Color(192,  40,  40);
    public static final Color BTN_DANGER_HOVER = new Color(160,  30,  30);
    public static final Color SUCCESS          = new Color(22,  163,  74);
    public static final Color ERROR            = new Color(192,   40,  40);
    public static final Color WARNING          = new Color(180,  100,  20);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_REGULAR      = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD         = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_HEADING      = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_CARD_TITLE   = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD,  13);

    // ── Sizes ─────────────────────────────────────────────────────────────────
    public static final int BUTTON_H = 38;
    public static final int INPUT_H  = 36;
    public static final int RADIUS   = 6;

    // ── Buttons ────────────────────────────────────────────────────────────────

    public static JButton createPrimaryButton(String text) {
        return solidBtn(text, ACCENT, ACCENT_HOVER, WHITE);
    }

    public static JButton createDangerButton(String text) {
        return solidBtn(text, BTN_DANGER, BTN_DANGER_HOVER, WHITE);
    }

    public static JButton createOutlineButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(ACCENT_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
                }
                g2.setColor(getModel().isPressed() ? ACCENT_HOVER : ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, RADIUS, RADIUS);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        return baseBtn(b);
    }

    public static JButton createLinkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD); b.setForeground(ACCENT);
        b.setBorderPainted(false); b.setFocusPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JButton solidBtn(String text, Color bg, Color hover, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(getModel().isRollover()||getModel().isPressed() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
                g2.setColor(fg); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        return baseBtn(b);
    }

    private static JButton baseBtn(JButton b) {
        b.setFont(FONT_BOLD); b.setPreferredSize(new Dimension(130, BUTTON_H));
        b.setBorderPainted(false); b.setFocusPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    // ── Inputs ─────────────────────────────────────────────────────────────────

    public static JTextField createTextField() {
        JTextField f = new JTextField(); styleInput(f); return f;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField f = new JPasswordField(); styleInput(f); return f;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_REGULAR); cb.setBackground(WHITE); cb.setForeground(TEXT_PRIMARY);
        cb.setPreferredSize(new Dimension(200, INPUT_H)); return cb;
    }

    private static void styleInput(JTextField f) {
        f.setFont(FONT_REGULAR); f.setForeground(TEXT_PRIMARY);
        f.setBackground(WHITE); f.setCaretColor(ACCENT);
        f.setPreferredSize(new Dimension(200, INPUT_H));
        setInputBorder(f, BORDER);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { setInputBorder(f, BORDER_FOCUS); }
            @Override public void focusLost(java.awt.event.FocusEvent e)   { setInputBorder(f, BORDER); }
        });
    }

    public static void setInputBorder(JTextField f, Color c) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(c),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    }

    // ── Labels ─────────────────────────────────────────────────────────────────

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text); l.setFont(font); l.setForeground(color); return l;
    }

    // ── Table ──────────────────────────────────────────────────────────────────

    public static void styleTable(JTable t) {
        t.setFont(FONT_REGULAR); t.setRowHeight(40);
        t.setShowGrid(true); t.setGridColor(TABLE_GRID);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(TABLE_SELECTED); t.setSelectionForeground(TEXT_PRIMARY);
        t.setBackground(WHITE); t.setForeground(TEXT_PRIMARY);
        t.setFillsViewportHeight(true);

        JTableHeader h = t.getTableHeader();
        h.setFont(FONT_TABLE_HEADER); h.setBackground(TABLE_HEADER_BG);
        h.setForeground(TABLE_HEADER_FG);
        h.setBorder(BorderFactory.createMatteBorder(0,0,1,0,TABLE_HEADER_BG));
        h.setPreferredSize(new Dimension(0, 44));
        h.setReorderingAllowed(false);
        h.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tbl,v,s,f,r,c);
                l.setBackground(TABLE_HEADER_BG); l.setForeground(TABLE_HEADER_FG);
                l.setFont(FONT_TABLE_HEADER); l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0,14,0,14)); return l;
            }
        });
        t.setDefaultRenderer(Object.class, altRowRenderer());
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
                setBackground(sel ? TABLE_SELECTED : row%2==0 ? WHITE : TABLE_ROW_ALT);
                setForeground(TEXT_PRIMARY); return this;
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
        JPanel bar = new JPanel(); bar.setBackground(ACCENT);
        bar.setPreferredSize(new Dimension(4, 0));
        p.add(bar, BorderLayout.WEST);
        p.add(createLabel("  " + title, FONT_HEADING, TEXT_PRIMARY), BorderLayout.CENTER);
        return p;
    }

    public static JPanel buttonRow(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        for (JButton b : buttons) p.add(b); return p;
    }

    // ── Dashboard card builder (shared across all role frames) ─────────────────

    public static JPanel dashboardCard(String icon, String title, String desc,
                                        java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout()) {
            {
                setBackground(WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(24, 22, 24, 22)));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ACCENT, 2),
                            BorderFactory.createEmptyBorder(23, 21, 23, 21)));
                    }
                    @Override public void mouseExited(java.awt.event.MouseEvent e) {
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER),
                            BorderFactory.createEmptyBorder(24, 22, 24, 22)));
                    }
                    @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                        action.actionPerformed(null);
                    }
                });
            }
        };

        JPanel iconWrap = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(ACCENT_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(48, 48));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconWrap.add(iconLbl);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JLabel titleLbl = createLabel(title, FONT_CARD_TITLE, TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] lines = desc.split("\n");
        JLabel descLbl = createLabel(
            "<html>" + lines[0] + (lines.length > 1 ? "<br>" + lines[1] : "") + "</html>",
            FONT_SMALL, TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(titleLbl);
        text.add(Box.createRigidArea(new Dimension(0, 5)));
        text.add(descLbl);

        card.add(iconWrap, BorderLayout.NORTH);
        card.add(text,     BorderLayout.CENTER);
        return card;
    }

    // ── Standard dashboard frame header ───────────────────────────────────────

    public static JPanel dashboardHeader(String title, String welcomeName,
                                          java.awt.event.ActionListener onSignOut) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,BORDER),
            BorderFactory.createEmptyBorder(18,28,16,28)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        JPanel bar = new JPanel(); bar.setBackground(ACCENT);
        bar.setPreferredSize(new Dimension(4, 28));
        titleRow.add(bar);
        titleRow.add(Box.createRigidArea(new Dimension(12, 0)));
        titleRow.add(createLabel(title, FONT_HEADING, TEXT_PRIMARY));

        JLabel welcome = createLabel("  Welcome back, " + welcomeName, FONT_REGULAR, TEXT_SECONDARY);
        welcome.setBorder(BorderFactory.createEmptyBorder(4, 16, 0, 0));
        left.add(titleRow); left.add(welcome);

        JButton signOut = createOutlineButton("⏻  Sign Out");
        signOut.setPreferredSize(new Dimension(130, BUTTON_H));
        signOut.addActionListener(onSignOut);

        header.add(left,    BorderLayout.WEST);
        header.add(signOut, BorderLayout.EAST);
        return header;
    }

    // ── Standard dashboard footer ──────────────────────────────────────────────

    public static JPanel dashboardFooter(String moduleLabel, String userId) {
        JPanel f = new JPanel(new BorderLayout());
        f.setBackground(WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,BORDER),
            BorderFactory.createEmptyBorder(10,28,10,28)));
        f.add(createLabel("APU Automotive Service Centre  •  " + moduleLabel,
            FONT_SMALL, TEXT_SECONDARY), BorderLayout.WEST);
        f.add(createLabel("ID: " + userId, FONT_SMALL, TEXT_SECONDARY), BorderLayout.EAST);
        return f;
    }

    // ── Form helpers ───────────────────────────────────────────────────────────

    public static GridBagConstraints formGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.insets = new Insets(2, 0, 2, 0); return g;
    }

    public static void formRow(JPanel p, GridBagConstraints g, int row,
                                String label, JComponent field) {
        g.gridx=0; g.gridy=row*2;   g.gridwidth=2;
        p.add(createLabel(label, FONT_BOLD, TEXT_PRIMARY), g);
        g.gridx=0; g.gridy=row*2+1; g.gridwidth=2;
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_H));
        if (field instanceof JTextField)
            ((JTextField)field).setPreferredSize(new Dimension(400, INPUT_H));
        p.add(field, g);
        g.gridy=row*2+2;
        p.add(Box.createRigidArea(new Dimension(0, 5)), g);
    }

    public static JScrollPane formScrollPane(JPanel form) {
        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null); sp.getViewport().setBackground(WHITE); return sp;
    }

    public static void placeholder(JTextField f, String hint) {
        f.setForeground(TEXT_SECONDARY); f.setText(hint);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(TEXT_PRIMARY); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isBlank()) { f.setText(hint); f.setForeground(TEXT_SECONDARY); }
            }
        });
    }
}
