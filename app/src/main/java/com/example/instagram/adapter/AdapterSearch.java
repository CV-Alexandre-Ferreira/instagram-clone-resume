package com.example.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    private List<User> userList;
    private Context context;

    public AdapterSearch(List<User> l, Context c) {
        this.userList = l;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_user, parent, false);
        return new MyViewHolder(listItem);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = userList.get(position);

        holder.name.setText( user.getName() );

        if( user.getPicturePath() != null ){
            Uri uri = Uri.parse( user.getPicturePath() );
            Glide.with(context).load(uri).into(holder.picture);
        }else {
            holder.picture.setImageResource(R.drawable.avatar);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView picture;
        TextView name;


        public MyViewHolder(View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.imageFotoPesquisa);
            name = itemView.findViewById(R.id.textNomePesquisa);

        }
    }

}
