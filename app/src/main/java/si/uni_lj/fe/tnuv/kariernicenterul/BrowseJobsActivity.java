package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public static final String SAVED_JOBS_FILE = "savedJobs.txt";
    public static final String APPLIED_JOBS_FILE = "prijave.txt";
    public static final String USER_DATA_FILE = "userData.json";
    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;
    ArrayList<HashMap<String, String>> seznamDel;
    ListView lv;
    ProgressBar loadingIndicator;
    Context contextForAdapter;
    ArrayList<String> favourites;
    ArrayList<String> applied;
    String detailViewID;
    boolean detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_jobs);

        //start connection with FireBase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dr = db.getReference(Delo.class.getSimpleName());
        seznamDel = new ArrayList<>();

        Intent intent = getIntent();
        detailViewID = intent.getStringExtra(MainActivity.MESSAGE_KEY);
        if(detailViewID != null){
            readFavourites();
            readAppliedJobs();
            loadSingleJobAndOpenDetailView(detailViewID);
        }
        else {
            //set values for private variables
            setView();
            readFavourites();
            readAppliedJobs();
            loadSeznamDel(); //method also appends adapter after loading is done
        }
/*
        //TODO - detele test code
        Button addNew = findViewById(R.id.addItem);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewJobToFB();
            }
        });
        */
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationView.setSelectedItemId(R.id.jobs);
    };
    private void loadSeznamDel() {
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
                                //parsing data from fireBase and bulding seznamDel
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
    }
    private void loadSingleJobAndOpenDetailView(String id) {
        dr.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                } else {
                    try {
                        JSONObject deloi = new JSONObject(String.valueOf(task.getResult().getValue()));

                        HashMap<String, String> delo = new HashMap<>();
                        delo.put("jobId", id);
                        delo.put("jobTitle", String.valueOf(deloi.get("naziv")));
                        delo.put("jobPay", String.format("%.2f", Float.valueOf(String.valueOf(deloi.get("placa")))) + getResources().getString(R.string.placaNeto));
                        delo.put("freeSpace", getResources().getString(R.string.prostaMesta) + String.valueOf(deloi.get("prostaMesta")));
                        delo.put("duration", getResources().getString(R.string.trajanje) + String.valueOf(deloi.get("trajanje")));
                        delo.put("worktime", getResources().getString(R.string.delovnik) + String.valueOf(deloi.get("delovnik")));
                        delo.put("start", getResources().getString(R.string.zacetekDela) + String.valueOf(deloi.get("zacetekDela")));
                        delo.put("description", String.valueOf(deloi.get("opis")));

                        seznamDel.add(delo);

                        openJobDetailView(id,2);
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }
    private void readFavourites() {
        favourites = readFile(SAVED_JOBS_FILE);
    }
    private void readAppliedJobs(){
        applied = readFile(APPLIED_JOBS_FILE);
    }
    private ArrayList<String> readFile(String FILENAME){
        ArrayList<String> content = new ArrayList<String>();
        File file = this.getFileStreamPath(FILENAME);
        if(file.exists() && file != null) {
            try (FileInputStream fis = openFileInput(FILENAME);
                 InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    content.add(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
            }
        }
        return content;
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
                        resetSeznam();
                        return true;
                }
                return false;
            }
        });
    }

    private void resetSeznam() {
        if(detail){
            setContentView(R.layout.activity_browse_jobs);
            setView();
            //if the detail view was opened from the dashboard reload seznam and forget about it
            if(detailViewID != null){
                detailViewID = null;
                seznamDel = new ArrayList<>();
                loadSeznamDel();
            }
            else{
                appendAdapter(contextForAdapter);
            }
        }
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

                handleFavouriteButton(favourite, id);
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
        try (FileOutputStream fos = openFileOutput(SAVED_JOBS_FILE, Context.MODE_PRIVATE)) {
            fos.write(toSave.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openJobDetailView(String id){
        openJobDetailView(id, 1);
    }
    private void openJobDetailView(String id, int backBehaviour) {
        detail = true;
        setContentView(R.layout.job_detail);

        TextView jobTitle = findViewById(R.id.jobTitle);
        TextView jobPay = findViewById(R.id.jobPay);
        TextView freeSpace = findViewById(R.id.freeSpace);
        TextView duration = findViewById(R.id.duration);
        TextView worktime = findViewById(R.id.worktime);
        TextView start = findViewById(R.id.start);
        TextView description = findViewById(R.id.description);
        ImageView back = findViewById(R.id.back);
        Button favourite = findViewById(R.id.favourite);
        Button apply = findViewById(R.id.apply);
        TextView prijavaResponse = findViewById(R.id.prijavaResponse);

        for (HashMap<String, String> ponudba : seznamDel){
            if(ponudba.get("jobId").equals(id)){
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
                if(backBehaviour == 1)
                    backToList();
                else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                }
            }
        });
        handleFavouriteButton(favourite, id);
        handleApplicationButton(apply, id, prijavaResponse);
        setBottomNav();
    }
    private void backToList() {
        setContentView(R.layout.activity_browse_jobs);
        setView();
        appendAdapter(contextForAdapter);
    }

    private void handleFavouriteButton(Button favourite, String id) {
        //COMMENTED CODE HANDLES GRAPHIC CHANGES OF STARS, but it's buggy
        Drawable fullStar = getDrawable(R.drawable.baseline_star_24);
        Drawable emptyStar = getDrawable(R.drawable.baseline_star_outline_24);

        if(favourites.contains(id)){
            favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, fullStar);
        }
        else{
            favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, emptyStar);
        }
        favourite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                toggleFavourite(id);
                if(favourites.contains(id)){
                    favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, fullStar);
                }
                else{
                    favourite.setCompoundDrawablesWithIntrinsicBounds(null, null, null, emptyStar);
                }
            }
        });
    }

    private void handleApplicationButton(Button apply, String id, TextView prijavaResponse) {
        if(applied.contains(id)){
            //gray out button
            apply.setBackgroundColor(apply.getContext().getResources().getColor(R.color.gray));
            //setText on response
            prijavaResponse.setText(R.string.alreadyApplied);
        }
        else{
            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = contextForAdapter.getFileStreamPath(USER_DATA_FILE);
                    if(!(file.exists() && file != null)){
                        Toast.makeText(contextForAdapter,R.string.errorNoProfileData ,Toast.LENGTH_LONG).show();
                    }
                    else if(applyToJob(id)) {
                        apply.setOnClickListener(null);
                        apply.setBackgroundColor(apply.getContext().getResources().getColor(R.color.gray));
                        prijavaResponse.setText(R.string.alreadyApplied);
                    }
                }
            });
        }
    }

    private boolean applyToJob(String id) {
        if(applied.contains(id)){
            return false;
        }
        else{
            applied.add(id);
        }
        String toSave = "";
        if (applied.size() >= 1){
            for (int i = 0; i < applied.size(); i++){
                toSave += (applied.get(i)+"\n");
            }
        }
        try (FileOutputStream fos = openFileOutput(APPLIED_JOBS_FILE, Context.MODE_PRIVATE)) {
            fos.write(toSave.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void setView() {
        detail = false;
        lv = findViewById(R.id.list);
        lv.setOnItemClickListener((parent, view, position, id)->{
            TextView idView = (TextView) view.findViewById(R.id.jobId);
            String jobId = idView.getText().toString();
            openJobDetailView(jobId);
        });
        loadingIndicator = findViewById(R.id.loadingIndicator);
        contextForAdapter = this;
        setBottomNav();
    }

    @Override
    public void onBackPressed() {
        //detailViewID is only non-null when detail screen is opened straight from the dashboard, so if it's non-null we want to go back to the dashboard
        if(detail && detailViewID == null) {
            backToList();
        }
        else {
            finish();
        }
        overridePendingTransition(0,0);
    }
/*
    //TODO - delete TEST CODE - adds new listing to Firebase
    public Task<Void> addNewJobToFB(){
        String opis = "Potrebujejo študenta/ko za pomoč pri likvidaciji faktur v Finančno dokumentarni kontroli.";
        String naziv = "DELO NA RAČUNALNIKU";
        String delovnik = "izmensko";
        String trajanje = "po dogovoru";
        String zacetekDela = "26. 6. 2023";
        opis = "\"" + opis + "\"";
        naziv = "\"" + naziv + "\"";
        delovnik = "\"" + delovnik + "\"";
        trajanje = "\"" + trajanje + "\"";
        zacetekDela = "\"" + zacetekDela + "\"";

        float placa = (float)5.92;
        int prostaMesta = 3;
        Delo del = new Delo(opis, naziv, delovnik, trajanje, zacetekDela, placa, prostaMesta);
        return dr.push().setValue(del);
    }*/
}