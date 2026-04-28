package apu_asc;

import java.awt.*;
import javax.swing.*;

/**
 * CS_Frame — Counter Staff main dashboard window.
 * Displays feature cards; each opens a modal dialog.
 */
public class CS_Frame extends JFrame {

    private final CounterStaff user;

    public CS_Frame(CounterStaff user) {
        this.user = user;
        setTitle("Counter Staff Dashboard - APU-ASC");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.dashboardHeader("Counter Staff Dashboard", user.getName(), e -> signOut()), BorderLayout.NORTH);
        root.add(buildCards(),  BorderLayout.CENTER);
        root.add(UITheme.dashboardFooter("Counter Staff Module", user.getUserid()), BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildCards() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));

        JLabel section = UITheme.createLabel("Quick Actions", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        grid.add(UITheme.dashboardCard("👥", "Manage Customers",
            "Add, search, update and\ndelete customer records.",
            e -> new CS_ManageCustomersDialog(this)));

        grid.add(UITheme.dashboardCard("📅", "Manage Appointments",
            "Create new service\nappointments for customers.",
            e -> new CS_ManageAppointmentsDialog(this, user)));

        grid.add(UITheme.dashboardCard("💳", "Collect Payment",
            "Process payment for a\ncompleted appointment.",
            e -> new CS_CollectPaymentDialog(this)));

        grid.add(UITheme.dashboardCard("🧾", "Generate Receipt",
            "Display and save a receipt\nfor any payment.",
            e -> new CS_GenerateReceiptDialog(this)));

        grid.add(UITheme.dashboardCard("✏", "Edit My Profile",
            "Update your personal\ninformation and password.",
            e -> new CS_EditProfileDialog(this, user)));

        // Empty filler for 6th cell
        JPanel filler = new JPanel(); filler.setOpaque(false);
        grid.add(filler);

        wrapper.add(section, BorderLayout.NORTH);
        wrapper.add(grid,    BorderLayout.CENTER);
        return wrapper;
    }

    private void signOut() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose(); new Login_Frame(); }
    }
}
