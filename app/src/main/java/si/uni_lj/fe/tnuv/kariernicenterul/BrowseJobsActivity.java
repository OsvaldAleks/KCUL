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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowseJobsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;
    ArrayList<HashMap<String, String>> seznamDel;
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
        seznamDel = new ArrayList<>();

        /*
        dao = new DAODelo();
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

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dr = db.getReference(Delo.class.getSimpleName());

        //dao.get();

        ListView lv = findViewById(R.id.list);

        Context contextForAdapter = this;

        HashMap<String,String> delo = new HashMap<>();
        delo.put("jobTitle","ASDF");
        seznamDel.add(delo);

        dr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("test", "Error getting data", task.getException());
                }
                else {
                    Log.d("test", "123");
                    try {
                        JSONObject jsonObj = new JSONObject(String.valueOf(task.getResult().getValue()));

                        //TODO - some iteration through jobs

                        //to je for testing purposes only
                        Log.d("test", jsonObj+"succ");
                        //updateArrayList();

                        seznamDel.add(delo);


                        SimpleAdapter adapter = new SimpleAdapter(
                                contextForAdapter,
                                seznamDel,
                                R.layout.job_offer,
                                new String[]{"jobTitle"},
                                new int[]{R.id.jobTitle}
                        );

                        //vstavi v activity_main
                        lv.setAdapter(adapter);

                    } catch (JSONException e) {
                        Log.d("test", "err");
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        /*
        //to je for testing purposes only
        HashMap<String, String> tmp2 = new HashMap<>();
        tmp2.put("jobTitle", "TITLE");
        dao.jobs.add(tmp2);

        tmp2.put("jobTitle", "TITLE");
        dao.jobs.add(tmp2);
*/

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

    private void appendAdapter() {

    }

    private void generateCL() {


    }
}