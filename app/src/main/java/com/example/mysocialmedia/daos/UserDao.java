package com.example.mysocialmedia.daos;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mysocialmedia.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class UserDao {
    //get instance of firebase firestore database;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //make collection of users;
    private final CollectionReference userCollection = db.collection("users");

    @SuppressLint("RestrictedApi")
    public void addUser(Users users){  //now add user in sign in activity while sign in;
        if(users!=null){
            assert users.getUserId() != null;
            userCollection.document(users.getUserId()).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("signInSuccess","added user successfully!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    Log.d("signInSuccess","can't add user!",e);
                }
            });
        }
    }
    //from task we can get data by listener;
    public Task<DocumentSnapshot> getUserById(String uId){
        return userCollection.document(uId).get();
    }
}
