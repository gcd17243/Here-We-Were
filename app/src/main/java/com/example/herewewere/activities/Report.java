package com.example.herewewere.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.herewewere.R;
import com.example.herewewere.adapters.FBAdapter;
import com.example.herewewere.adapters.FBCommentAdapter;
import com.example.herewewere.adapters.FBReportAdapter;
import com.example.herewewere.databases.FBComment;
import com.example.herewewere.databases.FBPost;
import com.example.herewewere.databases.FBReport;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Report extends AppCompatActivity {
    RecyclerView recview;
    FBReportAdapter fbReportAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        recview=(RecyclerView)findViewById(R.id.fbReportlist);
        recview.setLayoutManager(new LinearLayoutManager(this));

        if(user.getEmail().equals("hanngcd17243@fpt.edu.vn")){
            FirebaseRecyclerOptions<FBReport> options =
                    new FirebaseRecyclerOptions.Builder<FBReport>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("FBReport"), FBReport.class)
                            .build();
            fbReportAdapter=new FBReportAdapter(options);
            recview.setAdapter(fbReportAdapter);
        }else{
            FirebaseRecyclerOptions<FBReport> options =
                    new FirebaseRecyclerOptions.Builder<FBReport>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("FBReport").orderByChild("userReportid").startAt(user.getEmail()).endAt(user.getEmail()+"\uf8ff"), FBReport.class)
                            .build();
            fbReportAdapter=new FBReportAdapter(options);
            fbReportAdapter.startListening();
            recview.setAdapter(fbReportAdapter);

        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        fbReportAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fbReportAdapter.stopListening();
    }
}