package apu_asc;

import java.util.ArrayList;

public class CustomerService {

    public OperationResult updateProfile(Customer loggedInUser, String newName, int newAge,
            String newEmail, String newUsername, String currentPassword,
            String newPassword, String newContact) {

        if (!loggedInUser.getPassword().equals(currentPassword))
            return new OperationResult(false, "Current password is incorrect");

        if (!FileHandler.checkusernameuniqueness(newUsername, loggedInUser.getUserid()))
            return new OperationResult(false, "Username is already taken");

        loggedInUser.setName(newName);
        loggedInUser.setAge(newAge);
        loggedInUser.setEmail(newEmail);
        loggedInUser.setUsername(newUsername);
        loggedInUser.setPassword(newPassword);
        loggedInUser.setContact(newContact);

        FileHandler.updateUser(loggedInUser);
        return new OperationResult(true, "Profile updated successfully");
    }

    public ArrayList<Appointment> getMyAppointments(String customerId) {
        ArrayList<Appointment> allAppointments = FileHandler.getAllAppointments();
        ArrayList<Appointment> myAppointments = new ArrayList<>();

        for (Appointment appointment : allAppointments) {
            if (appointment.getCustomerid().equals(customerId))
                myAppointments.add(appointment);
        }

        return myAppointments;
    }

    public Appointment getAppointmentById(String appointmentId, String customerId) {
        ArrayList<Appointment> myAppointments = getMyAppointments(customerId);

        for (Appointment appointment : myAppointments) {
            if (appointment.getAppointmentid().equals(appointmentId))
                return appointment;
        }

        return null;
    }

    public OperationResult submitFeedback(String appointmentId, String customerId, String feedback) {
        Appointment appointment = getAppointmentById(appointmentId, customerId);

        if (appointment == null)
            return new OperationResult(false, "Appointment not found or does not belong to you");

        if (!appointment.getStatus().equals("Completed"))
            return new OperationResult(false, "Feedback can only be submitted for completed appointments");

        appointment.setComments(feedback);
        FileHandler.updateAppointment(appointment);
        return new OperationResult(true, "Feedback submitted successfully");
    }

    public ArrayList<Payment> getMyPayments(String customerId) {
        ArrayList<Payment> allPayments = FileHandler.getAllPayments();
        ArrayList<Payment> myPayments = new ArrayList<>();

        for (Payment payment : allPayments) {
            if (payment.getCustomerId().equals(customerId))
                myPayments.add(payment);
        }

        return myPayments;
    }
}
