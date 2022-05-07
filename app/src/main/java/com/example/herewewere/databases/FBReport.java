package com.example.herewewere.databases;

public class FBReport {
    private String userid;
    private String comment;
    private String postid;
    private String userReportid;


    public FBReport(){};
    public FBReport(String userid, String comment, String postid,String userReportid) {
        this.userid = userid;
        this.comment = comment;
        this.postid = postid;
        this.userReportid = userReportid;

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

    public String getUserReportid() {
        return userReportid;
    }

    public void setUserReportid(String userReportid) {
        this.userReportid = userReportid;
    }
}
