package io.github.tavim26.onlinestore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {

    private int id;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String cnp;
    private String address;
    private String phone;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    /** A new user, not yet saved in the database. */
    public User(Role role, String firstName, String lastName, String email,
                String passwordHash, String cnp, String address, String phone) {
        this(0, role, firstName, lastName, email, passwordHash, cnp, address, phone,
                BigDecimal.ZERO, null);
    }

    /** A user loaded from the database. */
    public User(int id, Role role, String firstName, String lastName, String email,
                String passwordHash, String cnp, String address, String phone,
                BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.cnp = cnp;
        this.address = address;
        this.phone = phone;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}