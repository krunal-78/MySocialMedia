package com.example.mysocialmedia.models;

import androidx.annotation.NonNull;

public class Users {
    private String userId;
    private String displayName;
    private String imageUrl;
//    public Users(){
//
//    }
    public Users(String userId, @NonNull String displayName, String imageUrl){
        this.userId = userId;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
    }
    public Users(){

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDisplayName(@NonNull String displayName) {
        this.displayName = displayName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

}
