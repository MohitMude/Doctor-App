package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText edt_email,edt_password;
    Button btnLogin;
    TextView sign_up_patient,sign_up_doctor;
    LinearLayout progressLayout;

    String email,password;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference doctorReference,patientReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_email=findViewById(R.id.edt_txt_login_email);
        edt_password=findViewById(R.id.edt_txt_login_password);
        btnLogin=findViewById(R.id.btn_login);
        sign_up_doctor=findViewById(R.id.sign_up_doctor);
        sign_up_patient=findViewById(R.id.sign_up_patient);
        progressLayout=findViewById(R.id.progress_layout);

        firebaseAuth=FirebaseAuth.getInstance();
        doctorReference= FirebaseDatabase.getInstance().getReference().child("doctor");
        patientReference=FirebaseDatabase.getInstance().getReference().child("patient");


        btnLogin.setOnClickListener(this);
        sign_up_patient.setOnClickListener(this);
        sign_up_doctor.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view==btnLogin)
        {
            email=edt_email.getText().toString().trim();
            password=edt_password.getText().toString().trim();

            Log.println(Log.ASSERT,"login","button clicked");

            if(TextUtils.isEmpty(email))
            {
                edt_email.setError("Enter Email");
                return;
            }
            else if(TextUtils.isEmpty(password))
            {
                edt_password.setError("Enter Password");
                return;
            }

            progressLayout.setVisibility(View.VISIBLE);
            edt_email.setVisibility(View.INVISIBLE);
            edt_password.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            sign_up_doctor.setVisibility(View.INVISIBLE);
            sign_up_patient.setVisibility(View.INVISIBLE);


            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {

                        Log.println(Log.ASSERT,"login","email method");
                        if(task.isSuccessful())
                        {
                            //if doctor login
                            Log.println(Log.ASSERT,"login","success");
                            doctorReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                    Log.println(Log.ASSERT,"login","database doc");
                                    for(DataSnapshot ds:datasnapshot.getChildren())
                                    {
                                        if(email.equals(ds.child("Email").getValue(String.class)))
                                        {
                                            //more to add
                                            SharedPreferences sharedPreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                            editor1.putString("Doctor Login", "true");
                                            editor1.apply();

                                            Intent i = new Intent(getApplicationContext(), DoctorActivity.class);
                                            finish();
                                            startActivity(i);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            patientReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                    Log.println(Log.ASSERT,"login","database pat");
                                    for(DataSnapshot ds:datasnapshot.getChildren())
                                    {
                                       // Log.println(Log.ASSERT,"login",ds.child(""));
                                        if(email.equals(ds.child("Email").getValue(String.class)))
                                        { Log.println(Log.ASSERT,"login","pat found");
                                            //more to add
                                            //String id=ds.child("IDCount").getValue(String.class);
                                            SharedPreferences sharedPreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                            editor1.putString("Patient Login", "true");
//                                            assert id != null;
//                                            editor1.putInt("ID",Integer.parseInt(id));
                                            editor1.apply();

                                            Intent i = new Intent(getApplicationContext(), PatientActivity.class);
                                            finish();
                                            startActivity(i);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else
                        {
                            progressLayout.setVisibility(View.GONE);
                            edt_email.setVisibility(View.VISIBLE);
                            edt_password.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            sign_up_doctor.setVisibility(View.VISIBLE);
                            sign_up_patient.setVisibility(View.VISIBLE);
                            Log.w("12345", "signInWithEmail:failure", task.getException());

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    });
        }
        else if(view==sign_up_doctor)
        {
            Intent i = new Intent(getApplicationContext(), SignUpDoctorActivity.class);
            //finish();
            startActivity(i);
        }
        else if(view==sign_up_patient)
        {
            Intent i = new Intent(getApplicationContext(), SignUpPatientActivity.class);
            //finish();
            startActivity(i);
        }

    }
}
