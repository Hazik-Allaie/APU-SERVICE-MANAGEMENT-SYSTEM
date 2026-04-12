package apu_asc;


public class Appointment {
    private String appointmentid;
    private String customerid;
    private String technicianid;
    private String counterstaffid;
    private String date;
    private String time;
    private String servicetype;
    private double price;
    private String vehicleDetails;
    private String comments;
    private String status;

    // Constructor
    public Appointment(String appointmentid, String customerid, String technicianid, String counterstaffid, String date, String time, String servicetype, double price, String vehicleDetails, String comments, String status) {
        this.appointmentid = appointmentid;
        this.customerid = customerid;
        this.technicianid = technicianid;
        this.counterstaffid = counterstaffid;
        this.date = date;
        this.time = time;
        this.servicetype = servicetype;
        this.price = price;
        this.vehicleDetails = vehicleDetails;
        this.comments = comments;
        this.status = status;
    }

    // Getters
    public String getAppointmentid() { return appointmentid; }
    public String getCustomerid() { return customerid; }
    public String getTechnicianid() { return technicianid; }
    public String getCounterstaffid() { return counterstaffid; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getServicetype() { return servicetype; }
    public double getPrice() { return price; }
    public String getVehicleDetails() { return vehicleDetails; }
    public String getComments() { return comments; }
    public String getStatus() { return status; }

    // Setters
    public void setAppointmentid(String appointmentid) { this.appointmentid = appointmentid; }
    public void setCustomerid(String customerid) { this.customerid = customerid; }
    public void setTechnicianid(String technicianid) { this.technicianid = technicianid; }
    public void setCounterstaffid(String counterstaffid) { this.counterstaffid = counterstaffid; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setServicetype(String servicetype) { this.servicetype = servicetype; }
    public void setPrice(double price) { this.price = price; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }
    public void setComments(String comments) { this.comments = comments; }
    public void setStatus(String status) { this.status = status; }
    
         @Override
    
public String toString(){
    return appointmentid + "," + 
           customerid + "," + 
           technicianid + "," +
           counterstaffid + "," +
           date + "," +
           time + "," +
           servicetype + "," +
           price + "," +
           comments + "," +
           status;
}
    }
