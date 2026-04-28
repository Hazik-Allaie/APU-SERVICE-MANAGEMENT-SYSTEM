package apu_asc;

import java.awt.*;
import javax.swing.*;

/**
 * Manager_Frame — Manager main dashboard window.
 */
public class Manager_Frame extends JFrame {

    private final Manager manager;

    public Manager_Frame(Manager manager) {
        this.manager = manager;
        setTitle("Manager Dashboard - APU-ASC");
        setSize(900, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(UITheme.dashboardHeader("Manager Dashboard", manager.getName(), e -> signOut()), BorderLayout.NORTH);
        root.add(buildCards(),  BorderLayout.CENTER);
        root.add(UITheme.dashboardFooter("Manager Module", manager.getUserid()), BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildCards() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));

        JLabel section = UITheme.createLabel("Quick Actions", UITheme.FONT_BOLD, UITheme.TEXT_SECONDARY);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 16));
        grid.setOpaque(false);

        grid.add(UITheme.dashboardCard("👥", "Manage Staff",
            "Add, view, update and\ndelete staff accounts.",
            e -> new Manager_ManageStaffDialog(this)));

        grid.add(UITheme.dashboardCard("💲", "Set Service Prices",
            "Configure Normal and\nMajor service prices.",
            e -> new Manager_SetPricesDialog(this)));

        grid.add(UITheme.dashboardCard("💬", "View Feedbacks",
            "Review all customer\nappointment comments.",
            e -> new Manager_ViewFeedbacksDialog(this)));

        grid.add(UITheme.dashboardCard("📊", "View Reports",
            "See appointment stats\nand revenue summary.",
            e -> new Manager_ViewReportsDialog(this)));

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
