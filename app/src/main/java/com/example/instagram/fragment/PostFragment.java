package com.example.instagram.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.instagram.FilterActivity;
import com.example.instagram.R;
import com.example.instagram.helper.Permission;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private Button buttonOpenGallery, buttonOpenCamera;
    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    private String[] necessaryPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        //Validate permissions
        Permission.validatePermissions(permissions(),getActivity(), 1);

        //init components
        buttonOpenCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonOpenGallery = view.findViewById(R.id.buttonAbrirGaleria);

        buttonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                    startActivityForResult(i, CAMERA_SELECTION);
                }
            }
        });

        buttonOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                    startActivityForResult(i, GALLERY_SELECTION);
                }
            }
        });

        return view;
    }

    public static String[] storage_permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == getActivity().RESULT_OK ){

            Bitmap image = null;

            try {

                //Validate image selection type
                switch ( requestCode ){
                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALLERY_SELECTION:
                        Uri selectedImagePlace = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImagePlace);
                        break;
                }

                //validate selected image
                if( image != null ){

                    //convert image to byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //send selected image to FilterActivity
                    Intent i = new Intent(getActivity(), FilterActivity.class);
                    i.putExtra("fotoEscolhida", imageData );
                    startActivity( i );

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
