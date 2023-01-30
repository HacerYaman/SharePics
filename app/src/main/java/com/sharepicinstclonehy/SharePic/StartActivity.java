package com.sharepicinstclonehy.SharePic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sharepicinstclonehy.SharePic.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding activityStartBinding;
    private FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStartBinding = ActivityStartBinding.inflate(getLayoutInflater());
        View view = activityStartBinding.getRoot();
        setContentView(view);

        activityStartBinding.loginButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        activityStartBinding.registerButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}