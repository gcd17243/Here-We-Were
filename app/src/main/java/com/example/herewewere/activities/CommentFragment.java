package com.example.herewewere.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herewewere.R;
import com.example.herewewere.adapters.FBCommentAdapter;
import com.example.herewewere.databases.FBComment;
import com.example.herewewere.databases.FBDatabasePost;
import com.example.herewewere.databases.FBReport;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment implements  OnMapReadyCallback {
    RecyclerView recview;
    FBCommentAdapter fbCommentAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    FBDatabasePost postcomment = new FBDatabasePost();
    View map;
    GoogleMap mMap;
    EditText commenttxt;
    TextView Titletxt,Notetxt;
    String postid,latid,longid,imagePath,UserReport,Title,Note;
    Context context;
    Button Commentbtn,Reportbtn;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public CommentFragment() {

    }

    public CommentFragment(String postid, String title, String note, String latid, String longid, String imagePath, String userReport) {
        this.postid = postid;
        this.Title = title;
        this.Note = note;
        this.latid = latid;
        this.longid = longid;
        this.imagePath = imagePath;
        this.UserReport = userReport;
    }



    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        context = container.getContext();
        commenttxt=view.findViewById(R.id.uComment);
        recview=(RecyclerView)view.findViewById(R.id.fbCommentList);
        recview.setLayoutManager(new LinearLayoutManager(context));
        Titletxt=view.findViewById(R.id.Title);
        Notetxt=view.findViewById(R.id.Note);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_c);
        mapFragment.getMapAsync(this);

        Titletxt.setText(Title);
        Notetxt.setText(Note);

        Commentbtn=view.findViewById(R.id.commentbtn);
        Commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBComment comment = new FBComment(user.getEmail(),commenttxt.getText().toString(),postid);
                postcomment.add(comment).addOnSuccessListener(suc ->
                {
                    Toast.makeText(context,"Save in Firebase",Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(er ->
                {
                    Toast.makeText(context,""+er.getMessage(),Toast.LENGTH_SHORT).show();
                });
            }
        });
        Reportbtn=view.findViewById(R.id.reportbtn);
        Reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Post:"+postid+" have been report for.");
                builder.setMessage(commenttxt.getText().toString());

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FBReport report = new FBReport(user.getEmail(),commenttxt.getText().toString(),postid,UserReport);
                        postcomment.add(report).addOnSuccessListener(suc ->
                        {
                            Toast.makeText(context,"Save in Firebase",Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(er ->
                        {
                            Toast.makeText(context,""+er.getMessage(),Toast.LENGTH_SHORT).show();
                        });
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
        FirebaseRecyclerOptions<FBComment> options =
                new FirebaseRecyclerOptions.Builder<com.example.herewewere.databases.FBComment>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("FBComment").orderByChild("postid").startAt(postid).endAt(postid+"\uf8ff"), FBComment.class)
                        .build();
        fbCommentAdapter=new FBCommentAdapter(options);
        fbCommentAdapter.startListening();
        recview.setAdapter(fbCommentAdapter);

        return  view;

    }

    @Override
    public void onStart() {
        super.onStart();
        fbCommentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        fbCommentAdapter.stopListening();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        double valuelat;
        double valuelong;

        valuelat=0;
        valuelong=0;
        if(latid != null||longid != null){
            try
            {   valuelat = Double.parseDouble(latid);
                valuelong = Double.parseDouble(longid);
                // it means it is double
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

        LatLng latLng = new LatLng(valuelat, valuelong);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));


            mMap.addMarker(new MarkerOptions().position(latLng).title(Title));

    }

}