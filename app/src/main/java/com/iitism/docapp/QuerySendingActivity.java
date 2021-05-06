package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class QuerySendingActivity extends AppCompatActivity implements View.OnClickListener {
 TextInputEditText edtTextQuery;
 TextView txtAddImage;
 ImageView queryImageView;
 ImageButton selectImage;
 Button btnSend;
 String patientUid,docUid,docName,patName,patDob,patGender;
 Uri filepath;
 ProgressBar progressBar;
 LinearLayout progresslayout;
 RelativeLayout relativeLayout;
 int id;
 String i;

    int Gallery_request=100;

 FirebaseAuth firebaseAuth;
 FirebaseStorage firebaseStorage;
 FirebaseDatabase firebaseDatabase;
 DatabaseReference docRef,patientRef;
 StorageReference storageReference;
 Task<DataSnapshot> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_sending);
        edtTextQuery=findViewById(R.id.edt_txt_query);
        queryImageView=findViewById(R.id.query_image_view);
        selectImage=findViewById(R.id.btn_select_query_image);
        btnSend=findViewById(R.id.btn_send_query);
        progressBar=findViewById(R.id.progress_bar);
        progresslayout=findViewById(R.id.progress_layout);
        relativeLayout =findViewById(R.id.relative_layout);
        txtAddImage=findViewById(R.id.txt_add_image);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        docRef=firebaseDatabase.getReference().child("doctor");
        patientRef=firebaseDatabase.getReference().child("patient");
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        patientUid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        patientRef.child(patientUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.println(Log.ASSERT,"data","query sending activity");
                patName=snapshot.child("Name").getValue(String.class);
                patGender=snapshot.child("Gender").getValue(String.class);
                patDob=snapshot.child("DOB").getValue(String.class);
                i=snapshot.child("IDCount").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        Bundle b=getIntent().getExtras();
        if(b!=null)
        {

            docUid = (String) b.get("uid");
            docName=(String) b.get("name");
        }

        selectImage.setOnClickListener(this);
        btnSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view==selectImage)
        {
            txtAddImage.setVisibility(View.INVISIBLE);
            //selectimage();
            OpenGallery();


            if(filepath==null)
            {
                txtAddImage.setVisibility(View.VISIBLE);
            }

        }
        else if(view==btnSend)
        {
            send();
        }
    }

    public void selectimage()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(QuerySendingActivity.this);
    }

    public void OpenGallery()
    {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,Gallery_request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.println(Log.ASSERT,"image","activity result");
//
//
        if(requestCode==Gallery_request)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(QuerySendingActivity.this);
                }
            }
            else
            {
                Log.println(Log.ASSERT,"image","Failed");
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                filepath = result.getUri();
                queryImageView.setImageURI(filepath);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception e = result.getError();
                Toast.makeText(this, "Error : " + e, Toast.LENGTH_SHORT).show();
            }
        }



    }

    public void send()
    {
        if(filepath!=null)
        {
            String query;
            query=edtTextQuery.getText().toString().trim();


            if(TextUtils.isEmpty(query))
            {
                Toast.makeText(QuerySendingActivity.this,"Please enter something in query",Toast.LENGTH_SHORT).show();
                return ;
            }

//            docRef.child(docUid).child("patients").child(patientUid).child("Query").setValue(query);
//            patientRef.child(patientUid).child("doctors").child(docUid).child("Query").setValue(query);

            progresslayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.INVISIBLE);

            storageReference.child(docUid+patientUid).putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progresslayout.setVisibility(View.GONE);
                            docRef.child(docUid).child("patients").child(patientUid).child("Query").setValue(query);
                            patientRef.child(patientUid).child("doctors").child(docUid).child("Query").setValue(query);
                            docRef.child(docUid).child("patients").child(patientUid).child("Comment").setValue(" ");
                            patientRef.child(patientUid).child("doctors").child(docUid).child("Comment").setValue(" ");
                            patientRef.child(patientUid).child("doctors").child(docUid).child("Name").setValue(docName);
                            docRef.child(docUid).child("patients").child(patientUid).child("Name").setValue(patName);
                            docRef.child(docUid).child("patients").child(patientUid).child("Gender").setValue(patGender);
                            docRef.child(docUid).child("patients").child(patientUid).child("DOB").setValue(patDob);
                            docRef.child(docUid).child("patients").child(patientUid).child("IDCount").setValue(i);


                            Toast.makeText(getApplicationContext(),"Uploaded!",Toast.LENGTH_SHORT).show();
                          finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed! "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            progresslayout.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                        }
                    });
        }
        else
        {
            Toast.makeText(QuerySendingActivity.this,"Something went wrong. Try again!!",Toast.LENGTH_SHORT).show();
        }
    }
}
