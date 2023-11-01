package com.example.instagram.model;

import com.example.instagram.helper.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String picturePath;
    private int followers = 0;
    private int following = 0;
    private int posts = 0;

    public User() {
    }

    public void save(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference usersRef = firebaseRef.child("usuarios").child( getId() );
        usersRef.setValue( this );
    }

    public void updatePostQuantity(){

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference usersRef = firebaseRef
                .child("usuarios")
                .child( getId() );

        HashMap<String, Object> data = new HashMap<>();
        data.put("postagens", getPosts() );

        usersRef.updateChildren( data );

    }

    public void update(){

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();

        Map object = new HashMap();
        object.put("/usuarios/" + getId() + "/name", getName() );
        object.put("/usuarios/" + getId() + "/picturePath", getPicturePath() );

        firebaseRef.updateChildren( object );

    }

    public Map<String, Object> convertToMap(){

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail() );
        userMap.put("nome", getName() );
        userMap.put("id", getId() );
        userMap.put("caminhoFoto", getPicturePath() );
        userMap.put("seguidores", getFollowers() );
        userMap.put("seguindo", getFollowing() );
        userMap.put("postagens", getPosts() );

        return userMap;

    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
