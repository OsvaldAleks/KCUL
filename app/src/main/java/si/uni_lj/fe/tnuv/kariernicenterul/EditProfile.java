package si.uni_lj.fe.tnuv.kariernicenterul;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditProfile extends AppCompatActivity {
    public static final String USER_DATA_FILE = "userData.json";
    private static final int PERMISSION_REQUEST_CODE = 200;
    EditText imeView, ulicaView, hisnaStView, postnaStView, krajView, emailView, telefonView;
    LinearLayout izobrazbaView, izkusnjeView;
    BottomNavigationView bottomNavigationView;
    Context contextForPopup;
    boolean unsavedChanges;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreen();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setScreen();
    };
    private void setScreen() {
        setContentView(R.layout.edit_profile);
        contextForPopup = this;
        unsavedChanges = false;

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.cv);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        leaveTo(MainActivity.class);
                        return true;
                    case R.id.events:
                        leaveTo(BrowseEventsActivity.class);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.cv:
                        return true;
                    case R.id.jobs:
                        leaveTo(BrowseJobsActivity.class);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //get input fields
        imeView = findViewById(R.id.ime);
        ulicaView = findViewById(R.id.ulica);
        hisnaStView = findViewById(R.id.hisnaSt);
        postnaStView = findViewById(R.id.postnaSt);
        krajView = findViewById(R.id.kraj);
        emailView = findViewById(R.id.email);
        telefonView = findViewById(R.id.telefon);
        izobrazbaView = findViewById(R.id.seznamIzobrazbe);
        izkusnjeView = findViewById(R.id.seznamIzkusenj);

        //add 1 empty line per category by default
        addLineTo(R.id.seznamIzobrazbe);
        addLineTo(R.id.seznamIzkusenj);

        //try to read the user data file:
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
                fillForm(new JSONObject(stringBuilder.toString()));

            } catch (IOException e) {
            } catch (JSONException e) {
            }

        }

        //setting onClick listeners
        findViewById(R.id.shrani).setOnClickListener(v -> shrani());
        findViewById(R.id.newIzobrazba).setOnClickListener(v -> addLineTo(R.id.seznamIzobrazbe));
        findViewById(R.id.newDelovnoMesto).setOnClickListener(v -> addLineTo(R.id.seznamIzkusenj));
        //TODO - export file
        //findViewById(R.id.izvozi).setOnClickListener(v -> saveAndExport());
        imeView.addTextChangedListener(textWatcher);
        ulicaView.addTextChangedListener(textWatcher);
        hisnaStView.addTextChangedListener(textWatcher);
        postnaStView.addTextChangedListener(textWatcher);
        krajView.addTextChangedListener(textWatcher);
        emailView.addTextChangedListener(textWatcher);
        telefonView.addTextChangedListener(textWatcher);
    }
    private void leaveTo(Class activityClass) {
        if(unsavedChanges) {
            new AlertDialog.Builder(contextForPopup)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setTitle(R.string.pozor)
                    .setMessage(R.string.unsavedChanges)
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), activityClass));
                        }

                    })
                    .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bottomNavigationView.setSelectedItemId(R.id.cv);
                        }

                    })
                    .show();
        }
        else{
            startActivity(new Intent(getApplicationContext(), activityClass));
        }
        overridePendingTransition(0, 0);
    }

    //called if userData file exists - fills the form with saved data
    private void fillForm(JSONObject userData) {
        try {
            //sat text to all fields
            imeView.setText(userData.getString("ime"));
            ulicaView.setText(userData.getString("ulica"));
            hisnaStView.setText(userData.getString("hisnaSt"));
            krajView.setText(userData.getString("kraj"));
            postnaStView.setText(userData.getString("postnaSt"));
            emailView.setText(userData.getString("email"));
            telefonView.setText(userData.getString("telefon"));

            //iterate through all past education -> add new line of inputs for each and fill them with data
            if(userData.has("izobrazba")) {
                JSONArray izobrazbaArray = userData.getJSONArray("izobrazba");
                for (int i = 0; i < izobrazbaArray.length(); i++) {
                    JSONObject entry = izobrazbaArray.getJSONObject(i);
                    LinearLayout line = (LinearLayout) izobrazbaView.getChildAt(i);
                    ((EditText) line.getChildAt(0)).setText(entry.getString("od"));
                    ((EditText) line.getChildAt(1)).setText(entry.getString("do"));
                    ((EditText) line.getChildAt(2)).setText(entry.getString("opis"));
                    if (i < izobrazbaArray.length() - 1)
                        addLineTo(R.id.seznamIzobrazbe);
                }
            }
            //iterate through all past education -> add new line of inputs for each and fill them with data
            if(userData.has("izkusnje")) {
                JSONArray izkusnjeArray = userData.getJSONArray("izkusnje");
                for (int i = 0; i < izkusnjeArray.length(); i++) {
                    JSONObject entry = izkusnjeArray.getJSONObject(i);
                    LinearLayout line = (LinearLayout) izkusnjeView.getChildAt(i);
                    ((EditText) line.getChildAt(0)).setText(entry.getString("od"));
                    ((EditText) line.getChildAt(1)).setText(entry.getString("do"));
                    ((EditText) line.getChildAt(2)).setText(entry.getString("opis"));
                    if (i < izkusnjeArray.length() - 1)
                        addLineTo(R.id.seznamIzkusenj);
                }
            }
            unsavedChanges = false;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    //adds new line of inputs to Izobrazba/Izkusnje section
    private int addLineTo(int v){
        LinearLayout seznam = findViewById(v);
        boolean changes = unsavedChanges;
        if(v == R.id.seznamIzkusenj)
            getLayoutInflater().inflate(R.layout.list_item_izkusnje, seznam);
        else
            getLayoutInflater().inflate(R.layout.list_item_izobrazba, seznam);

        LinearLayout vrsta = (LinearLayout)seznam.getChildAt(seznam.getChildCount()-1);
        for(int i = 0; i < vrsta.getChildCount(); i++){
            ((EditText)vrsta.getChildAt(i)).addTextChangedListener(textWatcher);
        }
        unsavedChanges = changes;
        return 1;
    }
    //saves user data to JSON file
    private boolean shrani(){
        //read values from fields
        String ime = imeView.getText().toString(),
                ulica = ulicaView.getText().toString(),
                kraj = krajView.getText().toString(),
                email = emailView.getText().toString(),
                telefon = telefonView.getText().toString(),
                hisnaSt = hisnaStView.getText().toString(),
                postnaSt = postnaStView.getText().toString();
        String[][] izobrazba = readListItems(izobrazbaView);
        String[][] izkusnje = readListItems(izkusnjeView);

        //check validity of input
        boolean valid = true;
        String errorMsg = "";

        if(!ime.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$")){
            errorMsg += "ime";
            valid = false;
        }
        if(!ulica.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$")){
            if(!valid)
                errorMsg += ", ";
            errorMsg += "ulica";
            valid = false;
        }
        if(!kraj.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$")){
            if(!valid)
                errorMsg += ", ";
            errorMsg += "kraj";
            valid = false;
        }
        if(!email.matches("^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*$")){
            if(!valid)
                errorMsg += ", ";
            errorMsg += "email";
            valid = false;
        }
        if(!(telefon.matches("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$") || telefon.matches("|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$") || telefon.matches("|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$"))){
            if(!valid)
                errorMsg += ", ";
            errorMsg += "telefon";
            valid = false;
        }

        //check if any are empty
        if(ime.length() == 0 || ulica.length() == 0 || kraj.length() == 0 || email.length() == 0 || telefon.length() == 0 || hisnaSt.length() == 0 || postnaSt.length() == 0){
            Toast.makeText(this, R.string.missingInputError, Toast.LENGTH_LONG).show();
            return false;
        }

        if(!valid){
            Toast.makeText(this, getResources().getString(R.string.incorrectEntry) + ": " + errorMsg, Toast.LENGTH_LONG).show();
            return false;
        }

        //build JSONObject
        JSONObject data = new JSONObject();
        try {
            data.putOpt("ime", ime);
            data.putOpt("ulica", ulica);
            data.putOpt("kraj", kraj);
            data.putOpt("email", email);
            data.putOpt("telefon", telefon);
            data.putOpt("hisnaSt", hisnaSt);
            data.putOpt("postnaSt", postnaSt);

            //izobrazba and izkusnje are arrays of objects
            JSONObject izobrazbaObject = new JSONObject();
            JSONArray izobrazbaArray = new JSONArray();
            for (String[] entry: izobrazba) {
                izobrazbaObject.putOpt("od", entry[0]);
                izobrazbaObject.putOpt("do", entry[1]);
                izobrazbaObject.putOpt("opis", entry[2]);
                izobrazbaArray.put(new JSONObject(izobrazbaObject.toString()));
            }
            data.putOpt("izobrazba", izobrazbaArray);

            JSONObject izkusnjeObject = new JSONObject();
            JSONArray izkusnjeArray = new JSONArray();
            for (String[] entry: izkusnje) {
                izkusnjeObject.putOpt("od", entry[0]);
                izkusnjeObject.putOpt("do", entry[1]);
                izkusnjeObject.putOpt("opis", entry[2]);
                izkusnjeArray.put(new JSONObject(izkusnjeObject.toString()));
            }
            data.putOpt("izkusnje", izkusnjeArray);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //save JSONObject to file
        try (FileOutputStream fos = openFileOutput(USER_DATA_FILE, Context.MODE_PRIVATE)) {
            fos.write(data.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        unsavedChanges = false;
        Toast.makeText(EditProfile.this, R.string.fileSaved,Toast.LENGTH_LONG).show();
        return true;
    }

    //iterates through LinearLayouts containing EditText-s (for izkusnje/izobrazba) and saves the values as String[][]
    private String[][] readListItems(LinearLayout seznam){
        int numOfAllItems = seznam.getChildCount(),
            numOfValidEntries = 0;
        String[][] tmp = new String[numOfAllItems][3];
        for(int i = 0; i < numOfAllItems; i++){
            LinearLayout item = (LinearLayout) seznam.getChildAt(i);
            //read line
            String start = ((EditText) item.getChildAt(0)).getText().toString(),
                    end = ((EditText) item.getChildAt(1)).getText().toString(),
                    desc = ((EditText) item.getChildAt(2)).getText().toString();
            //check validity
            if(start.length() != 0 && end.length() != 0 && desc.length() != 0){
                tmp[numOfValidEntries][0] = start;
                tmp[numOfValidEntries][1] = end;
                tmp[numOfValidEntries][2] = desc;
                numOfValidEntries++;
            }
        }
        //remake the output array with the correct size
        String[][] out = new String[numOfValidEntries][3];
        for(int i = 0; i < numOfValidEntries; i++){
            out[i] = tmp[i];
        }
        return out;
    }

    @Override
    public void onBackPressed() {
        if(unsavedChanges) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.pozor)
                    .setMessage(R.string.unsavedChanges)
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            overridePendingTransition(0,0);
                        }
                    })
                    .setNegativeButton("Ne", null)
                    .show();
        }
        else {
            finish();
            overridePendingTransition(0,0);
        }
    }
    private TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            unsavedChanges = true;
        }
    };
    /*
    private void saveAndExport() {
        if(shrani()){
            export();
        }
    }
    private void export(){
        if (checkPermission()) {

            Log.d("test", Environment.getExternalStorageDirectory() + " - - - " + "GFG.pdf");
            Log.d("test", "AAAAAAA");
            pdfWriter writer = new pdfWriter(this);
            writer.generatePDF();
        } else {
            requestPermission();
        }
    }
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        if(checkPermission())
            export();
    }
     */
}