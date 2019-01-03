package com.example.india.messagingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    Button CreateAccountButton;
    EditText RegisterUserName, RegisterUserEmail, RegisterUserPassword;
    FirebaseAuth firebaseAuth;
    DatabaseReference storeUserDefaultDataReference;
    String email, password, name, current_user_id;
    ProgressDialog loadingBar;
    Intent in1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterUserName = findViewById(R.id.register_name);
        CreateAccountButton = findViewById(R.id.register_button);
        RegisterUserEmail = findViewById(R.id.register_email);
        RegisterUserPassword = findViewById(R.id.register_password);
        loadingBar = new ProgressDialog(this);
        in1 = new Intent(RegisterActivity.this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = RegisterUserName.getText().toString();
                email = RegisterUserEmail.getText().toString();
                password = RegisterUserPassword.getText().toString();

                RegisterAccount(name, email, password);
            }
        });
    }

    private void RegisterAccount(final String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please write your name", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please write your email", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating account for you..");
            loadingBar.show();


            //firebase
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String Device_token = FirebaseInstanceId.getInstance().getToken();
                        current_user_id = firebaseAuth.getCurrentUser().getUid();
                        storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

                        storeUserDefaultDataReference.child("user_name").setValue(name);
                        storeUserDefaultDataReference.child("user_status").setValue("Hi i am using TAKChat app");
                        storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                        storeUserDefaultDataReference.child("device_token").setValue(Device_token);
                        storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            startActivity(in1);
                                            finish();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }

                    loadingBar.dismiss();
                }
            });
        }

    }
}


