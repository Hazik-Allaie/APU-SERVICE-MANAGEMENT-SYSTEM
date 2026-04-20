package apu_asc;
import java.util.Scanner;
import java.util.ArrayList;

public class ManagerDashboard {
        private Manager manager; // the logged in manager
    Scanner sc= new Scanner(System.in);
    // Constructor
    public ManagerDashboard(Manager manager){
        this.manager = manager;
    }
    public void showmenu(){
    while (true){ 
    System.out.println("====Manager Dashboard=====");
    System.out.println( " Welcome" +" " +manager.getName() );
    System.out.println("1. Manage Staff");
    System.out.println("2. Set Service Prices");
    System.out.println("3. View Feedbacks");
    System.out.println("4. View Reportss");
    System.out.println("5. Logout");
    
    System.out.print("Enter your Choice : ");
    String choice = sc.nextLine();
    
    if(choice.equals("1")){
        manageStaff();
    } 
    else if(choice.equals("2")){
        setPrices();
    } 
    else if(choice.equals("3")){
        viewFeedbacks();
    } 
    else if(choice.equals("4")){
    viewReports();
    }   
    else if(choice.equals("5")){
    System.out.println("Logging out...");
    } 
    else {
    System.out.println("Invalid choice!");
}
    
    }
    
}
    public void manageStaff(){
        while(true){
    System.out.println("====Manage Staff =====");
    System.out.println("1. Add Staff");
    System.out.println("2. View All Staff");
    System.out.println("3. Update Staff ");
    System.out.println("4. Delete Staff ");
    System.out.println("5. Back to Main Menu ");
    
    System.out.print("Enter your Choice : ");
    String choice = sc.nextLine();  
        if (choice.equals("1")){
        addstaff();}
        else if (choice.equals("2")){
        viewallstaff();}
        else if (choice.equals("3")){
        updatestaff();}
        else if (choice.equals("4")){
        deletestaff();}
        else if (choice.equals("5")){
        break;}
        else {
    System.out.println("Invalid choice!");}
        }
    }
    private void addstaff(){
    System.out.println("Enter Staff type (Manager/CounterStaff/Technician):");
    String staffType= sc.nextLine();
    
    System.out.print("Enter User ID: ");
    String userid = sc.nextLine();

    System.out.print("Enter Name: ");
    String name = sc.nextLine();

    System.out.print("Enter User Age: ");
    int age = Integer.parseInt(sc.nextLine());

    System.out.print("Enter email: ");
    String email = sc.nextLine();
    
    System.out.print("Enter Username: ");
    String username = sc.nextLine();

    System.out.print("Enter password : ");
    String password = sc.nextLine();
    System.out.print("Enter contact : ");
    String contact = sc.nextLine();
    
    
    User newStaff = null;

    if(staffType.equals("Manager")){
    newStaff = new Manager(userid, name, age, email, username, password, contact);} 
    else if(staffType.equals("CounterStaff")){
    newStaff = new CounterStaff(userid, name, age, email, username, password, contact);}
    else if(staffType.equals("Technician")){
    newStaff = new Technician(userid, name, age, email, username, password, contact);} 
    else {
    System.out.println("Invalid staff type!");
    return;}
    
    FileHandler.saveuser(newStaff);
    System.out.println("Staff added successfully!");
    }
    
    private void viewallstaff(){
    ArrayList<User> users = FileHandler.getallusers();
    System.out.println("===== All Staff =====");
    boolean found = false;
    
    for(User user : users){
        if(!user.getRole().equals("Customer")){
            found = true;
            System.out.println("--------------------");
            System.out.println("ID: " + user.getUserid());
            System.out.println("Name: " + user.getName());
            System.out.println("Age: " + user.getAge());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Contact: " + user.getContact());
            System.out.println("Role: " + user.getRole());
        }
    }
    
    if(!found){
        System.out.println("No staff found!");
    }
}
    private void deletestaff(){
    System.out.print("Enter Staff ID to delete: ");
    String userid = sc.nextLine();
    FileHandler.deleteuser(userid);
    System.out.println("Staff deleted successfully!");}
    
    private void updatestaff(){
    System.out.print("Enter Staff ID to update: ");
    String userid = sc.nextLine();
    
    // Find existing staff
    User existingUser = null;
    for(User user : FileHandler.getallusers()){
        if(user.getUserid().equals(userid)){
            existingUser = user;
            break;
        }
    }
    
    if(existingUser == null){
        System.out.println("Staff not found!");
        return;
    }
    
    System.out.print("Enter new Name (" + existingUser.getName() + "): ");
    String name = sc.nextLine();
    
    System.out.print("Enter new Age (" + existingUser.getAge() + "): ");
    int age = Integer.parseInt(sc.nextLine());
    
    System.out.print("Enter new Email (" + existingUser.getEmail() + "): ");
    String email = sc.nextLine();
    
    System.out.print("Enter new Username (" + existingUser.getUsername() + "): ");
    String username = sc.nextLine();
    
    System.out.print("Enter new Password: ");
    String password = sc.nextLine();
    
    System.out.print("Enter new Contact (" + existingUser.getContact() + "): ");
    String contact = sc.nextLine();
    
    User updatedUser = null;
    if(existingUser.getRole().equals("Manager")){
        updatedUser = new Manager(userid, name, age, email, username, password, contact);
    } else if(existingUser.getRole().equals("CounterStaff")){
        updatedUser = new CounterStaff(userid, name, age, email, username, password, contact);
    } else if(existingUser.getRole().equals("Technician")){
        updatedUser = new Technician(userid, name, age, email, username, password, contact);
    }
    
    FileHandler.updateUser(updatedUser);
    System.out.println("Staff updated successfully!");
}
    
}



