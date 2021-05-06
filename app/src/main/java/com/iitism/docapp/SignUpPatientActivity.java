package com.iitism.docapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.Calendar;

public class SignUpPatientActivity extends AppCompatActivity implements View.OnClickListener{


    EditText edt_name,edt_email,edt_password,edt_phone;
    TextView txt_dob;
    Button btn_signup;
    String name,dob,email,password,phone,gender;
    Spinner spinner;
    LinearLayout prgressLayout,notProgressLayout;
    private int idCount=-1;

    DatePickerDialog datepicker;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference idReference;
    long a=6000000000L,b=9999999999L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_patient);

        edt_name=findViewById(R.id.edt_text_patient_name);
        txt_dob=findViewById(R.id.edt_text_patient_dob);
        edt_email=findViewById(R.id.edt_text_patient_email);
        edt_password=findViewById(R.id.edt_text_patient_password);
        edt_phone=findViewById(R.id.edt_text_patient_phone);
        btn_signup=findViewById(R.id.btn_patient_sign_up);

        notProgressLayout=findViewById(R.id.not_progress_layout);
        prgressLayout=findViewById(R.id.progress_layout);


        spinner=findViewById(R.id.patient_gender_spinner);

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
        txt_dob.setOnClickListener(this);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("patient");
        idReference=firebaseDatabase.getReference().child("count");

        idReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String i=snapshot.getValue(String.class);
                assert i != null;
                idCount=Integer.parseInt(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SignUpPatientActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view==btn_signup)
        {

            name = edt_name.getText().toString().trim();
            dob = txt_dob.getText().toString().trim();
            phone = edt_phone.getText().toString().trim();
            email = edt_email.getText().toString().trim();
            password = edt_password.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                edt_name.setError("Enter Name");
                return;
            }
            else if (TextUtils.isEmpty(dob)) {
                txt_dob.setError("Enter Date of Birth");
                return;
            }
            else if (TextUtils.isEmpty(email)) {
                edt_email.setError("Enter Email");
                return;
            } else if (TextUtils.isEmpty(password)) {
                edt_password.setError("Enter Password");
                return;
            } else if (TextUtils.isEmpty(phone)) {
                edt_phone.setError("Enter Phone");
                return;
            }  else if (gender.equals("Select gender")) {
                Toast.makeText(getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
                return;
            }else {
                long p = Long.parseLong(phone);

                if (p < a || p > b) {
                    edt_phone.setError("Enter Correct Phone");
                  return;
                }
            }
            notProgressLayout.setVisibility(View.INVISIBLE);
            notProgressLayout.setVisibility(View.VISIBLE);
            if(idCount!=-1)
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
                                databaseReference.child(id).child("DOB").setValue(dob);
                                databaseReference.child(id).child("Phone").setValue(phone);
                                idCount++;
                                String ic=String.valueOf(idCount);
                                databaseReference.child(id).child("IDCount").setValue(ic);
                                idReference.setValue(ic);

                                Toast.makeText(getApplicationContext(),"SignUp Successful",Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                editor1.putString("Patient Login", "true");
                                //editor1.putInt("ID",idCount);
                                editor1.apply();
                                Intent i=new Intent(getApplicationContext(), PatientActivity.class);
                                finish();
                                startActivity(i);

                            }
                            else
                            {
                                prgressLayout.setVisibility(View.GONE);
                                notProgressLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            }

                    });
             }
        else if(view==txt_dob)
        {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            datepicker = new DatePickerDialog(SignUpPatientActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Log.println(Log.ASSERT,"date",txt_dob.getText().toString());
                            txt_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            Log.println(Log.ASSERT,"date",txt_dob.getText().toString());
                        }
                    }, year, month, day);
            datepicker.show();
        }

        }
    }

