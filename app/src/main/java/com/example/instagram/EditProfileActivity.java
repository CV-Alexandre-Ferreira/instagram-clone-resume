package com.example.instagram;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.Permission;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView imageEditProfile;
    private TextView textChangePicture;
    private TextInputEditText editProfileName, editProfileEmail;
    private Button buttonSaveChanges;
    private User loggedUser;
    private static final int GALLERY_SELECTION = 200;
    private StorageReference storageRef;
    private String userId;

    private String[] necessaryPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Permission.validatePermissions(necessaryPermissions, this, 1 );

        loggedUser = UserFirebaseHelper.getLoggedUserData();
        storageRef = FirebaseConfig.getFirebaseStorage();
        userId = UserFirebaseHelper.getUserId();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.edit_profile);
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        initializeComponents();

        //get user data
        FirebaseUser profileUser = UserFirebaseHelper.getCurrentUser();
        editProfileName.setText( profileUser.getDisplayName().toUpperCase() );
        editProfileEmail.setText( profileUser.getEmail() );

        Uri url = profileUser.getPhotoUrl();
        if( url != null ){
            Glide.with(EditProfileActivity.this)
                    .load( url )
                    .into(imageEditProfile);
        }else {
            imageEditProfile.setImageResource(R.drawable.avatar);
        }

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameUpdated = editProfileName.getText().toString();

                //update name on profile
                UserFirebaseHelper.updateUserName( nameUpdated );

                //update name on database
                loggedUser.setName( nameUpdated );
                loggedUser.update();

                Toast.makeText(EditProfileActivity.this,
                        R.string.data_changed,
                        Toast.LENGTH_SHORT).show();

            }
        });

        //change user picture
        textChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, GALLERY_SELECTION);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap image = null;

            try {

                switch ( requestCode ){
                    case GALLERY_SELECTION:
                        Uri selectedImagePlace = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImagePlace );
                        break;
                }

                //case an image was selected
                if ( image != null ){

                    imageEditProfile.setImageBitmap( image );

                    //recover image data for firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //save image on firebase
                    StorageReference imageRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child( userId + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes( imageData );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this,
                                    R.string.image_upload_error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    updateUserPicture(url);
                                }
                            });

                            Toast.makeText(EditProfileActivity.this,
                                    R.string.image_upload_success,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void updateUserPicture(Uri url){

        UserFirebaseHelper.updateUserPicture( url );

        //update picture on firebase
        loggedUser.setPicturePath( url.toString() );
        loggedUser.update();

        Toast.makeText(EditProfileActivity.this,
                R.string.picture_updated,
                Toast.LENGTH_SHORT).show();

    }

    public void initializeComponents(){

        imageEditProfile = findViewById(R.id.imageEditarPerfil);
        textChangePicture = findViewById(R.id.textAlterarFoto);
        editProfileName = findViewById(R.id.editNomePerfil);
        editProfileEmail = findViewById(R.id.editEmailPerfil);
        buttonSaveChanges = findViewById(R.id.buttonSalvarAlteracoes);
        editProfileEmail.setFocusable(false);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }
}
