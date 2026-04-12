package apu_asc;

import apu_asc.Customer;
import apu_asc.CounterStaff;
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
       
        
    }
    

