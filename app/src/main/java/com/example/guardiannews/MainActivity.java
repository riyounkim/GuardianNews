package com.example.guardiannews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guardiannews.model.News;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String JSON_URL="https://content.guardianapis.com/search?api-key=test";
    ListView lvNews;

    FrameLayout fragmentLocation;

    ProgressBar pBNews;
    private List<News> list = new ArrayList<>( );
    private MyListAdapter myAdapter;
    SharedPreferences prefs = null;
    TextView tvMyName;
    TextView tvMyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);



        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvMyName=headerView.findViewById(R.id.tvMyName);
        tvMyEmail=headerView.findViewById(R.id.tvMyEmail);

        String savedName=prefs.getString("username", "");
        String savedEmail=prefs.getString("useremail", "");

        tvMyName.setText(savedName);
        tvMyEmail.setText(savedEmail);
        lvNews=findViewById(R.id.lvNews);


        lvNews.setAdapter(myAdapter=new MyListAdapter());
        lvNews.setOnItemClickListener((lv, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            News selectedItem=list.get(position);
            Bundle dataToPass = new Bundle();
            dataToPass.putString("id", selectedItem.getNews_id());
            dataToPass.putString("title", selectedItem.getWebTitle());
            dataToPass.putString("api", selectedItem.getApiUrl());
            dataToPass.putString("publicationDate", selectedItem.getWebPublicationDate());


                Intent nextActivity = new Intent(MainActivity.this, DetailActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition

        });

        pBNews=findViewById(R.id.pBNews);

        NewsHttp req = new NewsHttp(pBNews);
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedTodayDate = formatter.format(currentDate);
        String todayUrl=String.format("%s&from-date=%s", JSON_URL, formattedTodayDate);
        req.execute(JSON_URL);  //Type 1
    }

    private class NewsHttp extends AsyncTask< String, Integer, String> {

        private ProgressBar progressBar;
        public NewsHttp(ProgressBar pbNews){
            this.progressBar=pbNews;

        }
        @Override
        protected void onPostExecute(String s) {
            Log.i("MainActivity", s);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
           // super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonUrl = strings[0];
            try {


                list = getNewsJson(jsonUrl);


                Log.i("Download", "News was download") ;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("MainActivity", e.getMessage());
            }
            return "Done";
        }

        private List<News> getNewsJson(String jsonUrl) throws IOException, JSONException {

            URL url = new URL(jsonUrl);
            //open the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //wait for data:
            InputStream response = urlConnection.getInputStream();
            //JSON reading:
            //Build the entire string response:
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");

                for (int i= 0; i < 100; i++) {
                    try {
                        publishProgress(i);
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Download", "Data was download") ;
            }

            String responseText = sb.toString(); //result is the whole string

            JSONObject respnose = new JSONObject(responseText).getJSONObject("response");

            JSONArray news = respnose.getJSONArray("results");
            // convert string to JSON:
            List<News> result=new ArrayList<>();
            for (int i = 0; i < news.length(); i++) {
                JSONObject characterObject = news.getJSONObject(i);

                String id = characterObject.getString("id");
                String webTitle = characterObject.getString("webTitle");

                String apiUrl = characterObject.getString("apiUrl");
                String webPublicationDate = characterObject.getString("webPublicationDate");
                // Create a new NewsCharacter object
                News character = new News(id, webTitle, apiUrl, webPublicationDate);

                // Add the character to the list
                result.add(character);
            }


            //get the double associated with "value"
            return result ;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) { return true; }
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Intent intent=new Intent(MainActivity.this, SearchResultActivity.class);
//                intent.putExtra("query",query);
//                startActivity(intent);
                // Do your task here

                NewsHttp req = new NewsHttp(pBNews);
                String searchUrl=String.format("%s&q=%s", JSON_URL, query);
                req.execute(searchUrl);

                return true;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:

        int id = item.getItemId();
        if (id == R.id.goHome) {
            Intent nextActivity = new Intent(this, MainActivity.class);
            startActivity(nextActivity); //make the transition
        } else if (id == R.id.setting) {
            Intent nextActivity = new Intent(this, SettingActivity.class);
            startActivity(nextActivity); //make the transition
        }  else if (id == R.id.help) {
            View view=getLayoutInflater().inflate(R.layout.help_layout, null);
            TextView textView=view.findViewById(R.id.tvPageDescription);
            textView.setText("How can I help you in this page?");
            textView.setPadding(70,0,0,0);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setNegativeButton(R.string.str_close, (click, arg) -> { }).setView(view).create().show();
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = null;

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_my_news) {
            Intent intent = new Intent(this, MyNewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            finishAffinity();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return list.size(); }

        public Object getItem(int position) { return list.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent)
        {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if(newView == null) {
                newView = inflater.inflate(R.layout.row_layout, parent, false);

            }
            //set what the text should be for this row:
            TextView tvNameItem = newView.findViewById(R.id.tvTitle);
            TextView tvDate = newView.findViewById(R.id.tvDate);
            News item=(News)getItem(position);
            tvNameItem.setText( item.getWebTitle().length()>70?item.getWebTitle().substring(0,70).concat(".."):item.getWebTitle() );
            tvDate.setText(item.getWebPublicationDate());


            //return it to be put in the table
            return newView;
        }
    }
}