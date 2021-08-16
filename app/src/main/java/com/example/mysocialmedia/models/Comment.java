package com.example.mysocialmedia.models;

import java.util.ArrayList;

public class Comment {
    private String commentText;
    private Users commentedByUser = new Users();
    private Long commentedAt;
    private ArrayList<String> commentUpVote = new ArrayList<String>();
    private ArrayList<String> commentDownVote = new ArrayList<String>();

    public Comment(){

    }
    public Comment(String commentText,Users commentedByUser,Long commentedAt,ArrayList<String> commentUpVote,ArrayList<String> commentDownVote){
        this.commentText = commentText;
        this.commentedByUser = commentedByUser;
        this.commentedAt = commentedAt;
        this.commentUpVote = commentUpVote;
        this.commentDownVote = commentDownVote;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setCommentedBy(Users commentedByUser) {
        this.commentedByUser = commentedByUser;
    }

    public void setCommentedAt(Long commentedAt) {
        this.commentedAt = commentedAt;
    }

    public void setCommentUpVote(ArrayList<String> commentUpVote) {
        this.commentUpVote = commentUpVote;
    }

    public void setCommentDownVote(ArrayList<String> commentDownVote) {
        this.commentDownVote = commentDownVote;
    }

    public String getCommentText() {
        return commentText;
    }

    public Users getCommentedBy() {
        return commentedByUser;
    }

    public Long getCommentedAt() {
        return commentedAt;
    }

    public ArrayList<String> getCommentUpVote() {
        return commentUpVote;
    }

    public ArrayList<String> getCommentDownVote() {
        return commentDownVote;
    }
}
