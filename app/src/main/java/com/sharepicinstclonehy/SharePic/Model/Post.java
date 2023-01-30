package com.sharepicinstclonehy.SharePic.Model;

public class Post {

    private String postid;
    private String imageurl;
    private String description;
    private String publisher;
    private String useremail;

    public Post(){
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public Post(String postid, String imageurl, String description, String publisher, String useremail) {
        this.postid = postid;
        this.imageurl = imageurl;
        this.description = description;
        this.publisher = publisher;
        this.useremail = useremail;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
}