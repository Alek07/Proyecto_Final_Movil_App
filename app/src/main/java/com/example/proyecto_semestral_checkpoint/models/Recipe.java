package com.example.proyecto_semestral_checkpoint.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Recipe implements Serializable {

    private String _id;
    private String name;
    private int category;
    private String description;
    private String author;
    private ArrayList<HashMap<String, String>> ingredients;
    private ArrayList<HashMap<String, ?>> images;
    private int upvotes;

    public Recipe(String name, int category, String description, String author, ArrayList<HashMap<String, String>> ingredients, ArrayList<HashMap<String, ?>> images) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.author = author;
        this.ingredients = ingredients;
        this.images = images;
    }

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

    public void setImages(ArrayList<HashMap<String, ?>> images) {
        this.images = images;
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

    public ArrayList<HashMap<String, ?>> getImages() {
        return images;
    }

    public int getUpvotes() {
        return upvotes;
    }
}
