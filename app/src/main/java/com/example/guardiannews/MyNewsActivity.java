package com.example.guardiannews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guardiannews.db.NewsRepository;
import com.example.guardiannews.model.News;

import java.util.ArrayList;

public class MyNewsActivity extends AppCompatActivity {

    private static final String TAG = "  MyNewsActivity ";
    private ArrayList<News> list = new ArrayList<>( );
    private MyListAdapter myAdapter;

    NewsRepository repo;
    Button btnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_news);
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