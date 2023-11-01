package com.example.instagram.model;

public class Feed {

    private String id;
    private String postPicturePath;
    private String description;
    private String name;
    private String picturePath;

    public Feed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostPicturePath() {
        return postPicturePath;
    }

    public void setPostPicturePath(String postPicturePath) {
        this.postPicturePath = postPicturePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
