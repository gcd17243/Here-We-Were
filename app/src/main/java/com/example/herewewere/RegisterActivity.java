package com.example.herewewere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, repassword;
    Button signup;
    UserDB DB;
    TextView banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.email1);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        signup = (Button) findViewById(R.id.registerbtn);
        banner = (TextView) findViewById(R.id.banner);
        DB = new UserDB(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String repass = repassword.getText().toString().trim();
                //Error msg for User information not fill
                if(user.isEmpty()){
                    email.setError("Email is required!");
                    email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
                    email.setError("Email is Invalid!");
                    email.requestFocus();
                    return;
                }

                if(pass.isEmpty()){
                    password.setError("Password is required!");
                    password.requestFocus();
                    return;
                }
                if(repass.isEmpty()){
                    repassword.setError("Password is required!");
                    repassword.requestFocus();
                    return;
                }
                //Check password length < 6
                if(pass.length() < 6){
                    password.setError("Password min length should be 6 characters!");
                    password.requestFocus();
                    return;
                }
                if(repass.length() < 6){
                    repassword.setError("Password min length should be 6 characters!");
                    repassword.requestFocus();
                    return;
                }
                if(user.equals("")||pass.equals("")||repass.equals(""))
                    Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    //Check if password match repassword
                    if(pass.equals(repass)){
                        Boolean checkuser = DB.checkemail(user);
                        //Check already exit user
                        if(checkuser==false){
                            Boolean insert = DB.insertData(user, pass);
                            //Check if data is insert
                            if(insert==true){
                                Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "User already exists! please sign in", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                } }
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