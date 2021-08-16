package com.example.mysocialmedia.adapters;

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
import com.example.mysocialmedia.interfaces.IPostAdapter;
import com.example.mysocialmedia.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.protobuf.StringValue;

import org.w3c.dom.Text;

import java.util.Objects;


//Instead of recyclerview adapter extend with Firestore Recycler Adapter;
//it takes two arguents one type of item which is post and view holder(post viewholder)'
public class PostAdapter extends FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder> {
    private  static IPostAdapter iPostAdapter = null;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostAdapter(@NonNull  FirestoreRecyclerOptions<Post> options,IPostAdapter iPostAdapter) {
        super(options);
        this.iPostAdapter = iPostAdapter;
    }

    //view holder static class;
    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private final ImageView userImage;
        private final TextView userName;
        private final TextView createdAt;
        private final TextView postTitle;
        private final ImageView likeButton;
        private final TextView likeCount;
        private final ImageView commentButton;
        private final TextView commentCount;
        public PostViewHolder(@NonNull  View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View;
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            createdAt = (TextView)itemView.findViewById(R.id.createdAt);
            postTitle = (TextView)itemView.findViewById(R.id.postTitle);
            likeButton = (ImageView)itemView.findViewById(R.id.likeButton);
            likeCount = (TextView)itemView.findViewById(R.id.likeCount);
            commentButton = (ImageView)itemView.findViewById(R.id.commentButton);
            commentCount = (TextView)itemView.findViewById(R.id.commentCount);
//            likeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // for getting post id user snapshots of firebase;
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    DocumentReference reference = db.document("posts");
//                    reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            Post post = documentSnapshot.toObject(Post.class);
//                            assert post != null;
////                            post.setPostId(documentSnapshot.getId());
//                            iPostAdapter.onLikeClicked(post.getPostId());
//                            Log.d("signInSuccess","got post id successfully");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull  Exception e) {
//                            Log.d("signInSuccess","can't get post id!",e);
//                        }
//                    });
//
//                }
//            });

        }

        public ImageView getUserImage() {
            return userImage;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getCreatedAt() {
            return createdAt;
        }

        public TextView getPostTitle() {
            return postTitle;
        }
        public ImageView getLikeButton() {
            return likeButton;
        }

        public TextView getLikeCount() {
            return likeCount;
        }

        public ImageView getCommentButton() {
            return commentButton;
        }

        public TextView getCommentCount() {
            return commentCount;
        }
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View postViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false);
        return (new PostViewHolder(postViewHolder)); // returns a postviewholder object;
        //needs itemview; --> have to convert xml to view (itemview)--> layoutInflater;
    }
    @Override
    protected void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position, Post model) {
        holder.getPostTitle().setText(model.getText());
        holder.getUserName().setText(model.getCreatedBy().getDisplayName());
        Glide.with(holder.itemView.getContext()).load(model.getCreatedBy().getImageUrl()).circleCrop().into(holder.userImage);
//        holder.getLikeCount().setText(String.valueOf(model.getLikedBy().size()));
        if(model.getLikedBy()==null){
            holder.getLikeCount().setText(String.valueOf(0));
        }
        else{
            holder.getLikeCount().setText(String.valueOf(model.getLikedBy().size()));
        }
        //created at will return Long in millisecond; we will convert it into different formate through utils class time ago;
        Utils timeAgo = new Utils();
        holder.createdAt.setText(timeAgo.getTimeAgo(model.getCreatedAt()));

        // handling like button click;
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                String likedPostId = snapshot.getId();
                iPostAdapter.onLikeClicked(likedPostId);
            }
        });
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                String PostId = snapshot.getId();
                iPostAdapter.onCommentClicked(PostId);
            }
        });
//        if(model.getCommentedBy()==null){
//            holder.getCommentCount().setText(String.valueOf(0));
//        }
//        else{
            holder.getCommentCount().setText(String.valueOf(model.getCommentedBy().size()));
//        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        boolean isLiked = model.getLikedBy()==null? false : model.getLikedBy().contains(currentUserId);
        if(isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.getContext(),R.drawable.ic_liked));
        }
        else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.getContext(),R.drawable.ic_unliked));
        }

    }



    //we don't need to override getItemCount method since data is managed by Firestore recycler adapter;

}
