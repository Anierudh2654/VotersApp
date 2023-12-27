package com.anierudh.voters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    Button register,campaign,vote,result,signout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        register=findViewById(R.id.btnRegisterCandidate);
        campaign=findViewById(R.id.btnViewCampaign);
        vote=findViewById(R.id.btnVote);
        result=findViewById(R.id.btnViewResults);
        signout=findViewById(R.id.btnSignOut);

        signout.setOnClickListener((v)->signoutuser());
        register.setOnClickListener(v->startActivity(new Intent(Home.this,Register.class)));
        vote.setOnClickListener(v->startActivity(new Intent(Home.this, Vote.class)));
        result.setOnClickListener(v->startActivity(new Intent(Home.this, Result.class)));
        campaign.setOnClickListener(v->startActivity(new Intent(Home.this, Campaign.class)));
    }
    void signoutuser(){
        FirebaseAuth fba=FirebaseAuth.getInstance();
        fba.signOut();
        startActivity(new Intent(Home.this, Login.class));
        finish();
    }
}