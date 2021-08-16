package com.example.mysocialmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mysocialmedia.R;
import com.example.mysocialmedia.Utils;
import com.example.mysocialmedia.interfaces.ICommentAdapter;
import com.example.mysocialmedia.models.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.util.Objects;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {
    private String postId;
    private static ICommentAdapter iCommentAdapter = null;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options,ICommentAdapter iCommentAdapter) {
        super(options);
        this.iCommentAdapter = iCommentAdapter;
    }


    // comment view holder class;
    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        private final ImageView commentUserImage;
        private final TextView commentUserName;
        private final TextView commentedAt;
        private final TextView commentText;
        private final ImageView commentUpVoteButton;
        private final ImageView commentDownVoteButton;
        private final TextView commentUpVoteCount;
        private final TextView commentDownVoteCount;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserImage = (ImageView)itemView.findViewById(R.id.commentUserImage);
            commentUserName = (TextView)itemView.findViewById(R.id.commentUserName);
            commentedAt = (TextView)itemView.findViewById(R.id.commentedAt);
            commentText = (TextView)itemView.findViewById(R.id.commentText);
            commentUpVoteButton = (ImageView)itemView.findViewById(R.id.commentUpVoteButton);
            commentDownVoteButton = (ImageView)itemView.findViewById(R.id.commentDownVoteButton);
            commentUpVoteCount = (TextView)itemView.findViewById(R.id.commentUpVoteCount);
            commentDownVoteCount = (TextView)itemView.findViewById(R.id.commentDownVoteCount);
        }

        public ImageView getCommentUserImage() {
            return commentUserImage;
        }

        public TextView getCommentUserName() {
            return commentUserName;
        }

        public TextView getCommentedAt() {
            return commentedAt;
        }

        public TextView getCommentText() {
            return commentText;
        }

        public ImageView getCommentDownVoteButton() {
            return commentDownVoteButton;
        }

        public ImageView getCommentUpVoteButton() {
            return commentUpVoteButton;
        }

        public TextView getCommentDownVoteCount() {
            return commentDownVoteCount;
        }

        public TextView getCommentUpVoteCount() {
            return commentUpVoteCount;
        }
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commentViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        return new CommentViewHolder(commentViewHolder);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        holder.getCommentText().setText(model.getCommentText());
        holder.getCommentUserName().setText(model.getCommentedBy().getDisplayName());
        Glide.with(holder.itemView.getContext()).load(model.getCommentedBy().getImageUrl()).circleCrop().into(holder.commentUserImage);
        Utils timeAgo = new Utils();
        holder.getCommentedAt().setText(timeAgo.getTimeAgo(model.getCommentedAt()));

        // handling comment up vote click;
        holder.commentUpVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
                String commentId = documentSnapshot.getId();
                String postId = Objects.requireNonNull(documentSnapshot.getReference().getParent().getParent()).getId();
                iCommentAdapter.onUpVoteButtonClicked(postId,commentId);
            }
        });
        // handling comment down vote click;
        holder.commentDownVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
                String commentId = documentSnapshot.getId();
                String postId = Objects.requireNonNull(documentSnapshot.getReference().getParent().getParent()).getId();
                iCommentAdapter.onDownVoteButtonClicked(postId,commentId);
            }
        });
        // get current user id;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        boolean isUpVoted = model.getCommentUpVote().contains(currentUserId);
        boolean isDownVoted = model.getCommentDownVote().contains(currentUserId);

        // up vote;
        if(isUpVoted){
            holder.commentUpVoteButton.setImageDrawable(ContextCompat.getDrawable(holder.commentUpVoteButton.getContext(),R.drawable.up_vote_yes));
        }
        else{
            holder.commentUpVoteButton.setImageDrawable(ContextCompat.getDrawable(holder.commentUpVoteButton.getContext(),R.drawable.up_vote));
        }
        holder.getCommentUpVoteCount().setText(String.valueOf(model.getCommentUpVote().size()));

        // down vote;
        if(isDownVoted){
            holder.commentDownVoteButton.setImageDrawable(ContextCompat.getDrawable(holder.commentDownVoteButton.getContext(),R.drawable.down_vote_yes));
        }
        else{
            holder.commentDownVoteButton.setImageDrawable(ContextCompat.getDrawable(holder.commentDownVoteButton.getContext(),R.drawable.down_vote));
        }
        holder.getCommentDownVoteCount().setText(String.valueOf(model.getCommentDownVote().size()));

    }

    public String getPostId() {
        return postId;
    }
}
