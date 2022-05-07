package com.example.herewewere.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herewewere.databases.FBReport;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FBReportAdapter extends FirebaseRecyclerAdapter<FBReport, FBReportAdapter.Myviewholder> {

    public FBReportAdapter(@NonNull FirebaseRecyclerOptions<FBReport> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FBReportAdapter.Myviewholder holder, int position, @NonNull FBReport model) {

    }

    @NonNull
    @Override
    public FBReportAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public class Myviewholder extends RecyclerView.ViewHolder {
        public Myviewholder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
