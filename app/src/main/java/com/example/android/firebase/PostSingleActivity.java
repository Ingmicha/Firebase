package com.example.android.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.os.Build.VERSION_CODES.M;

public class PostSingleActivity extends AppCompatActivity {

    private String mPost_key = null;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ImageView mPostSingleImage;

    private TextView mPostSingleTitle;
    private TextView mPostSingleDesc;
    private TextView mPostSingleUser;

    private Button mPostSingleRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();

        mPost_key = getIntent().getExtras().getString("post_id");

        mPostSingleImage = (ImageView) findViewById(R.id.post_single_image);

        mPostSingleTitle = (TextView) findViewById(R.id.post_single_title);
        mPostSingleDesc = (TextView) findViewById(R.id.post_single_desc);
        mPostSingleUser = (TextView) findViewById(R.id.post_single_user);

        mPostSingleRemove = (Button) findViewById(R.id.post_single_remove);

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_Image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();
                String post_user = (String) dataSnapshot.child("username").getValue();

                setTitle(post_title);

                mPostSingleTitle.setText(post_title);
                mPostSingleDesc.setText(post_desc);
                mPostSingleUser.setText(post_user);
                Picasso.with(PostSingleActivity.this).load(post_Image).into(mPostSingleImage);

                if(mAuth.getCurrentUser().getUid().equals(post_uid)){

                    mPostSingleRemove.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPostSingleRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(mPost_key).removeValue();

                Intent mainIntent = new Intent(PostSingleActivity.this,MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }
}
