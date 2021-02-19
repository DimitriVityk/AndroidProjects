package com.example.newsgateway;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] items;
    private Menu opt_menu;
    private SubMenu topicsMenu;
    private SubMenu countriesMenu;
    private SubMenu languagesMenu;
    private List<Fragment> fragments;
    private ArrayList<String> sourcesDisplayed = new ArrayList<>();
    private ArrayList<NewsSource> allSources = new ArrayList<>();
    private Set<String> categorySet = new HashSet<String>();
    private Set<String> languageSet = new HashSet<String>();
    private Set<String> countrySet = new HashSet<String>();
    private String currentSource;
    private ViewPager pager;
    private MyPageAdapter pageAdapter;
    public static int screenWidth, screenHeight;
    private String currentSourceID;
    private HashMap<String, String> languageMap = new HashMap<>();
    private HashMap<String, String> countriesMap = new HashMap<>();
    private HashMap<String, String> languageMapReverse = new HashMap<>();
    private HashMap<String, String> countriesMapReverse = new HashMap<>();
    private HashMap<String, NewsSource> nameToSource = new HashMap<>();
    private String catSelection = "all";
    private String counSelection = "all";
    private String langSelection = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        createCountriesMap();
        createLanguageMap();

        mDrawerLayout = findViewById(R.id.mDrawerLayout);
        mDrawerList = findViewById(R.id.mDrawerList);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(   // <== Important!
                this,                /* host Activity */
                mDrawerLayout,             /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        // Load the data
        if (allSources.isEmpty())
            new Thread(new SourceLoader(this)).start();
    }

    public void setupSources(List<NewsSource> sources)
    {
        allSources.clear();
        categorySet.clear();
        languageSet.clear();
        countrySet.clear();
        topicsMenu.clear();
        topicsMenu.add(1, Menu.NONE, Menu.NONE, "all");
        languagesMenu.clear();
        languagesMenu.add(3, Menu.NONE, Menu.NONE, "all");
        countriesMenu.clear();
        countriesMenu.add(2, Menu.NONE, Menu.NONE, "all");

        for(NewsSource source : sources)
        {
            allSources.add(source);
            String category = source.getCategory();
            String language = source.getLanguage();
            String country = source.getCountry();
            categorySet.add(category);
            languageSet.add(language);
            countrySet.add(country);
        }

        for(NewsSource source : allSources)
        {
            nameToSource.put(source.getName(), source);
        }
        List<String> catList = new ArrayList<>();
        catList.addAll(categorySet);
        Collections.sort(catList);
        for(String cat : catList)
        {
            topicsMenu.add(1, Menu.NONE, Menu.NONE, cat);
        }

        List<String> langList = new ArrayList<>();
        List<String> fullLangList = new ArrayList<>();
        langList.addAll(languageSet);
        for(String lang : langList)
        {
            String capitalLang = lang.toUpperCase();
            String translatedName = languageMap.get(capitalLang);
            fullLangList.add(translatedName);
        }
        Collections.sort(fullLangList);
        for(String lang: fullLangList)
            languagesMenu.add(3, Menu.NONE, Menu.NONE, lang);

        List<String> countryList = new ArrayList<>();
        List<String> fullCountryList = new ArrayList<>();
        countryList.addAll(countrySet);
        for(String cntry : countryList)
        {
            String capitalCountry = cntry.toUpperCase();
            String translatedName = countriesMap.get(capitalCountry);
            fullCountryList.add(translatedName);
        }
        Collections.sort(fullCountryList);
        for(String cntry : fullCountryList)
            countriesMenu.add(2, Menu.NONE, Menu.NONE, cntry);

        setDisplayList(catSelection, langSelection, counSelection);

    }


    public void setDisplayList(String topic, String language, String country)
    {
        List<NewsSource> allSourcesTemp = new ArrayList<>();
        allSourcesTemp.addAll(allSources);
        sourcesDisplayed.clear();
        if(topic.equals("all") && language.equals("all") && country.equals("all")) {
            for(NewsSource src : allSources) {
                sourcesDisplayed.add(src.getName());
            }
        }
        else {
            if (!topic.equals("all")) {
                int i = 0;
                while(i < allSourcesTemp.size()) {
                    NewsSource src = allSourcesTemp.get(i);
                    String cat = src.getCategory();
                    if (!cat.equals(topic)) {
                        allSourcesTemp.remove(src);
                    } else { i++; }
                }
            }
            if (!language.equals("all")) {
                String translatedLanguage = languageMapReverse.get(language);
                translatedLanguage = translatedLanguage.toLowerCase();
                int i = 0;
                while ( i < allSourcesTemp.size()) {
                    if (!allSourcesTemp.get(i).getLanguage().equals(translatedLanguage)) {
                        allSourcesTemp.remove(allSourcesTemp.get(i));
                    }else { i++; }
                }
            }
            if (!country.equals("all")) {
                String translatedCountry = countriesMapReverse.get(country);
                translatedCountry = translatedCountry.toLowerCase();
                int i = 0;
                while (i < allSourcesTemp.size())
                {
                    if(!allSourcesTemp.get(i).getCountry().equals(translatedCountry)) {
                        allSourcesTemp.remove(allSourcesTemp.get(i));
                    } else { i++; }
                }
            }
            for(NewsSource src : allSourcesTemp)
            {
                sourcesDisplayed.add(src.getName());
            }
        }

        Collections.sort(sourcesDisplayed);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, sourcesDisplayed));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setTitle("News Gateway (" + sourcesDisplayed.size() + ")");

        if(sourcesDisplayed.size() == 0)
            noResultsDialog(topic, language, country);

    }

    public void noResultsDialog(String topic, String language, String country)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Results");
        builder.setMessage("No Sources found with your selected criteria. \n Topic: " + topic + "\n " +
                "Language: " + language + "\n Country: " + country);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createLanguageMap()
    {
        try {
            InputStream fis = getResources().openRawResource(R.raw.language_codes);

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            JSONObject APIo = new JSONObject(json);
            JSONArray jsonArray = APIo.getJSONArray("languages");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jObj = (JSONObject) jsonArray.get(i);
                String code = jObj.getString("code");
                String name = jObj.getString("name");
                languageMap.put(code, name);
                languageMapReverse.put(name, code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCountriesMap()
    {
        try {
            InputStream fis = getResources().openRawResource(R.raw.country_codes);

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            JSONObject APIo = new JSONObject(json);
            JSONArray jsonArray = APIo.getJSONArray("countries");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jObj = (JSONObject) jsonArray.get(i);
                String code = jObj.getString("code");
                String name = jObj.getString("name");
                countriesMap.put(code, name);
                countriesMapReverse.put(name, code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.opt_menu = menu;

        topicsMenu = opt_menu.addSubMenu("Topics");
        countriesMenu = opt_menu.addSubMenu("Countries");
        languagesMenu = opt_menu.addSubMenu("Languages");

        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Important!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        int groupID = item.getGroupId();
        if(groupID == 1)
        {
            String title = String.valueOf(item.getTitle());
            catSelection = title;
            setDisplayList(catSelection, langSelection, counSelection);
        } else if(groupID == 2){
            String title = String.valueOf(item.getTitle());
            counSelection = title;
            setDisplayList(catSelection, langSelection, counSelection);
        } else if(groupID == 3) {
            String title = String.valueOf(item.getTitle());
            langSelection = title;
            setDisplayList(catSelection, langSelection, counSelection);
        }


        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        pager.setBackground(null);
        currentSource = sourcesDisplayed.get(position);

        NewsSource mapSource = nameToSource.get(currentSource);
        currentSourceID = mapSource.getId();

        new Thread(new ArticleLoader(this, currentSourceID)).start();
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void setArticles( List<Article> articleList)
    {
        setTitle(currentSource);

        for(int i = 0; i < pageAdapter.getCount(); i++)
        {
            pageAdapter.notifyChangeInPosition(i);
        }
        fragments.clear();

        for(int i = 0; i < articleList.size(); i++)
        {
            fragments.add(ArticleFragment.newInstance(articleList.get(i), i+1, articleList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

    MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }

}