package com.example.rewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String apikey = "";
    private String userLoc;
    private EditText username;
    private EditText password;
    private CheckBox rememberCheckBox;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static String locationString = "Unspecified Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
        this.setTitle("  Rewards");
        testLoad();
        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        rememberCheckBox = findViewById(R.id.rememberCheckBox);
        loadLoginInfo();
        if(!username.getText().toString().equals("") && !password.getText().toString().equals("") )
        {
            rememberCheckBox.setChecked(true);
        }

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        determineLocation();
    }

    public void loginOnClick(View view)
    {
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        if (rememberCheckBox.isChecked())
        {
            saveLoginInfo(usernameText, passwordText);
        }
        else
        {
            deleteLoginInfo();
        }
        LoginAPIRunnable loginAPI = new LoginAPIRunnable(this, usernameText, passwordText, apikey);
        new Thread(loginAPI).start();

    }

    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);
                            userLoc = locationString;
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    private String getPlace(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            return city + ", " + state;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    userLoc = "Unspecified Location";
                }
            }
        }
    }

    public void testLoad()
    {
        if(loadAPIKey().equals("Load Failed")) {
            showAPIKeyDialog();
        } else {
            apikey = loadAPIKey();
            Toast.makeText(this, "the APIKEY is: " + apikey, Toast.LENGTH_LONG);
        }
    }

    public void createOnClick(View v)
    {
        testLoad();
        if(apikey.equals(""))
        {
            showAPIKeyDialog();
        }
        else {
            Intent createIntent = new Intent(this, CreateProfileActivity.class);
            createIntent.putExtra("apiKey", apikey);
            createIntent.putExtra("location", userLoc);
            startActivity(createIntent);
        }
    }

    public void showAPIKeyDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.api_key_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("API Key Needed");
        builder.setMessage("You need to request an API key");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText firstName = view.findViewById(R.id.dialogFirstName);
                EditText lastName = view.findViewById(R.id.dialogLastName);
                EditText Email = view.findViewById(R.id.dialogEmail);
                EditText ID = view.findViewById(R.id.dialogStudentIDNumber);

                getAPIKey(firstName.getText().toString(), lastName.getText().toString(), Email.getText().toString(), ID.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAPIKeyDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void getAPIKey(String firstName, String lastName, String Email, String ID)
    {
        GetStudentAPIKeyRunnable apiRun = new GetStudentAPIKeyRunnable(this, firstName, lastName, Email, ID);
        new Thread(apiRun).start();
    }

    public boolean saveAPIKey (String APIKey)
    {
        boolean success;
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("APIKEY.json", Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("APIKey").value(APIKey);
            writer.endObject();
            writer.close();
            success = true;
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            return false;
        }
    }

    public boolean saveLoginInfo(String usernameToSave, String passwordToSave)
    {
        boolean success;
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("LoginInfo.json", Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("Username").value(usernameToSave);
            writer.name("Password").value(passwordToSave);
            writer.endObject();
            writer.close();
            success = true;
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            return false;
        }
    }

    public void loadLoginInfo()
    {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("LoginInfo.json");

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            JSONObject APIo = new JSONObject(json);
            String checkUsername = APIo.getString("Username");
            String checkPassword = APIo.getString("Password");

            username.setText(checkUsername);
            password.setText(checkPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLoginInfo()
    {
        File dir = getFilesDir();
        File file = new File(dir, "LoginInfo.json");
        file.delete();
    }

    public void badAPIKeyForm(String result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Error retrieving API Key");
        builder.setMessage("Make sure you are filling out the form correctly!\nError: " + result);
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAPIKeyDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void badLoginForm(String result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Error During Login Attempt");
        builder.setMessage("Something is not right. Please Try Again.\nError: " + result);
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String loadAPIKey()
    {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("APIKEY.json");

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            JSONObject APIo = new JSONObject(json);
            String api = APIo.getString("APIKey");
            return api;
        } catch (Exception e) {
            e.printStackTrace();
            return "Load Failed";
        }
    }

    public void deleteAPIKeyOnClick(View view)
    {
        File dir = getFilesDir();
        File file = new File(dir, "APIKEY.json");
        if (file.delete()) {
            Toast.makeText(this, "API key deleted", Toast.LENGTH_SHORT).show();
            showAPIKeyDialog();
        } else {
            Toast.makeText(this, "API key failed to be deleted", Toast.LENGTH_SHORT).show();
        }

        apikey = "";
    }

    public void login(Profile profile)
    {
        Profile p = profile;
        Intent loginIntent = new Intent(this, ProfileActivity.class);
        loginIntent.putExtra("apiKey", apikey);
        loginIntent.putExtra("profile", p);
        startActivity(loginIntent);
    }

    public void saveAPIKeyDialog(String APIkey, String fName, String lName, String email, String ID)
    {
        if (saveAPIKey(APIkey))
        {
            apikey = APIkey;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("API Key Received and Stored");
            builder.setMessage("Name: " + fName + " " + lName + "\n" + "Student ID:" + ID + "\n" + "Email: " + email + "\n" + "API Key: " + APIkey);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("ERROR SAVING API KEY");
            builder.setMessage("There was an error saving you api key");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showAPIKeyDialog();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}