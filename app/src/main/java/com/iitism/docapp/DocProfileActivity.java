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
import java.io.FileOutputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DocProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private final int request_image_code=22;

    TextView txt_name,txt_email,txt_phone,txt_gender,txt_speciality,txt_qualification;
    ImageView profile_image_view;
    ImageButton btn_edit_image,btn_upload_image;
    Uri filepath;
    String uid;
    Bitmap mImageBitmap;
    int Gallery_request=100;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile);

        txt_name=findViewById(R.id.txt_doc_profile_name);
        txt_email=findViewById(R.id.txt_doc_profile_email);
        txt_phone=findViewById(R.id.txt_doc_profile_phone);
        txt_gender=findViewById(R.id.txt_doc_profile_gender);
        txt_speciality=findViewById(R.id.txt_doc_profile_speciality);
        txt_qualification=findViewById(R.id.txt_doc_profile_qualification);
        profile_image_view=findViewById(R.id.doc_profile_image);
        btn_edit_image=findViewById(R.id.btn_edit_image_doc);
        btn_upload_image=findViewById(R.id.btn_upload_image_doc);
        btn_upload_image.setOnClickListener(this);
        btn_edit_image.setOnClickListener(this);

        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        databaseReference=firebaseDatabase.getReference().child("doctor").child(uid);

        dataloader();
    }

    public void dataloader()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name,email,phone,gender,speciality,qualification;
                name="Name: "+ snapshot.child("Name").getValue(String.class);
                email="Email: "+ snapshot.child("Email").getValue(String.class);
                phone="Ph: "+ snapshot.child("Phone").getValue(String.class);
                gender="Gender: "+ snapshot.child("Gender").getValue(String.class);
                speciality="Speciality: "+ snapshot.child("Speciality").getValue(String.class);
                qualification="Qualification: "+snapshot.child("Qualification").getValue(String.class);

                txt_name.setText(name);
                txt_email.setText(email);
                txt_phone.setText(phone);
                txt_gender.setText(gender);
                txt_speciality.setText(speciality);
                txt_qualification.setText(qualification);

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

    public void OpenGallery()
    {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,Gallery_request);
    }


    public void selectImage()
    {

        Log.println(Log.ASSERT,"image","select image");
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(DocProfileActivity.this);
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
                        .start(DocProfileActivity.this);
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




    }



    public void uploadImage()
    {
        if(filepath!=null)
        {
//            final ProgressDialog progressDialog=new ProgressDialog(this);
//            progressDialog.setTitle("Uploading..");
//            progressDialog.show();

            StorageReference reference=storageReference.child(uid);
            reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // progressDialog.dismiss();

                            final String[] url = new String[1];
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url[0] =uri.toString();
                                    Log.println(Log.ASSERT,"storage",url[0]);
                                    databaseReference.child("Image").setValue(url[0]);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    url[0]=" ";
                                    Log.println(Log.ASSERT,"storage","url failed");
                                }
                            });
                            Log.println(Log.ASSERT,"storage","before toast");
                            Toast.makeText(getApplicationContext(),"Uploaded!",Toast.LENGTH_SHORT).show();
                            btn_upload_image.setVisibility(View.GONE);

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
