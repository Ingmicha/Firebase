package com.example.android.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mAddImage;

    private EditText mPostTitle;
    private EditText mPostDesc;

    private Button mSubmit;

    private Uri mSelectedImage = null;

    private static final int GALLERY_REQUEST = 1;
    private static final String ACTIVITY_NAME = "PostActivity";

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Blog");

        mAddImage = (ImageButton) findViewById(R.id.imageButtonAddImage);

        mPostTitle = (EditText) findViewById(R.id.editTextPostTitle);
        mPostDesc = (EditText) findViewById(R.id.editTextPostDesc);

        mSubmit = (Button) findViewById(R.id.buttonSubmit);

        mProgressDialog = new ProgressDialog(this);

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }
        });
    }

    private void startPosting() {

        final String tittle = mPostTitle.getText().toString();
        final String desc = mPostDesc.getText().toString();

        if (!TextUtils.isEmpty(tittle) && !TextUtils.isEmpty(desc) && mSelectedImage != null) {

            mProgressDialog.setMessage("Posting to Blog...");
            mProgressDialog.show();

            StorageReference filepath = mStorageReference.child("Blog_Images").child(mSelectedImage.getLastPathSegment());

            filepath.putFile(mSelectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabaseReference.push();
                    newPost.child("title").setValue(tittle);
                    newPost.child("desc").setValue(desc);
                    newPost.child("image").setValue(downloadUri.toString());

                    mProgressDialog.dismiss();

                    startActivity(new Intent(PostActivity.this, MainActivity.class));

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mSelectedImage = data.getData();
            mAddImage.setImageURI(mSelectedImage);

        }
    }
}