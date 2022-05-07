package com.example.herewewere.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herewewere.R;
import com.example.herewewere.databases.FBComment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;



    public class FBCommentAdapter extends FirebaseRecyclerAdapter<FBComment, FBCommentAdapter.Myviewholder> {


        public FBCommentAdapter(@NonNull FirebaseRecyclerOptions<FBComment> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull Myviewholder holder, int position, @NonNull FBComment model) {
            holder.comment.setText(model.getComment());
            holder.User.setText(model.getUserid());
        }

        @NonNull
        @Override
        public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_c,parent,false);
            return new Myviewholder(v);
        }

        public class Myviewholder extends RecyclerView.ViewHolder {
            TextView comment,User;

            public Myviewholder(@NonNull View itemView) {
                super(itemView);
                User = itemView.findViewById(R.id.FBUser);
                comment = itemView.findViewById(R.id.Comment);
            }
        }
    }

