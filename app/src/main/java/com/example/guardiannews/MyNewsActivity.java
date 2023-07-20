package com.example.guardiannews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guardiannews.db.NewsRepository;
import com.example.guardiannews.model.News;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MyNewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG = "  MyNewsActivity ";
    private ArrayList<News> list = new ArrayList<>( );
    private MyListAdapter myAdapter;
    SharedPreferences prefs = null;
    NewsRepository repo;
    Button btnHome;

    TextView tvMyName;
    TextView tvMyEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_news);

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

        btnHome=findViewById(R.id.btnHome);

        btnHome.setOnClickListener(e->{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        repo =new NewsRepository(this);
        //list= repo.get_ListByHashMap(News.class);
        list= repo.getList(News.class);

        ListView myList = findViewById(R.id.theListView);
        myList.setAdapter( myAdapter = new MyListAdapter());


        myList.setOnItemLongClickListener( (p, b, pos, id) -> {

            //showNews( position );
            News selectedItem = list.get(pos);
            View view=getLayoutInflater().inflate(R.layout.row_layout, null);
            TextView tvTitle=view.findViewById(R.id.tvTitle);
            TextView tvDate=view.findViewById(R.id.tvDate);
            tvTitle.setText(selectedItem.getWebTitle());
            tvDate.setText(selectedItem.getWebPublicationDate());
            tvTitle.setPadding(70,0,0,0);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder

                    //What is the message:
                    .setMessage(R.string.str_alert_del)

                    //what the Yes button does:
                    .setPositiveButton(R.string.str_yes, (click, arg) -> {
                        repo.delete_Item(selectedItem);
                        list.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton(R.string.str_no, (click, arg) -> { })

                    //An optional third button:
                    //.setNeutralButton("Maybe", (click, arg) -> {  })

                    //You can add extra layout elements:
                    .setView(view)

                    //Show the dialog
                    .create().show();
            return true;
        });

        //Whenever you swipe down on the list, do something:
        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false)  );
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
            textView.setText("This page is my news list that I added.If you want to store the news, you add the news in the detail activity. " +
                    "If you want to remove my news, you can click longer.");
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