package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TeacherGetAttReport extends AppCompatActivity {

    public static String inputvalue;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Button generate,dateBtn;
    private static final String TAG = "appliances";
    String date,date1 = "";
    TextView datetoset,counttoset;
    FirebaseAuth fAuth;
    String userID;
    DatabaseReference currentuserdata,daysattendance;
    String flag = "FALSE";
    ListView AttlistView;
    List<Student_info> Attlist;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_get_att_report);

        dateBtn = findViewById(R.id.teachergetattrptdateButton);
        generate = findViewById(R.id.teachergetattrptgenerateButton);
        counttoset = findViewById(R.id.teachergetattrptCounttext);
        datetoset = findViewById(R.id.teachergetattrptDatetext);
        AttlistView = findViewById(R.id.teachergetattrptattlist);

        Attlist = new ArrayList<>();

        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        currentuserdata = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID);

        daysattendance = FirebaseDatabase.getInstance().getReference("CLASSES");



        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TeacherGetAttReport.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                date1 = String.valueOf(month+ day + year);
                date = month + "/" + day + "/" + year;
                datetoset.setText(date);
                flag = "TRUE";

            }
        };

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("TRUE")){
                    inputvalue = date1+userID;
                    if(date1.length()>0){

                        daysattendance.child(inputvalue).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                Attlist.clear();

                                for(DataSnapshot studentSnapshot: dataSnapshot.getChildren() ){

                                    id = studentSnapshot.child("uid").getValue().toString();

                                    if(!(id.equals(userID))) {
                                        Student_info student = studentSnapshot.getValue(Student_info.class);
                                        Attlist.add(student);
                                    }
                                }

                                Att_list adapter = new Att_list(TeacherGetAttReport.this,Attlist);
                                AttlistView.setAdapter(adapter);



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        daysattendance.child(inputvalue).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                long count = dataSnapshot.getChildrenCount();
                                count = count - 1;

                                if(count>=1) {
                                    counttoset.setText("Total count : " + count);
                                }else{
                                    counttoset.setText("Report Unavailable");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
                else{
                    Toast.makeText(TeacherGetAttReport.this, "Please Select a Date !!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
