package com.example.instagram;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.example.instagram.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizePostActivity extends AppCompatActivity {

    private TextView textProfilePost, textPostLikesQuantity,
            textDescriptionPost, textVisualizePostComments;
    private ImageView selectedPostImage;
    private CircleImageView profilePostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_post);

        initializeComponents();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.visualize_post);
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){

            Post post = (Post) bundle.getSerializable("postagem");
            User user = (User) bundle.getSerializable("usuario");

            //show data of the user
            if(user.getPicturePath() != null) {
               Uri uri = Uri.parse( user.getPicturePath() );
                Glide.with(VisualizePostActivity.this)
                        .load( uri )
                        .into(profilePostImage);
                textProfilePost.setText( user.getName() );
            }

            //show post data
            if(post.getPostPicturePath() != null) {
                Uri uriPostagem = Uri.parse( post.getPostPicturePath() );
                Glide.with(VisualizePostActivity.this)
                        .load( uriPostagem )
                        .into(selectedPostImage);
                textDescriptionPost.setText( post.getDescription() );
            }

        }

    }

    private void initializeComponents(){
        textProfilePost = findViewById(R.id.textPerfilPostagem);
        textPostLikesQuantity = findViewById(R.id.textQtdCurtidasPostagem);
        textDescriptionPost = findViewById(R.id.textDescricaoPostagem);
        selectedPostImage = findViewById(R.id.imagePostagemSelecionada);
        profilePostImage = findViewById(R.id.imagePerfilPostagem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
