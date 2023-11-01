package com.example.instagram.model;

import com.example.instagram.helper.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;

public class LikedPost {
    public Feed feed;
    public User user;
    public int likesQuantity = 0;

    public LikedPost() {
    }

    public void save(){

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("nomeUsuario", user.getName() );
        userData.put("caminhoFoto", user.getPicturePath() );

        DatabaseReference postLikesRef = firebaseRef
                .child("postagens-curtidas")
                .child( feed.getId() )
                .child( user.getId() );
        postLikesRef.setValue( userData );

        updateLikeQuantity(1);

    }

    public void updateLikeQuantity(int value){

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();

        DatabaseReference postLikesRef = firebaseRef
                .child("postagens-curtidas")
                .child( feed.getId() )
                .child("qtdCurtidas");
        setLikesQuantity( getLikesQuantity() + value );
        postLikesRef.setValue( getLikesQuantity() );
    }

    public void remove(){

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();

        DatabaseReference postLikesRef = firebaseRef
                .child("postagens-curtidas")
                .child( feed.getId() )
                .child( user.getId() );
        postLikesRef.removeValue();

        updateLikeQuantity(-1);

    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLikesQuantity() {
        return likesQuantity;
    }

    public void setLikesQuantity(int likesQuantity) {
        this.likesQuantity = likesQuantity;
    }
}
