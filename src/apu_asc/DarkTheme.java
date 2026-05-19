package apu_asc;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class DarkTheme {

    // ── Colors ────────────────────────────────────────────────────────────────
    public static final Color BG_DARK        = new Color(8, 12, 40);
    public static final Color BG_CARD        = new Color(15, 25, 70, 180);
    public static final Color BG_HEADER      = new Color(10, 18, 55);
    public static final Color ROYAL_BLUE     = new Color(30, 60, 180);
    public static final Color ROYAL_LIGHT    = new Color(60, 100, 220);
    public static final Color GLASS_BG       = new Color(255, 255, 255, 15);
    public static final Color GLASS_BORDER   = new Color(255, 255, 255, 40);
    public static final Color TEXT_WHITE     = new Color(240, 245, 255);
    public static final Color TEXT_MUTED     = new Color(140, 160, 210);
    public static final Color SUCCESS        = new Color(34, 197, 94);
    public static final Color WARNING        = new Color(251, 146, 60);
    public static final Color ERROR          = new Color(239, 68, 68);
    public static final Color ACCENT_BLUE    = new Color(96, 165, 250);
    public static final Color PURPLE         = new Color(139, 92, 246);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Create dark dialog ────────────────────────────────────────────────────
    public static JDialog createDialog(JFrame parent, String title, int w, int h) {
        JDialog d = new JDialog(parent, title, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(parent);
        d.setResizable(false);
        d.getContentPane().setBackground(BG_DARK);
        return d;
    }

    // ── 3D Gradient dialog header ─────────────────────────────────────────────
public static JPanel dialogHeader(String title, String iconFile) {
    JPanel header = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                0, 0,            new Color(10, 20, 80),
                getWidth(), 0,   new Color(40, 80, 200));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            // 3D shine on top
            GradientPaint shine = new GradientPaint(
                0, 0,              new Color(255, 255, 255, 50),
                0, getHeight()/2,  new Color(255, 255, 255, 0));
            g2.setPaint(shine);
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);
            // Bottom glow line
            g2.setColor(new Color(80, 130, 255, 180));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            // Decorative circles
            g2.setColor(new Color(255, 255, 255, 8));
            g2.fillOval(getWidth()-120, -40, 160, 160);
            g2.fillOval(-40, -30, 120, 120);
            g2.dispose();
        }
    };
    header.setOpaque(false);
    header.setPreferredSize(new Dimension(0, 52));
    header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    left.setOpaque(false);

    // Icon
    if (iconFile != null && !iconFile.isEmpty()) {
        try {
            ImageIcon raw = new ImageIcon(iconFile);
            Image scaled = raw.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
            JLabel iconLbl = new JLabel(new ImageIcon(scaled));
            left.add(iconLbl);
        } catch (Exception ignored) {}
    }

    // Title with 3D shadow
    JLabel titleLbl = new JLabel(title) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(new Color(0, 0, 0, 80));
            g2.drawString(getText(), 2, g2.getFontMetrics().getAscent() + 2);
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
            g2.dispose();
        }
    };
    titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLbl.setForeground(Color.WHITE);
    left.add(titleLbl);

    header.add(left, BorderLayout.CENTER);
    return header;
}

