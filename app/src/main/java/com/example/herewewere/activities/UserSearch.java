package com.example.herewewere.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herewewere.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class UserSearch extends AppCompatActivity {
    SearchView searchView;
    ImageView imageView;
    TextView email,uid;
    Button search,delete;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        searchView = findViewById(R.id.User_search);
        imageView = findViewById(R.id.Userimg);
        email = findViewById(R.id.UserEmail);
        uid = findViewById(R.id.userID);
        search = findViewById(R.id.Searchbutton);
        delete = findViewById(R.id.DeleteUserButton);


    }
}