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
}
