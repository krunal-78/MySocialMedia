package com.example.mysocialmedia.interfaces;

public interface ICommentAdapter {
    public void onUpVoteButtonClicked(String postId,String commentId);
    public void onDownVoteButtonClicked(String postId,String commentId);
}
