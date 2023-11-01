package com.example.instagram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    private User selectedUser;
    private User loggedUser;
    private Button buttonProfileAction;
    private CircleImageView imageProfile;
    private TextView textPublications, textFollowers, textFollowing;
    private GridView gridViewProfile;
    private AdapterGrid adapterGrid;

    private DatabaseReference firebaseRef;
    private DatabaseReference usersRef;
    private DatabaseReference friendUserRef;
    private DatabaseReference loggedUserRef;
    private DatabaseReference followersRef;
    private DatabaseReference userPostsRef;
    private ValueEventListener valueEventListenerFriendProfile;
    private String loggedUserId;
    private List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        firebaseRef = FirebaseConfig.getFirebase();
        usersRef = firebaseRef.child("usuarios");
        followersRef = firebaseRef.child("seguidores");
        loggedUserId = UserFirebaseHelper.getUserId();

        initializeComponents();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.profile);
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //recover selected user
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            selectedUser = (User) bundle.getSerializable("usuarioSelecionado");

            userPostsRef = FirebaseConfig.getFirebase()
                    .child("postagens")
                    .child( selectedUser.getId() );

            //set user name on toolbar
            getSupportActionBar().setTitle( selectedUser.getName() );

            //recover user image
            String picturePath = selectedUser.getPicturePath();
            if( picturePath != null ){
                Uri url = Uri.parse( picturePath );
                Glide.with(FriendProfileActivity.this)
                        .load( url )
                        .into(imageProfile);
            }

        }

        initializeImageLoader();

        loadPostPictures();

        //open clicked picture
        gridViewProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Post post = posts.get( position );
                Intent i = new Intent(getApplicationContext(), VisualizePostActivity.class );

                i.putExtra("postagem", post );
                i.putExtra("usuario", selectedUser);

                startActivity( i );

            }
        });

    }

    public void initializeImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );

    }

    public void loadPostPictures(){

        //recover images posted by the user
        posts = new ArrayList<>();
        userPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //set grid size
                int gridSize = getResources().getDisplayMetrics().widthPixels;
                int imageSize = gridSize / 3;
                gridViewProfile.setColumnWidth( imageSize );

                List<String> urlPictures = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Post post = ds.getValue( Post.class );
                    posts.add( post );
                    urlPictures.add( post.getPostPicturePath() );
                }

                //set adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_post, urlPictures );
                gridViewProfile.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recoverLoggedUserData(){

        loggedUserRef = usersRef.child(loggedUserId);
        loggedUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //recover data from logged user
                        loggedUser = dataSnapshot.getValue( User.class );

                        checkFollowUserFriend();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void checkFollowUserFriend(){

        DatabaseReference followerRef = followersRef
                .child( selectedUser.getId() )
                .child(loggedUserId);

        followerRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot.exists() ){
                            //already following
                            Log.i("userData", ": following" );
                            enableFollowButton( true );
                        }else {
                            //not following yet
                            Log.i("userData", ": follow" );
                            enableFollowButton( false );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void enableFollowButton(boolean followsUser ){

        if ( followsUser ){
            buttonProfileAction.setText(R.string.following);
        }else {

            buttonProfileAction.setText(R.string.follow);

            buttonProfileAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveFollower(loggedUser, selectedUser);
                }
            });

        }

    }

    private void saveFollower(User userLogged, User userFriend){

        HashMap<String, Object> loggedUserData = new HashMap<>();
        loggedUserData.put("nome", userLogged.getName() );
        loggedUserData.put("caminhoFoto", userLogged.getPicturePath() );
        DatabaseReference followerRef = followersRef
                .child( userFriend.getId() )
                .child( userLogged.getId() );
        followerRef.setValue( loggedUserData );

        //change action button to following
        buttonProfileAction.setText(R.string.following);
        buttonProfileAction.setOnClickListener(null);

        //increment "following" of logged user
        int following = userLogged.getFollowing() + 1;
        HashMap<String, Object> followingData = new HashMap<>();
        followingData.put("seguindo", following );
        DatabaseReference userFollowing = usersRef
                .child( userLogged.getId() );
        userFollowing.updateChildren( followingData );

        //increment followers of a friend
        int followers = userFriend.getFollowers() + 1;
        HashMap<String, Object> followersData = new HashMap<>();
        followersData.put("seguidores", followers );
        DatabaseReference userFollowers = usersRef
                .child( userFriend.getId() );
        userFollowers.updateChildren( followersData );

    }

    @Override
    protected void onStart() {
        super.onStart();

        recoverProfileFriendData();
        recoverLoggedUserData();

    }

    @Override
    protected void onStop() {
        super.onStop();
        friendUserRef.removeEventListener(valueEventListenerFriendProfile);
    }

    private void recoverProfileFriendData(){

        friendUserRef = usersRef.child( selectedUser.getId() );
        valueEventListenerFriendProfile = friendUserRef.addValueEventListener(
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

    private void initializeComponents(){
        imageProfile = findViewById(R.id.imagePerfil);
        gridViewProfile = findViewById(R.id.gridViewPerfil);
        buttonProfileAction = findViewById(R.id.buttonAcaoPerfil);
        textPublications = findViewById(R.id.textPublicacoes);
        textFollowers = findViewById(R.id.textSeguidores);
        textFollowing = findViewById(R.id.textSeguindo);
        buttonProfileAction.setText(R.string.loading);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
