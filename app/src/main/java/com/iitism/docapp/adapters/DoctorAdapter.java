package com.iitism.docapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iitism.docapp.QuerySendingActivity;
import com.iitism.docapp.R;
import com.iitism.docapp.model.DoctorModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
    private Context context;
    private List<DoctorModel> doclist;

    public DoctorAdapter(Context context, List<DoctorModel> doclist) {
        this.context = context;
        this.doclist = doclist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_doctor_card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

         DoctorModel doctorModel=doclist.get(i);
         holder.linearLayout.setTag(i);
        String name,email,phone,speciality,qualification,uid,url;
        name="Name: "+ doctorModel.getName();
        email="Email: "+ doctorModel.getEmail();
        phone="Ph: " + doctorModel.getPhone();
        speciality="Speciality: "+ doctorModel.getSpeciality();
        qualification="Qualification: " + doctorModel.getQualification();
        url=doctorModel.getUrl();
        uid=doctorModel.getUid();

        if(!TextUtils.equals(" ",url))
        {
            Glide.with(context).load(url).into(holder.doc_image);
        }



        holder.txt_name.setText(name);
        holder.txt_email.setText(email);
        holder.txt_phone.setText(phone);
        holder.txt_speciality.setText(speciality);
        holder.txt_qualification.setText(qualification);




    }



    @Override
    public int getItemCount() {
        return doclist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView doc_image;
        TextView txt_name,txt_email,txt_phone,txt_speciality,txt_qualification;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doc_image=itemView.findViewById(R.id.doc_image);
            txt_email=itemView.findViewById(R.id.doc_txt_email);
            txt_name=itemView.findViewById(R.id.doc_txt_name);
            txt_phone=itemView.findViewById(R.id.doc_txt_phone);
            txt_speciality=itemView.findViewById(R.id.doc_txt_speciality);
            txt_qualification=itemView.findViewById(R.id.doc_txt_qualification);
            linearLayout=itemView.findViewById(R.id.all_doc_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open new activity
                   Intent i=new Intent(context, QuerySendingActivity.class);
                    int position=(int)v.getTag();
                    i.putExtra("uid",doclist.get(position).getUid());
                    i.putExtra("name",doclist.get(position).getName());
                    context.startActivity(i);
                }
            });
        }
    }
}
