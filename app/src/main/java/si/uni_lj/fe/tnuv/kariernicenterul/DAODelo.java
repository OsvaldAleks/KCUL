package si.uni_lj.fe.tnuv.kariernicenterul;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DAODelo {
    private DatabaseReference dr;
    private String[] IDs;
    public DAODelo(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dr = db.getReference(Delo.class.getSimpleName());
        /*
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Delo delo = dataSnapshot.getValue(Delo.class);
                // ..
                Log.w("TAG", ""+delo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        dr.addValueEventListener(postListener);
        */
    }

    public Task<Void> add(Delo delo){
        return dr.push().setValue(delo);
    }

    public String get(){
        final String[] seznamDel = {null};
        dr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                seznamDel[0] = String.valueOf(task.getResult().getValue());
            }
            }
        });
        return seznamDel[0];
    }
}
