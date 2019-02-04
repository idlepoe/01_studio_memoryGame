package com.memorygame2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences prefs;
    String dataName = "MyData";
    String intName = "MyString";
    int defaultInt =0;
    public static int hiScore;

    TextView tetHiScore;
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        prefs = getSharedPreferences(dataName,MODE_PRIVATE);

        hiScore = prefs.getInt(intName,defaultInt);

        tetHiScore = findViewById(R.id.txtHiScore);

        tetHiScore.setText("Hi:"+hiScore);

        btnPlay = findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnPlay:
                Intent i;
                i = new Intent(this,GameActivity.class);
                startActivity(i);
                break;
        }

    }
}
