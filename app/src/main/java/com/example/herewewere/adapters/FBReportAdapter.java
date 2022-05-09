package com.example.herewewere.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herewewere.Login.RegisterActivity;
import com.example.herewewere.R;
import com.example.herewewere.activities.CommentFragment;
import com.example.herewewere.databases.FBDatabasePost;
import com.example.herewewere.databases.FBReport;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class FBReportAdapter extends FirebaseRecyclerAdapter<FBReport, FBReportAdapter.Myviewholder> {

    public FBReportAdapter(@NonNull FirebaseRecyclerOptions<FBReport> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FBReportAdapter.Myviewholder holder, int position, @NonNull FBReport model) {
        holder.comment.setText(model.getComment());
        holder.User.setText("User:"+model.getUserid());
        holder.postid.setText("had been report Post:"+model.getPostid()+" with the comment:");
        holder.UserReport.setText("The post was made by:"+model.getUserReportid());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.User.getContext());
                builder.setTitle("Delete Panel");
                builder.setMessage("Delete...?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("FBReport")
                                .child(getRef(position).getKey()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("FBPost")
                                .child(model.getPostid()).removeValue();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public FBReportAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_r,parent,false);
        return new FBReportAdapter.Myviewholder(v);    }

    public class Myviewholder extends RecyclerView.ViewHolder {
        TextView comment,User,postid,UserReport;
        RelativeLayout relativeLayout;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            User = itemView.findViewById(R.id.FBUser);
            comment = itemView.findViewById(R.id.Comment);
            postid = itemView.findViewById(R.id.PostID);
            UserReport = itemView.findViewById(R.id.UserReport);
            relativeLayout = itemView.findViewById(R.id.item);
        }
    }
}
