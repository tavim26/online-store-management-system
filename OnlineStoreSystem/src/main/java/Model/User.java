package Model;

public class User {

    private int userID;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String cnp;
    private String address;
    private String phone;

    private double balance;



    public User(int userID, String role, String firstName, String lastName, String email, String password, String cnp, String address, String phone, double balance) {
        this.userID = userID;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.cnp = cnp;
        this.address = address;
        this.phone = phone;
        this.balance = 0.0;
    }


    public User(String role, String firstName, String lastName, String email, String password, String cnp, String address, String phone, double balance) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.cnp = cnp;
        this.address = address;
        this.phone = phone;

        this.balance = 0.0;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
