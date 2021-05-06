package com.iitism.docapp.model;

import android.net.Uri;

public class MyDoctorModel {
    private String Name,Query,Comment,uid,Gender,DOB,IDCount;
    private Uri uri;
    public MyDoctorModel() {
    }

    public MyDoctorModel(String name, String query, String comment, String uid, String gender, String DOB, String IDCount, Uri uri) {
        Name = name;
        Query = query;
        Comment = comment;
        this.uid = uid;
        Gender = gender;
        this.DOB = DOB;
        this.IDCount = IDCount;
        this.uri = uri;
    }

    public MyDoctorModel(String name, String query, String comment) {
        Name = name;
        Query = query;
        Comment = comment;
    }

    public MyDoctorModel(String name, String query, String comment, String uid, String Gender, String DOB) {
        Name = name;
        Query = query;
        Comment = comment;
        this.uid = uid;
        this.Gender = Gender;
        this.DOB = DOB;
    }

    public String getGender() { return Gender; }

    public String getDob() { return DOB; }

    public String getName() {
        return Name;
    }

    public String getQuery() {
        return Query;
    }

    public String getComment() {
        return Comment;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setQuery(String query) {
        Query = query;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getIDCount() {
        return IDCount;
    }

    public void setIDCount(String IDCount) {
        this.IDCount = IDCount;
    }
}
