package apu_asc;


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
}
