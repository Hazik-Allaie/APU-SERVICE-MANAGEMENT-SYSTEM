package apu_asc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * CollectPaymentPanel — Collect payment for a completed appointment.
 *
 * The staff enters an Appointment ID, selects a payment method,
 * then confirms. Live appointment summary is shown before confirming.
 */
public class CollectPaymentPanel extends JPanel {

    private final CounterStaffFrame   frame;
    private final CounterStaffService service = new CounterStaffService();

    // Input fields
    private JTextField        appointmentIdField;
    private JComboBox<String> paymentMethodCombo;

    // Summary display
    private JPanel summaryCard;
    private JLabel lblApptId, lblCustomer, lblService, lblAmount, lblDate, lblStatus;

    // Status
    private JLabel statusLabel;
    private JButton confirmBtn;

    public CollectPaymentPanel(CounterStaffFrame frame, CounterStaff user) {
        this.frame = frame;
        setBackground(UITheme.CONTENT_BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Collect Payment", "Process payment for a completed service appointment."),
            BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    // ── Body ──────────────────────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(0, 36, 36, 36));

        // ── Input card ─────────────────────────────────────────────────────────
        UITheme.RoundedPanel inputCard = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        inputCard.setLayout(new GridBagLayout());
        inputCard.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        inputCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 0, 6, 16);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        appointmentIdField = UITheme.createTextField();
        paymentMethodCombo = UITheme.createComboBox(new String[]{"Cash", "Card", "Online"});

        // Appointment ID
        g.weightx = 0; g.gridx = 0; g.gridy = 0;
        inputCard.add(UITheme.createLabel("Appointment ID", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.weightx = 1; g.gridx = 1;
        inputCard.add(appointmentIdField, g);

        // Payment Method
        g.weightx = 0; g.gridx = 2;
        inputCard.add(UITheme.createLabel("Payment Method", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY), g);
        g.weightx = 1; g.gridx = 3;
        inputCard.add(paymentMethodCombo, g);

        // Load button
        JButton loadBtn = UITheme.createOutlineButton("Load Details");
        loadBtn.setPreferredSize(new Dimension(150, UITheme.BUTTON_H));
        g.gridx = 0; g.gridy = 1; g.gridwidth = 4; g.insets = new Insets(14, 0, 0, 0);
        inputCard.add(loadBtn, g);
        loadBtn.addActionListener(e -> handleLoad());

        // ── Summary card (hidden until loaded) ────────────────────────────────
        summaryCard = buildSummaryCard();
        summaryCard.setVisible(false);
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Status + confirm ───────────────────────────────────────────────────
        statusLabel = UITheme.createLabel(" ", UITheme.FONT_REGULAR, UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmBtn = UITheme.createPrimaryButton("Confirm Payment");
        confirmBtn.setPreferredSize(new Dimension(200, UITheme.BUTTON_H));
        confirmBtn.setMaximumSize(new Dimension(200, UITheme.BUTTON_H));
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.setEnabled(false);
        confirmBtn.addActionListener(e -> handleConfirm());

        body.add(inputCard);
        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(summaryCard);
        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(statusLabel);
        body.add(Box.createRigidArea(new Dimension(0, 10)));
        body.add(confirmBtn);

        return body;
    }

    // ── Summary card ──────────────────────────────────────────────────────────

    private JPanel buildSummaryCard() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(UITheme.WHITE, UITheme.RADIUS_CARD, true);
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));
        header.add(UITheme.createLabel("Appointment Summary", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY),
                BorderLayout.WEST);

        JPanel rows = new JPanel(new GridLayout(3, 4, 12, 8));
        rows.setOpaque(false);
        rows.setBorder(BorderFactory.createEmptyBorder(0, 24, 20, 24));

        lblApptId   = makeSummaryValueLabel();
        lblCustomer = makeSummaryValueLabel();
        lblService  = makeSummaryValueLabel();
        lblAmount   = makeSummaryValueLabel();
        lblAmount.setForeground(UITheme.ACCENT);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDate     = makeSummaryValueLabel();
        lblStatus   = makeSummaryValueLabel();

        rows.add(summaryPair("Appointment ID", lblApptId));
        rows.add(summaryPair("Customer ID",    lblCustomer));
        rows.add(summaryPair("Service Type",   lblService));
        rows.add(summaryPair("Amount Due",     lblAmount));
        rows.add(summaryPair("Appt Date",      lblDate));
        rows.add(summaryPair("Status",         lblStatus));

        card.add(header, BorderLayout.NORTH);
        card.add(rows,   BorderLayout.CENTER);
        return card;
    }

    private JLabel makeSummaryValueLabel() {
        return UITheme.createLabel("—", UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
    }

    private JPanel summaryPair(String label, JLabel value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(UITheme.createLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY));
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        p.add(value);
        return p;
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    private void handleLoad() {
        String apptId = appointmentIdField.getText().trim();
        if (apptId.isEmpty()) {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Please enter an Appointment ID.");
            return;
        }

        // Load appointment from service layer
        ArrayList<Appointment> all = FileHandler.getAllAppointments();
        Appointment appt = null;
        for (Appointment a : all) {
            if (a.getAppointmentid().equalsIgnoreCase(apptId)) { appt = a; break; }
        }

        if (appt == null) {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Appointment not found: " + apptId);
            summaryCard.setVisible(false);
            confirmBtn.setEnabled(false);
            return;
        }

        // Populate summary card
        lblApptId.setText(appt.getAppointmentid());
        lblCustomer.setText(appt.getCustomerid());
        lblService.setText(appt.getServicetype());
        lblAmount.setText("RM " + String.format("%.2f", appt.getPrice()));
        lblDate.setText(appt.getDate());

        boolean completed = appt.getStatus().equals("Completed");
        lblStatus.setText(appt.getStatus());
        lblStatus.setForeground(completed ? UITheme.SUCCESS : UITheme.ERROR);

        summaryCard.setVisible(true);
        summaryCard.revalidate();
        summaryCard.repaint();

        if (completed) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Appointment is completed. Ready to collect payment.");
            confirmBtn.setEnabled(true);
        } else {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Payment cannot be collected — appointment is not yet Completed.");
            confirmBtn.setEnabled(false);
        }
    }

    private void handleConfirm() {
        String apptId  = appointmentIdField.getText().trim();
        String method  = (String) paymentMethodCombo.getSelectedItem();

        OperationResult res = service.collectPayment(apptId, method);

        if (res.getResult()) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("✓  " + res.getMessage());
            confirmBtn.setEnabled(false);
            // Refresh status label to Paid
            lblStatus.setText("Paid");
            lblStatus.setForeground(UITheme.SUCCESS);

            JOptionPane.showMessageDialog(frame,
                "Payment collected successfully!\nYou can now generate a receipt from the Receipt screen.",
                "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("✕  " + res.getMessage());
        }
    }
}
