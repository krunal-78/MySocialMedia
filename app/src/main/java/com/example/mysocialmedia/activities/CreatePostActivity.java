package com.example.mysocialmedia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysocialmedia.R;
import com.example.mysocialmedia.daos.PostDao;

public class CreatePostActivity extends AppCompatActivity {
    private EditText postInput;
    private Button postButton;
    private PostDao postDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        postInput = findViewById(R.id.postInput);
        postButton = findViewById(R.id.postButton);
        postDao = new PostDao();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = postInput.getText().toString();
                if(!input.isEmpty()){
                    postDao.addPost(input);
                    Log.d("signInSuccess","Posted Successfully!");
                    finish();
                }
                else{
                    Toast.makeText(CreatePostActivity.this, "Can't post empty text!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}