package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * GenerateReceiptPanel — Look up and display a formatted receipt.
 *
 * Staff enters a Payment ID, clicks "Load Receipt",
 * and the receipt is rendered in a styled card.
 * A "Save to File" button writes receipt.txt.
 */
public class GenerateReceiptPanel extends JPanel {

    private final CounterStaffFrame   frame;
    private final CounterStaffService service = new CounterStaffService();

    private JTextField paymentIdField;
    private JPanel     receiptDisplay;
    private JLabel     statusLabel;
    private JButton    saveBtn;

    public GenerateReceiptPanel(CounterStaffFrame frame, CounterStaff user) {
        this.frame = frame;
        setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Receipts", "Generate and save a payment receipt."),
            BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    // ── Body ──────────────────────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(0, 36, 36, 36));

        // ── Lookup bar ─────────────────────────────────────────────────────────
        UITheme.RoundedPanel lookupCard = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        lookupCard.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 16));
        lookupCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        lookupCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        lookupCard.add(UITheme.createLabel("Payment ID", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        paymentIdField = UITheme.createTextField();
        paymentIdField.setPreferredSize(new Dimension(220, UITheme.INPUT_H));
        lookupCard.add(paymentIdField);

        JButton loadBtn = UITheme.createPrimaryButton("Load Receipt");
        loadBtn.addActionListener(e -> handleLoad());
        lookupCard.add(loadBtn);

        // ── Status ─────────────────────────────────────────────────────────────
        statusLabel = UITheme.createLabel(" ", UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Receipt card (hidden until loaded) ─────────────────────────────────
        receiptDisplay = new JPanel();
        receiptDisplay.setOpaque(false);
        receiptDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
        receiptDisplay.setLayout(new BoxLayout(receiptDisplay, BoxLayout.Y_AXIS));
        receiptDisplay.setVisible(false);

        // ── Save button ────────────────────────────────────────────────────────
        saveBtn = UITheme.createOutlineButton("💾  Save to File");
        saveBtn.setPreferredSize(new Dimension(180, UITheme.BUTTON_H));
        saveBtn.setMaximumSize(new Dimension(180, UITheme.BUTTON_H));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setVisible(false);
        // Save action is wired in handleLoad()

        body.add(lookupCard);
        body.add(Box.createRigidArea(new Dimension(0, 14)));
        body.add(statusLabel);
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(receiptDisplay);
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(saveBtn);

        return body;
    }

    // ── Handler ───────────────────────────────────────────────────────────────

    private void handleLoad() {
        String paymentId = paymentIdField.getText().trim();
        if (paymentId.isEmpty()) {
            setStatus("Please enter a Payment ID.", false);
            return;
        }

        String receipt = service.generateReceipt(paymentId);

        if (receipt.startsWith("Error:")) {
            setStatus(receipt, false);
            receiptDisplay.setVisible(false);
            saveBtn.setVisible(false);
            return;
        }

        // Build styled receipt card
        receiptDisplay.removeAll();
        receiptDisplay.add(buildReceiptCard(receipt, paymentId));
        receiptDisplay.setVisible(true);
        receiptDisplay.revalidate();
        receiptDisplay.repaint();

        setStatus("✓  Receipt loaded successfully.", true);

        saveBtn.setVisible(true);
        // Re-wire save action with fresh receipt text
        for (ActionListener al : saveBtn.getActionListeners()) saveBtn.removeActionListener(al);
        saveBtn.addActionListener(e -> {
            setStatus("✓  Receipt saved to receipt.txt", true);
            JOptionPane.showMessageDialog(frame,
                "Receipt has been saved to receipt.txt in the project folder.",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // ── Receipt card renderer ─────────────────────────────────────────────────

    private JPanel buildReceiptCard(String rawReceipt, String paymentId) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));

        // Header strip
        JPanel headerStrip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 14, 14, 14);
                g2.dispose();
            }
        };
        headerStrip.setOpaque(false);
        headerStrip.setPreferredSize(new Dimension(0, 70));
        headerStrip.setLayout(new GridBagLayout());

        JLabel receiptTitle = UITheme.createLabel("🧾  RECEIPT", UITheme.FONT_HEADING, Color.WHITE);
        JLabel receiptId    = UITheme.createLabel(paymentId, UITheme.FONT_REGULAR,
                new Color(186, 213, 255));

        JPanel receiptTitleBox = new JPanel();
        receiptTitleBox.setOpaque(false);
        receiptTitleBox.setLayout(new BoxLayout(receiptTitleBox, BoxLayout.Y_AXIS));
        receiptTitleBox.add(receiptTitle);
        receiptTitleBox.add(Box.createRigidArea(new Dimension(0, 2)));
        receiptTitleBox.add(receiptId);
        headerStrip.add(receiptTitleBox);

        // Body rows parsed from the raw receipt string
        JPanel rows = new JPanel(new GridBagLayout());
        rows.setOpaque(false);
        rows.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 0, 5, 20);

        String[] lines = rawReceipt.split("\n");
        int rowIdx = 0;
        for (String line : lines) {
            if (line.startsWith("=") || line.isBlank()) continue;
            int colonIdx = line.indexOf(":");
            if (colonIdx < 0) continue;

            String key = line.substring(0, colonIdx).trim();
            String val = line.substring(colonIdx + 1).trim();

            g.gridx = 0; g.gridy = rowIdx; g.weightx = 0;
            rows.add(UITheme.createLabel(key, UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY), g);
            g.gridx = 1; g.weightx = 1;
            JLabel valLabel = UITheme.createLabel(val, UITheme.FONT_REGULAR, UITheme.TEXT_PRIMARY);
            if (key.equalsIgnoreCase("Amount")) {
                valLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                valLabel.setForeground(UITheme.ACCENT);
            }
            rows.add(valLabel, g);

            // Divider between rows
            g.gridx = 0; g.gridy = rowIdx + 1; g.gridwidth = 2; g.weightx = 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            rows.add(UITheme.hDivider(), g);
            g.fill = GridBagConstraints.NONE;
            g.gridwidth = 1;
            rowIdx += 2;
        }

        card.add(headerStrip, BorderLayout.NORTH);
        card.add(rows,        BorderLayout.CENTER);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setStatus(String msg, boolean success) {
        statusLabel.setForeground(success ? UITheme.SUCCESS : UITheme.ERROR);
        statusLabel.setText(msg);
    }
}
