package si.uni_lj.fe.tnuv.kariernicenterul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import java.util.ArrayList;


public class BrowseEventsActivity extends AppCompatActivity implements RecyclerClickListener {

    BottomNavigationView bottomNavigationView;
    DatabaseReference dr;

    RecyclerView recyclerDogodki;

    ArrayList<Dogodek> seznam;

    DogodekAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        recyclerDogodki = findViewById(R.id.eventList);
        dr = FirebaseDatabase.getInstance().getReference("Dogodki");
        seznam = new ArrayList<>();
        recyclerDogodki.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DogodekAdapter(this, seznam, this);
        recyclerDogodki.setAdapter(adapter);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Dogodek dogodek = dataSnapshot.getValue(Dogodek.class);
                    seznam.add(dogodek);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setBottomNav();
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
    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationView.setSelectedItemId(R.id.events);
    };
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }

    @Override
    public void onItemClick(int position) {

        //Toast.makeText(BrowseEventsActivity.this,"You clicked on: " + seznam.get(position).getIme(), Toast.LENGTH_SHORT ).show();
        Intent intent = new Intent(BrowseEventsActivity.this, EventDetailActivity.class);
        intent.putExtra("EVENT_NAME", seznam.get(position).getIme());
        intent.putExtra("EVENT_TIME", seznam.get(position).getDatum());
        intent.putExtra("EVENT_LOCATION", seznam.get(position).getLokacija());
        intent.putExtra("EVENT_HOST", seznam.get(position).getPredavatelj());
        //intent.putExtra("EVENT_DESCRIPTION", seznam.get(position).getOpis());

        startActivity(intent);

    }
}