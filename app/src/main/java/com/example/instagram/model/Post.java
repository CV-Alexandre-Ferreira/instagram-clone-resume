package com.example.instagram.model;

import android.widget.Toast;

import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.UserFirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Post implements Serializable {
    private String id;
    private String userId;
    private String description;
    private String postPicturePath;

    public Post() {

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference postRef = firebaseRef.child("postagens");
        String postId = postRef.push().getKey();
        setId( postId );

    }

    public boolean save(DataSnapshot followersSnapshot){

        Map object = new HashMap();
        User loggedUser = UserFirebaseHelper.getLoggedUserData();

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();

        String combinationId = "/" + getUserId() + "/" + getId();
        object.put("/postagens" + combinationId, this );

        //ref to post
        for( DataSnapshot followers: followersSnapshot.getChildren() ){

            String followerId = followers.getKey();
            HashMap<String, Object> followerData = new HashMap<>();
            followerData.put("postPicturePath", getPostPicturePath() );
            followerData.put("description", getDescription() );
            followerData.put("id", getId() );
            followerData.put("name", loggedUser.getName() );
            followerData.put("picturePath", loggedUser.getPicturePath());

            String idsUpdate = "/" + followerId + "/" + getId();
            object.put("/feed" + idsUpdate, followerData );

        }

        firebaseRef.updateChildren( object );
        return true;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostPicturePath() {
        return postPicturePath;
    }

    public void setPostPicturePath(String postPicturePath) {
        this.postPicturePath = postPicturePath;
    }
}
