package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.Objects;

public class ReplyActivity extends AppCompatActivity implements View.OnClickListener {
    //ImageView imageView;
    ZoomInImageView inImageView;
    ZoomInImageView replyImageView;
    CheckBox checkBox;
    TextView txt_query;
    TextInputEditText edt_txt_reply;
    Button btn_send;
    ImageButton btn_replyImage;
    RelativeLayout relativeLayout;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference docReference,patReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference,replyStorageReference;

    int Gallery_request=100;
    Uri filepath;
    String docUid,patUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

       // imageView=findViewById(R.id.reply_image_view);
        inImageView=findViewById(R.id.reply_image_view_zoom);
        replyImageView=findViewById(R.id.doc_reply_image_view);
        checkBox=findViewById(R.id.checkBox);
        txt_query=findViewById(R.id.txt_view_reply_query);
        edt_txt_reply=findViewById(R.id.edt_text_reply);
        btn_send=findViewById(R.id.btn_reply);
        btn_replyImage=findViewById(R.id.btn_select_reply_image);
        relativeLayout=findViewById(R.id.reply_image_relative_layout);

        btn_send.setOnClickListener(this);
        btn_replyImage.setOnClickListener(this);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked())
                {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        docUid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        Bundle b=getIntent().getExtras();
        if(b!=null)
        {

            patUid = (String) b.get("uid");
            String query="Query: "+ (String)b.get("query");
            txt_query.setText(query);

        }

       // Toast.makeText(getApplicationContext(),patUid,Toast.LENGTH_SHORT).show();
        firebaseDatabase=FirebaseDatabase.getInstance();
        docReference=firebaseDatabase.getReference().child("doctor").child(docUid).child("patients").child(patUid);
        patReference=firebaseDatabase.getReference().child("patient").child(patUid).child("doctors").child(docUid);
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child(docUid+patUid);
        replyStorageReference=firebaseStorage.getReference().child(patUid+docUid);



        loaddata();
    }
    public void loaddata()
    {
        final Uri[] imageUri = new Uri[1];
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageUri[0] = uri;
               // Picasso.get().load(uri).into(imageView);
                Picasso.get().load(uri).into(inImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageUri[0] =null;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view==btn_send) {

            String reply;
            reply = edt_txt_reply.getText().toString().trim();


            if (TextUtils.isEmpty(reply)) {
                Toast.makeText(ReplyActivity.this, "Please enter something in reply", Toast.LENGTH_SHORT).show();
                return;
            }

            docReference.child("Comment").setValue(reply);
            patReference.child("Comment").setValue(reply);

            if(filepath!=null)
            {
               replyStorageReference.putFile(filepath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //progresslayout.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(),"Uploaded!",Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failed! "+e.getMessage(),Toast.LENGTH_SHORT).show();
                               // progresslayout.setVisibility(View.GONE);
                                //relativeLayout.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                                //progressBar.setProgress((int)progress);
                            }
                        });
            }

//            Toast.makeText(getApplicationContext(),"Uploaded!",Toast.LENGTH_SHORT).show();
//            finish();
        }
        else if(view==btn_replyImage)
        {
            OpenGallery();
        }
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
                            .start(ReplyActivity.this);
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
                replyImageView.setImageURI(filepath);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception e = result.getError();
                Toast.makeText(this, "Error : " + e, Toast.LENGTH_SHORT).show();
            }
        }



    }

}
