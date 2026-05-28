package apu_asc;

public class Validator {

    // ── Empty check ───────────────────────────────────────────────────────────
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // ── Name validation ───────────────────────────────────────────────────────
    public static String validateName(String name) {
        if (isEmpty(name))
            return "Name cannot be empty.";
        if (name.trim().length() < 2)
            return "Name must be at least 2 characters.";
        if (!name.trim().matches("[a-zA-Z ]+"))
            return "Name can only contain letters and spaces.";
        return null;
    }

    // ── Age validation ────────────────────────────────────────────────────────
    public static String validateAge(String ageStr) {
        if (isEmpty(ageStr))
            return "Age cannot be empty.";
        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 18 || age > 100)
                return "Age must be between 18 and 100.";
        } catch (NumberFormatException e) {
            return "Age must be a valid number.";
        }
        return null;
    }

    // ── Email validation ──────────────────────────────────────────────────────
    public static String validateEmail(String email) {
        if (isEmpty(email))
            return "Email cannot be empty.";
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            return "Email must be valid (e.g. user@example.com).";
        return null;
    }

    // ── Username validation ───────────────────────────────────────────────────
    public static String validateUsername(String username) {
        if (isEmpty(username))
            return "Username cannot be empty.";
        if (username.trim().length() < 4)
            return "Username must be at least 4 characters.";
        if (!username.trim().matches("[a-zA-Z0-9_]+"))
            return "Username can only contain letters, numbers and underscores.";
        return null;
    }

    // ── Password validation ───────────────────────────────────────────────────
    public static String validatePassword(String password) {
        if (isEmpty(password))
            return "Password cannot be empty.";
        if (password.length() < 6)
            return "Password must be at least 6 characters.";
        return null;
    }

    // ── Contact validation ────────────────────────────────────────────────────
    public static String validateContact(String contact) {
        if (isEmpty(contact))
            return "Contact cannot be empty.";
        if (!contact.trim().matches("[0-9+\\-() ]+"))
            return "Contact must contain numbers only.";
        if (contact.trim().replaceAll("[^0-9]", "").length() < 10)
            return "Contact must have at least 10 digits.";
        return null;
    }

    // ── Price validation ──────────────────────────────────────────────────────
    public static String validatePrice(String priceStr) {
        if (isEmpty(priceStr))
            return "Price cannot be empty.";
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0)
                return "Price must be greater than zero.";
        } catch (NumberFormatException e) {
            return "Price must be a valid number.";
        }
        return null;
    }

    // ── Staff ID validation ───────────────────────────────────────────────────
    public static String validateStaffId(String id) {
        if (isEmpty(id))
            return "Staff ID cannot be empty.";
        if (!id.trim().matches("[A-Z]{2}[0-9]{3}"))
            return "Staff ID must be 2 uppercase letters followed by 3 digits (e.g. MG001).";
        return null;
    }

    // ── Appointment ID validation ─────────────────────────────────────────────
    public static String validateAppointmentId(String id) {
        if (isEmpty(id))
            return "Appointment ID cannot be empty.";
        if (!id.trim().matches("AP[0-9]{3}"))
            return "Appointment ID must be in format AP followed by 3 digits (e.g. AP001).";
        return null;
    }

    // ── Payment ID validation ─────────────────────────────────────────────────
    public static String validatePaymentId(String id) {
        if (isEmpty(id))
            return "Payment ID cannot be empty.";
        if (!id.trim().matches("PAY[0-9]{3}"))
            return "Payment ID must be in format PAY followed by 3 digits (e.g. PAY001).";
        return null;
    }

    // ── Date validation ───────────────────────────────────────────────────────
    public static String validateDate(String date) {
        if (isEmpty(date))
            return "Date cannot be empty.";
        if (!date.trim().matches("\\d{4}-\\d{2}-\\d{2}"))
            return "Date must be in format YYYY-MM-DD.";
        try {
            java.time.LocalDate.parse(date.trim());
        } catch (Exception e) {
            return "Date is not a valid calendar date.";
        }
        return null;
    }

    // ── Time validation ───────────────────────────────────────────────────────
    public static String validateTime(String time) {
        if (isEmpty(time))
            return "Time cannot be empty.";
        if (!time.trim().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]"))
            return "Time must be in format HH:MM (e.g. 09:00).";
        return null;
    }

    // ── Vehicle details validation ────────────────────────────────────────────
    public static String validateVehicle(String vehicle) {
        if (isEmpty(vehicle))
            return "Vehicle details cannot be empty.";
        if (vehicle.trim().length() < 3)
            return "Vehicle details must be at least 3 characters.";
        return null;
    }

    // ── Feedback validation ───────────────────────────────────────────────────
    public static String validateFeedback(String feedback) {
        if (isEmpty(feedback))
            return "Feedback cannot be empty.";
        if (feedback.trim().length() < 5)
            return "Feedback must be at least 5 characters.";
        return null;
    }

    // ── Customer ID validation ────────────────────────────────────────────────
    public static String validateCustomerId(String id) {
        if (isEmpty(id))
            return "Customer ID cannot be empty.";
        if (!id.trim().matches("CU[0-9]{3}"))
            return "Customer ID must be in format CU followed by 3 digits (e.g. CU001).";
        return null;
    }

    // ── Validate all and return first error ───────────────────────────────────
    public static String validateAll(String... results) {
        for (String result : results)
            if (result != null) return result;
        return null;
    }
}