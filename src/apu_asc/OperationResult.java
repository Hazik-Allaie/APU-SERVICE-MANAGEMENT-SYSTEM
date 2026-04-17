package apu_asc;


public class OperationResult {
    private boolean result;
    private String message;
    
    //Constructor
    public OperationResult(boolean result, String message){
        this.result = result;
        this.message = message;
    }
    
    //Getters
    public boolean getResult(){ return result; }
    public String getMessage(){ return message; }
}
