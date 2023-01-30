package com.sharepicinstclonehy.SharePic.Model;

public class User {

    private String username;        //database deki yollarla ayni olali !!!!!
    private String fullname;
    private String imageurl;
    private String bio;
    private String id;
    private String email;

    public User(){

    }

    public User(String username, String fullname, String imageurl, String bio, String id, String email) {
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
        this.id = id;
        this.email = email;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}