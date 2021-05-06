package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PatientProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final int request_image_code=22;

   TextView txt_name,txt_email,txt_phone,txt_gender,txt_id;
   ImageView profile_image_view;
   ImageButton btn_edit_image,btn_upload_image;
   LinearLayout progressLayout;
   Uri filepath;
   String uid;
    int Gallery_request=100;
   Bitmap mImageBitmap;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        txt_name=findViewById(R.id.txt_patient_profile_name);
        txt_email=findViewById(R.id.txt_patient_profile_email);
        txt_phone=findViewById(R.id.txt_patient_profile_phone);
        txt_gender=findViewById(R.id.txt_patient_profile_gender);
        txt_id=findViewById(R.id.txt_patient_profile_id);
        profile_image_view=findViewById(R.id.patient_profile_image);
        btn_edit_image=findViewById(R.id.btn_edit_image_patient);
        btn_upload_image=findViewById(R.id.btn_upload_image_patient);
        btn_upload_image.setOnClickListener(this);
        btn_edit_image.setOnClickListener(this);

        progressLayout=findViewById(R.id.progress_layout);

        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        databaseReference=firebaseDatabase.getReference().child("patient").child(uid);

        dataloader();
    }

    public void dataloader()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name,email,phone,gender,id;
                name="Name: "+ snapshot.child("Name").getValue(String.class);
                email="Email: "+ snapshot.child("Email").getValue(String.class);
                phone="Ph: "+ snapshot.child("Phone").getValue(String.class);
                gender="Gender: "+ snapshot.child("Gender").getValue(String.class);
                id="ID: "+snapshot.child("IDCount").getValue(String.class);

                txt_name.setText(name);
                txt_email.setText(email);
                txt_phone.setText(phone);
                txt_gender.setText(gender);
                txt_id.setText(id);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                 Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        storageReference.child(uid).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile_image_view);
                    }
                });

        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view==btn_edit_image)
        {
             //selectImage();
            OpenGallery();
             btn_upload_image.setVisibility(View.VISIBLE);
        }
        else if(view==btn_upload_image)
        {
          uploadImage();
        }
    }

    public void selectImage()
    {


        Log.println(Log.ASSERT,"image","select image");
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(PatientProfileActivity.this);
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
                            .start(PatientProfileActivity.this);
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
                profile_image_view.setImageURI(filepath);
                // mImageBitmap = result.getBitmap();
                mImageBitmap = ((BitmapDrawable) profile_image_view.getDrawable()).getBitmap();

                File filesDir = getApplicationContext().getCacheDir();
                File imageFile = new File(filesDir, "image" + ".jpg");

                OutputStream os;
                try {
                    os = new FileOutputStream(imageFile);
                    mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                RequestBody requestBitmap = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBitmap);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception e = result.getError();
                Toast.makeText(this, "Error : " + e, Toast.LENGTH_SHORT).show();
            }
        }


        //gfg method

//        if(requestCode==request_image_code && resultCode==RESULT_OK && data!=null && data.getData()!=null)
//        {
//            filepath=data.getData();
//
//            try {
//                Bitmap bitmap= MediaStore
//                                .Images
//                                .Media
//                                .getBitmap(getContentResolver(),filepath);
//                profile_image_view.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

    }

    public void uploadImage()
    {
        if(filepath!=null)
        {


            StorageReference reference=storageReference.child(uid);
            reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          // progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Uploaded!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           // progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Failed!"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            //progressDialog.setMessage((int)progress+" %");
                        }
                    });
        }
    }
}
