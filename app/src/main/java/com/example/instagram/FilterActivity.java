package com.example.instagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.adapter.AdapterMiniatures;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView selectedPictureImage;
    private Bitmap image;
    private Bitmap filterImage;
    private TextInputEditText textFilterDescription;
    private List<ThumbnailItem> filtersList;
    private String loggedUserId;
    private User loggedUser;
    private AlertDialog dialog;

    private RecyclerView recyclerFilters;
    private AdapterMiniatures adapterMiniatures;

    private DatabaseReference usersRef;
    private DatabaseReference loggedUserRef;
    private DatabaseReference firebaseRef;
    private DataSnapshot followersSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //initial config
        filtersList = new ArrayList<>();
        firebaseRef = FirebaseConfig.getFirebase();
        loggedUserId = UserFirebaseHelper.getUserId();
        usersRef = FirebaseConfig.getFirebase().child("usuarios");

        //initialize components
        selectedPictureImage = findViewById(R.id.imageFotoEscolhida);
        recyclerFilters = findViewById(R.id.recyclerFiltros);
        textFilterDescription = findViewById(R.id.textDescricaoFiltro);

        recoverPostData();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.filters);
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        //recover selected image
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            byte[] imageData = bundle.getByteArray("fotoEscolhida");
            image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length );
            selectedPictureImage.setImageBitmap(image);
            filterImage = image.copy(image.getConfig(), true );

            //set filter recyclerView
            adapterMiniatures = new AdapterMiniatures(filtersList, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false );
            recyclerFilters.setLayoutManager( layoutManager );
            recyclerFilters.setAdapter(adapterMiniatures);

            recyclerFilters.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFilters,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    ThumbnailItem item = filtersList.get(position);

                                    filterImage = image.copy(image.getConfig(), true );
                                    Filter filter = item.filter;
                                    selectedPictureImage.setImageBitmap( filter.processFilter(filterImage) );

                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );

            recoverFilters();

        }

    }

    private void openLoadingDialog(String title){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle( title );
        alert.setCancelable(false);
        alert.setView(R.layout.loading);

        dialog = alert.create();
        dialog.show();

    }

    private void recoverPostData(){

        openLoadingDialog(getResources().getString(R.string.loading_data));
        loggedUserRef = usersRef.child(loggedUserId);
        loggedUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //recover logged user data
                        loggedUser = dataSnapshot.getValue( User.class );

                        /*
                         * recover followers */
                        DatabaseReference followersRef = firebaseRef
                                .child("seguidores")
                                .child(loggedUserId);
                        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                followersSnapshot = dataSnapshot;
                                dialog.cancel();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void recoverFilters(){

        //clean itens
        ThumbnailsManager.clearThumbs();
        filtersList.clear();

        //config normal filter
        ThumbnailItem item = new ThumbnailItem();
        item.image = image;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb( item );

        //list all filters
        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filter: filters ){

            ThumbnailItem filterItem = new ThumbnailItem();
            filterItem.image = image;
            filterItem.filter = filter;
            filterItem.filterName = filter.getName();

            ThumbnailsManager.addThumb( filterItem );

        }

        filtersList.addAll( ThumbnailsManager.processThumbs(getApplicationContext()) );
        adapterMiniatures.notifyDataSetChanged();

    }

    private void publishPost(){

        openLoadingDialog(getResources().getString(R.string.saving_post));
        final Post post = new Post();
        post.setUserId(loggedUserId);
        post.setDescription( textFilterDescription.getText().toString() );

        //recover image data for firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        filterImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageData = baos.toByteArray();

        //Save image on firebase storage
        StorageReference storageRef = FirebaseConfig.getFirebaseStorage();
        StorageReference imageRef = storageRef
                .child("imagens")
                .child("postagens")
                .child( post.getId() + ".jpeg");

        UploadTask uploadTask = imageRef.putBytes( imageData );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FilterActivity.this,
                        R.string.image_saving_error,
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //recover image location
                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        post.setPostPicturePath( url.toString() );
                        post.save(followersSnapshot);
                    }
                });

                //update posts quantity
                int postQuantity = loggedUser.getPosts() + 1;
                loggedUser.setPosts( postQuantity );
                loggedUser.updatePostQuantity();


                //save post
                if( post.save(followersSnapshot) ){


                    Toast.makeText(FilterActivity.this,
                            R.string.success_saving_post,
                            Toast.LENGTH_SHORT).show();

                    dialog.cancel();
                    finish();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.ic_salvar_postagem :
                publishPost();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
