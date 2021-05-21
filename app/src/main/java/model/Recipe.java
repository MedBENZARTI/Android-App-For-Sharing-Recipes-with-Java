package model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Recipe {
    private String recipeName;
    private ArrayList<String> ingredients;
    private String userId;
    private String userName;
    private String imageUrl;
    private Timestamp timeAdded;

    public Recipe() {
    }

    public Recipe(String recipeName, ArrayList<String> ingredients, String userId, String userName, String imageUrl, Timestamp timeAdded) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.timeAdded = timeAdded;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
