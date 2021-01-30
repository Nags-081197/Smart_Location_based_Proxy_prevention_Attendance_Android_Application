package com.example.mcc_final_authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {

    ListView listView;
    FirebaseAuth fAuth;
    String userID,currentuser;
    TextView customergreetingmessage;
    DatabaseReference studentretrieve;

    String mTitle[] = {"CLASS A", "CLASS B", "CLASS C","Logout"};
    int images[] = {R.drawable.classroomlogoclassa,R.drawable.classroomlogoclass_b,R.drawable.classroomlogoclass_c,R.drawable.logout};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        customergreetingmessage = findViewById(R.id.customermessage);

        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        currentuser = fAuth.getCurrentUser().getEmail();
        studentretrieve = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID);


        studentretrieve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    customergreetingmessage.setTextSize(30);
                    customergreetingmessage.setText("Hi  "+dataSnapshot.child("fullname").getValue().toString()+" !!");

                }
                else{
                    customergreetingmessage.setTextSize(30);
                    customergreetingmessage.setText("Hi  "+currentuser+" !!");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        listView = findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(this, mTitle, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    Intent intent = new Intent(HomePage.this,Class_A.class);
                    startActivity(intent);

                }
                if (position ==  1) {
                    Intent intent = new Intent(HomePage.this,Class_B.class);
                    startActivity(intent);
                }
                if (position ==  2) {
                    Intent intent = new Intent(HomePage.this,Class_C.class);
                    startActivity(intent);
                }
                if (position ==  3) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }

            }
        });


    }
    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        int rImgs[];

        MyAdapter (Context c, String title[], int imgs[]) {
            super(c, R.layout.row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rImgs = imgs;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);

            // now set our resources on views
            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);

            return row;
        }
    }
}