// Overload for dialogs without icon
public static JPanel dialogHeader(String title) {
    return dialogHeader(title, "");
}

    // ── Dark background panel ─────────────────────────────────────────────────
    public static JPanel darkPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,              new Color(8, 12, 40),
                    getWidth(), getHeight(), new Color(15, 35, 90));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Subtle grid
                g2.setColor(new Color(255, 255, 255, 4));
                g2.setStroke(new BasicStroke(1f));
                for (int x = 0; x < getWidth(); x += 40)
                    g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40)
                    g2.drawLine(0, y, getWidth(), y);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ── Glass card panel ──────────────────────────────────────────────────────
    public static JPanel glassCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    // ── Style table for dark mode ─────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setForeground(TEXT_WHITE);
        table.setBackground(new Color(0, 0, 0, 0));
        table.setOpaque(false);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 4));
        table.setSelectionBackground(new Color(60, 100, 220, 80));
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setForeground(TEXT_WHITE);
        table.getTableHeader().setBackground(new Color(20, 35, 100));
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, new DarkRowRenderer());
    }

    // ── Dark row renderer (frosted glass rows) ────────────────────────────────
    public static class DarkRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JPanel cell = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isSelected) {
                        g2.setColor(new Color(60, 100, 220, 80));
                    } else if (row % 2 == 0) {
                        g2.setColor(new Color(255, 255, 255, 10));
                    } else {
                        g2.setColor(new Color(255, 255, 255, 5));
                    }
                    g2.fillRoundRect(0, 1, getWidth(), getHeight()-2, 8, 8);
                    if (!isSelected) {
                        g2.setColor(new Color(255, 255, 255, 20));
                        g2.setStroke(new BasicStroke(0.5f));
                        g2.drawRoundRect(0, 1, getWidth()-1, getHeight()-3, 8, 8);
                    }
                    g2.dispose();
                }
            };
            cell.setOpaque(false);

            JLabel lbl = new JLabel(value == null ? "" : value.toString());
            lbl.setFont(FONT_REGULAR);
            lbl.setForeground(isSelected ? Color.WHITE : TEXT_WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            cell.add(lbl, BorderLayout.CENTER);
            return cell;
        }
    }

    // ── Colored badge ─────────────────────────────────────────────────────────
    public static JPanel badge(String text, Color bg, Color fg) {
        JPanel b = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        b.setOpaque(false);
        b.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 3));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(fg);
        b.add(lbl);
        return b;
    }

    // ── Progress bar ──────────────────────────────────────────────────────────
    public static JPanel progressBar(int completed, int total, Color color) {
        JPanel outer = new JPanel(new BorderLayout(0, 2));
        outer.setOpaque(false);

        double pct = total == 0 ? 0 : (double) completed / total;
        int percent = (int)(pct * 100);

        JLabel pctLbl = new JLabel(percent + "%");
        pctLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        pctLbl.setForeground(color);

        JPanel track = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Track
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                // Fill
                int fillW = (int)(getWidth() * pct);
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(
                        0, 0, color.brighter(),
                        fillW, 0, color);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), 6, 6);
                    // Shine
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.fillRoundRect(0, 0, fillW, getHeight()/2, 6, 6);
                }
                g2.dispose();
            }
        };
        track.setOpaque(false);
        track.setPreferredSize(new Dimension(0, 6));

        outer.add(pctLbl, BorderLayout.EAST);
        outer.add(track,  BorderLayout.CENTER);
        return outer;
    }

    // ── Dark scroll pane ──────────────────────────────────────────────────────
    public static JScrollPane darkScrollPane(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(GLASS_BORDER));
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(new Color(0,0,0,0));
        sp.getVerticalScrollBar().setBackground(new Color(255,255,255,10));
        return sp;
    }

    // ── Dark label ────────────────────────────────────────────────────────────
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ── Dark button ───────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = getModel().isRollover()
                    ? new Color(60, 110, 230) : new Color(40, 80, 200);
                Color c2 = getModel().isRollover()
                    ? new Color(100, 150, 255) : new Color(70, 120, 230);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Shine
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(140, 38));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton outlineButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(GLASS_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(TEXT_WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(FONT_BOLD);
        b.setPreferredSize(new Dimension(120, 38));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setForeground(ACCENT_BLUE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Button row ────────────────────────────────────────────────────────────
    public static JPanel buttonRow(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(new Color(10, 18, 55));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
            new Color(255, 255, 255, 30)));
        for (JButton b : buttons) p.add(b);
        return p;
    }

    // ── Section label ─────────────────────────────────────────────────────────
    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(255, 255, 255, 20)),
            BorderFactory.createEmptyBorder(0, 0, 6, 0)));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return l;
    }

    // ── Dark combo box ────────────────────────────────────────────────────────
    public static JComboBox<String> darkCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_REGULAR);
        cb.setForeground(TEXT_WHITE);
        cb.setBackground(new Color(20, 35, 100));
        cb.setBorder(BorderFactory.createLineBorder(GLASS_BORDER));
        return cb;
    }
}
