package com.example.instagram.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagram.EditProfileActivity;
import com.example.instagram.R;
import com.example.instagram.adapter.AdapterGrid;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView profileImage;
    public GridView gridViewPerfil;
    private TextView textPublications, textFollowers, textFollowing;
    private Button buttonEditProfile;
    private User loggedUser;

    private DatabaseReference firebaseRef;
    private DatabaseReference usersRef;
    private DatabaseReference loggedUserRef;
    private ValueEventListener valueEventListenerProfile;
    private DatabaseReference userPostsRef;
    private AdapterGrid adapterGrid;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initial Config
        loggedUser = UserFirebaseHelper.getLoggedUserData();
        firebaseRef = FirebaseConfig.getFirebase();
        usersRef = firebaseRef.child("usuarios");

        userPostsRef = FirebaseConfig.getFirebase()
                .child("postagens")
                .child( loggedUser.getId() );

        initializeComponents(view);

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        initializeImageLoader();

        loadProfilePosts();

        return view;
    }

    public void loadProfilePosts(){

        userPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //set grid size
                int gridSize = requireContext().getResources().getDisplayMetrics().widthPixels;
                int imageSize = gridSize / 3;
                gridViewPerfil.setColumnWidth( imageSize );

                List<String> urlPictures = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Post post = ds.getValue( Post.class );
                    urlPictures.add( post.getPostPicturePath() );
                }

                //set adapter
                adapterGrid = new AdapterGrid( getActivity() , R.layout.grid_post, urlPictures );
                gridViewPerfil.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void initializeImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder( getActivity() )
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );

    }

    private void initializeComponents(View view){
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
        profileImage = view.findViewById(R.id.imagePerfil);
        textPublications = view.findViewById(R.id.textPublicacoes);
        textFollowers = view.findViewById(R.id.textSeguidores);
        textFollowing = view.findViewById(R.id.textSeguindo);
        buttonEditProfile = view.findViewById(R.id.buttonAcaoPerfil);
    }

    private void recoverLoggedUserData(){

        loggedUserRef = usersRef.child( loggedUser.getId() );
        valueEventListenerProfile = loggedUserRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue( User.class );

                        String posts = String.valueOf( user.getPosts() );
                        String following = String.valueOf( user.getFollowing() );
                        String followers = String.valueOf( user.getFollowers() );

                        textPublications.setText( posts );
                        textFollowers.setText( followers );
                        textFollowing.setText( following );

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void recoverUserPicture(){

        loggedUser = UserFirebaseHelper.getLoggedUserData();

        String picturePath = loggedUser.getPicturePath();
        if( picturePath != null ){
            if(!picturePath.equals("")) {
                Uri url = Uri.parse(picturePath);
                Glide.with(getActivity())
                        .load(url)
                        .into(profileImage);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        recoverLoggedUserData();
        recoverUserPicture();

    }

    @Override
    public void onStop() {
        super.onStop();
        loggedUserRef.removeEventListener(valueEventListenerProfile);
    }
}
