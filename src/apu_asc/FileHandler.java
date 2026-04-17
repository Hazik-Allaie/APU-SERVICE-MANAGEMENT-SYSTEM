package apu_asc;

import java.io.*;
import java.util.ArrayList;
public class FileHandler {
    public static void saveuser(User user){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("user.txt",true));
            bw.write(user.toString());
            bw.newLine(); // moves to next line
            bw.close();   // closes the file
        }
        catch(IOException  e){
        System.out.print("Error : " + e.getMessage());
        }
    }
    
public static ArrayList<User> getallusers(){
    ArrayList<User> users = new ArrayList<>();
    try{
        BufferedReader br = new BufferedReader(new FileReader("user.txt"));
        String line;
        while((line = br.readLine()) != null){
            String[] parts = line.split(",");
            User user = null;
            
            if(parts[7].equals("Manager")){
                user = new Manager(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4], parts[5], parts[6]);
            } else if(parts[7].equals("CounterStaff")){
                user = new CounterStaff(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4], parts[5], parts[6]);
            } else if(parts[7].equals("Technician")){
                user = new Technician(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4], parts[5], parts[6]);
            } else if(parts[7].equals("Customer")){
                user = new Customer(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4], parts[5], parts[6]);
            }
            
            if(user != null){
                users.add(user);
            }
        }
        br.close();
    }
    catch (IOException e){
        System.out.println("Error :" + e.getMessage());
    }
    return users;
}
public static User getUserbyUsername(String username,String password){
 for (User user : getallusers()){
    if (user.getUsername().equals(username) && user.getPassword().equals(password)){
    return user;}
        }
 return null ;
};

 public static void deleteuser(String userid){
     ArrayList<User> users = getallusers();
     ArrayList<User> updatedUsers = new ArrayList<>();
     for (User user : users){
     if (!user.getUserid().equals(userid) ){
     updatedUsers.add(user);}} 
     try{
    BufferedWriter bw = new BufferedWriter(new FileWriter("user.txt", false)); 
    for(User u : updatedUsers){
        bw.write(u.toString());
        bw.newLine();
    }
    bw.close();
} catch(IOException e){
    System.out.println("Error: " + e.getMessage());
}
 };    
public static void updateUser(User updatedUser){
    ArrayList<User> users = getallusers();
    ArrayList<User> updatedUsers = new ArrayList<>();
    
    for(User user : users){
        if(user.getUserid().equals(updatedUser.getUserid())){
            updatedUsers.add(updatedUser); // replace with new info
        } else {
            updatedUsers.add(user); // keep original
        }
    }
    
    try{
        BufferedWriter bw = new BufferedWriter(new FileWriter("user.txt", false));
        for(User u : updatedUsers){
            bw.write(u.toString());
            bw.newLine();
        }
        bw.close();
    } catch(IOException e){
        System.out.println("Error: " + e.getMessage());
    }
}
public static void saveAppointment(Appointment appointment){
    try{
        BufferedWriter bw = new BufferedWriter(new FileWriter("appointment.txt", true));
        bw.write(appointment.toString());
        bw.newLine();
        bw.close();
        }  
    
     catch(IOException e){
        System.out.println("Error: " + e.getMessage());
    }
}
<<<<<<< HEAD

public static ArrayList<Appointment> getAllAppointments(){
    ArrayList<Appointment> appointments = new ArrayList<>();
    try {
=======
public static ArrayList<Appointment> getAllAppointments(){
    ArrayList<Appointment> appointments = new ArrayList<>();
    try{
>>>>>>> f1aa9eb (file handler by hazik)
        BufferedReader br = new BufferedReader(new FileReader("appointment.txt"));
        String line;
        while((line = br.readLine()) != null){
            String[] parts = line.split(",");
<<<<<<< HEAD
            Appointment appointment = null;
            
            appointment = new Appointment (parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Double.parseDouble(parts[7]), parts[8], parts[9], parts[10]);
            
            appointments.add(appointment);
        }
        br.close();
        }
=======
            Appointment appointment = new Appointment(
                parts[0],                      // appointmentid
                parts[1],                      // customerid
                parts[2],                      // technicianid
                parts[3],                      // counterstaffid
                parts[4],                      // date
                parts[5],                      // time
                parts[6],                      // servicetype
                Double.parseDouble(parts[7]),  // price
                parts[8],                      // vehicleDetails
                parts[9],                      // comments
                parts[10]                      // status
            );
            appointments.add(appointment);
        }
        br.close();
    }
>>>>>>> f1aa9eb (file handler by hazik)
    catch (IOException e){
        System.out.println("Error :" + e.getMessage());
    }
    return appointments;
}
<<<<<<< HEAD

=======
>>>>>>> f1aa9eb (file handler by hazik)
public static void updateAppointment(Appointment updatedAppointment){
    ArrayList<Appointment> appointments = getAllAppointments();
    ArrayList<Appointment> updatedAppointments = new ArrayList<>();
    
    for(Appointment appointment : appointments){
        if(appointment.getAppointmentid().equals(updatedAppointment.getAppointmentid())){
<<<<<<< HEAD
            updatedAppointments.add(updatedAppointment); // replace with new info
=======
            updatedAppointments.add(updatedAppointment); // replace
>>>>>>> f1aa9eb (file handler by hazik)
        } else {
            updatedAppointments.add(appointment); // keep original
        }
    }
    
    try{
        BufferedWriter bw = new BufferedWriter(new FileWriter("appointment.txt", false));
        for(Appointment a : updatedAppointments){
            bw.write(a.toString());
            bw.newLine();
        }
        bw.close();
    } catch(IOException e){
        System.out.println("Error: " + e.getMessage());
    }
}
<<<<<<< HEAD

public static void savePayment(Payment payment){
    try{
        BufferedWriter bw = new BufferedWriter(new FileWriter("payment.txt", true));
        bw.write(payment.toString());
        bw.newLine();
        bw.close();
        }  
    
     catch(IOException e){
        System.out.println("Error: " + e.getMessage());
    }
}

public static ArrayList<Payment> getAllPayments(){
    ArrayList<Payment> payments = new ArrayList<>();
    try {
        BufferedReader br = new BufferedReader(new FileReader("payment.txt"));
        String line;
        while((line = br.readLine()) != null){
            String[] parts = line.split(",");
            Payment payment = null;
            
            payment = new Payment (parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4], Integer.parseInt(parts[5]), parts[6], parts[7], parts[8]);
            
            payments.add(payment);
        }
        br.close();
        }
    catch (IOException e){
        System.out.println("Error :" + e.getMessage());
    }
    return payments;
}

public static void savePrice(double normalPrice, double majorPrice){
    try{
        BufferedWriter bw = new BufferedWriter(new FileWriter("prices.txt", false));
        bw.write(normalPrice + "," + majorPrice);
        bw.close();
        }  
    
     catch(IOException e){
        System.out.println("Error: " + e.getMessage());
    }
}

public static Double[] getPrice(){
    Double[] prices = new Double[2];
    try {
        BufferedReader br = new BufferedReader(new FileReader("prices.txt"));
        String line;
        line = br.readLine();
        String[] parts = line.split(",");
        prices[0] = Double.parseDouble(parts[0]);
        prices[1] = Double.parseDouble(parts[1]);
        br.close();
        }
    catch (IOException e){
        System.out.println("Error :" + e.getMessage());
    }
    return prices;
}
}
=======
}


    
>>>>>>> f1aa9eb (file handler by hazik)
    

