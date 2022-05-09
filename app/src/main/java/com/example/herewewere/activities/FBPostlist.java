package com.example.herewewere.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.herewewere.MapsActivity;
import com.example.herewewere.R;
import com.example.herewewere.adapters.FBAdapter;
import com.example.herewewere.databases.FBPost;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;


public class FBPostlist extends AppCompatActivity {
    RecyclerView recview;
    FBAdapter fbAdapter;
    FloatingActionButton add,map,personal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbpostlist);
        recview=(RecyclerView)findViewById(R.id.fbPostList);
        add = findViewById(R.id.floatingActionButton5);
        map = findViewById(R.id.floatingActionButton6);
        personal = findViewById(R.id.Personalpostbtn);
        recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<FBPost> options =
                new FirebaseRecyclerOptions.Builder<FBPost>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("FBPost").orderByChild("date"), FBPost.class)
                        .build();
        fbAdapter=new FBAdapter(options);
        recview.setAdapter(fbAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        fbAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fbAdapter.stopListening();
    }

    public void gotToMapActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void gotToCreateNewNoteActivity(View view) {
        Intent intent = new Intent(this, CreateOrShowNoteActivity.class);
        intent.putExtra("our_note_key", "create_note_here");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.searchmenu,menu);

        MenuItem item=menu.findItem(R.id.search);

        SearchView searchView=(SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s) {

                processsearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processsearch(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void processsearch(String s)
    {
        FirebaseRecyclerOptions<FBPost> options =
                new FirebaseRecyclerOptions.Builder<FBPost>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("FBPost").orderByChild("title").startAt(s).endAt(s+"\uf8ff"), FBPost.class)
                        .build();

        fbAdapter=new FBAdapter(options);
        fbAdapter.startListening();
        recview.setAdapter(fbAdapter);

    }
    public void goToPersonalpost(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}