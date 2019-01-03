package com.example.india.messagingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class StartPageActivity extends AppCompatActivity {

    private Button NeednewAccountButton;
    private Button AlreadyHaveAccountButton;
    //AdView madview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        NeednewAccountButton = (Button) findViewById(R.id.need_account_button);
        AlreadyHaveAccountButton = (Button)findViewById(R.id.already_have_account_button);

        //MobileAds.initialize(this,"ca-app-pub-5829379307081392~1098555338");

        //madview = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //madview.loadAd(adRequest);


        NeednewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(StartPageActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
