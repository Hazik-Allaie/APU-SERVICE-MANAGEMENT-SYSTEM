package apu_asc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CounterStaffService {
    private static String generateNextCustomerId(){
        ArrayList<User> users = FileHandler.getallusers();
        int max = 0;
        
        for (User user : users){
            if(user instanceof Customer){
                String id = user.getUserid();
                
                if(id != null && id.startsWith("CU")){
                    String numberPart = id.substring(2);
                    
                    try{
                        int num = Integer.parseInt(numberPart);
                        if(num>max){
                            max=num;
                        }
                    }
                    catch(NumberFormatException e){
                    }
                }
            }
        }
        
        int next = max +1;
        
        return String.format("CU%03d", next);
    }
    
    public OperationResult updateProfile(CounterStaff loggedInUser, String newName, String newEmail, String newUsername, String currentPassword, String newPassword, String newContact){
        if(!(loggedInUser.getPassword().equals(currentPassword)))
            return new OperationResult(false, "Current password is incorrect");
        
        if(!FileHandler.checkusernameuniqueness(newUsername, loggedInUser.getUserid()))
            return new OperationResult(false, "Username not available");
        
        loggedInUser.setName(newName);
        loggedInUser.setEmail(newEmail);
        loggedInUser.setUsername(newUsername);
        loggedInUser.setPassword(newPassword);
        loggedInUser.setContact(newContact);
        
        FileHandler.updateUser(loggedInUser);
        return new OperationResult(true, "Profile updated successfully");
    }
    
    public OperationResult createCustomer(String name, int age, String email, String username, String password, String contact){
        String userid = generateNextCustomerId();
            
        if(!FileHandler.checkusernameuniqueness(username, ""))
            return new OperationResult(false, "Username not available");
        
        User newCustomer = new Customer(userid, name, age, email, username, password, contact);
        
        FileHandler.saveuser(newCustomer);
        return new OperationResult(true, "New Customer added Successfully");
    }
    
    public Customer searchCustomerById(String userid){
        ArrayList<User> users = FileHandler.getallusers();
        
        for(User user: users){
            if(user instanceof Customer){
                if(user.getUserid().equals(userid))
                    return (Customer) user;
            }
        }
        return null;
    }
    
    public ArrayList<Customer> searchCustomerByName(String customerName){
        ArrayList<User> users = FileHandler.getallusers();
        ArrayList<Customer> customer = new ArrayList<>();
        
        for(User user: users){
            if(user instanceof Customer){
                if(user.getName().equalsIgnoreCase(customerName))
                    customer.add((Customer)user);
            }
        }
        
        return customer;
    }
    
    public ArrayList<Customer> getAllCustomers(){
        ArrayList<User> users = FileHandler.getallusers();
        ArrayList<Customer> customer = new ArrayList<>();
        
        for(User user: users){
            if(user instanceof Customer)
                customer.add((Customer)user);
        }
        
        return customer;
    }
    
    public OperationResult updateCustomer(String userId, String newName, int newAge, String newEmail, String newContact){
        Customer current_data = searchCustomerById(userId);
        if(current_data == null)
            return new OperationResult(false, "User not Found");
        
        
        Customer updatedCustomer = new Customer(userId, newName, newAge, newEmail, current_data.getUsername(), current_data.getPassword(), newContact);
        
        FileHandler.updateUser(updatedCustomer);
        return new OperationResult(true, "Customer updated succesfully");
    }
    
    public OperationResult deleteCustomer(String userId){
        Customer current_data = searchCustomerById(userId);
        if(current_data == null)
            return new OperationResult(false, "User not Found");
        
        ArrayList<Appointment> appointments = FileHandler.getAllAppointments();
        for(Appointment appointment: appointments){
            if(appointment.getCustomerid().equals(userId))
                return new OperationResult(false, "Can't delete Customer pending appointments");
        }
        
        ArrayList<Payment> payments = FileHandler.getAllPayments();
        for(Payment payment: payments){
            if(payment.getCustomerId().equals(userId) && payment.getStatus().equals("Unpaid"))
                return new OperationResult(false, "Can't delete Customer pending payments");
        }
        
        FileHandler.deleteuser(userId);
        return new OperationResult(true, "Customer deleted successfully");
    }
    
    private boolean isOverlapping(String technicianId, ArrayList<Appointment> existingAppointments, String date, String time, String serviceType){
        ArrayList<Integer> existingStrTime = new ArrayList<>();
        ArrayList<Integer> existingEndTime = new ArrayList<>();
        
        for(Appointment appointment: existingAppointments){
            if(appointment.getTechnicianid().equals(technicianId)){
                if(appointment.getDate().equals(date)){
                    String existingTime = appointment.getTime();
                    String[] parts = existingTime.split(":");
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    int totalMinutes = (hours * 60) + minutes;
                    existingStrTime.add(totalMinutes);
                    String existingServiceType = appointment.getServicetype();
                    if(existingServiceType.equalsIgnoreCase("Normal"))
                        existingEndTime.add(totalMinutes + 60);
                    else
                        existingEndTime.add(totalMinutes + 180);
                }
            }
        }
        
        String[] part = time.split(":");
        int newHours = Integer.parseInt(part[0]);
        int newMinutes = Integer.parseInt(part[1]);
        int newStrTime = (newHours * 60) + newMinutes;
        int newEndTime;
        if( serviceType.equalsIgnoreCase("Normal"))
            newEndTime = newStrTime + 60;
        else
            newEndTime = newStrTime + 180;
        
        for(int i = 0; i< existingStrTime.size(); i++){
            if(newStrTime < existingEndTime.get(i) && newEndTime > existingStrTime.get(i))
                return true;
        }
        return false;
    }
    
    public ArrayList<Technician> getAvailableTechnicians(String date, String time, String serviceType){
        ArrayList<Technician> availableTechnicians = new ArrayList<>();
        ArrayList<User> allUsers = FileHandler.getallusers();
        ArrayList<Appointment> allAppointments = FileHandler.getAllAppointments();
        
        for(User user: allUsers){
            if(user instanceof Technician){
                if(!isOverlapping(user.getUserid(), allAppointments, date, time, serviceType))
                    availableTechnicians.add((Technician)user);
            }
        }
        
        return availableTechnicians;
    }
    
    private static String generateNextAppointmentId(){
        ArrayList<Appointment> appointments = FileHandler.getAllAppointments();
        int max = 0;
        
        for (Appointment appointment : appointments){
            String id = appointment.getAppointmentid();
                
            if(id != null && id.startsWith("AP")){
                String numberPart = id.substring(2);
                    
                try{
                    int num = Integer.parseInt(numberPart);
                    if(num>max){
                        max=num;
                    }
                }
                catch(NumberFormatException e){
                }
            }
        }
        
        int next = max +1;
        
        return String.format("AP%03d", next);
    }
    
    public OperationResult createAppointment(String customerId, String technicianId, String date, String time, String serviceType, String vehicleDetails, String comments, CounterStaff loggedInUser){
        String appointmentId = generateNextAppointmentId();
        
        Double[] prices = FileHandler.getPrice();
        double price;
        if(serviceType.equalsIgnoreCase("Normal"))
            price = prices[0];
        else
            price = prices[1];
        
        Appointment newAppointment = new Appointment(appointmentId, customerId, technicianId, loggedInUser.getUserid(), date, time, serviceType, price, vehicleDetails, comments, "Pending");
        
        FileHandler.saveAppointment(newAppointment);
        return new OperationResult(true, "New Appointment added Successfully");
    }
    
    private static String generateNextPaymentId(){
        ArrayList<Payment> payments = FileHandler.getAllPayments();
        int max = 0;
        
        for (Payment payment : payments){
            String id = payment.getPaymentId();
                
            if(id != null && id.startsWith("PAY")){
                String numberPart = id.substring(3);
                    
                try{
                    int num = Integer.parseInt(numberPart);
                    if(num>max){
                        max=num;
                    }
                }
                catch(NumberFormatException e){
                }
            }
        }
        
        int next = max +1;
        
        return String.format("PAY%03d", next);
    }
    
    private Appointment searchAppointmentById(String appointmentId){
        ArrayList<Appointment> appointments = FileHandler.getAllAppointments();
        
        for(Appointment appointment: appointments){
            if(appointment.getAppointmentid().equals(appointmentId))
                return appointment;
        }
        return null;
    }
    
    public OperationResult collectPayment(String appointmentId, String paymentMethod){
        Appointment currentAppointment = searchAppointmentById(appointmentId);
        if(currentAppointment == null)
            return new OperationResult(false, "Payment failed, Appointment doesn't exist");
        if(!currentAppointment.getStatus().equals("Completed"))
            return new OperationResult(false, "Payment failed, Appointment is not Completed Yet!");
            
        String paymentId = generateNextPaymentId();
        String paymentDate = java.time.LocalDate.now().toString();
        String customerId = currentAppointment.getCustomerid();
        String serviceType = currentAppointment.getServicetype();
        double price = currentAppointment.getPrice();
        int duration;
            
        if(serviceType.equalsIgnoreCase("Normal"))
            duration = 60;
        else
            duration = 180;
            
        Payment payment = new Payment(paymentId, appointmentId, customerId, price, serviceType, duration, paymentDate, paymentMethod, "Paid");
        FileHandler.savePayment(payment);
        return new OperationResult(true, "Payment Successful");
            
    }
    
    private Payment searchPaymentById(String paymentId){
        ArrayList<Payment> payments = FileHandler.getAllPayments();
        
        for(Payment payment: payments){
            if(payment.getPaymentId().equals(paymentId))
                return payment;
        }
        return null;
    }
    
    private Technician searchTechnicianById(String userid){
        ArrayList<User> users = FileHandler.getallusers();
        
        for(User user: users){
            if(user instanceof Technician){
                if(user.getUserid().equals(userid))
                    return (Technician) user;
            }
        }
        return null;
    }
    
    public String generateReceipt(String paymentId){
        Payment paymentRecord = searchPaymentById(paymentId);
        if(paymentRecord == null)
            return "Error: Payment not found";
        
        Appointment appointment = searchAppointmentById(paymentRecord.getAppointmentId());
        String technicianId = appointment.getTechnicianid();
        Technician technician = searchTechnicianById(technicianId);
        Customer customer = searchCustomerById(appointment.getCustomerid());
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====Receipt=====\n");
        receipt.append("Payment ID: ");
        receipt.append(paymentId);
        receipt.append("\nCustomer Name: ");
        receipt.append(customer.getName());
        receipt.append("\tCustomer ID: ");
        receipt.append(paymentRecord.getCustomerId());
        receipt.append("\nService Type: ");
        receipt.append(paymentRecord.getServiceType());
        receipt.append("\nAmount: ");
        receipt.append(paymentRecord.getAmount());
        receipt.append("\nAppointment Date: ");
        receipt.append(appointment.getDate());
        receipt.append("\nPayment Date: ");
        receipt.append(paymentRecord.getPaymentDate());
        receipt.append("\nPayment Method: ");
        receipt.append(paymentRecord.getPaymentMethod());
        receipt.append("\nTechnician Name: ");
        receipt.append(technician.getName());
        receipt.append("\tTechnician ID: ");
        receipt.append(technicianId);
        receipt.append("\nVehicle Details: ");
        receipt.append(appointment.getVehicleDetails());
        
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("receipt.txt", true));
            bw.write(receipt.toString());
            bw.newLine();
            bw.close();
        } catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
        
        return receipt.toString();
    }
}

    