package com.sharepicinstclonehy.SharePic.Model;

public class Comment {          //database comment altına baktım

    private String comment;
    private String publisher;
    private String commentid;


    public Comment() {
    }

    public Comment(String comment, String publisher, String commentid) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid= commentid;
    }


    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}

