package com.anierudh.voters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText EmailLogin,PasswordLogin;
    Button btnlogin;
    TextView GoToSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EmailLogin=findViewById(R.id.Emaillogin);
        PasswordLogin=findViewById(R.id.Passwordlogin);
        btnlogin=findViewById(R.id.btnLogin);
        GoToSignup=findViewById(R.id.GoToSignUp);

        GoToSignup.setOnClickListener((v)->startActivity(new Intent(Login.this,Signup.class)));
        btnlogin.setOnClickListener((v)->loginUser());
    }
    void loginUser(){
        String email=EmailLogin.getText().toString();
        String pswd=PasswordLogin.getText().toString();
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        startActivity(new Intent(Login.this, Home.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Email not verified. Please check your email.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Login failed: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}