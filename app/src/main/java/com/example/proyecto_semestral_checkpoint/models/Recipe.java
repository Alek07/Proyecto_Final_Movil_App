package com.example.proyecto_semestral_checkpoint.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe {

    private String _id;
    private String name;
    private int category;
    private String description;
    private String author;
    private ArrayList<HashMap<String, String>> ingredients;
    private int upvotes;

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIngredients(ArrayList<HashMap<String, String>> ingredients) {
        this.ingredients = ingredients;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public ArrayList<HashMap<String, String>> getIngredients() {
        return ingredients;
    }

    public int getUpvotes() {
        return upvotes;
    }
}
