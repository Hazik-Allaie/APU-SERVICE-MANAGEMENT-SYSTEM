package apu_asc;


public class Manager extends User{
     public Manager(String userid, String name, int age, String email, String username, String password, String contact) {
        super(userid, name, age, email, username, password, contact, "Manager");
    }
    
    @Override
    public void displayMenu() {
    // manager menu options
    }
    
}
