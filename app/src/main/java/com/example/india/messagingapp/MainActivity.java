package com.example.india.messagingapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager myviewpager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

    FirebaseUser currentUser;
    private DatabaseReference UserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid();
            UserReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(online_user_id);
        }

        myviewpager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myviewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
           LogoutUser();
        }

        else if(currentUser != null)
        {
            UserReference.child("online").setValue("true");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_button)
        {
            if(currentUser != null)
            {
                UserReference.child("online").setValue(ServerValue.TIMESTAMP);
            }

            mAuth.signOut();

            LogoutUser();
        }

        if(item.getItemId() == R.id.main_account_settings_button)
        {
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if(item.getItemId() == R.id.main_all_users_button)
        {
            Intent allUserIntent = new Intent(MainActivity.this,AllUsersActivtiy.class);
            startActivity(allUserIntent);
        }

        return true;
    }

    private void LogoutUser()
    {
        Intent startPageIntent = new Intent(MainActivity.this,StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(currentUser != null)
        {
            UserReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
