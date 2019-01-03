package com.example.india.messagingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivtiy extends AppCompatActivity {

    private RecyclerView allUsersList;
    private DatabaseReference allDataBaseUserReference;
    private EditText SearchInputText;
    ImageView SearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_activtiy);

        SearchButton = (ImageView) findViewById(R.id.search_people_btn);
        SearchInputText = (EditText) findViewById(R.id.search_input_text);

        allUsersList = findViewById(R.id.all_user_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        allDataBaseUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        allDataBaseUserReference.keepSynced(true);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String searchUserName = SearchInputText.getText().toString();
                if(TextUtils.isEmpty(searchUserName))
                {
                    Toast.makeText(AllUsersActivtiy.this, "Please write user name to search....", Toast.LENGTH_SHORT).show();
                }
                SearchForPeople(searchUserName);

            }
        });
    }

    private void SearchForPeople(String searchUserName)
    {
        Toast.makeText(this, "Searching....", Toast.LENGTH_LONG).show();
        Query searchPeopleAndFriends = allDataBaseUserReference.orderByChild("user_name").startAt(searchUserName)
                .endAt(searchUserName + "\uf8ff");
        FirebaseRecyclerAdapter<AllUsers,AllUserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, AllUserViewHolder>
                (
                        AllUsers.class,
                        R.layout.all_users_display_layout,
                        AllUserViewHolder.class,
                        searchPeopleAndFriends
                ) {

            @Override
            protected void populateViewHolder(AllUserViewHolder viewHolder, AllUsers model, final int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_thumb_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(AllUsersActivtiy.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };

        allUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public AllUserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public  void setUser_name(String user_name) {
            TextView name =(TextView) mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }

        public void setUser_status(String user_status){
            TextView status = (TextView) mView.findViewById(R.id.all_user_status);
            status.setText(user_status);
        }

        public void setUser_thumb_image(final Context ctx,final String user_thumb_image){
            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);


            //in Picasso .placeholder(R.drawable.defaultprofilepicture) is not working

            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(ctx).load(user_thumb_image).into(thumb_image);
                            //in Picasso .placeholder(R.drawable.defaultprofilepicture) is not working
                        }
                    });

        }
    }
}
