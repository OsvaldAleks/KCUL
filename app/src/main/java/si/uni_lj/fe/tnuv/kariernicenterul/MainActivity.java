package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import si.uni_lj.fe.tnuv.kariernicenterul.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    Button profileB, eventsB, jobsB;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}