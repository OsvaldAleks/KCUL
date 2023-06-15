package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerClickListener {
    public static final String MESSAGE_KEY = "si.uni_lj.fe.tnuv.KCUL.MESSAGE";
    public static final String USER_DATA_FILE = "userData.json";
    public static final String SAVED_JOBS_FILE = "savedJobs.txt";
    LinearLayout profileB;
    TextView jobsB, eventsB;
    BottomNavigationView bottomNavigationView;
    ArrayList<String> favouriteJobs;
    DatabaseReference dr;
    TextView imeUporabnika, emailUporabnika, izobrazbaUporabnika;
    LinearLayout seznamDel;
    RecyclerView recyclerApliedDogodki;
    AppliedDogodekAdapter appliedDogodekAdapter;
    ArrayList<Dogodek> seznam;

    private boolean isFirstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isFirstLaunch = true;

        //builds "Moj profil" section
        profileB = findViewById(R.id.buttonProfile);
        profileB.setOnClickListener(v->{
            Intent intent = new Intent(this, EditProfile.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        });

        imeUporabnika = findViewById(R.id.imeUporabnika);
        emailUporabnika = findViewById(R.id.emailUporabnika);
        izobrazbaUporabnika = findViewById(R.id.izobrazbaUporabnika);
        readUserData(); //prebere uporabnikove podatke in jih prikaže

        //builds "Shranjena dela" section
        jobsB = findViewById(R.id.buttonJobs);
        jobsB.setOnClickListener(v->{
            Intent intent = new Intent(this, BrowseJobsActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        });

        seznamDel = findViewById(R.id.seznamDel);
        readFavouriteJobs(); //prebere ID-je del, ki so shranjeni v lokalni datoteki
        fillListOfJobTitles();//naloži info o teh delih iz firebase in jih vstavi v seznam

        //TODO - build "dogodki" section



        eventsB = findViewById(R.id.buttonEvents);
        eventsB.setOnClickListener(v->{
            Intent intent = new Intent(this, BrowseEventsActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        });

        //nastavi navigacijo
        setBottomNav();
    }

    private void populateRecycler() {

        // Get the reference to your Firebase database

        recyclerApliedDogodki = findViewById(R.id.appliedEventList);
        recyclerApliedDogodki.setLayoutManager(new LinearLayoutManager(this));

        seznam = new ArrayList<>();
        appliedDogodekAdapter = new AppliedDogodekAdapter(this, seznam, this); // Initialize the adapter
        recyclerApliedDogodki.setAdapter(appliedDogodekAdapter); // Set the adapter to the RecyclerView

        Query query = dr.orderByChild("prijavljen").equalTo(true);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dogodek dogodek = snapshot.getValue(Dogodek.class);
                    if(dogodek != null){
                        seznam.add(dogodek);
                    }
                }
                appliedDogodekAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationView.setSelectedItemId(R.id.home);

        //have to reset saved jobs else changes whon't be visible when going back with back button
        removeJobsFromList();
        readFavouriteJobs(); //prebere ID-je del, ki so shranjeni v lokalni datoteki
        fillListOfJobTitles();//naloži info o teh delih iz firebase in jih vstavi v seznam
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFirstLaunch){
            //initial check if there is any dogodek applied
            dr = FirebaseDatabase.getInstance().getReference("Dogodki");
            Query queryInitial = dr.orderByChild("prijavljen").equalTo(true);

            queryInitial.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Dogodek dogodek = snapshot.getValue(Dogodek.class);
                        if(dogodek != null){
                            populateRecycler();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that occur during data retrieval
                }
            });

            isFirstLaunch = false;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }

    private void removeJobsFromList() {
        for(int i = seznamDel.getChildCount()-1; i >= 0; i--){
            seznamDel.removeView(seznamDel.getChildAt(i));
        }
    }

    ;
    private void setBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
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
                        startActivity(new Intent(getApplicationContext(), BrowseJobsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    private void readUserData() {
        StringBuilder stringBuilder = new StringBuilder();
        File file = this.getFileStreamPath(USER_DATA_FILE);
        if(file.exists() && file != null) {
            try (FileInputStream fis = openFileInput(USER_DATA_FILE);
                 InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                //if there's no error fill the inputs with values
                //fillForm(new JSONObject(stringBuilder.toString()));
                JSONObject data = new JSONObject(stringBuilder.toString());
                emailUporabnika.setVisibility(View.VISIBLE);
                imeUporabnika.setVisibility(View.VISIBLE);
                imeUporabnika.setText(data.getString("ime"));
                emailUporabnika.setText(data.getString("email"));

                String izobrazba = "Izobrazba: ";
                boolean commaFlag = false;
                if(data.has("izobrazba")) {
                    JSONArray izobrazbaArray = data.getJSONArray("izobrazba");
                    for (int i = 0; i < izobrazbaArray.length(); i++) {
                        JSONObject entry = izobrazbaArray.getJSONObject(i);
                        if(commaFlag)
                            izobrazba += (", " + entry.getString("opis"));
                        else{
                            izobrazba += entry.getString("opis");
                            commaFlag = true;
                        }
                    }
                    if(izobrazbaArray.length() > 0) {
                        izobrazbaUporabnika.setText(izobrazba);
                        izobrazbaUporabnika.setVisibility(View.VISIBLE);
                    }
                    else{
                        izobrazbaUporabnika.setVisibility(View.GONE);
                    }
                }

            } catch (IOException e) {
            } catch (JSONException e) {
            }
        }
        else{
            izobrazbaUporabnika.setText(getResources().getText(R.string.ustvariProfil));
            izobrazbaUporabnika.setVisibility(View.VISIBLE);
            emailUporabnika.setVisibility(View.GONE);
            imeUporabnika.setVisibility(View.GONE);
        }
    }
    private void readFavouriteJobs() {
        favouriteJobs = new ArrayList<String>();
        File file = this.getFileStreamPath(SAVED_JOBS_FILE);
        if(file.exists() && file != null) {
            try (FileInputStream fis = openFileInput(SAVED_JOBS_FILE);
                 InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    favouriteJobs.add(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
            }
        }
    }
    private void fillListOfJobTitles() {
        for(int i = 0; i < favouriteJobs.size(); i++){
            final String id = favouriteJobs.get(i);
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            dr = db.getReference(Delo.class.getSimpleName());
            dr.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                    }
                    else {
                        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicatorJobs);
                        loadingIndicator.setVisibility(View.GONE);
                        try {
                            if(task.getResult().getValue() != null) {
                                JSONObject delo = new JSONObject(String.valueOf(task.getResult().getValue()));
                                addLineToSavedJobs(id, delo.get("naziv").toString());
                            }
                            else{
                                favouriteJobs.remove(favouriteJobs.indexOf(id));
                                saveFavourtesFile();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
        if (favouriteJobs.size() == 0){
            setNoJobsText();
            ProgressBar loadingIndicator = findViewById(R.id.loadingIndicatorJobs);
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private void saveFavourtesFile() {
        String toSave = "";
        if (favouriteJobs.size() >= 1){
            for (int i = 0; i < favouriteJobs.size(); i++){
                toSave += (favouriteJobs.get(i)+"\n");
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

    private void addLineToSavedJobs(String id, String naziv) {
        getLayoutInflater().inflate(R.layout.saved_job_line, seznamDel);
        LinearLayout line = (LinearLayout) seznamDel.getChildAt(seznamDel.getChildCount()-1);
        TextView nazivTV = (TextView) line.getChildAt(0);
        ImageView favourite = (ImageView) line.getChildAt(1);
        nazivTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BrowseJobsActivity.class);
                intent.putExtra(MESSAGE_KEY, id);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteJobs.remove(id);
                if(favouriteJobs.size() == 0){
                    setNoJobsText();
                }
                saveFavourtesFile();
                line.setVisibility(View.GONE);
            }
        });
        nazivTV.setText(naziv);
    }
    private void setNoJobsText() {
        getLayoutInflater().inflate(R.layout.saved_job_line, seznamDel);
        LinearLayout line = (LinearLayout) seznamDel.getChildAt(seznamDel.getChildCount()-1);
        TextView nazivTV = (TextView) line.getChildAt(0);
        ImageView star = (ImageView) line.getChildAt(1);
        nazivTV.setText(getResources().getString(R.string.nicShranjenihDel));
        star.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {

    }
}