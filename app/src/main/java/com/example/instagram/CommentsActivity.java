package com.example.instagram;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.adapter.AdapterComment;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.Comment;
import com.example.instagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private EditText editTextComment;
    private RecyclerView recyclerComments;
    private String postId;
    private User user;
    private AdapterComment adapterComment;
    private List<Comment> commentsList = new ArrayList<>();

    private DatabaseReference firebaseRef;
    private DatabaseReference commentsRef;
    private ValueEventListener valueEventListenerComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        editTextComment = findViewById(R.id.editComentario);
        recyclerComments = findViewById(R.id.recyclerComentarios);

        user = UserFirebaseHelper.getLoggedUserData();
        firebaseRef = FirebaseConfig.getFirebase();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.comments);
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //set recyclerview
        adapterComment = new AdapterComment(commentsList, getApplicationContext() );
        recyclerComments.setHasFixedSize( true );
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(adapterComment);

        //recover post id
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            postId = bundle.getString("idPostagem");
        }

    }

    private void recoverComment(){

        commentsRef = firebaseRef.child("comentarios")
                .child(postId);
        valueEventListenerComments = commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    commentsList.add( ds.getValue(Comment.class) );
                }
                adapterComment.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverComment();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentsRef.removeEventListener(valueEventListenerComments);
    }

    public  void saveComment(View view){

        String commentText = editTextComment.getText().toString();
        if( commentText != null && !commentText.equals("") ){

            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserId( user.getId() );
            comment.setUserName( user.getName() );
            comment.setPicturePath( user.getPicturePath() );
            comment.setComment( commentText );
            if(comment.save()){
                Toast.makeText(this,
                        R.string.comment_saved,
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this,
                    R.string.insert_comment,
                    Toast.LENGTH_SHORT).show();
        }

        //clean typed comment
        editTextComment.setText("");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
