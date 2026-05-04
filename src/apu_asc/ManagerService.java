package apu_asc;

import java.util.ArrayList;

/**
 * ManagerService — business logic layer for the Manager role.
 * All Manager UI files call this class exclusively.
 * No UI file calls FileHandler directly.
 */
public class ManagerService {

    // ── Staff Management ───────────────────────────────────────────────────────

    public ArrayList<User> getAllStaff() {
        ArrayList<User> allUsers = FileHandler.getallusers();
        ArrayList<User> staff = new ArrayList<>();
        for (User u : allUsers)
            if (!u.getRole().equals("Customer"))
                staff.add(u);
        return staff;
    }

    public OperationResult addStaff(String role, String userid, String name, int age,
                                     String email, String username, String password,
                                     String contact) {
        // Check ID uniqueness
        for (User u : FileHandler.getallusers())
            if (u.getUserid().equals(userid))
                return new OperationResult(false, "Staff ID already exists.");

        // Check username uniqueness
        if (!FileHandler.checkusernameuniqueness(username, ""))
            return new OperationResult(false, "Username is already taken.");

        User newStaff = switch (role) {
            case "Manager"      -> new Manager(userid, name, age, email, username, password, contact);
            case "CounterStaff" -> new CounterStaff(userid, name, age, email, username, password, contact);
            case "Technician"   -> new Technician(userid, name, age, email, username, password, contact);
            default             -> null;
        };

        if (newStaff == null)
            return new OperationResult(false, "Invalid staff role: " + role);

        FileHandler.saveuser(newStaff);
        return new OperationResult(true, "Staff added successfully.");
    }

    public OperationResult updateStaff(String userid, String name, int age,
                                        String email, String username,
                                        String password, String contact) {
        User existing = getStaffById(userid);
        if (existing == null)
            return new OperationResult(false, "Staff not found.");

        // Check username uniqueness (exclude current user)
        if (!FileHandler.checkusernameuniqueness(username, userid))
            return new OperationResult(false, "Username is already taken.");

        User updated = switch (existing.getRole()) {
            case "Manager"      -> new Manager(userid, name, age, email, username, password, contact);
            case "CounterStaff" -> new CounterStaff(userid, name, age, email, username, password, contact);
            case "Technician"   -> new Technician(userid, name, age, email, username, password, contact);
            default             -> null;
        };

        if (updated == null)
            return new OperationResult(false, "Unknown role for staff ID: " + userid);

        FileHandler.updateUser(updated);
        return new OperationResult(true, "Staff updated successfully.");
    }

    public OperationResult deleteStaff(String userid) {
        User existing = getStaffById(userid);
        if (existing == null)
            return new OperationResult(false, "Staff not found.");

        FileHandler.deleteuser(userid);
        return new OperationResult(true, "Staff deleted successfully.");
    }

    public User getStaffById(String userid) {
        for (User u : FileHandler.getallusers())
            if (u.getUserid().equals(userid) && !u.getRole().equals("Customer"))
                return u;
        return null;
    }

    // ── Service Prices ────────────────────────────────────────────────────────

    public Double[] getPrices() {
        return FileHandler.getPrice();
    }

    public OperationResult savePrices(double normalPrice, double majorPrice) {
        if (normalPrice <= 0 || majorPrice <= 0)
            return new OperationResult(false, "Prices must be greater than zero.");

        FileHandler.savePrice(normalPrice, majorPrice);
        return new OperationResult(true, "Prices saved successfully.");
    }

    // ── Feedbacks ─────────────────────────────────────────────────────────────

    public ArrayList<Appointment> getAllFeedbacks() {
        return FileHandler.getAllAppointments();
    }

    public ArrayList<Appointment> getFeedbacksByStatus(String status) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : FileHandler.getAllAppointments()) {
            if (status.equals("All") || a.getStatus().equals(status))
                result.add(a);
        }
        return result;
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    public int[] getAppointmentStats() {
        // returns [total, completed, pending]
        ArrayList<Appointment> appointments = FileHandler.getAllAppointments();
        int total = 0, completed = 0, pending = 0;
        for (Appointment a : appointments) {
            total++;
            if (a.getStatus().equals("Completed")) completed++;
            else pending++;
        }
        return new int[]{total, completed, pending};
    }

    public double[] getRevenueStats() {
        // returns [totalRevenue, normalRevenue, majorRevenue]
        double total = 0, normal = 0, major = 0;
        for (Appointment a : FileHandler.getAllAppointments()) {
            if (a.getStatus().equals("Completed")) {
                total += a.getPrice();
                if (a.getServicetype().equalsIgnoreCase("Normal"))
                    normal += a.getPrice();
                else
                    major += a.getPrice();
            }
        }
        return new double[]{total, normal, major};
    }

    public int[] getServiceTypeCounts() {
        // returns [normalCount, majorCount]
        int normal = 0, major = 0;
        for (Appointment a : FileHandler.getAllAppointments()) {
            if (a.getStatus().equals("Completed")) {
                if (a.getServicetype().equalsIgnoreCase("Normal")) normal++;
                else major++;
            }
        }
        return new int[]{normal, major};
    }

    public int[] getUserCounts() {
        // returns [managers, counterStaff, technicians, customers]
        int managers = 0, cs = 0, techs = 0, customers = 0;
        for (User u : FileHandler.getallusers()) {
            switch (u.getRole()) {
                case "Manager"      -> managers++;
                case "CounterStaff" -> cs++;
                case "Technician"   -> techs++;
                case "Customer"     -> customers++;
            }
        }
        return new int[]{managers, cs, techs, customers};
    }

    public ArrayList<Appointment> getAllAppointmentsForReport() {
        return FileHandler.getAllAppointments();
    }
}
