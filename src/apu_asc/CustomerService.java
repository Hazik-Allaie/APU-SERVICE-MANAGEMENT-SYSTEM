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
    // ── Cancel Appointment ────────────────────────────────────────────────────

public OperationResult cancelAppointment(String appointmentId, String customerId) {
    Appointment appointment = getAppointmentById(appointmentId, customerId);

    if (appointment == null)
        return new OperationResult(false,
            "Appointment not found or does not belong to you.");

    if (appointment.getStatus().equals("Completed"))
        return new OperationResult(false,
            "Cannot cancel a completed appointment.");

    if (appointment.getStatus().equals("In Progress"))
        return new OperationResult(false,
            "Cannot cancel an appointment that is In Progress.");

    appointment.setStatus("Cancelled");
    FileHandler.updateAppointment(appointment);
    return new OperationResult(true, "Appointment cancelled successfully.");
}

// ── Service History ───────────────────────────────────────────────────────

public ArrayList<Appointment> getServiceHistory(String customerId) {
    ArrayList<Appointment> result = new ArrayList<>();
    for (Appointment a : FileHandler.getAllAppointments())
        if (a.getCustomerid().equals(customerId)
                && a.getStatus().equals("Completed"))
            result.add(a);
    return result;
}

// ── View Receipt ──────────────────────────────────────────────────────────

public Payment getPaymentByAppointmentId(String appointmentId, String customerId) {
    for (Payment p : FileHandler.getAllPayments())
        if (p.getAppointmentId().equals(appointmentId)
                && p.getCustomerId().equals(customerId))
            return p;
    return null;
}

public Payment getPaymentById(String paymentId, String customerId) {
    for (Payment p : FileHandler.getAllPayments())
        if (p.getPaymentId().equals(paymentId)
                && p.getCustomerId().equals(customerId))
            return p;
    return null;
}

public String generateCustomerReceipt(String paymentId, String customerId) {
    Payment payment = getPaymentById(paymentId, customerId);
    if (payment == null)
        return "Error: Payment not found or does not belong to you.";

    Appointment appointment = getAppointmentById(
        payment.getAppointmentId(), customerId);
    if (appointment == null)
        return "Error: Appointment not found.";

    StringBuilder sb = new StringBuilder();
    sb.append("========================================\n");
    sb.append("           APU-ASC RECEIPT\n");
    sb.append("========================================\n");
    sb.append("Payment ID      : ").append(payment.getPaymentId()).append("\n");
    sb.append("Appointment ID  : ").append(payment.getAppointmentId()).append("\n");
    sb.append("Customer ID     : ").append(customerId).append("\n");
    sb.append("Service Type    : ").append(payment.getServiceType()).append("\n");
    sb.append("Duration        : ").append(payment.getDuration()).append(" mins\n");
    sb.append("Amount          : RM ").append(
        String.format("%.2f", payment.getAmount())).append("\n");
    sb.append("Payment Date    : ").append(payment.getPaymentDate()).append("\n");
    sb.append("Payment Method  : ").append(payment.getPaymentMethod()).append("\n");
    sb.append("Appointment Date: ").append(appointment.getDate()).append("\n");
    sb.append("Vehicle         : ").append(appointment.getVehicleDetails()).append("\n");
    sb.append("Technician ID   : ").append(appointment.getTechnicianid()).append("\n");
    sb.append("Status          : ").append(payment.getStatus()).append("\n");
    sb.append("========================================\n");
    return sb.toString();
}
}
