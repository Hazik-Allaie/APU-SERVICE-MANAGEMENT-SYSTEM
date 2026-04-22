package apu_asc;

import java.util.ArrayList;
import java.util.Scanner;

public class CounterStaffDashboard {

    private CounterStaff loggedInUser;
    private CounterStaffService service;
    Scanner sc = new Scanner(System.in);

    // Constructor
    public CounterStaffDashboard(CounterStaff loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.service = new CounterStaffService();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n====== Counter Staff Dashboard ======");
            System.out.println("Welcome, " + loggedInUser.getName());
            System.out.println("1. Manage Customers");
            System.out.println("2. Manage Appointments");
            System.out.println("3. Collect Payment");
            System.out.println("4. Generate Receipt");
            System.out.println("5. Edit My Profile");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                manageCustomers();
            } else if (choice.equals("2")) {
                manageAppointments();
            } else if (choice.equals("3")) {
                collectPayment();
            } else if (choice.equals("4")) {
                generateReceipt();
            } else if (choice.equals("5")) {
                editProfile();
            } else if (choice.equals("6")) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    // ─────────────────────────────────────────────
    //  MANAGE CUSTOMERS
    // ─────────────────────────────────────────────

    private void manageCustomers() {
        while (true) {
            System.out.println("\n====== Manage Customers ======");
            System.out.println("1. Add Customer");
            System.out.println("2. Search Customer by ID");
            System.out.println("3. Search Customer by Name");
            System.out.println("4. View All Customers");
            System.out.println("5. Update Customer");
            System.out.println("6. Delete Customer");
            System.out.println("7. Back");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                addCustomer();
            } else if (choice.equals("2")) {
                searchCustomerById();
            } else if (choice.equals("3")) {
                searchCustomerByName();
            } else if (choice.equals("4")) {
                viewAllCustomers();
            } else if (choice.equals("5")) {
                updateCustomer();
            } else if (choice.equals("6")) {
                deleteCustomer();
            } else if (choice.equals("7")) {
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    private void addCustomer() {
        System.out.println("\n--- Add New Customer ---");

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Age: ");
        int age = Integer.parseInt(sc.nextLine());

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        System.out.print("Contact: ");
        String contact = sc.nextLine();

        OperationResult result = service.createCustomer(name, age, email, username, password, contact);
        System.out.println(result.getMessage());
    }

    private void searchCustomerById() {
        System.out.print("\nEnter Customer ID: ");
        String id = sc.nextLine();

        Customer customer = service.searchCustomerById(id);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        printCustomerDetails(customer);
    }

    private void searchCustomerByName() {
        System.out.print("\nEnter Customer Name: ");
        String name = sc.nextLine();

        ArrayList<Customer> results = service.searchCustomerByName(name);
        if (results.isEmpty()) {
            System.out.println("No customers found with that name.");
            return;
        }
        for (Customer c : results) {
            printCustomerDetails(c);
        }
    }

    private void viewAllCustomers() {
        ArrayList<Customer> customers = service.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        System.out.println("\n--- All Customers ---");
        for (Customer c : customers) {
            printCustomerDetails(c);
        }
    }

    private void updateCustomer() {
        System.out.print("\nEnter Customer ID to update: ");
        String id = sc.nextLine();

        Customer existing = service.searchCustomerById(id);
        if (existing == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("New Name (" + existing.getName() + "): ");
        String name = sc.nextLine();
        if (name.isEmpty()) name = existing.getName();

        System.out.print("New Age (" + existing.getAge() + "): ");
        String ageInput = sc.nextLine();
        int age = ageInput.isEmpty() ? existing.getAge() : Integer.parseInt(ageInput);

        System.out.print("New Email (" + existing.getEmail() + "): ");
        String email = sc.nextLine();
        if (email.isEmpty()) email = existing.getEmail();

        System.out.print("New Contact (" + existing.getContact() + "): ");
        String contact = sc.nextLine();
        if (contact.isEmpty()) contact = existing.getContact();

        OperationResult result = service.updateCustomer(id, name, age, email, contact);
        System.out.println(result.getMessage());
    }

    private void deleteCustomer() {
        System.out.print("\nEnter Customer ID to delete: ");
        String id = sc.nextLine();

        OperationResult result = service.deleteCustomer(id);
        System.out.println(result.getMessage());
    }

    private void printCustomerDetails(Customer c) {
        System.out.println("--------------------");
        System.out.println("ID      : " + c.getUserid());
        System.out.println("Name    : " + c.getName());
        System.out.println("Age     : " + c.getAge());
        System.out.println("Email   : " + c.getEmail());
        System.out.println("Username: " + c.getUsername());
        System.out.println("Contact : " + c.getContact());
    }

    // ─────────────────────────────────────────────
    //  MANAGE APPOINTMENTS
    // ─────────────────────────────────────────────

    private void manageAppointments() {
        while (true) {
            System.out.println("\n====== Manage Appointments ======");
            System.out.println("1. Create Appointment");
            System.out.println("2. Back");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                createAppointment();
            } else if (choice.equals("2")) {
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    private void createAppointment() {
        System.out.println("\n--- Create New Appointment ---");

        System.out.print("Customer ID: ");
        String customerId = sc.nextLine();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Time (HH:MM): ");
        String time = sc.nextLine();

        System.out.print("Service Type (Normal/Major): ");
        String serviceType = sc.nextLine();

        // Show available technicians for the given slot
        ArrayList<Technician> available = service.getAvailableTechnicians(date, time, serviceType);
        if (available.isEmpty()) {
            System.out.println("No technicians available for the selected date and time. Please choose a different slot.");
            return;
        }

        System.out.println("\nAvailable Technicians:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". [" + available.get(i).getUserid() + "] " + available.get(i).getName());
        }

        System.out.print("Select technician number: ");
        int techChoice;
        try {
            techChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection.");
            return;
        }

        if (techChoice < 1 || techChoice > available.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String technicianId = available.get(techChoice - 1).getUserid();

        System.out.print("Vehicle Details: ");
        String vehicleDetails = sc.nextLine();

        System.out.print("Comments (optional): ");
        String comments = sc.nextLine();

        OperationResult result = service.createAppointment(
                customerId, technicianId, date, time,
                serviceType, vehicleDetails, comments, loggedInUser);
        System.out.println(result.getMessage());
    }

    // ─────────────────────────────────────────────
    //  COLLECT PAYMENT
    // ─────────────────────────────────────────────

    private void collectPayment() {
        System.out.println("\n--- Collect Payment ---");

        System.out.print("Appointment ID: ");
        String appointmentId = sc.nextLine();

        System.out.print("Payment Method (Cash/Card/Online): ");
        String paymentMethod = sc.nextLine();

        OperationResult result = service.collectPayment(appointmentId, paymentMethod);
        System.out.println(result.getMessage());
    }

    // ─────────────────────────────────────────────
    //  GENERATE RECEIPT
    // ─────────────────────────────────────────────

    private void generateReceipt() {
        System.out.println("\n--- Generate Receipt ---");

        System.out.print("Payment ID: ");
        String paymentId = sc.nextLine();

        String receipt = service.generateReceipt(paymentId);
        System.out.println(receipt);
    }

    // ─────────────────────────────────────────────
    //  EDIT PROFILE
    // ─────────────────────────────────────────────

    private void editProfile() {
        System.out.println("\n--- Edit My Profile ---");
        System.out.println("Leave a field blank to keep the current value.");

        System.out.print("New Name (" + loggedInUser.getName() + "): ");
        String name = sc.nextLine();
        if (name.isEmpty()) name = loggedInUser.getName();

        System.out.print("New Age (" + loggedInUser.getAge() + "): ");
        String ageInput = sc.nextLine();
        int age = ageInput.isEmpty() ? loggedInUser.getAge() : Integer.parseInt(ageInput);

        System.out.print("New Email (" + loggedInUser.getEmail() + "): ");
        String email = sc.nextLine();
        if (email.isEmpty()) email = loggedInUser.getEmail();

        System.out.print("New Username (" + loggedInUser.getUsername() + "): ");
        String username = sc.nextLine();
        if (username.isEmpty()) username = loggedInUser.getUsername();

        System.out.print("New Contact (" + loggedInUser.getContact() + "): ");
        String contact = sc.nextLine();
        if (contact.isEmpty()) contact = loggedInUser.getContact();

        System.out.print("Current Password (required to save changes): ");
        String currentPassword = sc.nextLine();

        System.out.print("New Password (leave blank to keep current): ");
        String newPassword = sc.nextLine();
        if (newPassword.isEmpty()) newPassword = loggedInUser.getPassword();

        OperationResult result = service.updateProfile(
                loggedInUser, name, age, email, username, currentPassword, newPassword, contact);
        System.out.println(result.getMessage());
    }
}
