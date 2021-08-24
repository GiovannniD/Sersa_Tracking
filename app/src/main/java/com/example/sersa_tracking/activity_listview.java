package com.example.sersa_tracking;

import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class activity_listview extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
       //setAmbientEnabled();
    }
}