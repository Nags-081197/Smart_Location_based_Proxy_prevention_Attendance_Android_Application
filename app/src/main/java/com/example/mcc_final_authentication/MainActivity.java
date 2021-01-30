package com.example.mcc_final_authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @SuppressWarnings("ALL")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
        public void logout(View view){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Register.class));
            finish();
        }
    public void gotohome(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,HomePage.class));
        finish();
    }

}
