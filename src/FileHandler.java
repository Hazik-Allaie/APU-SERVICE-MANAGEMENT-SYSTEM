import java.io.*;
import java.util.ArrayList;
public class FileHandler {
    public static void saveuser(User user){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("user.txt",true));
            bw.write(user.getUserid() + "," + user.getName()+","+ user.getAge()+","+ user.getEmail()+","+ user.getUsername() +user.getPassword()+"," + user.getContact()+","+ user.getRole() );
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
             String line ;
        while((line = br.readLine())!= null){
            String[] parts = line.split(",");
            User user = new User(
            parts[0],                    // userid
            parts[1],                    // name
            Integer.parseInt(parts[2]),  // age converted to string
            parts[3],                    //email
            parts[4],                    //username
            parts[5],                    // password
            parts[6],                    // contact
            parts[7]                     //role
            );
            users.add(user);
            br.close(); 
            
        }
        }
        catch (IOException e){
        System.out.println("Error :" + e.getMessage());}
        return users;
       
        
    }
    
}
