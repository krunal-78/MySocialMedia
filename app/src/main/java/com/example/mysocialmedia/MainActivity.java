package com.example.mysocialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mysocialmedia.daos.PostDao;
import com.example.mysocialmedia.interfaces.IPostAdapter;
import com.example.mysocialmedia.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;


public class MainActivity extends AppCompatActivity  implements IPostAdapter {
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder> postAdapter;
    private PostDao postDao;
    private ImageView logoutButton;
    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        logoutButton = findViewById(R.id.logoutButton);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take user to Create post activity through intent;
                Intent intent = new Intent(MainActivity.this,CreatePostActivity.class);
                startActivity(intent);

            }

        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogoutDialog logoutDialog = new LogoutDialog();
//                logoutDialog.show(getSupportFragmentManager(),"logout");
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference userCollection = db.collection("users");
                userCollection.document(currentUserId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                        startActivity(intent);
                        Log.d("signInSuccess", "Log out current user Successfully!");
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("signInSuccess","can't Logout current user!",e);
                    }
                });
            }
        });
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //we need to set posts in this adapter in order which is created first, we will use queries for getting this from firebase post collection;
        //first create a postDao to use post collection from firebase;
        postDao = new PostDao();
        // create collection reference from postDao;
        CollectionReference postCollection = postDao.getPostCollection();
        // create query from collection reference ordered by time;
        Query query = postCollection.orderBy("createdAt",Query.Direction.DESCENDING);//latest post will be displayed first;
        //make Firestore Recycler Option through this query;
        FirestoreRecyclerOptions<Post> RecyclerOptions = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class).build();
        postAdapter = new PostAdapter(RecyclerOptions,this);
        //this takes a argument Firestore recycler option which can be made by a query;
        recyclerView.setAdapter(postAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
    //now adapter should listen changes from firebase also;
    //we have to specify when should adapter start and stop listening to firebase;


    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }

    @Override
    public void onLikeClicked(String postId) {
        postDao.updateLike(postId);
    }
}