package com.iitism.docapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iitism.docapp.DoctorActivity;
import com.iitism.docapp.QuerySendingActivity;
import com.iitism.docapp.R;
import com.iitism.docapp.ReplyActivity;
import com.iitism.docapp.model.MyDoctorModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PendingAdapter extends FirebaseRecyclerAdapter<MyDoctorModel,PendingAdapter.ViewHolder> {
Context context;


    public PendingAdapter(Context context,@NonNull FirebaseRecyclerOptions<MyDoctorModel> options) {

        super(options);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MyDoctorModel model) {
        holder.linearLayout.setTag(position);

        String name,query,patUid,gender,dob;
        name="Name: "+ model.getName();
        query="Query: "+ model.getQuery();
        gender="Gender: "+model.getGender();
        patUid="Id: "+ model.getIDCount();
        int len=model.getDob().length();
        String DOB=model.getDob();

        String [] dateParts = DOB.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        dob="Age: "+ getAge(year,month-1,day);

        if(model.getComment().equals(" "))
        {
            holder.txt_name.setText(name);
            holder.txt_query.setText(query);
            holder.txt_gender.setText(gender);
            holder.txt_dob.setText(dob);
            holder.txt_uid.setText(patUid);
        }
        else
        {
            holder.hideLayout();
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, ReplyActivity.class);

                i.putExtra("uid",model.getUid());
                i.putExtra("query",model.getQuery());
                context.startActivity(i);

            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_patient_card,parent,false);
        return  new PendingAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name,txt_query,txt_gender,txt_dob,txt_uid;
        LinearLayout linearLayout,childLayout;

        final LinearLayout.LayoutParams param;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_view_pending_patient_name);
            txt_dob=itemView.findViewById(R.id.txt_view_pending_patient_dob);
            txt_gender=itemView.findViewById(R.id.txt_view_pending_patient_gender);
            txt_query=itemView.findViewById(R.id.txt_view_pending_query);
            txt_uid=itemView.findViewById(R.id.txt_view_pending_patient_uid);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.pending_patient_linear_layout);
            childLayout=itemView.findViewById(R.id.pending_patient_expandable_linear_layout);
            param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);





        }



        private void hideLayout()
        {
            param.height=0;
            itemView.setLayoutParams(param);
        }
    }

    public int getAge (int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
//        if(a < 0)
//            throw new IllegalArgumentException("Age < 0");
        return a;
    }
}
