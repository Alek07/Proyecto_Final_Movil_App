package com.example.proyecto_semestral_checkpoint.models;

import java.util.HashMap;
import java.util.List;

public class Recipe {

    private String _id;
    private String name;
    private int category;
    private String description;
    private String author;
    private List<HashMap<String, String>> ingredients;
    private int upvotes;

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

    public List<HashMap<String, String>> getIngredients() {
        return ingredients;
    }

    public int getUpvotes() {
        return upvotes;
    }
}
