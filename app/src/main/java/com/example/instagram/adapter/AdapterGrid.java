package com.example.instagram.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.List;

public class AdapterGrid extends ArrayAdapter<String> {

    private Context context;
    private int layoutResource;
    private List<String> urlPictures;

    public AdapterGrid(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.urlPictures = objects;
    }

    public class ViewHolder {
        ImageView image;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        //if view is not inflated
        if( convertView == null ){

            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder.image = convertView.findViewById(R.id.imageGridPefil);
            viewHolder.progressBar = convertView.findViewById(R.id.progressGridPerfil);

            convertView.setTag( viewHolder );

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Recover image data
        String urlImage = getItem( position );
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urlImage, viewHolder.image,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        viewHolder.progressBar.setVisibility( View.VISIBLE );
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        viewHolder.progressBar.setVisibility( View.GONE );
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        viewHolder.progressBar.setVisibility( View.GONE );
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        viewHolder.progressBar.setVisibility( View.GONE );
                    }
                });

        return convertView;
    }
}
