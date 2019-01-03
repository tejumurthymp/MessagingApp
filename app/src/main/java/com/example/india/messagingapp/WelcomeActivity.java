package com.example.india.messagingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class WelcomeActivity extends AppCompatActivity {

    ProgressBar loadingbar;
    int pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loadingbar = findViewById(R.id.progressBar_id);
        //loadingbar.setProgress(0);
        //loadingbar.setMax(100);
        Thread thread = new Thread(){
            @Override
            public void run() {
                while (loadingbar.getProgress()<= 100) {
                    try {
                        sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pro = pro + 5;
                    loadingbar.setProgress(pro);
                    if (pro == 100){
                        break;
                    }
                }
                    Intent mainIntent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(mainIntent);
            }
        };
        thread.start();
    }



    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
