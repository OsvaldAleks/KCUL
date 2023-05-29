package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BrowseJobsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    public static class Post {

        public String naziv;
        public String opis;
        public int placa;
        public Post(String naziv, String opis, int placa) {
            // ...
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_jobs);



        DAODelo dao = new DAODelo();
        /*
        THIS CODE CREATES NEW Delo IN DATABASE
        IT WAS HERE FOR TESTING

        // Read from the database
        Delo d = new Delo("ASDF","fdDA",(float)8.99);

        dao.add(d).addOnSuccessListener(suc->{
            Toast.makeText(this, "succ", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(er->{
            Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
        });
        */
        Log.d("firebase",""+dao.get());



        //bottom navigation code
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.jobs);

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
                        startActivity(new Intent(getApplicationContext(), BrowseEventsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.cv:
                        startActivity(new Intent(getApplicationContext(), EditProfile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.jobs:

                        return true;
                }
                return false;
            }
        });
    }

    private void generateCL() {


    }
}