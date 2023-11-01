package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;


public class AdapterMiniatures extends RecyclerView.Adapter<AdapterMiniatures.MyViewHolder> {

    private List<ThumbnailItem> filtersList;
    private Context context;

    public AdapterMiniatures(List<ThumbnailItem> filtersList, Context context) {
        this.filtersList = filtersList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filters, parent, false);
        return new AdapterMiniatures.MyViewHolder(listItem);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ThumbnailItem item = filtersList.get( position );

        holder.picture.setImageBitmap( item.image );
        holder.filterName.setText( item.filterName );

    }

    @Override
    public int getItemCount() {
        return filtersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView filterName;


        public MyViewHolder(View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.imageFotoFiltro);
            filterName = itemView.findViewById(R.id.textNomeFiltro);

        }
    }

}
