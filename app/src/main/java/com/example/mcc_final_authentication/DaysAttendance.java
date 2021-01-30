package com.example.mcc_final_authentication;

public class DaysAttendance {

    public String uid,fullname, email, collegeid,latitude,longitude,status;

    public DaysAttendance(){

    }

    public DaysAttendance(String uid, String fullname, String email, String collegeid, String latitude, String longitude, String status) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
        this.collegeid = collegeid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }
}
