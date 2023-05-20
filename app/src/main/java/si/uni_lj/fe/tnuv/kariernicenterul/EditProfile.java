package si.uni_lj.fe.tnuv.kariernicenterul;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditProfile extends AppCompatActivity {
    EditText imeView, ulicaView, hisnaStView, postnaStView, krajView, emailView, telefonView;
    LinearLayout izobrazbaView, izkusnjeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

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
        File file = this.getFileStreamPath("userData.json");
        if(file.exists() && file != null) {

            try (FileInputStream fis = openFileInput("userData.json");
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
        } catch (JSONException e) {
            Toast.makeText(EditProfile.this, ""+e, Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }
    }

    //adds new line of inputs to Izobrazba/Izkusnje section
    private int addLineTo(int v){
        LinearLayout seznam = findViewById(v);
        getLayoutInflater().inflate(R.layout.list_item_izkusnje, seznam);
        return 1;
    }

    //saves user data to JSON file
    private int shrani(){
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

        //check validity of input - TODO contextual verification

        //check if any are empty
        if(ime.length() == 0 || ulica.length() == 0 || kraj.length() == 0 || email.length() == 0 || telefon.length() == 0 || hisnaSt.length() == 0 || postnaSt.length() == 0){
            Toast.makeText(this, R.string.missingInputError, Toast.LENGTH_LONG).show();
            return 0;
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
        try (FileOutputStream fos = openFileOutput("userData.json", Context.MODE_PRIVATE)) { //TODO - filename bi blo smiselno definirat nekje drugje... not totally sure where
            fos.write(data.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(EditProfile.this, R.string.fileSaved,Toast.LENGTH_LONG).show();
        return 1;
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
}
