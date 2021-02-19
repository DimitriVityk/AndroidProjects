package com.example.knowyourgov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private TextView officialTitle2;
    private TextView officialName2;
    private TextView officialUserLocation2;
    private ImageView officialImage2;
    private ImageView logo;
    private View officialView2;
    private String partyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        officialView2 = findViewById(R.id.officialView2);
        logo = findViewById(R.id.logo);

        officialUserLocation2 = findViewById(R.id.officialUserLocation2);
        String userLoc = getIntent().getStringExtra("currentLocation");
        officialUserLocation2.setText(userLoc);

        officialName2 = findViewById(R.id.officialName2);
        String offName = getIntent().getStringExtra("name");
        officialName2.setText(offName);

        officialTitle2 = findViewById(R.id.officialTitle2);
        String offTitle = getIntent().getStringExtra("title");
        officialTitle2.setText(offTitle);

        officialImage2 = findViewById(R.id.officialImage2);
        String URL = getIntent().getStringExtra("photo");
        if(!(URL == null)) {
            loadImage(URL);
        }

        partyText = getIntent().getStringExtra("party");


        if (partyText.equals("(Republican Party)") || partyText.equals("(Republican)"))
        {
            officialView2.setBackgroundColor(Color.parseColor("#FF0000"));
            Picasso.get().load(R.drawable.rep_logo).into(logo);

        }
        else if(partyText.equals("(Democratic Party)") || partyText.equals("(Democratic)"))
        {
            officialView2.setBackgroundColor(Color.parseColor("#0000FF"));
            Picasso.get().load(R.drawable.dem_logo).into(logo);
        }
        else
        {
            //use black
            logo.setVisibility(View.GONE);
        }
    }

    public void logoClicked(View V)
    {
        if (partyText.equals("(Republican Party)") || partyText.equals("(Republican)")
        ) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.gop.com/"));
            startActivity(i);
        }
        else if (partyText.equals("(Democratic Party)") || partyText.equals("(Democratic)"))
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://democrats.org/"));
            startActivity(i);
        }
    }
    public void loadImage(String imageURL)
    {
        Picasso.get().load(imageURL).error(R.drawable.brokenimage).placeholder(R.drawable.placeholder).into(officialImage2);
    }

    public void pictureClicked(View v)
    {

    }
}