package apu_asc;


public class Technician extends User {
      public Technician(String userid, String name, int age, String email, String username, String password, String contact) {
        super(userid, name, age, email, username, password, contact, "Technician");
    }
    
     @Override
    public void displayMenu() {
    // technician menu options
    }
}
