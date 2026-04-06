
public class Appointment {
    private String appointmentid ;
    private final String customerid;
    private String technicianid;
    private String counterstaffid;
    private String date;
    private String time;
    private String servicetype;
    private double price;
    private String comments;
    private String status;
    //Constructor 
public Appointment(String appointmentid ,String customerid,String technicianid ,String date,String servicetype,String time ,double price,String comments,String status){
this.appointmentid= appointmentid;
this.customerid=customerid;
this.technicianid=technicianid;
this.date=date;
this.servicetype=servicetype;
this.time=time;
this.price=price;
this.comments=comments;
this .status= status ;
}

//getter
public String getAppointmentid(){
return appointmentid;}

public String getCustomerid(){
return customerid;}


}

