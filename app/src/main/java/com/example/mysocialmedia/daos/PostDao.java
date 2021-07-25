package com.example.mysocialmedia.daos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mysocialmedia.models.Post;
import com.example.mysocialmedia.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class PostDao {
    //get instance of firebase firestore database;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //make collection of posts;
    CollectionReference postCollection = db.collection("posts");
    //for getting user instance of  firebaseAuth will be needed;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void addPost(String text){
        //get current user id(singed in );
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        //get current user data with user id;
        UserDao userDao = new UserDao();
        //get task with get user by userid;
        Task<DocumentSnapshot> user = userDao.getUserById(currentUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //get user from this Task<DocumentSnapshot>;
                Users users = documentSnapshot.toObject(Users.class);
                if (users == null) {
                    Log.d("signInSuccess", "can't get user!");
                } else {
                    Log.d("signInSuccess", "got user successfully!");

                    //get current time for displaying time of post it will return Long;
                    Long currentTime = System.currentTimeMillis();
                    ArrayList<String> likedBy = new ArrayList<String>();
                    //create post for that user;
                    Post post = new Post(text, users, currentTime,likedBy);
                    //put post into post collection;
                    postCollection.document().set(post);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Log.d("signInSuccess","can't get user!",e);
            }
        });
    }


    public FirebaseFirestore getDb() {
        return db;
    }

    public CollectionReference getPostCollection() {
        return postCollection;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public Task<DocumentSnapshot> getPostByPostId(String postId){
        return postCollection.document(postId).get();
    }
    public void updateLike(String postId){
        //get current user id(singed in );
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        //get the post by post id;
        Task<DocumentSnapshot> postTask = getPostByPostId(postId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                if(post==null){
                    Log.d("signInSuccess", "can't get post!");
                }
                else{
                    Log.d("signInSuccess", "got post successfully!");
                    //check for like condition;
                    boolean isLiked = post.getLikedBy().contains(currentUserId);

                    if(isLiked){
                        post.getLikedBy().remove(currentUserId);
                    }
                    else{
                        post.getLikedBy().add(currentUserId);
                    }
                    //now update the post by passing post id;
                    postCollection.document(postId).set(post);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Log.d("signInSuccess", "got post failer!!");
            }
        });
    }
}
