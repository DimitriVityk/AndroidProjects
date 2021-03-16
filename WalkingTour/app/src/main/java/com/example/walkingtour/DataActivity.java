package com.example.walkingtour;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DataActivity extends AppCompatActivity {

    private Typeface myCustomFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Acme-Regular.ttf");

        ConstraintLayout layout = findViewById(R.id.layout);
        TextView titleText = findViewById(R.id.dataTitle);
        TextView addressText = findViewById(R.id.dataAddress);
        TextView descriptionText = findViewById(R.id.dataDescription);
        descriptionText.setMovementMethod(new ScrollingMovementMethod());

        titleText.setTypeface(myCustomFont);
        addressText.setTypeface(myCustomFont);
        descriptionText.setTypeface(myCustomFont);

        FenceData fd = (FenceData) getIntent().getSerializableExtra("DATA");

        if (fd != null) {
            titleText.setText(fd.getId());
            addressText.setText(fd.getAddress());
            descriptionText.setText(fd.getDescription());

            ImageView imageView = findViewById(R.id.dataImage);
            String imageUrl = fd.getImageURL();
            if(imageUrl != null && !imageUrl.equals("")) {
                Picasso.get().load(imageUrl).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView);
            }
        }
    }

    private void customizeActionBar() {

        // This function sets the font of the title in the app bar

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setIcon(R.drawable.home_image);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
}