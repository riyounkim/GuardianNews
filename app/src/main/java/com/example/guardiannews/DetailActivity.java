package com.example.guardiannews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guardiannews.db.NewsRepository;
import com.example.guardiannews.model.News;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button btnList, btnAdd;
    NewsRepository repo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);


        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        DetailFragment dFragment = new DetailFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, dFragment)
                .commit();
        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnList=findViewById(R.id.btnList);
        btnAdd=findViewById(R.id.btnAdd);
        btnList.setOnClickListener(click -> {

                // Perform any necessary actions before finishing the current activity

                finish(); // Finish the current activity and go back to the previous activity

        });
        btnAdd.setOnClickListener(e->{
            repo=new NewsRepository(this);
            String n_id = dataToPass.getString("id" );
            String title = dataToPass.getString("title" );
            String api = dataToPass.getString("api" );
            String publicationDate=dataToPass.getString("publicationDate" );
            News myNews=new News(n_id,title,api,publicationDate);
            int id=repo.insert_Item(myNews);


            Snackbar snackbar = Snackbar
                    .make(btnAdd, "This News is added to My News List!", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            News addedNews=repo.getItem(News.class, id);
                            //News addedNews=repo.get_ItemByHashMap(News.class, id);
                            repo.delete_Item(addedNews);
                        }
                    });

            snackbar.show();

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_no_search, menu);



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

}