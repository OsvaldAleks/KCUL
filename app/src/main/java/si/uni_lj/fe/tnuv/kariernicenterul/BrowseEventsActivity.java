package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BrowseEventsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);


        String dogodek = "dogodek1";
        readData(dogodek);

        setBottomNav();
    }

    private void readData(String dogodek){ //dogodek1

        dr = FirebaseDatabase.getInstance().getReference("Dogodki");
        dr.child(dogodek).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        DataSnapshot dataSnapshot = task.getResult();
                        String datum = String.valueOf(dataSnapshot.child("datum").getValue());
                        String ime = String.valueOf(dataSnapshot.child("ime").getValue());
                        String lokacija = String.valueOf(dataSnapshot.child("lokacija").getValue());
                        String predavatelj = String.valueOf(dataSnapshot.child("predavatelj").getValue());

                        System.out.println(datum);
                        System.out.println(predavatelj);

                    }
                    else {
                        Toast.makeText(BrowseEventsActivity.this, "Dogodek ne obstaja", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(BrowseEventsActivity.this,"Branje neuspesno" , Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void setBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.events);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.events:
                        return true;
                    case R.id.cv:
                        startActivity(new Intent(getApplicationContext(), EditProfile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.jobs:
                        startActivity(new Intent(getApplicationContext(), BrowseJobsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationView.setSelectedItemId(R.id.events);
    };
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }
}