package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    LinearLayout profileB, eventsB, jobsB;
    BottomNavigationView bottomNavigationView;
    ArrayList<String> favouriteJobs;
    DatabaseReference dr;
    LinearLayout seznamDel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seznamDel = findViewById(R.id.seznamDel);

        readFavouriteJobs(); //prebere ID-je del, ki so shranjeni v lokalni datoteki
        fillListOfJobTitles();//naloži info o teh delih iz firebase

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

        //find buttons
        profileB = findViewById(R.id.buttonProfile);
        eventsB = findViewById(R.id.buttonEvents);
        jobsB = findViewById(R.id.buttonJobs);

        //start given activity on button press
        profileB.setOnClickListener(v->{
            Intent intent = new Intent(this, EditProfile.class);
            startActivity(intent);
        });

        eventsB.setOnClickListener(v->{
            Intent intent = new Intent(this, BrowseEventsActivity.class);
            startActivity(intent);
        });

        jobsB.setOnClickListener(v->{
            Intent intent = new Intent(this, BrowseJobsActivity.class);
            startActivity(intent);
        });
    }

    private void readFavouriteJobs() {
        favouriteJobs = new ArrayList<String>();
        File file = this.getFileStreamPath("savedJobs.txt");
        boolean remove = false;
        if(file.exists() && file != null) {
            try (FileInputStream fis = openFileInput("savedJobs.txt");
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
                        try {
                            JSONObject delo = new JSONObject(String.valueOf(task.getResult().getValue()));
                            addLineToSavedJobs(id, delo.get("naziv").toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    private void addLineToSavedJobs(String s, String naziv) {
        getLayoutInflater().inflate(R.layout.saved_job_line, seznamDel);
        LinearLayout line = (LinearLayout) seznamDel.getChildAt(seznamDel.getChildCount()-1);
        TextView nazivTV = (TextView) line.getChildAt(0);
        nazivTV.setText(naziv);
    }
}