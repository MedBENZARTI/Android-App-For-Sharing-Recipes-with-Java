package util;

import android.app.Application;

public class RecipeApi extends Application {
    private String username;
    private String userId;

    private static RecipeApi instance;

    public static RecipeApi getInstance(){
        if (instance == null) {
            instance = new RecipeApi();
        }
        return instance;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
