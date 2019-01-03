package com.example.india.messagingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    Button LoginButton;
    EditText LoginEmail,LoginPassword;
    FirebaseAuth firebaseAuth;
    String email, password;
    ProgressDialog loadingBar;
    Intent in2;

    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = findViewById(R.id.login_button);
        LoginEmail = findViewById(R.id.login_email);
        LoginPassword = findViewById(R.id.login_password);
        loadingBar = new ProgressDialog(this);
        in2 = new Intent(LoginActivity.this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = LoginEmail.getText().toString();
                password = LoginPassword.getText().toString();

                LoginUserAccount(email, password);
            }
        });
    }

    private void LoginUserAccount(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Please write your email", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Opening Existing Account");
            loadingBar.setMessage("Please wait, while we are connecting your account..");
            loadingBar.show();


            //firebase
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String online_user_id = firebaseAuth.getCurrentUser().getUid();
                        String Device_token = FirebaseInstanceId.getInstance().getToken();

                        usersReference.child(online_user_id).child("device_token").setValue(Device_token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(in2);
                                        finish();
                                    }
                                });

                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }

                    loadingBar.dismiss();
                }
            });
        }

    }
}
