package com.iitism.docapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iitism.docapp.R;
import com.iitism.docapp.model.DoctorModel;
import com.iitism.docapp.model.MyDoctorModel;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.List;

public class MyDoctorAdapter extends FirebaseRecyclerAdapter<MyDoctorModel,MyDoctorAdapter.ViewHolder> {

      Context context;
      String uid;
    private static int currentPosition = -1;

    public MyDoctorAdapter(Context context,String uid,@NonNull FirebaseRecyclerOptions<MyDoctorModel> options) {
        super(options);
        this.context = context;
        this.uid=uid;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyDoctorAdapter.ViewHolder holder, int position, @NonNull MyDoctorModel model) {
        String name,query,comment;
        name="Name: "+ model.getName();
        query="Query: "+ model.getQuery();
        comment="Comment: "+ model.getComment();
        Uri uri=model.getUri();

        if(uri!=null) {
            Log.println(Log.ASSERT, "reply_adapter", uri.toString());
//            holder.imageView.setVisibility(View.VISIBLE);
//            holder.line.setVisibility(View.VISIBLE);
            Picasso.get().load(uri).into(holder.imageView);
        }


        holder.txt_name.setText(name);
        holder.txt_query.setText(query);
        holder.txt_comment.setText(comment);

        if (currentPosition == position) {
            //creating an animation
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

            //toggling visibility
            holder.imageView.setVisibility(View.VISIBLE);

            //adding sliding effect
            holder.imageView.startAnimation(slideDown);
        }

        holder.btnDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                if(uri==null)
                {
                    Toast.makeText(context,"No image available!!",Toast.LENGTH_SHORT).show();

                    return ;
                }


                if(holder.imageView.getVisibility()==View.VISIBLE)
                {
                    holder.imageView.setVisibility(View.GONE);
                    holder.btnDropDown.setImageResource(R.drawable.ic_drop_down);
                    currentPosition=-1;
                    notifyDataSetChanged();
                }
                else
                {
                    currentPosition = position;
                    holder.btnDropDown.setImageResource(R.drawable.ic_up);
                    notifyDataSetChanged();
                }


            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences=context.getSharedPreferences("Login",Context.MODE_PRIVATE);
                if(sharedPreferences.getString("Doctor Login","").equals("true"))
                {
                   // Toast.makeText(context,model.getUid(),Toast.LENGTH_SHORT).show();
                    AlertDialog diaBox = Doctor(model.getUid());
                    diaBox.show();
                }
                else if(sharedPreferences.getString("Patient Login","").equals("true"))
                {
                    AlertDialog diaBox =Patient(model.getUid());
                    diaBox.show();
                }


            }
        });

    }


    @NonNull
    @Override
    public MyDoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view=LayoutInflater.from(parent.getContext())
               .inflate(R.layout.my_doctor_card,parent,false);
       currentPosition=-1;
       return  new MyDoctorAdapter.ViewHolder(view);
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_name,txt_query,txt_comment;
        ImageButton btnDelete;
        ImageButton btnDropDown;
        ZoomInImageView imageView;
        View line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_name=itemView.findViewById(R.id.my_doc_name);
            txt_query=itemView.findViewById(R.id.my_doc_query);
            txt_comment=itemView.findViewById(R.id.my_doc_comment);
            imageView=itemView.findViewById(R.id.my_doctor_reply_image_view_zoom);
            line=itemView.findViewById(R.id.line_view);
            btnDelete=itemView.findViewById(R.id.btn_delete);
            btnDropDown=itemView.findViewById(R.id.btn_drop_down);
        }
    }



    private AlertDialog Doctor(String Refuid)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("doctor").child(uid).child("patients");
                        databaseReference.child(Refuid).removeValue();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private AlertDialog Patient(String Refuid)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("patient").child(uid).child("doctors");
                        databaseReference.child(Refuid).removeValue();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }
}
