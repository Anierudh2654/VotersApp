package com.anierudh.voters;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {
    EditText EmailSignup,PasswordSignup,ReenterSignup;
    Button Signup;
    TextView GoToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EmailSignup=findViewById(R.id.EmailSignup);
        PasswordSignup=findViewById(R.id.PasswordSignup);
        ReenterSignup=findViewById(R.id.ReenterPassword);
        Signup=findViewById(R.id.Signup);
        GoToLogin=findViewById(R.id.GoToLogin);

        Signup.setOnClickListener(v->createaccount());
        GoToLogin.setOnClickListener((v)->startActivity(new Intent(Signup.this,Login.class)));
    }
    void createaccount(){
        String email=EmailSignup.getText().toString();
        String password=PasswordSignup.getText().toString();
        String reenter=ReenterSignup.getText().toString();
        boolean isvalid= validate(email,password,reenter);
        if(!isvalid){
            return;
        }
        createAccountInFirebase(email,password);
    }
    boolean validate(String email, String pswd, String reenter){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailSignup.setError("invalid email");
            return false;
        }
        if(pswd.length()<6){
            PasswordSignup.setError("min 6 char");
            return false;
        }
        if(!pswd.equals(reenter)){
            ReenterSignup.setError("password doesn't match");
            return false;
        }
        return true;
    }
    void createAccountInFirebase(String email,String pswd){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener(Signup.this,
                new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()){
                            Toast.makeText(Signup.this,"Succesful,Check email to verify",Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            Intent loginIntent = new Intent(Signup.this, Login.class);
                            startActivity(loginIntent);
                            finish();
                        }
                        else{
                            Toast.makeText(Signup.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

}