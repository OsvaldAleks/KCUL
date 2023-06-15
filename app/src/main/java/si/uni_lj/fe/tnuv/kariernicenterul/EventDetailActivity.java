package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetailActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;

    TextView eventName, eventLocation, eventDate, eventHost, eventDescription;

    ImageView backButton;
    Button eventApply;

    int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        String currEventName = getIntent().getStringExtra("EVENT_NAME");
        String currEventLocation = getIntent().getStringExtra("EVENT_LOCATION");
        String currEventTime = getIntent().getStringExtra("EVENT_TIME");
        String currEventHost = getIntent().getStringExtra("EVENT_HOST");
        String currEventDescription = getIntent().getStringExtra("EVENT_DESCRIPTION");
        boolean currEventApplied = getIntent().getBooleanExtra("EVENT_APPLIED", false);
        int currEventID = getIntent().getIntExtra("EVENT_ID", 0);
        eventID = currEventID;

        eventName = findViewById(R.id.eventName);
        eventLocation = findViewById(R.id.eventLocation);
        eventDate = findViewById(R.id.eventDate);
        eventHost = findViewById(R.id.eventHost);
        eventDescription = findViewById(R.id.eventDescription);
        eventApply = findViewById(R.id.eventApply);

        eventName.setText(currEventName);
        eventLocation.setText(currEventLocation);
        eventDate.setText(currEventTime);
        eventHost.setText(currEventHost);
        eventDescription.setText(currEventDescription);

        if(currEventApplied){
            eventApply.setBackgroundColor(eventApply.getContext().getResources().getColor(R.color.gray));
        }

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventDetailActivity.this, BrowseEventsActivity.class));
                finish();
            }
        });

        setBottomNav();
    }

    public void handleEventbutton(View view) {

        eventApply.setBackgroundColor(eventApply.getContext().getResources().getColor(R.color.gray));

        dr = FirebaseDatabase.getInstance().getReference("Dogodki");
        dr.child("dogodek" + String.valueOf(eventID)).child("prijavljen").setValue(true);

        //V bazi spremenimo za ta dogodek flag prijavljen -> true


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationView.setSelectedItemId(R.id.events);

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



}