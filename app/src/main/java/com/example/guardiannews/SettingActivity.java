package com.example.guardiannews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    EditText etMyname;
    EditText etMyEmail;
    Button btnNameSave;
    Button btnEmailSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
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

    private void saveSharedPrefs(String key, String val)
    {
        if(!key.isEmpty() && !val.isEmpty()){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, val);
            editor.commit();
        }

    }
}