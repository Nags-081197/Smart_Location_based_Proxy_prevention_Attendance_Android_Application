package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Class_C extends AppCompatActivity {


    FirebaseAuth fAuth;
    String userID,currentuser;
    Button markattendance;
    Button getattreport;
    Button timetable;
    DatabaseReference accountype;
    String currentaccounttype ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class__c);

        markattendance = findViewById(R.id.classC_markattendance);
        getattreport = findViewById(R.id.classC_getattreport);
        timetable = findViewById(R.id.classC_timetable);

        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        currentuser = fAuth.getCurrentUser().getEmail();
        accountype = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID);


        accountype.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    currentaccounttype = dataSnapshot.child("accounttype").getValue().toString();
                }
                else{
                    Toast.makeText(Class_C.this, "Error Retrieving data!!", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        markattendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentaccounttype.equals("STUDENT")){
                    startActivity(new Intent(getApplicationContext(),StudentMarkAttendance.class));
                    finish();
                }
                else if(currentaccounttype.equals("TEACHER")){
                    startActivity(new Intent(getApplicationContext(),TeacherMarkAttendance.class));
                    finish();
                }

            }
        });
        getattreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentaccounttype.equals("STUDENT")){
                    startActivity(new Intent(getApplicationContext(),StudentGetAttReport.class));
                    finish();
                }
                else if(currentaccounttype.equals("TEACHER")){
                    startActivity(new Intent(getApplicationContext(),TeacherGetAttReport.class));
                    finish();
                }

            }
        });

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentaccounttype.equals("STUDENT")){
                    startActivity(new Intent(getApplicationContext(),StudentTimeTable.class));
                    finish();
                }
                else if(currentaccounttype.equals("TEACHER")){
                    startActivity(new Intent(getApplicationContext(),TeacherTimeTable.class));
                    finish();
                }

            }
        });

    }

}
