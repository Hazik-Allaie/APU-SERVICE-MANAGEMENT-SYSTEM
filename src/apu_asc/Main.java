/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package apu_asc;

/**
 *
 * @author HAZIK
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        // TODO code application logic here
    }*/
    
    public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
        CounterStaff testUser = new CounterStaff(
            "CS001", "John Smith", 30,
            "john@apu.edu.my", "john", "password123", "0123456789"
        );
        new CounterStaffFrame(testUser);
    });
}
}

