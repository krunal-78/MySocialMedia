package com.example.mysocialmedia.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class Post {
    private String text;
    private Users createdBy = new Users();
    private Long createdAt;
    private String PostId;
    private ArrayList<String> likedBy = new ArrayList<String>();

    public Post(){
        likedBy = new ArrayList<String>();
    }
    public Post(String text,Users createdBy,Long createdAt, ArrayList<String> likedBy){
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.likedBy = likedBy;
    }
    public Post(String text,Users createdBy,Long createdAt){
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    public Post(String text, Task<DocumentSnapshot> user, Long currentTime) {
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public String getText() {
        return text;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    @Exclude
    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }
}
