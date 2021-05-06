package com.iitism.docapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iitism.docapp.adapters.DoctorAdapter;
import com.iitism.docapp.adapters.MyDoctorAdapter;
import com.iitism.docapp.adapters.PendingAdapter;
import com.iitism.docapp.model.DoctorModel;
import com.iitism.docapp.model.MyDoctorModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    LinearLayout progressLayout;

    RecyclerView recyclerView;
    PendingAdapter adapter;
    List<DoctorModel> list;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressLayout=findViewById(R.id.progress_layout);

        recyclerView=findViewById(R.id.pending_patient_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getCurrentUser().getUid();
        reference=firebaseDatabase.getReference().child("doctor").child(uid).child("patients");
        firebaseStorage=FirebaseStorage.getInstance();


        loadData();


    }

    public void loadData()
    {

//        FirebaseRecyclerOptions<MyDoctorModel> options=new FirebaseRecyclerOptions.Builder<MyDoctorModel>()
//                .setQuery(reference,MyDoctorModel.class)
//                .build();



        FirebaseRecyclerOptions<MyDoctorModel> options=new FirebaseRecyclerOptions.Builder<MyDoctorModel>()
                .setQuery(reference, new SnapshotParser<MyDoctorModel>() {
                    @NonNull
                    @Override
                    public MyDoctorModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        MyDoctorModel myDoctorModel=snapshot.getValue(MyDoctorModel.class);

                        assert myDoctorModel != null;
                        myDoctorModel.setUid(snapshot.getKey());
                       // Toast.makeText(getApplicationContext(), snapshot.getKey(),Toast.LENGTH_SHORT).show();
                        Log.println(Log.ASSERT,"recycler" , Objects.requireNonNull(snapshot.getKey()));
                        progressLayout.setVisibility(View.GONE);
                        return myDoctorModel;
                    }
                }).build();

        adapter=new PendingAdapter(DoctorActivity.this,options);


        recyclerView.setAdapter(adapter);

        //progressLayout.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //progressLayout.setVisibility(View.VISIBLE);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doc_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_profile) {
            Intent i=new Intent(getApplicationContext(),DocProfileActivity.class);
            startActivity(i);
        }
        else if(id==R.id.action_my_patients)
        {
            Intent i=new Intent(getApplicationContext(),MyPatientActivity.class);
            startActivity(i);
        }
        else if(id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            SharedPreferences sharedPreferences=getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1=sharedPreferences.edit();
            editor1.putString("Doctor Login","");
            editor1.apply();

            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }



}
