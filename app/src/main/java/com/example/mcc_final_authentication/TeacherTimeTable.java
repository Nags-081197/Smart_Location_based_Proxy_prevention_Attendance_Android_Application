package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TeacherTimeTable extends AppCompatActivity {

    TextView teacherttDatetext;
    Button teacherttgeneratebutton,dateBtn;
    ListView teachertteventslist;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "a";
    String date,date1 = "";
    DatabaseReference eventtocalendar;
    List<Events> eventsList;
    CalendarView mCalendarView;

    @Override
    protected void onStart() {
        super.onStart();

        eventtocalendar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                eventsList.clear();
                for(DataSnapshot eventsnapshot : dataSnapshot.getChildren() ){

                    Events events = eventsnapshot.getValue(Events.class);
                    eventsList.add(events);
                }

                Events_list adapter = new Events_list(TeacherTimeTable.this,eventsList);
                teachertteventslist.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_time_table);

        teacherttDatetext = findViewById(R.id.teacherttDatetext);
        teacherttgeneratebutton = findViewById(R.id.teacherttgenerateButton);
        dateBtn = findViewById(R.id.teacherttdateButton);
        teachertteventslist = findViewById(R.id.teachertteventslist);

        eventtocalendar = FirebaseDatabase.getInstance().getReference("EVENTS");

        eventsList = new ArrayList<>();

        mCalendarView = findViewById(R.id.teacherttcalendarView);

        teachertteventslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String date = eventsList.get(position).getEventdate();
                String parts[] = date.split("/");
                String day1 = parts[1];
                String month1 = parts[0];
                String year1 = parts[2];

                int day = Integer.valueOf( day1);
                int month = Integer.valueOf(month1)-1;
                int year = Integer.valueOf(year1);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                long milliTime = calendar.getTimeInMillis();

                mCalendarView.setDate (milliTime, true, true);

            }
        });

        teacherttgeneratebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (date1.length() > 0) {
                    final EditText eventname = new EditText(v.getContext());
                    final AlertDialog.Builder eventNameDialog = new AlertDialog.Builder(v.getContext());
                    eventNameDialog.setMessage("ENTER EVENT NAME : ");
                    eventNameDialog.setView(eventname);

                    eventNameDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String meventname = eventname.getText().toString().trim();
                            if (TextUtils.isEmpty(meventname)) {
                                Toast.makeText(TeacherTimeTable.this, "Event Name is Required !!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else{

                                String id=eventtocalendar.push().getKey();

                                Events event = new Events(id,meventname,date);

                                eventtocalendar.child(id).setValue(event);

                                Toast.makeText(TeacherTimeTable.this, "Event Added Successfully !!", Toast.LENGTH_SHORT).show();

                            }


                        }
                    });

                    eventNameDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    eventNameDialog.create().show();

                }
                else{
                    Toast.makeText(TeacherTimeTable.this, "Please Select a Date !!", Toast.LENGTH_SHORT).show();
                }
            }

        });


        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TeacherTimeTable.this,
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
                teacherttDatetext.setText(date);

            }
        };





    }




}
