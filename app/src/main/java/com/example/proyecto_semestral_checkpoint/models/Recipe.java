package com.example.proyecto_semestral_checkpoint.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe implements Parcelable {

    private String _id;
    private String name;
    private int category;
    private String description;
    private String author;
    private ArrayList<HashMap<String, String>> ingredients;
    private int upvotes;

    public Recipe(Parcel in) {
        _id = in.readString();
        name = in.readString();
        category = in.readInt();
        description = in.readString();
        author = in.readString();
        upvotes = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public Recipe(String name, int category, String description, String author, ArrayList<HashMap<String, String>> ingredients) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.author = author;
        this.ingredients = ingredients;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeInt(category);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeInt(upvotes);
    }
}
