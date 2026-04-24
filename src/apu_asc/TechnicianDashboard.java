/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apu_asc;


import java.util.ArrayList;
import java.util.Scanner;

public class TechnicianDashboard {

    private Technician technician;
    private TechnicianService service;
    private Scanner sc;

    public TechnicianDashboard(Technician technician) {
        this.technician = technician;
        this.service = new TechnicianService();
        this.sc = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n====== Technician Dashboard ======");
            System.out.println(" Welcome, " + technician.getName());
            System.out.println("1. View My Appointments");
            System.out.println("2. View Appointment Details");
            System.out.println("3. Mark Appointment as Completed");
            System.out.println("4. Provide Feedback for Appointment");
            System.out.println("5. Edit My Profile");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": viewMyAppointments();    break;
                case "2": viewAppointmentDetails(); break;
                case "3": markCompleted();          break;
                case "4": provideFeedback();        break;
                case "5": editProfile();            break;
                case "6":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewMyAppointments() {
        ArrayList<Appointment> appointments = service.getMyAppointments(technician.getUserid());

        System.out.println("\n===== My Appointments =====");
        if (appointments.isEmpty()) {
            System.out.println("No appointments assigned to you.");
            return;
        }

        for (Appointment a : appointments) {
            printAppointmentSummary(a);
        }
    }

    private void viewAppointmentDetails() {
        System.out.print("Enter Appointment ID: ");
        String apptId = sc.nextLine().trim();

        Appointment a = service.getAppointmentById(apptId, technician.getUserid());
        if (a == null) {
            System.out.println("Appointment not found or not assigned to you.");
            return;
        }

        System.out.println("\n===== Appointment Details =====");
        System.out.println("Appointment ID  : " + a.getAppointmentid());
        System.out.println("Customer ID     : " + a.getCustomerid());
        System.out.println("Counter Staff ID: " + a.getCounterstaffid());
        System.out.println("Date            : " + a.getDate());
        System.out.println("Time            : " + a.getTime());
        System.out.println("Service Type    : " + a.getServicetype());
        System.out.println("Price           : RM " + String.format("%.2f", a.getPrice()));
        System.out.println("Vehicle Details : " + a.getVehicleDetails());
        System.out.println("Status          : " + a.getStatus());
        System.out.println("Comments        : " + (a.getComments().equals("N/A") ? "(none yet)" : a.getComments()));
    }

    private void markCompleted() {
        ArrayList<Appointment> appointments = service.getMyAppointments(technician.getUserid());

        System.out.println("\n===== Pending Appointments =====");
        boolean anyPending = false;
        for (Appointment a : appointments) {
            if (!a.getStatus().equals("Completed")) {
                printAppointmentSummary(a);
                anyPending = true;
            }
        }
        if (!anyPending) {
            System.out.println("No pending appointments.");
            return;
        }

        System.out.print("Enter Appointment ID to mark as Completed: ");
        String apptId = sc.nextLine().trim();

        OperationResult result = service.markAsCompleted(apptId, technician.getUserid());
        System.out.println(result.getMessage());
    }

    private void provideFeedback() {
        System.out.print("Enter Appointment ID: ");
        String apptId = sc.nextLine().trim();

        Appointment a = service.getAppointmentById(apptId, technician.getUserid());
        if (a == null) {
            System.out.println("Appointment not found or not assigned to you.");
            return;
        }

        if (!a.getComments().equals("N/A") && !a.getComments().isEmpty()) {
            System.out.println("Existing feedback: " + a.getComments());
            System.out.print("Do you want to update it? (yes/no): ");
            String confirm = sc.nextLine().trim();
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Feedback unchanged.");
                return;
            }
        }

        System.out.print("Enter your feedback: ");
        String feedback = sc.nextLine().trim();

        OperationResult result = service.provideFeedback(apptId, technician.getUserid(), feedback);
        System.out.println(result.getMessage());
    }

    private void editProfile() {
        System.out.println("\n===== Edit Profile =====");
        System.out.println("(Press Enter to keep current value)");

        System.out.print("New Name (" + technician.getName() + "): ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = technician.getName();

        System.out.print("New Age (" + technician.getAge() + "): ");
        String ageInput = sc.nextLine().trim();
        int age = ageInput.isEmpty() ? technician.getAge() : Integer.parseInt(ageInput);

        System.out.print("New Email (" + technician.getEmail() + "): ");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) email = technician.getEmail();

        System.out.print("New Username (" + technician.getUsername() + "): ");
        String username = sc.nextLine().trim();
        if (username.isEmpty()) username = technician.getUsername();

        System.out.print("Current Password (required to save changes): ");
        String currentPassword = sc.nextLine().trim();

        System.out.print("New Password (leave blank to keep current): ");
        String newPassword = sc.nextLine().trim();
        if (newPassword.isEmpty()) newPassword = technician.getPassword();

        System.out.print("New Contact (" + technician.getContact() + "): ");
        String contact = sc.nextLine().trim();
        if (contact.isEmpty()) contact = technician.getContact();

        OperationResult result = service.updateProfile(
                technician, name, age, email, username, currentPassword, newPassword, contact);
        System.out.println(result.getMessage());
    }

    private void printAppointmentSummary(Appointment a) {
        System.out.println("--------------------");
        System.out.println("ID      : " + a.getAppointmentid());
        System.out.println("Date    : " + a.getDate() + "  " + a.getTime());
        System.out.println("Service : " + a.getServicetype());
        System.out.println("Vehicle : " + a.getVehicleDetails());
        System.out.println("Status  : " + a.getStatus());
    }
}
