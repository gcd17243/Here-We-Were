package com.example.herewewere.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herewewere.R;
import com.example.herewewere.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, repassword;
    Button signup;

    TextView banner;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.email1);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        signup = (Button) findViewById(R.id.registerbtn);
        banner = (TextView) findViewById(R.id.banner);
        mAuth = FirebaseAuth.getInstance();



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get input from layout

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
                        //check register in firebase
                        mAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });



                    }
                     else{
                        Toast.makeText(RegisterActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //Go to LoginActivity
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}