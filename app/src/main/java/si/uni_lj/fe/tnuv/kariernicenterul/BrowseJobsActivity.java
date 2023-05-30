package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowseJobsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;
    ArrayList<HashMap<String, String>> seznamDel;
    ListView lv;
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

        //set values for private variables
        seznamDel = new ArrayList<>();
        lv = findViewById(R.id.list);
        Context contextForAdapter = this;
        //variable delo is used for testing only TODO - remove
        HashMap<String,String> delo = new HashMap<>();
        delo.put("jobTitle","ASDF");
        seznamDel.add(delo);

        //start connection with FireBase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dr = db.getReference(Delo.class.getSimpleName());
        //load data from base
        dr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("test", "Error getting data", task.getException());
                }
                else {
                    try {
                        JSONObject jsonObj = new JSONObject(String.valueOf(task.getResult().getValue()));
                        //TODO - some iteration through jobs
                        //to je for testing purposes only
                        seznamDel.add(delo);

                        //after data has been processed append seznam to adapter
                        appendAdapter(contextForAdapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

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
    private void appendAdapter(Context context) {
        //creates adapter for ListView and appends Array of job offers to said ListView
        SimpleAdapter adapter = new SimpleAdapter(
                context,
                seznamDel,
                R.layout.job_offer,
                new String[]{"jobTitle"},
                new int[]{R.id.jobTitle}
        );
        lv.setAdapter(adapter);
    }
}