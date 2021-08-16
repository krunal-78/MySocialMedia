package com.example.mysocialmedia.daos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mysocialmedia.models.Comment;
import com.example.mysocialmedia.models.Post;
import com.example.mysocialmedia.models.Users;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CommentDao {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference postCollection = db.collection("posts");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // add comment ;
//    public void addComment(String commentText,String postId){
//        //get user id by firebase auth;
//        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
//        // post dao for collecting post data;
//        PostDao postDao = new PostDao();
//        UserDao userDao = new UserDao();
//        // get task by get post by postId ;
//
//        Task<DocumentSnapshot> userTask = userDao.getUserById(currentUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Users users = documentSnapshot.toObject(Users.class);
////                Comment comment = documentSnapshot.toObject(Comment.class);
//                if(users==null){
//                    Log.d("singInSuccess","can't add comment!");
//                }
//                else{
//                    Log.d("signInSuccess","added comment successfully!");
//                    // comment time;
//                    Long commentedAt = System.currentTimeMillis();
//
//                    //comment like;
//                    ArrayList<String> commentLikedBy = new ArrayList<String>();
//
//                    Comment setComment = new Comment(commentText,users,commentedAt,commentLikedBy);
//                    postCollection.document(postId).collection("commentedBy").add(setComment);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("signInSuccess","can't add comment successfully!",e);
//            }
//        });
//    }
    public void addComment(String commentText,String postId){
        //get user id by firebase auth;
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        // post dao for collecting post data;
        PostDao postDao = new PostDao();
        UserDao userDao = new UserDao();
        // get task by get post by postId ;

        Task<DocumentSnapshot> postTask = postDao.getPostByPostId(postId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
//                Comment comment = documentSnapshot.toObject(Comment.class);
                if(post==null){
                    Log.d("singInSuccess","can't get post!");
                }
                else{
                    Log.d("signInSuccess","get  post  successfully!");
                   // add comment in the post array list;
                    Task<DocumentSnapshot> userTask  = userDao.getUserById(currentUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Users users = documentSnapshot.toObject(Users.class);
                            if(users==null){
                                Log.d("signInSuccess","can't get user!");
                            }
                            else{
                                Log.d("signInSuccess","added comment successfully!");
                                // time of comment;
                                Long commentedAt = System.currentTimeMillis();
                                // comment up vote by ;
                                ArrayList<String> commentUpVote = new ArrayList<String>();
                                //comment down vote by;
                                ArrayList<String> commentDownVote = new ArrayList<String>();
                                Comment setComment  = new Comment(commentText,users,commentedAt,commentUpVote,commentDownVote);
                                post.getCommentedBy().add(setComment);
                                postCollection.document(postId).collection("commentedBy").add(setComment);
                                postCollection.document(postId).set(post);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("signInSuccess","can't get user successfully!",e);
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("signInSuccess","can't add comment successfully!",e);
            }
        });
    }
    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public CollectionReference getPostCollection() {
        return postCollection;
    }

    public Task<DocumentSnapshot> getCommentByCommentId(String postId,String commentId){
        return postCollection.document(postId).collection("commentedBy").document(commentId).get();
    }
    public void updateCommentUp(String postId,String commentId){
        //get current user id first;
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        // get comment task through comment id and convert it into class;
        Task<DocumentSnapshot> commentTask =  getCommentByCommentId(postId,commentId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Comment comment = documentSnapshot.toObject(Comment.class);
                if(comment==null){
                    Log.d("signInSuccess","Can't get comment Successfully comment is null");
                    Log.d("signInSuccess","\npost id : "+postId + "\ncommentId : "+commentId);
                }
                else{
                    Log.d("signInSuccess","got comment Successfully");
                    //check for is up voted or not;
                    boolean isUpVoted = comment.getCommentUpVote().contains(currentUserId);
                    boolean isDownVoted = comment.getCommentDownVote().contains(currentUserId);

                    if(isUpVoted){
                        comment.getCommentUpVote().remove(currentUserId);
                    }
                    else{
                        if(isDownVoted){
                            comment.getCommentDownVote().remove(currentUserId);
                        }
                        comment.getCommentUpVote().add(currentUserId);
                    }
                    // now update the comment in the same post;
                    postCollection.document(postId).collection("commentedBy").document(commentId).set(comment);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("signInSuccess","Can't get comment Successfully got exception",e);
            }
        });
    }
    public void updateCommentDown(String postId,String commentId){
        //get current user id first;
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        // get comment task through comment id and convert it into class;
        Task<DocumentSnapshot> commentTask = getCommentByCommentId(postId,commentId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Comment comment = documentSnapshot.toObject(Comment.class);
                if(comment==null){
                    Log.d("signInSuccess","Can't get comment Successfully comment is null");
                    Log.d("signInSuccess","\npost id : "+postId + "\ncommentId : "+commentId);

                }
                else{
                    Log.d("signInSuccess","got comment Successfully");
                    // check if comment is down voted or not;
                    boolean isUpVoted = comment.getCommentUpVote().contains(currentUserId);
                    boolean isDownVoted = comment.getCommentDownVote().contains(currentUserId);

                    if(isDownVoted){
                        comment.getCommentDownVote().remove(currentUserId);
                    }
                    else{
                        if(isUpVoted){
                            comment.getCommentUpVote().remove(currentUserId);
                        }
                        comment.getCommentDownVote().add(currentUserId);

                    }
                    // now set comment in the same post;
                    postCollection.document(postId).collection("commentedBy").document(commentId).set(comment);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("signInSuccess","Can't get comment Successfully  got exception",e);

            }
        });
    }
}
