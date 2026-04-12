package apu_asc;

public class Payment {
    private String paymentId;
    private String appointmentId;
    private String customerId;
    private double amount;
    private String serviceType;
    private int duration;
    private String paymentDate;
    private String paymentMethod;
    private String status;

    // Constructor
    public Payment(String paymentId, String appointmentId, String customerId, double amount, String serviceType, int duration, String paymentDate, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.amount = amount;
        this.serviceType = serviceType;
        this.duration = duration;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getAppointmentId() { return appointmentId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getServiceType() { return serviceType; }
    public int getDuration() { return duration; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }

    // Setters
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
}