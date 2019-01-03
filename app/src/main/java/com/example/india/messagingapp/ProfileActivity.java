package com.example.india.messagingapp;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button sendfriendrequestButton,declinefriendrequestButton;
    private TextView ProfileName,ProfileStatus;
    private ImageView ProfileImage;
    private DatabaseReference UsersReference;

    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;

    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true);


        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        sendfriendrequestButton = findViewById(R.id.profile_visit_send_req_button);
        declinefriendrequestButton = findViewById(R.id.profile_decline_button);
        ProfileName = findViewById(R.id.profile_visit_user_name);
        ProfileStatus = findViewById(R.id.profile_visit_user_status);
        ProfileImage = findViewById(R.id.profile_visit_user_image);

        CURRENT_STATE = "not_friends";


        UsersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                // it is not working .placeholder(R.drawable.defaultprofilepicture)
                Picasso.with(ProfileActivity.this).load(image).into(ProfileImage);

                FriendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(receiver_user_id))
                                    {
                                        String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                        if(req_type.equals("sent"))
                                        {
                                            CURRENT_STATE = "request_sent";
                                            sendfriendrequestButton.setText("Cancel Friend Request");

                                            declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                            declinefriendrequestButton.setEnabled(false);
                                        }

                                        else if(req_type.equals("received"))
                                        {
                                            CURRENT_STATE = "request_received";
                                            sendfriendrequestButton.setText("Accept Friend Request");

                                            int notificationId = (int) System.currentTimeMillis();

                                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ProfileActivity.this)
                                                    .setSmallIcon(R.mipmap.appicon)
                                                    .setContentTitle("My notification")
                                                    .setContentText("Hello World");

                                            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            mNotifyMgr.notify(notificationId , mBuilder.build());

                                            declinefriendrequestButton.setVisibility(View.VISIBLE);
                                            declinefriendrequestButton.setEnabled(true);

                                            declinefriendrequestButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    DeclineFriendRequest();
                                                }
                                            });
                                        }
                                    }
                                else
                                {
                                    FriendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiver_user_id))
                                            {
                                                CURRENT_STATE = "friends";
                                                sendfriendrequestButton.setText("Unfriend");

                                                declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                                declinefriendrequestButton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        declinefriendrequestButton.setVisibility(View.INVISIBLE);
        declinefriendrequestButton.setEnabled(false);


        if(!sender_user_id.equals(receiver_user_id))
        {
            sendfriendrequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendfriendrequestButton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends"))
                    {
                        sendfriendrequestToAFriend();
                    }

                    if(CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }

                    if(CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }

                    if(CURRENT_STATE.equals("friends"))
                    {
                        UnFriendAFriend();
                    }
                }
            });
        }

        else
        {
            declinefriendrequestButton.setVisibility(View.INVISIBLE);
            sendfriendrequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void DeclineFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                sendfriendrequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendfriendrequestButton.setText("Send Friend Request");

                                                declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                                declinefriendrequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void UnFriendAFriend() {

        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                         if(task.isSuccessful())
                         {
                             FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful())
                                             {
                                                 sendfriendrequestButton.setEnabled(true);
                                                 CURRENT_STATE = "not_friends";
                                                 sendfriendrequestButton.setText("Send Friend Request");

                                                 declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                                 declinefriendrequestButton.setEnabled(false);
                                             }
                                         }
                                     });
                         }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());


        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                sendfriendrequestButton.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendfriendrequestButton.setText("Unfriend");

                                                                                declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                                                                declinefriendrequestButton.setEnabled(false);
                                                                            }

                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                sendfriendrequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendfriendrequestButton.setText("Send Friend Request");
                                                declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                                declinefriendrequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendfriendrequestToAFriend() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                HashMap<String , String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from",sender_user_id);
                                                notificationData.put("type","request");

                                                NotificationsReference.child(receiver_user_id).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    sendfriendrequestButton.setEnabled(true);
                                                                    CURRENT_STATE = "request_sent";
                                                                    sendfriendrequestButton.setText("Cancel Friend Request");

                                                                    declinefriendrequestButton.setVisibility(View.INVISIBLE);
                                                                    declinefriendrequestButton.setEnabled(false);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    }
                });
    }
}
