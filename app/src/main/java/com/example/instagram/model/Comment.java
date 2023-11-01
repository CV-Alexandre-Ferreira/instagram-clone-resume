package com.example.instagram.model;

import com.example.instagram.helper.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;

public class Comment {

    private String commentId;
    private String postId;
    private String userId;
    private String picturePath;
    private String userName;
    private String comment;

    public Comment() {
    }

    public boolean save(){

        DatabaseReference commentsRef = FirebaseConfig.getFirebase()
                .child("comentarios")
                .child( getPostId() );

        String commentKey = commentsRef.push().getKey();
        setCommentId( commentKey );
        commentsRef.child( getCommentId() ).setValue( this );

        return true;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
