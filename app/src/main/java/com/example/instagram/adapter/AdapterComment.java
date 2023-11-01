package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.model.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyViewHolder> {

    private List<Comment> commentsList;
    private Context context;

    public AdapterComment(List<Comment> commentsList, Context context) {
        this.commentsList = commentsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment, parent, false);
        return new AdapterComment.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Comment comment = commentsList.get( position );

        holder.userName.setText( comment.getUserName() );
        holder.comment.setText( comment.getComment() );
        Glide.with(context).load(comment.getPicturePath()).into(holder.imageProfile);

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageProfile;
        TextView userName, comment;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageFotoComentario);
            userName = itemView.findViewById(R.id.textNomeComentario);
            comment = itemView.findViewById(R.id.textComentario);

        }
    }

}
