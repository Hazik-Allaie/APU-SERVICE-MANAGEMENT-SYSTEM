package apu_asc;


public abstract class User {
    private String userid;
    private String name;
    private int age ;
    private String email ;
    private String username ;
    private String password ;
    private String contact ;
    private String role;

    
    //Constructor
    
public  User(String userid,String name,int age,String email ,String username ,String password,String contact ,String role){
this.name=name;
this.username=username;
this.userid=userid;
this.age=age;
this.email=email;
this.password=password;
this.contact=contact;
this.role=role;}


    //getter 
    
    public String getName(){
    return name ;}
    
    public String getUserid(){
    return userid;}
    
    public int getAge(){
    return age ;}
    
    public String getEmail(){
    return email ; }
    
     public String getUsername(){
    return username ; }
     
    public String getPassword(){
    return password ; }
    
    public String getContact(){
    return contact ; }
    
    public String  getRole(){
    return role ; }
    
    
    //setter 
    public void setUserid( String userid ){
    this.userid =userid;}
    
    public void setName( String name ){
    this.name = name ;}
    
    public void setAge( int age ){
    this.age = age ;}
    
    public void setEmail (String email){
    this.email = email ;}
    
    public void setUsername( String username ){
    this.username =username;}
    
    public void setPassword (String password){
    this.password=password;}
    
    public void setContact(String contact){
    this.contact=contact;}
    
    public void setRole(String role){
    this.role=role;}
    
    public abstract void displayMenu();
    
        @Override
    public String toString() {
        return userid + "," + name + "," + age + "," + 
               email + "," + username + "," + password + "," + 
               contact + "," + role;
    }
    
}
