package com.example.mcc_final_authentication;

public class Events {

    String uid,eventname,eventdate;

    public Events(){

    }

    public Events(String uid, String eventname, String eventdate) {
        this.uid = uid;
        this.eventname = eventname;
        this.eventdate = eventdate;
    }

    public String getUid() {
        return uid;
    }

    public String getEventname() {
        return eventname;
    }

    public String getEventdate() {
        return eventdate;
    }
}
