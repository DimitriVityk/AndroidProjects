package com.example.knowyourgov;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "Official";
    private TextView officialUserLocation;
    private TextView officialTitle;
    private TextView officialName;
    private TextView officialParty;
    private View officialView;
    private ImageView officialImage;
    private TextView addressText;
    private TextView phoneText;
    private TextView emailText;
    private TextView websiteText;
    private ImageButton facebook;
    private ImageButton twitter;
    private ImageButton youtube;
    private ImageView party;
    private TextView officialAddress;
    private TextView officialPhone;
    private TextView officialEmail;
    private TextView officialWebsite;
    private String facebookURL;
    private String twitterURL;
    private String youtubeURL;
    private String locationText;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        officialAddress = findViewById(R.id.officialAddress);
        officialPhone = findViewById(R.id.officialPhone);
        officialEmail = findViewById(R.id.officialEmail);
        officialWebsite = findViewById(R.id.officialWebsite);


        officialUserLocation = findViewById(R.id.officialUserLocation);
        locationText = getIntent().getStringExtra("currentLocation");
        officialUserLocation.setText(locationText);

        officialTitle = findViewById(R.id.officialTitle);
        String titleText = getIntent().getStringExtra("officialTitle");
        officialTitle.setText(titleText);

        officialName = findViewById(R.id.officialName);
        String nameText = getIntent().getStringExtra("officialName");
        officialName.setText(nameText);



        officialImage = findViewById(R.id.imageView);
        imageURL = getIntent().getStringExtra("officialImage");
        if(!(imageURL == null)) {
            Log.d(TAG, "onCreate: " + imageURL);
            loadImage(imageURL);
        }

        addressText = findViewById(R.id.addressText);
        String addtext = getIntent().getStringExtra("officialAddress");
        addressText.setText(addtext);
        if(addressText.getText().equals(", , , "))
        {
            addressText.setVisibility(View.GONE);
            officialAddress.setVisibility(View.GONE);
        }
        Linkify.addLinks(addressText, Linkify.ALL);

        emailText = findViewById(R.id.emailText);
        String eText = getIntent().getStringExtra("officialEmail");
        emailText.setText(eText);
        if(emailText.getText().equals(""))
        {
            officialEmail.setVisibility(View.GONE);
            emailText.setVisibility(View.GONE);
        }
        Linkify.addLinks(emailText, Linkify.ALL);

        websiteText = findViewById(R.id.websiteText);
        String webText = getIntent().getStringExtra("officialWebsite");
        websiteText.setText(webText);
        if(websiteText.getText().equals(""))
        {
            websiteText.setVisibility(View.GONE);
            officialWebsite.setVisibility(View.GONE);
        }
        Linkify.addLinks(websiteText, Linkify.ALL);

        phoneText = findViewById(R.id.phoneText);
        String phoText = getIntent().getStringExtra("officialPhone");
        phoneText.setText(phoText);
        if(phoneText.getText().equals(""))
        {
            phoneText.setVisibility(View.GONE);
            officialPhone.setVisibility(View.GONE);
        }
        Linkify.addLinks(phoneText, Linkify.ALL);



        facebook = findViewById(R.id.facebook);
        facebookURL = getIntent().getStringExtra("officialFacebook");
        if (facebookURL.equals(""))
        {
            facebook.setVisibility(View.GONE);
        }


        twitter = findViewById(R.id.twitter);
        twitterURL = getIntent().getStringExtra("officialTwitter");
        if(twitterURL.equals(""))
        {
            twitter.setVisibility(View.GONE);
        }



        youtube = findViewById(R.id.youtube);
        youtubeURL = getIntent().getStringExtra("officialYoutube");
        if(youtubeURL.equals(""))
        {
            youtube.setVisibility(View.GONE);
        }

        party = findViewById(R.id.party);
        party.setBackgroundColor(Color.parseColor("#00000000"));

        officialView = findViewById(R.id.officialView);
        officialParty = findViewById(R.id.officialParty);
        String partyText = getIntent().getStringExtra("officialParty");
        officialParty.setText("(" + partyText + ")");
        if (partyText.equals("Republican Party") || partyText.equals("Republican"))
        {
            officialView.setBackgroundColor(Color.parseColor("#FF0000"));
            facebook.setBackgroundColor(Color.parseColor("#FF0000"));
            twitter.setBackgroundColor(Color.parseColor("#FF0000"));
            youtube.setBackgroundColor(Color.parseColor("#FF0000"));
            Picasso.get().load(R.drawable.rep_logo).into(party);

        }
        else if(partyText.equals("Democratic Party") || partyText.equals("Democratic"))
        {
            officialView.setBackgroundColor(Color.parseColor("#0000FF"));
            facebook.setBackgroundColor(Color.parseColor("#0000FF"));
            twitter.setBackgroundColor(Color.parseColor("#0000FF"));
            youtube.setBackgroundColor(Color.parseColor("#0000FF"));
            Picasso.get().load(R.drawable.dem_logo).into(party);
        }
        else
        {
            //use black
            party.setVisibility(View.GONE);
        }
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = twitterURL;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }


    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookURL;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + facebookURL;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void youTubeClicked(View v) {
        String name = youtubeURL;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void logoClicked(View V)
    {
        if (officialParty.getText().equals("(Republican Party)") || officialParty.getText().equals("(Republican)")
        ) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.gop.com/"));
            startActivity(i);
        }
        else if (officialParty.getText().equals("(Democratic Party)") || officialParty.getText().equals("(Democratic)"))
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://democrats.org/"));
            startActivity(i);
        }
    }

    public void pictureClicked(View v)
    {
        if(!(imageURL == null)) {
            Intent photoIntent = new Intent(this, PhotoActivity.class);
            photoIntent.putExtra("currentLocation", locationText);
            photoIntent.putExtra("name", officialName.getText().toString());
            photoIntent.putExtra("title", officialTitle.getText().toString());
            photoIntent.putExtra("photo", imageURL);
            photoIntent.putExtra("party", officialParty.getText().toString());
            startActivity(photoIntent);
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("officialUserLocation", officialUserLocation.getText().toString());


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        officialUserLocation.setText(savedInstanceState.getString("officialUserLocation"));
    }

    public void loadImage(String imageURL)
    {
        Picasso.get().load(imageURL).error(R.drawable.brokenimage).placeholder(R.drawable.placeholder).into(officialImage);
    }



}