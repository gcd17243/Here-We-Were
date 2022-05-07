package com.example.herewewere.databases;

public class FBComment {
    private String userid;
    private String comment;
    private String postid;

    public FBComment(){};
    public FBComment(String userid, String comment, String postid) {
        this.userid = userid;
        this.comment = comment;
        this.postid = postid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
