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
    // ── View All Appointments ─────────────────────────────────────────────────

public ArrayList<Appointment> getAppointmentsByStatus(String status) {
    ArrayList<Appointment> result = new ArrayList<>();
    for (Appointment a : FileHandler.getAllAppointments()) {
        if (status.equals("All") || a.getStatus().equals(status))
            result.add(a);
    }
    return result;
}

public ArrayList<Appointment> getAppointmentsByTechnician(String technicianId) {
    ArrayList<Appointment> result = new ArrayList<>();
    for (Appointment a : FileHandler.getAllAppointments()) {
        if (a.getTechnicianid().equals(technicianId))
            result.add(a);
    }
    return result;
}

// ── Technician Performance ────────────────────────────────────────────────

public ArrayList<User> getAllTechnicians() {
    ArrayList<User> result = new ArrayList<>();
    for (User u : FileHandler.getallusers())
        if (u.getRole().equals("Technician"))
            result.add(u);
    return result;
}

public int[] getTechnicianStats(String technicianId) {
    // returns [total, completed, pending, inProgress]
    int total = 0, completed = 0, pending = 0, inProgress = 0;
    for (Appointment a : FileHandler.getAllAppointments()) {
        if (!a.getTechnicianid().equals(technicianId)) continue;
        total++;
        switch (a.getStatus()) {
            case "Completed"   -> completed++;
            case "Pending"     -> pending++;
            case "In Progress" -> inProgress++;
        }
    }
    return new int[]{total, completed, pending, inProgress};
}

public double getTechnicianRevenue(String technicianId) {
    double revenue = 0;
    for (Appointment a : FileHandler.getAllAppointments())
        if (a.getTechnicianid().equals(technicianId) && a.getStatus().equals("Completed"))
            revenue += a.getPrice();
    return revenue;
}
// ── View All Payments ─────────────────────────────────────────────────────

public ArrayList<Payment> getAllPayments() {
    return FileHandler.getAllPayments();
}

public ArrayList<Payment> getPaymentsByMethod(String method) {
    ArrayList<Payment> result = new ArrayList<>();
    for (Payment p : FileHandler.getAllPayments()) {
        if (method.equals("All") || p.getPaymentMethod().equals(method))
            result.add(p);
    }
    return result;
}

public ArrayList<Payment> getPaymentsByDateRange(String fromDate, String toDate) {
    ArrayList<Payment> result = new ArrayList<>();
    try {
        java.time.LocalDate from = java.time.LocalDate.parse(fromDate);
        java.time.LocalDate to   = java.time.LocalDate.parse(toDate);
        for (Payment p : FileHandler.getAllPayments()) {
            java.time.LocalDate payDate =
                java.time.LocalDate.parse(p.getPaymentDate());
            if (!payDate.isBefore(from) && !payDate.isAfter(to))
                result.add(p);
        }
    } catch (Exception e) {
        System.out.println("Date parse error: " + e.getMessage());
    }
    return result;
}

public double getTotalRevenue() {
    double total = 0;
    for (Payment p : FileHandler.getAllPayments())
        total += p.getAmount();
    return total;
}

public double getRevenueByMethod(String method) {
    double total = 0;
    for (Payment p : FileHandler.getAllPayments())
        if (p.getPaymentMethod().equals(method))
            total += p.getAmount();
    return total;
}

public int getPaymentCountByMethod(String method) {
    int count = 0;
    for (Payment p : FileHandler.getAllPayments())
        if (p.getPaymentMethod().equals(method))
            count++;
    return count;
}

// ── System Statistics ─────────────────────────────────────────────────────

public java.util.LinkedHashMap<String, Integer> getAppointmentsByMonth() {
    java.util.LinkedHashMap<String, Integer> result = new java.util.LinkedHashMap<>();
    String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                       "Jul","Aug","Sep","Oct","Nov","Dec"};
    for (String m : months) result.put(m, 0);

    for (Appointment a : FileHandler.getAllAppointments()) {
        try {
            int month = java.time.LocalDate.parse(a.getDate()).getMonthValue();
            String key = months[month - 1];
            result.put(key, result.get(key) + 1);
        } catch (Exception e) {}
    }
    return result;
}

public java.util.LinkedHashMap<String, Double> getRevenueByMonth() {
    java.util.LinkedHashMap<String, Double> result = new java.util.LinkedHashMap<>();
    String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                       "Jul","Aug","Sep","Oct","Nov","Dec"};
    for (String m : months) result.put(m, 0.0);

    for (Appointment a : FileHandler.getAllAppointments()) {
        if (!a.getStatus().equals("Completed")) continue;
        try {
            int month = java.time.LocalDate.parse(a.getDate()).getMonthValue();
            String key = months[month - 1];
            result.put(key, result.get(key) + a.getPrice());
        } catch (Exception e) {}
    }
    return result;
}

public java.util.LinkedHashMap<String, Integer> getAppointmentsByDayOfWeek() {
    java.util.LinkedHashMap<String, Integer> result = new java.util.LinkedHashMap<>();
    String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    for (String d : days) result.put(d, 0);

    for (Appointment a : FileHandler.getAllAppointments()) {
        try {
            java.time.DayOfWeek dow =
                java.time.LocalDate.parse(a.getDate()).getDayOfWeek();
            String key = days[dow.getValue() - 1];
            result.put(key, result.get(key) + 1);
        } catch (Exception e) {}
    }
    return result;
}

public java.util.LinkedHashMap<String, Integer> getTopTechnicians() {
    java.util.LinkedHashMap<String, Integer> result = new java.util.LinkedHashMap<>();
    for (Appointment a : FileHandler.getAllAppointments()) {
        if (!a.getStatus().equals("Completed")) continue;
        String id = a.getTechnicianid();
        result.put(id, result.getOrDefault(id, 0) + 1);
    }
    return result;
}

public String getTechnicianName(String technicianId) {
    for (User u : FileHandler.getallusers())
        if (u.getUserid().equals(technicianId))
            return u.getName();
    return technicianId;
}
}
