package si.uni_lj.fe.tnuv.kariernicenterul;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {

    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        findViewById(R.id.shrani).setOnClickListener(v -> shrani());
    }

    //SAVE USER DATA to FILE
    private int shrani(){
        //get input fields
        EditText imeView = findViewById(R.id.ime),
                ulicaView = findViewById(R.id.ulica),
                hisnaStView = findViewById(R.id.hisnaSt),
                postnaStView = findViewById(R.id.postnaSt),
                krajView = findViewById(R.id.kraj),
                emailView = findViewById(R.id.email),
                telefonView = findViewById(R.id.telefon);

        //get values from fields
        String ime = imeView.getText().toString(),
                ulica = ulicaView.getText().toString(),
                kraj = krajView.getText().toString(),
                email = emailView.getText().toString(),
                telefon = telefonView.getText().toString(),
                hisnaSt = hisnaStView.getText().toString(),
                postnaSt = postnaStView.getText().toString();

        //check if any are empty
        if(ime.length() == 0 || ulica.length() == 0 || kraj.length() == 0 || email.length() == 0 || telefon.length() == 0 || hisnaSt.length() == 0 || postnaSt.length() == 0){
            Toast.makeText(this, R.string.missingInputError, Toast.LENGTH_LONG).show();
            return 0;
        }

        //check validity of input - TODO


        //build file - https://www.newtonsoft.com/json/help/html/ReadingWritingJSON.htm
        JSONObject data = new JSONObject();
        try {
            data.putOpt("ime", ime);
            data.putOpt("ulica", ulica);
            data.putOpt("kraj", kraj);
            data.putOpt("email", email);
            data.putOpt("telefon", telefon);
            data.putOpt("hisnaSt", hisnaSt);
            data.putOpt("postnaSt", postnaSt);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //save to file
        try (FileOutputStream fos = openFileOutput("userData.json", Context.MODE_PRIVATE)) { //filename bi blo smiselno definirat nekje drugje... not totally sure where - TODO
            fos.write(data.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* //READ FILE (here for testing)
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fis = openFileInput("userData.json");
             InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
        */

        return 1;
    }
}
