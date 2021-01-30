package com.example.mcc_final_authentication;

public class Student_info {

    public String uid,fullname, email, collegeid,latitude,longitude,status;

    public  Student_info(){

    }

    public Student_info(String uid, String fullname, String email, String collegeid, String latitude, String longitude, String status) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
        this.collegeid = collegeid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getCollegeid() {
        return collegeid;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }
}

