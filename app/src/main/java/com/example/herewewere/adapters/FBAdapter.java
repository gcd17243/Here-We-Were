package com.example.herewewere.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.herewewere.R;
import com.example.herewewere.activities.CommentFragment;
import com.example.herewewere.databases.FBPost;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;


public class FBAdapter extends FirebaseRecyclerAdapter<FBPost,FBAdapter.Myviewholder> {

    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();


    public FBAdapter(@NonNull FirebaseRecyclerOptions<FBPost> options) {super(options);}

    @Override
    protected void onBindViewHolder(@NonNull Myviewholder holder, int position, @NonNull FBPost fbPost) {

        Glide.with(holder.img.getContext()).load(fbPost.getImgpath()).into(holder.img);
        holder.headLine.setText(fbPost.getTitle());
        holder.details.setText(fbPost.getNote());
        holder.User.setText(fbPost.getUserid());
        holder.dateShow.setText(fbPost.getDate());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity)view.getContext();
/*
                Intent intent = new Intent(activity, FBComment.class);
                intent.putExtra("id", getRef(position).getKey());
                intent.putExtra("Title",fbPost.getTitle() );
                intent.putExtra("Note",fbPost.getNote() );
                intent.putExtra("Latid",fbPost.getLatid() );
                intent.putExtra("Longid",fbPost.getLongid() );
                intent.putExtra("Userid",fbPost.getUserid() );
                intent.putExtra("ImagePath", fbPost.getImgpath());
                context.startActivity(intent);

 */
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper,new CommentFragment(getRef(position).getKey(),fbPost.getTitle(),fbPost.getNote(),fbPost.getLatid(),fbPost.getLongid(),fbPost.getImgpath(),fbPost.getUserid())).addToBackStack(null).commit();
            }
        });
        if(user.getEmail().equals("hanngcd17243@fpt.edu.vn")){
            holder.linearLayout.setVisibility(View.VISIBLE);
        }else
if(user.getEmail().equals(fbPost.getUserid())){
    holder.linearLayout.setVisibility(View.VISIBLE);
}
else{
    holder.linearLayout.setVisibility(View.GONE);
}

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialogcontent))
                        .setExpanded(true,1100)
                        .create();

                View myview=dialogPlus.getHolderView();
                final EditText Title=myview.findViewById(R.id.uTitle);
                final EditText Note=myview.findViewById(R.id.uNote);
                Button submit=myview.findViewById(R.id.usubmit);

                Title.setText(fbPost.getTitle());
                Note.setText(fbPost.getNote());
                dialogPlus.show();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,Object> map=new HashMap<>();
                        map.put("title",Title.getText().toString());
                        map.put("note",Note.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("FBPost")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.img.getContext());
                builder.setTitle("Delete Panel");
                builder.setMessage("Delete...?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("FBPost")
                                .child(getRef(position).getKey()).removeValue();
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
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new Myviewholder(v);
    }

    class Myviewholder extends RecyclerView.ViewHolder{
        ImageView img,edit,delete;
        TextView headLine,details,User,dateShow,view;
        RelativeLayout relativeLayout;
        LinearLayout linearLayout;


        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.FBimg);
            headLine = itemView.findViewById(R.id.FBheadLine);
            details = itemView.findViewById(R.id.FBdetails);
            User = itemView.findViewById(R.id.FBUser);
            dateShow = itemView.findViewById(R.id.FBdateShow);
            relativeLayout = itemView.findViewById(R.id.item);
            view = itemView.findViewById(R.id.View);
            edit=itemView.findViewById(R.id.editicon);
            delete=itemView.findViewById(R.id.deleteicon);
            linearLayout=itemView.findViewById(R.id.editfb);

        }
    }
}