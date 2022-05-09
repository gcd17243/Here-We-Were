package com.example.herewewere.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;


import com.example.herewewere.Login.LoginActivity;
import com.example.herewewere.MapsActivity;
import com.example.herewewere.ProfileActivity;
import com.example.herewewere.R;
import com.example.herewewere.adapters.RecyclerViewAdapter;
import com.example.herewewere.databases.MyNoteDbManager;
import com.example.herewewere.models.MyNote;
import com.example.herewewere.preferences.MyPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private MyPreferences myPreferences;
    private MyNoteDbManager myNoteDbManager;

    private List<MyNote> myNotesList;
    private int fontSize, backgroundColor;
    private boolean isUndoClicked;

    private TextView noDataTextView;
    private MenuItem listMenuItem, gridMenuItem, listOrGridViewIcon;
    private FloatingActionButton floatingActionButton,floatingActionButton2,Globalbtn;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setTitle("Notes");
        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noDataText);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton2 = findViewById(R.id.floatingActionButton2);
        Globalbtn = findViewById(R.id.Globalpostbtn);

        myPreferences = MyPreferences.getMyPreferences(this);
        myNoteDbManager = new MyNoteDbManager(this);

        fontSize = myPreferences.getFontSize();  //by default medium = 1
        backgroundColor = myPreferences.getBackgroundColor();


        displayAllNotes();
        floatingActionButtonHideOrShow();
    }
    private void displayAllNotes() {
        MyNoteDbManager myNoteDbManager = new MyNoteDbManager(this);
        myNotesList = myNoteDbManager.getAllNotes();

        if (!myNotesList.isEmpty()) {
            noDataTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (myPreferences.isViewChange()) {
                int screenRotation = this.getResources().getConfiguration().orientation;

                if (screenRotation == 1) { //1 for portrait mode
                    layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                } else if (screenRotation == 2) { // 2 for landscape mode
                    layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                }
            } else {
                //true means list view mode
                layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }

            Collections.reverse(myNotesList);
            recyclerView.setHasFixedSize(true);
            recyclerViewAdapter = new RecyclerViewAdapter(this, myNotesList, fontSize, backgroundColor);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void floatingActionButtonHideOrShow() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**dy is increasing when scrolling down (+ve)
             dy is decreasing when scrolling up (-ve)*/
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if ((dy > 0 || dy < 0) && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        listOrGridViewIcon = menu.findItem(R.id.listOrGridViewIcon);

        if (myPreferences.isViewChange()) {
            listOrGridViewIcon.setIcon(R.drawable.list_view_ivon);
        } else {
            listOrGridViewIcon.setIcon(R.drawable.grid_view_icon);
        }

        if (!myNotesList.isEmpty()) {
            menu.findItem(R.id.deleteAllNoteIcon).setVisible(true);
        } else {
            menu.findItem(R.id.deleteAllNoteIcon).setVisible(false);
        }

        MenuItem searchMenuItem = menu.findItem(R.id.searchIcon);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Type note title or body");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    displayAllNotes();
                } else if (!newText.trim().isEmpty() && (myNotesList.size() > 0 && !myNotesList.isEmpty())){
                    recyclerViewAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

        switch (item.getItemId()) {

            case R.id.listOrGridViewIcon:
                if (myPreferences.isViewChange()) {
                    listOrGridViewIcon.setIcon(R.drawable.list_view_ivon);
                    myPreferences.setViewChange(false);
                } else {
                    listOrGridViewIcon.setIcon(R.drawable.grid_view_icon);
                    myPreferences.setViewChange(true);
                }
                displayAllNotes();
                break;

            case R.id.deleteAllNoteIcon:
                deleteAllNotes();
                break;

            case R.id.fontChangeIcon:
                fontChange();
                break;
            case R.id.ProfileIcon:
                if(user.getEmail().equals("hanngcd17243@fpt.edu.vn")){
                    startActivity(new Intent(this, UserSearch.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }else{
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

                break;
            case R.id.LogoutIcon:
                signOut();
                break;
            case R.id.UsersSearch:
                intent = new Intent(getApplicationContext(), FBPostlist.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            case R.id.Report:
                intent = new Intent(getApplicationContext(), Report.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

        }
        return true;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "User Signed Out Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } });  }

    public void gotToCreateNewNoteActivity(View view) {
        Intent intent = new Intent(this, CreateOrShowNoteActivity.class);
        intent.putExtra("our_note_key", "create_note_here");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void deleteAllNotes() {
        floatingActionButton.hide();
        myNotesList.clear();
        recyclerViewAdapter.notifyDataSetChanged();

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "All notes delete", Snackbar.LENGTH_INDEFINITE).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUndoClicked = true;
                floatingActionButton.show();
                displayAllNotes();
            }
        }).setActionTextColor(Color.YELLOW);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isUndoClicked) {
                    myNotesList = myNoteDbManager.getAllNotes();


                    boolean deleteStatus = myNoteDbManager.deleteAllNotes();
                    if (deleteStatus) {
                        snackbar.dismiss();
                        myNotesList.clear();
                        recyclerViewAdapter.notifyDataSetChanged();
                        noDataTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        }, 5000);
        isUndoClicked = false;
        myNotesList = myNoteDbManager.getAllNotes();
    }

    private void fontChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.font_icon);
        builder.setTitle("Choose a font size");

        builder.setSingleChoiceItems(R.array.font_size, fontSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fontSize = which;
                myPreferences.setFontSize(fontSize);
                dialog.dismiss();

                //after 1 second the activity is recreated
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 500);
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        isUndoClicked = true;  // if clicked on all delete button and close the app before the snack-bar is hiding
        super.onBackPressed();
    }


    public void gotToMapActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void goToGlobalpost(View view){
        Intent intent = new Intent(getApplicationContext(), FBPostlist.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}