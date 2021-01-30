package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TeacherMarkAttendance extends AppCompatActivity {


    public static String latittude,longitude,inputvalue;
    FirebaseAuth fAuth;
    String date,date1 = "";
    String flag = "";
    String userID;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Button generate,dateBtn,stopatt,startatt;
    TextView datetoset;
    ImageView qrimg;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    DatabaseReference daysattendance,currentuserdata;
    private static final String TAG = "appliances";
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    String fullname,email,collegeid,status,closestatus ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_mark_attendance);

        qrimg = findViewById(R.id.teachermarkattQR);
        dateBtn = findViewById(R.id.teachermarkattdateButton);
        generate = findViewById(R.id.teachermarkattgenerateButton);
        stopatt = findViewById(R.id.teachermarkattStopButton);
        startatt = findViewById(R.id.teachermarkattStartButton);
        datetoset = findViewById(R.id.teachermarkattDatetext);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        currentuserdata = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID);
        daysattendance = FirebaseDatabase.getInstance().getReference("CLASSES");

        currentuserdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    collegeid = dataSnapshot.child("collegeid").getValue().toString();
                    status="OPEN";

                }
                else{
                    Toast.makeText(TeacherMarkAttendance.this, "Error Retrieving data!!", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        TeacherMarkAttendance.this,
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

            }
        };


        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputvalue = date1+userID;
                if(date1.length()>0){
                    WindowManager managaer = (WindowManager)getSystemService(WINDOW_SERVICE);
                    Display display = managaer.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width= point.x;
                    int height= point.y;
                    int smallerdimension = width<height?width:height;
                    smallerdimension = smallerdimension*3/4;
                    qrgEncoder= new QRGEncoder(inputvalue,null, QRGContents.Type.TEXT,smallerdimension);
                    try{
                        bitmap= qrgEncoder.encodeAsBitmap();
                        qrimg.setImageBitmap(bitmap);
                        flag = "TRUE";
                    }
                    catch(WriterException e){
                        Log.v(TAG,e.toString());
                        flag="FALSE";
                    }

                }
                else{

                    Toast.makeText(TeacherMarkAttendance.this, "Please Select a Date !!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        stopatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("TRUE")){
                    fetchLocation();
                    closestatus = "CLOSED";
                    if((latittude != null)&&(longitude != null)){
                        DaysAttendance user1 = new DaysAttendance(
                                userID,
                                fullname,
                                email,
                                collegeid,
                                latittude,
                                longitude,
                                closestatus
                        );
                        daysattendance.child(inputvalue).child(inputvalue).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(TeacherMarkAttendance.this, "Attendance window is now closed!!", Toast.LENGTH_SHORT).show();

                                }

                                else{
                                    Toast.makeText(TeacherMarkAttendance.this, "Error while update", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                    else{
                        Toast.makeText(TeacherMarkAttendance.this, "Try it one more Time !!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(TeacherMarkAttendance.this, "Generate QR CODE first!!", Toast.LENGTH_SHORT).show();
                }
            }


        });

        startatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("TRUE")){
                    fetchLocation();
                    if((latittude != null)&&(longitude != null)){
                        DaysAttendance user1 = new DaysAttendance(
                                userID,
                                fullname,
                                email,
                                collegeid,
                                latittude,
                                longitude,
                                status
                        );
                        daysattendance.child(inputvalue).child(inputvalue).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(TeacherMarkAttendance.this, "Attendance window is now Open!!", Toast.LENGTH_SHORT).show();

                                }

                                else{
                                    Toast.makeText(TeacherMarkAttendance.this, "Error while update", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                    else{
                        Toast.makeText(TeacherMarkAttendance.this, "Try it one more Time !!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(TeacherMarkAttendance.this, "Generate QR CODE first!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void fetchLocation() {


        if (ContextCompat.checkSelfPermission(TeacherMarkAttendance.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TeacherMarkAttendance.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setMessage("Required Location Permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(TeacherMarkAttendance.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(TeacherMarkAttendance.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                 latittude = String.valueOf(location.getLatitude());
                                 longitude = String.valueOf(location.getLongitude());



                            }
                        }
                    });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
            }else{

            }
        }
    }
}
