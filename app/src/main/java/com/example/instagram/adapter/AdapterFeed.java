package com.example.instagram.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.CommentsActivity;
import com.example.instagram.R;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.Feed;
import com.example.instagram.model.LikedPost;
import com.example.instagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> feedList;
    private Context context;

    public AdapterFeed(List<Feed> feedList, Context context) {
        this.feedList = feedList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Feed feed = feedList.get(position);
        final User userLogged = UserFirebaseHelper.getLoggedUserData();

        //Load feed data
        if(feed.getPicturePath() != null && !feed.getPicturePath().equals("")) {

            Uri uriUserPicture = Uri.parse( feed.getPicturePath() );
            Glide.with( context ).load( uriUserPicture ).into(holder.profilePicture);

        }
        if(feed.getPostPicturePath() != null && !feed.getPostPicturePath().equals("")) {

            Uri uriPostPicture = Uri.parse( feed.getPostPicturePath() );
            Glide.with( context ).load( uriPostPicture ).into(holder.postImage);

        }

        holder.description.setText( feed.getDescription() );
        holder.name.setText( feed.getName() );

        //Add click event on comments
        holder.visualizeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CommentsActivity.class);
                i.putExtra("idPostagem", feed.getId() );
                context.startActivity( i );
            }
        });

        //Recover data of liked post
        DatabaseReference likesRef = FirebaseConfig.getFirebase()
                .child("postagens-curtidas")
                .child( feed.getId() );
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int likeQuantity = 0;
                if( dataSnapshot.hasChild("qtdCurtidas") ){
                    LikedPost postLiked = dataSnapshot.getValue( LikedPost.class );
                    likeQuantity = postLiked.getLikesQuantity();
                }

                //Check if it was already clicked
                if( dataSnapshot.hasChild( userLogged.getId() ) ){
                    holder.likeButton.setLiked(true);
                }else {
                    holder.likeButton.setLiked(false);
                }

                //Create liked post object
                final LikedPost curtida = new LikedPost();
                curtida.setFeed( feed );
                curtida.setUser( userLogged );
                curtida.setLikesQuantity( likeQuantity );

                //Add events to like a post
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.save();
                        String likesString = "";
                        if(curtida.getLikesQuantity() == 0) likesString = " like";
                        else likesString = " likes";
                        holder.likesQuantity.setText( String.valueOf(curtida.getLikesQuantity()) + likesString );
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.remove();
                        String likesString = "";
                        if(curtida.getLikesQuantity() == 0) likesString = " like";
                        else likesString = " likes";
                        holder.likesQuantity.setText( String.valueOf(curtida.getLikesQuantity()) + likesString);
                    }
                });

                holder.likesQuantity.setText( String.valueOf(curtida.getLikesQuantity()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePicture;
        TextView name, description, likesQuantity;
        ImageView postImage, visualizeComment;
        LikeButton likeButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.imagePerfilPostagem);
            postImage = itemView.findViewById(R.id.imagePostagemSelecionada);
            name = itemView.findViewById(R.id.textPerfilPostagem);
            likesQuantity = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            description = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizeComment = itemView.findViewById(R.id.imageComentarioFeed);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);
        }
    }

}
