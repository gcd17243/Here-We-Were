package com.example.herewewere.databases;

import java.util.Date;

public class FBPost {
    private String title;
    private String note;
    private String date;
    private String imgpath;
    private String latid;
    private String longid;
    private String userid;
    private Integer view;

    public FBPost(){};
    public FBPost(String title,String note,String date,String imgpath,String latid,String longid,String userid,Integer view)
    {
        this.title = title;
        this.note = note;
        this.date = date;
        this.imgpath = imgpath;
        this.latid = latid;
        this.longid = longid;
        this.userid = userid;
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getLatid() {
        return latid;
    }

    public void setLatid(String latid) {
        this.latid = latid;
    }

    public String getLongid() {
        return longid;
    }

    public void setLongid(String longid) {
        this.longid = longid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }
}
