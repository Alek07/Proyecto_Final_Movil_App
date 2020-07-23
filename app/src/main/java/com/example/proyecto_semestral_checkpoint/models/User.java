package com.example.proyecto_semestral_checkpoint.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

    private String _id;
    private String name;
    private String email;
    private ArrayList<HashMap<String, String>> favorites;
    private String password;

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<HashMap<String, String>> getFavorites() {
        return favorites;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
