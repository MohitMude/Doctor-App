package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpDoctorActivity extends AppCompatActivity implements View.OnClickListener{
    EditText edt_name,edt_speciality,edt_qualification,edt_email,edt_password,edt_phone;
    Button btn_signup;
    String name,speciality,qualification,email,password,phone,gender;
    Spinner spinner;
    LinearLayout progressLayout,notProgressLayout;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    long a=6000000000L,b=9999999999L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_doctor);

        edt_name=findViewById(R.id.edt_text_doc_name);
        edt_speciality=findViewById(R.id.edt_text_doc_speciality);
        edt_qualification=findViewById(R.id.edt_text_doc_qualification);
        edt_email=findViewById(R.id.edt_text_doc_email);
        edt_password=findViewById(R.id.edt_text_doc_password);
        edt_phone=findViewById(R.id.edt_text_doc_phone);
        btn_signup=findViewById(R.id.btn_doc_sign_up);
        progressLayout=findViewById(R.id.progress_layout);
        notProgressLayout=findViewById(R.id.not_progress_layout);

        spinner=findViewById(R.id.doctor_gender_spinner);


        String[] gendername=getResources().getStringArray(R.array.gender_spinner);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,gendername);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender=spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_signup.setOnClickListener(this);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("doctor");
    }

    @Override
    public void onClick(View view) {
        if(view==btn_signup)
        {
            name = edt_name.getText().toString().trim();
            speciality = edt_speciality.getText().toString().trim();
            qualification=edt_qualification.getText().toString().trim();
            phone = edt_phone.getText().toString().trim();
            email = edt_email.getText().toString().trim();
            password = edt_password.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                edt_name.setError("Enter Name");
                return;
            } else if (TextUtils.isEmpty(speciality)) {
                edt_speciality.setError("Enter speciality");
                return;
            } else if (TextUtils.isEmpty(qualification)) {
                edt_qualification.setError("Enter qualification");
                return;
            }else if (TextUtils.isEmpty(email)) {
                edt_email.setError("Enter Email");
                return;
            } else if (TextUtils.isEmpty(password)) {
                edt_password.setError("Enter Password");
                return;
            } else if (TextUtils.isEmpty(phone)) {
                edt_phone.setError("Enter Phone");
                return;
            }
            else if (gender.equals("Select gender")) {
                Toast.makeText(getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                long p = Long.parseLong(phone);

                if (p < a || p > b) {
                    edt_phone.setError("Enter Correct Phone");
                    return;
                }
            }

            progressLayout.setVisibility(View.VISIBLE);
            notProgressLayout.setVisibility(View.INVISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                firebaseUser=firebaseAuth.getCurrentUser();
                                assert firebaseUser != null;
                                String id=firebaseUser.getUid();
                                databaseReference.child(id).child("Email").setValue(email);
                                databaseReference.child(id).child("Name").setValue(name);
                                databaseReference.child(id).child("Gender").setValue(gender);
                                databaseReference.child(id).child("Speciality").setValue(speciality);
                                databaseReference.child(id).child("Qualification").setValue(qualification);
                                databaseReference.child(id).child("Phone").setValue(phone);
                                databaseReference.child(id).child("Image").setValue(" ");

                                Toast.makeText(getApplicationContext(),"SignUp Successful",Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                editor1.putString("Doctor Login", "true");
                                editor1.apply();
                                Intent i=new Intent(getApplicationContext(), DoctorActivity.class);
                                finish();
                                startActivity(i);

                            }
                            else
                            {
                                progressLayout.setVisibility(View.GONE);
                                notProgressLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                    });
        }
    }
}
