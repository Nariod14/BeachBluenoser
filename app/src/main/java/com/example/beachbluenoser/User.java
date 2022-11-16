package com.example.beachbluenoser;

public class User{

    String username, fullname, email, password;

    public User() {}

    public User(String username, String fullName,  String email, String password) {
        this.username = username;
        this.fullname = fullName;
        this.email = email;
        this.password = password;
    }

    // Getter Methods
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public String getFullName() {
        return fullname;
    }
    public String getEmail() {
        return email;
    }

    // Setter Methods
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setFullName(String fullName) {
        this.fullname = fullName;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}