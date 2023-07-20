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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences prefs = null;
    EditText etMyname;
    EditText etMyEmail;
    Button btnNameSave;
    Button btnEmailSave;

    TextView tvMyName;
    TextView tvMyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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

        etMyname=findViewById(R.id.etMyname);
        etMyEmail=findViewById(R.id.etMyEmail);
        btnNameSave =findViewById(R.id.btnNameSave);
        btnEmailSave =findViewById(R.id.btnEmailSave);


        etMyname.setText(prefs.getString("username", ""));
        etMyEmail.setText(prefs.getString("useremail", ""));


        btnNameSave.setOnClickListener(e->{
            String newName=String.valueOf(etMyname.getText());
            saveSharedPrefs("username",newName);
            Toast.makeText(getApplicationContext(), "Your name is edited", Toast.LENGTH_LONG).show();

        });

        btnEmailSave.setOnClickListener(e->{
            String newEmail=String.valueOf(etMyEmail.getText());
            saveSharedPrefs("useremail",newEmail);
            Toast.makeText(getApplicationContext(), "Your email is edited", Toast.LENGTH_LONG).show();
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
            textView.setText("This page is to setting the name and email for users.");
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


    private void saveSharedPrefs(String key, String val)
    {
        if(!key.isEmpty() && !val.isEmpty()){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, val);
            editor.commit();
        }

    }
}