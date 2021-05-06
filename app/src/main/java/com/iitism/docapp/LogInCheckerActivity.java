package com.iitism.docapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LogInCheckerActivity extends AppCompatActivity {
    String Patientloginvalue,Doctorloginvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences sharedPreferences=getSharedPreferences("Login", Context.MODE_PRIVATE);
        Patientloginvalue=sharedPreferences.getString("Patient Login","");
        Doctorloginvalue=sharedPreferences.getString("Doctor Login","");


        if(Patientloginvalue.equals("true"))
        {
            Intent i=new Intent(getApplicationContext(), PatientActivity.class);
            finish();
            startActivity(i);
        }
        else if(Doctorloginvalue.equals("true"))
        {
            Intent i=new Intent(getApplicationContext(), DoctorActivity.class);
            finish();
            startActivity(i);
        }
        else
        {
            Intent i=new Intent(getApplicationContext(), LoginActivity.class);
            finish();
            startActivity(i);

        }

    }
}
