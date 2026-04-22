package apu_asc;

import java.util.ArrayList;
import java.util.Scanner;

public class CustomerDashboard {

    private Customer customer;
    private CustomerService customerService;
    Scanner sc = new Scanner(System.in);

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        this.customerService = new CustomerService();
    }

    public void showMenu() {
        while (true) {
            System.out.println("===== Customer Dashboard =====");
            System.out.println("Welcome, " + customer.getName());
            System.out.println("1. View My Appointments");
            System.out.println("2. View Appointment Details");
            System.out.println("3. Submit Feedback");
            System.out.println("4. View Payment History");
            System.out.println("5. Update Profile");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                viewMyAppointments();
            } else if (choice.equals("2")) {
                viewAppointmentDetails();
            } else if (choice.equals("3")) {
                submitFeedback();
            } else if (choice.equals("4")) {
                viewPaymentHistory();
            } else if (choice.equals("5")) {
                updateProfile();
            } else if (choice.equals("6")) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    private void viewMyAppointments() {
        ArrayList<Appointment> appointments = customerService.getMyAppointments(customer.getUserid());

        System.out.println("===== My Appointments =====");

        if (appointments.isEmpty()) {
            System.out.println("You have no appointments.");
            return;
        }

        for (Appointment appointment : appointments) {
            System.out.println("--------------------");
            System.out.println("Appointment ID : " + appointment.getAppointmentid());
            System.out.println("Date           : " + appointment.getDate());
            System.out.println("Time           : " + appointment.getTime());
            System.out.println("Service Type   : " + appointment.getServicetype());
            System.out.println("Status         : " + appointment.getStatus());
        }
    }

    private void viewAppointmentDetails() {
        System.out.print("Enter Appointment ID: ");
        String appointmentId = sc.nextLine();

        Appointment appointment = customerService.getAppointmentById(appointmentId, customer.getUserid());

        if (appointment == null) {
            System.out.println("Appointment not found or does not belong to you.");
            return;
        }

        System.out.println("===== Appointment Details =====");
        System.out.println("Appointment ID : " + appointment.getAppointmentid());
        System.out.println("Date           : " + appointment.getDate());
        System.out.println("Time           : " + appointment.getTime());
        System.out.println("Service Type   : " + appointment.getServicetype());
        System.out.println("Price          : RM " + appointment.getPrice());
        System.out.println("Vehicle        : " + appointment.getVehicleDetails());
        System.out.println("Comments       : " + appointment.getComments());
        System.out.println("Status         : " + appointment.getStatus());
        System.out.println("Technician ID  : " + appointment.getTechnicianid());
    }

    private void submitFeedback() {
        System.out.print("Enter Appointment ID to leave feedback: ");
        String appointmentId = sc.nextLine();

        System.out.print("Enter your feedback: ");
        String feedback = sc.nextLine();

        OperationResult result = customerService.submitFeedback(appointmentId, customer.getUserid(), feedback);
        System.out.println(result.getMessage());
    }

    private void viewPaymentHistory() {
        ArrayList<Payment> payments = customerService.getMyPayments(customer.getUserid());

        System.out.println("===== My Payment History =====");

        if (payments.isEmpty()) {
            System.out.println("No payment records found.");
            return;
        }

        for (Payment payment : payments) {
            System.out.println("--------------------");
            System.out.println("Payment ID     : " + payment.getPaymentId());
            System.out.println("Appointment ID : " + payment.getAppointmentId());
            System.out.println("Service Type   : " + payment.getServiceType());
            System.out.println("Amount         : RM " + payment.getAmount());
            System.out.println("Payment Date   : " + payment.getPaymentDate());
            System.out.println("Payment Method : " + payment.getPaymentMethod());
            System.out.println("Status         : " + payment.getStatus());
        }
    }

    private void updateProfile() {
        System.out.println("===== Update Profile =====");
        System.out.println("Leave a field blank to keep current value.");

        System.out.print("New Name (" + customer.getName() + "): ");
        String name = sc.nextLine();
        if (name.isEmpty()) name = customer.getName();

        System.out.print("New Age (" + customer.getAge() + "): ");
        String ageInput = sc.nextLine();
        int age = ageInput.isEmpty() ? customer.getAge() : Integer.parseInt(ageInput);

        System.out.print("New Email (" + customer.getEmail() + "): ");
        String email = sc.nextLine();
        if (email.isEmpty()) email = customer.getEmail();

        System.out.print("New Username (" + customer.getUsername() + "): ");
        String username = sc.nextLine();
        if (username.isEmpty()) username = customer.getUsername();

        System.out.print("New Contact (" + customer.getContact() + "): ");
        String contact = sc.nextLine();
        if (contact.isEmpty()) contact = customer.getContact();

        System.out.print("Current Password (required): ");
        String currentPassword = sc.nextLine();

        System.out.print("New Password (leave blank to keep current): ");
        String newPassword = sc.nextLine();
        if (newPassword.isEmpty()) newPassword = customer.getPassword();

        OperationResult result = customerService.updateProfile(
                customer, name, age, email, username, currentPassword, newPassword, contact);

        System.out.println(result.getMessage());
    }
}
