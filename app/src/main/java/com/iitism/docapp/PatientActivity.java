package com.iitism.docapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iitism.docapp.adapters.DoctorAdapter;
import com.iitism.docapp.model.DoctorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    LinearLayout progressLayout;
    EditText searchBox;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<DoctorModel> list;
    List<DoctorModel> searchList;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchBox=findViewById(R.id.search_view);
        recyclerView=findViewById(R.id.all_doc_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressLayout=findViewById(R.id.progress_layout);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference().child("doctor");
        firebaseStorage=FirebaseStorage.getInstance();

        loadData();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               filter(editable.toString());
            }
        });


    }

    private void filter(final String s) {
      int size=list.size();
      searchList=new ArrayList<>();
      if(!searchList.isEmpty())
      searchList.clear();

      for(int i=0;i<size;i++)
      {
          DoctorModel model=list.get(i);

          if(model.getName().toLowerCase().contains(s.toLowerCase()) ||
                  model.getEmail().toLowerCase().contains(s.toLowerCase()) ||
                  model.getPhone().toLowerCase().contains(s.toLowerCase()) ||
                  model.getSpeciality().toLowerCase().contains(s.toLowerCase()) ||
                  model.getQualification().toLowerCase().contains(s.toLowerCase()))
          {
              searchList.add(model);

          }


      }
        adapter = new DoctorAdapter(PatientActivity.this, searchList);
        recyclerView.setAdapter(adapter);

    }


    public void loadData()
    {
        Log.println(Log.ASSERT,"data","data loading..");
        list=new ArrayList<>();
        list.clear();
        progressLayout.setVisibility(View.VISIBLE);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.println(Log.ASSERT,"data", Objects.requireNonNull(snapshot.getKey()));
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    uid=ds.getKey();
                    String name,email,phone,speciality,qualification,url;
                    Uri[] imageUri = new Uri[1];

                    name=ds.child("Name").getValue(String.class);
                    email=ds.child("Email").getValue(String.class);
                    phone=ds.child("Phone").getValue(String.class);
                    speciality=ds.child("Speciality").getValue(String.class);
                    qualification=ds.child("Qualification").getValue(String.class);
                    url=ds.child("Image").getValue(String.class);

                    imageUri=storage(uid);

                    //DoctorModel doctorModel = new DoctorModel(name,email,phone,speciality,qualification,imageUri[0],uid);
                    DoctorModel doctorModel = new DoctorModel(name,email,phone,speciality,qualification,uid,url);
                    list.add(doctorModel);
                   // searchList.add(doctorModel);
                    Log.println(Log.ASSERT,"data",name+email+phone);
                }
                adapter = new DoctorAdapter(PatientActivity.this, list);
                recyclerView.setAdapter(adapter);

                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressLayout.setVisibility(View.GONE);
            }
        });


//        if(list.size()==0)
//        {
//            Toast.makeText(getApplicationContext(),"No doctors available",Toast.LENGTH_LONG).show();
//        }

    }

    public Uri[] storage(String x)
    {
        final Uri[] imageUri = new Uri[1];
        storageReference=firebaseStorage.getReference().child(x);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.println(Log.ASSERT,"error",uri.toString());
                imageUri[0] = uri;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.ASSERT,"error","failed to load image");
                imageUri[0] =null;
            }
        });
        return imageUri;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_profile) {
            Intent i=new Intent(getApplicationContext(),PatientProfileActivity.class);
            startActivity(i);
        }
        else if(id==R.id.action_my_docs)
        {
            Intent i=new Intent(getApplicationContext(),MyDoctorActivity.class);
            startActivity(i);
        }
        else if(id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            SharedPreferences sharedPreferences=getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1=sharedPreferences.edit();
            editor1.putString("Patient Login","");
            editor1.apply();

            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
