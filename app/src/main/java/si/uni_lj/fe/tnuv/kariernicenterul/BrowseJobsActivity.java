package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.util.Iterator;

public class BrowseJobsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;
    ArrayList<HashMap<String, String>> seznamDel;
    ListView lv;
    Context contextForAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_jobs);

        //set values for private variables
        seznamDel = new ArrayList<>();
        setView();

        //start connection with FireBase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dr = db.getReference(Delo.class.getSimpleName());
        //load data from base
        dr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                }
                else {
                    try {
                        JSONObject jsonObj = new JSONObject(String.valueOf(task.getResult().getValue()));
                        //iteration through jobs
                        Iterator<String> keys = jsonObj.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            if (jsonObj.get(key) instanceof JSONObject) {
                                //parsing data from fireBase and bulding seznamDel - TODO add more data(so far it's title only)

                                JSONObject deloi = new JSONObject(String.valueOf(jsonObj.get(key)));

                                HashMap<String,String> delo = new HashMap<>();
                                delo.put("id",key);
                                delo.put("jobTitle",String.valueOf(deloi.get("naziv")));
                                delo.put("jobPay", String.format("%.2f", Float.valueOf(String.valueOf(deloi.get("placa")))) + getResources().getString(R.string.placaNeto));
                                delo.put("freeSpace", getResources().getString(R.string.prostaMesta) + String.valueOf(deloi.get("prostaMesta")));
                                delo.put("duration", getResources().getString(R.string.trajanje) + String.valueOf(deloi.get("trajanje")));
                                delo.put("worktime", getResources().getString(R.string.delovnik) + String.valueOf(deloi.get("delovnik")));
                                delo.put("start", getResources().getString(R.string.zacetekDela) + String.valueOf(deloi.get("zacetekDela")));

                                seznamDel.add(delo);
                            }
                        }
                        //after data has been processed append seznam to adapter
                        appendAdapter(contextForAdapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }

    private void setBottomNav() {
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
                new String[]{"jobTitle", "jobPay", "freeSpace", "duration", "worktime", "start"},
                new int[]{R.id.jobTitle, R.id.jobPay, R.id.freeSpace, R.id.duration, R.id.worktime, R.id.start}
        );

        Log.d("test", "appendingAdapter:"+context);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id)->{

            String ime = (String)((HashMap)seznamDel.get(position)).get("id");
            Toast.makeText(this, "Klik na " + ime, Toast.LENGTH_SHORT).show();

            setContentView(R.layout.job_detail);

            TextView jobTitle = findViewById(R.id.jobTitle);
            TextView jobPay = findViewById(R.id.jobPay);
            TextView freeSpace = findViewById(R.id.freeSpace);
            TextView duration = findViewById(R.id.duration);
            TextView worktime = findViewById(R.id.worktime);
            TextView start = findViewById(R.id.start);
            TextView description = findViewById(R.id.description);
            Button back = findViewById(R.id.back);

            jobTitle.setText("NAZIV");
            jobPay.setText("NAZIV");
            freeSpace.setText("NAZIV");
            duration.setText("NAZIV");
            worktime.setText("NAZIV");
            start.setText("NAZIV");
            description.setText("NAZIV");
            back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setContentView(R.layout.activity_browse_jobs);
                    setView();
                    appendAdapter(contextForAdapter);
                }
            });

        });
        Toast.makeText(this, "Klik na ", Toast.LENGTH_SHORT).show();
    }

    private void setView() {
        lv = findViewById(R.id.list);
        contextForAdapter = this;
        setBottomNav();
    }
}