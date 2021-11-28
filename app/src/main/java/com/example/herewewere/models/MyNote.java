package com.example.herewewere.models;

public class MyNote {

    private String title, note, date, imagePath,latid,longid;
    private int id;

    public MyNote(int id, String title, String note, String date, String imagePath,String latid,String longid) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.date = date;
        this.imagePath = imagePath;
        this.latid = latid;
        this.longid = longid;

    }

    public MyNote(String title, String note, String date, String imagePath,String latid,String longid) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.imagePath = imagePath;
        this.latid = latid;
        this.longid = longid;

    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLatid() {
        return latid;
    }

    public String getLongid() {return longid; }
}
