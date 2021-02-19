package com.example.rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity {

    private static final int MAX_LEN = 360;
    private SharedPreferences.Editor editor;

    private final int REQUEST_IMAGE_GALLERY = 1;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private File currentImageFile;

    private static final int STORAGE_PERMISSION_CODE = 101;

    private String location;
    private String apiKey;
    private Bitmap imageBitmap;

    private EditText bio;
    private TextView textSizeDisplay;
    private EditText createUsername;
    private EditText createPassword;
    private EditText createFirstName;
    private EditText createLastName;
    private EditText createDepartment;
    private EditText createPosition;
    private ImageView createImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Intent intent = getIntent();
        apiKey = intent.getStringExtra("apiKey");
        location = intent.getStringExtra("location");

        createUsername = findViewById(R.id.createUsername);
        createPassword = findViewById(R.id.createPassword);
        createFirstName = findViewById(R.id.createFirst);
        createLastName = findViewById(R.id.createLast);
        createDepartment = findViewById(R.id.createDepartment);
        createPosition = findViewById(R.id.createPosition);
        createImage = findViewById(R.id.createImage);

        textSizeDisplay = findViewById(R.id.createYourStory);
        bio = findViewById(R.id.createBio);
        setupEditText();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
        this.setTitle("  Create Profile");
    }

    public boolean checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(CreateProfileActivity.this,
                    new String[] { permission },
                    requestCode);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

         if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(CreateProfileActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(CreateProfileActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void setupEditText() {

        bio.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        bio.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        textSizeDisplay.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // Nothing to do here
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // Nothing to do here
                    }
                });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "image+";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }


    public void doCamera() {
        try {
            currentImageFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(
                this, "com.example.android.fileprovider2", currentImageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void processFullCameraImage() {

        Uri selectedImage = Uri.fromFile(currentImageFile);
        createImage.setImageURI(selectedImage);

    }

    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        createImage.setImageBitmap(selectedImage);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processFullCameraImage();
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void createImageOnClick(View view) {
        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("Profile Picture");
            builder.setMessage("Take picture from:");
            builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doCamera();
                }
            });
            builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doGallery();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_save_menu, menu);
        return true;
    }

    public void createProfile()
    {
        BitmapDrawable drawable = (BitmapDrawable) createImage.getDrawable();
        imageBitmap = drawable.getBitmap();
        CreateProfileAPIRunnable createProfile = new CreateProfileAPIRunnable(this, createUsername.getText().toString(), createPassword.getText().toString(), createFirstName.getText().toString(), createLastName.getText().toString(), "1000", createDepartment.getText().toString(), bio.getText().toString(), imageBitmap , createPosition.getText().toString(), location, apiKey);
        new Thread(createProfile).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Save Changes?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createProfile();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return super.onOptionsItemSelected(item);
    }
    public void login(Profile profile)
    {
        Profile p = profile;
        Intent loginIntent = new Intent(this, ProfileActivity.class);
        loginIntent.putExtra("apiKey", apiKey);
        loginIntent.putExtra("profile", p);
        startActivity(loginIntent);
    }

    public void saveError(String result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Error Creating Profile");
        builder.setMessage("Make sure you are filling out the form correctly and completely!\nError: " + result);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loginFromCreate(String userName, String password)//logs new user into profile immediately after creating it, called in process method in createProfileAPIRunnable
    {
        LoginAPIRunnable loginAPI = new LoginAPIRunnable(this, userName, password, apiKey);
        new Thread(loginAPI).start();

    }

    public void badLogin(String result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Error Creating Profile");
        builder.setMessage("Something went wrong! :(\nError: " + result);
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

}