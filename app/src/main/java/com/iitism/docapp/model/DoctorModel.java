package com.iitism.docapp.model;

import android.net.Uri;

public class DoctorModel {

    private String name,email,phone,speciality,qualification,Uid,url;
    private Uri imageUri;

    public DoctorModel(String name, String email, String phone, String speciality, String qualification, String uid, String url) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.speciality = speciality;
        this.qualification = qualification;
        Uid = uid;
        this.url = url;
    }



    public DoctorModel(String name, String email, String phone, String speciality, String qualification, Uri imageUri, String Uid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.speciality = speciality;
        this.qualification = qualification;
        this.imageUri = imageUri;
        this.Uid=Uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getQualification() {
        return qualification;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getUid() {
        return Uid;
    }

    public String getUrl() {
        return url;
    }
}
