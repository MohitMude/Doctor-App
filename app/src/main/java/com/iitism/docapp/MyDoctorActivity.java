package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iitism.docapp.adapters.DoctorAdapter;
import com.iitism.docapp.adapters.MyDoctorAdapter;
import com.iitism.docapp.model.DoctorModel;
import com.iitism.docapp.model.MyDoctorModel;

import java.util.List;
import java.util.Objects;

public class MyDoctorActivity extends AppCompatActivity {
   RecyclerView recyclerView;
   String uid;
   MyDoctorAdapter adapter;
   List<MyDoctorModel> list;

   FirebaseAuth firebaseAuth;
   FirebaseDatabase firebaseDatabase;
   DatabaseReference databaseReference;
   StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doctor);

        recyclerView=findViewById(R.id.my_doc_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth=FirebaseAuth.getInstance();
        uid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        //Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_LONG).show();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("patient").child(uid).child("doctors");


        FirebaseRecyclerOptions<MyDoctorModel> options=new FirebaseRecyclerOptions.Builder<MyDoctorModel>()
                .setQuery(databaseReference, new SnapshotParser<MyDoctorModel>() {
                    @NonNull
                    @Override
                    public MyDoctorModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        MyDoctorModel myDoctorModel=snapshot.getValue(MyDoctorModel.class);

                        assert myDoctorModel != null;
                        myDoctorModel.setUid(snapshot.getKey());

                        storageReference= FirebaseStorage.getInstance().getReference().child(uid+snapshot.getKey());

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                myDoctorModel.setUri(uri);
                                adapter.notifyDataSetChanged();
                                Log.println(Log.ASSERT,"reply",uri.toString());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                myDoctorModel.setUri(null);
                            }
                        });



                        // Toast.makeText(getApplicationContext(), snapshot.getKey(),Toast.LENGTH_SHORT).show();
                        Log.println(Log.ASSERT,"recycler" , Objects.requireNonNull(snapshot.getKey()));
                        return myDoctorModel;
                    }
                }).build();

        adapter=new MyDoctorAdapter(MyDoctorActivity.this,uid,options);

        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
