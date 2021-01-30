package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentTimeTable extends AppCompatActivity {


    ListView studenttteventslist;
    DatabaseReference eventtocalendar;
    List<Events> eventsList;
    CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_time_table);


        eventtocalendar = FirebaseDatabase.getInstance().getReference("EVENTS");

        eventsList = new ArrayList<>();

        studenttteventslist = findViewById(R.id.studenttteventlist);
        mCalendarView = findViewById(R.id.studentttcalendarView);

        studenttteventslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    }



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

                Events_list adapter = new Events_list(StudentTimeTable.this,eventsList);
                studenttteventslist.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
