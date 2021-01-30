package com.example.mcc_final_authentication;

public class User {

    public String fullname, email, collegeid, accounttype,classes;

    public User(){

    }

    public User(String fullname, String email, String collegeid, String accounttype, String classes) {
        this.fullname = fullname;
        this.email = email;
        this.collegeid = collegeid;
        this.accounttype = accounttype;
        this.classes = classes;
    }

}


