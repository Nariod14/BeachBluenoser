package com.example.beachbluenoser;

import java.util.ArrayList;

public class User{

    String username, fullName, email, password;
    ArrayList<BeachItem> favBeaches;

    public User() {}

    public User(String username, String fullName,  String email, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.favBeaches = new ArrayList<>();
    }

    // Getter Methods
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public String getFullName() {
        return fullName;
    }
    public String getEmail() {
        return email;
    }

    public ArrayList<BeachItem> getFavBeaches(){
        return favBeaches;
    }

    // Setter Methods
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void AddToFavBeaches(ArrayList<BeachItem> favBeaches){
        this.favBeaches = favBeaches;
    }
}
