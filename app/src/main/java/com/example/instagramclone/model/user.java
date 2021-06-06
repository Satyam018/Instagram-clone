package com.example.instagramclone.model;

public class user {

    private String id;
    private String username;
    private String fullname;
    private String imgurl;
    private String bio;
    private user(){}

    public user(String id, String username, String fullname, String imgurl, String bio) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imgurl = imgurl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
