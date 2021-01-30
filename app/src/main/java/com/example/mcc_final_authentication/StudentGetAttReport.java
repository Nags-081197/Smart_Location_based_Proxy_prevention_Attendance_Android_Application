package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Math.round;

public class StudentGetAttReport extends AppCompatActivity {

    FirebaseAuth fAuth;
    String userID;
    DatabaseReference presentcountattendance,fullattendance,currentuserdata;
    TextView totalcount,presentcount,percentagetoset,fullnametoset,collegeidtoset;
    public long count1 = 0;
    public long count = 0;
    public double percentageatt = 0;
    String fullname,collegeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_get_att_report);

        fullnametoset = findViewById(R.id.studentgetattrptname);
        collegeidtoset = findViewById(R.id.studentgetattrptcollegeid);
        totalcount = findViewById(R.id.studentgetattrpttotalnoclasses);
        presentcount = findViewById(R.id.studentgetattrptclassespresent);
        percentagetoset = findViewById(R.id.studentgetattrptattendancepercentage);

        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        currentuserdata = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID);

        fullattendance = FirebaseDatabase.getInstance().getReference();
        presentcountattendance = FirebaseDatabase.getInstance().getReference("CLASSES");


        currentuserdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    collegeid = dataSnapshot.child("collegeid").getValue().toString();
                    fullnametoset.setText("NAME: "+fullname);
                    collegeidtoset.setText("COLLEGE ID: "+collegeid);

                }
                else{
                    Toast.makeText(StudentGetAttReport.this, "Error Retrieving data!!", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        fullattendance.child("CLASSES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                count = dataSnapshot.getChildrenCount();
                count = count - 1;
                totalcount.setText("Total No. of Classes : "+count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        presentcountattendance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                count1 = 0;
                for(DataSnapshot classesSnapshot: dataSnapshot.getChildren() ){


                    if((classesSnapshot.child(userID).exists())){
                         count1 = count1 +1;
                    }
                }

                presentcount.setText("Total No. of Classes Present : "+count1);

                percentageatt = calculatePercentage(count1,count);

                percentageatt = round(percentageatt,2);

                percentagetoset.setText("Attendance Percentage: "+percentageatt+"%");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
