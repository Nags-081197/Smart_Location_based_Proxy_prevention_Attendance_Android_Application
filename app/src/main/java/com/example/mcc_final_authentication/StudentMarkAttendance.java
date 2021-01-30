package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class StudentMarkAttendance extends AppCompatActivity {

    CodeScanner mCodeScanner;
    String QRdecoded,userID;
    public static String latittude,longitude,inputvalue;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    FirebaseAuth fAuth;
    DatabaseReference daysattendance,currentuserdata,classdetails;
    public static String fullname,email,collegeid,status,classdetailsstatus,classdetailslatitude,classdetailslongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_mark_attendance);

        CodeScannerView scannerView = findViewById(R.id.studentmarkattscanner_view);
        mCodeScanner = new CodeScanner(StudentMarkAttendance.this, scannerView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        daysattendance = FirebaseDatabase.getInstance().getReference("CLASSES");

        classdetails = FirebaseDatabase.getInstance().getReference("CLASSES");

        currentuserdata = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID);

        currentuserdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    collegeid = dataSnapshot.child("collegeid").getValue().toString();
                    status="NOTAPPLICABLE";

                }
                else{
                    Toast.makeText(StudentMarkAttendance.this, "Error Retrieving data!!", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QRdecoded = result.getText();
                        final AlertDialog.Builder attconfirmation;
                        attconfirmation = new AlertDialog.Builder(StudentMarkAttendance.this);
                        attconfirmation.setMessage("Are you sure you want to Mark your Presence:");

                        attconfirmation.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                statusCheck();

                                classdetails.child(QRdecoded).child(QRdecoded).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {

                                            classdetailslatitude = dataSnapshot.child("latitude").getValue().toString();
                                            classdetailslongitude = dataSnapshot.child("longitude").getValue().toString();
                                            classdetailsstatus=dataSnapshot.child("status").getValue().toString();

                                        }
                                        else{
                                            Toast.makeText(StudentMarkAttendance.this, "Error Retrieving data!!", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                if(classdetailsstatus != null &&classdetailsstatus.equals("CLOSED")){
                                    Toast.makeText(StudentMarkAttendance.this, "Sorry!! Attendance Window is currently CLOSED !", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StudentMarkAttendance.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if(classdetailsstatus != null &&classdetailsstatus.equals("OPEN")){
                                    fetchLocation();
                                    if((latittude != null)&&(longitude != null)){
                                        float[] results = new float[1];
                                        double classdetailslatitude1 = Double.parseDouble(classdetailslatitude);
                                        double classdetailslongitude1 = Double.parseDouble(classdetailslongitude);
                                        double latittude1 = Double.parseDouble(latittude);
                                        double longitude1 = Double.parseDouble(longitude);

                                        Location.distanceBetween(classdetailslatitude1, classdetailslongitude1, latittude1,longitude1, results);
                                        float distanceInMeters = results[0];
                                        boolean isWithin50m = distanceInMeters < 50;

                                        if(isWithin50m){
                                            DaysAttendance user1 = new DaysAttendance(
                                                    userID,
                                                    fullname,
                                                    email,
                                                    collegeid,
                                                    latittude,
                                                    longitude,
                                                    status
                                            );

                                            daysattendance.child(QRdecoded).child(userID).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){
                                                        Toast.makeText(StudentMarkAttendance.this, "Your Presence has been Marked !!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(StudentMarkAttendance.this,HomePage.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    else{
                                                        Toast.makeText(StudentMarkAttendance.this, "Error while update", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                            });




                                        }
                                        else{
                                            Toast.makeText(StudentMarkAttendance.this, "Student Not Present in Class !!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(StudentMarkAttendance.this,HomePage.class);
                                            startActivity(intent);
                                            finish();
                                        }


                                    }
                                    else{
                                        Toast.makeText(StudentMarkAttendance.this, "Cannot Proceed without GPS Turned ON !!", Toast.LENGTH_SHORT).show();
                                    }
                                }








                            }
                        });

                        attconfirmation.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        attconfirmation.create().show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                statusCheck();
                mCodeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(StudentMarkAttendance.this, "Camera Permission is Required !!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void fetchLocation() {


        if (ContextCompat.checkSelfPermission(StudentMarkAttendance.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(StudentMarkAttendance.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new android.app.AlertDialog.Builder(this)
                        .setMessage("Required Location Permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(StudentMarkAttendance.this,
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
                ActivityCompat.requestPermissions(StudentMarkAttendance.this,
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}