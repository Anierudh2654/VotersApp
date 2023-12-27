package com.anierudh.voters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                FirebaseUser curr= FirebaseAuth.getInstance().getCurrentUser();
                if(curr==null){
                    startActivity(new Intent(MainActivity.this, Login.class));
                }else{
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
                finish();

    }
}