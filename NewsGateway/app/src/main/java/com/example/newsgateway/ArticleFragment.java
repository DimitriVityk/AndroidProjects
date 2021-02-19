package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ArticleFragment extends Fragment {

    public ArticleFragment() {
        // Required empty public constructor
    }

    public static ArticleFragment newInstance(Article ar, int index, int max) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", ar);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Article currentArticle = (Article) args.getSerializable("ARTICLE_DATA");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            if(currentArticle.getTitle() != null && !currentArticle.getTitle().equals("")) {
                TextView title = fragment_layout.findViewById(R.id.fragTitle);
                if(currentArticle.getTitle().length() > 50)
                {
                    title.setTextSize(24);
                    if(currentArticle.getTitle().length() > 100)
                    {
                        title.setTextSize(22);
                    }
                }
                title.setText(currentArticle.getTitle());
                title.setOnClickListener(v -> clickArticle(currentArticle.getUrl()));
            }

            TextView date = fragment_layout.findViewById(R.id.fragDate);
            if (currentArticle.getPublishedAt() != null && !currentArticle.getPublishedAt().equals("")) {
                TimeZone utc = TimeZone.getTimeZone("UTC");
                SimpleDateFormat sourceFormat = currentArticle.getPublishedAt().contains(".") ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                sourceFormat.setTimeZone(utc);
                Date convertedDate = null;
                try {
                    convertedDate = sourceFormat.parse(currentArticle.getPublishedAt());
                    date.setText(destFormat.format(convertedDate));
                } catch (ParseException e) {

                    e.printStackTrace();
                }
            } else { date.setVisibility(View.GONE);}

            TextView author = fragment_layout.findViewById(R.id.fragAuthor);
            if ((currentArticle.getAuthor() != null && !currentArticle.getAuthor().equals("")) && !currentArticle.getAuthor().equals("null")) {

                author.setText(currentArticle.getAuthor());
            } else { author.setVisibility(View.GONE); }

            ImageView imageView = fragment_layout.findViewById(R.id.fragImage);
            String imageUrl = currentArticle.getUrlToImage();
            if(imageUrl != null && !imageUrl.equals("")) {
                Picasso.get().load(imageUrl).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView);
                imageView.setOnClickListener(v -> clickArticle(currentArticle.getUrl()));
            }

            if ((currentArticle.getDescription() != null && !currentArticle.getDescription().equals("")) && !currentArticle.getDescription().equals("null")) {
                TextView description = fragment_layout.findViewById(R.id.fragDescription);
                description.setText(currentArticle.getDescription());
                description.setMovementMethod(new ScrollingMovementMethod());
                description.setOnClickListener(v -> clickArticle(currentArticle.getUrl()));
            }

            TextView pageNum = fragment_layout.findViewById(R.id.fragPageNum);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            return fragment_layout;
        } else {
            return null;
        }
    }

    public void clickArticle(String url)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}