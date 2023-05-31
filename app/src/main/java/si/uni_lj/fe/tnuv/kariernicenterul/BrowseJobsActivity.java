package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BrowseJobsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;
    ArrayList<HashMap<String, String>> seznamDel;
    ListView lv;
    ProgressBar loadingIndicator;
    Context contextForAdapter;
    ArrayList<String> favourites;

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
                                delo.put("jobId",key);
                                delo.put("jobTitle",String.valueOf(deloi.get("naziv")));
                                delo.put("jobPay", String.format("%.2f", Float.valueOf(String.valueOf(deloi.get("placa")))) + getResources().getString(R.string.placaNeto));
                                delo.put("freeSpace", getResources().getString(R.string.prostaMesta) + String.valueOf(deloi.get("prostaMesta")));
                                delo.put("duration", getResources().getString(R.string.trajanje) + String.valueOf(deloi.get("trajanje")));
                                delo.put("worktime", getResources().getString(R.string.delovnik) + String.valueOf(deloi.get("delovnik")));
                                delo.put("start", getResources().getString(R.string.zacetekDela) + String.valueOf(deloi.get("zacetekDela")));
                                delo.put("description", String.valueOf(deloi.get("opis")));

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

        favourites = new ArrayList<String>();
        File file = this.getFileStreamPath("savedJobs.txt");
        boolean remove = false;
        if(file.exists() && file != null) {
            try (FileInputStream fis = openFileInput("savedJobs.txt");
                 InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    favourites.add(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
            }
        }
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
                new String[]{"jobId", "jobTitle", "jobPay", "freeSpace", "duration", "worktime", "start"},
                new int[]{R.id.jobId, R.id.jobTitle, R.id.jobPay, R.id.freeSpace, R.id.duration, R.id.worktime, R.id.start}
        ){
            //getView has to be overWritten, else buttons couldn't be clickable within a list item
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                Button showMore = (Button) view.findViewById(R.id.showMore);
                Button favourite = (Button) view.findViewById(R.id.favourite);
                //there's a hidden TextView in each List item, that holds an ID of the Job Offer
                TextView idView = (TextView) view.findViewById(R.id.jobId);
                String id = idView.getText().toString();
                showMore.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //that ID is passed, so that the correct offer may be shown in detail on button press
                        openJobDetailView(id);
                    }
                });


                //COMMENTED CODE HANDLES GRAPHIC CHANGES OF STARS, but it's buggy

                //Drawable fullStar = getDrawable(R.drawable.baseline_star_24);
                //Drawable emptyStar = getDrawable(R.drawable.baseline_star_outline_24);

                //Log.d("test",favourites+" - "+id);
                //if(favourites.contains(id)){ //TODO - possibly problematic if the firebase data loads before local savedJobs.txt file is read, because favorites would still be unset
                //    favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, fullStar);
                //}
                favourite.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        toggleFavourite(id);

                        //if(favourites.contains(id)){
                        //    favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, fullStar);
                        //}
                        //else{
                        //    favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, emptyStar);
                        //}
                    }
                });
                return view;
            }
        };
        loadingIndicator.setVisibility(View.GONE);
        lv.setAdapter(adapter);
    }
    private void toggleFavourite(String jobToFavourite) {
        if(favourites.contains(jobToFavourite)){
            favourites.remove(jobToFavourite);
        }
        else{
            favourites.add(jobToFavourite);
        }
        String toSave = "";
        if (favourites.size() >= 1){
            for (int i = 0; i < favourites.size(); i++){
                toSave += (favourites.get(i)+"\n");
            }
        }
        try (FileOutputStream fos = openFileOutput("savedJobs.txt", Context.MODE_PRIVATE)) {
            fos.write(toSave.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openJobDetailView(String id) {
        setContentView(R.layout.job_detail);

        TextView jobTitle = findViewById(R.id.jobTitle);
        TextView jobPay = findViewById(R.id.jobPay);
        TextView freeSpace = findViewById(R.id.freeSpace);
        TextView duration = findViewById(R.id.duration);
        TextView worktime = findViewById(R.id.worktime);
        TextView start = findViewById(R.id.start);
        TextView description = findViewById(R.id.description);
        ImageView back = findViewById(R.id.back);


        for (HashMap<String, String> ponudba : seznamDel){
            if(ponudba.get("jobId")==id){
                jobTitle.setText(ponudba.get("jobTitle"));
                jobPay.setText(ponudba.get("jobPay"));
                freeSpace.setText(ponudba.get("freeSpace"));
                duration.setText(ponudba.get("duration"));
                worktime.setText(ponudba.get("worktime"));
                start.setText(ponudba.get("start"));
                description.setText(ponudba.get("description"));
                break;
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.activity_browse_jobs);
                setView();
                appendAdapter(contextForAdapter);
            }
        });
        setBottomNav();
    }

    private void setView() {
        lv = findViewById(R.id.list);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        contextForAdapter = this;
        setBottomNav();
    }
}