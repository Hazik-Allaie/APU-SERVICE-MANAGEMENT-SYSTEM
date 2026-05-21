/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apu_asc;



import java.util.ArrayList;

public class TechnicianService {

    public OperationResult updateProfile(Technician loggedInUser,
                                         String newName, int newAge,
                                         String newEmail, String newUsername,
                                         String currentPassword, String newPassword,
                                         String newContact) {

        if (!loggedInUser.getPassword().equals(currentPassword))
            return new OperationResult(false, "Current password is incorrect.");

        if (!FileHandler.checkusernameuniqueness(newUsername, loggedInUser.getUserid()))
            return new OperationResult(false, "Username is already taken.");

        loggedInUser.setName(newName);
        loggedInUser.setAge(newAge);
        loggedInUser.setEmail(newEmail);
        loggedInUser.setUsername(newUsername);
        loggedInUser.setPassword(newPassword);
        loggedInUser.setContact(newContact);

        FileHandler.updateUser(loggedInUser);
        return new OperationResult(true, "Profile updated successfully.");
    }

    public ArrayList<Appointment> getMyAppointments(String technicianId) {
        ArrayList<Appointment> all = FileHandler.getAllAppointments();
        ArrayList<Appointment> mine = new ArrayList<>();

        for (Appointment a : all) {
            if (a.getTechnicianid().equals(technicianId)) {
                mine.add(a);
            }
        }
        return mine;
    }

    public Appointment getAppointmentById(String appointmentId, String technicianId) {
        for (Appointment a : getMyAppointments(technicianId)) {
            if (a.getAppointmentid().equals(appointmentId)) {
                return a;
            }
        }
        return null;
    }

    public OperationResult markAsCompleted(String appointmentId, String technicianId) {
        Appointment appointment = getAppointmentById(appointmentId, technicianId);

        if (appointment == null)
            return new OperationResult(false, "Appointment not found or not assigned to you.");

        if (appointment.getStatus().equals("Completed"))
            return new OperationResult(false, "Appointment is already marked as Completed.");

        appointment.setStatus("Completed");
        FileHandler.updateAppointment(appointment);
        return new OperationResult(true, "Appointment marked as Completed.");
    }

    public OperationResult provideFeedback(String appointmentId, String technicianId, String feedback) {
        Appointment appointment = getAppointmentById(appointmentId, technicianId);

        if (appointment == null)
            return new OperationResult(false, "Appointment not found or not assigned to you.");

        if (feedback == null || feedback.trim().isEmpty())
            return new OperationResult(false, "Feedback cannot be empty.");

        String safeFeedback = feedback.replace(",", ";");
        appointment.setComments(safeFeedback);
        FileHandler.updateAppointment(appointment);
        return new OperationResult(true, "Feedback submitted successfully.");
    }
    // ── Submit Feedback ───────────────────────────────────────────────────────

public OperationResult submitJobFeedback(String appointmentId,
        String technicianId, String feedback) {
    Appointment appointment = getAppointmentById(appointmentId, technicianId);

    if (appointment == null)
        return new OperationResult(false,
            "Appointment not found or not assigned to you.");

    if (!appointment.getStatus().equals("Completed"))
        return new OperationResult(false,
            "Feedback can only be submitted for completed appointments.");

    if (feedback == null || feedback.trim().isEmpty())
        return new OperationResult(false, "Feedback cannot be empty.");

    String safeFeedback = feedback.replace(",", ";");
    appointment.setComments(safeFeedback);
    FileHandler.updateAppointment(appointment);
    return new OperationResult(true, "Feedback submitted successfully.");
}

// ── View Job Details ──────────────────────────────────────────────────────

public Appointment getJobDetails(String appointmentId, String technicianId) {
    return getAppointmentById(appointmentId, technicianId);
}

public String getCustomerNameForAppointment(String customerId) {
    ArrayList<User> users = FileHandler.getallusers();
    for (User u : users)
        if (u.getUserid().equals(customerId))
            return u.getName();
    return customerId;
}

public int[] getMyStats(String technicianId) {
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
        if (a.getTechnicianid().equals(technicianId)
                && a.getStatus().equals("Completed"))
            revenue += a.getPrice();
    return revenue;
}
// ── View My Schedule ──────────────────────────────────────────────────────

public java.util.LinkedHashMap<String, java.util.ArrayList<Appointment>> getWeeklySchedule(String technicianId) {
    java.util.LinkedHashMap<String, java.util.ArrayList<Appointment>> schedule = new java.util.LinkedHashMap<>();

    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);

    for (int i = 0; i < 7; i++) {
        java.time.LocalDate day = monday.plusDays(i);
        schedule.put(day.toString(), new java.util.ArrayList<>());
    }

    for (Appointment a : FileHandler.getAllAppointments()) {
        if (!a.getTechnicianid().equals(technicianId)) continue;
        if (schedule.containsKey(a.getDate()))
            schedule.get(a.getDate()).add(a);
    }

    return schedule;
}

public String getDayName(String date) {
    try {
        java.time.LocalDate d = java.time.LocalDate.parse(date);
        return d.getDayOfWeek().toString().substring(0, 1)
            + d.getDayOfWeek().toString().substring(1).toLowerCase();
    } catch (Exception e) {
        return date;
    }
}

// ── Job History ───────────────────────────────────────────────────────────

public java.util.ArrayList<Appointment> getJobHistory(String technicianId) {
    java.util.ArrayList<Appointment> history = new java.util.ArrayList<>();
    for (Appointment a : FileHandler.getAllAppointments()) {
        if (a.getTechnicianid().equals(technicianId)
                && a.getStatus().equals("Completed"))
            history.add(a);
    }
    return history;
}

public java.util.ArrayList<Appointment> getJobHistoryByDateRange(
        String technicianId, String fromDate, String toDate) {
    java.util.ArrayList<Appointment> result = new java.util.ArrayList<>();
    try {
        java.time.LocalDate from = java.time.LocalDate.parse(fromDate);
        java.time.LocalDate to   = java.time.LocalDate.parse(toDate);
        for (Appointment a : FileHandler.getAllAppointments()) {
            if (!a.getTechnicianid().equals(technicianId)) continue;
            if (!a.getStatus().equals("Completed")) continue;
            java.time.LocalDate apptDate = java.time.LocalDate.parse(a.getDate());
            if (!apptDate.isBefore(from) && !apptDate.isAfter(to))
                result.add(a);
        }
    } catch (Exception e) {
        System.out.println("Date parse error: " + e.getMessage());
    }
    return result;
}

public double getJobHistoryRevenue(java.util.ArrayList<Appointment> jobs) {
    double total = 0;
    for (Appointment a : jobs)
        total += a.getPrice();
    return total;
}

public int[] getJobHistoryStats(java.util.ArrayList<Appointment> jobs) {
    // returns [total, normalCount, majorCount]
    int normal = 0, major = 0;
    for (Appointment a : jobs) {
        if (a.getServicetype().equalsIgnoreCase("Normal")) normal++;
        else major++;
    }
    return new int[]{jobs.size(), normal, major};
}
}
