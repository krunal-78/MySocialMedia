package com.example.mysocialmedia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mysocialmedia.R;
import com.example.mysocialmedia.adapters.CommentAdapter;
import com.example.mysocialmedia.daos.CommentDao;
import com.example.mysocialmedia.daos.PostDao;
import com.example.mysocialmedia.interfaces.ICommentAdapter;
import com.example.mysocialmedia.models.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.nio.charset.MalformedInputException;

public class comment_box extends AppCompatActivity implements ICommentAdapter {
    private EditText inputComment;
    private ImageView sendComment;
    private CommentDao commentDao;
    private PostDao postDao;
    private RecyclerView commentRecyclerView;
    private String postId;
    private CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_box);
        inputComment = findViewById(R.id.inputComment);
        sendComment = findViewById(R.id.sendComment);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDao = new CommentDao();
                Intent intent = getIntent();
                postId = intent.getStringExtra(MainActivity.EXTRA_POST_ID);
                String inputCommentText = inputComment.getText().toString();

                if(inputCommentText.isEmpty()){
                    Toast.makeText(comment_box.this, "Can't comment empty text!", Toast.LENGTH_SHORT).show();
                }
                else{
                    commentDao.addComment(inputCommentText,postId);
                    Log.d("singInSuccess","Commented Successfully!");
                    inputComment.setText("");
                }
            }
        });
        setUpCommentRecyclerView();
    }

    private void setUpCommentRecyclerView() {
        //set layout manager of recycler view;
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //we need to set comment in this adapter in order they are commented for this we will use queries ;
        // we need to get collection reference and we will user commentdao;
        commentDao = new CommentDao();
        postDao = new PostDao();
        // get post collection reference;
        CollectionReference postCollection = commentDao.getPostCollection();
        postId = getIntent().getStringExtra(MainActivity.EXTRA_POST_ID);
        // create a query ordered by time;
        Query query = postCollection.document(postId).collection("commentedBy")
                .orderBy("commentedAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Comment> RecyclerOptions = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query,Comment.class).build();
        // adapter takes one parameter which is Firestore recycler options which is made by a query;
        commentAdapter = new CommentAdapter(RecyclerOptions,this);
        commentRecyclerView.setAdapter(commentAdapter);
        commentRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        commentAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentAdapter.stopListening();
    }

    @Override
    public void onUpVoteButtonClicked(String postId, String commentId) {
        commentDao.updateCommentUp(postId,commentId);
    }

    @Override
    public void onDownVoteButtonClicked(String postId, String commentId) {
        commentDao.updateCommentDown(postId,commentId);
    }
}