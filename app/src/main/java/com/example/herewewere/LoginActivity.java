package com.example.herewewere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity  {

    TextView register,banner;
    EditText email, password;
    Button loginbtn;
    UserDB DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Link to layout interaction
        email = (EditText) findViewById(R.id.email1);
        password = (EditText) findViewById(R.id.password);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        //Set text as button
        register = (TextView) findViewById(R.id.register);
        banner = (TextView) findViewById(R.id.Banner);



        DB = new UserDB(this);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get email and password from database
                String user = email.getText().toString();
                String pass = password.getText().toString();
                //Check User
                if(user.equals("")||pass.equals(""))
                    //If user match
                    Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkuserpass = DB.checkemailpassword(user, pass);
                    if(checkuserpass==true){
                        Toast.makeText(LoginActivity.this, "Sign in successfull", Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}