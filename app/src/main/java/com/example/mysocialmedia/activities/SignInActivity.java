package com.example.mysocialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mysocialmedia.R;
import com.example.mysocialmedia.daos.UserDao;
import com.example.mysocialmedia.models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "signInSuccess";
    private GoogleSignInClient googleSignInClient;
    private SignInButton signIn;
    private FirebaseAuth firebaseAuth;
    private  ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn =  findViewById(R.id.signIn);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        // configure google sign in;
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String currentUserId = firebaseUser.getUid();
        assert firebaseUser != null;
        Log.d("signInSuccess","id : "+firebaseUser.getUid());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        usersCollection.document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("signInSuccess","successfully got document with user id!");
                    DocumentSnapshot userIdDocument = task.getResult();
                    assert userIdDocument != null;
                    if(userIdDocument.exists()){
                        updateUI(firebaseUser);
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        Log.d("signInSuccess","user id document don't exist");
                    }
                }
                else{
                    Log.d("signInSuccess"," can't successfully got document with user id!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("signInSuccess","failed with exception : ",e);
            }
        });
    }

    private void signIn() {
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            //if singIn was successful then authenticate with firebase;
            // get account;
            GoogleSignInAccount account = task.getResult(ApiException.class);
            assert account != null;
            Log.d(TAG,"firebaseAuthWithGoogle :"+ account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            e.printStackTrace();
            Log.d(TAG,"Google SignIn Failed ",e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        signIn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //signIn successful and update UI with the Signed_in user's information;
                            Log.d(TAG,"signInWithCredential : success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            Log.d(TAG,"signInWithCredential : failure");
                            updateUI(null);
                        }
                    }
                });
    }

    private  void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null){
            //make user from firebase user;
            Users users = new Users(firebaseUser.getUid(), Objects.requireNonNull(firebaseUser.getDisplayName()), Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());
            //for adding into database make userDao instance;
            UserDao userDao = new UserDao();
            userDao.addUser(users);
            Intent mainActivityIntent = new Intent(SignInActivity.this, MainActivity.class);
            SignInActivity.this.startActivity(mainActivityIntent);
            Log.d(TAG,"MAIN ACTIVITY SUCCESS");
//            finish();
        }
        else{
            // If can't get firebase user then come back to login;
            signIn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}